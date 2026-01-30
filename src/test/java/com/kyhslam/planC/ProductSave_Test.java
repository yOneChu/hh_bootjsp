package com.kyhslam.planC;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.service.PlanCService;
import com.kyhslam.util.PartCommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;


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


    @Description("엑셀 데이터 읽어서 원갈절감 실적 조회 후의 데이터 저장")
    @Test
    public void findCostData() {

        StopWatch sw = new StopWatch();
        sw.start();

        List<PartPlanC> list = service.findAll();

        System.out.println("list = " + list.size());

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        //N27200L19 > C189P001148
        ArrayList costData = new ArrayList();

        String PARTNO = "";
        int findCnt = 0;
        for (int i = 0; i < 20; i++) {
            String vPartNo = list.get(i).getPartNo();

            System.out.println("vPartNo = " + vPartNo);
            //1.호기들 원가절감실적조회로 조회해서 데이터 넣기
            PARTNO += vPartNo + ",";
            findCnt++;

            //100개씩 원가절감실적조회하기 - 속도때문에
            if (findCnt > 5) {
                PARTNO = PARTNO.substring(0, PARTNO.length() - 1);

                //2026.01 이후부터 집계
                costData = service.findSAPIF(PARTNO, "20260101", "20261231");
                //System.out.println("완료 0000000000000000000");

                // 5초 대기
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                //System.out.println("완료 1111111111111111111111111111");

                PARTNO = "";
                findCnt = 0;
            }
        }

        getProductSave(costData, todayValue);

        //남은거 추가로 돌리기
        if(PARTNO != null && !"".equals(PARTNO)){
            PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
            System.out.println("추가로 도는거 PARTNO = " + PARTNO);

            //2026.01 이후부터 집계
            costData = service.findSAPIF(PARTNO, "20260101", "20261231");
            PARTNO = "";
            findCnt = 0;
        }
        getProductSave(costData, todayValue);

        //저장된 데이터에서 호기만 빼서 따로 출하예정일 계산


        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    @Test
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
            String spec = (String) row.get(9); //StringUtil.NVL(row.get(9), "*"); // 스펙

            String createNation = (String) row.get(10); // StringUtil.NVL(row.get(11), "*"); // 생산거점
            String module = (String) row.get(11); // StringUtil.NVL(row.get(11), "*"); // 모듈러
            String mUser = (String) row.get(12); //StringUtil.NVL(row.get(10), "*"); // 기계담당자
            String eUser = (String) row.get(13); // StringUtil.NVL(row.get(11), "*"); // 전기담당자

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

            pData.setAspscd(createNation);
            pData.setMmanager(mUser);
            pData.setEmanager(eUser);
            pData.setModule(module);

            pData.setBatchDate(todayValue); //배치수행일

            //원가실적조회 결과 데이터 저장
            service.productSave(pData);
        }
    }


    @Description("출하예정일 셋팅")
    @Transactional
    //@Commit
    @Test
    public void setExportData() {
        List<ProductPlanC> list = new ArrayList<>();
        list = service.findProductAll();

        HashMap<String, HogiExportDTO> resultMap = new HashMap<>();
        ArrayList<String> data = new ArrayList<>();

        for(int i=0;i<list.size();i++){
            ProductPlanC dto =  list.get(i);
            String hogi = dto.getProductNo();

            data.add(hogi);
        }

        findExportDateV3(data, resultMap);

        for (String s : resultMap.keySet()) {
            HogiExportDTO dto =  resultMap.get(s);
            System.out.println(dto.getHogi() + " > " + dto.getSHIP_A());
        }

        //출하예정일 넣기
        for(int i=0;i<list.size();i++){
            ProductPlanC dto =  list.get(i);
            String hogi = dto.getProductNo();
            String blockNo = dto.getBlockNo();
            blockNo = blockNo.substring(0, 1);

            System.out.println("blockNo = " + blockNo);
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

            //service.productSave(dto);

        }
        //System.out.println("resultMap = " + resultMap);
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
}
