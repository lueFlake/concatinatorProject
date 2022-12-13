package org.example;

import org.lib.concatination.ConcatFilesDepManager;
import org.lib.domain.interfaces.Dependency;
import org.lib.domain.models.FoundCycleException;
import org.lib.implementaions.DependencyGraph;
import org.lib.utils.FileSearcher;

import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {

    private static final Scanner stdIn = new Scanner(System.in);
    private static final PrintStream stdOut = System.out;
    public static void main(String[] args) {
        System.out.println("Просьба не менять используемые файлы и директории до завершения программы.");
        boolean error = true;
        ConcatFilesDepManager manager = null;
        while (error) {
            String mainFolder = stdIn.nextLine();
            try {
                manager = new ConcatFilesDepManager(mainFolder);
                var searcher = new FileSearcher(mainFolder);
                var files = searcher.Search();
                files.forEach(manager::add);
                System.out.println("Найдено " + searcher.filesFound() + " файлов.");
                manager.build();
                System.out.println("Файлы предобработаны.");
                error = false;
            } catch (RuntimeException ex) {
                stdOut.println(ex.getMessage());
            }
        }
        var dependencies = manager.getResult().stream().map(x->(Dependency<File>)x).toList();
        var graph = new DependencyGraph<File>(dependencies);
        try {
            var order = graph.getOrder();
            
        } catch (FoundCycleException ex) {

        }
    }
}