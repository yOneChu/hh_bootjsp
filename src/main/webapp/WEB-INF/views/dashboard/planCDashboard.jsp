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
<%@ page import="com.kyhslam.service.PlanCService" %>
<%@ page import="com.kyhslam.util.DateUtil" %>
<%@ page import="com.kyhslam.domain.ProductPlanC" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kyhslam.domain.PlanCDash" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //부품공용화 수량 - 대시보드 개발
    // searchPriceReductionRate.jsp
    // http://10.225.4.20/jsp/searchLogic/searchPriceReductionRate.jsp
    // http://localhost/jsp/searchLogic/searchPriceReductionRate.jsp

    String contextPath = request.getContextPath();
    System.out.println("--- searchPriceReductionRate.jsp ---");

    LocalDate now = LocalDate.now();
    //String todayVal = now.toString();
    String todayVal = DateUtil.getYesterdayDate();

    // ServletContext에서 Spring WebApplicationContext 얻기
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

   /* // 원하는 Bean 가져오기
    PlanCService planCService = (PlanCService) context.getBean("PlanCService");

    System.out.println("planCService = " + planCService);

    //List<PlanCDash> result =  planCService.findPlanDash(todayVal);
    List<PlanCDash> result =  planCService.findPlanDashAsBrand("2026-02-05", "LUXEN_2");
    //findPlanDashAsBrand

    System.out.println("result.size() = " + result.size());*/
    
    int countNum = 1;
    System.out.println(" ---------------- end dashboard -------------");

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>PLAN-C 원가절감 실적 집계</title>

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
<%--    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true">
        <jsp:param name="menuType" value="dashboard" />
    </jsp:include>--%>


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <!-- <h1>부품공용화 - 월별실적 Dashboard <font color="red">(2024/11/17, 06:00기준)</font> </h1> -->
                        <h1>PLAN-C 대시보드 - 월별실적(수량) <font color="red">( <%=todayVal %>, 04:00기준)</font> </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <%--<li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>--%>

                            <div class="custom-control custom-switch custom-switch-off-danger custom-switch-on-success">
                                <input type="checkbox" class="custom-control-input" id="darkModeToggle">
                                <label class="custom-control-label" for="darkModeToggle">🌓 다크모드</label>
                            </div>

                        </ol>
                    </div>
                </div>
            </div><!-- /.container-fluid -->
        </section>



        <!-- Main content -->
        <section class="content" style="zoom:95%;">

            <div class="container-fluid"> <!-- start - container-fluid -->

                <div class="card card-primary">
                    <div class="card-header">
                        <h3 class="card-title"> 테스트 </h3>

                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                            <button type="button" class="btn btn-tool" data-card-widget="remove">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                    </div>

                    <!-- /.card-header -->
                    <div class="card-body" style="zoom:85%;">
                        <div class="row">
                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger" style="zoom:85%;">
                                    <h4><%--<i class="fas fa-bullhorn"></i>--%>🔊 도움말</h4>
                                    <h5 style="color: #60A5FA;">- etc는 SAP에 출하예정일이 지정되지 않은 호기 입니다.</h5>
                                    <h5>- 'Excel 다운로드'는 집계에 포함된 모든 실적 자재들 입니다. </h5>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>브랜드</label>
                                    <select id="brand" class="form-control select" style="width: 100%;">
                                        <option selected="selected" value="LUXEN_2">LUXEN_G</option>
                                        <option value="NEX_MR">NEX_MRL_G</option>
                                        <option>NEX_MR_G</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>-</label>
                                    <select id="partName" class="form-control select" style="width: 100%;">
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>-</label>
                                    <%--<label>PID-01</label>--%>
                                    <input type="search" id="pidVal" class="form-control" placeholder="PID-01" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>


                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>


                    <div class="card-footer">
                        <button class="btn btn-secondary float-right" style="margin-right: 5px;" onclick="searchExcel()">전체 데이터 EXCEL Download</button>
                        <%--<button class="btn btn-primary float-right" style="margin-right: 5px;" onclick="searchPID()">검색</button>--%>
                    </div>

                </div>


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
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">INDEX</th>
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">브랜드</th>
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">자재번호</th>
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">과제명</th>
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">총 수량</th>
                                            <th style="font-weight: bold; text-align: center;" colspan="20">월별실적</th>
                                        </tr>

                                        <tr class="bg-secondary">
                                            <th style="font-weight: bold; text-align: center;" colspan="13">2026년</th>
                                        </tr>

                                        <tr class="bg-secondary">
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

                                    </tbody>

                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div>
                        <!-- /.card -->
                    </div>
                    <!-- </section> -->
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
        <strong>Copyright &copy; 2026 <a href="#">수배로직설계팀</a>.</strong> All rights reserved.
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

<script src="/resources/javascript/commonUtil.js"></script>
<script src="/resources/javascript/planCDashboard.js"></script>


</html>
