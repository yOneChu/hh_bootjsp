<%@ page import="com.kyhslam.util.DesignReqCommonUtil" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.kyhslam.dto.DesignRequestDTO" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.kyhslam.service.DesignDashboardService" %>
<%@ page import="com.kyhslam.dto.DesignDashDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
    //(신)전산요청 현황 팝업창
    //DesignDashboardService service = new DesignDashboardService();

    ArrayList<DesignDashDTO> data = DesignReqCommonUtil.getDashboard();

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
                            <h1>(新)전산작업요청 현황 </h1>
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



                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary card-tabs">

                            <div class="card-header p-0 pt-1">
                                <%--<h3 class="card-title">Dashboard</h3>--%>

                                <ul class="nav nav-tabs" id="custom-content-above-tab" role="tablist">
                                    <li class="nav-item">
                                        <a class="nav-link active" id="custom-content-above-home-tab" data-toggle="pill" href="#custom-content-above-home" role="tab" aria-controls="custom-content-above-home" aria-selected="false">완료</a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" id="custom-content-above-profile-tab" data-toggle="pill" href="#custom-content-above-profile" role="tab" aria-controls="custom-content-above-profile" aria-selected="false">등록</a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link" id="custom-content-above-messages-tab" data-toggle="pill" href="#custom-content-above-messages" role="tab" aria-controls="custom-content-above-messages" aria-selected="true">시각화</a>
                                    </li>
                                </ul>
                            </div>

                            <!-- /.card-header -->
                            <%--<div class="card-body" style="zoom:100%;">--%>
                            <div class="card-body">


                                <div class="col-md-12">
                                    <div class="callout callout-info">
                                        <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                        <%--<ul>
                                            <li>PLM (新)전산 작업요청 완료 및 등록 현황</li>
                                            <li>완료/등록 Tab을 통해 확인 가능</li>
                                        </ul>--%>
                                        <%--<p>- PLM (新)전산 작업요청 완료 및 등록 현황 </p><br>
                                        <p>- 완료/등록 Tab을 통해 확인 가능</p>--%>
                                        <h5>PLM (新)전산 작업요청 완료 및 등록 현황 </h5>
                                        <h5>완료/등록 Tab을 통해 확인 가능</h5>
                                    </div>
                                </div>


<%--


                                <div class="tab-custom-content">
                                    <p class="lead mb-0">-</p>
                                </div>
