package com.kyhslam.controller.lecture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class LectureController {


    @GetMapping("/lec/balanceCal")
    @CrossOrigin
    public String balanceCal(String key) {

        return "thymeleaf/balance/balanceManual";
    }

    @GetMapping("/lec/theory/balance")
    @CrossOrigin
    public String balanceTheory(String key) {

        return "thymeleaf/manual/balance-weight-lecture";
    }
}
