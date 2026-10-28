package com.kyhslam.util.pidTest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 비교 결과 CSV 저장 (Excel 에서 한글이 깨지지 않도록 UTF-8 BOM 포함)
 */
public class PidTestCsvWriter {

    public static void write(PidTestResult result, File file) throws IOException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        try {
            w.write('﻿');

            List<String> head = new ArrayList<String>();
            head.add("No");
            head.add("현장번호");
            head.add("상태");
            head.add(result.getSrcTitle());
            head.add(result.getTargetTitle());
            head.add("일치여부");
            head.add("실행 시간(ms)");
            head.addAll(result.getSpecs());
            head.add("메시지");
            head.add("PID 오류");
            writeLine(w, head);

            for (PidTestRow row : result.getRows()) {
                List<String> line = new ArrayList<String>();
                line.add(String.valueOf(row.getNo()));
                line.add(row.getProductNo());
                line.add(row.getStatus());
                line.add(row.getSrcResult());
                line.add(row.getTargetResult());
                line.add(String.valueOf(row.isMatch()));
                line.add(String.valueOf(row.getElapsedMs()));
                for (String spec : result.getSpecs()) {
                    line.add(row.getSpecValues().get(spec));
                }
                line.add(row.getMessage());
                line.add(join(row.getErrors()));
                writeLine(w, line);
            }
        } finally {
            w.close();
        }
    }

    private static String join(List<String> values) {
        StringBuffer sb = new StringBuffer();
        for (String v : values) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(v);
        }
        return sb.toString();
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
