package com.kyhslam.excel;

import com.kyhslam.util.pidSimulatorUp.PIDSimul;
import com.kyhslam.util.pidSimulatorUp.PIDSimulResult;
import com.kyhslam.util.pidSimulatorUp.SimDb;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BOM_excel_API_20261028 {

    /**
     * PID시뮬 돌려서 특정값 추출
     * @param args
     */
    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        String fileName = "c:\\excel\\BBB_서동기.xlsx";
        // 원본 보호를 위해 결과는 별도 파일로 저장
        String outFileName = fileName.replace(".xlsx", "_result.xlsx");

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;

        try {

            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);

            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            for (int i = 70; i < 75; i++) {
                Row row = sheet.getRow(i);

                Cell cell0 = row.getCell(0); //호기번호
                String hogi = cell0.getStringCellValue();

                Cell cell1 = row.getCell(1); //PID
                String pidName = cell1.getStringCellValue();

                PIDSimulResult result = simulatePid(hogi, pidName);

                String deadWtCmt = getResultValue(result, "DEAD_WT_CMT");
                String newBalanceWtCmt = getResultValue(result, "NEW_BALANCE_WT_CMT");
                String ncBalanceWtCmt = getResultValue(result, "XC_BALANCE_WT_CMT");


                row.createCell(2).setCellValue(deadWtCmt);
                row.createCell(3).setCellValue(newBalanceWtCmt);
                row.createCell(4).setCellValue(ncBalanceWtCmt);

                System.out.println((i) +  " [" + hogi + " / " + pidName + "] DEAD_WT_CMT=" + deadWtCmt + ", NEW_BALANCE_WT_CMT=" + newBalanceWtCmt);

            }

            fis.close();
            fos = new FileOutputStream(fileName);
            workbook.write(fos);

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
     * PID 시뮬레이션 (최신 버전) 실행 후 결과를 콘솔에 출력한다.
     * (PIDSimulatorUpController.simulatePid 와 동일 : floor / beforePid / subVersions 없음)
     * @param hogi    호기번호 ex) 206663L01
     * @param pidName PID 명 ex) D375A
     * @return 실행 결과 (실패시 null)
     */
    public static PIDSimulResult simulatePid(String hogi, String pidName) {
        System.out.println("==================================================");
        System.out.println("simulatePid hogi:" + hogi + ", pid:" + pidName);

        try (SimDb db = SimDb.open()) {
            // version null : 최신 버전
            PIDSimulResult result = PIDSimul.run(db, hogi.trim(), pidName.trim(), null, (Integer) null, null, false);
            //result.print(System.out, false);
            return result;
        } catch (Exception e) {
            //System.out.println("[ERROR] simulatePid hogi:" + hogi + ", pid:" + pidName + " : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 시뮬레이션 최종 결과값(resultMap)에서 key 의 값을 꺼낸다.
     * @return 값 (결과가 없거나 key 가 없으면 "")
     */
    public static String getResultValue(PIDSimulResult result, String key) {
        if (result == null || result.resultMap == null)
            return "";
        Object value = result.resultMap.get(key);
        return value == null ? "" : String.valueOf(value);
    }
}
