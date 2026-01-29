package com.kyhslam.planC;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.repository.PlanCRepository;
import com.kyhslam.service.PlanCService;
import com.kyhslam.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;

@SpringBootTest
public class partExcel {


    /**
     * PLAN-C 집계할 엑셀 데이터 넣기
     */


    @Autowired
    PlanCRepository repository;

    @Autowired
    PlanCService service;



    @Description("PLAN-C 대상 자재번호랑 가격 엑셀정보 저장")
    @Test
    public void excelDataSave() {


        StopWatch sw = new StopWatch();
        sw.start();

        //String fileName = "D:\\C자재번호 단가 없는거 (20250808).xlsx";
        String fileName = "C:\\excel\\PLAN-C_260129.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;


        try {


            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);
            workbook = new XSSFWorkbook(fis);


            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);


            for (int i = 1; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell01 = row.getCell(0); //index
                String index = cell01.getStringCellValue();

                //String brand = row.getCell(1).getStringCellValue();
                String brand = ExcelUtil.getCellValue(row.getCell(1));

                String partName = ExcelUtil.getCellValue(row.getCell(2));
                String partNo_as = ExcelUtil.getCellValue(row.getCell(3));
                String cost_as = ExcelUtil.getCellValue(row.getCell(4));

                String partNo = ExcelUtil.getCellValue(row.getCell(5));
                String cost = ExcelUtil.getCellValue(row.getCell(6));

                PartPlanC partPlanC = new PartPlanC();
                partPlanC.setPlanIndex(index);
                partPlanC.setBrand(brand);
                partPlanC.setPartName(partName.toUpperCase());
                partPlanC.setPartNo_as(partNo_as);
                partPlanC.setCost_as(cost_as);
                partPlanC.setPartNo(partNo);
                partPlanC.setCost(cost);

                service.save(partPlanC);
            }

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




    }
}
