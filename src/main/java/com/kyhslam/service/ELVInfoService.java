package com.kyhslam.service;

import com.kyhslam.domain.ELVInfo;
import com.kyhslam.domain.ELVInfoDash;
import com.kyhslam.repository.ElvInfoRepository;
import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.VaultDBConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ELVInfoService {

    private final ElvInfoRepository elvInfoRepository;


    //각 월 영업사양 정보 넣기
    public void findELVInfoData(String month) {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            String id = "hdel_default";
            String pass = "hdel_default";

            //con = DriverManager.getConnection(url, id, pass);
            con = PLMDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();

            sql.append(" SELECT   "); // 요청번호
            sql.append(" A.MD$NUMBER AS hogi ,  A.VF$VERSION as version, A.MD$DESC as sujuName, "); //
            sql.append(" A.MD$CDATE AS creDate,   ");
            sql.append(" A.MD$MDATE AS modDate,   ");
            sql.append(" SUBSTR(A.MD$MDATE, 0, 6) AS modMonth,   ");
            sql.append(" A.MD$STATUS as status  ");

            sql.append(" A.ARKTX AS ARKTX, A.BDUSE AS BDUSE,  ");
            sql.append(" CODN(A.EL_ABRAND) as ABRAND   "); // 브랜드
            sql.append(" COD(A.EL_ATYP) AS ATYP),   ");
            sql.append(" CODN(A.EL_ASPD) AS ASPD,   ");
            sql.append(" COD(A.EL_AUSE) AS AUSE,   ");
            sql.append(" COD(A.EL_ACAPA) AS ACAPA,   "); //용량

            sql.append(" CODN(A.EL_ADRV) AS ADRV, "); // --운행방식
            sql.append(" COD(A.EL_AEXP) AS AEXP, ");  // 기종파생모델
            sql.append(" A.EL_AFQ, "); //층수
            sql.append(" A.EL_AMAN, "); //인승
            sql.append(" COD(A.EL_AOPEN) AS AOPEN, ");  // --열림 (1SCO..)
            sql.append(" COD(A.EL_BCDM) AS BCDM, "); //DOOR재질
            sql.append(" COD(A.EL_BCL) AS BCL, "); //천장종류
            sql.append(" COD(A.EL_BCS) AS BCS, "); //SILL재질
            sql.append(" A.EL_BETC AS BETC, "); //TRANSOM 색상/무늬
            sql.append(" COD(A.EL_BETM) AS BETM, "); // --TRANSOM 재질/무늬
            sql.append(" COD(A.EL_BFLOORS) AS BFLOORS "); //FLOOR종류/공급주제

            sql.append(" FROM ELV_INFO$VF A   ");
            sql.append(" WHERE A.MD$STATUS = 'RLS'  ");
            sql.append(" AND SUBSTR(A.MD$MDATE, 0, 6) = ?   ");

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, month);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                String hogi = rs.getString("hogi") == null ? "" : rs.getString("hogi");
                String version = rs.getString("version") == null ? "" : rs.getString("version");
                String sujuName = rs.getString("sujuName") == null ? "" : rs.getString("sujuName");
                String creDate = rs.getString("creDate") == null ? "" : rs.getString("creDate");
                String modDate = rs.getString("modDate") == null ? "" : rs.getString("modDate");
                String modMonth = rs.getString("modMonth") == null ? "" : rs.getString("modMonth");
                String status = rs.getString("status") == null ? "" : rs.getString("status");

                String ARKTX = rs.getString("ARKTX") == null ? "" : rs.getString("ARKTX");
                String BDUSE = rs.getString("BDUSE") == null ? "" : rs.getString("BDUSE");
                String ABRAND = rs.getString("ABRAND") == null ? "" : rs.getString("ABRAND");
                String ATYP  = rs.getString("ATYP") == null ? "" : rs.getString("ATYP");
                String ASPD  = rs.getString("ASPD") == null ? "" : rs.getString("ASPD");
                String AUSE  = rs.getString("AUSE") == null ? "" : rs.getString("AUSE");
                String ACAPA = rs.getString("ACAPA") == null ? "" : rs.getString("ACAPA");
                String ADRV  = rs.getString("ADRV") == null ? "" : rs.getString("ADRV");
                String AEXP  = rs.getString("AEXP") == null ? "" : rs.getString("AEXP");

                String AFQ   = rs.getString("AFQ") == null ? "" : rs.getString("AFQ");
                String AMAN  = rs.getString("AMAN") == null ? "" : rs.getString("AMAN");
                String AOPEN = rs.getString("AOPEN") == null ? "" : rs.getString("AOPEN");
                String BCDM  = rs.getString("BCDM") == null ? "" : rs.getString("BCDM");
                String BCL   = rs.getString("BCL") == null ? "" : rs.getString("BCL");
                String BCS   = rs.getString("BCS") == null ? "" : rs.getString("BCS");

                String BETC   = rs.getString("BETC") == null ? "" : rs.getString("BETC");
                String BETM   = rs.getString("BETM") == null ? "" : rs.getString("BETM");
                String BFLOORS   = rs.getString("BFLOORS") == null ? "" : rs.getString("BFLOORS");

                ELVInfo data = new ELVInfo();
                data.setHogi(hogi);
                data.setVersion(version);
                data.setSujuName(sujuName);
                data.setCreDate(creDate);
                data.setModDate(modDate);
                data.setStatus(status);

                data.setARKTX(ARKTX);
                data.setBDUSE(BDUSE);
                data.setABRAND(ABRAND);
                data.setATYP(ATYP);
                data.setASPD(ASPD);
                data.setAUSE(AUSE);
                data.setACAPA(ACAPA);
                data.setADRV(ADRV);
                data.setAEXP(AEXP);

                data.setAFQ(AFQ);
                data.setAMAN(AMAN);
                data.setAOPEN(AOPEN);
                data.setBCDM(BCDM);
                data.setBCL(BCL);
                data.setBCL(BCS);

                data.setBETC(BETC);
                data.setBETM(BETM);
                data.setBFLOORS(BFLOORS);

                elvInfoRepository.elvInfoSave(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }


    }


    //월별 대시보드 집계
    public HashMap<String, String> calculateElvInfo(String type) {
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = VaultDBConnection.getConnection();

            //con = DriverManager.getConnection(url, id, pass);

            System.out.println("con = " + con);


            StringBuffer sql = new StringBuffer();

            sql.append(" SELECT    ");

            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202401' AND A.atyp = ?) AS C202401,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202402' AND A.atyp = ?) AS C202402,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202403' AND A.atyp = ?) AS C202403,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202404' AND A.atyp = ?) AS C202404,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202405' AND A.atyp = ?) AS C202405,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202406' AND A.atyp = ?) AS C202406,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202407' AND A.atyp = ?) AS C202407,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202408' AND A.atyp = ?) AS C202408,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202409' AND A.atyp = ?) AS C202409,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202410' AND A.atyp = ?) AS C202410,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202411' AND A.atyp = ?) AS C202411,   ");
            sql.append(" (SELECT COUNT(*) FROM elv_info A WHERE A.mod_month = '202412' AND A.atyp = ?) AS C202412   ");


            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, type);
            pstmt.setString(2, type);
            pstmt.setString(3, type);
            pstmt.setString(4, type);
            pstmt.setString(5, type);
            pstmt.setString(6, type);
            pstmt.setString(7, type);
            pstmt.setString(8, type);
            pstmt.setString(9, type);
            pstmt.setString(10, type);
            pstmt.setString(11, type);
            pstmt.setString(12, type);

            rs = pstmt.executeQuery();

            //int totalCnt = 0;
            while (rs.next()) {

                String C202401 = rs.getString("C202401") == null ? "" : rs.getString("C202401");
                String C202402 = rs.getString("C202402") == null ? "" : rs.getString("C202402");
                String C202403 = rs.getString("C202403") == null ? "" : rs.getString("C202403");
                String C202404 = rs.getString("C202404") == null ? "" : rs.getString("C202404");
                String C202405 = rs.getString("C202405") == null ? "" : rs.getString("C202405");
                String C202406 = rs.getString("C202406") == null ? "" : rs.getString("C202406");
                String C202407 = rs.getString("C202407") == null ? "" : rs.getString("C202407");
                String C202408 = rs.getString("C202408") == null ? "" : rs.getString("C202408");
                String C202409 = rs.getString("C202409") == null ? "" : rs.getString("C202409");
                String C202410 = rs.getString("C202410") == null ? "" : rs.getString("C202410");
                String C202411 = rs.getString("C202411") == null ? "" : rs.getString("C202411");
                String C202412 = rs.getString("C202412") == null ? "" : rs.getString("C202412");

                //HashMap<String, String> data = new HashMap<>();


                int totalCnt = Integer.parseInt(C202401) + Integer.parseInt(C202402) + Integer.parseInt(C202403)
                              + Integer.parseInt(C202404) + Integer.parseInt(C202405) + Integer.parseInt(C202406)
                              + Integer.parseInt(C202407)  + Integer.parseInt(C202408) + Integer.parseInt(C202409)
                               + Integer.parseInt(C202410) + Integer.parseInt(C202411) + Integer.parseInt(C202412);

                ELVInfoDash dash = new ELVInfoDash();
                dash.setBatchDate(todayValue);
                dash.setElvType(type);
                dash.setTotalCnt(totalCnt);


                dash.setDis202401(Integer.parseInt(C202401));
                dash.setDis202402(Integer.parseInt(C202402));
                dash.setDis202403(Integer.parseInt(C202403));
                dash.setDis202404(Integer.parseInt(C202404));
                dash.setDis202405(Integer.parseInt(C202405));
                dash.setDis202406(Integer.parseInt(C202406));
                dash.setDis202407(Integer.parseInt(C202407));
                dash.setDis202408(Integer.parseInt(C202408));
                dash.setDis202409(Integer.parseInt(C202408));
                dash.setDis202410(Integer.parseInt(C202408));
                dash.setDis202411(Integer.parseInt(C202408));
                dash.setDis202412(Integer.parseInt(C202412));

                elvInfoRepository.dashSave(dash);


                /*data.put("202401", C202401);
                data.put("202402", C202402);
                data.put("202403", C202403);
                data.put("202404", C202404);
                data.put("202405", C202405);
                data.put("202406", C202406);
                data.put("202407", C202407);
                data.put("202408", C202408);
                data.put("202409", C202409);
                data.put("202410", C202410);
                data.put("202411", C202411);
                data.put("202412", C202412);*/
            }

            System.out.println("data = " + data);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return data;
    }
}
