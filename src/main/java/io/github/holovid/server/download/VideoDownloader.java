package io.github.holovid.server.download;

import io.github.holovid.server.HolovidServerApplication;
import io.github.holovid.server.exception.VideoTooLongException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public abstract class VideoDownloader {

    protected static final long MAX_LENGTH_MILLIS = TimeUnit.MINUTES.toMillis(60);
    protected final HolovidServerApplication server;
    protected final File directory;

    protected VideoDownloader(final HolovidServerApplication server, final String dirName) {
        this.server = server;
        this.directory = new File(server.getDownloadsDir(), dirName);
    }

    /**
     * @param videoUrl video url
     * @return file the video has been downloaded to
     * @throws VideoTooLongException if the video is considered too long
     * @throws Exception             if anything else during the download process fails
     */
    public abstract DownloadResult downloadVideo(URL videoUrl) throws VideoTooLongException, Exception;

    /**
     * @param videoUrl video url
     * @return unique string of the video for this video platform
     */
    @Nullable
    public abstract String getIdFromVideo(URL videoUrl);

    /**
     * @return directory of this platform in the downloads dir
     */
    public File getDirectory() {
        return directory;
    }
}
