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

    LocalDate now = LocalDate.now();
    String todayVal = now.toString();




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
                                        DashDtoV2 cpMRL_5_dto = jdcbService.findByIdV2(todayVal, "cpMRL_5");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_5.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', 'total');"><font color="red"><%=cpMRL_5_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202405');"><%= cpMRL_5_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202406');"><%= cpMRL_5_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202407');"><%= cpMRL_5_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202408');"><%= cpMRL_5_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202409');"><%= cpMRL_5_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202410');"><%= cpMRL_5_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202411');"><%= cpMRL_5_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202412');"><%= cpMRL_5_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202501');"><%= cpMRL_5_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202502');"><%= cpMRL_5_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202503');"><%= cpMRL_5_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202504');"><%= cpMRL_5_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202505');"><%= cpMRL_5_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202506');"><%= cpMRL_5_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202507');"><%= cpMRL_5_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202508');"><%= cpMRL_5_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202509');"><%= cpMRL_5_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202510');"><%= cpMRL_5_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202511');"><%= cpMRL_5_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_5', '202512');"><%= cpMRL_5_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMRL_9_dto = jdcbService.findByIdV2(todayVal, "cpMRL_9");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_9kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', 'total');"><font color="red"><%=cpMRL_9_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202405');"><%= cpMRL_9_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202406');"><%= cpMRL_9_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202407');"><%= cpMRL_9_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202408');"><%= cpMRL_9_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202409');"><%= cpMRL_9_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202410');"><%= cpMRL_9_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202411');"><%= cpMRL_9_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202412');"><%= cpMRL_9_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202501');"><%= cpMRL_9_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202502');"><%= cpMRL_9_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202503');"><%= cpMRL_9_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202504');"><%= cpMRL_9_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202505');"><%= cpMRL_9_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202506');"><%= cpMRL_9_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202507');"><%= cpMRL_9_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202508');"><%= cpMRL_9_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202509');"><%= cpMRL_9_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202510');"><%= cpMRL_9_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202511');"><%= cpMRL_9_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_9', '202512');"><%= cpMRL_9_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMRL_14_dto = jdcbService.findByIdV2(todayVal, "cpMRL_14");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_14kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', 'total');"><font color="red"> <%=cpMRL_14_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202405');"><%= cpMRL_14_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202406');"><%= cpMRL_14_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202407');"><%= cpMRL_14_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202408');"><%= cpMRL_14_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202409');"><%= cpMRL_14_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202410');"><%= cpMRL_14_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202411');"><%= cpMRL_14_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202412');"><%= cpMRL_14_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202501');"><%= cpMRL_14_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202502');"><%= cpMRL_14_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202503');"><%= cpMRL_14_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202504');"><%= cpMRL_14_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202505');"><%= cpMRL_14_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202506');"><%= cpMRL_14_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202507');"><%= cpMRL_14_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202508');"><%= cpMRL_14_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202509');"><%= cpMRL_14_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202510');"><%= cpMRL_14_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202511');"><%= cpMRL_14_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_14', '202512');"><%= cpMRL_14_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMRL_17_dto = jdcbService.findByIdV2(todayVal, "cpMRL_17");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MRL_17.5kW_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', 'total');"><font color="red"> <%=cpMRL_17_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202405');"><%= cpMRL_17_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202406');"><%= cpMRL_17_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202407');"><%= cpMRL_17_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202408');"><%= cpMRL_17_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202409');"><%= cpMRL_17_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202410');"><%= cpMRL_17_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202411');"><%= cpMRL_17_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202412');"><%= cpMRL_17_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202501');"><%= cpMRL_17_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202502');"><%= cpMRL_17_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202503');"><%= cpMRL_17_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202504');"><%= cpMRL_17_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202505');"><%= cpMRL_17_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202506');"><%= cpMRL_17_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202507');"><%= cpMRL_17_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202508');"><%= cpMRL_17_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202509');"><%= cpMRL_17_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202510');"><%= cpMRL_17_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202511');"><%= cpMRL_17_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMRL_17', '202512');"><%= cpMRL_17_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMR_5_5_dto = jdcbService.findByIdV2(todayVal, "cpMR_5_5");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_5.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', 'total');"><font color="red"> <%=cpMR_5_5_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202405');"><%= cpMR_5_5_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202406');"><%= cpMR_5_5_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202407');"><%= cpMR_5_5_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202408');"><%= cpMR_5_5_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202409');"><%= cpMR_5_5_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202410');"><%= cpMR_5_5_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202411');"><%= cpMR_5_5_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202412');"><%= cpMR_5_5_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202501');"><%= cpMR_5_5_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202502');"><%= cpMR_5_5_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202503');"><%= cpMR_5_5_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202504');"><%= cpMR_5_5_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202505');"><%= cpMR_5_5_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202506');"><%= cpMR_5_5_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202507');"><%= cpMR_5_5_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202508');"><%= cpMR_5_5_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202509');"><%= cpMR_5_5_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202510');"><%= cpMR_5_5_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202511');"><%= cpMR_5_5_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_5_5', '202512');"><%= cpMR_5_5_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMR_9_dto = jdcbService.findByIdV2(todayVal, "cpMR_9");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_9kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', 'total');"><font color="red"> <%=cpMR_9_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202405');"><%= cpMR_9_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202406');"><%= cpMR_9_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202407');"><%= cpMR_9_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202408');"><%= cpMR_9_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202409');"><%= cpMR_9_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202410');"><%= cpMR_9_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202411');"><%= cpMR_9_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202412');"><%= cpMR_9_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202501');"><%= cpMR_9_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202502');"><%= cpMR_9_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202503');"><%= cpMR_9_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202504');"><%= cpMR_9_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202505');"><%= cpMR_9_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202506');"><%= cpMR_9_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202507');"><%= cpMR_9_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202508');"><%= cpMR_9_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202509');"><%= cpMR_9_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202510');"><%= cpMR_9_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202511');"><%= cpMR_9_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_9', '202512');"><%= cpMR_9_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMR_14_dto = jdcbService.findByIdV2(todayVal, "cpMR_14");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_14kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', 'total');"><font color="red"> <%=cpMR_14_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202405');"><%= cpMR_14_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202406');"><%= cpMR_14_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202407');"><%= cpMR_14_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202408');"><%= cpMR_14_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202409');"><%= cpMR_14_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202410');"><%= cpMR_14_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202411');"><%= cpMR_14_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202412');"><%= cpMR_14_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202501');"><%= cpMR_14_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202502');"><%= cpMR_14_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202503');"><%= cpMR_14_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202504');"><%= cpMR_14_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202505');"><%= cpMR_14_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202506');"><%= cpMR_14_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202507');"><%= cpMR_14_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202508');"><%= cpMR_14_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202509');"><%= cpMR_14_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202510');"><%= cpMR_14_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202511');"><%= cpMR_14_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_14', '202512');"><%= cpMR_14_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cpMR_17_5_dto = jdcbService.findByIdV2(todayVal, "cpMR_17_5");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">CP (MR_17.5kW_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', 'total');"><font color="red"> <%=cpMR_17_5_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202405');"><%= cpMR_17_5_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202406');"><%= cpMR_17_5_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202407');"><%= cpMR_17_5_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202408');"><%= cpMR_17_5_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202409');"><%= cpMR_17_5_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202410');"><%= cpMR_17_5_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202411');"><%= cpMR_17_5_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202412');"><%= cpMR_17_5_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202501');"><%= cpMR_17_5_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202502');"><%= cpMR_17_5_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202503');"><%= cpMR_17_5_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202504');"><%= cpMR_17_5_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202505');"><%= cpMR_17_5_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202506');"><%= cpMR_17_5_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202507');"><%= cpMR_17_5_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202508');"><%= cpMR_17_5_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202509');"><%= cpMR_17_5_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202510');"><%= cpMR_17_5_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202511');"><%= cpMR_17_5_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202512');"><%= cpMR_17_5_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cp2_MRL_General_dto = jdcbService.findByIdV2(todayVal, "cp2_MRL_General");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', 'total');"><font color="red"> <%=cp2_MRL_General_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202405');"><%= cp2_MRL_General_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202406');"><%= cp2_MRL_General_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202407');"><%= cp2_MRL_General_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202408');"><%= cp2_MRL_General_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202409');"><%= cp2_MRL_General_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202410');"><%= cp2_MRL_General_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202411');"><%= cp2_MRL_General_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202412');"><%= cp2_MRL_General_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202501');"><%= cp2_MRL_General_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202502');"><%= cp2_MRL_General_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202503');"><%= cp2_MRL_General_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202504');"><%= cp2_MRL_General_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202505');"><%= cp2_MRL_General_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202506');"><%= cp2_MRL_General_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202507');"><%= cp2_MRL_General_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202508');"><%= cp2_MRL_General_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202509');"><%= cp2_MRL_General_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202510');"><%= cp2_MRL_General_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202511');"><%= cp2_MRL_General_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_General', '202512');"><%= cp2_MRL_General_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cp2_MRL_Revive_dto = jdcbService.findByIdV2(todayVal, "cp2_MRL_Revive");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MRL_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', 'total');"><font color="red"> <%=cp2_MRL_Revive_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202405');"><%= cp2_MRL_Revive_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202406');"><%= cp2_MRL_Revive_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202407');"><%= cp2_MRL_Revive_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202408');"><%= cp2_MRL_Revive_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202409');"><%= cp2_MRL_Revive_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202410');"><%= cp2_MRL_Revive_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202411');"><%= cp2_MRL_Revive_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202412');"><%= cp2_MRL_Revive_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202501');"><%= cp2_MRL_Revive_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202502');"><%= cp2_MRL_Revive_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202503');"><%= cp2_MRL_Revive_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202504');"><%= cp2_MRL_Revive_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202505');"><%= cp2_MRL_Revive_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202506');"><%= cp2_MRL_Revive_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202507');"><%= cp2_MRL_Revive_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202508');"><%= cp2_MRL_Revive_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202509');"><%= cp2_MRL_Revive_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202510');"><%= cp2_MRL_Revive_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202511');"><%= cp2_MRL_Revive_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MRL_Revive', '202512');"><%= cp2_MRL_Revive_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cp2_MR_General_dto = jdcbService.findByIdV2(todayVal, "cp2_MR_General");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_일반)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', 'total');"><font color="red"> <%=cp2_MR_General_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202405');"><%= cp2_MR_General_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202406');"><%= cp2_MR_General_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202407');"><%= cp2_MR_General_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202408');"><%= cp2_MR_General_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202409');"><%= cp2_MR_General_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202410');"><%= cp2_MR_General_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202411');"><%= cp2_MR_General_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202412');"><%= cp2_MR_General_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202501');"><%= cp2_MR_General_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202502');"><%= cp2_MR_General_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202503');"><%= cp2_MR_General_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202504');"><%= cp2_MR_General_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202505');"><%= cp2_MR_General_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202506');"><%= cp2_MR_General_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202507');"><%= cp2_MR_General_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202508');"><%= cp2_MR_General_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202509');"><%= cp2_MR_General_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202510');"><%= cp2_MR_General_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202511');"><%= cp2_MR_General_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_General', '202512');"><%= cp2_MR_General_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 cp2_MR_Revive_dto = jdcbService.findByIdV2(todayVal, "cp2_MR_Revive");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">2단계 CP(MR_회생)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', 'total');"><font color="red"> <%=cp2_MR_Revive_dto.getTotal() %> </font> </a> </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202405');"><%= cp2_MR_Revive_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202406');"><%= cp2_MR_Revive_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202407');"><%= cp2_MR_Revive_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202408');"><%= cp2_MR_Revive_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202409');"><%= cp2_MR_Revive_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202410');"><%= cp2_MR_Revive_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202411');"><%= cp2_MR_Revive_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202412');"><%= cp2_MR_Revive_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202501');"><%= cp2_MR_Revive_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202502');"><%= cp2_MR_Revive_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202503');"><%= cp2_MR_Revive_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202504');"><%= cp2_MR_Revive_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202505');"><%= cp2_MR_Revive_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202506');"><%= cp2_MR_Revive_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202507');"><%= cp2_MR_Revive_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202508');"><%= cp2_MR_Revive_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202509');"><%= cp2_MR_Revive_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202510');"><%= cp2_MR_Revive_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202511');"><%= cp2_MR_Revive_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('cp2_MR_Revive', '202512');"><%= cp2_MR_Revive_dto.getDIS202512() %></a></td>
                                    </tr>


                                    <%
                                        DashDtoV2 TM_dto = jdcbService.findByIdV2(todayVal, "TM");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(Belt Type)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', 'total');"><font color="red"> <%=TM_dto.getTotal() %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202405');"><%= TM_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202406');"><%= TM_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202407');"><%= TM_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202408');"><%= TM_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202409');"><%= TM_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202410');"><%= TM_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202411');"><%= TM_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202412');"><%= TM_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202501');"><%= TM_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202502');"><%= TM_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202503');"><%= TM_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202504');"><%= TM_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202505');"><%= TM_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202506');"><%= TM_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202507');"><%= TM_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202508');"><%= TM_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202509');"><%= TM_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202510');"><%= TM_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202511');"><%= TM_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TM', '202512');"><%= TM_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 TMRope_dto = jdcbService.findByIdV2(todayVal, "TMRope");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> TM(ROPE)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', 'total');"><font color="red"> <%=TMRope_dto.getTotal() %></font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202405');"><%= TMRope_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202406');"><%= TMRope_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202407');"><%= TMRope_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202408');"><%= TMRope_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202409');"><%= TMRope_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202410');"><%= TMRope_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202411');"><%= TMRope_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202412');"><%= TMRope_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202501');"><%= TMRope_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202502');"><%= TMRope_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202503');"><%= TMRope_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202504');"><%= TMRope_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202505');"><%= TMRope_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202506');"><%= TMRope_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202507');"><%= TMRope_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202508');"><%= TMRope_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202509');"><%= TMRope_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202510');"><%= TMRope_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202511');"><%= TMRope_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('TMRope', '202512');"><%= TMRope_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 CARTOPBOX_dto = jdcbService.findByIdV2(todayVal, "CARTOPBOX");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Car Top Box</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', 'total');"><font color="red"><%=CARTOPBOX_dto.getTotal() %></font> </a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202405');"><%= CARTOPBOX_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202406');"><%= CARTOPBOX_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202407');"><%= CARTOPBOX_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202408');"><%= CARTOPBOX_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202409');"><%= CARTOPBOX_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202410');"><%= CARTOPBOX_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202411');"><%= CARTOPBOX_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202412');"><%= CARTOPBOX_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202501');"><%= CARTOPBOX_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202502');"><%= CARTOPBOX_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202503');"><%= CARTOPBOX_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202504');"><%= CARTOPBOX_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202505');"><%= CARTOPBOX_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202506');"><%= CARTOPBOX_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202507');"><%= CARTOPBOX_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202508');"><%= CARTOPBOX_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202509');"><%= CARTOPBOX_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202510');"><%= CARTOPBOX_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202511');"><%= CARTOPBOX_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('CARTOPBOX', '202512');"><%= CARTOPBOX_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 GOV_dto = jdcbService.findByIdV2(todayVal, "GOV");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> Governor</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', 'total');"><font color="red"><%=GOV_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202405');"><%= GOV_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202406');"><%= GOV_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202407');"><%= GOV_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202408');"><%= GOV_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202409');"><%= GOV_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202410');"><%= GOV_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202411');"><%= GOV_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202412');"><%= GOV_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202501');"><%= GOV_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202502');"><%= GOV_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202503');"><%= GOV_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202504');"><%= GOV_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202505');"><%= GOV_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202506');"><%= GOV_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202507');"><%= GOV_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202508');"><%= GOV_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202509');"><%= GOV_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202510');"><%= GOV_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202511');"><%= GOV_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('GOV', '202512');"><%= GOV_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 LAMP_HOIST_dto = jdcbService.findByIdV2(todayVal, "LAMP_HOIST");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> 승강로 LAMP(HOISTWAY 기준)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', 'total');"><font color="red"><%=LAMP_HOIST_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202405');"><%= LAMP_HOIST_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202406');"><%= LAMP_HOIST_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202407');"><%= LAMP_HOIST_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202408');"><%= LAMP_HOIST_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202409');"><%= LAMP_HOIST_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202410');"><%= LAMP_HOIST_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202411');"><%= LAMP_HOIST_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202412');"><%= LAMP_HOIST_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202501');"><%= LAMP_HOIST_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202502');"><%= LAMP_HOIST_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202503');"><%= LAMP_HOIST_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202504');"><%= LAMP_HOIST_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202505');"><%= LAMP_HOIST_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202506');"><%= LAMP_HOIST_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202507');"><%= LAMP_HOIST_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202508');"><%= LAMP_HOIST_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202509');"><%= LAMP_HOIST_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202510');"><%= LAMP_HOIST_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202511');"><%= LAMP_HOIST_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('LAMP_HOIST', '202512');"><%= LAMP_HOIST_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 PIT_dto = jdcbService.findByIdV2(todayVal, "PIT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> PIT </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', 'total');"><font color="red"><%=PIT_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202405');"><%= PIT_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202406');"><%= PIT_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202407');"><%= PIT_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202408');"><%= PIT_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202409');"><%= PIT_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202410');"><%= PIT_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202411');"><%= PIT_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202412');"><%= PIT_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202501');"><%= PIT_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202502');"><%= PIT_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202503');"><%= PIT_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202504');"><%= PIT_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202505');"><%= PIT_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202506');"><%= PIT_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202507');"><%= PIT_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202508');"><%= PIT_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202509');"><%= PIT_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202510');"><%= PIT_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202511');"><%= PIT_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('PIT', '202512');"><%= PIT_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HPB_BOT_dto = jdcbService.findByIdV2(todayVal, "HPB_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;">HPB(J21,BOT 기준)</td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', 'total');"><font color="red"><%=HPB_BOT_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202405');"><%= HPB_BOT_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202406');"><%= HPB_BOT_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202407');"><%= HPB_BOT_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202408');"><%= HPB_BOT_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202409');"><%= HPB_BOT_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202410');"><%= HPB_BOT_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202411');"><%= HPB_BOT_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202412');"><%= HPB_BOT_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202501');"><%= HPB_BOT_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202502');"><%= HPB_BOT_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202503');"><%= HPB_BOT_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202504');"><%= HPB_BOT_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202505');"><%= HPB_BOT_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202506');"><%= HPB_BOT_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202507');"><%= HPB_BOT_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202508');"><%= HPB_BOT_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202509');"><%= HPB_BOT_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202510');"><%= HPB_BOT_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202511');"><%= HPB_BOT_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_BOT', '202512');"><%= HPB_BOT_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 HIP_BOT_dto = jdcbService.findByIdV2(todayVal, "HIP_BOT");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> HIP(SJ21,BOT 기준) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', 'total');"><font color="red"><%=HIP_BOT_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202405');"><%= HIP_BOT_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202406');"><%= HIP_BOT_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202407');"><%= HIP_BOT_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202408');"><%= HIP_BOT_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202409');"><%= HIP_BOT_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202410');"><%= HIP_BOT_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202411');"><%= HIP_BOT_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202412');"><%= HIP_BOT_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202501');"><%= HIP_BOT_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202502');"><%= HIP_BOT_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202503');"><%= HIP_BOT_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202504');"><%= HIP_BOT_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202505');"><%= HIP_BOT_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202506');"><%= HIP_BOT_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202507');"><%= HIP_BOT_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202508');"><%= HIP_BOT_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202509');"><%= HIP_BOT_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202510');"><%= HIP_BOT_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202511');"><%= HIP_BOT_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_BOT', '202512');"><%= HIP_BOT_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 opb_D521AG_dto = jdcbService.findByIdV2(todayVal, "opb_D521AG");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (D521AG) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', 'total');"><font color="red"><%=opb_D521AG_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202405');"><%= opb_D521AG_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202406');"><%= opb_D521AG_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202407');"><%= opb_D521AG_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202408');"><%= opb_D521AG_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202409');"><%= opb_D521AG_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202410');"><%= opb_D521AG_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202411');"><%= opb_D521AG_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202412');"><%= opb_D521AG_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202501');"><%= opb_D521AG_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202502');"><%= opb_D521AG_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202503');"><%= opb_D521AG_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202504');"><%= opb_D521AG_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202505');"><%= opb_D521AG_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202506');"><%= opb_D521AG_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202507');"><%= opb_D521AG_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202508');"><%= opb_D521AG_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202509');"><%= opb_D521AG_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202510');"><%= opb_D521AG_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202511');"><%= opb_D521AG_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_D521AG', '202512');"><%= opb_D521AG_dto.getDIS202512() %></a></td>
                                    </tr>

                                    <%
                                        DashDtoV2 opb_S521A_dto = jdcbService.findByIdV2(todayVal, "opb_S521A");
                                    %>
                                    <tr>
                                        <td style="font-weight: bold; text-align: center;"><%=countNum++%></td>
                                        <td style="font-weight: bold; text-align: center;"> OPB (S521A) </td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', 'total');"><font color="red"><%=opb_S521A_dto.getTotal() %> </font></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202405');"><%= opb_S521A_dto.getDIS202405() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202406');"><%= opb_S521A_dto.getDIS202406() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202407');"><%= opb_S521A_dto.getDIS202407() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202408');"><%= opb_S521A_dto.getDIS202408() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202409');"><%= opb_S521A_dto.getDIS202409() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202410');"><%= opb_S521A_dto.getDIS202410() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202411');"><%= opb_S521A_dto.getDIS202411() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202412');"><%= opb_S521A_dto.getDIS202412() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202501');"><%= opb_S521A_dto.getDIS202501() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202502');"><%= opb_S521A_dto.getDIS202502() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202503');"><%= opb_S521A_dto.getDIS202503() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202504');"><%= opb_S521A_dto.getDIS202504() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202505');"><%= opb_S521A_dto.getDIS202505() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202506');"><%= opb_S521A_dto.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202507');"><%= opb_S521A_dto.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202508');"><%= opb_S521A_dto.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202509');"><%= opb_S521A_dto.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202510');"><%= opb_S521A_dto.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202511');"><%= opb_S521A_dto.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('opb_S521A', '202512');"><%= opb_S521A_dto.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202506');"><%= HPB_K21_TOP.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202507');"><%= HPB_K21_TOP.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202508');"><%= HPB_K21_TOP.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202509');"><%= HPB_K21_TOP.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202510');"><%= HPB_K21_TOP.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202511');"><%= HPB_K21_TOP.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_TOP', '202512');"><%= HPB_K21_TOP.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202506');"><%= HPB_K21_MID.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202507');"><%= HPB_K21_MID.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202508');"><%= HPB_K21_MID.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202509');"><%= HPB_K21_MID.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202510');"><%= HPB_K21_MID.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202511');"><%= HPB_K21_MID.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_MID', '202512');"><%= HPB_K21_MID.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202506');"><%= HPB_K21_BOT.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202507');"><%= HPB_K21_BOT.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202508');"><%= HPB_K21_BOT.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202509');"><%= HPB_K21_BOT.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202510');"><%= HPB_K21_BOT.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202511');"><%= HPB_K21_BOT.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21_BOT', '202512');"><%= HPB_K21_BOT.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202506');"><%= HPB_K21A_TOP.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202507');"><%= HPB_K21A_TOP.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202508');"><%= HPB_K21A_TOP.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202509');"><%= HPB_K21A_TOP.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202510');"><%= HPB_K21A_TOP.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202511');"><%= HPB_K21A_TOP.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_TOP', '202512');"><%= HPB_K21A_TOP.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202506');"><%= HPB_K21A_MID.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202507');"><%= HPB_K21A_MID.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202508');"><%= HPB_K21A_MID.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202509');"><%= HPB_K21A_MID.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202510');"><%= HPB_K21A_MID.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202511');"><%= HPB_K21A_MID.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_MID', '202512');"><%= HPB_K21A_MID.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202506');"><%= HPB_K21A_BOT.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202507');"><%= HPB_K21A_BOT.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202508');"><%= HPB_K21A_BOT.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202509');"><%= HPB_K21A_BOT.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202510');"><%= HPB_K21A_BOT.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202511');"><%= HPB_K21A_BOT.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPB_K21A_BOT', '202512');"><%= HPB_K21A_BOT.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202506');"><%= HIP_SK21_TOP.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202507');"><%= HIP_SK21_TOP.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202508');"><%= HIP_SK21_TOP.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202509');"><%= HIP_SK21_TOP.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202510');"><%= HIP_SK21_TOP.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202511');"><%= HIP_SK21_TOP.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_TOP', '202512');"><%= HIP_SK21_TOP.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202506');"><%= HIP_SK21_MID.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202507');"><%= HIP_SK21_MID.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202508');"><%= HIP_SK21_MID.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202509');"><%= HIP_SK21_MID.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202510');"><%= HIP_SK21_MID.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202511');"><%= HIP_SK21_MID.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_MID', '202512');"><%= HIP_SK21_MID.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202506');"><%= HIP_SK21_BOT.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202507');"><%= HIP_SK21_BOT.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202508');"><%= HIP_SK21_BOT.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202509');"><%= HIP_SK21_BOT.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202510');"><%= HIP_SK21_BOT.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202511');"><%= HIP_SK21_BOT.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HIP_SK21_BOT', '202512');"><%= HIP_SK21_BOT.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202506');"><%= HPI_S700.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202507');"><%= HPI_S700.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202508');"><%= HPI_S700.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202509');"><%= HPI_S700.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202510');"><%= HPI_S700.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202511');"><%= HPI_S700.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_S700', '202512');"><%= HPI_S700.getDIS202512() %></a></td>
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
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202506');"><%= HPI_SC.getDIS202506() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202507');"><%= HPI_SC.getDIS202507() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202508');"><%= HPI_SC.getDIS202508() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202509');"><%= HPI_SC.getDIS202509() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202510');"><%= HPI_SC.getDIS202510() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202511');"><%= HPI_SC.getDIS202511() %></a></td>
                                        <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('HPI_SC', '202512');"><%= HPI_SC.getDIS202512() %></a></td>
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
