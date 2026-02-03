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

        //PART_NAME
        HashMap<String, String> nexMR_Map3 = new HashMap<>();
        HashMap<String, String> nexMRL_Map3 = new HashMap<>();
        HashMap<String, String> LUXEN_Map3 = new HashMap<>();

        for (int i = 0; i < partList.size(); i++) {
            PartPlanC vPartInfo = partList.get(i);
            String vPartNo = partList.get(i).getPartNo();
            String vPartName = partList.get(i).getPartName();
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

            if (brand.equals("NEX_MR_G")) {
                nexMR_Map3.put(vPartNo, vPartName);
            } else if (brand.equals("NEX_MRL_G")) {
                nexMRL_Map3.put(vPartNo, vPartName);
            } else if(brand.equals("LUXEN_G")) {
                LUXEN_Map3.put(vPartNo, vPartName);
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
                dto.setPartName(LUXEN_Map3.get(partNo));
            } else if(brand.equals("NEX_MR") && "KC01".equals(aspscd)){
                dto.setToCost(nexMR_Map.get(partNo));
                dto.setIndexNo(nexMR_Map2.get(partNo));
                dto.setPartName(nexMR_Map3.get(partNo));
            } else if(brand.equals("NEX_MRL") && "KC01".equals(aspscd)){
                dto.setToCost(nexMRL_Map.get(partNo));
                dto.setIndexNo(nexMRL_Map2.get(partNo));
                dto.setPartName(nexMRL_Map3.get(partNo));
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
                String partName =  dto.getPartName();
                //String exportDate = ""; // dto.getExportDate();

                if (brand.equals("NEX_MR_G")) {
                    brand = "NEX_MR";
                } else if (brand.equals("NEX_MRL_G")) {
                    brand = "NEX_MRL";
                } else if(brand.equals("LUXEN_G")) {
                    brand = "LUXEN_2";
                }

                System.out.println("partNo = " + partNo);
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


                HashMap<String, String> dateInfo = new HashMap<>();
                dateInfo = findMonth_V2(partNo, brand);

                dis202601 = Integer.parseInt(dateInfo.get("202601"));
                dis202602 = Integer.parseInt(dateInfo.get("202602"));
                dis202603 = Integer.parseInt(dateInfo.get("202603"));
                dis202604 = Integer.parseInt(dateInfo.get("202604"));
                dis202605 = Integer.parseInt(dateInfo.get("202605"));
                disTotal = Integer.parseInt(dateInfo.get("TOTAL"));


                PlanCDash planCDash = new PlanCDash();
                planCDash.setPartNo(partNo);
                planCDash.setBrand(brand);
                planCDash.setBlockNo(blockNo);
                planCDash.setPlanIndex(planIndex);
                planCDash.setPartName(partName);

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
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //월별조회 - 쿼리 한방에
    public HashMap<String, String> findMonth_V2(String partNo, String brand) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap<String, String> data = new HashMap<>();

        try {
            con = VaultDBConnection.getConnection();

            String sql = """
                    SELECT
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202601') AS DIS01,
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202602') AS DIS02,
                            (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202603') AS DIS03,
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202604') AS DIS04,
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202605') AS DIS05,
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202606') AS DIS06,
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202607') AS DIS07,
                        (select COUNT(a.part_no) AS COUNT
                                        from plancproduct A
                                        WHERE A.ASPSCD = 'KC01'
                                          AND A.part_no = ?
                                        and a.brand = ? AND LEFT(A.export_date, 6) = '202608') AS DIS08,
                        (select COUNT(a.part_no) AS COUNT
                                            from plancproduct A
                                            WHERE A.ASPSCD = 'KC01'
                                              AND A.part_no = ?
                                            and a.brand = ?) AS TOTAL
                    """;

            System.out.println(sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            pstmt.setString(2, brand);

            pstmt.setString(3, partNo);
            pstmt.setString(4, brand);

            pstmt.setString(5, partNo);
            pstmt.setString(6, brand);

            pstmt.setString(7, partNo);
            pstmt.setString(8, brand);

            pstmt.setString(9, partNo);
            pstmt.setString(10, brand);

            pstmt.setString(11, partNo);
            pstmt.setString(12, brand);

            pstmt.setString(13, partNo);
            pstmt.setString(14, brand);

            pstmt.setString(15, partNo);
            pstmt.setString(16, brand);

            pstmt.setString(17, partNo);
            pstmt.setString(18, brand);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String DIS01 = rs.getString("DIS01") == null ? "" : rs.getString("DIS01");
                String DIS02 = rs.getString("DIS02") == null ? "" : rs.getString("DIS02");
                String DIS03 = rs.getString("DIS03") == null ? "" : rs.getString("DIS03");
                String DIS04 = rs.getString("DIS04") == null ? "" : rs.getString("DIS04");
                String DIS05 = rs.getString("DIS05") == null ? "" : rs.getString("DIS05");
                String TOTAL = rs.getString("TOTAL") == null ? "" : rs.getString("TOTAL");
                //TOTAL

                data.put("202601", DIS01);
                data.put("202602", DIS02);
                data.put("202603", DIS03);
                data.put("202604", DIS04);
                data.put("202605", DIS05);
                data.put("TOTAL", TOTAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return data;
    }
}
