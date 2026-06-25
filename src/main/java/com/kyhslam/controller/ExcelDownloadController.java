package com.kyhslam.controller;


import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.PlanCService;
import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.context.annotation.Description;
import org.springframework.util.StopWatch;
import org.springframework.web.accept.MediaTypeFileExtensionResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelDownloadController {

    private final SubaeService subaeService;
    private final MediaTypeFileExtensionResolver mediaTypeFileExtensionResolver;

    private final PlanCService planCService;

    private final SubaeHogiService subaeHogiService;


    @PostMapping("/subaeDownload")
    public void downloadExcel(HttpServletResponse response, String month) throws IOException {
        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"data.xlsx\"");

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        // 헤더 작성
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("호기번호");
        header.createCell(1).setCellValue("버전");
        header.createCell(2).setCellValue("수주명");
        header.createCell(3).setCellValue("최종설계일");

        header.createCell(4).setCellValue("기종");
        header.createCell(5).setCellValue("기계");
        header.createCell(6).setCellValue("전기");

        // 데이터 가져오기
        //List<MyDataDto> dataList = myDataService.getLargeData();
        ArrayList<ProductDto> dataList = new ArrayList<>();
        ProductDto param = new ProductDto();
        param.setProductAppdate(month);

        dataList = subaeService.findSubaeProductList(param);
        for (int i = 0; i < dataList.size(); i++) {
            ProductDto dto = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dto.getProductNo());
            row.createCell(1).setCellValue(dto.getProductVersion());
            row.createCell(2).setCellValue(dto.getProductName());
            row.createCell(3).setCellValue(dto.getProductAppdate());

            row.createCell(4).setCellValue(dto.getGisong());
            row.createCell(5).setCellValue(dto.getMmanager());
            row.createCell(6).setCellValue(dto.getEmanager());

        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    // 2025-07 날짜로 수배율 계산한 PARTNO 전체 조회 -> EXCEL
    @PostMapping("/subaeDownloadV2")
    public void subaeDownloadV2(HttpServletResponse response, String month, String ucheck) throws IOException {

        String fileName = "";
        fileName = "ALL_PARTLIST_" + month + ".xlsx";

        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"SUBAE_PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        System.out.println("subaeDownloadV2 -- " + month);
        System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        //--스타일
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

        // 행 생성 및 스타일 적용
        //Row header = sheet.createRow(0);



        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "제품번호", "제품버전", "수주명", "기종", "최초설계일", "자재번호", "자재명", "수량", "품목", "BlockNo", "GL_CODE", "수정여부", "기계", "전기", "CMT" };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));

       /* header.createCell(0).setCellValue("제품번호");
        header.createCell(1).setCellValue("제품버전");
        header.createCell(2).setCellValue("수주명");
        header.createCell(3).setCellValue("기종");
        header.createCell(4).setCellValue("최초설계일");

        header.createCell(5).setCellValue("자재번호");
        header.createCell(6).setCellValue("자재명");
        header.createCell(7).setCellValue("수량");
        header.createCell(8).setCellValue("품목");
        header.createCell(9).setCellValue("BLOCKNO");
        header.createCell(10).setCellValue("GL_CODE");
        header.createCell(11).setCellValue("수정여부");
        header.createCell(12).setCellValue("기계");
        header.createCell(13).setCellValue("전기");
        header.createCell(14).setCellValue("CMT");*/




        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyStyle.setAlignment(HorizontalAlignment.LEFT);

        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 10);
        bodyFont.setFontName("맑은 고딕");
        bodyStyle.setFont(bodyFont);


        // 데이터 가져오기
        //List<MyDataDto> dataList = myDataService.getLargeData();
        ArrayList<ProductDto> dataList = new ArrayList<>();
        ProductDto param = new ProductDto();
        param.setProductAppdate(month);
        param.setUcheck(ucheck);

        System.out.println(param.getUcheck());

        // 수배율 데이터 조회
        dataList = subaeService.findSubaePartNoList(param);
        System.out.println("dataList = " + dataList.size());


        for (int i = 0; i < dataList.size(); i++) {
            ProductDto dto = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dto.getProductNo());
            row.createCell(1).setCellValue(dto.getProductVersion());
            row.createCell(2).setCellValue(dto.getProductName());
            row.createCell(3).setCellValue(dto.getGisong());
            row.createCell(4).setCellValue(dto.getProductAppdate());
