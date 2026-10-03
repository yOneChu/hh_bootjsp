package com.kyhslam.service;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PLM(DynaPLM) 원사이클 처리.
 *
 * 영업사양 하나에 대해 [로그인 -> WIP 생성 -> 종속사양 산출 -> BOM 계산] 을 한 번에 수행한다.
 * 각 단계는 public 메서드로 분리되어 있어 개별 호출도 가능하다.
 *
 * 별도 기능 : 동일정보 만들기 (기존 호기의 사양을 그대로 복사해 새 공사정보(TEST 호기) 생성) - {@link #runEqualInfo(String)}
 * 별도 기능 : 호기 속성정보 변경 ({특성코드: 값} 으로 여러 속성을 한 번에 변경) - {@link #runAttrChange(String, Map, boolean)}
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

    /** 공사정보 클래스 ouid (동일정보 생성 시 사용) */
    private static final String CLASS_OUID_ELV_INFO = "860cebeb";

    /** BOM 계산 전개 옵션 (구성전개 c / 자재전개 m / 사양전개 f) */
    public static final String BOM_C = "c";
    public static final String BOM_M = "m";
    public static final String BOM_F = "f";

    /** 종속사양 산출 / BOM 계산 재시도 횟수 */
    private static final int MAX_RETRY = 3;

    /** 재시도 간 대기 시간(ms) */
    private static final long RETRY_INTERVAL = 3000L;

    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 120000;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** PLM 이 json 대신 작은따옴표 dict 표기로 응답할 때가 있어 느슨하게 파싱하는 mapper */
    private static final ObjectMapper LENIENT_MAPPER = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            .enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES)
            .build();

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
     * 원사이클 실행 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> WIP 생성 -> 종속사양 산출 -> BOM 계산
     *
     * @param productNo 프로젝트호기번호 (ELV_INFO$VF.MD$NUMBER)
     */
    public OneCycleResult runOneCycle(String productNo) {
        return runOneCycle(productNo, plmUserId, plmPassword);
    }

    /**
     * 원사이클 실행 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> WIP 생성 -> 종속사양 산출 -> BOM 계산
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
            String jongsoksungMessage = executeJongsoksung(session, vfOuid);
            result.setJongsoksungMessage(jongsoksungMessage);
            if (jongsoksungMessage == null) {
                result.setMessage("종속사양 산출 응답을 확인하지 못했습니다.");
                return result;
            }

            // 5) BOM 계산 (구성전개 c / 자재전개 m / 사양전개 f)
            String bomMessage = bomCalStart(session, vfOuid);
            result.setBomMessage(bomMessage);
            result.setSuccess(bomMessage != null);
            result.setMessage(bomMessage != null ? bomMessage : "BOM 계산 응답을 확인하지 못했습니다.");

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
     * BOM 계산 (구성전개 c / 자재전개 m / 사양전개 f 를 모두 수행)
     *
     * @param vfOuid {@link #toObjectOuid(String)} 로 조회한 영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @return 응답 json 의 message 값. 끝내 실패하면 null
     */
    public String bomCalStart(PlmSession session, String vfOuid) {
        return bomCalStart(session, vfOuid, BOM_C, BOM_M, BOM_F);
    }

    /**
     * BOM 계산 (POST /SubaeManager.do, cmd=bomCalStart)
     *
     * @param vfOuid 영업사양 ouid. "ac45dd18" 처럼 ouid 만 넘겨도 되고 "elv_info$vf@ac45dd18" 전체를 넘겨도 된다.
     * @param bc     b_c 값. 안 쓰면 null (예: "c")
     * @param bm     b_m 값. 안 쓰면 null (예: "m")
     * @param bf     b_f 값. 안 쓰면 null (예: "f")
     * @return 응답 json 의 message 값. 응답이 json 이 아니면(세션 만료 등) 재시도하고, 끝내 실패하면 null
     */
    public String bomCalStart(PlmSession session, String vfOuid, String bc, String bm, String bf) {

        String iOuid = toIOuid(vfOuid);

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "bomCalStart");
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

        // 계산이 오래 걸려 응답이 끊기는 경우가 있어 실패 시 재시도
        for (int i = 1; i <= MAX_RETRY; i++) {

            PlmResponse response = post(session, baseUrl + "/SubaeManager.do", data, baseUrl + "/");
            String message = getMessage(response.getBody());

            System.out.println("[BOM 계산 " + i + "회차] iOuid = " + iOuid + ", message = " + message);

            if (message != null) {
                return message;
            }

            System.out.println("### BOM 계산 응답이 json 이 아닙니다. " + describe(response));

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
    // 별도 기능 : 동일정보 만들기 (TEST 호기 생성)
    // ================================================================

    /**
     * 동일정보 만들기 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> 동일정보 생성 -> 신규 호기번호 조회
     *
     * @param productNo 원본 프로젝트호기번호
     */
    public EqualInfoResult runEqualInfo(String productNo) {
        return runEqualInfo(productNo, plmUserId, plmPassword);
    }

    /**
     * 동일정보 만들기 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> 동일정보 생성 -> 신규 호기번호 조회
     *
     * @param productNo 원본 프로젝트호기번호
     * @param userid    PLM 사용자 ID
     * @param pwd       PLM 비밀번호
     */
    public EqualInfoResult runEqualInfo(String productNo, String userid, String pwd) {

        long startTime = System.currentTimeMillis();
        EqualInfoResult result = new EqualInfoResult();
        result.setProductNo(productNo);

        try {
            // 1) 원본 프로젝트호기번호 -> 영업사양 ouid
            String vfOuid = toObjectOuid(productNo);
            if (vfOuid == null || vfOuid.isBlank()) {
                result.setMessage("프로젝트호기번호에 해당하는 영업사양(WIP)을 찾지 못했습니다. productNo = " + productNo);
                return result;
            }
            result.setObjectOuid(vfOuid);

            // 2) 로그인
            PlmSession session = login(userid, pwd);
            if (session == null) {
                result.setMessage("PLM 로그인 실패. 아이디/비밀번호를 확인하세요.");
                return result;
            }
            result.setLoginSuccess(true);

            // 3) 동일정보 생성 (원본 사양 조회 -> 신규 등록)
            String newOuid = makeEqualInfo(session, vfOuid);
            if (newOuid == null) {
                result.setMessage("동일정보 생성에 실패했습니다.");
                return result;
            }
            result.setNewObjectOuid(newOuid);

            // 4) PLM 이 새로 채번한 호기번호(TEST 번호) 조회
            String newProductNo = "";
            for (int i = 1; i <= MAX_RETRY && newProductNo.isBlank(); i++) {
                newProductNo = findProductNo(newOuid);
                if (newProductNo.isBlank() && i < MAX_RETRY) {
                    sleep(RETRY_INTERVAL);
                }
            }
            result.setNewProductNo(newProductNo);
            result.setSuccess(true);
            result.setMessage(newProductNo.isBlank()
                    ? "동일정보는 생성되었으나 신규 호기번호를 조회하지 못했습니다. newObjectOuid = " + newOuid
                    : "동일정보 생성 완료 : " + productNo + " -> " + newProductNo);

        } catch (Exception e) {
            result.setMessage("동일정보 생성 중 오류 : " + e.getMessage());
            e.printStackTrace();

        } finally {
            result.setElapsedMillis(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    /**
     * 동일정보 생성 : 원본 영업사양의 전체 특성값을 조회해서 새 공사정보로 등록한다.
     *
     * @param vfOuid 원본 영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @return 신규 공사정보 ouid. 실패하면 null
     */
    public String makeEqualInfo(PlmSession session, String vfOuid) {

        // 1) 원본 공사정보의 모든 특성값
        JsonNode sourceInfo = getObjectInfo(session, vfOuid);
        System.out.println("sourceInfo = " + sourceInfo);
        if (sourceInfo == null) {
            return null;
        }

        // 2) 신규 등록용으로 가공
        Map<String, String> data = toRegistData(sourceInfo);

        // 3) 새 공사정보 등록
        String newOuid = registObject(session, data);
        System.out.println("[동일정보 생성] 원본 = " + vfOuid + ", 신규 = " + newOuid);
        return newOuid;
    }

    /**
     * 공사정보의 전체 특성값 조회 (POST /SalesObject.do, cmd=objectInfoAjax)
     *
     * @param vfOuid 영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @return 특성코드 -> 값 json 객체. 응답이 올바르지 않으면 재시도하고, 끝내 실패하면 null
     */
    public JsonNode getObjectInfo(PlmSession session, String vfOuid) {

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "objectInfoAjax");
        data.put("objectOuid", vfOuid);

        for (int i = 1; i <= MAX_RETRY; i++) {

            PlmResponse response = post(session, baseUrl + "/SalesObject.do", data, baseUrl + "/");
            JsonNode info = parseLenient(response.getBody());

            System.out.println("[원본 사양 조회 " + i + "회차] objectOuid = " + vfOuid
                    + ", status = " + response.getStatus()
                    + ", 항목 수 = " + (info == null ? 0 : info.size()));

            if (info != null && info.isObject() && info.size() > 0) {
                return info;
            }

            System.out.println("### 원본 사양 조회 실패. " + describe(response));

            if (i < MAX_RETRY) {
                sleep(RETRY_INTERVAL);
            }
        }

        return null;
    }

    /**
     * 새 공사정보 등록 (POST /SalesObject.do, cmd=registObject, classOuid=860cebeb)
     *
     * 등록이 실제로는 됐는데 응답만 깨진 경우 재시도하면 호기가 중복 생성되므로 재시도하지 않는다.
     *
     * @param data 등록할 특성값 ({@link #getObjectInfo} 결과를 가공한 값). cmd / classOuid 는 여기서 채운다.
     * @return 신규 공사정보 ouid (응답의 iOuid). 실패하면 null
     */
    public String registObject(PlmSession session, Map<String, String> data) {

        Map<String, String> payload = new LinkedHashMap<>(data);
        payload.put("cmd", "registObject");
        payload.put("classOuid", CLASS_OUID_ELV_INFO);

        PlmResponse response = post(session, baseUrl + "/SalesObject.do", payload, baseUrl + "/");
        JsonNode body = parseLenient(response.getBody());

        JsonNode iOuid = body == null ? null : body.get("iOuid");
        if (iOuid == null || iOuid.isNull() || iOuid.asText().isBlank()) {
            System.out.println("### 동일정보 등록 실패. " + describe(response));
            return null;
        }

        return iOuid.asText();
    }

    /**
     * 공사정보 ouid 로 프로젝트호기번호(MD$NUMBER) 를 조회한다.
     *
     * @param vfOuid "elv_info$vf@ac45dd18" 또는 "ac45dd18"
     * @return 프로젝트호기번호. 대상이 없으면 빈 문자열
     */
    public static String findProductNo(String vfOuid) {

        String hex = vfOuid.substring(vfOuid.indexOf('@') + 1).trim().toUpperCase();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String productNo = "";

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                    select V.MD$NUMBER AS HOGI
                      from ELV_INFO$VF V
                     where V.vf$ouid = HEXTODEC(?)
                    """;

            stmt = con.prepareStatement(sql);
            stmt.setString(1, hex);
            rs = stmt.executeQuery();

            if (rs.next()) {
                productNo = rs.getString("HOGI");
            }

            System.out.println("[호기번호 조회] objectOuid = " + vfOuid + ", productNo = " + productNo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return productNo == null ? "" : productNo;
    }


    // ================================================================
    // 별도 기능 : 호기 속성정보 변경
    //   바꿀 속성을 {특성코드: 값} 으로 넘기면 몇 개든 한 번에 변경한다.
    //   자주 쓰는 특성코드
    //     designer       : 담당 설계자 사번 (여러 명이면 List 또는 콤마 구분)
    //     MANAGER_M      : 기계 담당자명
    //     MANAGER_E      : 전기 담당자명
    //     md$user        : 담당자명
    //     md$description : 현장명
    // ================================================================

    /**
     * 속성정보 변경 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> 속성 변경 (-> 반영 확인)
     *
     * @param productNo 프로젝트호기번호
     * @param attrs     바꿀 속성 {특성코드: 값}. 값이 Collection 이면 콤마로 이어 붙인다.
     * @param verify    true 면 변경 후 값을 다시 읽어 반영 여부를 확인한다.
     */
    public AttrChangeResult runAttrChange(String productNo, Map<String, ?> attrs, boolean verify) {
        return runAttrChange(productNo, attrs, verify, plmUserId, plmPassword);
    }

    /**
     * 속성정보 변경 : 프로젝트호기번호 -> 영업사양 ouid 조회 -> 로그인 -> 속성 변경 (-> 반영 확인)
     *
     * @param productNo 프로젝트호기번호
     * @param attrs     바꿀 속성 {특성코드: 값}. 값이 Collection 이면 콤마로 이어 붙인다.
     * @param verify    true 면 변경 후 값을 다시 읽어 반영 여부를 확인한다.
     * @param userid    PLM 사용자 ID
     * @param pwd       PLM 비밀번호
     */
    public AttrChangeResult runAttrChange(String productNo, Map<String, ?> attrs, boolean verify,
                                          String userid, String pwd) {

        long startTime = System.currentTimeMillis();
        AttrChangeResult result = new AttrChangeResult();
        result.setProductNo(productNo);

        try {
            if (attrs == null || attrs.isEmpty()) {
                result.setMessage("변경할 속성이 없습니다.");
                return result;
            }
            result.setRequested(toAttrData(attrs));

            // 1) 프로젝트호기번호 -> 영업사양 ouid
            String vfOuid = toObjectOuid(productNo);
            if (vfOuid == null || vfOuid.isBlank()) {
                result.setMessage("프로젝트호기번호에 해당하는 영업사양(WIP)을 찾지 못했습니다. productNo = " + productNo);
                return result;
            }
            result.setObjectOuid(vfOuid);

            // 2) 로그인
            PlmSession session = login(userid, pwd);
            if (session == null) {
                result.setMessage("PLM 로그인 실패. 아이디/비밀번호를 확인하세요.");
                return result;
            }
            result.setLoginSuccess(true);

            // 3) 속성 변경
            String message = changeAttr(session, vfOuid, attrs);
            if (message == null) {
                result.setMessage("속성정보 변경에 실패했습니다.");
                return result;
            }
            result.setSuccess(true);
            result.setMessage(message);

            // 4) 반영 확인
            if (verify) {
                Map<String, String> current = getAttrValues(session, vfOuid, attrs.keySet());
                result.setCurrent(current);
                result.setVerified(result.getRequested().equals(current));
            }

        } catch (Exception e) {
            result.setMessage("속성정보 변경 중 오류 : " + e.getMessage());
            e.printStackTrace();

        } finally {
            result.setElapsedMillis(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    /**
     * 호기 속성정보 변경 (POST /Object.do, cmd=objectUpdate)
     *
     * @param vfOuid 영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @param attrs  바꿀 속성 {특성코드: 값}. 값이 Collection 이면 콤마로 이어 붙인다.
     *               예) Map.of("designer", "2035570")
     *                   Map.of("designer", List.of("2035570", "2014718"), "MANAGER_E", "오찬석")
     * @return 처리 결과 메시지. 실패(오류 페이지, 세션 만료 등)하면 재시도하고, 끝내 실패하면 null
     */
    public String changeAttr(PlmSession session, String vfOuid, Map<String, ?> attrs) {

        Map<String, String> data = new LinkedHashMap<>();
        data.put("cmd", "objectUpdate");
        data.put("objectOuid", vfOuid);
        data.putAll(toAttrData(attrs));

        for (int i = 1; i <= MAX_RETRY; i++) {

            PlmResponse response = post(session, baseUrl + "/Object.do", data, baseUrl + "/");
            String message = getMessage(response.getBody());

            System.out.println("[속성정보 변경 " + i + "회차] objectOuid = " + vfOuid
                    + ", attrs = " + attrs + ", status = " + response.getStatus() + ", message = " + message);

            if (message != null) {
                return message;
            }

            // 메시지 없이 200 만 주는 경우도 있어, 오류/로그인 화면이 아니면 성공으로 본다.
            if (response.getStatus() == HttpURLConnection.HTTP_OK && !isErrorPage(response)) {
                return "속성정보 변경 완료";
            }

            System.out.println("### 속성정보 변경 실패. " + describe(response));

            if (i < MAX_RETRY) {
                sleep(RETRY_INTERVAL);
            }
        }

        return null;
    }

    /**
     * 담당 설계자(designer) 만 바꾸는 단축 함수.
     *
     * @param designer 사번 문자열 또는 사번 목록 (예 "2035570" / List.of("2035570", "2014718"))
     */
    public String changeDesigner(PlmSession session, String vfOuid, Object designer) {
        return changeAttr(session, vfOuid, Map.of("designer", designer));
    }

    /**
     * 공사정보의 특성값을 조회한다. (속성 변경 후 반영 확인용)
     *
     * @param vfOuid    영업사양 ouid (elv_info$vf@xxxxxxxx)
     * @param codeNames 조회할 특성코드 목록
     * @return 특성코드 -> 현재 값 (값이 없으면 null). 조회 자체가 실패하면 빈 Map
     */
    public Map<String, String> getAttrValues(PlmSession session, String vfOuid, Collection<String> codeNames) {

        Map<String, String> values = new LinkedHashMap<>();

        JsonNode info = getObjectInfo(session, vfOuid);
        if (info == null) {
            return values;
        }

        for (String code : codeNames) {
            JsonNode value = info.get(code);
            String current = (value == null || value.isNull()) ? null
                    : value.isValueNode() ? value.asText() : value.toString();
            values.put(code, current);

            System.out.println("[속성 확인] " + code + " = " + current);
        }

        return values;
    }


    // ================================================================
    // 내부 처리
    // ================================================================

    /**
     * objectInfoAjax 응답을 registObject 전송용 폼 데이터로 가공한다.
     *  - name@ 항목 : 화면 표시용이라 제외
     *  - md$number / ouid : 호기번호는 PLM 이 새로 채번, ouid 는 신규 생성이므로 제외
     *  - 값이 null 인 항목 : 제외
     */
    private Map<String, String> toRegistData(JsonNode info) {

        Map<String, String> data = new LinkedHashMap<>();

        info.fields().forEachRemaining(entry -> {

            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (key.contains("name@") || "md$number".equals(key) || "ouid".equals(key)) {
                return;
            }
            if (value == null || value.isNull()) {
                return;
            }

            data.put(key, value.isValueNode() ? value.asText() : value.toString());
        });

        return data;
    }

    /**
     * 속성값을 폼 전송용 문자열로 바꾼다.
     * designer 처럼 여러 값을 넣는 속성은 Collection 으로 넘기면 콤마로 이어 붙인다.
     */
    private Map<String, String> toAttrData(Map<String, ?> attrs) {

        Map<String, String> data = new LinkedHashMap<>();

        for (Map.Entry<String, ?> entry : attrs.entrySet()) {
            Object value = entry.getValue();
            String text = value instanceof Collection<?> c
                    ? c.stream().map(String::valueOf).collect(Collectors.joining(","))
                    : String.valueOf(value);
            data.put(entry.getKey(), text);
        }

        return data;
    }

    /** PLM 오류 페이지 또는 로그인 화면(세션 만료) 응답인지 */
    private boolean isErrorPage(PlmResponse response) {

        String body = response.getBody() == null ? "" : response.getBody();
        return body.contains("SYSTEM ERROR")
                || body.contains("JsLogin")
                || body.contains("아이디 / 비밀번호를 입력하세요");
    }

    /** json 또는 작은따옴표 dict 표기 응답을 파싱한다. 실패하면 null */
    private JsonNode parseLenient(String body) {

        if (body == null || body.isBlank()) {
            return null;
        }

        try {
            return LENIENT_MAPPER.readTree(body);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * "AC45DD18" -> "elv_info$vf@ac45dd18"
     * BOM 계산은 ouid 가 소문자여야 하므로 prefix 를 붙인 뒤 전체를 소문자로 만든다.
     */
    private String toIOuid(String vfOuid) {

        String ouid = vfOuid.trim();
        if (!ouid.startsWith(VF_PREFIX)) {
            ouid = VF_PREFIX + ouid;
        }
        return ouid.toLowerCase();
    }

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

        // 응답에 charset 이 명시되어 있으면 그 값으로, 없으면 UTF-8 로 읽는다. (동일정보 생성 시 한글 값이 그대로 재전송되므로 중요)
        Charset charset = responseCharset(conn.getContentType());

        StringBuilder body = new StringBuilder();
        if (is != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, charset))) {
                String line;
                while ((line = br.readLine()) != null) {
                    body.append(line);
                }
            }
        }

        System.out.println("[" + apiUrl + "] status = " + status + (location == null ? "" : ", location = " + location));
        return new PlmResponse(status, location, body.toString());
    }

    /** Content-Type 헤더의 charset. 없거나 알 수 없으면 UTF-8 */
    private Charset responseCharset(String contentType) {

        if (contentType != null) {
            for (String part : contentType.split(";")) {
                String p = part.trim();
                if (p.toLowerCase().startsWith("charset=")) {
                    try {
                        return Charset.forName(p.substring(8).replace("\"", "").trim());
                    } catch (Exception ignore) {
                        break;
                    }
                }
            }
        }
        return StandardCharsets.UTF_8;
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

        /** BOM 계산까지 정상 수행 여부 */
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

        /** BOM 계산 결과 메시지 */
        private String bomMessage;

        /** 최종 결과 메시지 */
        private String message;

        /** 수행 시간(ms) */
        private long elapsedMillis;
    }

    /** 동일정보 만들기 결과 */
    @Getter
    @Setter
    @ToString
    public static class EqualInfoResult {

        /** 동일정보 생성 성공 여부 */
        private boolean success;

        /** 로그인 성공 여부 */
        private boolean loginSuccess;

        /** 원본 프로젝트호기번호 */
        private String productNo;

        /** 원본 영업사양 (elv_info$vf@xxxxxxxx) */
        private String objectOuid;

        /** 신규 공사정보 ouid */
        private String newObjectOuid;

        /** 신규 프로젝트호기번호 (TEST 번호) */
        private String newProductNo;

        /** 최종 결과 메시지 */
        private String message;

        /** 수행 시간(ms) */
        private long elapsedMillis;
    }

    /** 속성정보 변경 결과 */
    @Getter
    @Setter
    @ToString
    public static class AttrChangeResult {

        /** 속성정보 변경 성공 여부 */
        private boolean success;

        /** 로그인 성공 여부 */
        private boolean loginSuccess;

        /** 프로젝트호기번호 */
        private String productNo;

        /** 대상 영업사양 (elv_info$vf@xxxxxxxx) */
        private String objectOuid;

        /** 요청한 속성 {특성코드: 값} (폼 전송용 문자열로 변환된 값) */
        private Map<String, String> requested;

        /** 변경 후 다시 읽은 속성 {특성코드: 값} (verify=true 일 때만) */
        private Map<String, String> current;

        /** 요청값과 변경 후 값이 모두 일치하는지 (verify=true 일 때만 의미 있음) */
        private boolean verified;

        /** 최종 결과 메시지 */
        private String message;

        /** 수행 시간(ms) */
        private long elapsedMillis;
    }
}
