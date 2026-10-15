package com.kyhslam.util.gongsaExtract;

import java.io.*;
import java.util.List;

/**
 * 조회 결과 CSV 저장 (Excel 에서 한글이 깨지지 않도록 UTF-8 BOM 포함)
 * (원본: productNumExt_right.jsp 의 excel 다운로드)
 */
public class GongSaCsvWriter {

    public static void write(GongSaExtractResult result, File file) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        try {
            write(result, w);
        } finally {
            w.close();
        }
    }

    /** Writer 로 CSV 출력 (Writer 는 호출한 쪽에서 close) */
    public static void write(GongSaExtractResult result, Writer w) throws IOException {
        w.write('﻿');
        writeLine(w, result.getTitles());
        for (List<String> row : result.getData()) {
            writeLine(w, row);
        }
        w.flush();
    }

    private static void writeLine(Writer w, List<String> values) throws IOException {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                w.write(',');
            }
            w.write(escape(values.get(i)));
        }
        w.write("\r\n");
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
