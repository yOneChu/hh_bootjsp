package com.kyhslam.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class csvFileTest {

    public static void main(String[] args) {
        String filePath = "C:\\excel\\BOM원복리스트_2차분_102대_20260421.xlsx";
        readExcel(filePath);
    }

    public static void readExcel(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("파일을 찾을 수 없습니다: " + filePath);
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();
            System.out.println("시트 수: " + sheetCount);

            for (int s = 0; s < sheetCount; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                System.out.println("\n===== 시트명: " + sheet.getSheetName() + " =====");

                for (Row row : sheet) {
                    int rowNum = row.getRowNum();
                    System.out.print("[행 " + (rowNum + 1) + "] ");

                    for (Cell cell : row) {
                        String value = getCellValue(cell);
                        System.out.print("[" + value + "] ");
                    }
                    System.out.println();
                }
            }

        } catch (IOException e) {
            System.out.println("파일 읽기 오류: " + e.getMessage());
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                // 정수면 소수점 제거
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // 수식 결과값 반환
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook()
                        .getCreationHelper().createFormulaEvaluator();
                CellValue evaluated = evaluator.evaluate(cell);
                return evaluated.formatAsString();
            default:
                return "";
        }
    }
}