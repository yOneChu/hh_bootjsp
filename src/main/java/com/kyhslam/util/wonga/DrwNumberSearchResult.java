package com.kyhslam.util.wonga;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 원가절감 실적조회 결과 (head / data / mengeSum).
 */
public class DrwNumberSearchResult {

    public static final int MAX_ROWS = 10000;

    public List<String> head = new ArrayList<String>();
    public List<List<String>> data = new ArrayList<List<String>>();
    public int mengeSum = 0;

    public int size() {
        return data.size();
    }

    /**
     * _right.jsp 의 excel 다운로드 대신 CSV(UTF-8 BOM, 엑셀에서 바로 열림)로 저장.
     * null 값은 화면과 동일하게 "*" 로 출력.
     */
    public void writeCsv(String filePath) throws IOException {

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
            pw.write('﻿');
            pw.println(toCsvLine(head));
            for (List<String> row : data) {
                pw.println(toCsvLine(row));
            }
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    public void printToConsole() {

        System.out.println(" - 총" + size() + "건(총자재수량 " + mengeSum + "개)");
        System.out.println(String.join("\t", head));
        for (List<String> row : data) {
            List<String> line = new ArrayList<String>();
            for (String col : row) {
                line.add(col == null ? "*" : col);
            }
            System.out.println(String.join("\t", line));
        }
    }

    private static String toCsvLine(List<String> cols) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0)
                sb.append(',');
            String v = cols.get(i) == null ? "*" : cols.get(i);
            sb.append('"').append(v.replace("\"", "\"\"")).append('"');
        }
        return sb.toString();
    }
}
