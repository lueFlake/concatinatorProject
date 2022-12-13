package org.lib.concatination;

import org.lib.domain.interfaces.Dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConcatFilesDependency implements Dependency<File> {
    private static int globalDependencyIdCounter = 0;
    private final File file;
    private final List<Dependency<File>> dependencies;
    private final int id;

    public ConcatFilesDependency(File file) {
        this.file = file;
        this.dependencies = new ArrayList<>();
        this.id = globalDependencyIdCounter++;
    }

    @Override
    public List<Dependency<File>> getDependent() {
        return dependencies;
    }

    public void addDependent(ConcatFilesDependency dep) {
        dependencies.add(dep);
    }

    @Override
    public File getValue() {
        return file;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return file.getAbsolutePath();
    }
}
