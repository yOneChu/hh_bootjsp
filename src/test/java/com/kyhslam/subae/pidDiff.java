package com.kyhslam.subae;

import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.SubaeCommonUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class pidDiff {


    public static void main(String[] args) {


        HashSet<String> beforeMap = new HashSet<>();

        findPIDLineDiffBefore("EL_PA101A", "1276397", beforeMap); // 이전 108v

        System.out.println("beforeMap = " + beforeMap);

        findPIDLineDiffMain("EL_PA101A", "1280285", beforeMap); //최신 (기준이 되는 pid) > 해당 pid를 화면에 보여줌  112v
    }


    //처음에 셋팅
    public static ArrayList<ArrayList<String>> findPIDLineDiffBefore(String paramPid, String pidOid, HashSet<String> beforeMap) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        HashSet<String> dupCheck = new HashSet<>();

        HashMap<String, String> codeMap = new HashMap<>();
        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                    SELECT h.pid AS PID,
                            D.NO AS NO,
                            D.ADDR AS ADDR,
                            NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,
                            NVL(D.SPEC2, '-') AS SPEC2, NVL(D.CON2, '-') AS CON2,
                            NVL(D.SPEC3, '-') AS SPEC3, NVL(D.CON3, '-') AS CON3,
                            NVL(D.SPEC4, '-') AS SPEC4, NVL(D.CON4, '-') AS CON4,
                            NVL(D.SPEC5, '-') AS SPEC5, NVL(D.CON5, '-') AS CON5,
                            NVL(D.SPEC6, '-') AS SPEC6, NVL(D.CON6, '-') AS CON6,
                            NVL(D.SPEC7, '-') AS SPEC7, NVL(D.CON7, '-') AS CON7,
                            NVL(D.SPEC8, '-') AS SPEC8, NVL(D.CON8, '-') AS CON8,
                            NVL(D.SPEC9, '-') AS SPEC9, NVL(D.CON9, '-') AS CON9,
                            NVL(D.SPEC10, '-') AS SPEC10, NVL(D.CON10, '-') AS CON10,
                            NVL(D.SPEC11, '-') AS SPEC11, NVL(D.CON11, '-') AS CON11,
                            NVL(D.SPEC12, '-') AS SPEC12, NVL(D.CON12, '-') AS CON12,
                            NVL(D.SPEC13, '-') AS SPEC13, NVL(D.CON13, '-') AS CON13,
                            NVL(D.SPEC14, '-') AS SPEC14, NVL(D.CON14, '-') AS CON14,
                            NVL(D.SPEC15, '-') AS SPEC15, NVL(D.CON15, '-') AS CON15,
                            NVL(D.SPEC16, '-') AS SPEC16, NVL(D.CON16, '-') AS CON16,
                            NVL(D.SPEC17, '-') AS SPEC17, NVL(D.CON17, '-') AS CON17,
                            NVL(D.SPEC18, '-') AS SPEC18, NVL(D.CON18, '-') AS CON18,
                            NVL(D.SPEC19, '-') AS SPEC19, NVL(D.CON19, '-') AS CON19,
                            NVL(D.SPEC20, '-') AS SPEC20, NVL(D.CON20, '-') AS CON20,
                            NVL(D.KEY1, '-') AS KEY1, NVL(D.VAL1, '-') AS VAL1,
                            NVL(D.KEY2, '-') AS KEY2, NVL(D.VAL2, '-') AS VAL2,
                            NVL(D.KEY3, '-') AS KEY3, NVL(D.VAL3, '-') AS VAL3,
                            NVL(D.KEY4, '-') AS KEY4, NVL(D.VAL4, '-') AS VAL4,
                            NVL(D.KEY5, '-') AS KEY5, NVL(D.VAL5, '-') AS VAL5,
                            NVL(D.KEY6, '-') AS KEY6, NVL(D.VAL6, '-') AS VAL6,
                            NVL(D.KEY7, '-') AS KEY7, NVL(D.VAL7, '-') AS VAL7,
                            NVL(D.KEY8, '-') AS KEY8, NVL(D.VAL8, '-') AS VAL8,
                            NVL(D.KEY9, '-') AS KEY9, NVL(D.VAL9, '-') AS VAL9,
                            NVL(D.KEY10, '-') AS KEY10, NVL(D.VAL10, '-') AS VAL10,
                            NVL(D.KEY11, '-') AS KEY11, NVL(D.VAL11, '-') AS VAL11,
                            NVL(D.KEY12, '-') AS KEY12, NVL(D.VAL12, '-') AS VAL12,
                            NVL(D.KEY13, '-') AS KEY13, NVL(D.VAL13, '-') AS VAL13,
                            NVL(D.KEY14, '-') AS KEY14, NVL(D.VAL14, '-') AS VAL14,
                            NVL(D.KEY15, '-') AS KEY15, NVL(D.VAL15, '-') AS VAL15,
                            NVL(D.KEY16, '-') AS KEY16, NVL(D.VAL16, '-') AS VAL16,
                            NVL(D.KEY17, '-') AS KEY17, NVL(D.VAL17, '-') AS VAL17,
                            NVL(D.KEY18, '-') AS KEY18, NVL(D.VAL18, '-') AS VAL18,
                            NVL(D.KEY19, '-') AS KEY19, NVL(D.VAL19, '-') AS VAL19,
                            NVL(D.KEY20, '-') AS KEY20, NVL(D.VAL20, '-') AS VAL20,
                            NVL(D.REMARKS, '-') AS REMARKS, D.GOTO AS GOTO
                     FROM variant_d d, variant_h h
                     WHERE H.HOUID = D.HOUID 
                     AND H.PID = ?
                     AND D.HOUID = ?
                    """;

            //System.out.println("sql.toString() = " + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, paramPid);
            pstmt.setString(2, pidOid);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArrayList<String> row = new ArrayList<>();

                String PID = rs.getString("PID"); //제품번호
                String NO = rs.getString("NO") == null ? "" : rs.getString("NO");
                String ADDR = rs.getString("ADDR") == null ? "" : rs.getString("ADDR");
                String REMARKS = rs.getString("REMARKS") == null ? "" : rs.getString("REMARKS");
                String GOTO = rs.getString("GOTO") == null ? "" : rs.getString("GOTO");


                StringBuffer buff = new StringBuffer();


                if(REMARKS != null && !"".equals(REMARKS)) {
                    REMARKS = REMARKS.trim();
                    REMARKS = REMARKS.replace("-", "");
                }

                row.add(ADDR);

                if("".equals(ADDR)) {
                    buff.append("X-");
                } else {
                    buff.append(ADDR + "-");
                }




                for (int i = 1; i <= 20; i++) {
                    String s = rs.getString("SPEC" + i);

                    if(s != null && !"".equals(s)) {
                        s = s.trim();
                        s = s.replace("-", "");
                    }


                    String c = rs.getString("CON" + i);
                    if(c != null && !"".equals(c)) {
                        c = c.trim();
                        c = c.replace("-", "");
                    }
                    row.add(s);
                    row.add(c);

                    if("".equals(s)) {
                        buff.append("X-");
                    } else {
                        buff.append(s + "-");
                    }

                    if("".equals(c)) {
                        buff.append("X-");
                    } else {
                        buff.append(c + "-");
                    }
                }

                for (int i = 1; i <= 20; i++) {
                    String k = rs.getString("KEY" + i);
                    String v = rs.getString("VAL" + i);

                    if(k != null && !"".equals(k)) {
                        k = k.trim();
                        k = k.replace("-", "");
                    }

                    if(v != null && !"".equals(v)) {
                        v = v.trim();
                        v = v.replace("-", "");
                    }

                    row.add(k);
                    row.add(v);

                    if("".equals(k)) {
                        buff.append("X-");
                    } else {
                        buff.append(k + "-");
                    }

                    if("".equals(v)) {
                        buff.append("X-");
                    } else {
                        buff.append(v + "-");
                    }
                }

                row.add(GOTO);
                row.add(REMARKS);


                beforeMap.add(buff.toString());

                //System.out.println("row = " + buff.toString());
                //System.out.println();

                result.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    public static ArrayList<ArrayList<String>> findPIDLineDiffMain(String paramPid, String pidOid, HashSet<String> beforeMap) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                    SELECT h.pid AS PID,
                            D.NO AS NO,
                            D.ADDR AS ADDR,
                            NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,
                            NVL(D.SPEC2, '-') AS SPEC2, NVL(D.CON2, '-') AS CON2,
                            NVL(D.SPEC3, '-') AS SPEC3, NVL(D.CON3, '-') AS CON3,
                            NVL(D.SPEC4, '-') AS SPEC4, NVL(D.CON4, '-') AS CON4,
                            NVL(D.SPEC5, '-') AS SPEC5, NVL(D.CON5, '-') AS CON5,
                            NVL(D.SPEC6, '-') AS SPEC6, NVL(D.CON6, '-') AS CON6,
                            NVL(D.SPEC7, '-') AS SPEC7, NVL(D.CON7, '-') AS CON7,
                            NVL(D.SPEC8, '-') AS SPEC8, NVL(D.CON8, '-') AS CON8,
                            NVL(D.SPEC9, '-') AS SPEC9, NVL(D.CON9, '-') AS CON9,
                            NVL(D.SPEC10, '-') AS SPEC10, NVL(D.CON10, '-') AS CON10,
                            NVL(D.SPEC11, '-') AS SPEC11, NVL(D.CON11, '-') AS CON11,
                            NVL(D.SPEC12, '-') AS SPEC12, NVL(D.CON12, '-') AS CON12,
                            NVL(D.SPEC13, '-') AS SPEC13, NVL(D.CON13, '-') AS CON13,
                            NVL(D.SPEC14, '-') AS SPEC14, NVL(D.CON14, '-') AS CON14,
                            NVL(D.SPEC15, '-') AS SPEC15, NVL(D.CON15, '-') AS CON15,
                            NVL(D.SPEC16, '-') AS SPEC16, NVL(D.CON16, '-') AS CON16,
                            NVL(D.SPEC17, '-') AS SPEC17, NVL(D.CON17, '-') AS CON17,
                            NVL(D.SPEC18, '-') AS SPEC18, NVL(D.CON18, '-') AS CON18,
                            NVL(D.SPEC19, '-') AS SPEC19, NVL(D.CON19, '-') AS CON19,
                            NVL(D.SPEC20, '-') AS SPEC20, NVL(D.CON20, '-') AS CON20,
                            NVL(D.KEY1, '-') AS KEY1, NVL(D.VAL1, '-') AS VAL1,
                            NVL(D.KEY2, '-') AS KEY2, NVL(D.VAL2, '-') AS VAL2,
                            NVL(D.KEY3, '-') AS KEY3, NVL(D.VAL3, '-') AS VAL3,
                            NVL(D.KEY4, '-') AS KEY4, NVL(D.VAL4, '-') AS VAL4,
                            NVL(D.KEY5, '-') AS KEY5, NVL(D.VAL5, '-') AS VAL5,
                            NVL(D.KEY6, '-') AS KEY6, NVL(D.VAL6, '-') AS VAL6,
                            NVL(D.KEY7, '-') AS KEY7, NVL(D.VAL7, '-') AS VAL7,
                            NVL(D.KEY8, '-') AS KEY8, NVL(D.VAL8, '-') AS VAL8,
                            NVL(D.KEY9, '-') AS KEY9, NVL(D.VAL9, '-') AS VAL9,
                            NVL(D.KEY10, '-') AS KEY10, NVL(D.VAL10, '-') AS VAL10,
                            NVL(D.KEY11, '-') AS KEY11, NVL(D.VAL11, '-') AS VAL11,
                            NVL(D.KEY12, '-') AS KEY12, NVL(D.VAL12, '-') AS VAL12,
                            NVL(D.KEY13, '-') AS KEY13, NVL(D.VAL13, '-') AS VAL13,
                            NVL(D.KEY14, '-') AS KEY14, NVL(D.VAL14, '-') AS VAL14,
                            NVL(D.KEY15, '-') AS KEY15, NVL(D.VAL15, '-') AS VAL15,
                            NVL(D.KEY16, '-') AS KEY16, NVL(D.VAL16, '-') AS VAL16,
                            NVL(D.KEY17, '-') AS KEY17, NVL(D.VAL17, '-') AS VAL17,
                            NVL(D.KEY18, '-') AS KEY18, NVL(D.VAL18, '-') AS VAL18,
                            NVL(D.KEY19, '-') AS KEY19, NVL(D.VAL19, '-') AS VAL19,
                            NVL(D.KEY20, '-') AS KEY20, NVL(D.VAL20, '-') AS VAL20,
                            NVL(D.REMARKS, '-') AS REMARKS, D.GOTO AS GOTO
                     FROM variant_d d, variant_h h
                     WHERE H.HOUID = D.HOUID 
                     AND H.PID = ?
                     AND D.HOUID = ?
                    """;

            //System.out.println("sql.toString() = " + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, paramPid);
            pstmt.setString(2, pidOid);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArrayList<String> row = new ArrayList<>();

                String PID = rs.getString("PID"); //제품번호
                String NO = rs.getString("NO") == null ? "" : rs.getString("NO");
                String ADDR = rs.getString("ADDR") == null ? "" : rs.getString("ADDR");
                String REMARKS = rs.getString("REMARKS") == null ? "" : rs.getString("REMARKS");
                String GOTO = rs.getString("GOTO") == null ? "" : rs.getString("GOTO");


                StringBuffer buff = new StringBuffer();


                if(REMARKS != null && !"".equals(REMARKS)) {
                    REMARKS = REMARKS.trim();
                    REMARKS = REMARKS.replace("-", "");
                }

                row.add(ADDR);

                if("".equals(ADDR)) {
                    buff.append("X-");
                } else {
                    buff.append(ADDR + "-");
                }




                for (int i = 1; i <= 20; i++) {
                    String s = rs.getString("SPEC" + i);

                    if(s != null && !"".equals(s)) {
                        s = s.trim();
                        s = s.replace("-", "");
                    }


                    String c = rs.getString("CON" + i);
                    if(c != null && !"".equals(c)) {
                        c = c.trim();
                        c = c.replace("-", "");
                    }
                    row.add(s);
                    row.add(c);

                    if("".equals(s)) {
                        buff.append("X-");
                    } else {
                        buff.append(s + "-");
                    }

                    if("".equals(c)) {
                        buff.append("X-");
                    } else {
                        buff.append(c + "-");
                    }
                }

                for (int i = 1; i <= 20; i++) {
                    String k = rs.getString("KEY" + i);
                    String v = rs.getString("VAL" + i);

                    if(k != null && !"".equals(k)) {
                        k = k.trim();
                        k = k.replace("-", "");
                    }

                    if(v != null && !"".equals(v)) {
                        v = v.trim();
                        v = v.replace("-", "");
                    }

                    row.add(k);
                    row.add(v);

                    if("".equals(k)) {
                        buff.append("X-");
                    } else {
                        buff.append(k + "-");
                    }

                    if("".equals(v)) {
                        buff.append("X-");
                    } else {
                        buff.append(v + "-");
                    }
                }

                row.add(GOTO);
                row.add(REMARKS);


                if (beforeMap.contains(buff.toString())) {
                    //동일한 라인이 있는거다.
                    row.add("EQUAL");
                } else {
                    //동일한 라인이 없는거다
                    row.add("DIFF");
                    System.out.println("row = " + "다름다ㅡㄻ다름");
                    System.out.println(NO + " >>> row = " + buff.toString());
                }


                //System.out.println("row = " + buff.toString());
                //System.out.println();

                result.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }
}
