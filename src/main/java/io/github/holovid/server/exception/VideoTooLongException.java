package io.github.holovid.server.exception;

/**
 * Thrown when a video is considered too long to be downloaded or to have its audio encoded.
 */
public class VideoTooLongException extends Exception {

    public static final VideoTooLongException INSTANCE = new VideoTooLongException();

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
