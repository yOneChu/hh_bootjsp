package com.kyhslam.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ViewController {


    @GetMapping("/vault/designMain")
    public String DesignView() {

        return "/vault/designMain";
    }

    @GetMapping("/vault/designView")
    public String DesignView(String fileName) {

        fileName = "C:\\200C0374.iam.dwf";

        return "/vault/designView";
    }

}
