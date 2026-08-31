package com.kyhslam.api.COP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.util.PLMDBConnection;
import org.springframework.util.StopWatch;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PLM_API_Test {

    private static final String BASE_URL = "http://plmpro.hdel.co.kr";
    private static final String LOGIN_PAGE_URL = BASE_URL + "/jsp/login/JsLogin.jsp";   // 로그인 화면 (파이썬 selenium 이 접속하던 페이지)
    private static final String LOGIN_URL = BASE_URL + "/LogIn.do";                     // 로그인 화면의 <form action> 값
    private static final String SALES_OBJECT_URL = BASE_URL + "/SalesObject.do";
    private static final String MODEL_DYNAPLM_V5 = "80001764";                          // 로그인 화면 model 콤보값(셀렉트박스의 value 값) (PLM_China = 950695d0)

    /**
     * 파이썬의 requests.Session() 역할.
     * 로그인 응답의 Set-Cookie(JSESSIONID 등)를 보관했다가 이후 요청에 자동으로 실어 보낸다.
     */
    private static final CookieManager COOKIE_MANAGER = new CookieManager();

    static {
        COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(COOKIE_MANAGER);
    }


    /**
     * 공사정보 wip 생성
     * @param authToken
     * @param userId
     * @param password
     * @return
     */
    public static String getQueryResult(String authToken, String userId, String password) {
        String apiUrl = "https://plmpro.hdel.co.kr/plmetc/plmdb/api/json";
        HttpsURLConnection conn = null;
        BufferedReader br = null;

        try {
            // URL 연결 설정
            URL url = new URL(apiUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("authToken", authToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            // 요청 데이터 (userid, password)
            Map<String, String> data = new HashMap<>();
            data.put("userId", "2035570");
            data.put("password", "gel1375a!");

            // 요청 데이터(JSON 변환 후 전송)
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(data);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonData.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // 응답 읽기
            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                response.append(line);
            }

            // JSON 문자열 그대로 반환 (혹은 Map으로 변환 가능)
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (br != null) try { br.close(); } catch (IOException ignore) {}
            if (conn != null) conn.disconnect();
        }
    }

    // 영업사양 최신 객체 OID
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


    //SELECT CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) , V.* FROM ELV_INFO$VF V WHERE V.MD$NUMBER = 'TEST-630211';

    /**
     * 로그인 (세션 쿠키 획득).
     * 파이썬 selenium 이 JsLogin.jsp 에서 login() 클릭 -> document.login.submit() 하는 것과 동일한 요청.
     * (form action = /LogIn.do, hidden cmd=check, clientType=WEB, 입력값 userid / pwd)
     * 응답의 Set-Cookie(JSESSIONID 등)는 COOKIE_MANAGER 에 자동 저장되어 이후 요청에 실려 나간다.
     *
     * @return 로그인 응답 문자열
     */
    public static String login(String userid, String pwd) {

        // 브라우저와 동일하게 로그인 화면을 먼저 열어 세션 쿠키를 발급받는다.
        get(LOGIN_PAGE_URL, null);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "check");
        data.put("clientType", "WEB");
        data.put("userid", userid);
        data.put("pwd", pwd);
        data.put("locale", "ko");
        data.put("model", MODEL_DYNAPLM_V5);

        String result = post(LOGIN_URL, data, LOGIN_PAGE_URL);
        printCookies();

        // 로그인 실패 시 다시 로그인 화면(JsLogin)이 내려온다.
        if (result != null && result.contains("아이디 / 비밀번호를 입력하세요")) {
            System.out.println("### 로그인 실패: 아이디/비밀번호를 확인하세요.");
        }
        return result;
    }

    /**
     * 파이썬의 driver.get_cookies() -> s.cookies.set(...) 과 동일한 역할.
     * 브라우저(개발자도구 > Application > Cookies)에서 복사한 쿠키 문자열을 세션에 넣는다.
     *
     * @param cookieHeader 예: "JSESSIONID=ABCD1234...; SSO_TOKEN=xxxx"
     */
    public static void setSessionCookies(String cookieHeader) {
        try {
            URI uri = new URI(BASE_URL);
            for (String pair : cookieHeader.split(";")) {
                String token = pair.trim();
                int idx = token.indexOf('=');
                if (idx <= 0) {
                    continue;
                }
                HttpCookie cookie = new HttpCookie(token.substring(0, idx).trim(), token.substring(idx + 1).trim());
                cookie.setDomain(uri.getHost());
                cookie.setPath("/");
                cookie.setVersion(0);
                COOKIE_MANAGER.getCookieStore().add(uri, cookie);
            }
            printCookies();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 현재 세션에 담긴 쿠키 출력 (디버깅용) */
    public static void printCookies() {
        List<HttpCookie> cookies = COOKIE_MANAGER.getCookieStore().getCookies();
        System.out.println("[세션 쿠키] " + cookies);
    }

    /**
     * 영업사양 객체(objectOuid)로 wip 생성.
     * 반드시 login() 또는 setSessionCookies() 로 세션을 만든 뒤 호출해야 한다.
     *
     * @return 응답 문자열
     */
    public static String make_wip() {
        return make_wip("elv_info$vf@ac45dd18");
    }

    /**
     * 영업사양 객체(objectOuid)로 wip 생성.
     *
     * @param vfOuid "ac45dd18" 처럼 ouid만 넘겨도 되고 "elv_info$vf@ac45dd18" 전체를 넘겨도 된다.
     */
    public static String make_wip(String vfOuid) {

        //파라미터 변수
        String cmd = "makeWip";
        String objectOuid = vfOuid.startsWith("elv_info$vf@") ? vfOuid : "elv_info$vf@" + vfOuid;

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", cmd);
        data.put("objectOuid", objectOuid);

        String result = post(SALES_OBJECT_URL, data, BASE_URL + "/");
        System.out.println("[make_wip 응답] " + result);
        return result;
    }

    /**
     * form-urlencoded POST 공통 처리. 세션 쿠키는 CookieHandler(COOKIE_MANAGER)가 자동으로 붙인다.
     *
     * @param apiUrl  호출 URL
     * @param data    요청 파라미터
     * @param referer Referer 헤더 (세션 검증하는 서버 대응)
     * @return 응답 문자열
     */
    public static String post(String apiUrl, Map<String, String> data, String referer) {

        HttpURLConnection conn = null;

        try {
            // 요청 데이터 (form-urlencoded)
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            String inputstr = sb.toString();

            // URL 연결 설정
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "*/*");
            if (referer != null) {
                conn.setRequestProperty("Referer", referer);
            }
            conn.setInstanceFollowRedirects(false);   // 로그인 페이지로 리다이렉트되면 바로 확인 가능
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = inputstr.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            return readResponse(conn, apiUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * GET 공통 처리 (로그인 화면 호출 등). 세션 쿠키는 CookieHandler 가 자동으로 처리한다.
     */
    public static String get(String apiUrl, String referer) {

        HttpURLConnection conn = null;

        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "*/*");
            if (referer != null) {
                conn.setRequestProperty("Referer", referer);
            }
            conn.setInstanceFollowRedirects(false);

            return readResponse(conn, apiUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** 응답 본문 읽기 */
    private static String readResponse(HttpURLConnection conn, String apiUrl) throws IOException {

        int responseCode = conn.getResponseCode();
        System.out.println("[" + apiUrl + "] responseCode = " + responseCode
                + ", Location = " + conn.getHeaderField("Location"));

        InputStream is = (responseCode >= 200 && responseCode < 400)
                ? conn.getInputStream()
                : conn.getErrorStream();

        if (is == null) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }



    public static void main(String[] args) {

        StopWatch sw = new StopWatch();
        sw.start();


        String authToken = "YOUR_AUTH_TOKEN";
        String userId = "testuser";
        String password = "testpass";

        //String result = getQueryResult(authToken, userId, password);
        //System.out.println("API Result: " + result);

        // 1) 로그인해서 세션 쿠키(JSESSIONID) 확보 - selenium 로그인 + requests.Session() 역할
        login("2035570", "gel1375a!");

        // 1-1) 필요하면 브라우저에서 로그인 후 개발자도구의 쿠키를 직접 주입할 수도 있다.
        // setSessionCookies("JSESSIONID=여기에_브라우저_쿠키값");

        // 2) 같은 세션으로 wip 생성 호출
        make_wip();


        sw.stop();

        long millis = sw.getTotalTimeMillis();

        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;

        System.out.println("⏱ 수행 시간:");
        System.out.printf("   - %.3f 초%n", seconds);
        System.out.printf("   - %.3f 분%n", minutes);

    }



}
