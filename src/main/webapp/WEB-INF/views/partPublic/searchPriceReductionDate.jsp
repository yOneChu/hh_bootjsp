<%@page import="java.time.LocalDate"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.kyhslam.service.JdbcTestService" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.kyhslam.dto.DashDtoV3" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //출하예정일 기준 대시보드 개발
    // searchPriceReductionDate.jsp
    // http://10.225.4.20/jsp/searchLogic/searchPriceReductionDate.jsp
    // http://localhost/jsp/searchLogic/searchPriceReductionDate.jsp

    //http://10.225.4.20/jsp/searchLogic/searchPriceReductionDate.jsp

    String contextPath = request.getContextPath();
    System.out.println("--- searchPriceReductionDate.jsp ---");

    // ServletContext에서 Spring WebApplicationContext 얻기
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // 원하는 Bean 가져오기
    JdbcTestService jdcbService = (JdbcTestService) context.getBean("JdbcTestService");


    ArrayList<HashMap<String, String>> dashList = new ArrayList<HashMap<String, String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;

    //2단계 CP(MRL_일반)
    String cp2_MRL_General = "";
    String cp2_MRL_General_2024 = "";
    String cp2_MRL_General_05 = "";
    String cp2_MRL_General_06 = "";
    String cp2_MRL_General_07 = "";
    String cp2_MRL_General_08 = "";
    String cp2_MRL_General_09 = "";
    String cp2_MRL_General_10 = "";
    String cp2_MRL_General_11 = "";
    String cp2_MRL_General_12 = "";
    String cp2_MRL_General_2025 = "";
    String cp2_MRL_General_13 = "";
    String cp2_MRL_General_14 = "";
    String cp2_MRL_General_15 = "";
    String cp2_MRL_General_16 = "";
    String cp2_MRL_General_17 = "";
    String cp2_MRL_General_18 = "";
    String cp2_MRL_General_19 = "";
    String cp2_MRL_General_20 = "";
    String cp2_MRL_General_21 = "";
    String cp2_MRL_General_22 = "";
    String cp2_MRL_General_23 = "";
    String cp2_MRL_General_24 = "";
    String cp2_MRL_General_ETC = "";

    //2단계 CP(MRL_회생)
    String cp2_MRL_Revive = "";
    String cp2_MRL_Revive_2024 = "";
    String cp2_MRL_Revive_05 = "";
    String cp2_MRL_Revive_06 = "";
    String cp2_MRL_Revive_07 = "";
    String cp2_MRL_Revive_08 = "";
    String cp2_MRL_Revive_09 = "";
    String cp2_MRL_Revive_10 = "";
    String cp2_MRL_Revive_11 = "";
    String cp2_MRL_Revive_12 = "";
    String cp2_MRL_Revive_2025 = "";
    String cp2_MRL_Revive_13 = "";
    String cp2_MRL_Revive_14 = "";
    String cp2_MRL_Revive_15 = "";
    String cp2_MRL_Revive_16 = "";
    String cp2_MRL_Revive_17 = "";
    String cp2_MRL_Revive_18 = "";
    String cp2_MRL_Revive_19 = "";
    String cp2_MRL_Revive_20 = "";
    String cp2_MRL_Revive_21 = "";
    String cp2_MRL_Revive_22 = "";
    String cp2_MRL_Revive_23 = "";
    String cp2_MRL_Revive_24 = "";
    String cp2_MRL_Revive_ETC = "";

    //2단계 CP(MR_일반)
    String cp2_MR_General = "";
    String cp2_MR_General_2024 = "";
    String cp2_MR_General_05 = "";
    String cp2_MR_General_06 = "";
    String cp2_MR_General_07 = "";
    String cp2_MR_General_08 = "";
    String cp2_MR_General_09 = "";
    String cp2_MR_General_10 = "";
    String cp2_MR_General_11 = "";
    String cp2_MR_General_12 = "";
    String cp2_MR_General_2025 = "";
    String cp2_MR_General_13 = "";
    String cp2_MR_General_14 = "";
    String cp2_MR_General_15 = "";
    String cp2_MR_General_16 = "";
    String cp2_MR_General_17 = "";
    String cp2_MR_General_18 = "";
    String cp2_MR_General_19 = "";
    String cp2_MR_General_20 = "";
    String cp2_MR_General_21 = "";
    String cp2_MR_General_22 = "";
    String cp2_MR_General_23 = "";
    String cp2_MR_General_24 = "";
    String cp2_MR_General_ETC = "";

    //2단계 CP(MR_회생)
    String cp2_MR_Revive = "";
    String cp2_MR_Revive_2024 = "";
    String cp2_MR_Revive_05 = "";
    String cp2_MR_Revive_06 = "";
    String cp2_MR_Revive_07 = "";
    String cp2_MR_Revive_08 = "";
    String cp2_MR_Revive_09 = "";
    String cp2_MR_Revive_10 = "";
    String cp2_MR_Revive_11 = "";
    String cp2_MR_Revive_12 = "";
    String cp2_MR_Revive_2025 = "";
    String cp2_MR_Revive_13 = "";
    String cp2_MR_Revive_14 = "";
    String cp2_MR_Revive_15 = "";
    String cp2_MR_Revive_16 = "";
    String cp2_MR_Revive_17 = "";
    String cp2_MR_Revive_18 = "";
    String cp2_MR_Revive_19 = "";
    String cp2_MR_Revive_20 = "";
    String cp2_MR_Revive_21 = "";
    String cp2_MR_Revive_22 = "";
    String cp2_MR_Revive_23 = "";
    String cp2_MR_Revive_24 = "";
    String cp2_MR_Revive_ETC = "";


    String cpMRL_5 = "";
    String cpMRL_5_2024 = "";
    String cpMRL_5_05 = "";
    String cpMRL_5_06 = "";
    String cpMRL_5_07 = "";
    String cpMRL_5_08 = "";
    String cpMRL_5_09 = "";
    String cpMRL_5_10 = "";
    String cpMRL_5_11 = "";
    String cpMRL_5_12 = "";
    String cpMRL_5_2025 = "";
    String cpMRL_5_13 = "";
    String cpMRL_5_14 = "";
    String cpMRL_5_15 = "";
    String cpMRL_5_16 = "";
    String cpMRL_5_17 = "";
    String cpMRL_5_18 = "";
    String cpMRL_5_19 = "";
    String cpMRL_5_20 = "";
    String cpMRL_5_21 = "";
    String cpMRL_5_22 = "";
    String cpMRL_5_23 = "";
    String cpMRL_5_24 = "";
    String cpMRL_5_ETC = "";

    String cpMRL_9 = "";
    String cpMRL_9_2024 = "";
    String cpMRL_9_05 = "";
    String cpMRL_9_06 = "";
    String cpMRL_9_07 = "";
    String cpMRL_9_08 = "";
    String cpMRL_9_09 = "";
    String cpMRL_9_10 = "";
    String cpMRL_9_11 = "";
    String cpMRL_9_12 = "";
    String cpMRL_9_2025 = "";
    String cpMRL_9_13 = "";
    String cpMRL_9_14 = "";
    String cpMRL_9_15 = "";
    String cpMRL_9_16 = "";
    String cpMRL_9_17 = "";
    String cpMRL_9_18 = "";
    String cpMRL_9_19 = "";
    String cpMRL_9_20 = "";
    String cpMRL_9_21 = "";
    String cpMRL_9_22 = "";
    String cpMRL_9_23 = "";
    String cpMRL_9_24 = "";
    String cpMRL_9_ETC = "";

    String cpMRL_14 = "";
    String cpMRL_14_2024 = "";
    String cpMRL_14_05 = "";
    String cpMRL_14_06 = "";
    String cpMRL_14_07 = "";
    String cpMRL_14_08 = "";
    String cpMRL_14_09 = "";
    String cpMRL_14_10 = "";
    String cpMRL_14_11 = "";
    String cpMRL_14_12 = "";
    String cpMRL_14_2025 = "";
    String cpMRL_14_13 = "";
    String cpMRL_14_14 = "";
    String cpMRL_14_15 = "";
    String cpMRL_14_16 = "";
    String cpMRL_14_17 = "";
    String cpMRL_14_18 = "";
    String cpMRL_14_19 = "";
    String cpMRL_14_20 = "";
    String cpMRL_14_21 = "";
    String cpMRL_14_22 = "";
    String cpMRL_14_23 = "";
    String cpMRL_14_24 = "";
    String cpMRL_14_ETC = "";

    String cpMRL_17 = "";
    String cpMRL_17_2024 = "";
    String cpMRL_17_05 = "";
    String cpMRL_17_06 = "";
    String cpMRL_17_07 = "";
    String cpMRL_17_08 = "";
    String cpMRL_17_09 = "";
    String cpMRL_17_10 = "";
    String cpMRL_17_11 = "";
    String cpMRL_17_12 = "";
    String cpMRL_17_2025 = "";
    String cpMRL_17_13 = "";
    String cpMRL_17_14 = "";
    String cpMRL_17_15 = "";
    String cpMRL_17_16 = "";
    String cpMRL_17_17 = "";
    String cpMRL_17_18 = "";
    String cpMRL_17_19 = "";
    String cpMRL_17_20 = "";
    String cpMRL_17_21 = "";
    String cpMRL_17_22 = "";
    String cpMRL_17_23 = "";
    String cpMRL_17_24 = "";
    String cpMRL_17_ETC = "";

    String cpMR_5 = "";
    String cpMR_5_2024 = "";
    String cpMR_5_05 = "";
    String cpMR_5_06 = "";
    String cpMR_5_07 = "";
    String cpMR_5_08 = "";
    String cpMR_5_09 = "";
    String cpMR_5_10 = "";
    String cpMR_5_11 = "";
    String cpMR_5_12 = "";
    String cpMR_5_2025 = "";
    String cpMR_5_13 = "";
    String cpMR_5_14 = "";
    String cpMR_5_15 = "";
    String cpMR_5_16 = "";
    String cpMR_5_17 = "";
    String cpMR_5_18 = "";
    String cpMR_5_19 = "";
    String cpMR_5_20 = "";
    String cpMR_5_21 = "";
    String cpMR_5_22 = "";
    String cpMR_5_23 = "";
    String cpMR_5_24 = "";
    String cpMR_5_ETC = "";

    String cpMR_9 = "";
    String cpMR_9_2024 = "";
    String cpMR_9_05 = "";
    String cpMR_9_06 = "";
    String cpMR_9_07 = "";
    String cpMR_9_08 = "";
    String cpMR_9_09 = "";
    String cpMR_9_10 = "";
    String cpMR_9_11 = "";
    String cpMR_9_12 = "";
    String cpMR_9_2025 = "";
    String cpMR_9_13 = "";
    String cpMR_9_14 = "";
    String cpMR_9_15 = "";
    String cpMR_9_16 = "";
    String cpMR_9_17 = "";
    String cpMR_9_18 = "";
    String cpMR_9_19 = "";
    String cpMR_9_20 = "";
    String cpMR_9_21 = "";
    String cpMR_9_22 = "";
    String cpMR_9_23 = "";
    String cpMR_9_24 = "";
    String cpMR_9_ETC = "";

    String cpMR_14 = "";
    String cpMR_14_2024 = "";
    String cpMR_14_05 = "";
    String cpMR_14_06 = "";
    String cpMR_14_07 = "";
    String cpMR_14_08 = "";
    String cpMR_14_09 = "";
    String cpMR_14_10 = "";
    String cpMR_14_11 = "";
    String cpMR_14_12 = "";
    String cpMR_14_2025 = "";
    String cpMR_14_13 = "";
    String cpMR_14_14 = "";
    String cpMR_14_15 = "";
    String cpMR_14_16 = "";
    String cpMR_14_17 = "";
    String cpMR_14_18 = "";
    String cpMR_14_19 = "";
    String cpMR_14_20 = "";
    String cpMR_14_21 = "";
    String cpMR_14_22 = "";
    String cpMR_14_23 = "";
    String cpMR_14_24 = "";
    String cpMR_14_ETC = "";

    String cpMR_17 = "";
    String cpMR_17_2024 = "";
    String cpMR_17_05 = "";
    String cpMR_17_06 = "";
    String cpMR_17_07 = "";
    String cpMR_17_08 = "";
    String cpMR_17_09 = "";
    String cpMR_17_10 = "";
    String cpMR_17_11 = "";
    String cpMR_17_12 = "";
    String cpMR_17_2025 = "";
    String cpMR_17_13 = "";
    String cpMR_17_14 = "";
    String cpMR_17_15 = "";
    String cpMR_17_16 = "";
    String cpMR_17_17 = "";
    String cpMR_17_18 = "";
    String cpMR_17_19 = "";
    String cpMR_17_20 = "";
    String cpMR_17_21 = "";
    String cpMR_17_22 = "";
    String cpMR_17_23 = "";
    String cpMR_17_24 = "";
    String cpMR_17_ETC = "";

    String TM = "";
    String TM_2024 = "";
    String TM_05 = "";
    String TM_06 = "";
    String TM_07 = "";
    String TM_08 = "";
    String TM_09 = "";
    String TM_10 = "";
    String TM_11 = "";
    String TM_12 = "";
    String TM_2025 = "";
    String TM_13 = "";
    String TM_14 = "";
    String TM_15 = "";
    String TM_16 = "";
    String TM_17 = "";
    String TM_18 = "";
    String TM_19 = "";
    String TM_20 = "";
    String TM_21 = "";
    String TM_22 = "";
    String TM_23 = "";
    String TM_24 = "";
    String TM_ETC = "";

    String TMRope = "";
    String TMRope_2024 = "";
    String TMRope_05 = "";
    String TMRope_06 = "";
    String TMRope_07 = "";
    String TMRope_08 = "";
    String TMRope_09 = "";
    String TMRope_10 = "";
    String TMRope_11 = "";
    String TMRope_12 = "";
    String TMRope_2025 = "";
    String TMRope_13 = "";
    String TMRope_14 = "";
    String TMRope_15 = "";
    String TMRope_16 = "";
    String TMRope_17 = "";
    String TMRope_18 = "";
    String TMRope_19 = "";
    String TMRope_20 = "";
    String TMRope_21 = "";
    String TMRope_22 = "";
    String TMRope_23 = "";
    String TMRope_24 = "";
    String TMRope_ETC = "";

    String CARTOP = "";
    String CARTOP_2024 = "";
    String CARTOP_05 = "";
    String CARTOP_06 = "";
    String CARTOP_07 = "";
    String CARTOP_08 = "";
    String CARTOP_09 = "";
    String CARTOP_10 = "";
    String CARTOP_11 = "";
    String CARTOP_12 = "";
    String CARTOP_2025 = "";
    String CARTOP_13 = "";
    String CARTOP_14 = "";
    String CARTOP_15 = "";
    String CARTOP_16 = "";
    String CARTOP_17 = "";
    String CARTOP_18 = "";
    String CARTOP_19 = "";
    String CARTOP_20 = "";
    String CARTOP_21 = "";
    String CARTOP_22 = "";
    String CARTOP_23 = "";
    String CARTOP_24 = "";
    String CARTOP_ETC = "";

    String GOV = "";
    String GOV_2024 = "";
    String GOV_05 = "";
    String GOV_06 = "";
    String GOV_07 = "";
    String GOV_08 = "";
    String GOV_09 = "";
    String GOV_10 = "";
    String GOV_11 = "";
    String GOV_12 = "";
    String GOV_2025 = "";
    String GOV_13 = "";
    String GOV_14 = "";
    String GOV_15 = "";
    String GOV_16 = "";
    String GOV_17 = "";
    String GOV_18 = "";
    String GOV_19 = "";
    String GOV_20 = "";
    String GOV_21 = "";
    String GOV_22 = "";
    String GOV_23 = "";
    String GOV_24 = "";
    String GOV_ETC = "";

    int LAMP = 0;
    int LAMP_2024 = 0;
    int LAMP_05 = 0;
    int LAMP_06 = 0;
    int LAMP_07 = 0;
    int LAMP_08 = 0;
    int LAMP_09 = 0;
    int LAMP_10 = 0;
    int LAMP_11 = 0;
    int LAMP_12 = 0;
    int LAMP_2025 = 0;
    int LAMP_13 = 0;
    int LAMP_14 = 0;
    int LAMP_15 = 0;
    int LAMP_16 = 0;
    int LAMP_17 = 0;
    int LAMP_18 = 0;
    int LAMP_19 = 0;
    int LAMP_20 = 0;
    int LAMP_21 = 0;
    int LAMP_22 = 0;
    int LAMP_23 = 0;
    int LAMP_24 = 0;
    int LAMP_ETC = 0;

    int HPB = 0;
    int HPB_2024 = 0;
    int HPB_05 = 0;
    int HPB_06 = 0;
    int HPB_07 = 0;
    int HPB_08 = 0;
    int HPB_09 = 0;
    int HPB_10 = 0;
    int HPB_11 = 0;
    int HPB_12 = 0;
    int HPB_2025 = 0;
    int HPB_13 = 0;
    int HPB_14 = 0;
    int HPB_15 = 0;
    int HPB_16 = 0;
    int HPB_17 = 0;
    int HPB_18 = 0;
    int HPB_19 = 0;
    int HPB_20 = 0;
    int HPB_21 = 0;
    int HPB_22 = 0;
    int HPB_23 = 0;
    int HPB_24 = 0;
    int HPB_ETC = 0;

    int HIP = 0;
    int HIP_2024 = 0;
    int HIP_05 = 0;
    int HIP_06 = 0;
    int HIP_07 = 0;
    int HIP_08 = 0;
    int HIP_09 = 0;
    int HIP_10 = 0;
    int HIP_11 = 0;
    int HIP_12 = 0;
    int HIP_2025 = 0;
    int HIP_13 = 0;
    int HIP_14 = 0;
    int HIP_15 = 0;
    int HIP_16 = 0;
    int HIP_17 = 0;
    int HIP_18 = 0;
    int HIP_19 = 0;
    int HIP_20 = 0;
    int HIP_21 = 0;
    int HIP_22 = 0;
    int HIP_23 = 0;
    int HIP_24 = 0;
    int HIP_ETC = 0;


    String HPI = "";
    String HPI_2024 = "";
    String HPI_05 = "";
    String HPI_06 = "";
    String HPI_07 = "";
    String HPI_08 = "";
    String HPI_09 = "";
    String HPI_10 = "";
    String HPI_11 = "";
    String HPI_12 = "";
    String HPI_2025 = "";
    String HPI_13 = "";
    String HPI_14 = "";
    String HPI_15 = "";
    String HPI_16 = "";
    String HPI_17 = "";
    String HPI_18 = "";
    String HPI_19 = "";
    String HPI_20 = "";
    String HPI_21 = "";
    String HPI_22 = "";
    String HPI_23 = "";
    String HPI_24 = "";
    String HPI_ETC = "";

    String PIT = "";
    String PIT_2024 = "";
    String PIT_05 = "";
    String PIT_06 = "";
    String PIT_07 = "";
    String PIT_08 = "";
    String PIT_09 = "";
    String PIT_10 = "";
    String PIT_11 = "";
    String PIT_12 = "";
    String PIT_2025 = "";
    String PIT_13 = "";
    String PIT_14 = "";
    String PIT_15 = "";
    String PIT_16 = "";
    String PIT_17 = "";
    String PIT_18 = "";
    String PIT_19 = "";
    String PIT_20 = "";
    String PIT_21 = "";
    String PIT_22 = "";
    String PIT_23 = "";
    String PIT_24 = "";
    String PIT_ETC = "";

    //opb_D521AG
    String OPB_D521AG = "";
    String OPB_D521AG_2024 = "";
    String OPB_D521AG_05 = "";
    String OPB_D521AG_06 = "";
    String OPB_D521AG_07 = "";
    String OPB_D521AG_08 = "";
    String OPB_D521AG_09 = "";
    String OPB_D521AG_10 = "";
    String OPB_D521AG_11 = "";
    String OPB_D521AG_12 = "";
    String OPB_D521AG_2025 = "";
    String OPB_D521AG_13 = "";
    String OPB_D521AG_14 = "";
    String OPB_D521AG_15 = "";
    String OPB_D521AG_16 = "";
    String OPB_D521AG_17 = "";
    String OPB_D521AG_18 = "";
    String OPB_D521AG_19 = "";
    String OPB_D521AG_20 = "";
    String OPB_D521AG_21 = "";
    String OPB_D521AG_22 = "";
    String OPB_D521AG_23 = "";
    String OPB_D521AG_24 = "";
    String OPB_D521AG_ETC = "";

    //opb_S521A
    String OPB_S521A = "";
    String OPB_S521A_2024 = "";
    String OPB_S521A_05 = "";
    String OPB_S521A_06 = "";
    String OPB_S521A_07 = "";
    String OPB_S521A_08 = "";
    String OPB_S521A_09 = "";
    String OPB_S521A_10 = "";
    String OPB_S521A_11 = "";
    String OPB_S521A_12 = "";
    String OPB_S521A_2025 = "";
    String OPB_S521A_13 = "";
    String OPB_S521A_14 = "";
    String OPB_S521A_15 = "";
    String OPB_S521A_16 = "";
    String OPB_S521A_17 = "";
    String OPB_S521A_18 = "";
    String OPB_S521A_19 = "";
    String OPB_S521A_20 = "";
    String OPB_S521A_21 = "";
    String OPB_S521A_22 = "";
    String OPB_S521A_23 = "";
    String OPB_S521A_24 = "";
    String OPB_S521A_ETC = "";

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
        sql.append(" A.batch_date, ");

        sql.append(" A.part_name, ");
        sql.append(" A.export202405, ");
        sql.append(" A.export202406, ");
        sql.append(" A.export202407, ");
        sql.append(" A.export202408, ");
        sql.append(" A.export202409, ");
        sql.append(" A.export202410, ");
        sql.append(" A.export202411, ");
        sql.append(" A.export202412, ");
        sql.append(" A.export202501, ");
        sql.append(" A.export202502, ");
        sql.append(" A.export202503, ");
        sql.append(" A.export202504, ");
        sql.append(" A.export202505, ");
        sql.append(" A.export202506, ");
        sql.append(" A.export202507, ");
        sql.append(" A.export202508, ");
        sql.append(" A.export202509, ");
        sql.append(" A.export202510, ");
        sql.append(" A.export202511, ");
        sql.append(" A.export202512, ");
        sql.append(" A.export_etc ");
        sql.append(" FROM DASH_PUBLIC A ");
        sql.append(" WHERE A.BATCH_DATE = ? ");


        pstmt = con.prepareStatement(sql.toString());
        pstmt.setString(1, todayVal);

        rs = pstmt.executeQuery();

        while(rs.next())
        {
            String part_name = rs.getString("part_name") == null ? "" : rs.getString("part_name");
            String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");

            String DIS202405 = rs.getString("export202405") == null ? "" : rs.getString("export202405");
            String DIS202406 = rs.getString("export202406") == null ? "" : rs.getString("export202406");
            String DIS202407 = rs.getString("export202407") == null ? "" : rs.getString("export202407");
            String DIS202408 = rs.getString("export202408") == null ? "" : rs.getString("export202408");
            String DIS202409 = rs.getString("export202409") == null ? "" : rs.getString("export202409");
            String DIS202410 = rs.getString("export202410") == null ? "" : rs.getString("export202410");
            String DIS202411 = rs.getString("export202411") == null ? "" : rs.getString("export202411");
            String DIS202412 = rs.getString("export202412") == null ? "" : rs.getString("export202412");

            String DIS202501 = rs.getString("export202501") == null ? "" : rs.getString("export202501");
            String DIS202502 = rs.getString("export202502") == null ? "" : rs.getString("export202502");
            String DIS202503 = rs.getString("export202503") == null ? "" : rs.getString("export202503");
            String DIS202504 = rs.getString("export202504") == null ? "" : rs.getString("export202504");
            String DIS202505 = rs.getString("export202505") == null ? "" : rs.getString("export202505");
            String DIS202506 = rs.getString("export202506") == null ? "" : rs.getString("export202506");
            String DIS202507 = rs.getString("export202507") == null ? "" : rs.getString("export202507");
            String DIS202508 = rs.getString("export202508") == null ? "" : rs.getString("export202508");
            String DIS202509 = rs.getString("export202509") == null ? "" : rs.getString("export202509");
            String DIS202510 = rs.getString("export202510") == null ? "" : rs.getString("export202510");
            String DIS202511 = rs.getString("export202511") == null ? "" : rs.getString("export202511");
            String DIS202512 = rs.getString("export202512") == null ? "" : rs.getString("export202512");
            String DISETC = rs.getString("export_etc") == null ? "" : rs.getString("export_etc");

            int totalCnt = 0;


            totalCnt = Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                    + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412)
                    + Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                    + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                    + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) + Integer.parseInt(DISETC);




            HashMap<String, String> data = new HashMap<String, String>();


            if("cpMRL_5".equals(part_name)) {
                cpMRL_5 = String.valueOf(totalCnt);
                cpMRL_5_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMRL_5_05 = DIS202405;
                cpMRL_5_06 = DIS202406;
                cpMRL_5_07 = DIS202407;
                cpMRL_5_08 = DIS202408;
                cpMRL_5_09 = DIS202409;
                cpMRL_5_10 = DIS202410;
                cpMRL_5_11 = DIS202411;
                cpMRL_5_12 = DIS202412;
                cpMRL_5_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) );
                cpMRL_5_13 = DIS202501;
                cpMRL_5_14 = DIS202502;
                cpMRL_5_15 = DIS202503;
                cpMRL_5_16 = DIS202504;
                cpMRL_5_17 = DIS202505;
                cpMRL_5_18 = DIS202506;
                cpMRL_5_19 = DIS202507;
                cpMRL_5_20 = DIS202508;
                cpMRL_5_21 = DIS202509;
                cpMRL_5_22 = DIS202510;
                cpMRL_5_23 = DIS202511;
                cpMRL_5_24 = DIS202512;
                cpMRL_5_ETC = DISETC;

            } else if("cpMRL_9".equals(part_name)) {
                cpMRL_9    = String.valueOf(totalCnt);
                cpMRL_9_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMRL_9_05 = DIS202405;
                cpMRL_9_06 = DIS202406;
                cpMRL_9_07 = DIS202407;
                cpMRL_9_08 = DIS202408;
                cpMRL_9_09 = DIS202409;
                cpMRL_9_10 = DIS202410;
                cpMRL_9_11 = DIS202411;
                cpMRL_9_12 = DIS202412;
                cpMRL_9_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
                cpMRL_9_13 = DIS202501;
                cpMRL_9_14 = DIS202502;
                cpMRL_9_15 = DIS202503;
                cpMRL_9_16 = DIS202504;
                cpMRL_9_17 = DIS202505;
                cpMRL_9_18 = DIS202506;
                cpMRL_9_19 = DIS202507;
                cpMRL_9_20 = DIS202508;
                cpMRL_9_21 = DIS202509;
                cpMRL_9_22 = DIS202510;
                cpMRL_9_23 = DIS202511;
                cpMRL_9_24 = DIS202512;
                cpMRL_9_ETC = DISETC;

            } else if("cpMRL_14".equals(part_name)) {

                cpMRL_14    = String.valueOf(totalCnt);
                cpMRL_14_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMRL_14_05 = DIS202405;
                cpMRL_14_06 = DIS202406;
                cpMRL_14_07 = DIS202407;
                cpMRL_14_08 = DIS202408;
                cpMRL_14_09 = DIS202409;
                cpMRL_14_10 = DIS202410;
                cpMRL_14_11 = DIS202411;
                cpMRL_14_12 = DIS202412;
                cpMRL_14_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) );
                cpMRL_14_13 = DIS202501;
                cpMRL_14_14 = DIS202502;
                cpMRL_14_15 = DIS202503;
                cpMRL_14_16 = DIS202504;
                cpMRL_14_17 = DIS202505;
                cpMRL_14_18 = DIS202506;
                cpMRL_14_19 = DIS202507;
                cpMRL_14_20 = DIS202508;
                cpMRL_14_21 = DIS202509;
                cpMRL_14_22 = DIS202510;
                cpMRL_14_23 = DIS202511;
                cpMRL_14_24 = DIS202512;
                cpMRL_14_ETC = DISETC;

            } else if("cpMRL_17".equals(part_name)) {
                cpMRL_17    = String.valueOf(totalCnt);
                cpMRL_17_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMRL_17_05 = DIS202405;
                cpMRL_17_06 = DIS202406;
                cpMRL_17_07 = DIS202407;
                cpMRL_17_08 = DIS202408;
                cpMRL_17_09 = DIS202409;
                cpMRL_17_10 = DIS202410;
                cpMRL_17_11 = DIS202411;
                cpMRL_17_12 = DIS202412;
                cpMRL_17_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
                cpMRL_17_13 = DIS202501;
                cpMRL_17_14 = DIS202502;
                cpMRL_17_15 = DIS202503;
                cpMRL_17_16 = DIS202504;
                cpMRL_17_17 = DIS202505;
                cpMRL_17_18 = DIS202506;
                cpMRL_17_19 = DIS202507;
                cpMRL_17_20 = DIS202508;
                cpMRL_17_21 = DIS202509;
                cpMRL_17_22 = DIS202510;
                cpMRL_17_23 = DIS202511;
                cpMRL_17_24 = DIS202512;
                cpMRL_17_ETC = DISETC;

            } else if("cpMR_5_5".equals(part_name)) {
                cpMR_5    = String.valueOf(totalCnt);
                cpMR_5_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMR_5_05 = DIS202405;
                cpMR_5_06 = DIS202406;
                cpMR_5_07 = DIS202407;
                cpMR_5_08 = DIS202408;
                cpMR_5_09 = DIS202409;
                cpMR_5_10 = DIS202410;
                cpMR_5_11 = DIS202411;
                cpMR_5_12 = DIS202412;
                cpMR_5_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) );
                cpMR_5_13 = DIS202501;
                cpMR_5_14 = DIS202502;
                cpMR_5_15 = DIS202503;
                cpMR_5_16 = DIS202504;
                cpMR_5_17 = DIS202505;
                cpMR_5_18 = DIS202506;
                cpMR_5_19 = DIS202507;
                cpMR_5_20 = DIS202508;
                cpMR_5_21 = DIS202509;
                cpMR_5_22 = DIS202510;
                cpMR_5_23 = DIS202511;
                cpMR_5_24 = DIS202512;
                cpMR_5_ETC = DISETC;

            } else if("cpMR_9".equals(part_name)) {
                cpMR_9    = String.valueOf(totalCnt);
                cpMR_9_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMR_9_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                cpMR_9_18 = DIS202506;
                cpMR_9_19 = DIS202507;
                cpMR_9_20 = DIS202508;
                cpMR_9_21 = DIS202509;
                cpMR_9_22 = DIS202510;
                cpMR_9_23 = DIS202511;
                cpMR_9_24 = DIS202512;
                cpMR_9_ETC = DISETC;

            } else if("cpMR_14".equals(part_name)) {
                cpMR_14    = String.valueOf(totalCnt);
                cpMR_14_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMR_14_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                cpMR_14_18 = DIS202506;
                cpMR_14_19 = DIS202507;
                cpMR_14_20 = DIS202508;
                cpMR_14_21 = DIS202509;
                cpMR_14_22 = DIS202510;
                cpMR_14_23 = DIS202511;
                cpMR_14_24 = DIS202512;
                cpMR_14_ETC = DISETC;

            } else if("cpMR_17_5".equals(part_name)) {
                cpMR_17    = String.valueOf(totalCnt);
                cpMR_17_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cpMR_17_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) );
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
                cpMR_17_18 = DIS202506;
                cpMR_17_19 = DIS202507;
                cpMR_17_20 = DIS202508;
                cpMR_17_21 = DIS202509;
                cpMR_17_22 = DIS202510;
                cpMR_17_23 = DIS202511;
                cpMR_17_24 = DIS202512;
                cpMR_17_ETC = DISETC;

            } else if("cp2_MRL_General".equals(part_name)) {
                cp2_MRL_General    = String.valueOf(totalCnt);
                cp2_MRL_General_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cp2_MRL_General_05 = DIS202405;
                cp2_MRL_General_06 = DIS202406;
                cp2_MRL_General_07 = DIS202407;
                cp2_MRL_General_08 = DIS202408;
                cp2_MRL_General_09 = DIS202409;
                cp2_MRL_General_10 = DIS202410;
                cp2_MRL_General_11 = DIS202411;
                cp2_MRL_General_12 = DIS202412;
                cp2_MRL_General_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
                cp2_MRL_General_13 = DIS202501;
                cp2_MRL_General_14 = DIS202502;
                cp2_MRL_General_15 = DIS202503;
                cp2_MRL_General_16 = DIS202504;
                cp2_MRL_General_17 = DIS202505;
                cp2_MRL_General_18 = DIS202506;
                cp2_MRL_General_19 = DIS202507;
                cp2_MRL_General_20 = DIS202508;
                cp2_MRL_General_21 = DIS202509;
                cp2_MRL_General_22 = DIS202510;
                cp2_MRL_General_23 = DIS202511;
                cp2_MRL_General_24 = DIS202512;
                cp2_MRL_General_ETC = DISETC;
            } else if("cp2_MRL_Revive".equals(part_name)) {
                cp2_MRL_Revive    = String.valueOf(totalCnt);
                cp2_MRL_Revive_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cp2_MRL_Revive_05 = DIS202405;
                cp2_MRL_Revive_06 = DIS202406;
                cp2_MRL_Revive_07 = DIS202407;
                cp2_MRL_Revive_08 = DIS202408;
                cp2_MRL_Revive_09 = DIS202409;
                cp2_MRL_Revive_10 = DIS202410;
                cp2_MRL_Revive_11 = DIS202411;
                cp2_MRL_Revive_12 = DIS202412;
                cp2_MRL_Revive_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
                cp2_MRL_Revive_13 = DIS202501;
                cp2_MRL_Revive_14 = DIS202502;
                cp2_MRL_Revive_15 = DIS202503;
                cp2_MRL_Revive_16 = DIS202504;
                cp2_MRL_Revive_17 = DIS202505;
                cp2_MRL_Revive_18 = DIS202506;
                cp2_MRL_Revive_19 = DIS202507;
                cp2_MRL_Revive_20 = DIS202508;
                cp2_MRL_Revive_21 = DIS202509;
                cp2_MRL_Revive_22 = DIS202510;
                cp2_MRL_Revive_23 = DIS202511;
                cp2_MRL_Revive_24 = DIS202512;
                cp2_MRL_Revive_ETC = DISETC;
            } else if("cp2_MR_General".equals(part_name)) {
                cp2_MR_General    = String.valueOf(totalCnt);
                cp2_MR_General_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cp2_MR_General_05 = DIS202405;
                cp2_MR_General_06 = DIS202406;
                cp2_MR_General_07 = DIS202407;
                cp2_MR_General_08 = DIS202408;
                cp2_MR_General_09 = DIS202409;
                cp2_MR_General_10 = DIS202410;
                cp2_MR_General_11 = DIS202411;
                cp2_MR_General_12 = DIS202412;
                cp2_MR_General_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
                cp2_MR_General_13 = DIS202501;
                cp2_MR_General_14 = DIS202502;
                cp2_MR_General_15 = DIS202503;
                cp2_MR_General_16 = DIS202504;
                cp2_MR_General_17 = DIS202505;
                cp2_MR_General_18 = DIS202506;
                cp2_MR_General_19 = DIS202507;
                cp2_MR_General_20 = DIS202508;
                cp2_MR_General_21 = DIS202509;
                cp2_MR_General_22 = DIS202510;
                cp2_MR_General_23 = DIS202511;
                cp2_MR_General_24 = DIS202512;
                cp2_MR_General_ETC = DISETC;

            } else if("cp2_MR_Revive".equals(part_name)) {
                cp2_MR_Revive    = String.valueOf(totalCnt);
                cp2_MR_Revive_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                cp2_MR_Revive_05 = DIS202405;
                cp2_MR_Revive_06 = DIS202406;
                cp2_MR_Revive_07 = DIS202407;
                cp2_MR_Revive_08 = DIS202408;
                cp2_MR_Revive_09 = DIS202409;
                cp2_MR_Revive_10 = DIS202410;
                cp2_MR_Revive_11 = DIS202411;
                cp2_MR_Revive_12 = DIS202412;
                cp2_MR_Revive_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
                cp2_MR_Revive_13 = DIS202501;
                cp2_MR_Revive_14 = DIS202502;
                cp2_MR_Revive_15 = DIS202503;
                cp2_MR_Revive_16 = DIS202504;
                cp2_MR_Revive_17 = DIS202505;
                cp2_MR_Revive_18 = DIS202506;
                cp2_MR_Revive_19 = DIS202507;
                cp2_MR_Revive_20 = DIS202508;
                cp2_MR_Revive_21 = DIS202509;
                cp2_MR_Revive_22 = DIS202510;
                cp2_MR_Revive_23 = DIS202511;
                cp2_MR_Revive_24 = DIS202512;
                cp2_MR_Revive_ETC = DISETC;
            }

            else if("TM".equals(part_name)) {
                TM    = String.valueOf(totalCnt);
                TM_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                TM_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) );
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
                TM_18 = DIS202506;
                TM_19 = DIS202507;
                TM_20 = DIS202508;
                TM_21 = DIS202509;
                TM_22 = DIS202510;
                TM_23 = DIS202511;
                TM_24 = DIS202512;
                TM_ETC = DISETC;

            } else if("TMRope".equals(part_name)) {
                TMRope    = String.valueOf(totalCnt);
                TMRope_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                TMRope_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                TMRope_18 = DIS202506;
                TMRope_19 = DIS202507;
                TMRope_20 = DIS202508;
                TMRope_21 = DIS202509;
                TMRope_22 = DIS202510;
                TMRope_23 = DIS202511;
                TMRope_24 = DIS202512;
                TMRope_ETC = DISETC;

            }else if("CARTOPBOX".equals(part_name)) {
                CARTOP    = String.valueOf(totalCnt);
                CARTOP_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                CARTOP_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                CARTOP_18 = DIS202506;
                CARTOP_19 = DIS202507;
                CARTOP_20 = DIS202508;
                CARTOP_21 = DIS202509;
                CARTOP_22 = DIS202510;
                CARTOP_23 = DIS202511;
                CARTOP_24 = DIS202512;
                CARTOP_ETC = DISETC;

            } else if("GOV".equals(part_name)) {
                GOV    = String.valueOf(totalCnt);
                GOV_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                GOV_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) );
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
                GOV_18 = DIS202506;
                GOV_19 = DIS202507;
                GOV_20 = DIS202508;
                GOV_21 = DIS202509;
                GOV_22 = DIS202510;
                GOV_23 = DIS202511;
                GOV_24 = DIS202512;
                GOV_ETC = DISETC;

            } else if("LAMP_HOIST".equals(part_name) || part_name.startsWith("LAMP")) {
                //LAMP    = totalCnt;
                LAMP_2024 += Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412);
                LAMP_2025 += Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512) ;
                LAMP_05 += Integer.parseInt(DIS202405);
                LAMP_06 += Integer.parseInt(DIS202406);
                LAMP_07 += Integer.parseInt(DIS202407);
                LAMP_08 += Integer.parseInt(DIS202408);
                LAMP_09 += Integer.parseInt(DIS202409);
                LAMP_10 += Integer.parseInt(DIS202410);
                LAMP_11 += Integer.parseInt(DIS202411);
                LAMP_12 += Integer.parseInt(DIS202412);

                LAMP_13 += Integer.parseInt(DIS202501);
                LAMP_14 += Integer.parseInt(DIS202502);
                LAMP_15 += Integer.parseInt(DIS202503);
                LAMP_16 += Integer.parseInt(DIS202504);
                LAMP_17 += Integer.parseInt(DIS202505);
                LAMP_18 += Integer.parseInt(DIS202506);
                LAMP_19 += Integer.parseInt(DIS202507);
                LAMP_20 += Integer.parseInt(DIS202508);
                LAMP_21 += Integer.parseInt(DIS202509);
                LAMP_22 += Integer.parseInt(DIS202510);
                LAMP_23 += Integer.parseInt(DIS202511);
                LAMP_24 += Integer.parseInt(DIS202512);
                LAMP_ETC += Integer.parseInt(DISETC);
                LAMP    = LAMP_2024 + LAMP_2025 + LAMP_ETC;

            } else if("HPB_BOT".equals(part_name) || part_name.equals("HPB_MID") || part_name.equals("HPB_TOP")) {
                //HPB    += totalCnt;
                HPB_2024 += Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412);
                HPB_2025 += Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512);
                HPB_05 += Integer.parseInt(DIS202405);
                HPB_06 += Integer.parseInt(DIS202406);
                HPB_07 += Integer.parseInt(DIS202407);
                HPB_08 += Integer.parseInt(DIS202408);
                HPB_09 += Integer.parseInt(DIS202409);
                HPB_10 += Integer.parseInt(DIS202410);
                HPB_11 += Integer.parseInt(DIS202411);
                HPB_12 += Integer.parseInt(DIS202412);
                HPB_13 += Integer.parseInt(DIS202501);
                HPB_14 += Integer.parseInt(DIS202502);
                HPB_15 += Integer.parseInt(DIS202503);
                HPB_16 += Integer.parseInt(DIS202504);
                HPB_17 += Integer.parseInt(DIS202505);
                HPB_18 += Integer.parseInt(DIS202506);
                HPB_19 += Integer.parseInt(DIS202507);
                HPB_20 += Integer.parseInt(DIS202508);
                HPB_21 += Integer.parseInt(DIS202509);
                HPB_22 += Integer.parseInt(DIS202510);
                HPB_23 += Integer.parseInt(DIS202511);
                HPB_24 += Integer.parseInt(DIS202512);
                HPB_ETC += Integer.parseInt(DISETC);
                HPB    = HPB_2024 + HPB_2025 + HPB_ETC;

            } else if("HIP_BOT".equals(part_name) || part_name.equals("HIP_TOP") || part_name.equals("HIP_MID")) {
                HIP    += totalCnt;
                HIP_2024 += Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412);
                HIP_2025 += Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512);
                HIP_05 += Integer.parseInt(DIS202405);
                HIP_06 += Integer.parseInt(DIS202406);
                HIP_07 += Integer.parseInt(DIS202407);
                HIP_08 += Integer.parseInt(DIS202408);
                HIP_09 += Integer.parseInt(DIS202409);
                HIP_10 += Integer.parseInt(DIS202410);
                HIP_11 += Integer.parseInt(DIS202411);
                HIP_12 += Integer.parseInt(DIS202412);

                HIP_13 += Integer.parseInt(DIS202501);
                HIP_14 += Integer.parseInt(DIS202502);
                HIP_15 += Integer.parseInt(DIS202503);
                HIP_16 += Integer.parseInt(DIS202504);
                HIP_17 += Integer.parseInt(DIS202505);
                HIP_18 += Integer.parseInt(DIS202506);
                HIP_19 += Integer.parseInt(DIS202507);
                HIP_20 += Integer.parseInt(DIS202508);
                HIP_21 += Integer.parseInt(DIS202509);
                HIP_22 += Integer.parseInt(DIS202510);
                HIP_23 += Integer.parseInt(DIS202511);
                HIP_24 += Integer.parseInt(DIS202512);
                HIP_ETC += Integer.parseInt(DISETC);

            } else if("HPI_S700".equals(part_name)) {
                HPI    = String.valueOf(totalCnt);
                HPI_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                HPI_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                HPI_18 = DIS202506;
                HPI_19 = DIS202507;
                HPI_20 = DIS202508;
                HPI_21 = DIS202509;
                HPI_22 = DIS202510;
                HPI_23 = DIS202511;
                HPI_24 = DIS202512;
                HPI_ETC = DISETC;

            } else if("PIT".equals(part_name)) {
                PIT    = String.valueOf(totalCnt);
                PIT_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                PIT_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                PIT_18 = DIS202506;
                PIT_19 = DIS202507;
                PIT_20 = DIS202508;
                PIT_21 = DIS202509;
                PIT_22 = DIS202510;
                PIT_23 = DIS202511;
                PIT_24 = DIS202512;
                PIT_ETC = DISETC;

            } else if("OPB_D521AG".equals(part_name.toUpperCase())) {
                OPB_D521AG    = String.valueOf(totalCnt);
                OPB_D521AG_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                OPB_D521AG_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                OPB_D521AG_18 = DIS202506;
                OPB_D521AG_19 = DIS202507;
                OPB_D521AG_20 = DIS202508;
                OPB_D521AG_21 = DIS202509;
                OPB_D521AG_22 = DIS202510;
                OPB_D521AG_23 = DIS202511;
                OPB_D521AG_24 = DIS202512;
                OPB_D521AG_ETC = DISETC;

            } else if("OPB_S521A".equals(part_name.toUpperCase())) {
                OPB_S521A    = String.valueOf(totalCnt);
                OPB_S521A_2024 = String.valueOf(Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) );
                OPB_S521A_2025 = String.valueOf( Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512)  );
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
                OPB_S521A_18 = DIS202506;
                OPB_S521A_19 = DIS202507;
                OPB_S521A_20 = DIS202508;
                OPB_S521A_21 = DIS202509;
                OPB_S521A_22 = DIS202510;
                OPB_S521A_23 = DIS202511;
                OPB_S521A_24 = DIS202512;
                OPB_S521A_ETC = DISETC;
            }


            //dashList.add(data);
        }


    } catch (Exception e) {
        e.printStackTrace();
    } finally {
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
    <%--<link rel="stylesheet" href="/resources/dist/googleFont.css">--%>



    <!-- Font Awesome -->
    <link rel="stylesheet" href="/resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="/resources/dist/plugins/select2/css/select2.min.css">

    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">

    <style>
        body {
            font-family: 'NotoSans', 'Cascadia Code', sans-serif;
        }
    </style>
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
                        <h1>부품공용화 - 월별실적(출하예정일) <font color="red">( <%=todayVal %>, 06:00기준)</font> </h1>
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
        <section class="content" style="zoom:95%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <div class="row">
                    <div class="col-12">
                        <!-- <div class="col-lg-7"> -->
                        <!-- <section class="col-lg-6 connectedSortable ui-sortable"> -->

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">Dashboard(수량)</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:90%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <%--<table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">--%>
                                <table id="infoTable" class="table table-bordered table-hover" style="font-size:15px;">
                                    <thead>

                                    <!-- bg-primary -->
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">과제명</th>
                                        <!-- <th style="font-weight: bold; text-align: center;" rowspan="3">
                                            총 수량
                                            <br>(2024.05~2025.05)
                                        </th> -->
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">
                                            총 수량
                                        </th>
                                        <th style="font-weight: bold; text-align: center;" colspan="23">월별실적</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" colspan="9">2024년</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="13">2025년</th>
                                        <th style="font-weight: bold; text-align: center;" >기타</th>
                                    </tr>

                                    <tr class="bg-secondary">

                                        <th style="font-weight: bold; text-align: center;">총 수량 <br>(2024년)</th>
                                        <th style="font-weight: bold; text-align: center;">05</th>
                                        <th style="font-weight: bold; text-align: center;">06</th>
                                        <th style="font-weight: bold; text-align: center;">07</th>
                                        <th style="font-weight: bold; text-align: center;">08</th>
                                        <th style="font-weight: bold; text-align: center;">09</th>
                                        <th style="font-weight: bold; text-align: center;">10</th>
                                        <th style="font-weight: bold; text-align: center;">11</th>
                                        <th style="font-weight: bold; text-align: center;">12</th>

                                        <th style="font-weight: bold; text-align: center;">총 수량 <br>(2025년)</th>

                                        <th style="font-weight: bold; text-align: center;">01</th>
                                        <th style="font-weight: bold; text-align: center;">02</th>
                                        <th style="font-weight: bold; text-align: center;">03</th>
                                        <th style="font-weight: bold; text-align: center;">04</th>
                                        <th style="font-weight: bold; text-align: center;">05</th>
                                        <th style="font-weight: bold; text-align: center;">06</th>
                                        <th style="font-weight: bold; text-align: center;">07</th>
                                        <th style="font-weight: bold; text-align: center;">08</th>
                                        <th style="font-weight: bold; text-align: center;">09</th>
                                        <th style="font-weight: bold; text-align: center;">10</th>
                                        <th style="font-weight: bold; text-align: center;">11</th>
                                        <th style="font-weight: bold; text-align: center;">12</th>
                                        <th style="font-weight: bold; text-align: center;">ETC</th>

                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_5.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', 'total');"> <font color="red"> <%=cpMRL_5 %> </font> </a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '2024');"> <font color="red"> <%=cpMRL_5_2024 %> </font> </a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202405');"><%= cpMRL_5_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202406');"><%= cpMRL_5_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202407');"><%= cpMRL_5_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202408');"><%= cpMRL_5_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202409');"><%= cpMRL_5_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202410');"><%= cpMRL_5_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202411');"><%= cpMRL_5_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202412');"><%= cpMRL_5_12 %></a></td>


                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '2025');"> <font color="red"><%= cpMRL_5_2025 %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202501');"><%= cpMRL_5_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202502');"><%= cpMRL_5_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202503');"><%= cpMRL_5_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202504');"><%= cpMRL_5_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202505');"><%= cpMRL_5_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202506');"><%= cpMRL_5_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202507');"><%= cpMRL_5_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202508');"><%= cpMRL_5_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202509');"><%= cpMRL_5_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202510');"><%= cpMRL_5_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202511');"><%= cpMRL_5_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202512');"><%= cpMRL_5_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', 'ETC');"><font color="red"><%= cpMRL_5_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_9kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', 'total');"><font color="red"> <%=cpMRL_9 %> </font> </a> </td>


                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '2024');"><font color="red"><%= cpMRL_9_2024 %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202405');"><%= cpMRL_9_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202406');"><%= cpMRL_9_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202407');"><%= cpMRL_9_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202408');"><%= cpMRL_9_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202409');"><%= cpMRL_9_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202410');"><%= cpMRL_9_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202411');"><%= cpMRL_9_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202412');"><%= cpMRL_9_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '2025');"><font color="red"><%= cpMRL_9_2025 %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202501');"><%= cpMRL_9_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202502');"><%= cpMRL_9_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202503');"><%= cpMRL_9_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202504');"><%= cpMRL_9_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202505');"><%= cpMRL_9_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202506');"><%= cpMRL_9_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202507');"><%= cpMRL_9_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202508');"><%= cpMRL_9_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202509');"><%= cpMRL_9_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202510');"><%= cpMRL_9_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202511');"><%= cpMRL_9_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202512');"><%= cpMRL_9_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', 'ETC');"><font color="red"><%= cpMRL_9_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_14kW_일반)</td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', 'total');"><font color="red"> <%=cpMRL_14 %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '2024');"><font color="red"><%= cpMRL_14_2024 %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202405');"><%= cpMRL_14_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202406');"><%= cpMRL_14_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202407');"><%= cpMRL_14_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202408');"><%= cpMRL_14_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202409');"><%= cpMRL_14_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202410');"><%= cpMRL_14_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202411');"><%= cpMRL_14_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202412');"><%= cpMRL_14_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '2025');"><font color="red"><%= cpMRL_14_2025 %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202501');"><%= cpMRL_14_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202502');"><%= cpMRL_14_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202503');"><%= cpMRL_14_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202504');"><%= cpMRL_14_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202505');"><%= cpMRL_14_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202506');"><%= cpMRL_14_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202507');"><%= cpMRL_14_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202508');"><%= cpMRL_14_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202509');"><%= cpMRL_14_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202510');"><%= cpMRL_14_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202511');"><%= cpMRL_14_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202512');"><%= cpMRL_14_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', 'ETC');"><font color="red"><%= cpMRL_14_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_17.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', 'total');"><font color="red"> <%=cpMRL_17 %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '2024');"><font color="red"> <%=cpMRL_17_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202405');"><%= cpMRL_17_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202406');"><%= cpMRL_17_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202407');"><%= cpMRL_17_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202408');"><%= cpMRL_17_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202409');"><%= cpMRL_17_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202410');"><%= cpMRL_17_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202411');"><%= cpMRL_17_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202412');"><%= cpMRL_17_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '2025');"><font color="red"> <%=cpMRL_17_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202501');"><%= cpMRL_17_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202502');"><%= cpMRL_17_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202503');"><%= cpMRL_17_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202504');"><%= cpMRL_17_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202505');"><%= cpMRL_17_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202506');"><%= cpMRL_17_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202507');"><%= cpMRL_17_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202508');"><%= cpMRL_17_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202509');"><%= cpMRL_17_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202510');"><%= cpMRL_17_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202511');"><%= cpMRL_17_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202512');"><%= cpMRL_17_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', 'ETC');"><font color="red"><%= cpMRL_17_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_5.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', 'total');"><font color="red"> <%=cpMR_5 %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '2024');"><font color="red"> <%=cpMR_5_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202405');"><%= cpMR_5_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202406');"><%= cpMR_5_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202407');"><%= cpMR_5_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202408');"><%= cpMR_5_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202409');"><%= cpMR_5_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202410');"><%= cpMR_5_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202411');"><%= cpMR_5_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202412');"><%= cpMR_5_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '2025');"><font color="red"> <%=cpMR_5_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202501');"><%= cpMR_5_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202502');"><%= cpMR_5_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202503');"><%= cpMR_5_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202504');"><%= cpMR_5_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202505');"><%= cpMR_5_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202506');"><%= cpMR_5_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202507');"><%= cpMR_5_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202508');"><%= cpMR_5_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202509');"><%= cpMR_5_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202510');"><%= cpMR_5_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202511');"><%= cpMR_5_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202512');"><%= cpMR_5_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', 'ETC');"><font color="red"><%= cpMR_5_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_9kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', 'total');"><font color="red"> <%=cpMR_9 %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '2024');"><font color="red"> <%=cpMR_9_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202405');"><%= cpMR_9_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202406');"><%= cpMR_9_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202407');"><%= cpMR_9_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202408');"><%= cpMR_9_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202409');"><%= cpMR_9_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202410');"><%= cpMR_9_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202411');"><%= cpMR_9_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202412');"><%= cpMR_9_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '2025');"><font color="red"> <%=cpMR_9_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202501');"><%= cpMR_9_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202502');"><%= cpMR_9_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202503');"><%= cpMR_9_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202504');"><%= cpMR_9_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202505');"><%= cpMR_9_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202506');"><%= cpMR_9_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202507');"><%= cpMR_9_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202508');"><%= cpMR_9_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202509');"><%= cpMR_9_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202510');"><%= cpMR_9_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202511');"><%= cpMR_9_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202512');"><%= cpMR_9_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', 'ETC');"><font color="red"><%= cpMR_9_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_14kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', 'total');"><font color="red"> <%=cpMR_14 %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '2024');"><font color="red"> <%=cpMR_14_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202405');"><%= cpMR_14_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202406');"><%= cpMR_14_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202407');"><%= cpMR_14_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202408');"><%= cpMR_14_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202409');"><%= cpMR_14_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202410');"><%= cpMR_14_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202411');"><%= cpMR_14_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202412');"><%= cpMR_14_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '2025');"><font color="red"> <%=cpMR_14_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202501');"><%= cpMR_14_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202502');"><%= cpMR_14_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202503');"><%= cpMR_14_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202504');"><%= cpMR_14_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202505');"><%= cpMR_14_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202506');"><%= cpMR_14_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202507');"><%= cpMR_14_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202508');"><%= cpMR_14_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202509');"><%= cpMR_14_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202510');"><%= cpMR_14_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202511');"><%= cpMR_14_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202512');"><%= cpMR_14_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', 'ETC');"><font color="red"><%= cpMR_14_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_17.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', 'total');"><font color="red"> <%=cpMR_17 %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '2024');"><font color="red"> <%=cpMR_17_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202405');"><%= cpMR_17_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202406');"><%= cpMR_17_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202407');"><%= cpMR_17_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202408');"><%= cpMR_17_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202409');"><%= cpMR_17_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202410');"><%= cpMR_17_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202411');"><%= cpMR_17_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202412');"><%= cpMR_17_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '2025');"><font color="red"> <%=cpMR_17_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202501');"><%= cpMR_17_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202502');"><%= cpMR_17_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202503');"><%= cpMR_17_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202504');"><%= cpMR_17_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202505');"><%= cpMR_17_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202506');"><%= cpMR_17_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202507');"><%= cpMR_17_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202508');"><%= cpMR_17_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202509');"><%= cpMR_17_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202510');"><%= cpMR_17_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202511');"><%= cpMR_17_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202512');"><%= cpMR_17_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', 'ETC');"><font color="red"><%= cpMR_17_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', 'total');"><font color="red"> <%=cp2_MRL_General %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '2024');"><font color="red"> <%=cp2_MRL_General_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202405');"><%= cp2_MRL_General_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202406');"><%= cp2_MRL_General_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202407');"><%= cp2_MRL_General_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202408');"><%= cp2_MRL_General_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202409');"><%= cp2_MRL_General_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202410');"><%= cp2_MRL_General_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202411');"><%= cp2_MRL_General_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202412');"><%= cp2_MRL_General_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '2025');"><font color="red"> <%=cp2_MRL_General_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202501');"><%= cp2_MRL_General_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202502');"><%= cp2_MRL_General_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202503');"><%= cp2_MRL_General_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202504');"><%= cp2_MRL_General_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202505');"><%= cp2_MRL_General_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202506');"><%= cp2_MRL_General_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202507');"><%= cp2_MRL_General_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202508');"><%= cp2_MRL_General_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202509');"><%= cp2_MRL_General_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202510');"><%= cp2_MRL_General_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202511');"><%= cp2_MRL_General_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202512');"><%= cp2_MRL_General_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', 'ETC');"><font color="red"><%= cp2_MRL_General_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', 'total');"><font color="red"> <%=cp2_MRL_Revive %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '2024');"><font color="red"> <%=cp2_MRL_Revive_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202405');"><%= cp2_MRL_Revive_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202406');"><%= cp2_MRL_Revive_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202407');"><%= cp2_MRL_Revive_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202408');"><%= cp2_MRL_Revive_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202409');"><%= cp2_MRL_Revive_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202410');"><%= cp2_MRL_Revive_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202411');"><%= cp2_MRL_Revive_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202412');"><%= cp2_MRL_Revive_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '2025');"><font color="red"> <%=cp2_MRL_Revive_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202501');"><%= cp2_MRL_Revive_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202502');"><%= cp2_MRL_Revive_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202503');"><%= cp2_MRL_Revive_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202504');"><%= cp2_MRL_Revive_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202505');"><%= cp2_MRL_Revive_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202506');"><%= cp2_MRL_Revive_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202507');"><%= cp2_MRL_Revive_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202508');"><%= cp2_MRL_Revive_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202509');"><%= cp2_MRL_Revive_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202510');"><%= cp2_MRL_Revive_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202511');"><%= cp2_MRL_Revive_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202512');"><%= cp2_MRL_Revive_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', 'ETC');"><font color="red"><%= cp2_MRL_Revive_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', 'total');"><font color="red"> <%=cp2_MR_General %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '2024');"><font color="red"> <%=cp2_MR_General_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202405');"><%= cp2_MR_General_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202406');"><%= cp2_MR_General_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202407');"><%= cp2_MR_General_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202408');"><%= cp2_MR_General_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202409');"><%= cp2_MR_General_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202410');"><%= cp2_MR_General_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202411');"><%= cp2_MR_General_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202412');"><%= cp2_MR_General_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '2025');"><font color="red"> <%=cp2_MR_General_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202501');"><%= cp2_MR_General_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202502');"><%= cp2_MR_General_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202503');"><%= cp2_MR_General_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202504');"><%= cp2_MR_General_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202505');"><%= cp2_MR_General_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202506');"><%= cp2_MR_General_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202507');"><%= cp2_MR_General_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202508');"><%= cp2_MR_General_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202509');"><%= cp2_MR_General_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202510');"><%= cp2_MR_General_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202511');"><%= cp2_MR_General_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202512');"><%= cp2_MR_General_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', 'ETC');"><font color="red"><%= cp2_MR_General_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', 'total');"><font color="red"> <%=cp2_MR_Revive %> </font> </a> </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '2024');"><font color="red"> <%=cp2_MR_Revive_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202405');"><%= cp2_MR_Revive_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202406');"><%= cp2_MR_Revive_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202407');"><%= cp2_MR_Revive_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202408');"><%= cp2_MR_Revive_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202409');"><%= cp2_MR_Revive_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202410');"><%= cp2_MR_Revive_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202411');"><%= cp2_MR_Revive_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202412');"><%= cp2_MR_Revive_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '2025');"><font color="red"> <%=cp2_MR_Revive_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202501');"><%= cp2_MR_Revive_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202502');"><%= cp2_MR_Revive_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202503');"><%= cp2_MR_Revive_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202504');"><%= cp2_MR_Revive_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202505');"><%= cp2_MR_Revive_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202506');"><%= cp2_MR_Revive_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202507');"><%= cp2_MR_Revive_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202508');"><%= cp2_MR_Revive_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202509');"><%= cp2_MR_Revive_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202510');"><%= cp2_MR_Revive_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202511');"><%= cp2_MR_Revive_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202512');"><%= cp2_MR_Revive_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', 'ETC');"><font color="red"><%= cp2_MR_Revive_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(Belt Type)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', 'total');"><font color="red"> <%=TM %></font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '2024');"><font color="red"> <%=TM_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202405');"><%= TM_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202406');"><%= TM_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202407');"><%= TM_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202408');"><%= TM_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202409');"><%= TM_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202410');"><%= TM_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202411');"><%= TM_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202412');"><%= TM_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '2025');"><font color="red"> <%=TM_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202501');"><%= TM_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202502');"><%= TM_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202503');"><%= TM_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202504');"><%= TM_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202505');"><%= TM_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202506');"><%= TM_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202507');"><%= TM_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202508');"><%= TM_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202509');"><%= TM_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202510');"><%= TM_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202511');"><%= TM_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202512');"><%= TM_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', 'ETC');"><font color="red"><%= TM_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(ROPE)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', 'total');"><font color="red"> <%=TMRope %></font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '2024');"><font color="red"> <%=TMRope_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202405');"><%= TMRope_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202406');"><%= TMRope_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202407');"><%= TMRope_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202408');"><%= TMRope_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202409');"><%= TMRope_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202410');"><%= TMRope_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202411');"><%= TMRope_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202412');"><%= TMRope_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '2025');"><font color="red"> <%=TMRope_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202501');"><%= TMRope_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202502');"><%= TMRope_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202503');"><%= TMRope_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202504');"><%= TMRope_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202505');"><%= TMRope_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202506');"><%= TMRope_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202507');"><%= TMRope_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202508');"><%= TMRope_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202509');"><%= TMRope_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202510');"><%= TMRope_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202511');"><%= TMRope_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202512');"><%= TMRope_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', 'ETC');"><font color="red"><%= TMRope_ETC %><font color="red"></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Car Top Box</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', 'total');"><font color="red"><%=CARTOP %></font> </a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '2024');"><font color="red"> <%=CARTOP_2024 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202405');"><%= CARTOP_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202406');"><%= CARTOP_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202407');"><%= CARTOP_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202408');"><%= CARTOP_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202409');"><%= CARTOP_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202410');"><%= CARTOP_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202411');"><%= CARTOP_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202412');"><%= CARTOP_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '2025');"><font color="red"> <%=CARTOP_2025 %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202501');"><%= CARTOP_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202502');"><%= CARTOP_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202503');"><%= CARTOP_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202504');"><%= CARTOP_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202505');"><%= CARTOP_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202506');"><%= CARTOP_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202507');"><%= CARTOP_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202508');"><%= CARTOP_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202509');"><%= CARTOP_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202510');"><%= CARTOP_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202511');"><%= CARTOP_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202512');"><%= CARTOP_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', 'ETC');"><font color="red"><%= CARTOP_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Governor</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', 'total');"><font color="red"><%=GOV %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '2024');"><font color="red"><%=GOV_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202405');"><%= GOV_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202406');"><%= GOV_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202407');"><%= GOV_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202408');"><%= GOV_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202409');"><%= GOV_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202410');"><%= GOV_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202411');"><%= GOV_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202412');"><%= GOV_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '2025');"><font color="red"><%=GOV_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202501');"><%= GOV_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202502');"><%= GOV_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202503');"><%= GOV_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202504');"><%= GOV_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202505');"><%= GOV_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202506');"><%= GOV_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202507');"><%= GOV_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202508');"><%= GOV_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202509');"><%= GOV_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202510');"><%= GOV_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202511');"><%= GOV_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202512');"><%= GOV_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', 'ETC');"><font color="red"><%= GOV_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> 승강로 LAMP</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', 'total');"><font color="red"><%=LAMP %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '2024');"><font color="red"><%=LAMP_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202405');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202406');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202407');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202408');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202409');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202410');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202411');"><%= 0 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202412');"><%= LAMP_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '2025');"><font color="red"><%=LAMP_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202501');"><%= LAMP_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202502');"><%= LAMP_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202503');"><%= LAMP_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202504');"><%= LAMP_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202505');"><%= LAMP_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202506');"><%= LAMP_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202507');"><%= LAMP_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202508');"><%= LAMP_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202509');"><%= LAMP_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202510');"><%= LAMP_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202511');"><%= LAMP_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', '202512');"><%= LAMP_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP', 'ETC');"><font color="red"><%= LAMP_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> PIT </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', 'total');"><font color="red"><%=PIT %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '2024');"><font color="red"><%=PIT_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202405');"><%= PIT_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202406');"><%= PIT_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202407');"><%= PIT_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202408');"><%= PIT_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202409');"><%= PIT_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202410');"><%= PIT_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202411');"><%= PIT_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202412');"><%= PIT_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '2025');"><font color="red"><%=PIT_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202501');"><%= PIT_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202502');"><%= PIT_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202503');"><%= PIT_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202504');"><%= PIT_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202505');"><%= PIT_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202506');"><%= PIT_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202507');"><%= PIT_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202508');"><%= PIT_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202509');"><%= PIT_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202510');"><%= PIT_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202511');"><%= PIT_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202512');"><%= PIT_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', 'ETC');"><font color="red"><%= PIT_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">HPB(J21)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', 'total');"><font color="red"><%=HPB %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '2024');"><font color="red"><%=HPB_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202405');"><%= HPB_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202406');"><%= HPB_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202407');"><%= HPB_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202408');"><%= HPB_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202409');"><%= HPB_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202410');"><%= HPB_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202411');"><%= HPB_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202412');"><%= HPB_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '2025');"><font color="red"><%=HPB_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202501');"><%= HPB_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202502');"><%= HPB_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202503');"><%= HPB_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202504');"><%= HPB_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202505');"><%= HPB_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202506');"><%= HPB_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202507');"><%= HPB_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202508');"><%= HPB_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202509');"><%= HPB_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202510');"><%= HPB_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202511');"><%= HPB_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', '202512');"><%= HPB_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB', 'ETC');"><font color="red"><%= HPB_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SJ21) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', 'total');"><font color="red"><%=HIP %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '2024');"><font color="red"><%=HIP_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202405');"><%= HIP_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202406');"><%= HIP_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202407');"><%= HIP_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202408');"><%= HIP_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202409');"><%= HIP_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202410');"><%= HIP_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202411');"><%= HIP_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202412');"><%= HIP_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '2025');"><font color="red"><%=HIP_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202501');"><%= HIP_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202502');"><%= HIP_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202503');"><%= HIP_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202504');"><%= HIP_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202505');"><%= HIP_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202506');"><%= HIP_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202507');"><%= HIP_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202508');"><%= HIP_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202509');"><%= HIP_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202510');"><%= HIP_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202511');"><%= HIP_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', '202512');"><%= HIP_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP', 'ETC');"><font color="red"><%= HIP_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (D521AG) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', 'total');"><font color="red"><%=OPB_D521AG %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '2024');"><font color="red"><%=OPB_D521AG_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202405');"><%= OPB_D521AG_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202406');"><%= OPB_D521AG_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202407');"><%= OPB_D521AG_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202408');"><%= OPB_D521AG_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202409');"><%= OPB_D521AG_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202410');"><%= OPB_D521AG_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202411');"><%= OPB_D521AG_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202412');"><%= OPB_D521AG_12 %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '2025');"><font color="red"><%=OPB_D521AG_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202501');"><%= OPB_D521AG_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202502');"><%= OPB_D521AG_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202503');"><%= OPB_D521AG_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202504');"><%= OPB_D521AG_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202505');"><%= OPB_D521AG_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202506');"><%= OPB_D521AG_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202507');"><%= OPB_D521AG_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202508');"><%= OPB_D521AG_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202509');"><%= OPB_D521AG_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202510');"><%= OPB_D521AG_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202511');"><%= OPB_D521AG_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202512');"><%= OPB_D521AG_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', 'ETC');"><font color="red"><%= OPB_D521AG_ETC %></font></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (S521A) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'total');"><font color="red"><%=OPB_S521A %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '2024');"><font color="red"><%=OPB_S521A_2024 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202405');"><%= OPB_S521A_05 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202406');"><%= OPB_S521A_06 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202407');"><%= OPB_S521A_07 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202408');"><%= OPB_S521A_08 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202409');"><%= OPB_S521A_09 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202410');"><%= OPB_S521A_10 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202411');"><%= OPB_S521A_11 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202412');"><%= OPB_S521A_12 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '2025');"><font color="red"><%=OPB_S521A_2025 %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202501');"><%= OPB_S521A_13 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202502');"><%= OPB_S521A_14 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202503');"><%= OPB_S521A_15 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202504');"><%= OPB_S521A_16 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202505');"><%= OPB_S521A_17 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202506');"><%= OPB_S521A_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202507');"><%= OPB_S521A_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202508');"><%= OPB_S521A_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202509');"><%= OPB_S521A_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202510');"><%= OPB_S521A_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202511');"><%= OPB_S521A_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202512');"><%= OPB_S521A_24 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'ETC');"><font color="red"><%= OPB_S521A_ETC %></font></a></td>
                                    </tr>


                                    <%
                                        DashDtoV3 HPI_S700 = jdcbService.findByIdV3(todayVal, "HPI_S700");
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPI(S700) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'total');"><font color="red"><%=HPI_S700.getTotal() %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '2024');"><font color="red"><%=HPI_S700.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202405');"><%= HPI_S700.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202406');"><%= HPI_S700.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202407');"><%= HPI_S700.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202408');"><%= HPI_S700.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202409');"><%= HPI_S700.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202410');"><%= HPI_S700.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202411');"><%= HPI_S700.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202412');"><%= HPI_S700.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '2025');"><font color="red"><%=HPI_S700.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202501');"><%= HPI_S700.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202502');"><%= HPI_S700.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202503');"><%= HPI_S700.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202504');"><%= HPI_S700.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202505');"><%= HPI_S700.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202506');"><%= HPI_S700.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202507');"><%= HPI_S700.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202508');"><%= HPI_S700.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202509');"><%= HPI_S700.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202510');"><%= HPI_S700.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202511');"><%= HPI_S700.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202512');"><%= HPI_S700.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'ETC');"><font color="red"><%= HPI_S700.getExport_etc() %></font></a></td>
                                    </tr>



                                    <%
                                        DashDtoV3 HPI_SC = jdcbService.findByIdV3(todayVal, "HPI_SC");
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPI(SC) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', 'total');"><font color="red"><%=HPI_SC.getTotal() %> </font></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '2024');"><font color="red"><%=HPI_SC.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202405');"><%= HPI_SC.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202406');"><%= HPI_SC.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202407');"><%= HPI_SC.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202408');"><%= HPI_SC.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202409');"><%= HPI_SC.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202410');"><%= HPI_SC.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202411');"><%= HPI_SC.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202412');"><%= HPI_SC.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '2025');"><font color="red"><%=HPI_SC.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202501');"><%= HPI_SC.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202502');"><%= HPI_SC.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202503');"><%= HPI_SC.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202504');"><%= HPI_SC.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202505');"><%= HPI_SC.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202506');"><%= HPI_SC.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202507');"><%= HPI_SC.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202508');"><%= HPI_SC.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202509');"><%= HPI_SC.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202510');"><%= HPI_SC.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202511');"><%= HPI_SC.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202512');"><%= HPI_SC.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', 'ETC');"><font color="red"><%= HPI_SC.getExport_etc() %></font></a></td>
                                    </tr>



                                    <%
                                        DashDtoV3 HPB_K21_TOP = jdcbService.findByIdV3(todayVal, "HPB_K21_TOP");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', 'total');"><font color="red"><%=HPB_K21_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '2024');"><font color="red"><%=HPB_K21_TOP.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202405');"><%= HPB_K21_TOP.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202406');"><%= HPB_K21_TOP.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202407');"><%= HPB_K21_TOP.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202408');"><%= HPB_K21_TOP.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202409');"><%= HPB_K21_TOP.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202410');"><%= HPB_K21_TOP.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202411');"><%= HPB_K21_TOP.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202412');"><%= HPB_K21_TOP.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '2025');"><font color="red"><%=HPB_K21_TOP.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202501');"><%= HPB_K21_TOP.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202502');"><%= HPB_K21_TOP.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202503');"><%= HPB_K21_TOP.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202504');"><%= HPB_K21_TOP.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202505');"><%= HPB_K21_TOP.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202506');"><%= HPB_K21_TOP.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202507');"><%= HPB_K21_TOP.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202508');"><%= HPB_K21_TOP.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202509');"><%= HPB_K21_TOP.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202510');"><%= HPB_K21_TOP.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202511');"><%= HPB_K21_TOP.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202512');"><%= HPB_K21_TOP.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', 'ETC');"><font color="red"><%= HPB_K21_TOP.getExport_etc() %></font></a></td>
                                    </tr>

                                    <%
                                        DashDtoV3 HPB_K21_MID = jdcbService.findByIdV3(todayVal, "HPB_K21_MID");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', 'total');"><font color="red"><%=HPB_K21_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '2024');"><font color="red"><%=HPB_K21_MID.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202405');"><%= HPB_K21_MID.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202406');"><%= HPB_K21_MID.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202407');"><%= HPB_K21_MID.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202408');"><%= HPB_K21_MID.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202409');"><%= HPB_K21_MID.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202410');"><%= HPB_K21_MID.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202411');"><%= HPB_K21_MID.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202412');"><%= HPB_K21_MID.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '2025');"><font color="red"><%=HPB_K21_MID.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202501');"><%= HPB_K21_MID.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202502');"><%= HPB_K21_MID.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202503');"><%= HPB_K21_MID.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202504');"><%= HPB_K21_MID.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202505');"><%= HPB_K21_MID.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202506');"><%= HPB_K21_MID.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202507');"><%= HPB_K21_MID.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202508');"><%= HPB_K21_MID.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202509');"><%= HPB_K21_MID.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202510');"><%= HPB_K21_MID.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202511');"><%= HPB_K21_MID.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202512');"><%= HPB_K21_MID.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', 'ETC');"><font color="red"><%= HPB_K21_MID.getExport_etc() %></font></a></td>
                                    </tr>

                                    <%
                                        DashDtoV3 HPB_K21_BOT = jdcbService.findByIdV3(todayVal, "HPB_K21_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', 'total');"><font color="red"><%=HPB_K21_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '2024');"><font color="red"><%=HPB_K21_BOT.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202405');"><%= HPB_K21_BOT.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202406');"><%= HPB_K21_BOT.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202407');"><%= HPB_K21_BOT.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202408');"><%= HPB_K21_BOT.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202409');"><%= HPB_K21_BOT.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202410');"><%= HPB_K21_BOT.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202411');"><%= HPB_K21_BOT.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202412');"><%= HPB_K21_BOT.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '2025');"><font color="red"><%=HPB_K21_BOT.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202501');"><%= HPB_K21_BOT.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202502');"><%= HPB_K21_BOT.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202503');"><%= HPB_K21_BOT.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202504');"><%= HPB_K21_BOT.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202505');"><%= HPB_K21_BOT.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202506');"><%= HPB_K21_BOT.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202507');"><%= HPB_K21_BOT.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202508');"><%= HPB_K21_BOT.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202509');"><%= HPB_K21_BOT.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202510');"><%= HPB_K21_BOT.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202511');"><%= HPB_K21_BOT.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202512');"><%= HPB_K21_BOT.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', 'ETC');"><font color="red"><%= HPB_K21_BOT.getExport_etc() %></font></a></td>
                                    </tr>


                                    <%
                                        DashDtoV3 HPB_K21A_TOP = jdcbService.findByIdV3(todayVal, "HPB_K21A_TOP");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', 'total');"><font color="red"><%=HPB_K21A_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '2024');"><font color="red"><%=HPB_K21A_TOP.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202405');"><%= HPB_K21A_TOP.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202406');"><%= HPB_K21A_TOP.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202407');"><%= HPB_K21A_TOP.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202408');"><%= HPB_K21A_TOP.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202409');"><%= HPB_K21A_TOP.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202410');"><%= HPB_K21A_TOP.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202411');"><%= HPB_K21A_TOP.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202412');"><%= HPB_K21A_TOP.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '2025');"><font color="red"><%=HPB_K21A_TOP.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202501');"><%= HPB_K21A_TOP.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202502');"><%= HPB_K21A_TOP.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202503');"><%= HPB_K21A_TOP.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202504');"><%= HPB_K21A_TOP.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202505');"><%= HPB_K21A_TOP.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202506');"><%= HPB_K21A_TOP.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202507');"><%= HPB_K21A_TOP.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202508');"><%= HPB_K21A_TOP.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202509');"><%= HPB_K21A_TOP.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202510');"><%= HPB_K21A_TOP.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202511');"><%= HPB_K21A_TOP.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202512');"><%= HPB_K21A_TOP.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', 'ETC');"><font color="red"><%= HPB_K21A_TOP.getExport_etc() %></font></a></td>
                                    </tr>

                                    <%
                                        DashDtoV3 HPB_K21A_MID = jdcbService.findByIdV3(todayVal, "HPB_K21A_MID");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', 'total');"><font color="red"><%=HPB_K21A_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '2024');"><font color="red"><%=HPB_K21A_MID.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202405');"><%= HPB_K21A_MID.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202406');"><%= HPB_K21A_MID.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202407');"><%= HPB_K21A_MID.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202408');"><%= HPB_K21A_MID.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202409');"><%= HPB_K21A_MID.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202410');"><%= HPB_K21A_MID.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202411');"><%= HPB_K21A_MID.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202412');"><%= HPB_K21A_MID.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '2025');"><font color="red"><%=HPB_K21A_MID.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202501');"><%= HPB_K21A_MID.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202502');"><%= HPB_K21A_MID.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202503');"><%= HPB_K21A_MID.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202504');"><%= HPB_K21A_MID.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202505');"><%= HPB_K21A_MID.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202506');"><%= HPB_K21A_MID.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202507');"><%= HPB_K21A_MID.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202508');"><%= HPB_K21A_MID.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202509');"><%= HPB_K21A_MID.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202510');"><%= HPB_K21A_MID.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202511');"><%= HPB_K21A_MID.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202512');"><%= HPB_K21A_MID.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', 'ETC');"><font color="red"><%= HPB_K21A_MID.getExport_etc() %></font></a></td>
                                    </tr>

                                    <%
                                        DashDtoV3 HPB_K21A_BOT = jdcbService.findByIdV3(todayVal, "HPB_K21A_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', 'total');"><font color="red"><%=HPB_K21A_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '2024');"><font color="red"><%=HPB_K21A_BOT.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202405');"><%= HPB_K21A_BOT.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202406');"><%= HPB_K21A_BOT.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202407');"><%= HPB_K21A_BOT.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202408');"><%= HPB_K21A_BOT.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202409');"><%= HPB_K21A_BOT.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202410');"><%= HPB_K21A_BOT.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202411');"><%= HPB_K21A_BOT.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202412');"><%= HPB_K21A_BOT.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '2025');"><font color="red"><%=HPB_K21A_BOT.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202501');"><%= HPB_K21A_BOT.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202502');"><%= HPB_K21A_BOT.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202503');"><%= HPB_K21A_BOT.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202504');"><%= HPB_K21A_BOT.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202505');"><%= HPB_K21A_BOT.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202506');"><%= HPB_K21A_BOT.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202507');"><%= HPB_K21A_BOT.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202508');"><%= HPB_K21A_BOT.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202509');"><%= HPB_K21A_BOT.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202510');"><%= HPB_K21A_BOT.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202511');"><%= HPB_K21A_BOT.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202512');"><%= HPB_K21A_BOT.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', 'ETC');"><font color="red"><%= HPB_K21A_BOT.getExport_etc() %></font></a></td>
                                    </tr>


                                    <%
                                        DashDtoV3 HIP_SK21_TOP = jdcbService.findByIdV3(todayVal, "HIP_SK21_TOP");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', 'total');"><font color="red"><%=HIP_SK21_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '2024');"><font color="red"><%=HIP_SK21_TOP.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202405');"><%= HIP_SK21_TOP.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202406');"><%= HIP_SK21_TOP.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202407');"><%= HIP_SK21_TOP.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202408');"><%= HIP_SK21_TOP.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202409');"><%= HIP_SK21_TOP.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202410');"><%= HIP_SK21_TOP.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202411');"><%= HIP_SK21_TOP.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202412');"><%= HIP_SK21_TOP.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '2025');"><font color="red"><%=HIP_SK21_TOP.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202501');"><%= HIP_SK21_TOP.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202502');"><%= HIP_SK21_TOP.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202503');"><%= HIP_SK21_TOP.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202504');"><%= HIP_SK21_TOP.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202505');"><%= HIP_SK21_TOP.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202506');"><%= HIP_SK21_TOP.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202507');"><%= HIP_SK21_TOP.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202508');"><%= HIP_SK21_TOP.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202509');"><%= HIP_SK21_TOP.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202510');"><%= HIP_SK21_TOP.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202511');"><%= HIP_SK21_TOP.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202512');"><%= HIP_SK21_TOP.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', 'ETC');"><font color="red"><%= HIP_SK21_TOP.getExport_etc() %></font></a></td>
                                    </tr>


                                    <%
                                        DashDtoV3 HIP_SK21_MID = jdcbService.findByIdV3(todayVal, "HIP_SK21_MID");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', 'total');"><font color="red"><%=HIP_SK21_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '2024');"><font color="red"><%=HIP_SK21_MID.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202405');"><%= HIP_SK21_MID.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202406');"><%= HIP_SK21_MID.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202407');"><%= HIP_SK21_MID.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202408');"><%= HIP_SK21_MID.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202409');"><%= HIP_SK21_MID.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202410');"><%= HIP_SK21_MID.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202411');"><%= HIP_SK21_MID.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202412');"><%= HIP_SK21_MID.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '2025');"><font color="red"><%=HIP_SK21_MID.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202501');"><%= HIP_SK21_MID.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202502');"><%= HIP_SK21_MID.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202503');"><%= HIP_SK21_MID.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202504');"><%= HIP_SK21_MID.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202505');"><%= HIP_SK21_MID.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202506');"><%= HIP_SK21_MID.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202507');"><%= HIP_SK21_MID.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202508');"><%= HIP_SK21_MID.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202509');"><%= HIP_SK21_MID.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202510');"><%= HIP_SK21_MID.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202511');"><%= HIP_SK21_MID.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202512');"><%= HIP_SK21_MID.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', 'ETC');"><font color="red"><%= HIP_SK21_MID.getExport_etc() %></font></a></td>
                                    </tr>


                                    <%
                                        DashDtoV3 HIP_SK21_BOT = jdcbService.findByIdV3(todayVal, "HIP_SK21_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', 'total');"><font color="red"><%=HIP_SK21_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '2024');"><font color="red"><%=HIP_SK21_BOT.getTotal2024() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202405');"><%= HIP_SK21_BOT.getExport202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202406');"><%= HIP_SK21_BOT.getExport202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202407');"><%= HIP_SK21_BOT.getExport202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202408');"><%= HIP_SK21_BOT.getExport202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202409');"><%= HIP_SK21_BOT.getExport202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202410');"><%= HIP_SK21_BOT.getExport202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202411');"><%= HIP_SK21_BOT.getExport202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202412');"><%= HIP_SK21_BOT.getExport202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '2025');"><font color="red"><%=HIP_SK21_BOT.getTotal2025() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202501');"><%= HIP_SK21_BOT.getExport202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202502');"><%= HIP_SK21_BOT.getExport202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202503');"><%= HIP_SK21_BOT.getExport202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202504');"><%= HIP_SK21_BOT.getExport202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202505');"><%= HIP_SK21_BOT.getExport202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202506');"><%= HIP_SK21_BOT.getExport202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202507');"><%= HIP_SK21_BOT.getExport202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202508');"><%= HIP_SK21_BOT.getExport202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202509');"><%= HIP_SK21_BOT.getExport202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202510');"><%= HIP_SK21_BOT.getExport202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202511');"><%= HIP_SK21_BOT.getExport202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202512');"><%= HIP_SK21_BOT.getExport202512() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', 'ETC');"><font color="red"><%= HIP_SK21_BOT.getExport_etc() %></font></a></td>
                                    </tr>

                                    </tbody>

                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div>
                        <!-- /.card -->
                    </div>
                    <!-- </section> -->
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
        <strong>Copyright &copy; 2025 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
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


    }); // end document ready


    //출하예정일 기준 대시보드 -> 팝업
    function viewList(partType, viewDate) {  //(cpMRL_5, 2024)

        let todayVal = '<%=todayVal %>';

        //http://10.225.4.20/jsp/searchLogic/searchPriceReductionRate.jsp

        //let urlValue = "http://10.225.4.20/jsp/searchLogic/searchPriceReductionDatePop.jsp?";

        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionDatePop.jsp?";
        let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionDatePop.jsp?";

        urlValue += "curDate=" + viewDate;
        urlValue += "&todayVal=" + todayVal;
        urlValue += "&partType=" + partType;

        window.open(urlValue,'_blank','width=1600, height=800, top=50, left=50, scrollbars=yes');
    }


</script>

</html>
