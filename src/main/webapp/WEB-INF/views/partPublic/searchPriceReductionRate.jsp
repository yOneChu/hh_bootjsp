<%@page import="java.time.LocalDate"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.kyhslam.service.JdbcTestService" %>
<%@ page import="com.kyhslam.dto.DashDto" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //부품공용화 수량 - 대시보드 개발
    // searchPriceReductionRate.jsp
    // http://10.225.4.20/jsp/searchLogic/searchPriceReductionRate.jsp
    // http://localhost/jsp/searchLogic/searchPriceReductionRate.jsp

    //http://10.225.4.20/jsp/searchLogic/searchPriceReductionRate.jsp

    String contextPath = request.getContextPath();
    System.out.println("--- searchPriceReductionRate.jsp ---");

    ArrayList<HashMap<String, String>> dashList = new ArrayList<HashMap<String, String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;

    // ServletContext에서 Spring WebApplicationContext 얻기
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // 원하는 Bean 가져오기
    JdbcTestService jdcbService = (JdbcTestService) context.getBean("JdbcTestService");


    System.out.println("jdcbService = " + jdcbService);


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



    int LAMP 	= 0;
    int LAMP_05 = 0;
    int LAMP_06 = 0;
    int LAMP_07 = 0;
    int LAMP_08 = 0;
    int LAMP_09 = 0;
    int LAMP_10 = 0;
    int LAMP_11 = 0;
    int LAMP_12 = 0;
    int LAMP_13 = 0; //202501
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

    int HPB    = 0;
    int HPB_05 = 0;
    int HPB_06 = 0;
    int HPB_07 = 0;
    int HPB_08 = 0;
    int HPB_09 = 0;
    int HPB_10 = 0;
    int HPB_11 = 0;
    int HPB_12 = 0;
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

    int HIP    = 0;
    int HIP_05 = 0;
    int HIP_06 = 0;
    int HIP_07 = 0;
    int HIP_08 = 0;
    int HIP_09 = 0;
    int HIP_10 = 0;
    int HIP_11 = 0;
    int HIP_12 = 0;
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


    LocalDate now = LocalDate.now();
    String todayVal = now.toString();


    String export05 = "";
    String export06 = "";
    String export07 = "";
    String export08 = "";
    String export09 = "";
    String export10 = "";
    String export11 = "";
    String export12 = "";
    String export13 = "";
    String export14 = "";
    String export15 = "";
    String export16 = "";
    String export17 = "";

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
        sql.append(" A.price202405, A.price202406, A.price202407, A.price202408, A.price202409, ");
        sql.append(" A.price202410, A.price202411, A.price202412, ");
        sql.append(" A.price202501, A.price202502, A.price202503, A.price202504, A.price202505, ");
        sql.append(" A.price202506, A.price202507, A.price202508, A.price202509, A.price202510, A.price202511, A.price202512 ");
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

            String DIS202405 = rs.getString("price202405") == null ? "" : rs.getString("price202405");
            String DIS202406 = rs.getString("price202406") == null ? "" : rs.getString("price202406");
            String DIS202407 = rs.getString("price202407") == null ? "" : rs.getString("price202407");
            String DIS202408 = rs.getString("price202408") == null ? "" : rs.getString("price202408");
            String DIS202409 = rs.getString("price202409") == null ? "" : rs.getString("price202409");
            String DIS202410 = rs.getString("price202410") == null ? "" : rs.getString("price202410");
            String DIS202411 = rs.getString("price202411") == null ? "" : rs.getString("price202411");
            String DIS202412 = rs.getString("price202412") == null ? "" : rs.getString("price202412");

            String DIS202501 = rs.getString("price202501") == null ? "" : rs.getString("price202501");
            String DIS202502 = rs.getString("price202502") == null ? "" : rs.getString("price202502");
            String DIS202503 = rs.getString("price202503") == null ? "" : rs.getString("price202503");
            String DIS202504 = rs.getString("price202504") == null ? "" : rs.getString("price202504");
            String DIS202505 = rs.getString("price202505") == null ? "" : rs.getString("price202505");
            String DIS202506 = rs.getString("price202506") == null ? "" : rs.getString("price202506");
            String DIS202507 = rs.getString("price202507") == null ? "" : rs.getString("price202507");
            String DIS202508 = rs.getString("price202508") == null ? "" : rs.getString("price202508");
            String DIS202509 = rs.getString("price202509") == null ? "" : rs.getString("price202509");
            String DIS202510 = rs.getString("price202510") == null ? "" : rs.getString("price202510");
            String DIS202511 = rs.getString("price202511") == null ? "" : rs.getString("price202511");
            String DIS202512 = rs.getString("price202512") == null ? "" : rs.getString("price202512");


           /* export05 = rs.getString("export05") == null ? "" : rs.getString("export05");
            export06 = rs.getString("export06") == null ? "" : rs.getString("export06");
            export07 = rs.getString("export07") == null ? "" : rs.getString("export07");
            export08 = rs.getString("export08") == null ? "" : rs.getString("export08");
            export09 = rs.getString("export09") == null ? "" : rs.getString("export09");
            export10 = rs.getString("export10") == null ? "" : rs.getString("export10");
            export11 = rs.getString("export11") == null ? "" : rs.getString("export11");
            export12 = rs.getString("export12") == null ? "" : rs.getString("export12");
            export13 = rs.getString("export13") == null ? "" : rs.getString("export13");
            export14 = rs.getString("export14") == null ? "" : rs.getString("export14");
            export15 = rs.getString("export15") == null ? "" : rs.getString("export15");
            export16 = rs.getString("export16") == null ? "" : rs.getString("export16");
            export17 = rs.getString("export17") == null ? "" : rs.getString("export17");*/

            int totalCnt = 0;

            totalCnt = Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                    + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412)
                    + Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505);

            if(part_name != null && !"".equals(part_name)) {
                part_name = part_name.trim();
            }

            if("LAMP_HOIST".equals(part_name) || part_name.startsWith("LAMP")) {
                LAMP    += totalCnt;
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
            } else if("HPB_BOT".equals(part_name) || part_name.contains("HPB_MID") || part_name.contains("HPB_TOP")) {
                HPB    += totalCnt;
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
            } else if("HIP_BOT".equals(part_name) || part_name.equals("HIP_TOP") || part_name.equals("HIP_MID")) {
                HIP    += totalCnt;
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
                        <h1>부품공용화 - 월별실적(수량) <font color="red">( <%=todayVal %>, 06:00기준)</font> </h1>
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
                    <%--<div class="col-lg-7">--%>
                        <!-- <section class="col-lg-6 connectedSortable ui-sortable"> -->

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">Dashboard(수량)</h3>
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
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">총 수량</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="20">월별실적</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" colspan="8">2024년</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="12">2025년</th>
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
                                        <th style="font-weight: bold; text-align: center;">06</th>
                                        <th style="font-weight: bold; text-align: center;">07</th>
                                        <th style="font-weight: bold; text-align: center;">08</th>
                                        <th style="font-weight: bold; text-align: center;">09</th>
                                        <th style="font-weight: bold; text-align: center;">10</th>
                                        <th style="font-weight: bold; text-align: center;">11</th>
                                        <th style="font-weight: bold; text-align: center;">12</th>
                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <%
                                        DashDto cpMRL_5_dto = jdcbService.findById("cpMRL_5", todayVal);
                                        System.out.println("cpMRL_5_dto = " + cpMRL_5_dto);
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MRL_5.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', 'total');"> <font color="red"> <%=cpMRL_5_dto.getTotal() %> </font> </a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202405');"><%= cpMRL_5_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202406');"><%= cpMRL_5_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202407');"><%= cpMRL_5_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202408');"><%= cpMRL_5_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202409');"><%= cpMRL_5_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202410');"><%= cpMRL_5_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202411');"><%= cpMRL_5_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202412');"><%= cpMRL_5_dto.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202501');"><%= cpMRL_5_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202502');"><%= cpMRL_5_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202503');"><%= cpMRL_5_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202504');"><%= cpMRL_5_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202505');"><%= cpMRL_5_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202506');"><%= cpMRL_5_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202507');"><%= cpMRL_5_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202508');"><%= cpMRL_5_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202509');"><%= cpMRL_5_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202510');"><%= cpMRL_5_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202511');"><%= cpMRL_5_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202512');"><%= cpMRL_5_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        //cpMRL_9
                                        DashDto cpMRL_9_dto = jdcbService.findById("cpMRL_9", todayVal);
                                        System.out.println("cpMRL_9_dto = " + cpMRL_9_dto);
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MRL_9kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', 'total');"><font color="red"> <%=cpMRL_9_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202405');"><%= cpMRL_9_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202406');"><%= cpMRL_9_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202407');"><%= cpMRL_9_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202408');"><%= cpMRL_9_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202409');"><%= cpMRL_9_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202410');"><%= cpMRL_9_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202411');"><%= cpMRL_9_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202412');"><%= cpMRL_9_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202501');"><%= cpMRL_9_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202502');"><%= cpMRL_9_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202503');"><%= cpMRL_9_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202504');"><%= cpMRL_9_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202505');"><%= cpMRL_9_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202506');"><%= cpMRL_9_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202507');"><%= cpMRL_9_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202508');"><%= cpMRL_9_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202509');"><%= cpMRL_9_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202510');"><%= cpMRL_9_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202511');"><%= cpMRL_9_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202512');"><%= cpMRL_9_dto.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto cpMRL_14_dto = jdcbService.findById("cpMRL_14", todayVal);
                                        System.out.println("cpMRL_14_dto = " + cpMRL_14_dto);
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MRL_14kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', 'total');"><font color="red"> <%=cpMRL_14_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202405');"><%= cpMRL_14_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202406');"><%= cpMRL_14_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202407');"><%= cpMRL_14_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202408');"><%= cpMRL_14_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202409');"><%= cpMRL_14_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202410');"><%= cpMRL_14_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202411');"><%= cpMRL_14_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202412');"><%= cpMRL_14_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202501');"><%= cpMRL_14_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202502');"><%= cpMRL_14_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202503');"><%= cpMRL_14_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202504');"><%= cpMRL_14_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202505');"><%= cpMRL_14_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202506');"><%= cpMRL_14_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202507');"><%= cpMRL_14_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202508');"><%= cpMRL_14_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202509');"><%= cpMRL_14_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202510');"><%= cpMRL_14_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202511');"><%= cpMRL_14_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202512');"><%= cpMRL_14_dto.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto cpMRL_17_dto = jdcbService.findById("cpMRL_17", todayVal);
                                        System.out.println("cpMRL_17_dto = " + cpMRL_17_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MRL_17.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', 'total');"><font color="red"> <%=cpMRL_17_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202405');"><%= cpMRL_17_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202406');"><%= cpMRL_17_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202407');"><%= cpMRL_17_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202408');"><%= cpMRL_17_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202409');"><%= cpMRL_17_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202410');"><%= cpMRL_17_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202411');"><%= cpMRL_17_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202412');"><%= cpMRL_17_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202501');"><%= cpMRL_17_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202502');"><%= cpMRL_17_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202503');"><%= cpMRL_17_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202504');"><%= cpMRL_17_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202505');"><%= cpMRL_17_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202506');"><%= cpMRL_17_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202507');"><%= cpMRL_17_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202508');"><%= cpMRL_17_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202509');"><%= cpMRL_17_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202510');"><%= cpMRL_17_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202511');"><%= cpMRL_17_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202512');"><%= cpMRL_17_dto.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto cpMR_5_dto = jdcbService.findById("cpMR_5_5", todayVal);
                                        System.out.println("cpMR_5_dto = " + cpMR_5_dto);
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MR_5.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', 'total');"><font color="red"> <%=cpMR_5_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202405');"><%= cpMR_5_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202406');"><%= cpMR_5_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202407');"><%= cpMR_5_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202408');"><%= cpMR_5_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202409');"><%= cpMR_5_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202410');"><%= cpMR_5_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202411');"><%= cpMR_5_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202412');"><%= cpMR_5_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202501');"><%= cpMR_5_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202502');"><%= cpMR_5_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202503');"><%= cpMR_5_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202504');"><%= cpMR_5_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202505');"><%= cpMR_5_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202506');"><%= cpMR_5_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202507');"><%= cpMR_5_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202508');"><%= cpMR_5_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202509');"><%= cpMR_5_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202510');"><%= cpMR_5_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202511');"><%= cpMR_5_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202512');"><%= cpMR_5_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto cpMR_9_dto = jdcbService.findById("cpMR_9", todayVal);
                                        System.out.println("cpMR_9_dto = " + cpMR_9_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MR_9kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', 'total');"><font color="red"> <%=cpMR_9_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202405');"><%= cpMR_9_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202406');"><%= cpMR_9_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202407');"><%= cpMR_9_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202408');"><%= cpMR_9_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202409');"><%= cpMR_9_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202410');"><%= cpMR_9_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202411');"><%= cpMR_9_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202412');"><%= cpMR_9_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202501');"><%= cpMR_9_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202502');"><%= cpMR_9_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202503');"><%= cpMR_9_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202504');"><%= cpMR_9_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202505');"><%= cpMR_9_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202506');"><%= cpMR_9_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202507');"><%= cpMR_9_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202508');"><%= cpMR_9_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202509');"><%= cpMR_9_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202510');"><%= cpMR_9_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202511');"><%= cpMR_9_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202512');"><%= cpMR_9_dto.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto cpMR_14_dto = jdcbService.findById("cpMR_14", todayVal);
                                        System.out.println("cpMR_14_dto = " + cpMR_14_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MR_14kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', 'total');"><font color="red"> <%=cpMR_14_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202405');"><%= cpMR_14_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202406');"><%= cpMR_14_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202407');"><%= cpMR_14_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202408');"><%= cpMR_14_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202409');"><%= cpMR_14_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202410');"><%= cpMR_14_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202411');"><%= cpMR_14_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202412');"><%= cpMR_14_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202501');"><%= cpMR_14_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202502');"><%= cpMR_14_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202503');"><%= cpMR_14_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202504');"><%= cpMR_14_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202505');"><%= cpMR_14_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202506');"><%= cpMR_14_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202507');"><%= cpMR_14_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202508');"><%= cpMR_14_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202509');"><%= cpMR_14_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202510');"><%= cpMR_14_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202511');"><%= cpMR_14_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202512');"><%= cpMR_14_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto cpMR_17_dto = jdcbService.findById("cpMR_17_5", todayVal);
                                        System.out.println("cpMR_17_dto = " + cpMR_17_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">1단계 CP (MR_17.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', 'total');"><font color="red"> <%=cpMR_17_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202405');"><%= cpMR_17_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202406');"><%= cpMR_17_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202407');"><%= cpMR_17_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202408');"><%= cpMR_17_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202409');"><%= cpMR_17_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202410');"><%= cpMR_17_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202411');"><%= cpMR_17_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202412');"><%= cpMR_17_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202501');"><%= cpMR_17_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202502');"><%= cpMR_17_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202503');"><%= cpMR_17_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202504');"><%= cpMR_17_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202505');"><%= cpMR_17_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202506');"><%= cpMR_17_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202507');"><%= cpMR_17_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202508');"><%= cpMR_17_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202509');"><%= cpMR_17_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202510');"><%= cpMR_17_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202511');"><%= cpMR_17_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202512');"><%= cpMR_17_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto cp2_MRL_General_dto = jdcbService.findById("cp2_MRL_General", todayVal);
                                        System.out.println("cp2_MRL_General_dto = " + cp2_MRL_General_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', 'total');"><font color="red"> <%=cp2_MRL_General_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202405');"><%= cp2_MRL_General_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202406');"><%= cp2_MRL_General_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202407');"><%= cp2_MRL_General_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202408');"><%= cp2_MRL_General_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202409');"><%= cp2_MRL_General_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202410');"><%= cp2_MRL_General_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202411');"><%= cp2_MRL_General_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202412');"><%= cp2_MRL_General_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202501');"><%= cp2_MRL_General_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202502');"><%= cp2_MRL_General_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202503');"><%= cp2_MRL_General_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202504');"><%= cp2_MRL_General_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202505');"><%= cp2_MRL_General_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202506');"><%= cp2_MRL_General_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202507');"><%= cp2_MRL_General_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202508');"><%= cp2_MRL_General_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202509');"><%= cp2_MRL_General_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202510');"><%= cp2_MRL_General_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202511');"><%= cp2_MRL_General_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202512');"><%= cp2_MRL_General_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto cp2_MRL_Revive_dto = jdcbService.findById("cp2_MRL_Revive", todayVal);
                                        System.out.println("cp2_MRL_Revive_dto = " + cp2_MRL_Revive_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', 'total');"><font color="red"> <%=cp2_MRL_Revive_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202405');"><%= cp2_MRL_Revive_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202406');"><%= cp2_MRL_Revive_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202407');"><%= cp2_MRL_Revive_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202408');"><%= cp2_MRL_Revive_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202409');"><%= cp2_MRL_Revive_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202410');"><%= cp2_MRL_Revive_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202411');"><%= cp2_MRL_Revive_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202412');"><%= cp2_MRL_Revive_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202501');"><%= cp2_MRL_Revive_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202502');"><%= cp2_MRL_Revive_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202503');"><%= cp2_MRL_Revive_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202504');"><%= cp2_MRL_Revive_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202505');"><%= cp2_MRL_Revive_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202506');"><%= cp2_MRL_Revive_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202507');"><%= cp2_MRL_Revive_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202508');"><%= cp2_MRL_Revive_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202509');"><%= cp2_MRL_Revive_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202510');"><%= cp2_MRL_Revive_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202511');"><%= cp2_MRL_Revive_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202512');"><%= cp2_MRL_Revive_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto cp2_MR_General_dto = jdcbService.findById("cp2_MR_General", todayVal);
                                        System.out.println("cp2_MR_General_dto = " + cp2_MR_General_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', 'total');"><font color="red"> <%=cp2_MR_General_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202405');"><%= cp2_MR_General_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202406');"><%= cp2_MR_General_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202407');"><%= cp2_MR_General_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202408');"><%= cp2_MR_General_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202409');"><%= cp2_MR_General_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202410');"><%= cp2_MR_General_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202411');"><%= cp2_MR_General_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202412');"><%= cp2_MR_General_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202501');"><%= cp2_MR_General_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202502');"><%= cp2_MR_General_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202503');"><%= cp2_MR_General_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202504');"><%= cp2_MR_General_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202505');"><%= cp2_MR_General_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202506');"><%= cp2_MR_General_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202507');"><%= cp2_MR_General_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202508');"><%= cp2_MR_General_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202509');"><%= cp2_MR_General_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202510');"><%= cp2_MR_General_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202511');"><%= cp2_MR_General_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202512');"><%= cp2_MR_General_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto cp2_MR_Revive_dto = jdcbService.findById("cp2_MR_Revive", todayVal);
                                        System.out.println("cp2_MR_Revive_dto = " + cp2_MR_Revive_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', 'total');"><font color="red"> <%=cp2_MR_Revive_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202405');"><%= cp2_MR_Revive_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202406');"><%= cp2_MR_Revive_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202407');"><%= cp2_MR_Revive_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202408');"><%= cp2_MR_Revive_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202409');"><%= cp2_MR_Revive_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202410');"><%= cp2_MR_Revive_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202411');"><%= cp2_MR_Revive_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202412');"><%= cp2_MR_Revive_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202501');"><%= cp2_MR_Revive_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202502');"><%= cp2_MR_Revive_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202503');"><%= cp2_MR_Revive_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202504');"><%= cp2_MR_Revive_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202505');"><%= cp2_MR_Revive_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202506');"><%= cp2_MR_Revive_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202507');"><%= cp2_MR_Revive_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202508');"><%= cp2_MR_Revive_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202509');"><%= cp2_MR_Revive_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202510');"><%= cp2_MR_Revive_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202511');"><%= cp2_MR_Revive_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202512');"><%= cp2_MR_Revive_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto TM_dto = jdcbService.findById("TM", todayVal);
                                        System.out.println("TM_dto = " + TM_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">TM(Belt Type)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', 'total');"><font color="red"> <%=TM_dto.getTotal() %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202405');"><%= TM_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202406');"><%= TM_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202407');"><%= TM_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202408');"><%= TM_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202409');"><%= TM_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202410');"><%= TM_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202411');"><%= TM_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202412');"><%= TM_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202501');"><%= TM_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202502');"><%= TM_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202503');"><%= TM_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202504');"><%= TM_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202505');"><%= TM_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202506');"><%= TM_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202507');"><%= TM_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202508');"><%= TM_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202509');"><%= TM_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202510');"><%= TM_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202511');"><%= TM_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202512');"><%= TM_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto TMRope_dto = jdcbService.findById("TMRope", todayVal);
                                        System.out.println("TMRope_dto = " + TMRope_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(ROPE)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', 'total');"><font color="red"> <%=TMRope_dto.getTotal() %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202405');"><%= TMRope_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202406');"><%= TMRope_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202407');"><%= TMRope_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202408');"><%= TMRope_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202409');"><%= TMRope_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202410');"><%= TMRope_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202411');"><%= TMRope_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202412');"><%= TMRope_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202501');"><%= TMRope_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202502');"><%= TMRope_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202503');"><%= TMRope_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202504');"><%= TMRope_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202505');"><%= TMRope_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202506');"><%= TMRope_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202507');"><%= TMRope_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202508');"><%= TMRope_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202509');"><%= TMRope_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202510');"><%= TMRope_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202511');"><%= TMRope_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202512');"><%= TMRope_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto CARTOPBOX_dto = jdcbService.findById("CARTOPBOX", todayVal);
                                        System.out.println("CARTOPBOX_dto = " + CARTOPBOX_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Car Top Box</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', 'total');"><font color="red"><%=CARTOPBOX_dto.getTotal() %></font> </a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202405');"><%= CARTOPBOX_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202406');"><%= CARTOPBOX_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202407');"><%= CARTOPBOX_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202408');"><%= CARTOPBOX_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202409');"><%= CARTOPBOX_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202410');"><%= CARTOPBOX_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202411');"><%= CARTOPBOX_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202412');"><%= CARTOPBOX_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202501');"><%= CARTOPBOX_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202502');"><%= CARTOPBOX_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202503');"><%= CARTOPBOX_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202504');"><%= CARTOPBOX_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202505');"><%= CARTOPBOX_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202506');"><%= CARTOPBOX_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202507');"><%= CARTOPBOX_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202508');"><%= CARTOPBOX_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202509');"><%= CARTOPBOX_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202510');"><%= CARTOPBOX_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202511');"><%= CARTOPBOX_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202512');"><%= CARTOPBOX_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto GOV_dto = jdcbService.findById("GOV", todayVal);
                                        System.out.println("GOV_dto = " + GOV_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Governor</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', 'total');"><font color="red"><%=GOV_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202405');"><%= GOV_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202406');"><%= GOV_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202407');"><%= GOV_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202408');"><%= GOV_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202409');"><%= GOV_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202410');"><%= GOV_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202411');"><%= GOV_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202412');"><%= GOV_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202501');"><%= GOV_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202502');"><%= GOV_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202503');"><%= GOV_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202504');"><%= GOV_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202505');"><%= GOV_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202506');"><%= GOV_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202507');"><%= GOV_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202508');"><%= GOV_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202509');"><%= GOV_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202510');"><%= GOV_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202511');"><%= GOV_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202512');"><%= GOV_dto.getPrice202512() %></a></td>
                                    </tr>


                                   <%-- //애는 그대로--%>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> 승강로 LAMP</td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202506');"><%= LAMP_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202507');"><%= LAMP_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202508');"><%= LAMP_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202509');"><%= LAMP_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202510');"><%= LAMP_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202511');"><%= LAMP_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202512');"><%= LAMP_24 %></a></td>
                                    </tr>

                                    <%
                                        DashDto PIT_dto = jdcbService.findById("PIT", todayVal);
                                        System.out.println("PIT_dto = " + PIT_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> PIT </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', 'total');"><font color="red"><%=PIT_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202405');"><%= PIT_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202406');"><%= PIT_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202407');"><%= PIT_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202408');"><%= PIT_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202409');"><%= PIT_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202410');"><%= PIT_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202411');"><%= PIT_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202412');"><%= PIT_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202501');"><%= PIT_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202502');"><%= PIT_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202503');"><%= PIT_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202504');"><%= PIT_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202505');"><%= PIT_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202506');"><%= PIT_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202507');"><%= PIT_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202508');"><%= PIT_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202509');"><%= PIT_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202510');"><%= PIT_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202511');"><%= PIT_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202512');"><%= PIT_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">HPB(J21)</td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202506');"><%= HPB_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202507');"><%= HPB_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202508');"><%= HPB_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202509');"><%= HPB_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202510');"><%= HPB_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202511');"><%= HPB_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202512');"><%= HPB_24 %></a></td>
                                    </tr>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SJ21) </td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202506');"><%= HIP_18 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202507');"><%= HIP_19 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202508');"><%= HIP_20 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202509');"><%= HIP_21 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202510');"><%= HIP_22 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202511');"><%= HIP_23 %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202512');"><%= HIP_24 %></a></td>
                                    </tr>

                                    <%
                                        DashDto opb_D521AG_dto = jdcbService.findById("opb_D521AG", todayVal);
                                        System.out.println("opb_D521AG_dto = " + opb_D521AG_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (D521AG) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', 'total');"><font color="red"><%=opb_D521AG_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202405');"><%= opb_D521AG_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202406');"><%= opb_D521AG_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202407');"><%= opb_D521AG_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202408');"><%= opb_D521AG_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202409');"><%= opb_D521AG_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202410');"><%= opb_D521AG_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202411');"><%= opb_D521AG_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202412');"><%= opb_D521AG_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202501');"><%= opb_D521AG_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202502');"><%= opb_D521AG_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202503');"><%= opb_D521AG_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202504');"><%= opb_D521AG_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202505');"><%= opb_D521AG_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202506');"><%= opb_D521AG_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202507');"><%= opb_D521AG_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202508');"><%= opb_D521AG_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202509');"><%= opb_D521AG_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202510');"><%= opb_D521AG_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202511');"><%= opb_D521AG_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202512');"><%= opb_D521AG_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        //개선
                                        DashDto OPB_S521A_dto = jdcbService.findById("OPB_S521A", todayVal);
                                        System.out.println("OPB_S521A_dto = " + OPB_S521A_dto);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (S521A) </td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'total');"><font color="red"><%=OPB_S521A_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202405');"><%= OPB_S521A_dto.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202406');"><%= OPB_S521A_dto.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202407');"><%= OPB_S521A_dto.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202408');"><%= OPB_S521A_dto.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202409');"><%= OPB_S521A_dto.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202410');"><%= OPB_S521A_dto.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202411');"><%= OPB_S521A_dto.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202412');"><%= OPB_S521A_dto.getPrice202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202501');"><%= OPB_S521A_dto.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202502');"><%= OPB_S521A_dto.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202503');"><%= OPB_S521A_dto.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202504');"><%= OPB_S521A_dto.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202505');"><%= OPB_S521A_dto.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202506');"><%= OPB_S521A_dto.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202507');"><%= OPB_S521A_dto.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202508');"><%= OPB_S521A_dto.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202509');"><%= OPB_S521A_dto.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202510');"><%= OPB_S521A_dto.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202511');"><%= OPB_S521A_dto.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202512');"><%= OPB_S521A_dto.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto HPB_K21_TOP = jdcbService.findById("HPB_K21_TOP", todayVal);
                                        System.out.println("11 " + HPB_K21_TOP.getTotal());
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', 'total');"><font color="red"><%=HPB_K21_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202405');"><%= HPB_K21_TOP.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202406');"><%= HPB_K21_TOP.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202407');"><%= HPB_K21_TOP.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202408');"><%= HPB_K21_TOP.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202409');"><%= HPB_K21_TOP.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202410');"><%= HPB_K21_TOP.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202411');"><%= HPB_K21_TOP.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202412');"><%= HPB_K21_TOP.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202501');"><%= HPB_K21_TOP.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202502');"><%= HPB_K21_TOP.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202503');"><%= HPB_K21_TOP.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202504');"><%= HPB_K21_TOP.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202505');"><%= HPB_K21_TOP.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202506');"><%= HPB_K21_TOP.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202507');"><%= HPB_K21_TOP.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202508');"><%= HPB_K21_TOP.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202509');"><%= HPB_K21_TOP.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202510');"><%= HPB_K21_TOP.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202511');"><%= HPB_K21_TOP.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202512');"><%= HPB_K21_TOP.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto HPB_K21_MID = jdcbService.findById("HPB_K21_MID", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', 'total');"><font color="red"><%=HPB_K21_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202405');"><%= HPB_K21_MID.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202406');"><%= HPB_K21_MID.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202407');"><%= HPB_K21_MID.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202408');"><%= HPB_K21_MID.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202409');"><%= HPB_K21_MID.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202410');"><%= HPB_K21_MID.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202411');"><%= HPB_K21_MID.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202412');"><%= HPB_K21_MID.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202501');"><%= HPB_K21_MID.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202502');"><%= HPB_K21_MID.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202503');"><%= HPB_K21_MID.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202504');"><%= HPB_K21_MID.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202505');"><%= HPB_K21_MID.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202506');"><%= HPB_K21_MID.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202507');"><%= HPB_K21_MID.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202508');"><%= HPB_K21_MID.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202509');"><%= HPB_K21_MID.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202510');"><%= HPB_K21_MID.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202511');"><%= HPB_K21_MID.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202512');"><%= HPB_K21_MID.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto HPB_K21_BOT = jdcbService.findById("HPB_K21_BOT", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', 'total');"><font color="red"><%=HPB_K21_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202405');"><%= HPB_K21_BOT.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202406');"><%= HPB_K21_BOT.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202407');"><%= HPB_K21_BOT.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202408');"><%= HPB_K21_BOT.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202409');"><%= HPB_K21_BOT.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202410');"><%= HPB_K21_BOT.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202411');"><%= HPB_K21_BOT.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202412');"><%= HPB_K21_BOT.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202501');"><%= HPB_K21_BOT.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202502');"><%= HPB_K21_BOT.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202503');"><%= HPB_K21_BOT.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202504');"><%= HPB_K21_BOT.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202505');"><%= HPB_K21_BOT.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202506');"><%= HPB_K21_BOT.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202507');"><%= HPB_K21_BOT.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202508');"><%= HPB_K21_BOT.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202509');"><%= HPB_K21_BOT.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202510');"><%= HPB_K21_BOT.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202511');"><%= HPB_K21_BOT.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202512');"><%= HPB_K21_BOT.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto HPB_K21A_TOP = jdcbService.findById("HPB_K21A_TOP", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', 'total');"><font color="red"><%=HPB_K21A_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202405');"><%= HPB_K21A_TOP.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202406');"><%= HPB_K21A_TOP.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202407');"><%= HPB_K21A_TOP.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202408');"><%= HPB_K21A_TOP.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202409');"><%= HPB_K21A_TOP.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202410');"><%= HPB_K21A_TOP.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202411');"><%= HPB_K21A_TOP.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202412');"><%= HPB_K21A_TOP.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202501');"><%= HPB_K21A_TOP.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202502');"><%= HPB_K21A_TOP.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202503');"><%= HPB_K21A_TOP.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202504');"><%= HPB_K21A_TOP.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202505');"><%= HPB_K21A_TOP.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202506');"><%= HPB_K21A_TOP.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202507');"><%= HPB_K21A_TOP.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202508');"><%= HPB_K21A_TOP.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202509');"><%= HPB_K21A_TOP.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202510');"><%= HPB_K21A_TOP.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202511');"><%= HPB_K21A_TOP.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202512');"><%= HPB_K21A_TOP.getPrice202512() %></a></td>
                                    </tr>


                                        <%
                                        DashDto HPB_K21A_MID = jdcbService.findById("HPB_K21A_MID", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', 'total');"><font color="red"><%=HPB_K21A_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202405');"><%= HPB_K21A_MID.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202406');"><%= HPB_K21A_MID.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202407');"><%= HPB_K21A_MID.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202408');"><%= HPB_K21A_MID.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202409');"><%= HPB_K21A_MID.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202410');"><%= HPB_K21A_MID.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202411');"><%= HPB_K21A_MID.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202412');"><%= HPB_K21A_MID.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202501');"><%= HPB_K21A_MID.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202502');"><%= HPB_K21A_MID.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202503');"><%= HPB_K21A_MID.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202504');"><%= HPB_K21A_MID.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202505');"><%= HPB_K21A_MID.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202506');"><%= HPB_K21A_MID.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202507');"><%= HPB_K21A_MID.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202508');"><%= HPB_K21A_MID.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202509');"><%= HPB_K21A_MID.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202510');"><%= HPB_K21A_MID.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202511');"><%= HPB_K21A_MID.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202512');"><%= HPB_K21A_MID.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto HPB_K21A_BOT = jdcbService.findById("HPB_K21A_BOT", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPB(K21A,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', 'total');"><font color="red"><%=HPB_K21A_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202405');"><%= HPB_K21A_BOT.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202406');"><%= HPB_K21A_BOT.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202407');"><%= HPB_K21A_BOT.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202408');"><%= HPB_K21A_BOT.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202409');"><%= HPB_K21A_BOT.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202410');"><%= HPB_K21A_BOT.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202411');"><%= HPB_K21A_BOT.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202412');"><%= HPB_K21A_BOT.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202501');"><%= HPB_K21A_BOT.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202502');"><%= HPB_K21A_BOT.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202503');"><%= HPB_K21A_BOT.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202504');"><%= HPB_K21A_BOT.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202505');"><%= HPB_K21A_BOT.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202506');"><%= HPB_K21A_BOT.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202507');"><%= HPB_K21A_BOT.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202508');"><%= HPB_K21A_BOT.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202509');"><%= HPB_K21A_BOT.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202510');"><%= HPB_K21A_BOT.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202511');"><%= HPB_K21A_BOT.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202512');"><%= HPB_K21A_BOT.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto HIP_SK21_TOP = jdcbService.findById("HIP_SK21_TOP", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,TOP) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', 'total');"><font color="red"><%=HIP_SK21_TOP.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202405');"><%= HIP_SK21_TOP.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202406');"><%= HIP_SK21_TOP.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202407');"><%= HIP_SK21_TOP.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202408');"><%= HIP_SK21_TOP.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202409');"><%= HIP_SK21_TOP.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202410');"><%= HIP_SK21_TOP.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202411');"><%= HIP_SK21_TOP.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202412');"><%= HIP_SK21_TOP.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202501');"><%= HIP_SK21_TOP.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202502');"><%= HIP_SK21_TOP.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202503');"><%= HIP_SK21_TOP.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202504');"><%= HIP_SK21_TOP.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202505');"><%= HIP_SK21_TOP.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202506');"><%= HIP_SK21_TOP.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202507');"><%= HIP_SK21_TOP.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202508');"><%= HIP_SK21_TOP.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202509');"><%= HIP_SK21_TOP.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202510');"><%= HIP_SK21_TOP.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202511');"><%= HIP_SK21_TOP.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202512');"><%= HIP_SK21_TOP.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto HIP_SK21_MID = jdcbService.findById("HIP_SK21_MID", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,MID) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', 'total');"><font color="red"><%=HIP_SK21_MID.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202405');"><%= HIP_SK21_MID.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202406');"><%= HIP_SK21_MID.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202407');"><%= HIP_SK21_MID.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202408');"><%= HIP_SK21_MID.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202409');"><%= HIP_SK21_MID.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202410');"><%= HIP_SK21_MID.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202411');"><%= HIP_SK21_MID.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202412');"><%= HIP_SK21_MID.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202501');"><%= HIP_SK21_MID.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202502');"><%= HIP_SK21_MID.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202503');"><%= HIP_SK21_MID.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202504');"><%= HIP_SK21_MID.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202505');"><%= HIP_SK21_MID.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202506');"><%= HIP_SK21_MID.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202507');"><%= HIP_SK21_MID.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202508');"><%= HIP_SK21_MID.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202509');"><%= HIP_SK21_MID.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202510');"><%= HIP_SK21_MID.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202511');"><%= HIP_SK21_MID.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202512');"><%= HIP_SK21_MID.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto HIP_SK21_BOT = jdcbService.findById("HIP_SK21_BOT", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SK21,BOT) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', 'total');"><font color="red"><%=HIP_SK21_BOT.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202405');"><%= HIP_SK21_BOT.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202406');"><%= HIP_SK21_BOT.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202407');"><%= HIP_SK21_BOT.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202408');"><%= HIP_SK21_BOT.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202409');"><%= HIP_SK21_BOT.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202410');"><%= HIP_SK21_BOT.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202411');"><%= HIP_SK21_BOT.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202412');"><%= HIP_SK21_BOT.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202501');"><%= HIP_SK21_BOT.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202502');"><%= HIP_SK21_BOT.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202503');"><%= HIP_SK21_BOT.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202504');"><%= HIP_SK21_BOT.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202505');"><%= HIP_SK21_BOT.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202506');"><%= HIP_SK21_BOT.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202507');"><%= HIP_SK21_BOT.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202508');"><%= HIP_SK21_BOT.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202509');"><%= HIP_SK21_BOT.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202510');"><%= HIP_SK21_BOT.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202511');"><%= HIP_SK21_BOT.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202512');"><%= HIP_SK21_BOT.getPrice202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDto HPI_S700 = jdcbService.findById("HPI_S700", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPI(S700) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', 'total');"><font color="red"><%=HPI_S700.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202405');"><%= HPI_S700.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202406');"><%= HPI_S700.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202407');"><%= HPI_S700.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202408');"><%= HPI_S700.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202409');"><%= HPI_S700.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202410');"><%= HPI_S700.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202411');"><%= HPI_S700.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202412');"><%= HPI_S700.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202501');"><%= HPI_S700.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202502');"><%= HPI_S700.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202503');"><%= HPI_S700.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202504');"><%= HPI_S700.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202505');"><%= HPI_S700.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202506');"><%= HPI_S700.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202507');"><%= HPI_S700.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202508');"><%= HPI_S700.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202509');"><%= HPI_S700.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202510');"><%= HPI_S700.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202511');"><%= HPI_S700.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202512');"><%= HPI_S700.getPrice202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDto HPI_SC = jdcbService.findById("HPI_SC", todayVal);
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HPI(SC) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', 'total');"><font color="red"><%=HPI_SC.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202405');"><%= HPI_SC.getPrice202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202406');"><%= HPI_SC.getPrice202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202407');"><%= HPI_SC.getPrice202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202408');"><%= HPI_SC.getPrice202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202409');"><%= HPI_SC.getPrice202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202410');"><%= HPI_SC.getPrice202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202411');"><%= HPI_SC.getPrice202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202412');"><%= HPI_SC.getPrice202412() %></a></td>

                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202501');"><%= HPI_SC.getPrice202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202502');"><%= HPI_SC.getPrice202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202503');"><%= HPI_SC.getPrice202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202504');"><%= HPI_SC.getPrice202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202505');"><%= HPI_SC.getPrice202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202506');"><%= HPI_SC.getPrice202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202507');"><%= HPI_SC.getPrice202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202508');"><%= HPI_SC.getPrice202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202509');"><%= HPI_SC.getPrice202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202510');"><%= HPI_SC.getPrice202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202511');"><%= HPI_SC.getPrice202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202512');"><%= HPI_SC.getPrice202512() %></a></td>
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




                    <!-- <div class="col-12"> -->
                    <%--<div class="col-lg-5">

                        <div class="card card-danger">
                            <div class="card-header">
                                <div class="d-flex justify-content-between">
                                    <h3 class="card-title">각 월별 출하(예정) 대수</h3>
                                    <h5 class="card-title">(그래프 클릭 시 건수 조회)</h5>
                                </div>
                            </div>

                            <div class="card-body">
                                <figure class="highcharts-figure">
                                    <div id="cpContainer"></div>
                                </figure>
                            </div>
                            <!-- /.card-body -->
                        </div>

                        <div class="card card-info">
                            <div class="card-header">
                                <div class="d-flex justify-content-between">
                                    <h3 class="card-title">CP MR/MRL 현황</h3>
                                </div>
                            </div>

                            <div class="card-body">
                                <figure class="highcharts-figure">
                                    <div id="cpMRContainer"></div>
                                </figure>
                            </div>
                            <!-- /.card-body -->
                        </div>

                        <!-- /.card -->
                    </div>--%>
                    <!-- /.col -->





                </div>
                <!-- /.row -->


                <!-- Dashboard -->
                <%--<div class="row">
                    <div class="col-12">
                        <div class="card card-warning">
                            <div class="card-header">
                                <h3 class="card-title">전체 과제별 월별 수량</h3>
                            </div>
                            <div class="card-body">
                                <!-- <table id="infoTable" class="table table-bordered table-striped"> -->

                                <!-- <div id="chartdiv"></div> -->
                                <figure class="highcharts-figure">
                                    <div id="container"></div>
                                </figure>
                            </div>
                        </div>
                    </div>
                </div>--%>


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


    let cp05 = ''; //Number("<%= cpMRL_5_05 %>");
    let cp06 = ''; //Number("<%= cpMRL_5_06 %>");
    let cp07 = ''; //Number("<%= cpMRL_5_07 %>");
    let cp08 = ''; //Number("<%= cpMRL_5_08 %>");
    let cp09 = ''; //Number("<%= cpMRL_5_09 %>");
    let cp10 = ''; //Number("<%= cpMRL_5_10 %>");
    let cp11 = ''; //Number("<%= cpMRL_5_11 %>");
    let cp12 = ''; //Number("<%= cpMRL_5_12 %>");
    let cp13 = ''; //Number("<%= cpMRL_5_13 %>");
    let cp14 = ''; //Number("<%= cpMRL_5_14 %>");
    let cp15 = ''; //Number("<%= cpMRL_5_15 %>");
    let cp16 = ''; //Number("<%= cpMRL_5_16 %>");
    let cp17 = ''; //Number("<%= cpMRL_5_17 %>");





    <%--let box05 = Number("<%= CARTOP_05%>");--%>
    <%--let box06 = Number("<%= CARTOP_06%>");--%>
    <%--let box07 = Number("<%= CARTOP_07%>");--%>
    <%--let box08 = Number("<%= CARTOP_08%>");--%>
    <%--let box09 = Number("<%= CARTOP_09%>");--%>
    <%--let box10 = Number("<%= CARTOP_10%>");--%>
    <%--let box11 = Number("<%= CARTOP_11%>");--%>
    <%--let box12 = Number("<%= CARTOP_12%>");--%>
    <%--let box13 = Number("<%= CARTOP_13%>");--%>
    <%--let box14 = Number("<%= CARTOP_14%>");--%>
    <%--let box15 = Number("<%= CARTOP_15%>");--%>
    <%--let box16 = Number("<%= CARTOP_16%>");--%>
    <%--let box17 = Number("<%= CARTOP_17%>");--%>





    //lamp05
    let lamp05 = Number("<%= LAMP_05 %>");
    let lamp06 = Number("<%= LAMP_06 %>");
    let lamp07 = Number("<%= LAMP_07 %>");
    let lamp08 = Number("<%= LAMP_08 %>");
    let lamp09 = Number("<%= LAMP_09 %>");
    let lamp10 = Number("<%= LAMP_10 %>");
    let lamp11 = Number("<%= LAMP_11 %>");
    let lamp12 = Number("<%= LAMP_12 %>");
    let lamp13 = Number("<%= LAMP_13 %>");
    let lamp14 = Number("<%= LAMP_14 %>");
    let lamp15 = Number("<%= LAMP_15 %>");
    let lamp16 = Number("<%= LAMP_16 %>");
    let lamp17 = Number("<%= LAMP_17 %>");



    let hpb05 = Number("<%= HPB_05 %>");
    let hpb06 = Number("<%= HPB_06 %>");
    let hpb07 = Number("<%= HPB_07 %>");
    let hpb08 = Number("<%= HPB_08 %>");
    let hpb09 = Number("<%= HPB_09 %>");
    let hpb10 = Number("<%= HPB_10 %>");
    let hpb11 = Number("<%= HPB_11 %>");
    let hpb12 = Number("<%= HPB_12 %>");
    let hpb13 = Number("<%= HPB_13 %>");
    let hpb14 = Number("<%= HPB_14 %>");
    let hpb15 = Number("<%= HPB_15 %>");
    let hpb16 = Number("<%= HPB_16 %>");
    let hpb17 = Number("<%= HPB_17 %>");

    let hips05 = Number("<%= HIP_05 %>");
    let hips06 = Number("<%= HIP_06 %>");
    let hips07 = Number("<%= HIP_07 %>");
    let hips08 = Number("<%= HIP_08 %>");
    let hips09 = Number("<%= HIP_09 %>");
    let hips10 = Number("<%= HIP_10 %>");
    let hips11 = Number("<%= HIP_11 %>");
    let hips12 = Number("<%= HIP_12 %>");
    let hips13 = Number("<%= HIP_13 %>");
    let hips14 = Number("<%= HIP_14 %>");
    let hips15 = Number("<%= HIP_15 %>");
    let hips16 = Number("<%= HIP_16 %>");
    let hips17 = Number("<%= HIP_17 %>");


    let hpi70005 = Number("<%= HPI_05 %>");
    let hpi70006 = Number("<%= HPI_06 %>");
    let hpi70007 = Number("<%= HPI_07 %>");
    let hpi70008 = Number("<%= HPI_08 %>");
    let hpi70009 = Number("<%= HPI_09 %>");
    let hpi70010 = Number("<%= HPI_10 %>");
    let hpi70011 = Number("<%= HPI_11 %>");
    let hpi70012 = Number("<%= HPI_12 %>");
    let hpi70013 = Number("<%= HPI_13 %>");
    let hpi70014 = Number("<%= HPI_14 %>");
    let hpi70015 = Number("<%= HPI_15 %>");
    let hpi70016 = Number("<%= HPI_16 %>");
    let hpi70017 = Number("<%= HPI_17 %>");


    <%--let OPB_D521AG_05 = Number("<%= OPB_D521AG_05 %>");--%>
    <%--let OPB_D521AG_06 = Number("<%= OPB_D521AG_06 %>");--%>
    <%--let OPB_D521AG_07 = Number("<%= OPB_D521AG_07 %>");--%>
    <%--let OPB_D521AG_08 = Number("<%= OPB_D521AG_08 %>");--%>
    <%--let OPB_D521AG_09 = Number("<%= OPB_D521AG_09 %>");--%>
    <%--let OPB_D521AG_10 = Number("<%= OPB_D521AG_10 %>");--%>
    <%--let OPB_D521AG_11 = Number("<%= OPB_D521AG_11 %>");--%>
    <%--let OPB_D521AG_12 = Number("<%= OPB_D521AG_12 %>");--%>
    <%--let OPB_D521AG_13 = Number("<%= OPB_D521AG_13 %>");--%>
    <%--let OPB_D521AG_14 = Number("<%= OPB_D521AG_14 %>");--%>
    <%--let OPB_D521AG_15 = Number("<%= OPB_D521AG_15 %>");--%>
    <%--let OPB_D521AG_16 = Number("<%= OPB_D521AG_16 %>");--%>
    <%--let OPB_D521AG_17 = Number("<%= OPB_D521AG_17 %>");--%>

    <%--let OPB_S521A_05 = Number("<%= OPB_S521A_05 %>");--%>
    <%--let OPB_S521A_06 = Number("<%= OPB_S521A_06 %>");--%>
    <%--let OPB_S521A_07 = Number("<%= OPB_S521A_07 %>");--%>
    <%--let OPB_S521A_08 = Number("<%= OPB_S521A_08 %>");--%>
    <%--let OPB_S521A_09 = Number("<%= OPB_S521A_09 %>");--%>
    <%--let OPB_S521A_10 = Number("<%= OPB_S521A_10 %>");--%>
    <%--let OPB_S521A_11 = Number("<%= OPB_S521A_11 %>");--%>
    <%--let OPB_S521A_12 = Number("<%= OPB_S521A_12 %>");--%>
    <%--let OPB_S521A_13 = Number("<%= OPB_S521A_13 %>");--%>
    <%--let OPB_S521A_14 = Number("<%= OPB_S521A_14 %>");--%>
    <%--let OPB_S521A_15 = Number("<%= OPB_S521A_15 %>");--%>
    <%--let OPB_S521A_16 = Number("<%= OPB_S521A_16 %>");--%>
    <%--let OPB_S521A_17 = Number("<%= OPB_S521A_17 %>");--%>


    let export05 = Number("<%= export05 %>");
    let export06 = Number("<%= export06 %>");
    let export07 = Number("<%= export07 %>");
    let export08 = Number("<%= export08 %>");
    let export09 = Number("<%= export09 %>");
    let export10 = Number("<%= export10 %>");
    let export11 = Number("<%= export11 %>");
    let export12 = Number("<%= export12 %>");
    let export13 = Number("<%= export13 %>");
    let export14 = Number("<%= export14 %>");
    let export15 = Number("<%= export15 %>");
    let export16 = Number("<%= export16 %>");
    let export17 = Number("<%= export17 %>");

    //ready
    $(document).ready(function() {

        console.log('highchart start');


        Highcharts.setOptions({
            colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4']
        })

/*


        Highcharts.chart('container', {
            chart: {
                type: 'column'
            },
            title: {
                text: '',
                align: 'left'
            },
            subtitle: {
                //text:
                //'Source: <a target="_blank" ' +
                // 'href="https://www.indexmundi.com/agriculture/?commodity=corn">indexmundi</a>',
                //align: 'left'
            },
            xAxis: {
                categories: ['2024.05', '06', '07', '08', '09', '10', '11', '12', '2025.01', '02', '03', '04', '05'],
                crosshair: true,
                accessibility: {
                    description: 'Part'
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: '' //'1000 metric tons (MT)'
                }
            },
            tooltip: {
                valueSuffix: ' (건)'
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            credits: {
                enabled: false
            },
            series: [
                {
                    name: 'CP(MRL_5)',
                    data: [cp05, cp06, cp07, cp08, cp09, cp10, cp11, cp12, cp13, cp14 , cp15, cp16, cp17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MRL_9)',
                    data: [cpMRL_9_05, cpMRL_9_06, cpMRL_9_07, cpMRL_9_08, cpMRL_9_09, cpMRL_9_10, cpMRL_9_11, cpMRL_9_12, cpMRL_9_13, cpMRL_9_14, cpMRL_9_15, cpMRL_9_16, cpMRL_9_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MRL_14)',
                    data: [cpMRL_14_05, cpMRL_14_06, cpMRL_14_07, cpMRL_14_08, cpMRL_14_09, cpMRL_14_10, cpMRL_14_11, cpMRL_14_12, cpMRL_14_13, cpMRL_14_14, cpMRL_14_15, cpMRL_14_16, cpMRL_14_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MRL_17)',
                    data: [cpMRL_17_05, cpMRL_17_06, cpMRL_17_07, cpMRL_17_08, cpMRL_17_09, cpMRL_17_10, cpMRL_17_11, cpMRL_17_12, cpMRL_17_13, cpMRL_17_14 , cpMRL_17_15, cpMRL_17_16, cpMRL_17_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MR_5.5kW_회생)',
                    data: [cpMR_5_05, cpMR_5_06, cpMR_5_07, cpMR_5_08, cpMR_5_09, cpMR_5_10, cpMR_5_11, cpMR_5_12, cpMR_5_13, cpMR_5_14 , cpMR_5_15, cpMR_5_16, cpMR_5_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MR_9kW_회생) ',
                    data: [cpMR_9_05, cpMR_9_06, cpMR_9_07, cpMR_9_08, cpMR_9_09, cpMR_9_10, cpMR_9_11, cpMR_9_12, cpMR_9_13, cpMR_9_14 , cpMR_9_15, cpMR_9_16, cpMR_9_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MR_14kW_회생)',
                    data: [cpMR_14_05, cpMR_14_06, cpMR_14_07, cpMR_14_08, cpMR_14_09, cpMR_14_10, cpMR_14_11, cpMR_14_12, cpMR_14_13, cpMR_14_14 , cpMR_14_15, cpMR_14_16, cpMR_14_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'CP(MR_17.5kW_회생)',
                    data: [cpMR_17_05, cpMR_17_06, cpMR_17_07, cpMR_17_08, cpMR_17_09, cpMR_17_10, cpMR_17_11, cpMR_17_12, cpMR_17_13, cpMR_17_14 , cpMR_17_15, cpMR_17_16, cpMR_17_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'TM(Belt Type)',
                    data: [tmbelt05, tmbelt06, tmbelt07, tmbelt08, tmbelt09, tmbelt10, tmbelt11, tmbelt12, tmbelt13, tmbelt14 , tmbelt15, tmbelt16, tmbelt17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'TM(Rope)',
                    data: [tmRope05, tmRope06, tmRope07, tmRope08, tmRope09, tmRope10, tmRope11, tmRope12, tmRope13, tmRope14 , tmRope15, tmRope16, tmRope17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'Car_Top_Box',
                    data: [box05, box06, box07, box08, box09, box10, box11, box12, box13, box14, box15, box16, box17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'Governor',
                    data: [gov05, gov06, gov07, gov08, gov09, gov10, gov11, gov12, gov13, gov14 , gov15, gov16, gov17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: '승강로_LAMP',
                    data: [lamp05, lamp06, lamp07, lamp08, lamp09, lamp10, lamp11, lamp12, lamp13, lamp14 , lamp15, lamp16, lamp17],
                    dataLabels: {
                        enabled: true
                    },
                    visible: false
                },
                {
                    name: 'PIT SW',
                    data: [pit05, pit06, pit07, pit08, pit09, pit10, pit11, pit12, pit13, pit14, pit15, pit16, pit17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'HPB(J21)',
                    data: [hpb05, hpb06, hpb07, hpb08, hpb09, hpb10, hpb11, hpb12, hpb13, hpb14, hpb15, hpb16, hpb17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'HIP(SJ21)',
                    data: [hips05, hips06, hips07, hips08, hips09, hips10, hips11, hips12, hips13, hips14, hips15, hips16, hips17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'HPI(S700)',
                    data: [hpi70005, hpi70006, hpi70007, hpi70008, hpi70009, hpi70010, hpi70011, hpi70012, hpi70013, hpi70014, hpi70015, hpi70016, hpi70017],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'OPB(D521AG)',
                    data: [OPB_D521AG_05, OPB_D521AG_06, OPB_D521AG_07, OPB_D521AG_08, OPB_D521AG_09, OPB_D521AG_10, OPB_D521AG_11, OPB_D521AG_12, OPB_D521AG_13, OPB_D521AG_14, OPB_D521AG_15, OPB_D521AG_16, OPB_D521AG_17],
                    dataLabels: {
                        enabled: true
                    }
                },
                {
                    name: 'OPB(S521A)',
                    data: [OPB_S521A_05, OPB_S521A_06, OPB_S521A_07, OPB_S521A_08, OPB_S521A_09, OPB_S521A_10, OPB_S521A_11, OPB_S521A_12, OPB_S521A_13, OPB_S521A_14, OPB_S521A_15, OPB_S521A_16, OPB_S521A_17],
                    dataLabels: {
                        enabled: true
                    }
                }
                //OPB_D521AG_17
            ]
        });


        //각 월별 출하(예정)대수
        Highcharts.chart('cpContainer', {
            chart: {
                type: 'column'
            },
            title: {
                text: '',
                align: 'left'
            },
            subtitle: {
                //text:
                //'Source: <a target="_blank" ' +
                // 'href="https://www.indexmundi.com/agriculture/?commodity=corn">indexmundi</a>',
                //align: 'left'
            },
            xAxis: {
                categories: ['2024.05', '06', '07', '08', '09', '10', '11', '12', '2025.01', '02', '03', '04', '05'],
                crosshair: true,
                accessibility: {
                    //description: 'Part'
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: '' //'1000 metric tons (MT)'
                }
            },
            tooltip: {
                valueSuffix: ' (건)'
            },
            lagend: {
                enabled: false
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                },
                series: {
                    cursor: 'pointer',
                    showInLegend: false,
                    point: {
                        events: {
                            click: function() {
                                //alert(this.options.key);
                                viewExportList(this.options.key);
                            }
                        }
                    },
                    //click: function() {
                    //location.href = 'https://en.wikipedia.org/wiki/' +this.options.key;
                    //console.log(this.name + ' > ' + this..)
                    //alert(this.options.key);
                    //}
                }
            },
            credits: {
                enabled: false
            },
            series: [
                {
                    data: [
                        {
                            y: export05,
                            name: '2024-05',
                            key: '202405'
                        },
                        {
                            y: export06,
                            name: '06',
                            key: '202406'
                        },
                        {
                            y: export07,
                            name: '07',
                            key: '202407'
                        },
                        {
                            y: export08,
                            name: '08',
                            key: '202408'
                        },
                        {
                            y: export09,
                            name: '09',
                            key: '202409'
                        },
                        {
                            y: export10,
                            name: '10',
                            key: '202410'
                        },
                        {
                            y: export11,
                            name: '11',
                            key: '202411'
                        },
                        {
                            y: export12,
                            name: '12',
                            key: '202412'
                        },
                        {
                            y: export13,
                            name: '2025-01',
                            key: '202501'
                        },
                        {
                            y: export14,
                            name: '02',
                            key: '202502'
                        },
                        {
                            y: export15,
                            name: '03',
                            key: '202503'
                        },
                        {
                            y: export16,
                            name: '04',
                            key: '202504'
                        },
                        {
                            y: export17,
                            name: '05',
                            key: '202505'
                        }
                    ], // data
                    dataLabels: {
                        enabled: true
                    }
                    /!* name: '월별 출하일',
                    data: [export05, export06, export07, export08, export09, export10, export11, export12, export13],
                    dataLabels: {
                        enabled: true
                         //color:'#b1b6c1',
                    } *!/
                }

            ]
        });


        // CP MR/MRL 월별 현황
        Highcharts.chart('cpMRContainer', {
            colors:['#fe6a35', '#00e272'],
            chart: {
                type: 'column'
            },
            title: {
                text: '', //'Major trophies for some English teams',
                align: 'left'
            },
            xAxis: {
                categories: ['2024.05', '06', '07', '08', '09','10', '11', '12', '2025.01']
            },
            yAxis: {
                min: 0,
                title: {
                    text: '건'
                },
                stackLabels: {
                    enabled: true
                }
            },
            legend: {
                align: 'left',
                x: 70,
                verticalAlign: 'top',
                y: 70,
                floating: true,
                backgroundColor:
                    Highcharts.defaultOptions.legend.backgroundColor || 'white',
                borderColor: '#CCC',
                borderWidth: 1,
                shadow: false
            },
            tooltip: {
                headerFormat: '<b>{category}</b><br/>',
                pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
            },
            plotOptions: {
                column: {
                    stacking: 'normal',
                    dataLabels: {
                        enabled: true
                    }
                }
            },
            credits: {
                enabled: false
            },
            series: [{
                name: 'CP MR',
                data: [cpMR05, cpMR06, cpMR07, cpMR08, cpMR09, cpMR10, cpMR11, cpMR12, cpMR13]
            }, {
                name: 'CP MRL',
                data: [cpMRL05, cpMRL06, cpMRL07, cpMRL08, cpMRL09, cpMRL10, cpMRL11, cpMRL12, cpMRL13]
            }]
        });

    }); // end document ready
*/




    function viewList(type, viewDate) {

        console.log(type + " -- " + viewDate);

        let todayVal = '<%=todayVal %>'



        let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionPopRev.jsp?";
        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionPopRev.jsp?";

        urlValue += "viewType=" + type;
        urlValue += "&startDate=" + viewDate;
        urlValue += "&todayVal=" + todayVal;
        urlValue += "&rate=TRUE";
        window.open(urlValue,'_blank','width=1500, height=800, top=50, left=50, scrollbars=yes');
    }

    function viewExportList(curDate) {

        //alert('curdate == ' + curDate);
        let todayVal = '<%=todayVal %>'
        //searchPriceReductionExportDataPop.jsp

        let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionExportDataPop.jsp?";
        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionExportDataPop.jsp?";
        urlValue += "curDate=" + curDate;
        urlValue += "&todayVal=" + todayVal;
        window.open(urlValue,'_blank','width=1600, height=800, top=50, left=50, scrollbars=yes');

    }


</script>

</html>
