package com.kyhslam.util;

public class ChatUtil {


    //비표준사양검토 DB 정의서
    public static String getNonStandardContent() {

        String content = "";

        content = """
                [비표준 사양검토 SQL 생성 가이드 - HDEL_DEFAULT.dutyreview$sf]
                
                기본 테이블: HDEL_DEFAULT.dutyreview$sf D (별칭 D)
                
                [SQL 생성 원칙]
                1. SELECT 문만 작성 (INSERT/UPDATE/DELETE/DROP/ALTER/TRUNCATE/MERGE 금지)
                2. 요청하지 않은 테이블 임의 추가 금지
                3. 코드값은 HDEL_DEFAULT.CODN(컬럼) 함수로 명칭 변환
                4. 요청번호/호기번호/현장명/담당자/작업상태 언급 시 WHERE 조건 반영
                5. 날짜 조건은 표현에 맞는 컬럼 사용
                6. 조건 불명확 시 일반적 기준으로 작성, 필요시 확인 질문
                7. 사용 DB는 ORACLE
                
                [주요 컬럼]
                - 요청기본: MD$NUMBER(요청번호), MD$CDATE(등록일), MD$MDATE(수정일), MD$STATUS(상태: RLS=검토완료, CRT=작성중), REQTIME(의뢰일), DUTYTITLE1(제목), DIVISION(등록부서*), USER1(사용자)
                - 수주/현장: SUJUSTAT(수주상태*), SUJUNUM(호기번호), SUJUVER(계약변경차수), QUOTENUM/QUOTEVER/QUOTESERIAL(견적번호/차수/일련번호), FILEDNAME(현장명), PAY_EST_DATE(납기예정일), NATION(국내/해외*), EL_DKEY(교체공사여부*)
                - 제품/승강로: PRODUCT_TYPE01(기종), PRODUCT_TYPE02(용도*), OVERHEAD, TRAVEL_HT, EL_ACAPA(용량), PIT, EL_ASPD(속도), TOTAL_HT, FLOOR(층수), STOPFLOOR(정지층수)
                - 카/도어: EL_ECWTP(CWT위치), EL_BWCAD(풍음대책), EL_ADRV(운행방식), EL_BWALLT(WALL구조), DOORTYPE(도어열림방식), ECCA/ECCB/ECCH/ECHH/ECJJ, EL_CDFR(방화도어*)
                - 검토요청: WORKSCOPE(작업구분*), REVIEWTITLE(검토요청내용), FIRST_TYPE(대분류*), SECOND_TYPE(중분류*), DETAIL(상세내용), FLOORTYPE, EL_ACD2(적용코드)
                - 상세사양: EL_ECW(CAR자중), EL_ECSF(CAR SAFETY), EL_ERPR(ROPING), EL_DCRG(CAR RGS적용*), EL_AARRT(CAR배열), EL_AEXP(기종파생모델), EL_DDTM(국산TM적용*), EL_ECWAD(추가의장무게), EL_DCAIR(에어컨*), ATTACH_YN(첨부유무*)
                - 회신/작업: MEMO(회신내용), GUBUN(회신구분*), ACPTTIME(작업접수일), FINTIME(완료일), MANAGER(작업담당자), STAT(작업상태*)
                
                (* 표시 컬럼은 코드값 → HDEL_DEFAULT.CODN(D.컬럼)으로 변환하여 조회 권장. 예: HDEL_DEFAULT.CODN(D.STAT) AS 작업상태)
                
                [날짜 컬럼 매핑]
                등록일→MD$CDATE, 수정일→MD$MDATE, 의뢰일→REQTIME, 납기예정일→PAY_EST_DATE, 접수일→ACPTTIME, 완료일→FINTIME
                저장형식이 YYYYMMDD(HH24MISS)면 SUBSTR() 사용, 예: SUBSTR(D.MD$CDATE,1,6)='202606'
                
                [자연어→SQL 해석 규칙]
                - 요청번호 조회 → D.MD$NUMBER = '값' (예: NS-2026-1686)
                - 호기 조회 → D.SUJUNUM = '값'
                - 현장명 조회 → D.FILEDNAME LIKE '%값%'
                - 견적번호 → D.QUOTENUM = '값'
                - 담당자 → D.MANAGER = '값'
                - 작업상태 → HDEL_DEFAULT.CODN(D.STAT) 기준
                - 완료 건 → STAT 또는 D.FINTIME 기준, 미완료 → D.FINTIME IS NULL
                - 연/월별 조회 → SUBSTR(D.MD$CDATE,1,4/6)='값'
                - 집계(작업구분/대분류/중분류/담당자/국내해외/기종/용도)별 → 해당 컬럼(코드컬럼은 CODN 적용) GROUP BY
                
                [기본 SELECT 예시]
                SELECT D.MD$NUMBER AS 요청번호, D.MD$CDATE AS 등록일, D.DUTYTITLE1 AS 제목,
                  D.SUJUNUM AS 호기번호, D.FILEDNAME AS 현장명, D.REVIEWTITLE AS 검토요청내용,
                  D.DETAIL AS 상세내용, D.MEMO AS 회신내용, D.MANAGER AS 작업담당자,
                  HDEL_DEFAULT.CODN(D.STAT) AS 작업상태
                FROM HDEL_DEFAULT.dutyreview$sf D
                WHERE 1=1
                
                [주의사항]
                - MD$NUMBER 예시 형식: NS-2026-1686
                - FILEDNAME(현장명) 검색 시 LIKE 사용 가능
                - 완료 여부는 STAT 또는 FINTIME 기준 판단
                - 대량 조회 우려 시 기간/담당자/상태 조건 병행 권장
                """;
        return content;
    }


