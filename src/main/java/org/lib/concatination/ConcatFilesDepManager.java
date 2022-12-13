package org.lib.concatination;

import org.lib.domain.interfaces.Dependency;

import java.io.*;
import java.util.*;

public class ConcatFilesDepManager {
    private Map<File, ConcatFilesDependency> innerRepo;
    private final String mainFolder;

    private boolean isBuilt;

    public ConcatFilesDepManager(String mainFolder) {
        var check = new File(mainFolder);
        if (!check.exists() || !check.isDirectory()) {
            throw new RuntimeException("Нет такой папки");
        }
        this.mainFolder = mainFolder;
        if (!mainFolder.endsWith(File.pathSeparator)) {
            mainFolder += File.pathSeparator;
        }
        this.isBuilt = false;
    }

    public Dependency<File> add(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            throw new MissingFormatArgumentException("Аргумент должен быть существующим файлом.");
        }
        var dep = new ConcatFilesDependency(file);
        innerRepo.put(file, dep);
        return dep;
    }

    public void build() throws RuntimeException {
        for (var item : innerRepo.values()) {
            var files = getDependenciesFiles(item.getValue().getPath()).stream()
                    .map(s->mainFolder + s).toList();
            for (var file : files) {
                if (!innerRepo.containsKey(file)) {
                    throw new RuntimeException(String.format("Файл не существовал на момент записи."));
                }
                item.addDependency(innerRepo.get(file));
            }
        }
        isBuilt = true;
    }

    public List<ConcatFilesDependency> getResult() {
        return innerRepo.values().stream().toList();
    }

    private List<File> getDependenciesFiles(String path) throws RuntimeException {
        try (var reader = new BufferedReader(new FileReader(path))) {
            var result = new ArrayList<File>();
            String line = reader.readLine();
            while (line != null) {
                if (line.startsWith("require ")) {
                    String depPath = line.substring(8);
                    result.add(new File(depPath));
                }
                line = reader.readLine();
            }
            return result;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Файл не найден: " + e.getMessage() + ".");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException("Невозможно выполнить с null.");
        }
    }
}
