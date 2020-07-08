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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
            copyResource("pack.mcmeta", new File(templateDir, "pack.mcmeta"));
            copyResource("sounds.json", new File(templateDir, "sounds.json"));
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
        //TODO support multiple platforms, proper pattern matching (and potentially another lib)
        final String authority = videoUrl.getAuthority().toLowerCase();
        final String[] split = authority.split("\\.");
        if (split.length < 2) return null;

        final String mainPart = split[split.length - 2];
        return mainPart.equals("youtube") || (mainPart.equals("youtu") && split[split.length - 1].equals("be")) ? videoDownloader : null;
    }

    public void copyResource(final String resourceName, final File to) throws IOException {
        if (to.exists()) return;
        try (final InputStream in = HolovidServerApplication.class.getResourceAsStream(resourceName); final OutputStream out = new FileOutputStream(to)) {
            final byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
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
