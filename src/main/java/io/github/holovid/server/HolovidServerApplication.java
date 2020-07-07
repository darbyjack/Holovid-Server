package io.github.holovid.server;

import io.github.holovid.server.download.VideoDownloader;
import io.github.holovid.server.download.YouTubeDownloader;
import io.github.holovid.server.util.FileUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

@SpringBootApplication
public class HolovidServerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolovidServerApplication.class);
    private final File getDataBaseDir = new File("data");
    private final File templateDir = new File(getDataBaseDir, "template");
    private final File downloadsDir = new File(getDataBaseDir, "downloads");
    private final VideoDownloader videoDownloader;

    public HolovidServerApplication() {
        LOGGER.info("\n" +
                "  _   _ _____ _     _     ___  _   _ \n" +
                " | | | | ____| |   | |   / _ \\| | | |\n" +
                " | |_| |  _| | |   | |  | | | | | | |\n" +
                " |  _  | |___| |___| |__| |_| | |_| |\n" +
                " |_| |_|_____|_____|_____\\___/ \\___/ \n" +
                " It'sa me, KennyTV\n");

        // Cleanup old downloaded files
        FileUtil.deleteDirectory(downloadsDir);

        // Create base directories and templates
        templateDir.mkdirs();
        downloadsDir.mkdirs();

        // Write template files
        try {
            final File mcMetaFile = new File(templateDir, "pack.mcmeta");
            if (!mcMetaFile.exists()) {
                Files.write(mcMetaFile.toPath(), "{\"pack\":{\"pack_format\":5,\"description\":\"Holovid\"}}".getBytes());
            }

            final File soundFile = new File(templateDir, "sounds.json");
            if (!soundFile.exists()) {
                Files.write(soundFile.toPath(), "{\"holovid.video\":{\"sounds\": [\"holovid/audio\"]}}".getBytes());
            }
        } catch (final IOException e) {
            LOGGER.error("Error writing resource pack files into the template folder", e);
            System.exit(1);
        }

        videoDownloader = new YouTubeDownloader(this);
    }

    public static void main(final String[] args) {
        SpringApplication.run(HolovidServerApplication.class, args);
    }

    @PreDestroy
    public void destroy() {
        FileUtil.deleteDirectory(downloadsDir);
    }

    @Nullable
    public VideoDownloader getVideoDownloaderForUrl(final URL videoUrl) {
        //TODO support multiple platforms
        return videoDownloader;
    }

    public File getGetDataBaseDir() {
        return getDataBaseDir;
    }

    public File getTemplateDir() {
        return templateDir;
    }

    public File getDownloadsDir() {
        return downloadsDir;
    }
}
