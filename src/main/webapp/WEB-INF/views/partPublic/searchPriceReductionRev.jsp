<%@page import="java.time.LocalDate"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.kyhslam.service.JdbcTestService" %>
<%@ page import="com.kyhslam.dto.DashDtoV2" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //부품공용화 집계 - 대수
    // searchPriceReductionRev.jsp
    // http://10.225.4.20/jsp/searchLogic/searchPriceReductionRev.jsp
    // http://localhost/jsp/searchLogic/searchPriceReductionRev.jsp

    // ServletContext에서 Spring WebApplicationContext 얻기
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // 원하는 Bean 가져오기
    JdbcTestService jdcbService = (JdbcTestService) context.getBean("JdbcTestService");

    String contextPath = request.getContextPath();
    System.out.println("--- searchPriceReductionRev.jsp ---");

    ArrayList<HashMap<String, String>> dashList = new ArrayList<HashMap<String, String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;


    //2단계 CP(MRL_일반)
    String cp2_MRL_General = "";
    String cp2_MRL_General_05 = "";
    String cp2_MRL_General_06 = "";
    String cp2_MRL_General_07 = "";
    String cp2_MRL_General_08 = "";
    String cp2_MRL_General_09 = "";
    String cp2_MRL_General_10 = "";
    String cp2_MRL_General_11 = "";
    String cp2_MRL_General_12 = "";
    String cp2_MRL_General_13 = "";
    String cp2_MRL_General_14 = "";
    String cp2_MRL_General_15 = "";
    String cp2_MRL_General_16 = "";
    String cp2_MRL_General_17 = "";


    //2단계 CP(MRL_회생)
    String cp2_MRL_Revive = "";
    String cp2_MRL_Revive_05 = "";
    String cp2_MRL_Revive_06 = "";
    String cp2_MRL_Revive_07 = "";
    String cp2_MRL_Revive_08 = "";
    String cp2_MRL_Revive_09 = "";
    String cp2_MRL_Revive_10 = "";
    String cp2_MRL_Revive_11 = "";
    String cp2_MRL_Revive_12 = "";
    String cp2_MRL_Revive_13 = "";
    String cp2_MRL_Revive_14 = "";
    String cp2_MRL_Revive_15 = "";
    String cp2_MRL_Revive_16 = "";
    String cp2_MRL_Revive_17 = "";


    //2단계 CP(MR_일반)
    String cp2_MR_General = "";
    String cp2_MR_General_05 = "";
    String cp2_MR_General_06 = "";
    String cp2_MR_General_07 = "";
    String cp2_MR_General_08 = "";
    String cp2_MR_General_09 = "";
    String cp2_MR_General_10 = "";
    String cp2_MR_General_11 = "";
    String cp2_MR_General_12 = "";
    String cp2_MR_General_13 = "";
    String cp2_MR_General_14 = "";
    String cp2_MR_General_15 = "";
    String cp2_MR_General_16 = "";
    String cp2_MR_General_17 = "";


    //2단계 CP(MR_회생)
    String cp2_MR_Revive = "";
    String cp2_MR_Revive_05 = "";
    String cp2_MR_Revive_06 = "";
    String cp2_MR_Revive_07 = "";
    String cp2_MR_Revive_08 = "";
    String cp2_MR_Revive_09 = "";
    String cp2_MR_Revive_10 = "";
    String cp2_MR_Revive_11 = "";
    String cp2_MR_Revive_12 = "";
    String cp2_MR_Revive_13 = "";
    String cp2_MR_Revive_14 = "";
    String cp2_MR_Revive_15 = "";
    String cp2_MR_Revive_16 = "";
    String cp2_MR_Revive_17 = "";


    String cpMRL_5 = "";
    String cpMRL_5_05 = "";
    String cpMRL_5_06 = "";
    String cpMRL_5_07 = "";
    String cpMRL_5_08 = "";
    String cpMRL_5_09 = "";
    String cpMRL_5_10 = "";
    String cpMRL_5_11 = "";
    String cpMRL_5_12 = "";
    String cpMRL_5_13 = "";
    String cpMRL_5_14 = "";
    String cpMRL_5_15 = "";
    String cpMRL_5_16 = "";
    String cpMRL_5_17 = "";

    String cpMRL_9 = "";
    String cpMRL_9_05 = "";
    String cpMRL_9_06 = "";
    String cpMRL_9_07 = "";
    String cpMRL_9_08 = "";
    String cpMRL_9_09 = "";
    String cpMRL_9_10 = "";
    String cpMRL_9_11 = "";
    String cpMRL_9_12 = "";
    String cpMRL_9_13 = "";
    String cpMRL_9_14 = "";
    String cpMRL_9_15 = "";
    String cpMRL_9_16 = "";
    String cpMRL_9_17 = "";

    String cpMRL_14 = "";
    String cpMRL_14_05 = "";
    String cpMRL_14_06 = "";
    String cpMRL_14_07 = "";
    String cpMRL_14_08 = "";
    String cpMRL_14_09 = "";
    String cpMRL_14_10 = "";
    String cpMRL_14_11 = "";
    String cpMRL_14_12 = "";
    String cpMRL_14_13 = "";
    String cpMRL_14_14 = "";
    String cpMRL_14_15 = "";
    String cpMRL_14_16 = "";
    String cpMRL_14_17 = "";

    String cpMRL_17 = "";
    String cpMRL_17_05 = "";
    String cpMRL_17_06 = "";
    String cpMRL_17_07 = "";
    String cpMRL_17_08 = "";
    String cpMRL_17_09 = "";
    String cpMRL_17_10 = "";
    String cpMRL_17_11 = "";
    String cpMRL_17_12 = "";
    String cpMRL_17_13 = "";
    String cpMRL_17_14 = "";
    String cpMRL_17_15 = "";
    String cpMRL_17_16 = "";
    String cpMRL_17_17 = "";

    String cpMR_5 = "";
    String cpMR_5_05 = "";
    String cpMR_5_06 = "";
    String cpMR_5_07 = "";
    String cpMR_5_08 = "";
    String cpMR_5_09 = "";
    String cpMR_5_10 = "";
    String cpMR_5_11 = "";
    String cpMR_5_12 = "";
    String cpMR_5_13 = "";
    String cpMR_5_14 = "";
    String cpMR_5_15 = "";
    String cpMR_5_16 = "";
    String cpMR_5_17 = "";

    String cpMR_9 = "";
    String cpMR_9_05 = "";
    String cpMR_9_06 = "";
    String cpMR_9_07 = "";
    String cpMR_9_08 = "";
    String cpMR_9_09 = "";
    String cpMR_9_10 = "";
    String cpMR_9_11 = "";
    String cpMR_9_12 = "";
    String cpMR_9_13 = "";
    String cpMR_9_14 = "";
    String cpMR_9_15 = "";
    String cpMR_9_16 = "";
    String cpMR_9_17 = "";

    String cpMR_14 = "";
    String cpMR_14_05 = "";
    String cpMR_14_06 = "";
    String cpMR_14_07 = "";
    String cpMR_14_08 = "";
    String cpMR_14_09 = "";
    String cpMR_14_10 = "";
    String cpMR_14_11 = "";
    String cpMR_14_12 = "";
    String cpMR_14_13 = "";
    String cpMR_14_14 = "";
    String cpMR_14_15 = "";
    String cpMR_14_16 = "";
    String cpMR_14_17 = "";

    String cpMR_17 = "";
    String cpMR_17_05 = "";
    String cpMR_17_06 = "";
    String cpMR_17_07 = "";
    String cpMR_17_08 = "";
    String cpMR_17_09 = "";
    String cpMR_17_10 = "";
    String cpMR_17_11 = "";
    String cpMR_17_12 = "";
    String cpMR_17_13 = "";
    String cpMR_17_14 = "";
    String cpMR_17_15 = "";
    String cpMR_17_16 = "";
    String cpMR_17_17 = "";

    String TM = "";
    String TM_05 = "";
    String TM_06 = "";
    String TM_07 = "";
    String TM_08 = "";
    String TM_09 = "";
    String TM_10 = "";
    String TM_11 = "";
    String TM_12 = "";
    String TM_13 = "";
    String TM_14 = "";
    String TM_15 = "";
    String TM_16 = "";
    String TM_17 = "";

    String TMRope = "";
    String TMRope_05 = "";
    String TMRope_06 = "";
    String TMRope_07 = "";
    String TMRope_08 = "";
    String TMRope_09 = "";
    String TMRope_10 = "";
    String TMRope_11 = "";
    String TMRope_12 = "";
    String TMRope_13 = "";
    String TMRope_14 = "";
    String TMRope_15 = "";
    String TMRope_16 = "";
    String TMRope_17 = "";


    String CARTOP = "";
    String CARTOP_05 = "";
    String CARTOP_06 = "";
    String CARTOP_07 = "";
    String CARTOP_08 = "";
    String CARTOP_09 = "";
    String CARTOP_10 = "";
    String CARTOP_11 = "";
    String CARTOP_12 = "";
    String CARTOP_13 = "";
    String CARTOP_14 = "";
    String CARTOP_15 = "";
    String CARTOP_16 = "";
    String CARTOP_17 = "";

    String GOV = "";
    String GOV_05 = "";
    String GOV_06 = "";
    String GOV_07 = "";
    String GOV_08 = "";
    String GOV_09 = "";
    String GOV_10 = "";
    String GOV_11 = "";
    String GOV_12 = "";
    String GOV_13 = "";
    String GOV_14 = "";
    String GOV_15 = "";
    String GOV_16 = "";
    String GOV_17 = "";

    String LAMP = "";
    String LAMP_05 = "";
    String LAMP_06 = "";
    String LAMP_07 = "";
    String LAMP_08 = "";
    String LAMP_09 = "";
    String LAMP_10 = "";
    String LAMP_11 = "";
    String LAMP_12 = "";
    String LAMP_13 = "";
    String LAMP_14 = "";
    String LAMP_15 = "";
    String LAMP_16 = "";
    String LAMP_17 = "";

    String HPB = "";
    String HPB_05 = "";
    String HPB_06 = "";
    String HPB_07 = "";
    String HPB_08 = "";
    String HPB_09 = "";
    String HPB_10 = "";
    String HPB_11 = "";
    String HPB_12 = "";
    String HPB_13 = "";
    String HPB_14 = "";
    String HPB_15 = "";
    String HPB_16 = "";
    String HPB_17 = "";

    String HIP = "";
    String HIP_05 = "";
    String HIP_06 = "";
    String HIP_07 = "";
    String HIP_08 = "";
    String HIP_09 = "";
    String HIP_10 = "";
    String HIP_11 = "";
    String HIP_12 = "";
    String HIP_13 = "";
    String HIP_14 = "";
    String HIP_15 = "";
    String HIP_16 = "";
    String HIP_17 = "";

    String HPI = "";
    String HPI_05 = "";
    String HPI_06 = "";
    String HPI_07 = "";
    String HPI_08 = "";
    String HPI_09 = "";
    String HPI_10 = "";
    String HPI_11 = "";
    String HPI_12 = "";
    String HPI_13 = "";
    String HPI_14 = "";
    String HPI_15 = "";
    String HPI_16 = "";
    String HPI_17 = "";

    String PIT = "";
    String PIT_05 = "";
    String PIT_06 = "";
    String PIT_07 = "";
    String PIT_08 = "";
    String PIT_09 = "";
    String PIT_10 = "";
    String PIT_11 = "";
    String PIT_12 = "";
    String PIT_13 = "";
    String PIT_14 = "";
    String PIT_15 = "";
    String PIT_16 = "";
    String PIT_17 = "";

    //opb_D521AG
    String OPB_D521AG = "";
    String OPB_D521AG_05 = "";
    String OPB_D521AG_06 = "";
    String OPB_D521AG_07 = "";
    String OPB_D521AG_08 = "";
    String OPB_D521AG_09 = "";
    String OPB_D521AG_10 = "";
    String OPB_D521AG_11 = "";
    String OPB_D521AG_12 = "";
    String OPB_D521AG_13 = "";
    String OPB_D521AG_14 = "";
    String OPB_D521AG_15 = "";
    String OPB_D521AG_16 = "";
    String OPB_D521AG_17 = "";

    //opb_S521A
    String OPB_S521A = "";
    String OPB_S521A_05 = "";
    String OPB_S521A_06 = "";
    String OPB_S521A_07 = "";
    String OPB_S521A_08 = "";
    String OPB_S521A_09 = "";
    String OPB_S521A_10 = "";
    String OPB_S521A_11 = "";
    String OPB_S521A_12 = "";
    String OPB_S521A_13 = "";
    String OPB_S521A_14 = "";
    String OPB_S521A_15 = "";
    String OPB_S521A_16 = "";
    String OPB_S521A_17 = "";





    LocalDate now = LocalDate.now();
    String todayVal = now.toString();


    try {

        /*String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
        String id = "SA";
        String pw = "AutodeskVault@26200"; // "qwe123!@#"

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        con = DriverManager.getConnection(url,id,pw);*/

        con = VaultDBConnection.getConnection();

        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT ");
        sql.append(" A.dash_public_id AS OID, A.part_name, A.batch_date, A.total_cnt, ");
        sql.append(" A.DIS202405, A.DIS202406, A.DIS202407, A.DIS202408, A.DIS202409, ");
        sql.append(" A.DIS202410, A.DIS202411, A.DIS202412, ");
        sql.append(" A.DIS202501, A.DIS202502, A.DIS202503, A.DIS202504, A.DIS202505 ");
        sql.append(" FROM DASH_PUBLIC A ");
        sql.append(" WHERE A.BATCH_DATE = ? ");


        pstmt = con.prepareStatement(sql.toString());
        pstmt.setString(1, todayVal);

        rs = pstmt.executeQuery();

        while(rs.next())
        {
            String part_name = rs.getString("part_name") == null ? "" : rs.getString("part_name");
            String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");
            String total_cnt = rs.getString("total_cnt") == null ? "" : rs.getString("total_cnt");

            String DIS202405 = rs.getString("DIS202405") == null ? "" : rs.getString("DIS202405");
            String DIS202406 = rs.getString("DIS202406") == null ? "" : rs.getString("DIS202406");
            String DIS202407 = rs.getString("DIS202407") == null ? "" : rs.getString("DIS202407");
            String DIS202408 = rs.getString("DIS202408") == null ? "" : rs.getString("DIS202408");
            String DIS202409 = rs.getString("DIS202409") == null ? "" : rs.getString("DIS202409");
            String DIS202410 = rs.getString("DIS202410") == null ? "" : rs.getString("DIS202410");
            String DIS202411 = rs.getString("DIS202411") == null ? "" : rs.getString("DIS202411");
            String DIS202412 = rs.getString("DIS202412") == null ? "" : rs.getString("DIS202412");

            String DIS202501 = rs.getString("DIS202501") == null ? "" : rs.getString("DIS202501");
            String DIS202502 = rs.getString("DIS202502") == null ? "" : rs.getString("DIS202502");
            String DIS202503 = rs.getString("DIS202503") == null ? "" : rs.getString("DIS202503");
            String DIS202504 = rs.getString("DIS202504") == null ? "" : rs.getString("DIS202504");
            String DIS202505 = rs.getString("DIS202505") == null ? "" : rs.getString("DIS202505");


            int totalCnt = 0;

            totalCnt = Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                    + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412)
                    + Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505);

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("PARTNAME", part_name);
            data.put("BATCH_DATE", batch_date);
            data.put("TOTAL_CNT", String.valueOf(totalCnt));

            data.put("DIS202405", DIS202405);
            data.put("DIS202406", DIS202406);
            data.put("DIS202407", DIS202407);
            data.put("DIS202408", DIS202408);
            data.put("DIS202409", DIS202409);
            data.put("DIS202410", DIS202410);
            data.put("DIS202411", DIS202411);
            data.put("DIS202412", DIS202412);
            data.put("DIS202501", DIS202501);
            data.put("DIS202502", DIS202502);
            data.put("DIS202503", DIS202503);
            data.put("DIS202504", DIS202504);
            data.put("DIS202505", DIS202505);


            if("cpMRL_5".equals(part_name)) {
                cpMRL_5 = String.valueOf(totalCnt);
                cpMRL_5_05 = DIS202405;
                cpMRL_5_06 = DIS202406;
                cpMRL_5_07 = DIS202407;
                cpMRL_5_08 = DIS202408;
                cpMRL_5_09 = DIS202409;
                cpMRL_5_10 = DIS202410;
                cpMRL_5_11 = DIS202411;
                cpMRL_5_12 = DIS202412;
                cpMRL_5_13 = DIS202501;
                cpMRL_5_14 = DIS202502;
                cpMRL_5_15 = DIS202503;
                cpMRL_5_16 = DIS202504;
                cpMRL_5_17 = DIS202505;

            } else if("cpMRL_9".equals(part_name)) {
                cpMRL_9    = String.valueOf(totalCnt);
                cpMRL_9_05 = DIS202405;
                cpMRL_9_06 = DIS202406;
                cpMRL_9_07 = DIS202407;
                cpMRL_9_08 = DIS202408;
                cpMRL_9_09 = DIS202409;
                cpMRL_9_10 = DIS202410;
                cpMRL_9_11 = DIS202411;
                cpMRL_9_12 = DIS202412;
                cpMRL_9_13 = DIS202501;
                cpMRL_9_14 = DIS202502;
                cpMRL_9_15 = DIS202503;
                cpMRL_9_16 = DIS202504;
                cpMRL_9_17 = DIS202505;

            } else if("cpMRL_14".equals(part_name)) {

                cpMRL_14    = String.valueOf(totalCnt);
                cpMRL_14_05 = DIS202405;
                cpMRL_14_06 = DIS202406;
                cpMRL_14_07 = DIS202407;
                cpMRL_14_08 = DIS202408;
                cpMRL_14_09 = DIS202409;
                cpMRL_14_10 = DIS202410;
                cpMRL_14_11 = DIS202411;
                cpMRL_14_12 = DIS202412;
                cpMRL_14_13 = DIS202501;
                cpMRL_14_14 = DIS202502;
                cpMRL_14_15 = DIS202503;
                cpMRL_14_16 = DIS202504;
                cpMRL_14_17 = DIS202505;

            } else if("cpMRL_17".equals(part_name)) {
                cpMRL_17    = String.valueOf(totalCnt);
                cpMRL_17_05 = DIS202405;
                cpMRL_17_06 = DIS202406;
                cpMRL_17_07 = DIS202407;
                cpMRL_17_08 = DIS202408;
                cpMRL_17_09 = DIS202409;
                cpMRL_17_10 = DIS202410;
                cpMRL_17_11 = DIS202411;
                cpMRL_17_12 = DIS202412;
                cpMRL_17_13 = DIS202501;
                cpMRL_17_14 = DIS202502;
                cpMRL_17_15 = DIS202503;
                cpMRL_17_16 = DIS202504;
                cpMRL_17_17 = DIS202505;


            } else if("cpMR_5_5".equals(part_name)) {
                cpMR_5    = String.valueOf(totalCnt);
                cpMR_5_05 = DIS202405;
                cpMR_5_06 = DIS202406;
                cpMR_5_07 = DIS202407;
                cpMR_5_08 = DIS202408;
                cpMR_5_09 = DIS202409;
                cpMR_5_10 = DIS202410;
                cpMR_5_11 = DIS202411;
                cpMR_5_12 = DIS202412;
                cpMR_5_13 = DIS202501;
                cpMR_5_14 = DIS202502;
                cpMR_5_15 = DIS202503;
                cpMR_5_16 = DIS202504;
                cpMR_5_17 = DIS202505;
            } else if("cpMR_9".equals(part_name)) {
                cpMR_9    = String.valueOf(totalCnt);
                cpMR_9_05 = DIS202405;
                cpMR_9_06 = DIS202406;
                cpMR_9_07 = DIS202407;
                cpMR_9_08 = DIS202408;
                cpMR_9_09 = DIS202409;
                cpMR_9_10 = DIS202410;
                cpMR_9_11 = DIS202411;
                cpMR_9_12 = DIS202412;
                cpMR_9_13 = DIS202501;
                cpMR_9_14 = DIS202502;
                cpMR_9_15 = DIS202503;
                cpMR_9_16 = DIS202504;
                cpMR_9_17 = DIS202505;
            } else if("cpMR_14".equals(part_name)) {
                cpMR_14    = String.valueOf(totalCnt);
                cpMR_14_05 = DIS202405;
                cpMR_14_06 = DIS202406;
                cpMR_14_07 = DIS202407;
                cpMR_14_08 = DIS202408;
                cpMR_14_09 = DIS202409;
                cpMR_14_10 = DIS202410;
                cpMR_14_11 = DIS202411;
                cpMR_14_12 = DIS202412;
                cpMR_14_13 = DIS202501;
                cpMR_14_14 = DIS202502;
                cpMR_14_15 = DIS202503;
                cpMR_14_16 = DIS202504;
                cpMR_14_17 = DIS202505;
            } else if("cpMR_17_5".equals(part_name)) {
                cpMR_17    = String.valueOf(totalCnt);
                cpMR_17_05 = DIS202405;
                cpMR_17_06 = DIS202406;
                cpMR_17_07 = DIS202407;
                cpMR_17_08 = DIS202408;
                cpMR_17_09 = DIS202409;
                cpMR_17_10 = DIS202410;
                cpMR_17_11 = DIS202411;
                cpMR_17_12 = DIS202412;
                cpMR_17_13 = DIS202501;
                cpMR_17_14 = DIS202502;
                cpMR_17_15 = DIS202503;
                cpMR_17_16 = DIS202504;
                cpMR_17_17 = DIS202505;
            } else if("TM".equals(part_name)) {
                TM    = String.valueOf(totalCnt);
                TM_05 = DIS202405;
                TM_06 = DIS202406;
                TM_07 = DIS202407;
                TM_08 = DIS202408;
                TM_09 = DIS202409;
                TM_10 = DIS202410;
                TM_11 = DIS202411;
                TM_12 = DIS202412;
                TM_13 = DIS202501;
                TM_14 = DIS202502;
                TM_15 = DIS202503;
                TM_16 = DIS202504;
                TM_17 = DIS202505;
            } else if("TMRope".equals(part_name)) {
                TMRope    = String.valueOf(totalCnt);
                TMRope_05 = DIS202405;
                TMRope_06 = DIS202406;
                TMRope_07 = DIS202407;
                TMRope_08 = DIS202408;
                TMRope_09 = DIS202409;
                TMRope_10 = DIS202410;
                TMRope_11 = DIS202411;
                TMRope_12 = DIS202412;
                TMRope_13 = DIS202501;
                TMRope_14 = DIS202502;
                TMRope_15 = DIS202503;
                TMRope_16 = DIS202504;
                TMRope_17 = DIS202505;
            } else if("CARTOPBOX".equals(part_name)) {
                CARTOP    = String.valueOf(totalCnt);
                CARTOP_05 = DIS202405;
                CARTOP_06 = DIS202406;
                CARTOP_07 = DIS202407;
                CARTOP_08 = DIS202408;
                CARTOP_09 = DIS202409;
                CARTOP_10 = DIS202410;
                CARTOP_11 = DIS202411;
                CARTOP_12 = DIS202412;
                CARTOP_13 = DIS202501;
                CARTOP_14 = DIS202502;
                CARTOP_15 = DIS202503;
                CARTOP_16 = DIS202504;
                CARTOP_17 = DIS202505;
            } else if("GOV".equals(part_name)) {
                GOV    = String.valueOf(totalCnt);
                GOV_05 = DIS202405;
                GOV_06 = DIS202406;
                GOV_07 = DIS202407;
                GOV_08 = DIS202408;
                GOV_09 = DIS202409;
                GOV_10 = DIS202410;
                GOV_11 = DIS202411;
                GOV_12 = DIS202412;
                GOV_13 = DIS202501;
                GOV_14 = DIS202502;
                GOV_15 = DIS202503;
                GOV_16 = DIS202504;
                GOV_17 = DIS202505;
            } else if("LAMP_HOIST".equals(part_name)) {
                LAMP    = String.valueOf(totalCnt);
                LAMP_05 = DIS202405;
                LAMP_06 = DIS202406;
                LAMP_07 = DIS202407;
                LAMP_08 = DIS202408;
                LAMP_09 = DIS202409;
                LAMP_10 = DIS202410;
                LAMP_11 = DIS202411;
                LAMP_12 = DIS202412;
                LAMP_13 = DIS202501;
                LAMP_14 = DIS202502;
                LAMP_15 = DIS202503;
                LAMP_16 = DIS202504;
                LAMP_17 = DIS202505;
            } else if("HPB_BOT".equals(part_name)) {
                HPB    = String.valueOf(totalCnt);
                HPB_05 = DIS202405;
                HPB_06 = DIS202406;
                HPB_07 = DIS202407;
                HPB_08 = DIS202408;
                HPB_09 = DIS202409;
                HPB_10 = DIS202410;
                HPB_11 = DIS202411;
                HPB_12 = DIS202412;
                HPB_13 = DIS202501;
                HPB_14 = DIS202502;
                HPB_15 = DIS202503;
                HPB_16 = DIS202504;
                HPB_17 = DIS202505;
            } else if("HIP_BOT".equals(part_name)) {
                HIP    = String.valueOf(totalCnt);
                HIP_05 = DIS202405;
                HIP_06 = DIS202406;
                HIP_07 = DIS202407;
                HIP_08 = DIS202408;
                HIP_09 = DIS202409;
                HIP_10 = DIS202410;
                HIP_11 = DIS202411;
                HIP_12 = DIS202412;
                HIP_13 = DIS202501;
                HIP_14 = DIS202502;
                HIP_15 = DIS202503;
                HIP_16 = DIS202504;
                HIP_17 = DIS202505;
            } else if("HPI_S700".equals(part_name)) {
                HPI    = String.valueOf(totalCnt);
                HPI_05 = DIS202405;
                HPI_06 = DIS202406;
                HPI_07 = DIS202407;
                HPI_08 = DIS202408;
                HPI_09 = DIS202409;
                HPI_10 = DIS202410;
                HPI_11 = DIS202411;
                HPI_12 = DIS202412;
                HPI_13 = DIS202501;
                HPI_14 = DIS202502;
                HPI_15 = DIS202503;
                HPI_16 = DIS202504;
                HPI_17 = DIS202505;
            } else if("PIT".equals(part_name)) {
                PIT    = String.valueOf(totalCnt);
                PIT_05 = DIS202405;
                PIT_06 = DIS202406;
                PIT_07 = DIS202407;
                PIT_08 = DIS202408;
                PIT_09 = DIS202409;
                PIT_10 = DIS202410;
                PIT_11 = DIS202411;
                PIT_12 = DIS202412;
                PIT_13 = DIS202501;
                PIT_14 = DIS202502;
                PIT_15 = DIS202503;
                PIT_16 = DIS202504;
                PIT_17 = DIS202505;
            } else if("OPB_D521AG".equals(part_name.toUpperCase())) {
                OPB_D521AG    = String.valueOf(totalCnt);
                OPB_D521AG_05 = DIS202405;
                OPB_D521AG_06 = DIS202406;
                OPB_D521AG_07 = DIS202407;
                OPB_D521AG_08 = DIS202408;
                OPB_D521AG_09 = DIS202409;
                OPB_D521AG_10 = DIS202410;
                OPB_D521AG_11 = DIS202411;
                OPB_D521AG_12 = DIS202412;
                OPB_D521AG_13 = DIS202501;
                OPB_D521AG_14 = DIS202502;
                OPB_D521AG_15 = DIS202503;
                OPB_D521AG_16 = DIS202504;
                OPB_D521AG_17 = DIS202505;
            } else if("OPB_S521A".equals(part_name.toUpperCase())) {
                OPB_S521A    = String.valueOf(totalCnt);
                OPB_S521A_05 = DIS202405;
                OPB_S521A_06 = DIS202406;
                OPB_S521A_07 = DIS202407;
                OPB_S521A_08 = DIS202408;
                OPB_S521A_09 = DIS202409;
                OPB_S521A_10 = DIS202410;
                OPB_S521A_11 = DIS202411;
                OPB_S521A_12 = DIS202412;
                OPB_S521A_13 = DIS202501;
                OPB_S521A_14 = DIS202502;
                OPB_S521A_15 = DIS202503;
                OPB_S521A_16 = DIS202504;
                OPB_S521A_17 = DIS202505;
            } else if("cp2_MRL_General".equals(part_name)) {
                cp2_MRL_General    = String.valueOf(totalCnt);
                cp2_MRL_General_05 = DIS202405;
                cp2_MRL_General_06 = DIS202406;
                cp2_MRL_General_07 = DIS202407;
                cp2_MRL_General_08 = DIS202408;
                cp2_MRL_General_09 = DIS202409;
                cp2_MRL_General_10 = DIS202410;
                cp2_MRL_General_11 = DIS202411;
                cp2_MRL_General_12 = DIS202412;
                cp2_MRL_General_13 = DIS202501;
                cp2_MRL_General_14 = DIS202502;
                cp2_MRL_General_15 = DIS202503;
                cp2_MRL_General_16 = DIS202504;
                cp2_MRL_General_17 = DIS202505;
            } else if("cp2_MRL_Revive".equals(part_name)) {
                cp2_MRL_Revive    = String.valueOf(totalCnt);
                cp2_MRL_Revive_05 = DIS202405;
                cp2_MRL_Revive_06 = DIS202406;
                cp2_MRL_Revive_07 = DIS202407;
                cp2_MRL_Revive_08 = DIS202408;
                cp2_MRL_Revive_09 = DIS202409;
                cp2_MRL_Revive_10 = DIS202410;
                cp2_MRL_Revive_11 = DIS202411;
                cp2_MRL_Revive_12 = DIS202412;
                cp2_MRL_Revive_13 = DIS202501;
                cp2_MRL_Revive_14 = DIS202502;
                cp2_MRL_Revive_15 = DIS202503;
                cp2_MRL_Revive_16 = DIS202504;
                cp2_MRL_Revive_17 = DIS202505;
            } else if("cp2_MR_General".equals(part_name)) {
                cp2_MR_General    = String.valueOf(totalCnt);
                cp2_MR_General_05 = DIS202405;
                cp2_MR_General_06 = DIS202406;
                cp2_MR_General_07 = DIS202407;
                cp2_MR_General_08 = DIS202408;
                cp2_MR_General_09 = DIS202409;
                cp2_MR_General_10 = DIS202410;
                cp2_MR_General_11 = DIS202411;
                cp2_MR_General_12 = DIS202412;
                cp2_MR_General_13 = DIS202501;
                cp2_MR_General_14 = DIS202502;
                cp2_MR_General_15 = DIS202503;
                cp2_MR_General_16 = DIS202504;
                cp2_MR_General_17 = DIS202505;
            } else if("cp2_MR_Revive".equals(part_name)) {
                cp2_MR_Revive    = String.valueOf(totalCnt);
                cp2_MR_Revive_05 = DIS202405;
                cp2_MR_Revive_06 = DIS202406;
                cp2_MR_Revive_07 = DIS202407;
                cp2_MR_Revive_08 = DIS202408;
                cp2_MR_Revive_09 = DIS202409;
                cp2_MR_Revive_10 = DIS202410;
                cp2_MR_Revive_11 = DIS202411;
                cp2_MR_Revive_12 = DIS202412;
                cp2_MR_Revive_13 = DIS202501;
                cp2_MR_Revive_14 = DIS202502;
                cp2_MR_Revive_15 = DIS202503;
                cp2_MR_Revive_16 = DIS202504;
                cp2_MR_Revive_17 = DIS202505;
            }
        }




    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //DynaUtils.close(rs,pstmt,con);
        VaultDBConnection.disconnect(con, pstmt, rs);
    }



    int countNum = 1;


    System.out.println(" ---------------- end dashboard -------------");

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>부품공용화 Dahsboard</title>

    <!-- Google Font: Source Sans Pro -->
    <!--    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">-->
    <link rel="stylesheet" href="/resources/dist/googleFont.css">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="/resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="/resources/dist/plugins/select2/css/select2.min.css">

    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">
