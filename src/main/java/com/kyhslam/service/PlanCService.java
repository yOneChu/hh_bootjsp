package com.kyhslam.service;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.PlanCDash;
import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.repository.PlanCRepository;
import com.kyhslam.util.DateUtil;
import com.kyhslam.util.PartCommonUtil;
import com.kyhslam.util.SAPCommonUtil;
import com.kyhslam.util.VaultDBConnection;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("PlanCService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanCService {

    private final PlanCRepository repository;

    @Transactional
    public void partSave(PartPlanC partPlanC) {
        repository.partSave(partPlanC);
    }

    @Transactional
    public void productSave(ProductPlanC productPlanC) {
        repository.productSave(productPlanC);
    }

    @Transactional
    public void planDashSave(PlanCDash planDash) {
        repository.planDashSave(planDash);
    }


    public List<PlanCDash> findPlanDash(String batchDate) {
        List<PlanCDash> list = repository.findPlanDash(batchDate);
        return list;
    }

    public List<PlanCDash> findPlanDashAsBrand(String batchDate, String brand) {
        List<PlanCDash> list = repository.findPlanDashAsBrand(batchDate, brand);
        return list;
    }

    public List<PlanCDash> findPlanDashAsBrand(String batchDate, String brand, String partName) {
        List<PlanCDash> list = repository.findPlanDashAsBrand(batchDate, brand, partName);
        return list;
    }

    //findPlanDashAsPartName
    public List<PlanCDash> findPlanDashAsPartName(String batchDate, String partName) {
        List<PlanCDash> list = repository.findPlanDashAsPartName(batchDate, partName);
        return list;
    }

    public List<PartPlanC> findAll() {
        List<PartPlanC> list = repository.findAll();
        return list;
    }


    public List<ProductPlanC> findProductAll() {
        List<ProductPlanC> list = repository.findProductAll();
        return list;
    }

    public List<ProductPlanC> findProductAll_v2() {
        List<ProductPlanC> list = repository.findProductAll_v2();
        return list;
    }

    public List<ProductPlanC> findProductByBlock(String blockNo) {
        List<ProductPlanC> list = repository.findProductByBlock(blockNo);
        return list;
    }

    public List<ProductPlanC> findProductByBatchDate(String batchDate) {
        List<ProductPlanC> list = repository.findProductByBatchDate(batchDate);
        return list;
    }

    public List<ProductPlanC> findProductByBatchDate_v2(String batchDate, String blockNo) {
        List<ProductPlanC> list = repository.findProductByBatchDate_v2(batchDate, blockNo);
        return list;
    }

    public List<ProductPlanC> findProductByBatchDate_v3(String batchDate, String partNo, String brand, String month) {
        List<ProductPlanC> list = repository.findProductByBatchDate_v3(batchDate, partNo, brand, month);
        return list;
    }

    //findProductByPartName
    public List<ProductPlanC> findProductByPartName(String batchDate, String partName) {
        List<ProductPlanC> list = repository.findProductByPartName(batchDate, partName);
        return list;
    }

    public List<ProductPlanC> findProductByHogi(String productNo, String partNo) {
        List<ProductPlanC> list = repository.findProductByHogi(productNo, partNo);
        return list;
    }

    public List<ProductPlanC> findProductByPartNoBrand(String partNo, String brand) {
        List<ProductPlanC> list = repository.findProductByPartNoBrand(partNo, brand);
        return list;
    }

    //findPlanDashSum
    public List<Tuple> findPlanDashSum(String batchDate) {
        List<Tuple> list = repository.findPlanDashSum(batchDate);
        return list;
    }

    //특정일자 배치 삭제
    public void deletePlanCProduct() {

        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        try {
            con = VaultDBConnection.getConnection();

            StringBuffer temSql = new StringBuffer();
            temSql.append(" DELETE FROM plancproduct ");
            temSql.append(" WHERE BATCH_DATE = ?   ");
            //temSql.append(" NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,   ");

            pstmt = con.prepareStatement(temSql.toString());
            pstmt.setString(1, todayValue);

            //rs = pstmt.executeQuery();
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con,pstmt,rs);
        }
    }

    /**
     * 매일 PLAN-C 배치 실행
     * 저녁 10시 20분
     */
    //01
    @Description("PLAN-C 자재 읽어서(엑셀에 있던 자재) 원갈절감 실적 조회 후의 데이터 저장")
    @Scheduled(cron = "0 10 01 * * *")
    public void findCostData() {
        StopWatch sw = new StopWatch();
        sw.start();

        //중복 자재 제거
        ArrayList<String> dupCheck = new ArrayList<>();

        //EXCEL에 있는 PLAN C 대상 데이터 조회
        List<PartPlanC> list = findAll();

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
                if(costData != null &&  costData.size()>0){
                    getProductSave(costData, todayValue);
                }

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
            if(costData != null && costData.size()>0){
                getProductSave(costData, todayValue);
            }

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


    /**
     * @apiNote 월가절감조회 완료 한 데이터 DB에 저장
     * @param costData
     * @param todayValue
     */
    @Description("월가절감조회 완료 한 데이터 DB에 저장")
    public void getProductSave(ArrayList costData, String todayValue) {

        for (int i = 0; i < costData.size(); i++) {

            ArrayList row = (ArrayList) costData.get(i);

            //원가절감 추출 데이터 1차 저장
            String erpSendDate = (String) row.get(0); //StringUtil.NVL(row.get(0), "*");

            String hogi = (String) row.get(1); //StringUtil.NVL(row.get(1), "*");
            String partNo = (String) row.get(3); // StringUtil.NVL(row.get(3), "*");
            String qty = (String) row.get(4); // StringUtil.NVL(row.get(4), "*"); // 수량
            String dwgNo = (String) row.get(5);  //StringUtil.NVL(row.get(5), "*"); // 도면번호
            String blockNo = (String) row.get(6); // StringUtil.NVL(row.get(6), "*");
            String gongSa = (String) row.get(7); //StringUtil.NVL(row.get(7), "*");
            String gisong = (String) row.get(8); // StringUtil.NVL(row.get(8), "*"); // 기종
            String brand = (String) row.get(9); //브랜드

            String spec = (String) row.get(10); //StringUtil.NVL(row.get(9), "*"); // 스펙

            String createNation = (String) row.get(11); // StringUtil.NVL(row.get(11), "*"); // 생산거점
            String module = (String) row.get(12); // StringUtil.NVL(row.get(11), "*"); // 모듈러
            String mUser = (String) row.get(13); //StringUtil.NVL(row.get(10), "*"); // 기계담당자
            String eUser = (String) row.get(14); // StringUtil.NVL(row.get(11), "*"); // 전기담당자

            //출하예정일
            ProductPlanC pData = new ProductPlanC();
            pData.setPartNo(partNo);
            pData.setErpSendDate(erpSendDate);
            pData.setProductNo(hogi);
            pData.setQty(qty);
            pData.setDwgNo(dwgNo);
            pData.setBlockNo(blockNo);
            pData.setGongSa(gongSa);
            pData.setGisong(gisong);
            pData.setSpec(spec);
            pData.setBrand(brand);

            pData.setAspscd(createNation);
            pData.setMmanager(mUser);
            pData.setEmanager(eUser);
            pData.setModule(module);

            pData.setBatchDate(todayValue); //배치수행일

            //원가실적조회 결과 데이터 저장
            productSave(pData);
        }
    }

    //02
    @Description("출하예정일 셋팅")
    @Scheduled(cron = "0 0 02 * * *")
    public void setExportData() {
        StopWatch sw = new StopWatch();
        sw.start();

        List<ProductPlanC> list = new ArrayList<>();
        list = findProductAll();


        //EXCEL에 있는 PLAN C 대상 데이터 조회
        List<PartPlanC> partList = findAll();

        HashMap<String,String> SUBWEIGHT_Map = new HashMap();
        HashMap<String,String> SUBWEIGHT_Map2 = new HashMap();

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

            if("SUBWEIGHT".equals(vPartName.trim())) {
                SUBWEIGHT_Map.put(vPartNo, toCost); //단위절감액
                SUBWEIGHT_Map2.put(vPartNo, vPartName);
            }
        }

        System.out.println("nexMR_Map2 ---- " + nexMR_Map2);

        HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        ArrayList<String> dataList = new ArrayList<>();

        for(int i=0;i<list.size();i++){
            ProductPlanC dto =  list.get(i);
            String hogi = dto.getProductNo();

            if ( !dataList.contains(hogi) ) {
                dataList.add(hogi);
            }
        }

        //출하예정일 조회
        findExportDateV3(dataList, resultMap);

        /*for (String s : resultMap.keySet()) {
            HogiExportDTO dto =  resultMap.get(s);
            System.out.println(dto.getHogi() + " > " + dto.getSHIP_A());
        }*/

        System.out.println("list.size() = " + list.size());
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

            if(SUBWEIGHT_Map.containsKey(partNo)){
                dto.setToCost(SUBWEIGHT_Map.get(partNo));
                dto.setIndexNo("5-2-1-1");
                dto.setPartName(SUBWEIGHT_Map2.get(partNo));
            }

        } // end for
        System.out.println(" -------------- END ---------------- ");
        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ PLAN-C 02 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }

    //03
    @Description("엑셀의 자재INDEX를 기준으로 대시보드 테이블에 집계 > ERP전송날짜 기준으로")
    @Scheduled(cron = "0 40 02 * * *")
    public void setDashboardData() {
        StopWatch sw = new StopWatch();
        sw.start();

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();
        try {

            List<PartPlanC> list = new ArrayList<>();
            list = findAll();

            for(int i=0;i<list.size();i++) {
                PartPlanC dto = list.get(i);
                String partNo = dto.getPartNo();
                String brand = dto.getBrand();
                String blockNo = dto.getBlockNo();
                String planIndex = dto.getPlanIndex();
                String partName =  dto.getPartName();
                String toCost = dto.getCost();
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

                //ERP전송일자로 집계
                //dateInfo = findMonth_V2(partNo, brand);
                if("SUBWEIGHT".equals(partName)){
                    dateInfo = findMonth_SUBWEIGHT(partNo);
                } else {
                    dateInfo = findMonth_V3(partNo, brand);
                }

                if(dateInfo == null || dateInfo.get("202601") == null || "".equals(dateInfo.get("202601"))) continue;

                //System.out.println("dateInfo.get(\"202601\") = " + dateInfo.get("202601"));
                dis202601 = Integer.parseInt(dateInfo.get("202601"));
                dis202602 = Integer.parseInt(dateInfo.get("202602"));
                dis202603 = Integer.parseInt(dateInfo.get("202603"));
                dis202604 = Integer.parseInt(dateInfo.get("202604"));
                dis202605 = Integer.parseInt(dateInfo.get("202605"));
                dis202606 = Integer.parseInt(dateInfo.get("202606"));
                dis202607 = Integer.parseInt(dateInfo.get("202607"));
                dis202608 = Integer.parseInt(dateInfo.get("202608"));
                dis202609 = Integer.parseInt(dateInfo.get("202609"));
                dis202610 = Integer.parseInt(dateInfo.get("202610"));
                dis202611 = Integer.parseInt(dateInfo.get("202611"));
                dis202612 = Integer.parseInt(dateInfo.get("202612"));
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

                if(toCost != null && !toCost.equals("")) {
                    planCDash.setToCost(Integer.parseInt(toCost));
                }

                planCDash.setBatchDate(todayValue);
                planDashSave(planCDash);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ PLAN-C 03 배치 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }

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

    public HashMap<String, String> findMonth_V3(String partNo, String brand) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String todayVal = DateUtil.getTodayDate();

        HashMap<String, String> data = new HashMap<>();
        try {
            con = VaultDBConnection.getConnection();

            /*String sql = """
                    SELECT
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202601' THEN 1 END) AS DIS01,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202602' THEN 1 END) AS DIS02,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202603' THEN 1 END) AS DIS03,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202604' THEN 1 END) AS DIS04,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202605' THEN 1 END) AS DIS05,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202606' THEN 1 END) AS DIS06,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202607' THEN 1 END) AS DIS07,
                        COUNT(CASE WHEN LEFT(erp_send_date, 6) = '202608' THEN 1 END) AS DIS08,
                        COUNT(part_no) AS TOTAL,
                        MAX(to_cost) AS to_cost -- 그룹화 시 단일 값을 가져오기 위해 MAX/MIN 사용
                    FROM plancproduct
                    WHERE
                      part_no = ?
                      AND ASPSCD = 'KC01'
                      AND brand = ?
                      AND batch_date = ?
                    """;*/

            String sql = """
                    SELECT
                    SUM(CASE WHEN LEFT(erp_send_date, 6) = '202601'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS01,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202602'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS02,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202603'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS03,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202604'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS04,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202605'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS05,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202606'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS06,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202607'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS07,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202608'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS08,
                        -- 전체 수량 합계
                        SUM(TRY_CAST(REPLACE(qty, ',', '') AS INT)) AS TOTAL,
                        MAX(to_cost) AS to_cost
                    FROM plancproduct
                    WHERE
                      part_no = ?
                      AND ASPSCD = 'KC01'
                      AND brand = ?
                      AND batch_date = ?
                    """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            pstmt.setString(2, brand);
            pstmt.setString(3, todayVal);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String DIS01 = rs.getString("DIS01") == null ? "" : rs.getString("DIS01");
                String DIS02 = rs.getString("DIS02") == null ? "" : rs.getString("DIS02");
                String DIS03 = rs.getString("DIS03") == null ? "" : rs.getString("DIS03");
                String DIS04 = rs.getString("DIS04") == null ? "" : rs.getString("DIS04");
                String DIS05 = rs.getString("DIS05") == null ? "" : rs.getString("DIS05");
                String TOTAL = rs.getString("TOTAL") == null ? "" : rs.getString("TOTAL");
                String to_cost = rs.getString("TOTAL") == null ? "" : rs.getString("to_cost");

                data.put("202601", DIS01);
                data.put("202602", DIS02);
                data.put("202603", DIS03);
                data.put("202604", DIS04);
                data.put("202605", DIS05);
                data.put("TOTAL", TOTAL);
                data.put("to_cost", to_cost);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return data;
    }

    public HashMap<String, String> findMonth_SUBWEIGHT(String partNo) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String todayVal = DateUtil.getTodayDate();

        HashMap<String, String> data = new HashMap<>();
        try {
            con = VaultDBConnection.getConnection();

            String sql = """
                    SELECT
                    SUM(CASE WHEN LEFT(erp_send_date, 6) = '202601'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS01,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202602'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS02,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202603'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS03,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202604'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS04,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202605'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS05,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202606'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS06,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202607'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS07,
                        SUM(CASE WHEN LEFT(erp_send_date, 6) = '202608'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS08,
                         SUM(CASE WHEN LEFT(erp_send_date, 6) = '202609'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS09,
                         SUM(CASE WHEN LEFT(erp_send_date, 6) = '202610'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS10,
                         SUM(CASE WHEN LEFT(erp_send_date, 6) = '202611'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS11,
                         SUM(CASE WHEN LEFT(erp_send_date, 6) = '202612'
                                 THEN TRY_CAST(REPLACE(qty, ',', '') AS INT) ELSE 0 END) AS DIS12,
                        -- 전체 수량 합계
                        SUM(TRY_CAST(REPLACE(qty, ',', '') AS INT)) AS TOTAL,
                        MAX(to_cost) AS to_cost
                    FROM plancproduct
                    WHERE
                      part_no = ?
                      AND ASPSCD = 'KC01'
                      AND batch_date = ?
                      --AND brand = ?
                    """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            pstmt.setString(2, todayVal);
            //pstmt.setString(2, brand);


            rs = pstmt.executeQuery();

            while (rs.next()) {
                String DIS01 = rs.getString("DIS01") == null ? "" : rs.getString("DIS01");
                String DIS02 = rs.getString("DIS02") == null ? "" : rs.getString("DIS02");
                String DIS03 = rs.getString("DIS03") == null ? "" : rs.getString("DIS03");
                String DIS04 = rs.getString("DIS04") == null ? "" : rs.getString("DIS04");
                String DIS05 = rs.getString("DIS05") == null ? "" : rs.getString("DIS05");
                String DIS06 = rs.getString("DIS06") == null ? "" : rs.getString("DIS06");
                String DIS07 = rs.getString("DIS07") == null ? "" : rs.getString("DIS07");
                String DIS08 = rs.getString("DIS08") == null ? "" : rs.getString("DIS08");
                String DIS09 = rs.getString("DIS09") == null ? "" : rs.getString("DIS09");
                String DIS10 = rs.getString("DIS10") == null ? "" : rs.getString("DIS10");
                String DIS11 = rs.getString("DIS11") == null ? "" : rs.getString("DIS11");
                String DIS12 = rs.getString("DIS12") == null ? "" : rs.getString("DIS12");
                String TOTAL = rs.getString("TOTAL") == null ? "" : rs.getString("TOTAL");
                String to_cost = rs.getString("to_cost") == null ? "" : rs.getString("to_cost");

                data.put("202601", DIS01);
                data.put("202602", DIS02);
                data.put("202603", DIS03);
                data.put("202604", DIS04);
                data.put("202605", DIS05);
                data.put("202606", DIS06);
                data.put("202607", DIS07);
                data.put("202608", DIS08);
                data.put("202609", DIS09);
                data.put("202610", DIS10);
                data.put("202611", DIS11);
                data.put("202612", DIS12);
                data.put("TOTAL", TOTAL);
                data.put("to_cost", to_cost);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return data;
    }
}
