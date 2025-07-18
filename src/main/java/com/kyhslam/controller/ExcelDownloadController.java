package com.kyhslam.controller;


import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.SubaeService;
import com.kyhslam.util.PartDashboardUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelDownloadController {

    private final SubaeService subaeService;

    @PostMapping("/subaeDownload")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"data.xlsx\"");

        // SXSSF 워크북 생성 (스트리밍)
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Sheet1");

        // 헤더 작성
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("이름");
        header.createCell(2).setCellValue("이메일");

        // 데이터 가져오기
        //List<MyDataDto> dataList = myDataService.getLargeData();
        ArrayList<ProductDto> dataList = new ArrayList<>();
        ProductDto param = new ProductDto();
        param.setProductAppdate("2025-07");

        dataList = subaeService.findSubaeProductList(param);
        for (int i = 0; i < dataList.size(); i++) {
            ProductDto dto = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dto.getProductNo());
            row.createCell(1).setCellValue(dto.getProductVersion());
            row.createCell(2).setCellValue(dto.getProductName());
            row.createCell(2).setCellValue(dto.getProductAppdate());

            row.createCell(2).setCellValue(dto.getPartNo());
            row.createCell(2).setCellValue(dto.getPartName());
            row.createCell(2).setCellValue(dto.getQty());
            row.createCell(2).setCellValue(dto.getCmt());
            row.createCell(2).setCellValue(dto.getProductName());
            row.createCell(2).setCellValue(dto.getProductName());

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
        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"SUBAE_PART_DATA.xlsx\"");

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


    @PostMapping("/searchPart")
    public void searchPart(HttpServletResponse response) throws IOException {

        System.out.println("--------- searchPart -----------");

        // HTTP 응답 헤더 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"PART_DATA.xlsx\"");

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
        String[] titles = { "자재번호", "자재명", "버전"};
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

        dataList = PartDashboardUtil.findPLMPartV1("2025", "10111175*", "ACTIVE");
        System.out.println("dataList = " + dataList.size());


        for (int i = 0; i < dataList.size(); i++) {
            PartInfoDTO dto = dataList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dto.getPartNo());
            row.createCell(1).setCellValue(dto.getPartName());
            row.createCell(2).setCellValue(dto.getVersion());


            row.getCell(0).setCellStyle(bodyStyle);
            row.getCell(1).setCellStyle(bodyStyle);
            row.getCell(2).setCellStyle(bodyStyle);


        }

        // 엑셀 파일 작성 및 스트림으로 출력
        workbook.write(response.getOutputStream());

        // 메모리 정리
        workbook.dispose(); // 임시파일 삭제
        workbook.close();

    }

}
