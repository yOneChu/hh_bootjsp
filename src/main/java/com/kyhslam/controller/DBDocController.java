package com.kyhslam.controller;

import com.kyhslam.util.dbDoc.Doc_SalesBOM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/db")
@RequiredArgsConstructor
@Slf4j
public class DBDocController {

    @GetMapping("/salesBOM")
    @ResponseBody
    @CrossOrigin
    public String getSalesMetaInfo(String key) {

        String result = "";

        if ("subae".equals(key)) {
            result = Doc_SalesBOM.getSaleBOM_Define(); // String 형식
        }
        return result;
    }

}
