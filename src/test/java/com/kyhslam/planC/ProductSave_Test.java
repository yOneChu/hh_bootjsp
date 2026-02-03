package com.kyhslam.planC;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.PlanCDash;
import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.dto.PlanDashDTO;
import com.kyhslam.service.PlanCService;
import com.kyhslam.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class ProductSave_Test {

    @Autowired
    PlanCService service;

    @Test
    public void dateTest() {

        String today = DateUtil.getTodayDateNoHyphen();
        System.out.println("today = " + today);

    }

    //01
    @Description("PLAN-C 자재 읽어서(엑셀에 있던 자재) 원갈절감 실적 조회 후의 데이터 저장")
    @Test
    public void findCostData() {

        StopWatch sw = new StopWatch();
        sw.start();

        //중복 자재 제거
        ArrayList<String> dupCheck = new ArrayList<>();

        //EXCEL에 있는 PLAN C 대상 데이터 조회
        List<PartPlanC> list = service.findAll();

        System.out.println("list = " + list.size());

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        //N27200L19 > C189P001148
        ArrayList costData = new ArrayList();


        String PARTNO = "";
        int findCnt = 0;

        HashMap<String,String> oMap = new HashMap();


        for (int i = 0; i < list.size(); i++) {
            PartPlanC vPartInfo = list.get(i);
            String vPartNo = list.get(i).getPartNo();
            String toCost = list.get(i).getCost();
            String brand =  list.get(i).getBrand();

            oMap.put(vPartNo,toCost);
            System.out.println( (i+1) + "------- vPartNo = " + vPartNo);

            //이미 조회한거는 넘어간다. > 어차피 전체로 조회하기 때문에.
            if(dupCheck.contains(vPartNo)){
                continue;
            } else {
                dupCheck.add(vPartNo);
            }

            //System.out.println("vPartNo = " + vPartNo);
            //1.호기들 원가절감실적조회로 조회해서 데이터 넣기
            PARTNO += vPartNo + ",";
            findCnt++;

            //100개씩 원가절감실적조회하기 - 속도때문에
            if (findCnt > 100) {
                PARTNO = PARTNO.substring(0, PARTNO.length() - 1);

                //2026.01 이후부터 집계
                costData = SAPCommonUtil.findSAPIF(PARTNO, "20260101", "20261231");

                //저장
                service.getProductSave(costData, todayValue);

                // 5초 대기
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }

                PARTNO = "";
                findCnt = 0;
                oMap.clear();
            }


        } // end for

        if(PARTNO != null && !"".equals(PARTNO)){
            PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
            System.out.println("추가로 도는거 PARTNO = " + PARTNO);

            //2026.01 이후부터 집계
            costData = SAPCommonUtil.findSAPIF(PARTNO, "20260101", "20261231");

            //저장
            service.getProductSave(costData, todayValue);

            PARTNO = "";
            findCnt = 0;
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    //02
    @Description("출하예정일 셋팅")
    @Transactional
    @Commit  //COMMIT해야 데이터 수정됨
    @Test
    public void setExportData() {
        List<ProductPlanC> list = new ArrayList<>();
        list = service.findProductAll();


        //EXCEL에 있는 PLAN C 대상 데이터 조회
        List<PartPlanC> partList = service.findAll();
        HashMap<String, String> nexMR_Map = new HashMap<>();
        HashMap<String, String> nexMRL_Map = new HashMap<>();
        HashMap<String, String> LUXEN_Map = new HashMap<>();

        HashMap<String, String> nexMR_Map2 = new HashMap<>();
        HashMap<String, String> nexMRL_Map2 = new HashMap<>();
        HashMap<String, String> LUXEN_Map2 = new HashMap<>();

        for (int i = 0; i < partList.size(); i++) {
            PartPlanC vPartInfo = partList.get(i);
            String vPartNo = partList.get(i).getPartNo();
            String toCost = partList.get(i).getCost();
            String brand = partList.get(i).getBrand();
            String indexNo = partList.get(i).getPlanIndex();

            if(vPartNo != null && !"".equals(vPartNo)){ vPartNo = vPartNo.toUpperCase().trim(); }
            if(brand != null && !"".equals(brand)){ brand = brand.toUpperCase().trim(); }

            if (brand.equals("NEX_MR_G")) {
                nexMR_Map.put(vPartNo, toCost);
            } else if (brand.equals("NEX_MRL_G")) {
                nexMRL_Map.put(vPartNo, toCost);
            } else if(brand.equals("LUXEN_G")) {
                LUXEN_Map.put(vPartNo, toCost);
            }

            if (brand.equals("NEX_MR_G")) {
                nexMR_Map2.put(vPartNo, indexNo);
            } else if (brand.equals("NEX_MRL_G")) {
                nexMRL_Map2.put(vPartNo, indexNo);
            } else if(brand.equals("LUXEN_G")) {
                LUXEN_Map2.put(vPartNo, indexNo);
            }
        }

        //System.out.println("nexMR_Map2 ---- " + nexMR_Map2);

        HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        ArrayList<String> dataList = new ArrayList<>();

        for(int i=0;i<list.size();i++){
            ProductPlanC dto =  list.get(i);
            String hogi = dto.getProductNo();

            if ( !dataList.contains(hogi) ) {
                dataList.add(hogi);
            }
        }

        findExportDateV3(dataList, resultMap);

        /*for (String s : resultMap.keySet()) {
            HogiExportDTO dto =  resultMap.get(s);
            System.out.println(dto.getHogi() + " > " + dto.getSHIP_A());
        }*/

        //출하예정일 넣기
        for(int i=0;i<list.size();i++){
            ProductPlanC dto =  list.get(i);
            String hogi = dto.getProductNo();
            String partNo = dto.getPartNo();
            String brand = dto.getBrand();
            String blockNo = dto.getBlockNo();
            String aspscd = dto.getAspscd();
            blockNo = blockNo.substring(0, 1);

            if(brand != null && !"".equals(brand)){ brand = brand.toUpperCase().trim(); }
            if(partNo != null && !"".equals(partNo)){ partNo = partNo.toUpperCase().trim(); }

            if(resultMap.containsKey(hogi)){
                HogiExportDTO exportMap = resultMap.get(hogi.trim());
                if ("A".equals(blockNo)) {
                    dto.setExportDate(exportMap.getSHIP_A());
                } else if("B".equals(blockNo)) {
                    dto.setExportDate(exportMap.getSHIP_B());
                } else if("C".equals(blockNo)) {
                    dto.setExportDate(exportMap.getSHIP_C());
                } else if("D".equals(blockNo)) {
                    dto.setExportDate(exportMap.getSHIP_D());
                } else if("E".equals(blockNo)) {
                    dto.setExportDate(exportMap.getSHIP_E());
                } else if("F".equals(blockNo)) {
                    dto.setExportDate(exportMap.getSHIP_F());
                }
            }

            //COST, index 셋팅
            if(brand.equals("LUXEN_2") && "KC01".equals(aspscd)){
                dto.setToCost(LUXEN_Map.get(partNo));
                dto.setIndexNo(LUXEN_Map2.get(partNo));

            } else if(brand.equals("NEX_MR") && "KC01".equals(aspscd)){
                dto.setToCost(nexMR_Map.get(partNo));
                dto.setIndexNo(nexMR_Map2.get(partNo));
            } else if(brand.equals("NEX_MRL") && "KC01".equals(aspscd)){
                dto.setToCost(nexMRL_Map.get(partNo));
                dto.setIndexNo(nexMRL_Map2.get(partNo));
            }


        } // end for
        System.out.println(" -------------- END ---------------- ");
    }


    /**
     * 출하예정일 데이터 700개씩 나눠서 MAP에 넣는다.
     * @param data
     * @param resultMap
     * @return
     */
    public static HashMap<String, HogiExportDTO> findExportDateV3(ArrayList<String> data, HashMap<String, HogiExportDTO> resultMap) {

        //HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        try {

            String appendVal = "";

            int cnt = 0;
            for(int i=0; i < data.size(); i++) {
                //ArrayList<String> row = (ArrayList<String>) data.get(i);
                String hogi = data.get(i);
                appendVal += (" '" + hogi + "',");

                cnt++;

                if(cnt == 700) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    PartCommonUtil.findExportDateV4(appendVal, resultMap);
                    appendVal = "";
                    cnt = 0;
                }
            }

            if (cnt > 0) {
                if(appendVal != null && !appendVal.equals("")) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    PartCommonUtil.findExportDateV4(appendVal, resultMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }


    //03
    //조회한 데이터로 대시보드 집게
    @Description("엑셀의 자재INDEX를 기준으로 대시보드 테이블에 집계")
    @Test
    public void setDashboardData() {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        try {

            List<PartPlanC> list = new ArrayList<>();
            list = service.findAll();


            for(int i=0;i<list.size();i++) {
                PartPlanC dto = list.get(i);
                String partNo = dto.getPartNo();
                String brand = dto.getBrand();
                String blockNo = dto.getBlockNo();
                String planIndex = dto.getPlanIndex();
                //String exportDate = ""; // dto.getExportDate();

                if (brand.equals("NEX_MR_G")) {
                    brand = "NEX_MR";
                } else if (brand.equals("NEX_MRL_G")) {
                    brand = "NEX_MRL";
                } else if(brand.equals("LUXEN_G")) {
                    brand = "LUXEN_2";
                }

                /*List<ProductPlanC> product = service.findProductByPartNoBrand(partNo, brand);
                System.out.println("product = " + product.size());



                if(product != null && product.size() > 0) {
                    exportDate = product.get(0).getExportDate();
                }
                System.out.println("exportDate = " + exportDate);
                if(exportDate != null && !exportDate.equals("")) {
                    exportDate = exportDate.substring(0, 6);
                }*/
                System.out.println("partNo = " + partNo);
                //System.out.println("exportDate = " + exportDate);
                System.out.println("brand = " + brand);

                int dis202601 = 0;
                int dis202602 = 0;
                int dis202603 = 0;
                int dis202604 = 0;
                int dis202605 = 0;
                int dis202606 = 0;
                int dis202607 = 0;
                int dis202608 = 0;
                int dis202609 = 0;
                int dis202610 = 0;
                int dis202611 = 0;
                int dis202612 = 0;
                int disTotal = 0;

                dis202601 = findMonthCount(partNo, brand, "202601");
                dis202602 = findMonthCount(partNo, brand, "202602");
                dis202603 = findMonthCount(partNo, brand, "202603");
                dis202604 = findMonthCount(partNo, brand, "202604");
                dis202605 = findMonthCount(partNo, brand, "202605");
                dis202606 = findMonthCount(partNo, brand, "202606");
                dis202607 = findMonthCount(partNo, brand, "202607");
                dis202608 = findMonthCount(partNo, brand, "202608");
                dis202609 = findMonthCount(partNo, brand, "202609");
                dis202610 = findMonthCount(partNo, brand, "202610");
                dis202611 = findMonthCount(partNo, brand, "202611");
                dis202612 = findMonthCount(partNo, brand, "202612");
                disTotal = findMonthCount(partNo, brand, "total");


                PlanCDash planCDash = new PlanCDash();
                planCDash.setPartNo(partNo);
                planCDash.setBrand(brand);
                planCDash.setBlockNo(blockNo);
                planCDash.setPlanIndex(planIndex);

                planCDash.setDis202601(dis202601);
                planCDash.setDis202602(dis202602);
                planCDash.setDis202603(dis202603);
                planCDash.setDis202604(dis202604);
                planCDash.setDis202605(dis202605);
                planCDash.setDis202606(dis202606);
                planCDash.setDis202607(dis202607);
                planCDash.setDis202608(dis202608);
                planCDash.setDis202609(dis202609);
                planCDash.setDis202610(dis202610);
                planCDash.setDis202611(dis202611);
                planCDash.setDis202612(dis202612);
                planCDash.setTotalCnt(disTotal);

                planCDash.setBatchDate(todayValue);

                service.planDashSave(planCDash);

                System.out.println("202603 = " + dis202603);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Description("월별 조회")
//    @Test
    public int findMonthCount(String partNo, String brand, String month) {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String,String>>();

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        //partNo = "C18210766G0300";
        //brand = "LUXEN_2";
        //month = "202603";

        int result = 0;

        String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
        String id  = "SA";
        String pw  = "AutodeskVault@26200"; // "qwe123!@#"

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            con = DriverManager.getConnection(url,id,pw);

            System.out.println("con = " + con);
            
            String sql = """
                    select COUNT(a.part_no) AS COUNT
                    from plancproduct A
                    WHERE A.ASPSCD = 'KC01'
                      AND A.part_no = ?
                    and a.brand = ?
                    """;

            if ("total".equals(month)) {

            } else {
                sql += "AND LEFT(A.export_date, 6) = ? ";
                sql += "AND A.export_date IS NOT NULL";
            }




            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1,partNo);
            pstmt.setString(2,brand);
            //pstmt.setString(3,month);
            if ("total".equals(month)) {

            } else {
                pstmt.setString(3,month);
            }

            rs = pstmt.executeQuery();

            while(rs.next())
            {
                String COUNT = rs.getString("COUNT") == null ? "" : rs.getString("COUNT"); // 파일명

                result =  Integer.parseInt(COUNT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con,pstmt,rs);
        }

        return result;
    }
}
