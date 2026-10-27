package com.kyhslam.excel;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.ProductCommonUtil;
import com.kyhslam.util.elv.ElvInfoUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BOM_excel_20261027 {

    /**
     * 제품번호에 연결된 영업사양 정버(기계/전기 담당자), 연계된 ECO 정보 추출
     * @param args
     */
    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        ArrayList<String> hogiList = new ArrayList<>();

        //String fileName = "d:\\excel\\20260910 사양변경내역_R1_261014.xlsx";
        String fileName = "d:\\excel\\B120B03.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;

        try {
            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);

            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            for (int i = 3000; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                // 1. 빈 행이거나, 필터/수동 숨김 처리된 행이면 건너뛰기
                if (row == null || row.getZeroHeight()) {
                    continue;
                }

                Cell cell0 = row.getCell(0); //호기번호
                String hogi = cell0.getStringCellValue();

                Cell cell5 = row.getCell(2); //BLOCKNO
                String blockNo = cell5.getStringCellValue();

                System.out.println((i) + " > hogi = " + hogi + " , " + blockNo);

                //영업사양
                //String suju = findElvData(hogi.trim());
                //row.createCell(1).setCellValue(suju);

                PartInfoDTO dto = ProductCommonUtil.findProductInfoAsBlockNo(hogi, blockNo);

                row.createCell(3).setCellValue(dto.getUCheck());
                row.createCell(4).setCellValue(dto.getPartNo());
                row.createCell(5).setCellValue(dto.getPartName());
                row.createCell(6).setCellValue(dto.getGlCode());
                row.createCell(7).setCellValue(dto.getQty());
                row.createCell(8).setCellValue(dto.getCmt());


            }

            fis.close();
            fos = new FileOutputStream(fileName);
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fos != null) fos.close();
                if (fis != null) fis.close();
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


    public static HashMap<String, String> getProductInfo(String productNo, String changeDate) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        HashMap<String, String> productInfo = new HashMap<>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    WITH OUID AS
                        (
                            SELECT B.AS$END1 AS POID
                            FROM ECOANDPART$AS B
                            WHERE
                            AS$END2 = 
                            (
                                SELECT VFOID
                                  FROM (
                                      select V.vf$ouid AS VFOID
                                            , V.MD$CDATE
                                            , V.APP_DATE
                                            , ROW_NUMBER() OVER (ORDER BY V.APP_DATE DESC, V.MD$CDATE DESC) AS RN
                                            from product$vf V,
                                                 product$id A
                                            where V.vf$identity = A.id$ouid
                                              --and V.vf$ouid = A.id$wip
                                              and md$number = ?
                                              AND SUBSTR(V.APP_DATE, 0 , 8) = REPLACE(?, '-', '')
                                  ) WHERE RN = 1
                            )
                        )
                    SELECT
                                   A.SF$OUID,
                                   A.MD$NUMBER AS ECONO, -- ECONO
                                   A.MD$DESC AS ECONAME, -- ECO명
                                   (SELECT B.USER_NAME FROM V_USER_INFO B WHERE B.USER_ID = A.MD$USER) AS CRE_USER, -- 등록자(한글)
                                   --A.EC_CLASS, -- 구분 (제품신규/제품수정/ 등)
                                   CODN(A.EC_CLASS) AS GUBUN, --구분 (제품신규/제품수정/ 등)
                                   --A.EC_CLASS_DESC,       
                                   CODN(A.EC_CLASS_DESC) AS EC_CLASS_DESC, -- 구분내역/실시시기
                                   A.PROD_MODIFY_ETC AS ETC_CONTENT, -- 기타 변경사유
                                   A.EC_REMARK_DESC AS CONTENT, -- 내용및사유
                                   SUBSTR(A.MD$CDATE, 0 , 8) AS CRE_DATE, -- 생성일
                                   SUBSTR(A.APP_DATE, 0 , 8) AS APPROVAL_DATE, -- 승인일
                                   
                                   A.REMARKS AS REMARKS1, -- 변경 전
                                   A.REMARKS2 AS REMARKS2, -- 변경 후
                                   DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CRE_DATE,
                                   DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS MOD_DATE,
                                   DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS APP_DATE
                                    FROM CHANGEORDER$SF A
                                    WHERE A.SF$OUID = (SELECT POID FROM OUID)
                    """;

            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            stmt.setString(2, changeDate);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID

                String ECONO = rs.getString("ECONO");
                String ECONAME = rs.getString("ECONAME");
                String CRE_USER = rs.getString("CRE_USER");
                String APPROVAL_DATE = rs.getString("APPROVAL_DATE");
                String GUBUN = rs.getString("GUBUN");
                String CONTENT = rs.getString("CONTENT");
                String REMARKS1 = rs.getString("REMARKS1");
                String REMARKS2 = rs.getString("REMARKS2");
                String EC_CLASS_DESC = rs.getString("EC_CLASS_DESC");


                productInfo.put("ECONO", ECONO);
                productInfo.put("ECONAME", ECONAME);
                productInfo.put("CRE_USER", CRE_USER);
                productInfo.put("APPROVAL_DATE", APPROVAL_DATE);
                productInfo.put("GUBUN", GUBUN);
                productInfo.put("CONTENT", CONTENT);
                productInfo.put("REMARKS1", REMARKS1);
                productInfo.put("REMARKS2", REMARKS2);
                productInfo.put("EC_CLASS_DESC", EC_CLASS_DESC);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return productInfo;
    }


    /** 날짜 포맷 지정 가능 (예: "yyyyMMdd") */
    public static String getCellString(Cell cell, String datePattern) {
        if (cell == null) return "";

        CellType type = cell.getCellType();
        // 수식 셀이면 계산된 결과 타입 기준으로 처리
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultType();
        }

        switch (type) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat(datePattern).format(cell.getDateCellValue());
                }
                // 1.0 → "1", 2.026E7 → "20260408" 처럼 지수/소수점 제거
                return new BigDecimal(String.valueOf(cell.getNumericCellValue()))
                        .stripTrailingZeros()
                        .toPlainString();

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
            case ERROR:
            default:
                return "";
        }
    }


    //수주명
    public static String findElvData(String searchMdNumber) {
        System.out.println(" ElvInfoUtil  ::  findElvData = " );

        String result = "";


        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        try {

            con = PLMDBConnection.getConnection();

            String query = """
                SELECT 
                    V.MD$DESC AS SUJU -- 수주명
                FROM ELV_INFO$VF V, ELV_INFO$ID A 
                  WHERE 
                      V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip 
                      --AND V.MD$NUMBER NOT LIKE 'Q%'
                      --AND V.MD$NUMBER NOT LIKE '%TEST%'
                      AND V.MD$NUMBER = ?
            """;


            stmt = con.prepareStatement(query.toString());
            stmt.setString(1, searchMdNumber);

            rs = stmt.executeQuery();

            while(rs.next()) {
                result = rs.getString("SUJU");

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return result;
    }
}