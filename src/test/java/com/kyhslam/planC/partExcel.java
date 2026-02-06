package com.kyhslam.planC;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.dto.PartWhere;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.PlanCRepository;
import com.kyhslam.service.PartPublicationService;
import com.kyhslam.service.PlanCService;
import com.kyhslam.util.ExcelUtil;
import com.kyhslam.util.SAPCommonUtil;
import com.kyhslam.util.SubaeCommonUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class partExcel {


    /**
     * PLAN-C 집계할 엑셀 데이터 넣기
     */


    @Autowired
    PlanCRepository repository;

    @Autowired
    PlanCService service;

    @Autowired
    PartPublicationService partPublicationService;



    @Description("PLAN-C 대상 자재번호랑 가격 엑셀정보 저장")
    @Test
    public void excelDataSave() {


        StopWatch sw = new StopWatch();
        sw.start();

        //String fileName = "D:\\C자재번호 단가 없는거 (20250808).xlsx";
        String fileName = "C:\\excel\\TET.xlsx";

        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;

        try {
            // 1. 엑셀 파일 읽기
            fis = new FileInputStream(fileName);

            workbook = WorkbookFactory.create(new File(fileName));
            //workbook = new XSSFWorkbook(fis);


            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();


            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            System.out.println("rowCnt = " + rowCnt);

            for (int i = 1; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell01 = row.getCell(0); //index
                String index = cell01.getStringCellValue();

                //String brand = row.getCell(1).getStringCellValue();
                String brand = ExcelUtil.getCellValue(row.getCell(1));

                String partName = ExcelUtil.getCellValue(row.getCell(2));
                String partNo_as = ExcelUtil.getCellValue(row.getCell(3));

                //String cost_as = ExcelUtil.getCellValue(row.getCell(4));
                /*double value01 = evaluator.evaluate(row.getCell(4)).getNumberValue();
                long intValue01 = (long) value01;
                String cost_as = String.valueOf(intValue01);*/

                double value = evaluator.evaluate(row.getCell(4)).getNumberValue();
                BigDecimal bd = BigDecimal.valueOf(value);
                long result01 = bd.setScale(0, RoundingMode.DOWN).longValue();
                String cost_as = String.valueOf(result01);

                String partNo = ExcelUtil.getCellValue(row.getCell(8));


                //String cost = ExcelUtil.getCellValue(row.getCell(9));
                //CellValue value02 = evaluator.evaluate(row.getCell(9));
                //String cost = String.valueOf(value02.getNumberValue());

                /*double value02 = evaluator.evaluate(row.getCell(9)).getNumberValue();
                long intValue = (long) value02;
                String cost = String.valueOf(intValue);*/
                double value02 = evaluator.evaluate(row.getCell(9)).getNumberValue();
                BigDecimal bd02 = BigDecimal.valueOf(value02);
                long result02 = Math.round(value02);
                String cost = String.valueOf(result02);



                PartPlanC partPlanC = new PartPlanC();
                partPlanC.setPlanIndex(index);
                partPlanC.setBrand(brand);
                partPlanC.setPartName(partName.toUpperCase());
                partPlanC.setPartNo_as(partNo_as);
                partPlanC.setCost_as(cost_as);
                partPlanC.setPartNo(partNo);
                partPlanC.setCost(cost);

                service.partSave(partPlanC);
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


    //수정 테스트
    @Test
    public void select() {
        List<PartPlanC> list = service.findAll();

        System.out.println("list = " + list.size());


        //해당 자재 사용하는 호기 찾기
        for(int i=0; i < 10; i++){

            PartWhere where = new PartWhere();
            where.setPartNo(list.get(i).getPartNo());
            ArrayList<ProductDto> productList = SubaeCommonUtil.findPartOfProduct_v2(where);

            String PARTNO = "";
            int findCnt = 0;

            for(int j=0; j < productList.size(); j++){
                ProductDto productDto = productList.get(j);
                String vPartNO = productDto.getPartNo();

                //System.out.println("productDto = " + productDto.getProductNo() + " > " + productDto.getPartNo());

                //1.호기들 원가절감실적조회로 조회해서 데이터 넣기
                PARTNO += vPartNO + ",";
                findCnt++;

                //100개씩 원가절감실적조회하기 - 속도때문에
                if (findCnt > 100) {
                    PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
                    SAPCommonUtil.findSAPIF(PARTNO,"20260101", "20261231");
                    PARTNO = "";
                    findCnt = 0;
                }
            }

            //N27200L19 > C189P001148

            //2.호기들 출하예정일 정보 넣기




        }
    }

}
