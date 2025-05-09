<%@ page import="com.kyhslam.util.DesignReqCommonUtil" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.kyhslam.dto.DesignRequestDTO" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.kyhslam.service.DesignDashboardService" %>
<%@ page import="com.kyhslam.dto.DesignDashDTO" %>
<%@ page import="com.kyhslam.util.ElvInfoCommonUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%


    //기종 종류

    ArrayList<String> typeList = ElvInfoCommonUtil.getTypeList();







%>


<!doctpe html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <%--<meta name="viewport" content="width=device-width, user-scalable=no">--%>
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>수배로직설계팀</title>


    <link rel="stylesheet" href="resources/dist/googleFont.css">

    <!-- Font Awesome -->
    <link rel="stylesheet" type="text/css" href="resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="resources/dist/plugins/select2/css/select2.min.css">

    <!-- Theme style -->
    <link rel="stylesheet" href="resources/dist/css/adminlte.min.css">

    <%--    <script type="text/javascript" src="resources/dist/js/jquery-3.7.1.min.js"></script>--%>
    <script src="resources/dist/js/jquery-3.7.1.min.js"></script>


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

    <jsp:include page="./dashboardLayoutSideBar.jsp" flush="true" />


    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <%--<h1>(신)전산작업요청 현황 <font color="red">(2024/11/17, 15:00기준)</font> </h1>--%>
                        <h1>(신)전산작업요청 현황 </h1>
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
        <section class="content" style="zoom:100%;">

            <div class="container-fluid"> <!-- start - container-fluid -->

                <div class="row">
                    <!-- <div class="col-12"> -->
                    <div class="col-lg-7">
                        <!-- <section class="col-lg-6 connectedSortable ui-sortable"> -->

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">Dashboard(수량)</h3>
                            </div>

                            <!-- /.card-header -->
                            <%--<div class="card-body" style="zoom:100%;">--%>
                            <div class="card-body">
<%--

                                <div class="col-md-12">
                                    <div class="callout callout-info">
                                        <h5><i class="fas fa-bullhorn"></i> 도움말</h5>
                                        - 2024년 PLM에 등록된 기종별 현황 <br>
                                        - PICK,수량분리 선택 시 수량,PICK 합산되기 전의 정보가 분리되어 표시됨.
                                    </div>
                                </div>
--%>


                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>

                                    <!-- bg-primary -->
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">기종</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">총</th>
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

                                        for (int i = 0; i < typeList.size(); i++) {
                                            String type = typeList.get(i);

                                            HashMap<String, String> data = ElvInfoCommonUtil.getMonthDashboard(type);

                                            String c202401 = data.get("202401");
                                            String c202402 = data.get("202402");
                                            String c202403 = data.get("202403");
                                            String c202404 = data.get("202404");
                                            String c202405 = data.get("202405");
                                            String c202406 = data.get("202406");
                                            String c202407 = data.get("202407");
                                            String c202408 = data.get("202408");
                                            String c202409 = data.get("202409");
                                            String c202410 = data.get("202410");
                                            String c202411 = data.get("202411");
                                            String c202412 = data.get("202412");

                                    %>
                                        <tr>
                                            <td><%=(i+1)%></td>
                                            <td><%=type%></td>
                                            <td>322</td>
                                            <td><%=c202401%></td>
                                            <td><%=c202402%></td>
                                            <td><%=c202403%></td>
                                            <td><%=c202404%></td>
                                            <td><%=c202405%></td>
                                            <td><%=c202406%></td>
                                            <td><%=c202407%></td>
                                            <td><%=c202408%></td>
                                            <td><%=c202409%></td>
                                            <td><%=c202410%></td>
                                            <td><%=c202411%></td>
                                            <td><%=c202412%></td>

                                        </tr>
                                    <%
                                        }  // end for
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


                </div> <!-- /.row -->


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



<%--<script src="resources/dist/js/jquery-3.7.1.min.js"></script>--%>

<%--<script src="/adminLte/js/jquery-3.7.1.min.js"></script>--%>

<!-- AdminLTE App -->
<script src="resources/dist/js/adminlte.min.js"></script>

<!-- Bootstrap 4 -->
<script src="resources/dist/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- DataTables  & Plugins -->
<script src="resources/dist/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="resources/dist/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="resources/dist/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
<script src="resources/dist/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>


<script src="resources/dist/plugins/select2/js/select2.full.min.js"></script>

<script src="resources/dist/plugins/datatables-buttons/js/dataTables.buttons.min.js"></script>
<script src="resources/dist/plugins/datatables-buttons/js/buttons.bootstrap4.min.js"></script>
<script src="resources/dist/plugins/jszip/jszip.min.js"></script>
<script src="resources/dist/plugins/pdfmake/pdfmake.min.js"></script>
<script src="resources/dist/plugins/pdfmake/vfs_fonts.js"></script>
<script src="resources/dist/plugins/datatables-buttons/js/buttons.html5.min.js"></script>
<script src="resources/dist/plugins/datatables-buttons/js/buttons.print.min.js"></script>
<script src="resources/dist/plugins/datatables-buttons/js/buttons.colVis.min.js"></script>


<script>
    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        "ordering": true,
        "paging": false,
        "searching": false,
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');



    //ready
    $(document).ready(function() {

        console.log('start designDashboard');


    });


</script>


</html>


