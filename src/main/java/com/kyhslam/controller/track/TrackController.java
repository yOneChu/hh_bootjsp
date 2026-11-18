package com.kyhslam.controller.track;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/track")
public class TrackController {

    /**
     * @apiNote BOM 수정 검증 화면 : 설계자가 수정한 제품의 자재 변경 내역을 제품별로 보고 검증한다 (현재 UI 만)
     */
    @GetMapping("/findModifyBom")
    public String findModifyBomView() {
        //http://localhost:8070/track/findModifyBom
        return "thymeleaf/track/findModifyBom";
    }
}
