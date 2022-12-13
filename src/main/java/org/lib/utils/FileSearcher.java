package org.lib.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileSearcher {
    private final File where;
    private List<File> result;
    private int filesFound;
    private boolean searchComplete;

    public FileSearcher(String path) {
        if (!FileValidator.ValidateDirectory(path)) {
            throw new RuntimeException("Нет такой папки.");
        }
        this.where = new File(path);
        this.searchComplete = false;
        this.filesFound = 0;
        this.result = new ArrayList<>();
    }

    public List<File> Search() {
        InnerSearch(where);
        searchComplete = true;
        return result;
    }

    private void InnerSearch(File current) {
        if (current.isFile()) {
            result.add(current);
            filesFound++;
            return;
        }
        for (var subFile : Objects.requireNonNull(current.listFiles())) {
            InnerSearch(subFile);
        }
    }

    public int filesFound() {
        if (!searchComplete) {
            throw new RuntimeException("Поиск еще не был начат.");
        }
        return filesFound;
    }
}
