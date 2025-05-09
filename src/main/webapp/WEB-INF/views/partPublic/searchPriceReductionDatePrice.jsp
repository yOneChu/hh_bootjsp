<%@page import="java.time.LocalDate"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>
<%@ page import="com.kyhslam.util.PartPublicCommonUtil" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>




<%

    //출하예정일 기준 대시보드 개발 - 금액까지 표시
    // searchPriceReductionDatePrice.jsp
    // http://10.225.4.20/jsp/searchLogic/searchPriceReductionDatePrice.jsp
    // http://localhost/jsp/searchLogic/searchPriceReductionDatePrice.jsp

    //http://10.225.4.20/jsp/searchLogic/searchPriceReductionDatePrice.jsp


    ArrayList<HashMap<String, String>> dashList = new ArrayList<HashMap<String, String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;


    LocalDate now = LocalDate.now();
    String todayVal = now.toString();


    try {

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




    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //DynaUtils.close(rs,pstmt,con);
    }


    //System.out.println(" ---------------- end dashboard -------------");

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <%--<meta http-equiv="Cache-Control" content="no-cache"/>--%>
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />
    <title>부품공용화 Dahsboard</title>

    <!-- Google Font: Source Sans Pro -->
    <!--    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">-->
    <%--<link rel="stylesheet" href="/resources/dist/googleFont.css">--%>
    <link href="https://fonts.cdnfonts.com/css/cascadia-code" rel="stylesheet">
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
                        <h1>부품공용화 - 월별실적(출하예정일 -> 원가절감액) <font color="red">( <%=todayVal %>, 06:00기준)</font> </h1>
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
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">

                                    <thead>
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">과제명</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">구분</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">총 금액<br>(수량)</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="23">월별실적</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" colspan="9">2024년</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="13">2025년</th>
                                        <th style="font-weight: bold; text-align: center;" >기타</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">2024년 금액 <br>(수량)</th>
                                        <th style="font-weight: bold; text-align: center;">05</th>
                                        <th style="font-weight: bold; text-align: center;">06</th>
                                        <th style="font-weight: bold; text-align: center;">07</th>
                                        <th style="font-weight: bold; text-align: center;">08</th>
                                        <th style="font-weight: bold; text-align: center;">09</th>
                                        <th style="font-weight: bold; text-align: center;">10</th>
                                        <th style="font-weight: bold; text-align: center;">11</th>
                                        <th style="font-weight: bold; text-align: center;">12</th>

                                        <th style="font-weight: bold; text-align: center;">2025년 금액 <br>(수량)</th>
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


                                    <%

                                        int index = 1;


                                        try {


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
                                                String DISETC 	 = rs.getString("export_etc") == null ? "" : rs.getString("export_etc");

                                                int partPrice = 0;


                                                int total_2024 = Integer.parseInt(DIS202405) + Integer.parseInt(DIS202406) + Integer.parseInt(DIS202407) + Integer.parseInt(DIS202408) + Integer.parseInt(DIS202409) + Integer.parseInt(DIS202410)
                                                        + Integer.parseInt(DIS202411) + Integer.parseInt(DIS202412) ;

                                                int total_2025 = Integer.parseInt(DIS202501) + Integer.parseInt(DIS202502) + Integer.parseInt(DIS202503) + Integer.parseInt(DIS202504) + Integer.parseInt(DIS202505)
                                                        + Integer.parseInt(DIS202506) + Integer.parseInt(DIS202507) + Integer.parseInt(DIS202508) + Integer.parseInt(DIS202509) + Integer.parseInt(DIS202510)
                                                        + Integer.parseInt(DIS202511) + Integer.parseInt(DIS202512);

                                                int allTotal = total_2024 + total_2025 + Integer.parseInt(DISETC);


                                                System.out.println("part_name = " + part_name);
                                                //일단 1.5단계 제외
                                                if(part_name.startsWith("cp1_5")) {
                                                    continue;
                                                }


                                                if("cpMRL_5".equals(part_name)) {
                                                    part_name = "CP (MRL_5.5kW_일반)";
                                                    partPrice = 146168;

                                                } else if("cpMRL_9".equals(part_name)) {
                                                    part_name = "CP (MRL_9kW_일반)";
                                                    partPrice = 163848;

                                                } else if("cpMRL_14".equals(part_name)) {
                                                    part_name = "CP (MRL_14kW_일반)";
                                                    partPrice = 215279;

                                                } else if("cpMRL_17".equals(part_name)) {
                                                    part_name = "CP (MRL_17.5kW_일반)";
                                                    partPrice = 194456;

                                                } else if("cpMR_5_5".equals(part_name)) {
                                                    part_name = "CP (MR_5.5kW_회생)";
                                                    partPrice = 199458;

                                                } else if("cpMR_9".equals(part_name)) {
                                                    part_name = "CP (MR_9kW_회생)";
                                                    partPrice = 155362;

                                                } else if("cpMR_14".equals(part_name)) {
                                                    part_name = "CP (MR_14kW_회생)";
                                                    partPrice = 267279;

                                                } else if("cpMR_17_5".equals(part_name)) {
                                                    part_name = "CP (MR_17.5kW_회생)";
                                                    partPrice = 251594;

                                                } else if("TM".equals(part_name)) {
                                                    part_name = "TM(Belt Type)";
                                                    partPrice = 428252;

                                                } else if("TMRope".equals(part_name)) {
                                                    part_name = "TM(ROPE)";
                                                    partPrice = 428252;  //????

                                                } else if("CARTOPBOX".equals(part_name)) {
                                                    part_name = "Car Top Box";
                                                    partPrice = 12004;

                                                } else if("GOV".equals(part_name)) {
                                                    part_name = "Governor";
                                                    partPrice = 43234;

                                                } else if("PIT".equals(part_name)) {
                                                    part_name = "PIT";
                                                    partPrice = 3502;

                                                } else if("HPI_S700".equals(part_name)) {
                                                    part_name = "HPI(S700)";
                                                    partPrice = 2771;

                                                } else if("HPI_SC".equals(part_name)) {
                                                    part_name = "HPI(SC)";
                                                    partPrice = 2771;

                                                } else if("LAMP_OVER".equals(part_name)) {
                                                    partPrice = 2406;

                                                } else if("LAMP_PIT".equals(part_name)) {
                                                    partPrice = 1451;

                                                } else if("LAMP_CARTOP".equals(part_name)) {
                                                    partPrice = 1543;

                                                } else if("LAMP_HOIST".equals(part_name)) {
                                                    partPrice = 1553;

                                                } else if("HPB_TOP".equals(part_name)) {
                                                    partPrice = 1553;
                                                } else if("HPB_MID".equals(part_name)) {
                                                    partPrice = 1553;
                                                } else if("HPB_BOT".equals(part_name)) {
                                                    partPrice = 1553;

                                                } else if("HIP_TOP".equals(part_name)) {
                                                    //part_name = "HPI(S700)";
                                                    partPrice = 20219;
                                                } else if("HIP_MID".equals(part_name)) {
                                                    //part_name = "HPI(S700)";
                                                    partPrice = 20219;
                                                } else if("HIP_BOT".equals(part_name)) {
                                                    //part_name = "HPI(S700)";
                                                    partPrice = 20219;
                                                } else if("OPB_S521A".equals(part_name.toUpperCase())) {
                                                    //part_name = "OPB (S521A)";
                                                    partPrice = 20219;
                                                } else if("opb_D521AG".equals(part_name.toUpperCase())) {
                                                    //part_name = "OPB (D521AG)";
                                                    partPrice = 20219;
                                                } else if("cp2_MRL_General".equals(part_name)) {
                                                    part_name = "2단계 CP(MRL_일반)";
                                                    partPrice = 0;
                                                } else if("cp2_MRL_Revive".equals(part_name)) {
                                                    part_name = "2단계 CP(MRL_회생)";
                                                    partPrice = 0;
                                                } else if("cp2_MR_General".equals(part_name)) {
                                                    part_name = "2단계 CP(MR_일반)";
                                                    partPrice = 0;
                                                } else if("cp2_MR_Revive".equals(part_name)) {
                                                    part_name = "2단계 CP(MR_회생)";
                                                    partPrice = 0;
                                                }



                                                int pallTotal  = allTotal * partPrice;
                                                int ptotal_2024 = total_2024 * partPrice;
                                                int ptotal_2025 = total_2025 * partPrice;
                                                int pDIS202405 = Integer.parseInt(DIS202405) * partPrice;
                                                int pDIS202406 = Integer.parseInt(DIS202406) * partPrice;
                                                int pDIS202407 = Integer.parseInt(DIS202407) * partPrice;
                                                int pDIS202408 = Integer.parseInt(DIS202408) * partPrice;
                                                int pDIS202409 = Integer.parseInt(DIS202409) * partPrice;
                                                int pDIS202410 = Integer.parseInt(DIS202410) * partPrice;
                                                int pDIS202411 = Integer.parseInt(DIS202411) * partPrice;
                                                int pDIS202412 = Integer.parseInt(DIS202412) * partPrice;

                                                int pDIS202501 = Integer.parseInt(DIS202501) * partPrice;
                                                int pDIS202502 = Integer.parseInt(DIS202502) * partPrice;
                                                int pDIS202503 = Integer.parseInt(DIS202503) * partPrice;
                                                int pDIS202504 = Integer.parseInt(DIS202504) * partPrice;
                                                int pDIS202505 = Integer.parseInt(DIS202505) * partPrice;
                                                int pDIS202506 = Integer.parseInt(DIS202506) * partPrice;
                                                int pDIS202507 = Integer.parseInt(DIS202507) * partPrice;
                                                int pDIS202508 = Integer.parseInt(DIS202508) * partPrice;
                                                int pDIS202509 = Integer.parseInt(DIS202509) * partPrice;
                                                int pDIS202510 = Integer.parseInt(DIS202510) * partPrice;
                                                int pDIS202511 = Integer.parseInt(DIS202511) * partPrice;
                                                int pDIS202512 = Integer.parseInt(DIS202512) * partPrice;
                                                int pDISEPC = Integer.parseInt(DISETC) * partPrice;


                                                if(part_name.toUpperCase().startsWith("OPB")) {
                                                    HashMap<String, Integer> m = PartPublicCommonUtil.getMonthValue(todayVal, part_name.toUpperCase());


                                                    pDIS202405 = m.get("m202405");
                                                    pDIS202406 = m.get("m202406");
                                                    pDIS202407 = m.get("m202407");
                                                    pDIS202408 = m.get("m202408");
                                                    pDIS202409 = m.get("m202409");
                                                    pDIS202410 = m.get("m202410");
                                                    pDIS202411 = m.get("m202411");
                                                    pDIS202412 = m.get("m202412");

                                                    pDIS202501 = m.get("m202501");
                                                    pDIS202502 = m.get("m202502");
                                                    pDIS202503 = m.get("m202503");
                                                    pDIS202504 = m.get("m202504");
                                                    pDIS202505 = m.get("m202505");
                                                    pDIS202506 = m.get("m202506");
                                                    pDIS202507 = m.get("m202507");
                                                    pDIS202508 = m.get("m202508");
                                                    pDIS202509 = m.get("m202509");
                                                    pDIS202510 = m.get("m202510");
                                                    pDIS202511 = m.get("m202511");
                                                    pDIS202512 = m.get("m202512");
                                                    pDISEPC = m.get("etc");

                                                    ptotal_2024 = pDIS202405 + pDIS202406 + pDIS202407 + pDIS202408 + pDIS202409 + pDIS202410 + pDIS202411 + pDIS202412;
                                                    ptotal_2025 = pDIS202501 + pDIS202502 + pDIS202503 + pDIS202504 + pDIS202505 + pDIS202506 + pDIS202507 + pDIS202508 + pDIS202509 + pDIS202510
                                                                 + pDIS202511 + pDIS202512;
                                                    pallTotal  = ptotal_2024 + ptotal_2025 + pDISEPC;


                                                    if("OPB_S521A".equals(part_name.toUpperCase())) {
                                                        part_name = "OPB (S521A)";
                                                    } else if("opb_D521AG".equals(part_name.toUpperCase())) {
                                                        part_name = "OPB (D521AG)";
                                                    }
                                                }




                                                //System.out.println("index == " + index + " , " + part_name);
                                    %>

                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"> <%= (index++) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= part_name %> </td>
                                        <td style="font-weight: bold; text-align: center;"> 금액 </td>

                                        <td style="font-weight: bold; text-align: center;"> <font color="red"> <%=String.format("%,d",  pallTotal) %> </font> </td>

                                        <td style="font-weight: bold; text-align: center;"> <font color="red"> <%=String.format("%,d",  ptotal_2024) %>  </font> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202405) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202406) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202407) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202408) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202409) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202410) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202411) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d",  pDIS202412) %> </td>


                                        <td style="font-weight: bold; text-align: center;"> <font color="red"> <%=String.format("%,d",  ptotal_2025) %>  </font> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202501) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202502) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202503) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202504) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202505) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202506) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202507) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202508) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202509) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202510) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202511) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= String.format("%,d", pDIS202512) %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <font color="red"><%= String.format("%,d", pDISEPC) %>  </font> </td>
                                    </tr>


                                    <tr>

                                        <td style="font-weight: bold; text-align: center;"> - </td>
                                        <td style="font-weight: bold; text-align: center;"> - </td>
                                        <td style="font-weight: bold; text-align: center;"> 수량 </td>

                                        <td style="font-weight: bold; text-align: center;"> <font color="red"> <%=allTotal %> </font> </td>

                                        <td style="font-weight: bold; text-align: center;"> <font color="red"> <%=total_2024 %> </font> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202405 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202406 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202407 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202408 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202409 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202410 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202411 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%= DIS202412 %></td>

                                        <td style="font-weight: bold; text-align: center;"> <font color="red">  <%=total_2025 %> </font> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202501 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202502 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202503 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202504 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202505 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202506 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202507 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202508 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202509 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202510 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202511 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=DIS202512 %></td>
                                        <td style="font-weight: bold; text-align: center;"> <font color="red"> <%=DISETC %> </font> </td>
                                    </tr>

                                    <%
                                        } // end rs
                                    %>


                                    <%
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            //DynaUtils.close(rs,pstmt,con);
                                            VaultDBConnection.disconnect(con, pstmt, rs);
                                        }


                                    %>

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




<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "pageLength": 30,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "ordering" : false,
        "searching" : false,
        "paging" : false, // 페이징표시 삭제
        "buttons": ["csv", "excel", "copy"],
        "rowsGroup": [

        ]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {


    }); // end document ready




</script>

</html>
