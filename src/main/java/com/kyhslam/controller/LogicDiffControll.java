package com.kyhslam.controller;

import com.kyhslam.util.LogicDiffUtil;
import com.kyhslam.util.PIDCommonUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;


@RequiredArgsConstructor
@Slf4j
@Controller
public class LogicDiffControll {


    //클로드코드로 수정 중
    @GetMapping("/diff/logicView")
    public String logicViewDiffV3(HttpServletResponse response) {
        log.info("========== subae logicViewDiffV3.html");
        return "thymeleaf/logicViewDiffV3";
    }

    @Description("PID명으로 전체 버전 조회")
    @GetMapping("/diff/findPIDList")
    @ResponseBody
    public ArrayList<HashMap<String, String>> findPIDList(String pid) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        result = PIDCommonUtil.findPIDList(pid);

        return result;
    }


    /**
     * 과거(pidOid) / 최신(pidOidb) 두 버전을 행 단위로 비교.
     * ADDR 기준으로 행을 매칭하고 셀 단위 변경(EQUAL/MODIFIED/ADDED/DELETED)을 내려준다.
     */
    @PostMapping("/diff/findPIDLineDiff")
    @ResponseBody
    public HashMap<String, Object> findPIDLineDiff(String pid, String pidOid, String pidOidb) {

        log.info("findPIDLineDiff pid={}, before={}, after={}", pid, pidOid, pidOidb);

        // pidOid = 과거(기준) 버전, pidOidb = 최신(비교) 버전
        return LogicDiffUtil.diffVersions(pid, pidOid, pidOidb);
    }

}
