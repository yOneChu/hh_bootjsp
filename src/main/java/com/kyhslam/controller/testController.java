package com.kyhslam.controller;

import com.kyhslam.dto.DashDto;
import com.kyhslam.service.JdbcTestService;
import com.kyhslam.util.PIDCommonUtil;
import com.kyhslam.util.PLMDBConnection;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
