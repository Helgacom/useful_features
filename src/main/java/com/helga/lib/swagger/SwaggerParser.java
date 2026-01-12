package com.helga.lib.swagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SwaggerParser {

    /**
     * Метод читает Swagger-документацию из локального файла и сохраняет уникальные endpoints в указанный выходной файл.
     *
     * @param inputFilePath Путь к файлу с Swagger-документацией (предварительно формируется в корне проекта, копия web)
     * @param outputFilePath Путь к файлу, куда записать список endpoints
     */
    public static void extractEndpoints(String inputFilePath, String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new FileReader(inputFilePath));

        JsonNode pathsNode = root.path("paths");
        Set<String> uniquePaths = new HashSet<>();

        for (Iterator<String> it = pathsNode.fieldNames(); it.hasNext(); ) {
            String path = it.next();
            uniquePaths.add(path);
        }

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            for (String path : uniquePaths) {
                writer.write(path + "\n");
            }
        }
    }

    public static void main(String[] args) {
        try {
            extractEndpoints("./swagger.json", "./endpoints.txt");
            System.out.println("Уникальные endpoints сохранены");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

