package com.helga.lib.nio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class CallFilesHandler {

    /**
     * Копирует все raw файлы из startDir в ./sounds, исключая пустые файлы,
     * затем конвертирует их в wav и удаляет исходники.
     */
    public static void copyAndConvertRawFiles(String startDir) throws IOException {
        Path resultDir = Paths.get("D:/sounds");
        if (!Files.exists(resultDir)) {
            Files.createDirectories(resultDir);
        }

        try (Stream<Path> paths = Files.walk(Paths.get(startDir))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".raw"))
                    .filter(path -> {
                        try {
                            return Files.size(path) > 0;
                        } catch (IOException e) {
                            System.err.println("Не удалось проверить размер файла: " + path);
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Path target = resultDir.resolve(path.getFileName());
                            Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Скопирован файл: " + path + " -> " + target);
                        } catch (IOException e) {
                            System.err.println("Ошибка при копировании файла " + path + ": " + e.getMessage());
                        }
                    });
        }

        convertAndCleanRawFiles(resultDir);
    }

    /**
     * Конвертирует все raw файлы в папке в wav и удаляет исходные raw файлы.
     */
    private static void convertAndCleanRawFiles(Path directory) {
        File dir = directory.toFile();
        File[] rawFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".raw"));

        if (rawFiles == null || rawFiles.length == 0) {
            System.out.println("Файлы .raw не найдены в папке " + directory);
            return;
        }

        AudioFormat format = new AudioFormat(8000, 16, 1, true, false);

        for (File rawFile : rawFiles) {
            File wavFile = new File(rawFile.getParent(), rawFile.getName().replaceAll("\\.raw$", ".wav"));
            try {
                convertRawToWav(rawFile, wavFile, format);
                System.out.println("Конвертирован: " + rawFile.getName() + " -> " + wavFile.getName());

                if (rawFile.delete()) {
                    System.out.println("Удалён raw файл: " + rawFile.getName());
                } else {
                    System.err.println("Не удалось удалить raw файл: " + rawFile.getName());
                }
            } catch (IOException e) {
                System.err.println("Ошибка конвертации файла " + rawFile.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Конвертирует raw файл в wav
     */
    private static void convertRawToWav(File rawFile, File wavFile, AudioFormat format) throws IOException {
        try (BufferedInputStream rawInputStream = new BufferedInputStream(new FileInputStream(rawFile))) {
            long rawLength= rawFile.length();
            AudioInputStream audioInputStream = new AudioInputStream(rawInputStream, format, rawLength / format.getFrameSize());
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        }
    }

    /**
     * Обрезает все wav-файлы в папке sounds до указанной длительности
     * и сохраняет их в папку cf-<duration>.
     * @param soundsDirPath путь к папке с wav файлами
     * @param duration длительность в секундах
     */
    public static void trimWavFiles(String soundsDirPath, int duration) throws IOException {
        Path soundsDir = Paths.get(soundsDirPath);
        if (!Files.exists(soundsDir) || !Files.isDirectory(soundsDir)) {
            System.err.println("Папка " + soundsDirPath + " не найдена.");
            return;
        }

        Path outputDir = Paths.get("cf-" + duration);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        try (var files = Files.list(soundsDir)) {
            files.filter(path -> path.toString().toLowerCase().endsWith(".wav"))
                    .forEach(path -> {
                        try {
                            trimWavFile(path.toFile(), duration, outputDir);
                        } catch (Exception e) {
                            System.err.println("Ошибка при обрезке файла " + path.getFileName() + ": " + e.getMessage());
                        }
                    });
        }
    }

    /**
     * Обрезает wav-файл до указанной длительности и сохраняет в outputDir с префиксом длительности.
     *
     * @param wavFile исходный wav-файл
     * @param duration длительность в секундах
     * @param outputDir папка для сохранения обрезанных файлов
     */
    private static void trimWavFile(File wavFile, int duration, Path outputDir) throws IOException, UnsupportedAudioFileException {
        try (AudioInputStream originalStream = AudioSystem.getAudioInputStream(wavFile)) {
            AudioFormat format = originalStream.getFormat();
            long totalFrames = originalStream.getFrameLength();
            float frameRate = format.getFrameRate();

            float fileDuration = totalFrames / frameRate;

            if (fileDuration < duration && duration != 3) {
                System.out.println("Файл " + wavFile.getName() + " короче " + duration + " секунд, пропускаем.");
                return;
            }

            long framesToKeep = (long) (duration * frameRate);

            AudioInputStream trimmedStream = new AudioInputStream(originalStream, format, framesToKeep);
            File outputFile = outputDir.resolve(duration + "_" + wavFile.getName()).toFile();
            AudioSystem.write(trimmedStream, AudioFileFormat.Type.WAVE, outputFile);
            System.out.println("Обрезан и сохранен файл: " + outputFile.getName());
        }
    }

    public static void main(String[] args) {
//        try {
//            copyAndConvertRawFiles("D:/raw");
//        } catch (IOException e) {
//            System.err.println("Ошибка при обработке: " + e.getMessage());
//        }

        try {
            trimWavFiles("D:/auto", 10);
//            trimWavFiles("D:/people", 4);
//            trimWavFiles("D:/people", 5);
//            trimWavFiles("D:/people", 6);
//            trimWavFiles("D:/people", 7);
//            trimWavFiles("D:/people", 8);
//            trimWavFiles("D:/people", 9);
//            trimWavFiles("D:/people", 10);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке: " + e.getMessage());
        }
    }
}


