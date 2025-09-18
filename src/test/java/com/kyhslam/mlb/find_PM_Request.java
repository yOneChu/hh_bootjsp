package com.kyhslam.mlb;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.util.PLMDBConnection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class find_PM_Request {


    // COMPEN CHAIN : B189A 자재 조회
    // COMPEN CHAIN 들어간 호기의 변경 이력

    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        // 1.COMPEN CHAIN 자재 찾기 (활성)

        ArrayList<PartInfoDTO> compenList = findPLMPartWithBlock("2025", "B189A");

        System.out.println("compenList = " + compenList.size());
        for (PartInfoDTO partInfoDTO : compenList) {
            //System.out.println(partInfoDTO.getPartNo());
        }

        ArrayList<ProductDto> dataList = new ArrayList<>();

        for (int i = 0; i < compenList.size(); i++) {
            PartInfoDTO d = compenList.get(i);
            System.out.println(d.getPartNo());

            // 2.그 자재 사용중인 모든 버전의 제품 찾기
            findPartOfProduct_v2("", d.getPartNo(), dataList);
        }

        for(int i=0; i < dataList.size(); i++){
            ProductDto  d = dataList.get(i);
            String productNo = d.getProductNo();
            String productVersion = d.getProductVersion();
            String productDate = d.getProductCreDate();

            String partNo = d.getPartNo();
            String partVersion = d.getVersion();
            String partBlockNo = d.getBlockNo();
            String partQty = d.getQty();
            String partName = d.getPartName();
            String uCheck = d.getUcheck();
            String cmt = d.getCmt();
            String glcode = d.getGlCode();
            String blockOpt = d.getBlockopt();

            //System.out.println(productNo + " > " + partNo + " > " + partQty + " > " + cmt);
        }


        writeExcelFile(dataList);


        System.out.println(" ------------- END ------------- ");


        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    // B189A 전체년도 자재 조회
    public static ArrayList<PartInfoDTO> findPLMPartWithBlock(String year, String block) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                  with ouid as
                              ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                                where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                                --and ( md$number in ( '18900360G0700') )
                                -- AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
                              )
                  SELECT A.MD$NUMBER AS PARTNO,
                         A.MD$DESC AS PARTNAME,
                         A.G_L_CODE AS GL_CODE,
                         --A.MD$CDATE,
                         DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                         --CAD.MD$NUMBER AS CADNO,
                         --CAD.MD$DESC AS CADNAME,
                         --DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC) AS CADDESC,
                         CODN(A.PART_STATUS) AS PART_STATUS,
                         COD(A.UOM) AS UOM,
                         A.VF$VERSION AS VERSION,
                         CODN(A.NATION) AS NATION,
                         COD(A.DESIGN_USE) AS DESIGN_USE,
                         COD(A.COST_USE) AS COST_USE,
                         CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                         A.BLOCKNO_NUMBER,
                         A.SPEC,
                         A.PART_SIZE AS PARTSIZE
                  FROM NORMALPART$VF A 
                  --JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(A.DRAWING_NO)
                  WHERE A.VF$OUID IN (SELECT * FROM OUID)
                  --AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) != '6'
                  --AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) != '5'
                  --AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                  AND A.PART_STATUS = '2466425004' -- 활성
                """;



            //block
            if (block != null && !"".equals(block)) {
                sql += " AND A.BLOCKNO_NUMBER = '" + block.trim().toUpperCase() + "'";
                //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            }

            //sql += " AND A.MD$NUMBER LIKE '10111175G010%' ";



            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, year);
            //pstmt.setString(1, productOID);

            System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARTNO = rs.getString("PARTNO"); //제품번호
                String glCode = rs.getString("GL_CODE");
                String PARTNAME = rs.getString("PARTNAME");

                String VERSION = rs.getString("VERSION");
                String PART_STATUS = rs.getString("PART_STATUS");
                String DESIGN_USE = rs.getString("DESIGN_USE");
                String COST_USE = rs.getString("COST_USE");
                String ORIGIN_DIV = rs.getString("ORIGIN_DIV");
                String BLOCKNO_NUMBER = rs.getString("BLOCKNO_NUMBER");
                String SPEC = rs.getString("SPEC");
                String UOM = rs.getString("UOM");
                String PARTSIZE = rs.getString("PARTSIZE");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(PARTNO);
                dto.setGlCode(glCode);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setStatus(PART_STATUS);
                dto.setDesign(DESIGN_USE);
                dto.setCost(COST_USE);
                dto.setBlockNo(BLOCKNO_NUMBER);
                dto.setSpec(SPEC);
                dto.setUom(UOM);
                dto.setOriginDiv(ORIGIN_DIV);
                dto.setPartSize(PARTSIZE);
                dto.setOriginDiv(ORIGIN_DIV);

                result.add(dto);

            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    //모든 버전의 제품 찾기
    public static ArrayList<ProductDto> findPartOfProduct_v2(String year, String partNo, ArrayList<ProductDto> dataList) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        //ArrayList<ProductDto> dataList = new ArrayList<ProductDto>();

        try {
            con = PLMDBConnection.getConnection();

            String sql = """     
                with ouid as
                 (
                    select A.vf$ouid AS VFOID
                      from product$vf A
                      where SUBSTR(A.MD$CDATE, 0, 4) IN ('2025','2024')
                      --AND A.MD$NUMBER NOT LIKE '%TEST%')
                      AND SUBSTR(A.MD$NUMBER, 0, 4) != 'TEST'
                 )
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
                        PE.PRODUCTOUID IN (SELECT VFOID FROM ouid)
                        --AND NP.MD$NUMBER LIKE '2300005%'
                        --AND NP.MD$NUMBER LIKE ''
                        --ORDER BY TO_NUMBER(PE.SEQ)
                """;


            if(partNo != null && !"".equals(partNo)){

                if (partNo.contains("*")) {

                    partNo = partNo.replace("*", "%");

                    //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
                    sql += " AND NP.MD$NUMBER LIKE '" + partNo + "' ";
                } else {
                    sql += " AND NP.MD$NUMBER = '" + partNo + "' ";
                }
            }

            //System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, productOID);
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


                //System.out.println(productNo +">" + productVersion + " >>> " + PARTNO + " > " + PARTNAME);


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

                    if(beforeMap.containsKey(partNo)){
                        ProductDto beforeDto = beforeMap.get(partNo);

                        if(beforeDto.getQty() != null && beforeDto.getCmt() != null ) {
                            if (!beforeDto.getQty().equals(dto.getQty())) {
                                dto.setHASCHILD("MOD");
                            }

                            if (!beforeDto.getCmt().trim().equals(dto.getCmt().trim())) {
                                dto.setHASCHILD("MOD");
                            }
                        }
                    }

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



    private static void writeExcelFile(ArrayList<ProductDto> dataList) {
        Workbook workbook = new XSSFWorkbook();

        // 시트 생성
        Sheet sheet = workbook.createSheet("결과");


        // 스타일
        CellStyle headerStyle = workbook.createCellStyle();

        // 배경색 (연한 회색)
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 테두리 설정
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 정렬 설정 (가운데 정렬)
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 폰트 설정 (굵은 글씨 + 크기 조절)
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("맑은 고딕");
        headerStyle.setFont(headerFont);


        // 셀 스타일 생성
        CellStyle yelloStyle = workbook.createCellStyle();

        // 배경색 지정
        yelloStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yelloStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);



        Row header = sheet.createRow(0);
        String[] titles = { "제품번호", "버전", "등록일", "자재번호", "자재명", "버전", "수정여부", "BlockNo", "QTY", "SPEC", "SIZE", "CMT"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));


        for(int i=0; i < dataList.size(); i++){
            ProductDto dto = dataList.get(i);

            Row headerRow = sheet.createRow((i+1));

            //String[] titles = { "제품번호", "버전", "등록일", // "자재번호", "자재명", "버전",  "BlockNo", "QTY", "SPEC", "SIZE", "CMT"

            //headerRow.createCell(0).setCellValue(dto.getProductNo());


            Cell cell = headerRow.createCell(0);
            cell.setCellValue(dto.getProductNo());

            if(dto.getHASCHILD() != null && dto.getHASCHILD().equals("MOD")){
                //cell.setCellStyle(yelloStyle);
            }

            headerRow.createCell(1).setCellValue(dto.getProductVersion());
            headerRow.createCell(2).setCellValue(dto.getProductCreDate());

            headerRow.createCell(3).setCellValue(dto.getPartNo());
            headerRow.createCell(4).setCellValue(dto.getPartName());
            headerRow.createCell(5).setCellValue(Integer.parseInt(dto.getVersion()));
            headerRow.createCell(6).setCellValue(dto.getUcheck());
            headerRow.createCell(7).setCellValue(dto.getBlockNo());

            headerRow.createCell(8).setCellValue(dto.getQty());
            headerRow.createCell(9).setCellValue(dto.getSpec());
            headerRow.createCell(10).setCellValue(dto.getPart_size());
            headerRow.createCell(11).setCellValue(dto.getCmt());


        }



        // 자동 열 너비 조정
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }

        // 파일 저장
        try (FileOutputStream fileOut = new FileOutputStream("C:\\excel\\COMPEN_CHAIN_2024-2025.xlsx")) {
            workbook.write(fileOut);
            System.out.println("Excel 파일 생성 완료!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 리소스 해제
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" ---------- end ----------- ");

    }
}
