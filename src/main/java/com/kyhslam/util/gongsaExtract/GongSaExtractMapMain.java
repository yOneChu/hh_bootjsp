package com.kyhslam.util.gongsaExtract;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 공사번호 추출 - HashMap 파라미터 버전
 * (GongSaExtractMain 의 명령줄 인자 대신 HashMap 으로 조건을 받아 수행)
 *
 * 파라미터 KEY (원본 productNumExt_left.jsp 의 form name 과 동일, 대소문자 구분 안 함)
 *   tableType     : elv(기본값) / shipelv
 *   startdate     : 등록일 시작 (필수, yyyy-MM-dd 또는 yyyyMMdd)
 *   enddate       : 등록일 종료 (필수)
 *   spec0 ~ spec9 : 영업사양 특성 CODE
 *   cond0 ~ cond9 : 영업사양 값 CODE (비우면 조건 없이 컬럼만 조회)
 *   locale        : MSR 타이틀 locale (기본 ko)
 *   out           : 결과 CSV 파일 (생략 시 CSV 저장 안 함)
 *
 * 사용 예) 다른 클래스에서 호출
 *   HashMap<String, String> paramMap = new HashMap<String, String>();
 *   paramMap.put("tableType", "elv");
 *   paramMap.put("startdate", "2025-01-01");
 *   paramMap.put("enddate", "2025-01-31");
 *   paramMap.put("spec0", "SPEC_A");
 *   paramMap.put("cond0", ">10");
 *   GongSaExtractResult result = GongSaExtractMapMain.execute(paramMap);
 *
 * 사용 예) 명령줄 실행 (KEY=VALUE 형식, 인자가 없으면 main() 의 샘플 조건으로 실행)
 *   java -cp "out;ojdbc6.jar" dyna.gongsaExtract.GongSaExtractMapMain startdate=2025-01-01 enddate=2025-01-31 spec0=SPEC_A "cond0=>10" out=result.csv
 */
public class GongSaExtractMapMain {

    public static void main(String[] args) {
        HashMap<String, String> paramMap = new HashMap<String, String>();

        if (args.length == 0) {
            // 테스트용 샘플 조건 - 필요에 맞게 수정해서 사용
            paramMap.put("tableType", "shipelv");
            paramMap.put("startdate", "2025-10-15");
            paramMap.put("enddate", "2026-10-31");

            paramMap.put("locale", "ko");
            paramMap.put("out", "공사번호추출_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");

            paramMap.put("spec0", "EL_ETM");
            paramMap.put("cond0", ",?TM?,YJ140");

            paramMap.put("spec1", "EL_ECWBG");
            paramMap.put("cond1", "550");


        } else {
            for (String arg : args) {
                int idx = arg.indexOf('=');
                if (idx <= 0) {
                    System.err.println("[ERROR] KEY=VALUE 형식이 아닙니다. : " + arg);
                    System.exit(1);
                }
                paramMap.put(arg.substring(0, idx), arg.substring(idx + 1));
            }
        }

        try {
            GongSaExtractResult result = execute(paramMap);
            GongSaExtractMain.print(result);
            System.out.println("총 " + result.getData().size() + "건");
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * HashMap 조건으로 공사번호 추출 수행
     * out 키가 있으면 결과를 CSV 로 저장
     */
    public static GongSaExtractResult execute(Map<String, String> paramMap) throws Exception {
        GongSaExtractResult result = new GongSaExtractService().search(paramMap);

        String out = GongSaExtractService.getParam(paramMap, "out");
        if (!GongSaExtractParam.isEmpty(out)) {
            File outFile = new File(out);
            GongSaCsvWriter.write(result, outFile);
            System.out.println("저장 : " + outFile.getAbsolutePath());
        }
        return result;
    }
}
