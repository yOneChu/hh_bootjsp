package com.kyhslam.api.COP;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.util.PLMDBConnection;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * BOM 계산 관련 API 호출
 */
public class BOM_Calculate {

    private static final String BASE_URL = "http://plmpro.hdel.co.kr";
    private static final String SUBAE_MANAGER_URL = BASE_URL + "/SubaeManager.do";

    /** 영업사양 객체 prefix (elv_info$vf@ + ouid) */
    private static final String VF_PREFIX = "elv_info$vf@";

    /** 재시도 횟수 */
    private static final int MAX_RETRY = 3;

    /** 재시도 간 대기 시간(ms) */
    private static final long RETRY_INTERVAL = 3000L;

    /**
     * BOM 계산 (구성전개 c / 자재전개 m / 사양전개 f 를 모두 수행)
     *
     * @param projectNo 프로젝트호기 ouid. "ac45dd18" 처럼 ouid만 넘겨도 되고 "elv_info$vf@ac45dd18" 전체를 넘겨도 된다.
     * @return 응답 json 의 message 값 (실패 시 null)
     */
    public static String bomCalStart(String projectNo) {

        String vfOuid = getProductOuid(projectNo);

        return bomCalStart(vfOuid, "c", "m", "f");
    }

    /**
     * BOM 계산
     * 파이썬:
     *   inputstr = {'cmd':'bomCalStart', 'iOuid':'elv_info$vf@'+ouid.lower()}   # bom 계산시 ouid 는 소문자여야 함
     *   inputstr['b_c'] = 'c'
     *   inputstr['b_m'] = 'm'
     *   inputstr['b_f'] = 'f'
     *   req.post("http://plmpro.hdel.co.kr/SubaeManager.do", data=inputstr)
     *
     * 반드시 PLM_API_Test.login() 으로 세션(JSESSIONID)을 만든 뒤 호출해야 한다.
     * (쿠키는 PLM_API_Test 의 CookieManager 가 CookieHandler 기본값으로 등록되어 자동으로 실려 나간다)
     *
     * @param vfOuid 영업사양 ouid
     * @param bc     b_c 값. 안 쓰면 null (예: "c")
     * @param bm     b_m 값. 안 쓰면 null (예: "m")
     * @param bf     b_f 값. 안 쓰면 null (예: "f")
     * @return 응답 json 의 message 값 (실패 시 null)
     */
    public static String bomCalStart(String vfOuid, String bc, String bm, String bf) {

        //파라미터 변수
        String cmd = "bomCalStart";
        String iOuid = toIOuid(vfOuid);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", cmd);
        data.put("iOuid", iOuid);
        if (bc != null) {
            data.put("b_c", bc);
        }
        if (bm != null) {
            data.put("b_m", bm);
        }
        if (bf != null) {
            data.put("b_f", bf);
        }

        // 계산이 오래 걸려 응답이 끊기는 경우가 있어 실패 시 최대 3회까지 재시도
        for (int i = 1; i <= MAX_RETRY; i++) {

            String result = PLM_API_Test.post(SUBAE_MANAGER_URL, data, BASE_URL + "/");
            String message = getMessage(result);

            System.out.println("[BOM 계산 " + i + "회차] iOuid = " + iOuid + ", message = " + message);

            if (message != null) {
                return message;
            }

            System.out.println("### BOM 계산 응답 파싱 실패. 응답 = " + result);

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
     * "AC45DD18" -> "elv_info$vf@ac45dd18"
     * BOM 계산은 ouid 가 소문자여야 하므로 prefix 를 붙인 뒤 전체를 소문자로 만든다.
     */
    private static String toIOuid(String vfOuid) {

        String ouid = vfOuid.trim();
        if (!ouid.startsWith(VF_PREFIX)) {
            ouid = VF_PREFIX + ouid;
        }
        return ouid.toLowerCase();
    }

    /**
     * 응답 json 에서 message 값 추출.
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

    public static String getProductOuid(String productNo) {

        //SELECT CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) , V.* FROM ELV_INFO$VF V WHERE V.MD$NUMBER = 'N28866L01';
        String ouid = "elv_info$vf@AC443BE1";

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        HashMap<String, String> result = new HashMap<>();

        String resultOid = "";

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                    select CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) AS VFOBJ,
                            V.MD$NUMBER AS HOGI,
                            V.MD$STATUS AS STATUS,
                            V.VF$VERSION AS VERSION
                            --,V.*
                     from ELV_INFO$VF V, ELV_INFO$id A
                     where V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip
                       AND V.md$number = ?
                    """;

            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String VFOBJ = rs.getString("VFOBJ");
                String STATUS = rs.getString("STATUS");
                String HOGI = rs.getString("HOGI");
                String VERSION = rs.getString("VERSION");


                result.put("VFOBJ", VFOBJ);
                result.put("STATUS", STATUS);
                result.put("HOGI", HOGI);
                result.put("VERSION", VERSION);

                resultOid = VFOBJ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return resultOid;
    }


    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();

        // 1) 로그인해서 세션 쿠키(JSESSIONID) 확보
        PLM_API_Test.login("2035570", "gel1375a!");

        //SELECT CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) , V.* FROM ELV_INFO$VF V WHERE V.MD$NUMBER = 'TEST-630211';
        String projectNo = "TEST-630231";

        //elv_info$vf@ac45ee75

        // 2) 같은 세션으로 BOM 계산 호출
        String message = bomCalStart(projectNo);
        System.out.println("BOM 계산 결과 : " + message);

        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);
    }
}
