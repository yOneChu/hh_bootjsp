package com.kyhslam.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;


@Controller
public class DownloadController {

    @GetMapping("/download/vaultFile")
    @ResponseBody
    public void vaultFile(HttpServletResponse response) throws IOException {

        //String path = "D:\\프로젝트 관련 파일\\DataSheet And Inventor Addins\\HDELAddins_V1.0.0.7.exe";
        //String fileName = "HDELAddins_V1.0.0.7.exe";

        String path = "C:\\김영환\\04_3D_프로젝트\\05_설치파일\\HDELAddins_V1.0.0.6.exe";
        String fileName = "HDELAddins_V1.0.0.7.exe";

        byte[] fileByte = FileUtils.readFileToByteArray(new File(path));

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(fileName, "UTF-8")+"\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        response.getOutputStream().write(fileByte);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @GetMapping("/download/manualView")
    public String menualView(HttpServletResponse response) {
        return "document/menualList";
    }


    //3D 메뉴얼 다운로드 화면
    @GetMapping("/download/manualDashView")
    public String manualDashView(HttpServletResponse response) {
        return "document/manualView";
    }


}
