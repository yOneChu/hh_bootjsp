package com.kyhslam.util;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PIDCommonUtil {

    //수배로직 - 로직에디터


    //PID 총 라인(내용) 수
    public static void insert_Type01() {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        String PIDCOUNT = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {


            //getType02Info

            String todayCount = getType01Info();


            con = VaultDBConnection.getConnection();


            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO PID_TYPE01(BATCH_DATE ,  PID_COUNT ) ");
            sql.append(" VALUES(?, ?) ");


            //pstmt = con.prepareStatement(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayValue);
            pstmt.setString(2, todayCount);

            //rs = pstmt.executeQuery();
            pstmt.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
    }

    //PID 개수
    public static void insert_Type02() {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        String PIDCOUNT = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {


            //getType02Info

            String todayCount = getType02Info();


            con = VaultDBConnection.getConnection();


            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO PID_TYPE02(BATCH_DATE ,  PID_ALL_COUNT ) ");
            sql.append(" VALUES(?, ?) ");


            //pstmt = con.prepareStatement(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayValue);
            pstmt.setString(2, todayCount);

            //rs = pstmt.executeQuery();
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
    }


    //PID에 연결된 각각의 라인 수 저장
    public static void insert_Type03() {

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        String PIDCOUNT = "";


        //String PIDNAME = rs.getString("PIDNAME") == null ? "" : rs.getString("PIDNAME");
        //String PIDCOUNT = rs.getString("PIDCOUNT") == null ? "" : rs.getString("PIDCOUNT");


        HashMap<String, String> todayMap = getType03Info();

        for (String key : todayMap.keySet()) {

            String count = todayMap.get(key);

            PreparedStatement pstmt = null;
            ResultSet rs = null;
            Connection con = null;

            try {
                con = VaultDBConnection.getConnection();

                StringBuffer sql = new StringBuffer();
                sql.append(" INSERT INTO PID_TYPE03(BATCH_DATE, PID, PID_COUNT) ");
                sql.append(" VALUES(?, ?, ?) ");

                //pstmt = con.prepareStatement(sql.toString());
                pstmt = con.prepareStatement(sql.toString());
                pstmt.setString(1, todayValue);
                pstmt.setString(2, key.trim());
                pstmt.setString(3, count);


                //rs = pstmt.executeQuery();
                pstmt.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                VaultDBConnection.disconnect(con, pstmt, rs);
            }

        } // end for
    }


    //pic총 라인수
    public static String getType01Info() {

        String PIDCOUNT = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = PLMDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT COUNT(H.PID) AS PIDCOUNT");
            sql.append(" FROM variant_d d, variant_h h, variant_id id ");
            sql.append(" WHERE h.HOUID = id.LAST_HOUID AND h.HOUID =d.HOUID ");

            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PIDCOUNT = rs.getString("PIDCOUNT") == null ? "" : rs.getString("PIDCOUNT");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        return PIDCOUNT;
    }


    //PID총 개수
    public static String getType02Info() {

        String PIDCOUNT = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {

            con = PLMDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT COUNT(DISTINCT H.PID) AS PIDCOUNT FROM variant_h H  ");
            sql.append(" WHERE ");
            sql.append(" H.VERSION != -1 ");

            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                PIDCOUNT = rs.getString("PIDCOUNT") == null ? "" : rs.getString("PIDCOUNT");

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return PIDCOUNT;
    }


    //PID에 연결된 각가의 라인수
    public static HashMap<String, String> getType03Info() {

        HashMap<String, String> result = new HashMap<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        try {
            con = PLMDBConnection.getConnection();


            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT DISTINCT (H.PID) AS PIDNAME, COUNT(*) AS PIDCOUNT ");
            sql.append(" FROM variant_d d, variant_h h, variant_id id ");
            sql.append(" WHERE h.HOUID = id.LAST_HOUID AND h.HOUID =d.HOUID ");
            sql.append(" GROUP BY H.PID ");
            sql.append(" ORDER BY 1 ASC ");

            //GROUP BY H.PID
            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String PIDNAME = rs.getString("PIDNAME") == null ? "" : rs.getString("PIDNAME");
                String PIDCOUNT = rs.getString("PIDCOUNT") == null ? "" : rs.getString("PIDCOUNT");


                result.put(PIDNAME.trim(), PIDCOUNT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    /**
     * PID 상세조회
     * @param pid
     * @param FIELD
     * @param GUBUN
     * @param connectGubun
     * @param pid02
     * @param SPEC02
     * @param GUBUN02
     * @return
     */
    public static ArrayList<HashMap<String, String>> findPIDDetail(String pid, String FIELD, String GUBUN, String connectGubun
            , String pid02, String SPEC02, String GUBUN02, String CON05, String PID03, String PID04, String PID05, String join) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs2 = null;
        Connection con = null;

        try {

            StringBuffer sql = new StringBuffer();

            if(FIELD != null && !"".equals(FIELD)) FIELD = FIELD.toUpperCase();
            if(CON05 != null && !"".equals(CON05)) CON05 = CON05.trim();
            if(pid != null && !"".equals(pid)) pid = pid.trim();
            if(pid02 != null && !"".equals(pid02)) pid02 = pid02.trim();

            //SPEC, CON
            sql.append(" SELECT h.pid, D.NO, NVL(D.REMARKS, '-') AS REMARKS, D.ADDR AS ADDR,  ");
            sql.append(" NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,   ");
            sql.append(" NVL(D.SPEC2, '-') AS SPEC2,  NVL(D.CON2, '-') AS CON2,   ");
            sql.append(" NVL(D.SPEC3, '-') AS SPEC3,  NVL(D.CON3, '-') AS CON3,   ");
            sql.append(" NVL(D.SPEC4, '-') AS SPEC4,  NVL(D.CON4, '-') AS CON4,   ");
            sql.append(" NVL(D.SPEC5, '-') AS SPEC5,  NVL(D.CON5, '-') AS CON5,   ");
            sql.append(" NVL(D.SPEC6, '-') AS SPEC6,  NVL(D.CON6, '-') AS CON6,   ");
            sql.append(" NVL(D.SPEC7, '-') AS SPEC7,  NVL(D.CON7, '-') AS CON7,   ");
            sql.append(" NVL(D.SPEC8, '-') AS SPEC8,  NVL(D.CON8, '-') AS CON8,   ");
            sql.append(" NVL(D.SPEC9, '-') AS SPEC9,  NVL(D.CON9, '-') AS CON9,   ");
            sql.append(" NVL(D.SPEC10, '-') AS SPEC10, NVL(D.CON10, '-') AS CON10,    ");
            sql.append(" NVL(D.SPEC11, '-') AS SPEC11, NVL(D.CON11, '-') AS CON11,    ");
            sql.append(" NVL(D.SPEC12, '-') AS SPEC12, NVL(D.CON12, '-') AS CON12,    ");
            sql.append(" NVL(D.SPEC13, '-') AS SPEC13, NVL(D.CON13, '-') AS CON13,    ");
            sql.append(" NVL(D.SPEC14, '-') AS SPEC14, NVL(D.CON14, '-') AS CON14,    ");
            sql.append(" NVL(D.SPEC15, '-') AS SPEC15, NVL(D.CON15, '-') AS CON15,    ");
            sql.append(" NVL(D.SPEC16, '-') AS SPEC16, NVL(D.CON16, '-') AS CON16,    ");
            sql.append(" NVL(D.SPEC17, '-') AS SPEC17, NVL(D.CON17, '-') AS CON17,    ");
            sql.append(" NVL(D.SPEC18, '-') AS SPEC18, NVL(D.CON18, '-') AS CON18,    ");
            sql.append(" NVL(D.SPEC19, '-') AS SPEC19, NVL(D.CON19, '-') AS CON19,    ");
            sql.append(" NVL(D.SPEC20, '-') AS SPEC20, NVL(D.CON20, '-') AS CON20,    ");


            //KEY, VAL
            sql.append(" NVL(D.KEY1, '-') AS KEY1,  NVL(D.VAL1, '-') AS VAL1 ,      ");
            sql.append(" NVL(D.KEY2, '-') AS KEY2,  NVL(D.VAL2, '-') AS VAL2 ,      ");
            sql.append(" NVL(D.KEY3, '-') AS KEY3,  NVL(D.VAL3, '-') AS VAL3 ,      ");
            sql.append(" NVL(D.KEY4, '-') AS KEY4,  NVL(D.VAL4, '-') AS VAL4 ,      ");
            sql.append(" NVL(D.KEY5, '-') AS KEY5,  NVL(D.VAL5, '-') AS VAL5 ,      ");
            sql.append(" NVL(D.KEY6, '-') AS KEY6,  NVL(D.VAL6, '-') AS VAL6 ,      ");
            sql.append(" NVL(D.KEY7, '-') AS KEY7,  NVL(D.VAL7, '-') AS VAL7 ,      ");
            sql.append(" NVL(D.KEY8, '-') AS KEY8,  NVL(D.VAL8, '-') AS VAL8 ,      ");
            sql.append(" NVL(D.KEY9, '-') AS KEY9,  NVL(D.VAL9, '-') AS VAL9 ,      ");
            sql.append(" NVL(D.KEY10, '-') AS KEY10 , NVL(D.VAL10, '-') AS VAL10 ,      ");
            sql.append(" NVL(D.KEY11, '-') AS KEY11 , NVL(D.VAL11, '-') AS VAL11 ,      ");
            sql.append(" NVL(D.KEY12, '-') AS KEY12 , NVL(D.VAL12, '-') AS VAL12 ,      ");
            sql.append(" NVL(D.KEY13, '-') AS KEY13 , NVL(D.VAL13, '-') AS VAL13 ,      ");
            sql.append(" NVL(D.KEY14, '-') AS KEY14 , NVL(D.VAL14, '-') AS VAL14 ,      ");
            sql.append(" NVL(D.KEY15, '-') AS KEY15 , NVL(D.VAL15, '-') AS VAL15 ,      ");
            sql.append(" NVL(D.KEY16, '-') AS KEY16 , NVL(D.VAL16, '-') AS VAL16 ,      ");
            sql.append(" NVL(D.KEY17, '-') AS KEY17 , NVL(D.VAL17, '-') AS VAL17 ,      ");
            sql.append(" NVL(D.KEY18, '-') AS KEY18 , NVL(D.VAL18, '-') AS VAL18 ,      ");
            sql.append(" NVL(D.KEY19, '-') AS KEY19 , NVL(D.VAL19, '-') AS VAL19 ,      ");
            sql.append(" NVL(D.KEY20, '-') AS KEY20 , NVL(D.VAL20, '-') AS VAL20       ");

            //System.out.println("FIELD = " + FIELD);
            System.out.println("join = " + join);

            if(join != null && (join.contains("AND") || join.contains("OR")) ) {

                sql = makeConnectQueryV2(sql, pid, pid02, FIELD, SPEC02, GUBUN, GUBUN02, join);

            } else if( pid02 != null && !"".equals(pid02.trim()) && !FIELD.equals("REMARKS")) {

                //System.out.println("pid == " + pid); // PID01
                //System.out.println("pid02 == " + pid02); //
                //System.out.println("FIELD == " + FIELD); // 조건1
                //System.out.println("SPEC02 == " + SPEC02); //조건2
                //System.out.println("GUBUN == " + GUBUN); // LINK-01 (LIKE, EQUAL ..)
                //System.out.println("GUBUN02 == " + GUBUN02); // LINK-02 (LIKE, EQUAL ..)

                sql = makeConnectQuery(sql, pid, pid02, FIELD, SPEC02, GUBUN, GUBUN02);

            } else {
                if(FIELD.equals("VAL")) {
                    sql = makeQueryVAL(sql, pid, FIELD, GUBUN);
                } else if (FIELD.equals("SPEC")){
                    sql = makeQuerySPEC(sql, pid, FIELD, GUBUN);
                } else if (FIELD.equals("CON")) {
                    sql = makeQueryCON(sql, pid, FIELD, GUBUN);
                } else if (FIELD.equals("REMARKS")) {
                    sql = makeQueryRemarks(sql, pid, FIELD, GUBUN); // 보류
                } else {
                    sql = makeQueryKey(sql, pid, FIELD, GUBUN);
                }
            }



            if( !"".equals(PID03.trim()) || !"".equals(PID04.trim()) || !"".equals(PID05.trim()) ) {
                //System.out.println("PID-GROUP==============");
                //CON05 : PID-GROUP -> NOT EQUAL/EQUAL/LIKE/NOT LIKE

                //NOT이 포함되면 AND 조건으로 연결한다.
                String connectWhere = "OR";
                if(CON05.trim().startsWith("NOT")) {
                    connectWhere = "AND";
                }


                if(CON05.equals("EQUAL")) CON05 = "=";
                if(CON05.equals("NOT EQUAL")) CON05 = "!=";


                if(FIELD.equals("REMARKS")) {
                    //sql.append(" ) ");
                } else {
                    sql.append(" ) ");
                }
                sql.append(" AND ");

                if(PID03 != null && !"".equals(PID03.trim())) {

                    //sql.append(" AND ");

                    if(CON05.contains("LIKE")) {
                        sql.append(" ( H.PID " + CON05 + " '%" + PID03.trim() + "%'");
                    } else {
                        sql.append(" ( H.PID " + CON05 + " '" + PID03.trim() + "' ");
                    }

                    //String connectWhere = "OR";


                    System.out.println("connectWhere = " + connectWhere);

                    if(PID04 != null && !"".equals(PID04.trim())) {

                        if(CON05.contains("LIKE")) {
                            sql.append(" " + connectWhere + " H.PID " + CON05 + " '%" + PID04.trim() + "%'");
                           /* if(CON05.contains("NOT")) {
                                sql.append(" AND H.PID " + CON05 + " '%" + PID04.trim() + "%'");
                            } else {
                                sql.append(" OR H.PID " + CON05 + " '%" + PID04.trim() + "%'");
                            }*/
                        } else {

                            //sql.append(" OR H.PID " + CON05 + " '" +  PID04.trim() + "' ");
                            sql.append(" " + connectWhere +" H.PID " + CON05 + " '" +  PID04.trim() + "' ");
                        }


                        if(PID05 != null && !"".equals(PID05.trim())) {
                            if(CON05.contains("LIKE")) {
                                sql.append(" " + connectWhere + " H.PID " + CON05 + " '%" + PID05.trim() + "%'");
                            } else {
                                sql.append(" " + connectWhere + " H.PID " + CON05 + " '" + PID05.trim() + "'");
                            }
                        }
                    }


                    sql.append(" )  ");
                } // end PID03



                sql.append(" ORDER BY PID, NO ");
            } else {

                //System.out.println("111111111");
                if(FIELD.equals("REMARKS")) {
                    //System.out.println("remark okkkkkkkkk.");
                    sql.append(" ORDER BY PID, NO ");
                } else {
                    sql.append(" ) ORDER BY PID, NO ");
                }


            }

            con = PLMDBConnection.getConnection();

            System.out.println("PID_Search sql.toString() = " + sql.toString());
            pstmt = con.prepareStatement(sql.toString());

            //pstmt2.setString(1, projrctNo);
            rs2 = pstmt.executeQuery();
            //rsmd = rs.getMetaData();

            while(rs2.next())
            {
                HashMap<String, String> tmpObject = new HashMap<>();

                tmpObject.put("PID", rs2.getString("PID"));
                tmpObject.put("NO",  rs2.getString("NO"));
                tmpObject.put("REMARKS",  rs2.getString("REMARKS"));
                tmpObject.put("ADDR",  rs2.getString("ADDR") == null ? "" : rs2.getString("ADDR"));
                //tmpObject.put("KEY1", rs2.getString("KEY1"));
                //tmpObject.put("VAL1", rs2.getString("VAL1"));

                tmpObject.put("SPEC1", String.valueOf(rs2.getString("SPEC1"))); tmpObject.put("CON1", String.valueOf(rs2.getString("CON1")));
                tmpObject.put("SPEC2", String.valueOf(rs2.getString("SPEC2"))); tmpObject.put("CON2", String.valueOf(rs2.getString("CON2")));
                tmpObject.put("SPEC3", String.valueOf(rs2.getString("SPEC3"))); tmpObject.put("CON3", String.valueOf(rs2.getString("CON3")));
                tmpObject.put("SPEC4", String.valueOf(rs2.getString("SPEC4"))); tmpObject.put("CON4", String.valueOf(rs2.getString("CON4")));
                tmpObject.put("SPEC5", String.valueOf(rs2.getString("SPEC5"))); tmpObject.put("CON5", String.valueOf(rs2.getString("CON5")));
                tmpObject.put("SPEC6", String.valueOf(rs2.getString("SPEC6"))); tmpObject.put("CON6", String.valueOf(rs2.getString("CON6")));
                tmpObject.put("SPEC7", String.valueOf(rs2.getString("SPEC7"))); tmpObject.put("CON7", String.valueOf(rs2.getString("CON7")));
                tmpObject.put("SPEC8", String.valueOf(rs2.getString("SPEC8"))); tmpObject.put("CON8", String.valueOf(rs2.getString("CON8")));
                tmpObject.put("SPEC9", String.valueOf(rs2.getString("SPEC9"))); tmpObject.put("CON9", String.valueOf(rs2.getString("CON9")));
                tmpObject.put("SPEC10", String.valueOf(rs2.getString("SPEC10"))); tmpObject.put("CON10", String.valueOf(rs2.getString("CON10")));
                tmpObject.put("SPEC11", String.valueOf(rs2.getString("SPEC11"))); tmpObject.put("CON11", String.valueOf(rs2.getString("CON11")));
                tmpObject.put("SPEC12", String.valueOf(rs2.getString("SPEC12"))); tmpObject.put("CON12", String.valueOf(rs2.getString("CON12")));
                tmpObject.put("SPEC13", String.valueOf(rs2.getString("SPEC13"))); tmpObject.put("CON13", String.valueOf(rs2.getString("CON13")));
                tmpObject.put("SPEC14", String.valueOf(rs2.getString("SPEC14"))); tmpObject.put("CON14", String.valueOf(rs2.getString("CON14")));
                tmpObject.put("SPEC15", String.valueOf(rs2.getString("SPEC15"))); tmpObject.put("CON15", String.valueOf(rs2.getString("CON15")));
                tmpObject.put("SPEC16", String.valueOf(rs2.getString("SPEC16"))); tmpObject.put("CON16", String.valueOf(rs2.getString("CON16")));
                tmpObject.put("SPEC17", String.valueOf(rs2.getString("SPEC17"))); tmpObject.put("CON17", String.valueOf(rs2.getString("CON17")));
                tmpObject.put("SPEC18", String.valueOf(rs2.getString("SPEC18"))); tmpObject.put("CON18", String.valueOf(rs2.getString("CON18")));
                tmpObject.put("SPEC19", String.valueOf(rs2.getString("SPEC19"))); tmpObject.put("CON19", String.valueOf(rs2.getString("CON19")));
                tmpObject.put("SPEC20", String.valueOf(rs2.getString("SPEC20"))); tmpObject.put("CON20", String.valueOf(rs2.getString("CON20")));


                tmpObject.put("KEY1", String.valueOf(rs2.getString("KEY1"))); tmpObject.put("VAL1", String.valueOf(rs2.getString("VAL1")));
                tmpObject.put("KEY2", String.valueOf(rs2.getString("KEY2"))); tmpObject.put("VAL2", String.valueOf(rs2.getString("VAL2")));
                tmpObject.put("KEY3", String.valueOf(rs2.getString("KEY3"))); tmpObject.put("VAL3", String.valueOf(rs2.getString("VAL3")));
                tmpObject.put("KEY4", String.valueOf(rs2.getString("KEY4"))); tmpObject.put("VAL4", String.valueOf(rs2.getString("VAL4")));
                tmpObject.put("KEY5", String.valueOf(rs2.getString("KEY5"))); tmpObject.put("VAL5", String.valueOf(rs2.getString("VAL5")));
                tmpObject.put("KEY6", String.valueOf(rs2.getString("KEY6"))); tmpObject.put("VAL6", String.valueOf(rs2.getString("VAL6")));
                tmpObject.put("KEY7", String.valueOf(rs2.getString("KEY7"))); tmpObject.put("VAL7", String.valueOf(rs2.getString("VAL7")));
                tmpObject.put("KEY8", String.valueOf(rs2.getString("KEY8"))); tmpObject.put("VAL8", String.valueOf(rs2.getString("VAL8")));
                tmpObject.put("KEY9", String.valueOf(rs2.getString("KEY9"))); tmpObject.put("VAL9", String.valueOf(rs2.getString("VAL9")));
                tmpObject.put("KEY10", String.valueOf(rs2.getString("KEY10"))); tmpObject.put("VAL10", String.valueOf(rs2.getString("VAL10")));
                tmpObject.put("KEY11", String.valueOf(rs2.getString("KEY11"))); tmpObject.put("VAL11", String.valueOf(rs2.getString("VAL11")));
                tmpObject.put("KEY12", String.valueOf(rs2.getString("KEY12"))); tmpObject.put("VAL12", String.valueOf(rs2.getString("VAL12")));
                tmpObject.put("KEY13", String.valueOf(rs2.getString("KEY13"))); tmpObject.put("VAL13", String.valueOf(rs2.getString("VAL13")));
                tmpObject.put("KEY14", String.valueOf(rs2.getString("KEY14"))); tmpObject.put("VAL14", String.valueOf(rs2.getString("VAL14")));
                tmpObject.put("KEY15", String.valueOf(rs2.getString("KEY15"))); tmpObject.put("VAL15", String.valueOf(rs2.getString("VAL15")));
                tmpObject.put("KEY16", String.valueOf(rs2.getString("KEY16"))); tmpObject.put("VAL16", String.valueOf(rs2.getString("VAL16")));
                tmpObject.put("KEY17", String.valueOf(rs2.getString("KEY17"))); tmpObject.put("VAL17", String.valueOf(rs2.getString("VAL17")));
                tmpObject.put("KEY18", String.valueOf(rs2.getString("KEY18"))); tmpObject.put("VAL18", String.valueOf(rs2.getString("VAL18")));
                tmpObject.put("KEY19", String.valueOf(rs2.getString("KEY19"))); tmpObject.put("VAL19", String.valueOf(rs2.getString("VAL19")));
                tmpObject.put("KEY20", String.valueOf(rs2.getString("KEY20"))); tmpObject.put("VAL20", String.valueOf(rs2.getString("VAL20")));

                result.add(tmpObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs2);
        }

        return result;
    }

    public static StringBuffer makeQueryVAL(StringBuffer temSql, String pid, String field, String gubun) {

        String param1 = "";

        if(gubun != null && !"".equals(gubun)) {

            if(gubun.equals("LIKE")) {
                param1 = "'%" + pid + "%'";
            } else {
                gubun = "=";
                param1 = "'" + pid.trim() + "'";
            }
        }

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID ");


        temSql.append(" AND (  ");
        temSql.append(" d.VAL1 "    + gubun + " " + param1 + " or " + " d.VAL2 " + gubun + " " + param1);
        temSql.append(" or d.VAL3 " + gubun + " " + param1 + " or " + " d.VAL4 " + gubun + " " + param1);
        temSql.append(" or d.VAL5 " + gubun + " " + param1 + " or " + " d.VAL6 " + gubun + " " + param1);
        temSql.append(" or d.VAL7 " + gubun + " " + param1 + " or " + " d.VAL8 " + gubun + " " + param1);
        temSql.append(" or d.VAL9 " + gubun + " " + param1 + " or " + " d.VAL10 " +  gubun + " " + param1);
        temSql.append(" or d.VAL11 " + gubun + " " + param1 + " or " + " d.VAL12 " + gubun + " " + param1);
        temSql.append(" or d.VAL13 " + gubun + " " + param1 + " or " + " d.VAL14 " + gubun + " " + param1);
        temSql.append(" or d.VAL15 " + gubun + " " + param1 + " or " + " d.VAL16 " + gubun + " " + param1);
        temSql.append(" or d.VAL17 " + gubun + " " + param1 + " or " + " d.VAL18 " + gubun + " " + param1);
        temSql.append(" or d.VAL19 " + gubun + " " + param1 + " or " + " d.VAL20 " + gubun + " " + param1);
        //temSql.append(" ) ORDER BY PID, NO ");

        //System.out.println("makeQueryVAL == " + temSql.toString());//all part - pick이 하나 이상있는거

        return temSql;
    }


    //CON
    public static StringBuffer makeQueryCON(StringBuffer temSql, String pid, String field, String gubun) {
        String param1 = "";
        if(gubun != null && !"".equals(gubun)) {
            if(gubun.equals("LIKE")) {
                param1 = "'%" + pid + "%'";
            } else {
                gubun = "=";
                param1 = "'" + pid.trim() + "'";
            }
        }

        //SPEC, CON

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID ");


        temSql.append(" AND (  ");

        temSql.append(" d.CON1 "    + gubun + " " + param1 + " or " + " d.CON2 " + gubun + " " + param1);
        temSql.append(" or d.CON3 " + gubun + " " + param1 + " or " + " d.CON4 " + gubun + " " + param1);
        temSql.append(" or d.CON5 " + gubun + " " + param1 + " or " + " d.CON6 " + gubun + " " + param1);
        temSql.append(" or d.CON7 " + gubun + " " + param1 + " or " + " d.CON8 " + gubun + " " + param1);
        temSql.append(" or d.CON9 "  + gubun + " " + param1 + " or " + " d.CON10 " + gubun + " " + param1);
        temSql.append(" or d.CON11 " + gubun + " " + param1 + " or " + " d.CON12 " + gubun + " " + param1);
        temSql.append(" or d.CON13 " + gubun + " " + param1 + " or " + " d.CON14 " + gubun + " " + param1);
        temSql.append(" or d.CON15 " + gubun + " " + param1 + " or " + " d.CON16 " + gubun + " " + param1);
        temSql.append(" or d.CON17 " + gubun + " " + param1 + " or " + " d.CON18 " + gubun + " " + param1);
        temSql.append(" or d.CON19 " + gubun + " " + param1 + " or " + " d.CON20 " + gubun + " " + param1);
        //temSql.append(" ) ORDER BY PID, NO ");

        return temSql;
    }



    //SPEC
    public static StringBuffer makeQuerySPEC(StringBuffer temSql, String pid, String field, String gubun) {

        String param1 = "";

        if(gubun != null && !"".equals(gubun)) {

            if(gubun.equals("LIKE")) {
                param1 = "'%" + pid + "%'";
            } else {
                gubun = "=";
                param1 = "'" + pid.trim() + "'";
            }
        }

        //temSql.append(" SELECT h.pid, D.NO, D.SPEC1, D.CON1, D.SPEC2, D.CON2, D.SPEC3, D.CON3, D.SPEC4, D.CON4    ");

        //SPEC, CON
        //temSql.append(" SELECT h.pid, D.NO,   ");


        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID  ");

		/* if(remark != null && !"".equals(remark)) {
			temSql.append(" AND D.REMARKS LIKE '%" + remark + "%'");
		} */

        temSql.append(" AND (  ");

        temSql.append(" d.SPEC1 " + gubun + " " + param1 + " or " + " d.SPEC2 " + gubun + " " + param1);
        temSql.append(" or d.SPEC3 " + gubun + " " +  param1 + " or " + " d.SPEC4 " + gubun + " " + param1);
        temSql.append(" or d.SPEC5 " + gubun + " " + param1 + " or " + " d.SPEC6 " + gubun + " " + param1);
        temSql.append(" or d.SPEC7 " + gubun + " " + param1 + " or " + " d.SPEC8 " + gubun + " " + param1);
        temSql.append(" or d.SPEC9 "  + gubun + " " + param1 + " or " + " d.SPEC10 " + gubun + " " + param1);
        temSql.append(" or d.SPEC11 " + gubun + " " + param1 + " or " + " d.SPEC12 " + gubun + " " + param1);
        temSql.append(" or d.SPEC13 " + gubun + " " + param1 + " or " + " d.SPEC14 " + gubun + " " + param1);
        temSql.append(" or d.SPEC15 " + gubun + " " + param1 + " or " + " d.SPEC16 " + gubun + " " + param1);
        temSql.append(" or d.SPEC17 " + gubun + " " + param1 + " or " + " d.SPEC18 " + gubun + " " + param1);
        temSql.append(" or d.SPEC19 " + gubun + " " + param1 + " or " + " d.SPEC20 " + gubun + " " + param1);
        //temSql.append(" ) ORDER BY PID, NO ");

        System.out.println("makeQuerySPEC == " + temSql.toString());

        return temSql;
    }



    //KEY
    public static StringBuffer makeQueryKey(StringBuffer temSql, String pid, String field, String gubun) {

        String param1 = "";

        if(gubun != null && !"".equals(gubun)) {

            if(gubun.equals("LIKE")) {
                param1 = "'%" + pid + "%'";
            } else {
                gubun = "=";
                param1 = "'" + pid.trim() + "'";
            }
        }

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID  ");


        temSql.append(" AND (  ");
        temSql.append(" d.KEY1 "    + gubun + " " + param1 + " or " + " d.KEY2 " + gubun + " " + param1);
        temSql.append(" or d.KEY3 " + gubun + " " + param1 + " or " + " d.KEY4 " + gubun + " " + param1);
        temSql.append(" or d.KEY5 " + gubun + " " + param1 + " or " + " d.KEY6 " + gubun + " " + param1);
        temSql.append(" or d.KEY7 " + gubun + " " + param1 + " or " + " d.KEY8 " + gubun + " " + param1);
        temSql.append(" or d.KEY9 " + gubun + " " + param1 + " or " + " d.KEY10 " +  gubun + " " + param1);
        temSql.append(" or d.KEY11 " + gubun + " " + param1 + " or " + " d.KEY12 " + gubun + " " + param1);
        temSql.append(" or d.KEY13 " + gubun + " " + param1 + " or " + " d.KEY14 " + gubun + " " + param1);
        temSql.append(" or d.KEY15 " + gubun + " " + param1 + " or " + " d.KEY16 " + gubun + " " + param1);
        temSql.append(" or d.KEY17 " + gubun + " " + param1 + " or " + " d.KEY18 " + gubun + " " + param1);
        temSql.append(" or d.KEY19 " + gubun + " " + param1 + " or " + " d.KEY20 " + gubun + " " + param1);
        //temSql.append(" ) ORDER BY PID, NO ");


        //System.out.println("makeQueryKey == " + temSql.toString());

        return temSql;
    }

    //remark
    public static StringBuffer makeQueryRemarks(StringBuffer temSql, String pid, String field, String gubun) {

        String param1 = "";

        if(gubun != null && !"".equals(gubun)) {

            if(gubun.equals("LIKE")) {
                param1 = "'%" + pid + "%'";
            } else {
                gubun = "=";
                param1 = "'" + pid.trim() + "'";
            }
        }

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID  ");


        temSql.append(" AND d.REMARKS " + gubun + " " + param1);
        //temSql.append(" ORDER BY PID, NO ");

        //System.out.println("makeQuerySPEC == " + temSql.toString());

        return temSql;
    }


    //조건이 2개 일때
    public static StringBuffer makeConnectQuery(StringBuffer temSql, String pid01, String pid02, String field, String field02,
                                         String gubun01, String gubun02) {

        //makeConnectQuery(sql, pid01, pid02, field, SPEC02, GUBUN, GUBUN02);

        //System.out.println("makeConnectQuery ============================= ");
//        System.out.println("pid01 == " + pid01);
//        System.out.println("pid02 == " + pid02);
//        System.out.println("field == " + field);
//        System.out.println("field02 == " + field02);
//        System.out.println("gubun01 == " + gubun01);
//        System.out.println("gubun02 == " + gubun02);

        String param1 = "";
        if(gubun01 != null && !"".equals(gubun01)) {
            if(gubun01.equals("LIKE")) {
                param1 = "'%" + pid01.trim() + "%'";
            } else {
                gubun01 = "=";
                param1 = "'" + pid01.trim() + "'";
            }
        }

        String param2 = "";
        if(gubun02 != null && !"".equals(gubun02)) {
            if(gubun02.equals("LIKE") || gubun02.equals("NOT LIKE")) {
                param2 = "'%" + pid02.trim() + "%'";

            } else if(gubun02.equals("NOT_EQUAL")) {
                gubun02 = "!=";
                param2 = "'" + pid02.trim() + "'";

            } else {
                gubun02 = "=";
                param2 = "'" + pid02.trim() + "'";
            }
        }

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID  ");
        temSql.append(" AND (  ");


        for(int i=1; i <= 20; i++) {
            if (i == 20) {
                temSql.append(" (d." + field + String.valueOf(i) + " " + gubun01 + " " + param1 );
                temSql.append(" AND d." + field02 + String.valueOf(i) + " " + gubun02 + " " + param2 + ")" );

            } else {
                temSql.append(" (d." + field + String.valueOf(i) + " " + gubun01 + " " + param1 );
                temSql.append(" AND d." + field02 + String.valueOf(i) + " " + gubun02 + " " + param2 + ") OR" );
            }
        }


        //temSql.append(" ) ORDER BY PID, NO ");


        //System.out.println("makeQueryKey == " + temSql.toString());
        return temSql;
    }

    public static StringBuffer makeConnectQueryV2(StringBuffer temSql, String pid01, String pid02, String spec01, String spec02,
                                                String link01, String link02, String join) {


        //System.out.println("makeConnectQueryV2 2222============================= ");
//        System.out.println("pid01 == " + pid01);
//        System.out.println("pid02 == " + pid02);
//        System.out.println("spec01 == " + spec01);
//        System.out.println("spec02 == " + spec02);
//        System.out.println("link01 == " + link01);
//        System.out.println("link02 == " + link02);
        /*pid01 == 914
        pid02 == 32200452G01XB
        spec01 == CON
        spec02 == VAL
        link01 == LIKE
        link02 == EQUAL*/

        //StringBuffer testsql = new StringBuffer();


        String param1 = "";
        if(link01 != null && !"".equals(link01)) {
            if(link01.equals("LIKE")) {
                param1 = "'%" + pid01.trim() + "%'";
            } else {
                link01 = "=";
                param1 = "'" + pid01.trim() + "'";
            }
        }

        String param2 = "";
        if(link02 != null && !"".equals(link02)) {
            if(link02.equals("LIKE") || link02.equals("NOT LIKE")) {
                param2 = "'%" + pid02.trim() + "%'";

            } else if(link02.equals("NOT_EQUAL")) {
                link02 = "!=";
                param2 = "'" + pid02.trim() + "'";

            } else {
                link02 = "=";
                param2 = "'" + pid02.trim() + "'";
            }
        }

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID  ");
        temSql.append(" AND (  ");

        for(int i=1; i <= 20; i++) {
            if (i == 20) {
                //temSql.append(" (d." + spec01 + String.valueOf(i) + " " + gubun01 + " " + param1);
                //temSql.append(" AND d." + spec02 + String.valueOf(i) + " " + gubun02 + " " + param2 + ")");
                temSql.append(" d." + spec01 + String.valueOf(i) + " " + link01 + " " + param1);

            } else {
                temSql.append(" d." + spec01 + String.valueOf(i) + " " + link01 + " " + param1 + " OR ");
            }
        }

        temSql.append(" ) " + join + " (");

        for(int i=1; i <= 20; i++) {
            if (i == 20) {
                temSql.append(" d." + spec02 + String.valueOf(i) + " " + link02 + " " + param2 );

            } else {
                temSql.append(" d." + spec02 + String.valueOf(i) + " " + link02 + " " + param2 + " OR ");
            }
        }
        //temSql.append(" ) ");


        //System.out.println("makeQueryKey == " + temSql.toString());
        //System.out.println("makeQueryKeyV2 temSql== " + temSql.toString());
        return temSql;
    }


    /**
     * @apiNote PID의 SPEC, 조건값에 대한 한글명 조회
     * @param spec
     * @return
     */
    public static String findCodeName(String spec) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        String result = "";

        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                SELECT A.NAME AS CODE, A.TIT AS VAL
                FROM HDEL_SYSTEM.dosfld A
                --WHERE A.NAME = 'EL_ZORINO';
                WHERE A.NAME = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, spec);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String code = rs.getString("CODE"); //제품번호
                result = rs.getString("VAL");
            }

            System.out.println(result);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    /**
     * @apiNote PID코드의 라인 출력
     * @param paramPid
     */
    public static ArrayList<ArrayList<String>> findPIDLineView(String paramPid) {

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
                     FROM variant_d d, variant_h h, variant_id id
                     WHERE h.HOUID = id.LAST_HOUID AND h.HOUID =d.HOUID
                     AND H.PID = ?
                    """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, paramPid);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArrayList<String> row = new ArrayList<>();

                String PID = rs.getString("PID"); //제품번호
                String NO = rs.getString("NO") == null ? "" : rs.getString("NO");
                String ADDR = rs.getString("ADDR") == null ? "" : rs.getString("ADDR");
                String REMARKS = rs.getString("REMARKS") == null ? "" : rs.getString("REMARKS");
                String GOTO = rs.getString("GOTO") == null ? "" : rs.getString("GOTO");

                if(REMARKS != null && !"".equals(REMARKS)) {
                    REMARKS = REMARKS.trim();
                    REMARKS = REMARKS.replace("-", "");
                }


                //row.add(PID);
                //row.add(NO);
                row.add(ADDR);

                for (int i = 1; i <= 20; i++) {
                    String s = rs.getString("SPEC" + i);

                    if(s != null && !"".equals(s)) {
                        s = s.trim();
                        s = s.replace("-", "");
                    }

                    if (dupCheck.contains(s)) {
                        if(!"".equals(s)) {
                            String temp = codeMap.get(s.trim());
                            if(temp != null && !"".equals(temp) && !"null".equals(temp)) {
                                s += " \n" + "(" + codeMap.get(s.trim()) + ")";
                            }
                        }

                    } else {

                        if(!"".equals(s)) {


                            //System.out.println("s = " + s);
                            if (codeMap.containsKey(s.trim())) {
                                String temp = codeMap.get(s.trim());

                                //이미 있으면
                                if(temp != null && !"null".equals(temp) && !"".equals(temp)) {
                                    //System.out.println(s + "  111 codeMap = " + temp);
                                    s += " \n" + "(" + temp + ")";

                                }

                            } else {

                                //없으면
                                String val = "";
                                if (s.startsWith("EL_")) {
                                    val = SubaeCommonUtil.findCodeName(s); // 영업사양 코드 값 조회
                                    codeMap.put(s.trim(), val.trim());
                                }


                                if(val != null && !"null".equals(val) && !"".equals(val)) {
                                    //System.out.println(s + "  2222 = " + val);
                                    s += " \n" + "(" + val + ")";
                                }

                            }
                        }

                        dupCheck.add(s.trim());
                        //System.out.println("codeMap = " + codeMap);
                    }


                    String c = rs.getString("CON" + i);
                    if(c != null && !"".equals(c)) {
                        c = c.trim();
                        c = c.replace("-", "");
                    }
                    row.add(s);
                    row.add(c);
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
                }

                row.add(GOTO);
                row.add(REMARKS);

                result.add(row);
            }
            //System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    public static ArrayList<String> findPIDLineMaptify(String paramPid) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;


        ArrayList<String> result = new ArrayList<String>();

        //ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        HashSet<String> codeMap = new HashSet<>();
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
                            NVL(D.SPEC20, '-') AS SPEC20, NVL(D.CON20, '-') AS CON20
                     FROM variant_d d, variant_h h, variant_id id
                     WHERE h.HOUID = id.LAST_HOUID AND h.HOUID =d.HOUID
                     AND H.PID = ?
                    """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, paramPid);

            rs = pstmt.executeQuery();

            while(rs.next()) {

                String PID = rs.getString("PID"); //제품번호

                for (int i = 1; i <= 20; i++) {
                    String s = rs.getString("SPEC" + i);

                    if (codeMap.contains(s)) {

                    } else {
                        codeMap.add(s);
                        if(s != null && !"".equals(s)) {
                            s = s.trim();
                            s = s.replace("-", "");
                        }

                        if(!"".equals(s)) {
                            String val = SubaeCommonUtil.findCodeName(s); // 영업사양 코드 값 조회

                            if(val != null && !"".equals(val)) {

                                s += "(" + val + ")";

                                result.add(s);

                            }
                        }
                    }





                }

            }
            //System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }
}


