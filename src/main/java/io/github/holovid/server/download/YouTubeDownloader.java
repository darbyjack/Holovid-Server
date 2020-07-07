package io.github.holovid.server.download;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.AudioVideoFormat;
import io.github.holovid.server.HolovidServerApplication;
import io.github.holovid.server.exception.VideoTooLongException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public final class YouTubeDownloader extends VideoDownloader {

    private final YoutubeDownloader downloader = new YoutubeDownloader();

    public YouTubeDownloader(final HolovidServerApplication server) {
        super(server, "yt");
        downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
    }

    @Override
    public DownloadResult downloadVideo(final URL videoUrl) throws IOException, YoutubeException, VideoTooLongException {
        final String id = getIdFromVideo(videoUrl);
        final File outputFile = new File(directory, id + ".mp4");
        final YoutubeVideo video = downloader.getVideo(id);
        final AudioVideoFormat format = video.videoWithAudioFormats().get(0);
        if (format.duration() > MAX_LENGTH_MILLIS) {
            throw VideoTooLongException.INSTANCE;
        }

        final File download = video.download(format, directory);
        Files.move(download.toPath(), outputFile.toPath());
        return new DownloadResult(outputFile, format.duration());
    }

    @Override
    public String getIdFromVideo(final URL videoUrl) {
        return videoUrl.getQuery().substring(2);
    }
}
