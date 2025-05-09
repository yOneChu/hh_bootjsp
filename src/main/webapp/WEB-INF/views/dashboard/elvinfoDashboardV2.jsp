<%@ page import="com.kyhslam.util.DesignReqCommonUtil" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.kyhslam.dto.DesignRequestDTO" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.kyhslam.service.DesignDashboardService" %>
<%@ page import="com.kyhslam.dto.DesignDashDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
    //(신)전산요청 현황
    //기계-수배

    DesignDashboardService service = new DesignDashboardService();


    ArrayList<DesignDashDTO> data = service.getDashboard();






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
        <section class="content" style="zoom:90%;">

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
                    <div class="card-body">
                        <div class="row">

                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger">
                                    <h5><i class="fas fa-bullhorn"></i> 도움말</h5>
                                    <!-- <p class="text-info"> " 제품번호 입력 시 최신버전의 BOM 1레벨 추출." </p> -->
                                    <!-- <p class="text-danger"> " 제품번호 입력 시 최신버전의 BOM 1레벨 추출." </p> -->
                                    <!-- <p class="text-primary"> " 제품번호 입력 시 최신버전의 BOM 1레벨 추출."&nbsp; </p> -->
                                    <!-- <p> > 제품번호 입력 시 최신버전의 BOM 1레벨 추출." </p> 줄바꿈은 <br>로 해라. -->
                                    - 입력된 제품번호와 Block.No를 가지고 제품의 하위 <font color="red">전 레벨</font>을 검색하여 해당 Block.No와 연관된 부품들 출력. <br>
                                    - <font color="red">여러개의 제품번호를 입력하여 검색하고자 할 경우 꼭 구분값 ";"로 구분해주어야 됨. </font> <font color="white">(ex. 206397L06;206397L25;299397L17 ) </font> <br>
                                    - 속도 및 검색 개수의 한계가 있으므로 몇백 개 이상의 제품번호 입력을 통한 검색은 지양 요청. <br>
                                    - EQUAL: 입력한 Block.No와 정확히 <font color="red">일치</font>하는 자재만 검색 <br>
                                    - LIKE : 입력한 Block.No를 <font color="red">포함</font>하는 모든 자재 검색
                                </div>
                            </div>

                            <!-- <div class="input-group input-group-lg"> -->
                            <div class="col-md-6">
                                <!-- <div class="col-6"> -->
                                <div class="form-group">
                                    <label>제품번호</label>
                                    <!-- <input type="search" id="pidVal" class="form-control" placeholder="제품번호 입력하시오." value=""> -->

                                    <textarea class="form-control" id="prodVal" rows="2" placeholder="제품번호 입력하시오. (여러 제품번호로 검색 시 구분값 ; 로 구분해주시기 바랍니다.)"></textarea>

                                </div>
                            </div>

                            <div class="col-md-6">
                                <!-- <div class="col-6"> -->
                                <div class="form-group">
                                    <label>Block.NO</label>
                                    <input type="search" id="blockVal" class="form-control" placeholder="BLOCK.NO 입력하시오." value="">
                                    <!--  <div class="input-group-append">
                                         <button class="btn btn-outline-primary" onclick="searchVersion()">검색</button>
                                     </div> -->
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Block.No ( LIKE , EQUAL)</label>
                                    <select id="con-02" class="form-control select2bs4" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>EQUAL</option>
                                    </select>
                                </div>
                            </div>


                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>



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
        //"responsive": true,
        "lengthChange": true,
        "pageLength": 50,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        "ordering": false,
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


