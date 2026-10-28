package com.kyhslam.excel;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.ProductCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BOM_excel_20261028 {

    // 시트0(입력) 데이터 시작 행 (0 = 헤더 없음, 1 = 첫 행이 헤더)
    private static final int SRC_START_ROW = 1;
    // 시트1(출력) 데이터 시작 행 (상단 헤더 2줄 가정)
    private static final int DEST_START_ROW = 2;

    /**
     * 시트0의 호기/수주명/BLOCKNO 로 하위 자재를 조회하여 시트1에 일렬로 작성
     * @param args
     */
    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        String fileName = "d:\\excel\\B120B03.xlsx";
        // 원본 보호를 위해 결과는 별도 파일로 저장
        String outFileName = fileName.replace(".xlsx", "_result.xlsx");

        Workbook workbook = null;

        try (FileInputStream fis = new FileInputStream(fileName)) {
            // 1. 엑셀 파일 읽기
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            // 2. 출력 시트 준비 (없으면 생성, 기존 데이터 행은 비움)
            Sheet sheet1 = workbook.getNumberOfSheets() > 1 ? workbook.getSheetAt(1) : workbook.createSheet("자재목록");
            for (int r = sheet1.getLastRowNum(); r >= DEST_START_ROW; r--) {
                Row old = sheet1.getRow(r);
                if (old != null) sheet1.removeRow(old);
            }

            // 동일 호기+BLOCKNO 중복 조회 방지
            HashMap<String, ArrayList<PartInfoDTO>> cache = new HashMap<>();

            int writeRow = DEST_START_ROW;
            int srcCnt = 0;
            int noDataCnt = 0;

            // getPhysicalNumberOfRows 는 빈 행을 세지 않아 마지막 행이 누락될 수 있으므로 getLastRowNum 사용
            int lastRow = sheet.getLastRowNum();

            //for (int i = SRC_START_ROW; i <= lastRow; i++) {
            for (int i = SRC_START_ROW; i <= lastRow; i++) {
                Row row = sheet.getRow(i);

                // 빈 행이거나, 필터/수동 숨김 처리된 행이면 건너뛰기
                if (row == null || row.getZeroHeight()) {
                    continue;
                }

                String hogi = getCellString(row.getCell(0), "yyyyMMdd");    //호기번호
                String suju = getCellString(row.getCell(1), "yyyyMMdd");    //수주명
                String blockNo = getCellString(row.getCell(2), "yyyyMMdd"); //BLOCKNO

                if (hogi.isEmpty() || blockNo.isEmpty()) {
                    continue;
                }

                srcCnt++;
                System.out.println(i + " > hogi = " + hogi + " , " + blockNo);

                ArrayList<PartInfoDTO> bList = cache.computeIfAbsent(hogi + "|" + blockNo,
                        k -> findProductInfoAsBlockNo(hogi, blockNo));

                // 조회 결과가 없으면 표시용 1행만 작성
                if (bList.isEmpty()) {
                    noDataCnt++;
                    Row ssRow = sheet1.createRow(writeRow++);
                    ssRow.createCell(0).setCellValue(hogi);
                    ssRow.createCell(1).setCellValue(suju);
                    ssRow.createCell(2).setCellValue(blockNo);
                    ssRow.createCell(4).setCellValue("조회결과 없음");
                    continue;
                }

                // 결과 건수만큼 행을 늘려가며 일렬로 작성
                for (PartInfoDTO dto : bList) {
                    Row ssRow = sheet1.createRow(writeRow++);

                    ssRow.createCell(0).setCellValue(hogi);
                    ssRow.createCell(1).setCellValue(suju);
                    ssRow.createCell(2).setCellValue(blockNo);

                    ssRow.createCell(3).setCellValue(nvl(dto.getUCheck()));
                    ssRow.createCell(4).setCellValue(nvl(dto.getPartNo()));
                    ssRow.createCell(5).setCellValue(nvl(dto.getPartName()));
                    ssRow.createCell(6).setCellValue(nvl(dto.getGlCode()));
                    ssRow.createCell(7).setCellValue(nvl(dto.getQty()));
                    ssRow.createCell(8).setCellValue(nvl(dto.getCmt()));
                }
            }

            System.out.println("입력 " + srcCnt + "건 / 출력 " + (writeRow - DEST_START_ROW) + "행 / 조회결과 없음 " + noDataCnt + "건");

            // 3. 결과 저장
            try (FileOutputStream fos = new FileOutputStream(outFileName)) {
                workbook.write(fos);
            }
            System.out.println("저장 완료 : " + outFileName);
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


    private static String nvl(String s) {
        return s == null ? "" : s;
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

    public static ArrayList<PartInfoDTO> findProductInfoAsBlockNo(String productNo, String blockNo) {
        System.out.println("PartCommonUtil findProductInfo start ==-" + productNo );


        ArrayList<PartInfoDTO> list = new ArrayList<>();



        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
              ( select vf$ouid AS VFOID from product$vf, product$id
                where vf$identity = id$ouid and vf$ouid = id$wip
                and (
                      md$number = ?
                    )
              )
             SELECT
                         PE.ASSOOUID ASSOOUID
                         , PE.PRODUCTOUID PRODUCTOUID
                         , PE.PARTOUID PARTOUID
                         , PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                         , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                         , NP.MD$NUMBER AS PARTNO
                         , CODN(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS VERSION
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , NP.OLD_CODE
                         , NP.OLD_CODE2
                         , NP.OLD_CODE3
                         , COD(NP.SPT) SPT
                         , COD(NP.PARTMPCHECK) PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                         , PE.CUSER CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = (SELECT VFOID FROM ouid)
                 AND (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) = ?
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            stmt.setString(2, blockNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String PARTOUID = rs.getString("PARTOUID");
                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO"); // 자재번호
                String PARTNAME = rs.getString("PARTNAME"); //자재명
                String VERSION = rs.getString("VERSION"); //버전

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                String HASCHILD = rs.getString("HASCHILD");

                PartInfoDTO result = new PartInfoDTO();
                result.setPartNo(PARTNO);
                result.setPartName(PARTNAME);
                result.setSpec(SPEC);
                result.setBlockNo(BLOCKNO);
                result.setGlCode(GLCODE);
                result.setQty(QTY);
                result.setNation(NATION);
                result.setUCheck(UCHECK);
                result.setCmt(CMT);

                list.add(result);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }
}