package org.lib.utils;

import java.io.File;

public class FileValidator {
    public static boolean ValidateDirectory(String path) {
        if (path == null) {
            return false;
        }
        var file = new File(path);
        return file.exists() && file.isDirectory();
    }

    public static boolean ValidateFile(String path) {
        if (path == null) {
            return false;
        }
        var file = new File(path);
        return file.exists() && file.isFile();
    }
}
