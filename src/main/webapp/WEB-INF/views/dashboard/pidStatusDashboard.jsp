<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.service.PIDService" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    // 출하예정일
    // searchExportIngStatus.jsp
    // http://10.225.4.20/jsp/searchLogic/searchExportIngStatus.jsp
    // http://localhost/jsp/searchLogic/searchExportIngStatus.jsp


    PIDService service = new PIDService();

    //PID 총 라인수
    ArrayList<HashMap<String, String>> data01 = service.findType01();

    //PID 개수
    ArrayList<HashMap<String, String>> data02 = service.findType02();

    //PID에 연결된 각 라인 수
    ArrayList<HashMap<String, String>> data03 = service.findType03();


    for (int i = 0; i < data01.size(); i++) {

        HashMap<String, String> d = data01.get(i);

        String date = d.get("DATE");
        String pidCount = d.get("COUNT");
    }


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>[PP]출하예정일</title>

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
    <style>
        body {
            font-family: 'Cascadia Code', sans-serif;
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
    <%--<jsp:include page="./dashboardLayoutSideBar.jsp" flush="true" />--%>
    <jsp:include page="../layout/basicSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>PID 작업 현황</h1>
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

                <div class="card-body" style="zoom:85%;">
                        <div class="col-12">
                            <div class="callout callout-info">
                                <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                <h5 style="color: blue">- PID 개발 및 수정 현황 </h5>
                                <h5>- PID 별 라인 개수 : 각 PID(최신버전)에 연결된 라인 총 수 (1주 회 집계)</h5>
                                <h5>- PID 전체 라인 수 : 작업한 전체 라인 수 (매일)</h5>
                                <h5>- PID 개수 : 등록된 PID 개수 (매일)</h5>
                                <h5>- 집계 시간은 매일 오후 6시 30분 </h5>
                            </div>
                        </div>
                </div> <!-- /.card-body -->


                <div class="row">


                    <div class="col-lg-6">
                        <div class="card card-danger">

                            <div class="card-header">
                                <h3 class="card-title"><i class="ion ion-clipboard mr-1"></i>PID 전체 라인 수</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <table id="infoTable02" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">날짜</th>
                                        <th style="font-weight: bold; text-align: center;">전체 라인 수</th>

                                    </tr>
                                    </thead>

                                    <tbody id="contentTable02">
                                    <%
                                        for (int i = 0; i < data01.size(); i++) {
                                            HashMap<String, String> d = data01.get(i);

                                            String date = d.get("DATE");
                                            String pidCount = d.get("COUNT");
                                    %>
                                    <tr>
                                        <td style="text-align: center;"><%=date%></td>
                                        <td style="text-align: center;"><%=pidCount%></td>
                                    </tr>
                                    <%
                                        }

                                    %>
                                    </tbody>

                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div><!-- /.card -->
                        <!-- /.card -->

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">
                                    <i class="ion ion-clipboard mr-1"></i>
                                    PID별 라인개수
                                </h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <table id="infoTable01" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">날짜</th>
                                        <th style="font-weight: bold; text-align: center;">PID 이름</th>
                                        <th style="font-weight: bold; text-align: center;">라인 수</th>


                                    </tr>
                                    </thead>

                                    <tbody id="contentTable01">


                                    <%
                                        for (int i = 0; i < data03.size(); i++) {
                                            HashMap<String, String> d = data03.get(i);

                                            String date = d.get("DATE");
                                            String pidName = d.get("PID");
                                            String pidCount = d.get("COUNT");
                                    %>
                                    <tr>
                                        <td style="text-align: center;"><%=date%></td>
                                        <td style="text-align: center;"><%=pidName%></td>
                                        <td style="text-align: center;"><%=pidCount%></td>
                                    </tr>
                                    <%
                                        }
                                    %>


                                    </tbody>

                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div> <!-- /.card -->

                    </div> <!-- /.col-lg-6 -->

                    <!-- /.col-md-6 -->
                    <div class="col-lg-6">
                        <div class="card card-danger">
                            <div class="card-header border-0">
                                <div class="d-flex justify-content-between">
                                    <h3 class="card-title">PID 전체 라인 수 추이</h3>
                                    <%--<a href="javascript:void(0);">View Report</a>--%>
                                </div>
                            </div>

                            <div class="card-body">
                                <figure class="highcharts-figure">
                                    <div id="cpContainer"></div>
                                </figure>
                            </div> <!-- /.card-body -->

                        </div>
                        <!-- /.card -->


                        <div class="card card-info">

                            <div class="card-header border-0">
                                <h3 class="card-title">PID 개수</h3>
                                <div class="card-tools">
                                    <a href="#" class="btn btn-sm btn-tool">
                                        <i class="fas fa-download"></i>
                                    </a>
                                    <a href="#" class="btn btn-sm btn-tool">
                                        <i class="fas fa-bars"></i>
                                    </a>
                                </div>
                            </div>

                            <div class="card-body" style="zoom:85%;">
                                <table id="infoTable03" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                        <tr class="bg-secondary">
                                            <th style="font-weight: bold; text-align: center;">날짜</th>
                                            <th style="font-weight: bold; text-align: center;">개수</th>
                                        </tr>
                                    </thead>

                                    <tbody id="contentTable03">
                                    <%
                                        for (int i = 0; i < data02.size(); i++) {
                                            HashMap<String, String> d = data02.get(i);

                                            String date = d.get("DATE");
                                            String pidCount = d.get("COUNT");
                                    %>
                                    <tr>
                                        <td style="text-align: center;"><%=date%></td>
                                        <td style="text-align: center;"><%=pidCount%></td>
                                    </tr>
                                    <%
                                        }

                                    %>
                                    </tbody>

                                </table>
                            </div> <!-- /card-body -->
                        </div> <!-- /.card -->
                    </div>
                    <!-- /.col-md-6 -->
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

    let dtTable01 = $("#infoTable01").DataTable({
        "responsive": true,
        "lengthChange": true,
        "ordering": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "paging": true,
        "searching": true,
        "order": [[0, "desc"]],
        "destroy": true, // 테이블 재생성
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable01_wrapper .col-md-6:eq(0)');

    let dtTable02 = $("#infoTable02").DataTable({
        "responsive": true,
        "lengthChange": true,
        "ordering": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "searching": false,
        "destroy": true, // 테이블 재생성
        "order": [[0, "desc"]],
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable02_wrapper .col-md-6:eq(0)');

    let dtTable03 = $("#infoTable03").DataTable({
        "responsive": true,
        "lengthChange": true,
        "ordering": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        "searching": false,
        "order": [[0, "desc"]],
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable03_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {


        Highcharts.chart('cpContainer', {


            title: {
                text: '',
                align: 'left'
            },


/*
            subtitle: {
                text: 'By Job Category. Source: <a href="https://irecusa.org/programs/solar-jobs-census/" target="_blank">IREC</a>.',
                align: 'left'
            },
*/

            yAxis: {
                title: {
                    text: '라인 수'
                }
            },

            xAxis: {
                /*accessibility: {
                    rangeDescription: 'Range: 2010 to 2022'
                }*/
                categories: [
                    '2.10', '11', '12', '13', '14'
                ]
            },

            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle'
            },

            plotOptions: {
                series: {
                    label: {
                        connectorAllowed: false
                    },
                    //pointStart: 2010
                }
            },

            series: [{
                //name: 'Installation & Developers',
                showInLegend: false,
                data: [
                    260049, 261027, 267542, 268346, 268508
                ],
                dataLabels: {
                    enabled: true
                }
            }],
            tooltip: {
                valueSuffix: ' (건)'
            },
            lagend: {
                enabled: false
            },
            credits: {
                enabled: false
            },
            responsive: {
                rules: [{
                    condition: {
                        maxWidth: 500
                    },
                    chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'
                        }
                    }
                }]
            }

        });


    });


</script>

</html>
