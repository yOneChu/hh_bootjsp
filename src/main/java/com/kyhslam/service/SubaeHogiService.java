package com.kyhslam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.domain.PartPlanC;
import com.kyhslam.domain.PlanCDash;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.SubaeHogiRepository;
import com.kyhslam.util.DateUtil;
import com.kyhslam.util.ProductCommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service("SubaeHogiService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubaeHogiService {
    private final SubaeHogiRepository repository;

    @Transactional
    public void subaeHogiSave(SubaeHogi subae) {
        repository.subaeHogisave(subae);
    }


    @Transactional
    public void subaeHogiBOMSave(SubaeHogiBOM subae) {
        repository.subaeHogiBOMsave(subae);
    }


    public List<SubaeHogi> findSubaeHogi(String batchDate) {
        List<SubaeHogi> list = repository.findSubaeHogi(batchDate);
        return list;
    }

    public List<SubaeHogi> findSubaeHogiAsCodat(String codat) {
        List<SubaeHogi> list = repository.findSubaeHogiAsCodat(codat);
        return list;
    }

    public List<SubaeHogi> findSubaeHogiLikeCodat(String codat) {
        List<SubaeHogi> list = repository.findSubaeHogiLikeCodat(codat);
        return list;
    }

    public List<SubaeHogiBOM> findSubaeBOMAsBlockNo(String blockNo) {
        List<SubaeHogiBOM> list = repository.findSubaeBOMAsBlockNo(blockNo);
        return list;
    }


    /**
     * 금일 최초설계완료 호기 및 BOM 배치 처리
     */
    @Scheduled(cron = "0 30 22 * * *")
    public void subaeHogiBatch() {

        //1. 금일 최초설계완료 호기 저장
        subaeHogiInsertBatch();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        //2. 금일 최초설계완료 호기의 BOM 저장
        subaeHogiBOMInsertBatch();
    }


    //@Scheduled(cron = "0 30 22 * * *")
    public void subaeHogiInsertBatch() {
        StopWatch sw = new StopWatch();
        sw.start();

        String toDate = DateUtil.getTodayDateNoHyphen();
        String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/gethogilistByBlockopt.jsp?searchdate=";
        try {

            requestUrl+= toDate;

            String jsonResponse = getJson(requestUrl);
            System.out.println("응답 JSON:");
            System.out.println(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            //LocalDate now = LocalDate.now();
            String todayVal = DateUtil.getTodayDate();

            if (root.isArray()) {
                for (JsonNode node : root) {
                    String hogiNo = node.path("posid").asText();
                    String codat = node.path("codat").asText();

                    System.out.println("hogiNo = " + hogiNo);
                    System.out.println("codat = " + codat);

                    SubaeHogi s = new  SubaeHogi();
                    s.setHogi(hogiNo);
                    s.setCodat(codat);
                    s.setBatchDate(todayVal);

                    subaeHogiSave(s);

                    System.out.println("--------");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sw.stop();

            long millis = sw.getTotalTimeMillis();

            double seconds = millis / 1000.0;
            double minutes = seconds / 60.0;

            System.out.println("⏱ 수행 시간:");
            System.out.printf("   - %.3f 초%n", seconds);
            System.out.printf("   - %.3f 분%n", minutes);
        }

    }

    public static String getJson(String requestUrl) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP 요청 실패. status=" + response.statusCode()
                    + ", body=" + response.body());
        }

        return response.body();
    }

    //수배BOM 리스트 저장
    public void subaeHogiBOMInsertBatch() {
        StopWatch sw = new StopWatch();
        sw.start();

        LocalDate now = LocalDate.now();
        String todayVal = DateUtil.getTodayDate();
        try {

            //제품의 버전별 하위 BOM 저장
            HashMap<String, HashMap<String, ProductDto>> productBOM = new HashMap<>();

            //2026 01
            //설계완료일자로 호기 조회
            //List<SubaeHogi> hogiList = findSubaeHogiAsCodat("20260101");
            List<SubaeHogi> hogiList = findSubaeHogiAsCodat(todayVal);
            //List<SubaeHogi> hogiList = findSubaeHogiLikeCodat("202603");

            //호기조회
            for (SubaeHogi subaeHogi : hogiList) {
                String hogi = subaeHogi.getHogi();
                String codat =  subaeHogi.getCodat();

                ArrayList<ProductDto> productDtos = ProductCommonUtil.getInitialDesignBom(hogi);

                for(ProductDto dto : productDtos) {
                    String hogiVer = dto.getProductVersion();
                    String hogiName = dto.getProductName();

                    String partNo = dto.getPartNo();
                    String partName = dto.getPartName();
                    String qty = dto.getQty();
                    String blockOpt = dto.getBlockopt();
                    String blockNo = dto.getBlockNo();
                    String cmt = dto.getCmt();
                    String spec = dto.getSpec();
                    String ucheck = dto.getUcheck();

                    SubaeHogiBOM b = new  SubaeHogiBOM();
                    b.setHogi(hogi);
                    b.setHogiVersion(hogiVer);
                    b.setPartNo(partNo);
                    b.setPartName(partName);
                    b.setQty(qty);
                    b.setBlockNo(blockNo);
                    b.setBlockOpt(blockOpt);
                    b.setCodate(codat);
                    b.setUcheck(ucheck);
                    b.setSpec(spec);
                    b.setCmt(cmt);

                    subaeHogiBOMSave(b);
                }

            }

        } catch (Exception e)  {
            e.printStackTrace();
        } finally {
            sw.stop();

            long millis = sw.getTotalTimeMillis();

            double seconds = millis / 1000.0;
            double minutes = seconds / 60.0;

            System.out.println("⏱ 수행 시간:");
            System.out.printf("   - %.3f 초%n", seconds);
            System.out.printf("   - %.3f 분%n", minutes);
        }
    }

    /**
     * 블록번호별 요약 정보를 조회하는 메서드
     * @return 블록번호별 요약 정보 리스트
     */
    public ArrayList<HashMap<String, String>> findSummaryAsBlockNo() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        list = repository.findSummaryAsBlockNo();
        return list;
    }

    /**
     * 요약 정보를 조회하는 메서드-(총 수배건수, 총 수정건수)
     * @return 요약 정보
     */
    public HashMap<String, String> findSummaryAsCount() {
        HashMap<String,String> result = repository.findSummaryAsCount();
        return result;
    }

}
