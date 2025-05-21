package com.kyhslam.service;

import com.kyhslam.domain.JQPR;
import com.kyhslam.dto.JqplDTO;
import com.kyhslam.repository.JQPRRepository;
import com.kyhslam.util.VaultDBConnection;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JQPRService {

    private final JQPRRepository jqprRepository;


    public ArrayList<JqplDTO> getJqplDashbard() {
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        ArrayList<JqplDTO> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {
            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT  ");
            sql.append(" A.DASH_PUBLICDATA_ID, BOM_STATUS, COMPLETE_STATUS, CRE_DATE, CREATOR, E_USER, ETC_TEAM, ETC_TEAM_COST, ");
            sql.append(" F_COMPANY, F_COMPANY_COST, FAIL_COST, HOGI, ITEM_TYPE, JAJE_COST, JQPR_NO, JQPR_TYPE, M_USER, MANAGE_NO, ");
            sql.append(" NOMO_COST, PROBLEM_CAUSE, ");
            sql.append(" PROBLEM_PART, PROBLEM_STATUS, PROJECT_NAME, RECEPT_DATE, STATUS, STATUS02, ");
            sql.append(" TEAM01, TEAM01COST, TEAM02, TEAM02COST, TEAM03, TEAM03COST, TYPE_CODE ");
            sql.append(" FROM jqpr_data A ");

            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                //String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");
                String BOM_STATUS = rs.getString("BOM_STATUS") == null ? "" : rs.getString("BOM_STATUS");
                String COMPLETE_STATUS = rs.getString("COMPLETE_STATUS") == null ? "" : rs.getString("COMPLETE_STATUS");


                String CRE_DATE = rs.getString("CRE_DATE") == null ? "" : rs.getString("CRE_DATE");
                String CREATOR = rs.getString("CREATOR") == null ? "" : rs.getString("CREATOR");
                String E_USER = rs.getString("E_USER") == null ? "" : rs.getString("E_USER");
                String ETC_TEAM = rs.getString("ETC_TEAM") == null ? "" : rs.getString("ETC_TEAM");
                String ETC_TEAM_COST = rs.getString("ETC_TEAM_COST") == null ? "" : rs.getString("ETC_TEAM_COST");

                String F_COMPANY = rs.getString("F_COMPANY") == null ? "" : rs.getString("F_COMPANY");
                String F_COMPANY_COST = rs.getString("F_COMPANY_COST") == null ? "" : rs.getString("F_COMPANY_COST");
                String FAIL_COST = rs.getString("FAIL_COST") == null ? "" : rs.getString("FAIL_COST");

                String HOGI = rs.getString("HOGI") == null ? "" : rs.getString("HOGI");
                String JAJE_COST = rs.getString("JAJE_COST") == null ? "" : rs.getString("JAJE_COST");
                String ITEM_TYPE = rs.getString("ITEM_TYPE") == null ? "" : rs.getString("ITEM_TYPE");
                String JQPR_NO = rs.getString("JQPR_NO") == null ? "" : rs.getString("JQPR_NO");
                String JQPR_TYPE = rs.getString("JQPR_TYPE") == null ? "" : rs.getString("JQPR_TYPE");

                String M_USER = rs.getString("M_USER") == null ? "" : rs.getString("M_USER");
                String MANAGE_NO = rs.getString("MANAGE_NO") == null ? "" : rs.getString("MANAGE_NO");
                String NOMO_COST = rs.getString("NOMO_COST") == null ? "" : rs.getString("NOMO_COST");
                String PROBLEM_CAUSE = rs.getString("PROBLEM_CAUSE") == null ? "" : rs.getString("PROBLEM_CAUSE");


                String PROBLEM_PART = rs.getString("PROBLEM_PART") == null ? "" : rs.getString("PROBLEM_PART");
                String PROBLEM_STATUS = rs.getString("PROBLEM_STATUS") == null ? "" : rs.getString("PROBLEM_STATUS");
                String PROJECT_NAME = rs.getString("PROJECT_NAME") == null ? "" : rs.getString("PROJECT_NAME");
                String RECEPT_DATE = rs.getString("RECEPT_DATE") == null ? "" : rs.getString("RECEPT_DATE");
                String STATUS = rs.getString("STATUS") == null ? "" : rs.getString("STATUS");
                String STATUS02 = rs.getString("STATUS02") == null ? "" : rs.getString("STATUS02");

                String TEAM01 = rs.getString("TEAM01") == null ? "" : rs.getString("TEAM01");
                String TEAM01COST = rs.getString("TEAM01COST") == null ? "" : rs.getString("TEAM01COST");
                String TEAM02 = rs.getString("TEAM02") == null ? "" : rs.getString("TEAM02");
                String TEAM02COST = rs.getString("TEAM02COST") == null ? "" : rs.getString("TEAM02COST");
                String TEAM03 = rs.getString("TEAM03") == null ? "" : rs.getString("TEAM03");
                String TEAM03COST = rs.getString("TEAM03COST") == null ? "" : rs.getString("TEAM03COST");

                String TYPE_CODE = rs.getString("TYPE_CODE") == null ? "" : rs.getString("TYPE_CODE");

                JqplDTO dto = new JqplDTO();
                dto.setJqprNo(JQPR_NO);
                dto.setManageNo(MANAGE_NO);
                dto.setStatus(STATUS);
                dto.setStatus02(STATUS02);
                dto.setBomStatus(BOM_STATUS);
                dto.setReceptDate(RECEPT_DATE);

                dto.setEUser(E_USER);
                dto.setMUser(M_USER);

                dto.setProjectName(PROJECT_NAME);
                dto.setProblemPart(PROBLEM_PART);
                dto.setHogi(HOGI);
                dto.setCreator(CREATOR);
                dto.setCreDate(CRE_DATE);
                dto.setJqprType(JQPR_TYPE);
                dto.setProblemStatus(PROBLEM_STATUS);
                dto.setProblemCause(PROBLEM_CAUSE);
                dto.setTypeCode(TYPE_CODE);
                dto.setItemType(ITEM_TYPE);
                dto.setJajeCost(JAJE_COST);
                dto.setNomoCost(NOMO_COST);
                dto.setFailCost(FAIL_COST);

                dto.setTeam01(TEAM01);
                dto.setTeam01Cost(TEAM01COST);
                dto.setTeam02(TEAM02);
                dto.setTeam02Cost(TEAM02COST);
                dto.setTeam03(TEAM03);
                dto.setTeam03Cost(TEAM03COST);

                dto.setFCompany(F_COMPANY);
                dto.setFCompanyCost(F_COMPANY_COST);

                dto.setEtcTeam(ETC_TEAM);
                dto.setEtcTeamCost(ETC_TEAM_COST);
                dto.setCompleteStatus(COMPLETE_STATUS);
                //System.out.println(JQPR_NO + " > " + PROBLEM_CAUSE + " > " + JQPR_TYPE);

                result.add(dto);
            } // end while



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    @Transactional
    public void getJQPRData() {
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
            String id = "SA";
            String pw = "AutodeskVault@26200";

            StringBuffer sql = new StringBuffer();


            sql.append(" with dummytalbe as    ");

            pstmt = con.prepareStatement(sql.toString());

            String type = "";
            pstmt.setString(1, type);
            pstmt.setString(2, type);
            pstmt.setString(3, type);
            pstmt.setString(4, type);
            pstmt.setString(5, type);

            rs = pstmt.executeQuery();

            int totalCnt = 0;
            while (rs.next()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException ex) {
            }

            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException ex) {
            }

            try {
                if (con != null)
                    con.close();
            } catch (SQLException ex) {
            }
        }
    }


    public void excelWriteProcess() {

        try {

            FileInputStream file = new FileInputStream(new File("JQPL-20250131.XLSX"));

            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            int rowCnt = sheet.getPhysicalNumberOfRows();

            System.out.println("rowCnt = " + rowCnt);

            for (int i = 1; i < rowCnt; i++) {
                Row row = sheet.getRow(i);

                Cell cell00 = row.getCell(0); //상태
                String status = cell00.getStringCellValue();

                Cell cell01 = row.getCell(1); //변상합의상태
                String status02 = cell01.getStringCellValue();

                Cell cell04 = row.getCell(4); //BOM상태
                String bomStatus = cell04.getStringCellValue();

                Cell cell05 = row.getCell(5); //기계설계
                String mUser = cell05.getStringCellValue();

                Cell cell06 = row.getCell(6); //전기설계
                String eUser = cell06.getStringCellValue();

                Cell cell08 = row.getCell(8); //JQPR NO
                String jqprNo = cell08.getStringCellValue();


                Cell cell09 = row.getCell(9); //접수일
                //String receptDate = cell09.getStringCellValue();
                Date cell09Date = cell09.getDateCellValue();
                //String a = new SimpleDateFormat("yyyy-mm-dd").format(receptDate);

                String receptDate = "";
                if (cell09Date != null) {
                    receptDate =new SimpleDateFormat("yyyy-MM-dd").format(cell09Date);
                }

                Cell cell10 = row.getCell(10); //관리번호
                String manageNo = cell10.getStringCellValue();

                Cell cell11 = row.getCell(11); //프로젝트명
                String pjtName = cell11.getStringCellValue();

                Cell cell12 = row.getCell(12); //문제자재명
                String problemPart = cell12.getStringCellValue();

                Cell cell13 = row.getCell(13); //호기
                String hogi = cell13.getStringCellValue();

                Cell cell15 = row.getCell(15); //작성자
                String creator = cell15.getStringCellValue();

                Cell cell26 = row.getCell(26); //JQPR 유형
                String jqprtType = cell26.getStringCellValue();

                Cell cell27 = row.getCell(27); //승인부서 결재상신자
                String comTeamCuser = cell27.getStringCellValue();

                Cell cell28 = row.getCell(28); //승인부서 결재상신일
                Date cell28Date = cell28.getDateCellValue();

                String comTeamDate = "";
                if (cell28Date != null) {
                    comTeamDate =new SimpleDateFormat("yyyy-MM-dd").format(cell28Date);
                }


                Cell cell29 = row.getCell(29); //최종결재자
                String comTeamRuser = cell29.getStringCellValue();

                Cell cell30 = row.getCell(30); //종결완료일
                Date cell30Date = cell30.getDateCellValue();
                String completeDate = "";
                if (cell30Date != null) {
                    completeDate =new SimpleDateFormat("yyyy-MM-dd").format(cell30Date);
                }



                Cell cell31 = row.getCell(31); //고장현상
                String problemStatus = cell31.getStringCellValue();

                Cell cell32 = row.getCell(32); //고장원인
                String problemCause = cell32.getStringCellValue();

                Cell cell33 = row.getCell(33); //분류코드
                String typeCode = cell33.getStringCellValue();

                Cell cell34 = row.getCell(34); //item분류명
                String itemType = cell34.getStringCellValue();

                Cell cell35 = row.getCell(35); //자재비
                String cost01 = Integer.toString((int)cell35.getNumericCellValue());

                Cell cell36 = row.getCell(36); //노무비
                String cost02 = Integer.toString((int)cell36.getNumericCellValue());

                Cell cell38 = row.getCell(38); //실패비용
                String failCost = Integer.toString((int)cell38.getNumericCellValue());


                Cell cell40 = row.getCell(40); //내부부서명1
                String team01 = "";
                if (cell40.getStringCellValue() != null) {
                    team01 = cell40.getStringCellValue();
                }

                Cell cell41 = row.getCell(41); //내부부서명1 비용
                String team01Cost = Integer.toString((int)cell41.getNumericCellValue());

                Cell cell42 = row.getCell(42); //내부부서명2
                String team02 = cell42.getStringCellValue();
                Cell cell43 = row.getCell(43); //내부부서명2 비용
                String team02Cost = Integer.toString((int)cell43.getNumericCellValue());

                Cell cell44 = row.getCell(44); //내부부서명3
                String team03 = cell44.getStringCellValue();
                Cell cell45 = row.getCell(45); //내부부서명3 비용
                String team03Cost = Integer.toString((int)cell45.getNumericCellValue());


                Cell cell46 = row.getCell(46); //외부업체명
                String fCompanyName = cell46.getStringCellValue();

                Cell cell47 = row.getCell(47); //업체변상금액
                String fCompanyCost = Integer.toString((int)cell47.getNumericCellValue());

                Cell cell50 = row.getCell(50); //기타부서명
                String etcTeamName = cell50.getStringCellValue();

                Cell cell51 = row.getCell(51); //기타부서비
                String etcTeamCost = Integer.toString((int)cell51.getNumericCellValue());

                Cell cell53 = row.getCell(53); //조치상태
                String completeStatus = cell53.getStringCellValue();

                JQPR dto = new JQPR();
                dto.setStatus(status);
                dto.setStatus02(status02);
                dto.setBomStatus(bomStatus);

                dto.setJqprNo(jqprNo);
                dto.setJqprType(jqprtType);
                dto.setEUser(eUser);
                dto.setMUser(mUser);
                dto.setReceptDate(receptDate); //접수일

                dto.setProjectName(pjtName);
                dto.setProblemPart(problemPart);
                dto.setProblemStatus(problemStatus); //고장현상
                dto.setHogi(hogi);
                dto.setCreator(creator);

                dto.setTypeCode(typeCode); //분류코드
                dto.setItemType(itemType); //ITEM분류명

                dto.setTeam01(team01);
                dto.setTeam01Cost(team01Cost);
                dto.setTeam02(team02);
                dto.setTeam02Cost(team02Cost);
                dto.setTeam03(team03);
                dto.setTeam03Cost(team03Cost);

                dto.setJajeCost(cost01); //자재비
                dto.setNomoCost(cost02); //노무비
                dto.setFailCost(failCost); //실패비용

                dto.setFCompany(fCompanyName);
                dto.setFCompanyCost(fCompanyCost);
                dto.setEtcTeam(etcTeamName);
                dto.setEtcTeamCost(etcTeamCost);
                dto.setCompleteStatus(completeStatus);
                jqprRepository.save(dto);

                System.out.println(jqprNo + " > " + receptDate + " + " + cost02 + " - " + failCost);
            }

            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public String returnStringValue(XSSFWorkbook workbook, Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                double doubleVal = cell.getNumericCellValue();

                if (DateUtil.isValidExcelDate(doubleVal)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return String.valueOf(dateFormat.format(cell.getDateCellValue()));
                } else {
                    return String.valueOf(doubleVal);
                }
                //return String.valueOf(doubleVal);
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                return String.valueOf(cell.getErrorCellValue());
            case BLANK:
                return "";
            case FORMULA:
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                DataFormatter dataFormatter = new DataFormatter();
                //return cell.getCellFormula(); 수식그대로
                return dataFormatter.formatCellValue(evaluator.evaluateInCell(cell));
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }

    }

}