--%>


                                <div class="tab-content" id="custom-content-above-tabContent">
                                    <div class="tab-pane fade active show" id="custom-content-above-home" role="tabpanel" aria-labelledby="custom-content-above-home-tab">

                                        <table id="infoTable02" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;" >
                                            <thead>

                                                <tr class="bg-secondary">
                                                    <th style="font-weight: bold; text-align: center;" rowspan="3">구분</th>
                                                    <th style="font-weight: bold; text-align: center;" rowspan="3">작업구분</th>
                                                    <th style="font-weight: bold; text-align: center;" rowspan="3">전체</th>
                                                    <th style="font-weight: bold; text-align: center;" colspan="13">월별 실적 (완료)</th>
                                                </tr>

                                                <tr class="bg-secondary">
                                                    <th style="font-weight: bold; text-align: center;" colspan="12">2025년</th>
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

                                                    DesignDashDTO dto = data.get(i);
                                                    String batchDate = dto.getBatchDate();

                                                    int cre202501 = Integer.parseInt(dto.getCre202501());
                                                    int cre202502 = Integer.parseInt(dto.getCre202502());
                                                    int cre202503 = Integer.parseInt(dto.getCre202503());
                                                    int cre202504 = Integer.parseInt(dto.getCre202504());
                                                    int cre202505 = Integer.parseInt(dto.getCre202505());
                                                    int cre202506 = Integer.parseInt(dto.getCre202506());
                                                    int cre202507 = Integer.parseInt(dto.getCre202507());
                                                    int cre202508 = Integer.parseInt(dto.getCre202508());
                                                    int cre202509 = Integer.parseInt(dto.getCre202509());
                                                    int cre202510 = Integer.parseInt(dto.getCre202510());
                                                    int cre202511 = Integer.parseInt(dto.getCre202511());
                                                    int cre202512 = Integer.parseInt(dto.getCre202512());



                                                    int totalCnt = 0;
                                                    totalCnt = cre202501 + cre202502 + cre202503 + cre202504 + cre202505
                                                            + cre202506 + cre202507 + cre202508 + cre202509 + cre202510 + cre202511 + cre202512;

                                                    //e6f2ff
                                                    String colorValue = "";
                                                    String gubun = dto.getGubun();


                                            %>


                                                <tr style="background-color: <%=colorValue%>">

                                                    <td style="font-weight: bold; text-align: center;"><%=gubun%></td>
                                                    <td style="font-weight: bold; text-align: center;"><%=dto.getGubunName()%></td>
                                                    <td style="font-weight: bold; text-align: center; color: red;"><%=totalCnt%></td>

                                                    <td style="text-align: center;"><%=dto.getCre202501()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202502()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202503()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202504()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202505()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202506()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202507()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202508()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202509()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202510()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202511()%></td>
                                                    <td style="text-align: center;"><%=dto.getCre202512()%></td>
                                                </tr>

                                            <%
                                                } // end for
                                            %>
                                            </tbody>
                                        </table>



                                    </div>

                                    <div class="tab-pane fade" id="custom-content-above-profile" role="tabpanel" aria-labelledby="custom-content-above-profile-tab">
                                        <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                            <thead>

                                                <tr class="bg-secondary">
                                                    <th style="font-weight: bold; text-align: center;" rowspan="3">구분</th>
                                                    <th style="font-weight: bold; text-align: center;" rowspan="3">작업구분</th>
                                                    <th style="font-weight: bold; text-align: center;" rowspan="3">전체</th>
                                                    <th style="font-weight: bold; text-align: center;" colspan="13">월별 실적 (등록)</th>
                                                </tr>

                                                <tr class="bg-secondary">
                                                    <th style="font-weight: bold; text-align: center;" colspan="12">2025년</th>
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

                                            <tbody id="contentTable02">

                                            <%
                                                for (int i = 0; i < data.size(); i++) {

                                                    DesignDashDTO dto = data.get(i);
                                                    String batchDate = dto.getBatchDate();

                                                    int com202501 = Integer.parseInt(dto.getCom202501());
                                                    int com202502 = Integer.parseInt(dto.getCom202502());
                                                    int com202503 = Integer.parseInt(dto.getCom202503());
                                                    int com202504 = Integer.parseInt(dto.getCom202504());
                                                    int com202505 = Integer.parseInt(dto.getCom202505());
                                                    int com202506 = Integer.parseInt(dto.getCom202506());
                                                    int com202507 = Integer.parseInt(dto.getCom202507());
                                                    int com202508 = Integer.parseInt(dto.getCom202508());
                                                    int com202509 = Integer.parseInt(dto.getCom202509());
                                                    int com202510 = Integer.parseInt(dto.getCom202510());
                                                    int com202511 = Integer.parseInt(dto.getCom202511());
                                                    int com202512 = Integer.parseInt(dto.getCom202512());



                                                    int totalCnt = 0;
                                                    totalCnt = com202501 + com202502 + com202503 + com202504 + com202505
                                                            + com202506 + com202507 + com202508 + com202509 + com202510 + com202511 + com202512;

                                                    //e6f2ff
                                                    String colorValue = "";
                                                    String gubun = dto.getGubun();


                                            %>


                                            <tr style="background-color: <%=colorValue%>">

                                                <td style="font-weight: bold; text-align: center;"><%=gubun%></td>
                                                <td style="font-weight: bold; text-align: center;"><%=dto.getGubunName()%></td>
                                                <td style="font-weight: bold; text-align: center; color: red;"><%=totalCnt%></td>

                                                <td style="text-align: center;"><%=dto.getCom202501()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202502()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202503()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202504()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202505()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202506()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202507()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202508()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202509()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202510()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202511()%></td>
                                                <td style="text-align: center;"><%=dto.getCom202512()%></td>
                                            </tr>

                                            <%
                                                } // end for
                                            %>
                                            </tbody>
                                        </table>
                                    </div>

                                    <div class="tab-pane fade" id="custom-content-above-messages" role="tabpanel" aria-labelledby="custom-content-above-messages-tab">
                                        Morbi turpis dolor, vulputate vitae felis non, tincidunt congue mauris. Phasellus volutpat augue id mi placerat mollis. Vivamus faucibus eu massa eget condimentum. Fusce nec hendrerit sem, ac tristique nulla. Integer vestibulum orci odio. Cras nec augue ipsum. Suspendisse ut velit condimentum, mattis urna a, malesuada nunc. Curabitur eleifend facilisis velit finibus tristique. Nam vulputate, eros non luctus efficitur, ipsum odio volutpat massa, sit amet sollicitudin est libero sed ipsum. Nulla lacinia, ex vitae gravida fermentum, lectus ipsum gravida arcu, id fermentum metus arcu vel metus. Curabitur eget sem eu risus tincidunt eleifend ac ornare magna.
                                    </div>

                                </div>



                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->

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



<%--<script src="resources/dist/js/jquery-3.7.1.min.js"></script>--%>

<%--<script src="/adminLte/js/jquery-3.7.1.min.js"></script>--%>

<script src="resources/dist/js/jquery-3.7.1.min.js"></script>

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
        "ordering": false,
        "paging": false,
        "searching": false,
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    let dtTable02 = $("#infoTable02").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        "ordering": false,
        "paging": false,
        "searching": false,
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable02_wrapper .col-md-6:eq(0)');

    //ready
    $(document).ready(function() {

        console.log('start designDashboard');


    });


</script>


</html>


