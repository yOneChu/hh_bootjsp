package com.kyhslam.api.COP;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StopWatch;

import java.util.LinkedHashMap;
import java.util.Map;

public class jongsoksung {

    private static final String BASE_URL = "http://plmpro.hdel.co.kr";
    private static final String OBJECT_URL = BASE_URL + "/Object.do";

    /** 영업사양 객체 prefix (elv_info$vf@ + ouid) */
    private static final String VF_PREFIX = "elv_info$vf@";

    /** 종속사양 산출 액션 ouid */
    private static final String ACTION_OUID_JONGSOKSUNG = "9507f844";

    /** 재시도 횟수 (파이썬 while i < 4 와 동일하게 3회) */
    private static final int MAX_RETRY = 3;

    /** 재시도 간 대기 시간(ms) */
    private static final long RETRY_INTERVAL = 3000L;

    /**
     * 종속사양 산출
     * 파이썬:
     *   inputstr = {'cmd':'executeAction', 'objectOuid':'elv_info$vf@'+ouid, 'actionOuid':'9507f844'}
     *   req.post("http://plmpro.hdel.co.kr/Object.do", data=inputstr)
     *
     * 반드시 PLM_API_Test.login() 으로 세션(JSESSIONID)을 만든 뒤 호출해야 한다.
     * (쿠키는 PLM_API_Test 의 CookieManager 가 CookieHandler 기본값으로 등록되어 자동으로 실려 나간다)
     *
     * @param vfOuid 영업사양 ouid. "ac45dd18" 처럼 ouid만 넘겨도 되고 "elv_info$vf@ac45dd18" 전체를 넘겨도 된다.
     * @return 응답 json 의 message 값 (실패 시 null)
     */
    public static String executeJongsoksung(String vfOuid) {

        //파라미터 변수
        String cmd = "executeAction";
        String objectOuid = vfOuid.startsWith(VF_PREFIX) ? vfOuid : VF_PREFIX + vfOuid;
        String actionOuid = ACTION_OUID_JONGSOKSUNG;

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", cmd);
        data.put("objectOuid", objectOuid);
        data.put("actionOuid", actionOuid);

        // 파이썬과 동일하게 실패 시 최대 3회까지 재시도
        for (int i = 1; i <= MAX_RETRY; i++) {

            String result = PLM_API_Test.post(OBJECT_URL, data, BASE_URL + "/");
            String message = getMessage(result);

            System.out.println("[종속사양 산출 " + i + "회차] objectOuid = " + objectOuid + ", message = " + message);

            if (message != null) {
                return message;
            }

            System.out.println("### 종속사양 산출 응답 파싱 실패. 응답 = " + result);

            if (i < MAX_RETRY) {
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return null;
    }

    /**
     * 응답 json 에서 message 값 추출.
     * 파이썬의 migraion_result.json()['message'] 와 동일.
     *
     * @return message 값. json 이 아니거나 message 가 없으면 null
     */
    private static String getMessage(String result) {

        if (result == null || result.trim().isEmpty()) {
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(result);
            JsonNode message = root.get("message");
            return (message == null || message.isNull()) ? null : message.asText();

        } catch (Exception e) {
            // 세션이 끊기면 json 이 아니라 로그인 화면 html 이 내려온다.
            return null;
        }
    }


    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        // 1) 로그인해서 세션 쿠키(JSESSIONID) 확보
        PLM_API_Test.login("2035570", "gel1375a!");

        //SELECT CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) , V.* FROM ELV_INFO$VF V WHERE V.MD$NUMBER = 'TEST-630211';
        String vfOuid = "ac45ee75";

        //elv_info$vf@ac45ee75

        // 2) wip 생성 (WIP 버전이 아니면 종속사양 산출이 거부된다)
        PLM_API_Test.make_wip(vfOuid);

        // 3) 같은 세션으로 종속사양 산출 호출
        String message = executeJongsoksung(vfOuid);
        System.out.println("종속사양 산출 결과 : " + message);

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }
}