//cell1.setCellStyle(bodyStyle);


            row.createCell(5).setCellValue(dto.getPartNo());
            row.createCell(6).setCellValue(dto.getPartName());
            row.createCell(7).setCellValue(dto.getQty());
            row.createCell(8).setCellValue(dto.getBlockopt());
            row.createCell(9).setCellValue(dto.getBlockNo());
            row.createCell(10).setCellValue(dto.getGlCode());
            row.createCell(11).setCellValue(dto.getUcheck());
            row.createCell(12).setCellValue(dto.getMmanager());
            row.createCell(13).setCellValue(dto.getEmanager());
            row.createCell(14).setCellValue(dto.getCmt());


            row.getCell(0).setCellStyle(bodyStyle);
            row.getCell(1).setCellStyle(bodyStyle);
            row.getCell(2).setCellStyle(bodyStyle);
            row.getCell(3).setCellStyle(bodyStyle);
            row.getCell(4).setCellStyle(bodyStyle);
            row.getCell(5).setCellStyle(bodyStyle);
            row.getCell(6).setCellStyle(bodyStyle);
            row.getCell(7).setCellStyle(bodyStyle);
            row.getCell(8).setCellStyle(bodyStyle);
            row.getCell(9).setCellStyle(bodyStyle);
            row.getCell(10).setCellStyle(bodyStyle);
            row.getCell(11).setCellStyle(bodyStyle);
            row.getCell(12).setCellStyle(bodyStyle);
            row.getCell(13).setCellStyle(bodyStyle);
            row.getCell(14).setCellStyle(bodyStyle);

        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    /**
     * @apiNote 자재 대시보드 -> 자재조회 엑셀 다운로드
     * @param response
     * @param partNo
     * @param partName
     * @param year
     * @param status
     * @param qtyLogic
     * @throws IOException
     */
    @PostMapping("/searchPart")
    public void searchPart(HttpServletResponse response, String partNo, String partName, String year, String status, String qtyLogic) throws IOException {


        if(partNo != null && !"".equals(partNo)) partNo = partNo.trim();
        if(partName != null && !"".equals(partName)) partName = partName.trim();

        System.out.println("--------- searchPart -----------");
        StopWatch sw = new StopWatch();
        sw.start();


        System.out.println("status = " + status);
        
        // 현재 시간을 기반으로 파일명 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String fileName = "PartList_" + timestamp;

        if ("OSL".equals(status)) {
            fileName += "_disuse";
        } else {
            fileName += "_" + status;
        }
        fileName += ".xlsx";
        //fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

        System.out.println("fileName = " + fileName);
        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        //response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        //System.out.println("subaeDownloadV2 -- " + month);
        //System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        //--스타일
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

        // 행 생성 및 스타일 적용
        //Row header = sheet.createRow(0);



        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "자재번호", "자재명", "버전", "설계사용", "견적사용", "BlockNo", "상태", "단위", "DIV", "PART_SIZE", "SPEC", "GL_CODE", "생성일"};
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));


        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyStyle.setAlignment(HorizontalAlignment.LEFT);

        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 10);
        bodyFont.setFontName("맑은 고딕");
        bodyStyle.setFont(bodyFont);


        // 데이터 가져오기
        //List<MyDataDto> dataList = myDataService.getLargeData();
        ArrayList<PartInfoDTO> dataList = new ArrayList<>();

        dataList = PartDashboardUtil.findPLMPartV1(year, partNo, partName, status);
        System.out.println("dataList = " + dataList.size());


        for (int i = 0; i < dataList.size(); i++) {
            PartInfoDTO dto = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dto.getPartNo());
            row.createCell(1).setCellValue(dto.getPartName());
            row.createCell(2).setCellValue(dto.getVersion());
            row.createCell(3).setCellValue(dto.getDesign());
            row.createCell(4).setCellValue(dto.getCost());
            row.createCell(5).setCellValue(dto.getBlockNo());
            row.createCell(6).setCellValue(dto.getStatus());
            row.createCell(7).setCellValue(dto.getUom());
            row.createCell(8).setCellValue(dto.getOriginDiv());
            row.createCell(9).setCellValue(dto.getPartSize());
            row.createCell(10).setCellValue(dto.getSpec());
            row.createCell(11).setCellValue(dto.getGlCode());
            row.createCell(12).setCellValue(dto.getCreDate());

            row.getCell(0).setCellStyle(bodyStyle);
            row.getCell(1).setCellStyle(bodyStyle);
            row.getCell(2).setCellStyle(bodyStyle);
            row.getCell(3).setCellStyle(bodyStyle);
            row.getCell(4).setCellStyle(bodyStyle);
            row.getCell(5).setCellStyle(bodyStyle);
            row.getCell(6).setCellStyle(bodyStyle);
            row.getCell(7).setCellStyle(bodyStyle);
            row.getCell(8).setCellStyle(bodyStyle);
            row.getCell(9).setCellStyle(bodyStyle);
            row.getCell(10).setCellStyle(bodyStyle);
            row.getCell(11).setCellStyle(bodyStyle);
            row.getCell(12).setCellStyle(bodyStyle);
        }


        sw.stop();
        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    @PostMapping("/searchPIDExcel")
    public void searchPIDExcel(HttpServletResponse response,
                               String pid, String FIELD, String GUBUN, String connectGubun
            , String PID02, String SPEC02, String GUBUN02, String CON05, String PID03, String PID04, String PID05, String join) throws IOException {



        // 현재 시간을 기반으로 파일명 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String fileName = pid + "_" + timestamp + ".xlsx";


        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        //System.out.println("subaeDownloadV2 -- " + month);
        //System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        //--스타일
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

        // 행 생성 및 스타일 적용
        //Row header = sheet.createRow(0);



        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "PID", "NO", "REMARKS", "ADDR",
                "SPEC1", "CON1", "SPEC2", "CON2", "SPEC3", "CON3", "SPEC4", "CON4", "SPEC5", "CON5", "SPEC6", "CON6", "SPEC7", "CON7", "SPEC8", "CON8", "SPEC9", "CON9", "SPEC10", "CON10",
                "SPEC11", "CON11", "SPEC12", "CON12", "SPEC13", "CON13", "SPEC14", "CON14", "SPEC15", "CON15", "SPEC16", "CON16", "SPEC17", "CON17", "SPEC18", "CON18", "SPEC19", "CON19", "SPEC20", "CON20",
                "KEY1", "VAL1", "KEY2", "VAL2", "KEY3", "VAL3", "KEY4", "VAL4", "KEY5", "VAL5", "KEY6", "VAL6", "KEY7", "VAL7", "KEY8", "VAL8", "KEY9", "VAL9", "KEY10", "VAL10",
                "KEY11", "VAL11", "KEY12", "VAL12", "KEY13", "VAL13", "KEY14", "VAL14", "KEY15", "VAL15", "KEY16", "VAL16", "KEY17", "VAL17", "KEY18", "VAL18", "KEY19", "VAL19", "KEY20", "VAL20"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));


        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyStyle.setAlignment(HorizontalAlignment.LEFT);

        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 10);
        bodyFont.setFontName("맑은 고딕");
        bodyStyle.setFont(bodyFont);


        // 데이터 가져오기
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        dataList = PIDCommonUtil.findPIDDetail(pid, FIELD, GUBUN, connectGubun, PID02, SPEC02, GUBUN02, CON05, PID03, PID04, PID05, join);




        for (int i = 0; i < dataList.size(); i++) {
            HashMap<String,String> dto = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            String PID = dto.get("PID");
            String NO = dto.get("NO");
            String REMARKS = dto.get("REMARKS");
            String ADDR = dto.get("ADDR");

            String SPEC1 =  dto.get("SPEC1");     String CON1 =  dto.get("CON1");
            String SPEC2 =  dto.get("SPEC2");     String CON2 =  dto.get("CON2");
            String SPEC3 =  dto.get("SPEC3");     String CON3 =  dto.get("CON3");
            String SPEC4 =  dto.get("SPEC4");     String CON4 =  dto.get("CON4");
            String SPEC5 =  dto.get("SPEC5");     String CON5 =  dto.get("CON5");
            String SPEC6 =  dto.get("SPEC6");     String CON6 =  dto.get("CON6");
            String SPEC7 =  dto.get("SPEC7");     String CON7 =  dto.get("CON7");
            String SPEC8 =  dto.get("SPEC8");     String CON8 =  dto.get("CON8");
            String SPEC9 =  dto.get("SPEC9");     String CON9 =  dto.get("CON9");
            String SPEC10 = dto.get("SPEC10");    String CON10 = dto.get("CON10");
            String SPEC11 = dto.get("SPEC11");    String CON11 = dto.get("CON11");
            String SPEC12 = dto.get("SPEC12");    String CON12 = dto.get("CON12");
            String SPEC13 = dto.get("SPEC13");    String CON13 = dto.get("CON13");
            String SPEC14 = dto.get("SPEC14");    String CON14 = dto.get("CON14");
            String SPEC15 = dto.get("SPEC15");    String CON15 = dto.get("CON15");
            String SPEC16 = dto.get("SPEC16");    String CON16 = dto.get("CON16");
            String SPEC17 = dto.get("SPEC17");    String CON17 = dto.get("CON17");
            String SPEC18 = dto.get("SPEC18");    String CON18 = dto.get("CON18");
            String SPEC19 = dto.get("SPEC19");    String CON19 = dto.get("CON19");
            String SPEC20 = dto.get("SPEC20");    String CON20 = dto.get("CON20");

            String KEY1 =  dto.get("KEY1");     String VAL1 =  dto.get("VAL1");
            String KEY2 =  dto.get("KEY2");     String VAL2 =  dto.get("VAL2");
            String KEY3 =  dto.get("KEY3");     String VAL3 =  dto.get("VAL3");
            String KEY4 =  dto.get("KEY4");     String VAL4 =  dto.get("VAL4");
            String KEY5 =  dto.get("KEY5");     String VAL5 =  dto.get("VAL5");
            String KEY6 =  dto.get("KEY6");     String VAL6 =  dto.get("VAL6");
            String KEY7 =  dto.get("KEY7");     String VAL7 =  dto.get("VAL7");
            String KEY8 =  dto.get("KEY8");     String VAL8 =  dto.get("VAL8");
            String KEY9 =  dto.get("KEY9");     String VAL9 =  dto.get("VAL9");
            String KEY10 = dto.get("KEY10");    String VAL10 = dto.get("VAL10");
            String KEY11 = dto.get("KEY11");    String VAL11 = dto.get("VAL11");
            String KEY12 = dto.get("KEY12");    String VAL12 = dto.get("VAL12");
            String KEY13 = dto.get("KEY13");    String VAL13 = dto.get("VAL13");
            String KEY14 = dto.get("KEY14");    String VAL14 = dto.get("VAL14");
            String KEY15 = dto.get("KEY15");    String VAL15 = dto.get("VAL15");
            String KEY16 = dto.get("KEY16");    String VAL16 = dto.get("VAL16");
            String KEY17 = dto.get("KEY17");    String VAL17 = dto.get("VAL17");
            String KEY18 = dto.get("KEY18");    String VAL18 = dto.get("VAL18");
            String KEY19 = dto.get("KEY19");    String VAL19 = dto.get("VAL19");
            String KEY20 = dto.get("KEY20");    String VAL20 = dto.get("VAL20");


            row.createCell(0).setCellValue(PID);
            row.createCell(1).setCellValue(NO);
            row.createCell(2).setCellValue(REMARKS);
            row.createCell(3).setCellValue(ADDR);

            row.createCell(4).setCellValue(SPEC1); row.createCell(5).setCellValue(CON1);
            row.createCell(6).setCellValue(SPEC2); row.createCell(7).setCellValue(CON2);
            row.createCell(8).setCellValue(SPEC3); row.createCell(9).setCellValue(CON3);
            row.createCell(10).setCellValue(SPEC4); row.createCell(11).setCellValue(CON4);
            row.createCell(12).setCellValue(SPEC5); row.createCell(13).setCellValue(CON5);
            row.createCell(14).setCellValue(SPEC6); row.createCell(15).setCellValue(CON6);
            row.createCell(16).setCellValue(SPEC7); row.createCell(17).setCellValue(CON7);
            row.createCell(18).setCellValue(SPEC8); row.createCell(19).setCellValue(CON8);
            row.createCell(20).setCellValue(SPEC9); row.createCell(21).setCellValue(CON9);
            row.createCell(22).setCellValue(SPEC10); row.createCell(23).setCellValue(CON10);
            row.createCell(24).setCellValue(SPEC11); row.createCell(25).setCellValue(CON11);
            row.createCell(26).setCellValue(SPEC12); row.createCell(27).setCellValue(CON12);
            row.createCell(28).setCellValue(SPEC13); row.createCell(29).setCellValue(CON13);
            row.createCell(30).setCellValue(SPEC14); row.createCell(31).setCellValue(CON14);
            row.createCell(32).setCellValue(SPEC15); row.createCell(33).setCellValue(CON15);
            row.createCell(34).setCellValue(SPEC16); row.createCell(35).setCellValue(CON16);
            row.createCell(36).setCellValue(SPEC17); row.createCell(37).setCellValue(CON17);
            row.createCell(38).setCellValue(SPEC18); row.createCell(39).setCellValue(CON18);
            row.createCell(40).setCellValue(SPEC19); row.createCell(41).setCellValue(CON19);
            row.createCell(42).setCellValue(SPEC20); row.createCell(43).setCellValue(CON20);


            row.createCell(44).setCellValue(KEY1); row.createCell(45).setCellValue(VAL1);
            row.createCell(46).setCellValue(KEY2); row.createCell(47).setCellValue(VAL2);
            row.createCell(48).setCellValue(KEY3); row.createCell(49).setCellValue(VAL3);
            row.createCell(50).setCellValue(KEY4); row.createCell(51).setCellValue(VAL4);
            row.createCell(52).setCellValue(KEY5); row.createCell(53).setCellValue(VAL5);
            row.createCell(54).setCellValue(KEY6); row.createCell(55).setCellValue(VAL6);
            row.createCell(56).setCellValue(KEY7); row.createCell(57).setCellValue(VAL7);
            row.createCell(58).setCellValue(KEY8); row.createCell(59).setCellValue(VAL8);
            row.createCell(60).setCellValue(KEY9); row.createCell(61).setCellValue(VAL9);
            row.createCell(62).setCellValue(KEY10); row.createCell(63).setCellValue(VAL10);
            row.createCell(64).setCellValue(KEY11); row.createCell(65).setCellValue(VAL11);
            row.createCell(66).setCellValue(KEY12); row.createCell(67).setCellValue(VAL12);
            row.createCell(68).setCellValue(KEY13); row.createCell(69).setCellValue(VAL13);
            row.createCell(70).setCellValue(KEY14); row.createCell(71).setCellValue(VAL14);
            row.createCell(72).setCellValue(KEY15); row.createCell(73).setCellValue(VAL15);
            row.createCell(74).setCellValue(KEY16); row.createCell(75).setCellValue(VAL16);
            row.createCell(76).setCellValue(KEY17); row.createCell(77).setCellValue(VAL17);
            row.createCell(78).setCellValue(KEY18); row.createCell(79).setCellValue(VAL18);
            row.createCell(80).setCellValue(KEY19); row.createCell(81).setCellValue(VAL19);
            row.createCell(82).setCellValue(KEY20); row.createCell(83).setCellValue(VAL20);

            for (int m = 0; m < 80; m++) {
                row.getCell(m).setCellStyle(bodyStyle);
            }

        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    @PostMapping("/searchQtyPid")
    public void searchQtyPid(HttpServletResponse response,
                               String year, String blockNo, String qtyPid) throws IOException {

        if(year != null)    year = year.toUpperCase().trim();
        if(blockNo != null) blockNo = blockNo.toUpperCase().trim();
        if(qtyPid != null)  qtyPid = qtyPid.toUpperCase().trim();


        // 현재 시간을 기반으로 파일명 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String fileName = qtyPid + "_" + timestamp + ".xlsx";


        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        //System.out.println("subaeDownloadV2 -- " + month);
        //System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        //--스타일
        CellStyle headerStyle = ExcelUtil.getHeaderStyle(workbook);

        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "P_자재번호", "P_자재명", "P_BlockNo", "P_SPEC", "P_SIZE",
                "자재번호", "자재명", "BlockNo", "SPEC", "QTY", "CMT", "SIZE"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));

        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);

        // 데이터 가져오기
        ArrayList<PartInfoDTO> dataList = new ArrayList<>();
        MLBCommonUtil.findDownLevelQtyPID(year, blockNo, qtyPid, dataList);


        for (int i = 0; i < dataList.size(); i++) {
            PartInfoDTO dto = dataList.get(i);

            Row row = sheet.createRow(i + 1);
            String pPartNo = dto.getParentPartNo();
            String pPartName = dto.getParentPartName();
            String pBlockNo = dto.getParentBlockNo();
            String pSpec = dto.getParentSpec();
            String pSize = dto.getParentSize();

            String partNo = dto.getPartNo();
            String partName = dto.getPartName();
            String partSpec = dto.getSpec();
            String vblockNo = dto.getBlockNo();
            String partSize = dto.getPartSize();
            String cmt =  dto.getCmt();
            String qty = dto.getQty();

            row.createCell(0).setCellValue(pPartNo);
            row.createCell(1).setCellValue(pPartName);
            row.createCell(2).setCellValue(pBlockNo);
            row.createCell(3).setCellValue(pSpec);
            row.createCell(4).setCellValue(pSize);

            row.createCell(5).setCellValue(partNo);
            row.createCell(6).setCellValue(partName);
            row.createCell(7).setCellValue(vblockNo);
            row.createCell(8).setCellValue(partSpec);
            row.createCell(9).setCellValue(qty);
            row.createCell(10).setCellValue(cmt);
            row.createCell(11).setCellValue(partSize);

            for (int m = 0; m < 12; m++) {
                row.getCell(m).setCellStyle(bodyStyle);
            }
        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    //전산작업요청
    @Description("전산작업요청 엑셀 다운로드")
    @PostMapping("/searchDesignReqExcel")
    public void searchDesignExcel(HttpServletResponse response,
                             String year) throws IOException {



        // 현재 시간을 기반으로 파일명 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String fileName = year + "_" + timestamp + ".xlsx";


        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        //System.out.println("subaeDownloadV2 -- " + month);
        //System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        //--스타일
        CellStyle headerStyle = ExcelUtil.getHeaderStyle(workbook);

        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "요청번호", "상태", "등록일", "수정일", "담당자", "작업내용", "대표호기", "우선순위", "구분", "작업구분", "요청사유",
                "요청내용", "수배자료적합성", "수배자료적합성2", "제한조건작성여부", "layout", "DCB완려여부", "ISIR(초도품검사)"
                ,"인증완료여부","재고처리여부", "DUTY_TABLE수정요청여부", "시리즈현장 적용 여부", "기 수주/설계 현장 대응 여부", "유관팀 공유여부", "원가영향도"
                , "SUBSYSTEM공급구분"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));

        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);

        //ArrayList<DesignRequestDTO>
        // 데이터 가져오기
        ArrayList<DesignRequestDTO> dataList = new ArrayList<>();
        dataList = DesignReqCommonUtil.findALLDesignReq(year);



        for (int i = 0; i < dataList.size(); i++) {
            DesignRequestDTO dto = dataList.get(i);

            Row row = sheet.createRow(i + 1);
            String reqNo = dto.getReqNo();
            String status =  dto.getStatus();
            String wosun = dto.getWosun();
            String gubun = dto.getGubun();
            String workGubun = dto.getWorkGubun();
            String hogi = dto.getHogi();
            String first = dto.getFirst();

            String user = dto.getCUser();
            String manager = dto.getManager();
            String answerDetail = dto.getAnswerDetail();
            String reqCause = dto.getReqCause();
            String reqDetail = dto.getReqDetail();

            String subae01 = dto.getSubae01();
            String subae02 = dto.getSubae02();
            String isLimit = dto.getIsLimit();
            String layout = dto.getLayout();
            String dcbFinish = dto.getDcbFinish();
            String isIsir =  dto.getIsIsir();
            String isFinish = dto.getIsFinish();
            String ingStock = dto.getIngStock();
            String isDutyTable = dto.getIsDutyTable();

            String isSeries = dto.getIsSeries();
            String designSite = dto.getDesignSite();
            String teamShared = dto.getTeamShared();
            String costInfluence = dto.getCostInfluence();
            String subSystem = dto.getSubSystem();

            String creDate = dto.getCreDate();
            String modDate = dto.getModDate();

            row.createCell(0).setCellValue(reqNo);
            row.createCell(1).setCellValue(status);
            row.createCell(2).setCellValue(creDate);
            row.createCell(3).setCellValue(modDate);

            row.createCell(4).setCellValue(manager);
            row.createCell(5).setCellValue(answerDetail);
            row.createCell(6).setCellValue(hogi);

            row.createCell(7).setCellValue(wosun);
            row.createCell(8).setCellValue(gubun);
            row.createCell(9).setCellValue(workGubun);
            row.createCell(10).setCellValue(reqCause);
            row.createCell(11).setCellValue(reqDetail);

            row.createCell(12).setCellValue(subae01);
            row.createCell(13).setCellValue(subae02);
            row.createCell(14).setCellValue(isLimit);
            row.createCell(15).setCellValue(layout);
            row.createCell(16).setCellValue(dcbFinish);
            row.createCell(17).setCellValue(isIsir);
            row.createCell(18).setCellValue(isFinish);
            row.createCell(19).setCellValue(ingStock);
            row.createCell(20).setCellValue(isDutyTable);

            row.createCell(21).setCellValue(isSeries);
            row.createCell(22).setCellValue(designSite);
            row.createCell(23).setCellValue(teamShared);
            row.createCell(24).setCellValue(costInfluence);
            row.createCell(25).setCellValue(subSystem);

            for (int m = 0; m < 26; m++) {
                row.getCell(m).setCellStyle(bodyStyle);
            }
        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    @Description("PLAN-C 자재들 엑셀 다운로드")
    @PostMapping("/searchPlanDataExcel")
    public void searchPlanDataExcel(HttpServletResponse response,
                                  String year) throws IOException {



        // 현재 시간을 기반으로 파일명 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String fileName = "PLAN_C_" + timestamp + ".xlsx";


        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        //System.out.println("subaeDownloadV2 -- " + month);
        //System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        DataFormat format = workbook.createDataFormat();
        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(format.getFormat("#,##0"));

        //--스타일
        CellStyle headerStyle = ExcelUtil.getHeaderStyle(workbook);

        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "ERP전송일", "호기", "INDEX", "브랜드", "생산거점", "자재번호", "자재명", "SPEC", "기종", "공사", "dwgNo",
                "BlockNo", "개당 절감액", "출하예정일", "BATCH-DATE"
        };
        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));

        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);

        //ArrayList<DesignRequestDTO>
        // 데이터 가져오기
        List<ProductPlanC> dataList = new ArrayList<>();
        dataList = planCService.findProductAll_v2();



        for (int i = 0; i < dataList.size(); i++) {
            ProductPlanC dto = dataList.get(i);

            String indexNo = dto.getIndexNo();
            String toCost = dto.getToCost();
            if(indexNo == null || "".equals(indexNo.trim()) ){
                continue;
            }

            if(toCost == null || "".equals(toCost.trim()) ){
                continue;
            }

            Row row = sheet.createRow(i + 1);
            String reqNo = dto.getErpSendDate();
            
            // ProductPlanC fields extracted for further processing/export
            String batchDate = dto.getBatchDate();
            String erpSendDate = dto.getErpSendDate();
            String exportDate = dto.getExportDate();




            String productOid = dto.getProductOid();
            String productNo = dto.getProductNo();
            String aspd = dto.getAspd();
            String aspscd = dto.getAspscd();
            String acapa = dto.getAcapa();
            String brand = dto.getBrand();

            String dwgNo = dto.getDwgNo();
            String gongSa = dto.getGongSa();
            String gisong = dto.getGisong();
            String mmanager = dto.getMmanager();
            String emanager = dto.getEmanager();
            String module = dto.getModule();

            String seq = dto.getSeq();
            String parentNo = dto.getParentNo();
            String partNo = dto.getPartNo();
            String partNoOID = dto.getPartNoOID();
            String partName = dto.getPartName();
            String nation = dto.getNation();
            String version = dto.getVersion();
            String glCode = dto.getGlCode();
            String spec = dto.getSpec();
            String part_size = dto.getPart_size();
            String blockNo = dto.getBlockNo();
            String blockName = dto.getBlockName();

            row.createCell(0).setCellValue(erpSendDate);
            row.createCell(1).setCellValue(productNo);
            row.createCell(2).setCellValue(indexNo);
            row.createCell(3).setCellValue(brand);
            row.createCell(4).setCellValue(aspscd);
            row.createCell(5).setCellValue(partNo);
            row.createCell(6).setCellValue(partName);
            row.createCell(7).setCellValue(spec);
            row.createCell(8).setCellValue(gisong);
            row.createCell(9).setCellValue(gongSa);
            row.createCell(10).setCellValue(dwgNo);
            row.createCell(11).setCellValue(blockNo);

            BigDecimal amount = new BigDecimal(toCost);

            // 4. 엑셀 셀에 숫자로 입력
            Cell cell12 = row.createCell(12);   // 원하는 컬럼 인덱스
            cell12.setCellValue(amount.doubleValue());
            //row.createCell(12).setCellValue(toCost);

            // 5. 금액 서식 적용
            cell12.setCellStyle(moneyStyle);



            row.createCell(13).setCellValue(exportDate);
            row.createCell(14).setCellValue(batchDate);


            for (int m = 0; m < 15; m++) {
                row.getCell(m).setCellStyle(bodyStyle);
            }
        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }


    //BOM품목비교-EXCEL
    @PostMapping("/searchBlockSubae")
    public void searchBlockSubae(HttpServletResponse response, String blockNo, String partName) throws IOException {

        // 현재 시간을 기반으로 파일명 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String fileName = blockNo + "_" + timestamp + ".xlsx";

        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        //System.out.println("subaeDownloadV2 -- " + month);
        //System.out.println("subaeDownloadV2 -- " + ucheck);

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        //Sheet sheet = workbook.createSheet("Sheet1");
        Sheet sheet = workbook.createSheet(blockNo);

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

        // 행 생성 및 스타일 적용
        //Row header = sheet.createRow(0);

        // 헤더 작성
        Row header = sheet.createRow(0);
        String[] titles = { "현장번호", "버전", "자재번호", "자재명", "BlockNo", "품목", "최초설계완료일", "SEPC", "수량", "수정여부", "주석" };


        for (int i = 0; i < titles.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        //CellRangeAddress(시작행, 끝행, 시작열, 끝열)
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, titles.length - 1));

        // 본문 기본 텍스트 스타일
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyStyle.setAlignment(HorizontalAlignment.LEFT);

        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 10);
        bodyFont.setFontName("맑은 고딕");
        bodyStyle.setFont(bodyFont);

        CellStyle bodyCenterStyle = workbook.createCellStyle();
        bodyCenterStyle.setBorderTop(BorderStyle.THIN);
        bodyCenterStyle.setBorderBottom(BorderStyle.THIN);
        bodyCenterStyle.setBorderLeft(BorderStyle.THIN);
        bodyCenterStyle.setBorderRight(BorderStyle.THIN);
        bodyCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        bodyCenterStyle.setFont(bodyFont);


        // 데이터 가져오기
        List<SubaeHogiBOM> dataList = new ArrayList<>();

        dataList = subaeHogiService.findSubaeBOMAsBlockNo(blockNo);

        for (int i = 0; i < dataList.size(); i++) {
            SubaeHogiBOM bom =  dataList.get(i);

            Row row = sheet.createRow(i + 1);
            String hogi = bom.getHogi();
            String hogiVer = bom.getHogiVersion();
            String partNo = bom.getPartNo();
            //String partName = bom.getPartName();
            String blockOpt =  bom.getBlockOpt();
            String codate =  bom.getCodate();
            String spec = bom.getSpec();
            String qty = bom.getQty();
            String uCheck = bom.getUcheck();
            String cmt = bom.getCmt();

            row.createCell(0).setCellValue(hogi);
            row.createCell(1).setCellValue(hogiVer);
            row.createCell(2).setCellValue(partNo);
            row.createCell(3).setCellValue(partName);
            row.createCell(4).setCellValue(blockNo);
            row.createCell(5).setCellValue(blockOpt);
            row.createCell(6).setCellValue(codate);
            row.createCell(7).setCellValue(spec);
            row.createCell(8).setCellValue(qty);
            row.createCell(9).setCellValue(uCheck);
            row.createCell(10).setCellValue(cmt);

            for (int m = 0; m < 11; m++) {
                row.getCell(m).setCellStyle(bodyStyle);
            }

        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();
    }
}
