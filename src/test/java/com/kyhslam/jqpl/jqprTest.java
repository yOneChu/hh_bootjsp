package com.kyhslam.jqpl;

import com.kyhslam.domain.JQPR;
import com.kyhslam.repository.JQPRRepository;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@Transactional
public class jqprTest {


    @Autowired
    JQPRRepository jqprRepository;


    @Test
    @Commit
    void save() {

        try {
            String filePath = "C:\\Users\\Administrator\\Documents\\sap_script\\JQPL-20251005.xlsx";
            //C:\Users\Administrator\Documents\sap_script
            //FileInputStream file = new FileInputStream(new File("JQPL-20250131.XLSX"));
            FileInputStream file = new FileInputStream(new File(filePath));

            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);

            for (int i = 1; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell = row.getCell(0); // JQPR 상태
                String jqprState = cell.getStringCellValue();

                Cell cell05 = row.getCell(5); // 기계설계
                String mUser = cell05.getStringCellValue();

                Cell cell06 = row.getCell(6); // 전기설계
                String eUser = cell06.getStringCellValue();

                Cell cell07 = row.getCell(7); // JQPR 진행현황
                String ingStatus = cell07.getStringCellValue();

                Cell cell08 = row.getCell(8); //JQPR NO
                String jqprNo = cell08.getStringCellValue();


                Cell cell09 = row.getCell(9); //접수일
                Date cell09Date = cell09.getDateCellValue();
                String receptDate = "";
                if (cell09Date != null) {
                    receptDate =new SimpleDateFormat("yyyy-MM-dd").format(cell09Date);
                }



                Cell cell10 = row.getCell(10); //글로벌
                String globalVal = cell10.getStringCellValue();

                Cell cell11 = row.getCell(11); // 관리번호
                String manageNo = cell11.getStringCellValue();

                Cell cell12 = row.getCell(12); // 프로젝트명
                String projectName = cell12.getStringCellValue();

                Cell cell13 = row.getCell(13); // 문제자재명
                String problemPartName = cell13.getStringCellValue();

                Cell cell14 = row.getCell(14); // 호기
                String hogi = cell14.getStringCellValue();

                Cell cell16 = row.getCell(16); // 작성자
                String creator = cell16.getStringCellValue();

                Cell cell24 = row.getCell(24); // 작성일
                Date creDate = cell24.getDateCellValue();
                String creDateVal = "";
                if (creDate != null) {
                    creDateVal =new SimpleDateFormat("yyyy-MM-dd").format(creDate);
                }


                Cell cell27 = row.getCell(27); //JQPR 유형
                String jqprtType = cell27.getStringCellValue();

                Cell cell31 = row.getCell(31); // 종결완료일
                Date finishDate = cell31.getDateCellValue();
                String finishDateVal = "";
                if (finishDate != null) {
                    finishDateVal =new SimpleDateFormat("yyyy-MM-dd").format(finishDate);
                }



                Cell cell32 = row.getCell(32); // 고장현상
                String problemStatus = cell32.getStringCellValue();

                Cell cell33 = row.getCell(33); // 고장원인
                String problemCause = cell33.getStringCellValue();

                Cell cell34 = row.getCell(34); //분류코드
                String typeCode = cell34.getStringCellValue();

                Cell cell35 = row.getCell(35); // item분류명
                String itemType = cell35.getStringCellValue();

                Cell cell36 = row.getCell(36); // 자재비
                String jajeCost = Integer.toString((int)cell36.getNumericCellValue());

                Cell cell37 = row.getCell(37); // 노무비
                String nomoCost = Integer.toString((int)cell37.getNumericCellValue());

                Cell cell39 = row.getCell(39); // 실패비용
                String failCost = Integer.toString((int)cell39.getNumericCellValue());


                Cell cell41 = row.getCell(41); // 내부부서명1
                String inName01 = cell41.getStringCellValue();

                Cell cell42 = row.getCell(42); // 내부부서비용1
                String inNameCost01 = Integer.toString((int)cell42.getNumericCellValue());

                Cell cell43 = row.getCell(43); // 내부부서명2
                String inName02 = cell43.getStringCellValue();

                Cell cell44 = row.getCell(44); // 내부부서비용2
                String inNameCost02 = Integer.toString((int)cell44.getNumericCellValue());

                Cell cell45 = row.getCell(45); // 내부부서명3
                String inName03 = cell45.getStringCellValue();

                Cell cell46 = row.getCell(46); // 내부부서비용3
                String inNameCost03 = Integer.toString((int)cell46.getNumericCellValue());

                JQPR  jqpr = new JQPR();
                jqpr.setStatus(jqprState);
                jqpr.setEUser(eUser);
                jqpr.setMUser(mUser);
                jqpr.setJqprNo(jqprNo);
                jqpr.setReceptDate(receptDate);

                jqpr.setGlobal(globalVal);
                jqpr.setManageNo(manageNo);

                jqpr.setProjectName(projectName);
                jqpr.setProblemPart(problemPartName);
                jqpr.setHogi(hogi);
                jqpr.setCreator(creator);
                jqpr.setCreDate(creDateVal);

                jqpr.setJqprType(jqprtType);
                jqpr.setFinishDate(finishDateVal);
                jqpr.setProblemStatus(problemStatus);
                jqpr.setProblemCause(problemCause);
                jqpr.setTypeCode(typeCode);
                jqpr.setItemType(itemType);
                jqpr.setJajeCost(jajeCost);
                jqpr.setNomoCost(nomoCost);
                jqpr.setFailCost(failCost);
                jqpr.setTeam01(inName01);
                jqpr.setTeam01Cost(inNameCost01);

                jqpr.setTeam02(inName02);
                jqpr.setTeam02Cost(inNameCost02);


                jqprRepository.save(jqpr);

                System.out.println(jqprNo + " > " + receptDate + " + " + problemCause + " - " + failCost);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


        System.out.println("--------- end -----------");


    }
}
