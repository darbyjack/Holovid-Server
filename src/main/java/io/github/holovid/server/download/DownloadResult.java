package io.github.holovid.server.download;

import java.io.File;

public final class DownloadResult {

    private final File videoFile;
    private final long duration;

    /**
     * @param videoFile file the video has been downloaded to
     * @param duration  duration of the video, or -1 if unknown
     */
    public DownloadResult(final File videoFile, final long duration) {
        this.videoFile = videoFile;
        this.duration = duration;
    }

    public File getVideoFile() {
        return videoFile;
    }

    /**
     * @return duration of the video, or -1 if unknown
     */
    public long getDuration() {
        return duration;
    }
}
