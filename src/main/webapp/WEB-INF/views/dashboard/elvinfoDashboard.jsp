<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@ page import="com.kyhslam.util.ElvInfoCommonUtil" %>
<%@ page import="com.kyhslam.service.ELVInfoService" %>
<%@ page import="com.kyhslam.dto.DesignDashDTO" %>
<%@ page import="com.kyhslam.dto.ELVInfoDashDTO" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>



<%

    //기종 종류

    ArrayList<String> typeList = ElvInfoCommonUtil.getTypeList();

    //ELVInfoService elvInfoService = new ELVInfoService();
    ArrayList<ELVInfoDashDTO> data = ElvInfoCommonUtil.getDashbard();




%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->

    <title>법인자재 1LV 표준수배자재리스트</title>

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


<body class="hold-transition sidebar-mini text-sm" style="zoom:100%;">

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
    <jsp:include page="./dashboardLayoutSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->

        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>2024년 영업사양 등록 현황 </h1>
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
        <section class="content" style="zoom:90%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <!--<div class="col-lg-7"> -->

                <div class="card card-primary">

                    <div class="card-header" >
                        <h3 class="card-title"> </h3>
                    </div>

                    <!-- /.card-header -->

                    <div class="card-body" style="zoom:85%;">
                        <div class="row">

                            <div class="col-md-12">
                                <div class="callout callout-info">
                                    <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                    <%--<ul>
                                        <li>2024년에 PLM에 등록된 영업사양 현황 </li>
                                        <li>기종 별 기준으로 1~12월에 '릴리즈'된 데이터 기준으로 집계</li>
                                    </ul>--%>
                                    <h5>- 2024년도 PLM에 등록된 영업사양 등록 현황 </h5>
                                    <h5>- 집계 데이터 조건 : 각 월별 릴리즈 된 호기의 영업사양 정보 집계</h5>
                                    <h5>- 건 수 클릭 시, 상세 목록 및 사양 값 출력 (기종, 속도, 용도, 층수, SILL재질, TRANSOM색상 등)</h5>
                                </div>
                            </div>
                        </div>
                    </div>  <!-- /.card-body -->
                </div> <!-- /.card Primary -->

                    <div class="row">
                        <div class="col-12">

                            <div class="card card-primary">

                              <%--  <div class="card-header">
                                    <h3 class="card-title">검색 결과</h3>
                                </div>--%>

                                <!-- /.card-header -->
                                <div class="card-body" style="zoom:92%;">

                                    <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                    <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                        <thead>

                                        <!-- bg-primary -->
                                        <tr class="bg-secondary">
                                            <%--<th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>--%>
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">기종</th>
                                            <th style="font-weight: bold; text-align: center;" rowspan="3">총 합계(건)</th>
                                        </tr>

                                        <tr class="bg-secondary">
                                            <th style="font-weight: bold; text-align: center;" colspan="12">2024년</th>
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
                                        <%

                                            for (int i = 0; i < data.size(); i++) {
                                                ELVInfoDashDTO d = data.get(i);


                                                String c202401 = d.getDis202401();
                                                String c202402 = d.getDis202402();
                                                String c202403 = d.getDis202403();
                                                String c202404 = d.getDis202404();
                                                String c202405 = d.getDis202405();
                                                String c202406 = d.getDis202406();
                                                String c202407 = d.getDis202407();
                                                String c202408 = d.getDis202408();
                                                String c202409 = d.getDis202409();
                                                String c202410 = d.getDis202410();
                                                String c202411 = d.getDis202411();
                                                String c202412 = d.getDis202412();

                                                String elvType = d.getElvType();
                                                String totalCnt = d.getTotalCnt();

                                        %>
                                        <tr>
                                            <%-- <td><%=(i+1)%></td>--%>
                                            <td style="font-weight: bold; text-align: center;"><%=elvType%></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', 'total', '<%=totalCnt%>');">
                                                <font color="red"><%=totalCnt %></font></a>
                                            </td>

                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202401', '<%=c202401%>');"><%=c202401%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202402', '<%=c202402%>');"><%=c202402%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202403', '<%=c202403%>');"><%=c202403%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202404', '<%=c202404%>');"><%=c202404%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202405', '<%=c202405%>');"><%=c202405%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202406', '<%=c202406%>');"><%=c202406%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202407', '<%=c202407%>');"><%=c202407%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202408', '<%=c202408%>');"><%=c202408%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202409', '<%=c202409%>');"><%=c202409%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202410', '<%=c202410%>');"><%=c202410%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202411', '<%=c202411%>');"><%=c202411%></a></td>
                                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList('<%=elvType%>', '202412', '<%=c202412%>');"><%=c202412%></a></td>

                                        </tr>
                                        <%
                                            }  // end for
                                        %>
                                        </tbody>

                                    </table>
                                </div>  <!--/ card-body -->

                            </div> <!-- / card-primary -->


                        </div>

                    </div>  <!-- /.row -->

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



<!-- <script src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.10.24/js/dataTables.bootstrap4.min.js"></script>
 -->
.

<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        //"lengthChange": true,
        "pageLength": 30,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        //"processing": true,
        "destroy": true, // 테이블 재생성
        "paging": false,
        "searching": true,
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {



    });

    //total
    function viewList(elvType, month, totalCnt) {

        if(totalCnt > 10000) {
            alert("10,000건 이상은 담당자에게 요청해주세요.");
            return;
        }

        //http://10.225.4.20/jsp/searchLogic/searchPriceReductionRate.jsp
        let urlValue = "/elvinfoDashboardPop?";
        urlValue += "elvType=" + elvType;
        urlValue += "&month=" + month;
        window.open(urlValue,'_blank','width=1700, height=800, top=50, left=50, scrollbars=yes');
    }



</script>

</html>
