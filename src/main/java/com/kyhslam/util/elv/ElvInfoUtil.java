package com.kyhslam.util.elv;

import com.kyhslam.util.PLMDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ElvInfoUtil {


    public static ArrayList<HashMap<String, String>> findElvData(String searchMdNumber) {
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

        System.out.println(" ElvInfoUtil  ::  findElvData = " );

        String query = """
                SELECT 
                    NVL(COD(V.EL_ERPW), '') AS EL_ERPW,
                    NVL(COD(V.EL_CHPB0), '') AS EL_CHPB0, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB1), '') AS EL_CHPB1, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB2), '') AS EL_CHPB2, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB3), '') AS EL_CHPB3, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB4), '') AS EL_CHPB4, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB5), '') AS EL_CHPB5, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB6), '') AS EL_CHPB6, --BUTTON_(CP)
                    NVL(COD(V.EL_CHPB7), '') AS EL_CHPB7, --BUTTON_(CP)
                    NVL(COD(V.EL_DFR), '') AS EL_DFR, --소방 운전
                    NVL(COD(V.EL_ELADT), '') AS EL_ELADT, --사다리구조
                    NVL(COD(V.EL_DEHL), '') AS EL_DEHL, --비상용 승강로사다리
                    NVL(COD(V.EL_BCLCD), '') AS EL_BCLCD, -- LCD;사양
                    NVL(COD(V.EL_BCLCD2), '') AS EL_BCLCD2, -- LCD;사양2
                    NVL(COD(V.EL_BCLCDD), '') AS EL_BCLCDD, -- CAR측표시문자(DISPLAY)
                    NVL(COD(V.EL_BCLCDL), '') AS EL_BCLCDL, -- LCD;취부위치
                    --CODN(EL_BCLCDQ) AS EL_BCLCDQ, -- LCD;수량
                    NVL(COD(V.EL_BCDM), '') AS EL_BCDM, --도어재질
                    NVL(COD(V.EL_BCI), '') AS EL_BCI, --CAGE 인테리어 적용
                    NVL(COD(V.EL_BCPI), '') AS EL_BCPI, -- CPI
                    NVL(COD(V.EL_BCRL), '') AS EL_BCRL, -- WALL LED
                    NVL(COD(V.EL_BCS), '') AS EL_BCS, -- SILL 재질
                    NVL(COD(V.EL_BECM), '') AS EL_BECM, --ENTRANCE COLUMN 재질
                    NVL(COD(V.EL_BETM), '') AS EL_BETM, --TRANSOME 재질/무늬
                    NVL(COD(V.EL_BFLOORS), '') AS EL_BFLOORS, -- FLOOR 종류/공급주체
                    NVL(V.EL_BFSPC, '') AS EL_BFSPC, --FLOOR사양
                    NVL(COD(V.EL_BFTH), '') AS EL_BFTH, --바닥두께
                    NVL(COD(V.EL_BHOPB), '') AS EL_BHOPB, --장애인용OPB
                    NVL(COD(V.EL_BHOPBM), '') AS EL_BHOPBM, --장애자OPB 재질
                    NVL(COD(V.EL_BHOPBQ), '') AS EL_BHOPBQ, --장애자OPB 수량
                    NVL(COD(V.EL_BHR), '') AS EL_BHR, --HANDRAIL
                    NVL(COD(V.EL_BHRP), '') AS EL_BHRP, --HANDRAIL 위치
                    NVL(COD(V.EL_BKPL), '') AS EL_BKPL, --KICK PLATE
                    NVL(COD(V.EL_BMOPB), '') AS EL_BMOPB, --MAIN OPB사양
                    NVL(COD(V.EL_BMOPBM), '') AS EL_BMOPBM, --MAIN OPB 재질
                    NVL(COD(V.EL_BMOPBO), '') AS EL_BMOPBO, --MAIN OPB 열림 방향
                    NVL(COD(V.EL_BSFED), '') AS EL_BSFED, -- SAFETY EDGE
                    NVL(COD(V.EL_BTRM), '') AS EL_BTRM, --TRIM
                    NVL(COD(V.EL_BWALLT), '') AS EL_BWALLT, --WALL 구조
                    NVL(COD(V.EL_CECON), '') AS EL_CECON, --(리모델링) HIP 운행방향/층표기 교차점등
                    NVL(COD(V.EL_CHPBRBC), '') AS EL_CHPBRBC, --홀버튼개구부 막음판 공급
                    NVL(COD(V.EL_CHPIT1), '') AS EL_CHPIT1, -- HPI 사양/재질_(1)
                    NVL(COD(V.EL_DACAPA), '') AS EL_DACAPA, --(교체전)용량
                    NVL(COD(V.EL_DAFQ), '') AS EL_DAFQ, --(교체전)층수
                    NVL(COD(V.EL_DAMAN), '') AS EL_DAMAN, --(교체전)인승
                       V.EL_DAOPEN AS EL_DAOPEN, --(교체전)열림
                       V.EL_DASPD AS EL_DASPD, --(교체전)속도
                       V.EL_DATYP AS EL_DATYP, --(교체전)기종
                       V.EL_DATYP AS EL_DATYP, --(교체전)기종
                       V.EL_DAUSE AS EL_DAUSE, --(교체전)용도
                       COD(V.EL_DCARB) AS EL_DCARB, --(재사용) CAR BUFFER
                       COD(V.EL_DCARIL) AS EL_DCARIL, --(재사용) CAR RAIL
                       COD(V.EL_DCCTV) AS EL_DCCTV, --CCTV 카메라
                       COD(V.EL_DCFQ) AS EL_DCFQ, --	천장팬 2EA 적용
                       COD(V.EL_DCP) AS EL_DCP, --	CAR 의장 전면방음패드
                       COD(V.EL_DCRG) AS EL_DCRG, --	RGS 적용
                       COD(V.EL_DCWFRM) AS EL_DCWFRM, --(재사용) CWT FRAME
                       COD(V.EL_DCWRIL) AS EL_DCWRIL, --(재사용) CWT RAIL
                       COD(V.EL_DCWTB) AS EL_DCWTB, --(재사용) CWT BUFFER
                       COD(V.EL_DELDT) AS EL_DELDT, --	ELD 운전
                       V.EL_DERPR AS EL_DERPR, --	(교체전)ROPING
                       COD(V.EL_DFHCB) AS EL_DFHCB,  -- HANGER CASE BRACKET 현장실측
                       COD(V.EL_DFHSS) AS EL_DFHSS,  -- HATCH SILL SUPPORT 현장실측
                       COD(V.EL_DHIMET) AS EL_DHIMET,  -- HTM/STM/MTC 의장두께 (JAMB제외)
                       COD(V.EL_DHK) AS EL_DHK,  -- 기계실 HOOK
                       COD(V.EL_DIOS) AS EL_DIOS,  -- 교체공사 의장 판금부품 외주 적용
                       COD(V.EL_DJM) AS EL_DJM,  -- (재사용) JAMB (MAIN)
                       COD(V.EL_DJO) AS EL_DJO,  -- 	(재사용) JAMB (OTHER)
                       COD(V.EL_DLATT) AS EL_DLATT,  -- 전력회생형 적용
                       COD(V.EL_DMCB) AS EL_DMCB,  -- (재사용) MACHINE BEAM
                       COD(V.EL_DPFP) AS EL_DPFP,  -- PIT 구간 FASCIA PLATE 적용
                       COD(V.EL_DPK) AS EL_DPK,  -- PARKING KEY
                       COD(V.EL_DPL) AS EL_DPL,  -- PIT사다리 재질
                       COD(V.EL_DPLATET) AS EL_DPLATET,  -- STS304 의장두께
                       COD(V.EL_DRB) AS EL_DRB,  -- ROPE 방음 BOX TYPE
                       COD(V.EL_DREUSE) AS EL_DREUSE,  -- 교체공사
                       COD(V.EL_DSPD) AS EL_DSPD,  -- SPD(20KA)
                       COD(V.EL_DSRC) AS EL_DSRC,  -- ◎ 슬라이딩 레일 클립 적용
                       COD(V.EL_DSPD) AS EL_DSPD,  -- SPD(20KA)
                       COD(V.EL_ECAA) AS EL_ECAA,  -- CAR 외부가로 ; AA
                       COD(V.EL_ECBA) AS EL_ECBA,  -- CWT; BALANCE
                       COD(V.EL_ECBB) AS EL_ECBB,  -- CAR 외부세로 ; BB
                       COD(V.EL_ECBG) AS EL_ECBG,  -- CAR; BG
                       COD(V.EL_ECBUF) AS EL_ECBUF,  -- CAR; BUFFER
                       COD(V.EL_ECCA) AS EL_ECCA,  -- CAR 내부가로 ; CA
                       COD(V.EL_ECCB) AS EL_ECCB,  -- CAR 내부세로 ; CB
                       COD(V.EL_ECCC) AS EL_ECCC,  -- ◎ CAR;CC
                       V.EL_ECCH, --CAR 높이; CH
                       V.EL_ECDOP, --◎ CAR DOOR OPER
                       V.EL_ECEE, --CAR 무게중심;EE
                       COD(V.EL_ECGP) AS EL_ECGP, -- ◎ CAR : GOVERNOR 위치
                       COD(V.EL_ECGV) AS EL_ECGV, --	CAR; GOVERNOR
                       COD(V.EL_ECGSH) AS EL_ECGSH, -- CAR; GUIDE SHOE
                       COD(V.EL_ECHH) AS EL_ECHH, -- 도어높이;HH
                       COD(V.EL_ECHOR) AS EL_ECHOR, -- 	CAR중심 가로
                       COD(V.EL_ECGSH) AS EL_ECGSH, -- CAR; GUIDE SHOE
                       COD(V.EL_ECGSH) AS EL_ECGSH, -- CAR; GUIDE SHOE
                       V.CO_DPEXQ1, --	교환기(1) 대수
                       V.CO_DPEXQ2, --	교환기(2) 대수
                       V.CO_DPEXQ3, --	교환기(3) 대수
                       V.CO_ELQTY, -- EL대수
                       COD(V.CO_LAND1) AS CO_LAND1, --국가코드
                       COD(V.EL_AARRT) AS EL_AARRT, -- CAR배열 형식
                       COD(V.EL_ACD2) AS EL_ACD2, --적용코드
                       V.EL_ADRV, --운행방식
                       V.EL_AEVAUX, --
                       V.EL_AEVFQ,
                       V.EL_AEXP,
                       V.EL_AFF,
                       V.EL_AFFQ,
                       V.EL_AFFT0, EL_AFFT1,EL_AFFT2, EL_AFFT3, EL_AFFT4, EL_AFFT5, EL_AFFT6, EL_AFFT7,
                       V.EL_AFQ, --층수
                       COD(V.EL_AOPEN) AS EL_AOPEN, -- 열림방식
                       COD(V.EL_ARDR) AS EL_ARDR, -- RENDERING
                       CODN(v.EL_AUSE) AS EL_AUSE, -- 용도
                       COD(V.EL_AVOLT) AS EL_AVOLT_동력전원,
                       COD(V.EL_BABD) AS EL_BABD, -- ◎ ABD적용(ANTI-BOUNCING DEVICE)
                       COD(V.EL_ARFQ) AS EL_ARFQ,
                       COD(V.EL_DCRG) AS RGS적용,
                       COD(V.EL_BCL) AS EL_BCL, --천장종류
                       COD(V.EL_BWCAD) AS EL_BWCAD,
                       COD(V.EL_ECWTP), -- CWT_위치
                       V.EL_ECWBUF AS EL_ECWBUF, --	CWT; BUFFER
                       V.EL_ECWBUFBH, --CWT BUFFER BLOCKING 높이
                       V.EL_ECBG, --CAR:BG
                       V.EL_ECJJ, --도어폭;JJ
                       COD(V.EL_ECRL) AS EL_ECRL, -- CAR RAIL(K)
                        COD(V.EL_ECSD) AS EL_ECSD, -- ◎ CAR;SHEAVE DIA
                        COD(V.EL_ECSF) AS EL_ECSF, -- CAR; SAFETY
                        COD(V.EL_ECSL) AS EL_ECSL, --◎ 적용하중 (CAR)
                        COD(V.EL_ECSQ) AS EL_ECSQ, --◎ CAR;SHEAVE QTY
                        COD(V.EL_ECVER) AS EL_ECVER, --	CAR중심 세로
                        COD(V.EL_ECW) AS EL_ECW, --	CAR자중
                        COD(V.EL_ECWBG) AS EL_ECWBG, --	CWT; BG
                        COD(V.EL_ECWGS) AS EL_ECWGS, -- CWT; GUIDE SHOE
                       COD(V.EL_ECWRL) AS EL_ECWRL, --CWT RAIL(K)
                       COD(V.EL_ECWSD) AS EL_ECWSD, --	◎ CWT;SHEAVE DIA.
                       COD(V.EL_ECWSL) AS EL_ECWSL, --	◎ 적용하중 (CWT)
                       COD(V.EL_ECWSQ) AS EL_ECWSQ, --	◎ CWT;SHEAVE QTY
                       COD(V.EL_ECWTP) AS EL_ECWTP, --	◎ CWT : 위치
                       COD(V.EL_ETM) AS EL_ETM, --권상기
                       V.EL_ECWW, --CWT;폭
                       COD(V.EL_ECSF), --CAR; SAFETY
                       COD(V.EL_ASPC), --시방서
                       COD(V.EL_ASPCD), -- 시방서 DEVIATION 여부
                       COD(V.EL_BCL) AS EL_BCL, -- 천장종류
                       V.EL_AMAN AS EL_AMAN, --인승
                       COD(V.EL_ASPSCD) AS EL_ASPSCD, --생산거점(설계)
                       CONCAT('elv_info$vf@', LOWER(DECTOHEX(V.vf$ouid))) OUID,   -- 영업사양 객체
                       CODN(V.EL_ABRAND) AS EL_ABRAND, -- 브랜드
                       CODN(V.EL_ATYP) AS EL_ATYP, -- 기종
                       COD (V.EL_ASPD) AS EL_ASPD, -- 속도
                       CODN (V.EL_ACAPA) AS EL_ACAPA, --용량
                       COD(V.EL_EHDOP) AS EL_EHDOP,-- ◎ HATCH DOOR OPER
                       COD(V.EL_EHH) AS EL_EHH, --	승강로 가로;XX
                       COD(V.EL_EHJH) AS EL_EHJH, --	◎ LADDER; JH
                       COD(V.EL_EHM) AS EL_EHM, --	승강로 재질
                       COD(V.EL_EHO) AS EL_EHO, --	승강로 OVERHEAD
                       COD(V.EL_EHP) AS EL_EHP, --	승강로 PIT
                       COD(V.EL_EHTH) AS EL_EHTH, --	승강로 전장; TOTAL HEIGHT
                       COD(V.EL_EHTRH) AS EL_EHTRH, --주행거리
                       COD(V.EL_EHV) AS EL_EHV, --승강로 세로;YY
                       COD(V.EL_ELADHH) AS EL_ELADHH, --	◎ LADDER; HH
                       COD(V.EL_ELADRD) AS EL_ELADRD, --	◎ LADDER; RD
                       COD(V.EL_EMCBD) AS EL_EMCBD, --	◎ MC BEAM; 방향
                       V.EL_EMCBL AS EL_EMCBL, --	◎ MC BEAM; 길이
                       COD(V.EL_EMFD) AS EL_EMFD, --◎ MC FOUND;방향
                       COD(V.EL_EOPBP) AS EL_EOPBP, --	OPB; 위치
                       COD(V.EL_EPP) AS EL_EPP, --	◎ ROPE;PP(로프거리)
                       COD(V.EL_EPPX) AS EL_EPPX, --	ROPE ; X 가로
                       COD(V.EL_EPPY) AS EL_EPPY, -- ROPE ; Y 가로
                       COD(V.EL_EPSRD) AS EL_EPSRD, --	◎ PIT SCREEN;RD
                       COD(V.EL_ERB) AS EL_ERB, -- ◎ RAIL BRKT;TYPE
                       COD(V.EL_ERBH) AS EL_ERBH, --	RAIL BRACKET 간격
                       V.EL_ERBH1 AS EL_ERBH1, --	◎ RAIL BRKT 거리(H1)
                       V.EL_ERBH2 AS EL_ERBH2, --	◎ RAIL BRKT 거리(H2)
                       V.EL_ERBH3 AS EL_ERBH3, --	◎ RAIL BRKT 거리(H3)
                       COD(V.EL_ERBQ) AS EL_ERBQ, -- RAIL BRACKET 단수
                       COD(V.EL_ERHDH) AS EL_ERHDH, --교체; HATCH DOOR 판넬 높이
                       COD(V.EL_ERPD) AS EL_ERPD, --	ROPE/BELT; 직경
                       COD(V.EL_ERPR) AS EL_ERPR, --	ROPE/BELT; ROPING
                       COD(V.EL_ERPW) AS EL_ERPW, -- ROPE/BELT; 본수
                       COD(V.EL_ESSPRT) AS EL_ESSPRT, --SILL SUPPORT
                       COD(V.EL_ESWAW) AS EL_ESWAW, --	교체 ; SUBWEIGHT 추가 무게(기견적60KG외)
                       COD(V.EL_ETHRUTY) AS EL_ETHRUTY, --	◎ 관통 타입
                       COD(V.EL_ETMINV) AS EL_ETMINV, --	INVERTER용량
                       COD(V.EL_ETMM) AS EL_ETMM, --	MOTOR용량
                       COD(V.EL_EWM1) AS EL_EWM1 --	◎ WALL ; 후면중앙
                    , V.*
                FROM ELV_INFO$VF V, ELV_INFO$ID A 
                  WHERE 
                      V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip 
                      --AND V.MD$NUMBER NOT LIKE 'Q%'
                      --AND V.MD$NUMBER NOT LIKE '%TEST%'
                      AND V.MD$NUMBER = ?
            """;


        try (Connection conn = PLMDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {


            // 5. 쿼리의 '?' 위치에 파라미터 값 바인딩 (첫 번째 '?' 이므로 인덱스 1)
            // 주석 처리된 부분의 조건(LIKE 'Q%')을 볼 때 문자열로 추정되어 setString 사용
            pstmt.setString(1, searchMdNumber);

            // 6. 쿼리 실행 후 ResultSet은 내부에 중첩 try-with-resources로 감싸기
            try (ResultSet rs = pstmt.executeQuery()) {


                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // 🌟 핵심 로직: ResultSet을 순회하며 Map으로 변환
                while (rs.next()) {
                    // 컬럼 순서를 유지하기 위해 LinkedHashMap 사용
                    HashMap<String, String> rowMap = new LinkedHashMap<>();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = rsmd.getColumnLabel(i); // Key: 컬럼명
                        String columnValue = rs.getString(i) == null ? "" : rs.getString(i);       // Value: 데이터

                        // Map에 데이터 적재 (만약 DB 값이 NULL이면 columnValue도 null이 들어감)
                        if(rowMap.containsKey(columnName)) {

                        } else {
                            rowMap.put(columnName, columnValue);
                        }
                    }

                    // 완성된 1줄(Row)의 Map을 List에 추가
                    resultList.add(rowMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
