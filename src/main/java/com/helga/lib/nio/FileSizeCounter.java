package com.helga.lib.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileSizeCounter {

    /**
     * Обходит директорию и считает суммарный размер файлов с заданными расширениями.
     * Выводит результат в консоль.
     *
     * @param folderPath путь к папке для подсчета
     * @param allowedExtensions список разрешённых расширений (например, [".ts", ".css"])
     */
    public static void countFilesTotalSizeByExtensions(String folderPath, List<String> allowedExtensions) {

        var startPath = Paths.get(folderPath);
        if (!Files.exists(startPath)) {
            System.out.println("Ошибка: путь не существует: " + folderPath);
            return;
        }

        if (!Files.isDirectory(startPath)) {
            System.out.println("Ошибка: указанный путь не является директорией: " + folderPath);
            return;
        }

        try (var stream = Files.walk(startPath)) {
            long totalBytes = stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .map(String::toLowerCase)
                    .filter(fileName -> allowedExtensions.stream()
                            .anyMatch(fileName::endsWith))
                    .mapToLong(filePath -> {
                        try {
                            return Files.size(Paths.get(filePath));
                        } catch (IOException e) {
                            System.err.println("Не удалось получить размер файла: " + filePath);
                            return 0L;
                        }
                    })
                    .sum();

            System.out.println("Общий размер файлов с расширением: " + allowedExtensions);
            System.out.printf("  В байтах: %,d%n", totalBytes);
            System.out.printf("  В килобайтах: %,d КБ%n", totalBytes / 1024);
            System.out.printf("  В мегабайтах: %,.2f МБ%n", (double) totalBytes / (1024 * 1024));

        } catch (IOException e) {
            System.err.println("Ошибка при обходе директории: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Ошибка доступа к файлам: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        var folder = "D:/count code/tickets/src/main/webapp";
        var extensions = Arrays.asList(".ts", ".css", ".scss", ".html");
        countFilesTotalSizeByExtensions(folder, extensions);
    }
}
