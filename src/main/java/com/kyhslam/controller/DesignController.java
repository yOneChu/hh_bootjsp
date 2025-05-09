package com.kyhslam.controller;

import com.kyhslam.dto.DesignDashDTO;
import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.service.DesignDashboardService;
import com.kyhslam.util.DesignReqCommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class DesignController {

    //(신)전산요청

    private final DesignDashboardService designDashboardService;


    //데이터 배치
    @GetMapping("/designReq/designBatch")
    public void designBatch() {
        designDashboardService.designReqProcess();
    }

    //대시보드 화면
    @GetMapping("/designReq/dashboard")
    public String designRequestDashboard() {
        return "/dashboard/designRequestDashboard";
    }


    @GetMapping("/findAll")
    @ResponseBody
    public ArrayList<DesignRequestDTO> findAll() {

        ArrayList<DesignRequestDTO> result = DesignReqCommonUtil.findDesignReqs();
        return result;
    }



    @Description("월별 등록 및 완료 건수")
    @GetMapping("/findMonthData")
    @ResponseBody
    public ArrayList<DesignRequestDTO> findMonthData() {

        ArrayList<DesignRequestDTO> result = DesignReqCommonUtil.findDesignReqs();
        return result;
    }


    @GetMapping("/findDate")
    @ResponseBody
    public ArrayList<DesignRequestDTO> findDate(String reqType, String designType) {

        ArrayList<DesignRequestDTO> result = DesignReqCommonUtil.findDesignReqFromType(reqType, designType);
        return result;
    }



    @GetMapping("/findDashboard")
    @ResponseBody
    public ArrayList<DesignDashDTO> findDashboard() {

        ArrayList<DesignDashDTO> result = DesignReqCommonUtil.getDashboard();
        return result;
    }


    //3D Viewer
    @GetMapping("/designReq/viewTest")
    public String viewTest() {
        return "/viewTest/viewTest";
    }
}
