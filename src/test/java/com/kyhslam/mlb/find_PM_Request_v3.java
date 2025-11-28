package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.PartCommonUtil;
import com.kyhslam.util.ProductCommonUtil;
import com.kyhslam.util.SubaeCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class find_PM_Request_v3 {

    public static void main(String[] args) {


        //Path inputPath = Path.of("C:/excel/sample.xlsx");   // 원본 파일
        //Path outputPath = Path.of("C:\\excel\\sample_out.xlsx"); // 저장 파일
        //Path outputPath = Path.of("C:\\excel\\sample_11111111111.xlsx"); // 저장 파일

        String filePath = "";
        filePath = "C:\\excel\\글로벌소싱_20251127.xlsx"; // 읽을 엑셀 파일 경로


        //KEY: OID 에 연결된 그 하위 BOM
        HashMap<String, ArrayList<ProductDto>> productOIDBOM = new HashMap<>();

        HashMap<String, ArrayList<ProductDto>> productMap = new HashMap<>();

        HashMap<String, String> dupExportMap = new HashMap<>();


        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {


            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 읽기

            // 첫 번째 행(헤더)은 건너뛴다고 가정 (row 0은 헤더)
            //for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            //for (int rowIndex = 5001; rowIndex <= 8000; rowIndex++) {
            for (int rowIndex = 7045; rowIndex <= 12000; rowIndex++) {

                System.out.println("excel row == " + rowIndex);

                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // 각 컬럼 값 읽기
                String productNo = getCellValue(row.getCell(0));
                //String productVersion = getCellValue(row.getCell(1));
                //String productCreDate = getCellValue(row.getCell(2));
                //String partNo = getCellValue(row.getCell(3));

                // N: 리모델링현장 제외
                if(productNo.startsWith("N")) {
                    continue;
                }


                String partNo = getCellValue(row.getCell(4)); //자재번호

                String partName = getCellValue(row.getCell(5));
                String qty = getCellValue(row.getCell(6)); //수량변경이력
                String spec = getCellValue(row.getCell(7)); // 주석변경이력
                String firstRegDate = getCellValue(row.getCell(8)); // 최초등록일


                String cmt = getCellValue(row.getCell(9)); // 최초
                String exportDate = getCellValue(row.getCell(10)); // 출하일


                //출하예정일
                //productNo = productNo += ";";


                if(!dupExportMap.containsKey(productNo)){
                    ArrayList<HashMap<String, String>> exportList = PartCommonUtil.getExportDate(productNo + ";");
                    if(exportList != null && exportList.size()>0){
                        HashMap<String, String> exportMap = exportList.get(0);
                        exportDate = exportMap.get("SHIP_B");

                        dupExportMap.put(productNo, exportDate);
                    }
                } else {
                    exportDate = dupExportMap.get(productNo);
                }


                firstRegDate = ""; // COMPEN CHAIN 최초 등록일 -> 해당 제품 승인일
                String getProdAppDate = ""; // 제품 승인일

                // 데이터쓰기
                ArrayList<ProductDto> productOids = new ArrayList<>();

                // 모든 버전의 제품 OID 조회
                //productOids = SubaeCommonUtil.findProductALLInfo(productNo.trim());

                if(productMap != null && productMap.containsKey(productNo.trim())) {
                    productOids = productMap.get(productNo.trim());

                } else {
                    // 모든 버전의 제품 OID 조회
                    productOids = SubaeCommonUtil.findProductALLInfo(productNo.trim());
                    productMap.put(productNo.trim(), productOids);
                }

                //System.out.println(productOids.size());
                //ArrayList<ProductDto> dataList = new ArrayList<>();

                String modDate = "";
                String firstVersion = ""; // 최초 해당자재 등록된 버전
                String beforeQty = "";
                String beforeCmt = "";
                String qtyProcess = "";
                String cmtProcess = "";


                for (int i = 0; i < productOids.size(); i++) {
                    ProductDto dto = productOids.get(i);
                    String oid = dto.getProductOid();
                    String prodNo = dto.getProductNo();
                    String prodName = dto.getProductName();
                    String prodVersion = dto.getProductVersion();
                    String prodCreDate = dto.getProductCreDate();
                    String prodModDate =  dto.getProductModDate();
                    String prodAppDate = dto.getProductAppdate();


                    // 해당 버전의 BOM 자재 조회

                    //ArrayList<ProductDto> bomList = ProductCommonUtil.findProductBOMWithOID_partNo(oid, partNo);
                    ArrayList<ProductDto> bomList = new ArrayList<>();

                    if (productOIDBOM.containsKey(oid)) {
                        bomList = productOIDBOM.get(oid);
                    } else {
                        bomList = ProductCommonUtil.findProductBOMWithOID(oid);
                        productOIDBOM.put(oid,  bomList);
                    }

                    for (int j = 0; j < bomList.size(); j++) {
                        ProductDto dd = bomList.get(j);
                        String vPartNo = dd.getPartNo();
                        String vPartName = dd.getPartName();
                        String vQty = dd.getQty();
                        String vCmt =  dd.getCmt();

                        if (vPartNo.equals(partNo)) {
                            //if (vPartNo.contains("MAIN ROPE")) {

                            if ("".equals(firstVersion)) {
                                firstVersion = prodVersion;
                            }

                            // 최초등록일
                            if ("".equals(getProdAppDate)) {
                                getProdAppDate = prodModDate;
                            }


                            //변경등록일
                            if("".equals(beforeQty)) {
                                beforeQty = vQty;
                                qtyProcess += vQty;
                            } else {

                                if( !beforeQty.equals(vQty) ) {

                                    qtyProcess += " > " + vQty;
                                    beforeQty = vQty;

                                    System.out.println("변경함-- : " + prodCreDate);

                                    if ("".equals(modDate)) {
                                        //modDate = prodAppDate; // 변경등록일
                                        modDate = prodModDate;
                                    }

                                }
                            }

                            if("".equals(beforeCmt)) {
                                beforeCmt = vCmt;
                                cmtProcess += vCmt;
                            } else {

                                if(beforeCmt != null && !"".equals(beforeCmt)) {
                                    if( !beforeCmt.equals(vCmt) ) {

                                        cmtProcess += " > " + vCmt;
                                        beforeCmt = vQty;

                                    }
                                }

                            }


                        }
                    } // end for





                } // end for product



                //firstVersion
                //row.createCell(1).setCellValue(firstVersion);

                row.createCell(6).setCellValue(qtyProcess);

                row.createCell(7).setCellValue(cmtProcess);

                //row.createCell(8).setCellValue(getProdAppDate);
                Cell cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 최초 등록일
                cell.setCellValue(getProdAppDate);


                //modDate
                row.createCell(9).setCellValue(modDate); // 변경등록일

                row.createCell(10).setCellValue(exportDate); // 출하일
                //Cell cell = row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 최초 등록일
                //cell.setCellValue(getProdAppDate);


                //System.out.println("exportDate == " + exportDate);

                if(exportDate != null && !"".equals(exportDate)){
                    //row.createCell(13).setCellValue(com.kyhslam.util.DateUtil.formatDate(exportDate)); // 출하일
                }


                //Cell cell = row.getCell(13, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // 최초 등록일
                //cell.setCellValue(exportDate);

                //System.out.println(productNo + " > " + partNo + " > " + getProdAppDate + " > " + qtyProcess + " :: " + getProdAppDate);



                // 출력 (또는 DTO에 매핑 가능)
                //System.out.printf("제품번호=%s, 버전=%s, 등록일=%s, 자재번호=%s, 버전=%s%n",
                //productNo, productVersion, productCreDate, partNo, partName);

            } // end for excel

            //filePath
            //try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" ------------------ end ------------------");

    }


    // 셀 값을 문자열로 변환하는 유틸 함수
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 날짜 형식인 경우
                    return cell.getDateCellValue().toString();
                } else {
                    // 숫자인 경우
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }


    public static ArrayList<ProductDto> findPartOfProduct_v2(String productOID, String partNo, ArrayList<ProductDto> dataList) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        //ArrayList<ProductDto> dataList = new ArrayList<ProductDto>();

        try {
            con = PLMDBConnection.getConnection();

            String sql = """     
                SELECT
                          PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO
                         , (SELECT F.VF$VERSION FROM PRODUCT$VF F WHERE F.VF$OUID = PE.PRODUCTOUID) AS PARENT_VER
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_CREDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_MODDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_APP_DATE
                         , (SELECT PRODUCT.MD$STATUS FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_STATUS
                         , (SELECT COD(E.EL_ATYP) FROM ELV_INFO$ID A, ELV_INFO$VF E
                            WHERE A.ID$OUID = E.VF$IDENTITY AND E.vf$ouid = A.id$wip
                            AND E.MD$NUMBER = (SELECT F.MD$NUMBER FROM PRODUCT$VF F WHERE F.VF$OUID = PE.PRODUCTOUID) ) AS GISONG
                         , NP.MD$NUMBER AS PARTNO
                         , cod(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS PART_VERSION
                         , PE.CMT AS CMT
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) AS UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY AS PART_QTY
                         , VP.WORK_QTY
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(COD(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , COD(NP.PARTMPCHECK) AS PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) AS CUSERNAME
                         , PE.CUSER AS CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK AS UCHECK  -- 수정여부
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                        -- , DATEFORMAT(VP.MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS 등록일
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) HASCHILD -- 하위BOM 존재여부
                        FROM
                         PARTOFEBOM PE
                        INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                        WHERE
                        PE.PRODUCTOUID = ?
                        ORDER BY TO_NUMBER(PE.SEQ)
                """;


            //System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productOID);
            //pstmt.setString(2, partNo);

            rs = pstmt.executeQuery();

            //ProductDto beforeDto = new ProductDto();
            HashMap<String, ProductDto> beforeMap = new HashMap<>();


            while(rs.next()) {
                String productNo = rs.getString("PARENTNO"); //제품번호
                String productVersion = rs.getString("PARENT_VER") == null ? "" : rs.getString("PARENT_VER"); //제품버전
                String PROD_STATUS = rs.getString("PROD_STATUS") == null ? "" : rs.getString("PROD_STATUS");
                String PROD_CREDATE = rs.getString("PROD_CREDATE") == null ? "" : rs.getString("PROD_CREDATE"); //제품 등록일
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                String PROD_APP_DATE = rs.getString("PROD_APP_DATE") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일
                String GISONG = rs.getString("GISONG") == null ? "" : rs.getString("GISONG");


                String PARTNO = rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME") == null ? "" : rs.getString("PARTNAME");
                String PART_VERSION = rs.getString("PART_VERSION") == null ? "" : rs.getString("PART_VERSION");
                String BLOCKNO =  rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String partQTY =  rs.getString("PART_QTY") == null ? "" : rs.getString("PART_QTY");
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");
                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String UCHECK = rs.getString("UCHECK") == null ? "" : rs.getString("UCHECK");


                System.out.println(productNo +">" + productVersion + " >>> " + PARTNO + " > " + PARTNAME);


                ProductDto dto = new ProductDto();
                dto.setProductNo(productNo); //제품번호
                dto.setProductVersion(productVersion); //제품버전
                dto.setProductStatus(PROD_STATUS);
                dto.setProductCreDate(PROD_CREDATE);
                dto.setProductModDate(PROD_MODDATE);
                dto.setProductAppdate(PROD_APP_DATE); //제품승인일
                dto.setGisong(GISONG);

                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setVersion(PART_VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setCmt(CMT);
                dto.setGlCode(GLCODE);
                dto.setUcheck(UCHECK);
                dto.setQty(partQTY);
                dto.setBlockopt(BLOCK_OPT);



                if(beforeMap != null) {

                    /*if(beforeMap.containsKey(partNo)){
                        ProductDto beforeDto = beforeMap.get(partNo);

                        if(beforeDto.getQty() != null && beforeDto.getCmt() != null ) {
                            if (!beforeDto.getQty().equals(dto.getQty())) {
                                dto.setHASCHILD("MOD");
                            }

                            if (!beforeDto.getCmt().trim().equals(dto.getCmt().trim())) {
                                dto.setHASCHILD("MOD");
                            }
                        }
                    }*/

                    /*if(beforeDto.getQty() != null && beforeDto.getCmt() != null ) {
                        if(!beforeDto.getQty().equals(dto.getQty())) {
                            dto.setHASCHILD("MOD");
                        }

                        if(!beforeDto.getCmt().trim().equals(dto.getCmt().trim())) {
                            dto.setHASCHILD("MOD");
                        }
                    }*/
                }

                //beforeDto = dto;
                beforeMap.put(partNo, dto);
                dataList.add(dto);
                /*
                if(productNo.startsWith("0") ||  productNo.startsWith("V")){

                } else {
                    dataList.add(dto);
                }*/


            } //end while

            //System.out.println("dataList.size() = " + dataList.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return dataList;
    }
}
