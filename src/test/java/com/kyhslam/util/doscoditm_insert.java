package com.kyhslam.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class doscoditm_insert {

    /**
     * PLM으 DOSCODITM 테이블의 데이터를 VAULT DB로 마이그레이션
     * @param
     */


    public static List<Map<String, String>> readExcel(String filePath) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new IllegalArgumentException("헤더 행이 비어 있습니다.");
            }

            int colCount = headerRow.getPhysicalNumberOfCells();
            List<String> headers = new ArrayList<>();

            // 헤더 읽기
            for (int i = 0; i < colCount; i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell.getStringCellValue());
            }

            // 데이터 읽기
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    //String value = (cell != null) ? getCellValueAsString(cell) : "";
                    String value = (cell != null) ? getCellValueV2(cell) : "";
                    rowData.put(headers.get(j), value);
                }
                dataList.add(rowData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public static String getCellValueV2(Cell cell) {
        String value = "";
        // 셀 내용의 유형 판별
        if (cell != null) {
            System.out.println(cell.getCellType());
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell))
                        value = cell.getLocalDateTimeCellValue().toString();
                    else
                        value = String.format("%.0f", cell.getNumericCellValue());
                    if (value.endsWith(".0"))
                        value = value.substring(0, value.length() - 2);
                    break;
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                case BLANK:
                case _NONE:
                default:
                    value = "";
                    break;
            }
        }
        return value;
    }

    private static String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }




    public static void main(String[] args) {

        String filePath = "D:/DOSCODITM.xlsx";
        List<Map<String, String>> data = readExcel(filePath);




        for (Map<String, String> row : data) {
            System.out.println(row);

            String ouid = row.get("OUID");
            String des = row.get("DES");
            String MSRTITLECODE = row.get("MSRTITLECODE");
            String CODITM = row.get("CODITM");
            String NAME = row.get("NAME");


            Connection con = null;
            PreparedStatement pstmt = null;
            try {
                con = VaultDBConnection.getConnection();

                String insertSql = """
                  INSERT INTO V_doscoditm (OUID, NAME, DES, CODITM, MSRTITLECODE) VALUES (?, ?, ?, ?, ?)
                """;


                pstmt = con.prepareStatement(insertSql);

                pstmt.setString(1, ouid);
                pstmt.setString(2, NAME);
                pstmt.setString(3, des);
                pstmt.setString(4, CODITM);
                pstmt.setString(5, MSRTITLECODE);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    //System.out.println("데이터 삽입 성공: " + name);
                }
            } catch (SQLException e) {
                System.err.println("데이터 삽입 오류: " + e.getMessage());
            } finally {
                VaultDBConnection.disconnect(con, pstmt, null);
            }
        }

    }

}
