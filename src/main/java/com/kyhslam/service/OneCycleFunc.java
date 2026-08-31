package com.kyhslam.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.util.PLMDBConnection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PLM(DynaPLM) 원사이클 처리.
 *
 * 영업사양 하나에 대해 [로그인 -> WIP 생성 -> 종속사양 산출] 을 한 번에 수행한다.
 *
 * 각 호출은 자기만의 세션(JSESSIONID)을 들고 다닌다.
 * (CookieHandler.setDefault 같은 JVM 전역 설정을 쓰지 않으므로 다른 기능의 HTTP 호출에 영향을 주지 않는다)
 */
@Service
public class OneCycleFunc {

    /** 영업사양 객체 prefix (elv_info$vf@ + ouid) */
    public static final String VF_PREFIX = "elv_info$vf@";

    /** 종속사양 산출 액션 ouid */
    private static final String ACTION_OUID_JONGSOKSUNG = "9507f844";

    /** 종속사양 산출 재시도 횟수 */
    private static final int MAX_RETRY = 3;

    /** 재시도 간 대기 시간(ms) */
    private static final long RETRY_INTERVAL = 3000L;

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 120000;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${plm.base-url:http://plmpro.hdel.co.kr}")
    private String baseUrl;

    @Value("${plm.user-id:2035570}")
    private String plmUserId;

    @Value("${plm.password:}")
    private String plmPassword;

    /** 로그인 화면 model 콤보값 (DynaPLM v5 = 80001764, PLM_China = 950695d0) */
    @Value("${plm.model:80001764}")
    private String plmModel;


    // ================================================================
    // 공개 기능
    // ================================================================

    /**
     * 원사이클 실행 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> WIP 생성 -> 종속사양 산출
     *
     * @param productNo 프로젝트호기번호 (ELV_INFO$VF.MD$NUMBER)
     */
    public OneCycleResult runOneCycle(String productNo) {
        return runOneCycle(productNo, plmUserId, plmPassword);
    }

    /**
     * 원사이클 실행 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> WIP 생성 -> 종속사양 산출
     *
     * @param productNo 프로젝트호기번호 (ELV_INFO$VF.MD$NUMBER)
     * @param userid    PLM 사용자 ID
     * @param pwd       PLM 비밀번호
     */
    public OneCycleResult runOneCycle(String productNo, String userid, String pwd) {

        long startTime = System.currentTimeMillis();
        OneCycleResult result = new OneCycleResult();
        result.setProductNo(productNo);

        try {
            // 1) 프로젝트호기번호 -> 영업사양 ouid (elv_info$vf@xxxxxxxx)
            String vfOuid = toObjectOuid(productNo);
            if (vfOuid == null || vfOuid.isBlank()) {
                result.setMessage("프로젝트호기번호에 해당하는 영업사양(WIP)을 찾지 못했습니다. productNo = " + productNo);
                return result;
            }
            result.setObjectOuid(vfOuid);

            // 2) 로그인 (세션 쿠키 확보)
            PlmSession session = login(userid, pwd);
            if (session == null) {
                result.setMessage("PLM 로그인 실패. 아이디/비밀번호를 확인하세요.");
                return result;
            }
            result.setLoginSuccess(true);

            // 3) WIP 생성
            //    이미 WIP 인 경우 등 실패하더라도 종속사양 산출은 시도해본다. (산출 결과 메시지로 원인 판단)
            result.setMakeWipMessage(makeWip(session, vfOuid));

            // 4) 종속사양 산출
            String message = executeJongsoksung(session, vfOuid);
            result.setJongsoksungMessage(message);
            result.setSuccess(message != null);
            result.setMessage(message != null ? message : "종속사양 산출 응답을 확인하지 못했습니다.");

        } catch (Exception e) {
            result.setMessage("원사이클 처리 중 오류 : " + e.getMessage());
            e.printStackTrace();

        } finally {
            result.setElapsedMillis(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    /**
     * 설정(application.properties)의 계정으로 PLM 로그인.
     *
     * @return 로그인 성공 시 세션, 실패 시 null
     */
    public PlmSession login() {
        return login(plmUserId, plmPassword);
    }

    /**
     * PLM 로그인. 브라우저가 JsLogin.jsp 에서 login() 을 눌러 /LogIn.do 로 폼을 전송하는 것과 동일한 요청.
     *
     * @return 로그인 성공 시 세션, 실패 시 null
     */
    public PlmSession login(String userid, String pwd) {

        PlmSession session = new PlmSession();

        // 브라우저와 동일하게 로그인 화면을 먼저 열어 세션 쿠키를 발급받는다.
        get(session, baseUrl + "/jsp/login/JsLogin.jsp", null);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "check");
        data.put("clientType", "WEB");
        data.put("userid", userid);
        data.put("pwd", pwd);
        data.put("locale", "ko");
        data.put("model", plmModel);

        PlmResponse response = post(session, baseUrl + "/LogIn.do", data, baseUrl + "/jsp/login/JsLogin.jsp");

        // 로그인 성공 시 index.jsp 로 리다이렉트되고, 실패하면 로그인 화면이 그대로 다시 내려온다.
        boolean success = response.getStatus() == HttpURLConnection.HTTP_MOVED_TEMP
                && response.getLocation() != null
                && !response.getLocation().contains("JsLogin");

        if (!success) {
            System.out.println("### PLM 로그인 실패. status = " + response.getStatus()
                    + ", location = " + response.getLocation());
            return null;
        }

        System.out.println("[PLM 로그인 성공] " + session);
        return session;
    }

    /**
     * WIP 생성 (POST /SalesObject.do, cmd=makeWip)
     *
     * @param vfOuid {@link #toObjectOuid(String)} 로 조회한 영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @return 처리 결과 메시지 (서버가 json 을 주면 message 값, 오류 페이지를 주면 오류 요약)
     */
    public String makeWip(PlmSession session, String vfOuid) {

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "makeWip");
        data.put("objectOuid", vfOuid);

        PlmResponse response = post(session, baseUrl + "/SalesObject.do", data, baseUrl + "/");
        String message = describe(response);

        System.out.println("[WIP 생성] objectOuid = " + vfOuid + ", message = " + message);
        return message;
    }

    /**
     * 종속사양 산출 (POST /Object.do, cmd=executeAction, actionOuid=9507f844)
     *
     * @param vfOuid {@link #toObjectOuid(String)} 로 조회한 영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @return 응답 json 의 message 값. 응답이 json 이 아니면(세션 만료 등) 재시도하고, 끝내 실패하면 null
     */
    public String executeJongsoksung(PlmSession session, String vfOuid) {

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "executeAction");
        data.put("objectOuid", vfOuid);
        data.put("actionOuid", ACTION_OUID_JONGSOKSUNG);

        for (int i = 1; i <= MAX_RETRY; i++) {

            PlmResponse response = post(session, baseUrl + "/Object.do", data, baseUrl + "/");
            String message = getMessage(response.getBody());

            System.out.println("[종속사양 산출 " + i + "회차] objectOuid = " + vfOuid
                    + ", message = " + message);

            if (message != null) {
                return message;
            }

            System.out.println("### 종속사양 산출 응답이 json 이 아닙니다. " + describe(response));

            if (i < MAX_RETRY) {
                sleep(RETRY_INTERVAL);
            }
        }

        return null;
    }

    /**
     * 프로젝트호기번호(MD$NUMBER) 로 WIP 상태인 영업사양 ouid 를 조회한다.
     *
     * @param productNo 프로젝트호기번호
     * @return "elv_info$vf@ac45dd18" 형태의 영업사양 ouid. 대상이 없으면 빈 문자열
     */
    public static String toObjectOuid(String productNo) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String vfOuid = "";

        try {

            con = PLMDBConnection.getConnection();

            //CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid)))

            String sql = """
                    select CONCAT('%s', LOWER(DECTOHEX(V.vf$ouid))) AS VFOBJ,
                            V.MD$NUMBER AS HOGI,
                            V.MD$STATUS AS STATUS,
                            V.VF$VERSION AS VERSION
                            --,V.*
                     from ELV_INFO$VF V, ELV_INFO$id A
                     where V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip
                       AND V.md$number = ?
                    """.formatted(VF_PREFIX);

            stmt = con.prepareStatement(sql);
            stmt.setString(1, productNo);
            rs = stmt.executeQuery();

            while (rs.next()) {

                vfOuid = rs.getString("VFOBJ");

                System.out.println("[영업사양 조회] productNo = " + productNo
                        + ", objectOuid = " + vfOuid
                        + ", status = " + rs.getString("STATUS")
                        + ", version = " + rs.getString("VERSION"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return vfOuid;
    }





    // ================================================================
    // 내부 처리
    // ================================================================

    /**
     * 응답 json 에서 message 값 추출. (파이썬의 result.json()['message'] 와 동일)
     *
     * @return message 값. json 이 아니거나 message 가 없으면 null
     */
    private String getMessage(String body) {

        if (body == null || body.isBlank()) {
            return null;
        }

        try {
            JsonNode message = MAPPER.readTree(body).get("message");
            return (message == null || message.isNull()) ? null : message.asText();

        } catch (Exception e) {
            // 세션이 끊기거나 서버 오류면 json 이 아니라 html 이 내려온다.
            return null;
        }
    }

    /**
     * 응답을 사람이 읽을 수 있는 한 줄로 요약한다.
     * json 이면 message, PLM 오류 페이지면 ERROR ID 와 예외 메시지, 그 외에는 상태코드.
     */
    private String describe(PlmResponse response) {

        String message = getMessage(response.getBody());
        if (message != null) {
            return message;
        }

        String body = response.getBody() == null ? "" : response.getBody();

        if (body.contains("SYSTEM ERROR")) {
            return "PLM 시스템 오류 (status " + response.getStatus() + ")"
                    + errorDetail(body, "ERROR ID : ", "<br>")
                    + errorDetail(body, "<div id=\"errorMessage\"", "at ");
        }

        if (body.contains("JsLogin") || body.contains("아이디 / 비밀번호를 입력하세요")) {
            return "세션이 만료되었습니다. (로그인 화면 응답)";
        }

        return "응답 확인 필요 (status " + response.getStatus() + ")";
    }

    /** 오류 페이지에서 구간 문자열만 잘라낸다. 못 찾으면 빈 문자열 */
    private String errorDetail(String body, String from, String to) {

        int start = body.indexOf(from);
        if (start < 0) {
            return "";
        }
        start += from.length();

        int end = body.indexOf(to, start);
        if (end < 0) {
            end = Math.min(body.length(), start + 200);
        }

        String detail = body.substring(start, end).replace('>', ' ').trim();
        return detail.isEmpty() ? "" : " / " + detail;
    }

    /** form-urlencoded POST. 세션 쿠키를 실어 보내고 응답의 Set-Cookie 를 세션에 반영한다. */
    private PlmResponse post(PlmSession session, String apiUrl, Map<String, String> data, String referer) {

        HttpURLConnection conn = null;

        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            conn = openConnection(session, apiUrl, "POST", referer);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            }

            return readResponse(session, conn, apiUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return new PlmResponse(-1, null, "");

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** GET (로그인 화면 호출 등) */
    private PlmResponse get(PlmSession session, String apiUrl, String referer) {

        HttpURLConnection conn = null;

        try {
            conn = openConnection(session, apiUrl, "GET", referer);
            return readResponse(session, conn, apiUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return new PlmResponse(-1, null, "");

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private HttpURLConnection openConnection(PlmSession session, String apiUrl, String method, String referer)
            throws Exception {

        HttpURLConnection conn = (HttpURLConnection) URI.create(apiUrl).toURL().openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept", "*/*");
        if (referer != null) {
            conn.setRequestProperty("Referer", referer);
        }

        String cookie = session.getCookieHeader();
        if (!cookie.isEmpty()) {
            conn.setRequestProperty("Cookie", cookie);
        }

        // 로그인 성공 여부를 리다이렉트로 판단하므로 자동 추적하지 않는다.
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        return conn;
    }

    /** 응답 본문 읽기 + Set-Cookie 를 세션에 반영 */
    private PlmResponse readResponse(PlmSession session, HttpURLConnection conn, String apiUrl) throws Exception {

        int status = conn.getResponseCode();
        String location = conn.getHeaderField("Location");

        session.addCookies(conn.getHeaderFields().get("Set-Cookie"));

        InputStream is = (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder body = new StringBuilder();
        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    body.append(line);
                }
            }
        }

        System.out.println("[" + apiUrl + "] status = " + status + (location == null ? "" : ", location = " + location));
        return new PlmResponse(status, location, body.toString());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // ================================================================
    // 내부 클래스
    // ================================================================

    /**
     * PLM 세션. 파이썬 requests.Session() 의 쿠키 저장소 역할.
     * (selenium 으로 로그인한 브라우저 쿠키를 쓰고 싶으면 setCookie 로 직접 넣어도 된다)
     */
    public static class PlmSession {

        private final Map<String, String> cookies = new LinkedHashMap<>();

        public void setCookie(String name, String value) {
            cookies.put(name, value);
        }

        /** 응답의 Set-Cookie 헤더들을 세션에 반영 */
        void addCookies(List<String> setCookieHeaders) {

            if (setCookieHeaders == null) {
                return;
            }

            for (String header : setCookieHeaders) {
                String pair = header.split(";", 2)[0];
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    cookies.put(pair.substring(0, idx).trim(), pair.substring(idx + 1).trim());
                }
            }
        }

        /** 요청에 실을 Cookie 헤더 값 */
        String getCookieHeader() {

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("; ");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return "JSESSIONID=" + cookies.get("JSESSIONID");
        }
    }

    /** HTTP 응답 */
    @Getter
    public static class PlmResponse {

        private final int status;
        private final String location;
        private final String body;

        PlmResponse(int status, String location, String body) {
            this.status = status;
            this.location = location;
            this.body = body;
        }
    }

    /** 원사이클 처리 결과 */
    @Getter
    @Setter
    @ToString
    public static class OneCycleResult {

        /** 종속사양 산출까지 정상 수행 여부 */
        private boolean success;

        /** 로그인 성공 여부 */
        private boolean loginSuccess;

        /** 요청한 프로젝트호기번호 */
        private String productNo;

        /** 프로젝트호기번호로 조회한 대상 영업사양 (elv_info$vf@xxxxxxxx) */
        private String objectOuid;

        /** WIP 생성 결과 메시지 */
        private String makeWipMessage;

        /** 종속사양 산출 결과 메시지 */
        private String jongsoksungMessage;

        /** 최종 결과 메시지 */
        private String message;

        /** 수행 시간(ms) */
        private long elapsedMillis;
    }
}
