package com.kyhslam.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@Slf4j
public class ViewController {


    @GetMapping("/vault/designMain")
    public String DesignView() {

        return "/vault/designMain";
    }

    @GetMapping("/vault/designView")
    public String DesignView(String fileName) throws IOException {

        //해당 폴더에 파일 있는지 찾기

        /*PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/static/modelA/*");

        System.out.println("📁 static/model 폴더 내 파일 목록:");
        for (Resource resource : resources) {
            System.out.println("- " + resource.getFilename());
        }
*/
        return "/vault/designView";
    }


    //3D뷰어 메인 화면
    @GetMapping("/vault/eduViewMain")
    public String eduViewMain() throws IOException {

        return "/vault/eduMain";
    }

    //3D뷰어 메인 화면
    @GetMapping("/vault/eduView")
    public String eduView(String filename) throws IOException {

        return "/vault/eduView";
    }

    //모듈러 폴더 조회
    @GetMapping("/vault/moduleView")
    public String moduleView() {
        return "thymeleaf/searchModuleView";
    }

}
