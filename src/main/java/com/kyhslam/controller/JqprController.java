package com.kyhslam.controller;

import com.kyhslam.service.JQPRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class JqprController {

    private final JQPRService jqprService;

    @GetMapping("/jqprDashboard")
    public String jqprDashboard() {
        return "dashboard/jqprDashboard";
    }



    //EXCEL읽어서 넣기
    @GetMapping("/excelWrite")
    @ResponseBody
    public void excelWrite() {

        jqprService.excelWriteProcess();
        log.info("excelWriteProcess END ----------------");
    }
}
