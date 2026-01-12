package com.helga.lib.swagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

    /**
     * Метод читает Swagger-документацию из локального файла и сохраняет уникальные endpoints,
     * сгруппированные по контроллерам, в указанный выходной файл.
     *
     * @param inputFilePath Путь к файлу с Swagger-документацией
     * @param outputFilePath Путь к файлу, куда записать список endpoints
     */
    public static void extractEndpointsGroupedByResource(String inputFilePath, String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new FileReader(inputFilePath));

        TreeMap<String, List<String>> groupedEndpoints = new TreeMap<>();

        JsonNode pathsNode = root.path("paths");

        Iterator<Map.Entry<String, JsonNode>> it = pathsNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            String path = entry.getKey();
            JsonNode operations = entry.getValue();

            Iterator<Map.Entry<String, JsonNode>> opIt = operations.fields();
            while (opIt.hasNext()) {
                Map.Entry<String, JsonNode> opEntry = opIt.next();
                String method = opEntry.getKey();
                JsonNode operationDetails = opEntry.getValue();

                JsonNode tagsNode = operationDetails.path("tags");
                if (!tagsNode.isMissingNode()) {
                    for (JsonNode tag : tagsNode) {
                        String controllerTag = tag.textValue();
                        groupedEndpoints.computeIfAbsent(controllerTag, k -> new ArrayList<>())
                                .add(path + " " + method.toUpperCase());
                    }
                }
            }
        }

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            for (Map.Entry<String, List<String>> entry : groupedEndpoints.entrySet()) {
                writer.write(entry.getKey() + ":\n");
                for (String pathWithMethod : entry.getValue()) {
                    writer.write("\t" + pathWithMethod + "\n");
                }
                writer.write("\n");
            }
        }
    }

    public static void main(String[] args) {
        try {
//            extractEndpoints("./swagger.json", "./endpoints.txt");
//            System.out.println("Уникальные endpoints успешно сохранены!");
            extractEndpointsGroupedByResource("./swagger.json", "./g_endpoints.txt");
            System.out.println("Уникальные endpoints по группам успешно сохранены!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

