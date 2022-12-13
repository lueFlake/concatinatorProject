package org.example;

import org.lib.domain.interfaces.Dependency;
import org.lib.domain.models.ConcatFilesDependency;
import org.lib.implementaions.ConcatFilesDepManager;
import org.lib.implementaions.DependencyGraph;

import javax.annotation.processing.SupportedSourceVersion;

import static java.nio.file.StandardOpenOption.*;

import java.nio.file.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner stdIn = new Scanner(System.in);
    private static final PrintStream stdOut = System.out;
    public static void main(String[] args) {
        boolean error = true;
        while (error) {
            String mainFolder = stdIn.nextLine();
            try {
                var manager = new ConcatFilesDepManager(mainFolder);
                manager.build();
                error = false;
            } catch (RuntimeException ex) {
                stdOut.println(ex.getMessage());
            }
        }
    }
}