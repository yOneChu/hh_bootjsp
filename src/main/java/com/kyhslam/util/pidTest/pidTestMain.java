package com.kyhslam.util.pidTest;

import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;
import com.kyhslam.util.pidSimulatorUp.SimUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * PID Tester - HashMap 파라미터로 실행 (원본: jsp/plmetc/variant/PIDTester/PIDTester.jsp)
 * 같은 PID 의 기준 버전과 대상 버전을 여러 현장(호기)에 실행해서 결과가 같은지 비교한다.
 *
 * 파라미터 KEY (원본 PIDTester_left.jsp 의 form name 과 동일, 대소문자 구분 안 함)
 *   PID              : 테스트할 PID (필수)
 *   srcVersion       : 기준 버전 - lastest(최신, 기본값) / test(-1) / 버전번호
 *   targetVersion    : 대상 버전 - test(-1, 기본값) / lastest / 버전번호
 *   productList      : 현장번호 목록 (줄바꿈/콤마 구분, List 도 가능). 생략 시 테스트하기 적합한 현장 자동 조회
 *   specList         : 결과에 추가로 보여줄 영업사양 코드 (줄바꿈 구분). PID 사용 SPEC 은 자동 포함
 *   getMore          : Y 면 현장 자동 조회 범위 최근 3년 (기본 1년)
 *   maxCount         : 테스트할 최대 현장 수 (기본 0 = 제한 없음)
 *   timeoutSec       : 현장 1건당 제한 시간 (기본 30초)
 *   stopOnTimeout    : 타임아웃 시 전체 종료 여부 (기본 Y - 원본과 동일)
 *   saveErrorLog     : Y 면 PID 실행 오류를 운영 VARIANT_ERRORLOG 에 기록 (기본 N - 결과의 errors 로만 확인)
 *   codeFieldDisplay : 코드 특성 _CODE_NAME 표시 방식 (기본 N - 운영 etcConfig.xml 과 동일하게 코드명만)
 *   out              : 결과 CSV 파일 (생략 시 CSV 저장 안 함)
 *
 * 사용 예) 다른 클래스에서 호출
 *   HashMap<String, Object> paramMap = new HashMap<String, Object>();
 *   paramMap.put("PID", "EL1234_Q_01");
 *   paramMap.put("srcVersion", "lastest");
 *   paramMap.put("targetVersion", "test");
 *   paramMap.put("productList", "2512345L01\n2512346L01");
 *   PidTestResult result = pidTestMain.execute(paramMap);
 *   result.getMismatchProducts();   // 결과가 다른 현장번호
 *
 * 사용 예) 명령줄 실행 (KEY=VALUE 형식, 인자가 없으면 main() 의 샘플 조건으로 실행)
 *   java -cp "out;ojdbc6.jar" com.kyhslam.util.pidTest.pidTestMain PID=EL1234_Q_01 targetVersion=test "productList=2512345L01,2512346L01" out=result.csv
 *
 * 공사정보 로드 / PID 실행은 PID 시뮬레이터(pidSimulatorUp) 와 같은 코드를 사용한다. (METHOD=JAVA PID 도 실행 가능)
 */
public class pidTestMain {

    public static void main(String[] args) {
        HashMap<String, Object> paramMap = new HashMap<String, Object>();

        if (args.length == 0) {
            // 테스트용 샘플 조건 - 필요에 맞게 수정해서 사용
            paramMap.put("PID", "A101A");
            paramMap.put("srcVersion", "lastest");
            paramMap.put("targetVersion", "lastest");
            paramMap.put("productList", "211261L01");    // 비우면 자동 조회
            paramMap.put("maxCount", "20");
            paramMap.put("timeoutSec", "30");
            // paramMap.put("out", "PIDTest_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv");
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
            PidTestResult result = execute(paramMap);
            print(result);
            System.exit(result.isSuccess() ? 0 : 1);
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * HashMap 조건으로 PID 테스트 수행
     * out 키가 있으면 결과를 CSV 로 저장
     */
    public static PidTestResult execute(Map<String, ?> paramMap) throws Exception {
        PidTestParam param = PidTestParam.from(paramMap);
        System.out.println("검색조건 : " + param);

        PidTestResult result = new PidTestService().run(param);

        String out = PidTestParam.get(paramMap, "out");
        if (SimUtil.hasText(out)) {
            File outFile = new File(out.trim());
            PidTestCsvWriter.write(result, outFile);
            System.out.println("저장 : " + outFile.getAbsolutePath());
        }
        return result;
    }

    /** 결과 요약 출력 */
    public static void print(PidTestResult result) {
        System.out.println("==================================================");
        if (!result.isSuccess()) {
            System.out.println("[실패] " + result.getMessage());
            return;
        }

        System.out.println("PID : " + result.getPid());
        System.out.println(result.getSrcTitle() + " : " + toString(result.getSrcHeader()));
        System.out.println(result.getTargetTitle() + " : " + toString(result.getTargetHeader()));
        System.out.println("사용 SPEC : " + result.getSpecs());
        System.out.println("총 " + result.getTotalCount() + "건 / 일치 " + result.getMatchCount() + "건 / 불일치 " + result.getMismatchCount()
                + "건 / 실행실패 " + result.getFailedRows().size() + "건 (" + result.getElapsedMs() + "ms)");
        if (result.getMessage() != null) {
            System.out.println("메시지 : " + result.getMessage());
        }

        if (result.getMismatchCount() > 0) {
            System.out.println("--- 불일치 ---");
            for (PidTestRow row : result.getMismatchRows()) {
                System.out.println(row.getNo() + ". " + row.getProductNo());
                System.out.println("    기준 : " + PidTestRow.oneLine(row.getSrcResult()));
                System.out.println("    대상 : " + PidTestRow.oneLine(row.getTargetResult()));
                System.out.println("    SPEC : " + row.getSpecValues());
            }
        }
        if (!result.getFailedRows().isEmpty()) {
            System.out.println("--- 실행실패 ---");
            for (PidTestRow row : result.getFailedRows()) {
                System.out.println(row.getNo() + ". " + row.getProductNo() + " [" + row.getStatus() + "] " + row.getMessage());
            }
        }
    }

    private static String toString(PidInfo pid) {
        return pid == null ? "" : "[pid=" + pid.getPid() + ", version=" + pid.getVersion() + ", method=" + pid.getMethod() + ", name=" + pid.getName() + "]";
    }
}
