package com.kyhslam.util.gongsaExtract;

import com.kyhslam.service.GongSaExtractService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 공사번호 추출 (원본: WebContent/jsp/plmetc/product/productNumExt/productNumExt.jsp)
 *
 * 사용법
 *   java dyna.gongsaExtract.GongSaExtractMain -start 2025-01-01 -end 2025-12-31
 *        [-type elv|shipelv] [-spec 특성CODE[=값CODE]]... [-locale ko] [-out 결과.csv]
 *
 * 예)
 *   java -cp .;ojdbc8.jar dyna.gongsaExtract.GongSaExtractMain -type elv -start 2025-01-01 -end 2025-12-31 ^
 *        -spec "SPEC_A=>10" -spec "SPEC_B=,A,B" -spec SPEC_C -out result.csv
 */
public class GongSaExtractMain {

    public static void main(String[] args) {
        GongSaExtractParam param;
        File outFile;
        try {
            param = new GongSaExtractParam();
            outFile = parseArgs(args, param);
            param.validate();
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] " + e.getMessage());
            printUsage();
            System.exit(1);
            return;
        }

        try {
            System.out.println("검색조건 : " + param);

            GongSaExtractResult result = new GongSaExtractService().search(param);
            print(result);

            GongSaCsvWriter.write(result, outFile);
            System.out.println("총 " + result.getData().size() + "건, 저장 : " + outFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * 인자 파싱
     *   -spec 값은 "특성CODE=값CODE" 형식 (첫 번째 '=' 기준으로 분리, 값CODE 가 '=' 로 시작해도 됨: SPEC==A)
     *   값CODE 없이 특성CODE 만 주면 조건 없이 컬럼만 조회
     * @return 결과 파일
     */
    static File parseArgs(String[] args, GongSaExtractParam param) {
        String out = null;

        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            if (key.equals("-h") || key.equals("-help") || key.equals("--help")) {
                printUsage();
                System.exit(0);
            }
            if (i + 1 >= args.length) {
                throw new IllegalArgumentException(key + " 의 값이 없습니다.");
            }
            String value = args[++i];

            if (key.equals("-type")) {
                param.setTableType(value);
            } else if (key.equals("-start")) {
                param.setStartDate(value);
            } else if (key.equals("-end")) {
                param.setEndDate(value);
            } else if (key.equals("-locale")) {
                param.setLocale(value);
            } else if (key.equals("-out")) {
                out = value;
            } else if (key.equals("-spec")) {
                int idx = value.indexOf('=');
                if (idx < 0) {
                    param.addSpecCondition(value, null);
                } else {
                    param.addSpecCondition(value.substring(0, idx), value.substring(idx + 1));
                }
            } else {
                throw new IllegalArgumentException("알 수 없는 인자입니다. : " + key);
            }
        }

        if (out == null) {
            out = "공사번호추출_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";
        }
        return new File(out);
    }

    static void print(GongSaExtractResult result) {
        System.out.println(join(result.getTitles()));
        for (List<String> row : result.getData()) {
            System.out.println(join(row));
        }
    }

    private static String join(List<String> values) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append('\t');
            }
            sb.append(values.get(i) == null ? "" : values.get(i));
        }
        return sb.toString();
    }

    private static void printUsage() {
        System.out.println("Usage: java dyna.gongsaExtract.GongSaExtractMain -start <yyyy-MM-dd> -end <yyyy-MM-dd>");
        System.out.println("         [-type elv|shipelv] [-spec 특성CODE[=값CODE]] (최대 10개) [-locale ko] [-out 결과.csv]");
        System.out.println();
        System.out.println("  -type    elv(엘리베이터, 기본값) / shipelv(선박용 엘리베이터)");
        System.out.println("  -start   등록일 시작 (필수)");
        System.out.println("  -end     등록일 종료 (필수)");
        System.out.println("  -spec    영업사양 조건. 값CODE 문법");
        System.out.println("             N      : N 또는 NULL           !N     : N/NULL 제외");
        System.out.println("             ,A,B   : A 또는 B 포함(정규식)    !A,B   : A,B 미포함 또는 NULL");
        System.out.println("             >10    : 숫자 비교 (>10,<20 은 AND)");
        System.out.println("             =A     : A 와 같음              *      : NULL");
        System.out.println("             그 외   : 입력값과 같음");
        System.out.println("  -locale  MSR 타이틀 locale (기본 ko)");
        System.out.println("  -out     결과 CSV 파일 (기본 공사번호추출_yyyyMMdd_HHmmss.csv)");
    }
}