</head>


<body class="hold-transition sidebar-mini text-sm" style="zoom:95%;">

<div class="wrapper">
    <!-- Navbar -->
    <!-- <nav class="main-header navbar navbar-expand navbar-white navbar-light"> -->
    <nav class="main-header navbar navbar-expand">
        <!-- Left navbar links -->
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
            </li>

            <li class="nav-item">
                <a class="nav-link" data-widget="fullscreen" href="#" role="button">
                    <i class="fas fa-expand-arrows-alt"></i>
                </a>
            </li>
        </ul>
    </nav>
    <!-- /.navbar -->


    <!-- Main Sidebar Container -->
    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <!-- <h1>부품공용화 - 월별실적 Dashboard <font color="red">(2024/11/17, 06:00기준)</font> </h1> -->
                        <h1>부품공용화 - 월별실적(대수) <font color="red">( <%=todayVal %>, 06:00기준)</font> </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>
                        </ol>
                    </div>
                </div>
            </div><!-- /.container-fluid -->
        </section>


        <!-- Main content -->
        <section class="content" style="zoom:98%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">Dashboard</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:90%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>

                                    <!-- bg-primary -->
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">과제명</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">총 대수</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="13">월별실적</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" colspan="8">2024년</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="5">2025년</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">05</th>
                                        <th style="font-weight: bold; text-align: center;">06</th>
                                        <th style="font-weight: bold; text-align: center;">07</th>
                                        <th style="font-weight: bold; text-align: center;">08</th>
                                        <th style="font-weight: bold; text-align: center;">09</th>
                                        <th style="font-weight: bold; text-align: center;">10</th>
                                        <th style="font-weight: bold; text-align: center;">11</th>
                                        <th style="font-weight: bold; text-align: center;">12</th>

                                        <th style="font-weight: bold; text-align: center;">01</th>
                                        <th style="font-weight: bold; text-align: center;">02</th>
                                        <th style="font-weight: bold; text-align: center;">03</th>
                                        <th style="font-weight: bold; text-align: center;">04</th>
                                        <th style="font-weight: bold; text-align: center;">05</th>
                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_5.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', 'total');"> <font color="red"> <%=cpMRL_5 %> </font> </a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202405');"><%= cpMRL_5_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202406');"><%= cpMRL_5_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202407');"><%= cpMRL_5_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202408');"><%= cpMRL_5_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202409');"><%= cpMRL_5_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202410');"><%= cpMRL_5_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202411');"><%= cpMRL_5_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202412');"><%= cpMRL_5_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202501');"><%= cpMRL_5_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202502');"><%= cpMRL_5_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202503');"><%= cpMRL_5_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202504');"><%= cpMRL_5_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202505');"><%= cpMRL_5_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_9kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', 'total');"><font color="red"> <%=cpMRL_9 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202405');"><%= cpMRL_9_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202406');"><%= cpMRL_9_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202407');"><%= cpMRL_9_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202408');"><%= cpMRL_9_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202409');"><%= cpMRL_9_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202410');"><%= cpMRL_9_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202411');"><%= cpMRL_9_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202412');"><%= cpMRL_9_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202501');"><%= cpMRL_9_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202502');"><%= cpMRL_9_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202503');"><%= cpMRL_9_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202504');"><%= cpMRL_9_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202505');"><%= cpMRL_9_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_14kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', 'total');"><font color="red"> <%=cpMRL_14 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202405');"><%= cpMRL_14_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202406');"><%= cpMRL_14_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202407');"><%= cpMRL_14_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202408');"><%= cpMRL_14_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202409');"><%= cpMRL_14_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202410');"><%= cpMRL_14_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202411');"><%= cpMRL_14_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202412');"><%= cpMRL_14_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202501');"><%= cpMRL_14_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202502');"><%= cpMRL_14_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202503');"><%= cpMRL_14_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202504');"><%= cpMRL_14_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202505');"><%= cpMRL_14_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_17.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', 'total');"><font color="red"> <%=cpMRL_17 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202405');"><%= cpMRL_17_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202406');"><%= cpMRL_17_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202407');"><%= cpMRL_17_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202408');"><%= cpMRL_17_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202409');"><%= cpMRL_17_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202410');"><%= cpMRL_17_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202411');"><%= cpMRL_17_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202412');"><%= cpMRL_17_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202501');"><%= cpMRL_17_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202502');"><%= cpMRL_17_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202503');"><%= cpMRL_17_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202504');"><%= cpMRL_17_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202505');"><%= cpMRL_17_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_5.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', 'total');"><font color="red"> <%=cpMR_5 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202405');"><%= cpMR_5_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202406');"><%= cpMR_5_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202407');"><%= cpMR_5_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202408');"><%= cpMR_5_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202409');"><%= cpMR_5_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202410');"><%= cpMR_5_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202411');"><%= cpMR_5_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202412');"><%= cpMR_5_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202501');"><%= cpMR_5_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202502');"><%= cpMR_5_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202503');"><%= cpMR_5_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202504');"><%= cpMR_5_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202505');"><%= cpMR_5_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_9kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', 'total');"><font color="red"> <%=cpMR_9 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202405');"><%= cpMR_9_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202406');"><%= cpMR_9_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202407');"><%= cpMR_9_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202408');"><%= cpMR_9_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202409');"><%= cpMR_9_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202410');"><%= cpMR_9_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202411');"><%= cpMR_9_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202412');"><%= cpMR_9_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202501');"><%= cpMR_9_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202502');"><%= cpMR_9_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202503');"><%= cpMR_9_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202504');"><%= cpMR_9_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202505');"><%= cpMR_9_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_14kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', 'total');"><font color="red"> <%=cpMR_14 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202405');"><%= cpMR_14_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202406');"><%= cpMR_14_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202407');"><%= cpMR_14_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202408');"><%= cpMR_14_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202409');"><%= cpMR_14_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202410');"><%= cpMR_14_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202411');"><%= cpMR_14_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202412');"><%= cpMR_14_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202501');"><%= cpMR_14_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202502');"><%= cpMR_14_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202503');"><%= cpMR_14_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202504');"><%= cpMR_14_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202505');"><%= cpMR_14_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_17.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', 'total');"><font color="red"> <%=cpMR_17 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202405');"><%= cpMR_17_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202406');"><%= cpMR_17_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202407');"><%= cpMR_17_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202408');"><%= cpMR_17_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202409');"><%= cpMR_17_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202410');"><%= cpMR_17_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202411');"><%= cpMR_17_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202412');"><%= cpMR_17_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202501');"><%= cpMR_17_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202502');"><%= cpMR_17_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202503');"><%= cpMR_17_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202504');"><%= cpMR_17_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202505');"><%= cpMR_17_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', 'total');"><font color="red"> <%=cp2_MRL_General %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202405');"><%= cp2_MRL_General_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202406');"><%= cp2_MRL_General_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202407');"><%= cp2_MRL_General_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202408');"><%= cp2_MRL_General_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202409');"><%= cp2_MRL_General_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202410');"><%= cp2_MRL_General_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202411');"><%= cp2_MRL_General_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202412');"><%= cp2_MRL_General_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202501');"><%= cp2_MRL_General_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202502');"><%= cp2_MRL_General_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202503');"><%= cp2_MRL_General_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202504');"><%= cp2_MRL_General_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202505');"><%= cp2_MRL_General_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', 'total');"><font color="red"> <%=cp2_MRL_Revive %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202405');"><%= cp2_MRL_Revive_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202406');"><%= cp2_MRL_Revive_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202407');"><%= cp2_MRL_Revive_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202408');"><%= cp2_MRL_Revive_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202409');"><%= cp2_MRL_Revive_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202410');"><%= cp2_MRL_Revive_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202411');"><%= cp2_MRL_Revive_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202412');"><%= cp2_MRL_Revive_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202501');"><%= cp2_MRL_Revive_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202502');"><%= cp2_MRL_Revive_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202503');"><%= cp2_MRL_Revive_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202504');"><%= cp2_MRL_Revive_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202505');"><%= cp2_MRL_Revive_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', 'total');"><font color="red"> <%=cp2_MR_General %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202405');"><%= cp2_MR_General_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202406');"><%= cp2_MR_General_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202407');"><%= cp2_MR_General_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202408');"><%= cp2_MR_General_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202409');"><%= cp2_MR_General_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202410');"><%= cp2_MR_General_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202411');"><%= cp2_MR_General_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202412');"><%= cp2_MR_General_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202501');"><%= cp2_MR_General_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202502');"><%= cp2_MR_General_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202503');"><%= cp2_MR_General_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202504');"><%= cp2_MR_General_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202505');"><%= cp2_MR_General_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', 'total');"><font color="red"> <%=cp2_MR_Revive %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202405');"><%= cp2_MR_Revive_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202406');"><%= cp2_MR_Revive_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202407');"><%= cp2_MR_Revive_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202408');"><%= cp2_MR_Revive_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202409');"><%= cp2_MR_Revive_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202410');"><%= cp2_MR_Revive_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202411');"><%= cp2_MR_Revive_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202412');"><%= cp2_MR_Revive_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202501');"><%= cp2_MR_Revive_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202502');"><%= cp2_MR_Revive_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202503');"><%= cp2_MR_Revive_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202504');"><%= cp2_MR_Revive_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202505');"><%= cp2_MR_Revive_17 %></a></td>
                                    </tr>



                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(Belt Type)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', 'total');"><font color="red"> <%=TM %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202405');"><%= TM_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202406');"><%= TM_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202407');"><%= TM_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202408');"><%= TM_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202409');"><%= TM_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202410');"><%= TM_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202411');"><%= TM_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202412');"><%= TM_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202501');"><%= TM_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202502');"><%= TM_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202503');"><%= TM_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202504');"><%= TM_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202505');"><%= TM_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(ROPE)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', 'total');"><font color="red"> <%=TMRope %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202405');"><%= TMRope_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202406');"><%= TMRope_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202407');"><%= TMRope_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202408');"><%= TMRope_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202409');"><%= TMRope_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202410');"><%= TMRope_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202411');"><%= TMRope_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202412');"><%= TMRope_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202501');"><%= TMRope_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202502');"><%= TMRope_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202503');"><%= TMRope_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202504');"><%= TMRope_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202505');"><%= TMRope_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Car Top Box</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', 'total');"><font color="red"><%=CARTOP %></font> </a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202405');"><%= CARTOP_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202406');"><%= CARTOP_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202407');"><%= CARTOP_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202408');"><%= CARTOP_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202409');"><%= CARTOP_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202410');"><%= CARTOP_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202411');"><%= CARTOP_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202412');"><%= CARTOP_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202501');"><%= CARTOP_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202502');"><%= CARTOP_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202503');"><%= CARTOP_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202504');"><%= CARTOP_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202505');"><%= CARTOP_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Governor</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', 'total');"><font color="red"><%=GOV %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202405');"><%= GOV_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202406');"><%= GOV_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202407');"><%= GOV_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202408');"><%= GOV_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202409');"><%= GOV_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202410');"><%= GOV_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202411');"><%= GOV_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202412');"><%= GOV_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202501');"><%= GOV_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202502');"><%= GOV_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202503');"><%= GOV_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202504');"><%= GOV_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202505');"><%= GOV_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> 승강로 LAMP(HOISTWAY 기준)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', 'total');"><font color="red"><%=LAMP %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202405');"><%= LAMP_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202406');"><%= LAMP_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202407');"><%= LAMP_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202408');"><%= LAMP_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202409');"><%= LAMP_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202410');"><%= LAMP_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202411');"><%= LAMP_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202412');"><%= LAMP_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202501');"><%= LAMP_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202502');"><%= LAMP_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202503');"><%= LAMP_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202504');"><%= LAMP_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202505');"><%= LAMP_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> PIT </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', 'total');"><font color="red"><%=PIT %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202405');"><%= PIT_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202406');"><%= PIT_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202407');"><%= PIT_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202408');"><%= PIT_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202409');"><%= PIT_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202410');"><%= PIT_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202411');"><%= PIT_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202412');"><%= PIT_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202501');"><%= PIT_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202502');"><%= PIT_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202503');"><%= PIT_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202504');"><%= PIT_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202505');"><%= PIT_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">HPB(J21,BOT 기준)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', 'total');"><font color="red"><%=HPB %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202405');"><%= HPB_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202406');"><%= HPB_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202407');"><%= HPB_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202408');"><%= HPB_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202409');"><%= HPB_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202410');"><%= HPB_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202411');"><%= HPB_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202412');"><%= HPB_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202501');"><%= HPB_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202502');"><%= HPB_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202503');"><%= HPB_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202504');"><%= HPB_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202505');"><%= HPB_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SJ21,BOT 기준) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', 'total');"><font color="red"><%=HIP %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202405');"><%= HIP_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202406');"><%= HIP_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202407');"><%= HIP_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202408');"><%= HIP_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202409');"><%= HIP_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202410');"><%= HIP_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202411');"><%= HIP_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202412');"><%= HIP_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202501');"><%= HIP_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202502');"><%= HIP_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202503');"><%= HIP_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202504');"><%= HIP_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202505');"><%= HIP_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (D521AG) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', 'total');"><font color="red"><%=OPB_D521AG %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202405');"><%= OPB_D521AG_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202406');"><%= OPB_D521AG_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202407');"><%= OPB_D521AG_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202408');"><%= OPB_D521AG_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202409');"><%= OPB_D521AG_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202410');"><%= OPB_D521AG_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202411');"><%= OPB_D521AG_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202412');"><%= OPB_D521AG_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202501');"><%= OPB_D521AG_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202502');"><%= OPB_D521AG_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202503');"><%= OPB_D521AG_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202504');"><%= OPB_D521AG_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202505');"><%= OPB_D521AG_17 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (S521A) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'total');"><font color="red"><%=OPB_S521A %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202405');"><%= OPB_S521A_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202406');"><%= OPB_S521A_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202407');"><%= OPB_S521A_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202408');"><%= OPB_S521A_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202409');"><%= OPB_S521A_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202410');"><%= OPB_S521A_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202411');"><%= OPB_S521A_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202412');"><%= OPB_S521A_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202501');"><%= OPB_S521A_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202502');"><%= OPB_S521A_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202503');"><%= OPB_S521A_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202504');"><%= OPB_S521A_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202505');"><%= OPB_S521A_17 %></a></td>
                                    </tr>


                                    <%
                                        DashDtoV2 HPB_K21_TOP = jdcbService.findByIdV2(todayVal, "HPB_K21_TOP");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', 'total');"><font color="red"><%=HPB_K21_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202405');"><%= HPB_K21_TOP.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202406');"><%= HPB_K21_TOP.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202407');"><%= HPB_K21_TOP.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202408');"><%= HPB_K21_TOP.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202409');"><%= HPB_K21_TOP.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202410');"><%= HPB_K21_TOP.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202411');"><%= HPB_K21_TOP.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202412');"><%= HPB_K21_TOP.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202501');"><%= HPB_K21_TOP.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202502');"><%= HPB_K21_TOP.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202503');"><%= HPB_K21_TOP.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202504');"><%= HPB_K21_TOP.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202505');"><%= HPB_K21_TOP.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPB_K21_MID = jdcbService.findByIdV2(todayVal, "HPB_K21_MID");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', 'total');"><font color="red"><%=HPB_K21_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202405');"><%= HPB_K21_MID.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202406');"><%= HPB_K21_MID.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202407');"><%= HPB_K21_MID.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202408');"><%= HPB_K21_MID.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202409');"><%= HPB_K21_MID.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202410');"><%= HPB_K21_MID.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202411');"><%= HPB_K21_MID.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202412');"><%= HPB_K21_MID.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202501');"><%= HPB_K21_MID.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202502');"><%= HPB_K21_MID.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202503');"><%= HPB_K21_MID.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202504');"><%= HPB_K21_MID.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202505');"><%= HPB_K21_MID.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPB_K21_BOT = jdcbService.findByIdV2(todayVal, "HPB_K21_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', 'total');"><font color="red"><%=HPB_K21_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202405');"><%= HPB_K21_BOT.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202406');"><%= HPB_K21_BOT.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202407');"><%= HPB_K21_BOT.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202408');"><%= HPB_K21_BOT.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202409');"><%= HPB_K21_BOT.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202410');"><%= HPB_K21_BOT.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202411');"><%= HPB_K21_BOT.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202412');"><%= HPB_K21_BOT.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202501');"><%= HPB_K21_BOT.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202502');"><%= HPB_K21_BOT.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202503');"><%= HPB_K21_BOT.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202504');"><%= HPB_K21_BOT.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202505');"><%= HPB_K21_BOT.getDIS202505() %></a></td>
                                    </tr>


                                    <%
                                        DashDtoV2 HPB_K21A_TOP = jdcbService.findByIdV2(todayVal, "HPB_K21A_TOP");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', 'total');"><font color="red"><%=HPB_K21A_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202405');"><%= HPB_K21A_TOP.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202406');"><%= HPB_K21A_TOP.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202407');"><%= HPB_K21A_TOP.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202408');"><%= HPB_K21A_TOP.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202409');"><%= HPB_K21A_TOP.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202410');"><%= HPB_K21A_TOP.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202411');"><%= HPB_K21A_TOP.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202412');"><%= HPB_K21A_TOP.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202501');"><%= HPB_K21A_TOP.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202502');"><%= HPB_K21A_TOP.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202503');"><%= HPB_K21A_TOP.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202504');"><%= HPB_K21A_TOP.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202505');"><%= HPB_K21A_TOP.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPB_K21A_MID = jdcbService.findByIdV2(todayVal, "HPB_K21A_MID");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,MID)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', 'total');"><font color="red"><%=HPB_K21A_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202405');"><%= HPB_K21A_MID.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202406');"><%= HPB_K21A_MID.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202407');"><%= HPB_K21A_MID.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202408');"><%= HPB_K21A_MID.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202409');"><%= HPB_K21A_MID.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202410');"><%= HPB_K21A_MID.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202411');"><%= HPB_K21A_MID.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202412');"><%= HPB_K21A_MID.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202501');"><%= HPB_K21A_MID.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202502');"><%= HPB_K21A_MID.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202503');"><%= HPB_K21A_MID.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202504');"><%= HPB_K21A_MID.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202505');"><%= HPB_K21A_MID.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPB_K21A_BOT = jdcbService.findByIdV2(todayVal, "HPB_K21A_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,BOT)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', 'total');"><font color="red"><%=HPB_K21A_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202405');"><%= HPB_K21A_BOT.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202406');"><%= HPB_K21A_BOT.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202407');"><%= HPB_K21A_BOT.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202408');"><%= HPB_K21A_BOT.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202409');"><%= HPB_K21A_BOT.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202410');"><%= HPB_K21A_BOT.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202411');"><%= HPB_K21A_BOT.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202412');"><%= HPB_K21A_BOT.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202501');"><%= HPB_K21A_BOT.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202502');"><%= HPB_K21A_BOT.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202503');"><%= HPB_K21A_BOT.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202504');"><%= HPB_K21A_BOT.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202505');"><%= HPB_K21A_BOT.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HIP_SK21_TOP = jdcbService.findByIdV2(todayVal, "HIP_SK21_TOP");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,TOP)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', 'total');"><font color="red"><%=HIP_SK21_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202405');"><%= HIP_SK21_TOP.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202406');"><%= HIP_SK21_TOP.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202407');"><%= HIP_SK21_TOP.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202408');"><%= HIP_SK21_TOP.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202409');"><%= HIP_SK21_TOP.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202410');"><%= HIP_SK21_TOP.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202411');"><%= HIP_SK21_TOP.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202412');"><%= HIP_SK21_TOP.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202501');"><%= HIP_SK21_TOP.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202502');"><%= HIP_SK21_TOP.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202503');"><%= HIP_SK21_TOP.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202504');"><%= HIP_SK21_TOP.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202505');"><%= HIP_SK21_TOP.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HIP_SK21_MID = jdcbService.findByIdV2(todayVal, "HIP_SK21_MID");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,MID)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', 'total');"><font color="red"><%=HIP_SK21_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202405');"><%= HIP_SK21_MID.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202406');"><%= HIP_SK21_MID.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202407');"><%= HIP_SK21_MID.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202408');"><%= HIP_SK21_MID.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202409');"><%= HIP_SK21_MID.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202410');"><%= HIP_SK21_MID.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202411');"><%= HIP_SK21_MID.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202412');"><%= HIP_SK21_MID.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202501');"><%= HIP_SK21_MID.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202502');"><%= HIP_SK21_MID.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202503');"><%= HIP_SK21_MID.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202504');"><%= HIP_SK21_MID.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202505');"><%= HIP_SK21_MID.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HIP_SK21_BOT = jdcbService.findByIdV2(todayVal, "HIP_SK21_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,BOT)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', 'total');"><font color="red"><%=HIP_SK21_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202405');"><%= HIP_SK21_BOT.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202406');"><%= HIP_SK21_BOT.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202407');"><%= HIP_SK21_BOT.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202408');"><%= HIP_SK21_BOT.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202409');"><%= HIP_SK21_BOT.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202410');"><%= HIP_SK21_BOT.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202411');"><%= HIP_SK21_BOT.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202412');"><%= HIP_SK21_BOT.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202501');"><%= HIP_SK21_BOT.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202502');"><%= HIP_SK21_BOT.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202503');"><%= HIP_SK21_BOT.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202504');"><%= HIP_SK21_BOT.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202505');"><%= HIP_SK21_BOT.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPI_S700 = jdcbService.findByIdV2(todayVal, "HPI_S700");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPI(S700)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', 'total');"><font color="red"><%=HPI_S700.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202405');"><%= HPI_S700.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202406');"><%= HPI_S700.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202407');"><%= HPI_S700.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202408');"><%= HPI_S700.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202409');"><%= HPI_S700.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202410');"><%= HPI_S700.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202411');"><%= HPI_S700.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202412');"><%= HPI_S700.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202501');"><%= HPI_S700.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202502');"><%= HPI_S700.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202503');"><%= HPI_S700.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202504');"><%= HPI_S700.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202505');"><%= HPI_S700.getDIS202505() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPI_SC = jdcbService.findByIdV2(todayVal, "HPI_SC");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPI(SC)  </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', 'total');"><font color="red"><%=HPI_SC.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202405');"><%= HPI_SC.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202406');"><%= HPI_SC.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202407');"><%= HPI_SC.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202408');"><%= HPI_SC.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202409');"><%= HPI_SC.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202410');"><%= HPI_SC.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202411');"><%= HPI_SC.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202412');"><%= HPI_SC.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202501');"><%= HPI_SC.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202502');"><%= HPI_SC.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202503');"><%= HPI_SC.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202504');"><%= HPI_SC.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202505');"><%= HPI_SC.getDIS202505() %></a></td>
                                    </tr>


                                    </tbody>

                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div>
                        <!-- /.card -->
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->


            </div> <!-- /.container-fluid -->

        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->


    <footer class="main-footer">
        <div class="float-right d-none d-sm-block">
            <b>Version</b> 1.0.0
        </div>
        <strong>Copyright &copy; 2024 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>


