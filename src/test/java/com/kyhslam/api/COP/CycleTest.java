package com.kyhslam.api.COP;

import com.kyhslam.service.OneCycleFunc;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CycleTest {

    @Autowired
    private OneCycleFunc oneCycleFunc;

    @Test
    public void testCycle() {

        oneCycleFunc.runOneCycle("TEST-630231");


    }

    /** 동일정보 생성 -> WIP 생성 -> 종속사양 산출 -> BOM 계산 */
    @Test
    public void testEqualInfoAndCycle() {

        OneCycleFunc.EqualInfoResult equal = oneCycleFunc.runEqualInfo("TEST-632558");
        System.out.println("##### 동일정보 결과 : " + equal);


        if (!equal.isSuccess() || equal.getNewProductNo() == null || equal.getNewProductNo().isBlank()) {
            System.out.println("##### 동일정보 생성 실패로 사이클 중단");
            return;
        }


        OneCycleFunc.OneCycleResult cycle = oneCycleFunc.runOneCycle(equal.getNewProductNo());
        System.out.println("##### 원사이클 결과 : " + cycle);
    }


    @Description("동일정보 생성 -> WIP 생성 -> 속성값 변경 -> 종속사양 산출 -> BOM 계산")
    @Test
    public void changeAttribute() {

        // 원본 프로젝트호기번호
        String sourceNo = "TEST-632560";

        // 변경할 속성 {특성코드: 값}. 여러 값은 List 로 넣으면 콤마로 이어 붙여 전송된다.
        Map<String, Object> attrs = new LinkedHashMap<>();
        // attrs.put("designer", List.of("2035570", "2014718"));
        attrs.put("EL_ZFDA", "20261003");    // 기계구조 최초설계
        attrs.put("EL_ZFDB", "20261003");    // 기계의장 최초설계
        attrs.put("EL_ZFDC", "20261003");    // 전기구조 최초설계
        attrs.put("EL_ZFDD", "20261003");    // 전기_의장 최초 설계
        // attrs.put("MANAGER_E", "오찬석");


        // 0) 로그인 (이후 모든 단계가 같은 세션을 사용)
        OneCycleFunc.PlmSession session = oneCycleFunc.login();
        if (session == null) {
            System.out.println("##### 로그인 실패로 중단");
            return;
        }

        String sourceOuid = OneCycleFunc.toObjectOuid(sourceNo);

        // 1) wip 생성
        oneCycleFunc.makeWip(session, sourceOuid);


        // 1) 동일정보 생성
        String newOuid = oneCycleFunc.makeEqualInfo(session, sourceOuid);
        /*if (newOuid == null) {
            System.out.println("##### 동일정보 생성 실패로 중단");
            return;
        }*/

        String newNo = OneCycleFunc.findProductNo(newOuid);
        System.out.println("##### 1) 동일정보 생성 : " + sourceNo + " -> " + newNo + " (" + newOuid + ")");

        // 2) WIP 생성 (동일정보로 만든 호기는 이미 WIP 라 PLM 시스템 오류가 날 수 있으나 다음 단계는 진행된다)
        //String wipMessage = oneCycleFunc.makeWip(session, newOuid);
        //System.out.println("##### 2) WIP 생성 : " + wipMessage);

        // 3) 속성값 변경
        if (attrs.isEmpty()) {
            System.out.println("##### 3) 속성값 변경 : 변경할 속성이 없어 건너뜀");
        } else {
            String attrMessage = oneCycleFunc.changeAttr(session, newOuid, attrs);
            System.out.println("##### 3) 속성값 변경 : " + attrMessage);
            System.out.println("##### 3) 변경 후 값 : " + oneCycleFunc.getAttrValues(session, newOuid, attrs.keySet()));
            /*if (attrMessage == null) {
                System.out.println("##### 속성값 변경 실패로 중단");
                return;
            }*/
        }

        // 4) 종속사양 산출
        String jongsoksungMessage = oneCycleFunc.executeJongsoksung(session, newOuid);
        System.out.println("##### 4) 종속사양 산출 : " + jongsoksungMessage);
        if (jongsoksungMessage == null) {
            //System.out.println("##### 종속사양 산출 실패로 중단");
            //return;
        }

        // 5) BOM 계산
        String bomMessage = oneCycleFunc.bomCalStart(session, newOuid);
        System.out.println("##### 5) BOM 계산 : " + bomMessage);


        System.out.println(" ----------- end ------------");
    }

}
