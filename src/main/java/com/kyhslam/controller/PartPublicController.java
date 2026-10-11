package com.kyhslam.controller;

import com.kyhslam.dto.PartDashboardDTO;
import com.kyhslam.service.PartDashService;
import com.kyhslam.util.DateUtil;
import com.kyhslam.util.PartDashboardUtil;
import com.kyhslam.util.UtilCommonAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PartPublicController {

    private final PartDashService partDashService;
    //localhost:8070/partPublicList

    ////부품공용화 - 집계목록
    @GetMapping("/public/partPublicList")
    public String partPublicList() {
        return "dashboard/partPublicList";
    }


    //부품공용화 - 수량
    @GetMapping("/public/searchPriceReductionRate")
    public String searchPriceReductionRate() {
        return "partPublic/searchPriceReductionRate";
    }

    //부품공용화 - 대수
    @GetMapping("/public/searchPriceReductionRev")
    public String searchPriceReductionRev() {
        return "partPublic/searchPriceReductionRev";
    }

    //부품공용화 - 출하예정일
    @GetMapping("/public/searchPriceReductionDate")
    public String searchPriceReductionDate(Model model) {
        //return "partPublic/searchPriceReductionDate";
        //return "dashboard/partDashboardv2"; // 예전 PLM자재현황 대시보드

        // 배치는 평일에만 돌기 때문에 주말이면 직전 금요일 집계를 기준일로 삼는다
        LocalDate baseDate = LocalDate.now();
        if (baseDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            baseDate = baseDate.minusDays(1);
        } else if (baseDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            baseDate = baseDate.minusDays(2);
        }
        String baseDateStr = baseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        PartDashboardDTO param = new PartDashboardDTO();
        param.setBatchDate(baseDateStr);
        PartDashboardDTO dto = partDashService.findPartDashboard(param);

        model.addAttribute("baseDate", baseDateStr);
        model.addAttribute("allCnt", dto == null ? null : UtilCommonAPI.formatNumberWithCommas(dto.getPartALL()));
        model.addAttribute("activeCnt", dto == null ? null : UtilCommonAPI.formatNumberWithCommas(dto.getPartActive()));
        model.addAttribute("inactiveCnt", dto == null ? null : UtilCommonAPI.formatNumberWithCommas(dto.getPartInactive()));
        model.addAttribute("olsCnt", dto == null ? null : UtilCommonAPI.formatNumberWithCommas(dto.getOls()));

        //TOP 10 Block (화면에서 상위 10건만 표시)
        model.addAttribute("topList", PartDashboardUtil.findTopBlockPart());

        // 일별 추이 — getLast7BusinessDays() 는 최신일이 앞이므로 화면용으로 오름차순 정렬한다
        List<String> dateKeyList = new ArrayList<>();
        List<String> countList = new ArrayList<>();
        List<String> businessDays = DateUtil.getLast7BusinessDays();
        for (int i = businessDays.size() - 1; i >= 0; i--) {
            String day = businessDays.get(i);
            if (day == null || day.isEmpty()) continue;

            PartDashboardDTO dayParam = new PartDashboardDTO();
            dayParam.setBatchDate(day);
            PartDashboardDTO dayDto = partDashService.findPartDashboard(dayParam);
            if (dayDto != null && dayDto.getPartALL() != null) {
                dateKeyList.add(day);
                countList.add(dayDto.getPartALL());
            }
        }
        model.addAttribute("dateKeyList", dateKeyList);
        model.addAttribute("countList", countList);

        //return "thymeleaf/dashboard/partDashboard";
        return "thymeleaf/dashboard/partDashboardV2";
    }

    //부품공용화 - 금액
    @GetMapping("/public/searchPriceReductionDatePrice")
    public String publicPrice() {
        return "partPublic/searchPriceReductionDatePrice";
    }

}
