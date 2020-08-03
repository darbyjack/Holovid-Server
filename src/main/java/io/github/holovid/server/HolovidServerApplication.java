package io.github.holovid.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.holovid.server.download.VideoDownloader;
import io.github.holovid.server.download.YouTubeDownloader;
import io.github.holovid.server.util.FileUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@SpringBootApplication
public class HolovidServerApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolovidServerApplication.class);
    private File dataBaseDir;
    private File templateDir;
    private File downloadsDir;
    private final VideoDownloader videoDownloader;
    private String serverUrl;

    public HolovidServerApplication() {
        LOGGER.info("\n" +
                "  _   _ _____ _     _     ___  _   _ \n" +
                " | | | | ____| |   | |   / _ \\| | | |\n" +
                " | |_| |  _| | |   | |  | | | | | | |\n" +
                " |  _  | |___| |___| |__| |_| | |_| |\n" +
                " |_| |_|_____|_____|_____\\___/ \\___/ \n" +
                " It'sa me, KennyTV\n");

        // Read config, write template files
        try {
            loadConfig();

            copyResource("pack.mcmeta", new File(templateDir, "pack.mcmeta"));
            copyResource("sounds.json", new File(templateDir, "sounds.json"));
            copyResource("default.json", new File(templateDir, "default.json"));
            copyResource("pixel.png", new File(templateDir, "pixel.png"));
        } catch (final IOException e) {
            LOGGER.error("Error writing resource pack files into the template folder", e);
            System.exit(1);
        }

        videoDownloader = new YouTubeDownloader(this);
    }

    private void loadConfig() throws IOException {
        final File dataDir = new File("data");
        dataDir.mkdirs();
        final File configFile = new File(dataDir, "config.json");
        copyResource("config.json", configFile);

        final JsonObject object = new Gson().fromJson(Files.readString(configFile.toPath()), JsonObject.class);
        serverUrl = object.getAsJsonPrimitive("server-url").getAsString();
        if (serverUrl.endsWith("/")) {
            serverUrl += "/";
        }

        dataBaseDir = new File(object.getAsJsonPrimitive("resourcepack-path").getAsString());
        templateDir = new File(dataBaseDir, "template");
        downloadsDir = new File(dataBaseDir, "downloads");

        // Create base directories and templates
        templateDir.mkdirs();
        downloadsDir.mkdirs();
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
        try (final InputStream in = getResource(resourceName); final OutputStream out = new FileOutputStream(to)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource " + resourceName + " does not exist!");
            }

            final byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    @Nullable
    private InputStream getResource(final String resourceName) throws IOException {
        final URL url = HolovidServerApplication.class.getClassLoader().getResource(resourceName);
        if (url == null) return null;

        final URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }

    public File getTemplateDir() {
        return templateDir;
    }

    public File getDownloadsDir() {
        return downloadsDir;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public File getDataDir() {
        return dataBaseDir;
    }
}
