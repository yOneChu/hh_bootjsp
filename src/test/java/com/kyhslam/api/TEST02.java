package com.kyhslam.api;

import com.kyhslam.repository.BlockHistoryRepository;
import com.kyhslam.repository.MyBatisBlockRepository;
import com.kyhslam.service.BlockHistoryService;
import com.kyhslam.service.ElevatorInstallationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


@SpringBootTest
public class TEST02 {


    @Autowired
    ElevatorInstallationService service;

    @Test
    public void save() {

        StopWatch sw = new StopWatch();
        sw.start();

        boolean flag = true;

        for (int i = 0; i < 1000; i++) {

            String pageNo = String.valueOf((i+1));
            try {
                StringBuilder urlBuilder = new StringBuilder("http://openapi.elevator.go.kr/openapi/service/ElevatorInstallationService/getInstallationElvtrListV2"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=CWpHb1J7vmgbyEfLaUd0Ns1%2BLf6G7uQR5XDYRcI9muuFVS%2F5y7TaLvX8wdugeqSFlCk6r182ifKtK1ON%2FnitPg%3D%3D"); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8")); /*페이지번호*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("Installation_sdt","UTF-8") + "=" + URLEncoder.encode("20100101", "UTF-8")); /*자료생성일자(시작일)*/
                urlBuilder.append("&" + URLEncoder.encode("Installation_edt","UTF-8") + "=" + URLEncoder.encode("20101231", "UTF-8")); /*자료생성일자(종료일)*/
                //urlBuilder.append("&" + URLEncoder.encode("elevator_no","UTF-8") + "=" + URLEncoder.encode("2118325", "UTF-8")); /*승강기고유번호*/
                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    System.out.println("line = " + line);
                    System.out.println();
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                //System.out.println(sb.toString());

                String xml = sb.toString(); // "/* 질문에 주신 XML 문자열 통째로 붙여넣기 */";

                ArrayList<HashMap<String, String>> result = parseItems(xml);

                System.out.println("item 개수: " + result.size());
                if (!result.isEmpty()) {
                    for(int j = 0; j < result.size(); j++) {
                        HashMap<String, String> map = result.get(j);


                        String companyNm = map.get("companyNm");

                        if (companyNm != null && !"".equals(companyNm) && companyNm.contains("현대")) {
                            System.out.println("map = " + map);
                            service.save(map);
                        }


                    }
                } else {
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);


    }


    public static ArrayList<HashMap<String, String>> parseItems(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // 안전 설정(가능한 경우) - 없어도 동작은 하지만 보안상 권장
        try {
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
        } catch (ParserConfigurationException ignore) { /* JDK에 따라 미지원일 수 있음 */ }

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));

        NodeList itemNodes = doc.getElementsByTagName("item");
        ArrayList<HashMap<String, String>> result = new ArrayList<>(itemNodes.getLength());

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element itemEl = (Element) itemNodes.item(i);
            HashMap<String, String> row = new HashMap<>();

            NodeList children = itemEl.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node n = children.item(j);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    String key = n.getNodeName();
                    String val = n.getTextContent() == null ? "" : n.getTextContent().trim();
                    row.put(key, val);
                }
            }
            result.add(row);
        }
        return result;
    }
}
