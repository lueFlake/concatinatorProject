package org.example;

import org.lib.concatination.ConcatFilesDepManager;
import org.lib.domain.interfaces.Dependency;
import org.lib.domain.exceptions.FoundCycleException;
import org.lib.implementaions.DependencyGraph;
import org.lib.utils.FileSearcher;
import org.lib.utils.FileValidator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner stdIn = new Scanner(System.in);
    private static final PrintStream stdOut = System.out;
    public static void main(String[] args) {
        stdOut.println("Просьба не менять используемые файлы и директории до завершения программы.");
        stdOut.println("Символ для разделения пути на вашей ОС: " + File.separator);
        boolean error = true;
        ConcatFilesDepManager manager = null;
        while (error) {
            String mainFolder = stdIn.nextLine();
            try {
                manager = new ConcatFilesDepManager(mainFolder);
                var searcher = new FileSearcher(mainFolder);
                var files = searcher.Search();
                files.forEach(manager::add);
                stdOut.println("Найдено " + searcher.filesFound() + " файлов.");
                manager.build();
                stdOut.println("Файлы предобработаны.");
                error = false;
            } catch (RuntimeException ex) {
                stdOut.println(ex.getMessage());
            }
        }
        var dependencies = manager.getResult().stream().map(x->(Dependency<File>)x).toList();
        var graph = new DependencyGraph<File>(dependencies, new Comparator<Dependency<File>>() {
            @Override
            public int compare(Dependency<File> df1, Dependency<File> df2) {
                return -df1.getValue().getAbsolutePath().compareTo(df2.getValue().getAbsolutePath());
            }
        });

        try {
            stdOut.println("Определен следующий порядок файлов:");
            var order = graph.getOrder().stream().map(x->x.getValue().getAbsolutePath()).toList();
            order.forEach(stdOut::println);


            String writeTo = stdIn.nextLine();
            while (!FileValidator.ValidateFile(writeTo)) {
                stdOut.println("Некорректно заданный путь до файла. Попробуйте еще раз.");
                writeTo = stdIn.nextLine();
            }

            try (var writer = new BufferedWriter(new FileWriter(writeTo))) {
                stdOut.println("Начинаю конкатенацию файлов в " + writeTo);
                for (var file : order) {
                    var text = Files.readString(Path.of(file));
                    writer.write(text + "\n");
                }
                stdOut.println("Конкатенация завершена.");
            }
        } catch (FoundCycleException ex) {
            stdOut.println(ex.getMessage());
        } catch (IOException ex) {
            stdOut.println("Сбой при вводе из файла или выводе в файл:");
            stdOut.println(ex.getMessage());
        }
    }
}