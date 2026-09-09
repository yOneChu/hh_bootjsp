package com.kyhslam.util.eco;

import com.kyhslam.dto.EcoDTO;
import com.kyhslam.dto.UserDTO;
import com.kyhslam.util.PLMDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class EcoCommonUtil {


    public static ArrayList<EcoDTO> getECOList(String date) {
        ArrayList<EcoDTO> ecoList = new ArrayList<>();

        String query = """
                
                SELECT
                    A.ISTRANSFERED,
                       A.SF$OUID,
                       A.MD$NUMBER AS ECONO, -- ECONO
                       A.MD$STATUS AS STATUS, -- 상태
                       A.MD$DESC AS ECONAME, -- ECO명
                       (SELECT B.USER_NAME FROM V_USER_INFO B WHERE B.USER_ID = A.MD$USER) AS CRE_USER, -- 등록자(한글)
                       --A.EC_CLASS, -- 구분 (제품신규/제품수정/ 등)
                       CODN(A.EC_CLASS) AS GUBUN, --구분 (제품신규/제품수정/ 등)
                       --A.EC_CLASS_DESC,       CODN(A.EC_CLASS_DESC) AS 구분내역_실시시기, -- 구분내역/실시시기
                       A.PROD_MODIFY_ETC AS ETC_CONTENT, -- 기타 변경사유
                       A.EC_REMARK_DESC AS CONTENT, -- 내용및사유
                       CODN(A.VERIFICATION) AS VERIFY, --검증유무
                       CODN(A.LOGIC_EXCEPTION) AS EXCEP_FLAG, --정합성 예외
                       --CODN(A.PHANTOM_EXCEPTION) AS Phantom, --Phantom 하위 자재 검사 예외
                       SUBSTR(A.MD$CDATE, 0 , 8) AS CRE_DATE, -- 생성일
                       SUBSTR(A.APP_DATE, 0 , 8) AS APPROVAL_DATE, -- 승인일
                       DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CRE_DATE,
                       DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS MOD_DATE,
                       DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS APP_DATE
                       --,A.*
                FROM CHANGEORDER$SF A
                WHERE
                SUBSTR(A.APP_DATE, 1, 8) = ?
                
                """;

        try (Connection conn = PLMDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, date);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    String ECONO = rs.getString("ECONO");
                    String STATUS = rs.getString("STATUS");
                    String ECONAME = rs.getString("ECONAME");
                    String CRE_USER = rs.getString("CRE_USER");
                    String GUBUN = rs.getString("GUBUN");
                    String ETC_CONTENT = rs.getString("ETC_CONTENT"); //기타 변경사유
                    String CONTENT = rs.getString("CONTENT"); //내용및사유

                    String VERIFY = rs.getString("VERIFY"); //검증유무
                    String EXCEP_FLAG = rs.getString("EXCEP_FLAG"); //정합성 예외

                    String CRE_DATE = rs.getString("CRE_DATE"); //생성일
                    String APPROVAL_DATE = rs.getString("APPROVAL_DATE"); //승인일

                    EcoDTO dto = new EcoDTO();
                    dto.setEcono(ECONO);
                    dto.setStatus(STATUS);
                    dto.setEcoName(ECONAME);
                    dto.setCreUser(CRE_USER);
                    dto.setGubun(GUBUN); //구분 (제품신규/제품수정/ 등)
                    dto.setEtcContent(ETC_CONTENT); //기타 변경사유
                    dto.setContent(CONTENT); //내용 및 사유
                    dto.setVerify(VERIFY); //검증유무
                    dto.setExcepFlag(EXCEP_FLAG); //정합성유무
                    dto.setCreDate(CRE_DATE);
                    dto.setAppDate(APPROVAL_DATE);

                    ecoList.add(dto);


                   /* --파트연계 -> ECOANDPART$AS
                            --도면연계 -> ECOANDDRAW$AS
                            --2888224354
                    SELECT P.*
                    FROM ECOANDPART$AS P
                    WHERE P.AS$END1 = '2888224354'
                    ;

                    SELECT * FROM PRODUCT$VF WHERE VF$OUID = '2888223713';*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ecoList;
    }

}
