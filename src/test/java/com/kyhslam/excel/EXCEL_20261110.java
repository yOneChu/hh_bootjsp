package com.kyhslam.excel;

import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;
import com.kyhslam.util.pidSimulatorUp.SimVariantMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Block No. 별 영업사양 코드 추출
 * - A열(0) 의 BlockNo 로 PID 'EL_P' + BlockNo 의 최신 버전 로직을 조회
 * - 로직 SPEC1~30 열의 특성코드 중 영업사양 코드(dosfld 특성코드 사전에 있는 것)만 골라
 *   C열(2) 부터 가로로 순서대로 기록 (로직 등장 순, 중복 제거)
 */
public class EXCEL_20261110 {

    /** 영업사양 특성코드 사전 : 육상(2248993771) + 선박용(2706224418) */
    private static final String CODE_DICT_SQL =
            " SELECT A.NAME AS CODE FROM HDEL_SYSTEM.DOSFLD A WHERE A.DOSCLAS IN ('2248993771', '2706224418') ";

    /** SPEC 수식 안의 {코드} 참조 */
    private static final Pattern SPEC_REF = Pattern.compile("\\{([^{}]+)\\}");

    /** 영업사양 기록 시작 열 (0부터, 2 = C열) */
    private static final int START_COL = 2;

    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        String fileName = "c:\\excel\\법인 및 직도입품 반영 Block No.xlsx";
        // 원본 보호를 위해 결과는 별도 파일로 저장
        //String outFileName = fileName.replace(".xlsx", "_result.xlsx");

        DataFormatter fmt = new DataFormatter();
        SimPidRepository repo = new SimPidRepository();
        Map<String, List<String>> pidSpecCache = new HashMap<>(); // 같은 BlockNo 반복 시 재조회 방지

        Workbook workbook = null;

        try (SimDb db = SimDb.open()) {

            // 0. 영업사양 코드 사전
            Set<String> salesCodes = new HashSet<>();
            for (Map<String, String> r : db.queryForList(CODE_DICT_SQL)) {
                String code = r.get("CODE");
                if (code != null && !code.trim().isEmpty())
                    salesCodes.add(code.trim().toUpperCase());
            }
            System.out.println("영업사양 코드 사전 = " + salesCodes.size() + "건");

            // 1. 엑셀 파일 읽기
            try (FileInputStream fis = new FileInputStream(fileName)) {
                workbook = new XSSFWorkbook(fis);
            }
            Sheet sheet = workbook.getSheetAt(1);

            int lastRow = sheet.getLastRowNum();

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String blockNo = fmt.formatCellValue(row.getCell(0)).trim(); //BlockNO
                if (blockNo.isEmpty()) continue;

                String pid = blockNo.startsWith("EL_P") ? blockNo : "EL_P" + blockNo;

                List<String> specs = pidSpecCache.get(pid);
                if (specs == null) {
                    specs = findSalesSpecs(db, repo, pid, salesCodes);
                    pidSpecCache.put(pid, specs);
                }

                System.out.println(i + " > " + pid + " : " + (specs == null ? "로직 없음" : specs.size() + "개 " + specs));

                // 2열부터 기존 값 지우고 영업사양 가로 입력
                clearFrom(row, START_COL);
                if (specs == null) {
                    row.createCell(START_COL).setCellValue("로직 없음");
                    continue;
                }
                for (int c = 0; c < specs.size(); c++) {
                    row.createCell(START_COL + c).setCellValue(specs.get(c));
                }
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }

    /**
     * PID 최신 버전 로직의 SPEC 열에서 영업사양 코드만 추출 (로직 등장 순, 중복 제거, 주석(#) 라인 제외)
     * @return PID 가 없으면 null
     */
    @SuppressWarnings("rawtypes")
    private static List<String> findSalesSpecs(SimDb db, SimPidRepository repo, String pid, Set<String> salesCodes) throws Exception {
        SimPidRepository.PidInfo info = repo.getLastPid(db, pid);
        if (info == null) return null;

        SimVariantMap logic = repo.getLogic(db, pid, info.getVersion());
        Object data = logic.get("data");
        Set<String> result = new LinkedHashSet<>();
        if (!(data instanceof List)) return new ArrayList<>(result);

        for (Object o : (List) data) {
            SimVariantMap line = (SimVariantMap) o;
            Object addr = line.get("ADDR");
            if (addr != null && "#".equals(String.valueOf(addr).trim())) continue; // 주석 라인

            Object specList = line.get("specList");
            if (!(specList instanceof List)) continue;

            for (Object spec : (List) specList) {
                if (spec == null) continue;
                String s = String.valueOf(spec);
                if (s.contains("[$") && s.contains("$]")) {
                    // 수식형 SPEC : 안의 {코드} 참조 (pidSimulatorUp 영업사양 영향도와 동일 규칙)
                    Matcher m = SPEC_REF.matcher(s);
                    while (m.find()) {
                        String code = normalize(m.group(1));
                        if (code != null && salesCodes.contains(code)) result.add(code);
                    }
                } else {
                    String code = normalize(s);
                    if (code != null && salesCodes.contains(code)) result.add(code);
                }
            }
        }
        return new ArrayList<>(result);
    }

    /** XXX_CODE_NAME → XXX, 대문자 */
    private static String normalize(Object raw) {
        if (raw == null) return null;
        String code = String.valueOf(raw).trim().toUpperCase();
        if (code.endsWith("_CODE_NAME") && code.length() > 10)
            code = code.substring(0, code.length() - 10);
        return code.isEmpty() ? null : code;
    }

    /** 재실행 시 이전 결과가 남지 않도록 지정 열부터 끝까지 셀 제거 */
    private static void clearFrom(Row row, int fromCol) {
        short last = row.getLastCellNum(); // 마지막 셀 + 1, 셀 없으면 -1
        for (int c = last - 1; c >= fromCol; c--) {
            Cell cell = row.getCell(c);
            if (cell != null) row.removeCell(cell);
        }
    }
}
