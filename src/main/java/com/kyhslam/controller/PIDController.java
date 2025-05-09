package com.kyhslam.controller;

import com.kyhslam.service.PIDService;
import com.kyhslam.util.PIDCommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PIDController {

    private final PIDService pidService;


    @GetMapping("/pid/batch")
    @ResponseBody
    public void pidProcess() {
        PIDCommonUtil p = new PIDCommonUtil();

        p.insert_Type01();

        p.insert_Type02();

        p.insert_Type03();
    }


    @Description("PID 상세 조회 화면")
    @GetMapping("/pid/searchPIDDetail")
    public String searchPIDDetail() {
        return "subaeLogic/searchPIDDetail";
    }

    /**
     * PID 상세조회
     * @param pid
     * @param FIELD
     * @param GUBUN
     * @param connectGubun
     * @param PID02
     * @param SPEC02
     * @param GUBUN02
     * @return
     */
    @PostMapping("/pid/searchPIDSpecViewJson")
    @ResponseBody
    public ArrayList<HashMap<String, String>> searchPIDDetail(String pid, String FIELD, String GUBUN, String connectGubun
                                , String PID02, String SPEC02, String GUBUN02, String CON05, String PID03, String PID04, String PID05) {

        log.info("searchPIDDetail pid:{}", pid);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        result = PIDCommonUtil.findPIDDetail(pid, FIELD, GUBUN, connectGubun, PID02, SPEC02, GUBUN02, CON05, PID03, PID04, PID05);

        return result;
    }


    @Description("PID 대시보드 화면")
    @GetMapping("/pid/pidDashboard")
    public String pidView() {
        return "dashboard/pidStatusDashboard";
    }

    @Description("PID 작업한 총 라인 수")
    @GetMapping("/pid/findType01")
    @ResponseBody
    public ArrayList<HashMap<String, String>> findType01() {
        //PID 작업한 총 라인 수
        ArrayList<HashMap<String, String>> result = pidService.findType01();
        return result;
    }


    @Description("PID 개수")
    @GetMapping("/pid/findType02")
    @ResponseBody
    public ArrayList<HashMap<String, String>> findType02() {
        //PID 개수
        ArrayList<HashMap<String, String>> result = pidService.findType02();
        return result;
    }


    @Description("PID 별 각 라인 수")
    @GetMapping("/pid/findType03")
    @ResponseBody
    public ArrayList<HashMap<String, String>> findType03() {
        //PID 별 각 라인 수
        ArrayList<HashMap<String, String>> result = pidService.findType03();
        return result;
    }

}
