package com.kyhslam.controller;

import com.kyhslam.dto.DashDto;
import com.kyhslam.service.JdbcTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@Slf4j
public class testController {


    private final JdbcTestService jdbcTestService;


    @GetMapping("/jdbcTest")
    @ResponseBody
    public void findTest() {
        log.info("findTest");

        LocalDate now = LocalDate.now();
        String todayVal = now.toString();


        ArrayList<DashDto> list = jdbcTestService.findByAll(todayVal);

        for (DashDto dashDto : list) {
            System.out.println(dashDto.getPart_name());
        }


    }

}
