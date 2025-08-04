package com.helga.lib.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ExcelReaderService {

    /*
    HSSFWorkbook - .xls. XSSFWorkbook и SXSSFWorkbook -.xlsx
    HSSFWorbook, XSSFWorkbook — непотоковые рабочие книги, которые хранят все строки данных в памяти,
    SXSSFWorkbook — потоковая книга, хранит в памяти только определённое количество строк, эффективнее использует
    память, если набор данных большой
    */

    public void filterExcel(String inputFilePath, String outputFilePath) {
        try (FileInputStream fis = new FileInputStream(inputFilePath);
             var workbook = new XSSFWorkbook(fis);
             var outputWorkbook = new XSSFWorkbook()) {

            var sheet = workbook.getSheetAt(0);
            var outputSheet = outputWorkbook.createSheet("Filtered Data");

            int outputRowNum = 0;

            for (Row row : sheet) {
                Cell phoneCell = row.getCell(0);
                Cell thematicsCell = row.getCell(2);
                Cell reasonCell = row.getCell(3);
                if (thematicsCell != null && (thematicsCell.getStringCellValue().equals("Другое")
                        || thematicsCell.getStringCellValue().equals("Другой вопрос"))) {

                    log.info("Обрабатываем строку: {}", row.getRowNum());

                    for (int i = 7; i < row.getPhysicalNumberOfCells(); i += 3) {
                        Cell dialogCell = row.getCell(i);
                        log.info(dialogCell.getStringCellValue());
                        Cell transcriptionCell = row.getCell(i - 1);
                        log.info(transcriptionCell.getStringCellValue());

                        if (dialogCell.getStringCellValue().equals("pre_transfer")) {

                            Row outputRow = outputSheet.createRow(outputRowNum++);
                            outputRow.createCell(0).setCellValue(phoneCell.getStringCellValue());
                            outputRow.createCell(1).setCellValue(thematicsCell.getStringCellValue());
                            outputRow.createCell(2).setCellValue(reasonCell.getStringCellValue());
                            outputRow.createCell(3).setCellValue(dialogCell.getStringCellValue());
                            outputRow.createCell(4).setCellValue(transcriptionCell.getStringCellValue());
                            log.info("Записано: {}, {}", dialogCell.getStringCellValue(), transcriptionCell.getStringCellValue());
                            break;
                        }
                    }
                }
            }
            try (var fos = new FileOutputStream(outputFilePath)) {
                outputWorkbook.write(fos);
                log.info("Данные успешно записаны в {}", outputFilePath);
            }

        } catch (IOException e) {
            log.error("Ошибка обработки excel: {}", e.getMessage());
        }
    }

    public int getRowsWithKeyword(String filePath, int columnNumber, String keyword, String outputFilePath) throws IOException {
        int rowCount = 0;

        try (var fis = new FileInputStream(filePath);
             var workbook = WorkbookFactory.create(fis);
             var outputWorkbook = new XSSFWorkbook()) {

            var sheet = workbook.getSheetAt(0);
            var outputSheet = outputWorkbook.createSheet("Filtered rows");
            int outputRowNum = 0;

            for (Row row : sheet) {
                var cell = row.getCell(columnNumber);
                if (cell != null) {
                    var cellValue = cell.getStringCellValue();
                    if (cellValue.toLowerCase().contains(keyword.toLowerCase())) {
                        rowCount++;
                        var outputRow = outputSheet.createRow(outputRowNum++);
                        outputRow.createCell(0).setCellValue(rowCount);
                        outputRow.createCell(1).setCellValue(cellValue);
                    }
                }
            }
            try (var fos = new FileOutputStream(outputFilePath)) {
                outputWorkbook.write(fos);
                log.info("Данные записаны в {}", outputFilePath);
            }
        }
        return rowCount;
    }

    public int getRowsWithKeywords(String filePath, int columnNumber, String[] keywords, String outputFilePath) throws IOException {
        int rowCount = 0;

        try (var fis = new FileInputStream(filePath);
             var workbook = WorkbookFactory.create(fis);
             var outputWorkbook = new XSSFWorkbook()) {

            var sheet = workbook.getSheetAt(0);
            var outputSheet = outputWorkbook.createSheet("Filtered rows");
            int outputRowNum = 0;

            for (Row row : sheet) {
                var cell = row.getCell(columnNumber);
                if (cell != null) {
                    var cellValue = cell.getStringCellValue();
                    for (String keyword : keywords) {
                        if (cellValue.toLowerCase().contains(keyword.toLowerCase())) {
                            rowCount++;
                            var outputRow = outputSheet.createRow(outputRowNum++);
                            outputRow.createCell(0).setCellValue(rowCount);
                            outputRow.createCell(1).setCellValue(cellValue);
                            break;
                        }
                    }
                }
            }
            try (var fos = new FileOutputStream(outputFilePath)) {
                outputWorkbook.write(fos);
                log.info("Данные записаны - {}", outputFilePath);
            }
        }
        return rowCount;
    }

    public int getRowsWithAllKeywords(String filePath, int columnNumber, String[] keywords, String outputFilePath) throws IOException {
        int rowCount = 0;

        try (var fis = new FileInputStream(filePath);
             var workbook = WorkbookFactory.create(fis);
             var outputWorkbook = new XSSFWorkbook()) {

            var sheet = workbook.getSheetAt(0);
            var outputSheet = outputWorkbook.createSheet("Filtered rows");
            int outputRowNum = 0;

            for (Row row : sheet) {
                var cell = row.getCell(columnNumber);
                if (cell != null) {
                    var cellValue = cell.getStringCellValue();
                    var containsAllKeywords = true;

                    for (String keyword : keywords) {
                        if (!cellValue.toLowerCase().contains(keyword.toLowerCase())) {
                            containsAllKeywords = false;
                            break;
                        }
                    }

                    if (containsAllKeywords) {
                        rowCount++;
                        var outputRow = outputSheet.createRow(outputRowNum++);
                        outputRow.createCell(0).setCellValue(rowCount);
                        outputRow.createCell(1).setCellValue(cellValue);
                    }
                }
            }

            try (var fos = new FileOutputStream(outputFilePath)) {
                outputWorkbook.write(fos);
                log.info("Данные записаны: {}", outputFilePath);
            }
        }
        return rowCount;
    }
}
