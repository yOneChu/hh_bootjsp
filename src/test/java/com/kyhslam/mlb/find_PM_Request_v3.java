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
        Path outputPath = Path.of("C:\\excel\\sample_out.xlsx"); // 저장 파일

        String filePath = "";
        filePath = "C:\\excel\\복사본 COMPEN_CHAIN_2024-2025_BACK.xlsx"; // 읽을 엑셀 파일 경로

        LinkedHashMap<String, ArrayList<ProductDto>> dataMap = new LinkedHashMap<>();
        //key: 호기번호|자재번호


        /*// 1.제품의 OID 추출
        ArrayList<ProductDto> productOids = new ArrayList<>();
        productOids = SubaeCommonUtil.findProductALLInfo("197558L09");

        ArrayList<ProductDto> dataList = new ArrayList<>();

        for (int i = 0; i < productOids.size(); i++) {
            ProductDto dto = productOids.get(i);
            String oid = dto.getProductOid();
            String prodNo = dto.getProductNo();
            String prodName = dto.getProductName();
            String prodVersion = dto.getProductVersion();
            String prodCreDate = dto.getProductCreDate();
            String prodAppDate = dto.getProductAppdate();

            System.out.println(oid + " > " + prodNo + " > " + prodVersion);
            //findPartOfProduct_v2(oid, "", dataList);
            ArrayList<ProductDto> bomList = ProductCommonUtil.findProductBOMWithOID(oid);
        }*/


        HashSet<String> dupCheck =  new HashSet<>();


        try (FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis)) {


            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 읽기

            // 첫 번째 행(헤더)은 건너뛴다고 가정 (row 0은 헤더)
            //for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            for (int rowIndex = 1; rowIndex <= 2; rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                // 각 컬럼 값 읽기
                String productNo = getCellValue(row.getCell(0));
                String productVersion = getCellValue(row.getCell(1));
                String productCreDate = getCellValue(row.getCell(2));
                String partNo = getCellValue(row.getCell(3));
                String partName = getCellValue(row.getCell(4));
                String blockNo = getCellValue(row.getCell(5));
                String qty = getCellValue(row.getCell(6)); //수량변경이력
                String spec = getCellValue(row.getCell(7)); // 주석변경이력
                String size = getCellValue(row.getCell(8)); // 최초등록일
                String cmt = getCellValue(row.getCell(9)); // 최초

                String modDate = getCellValue(row.getCell(10)); // 변경 등록일
                String lastVal = getCellValue(row.getCell(11)); // 최종
                String modCount = getCellValue(row.getCell(12)); // 변경 수
                String exportDate = getCellValue(row.getCell(13)); // 출하일


                //출하예정일
                ArrayList<HashMap<String, String>> exportList = PartCommonUtil.getExportDate(productNo);
                if(exportList != null && exportList.size()>0){
                    HashMap<String, String> exportMap = exportList.get(0);
                    exportDate = exportMap.get("SHIP_B");

                }


                /*ProductDto dto = new ProductDto();
                dto.setProductNo(productNo);
                dto.setProductVersion(productVersion);
                dto.setProductCreDate(productCreDate);

                dto.setPartNo(partNo);
                dto.setPartName(partName);
                dto.setBlockNo(blockNo);
                dto.setQty(qty);
                dto.setSpec(spec);
                dto.setPart_size(size);
                dto.setCmt(cmt);
                */

                // 데이터쓰기
                ArrayList<ProductDto> productOids = new ArrayList<>();
                productOids = SubaeCommonUtil.findProductALLInfo(productNo.trim());

                //ArrayList<ProductDto> dataList = new ArrayList<>();
                String getProdAppDate = ""; // 제품 승인일

                for (int i = 0; i < productOids.size(); i++) {
                    ProductDto dto = productOids.get(i);
                    String oid = dto.getProductOid();
                    String prodNo = dto.getProductNo();
                    String prodName = dto.getProductName();
                    String prodVersion = dto.getProductVersion();
                    String prodCreDate = dto.getProductCreDate();
                    String prodAppDate = dto.getProductAppdate();

                    if(dupCheck.contains(prodNo)){
                        continue;
                    }


                    System.out.println(oid + " > " + prodNo + " > " + prodVersion);
                    //findPartOfProduct_v2(oid, "", dataList);
                    ArrayList<ProductDto> bomList = ProductCommonUtil.findProductBOMWithOID(oid);


                    for (int j = 0; j < bomList.size(); j++) {
                        ProductDto dd = bomList.get(j);
                        String vPartNo = dd.getPartNo();
                        //String vProdAppDate = dd.getProductAppdate();

                        if (vPartNo.equals(partNo)) {
                            getProdAppDate = prodAppDate;

                            if(!dupCheck.contains(productNo)) {
                                dupCheck.add(productNo);
                            }

                        }
                    }


                }


                // 8
                //row.createCell(8).setCellValue(getProdAppDate);
                Cell cell = row.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(getProdAppDate);



                // 출력 (또는 DTO에 매핑 가능)
                //System.out.printf("제품번호=%s, 버전=%s, 등록일=%s, 자재번호=%s, 버전=%s%n",
                        //productNo, productVersion, productCreDate, partNo, partName);

            }

            try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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
