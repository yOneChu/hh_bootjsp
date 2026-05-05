package com.kyhslam.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.domain.SubaeHogi;
import com.kyhslam.domain.SubaeHogiBOM;
import com.kyhslam.dto.ProductDto;
import com.kyhslam.service.SubaeHogiService;
import com.kyhslam.util.DateUtil;
import com.kyhslam.util.ProductCommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
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

@SpringBootTest
public class SubaeHogiBOMSaveTest_v2 {

    @Autowired
    SubaeHogiService subaeHogiService;

    @Test
    public void printDate() {
        String toDate = DateUtil.getTodayDateNoHyphen();
        System.out.println("toDate = " + toDate);
    }

    @Description("당일 설계완료 호기 BOM 조회하여 저장")
    @Test
    public void saveTest() {
        StopWatch sw = new StopWatch();
        sw.start();

        LocalDate now = LocalDate.now();
        String todayVal = DateUtil.getTodayDate();

        HashSet<String> dupSet = new HashSet<>();

        try {
            //String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/getBomByBlockoptList.jsp?proNo=210317L07";

            //제품의 버전별 하위 BOM 저장
            HashMap<String, HashMap<String, ProductDto>> productBOM = new HashMap<>();

            //2026 01
            //설계완료일자로 호기 조회
            //List<SubaeHogi> hogiList = subaeHogiService.findSubaeHogiAsCodat("20260101");
            List<SubaeHogi> hogiList = subaeHogiService.findSubaeHogiLikeCodat("202604");

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

                    subaeHogiService.subaeHogiBOMSave(b);
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

    public static HashMap<String, String> getJson(String requestUrl) throws Exception {
        HashMap<String, String> headers = new HashMap<>();

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
            //throw new RuntimeException("HTTP 요청 실패. status=" + response.statusCode() + ", body=" + response.body());

        }
        headers.put("responseStatusCode", String.valueOf(response.statusCode()));
        headers.put("responseBody", response.body());
        //return response.body();
        return headers;
    }
}