    //전산화요청 DB 정의서
    public static String getDesignReqDefinition() {
        String content = "";

        content = """
                [전산화요청(설계요청) SQL 생성 가이드 - HDEL_DEFAULT.NEWPLMDESIGNREQUEST$VF]
                
                기본 테이블: HDEL_DEFAULT.NEWPLMDESIGNREQUEST$VF A (별칭 A)
                사용자 테이블: HDEL_DEFAULT.FUSER$SF U (별칭 U) - 등록자명 조회용, 조인: U.MD$NUMBER = A.MD$USER
                
                [SQL 생성 원칙]
                1. SELECT 문만 작성 (INSERT/UPDATE/DELETE/DROP/ALTER/TRUNCATE 금지)
                2. 요청하지 않은 테이블 임의 추가 금지
                3. 등록자명은 서브쿼리로 FUSER$SF에서 조회
                4. 여부성 컬럼은 HDEL_DEFAULT.COD() 함수 사용
                5. 분류성 코드 컬럼은 HDEL_DEFAULT.CODN() 함수 사용
                6. 날짜 조건은 표현에 맞는 컬럼 사용
                7. 조건 불명확 시 일반적 기준으로 작성, 필요시 확인 질문
                8. 동일 컬럼 중복 조회 금지
                9. 사용 DB는 ORACLE
                
                등록자명 조회 서브쿼리:
                (SELECT U.MD$DESC FROM HDEL_DEFAULT.FUSER$SF U WHERE U.MD$NUMBER = A.MD$USER) AS MUSER
                
                [주요 컬럼]
                - MD$NUMBER(요청번호/REQNO), MD$STATUS(상태), MD$DESC(제목/설명), MD$USER(등록자ID), MUSER(등록자명-서브쿼리)
                - MANAGER(담당자), HOGI(대표호기)
                - PRIORITY(우선순위*), DESIGNPART(전기/기계구분*), REQUESTCAUSE(요청사유*), REQUESTTYPE(작업구분*)
                - REQUESTDETAIL(요청내용), ANSWERDETAIL(작업내용/처리내용)
                - COSTINFLUENCE(원가영향도*), SUBSYSSUPPLYDIV(SubSystem공급구분*)
                - MD$CDATE(생성일시), MD$MDATE(수정일시)
                
                (* = CODN() 함수로 코드명 변환 대상)
                예: HDEL_DEFAULT.CODN(A.PRIORITY) AS 우선순위, HDEL_DEFAULT.CODN(A.REQUESTTYPE) AS 작업구분
                
                [여부성 컬럼 - COD() 함수 사용]
                ISFINISHCERTIFY(인증완료여부), ISHANDLINGSTOCK(재고처리여부), ISUPDATEDUTYTABLE(DUTY_TABLE수정요청여부), ISAPPLYSERIES(시리즈현장적용여부), ISTEAMSHARED(유관팀공유여부), SUBAESUITABILITY1(수배자료적합성_유관부품), SUBAESUITABILITY2(수배자료적합성_수배조건), ISLIMITCONDITION(제한조건작성여부), ISLAYOUTMANAUL(LAYOUT_MANUAL), ISFINISHDCB(DCB완료여부), ISFINISHISIR(ISIR완료여부), ISORDERNDESIGNSITE(기수주설계현장대응여부)
                
                예: HDEL_DEFAULT.COD(A.ISFINISHDCB) AS DCB완료여부
                
                [날짜 처리 규칙]
                - 생성월: SUBSTR(A.MD$CDATE,1,6)
                - 수정월: SUBSTR(A.MD$MDATE,1,6)
                - 날짜조건 비교: SUBSTR(A.MD$CDATE,1,8) > '20260614'
                - 표시용 포맷: DATEFORMAT 미사용, SUBSTR+문자열 결합 방식
                
                [자연어→SQL 해석 규칙]
                - 최근 등록/수정 → ORDER BY MD$CDATE/MD$MDATE DESC
                - 특정 날짜 이후 생성/수정 → SUBSTR(MD$CDATE/MD$MDATE,1,8) > 'YYYYMMDD'
                - 특정 요청번호 → A.MD$NUMBER = '값'
                - 특정 담당자 → A.MANAGER = '값'
                - 특정 대표호기 → A.HOGI = '값'
                - DCB/인증 완료 건 → COD(A.ISFINISHDCB) / COD(A.ISFINISHCERTIFY) 기준
                - 집계(작업구분/월별/담당자별/구분별) → 해당 컬럼(코드컬럼은 CODN/COD 적용) GROUP BY
                
                [기본 SELECT 예시]
                SELECT A.MD$NUMBER AS REQNO, A.MD$STATUS, A.MD$DESC,
                  (SELECT U.MD$DESC FROM HDEL_DEFAULT.FUSER$SF U WHERE U.MD$NUMBER=A.MD$USER) AS MUSER,
                  A.MANAGER, A.HOGI,
                  HDEL_DEFAULT.CODN(A.PRIORITY) AS 우선순위,
                  HDEL_DEFAULT.CODN(A.DESIGNPART) AS 구분,
                  HDEL_DEFAULT.CODN(A.REQUESTTYPE) AS 작업구분,
                  A.REQUESTDETAIL, A.ANSWERDETAIL,
                  HDEL_DEFAULT.COD(A.ISFINISHDCB) AS DCB완료여부,
                  HDEL_DEFAULT.COD(A.ISFINISHISIR) AS ISIR완료여부,
                  HDEL_DEFAULT.COD(A.ISFINISHCERTIFY) AS 인증완료여부
                FROM HDEL_DEFAULT.NEWPLMDESIGNREQUEST$VF A
                WHERE 1=1
                
                [주의사항]
                - COD()/CODN() 실제 반환값은 사내 코드정의에 따라 다르므로, 완료/미완료 등 직접 필터링 시 실제 반환값 확인 필요
                - 원본 정의서에 중복 언급된 여부성 컬럼 있음(ISFINISHCERTIFY, ISHANDLINGSTOCK, ISUPDATEDUTYTABLE, ISAPPLYSERIES, ISTEAMSHARED) - SQL 생성 시 중복 조회 금지
                """;

        return content;
    }
}