</body>

<!-- <script src="https://code.jquery.com/jquery-3.5.1.js"></script> -->

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

<!-- AdminLTE App -->
<script src="/resources/dist/js/adminlte.min.js"></script>

<!-- Bootstrap 4 -->
<script src="/resources/dist/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- DataTables  & Plugins -->
<script src="/resources/dist/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/resources/dist/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="/resources/dist/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
<script src="/resources/dist/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>


<script src="/resources/dist/plugins/select2/js/select2.full.min.js"></script>

<script src="/resources/dist/plugins/datatables-buttons/js/dataTables.buttons.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.bootstrap4.min.js"></script>
<script src="/resources/dist/plugins/jszip/jszip.min.js"></script>
<script src="/resources/dist/plugins/pdfmake/pdfmake.min.js"></script>
<script src="/resources/dist/plugins/pdfmake/vfs_fonts.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.html5.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.print.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.colVis.min.js"></script>


<!-- Highhart -->
<script src="/resources/dist/js/highcharts.js"></script>
<script src="/resources/dist/js/exporting.js"></script>
<script src="/resources/dist/js/export-data.js"></script>
<script src="/resources/dist/js/accessibility.js"></script>



<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "ordering" : false,
        "searching" : false,
        "paging" : false, // 페이징표시 삭제
        "destroy": true, // 테이블 재생성
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

        console.log('highchart start');

    }); // end document ready

    function viewList(type, viewDate) {

        console.log(type + " -- " + viewDate);

        let todayVal = '<%=todayVal %>'

        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionPop.jsp?";
        //let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionPop.jsp?";
        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionPopRev.jsp?";
        let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionPopRev.jsp?";

        urlValue += "viewType=" + type;
        urlValue += "&startDate=" + viewDate;
        urlValue += "&todayVal=" + todayVal;

        window.open(urlValue,'_blank','width=1500, height=800, top=50, left=50, scrollbars=yes');

    }

</script>

</html>
