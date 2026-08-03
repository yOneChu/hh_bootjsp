package com.kyhslam.util;

import com.kyhslam.dto.CodeInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ShipCommonUtil {


    /**
     * 특성코드 리스트 - 선박
     * @return
     */
    public static ArrayList<CodeInfoDTO> getShipInfo() {

        ArrayList<CodeInfoDTO> result = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT a.name AS CODE,
                           a.TIT AS CODENAME,
                           c.NAME AS SPECNAME,
                           c.DES AS SPECVAL,
                           --c.ouid,
                           b.name
                        FROM HDEL_SYSTEM.dosfld a
                         INNER JOIN HDEL_SYSTEM.doscod b
                         ON a.TYPECLAS  = b.OUID
                         INNER JOIN HDEL_SYSTEM.DOSCODITM c
                         ON c.DOSCOD = b.OUID
                         WHERE TYPE=24 AND DOSCLAS=2706224418
                         ORDER BY a.name, c.CODITM
                    """;


            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String CODE = rs.getString("CODE") == null ? "" : rs.getString("CODE");
                String TIT = rs.getString("CODENAME") == null ? "" : rs.getString("CODENAME");
                String specName = rs.getString("SPECNAME") == null ? "" : rs.getString("SPECNAME");
                String specVal = rs.getString("SPECVAL") == null ? "" : rs.getString("SPECVAL");
                String name = rs.getString("name") == null ? "" : rs.getString("name");


                CodeInfoDTO  codeInfoDTO = new CodeInfoDTO();
                codeInfoDTO.setCode(CODE);
                codeInfoDTO.setCodeName(TIT);
                codeInfoDTO.setTypeName(specName);
                codeInfoDTO.setTypeVal(specVal);
                codeInfoDTO.setName(name);

                result.add(codeInfoDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }



    /**
     * 공사정보 필드 리스트 - 선박
     * @return
     */
    public static ArrayList<HashMap<String, String>> getShipField() {

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                select lower(dectohex(ouid)) as hexouid,
                       ouid,
                       name,
                       tit,
                 --    indx,
                 --    clm,
                 --    wdth,
                 --    titwdth,
                 --    wdth,
                 --    a.typeclas,
                       (select name from doscod where ouid=typeclas) codname
                     from hdel_system.dosfld a
                     where a.dosclas=2706224418
                     order by a.name
                    """;


            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String CODE = rs.getString("name") == null ? "" : rs.getString("name");
                String TIT = rs.getString("tit") == null ? "" : rs.getString("tit");
                //String specName = rs.getString("SPECNAME") == null ? "" : rs.getString("SPECNAME");
                //String specVal = rs.getString("SPECVAL") == null ? "" : rs.getString("SPECVAL");
                //String name = rs.getString("name") == null ? "" : rs.getString("name");



                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NAME", CODE);
                map.put("TIT", TIT);

                result.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }




}
