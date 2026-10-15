package com.kyhslam.controller.extract;

import com.kyhslam.service.WongaService;
import com.kyhslam.util.wonga.DrwNumberSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 원가절감 실적조회 화면
 * (원본: jsp/plmetc/product/ListSearchOfCondition/ListOfBasedOnDrwNumber.jsp)
 *
 *   GET  /extract/wonga         : 빈 조회 화면
 *   POST /extract/wonga/search  : 조회 후 같은 화면에 결과 표시
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class WongaController {

    private static final String VIEW = "thymeleaf/wonga/searchWonga";

    private final WongaService wongaService;

    /**
     * 처음 화면 진입
     * 입력칸은 비우고, 추출데이터만 기본 목록으로 채워서 보여준다.
     */
    @GetMapping("/extract/wonga")
    public String searchWonga(Model model) {
        model.addAttribute("form", wongaService.toForm(new HashMap<String, String>()));
        model.addAttribute("defaultSpecList", DrwNumberSearchCondition.DEFAULT_SPEC_LIST);   // 추출데이터 '기본값으로' 버튼
        return VIEW;
    }

    /**
     * 조회 실행
     * 화면 form 입력값을 그대로 받아 서비스로 조회하고,
     * 결과(또는 안내 메시지)와 입력값을 같은 화면에 다시 담아 보여준다.
     */
    @PostMapping("/extract/wonga/search")
    public String search(@RequestParam Map<String, String> form, Model model) {
        log.info("WongaController.search = {}", form);

        model.addAllAttributes(wongaService.search(form));
        model.addAttribute("form", wongaService.toForm(form));
        model.addAttribute("defaultSpecList", DrwNumberSearchCondition.DEFAULT_SPEC_LIST);
        return VIEW;
    }

    /**
     * 조회 URL 을 주소창에서 직접 열거나 새로고침한 경우 빈 화면으로 보낸다.
     */
    @GetMapping("/extract/wonga/search")
    public String searchRedirect() {
        return "redirect:/extract/wonga";
    }
}
