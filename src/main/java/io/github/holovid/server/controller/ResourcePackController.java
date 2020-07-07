package io.github.holovid.server.controller;

import io.github.holovid.server.HolovidServerApplication;
import io.github.holovid.server.download.DownloadResult;
import io.github.holovid.server.download.VideoDownloader;
import io.github.holovid.server.exception.VideoTooLongException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public final class ResourcePackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePackController.class);
    private static final long LARGE_THRESHOLD = TimeUnit.MINUTES.toMillis(30);
    private final Encoder audioEncoder = new Encoder();
    private final HolovidServerApplication server;
    private final Set<String> processing = new HashSet<>();

    public ResourcePackController(final HolovidServerApplication server) {
        this.server = server;
    }

    /**
     * Downloads the video from the given url and encodes its audio into a resource pack.
     *
     * @param videoUrl video url
     * @return download link for a resourcepack with the video's sound if successfull
     */
    @GetMapping("resourcepack/download")
    public ResponseEntity<String> downloadResourcePack(@RequestParam("videoUrl") final String videoUrl) throws Exception {
        try {
            synchronized (processing) {
                if (!processing.add(videoUrl.toLowerCase())) {
                    //TODO wait until process thread is finished and return same url
                    // (manual sleeping/reentrantlock)
                }
            }

            try {
                return createResourcepack(videoUrl);
            } catch (final VideoTooLongException e) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }
        } finally {
            synchronized (processing) {
                processing.remove(videoUrl.toLowerCase());
            }
        }
    }

    private ResponseEntity<String> createResourcepack(final String videoUrl) throws Exception {
        final URL url;
        try {
            url = new URL(videoUrl);
        } catch (final MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        final VideoDownloader downloader = server.getVideoDownloaderForUrl(url);
        if (downloader == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        final String id = downloader.getIdFromVideo(url);
        final File zipFile = new File(downloader.getDirectory(), id + ".zip");
        final String externalDownloadPath = zipFile.getAbsolutePath(); //TODO url for the download
        if (zipFile.exists()) {
            return ResponseEntity.ok(externalDownloadPath);
        }

        // Download video
        final DownloadResult result = downloader.downloadVideo(url);

        // Extract audio
        final File audioFile;
        try {
            audioFile = extractAudio(result);
        } finally {
            result.getVideoFile().delete();
        }

        // Zip it
        final File mcMetaFile = new File(server.getTemplateDir(), "pack.mcmeta");
        final File soundsFile = new File(server.getTemplateDir(), "sounds.json");
        try (final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            addToZipFile("", mcMetaFile, out);
            addToZipFile("assets/minecraft/", soundsFile, out);
            addToZipFile("assets/minecraft/sounds/holovid/", audioFile, out);
            out.flush();
        }

        audioFile.delete();
        return ResponseEntity.ok(externalDownloadPath);
    }

    private void addToZipFile(final String pathInZip, final File file, final ZipOutputStream out) throws IOException {
        try (final FileInputStream in = new FileInputStream(file)) {
            final ZipEntry zipEntry = new ZipEntry(pathInZip + file.getName());
            out.putNextEntry(zipEntry);

            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.closeEntry();
        }
    }

    private File extractAudio(final DownloadResult result) throws EncoderException, VideoTooLongException {
        final boolean largeVideo = result.getDuration() > LARGE_THRESHOLD;
        final File videoFile = result.getVideoFile();

        final AudioAttributes audio = new AudioAttributes();
        audio.setBitRate(largeVideo ? 32_000 : 64_000);
        audio.setChannels(largeVideo ? 1 : 2);
        audio.setSamplingRate(44_100);

        final EncodingAttributes encoding = new EncodingAttributes();
        encoding.setFormat("ogg");
        encoding.setAudioAttributes(audio);

        final File target = new File(videoFile.getParentFile(), "audio.ogg");
        audioEncoder.encode(new MultimediaObject(videoFile), target, encoding);
        return target;
    }
}