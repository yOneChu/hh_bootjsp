package com.kyhslam.util;

import javax.sql.PooledConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class searchListBasedOnCondition {
    public static HashMap searchListBasedOnCondition(String G_L_CODE, String EL_ATYP, String EL_ASPD_1, String EL_ASPD_2, String EL_AMAN_1, String EL_AMAN_2,
                                                     String EL_ECSF, String EL_ETM, String EL_ECJJ_1, String EL_ECJJ_2, String PARTNO, String BLOCKNO, String start_date_day, String end_date_day, String specList, boolean isOnlyElv, boolean isgeneralElv) throws Exception
    {
        System.out.println("### SearchListOfBasedCondition : get Search List Based On Condition...");

        ArrayList head = new ArrayList();
        ArrayList data = new ArrayList();
        HashMap resultHm = new HashMap();

        PooledConnection xc    = null;
        Connection con         = null;
        PreparedStatement stat = null;
        ResultSet rs           = null;
        ResultSetMetaData rsmd = null;
        StringBuffer sql       = new StringBuffer();
        String[] PARTNO_ARRAY = null;
        String[] EL_ASPD_ARRAY = null;
        String[] EL_AMAN_ARRAY = null;
        String[] EL_ATYP_ARRAY = null;
        String[] EL_ECJJ_ARRAY = null;
        String[] BLOCKNO_ARRAY = null;

        searchDesignedList SDL = new searchDesignedList();

        try
        {
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            //String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            //String id = "hdel_default";
            //String pass = "hdel_default";

            //con = DriverManager.getConnection(url, id, pass);
            con = PLMDBConnection.getConnection();

            sql.append(" select ");
            sql.append(" crdat ERP전송일자, ");
            sql.append(" woknum 공사번호, ");
            sql.append(" decode(bom.matnr,bom.woknum,'*', bom.matnr) 상위자재, ");
            sql.append(" idnrk 자재번호, ");
            sql.append(" menge 수량, ");
            sql.append(" dzeinr 도면번호, ");
            sql.append(" matkl 블럭번호, ");
            sql.append(" md$desc 공사명, ");

            sql.append(SDL.getSpecQuery("2248993771", specList));

            sql.append(" c.manager_m 기계담당자, ");
            sql.append(" c.manager_e 전기담당자");
            sql.append(" from ( ");
            sql.append(" select crdat,woknum,matnr, idnrk,menge,dzeinr,matkl from ");
            sql.append(" ( ");
            sql.append(" select /*+ INDEX_COMBINE(A) */ a.*,row_number() over(partition by woknum,matnr,idnrk,matkl order by nvl(a.wokver,-1) desc, a.crdat desc, a.seqno desc) rn from eai_bom a, ");
            sql.append(" (select woknum first_woknum,matnr first_matnr, idnrk first_idnrk,matkl first_matkl ");
            sql.append(" from eai_bom first   ");
            sql.append(" where ");

            if(PARTNO != null && !PARTNO.equals(""))
            {
                sql.append(" ( ");

                if (PARTNO.indexOf(",") == -1) {
                    //System.out.println("!!!!!PARTNO_ARRAY_COUNT_ONLY_ONE!!!!!!" + PARTNO.trim() + "!!!!!!!!");

                    if (PARTNO.contains("%")) {
                        sql.append(" idnrk like '"+PARTNO.trim()+"' ");
                    }else {
                        sql.append(" idnrk = '"+PARTNO.trim()+"' ");
                    }
                }
                else {
                    PARTNO = PARTNO.replace("\r\n","").replace("\n","");
                    PARTNO_ARRAY = PARTNO.split(",");

                    //System.out.println("!!!!!PARTNO_ARRAY_COUNT_MANY!!!!!!" + PARTNO_ARRAY.length + "!!!!!!!!");

                    ArrayList partNo_normal = new ArrayList();
                    ArrayList partNo_like = new ArrayList();

                    // like문 기본문 구분
                    for(int i = 0; i< PARTNO_ARRAY.length; i++) {
                        String partno = PARTNO_ARRAY[i].trim();

                        // 공백 및 오류 데이터는 pass
                        if ("".equals(partno) || partno.length() > 20) continue;

                        // LIKE문 (%는 마지막에만 사용 가능)
                        if ("%".equals(partno.substring(partno.length()-1))) {
                            partNo_like.add(partno);
                        }
                        // 기본문
                        else {
                            partNo_normal.add(partno);
                        }
                    }

                    // 기본문 셋팅
                    if (partNo_normal.size() > 0) {
                        sql.append(" idnrk in (");
                        sql.append(" '" + partNo_normal.get(0) + "' ");
                        if (partNo_normal.size() > 1) {
                            for(int i = 1; i < partNo_normal.size(); i++) {
                                sql.append(" , '" + partNo_normal.get(i) + "' ");
                            }
                        }
                        sql.append(" )");
                    }

                    // LIKE문 셋팅
                    if (partNo_like.size() > 0) {
                        if (partNo_normal.size() > 0) {
                            sql.append(" or ");
                        }
                        sql.append(" idnrk like '" + partNo_like.get(0) + "'");
                        if (partNo_like.size() > 1) {
                            for(int i = 1; i < partNo_like.size(); i++) {
                                sql.append(" or idnrk like '" + partNo_like.get(i) + "'");
                            }
                        }
                    }
                }
                sql.append(" ) and ");
            }

            if(G_L_CODE != null && !G_L_CODE.equals(""))
                sql.append(" dzeinr like '"+G_L_CODE+"%' and ");
            if(BLOCKNO != null && !BLOCKNO.equals(""))
            {
                if(BLOCKNO.contains(","))
                {
                    BLOCKNO = BLOCKNO.replace("\r\n","");
                    BLOCKNO = BLOCKNO.replace("\n","");
                    BLOCKNO_ARRAY = BLOCKNO.split(",");
                    for(int i = 0; i< BLOCKNO_ARRAY.length; i++)
                    {
                        //System.out.println("!!!!!BLOCKNO_ARRAY!!!!!!"+i+"!!!!!!!!"+BLOCKNO_ARRAY[i].trim());
                    }
                    sql.append(" matkl in ( ");

                    for(int i = 0; i < BLOCKNO_ARRAY.length; i++)
                    {
                        if(i == BLOCKNO_ARRAY.length - 1)
                        {
                            sql.append("'"+BLOCKNO_ARRAY[i].trim()+"' ) and ");
                        }
                        else
                        {
                            sql.append("'"+BLOCKNO_ARRAY[i].trim()+"', ");
                        }
                    }
                }
                else
                {
                    sql.append(" matkl = '"+BLOCKNO.trim()+"' and ");
                }
            }
            sql.append(" prctyp in ('A','N') and ");
            sql.append(" crdat between '"+start_date_day+"' and '"+end_date_day+"' and tp_status = 'T' and woknum not like 'TEST-%' ");
            if(isgeneralElv == true)
            {
                sql.append(" and woknum like '______L__' ");
            }
            sql.append(" ) s"); // 기간내 등록된 자재들을 먼저 탐색
            sql.append(" where a.woknum = s.first_woknum ");
            sql.append(" and   a.matnr = s.first_matnr ");
            sql.append(" and   a.idnrk = s.first_idnrk ");
            sql.append(" and   a.matkl = s.first_matkl ");
            sql.append(" and   (A.CTYP is null or a.ctyp <> 'T') "); // 주석만 바뀐 레코드는 필요없음.
            sql.append(" and   a.tp_status='T' ");
            sql.append(" )where rn=1 "); // woknum, idnrk, matkl 의 최신데이터를 뽑음
            sql.append(" and prctyp<>'D' "); // 최신 데이터가 D 플래그이면 무시
            sql.append(" and tp_status='T' ");
            sql.append(" ) bom ");

            if(isOnlyElv == false)
            {
                sql.append(" left outer  ");
            }
            sql.append(" join elv_info$vf c ");
            sql.append(" on bom.woknum = c.md$number ");
            sql.append(" where ");
            sql.append(" c.vf$ouid is null or ");
            sql.append(" ( ");
            sql.append(" c.vf$ouid in (select id$last from elv_info$id where id$ouid=vf$identity) ");


            if(EL_ATYP != null && !EL_ATYP.equals(""))
            {
                if(EL_ATYP.contains(","))
                {
                    EL_ATYP_ARRAY = EL_ATYP.split(",");

                    sql.append(" and c.EL_ATYP in (select ouid from doscoditm where ");

                    for(int i = 0; i < EL_ATYP_ARRAY.length; i++)
                    {
                        sql.append(" des like  '%"+EL_ATYP_ARRAY[i].trim()+"%' ");

                        if(i < EL_ATYP_ARRAY.length-1)
                        {
                            sql.append(" or ");
                        }
                    }

                    sql.append(" ) ");
                }
                else
                {
                    sql.append(" and c.EL_ATYP in (select ouid from doscoditm where des like '%"+EL_ATYP.trim()+"%') ");
                }
            }

            if(EL_ASPD_1 != null && !EL_ASPD_1.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ASPD),null,0,cod(c.EL_ASPD))) >= "+EL_ASPD_1.trim()+" ");
            }
            if(EL_ASPD_2 != null && !EL_ASPD_2.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ASPD),null,0,cod(c.EL_ASPD))) <= "+EL_ASPD_2.trim()+" ");
            }

            if(EL_AMAN_1 != null && !EL_AMAN_1.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_AMAN),null,0,' ',0,' 11',0,' 21',0,' 24',0,' 26',0,'08',0,'09',0,'13M',0,'17L',0,c.EL_AMAN)) >= "+EL_AMAN_1.trim()+" ");
            }
            if(EL_AMAN_2 != null && !EL_AMAN_2.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_AMAN),null,0,' ',0,' 11',0,' 21',0,' 24',0,' 26',0,'08',0,'09',0,'13M',0,'17L',0,c.EL_AMAN)) <= "+EL_AMAN_2.trim()+" ");
            }

            if(EL_ECSF != null && !EL_ECSF.equals(""))
                sql.append(" and c.EL_ECSF in (select ouid from doscoditm where des like '%"+EL_ECSF.trim()+"%') ");
            if(EL_ETM != null && !EL_ETM.equals(""))
                sql.append(" and c.EL_ETM in (select ouid from doscoditm where des like '%"+EL_ETM.trim()+"%') ");

            if(EL_ECJJ_1 != null && !EL_ECJJ_1.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ECJJ),null,0,c.EL_ECJJ)) >= "+EL_ECJJ_1.trim()+" ");
            }
            if(EL_ECJJ_2 != null && !EL_ECJJ_2.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ECJJ),null,0,c.EL_ECJJ)) <= "+EL_ECJJ_2.trim()+" ");
            }

            sql.append(" ) ");
            sql.append(" union ");
            sql.append(" select ");
            sql.append(" crdat ERP전송일자, ");
            sql.append(" woknum 공사번호, ");
            sql.append(" decode(bom.matnr,bom.woknum,'*', bom.matnr) 상위자재, ");
            sql.append(" idnrk 자재번호, ");
            sql.append(" menge 수량, ");
            sql.append(" dzeinr 도면번호, ");
            sql.append(" matkl 블럭번호, ");
            sql.append(" md$desc 공사명, ");

            sql.append(SDL.getSpecQuery("2248993771", specList));

            sql.append(" c.manager_m 기계담당자, ");
            sql.append(" c.manager_e 전기담당자");
            sql.append(" from ( ");
            sql.append(" select crdat,woknum,matnr, idnrk,menge,dzeinr,matkl from ");
            sql.append(" ( ");
            sql.append(" select /*+ INDEX_COMBINE(A) */ a.*,row_number() over(partition by woknum,matnr,idnrk,matkl order by nvl(a.wokver,-1) desc, a.crdat desc, a.seqno desc) rn from eai_bom_body a, ");
            sql.append(" (select woknum first_woknum,matnr first_matnr, idnrk first_idnrk,matkl first_matkl ");
            sql.append(" from eai_bom_body first   ");
            sql.append(" where ");
            if(PARTNO != null && !PARTNO.equals(""))
            {
                sql.append(" ( ");

                if (PARTNO.indexOf(",") == -1) {
                    if (PARTNO.contains("%")) {
                        sql.append(" idnrk like '"+PARTNO.trim()+"' ");
                    }else {
                        sql.append(" idnrk = '"+PARTNO.trim()+"' ");
                    }
                }
                else {
                    PARTNO = PARTNO.replace("\r\n","").replace("\n","");
                    PARTNO_ARRAY = PARTNO.split(",");

                    ArrayList partNo_normal = new ArrayList();
                    ArrayList partNo_like = new ArrayList();

                    // like문 기본문 구분
                    for(int i = 0; i< PARTNO_ARRAY.length; i++) {
                        String partno = PARTNO_ARRAY[i].trim();

                        // 공백 및 오류 데이터는 pass
                        if ("".equals(partno) || partno.length() > 20) continue;

                        // LIKE문 (%는 마지막에만 사용 가능)
                        if ("%".equals(partno.substring(partno.length()-1))) {
                            partNo_like.add(partno);
                        }
                        // 기본문
                        else {
                            partNo_normal.add(partno);
                        }
                    }

                    // 기본문 셋팅
                    if (partNo_normal.size() > 0) {
                        sql.append(" idnrk in (");
                        sql.append(" '" + partNo_normal.get(0) + "' ");
                        if (partNo_normal.size() > 1) {
                            for(int i = 1; i < partNo_normal.size(); i++) {
                                sql.append(" , '" + partNo_normal.get(i) + "' ");
                            }
                        }
                        sql.append(" )");
                    }

                    // LIKE문 셋팅
                    if (partNo_like.size() > 0) {
                        if (partNo_normal.size() > 0) {
                            sql.append(" or ");
                        }
                        sql.append(" idnrk like '" + partNo_like.get(0) + "'");
                        if (partNo_like.size() > 1) {
                            for(int i = 1; i < partNo_like.size(); i++) {
                                sql.append(" or idnrk like '" + partNo_like.get(i) + "'");
                            }
                        }
                    }
                }
                sql.append(" ) and ");
            }

            if(G_L_CODE != null && !G_L_CODE.equals(""))
                sql.append(" dzeinr like '"+G_L_CODE+"%' and ");
            if(BLOCKNO != null && !BLOCKNO.equals(""))
            {
                if(BLOCKNO.contains(","))
                {
                    BLOCKNO = BLOCKNO.replace("\r\n","");
                    BLOCKNO = BLOCKNO.replace("\n","");
                    BLOCKNO_ARRAY = BLOCKNO.split(",");
                    for(int i = 0; i< BLOCKNO_ARRAY.length; i++)
                    {
                        //System.out.println("!!!!!BLOCKNO_ARRAY!!!!!!"+i+"!!!!!!!!"+BLOCKNO_ARRAY[i].trim());
                    }
                    sql.append(" matkl in ( ");

                    for(int i = 0; i < BLOCKNO_ARRAY.length; i++)
                    {
                        if(i == BLOCKNO_ARRAY.length - 1)
                        {
                            sql.append("'"+BLOCKNO_ARRAY[i].trim()+"' ) and ");
                        }
                        else
                        {
                            sql.append("'"+BLOCKNO_ARRAY[i].trim()+"', ");
                        }
                    }
                }
                else
                {
                    sql.append(" matkl = '"+BLOCKNO.trim()+"' and ");
                }
            }
            sql.append(" prctyp in ('A','N') and ");
            sql.append(" crdat between '"+start_date_day+"' and '"+end_date_day+"' and woknum not like 'TEST-%' ");
            if(isgeneralElv == true)
            {
                sql.append(" and woknum like '______L__' ");
            }
            sql.append(" ) s"); // 기간내 등록된 자재들을 먼저 탐색
            sql.append(" where a.woknum = s.first_woknum ");
            sql.append(" and   a.matnr = s.first_matnr ");
            sql.append(" and   a.idnrk = s.first_idnrk ");
            sql.append(" and   a.matkl = s.first_matkl ");
            sql.append(" and   (A.CTYP is null or a.ctyp <> 'T') "); // 주석만 바뀐 레코드는 필요없음.
            sql.append(" )where rn=1 "); // woknum, idnrk, matkl 의 최신데이터를 뽑음
            sql.append(" and prctyp<>'D' "); // 최신 데이터가 D 플래그이면 무시
            sql.append(" ) bom ");

            if(isOnlyElv == false)
            {
                sql.append(" left outer  ");
            }
            sql.append(" join elv_info$vf c ");
            sql.append(" on bom.woknum = c.md$number ");
            sql.append(" where ");
            sql.append(" c.vf$ouid is null or ");
            sql.append(" ( ");
            sql.append(" c.vf$ouid in (select id$last from elv_info$id where id$ouid=vf$identity) ");


            if(EL_ATYP != null && !EL_ATYP.equals(""))
            {
                if(EL_ATYP.contains(","))
                {
                    EL_ATYP_ARRAY = EL_ATYP.split(",");

                    sql.append(" and c.EL_ATYP in (select ouid from doscoditm where ");

                    for(int i = 0; i < EL_ATYP_ARRAY.length; i++)
                    {
                        sql.append(" des like  '%"+EL_ATYP_ARRAY[i].trim()+"%' ");

                        if(i < EL_ATYP_ARRAY.length-1)
                        {
                            sql.append(" or ");
                        }
                    }

                    sql.append(" ) ");
                }
                else
                {
                    sql.append(" and c.EL_ATYP in (select ouid from doscoditm where des like '%"+EL_ATYP.trim()+"%') ");
                }
            }

            if(EL_ASPD_1 != null && !EL_ASPD_1.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ASPD),null,0,cod(c.EL_ASPD))) >= "+EL_ASPD_1.trim()+" ");
            }
            if(EL_ASPD_2 != null && !EL_ASPD_2.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ASPD),null,0,cod(c.EL_ASPD))) <= "+EL_ASPD_2.trim()+" ");
            }

            if(EL_AMAN_1 != null && !EL_AMAN_1.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_AMAN),null,0,' ',0,' 11',0,' 21',0,' 24',0,' 26',0,'08',0,'09',0,'13M',0,'17L',0,c.EL_AMAN)) >= "+EL_AMAN_1.trim()+" ");
            }
            if(EL_AMAN_2 != null && !EL_AMAN_2.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_AMAN),null,0,' ',0,' 11',0,' 21',0,' 24',0,' 26',0,'08',0,'09',0,'13M',0,'17L',0,c.EL_AMAN)) <= "+EL_AMAN_2.trim()+" ");
            }

            if(EL_ECSF != null && !EL_ECSF.equals(""))
                sql.append(" and c.EL_ECSF in (select ouid from doscoditm where des like '%"+EL_ECSF.trim()+"%') ");
            if(EL_ETM != null && !EL_ETM.equals(""))
                sql.append(" and c.EL_ETM in (select ouid from doscoditm where des like '%"+EL_ETM.trim()+"%') ");

            if(EL_ECJJ_1 != null && !EL_ECJJ_1.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ECJJ),null,0,c.EL_ECJJ)) >= "+EL_ECJJ_1.trim()+" ");
            }
            if(EL_ECJJ_2 != null && !EL_ECJJ_2.equals("")) {
                sql.append(" and to_number(decode(trim(c.EL_ECJJ),null,0,c.EL_ECJJ)) <= "+EL_ECJJ_2.trim()+" ");
            }

            sql.append(" ) ");

            stat = con.prepareStatement(sql.toString());



            //System.out.println(sql.toString());

            rs = stat.executeQuery();
            rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();

            int mengeSum = 0;

            for(int i=1; i<=columnCount; i++)
            {
                head.add(rsmd.getColumnName(i));
            }

            while(rs.next())
            {
                ArrayList row = new ArrayList();
                for(int i=1; i<=columnCount; i++)
                {
                    row.add(rs.getString(i));
                }
                data.add(row);

                //mengeSum += StringUtil.parseDouble(rs.getString("수량"));
                mengeSum += Double.parseDouble(rs.getString("수량"));
            }

            resultHm.put("head", head);
            resultHm.put("data", data);
            resultHm.put("mengeSum", String.valueOf(mengeSum));

        }

        catch (SQLException e)
        {
            e.printStackTrace();
            try {
                DTMUtil.sqlExceptionHelper(con, e);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } // rollback
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            /*try {
                if (rs != null)
                    rs.close();
            }
            catch (SQLException ex) {}

            DTMUtil.closeSafely(stat, con, xc);
            stat = null;
            con = null;
            xc = null;*/
            PLMDBConnection.disconnect(con, stat, rs);
        }
        return resultHm;
    }
}
