package org.example;

import org.lib.concatination.ConcatFilesDepManager;
import org.lib.domain.interfaces.Dependency;
import org.lib.domain.exceptions.FoundCycleException;
import org.lib.implementaions.DependencyGraph;
import org.lib.utils.FileSearcher;

import java.io.*;
import java.util.Comparator;
import java.util.Scanner;

public class Main {

    private static final Scanner stdIn = new Scanner(System.in);
    private static final PrintStream stdOut = System.out;
    public static void main(String[] args) {
        System.out.println("Просьба не менять используемые файлы и директории до завершения программы.");
        System.out.println("Символ для разделения пути на вашей ОС: " + File.separator);
        boolean error = true;
        ConcatFilesDepManager manager = null;
        while (error) {
            String mainFolder = stdIn.nextLine();
            //try {
                manager = new ConcatFilesDepManager(mainFolder);
                var searcher = new FileSearcher(mainFolder);
                var files = searcher.Search();
                files.forEach(manager::add);
                System.out.println("Найдено " + searcher.filesFound() + " файлов.");
                manager.build();
                System.out.println("Файлы предобработаны.");
                error = false;
            /*} catch (RuntimeException ex) {
                stdOut.println(ex.getMessage());
            }*/
        }
        var dependencies = manager.getResult().stream().map(x->(Dependency<File>)x).toList();
        var graph = new DependencyGraph<File>(dependencies, new Comparator<Dependency<File>>() {
            @Override
            public int compare(Dependency<File> df1, Dependency<File> df2) {
                return df1.getValue().getAbsolutePath().compareTo(df2.getValue().getAbsolutePath());
            }
        });

        try {
            System.out.println("Определен следующий порядок файлов:");
            var order = graph.getOrder().stream().map(x->x.getValue().getAbsolutePath()).toList();
            order.forEach(System.out::println);
        } catch (FoundCycleException ex) {
            System.out.println(ex.getMessage());
        }
    }
}