package com.kyhslam.service;

import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.ProductPlanC;
import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.repository.PlanCRepository;
import com.kyhslam.util.DateUtil;
import com.kyhslam.util.SAPCommonUtil;
import com.kyhslam.util.searchListBasedOnCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
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


    public List<PartPlanC> findAll() {
        List<PartPlanC> list = repository.findAll();
        return list;
    }


    public List<ProductPlanC> findProductAll() {
        List<ProductPlanC> list = repository.findProductAll();
        return list;
    }

    /**
     * 매일 PLAN-C 배치 실행
     * 저녁 10시 20분
     */
    @Description("PLAN-C 자재 읽어서(엑셀에 있던 자재) 원갈절감 실적 조회 후의 데이터 저장")
    @Scheduled(cron = "0 20 22 * * *")
    public void findCostData() {
        StopWatch sw = new StopWatch();
        sw.start();


        //오늘날짜 출력 -> YYYYMMDD
        String today_yyyymmdd = DateUtil.getTodayDateNoHyphen();

        //중복 자재 제거
        ArrayList<String> dupCheck = new ArrayList<>();

        //EXCEL에 있는 PLAN C 대상 데이터 조회
        List<PartPlanC> list = findAll();

        System.out.println("list = " + list.size());

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        ArrayList costData = new ArrayList();

        String PARTNO = "";
        int findCnt = 0;
        for (int i = 0; i < 10; i++) {
            String vPartNo = list.get(i).getPartNo();

            //이미 조회한거는 넘어간다.
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
                costData = SAPCommonUtil.findSAPIF(PARTNO, "today_yyyymmdd", "20261231");

                // 5초 대기
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
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
            costData = SAPCommonUtil.findSAPIF(PARTNO, "20260101", "20261231");
            PARTNO = "";
            findCnt = 0;
        }
        getProductSave(costData, todayValue);

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

}
