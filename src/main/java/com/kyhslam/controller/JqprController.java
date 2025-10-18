package com.kyhslam.controller;

import com.kyhslam.domain.JQPR;
import com.kyhslam.dto.JqprDTO;
import com.kyhslam.repository.JqprSearchCond;
import com.kyhslam.service.JQPRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/jqpr")
@Slf4j
public class JqprController {

    private final JQPRService jqprService;

    // 대시보드 화면
    @GetMapping("/dashboard")
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

    @PostMapping("/getSearchFinish")
    @ResponseBody
    public List<JQPR> getSearchFinish(JqprSearchCond condition) {

        log.info("getSearch  ----------------" + condition);
        System.out.println("getSearch  ----------------" + condition);

        List<HashMap<String, String>> result = new ArrayList<>();

        //condition.setState("종결완료");

        List<JQPR> list = jqprService.findAll(condition);
        for (JQPR jqpr : list) {
            //System.out.println(jqpr.getJqprNo());
        }

        //System.out.println(list);
        return list;
        //return result;
    }

    @PostMapping("/getSearch")
    @ResponseBody
    public List<JQPR> getSearch(JqprSearchCond condition) {

        log.info("getSearch  ----------------" + condition);

        List<HashMap<String, String>> result = new ArrayList<>();


        List<JQPR> list = jqprService.findAll(condition);
        for (JQPR jqpr : list) {
            //System.out.println(jqpr.getJqprNo());
        }

        //System.out.println(list);
        return list;
        //return result;
    }
}
