package com.kyhslam.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/test")
    public String test() {
        System.out.println("tttt");
        return "viewTest/viewTest";
    }



}
