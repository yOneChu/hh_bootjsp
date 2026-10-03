package com.kyhslam.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.service.OneCycleFunc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class SimulateBlockClient {

    /**
     * BOM 시뮬레이터
     */

    @Autowired
    private OneCycleFunc oneCycleFunc;

    @Test
    public void simulateBlock() {
        Map<String, Object> body = new HashMap<>();
        body.put("productNoList", Arrays.asList("TEST-632563"));
        //body.put("blockList", Arrays.asList("블록번호1", "블록번호2"));
        body.put("blockList", Arrays.asList("E321A"));
        body.put("blockOPTList", new ArrayList<>());   // 비워도 반드시 넣기

        // 0) 로그인 (application.properties 의 plm 계정, DESIGN 권한 사용자)
        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            System.out.println("##### 로그인 실패로 중단");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            byte[] json = mapper.writeValueAsBytes(body);

            URL url = new URL("http://plmpro.hdel.co.kr/plmetc/bom/simulate/block");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cookie", session.getCookieHeader());   // 로그인 세션 쿠키
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json);
            }

            int code = con.getResponseCode();
            InputStream is = code < 400 ? con.getInputStream() : con.getErrorStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                System.out.println(code + " : " + sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
