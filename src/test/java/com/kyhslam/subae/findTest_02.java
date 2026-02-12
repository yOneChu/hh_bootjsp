package com.kyhslam.subae;

import com.kyhslam.dto.ProductDto;
import com.kyhslam.repository.mybatis.SubaeMapper;
import com.kyhslam.service.SubaeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class findTest_02 {

    /**
     * BOM 수배율 관련
     */


    @Autowired
    SubaeService subaeService;

    @Autowired
    SubaeMapper  subaeMapper;


    @Test
    void subaeDataTest() {
        StopWatch sw = new StopWatch();
        sw.start();

        //subaeService.subaeTest("207034L13");

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    /**
     * 전체율 계산
     */
    @Description("BOM수배율 데이터 전체 테스트")
    @Test
    void subaeALLTest() {

        StopWatch sw = new StopWatch();
        sw.start();

        subaeService.findSubaeProductNo("");

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    @Description("자재번호로 제품찾기")
    @Test
    void findPartOfProduct_V2() {
        StopWatch sw = new StopWatch();
        sw.start();

        //subaeService.findPartOfProduct("2837255897", "HX");
        //subaeService.findPartOfProduct_v2("", "10311392G12*", "");

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }


    @Description("이미 수배율 계산한 제품번호 조회")
    @Test
    void usedProductNo() {


        ArrayList<String> list = subaeMapper.findUsedProductNo();

        System.out.println("list = " + list.size());

        for (String a : list) {
            System.out.println("a = " + a);
        }
    }

    @Description("계산환 수배율 조회")
    @Test
    void findSubaeProductList() {
        ProductDto param = new ProductDto();
        param.setProductAppdate("2025-07");
        ArrayList<ProductDto> list = subaeService.findSubaeProductList(param);

        for (int i = 0; i < list.size(); i++) {
            ProductDto a = list.get(i);
            System.out.println(a.getProductNo() + " > " + a.getQty() + ">"+ a.getMCount() +"," + a.getMmanager() +">"+ a.getEmanager());
        }
        //ArrayList<HashMap<String, String>> r = subaeService.findSubaeProductList(param);
        //System.out.println("r = " + r);
    }


    @Description("계산환 수배율 조회")
    @Test
    void findNoList() {
        ProductDto param = new ProductDto();
        param.setProductAppdate("2025-07");
        ArrayList<ProductDto> list = subaeService.findSubaePartNoList(param);

        for (int i = 0; i < 1000; i++) {
            ProductDto a = list.get(i);
            System.out.println(a.getProductNo() + " > " + a.getUcheck() + ">"+ a.getBlockopt());
        }
        //ArrayList<HashMap<String, String>> r = subaeService.findSubaeProductList(param);
        //System.out.println("r = " + r);
    }

    @Description("수배율 변경자재 TOP.10")
    @Test
    void topInfo() {
        ArrayList<HashMap<String, String>> r = subaeService.findTopModPartNo();
        System.out.println("r = " + r);
    }
}
