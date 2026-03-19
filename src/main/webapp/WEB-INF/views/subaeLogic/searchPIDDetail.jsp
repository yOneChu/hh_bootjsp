<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>로직 PID Analytics</title>

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
    <link rel="stylesheet" href="/resources/dashboard/tweak.css">
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
    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>로직 PID Analytics</h1>
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

                <!-- 검색조건 -->
                <!-- <div class="card card-default"> -->
                <div class="card card-primary">
                    <div class="card-header">
                        <h3 class="card-title"> 검색 조건</h3>

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
                                    <h5 style="color: #60A5FA;">- 10,000건 이상의 경우 "EXCEL Download"로 받으시기 바랍니다. </h5>
                                    <h5>- 현재 Excel 다운로드 시 숫자인식 관련 문제가 있어 Copy로 복사 후, EXCEL에 붙여넣기 하시기 바랍니다. </h5>
                                    <h5>- 조건1에 REMARKS로 검색 시, 조건2의 PID는 검색할 수 없습니다. </h5>
                                    <h5>- PID02 조건이 공백이라면 해당 조건은 포함되지 않고 조회된다. </h5>
                                    <h5>- PID-03,04,05는 앞 부분이 입력되어야 뒷부분을 입력가능합니다. (ex. PID04만 입력불가(03누락), PID03,05만 입력불가(04누락)). </h5>
                                    <h5>- PID-GROUP을 EQUAL,LIKE 입력시  PID03,04,05는 OR 조건으로 동작하고, NOT EQUAL, NOT LIKE 입력시 PID03,04,05는 AND 조건으로 동작합니다. </h5>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>조건-01</label>
                                    <select id="spec-01" class="form-control select" style="width: 100%;">
                                        <option selected="selected">SPEC</option>
                                        <option>CON</option>
                                        <option>KEY</option>
                                        <option>VAL</option>
                                        <option>REMARKS</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>-</label>
                                    <select id="link-01" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>EQUAL</option>
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


                            <div class="col-md-4">
                                <div class="form-group">
                                    <%--<label class="btn btn-default text-center">조건-02</label>--%>
                                        <label>조건-02</label>
                                    <select id="spec-02" class="form-control select" style="width: 100%;">
                                        <option selected="selected">SPEC</option>
                                        <option>CON</option>
                                        <option>KEY</option>
                                        <option>VAL</option>
                                        <!-- <option>REMARKS</option> -->
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>-</label>
                                    <select id="link-02" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>NOT LIKE</option>
                                        <option>EQUAL</option>
                                        <option>NOT_EQUAL</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <%--<label>PID-02</label>--%>
                                    <label>-</label>
                                    <input type="search" id="pidVal02" class="form-control" placeholder="PID-02" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <!-- AND / OR Join Operator -->
                            <div class="col-md-12">
                                <div class="form-group text-center my-2">
                                    <span class="badge badge-light border px-2 py-1 mr-2">(조건-01)</span>
                                    <select id="joinOp" class="custom-select w-auto d-inline-block">
                                        <option value="F" selected>-</option>
                                        <option value="AND" >AND</option>
                                        <%--<option value="OR">OR</option>--%>
                                    </select>
                                    <span class="badge badge-light border px-2 py-1 ml-2">
                                        (조건-02)
                                    </span>
                                    -> 조건1, 2를 and 조건으로 둘 다 만족하는 행 조회 시 사용
                                </div>
                            </div>

                            <%--PID-GROUP--%>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>PID-GROUP</label>
                                    <select id="con-05" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>NOT LIKE</option>
                                        <option>EQUAL</option>
                                        <option>NOT EQUAL</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>PID-03</label>
                                    <input type="search" id="pidVal03" class="form-control" placeholder="PID-03" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>PID-04</label>
                                    <input type="search" id="pidVal04" class="form-control" placeholder="PID-04" value="" readonly>
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>PID-05</label>
                                    <input type="search" id="pidVal05" class="form-control" placeholder="PID-05" value="" readonly>
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>


                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>


                    <div class="card-footer">
                        <button class="btn btn-secondary float-right" style="margin-right: 5px;" onclick="searchExcel()">EXCEL Download</button>
                        <button class="btn btn-primary float-right" style="margin-right: 5px;" onclick="searchPID()">검색</button>
                    </div>

                </div>


                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h3 class="card-title mb-0">검색 결과</h3>
                                <%--<button id="toggleEmptyColsBtn" type="button" class="btn btn-sm btn-outline-secondary" onclick="hideColumns()">빈 컬럼 숨기기</button>--%>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <!-- bg-primary -->
                                    <tr class="bg-secondary" id="headerInfo">
                                        <th>PID</th>
                                        <th>NO</th>
                                        <th>ADDR</th>
                                        <th>REMARKS</th>

                                        <th>SPEC1</th> <th>CON1</th>
                                        <th>SPEC2</th> <th>CON2</th>
                                        <th>SPEC3</th> <th>CON3</th>
                                        <th>SPEC4</th> <th>CON4</th>
                                        <th>SPEC5</th> <th>CON5</th>
                                        <th>SPEC6</th> <th>CON6</th>
                                        <th>SPEC7</th> <th>CON7</th>
                                        <th>SPEC8</th> <th>CON8</th>
                                        <th>SPEC9</th> <th>CON9</th>
                                        <th>SPEC10</th> <th>CON10</th>

                                        <th>SPEC11</th> <th>CON11</th>
                                        <th>SPEC12</th> <th>CON12</th>
                                        <th>SPEC13</th> <th>CON13</th>
                                        <th>SPEC14</th> <th>CON14</th>
                                        <th>SPEC15</th> <th>CON15</th>
                                        <th>SPEC16</th> <th>CON16</th>
                                        <th>SPEC17</th> <th>CON17</th>
                                        <th>SPEC18</th> <th>CON18</th>
                                        <th>SPEC19</th> <th>CON19</th>
                                        <th>SPEC20</th> <th>CON20</th>

                                        <th>KEY1</th> <th>VAL1</th>
                                        <th>KEY2</th> <th>VAL2</th>
                                        <th>KEY3</th> <th>VAL3</th>
                                        <th>KEY4</th> <th>VAL4</th>
                                        <th>KEY5</th> <th>VAL5</th>
                                        <th>KEY6</th> <th>VAL6</th>
                                        <th>KEY7</th> <th>VAL7</th>
                                        <th>KEY8</th> <th>VAL8</th>
                                        <th>KEY9</th> <th>VAL9</th>
                                        <th>KEY10</th> <th>VAL10</th>

                                        <th>KEY11</th> <th>VAL11</th>
                                        <th>KEY12</th> <th>VAL12</th>
                                        <th>KEY13</th> <th>VAL13</th>
                                        <th>KEY14</th> <th>VAL14</th>
                                        <th>KEY15</th> <th>VAL15</th>
                                        <th>KEY16</th> <th>VAL16</th>
                                        <th>KEY17</th> <th>VAL17</th>
                                        <th>KEY18</th> <th>VAL18</th>
                                        <th>KEY19</th> <th>VAL19</th>
                                        <th>KEY20</th> <th>VAL20</th>
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
        <strong>Copyright &copy; 2026 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>


</body>


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
<script src="/resources/javascript/searchPIDDetail.js"></script>

</html>
