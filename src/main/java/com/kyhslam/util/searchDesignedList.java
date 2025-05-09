package com.kyhslam.util;

import javax.management.ServiceNotFoundException;
import javax.sql.PooledConnection;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class searchDesignedList {

    private HashMap specTitleMap = null;

    public searchDesignedList() throws ServiceNotFoundException
    {
        //msr = (MSR)SvServer.getServiceInstance("DF30MSR1");
    }

    public HashMap getSearchDesignedList(String productCls, String designPart, String specList, String startdate, String enddate,String npnccheck)
    {
        ArrayList head = new ArrayList();
        ArrayList data = new ArrayList();
        HashMap resultHm = new HashMap();

        PooledConnection xc          = null;
        Connection con        = null;
        PreparedStatement stat = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        StringBuffer sql = new StringBuffer();
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            String id = "hdel_default";
            String pass = "hdel_default";

            con = DriverManager.getConnection(url, id, pass);
            //con = DBconnectionInfo.getPDM_DBConnection();


            sql.append("  select woknum 호기번호,                                                                                                 ");
            sql.append("  md$desc 수주명,                                                                                                         ");
            sql.append(getSpecQuery(productCls, specList));
            sql.append("  crdat 최초설계일,                                                                                                       ");
            sql.append("  manager_m 기계담당자,                                                                                                   ");
            sql.append("  manager_e 전기담당자                                                                                                    ");
            sql.append("  from                                                                                                                    ");
            sql.append("  (                                                                                                                       ");
            sql.append("    select a.woknum,a.crdat,a.idnrk,a.menge,a.prctyp,a.dzeinr,                                                                        ");
            sql.append("            ROW_NUMBER() OVER(PARTITION BY a.woknum order by a.crdat desc) rn                                                 ");
            sql.append("    from  eai_bom_body a, eai_bom_return b                                                                                                          ");
            sql.append("    where                                                                                                                 ");
            sql.append("          (a.matkl = "+getBlocknoByDesginPart(designPart)+" and a.prctyp = 'A')                                               ");
            sql.append("      and a.crdat between '"+setstr(startdate.replaceAll("-", ""))+"' and '"+setstr(enddate.replaceAll("-", ""))+"'         ");
            sql.append("     and a.aennr=b.aennr and a.if_date= REGEXP_REPLACE(b.if_date, '[^[:digit:]]') and a.if_time = REGEXP_REPLACE(b.if_time, '[^[:digit:]]') and a.woknum=b.woknum and a.matnr=b.matnr and a.idnrk=b.idnrk  and b.exe_result='S' ");
            sql.append("    order by a.woknum,a.crdat,a.prctyp                                                                 					      ");
            sql.append("  ), "+getTableNameByProductCls(productCls)+"                                                                             ");
            sql.append("  where vf$ouid = id$wip                                                                                                  ");
            sql.append("    and md$number = woknum                                                                                                ");

            //if(!StringUtil.NVL(npnccheck,"off").equals("on"))
            //sql.append("    and md$number like '______L__'                                                                                                ");

            sql.append("    and rn = 1                                                                                                            ");

            stat = con.prepareStatement(sql.toString());
            rs = stat.executeQuery();
            rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();

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
            }

            resultHm.put("head", head);
            resultHm.put("data", data);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            //dyna.plmetc.util.DynaUtils.closeStatements(stat, rs);
            try {
                if (rs != null)
                    rs.close();
            }
            catch (SQLException ex) {}

            try {
                if (stat != null)
                    stat.close();
            }
            catch (SQLException ex) {}


            DTMUtil.closeSafely(null, con, xc);
            rs = null;
            stat = null;
            con = null;
            xc = null;
        }

        return resultHm;
    }

    private String getBlocknoByDesginPart(String designPart) throws Exception {

        if(designPart.equals("MD"))
            return "'A101A'";
        else if(designPart.equals("ED"))
            return "'A204A'";
        else
            throw new Exception("no designPart");
    }

    private String getTableNameByProductCls(String productCls) throws Exception{
        if(productCls.equals("2248993771"))
        {
            return " elv_info$vf, elv_info$id ";
        }
        else if(productCls.equals("2706224418"))
        {
            return " shipelv_info$vf, shipelv_info$id ";
        }
        else
            throw new Exception("no productId");
    }

    public String getSpecQuery(String productCls, String specList) throws Exception
    {

        HashMap hm = new HashMap();

        PooledConnection   xc          = null;
        Connection         con        = null;
        PreparedStatement stat = null;
        ResultSet  rs = null;
        StringBuffer sql = new StringBuffer();
        StringBuffer mainSQL = new StringBuffer();

        LineNumberReader textlnr = new LineNumberReader(new StringReader(specList));
        String specs = "";

        specTitleMap = new HashMap();

        while(true)
        {
            String spec = textlnr.readLine();
            if (spec == null) break;
            specs += ",'"+spec.trim()+"'";
        }

        if(!specs.equals(""))
        {
            specs = specs.substring(1);
        }
        else
        {
            return "";
        }


        try
        {
            //con = DBconnectionInfo.getPDM_DBConnection();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@newplm-prod-db.cdqjoonblwol.ap-northeast-2.rds.amazonaws.com:1521/PLMPRD";
            String id = "hdel_default";
            String pass = "hdel_default";

            con = DriverManager.getConnection(url, id, pass);

            sql.append(" SELECT UPPER(CODE) NAME, TIT, MSRTITLECODE, TYPE                               ");
            sql.append(" FROM hdel_system.dosfld                                             ");
            sql.append(" WHERE DOSCLAS in (2248973239, " + productCls + ") AND UPPER(code) IN (" + specs.toUpperCase() + ")              ");
            //sql.append(" order by name                                                      ");

            stat = con.prepareStatement(sql.toString());

            rs = stat.executeQuery();

            while(rs.next())
            {
                HashMap row = new HashMap();
                row.put("NAME",rs.getString("NAME"));
                row.put("TIT",rs.getString("TIT"));
                row.put("MSRTITLECODE",rs.getString("MSRTITLECODE"));
                row.put("TYPE",rs.getString("TYPE"));

                hm.put(rs.getString("NAME"), row);


            }
        }
        catch(Exception e)
        {
            throw e;
        }
        finally
        {
            try {
                if (rs != null)
                    rs.close();
            }
            catch (SQLException ex) {}

            try {
                if (stat != null)
                    stat.close();
            }
            catch (SQLException ex) {}
            DTMUtil.closeSafely(null, con, xc);
            rs = null;
            stat = null;
            con = null;
            xc = null;
        }

        textlnr = new LineNumberReader(new StringReader(specList));
        specs = "";

        while(true)
        {
            String spec = textlnr.readLine();
            if (spec == null) break;
            HashMap row = (HashMap) hm.get(spec.toUpperCase().trim());

            if(row != null)
            {
                String NAME = (String) row.get("NAME");
                String TITLE = (String) row.get("TIT");
                String MSRTITLECODE = (String) row.get("MSRTITLECODE");
                String TYPE = (String) row.get("TYPE");

                if(MSRTITLECODE != null && !MSRTITLECODE.equals("")) // msrcode가 있으면 읽어옴
                {
                    //TITLE = msr.getStgrepString(MSRTITLECODE,TITLE,3);
                }



                if(TITLE == null || TITLE.trim().equals("")) // TIT 이 없으면 NAME을 그대로 사용
                {
                    TITLE = NAME;
                }

                specTitleMap.put(NAME,TITLE);


                if(TYPE.equals("24") || TYPE.equals("25")) // 타입이 코드형태일 때
                {
                    mainSQL.append(" (select des from doscoditm where ouid="+NAME+") \""+NAME+"\", ");
                }
                else
                {
                    mainSQL.append(" "+NAME+" \""+NAME+"\", ");
                }
            }
        }


        return mainSQL.toString();
    }

    public String getTitle(String specName)
    {
        String name = null;
        if(specTitleMap != null)
        {
            name = (String) specTitleMap.get(specName);
            if(name == null)
            {
                name = specName;
            }
        }
        else
        {
            name = specName;
        }

        return name;
    }

    public HashMap getTitleMap()
    {
        return specTitleMap;
    }

    public String setstr(String col)
    {
        String tmp = col.toLowerCase();
        if(tmp.contains("select")  || tmp.contains("insert") || tmp.contains("update") || tmp.contains("delete") || tmp.contains(";"))
            return "";
        else
            return col;
    }
}
