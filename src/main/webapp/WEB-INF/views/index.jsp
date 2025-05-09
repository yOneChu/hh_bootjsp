<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctpe html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>테스트</title>


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


    <script>
        $(document).ready(function() {
            alert('jquery good');
        })
    </script>
</head>

<body class="hold-transition sidebar-mini text-sm" style="zoom:95%;">
<div class="wrapper">
    <nav class="main-header navbar navbar-expand">
        <!-- Left navbar links -->
        <ul class="navbar-nav">

            <!--    <li class="nav-item">
                    <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" data-widget="fullscreen" href="#" role="button">
                        <i class="fas fa-expand-arrows-alt"></i>
                    </a>
                </li>-->

        </ul>
    </nav>

    <aside class="main-sidebar sidebar-dark-primary elevation-4">
        <!-- Brand Logo -->
        <a href="#" class="brand-link">
            <!--<img src="../../dist/img/AdminLTELogo.png" alt="AdminLTE Logo" class="brand-image img-circle elevation-3" style="opacity: .8">-->
            <!-- <span class="brand-text font-weight-light">설계표준화팀</span> -->
            <h4 class="text-center">설계표준화팀</h4>
        </a>

        <!-- Sidebar -->
        <div class="sidebar" style="zoom:90%;">


            <!-- Sidebar Menu -->
            <nav class="mt-2">
                <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">

                    <!-- 수배로직 -->
                    <li class="nav-item menu-open">
                        <a href="#" class="nav-link">
                            <i class="nav-icon fas fa-edit"></i>
                            <p>
                                수배로직
                                <i class="right fas fa-angle-left"></i>
                            </p>
                        </a>

                        <ul class="nav nav-treeview">
                            <li class="nav-item">
                                <a href="/jsp/help/searchLogicMain.jsp" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>PID-상세조회</p>
                                </a>
                            </li>

                            <li class="nav-item">
                                <a href="/jsp/searchLogic/searchComparePartCN.jsp" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>한국-중국 자재비교(CN)</p>
                                </a>
                            </li>

                            <!-- <li class="nav-item">
                                <a href="/jsp/searchLogic/searchCodeListView.jsp" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>마스터정보 리스트</p>
                                </a>
                            </li> -->

                            <li class="nav-item">
                                <a href="/jsp/searchLogic/searchProductIngStatus.jsp" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>(SAP)품목별 공정진행 현황</p>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a href="/jsp/searchLogic/searchExportIngStatus.jsp" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>(SAP)출하예정일</p>
                                </a>
                            </li>

                            <li class="nav-item">
                                <a href="/designRequestDashboard" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>(신)전산작업요청_현황</p>
                                </a>
                            </li>

                            <!-- <li class="nav-item">
                                <a href="#" class="nav-link">
                                    <i class="far fa-circle nav-icon"></i>
                                    <p>원가실적조회(예정)</p>
                                </a>
                            </li> -->
                        </ul>
                    </li>

                </ul>

            </nav>

        </div>

    </aside>


    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>부품공용화 - 월별실적 Dashboard <font color="red">(2024/11/17, 15:00기준)</font> </h1>
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
                                    <!--    <tr >
                                            <th style="font-weight: bold; text-align: center;" colspan="9" class="bg-secondary">한국PLM</th>
                                            <th style="font-weight: bold; text-align: center;" colspan="11" class="bg-secondary">중국PLM</th>
                                        </tr>-->

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">과제명</th>
                                        <th style="font-weight: bold; text-align: center;" rowspan="3">적용일정</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="11">월별실적</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;" colspan="8">2024년</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="3">2025년</th>
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
        <strong>Copyright &copy; 2024 <a href="#">설계표준화팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>


</body>
</html>


