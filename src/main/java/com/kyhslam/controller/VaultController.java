package com.kyhslam.controller;

import com.kyhslam.util.ModulerTeam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class VaultController {


    @GetMapping("/vault/searchVaultData")
    public String data() {

        return"vault/searchVaultData";
    }

    //모듈러 폴더의 파일 조회
    @PostMapping("/vault/findModuleFolder")
    @ResponseBody
    public ArrayList<HashMap<String, String>> findModuleFolder(String fileName, String filePath) {
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        result = ModulerTeam.findFolderList(filePath, fileName);

        return result;
    }

    //모듈러 폴더 조회
    /*@GetMapping("/vault/moduleView")
    public String moduleView() {
        return "thymeleaf/searchModuleView";
    }*/

    //모듈러 폴더 조회 화면
    @GetMapping("/vault/moduleDesignView")
    public String moduleDesignView() {
        return "thymeleaf/moduleView";
    }
}
