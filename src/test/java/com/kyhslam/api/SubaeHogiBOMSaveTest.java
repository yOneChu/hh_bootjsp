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
import java.util.List;

@SpringBootTest
public class SubaeHogiBOMSaveTest {

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

        try {
            //String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/getBomByBlockoptList.jsp?proNo=210317L07";

            //제품의 버전별 하위 BOM 저장
            HashMap<String, HashMap<String, ProductDto>> productBOM = new HashMap<>();

            //설계완료일자로 호기 조회
            //List<SubaeHogi> hogiList = subaeHogiService.findSubaeHogiAsCodat("20260101");
            List<SubaeHogi> hogiList = subaeHogiService.findSubaeHogiLikeCodat("202601");
            //List<SubaeHogi> hogiList = subaeHogiService.findSubaeHogi("2026-04-16");

            //호기조회
            for (SubaeHogi subaeHogi : hogiList) {
                String hogi = subaeHogi.getHogi();
                String codat =  subaeHogi.getCodat();
                System.out.println(hogi + " > " + codat);

                String requestUrl = "https://plmpro.hdel.co.kr/jsp/help/getBomByBlockoptList.jsp?proNo=";
                requestUrl += hogi.trim();


                // 5초 대기
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }

                HashMap<String, String> headers = getJson(requestUrl);
                String responseStatusCode = headers.get("responseStatusCode");
                String responseBody = headers.get("responseBody");

                //String jsonResponse = getJson(requestUrl);
                System.out.println("응답 JSON:");
                System.out.println(responseBody);
                System.out.println(responseStatusCode);

                if (responseStatusCode.equals("500")) {
                    continue;
                }

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);


                if (root.isArray()) {
                    for (JsonNode node : root) {
                        String vPartNo = node.path("PART").asText();
                        String vPartName = node.path("PART_DESC").asText();
                        String vHogi = node.path("MD$NUMBER").asText();

                        if (vHogi.length() > 15) {
                            continue;
                        }

                        if (vHogi.startsWith("Q")) {
                            continue;
                        }

                        String vHogiVersion = node.path("VF$VERSION").asText();
                        String key = hogi + "-" + vHogiVersion;

                        String vUCHECK =  node.path("UCHECK").asText();
                        String spec = "";

                        if(vUCHECK == null || "".equals(vUCHECK)){
                            vUCHECK = "";
                        }

                        String CDATE = node.path("CDATE").asText();
                        String QTY = node.path("QTY").asText();

                        String BLOCKNO_NUMBER = node.path("BLOCKNO_NUMBER").asText();
                        String BLOCK_OPT = node.path("BLOCK_OPT").asText();

                        String cmt = "";

                        if (productBOM.containsKey(key)) {

                            HashMap<String, ProductDto> map = productBOM.get(key);
                            ProductDto vDto = map.get(vPartNo);

                            cmt = vDto.getCmt() == null ? "" : vDto.getCmt();
                            spec = vDto.getSpec();

                        } else {
                            //주석 찾기 위한 용도
                            //호기-버전을 key값으로 해당 제품의 BOM 조회하여 저장
                            ArrayList<ProductDto> downBom = ProductCommonUtil.findProductInfo(vHogi, vHogiVersion);
                            HashMap<String, ProductDto> map = new HashMap<>();
                            for (int i = 0; i < downBom.size(); i++) {
                                ProductDto dto = downBom.get(i);
                                String getPartNo = dto.getPartNo();
                                map.put(getPartNo, dto);
                            }
                            productBOM.put(key, map);

                            //HashMap<String, ProductDto> map = productBOM.get(key);
                            ProductDto vDto = map.get(vPartNo);

                            cmt = vDto.getCmt() ==  null ? "" : vDto.getCmt();
                            spec = vDto.getSpec();
                        }

                        //System.out.println("hogi = " + hogi);
                        //System.out.println("hogiVersion = " + vHogiVersion);
                        //System.out.println("partNo = " + vPartNo);
                        //System.out.println("QTY = " + QTY);
                        //System.out.println("cmt = " + cmt);
                        //System.out.println("hogi = " + hogi + "_" + hogiVersion + " >> " + partNo + " >> " + cmt);


                        SubaeHogiBOM b = new  SubaeHogiBOM();
                        b.setHogi(vHogi);
                        b.setHogiVersion(vHogiVersion);
                        b.setPartNo(vPartNo);
                        b.setPartName(vPartName);
                        b.setQty(QTY);
                        b.setBlockNo(BLOCKNO_NUMBER);
                        b.setBlockOpt(BLOCK_OPT);
                        b.setCodate(codat);
                        b.setUcheck(vUCHECK);
                        b.setSpec(spec);
                        b.setCmt(cmt);

                        subaeHogiService.subaeHogiBOMSave(b);
                        //System.out.println("--------");
                    }
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
