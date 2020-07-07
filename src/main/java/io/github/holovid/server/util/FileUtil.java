package io.github.holovid.server.util;

import java.io.File;

public final class FileUtil {

    /**
     * Deletes a directory recursively with all of its contents.
     *
     * @param directory directory to delete
     */
    public static void deleteDirectory(final File directory) {
        if (!directory.isDirectory()) return;

        for (final File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }

        directory.delete();
    }
}
