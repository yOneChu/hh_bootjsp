<%@page import="java.util.ArrayList"%>
<%@ page import="com.kyhslam.service.JQPRService" %>
<%@ page import="com.kyhslam.dto.JqplDTO" %>
<%@ page import="java.util.Collections" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>



<%

    JQPRService jqprService = new JQPRService();


    ArrayList<JqplDTO> result = jqprService.getJqplDashbard();


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
    <%--<link rel="stylesheet" href="/resources/dist/googleFont.css">--%>

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


<body class="hold-transition sidebar-mini dark-mode text-sm" style="zoom:100%;">

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
                        <h1>2025년 JQPR 현황 (SAP-ZQMAL4020D) </h1>
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
                         <!--<div class="col-lg-7"> -->

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">검색 결과</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">

                                <div class="col-md-12">
                                    <div class="callout callout-info">
                                        <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                        <%--<ul>
                                            <li>2024년에 PLM에 등록된 영업사양 현황 </li>
                                            <li>기종 별 기준으로 1~12월에 '릴리즈'된 데이터 기준으로 집계</li>
                                        </ul>--%>
                                        <h5>- SAP (ZQMAL4020D) 데이터 기반 </h5>
                                        <h5>- 반려 건 제외 </h5>
                                    </div>
                                </div>

                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: Segoe UI; font-size:15px;">
                                    <thead>

                                    <!-- bg-primary -->
                                    <tr class="bg-secondary">
                                        <%--<th style="font-weight: bold; text-align: center;" rowspan="3">NO</th>--%>
                                        <th style="font-weight: bold; text-align: center;">JQPR.NO</th>
                                        <th style="font-weight: bold; text-align: center;">상태</th>
                                        <th style="font-weight: bold; text-align: center;">호기</th>
                                        <th style="font-weight: bold; text-align: center;">접수일</th>
                                        <th style="font-weight: bold; text-align: center;">작성자</th>
                                        <th style="font-weight: bold; text-align: center;">프로젝트명</th>
                                        <th style="font-weight: bold; text-align: center;">고장현상</th>
                                        <th style="font-weight: bold; text-align: center;">고장원인</th>
                                        <th style="font-weight: bold; text-align: center;">문제자재명</th>
                                        <th style="font-weight: bold; text-align: center;">기계설계</th>
                                        <th style="font-weight: bold; text-align: center;">전기설계</th>

                                        <th style="font-weight: bold; text-align: center;">자재비</th>
                                        <th style="font-weight: bold; text-align: center;">노무비</th>
                                        <th style="font-weight: bold; text-align: center;">실패비용</th>

                                        <th style="font-weight: bold; text-align: center;">내부부서1</th>
                                        <th style="font-weight: bold; text-align: center;">내부비용1</th>
                                        <th style="font-weight: bold; text-align: center;">내부부서2</th>
                                        <th style="font-weight: bold; text-align: center;">내부비용2</th>
                                        <%--<th style="font-weight: bold; text-align: center;">내부부서3</th>
                                        <th style="font-weight: bold; text-align: center;">내부비용3</th>--%>

                                    </tr>


                                    </thead>

                                    <tbody id="contentTable">
                                    <%

                                        for (int i = 0; i < result.size(); i++) {
                                            JqplDTO d = result.get(i);
                                            String jqprNo = d.getJqprNo();

                                            String status = d.getStatus();

                                            if ("반려".equals(status)) {
                                                continue;
                                            }

                                            String receiptDate = d.getReceptDate();
                                            String eUser = d.getEUser();
                                            String mUser = d.getMUser();

                                            String projectName = d.getProjectName();
                                            String problemPart = d.getProblemPart();
                                            String hogi = d.getHogi();
                                            String creator = d.getCreator();
                                            String createDate = d.getCreDate();
                                            String jqprType = d.getJqprType();

                                            String problemStatus = d.getProblemStatus();
                                            String problemCause = d.getProblemCause();
                                            String typeCode = d.getTypeCode();
                                            String itemType = d.getItemType();

                                            String jajeCost = d.getJajeCost();

                                            if (jajeCost != null && !"".equals(jajeCost)) {
                                                jajeCost = String.format("%,d", Integer.parseInt(jajeCost));
                                            }

                                            String nomoCost = d.getNomoCost();
                                            if (nomoCost != null && !"".equals(nomoCost)) {
                                                nomoCost = String.format("%,d", Integer.parseInt(nomoCost));
                                            }

                                            String failCost = d.getFailCost();
                                            if (failCost != null && !"".equals(failCost)) {
                                                failCost = String.format("%,d", Integer.parseInt(failCost));
                                            }

                                            String team01 = d.getTeam01();
                                            String team01Cost = d.getTeam01Cost();
                                            if (team01Cost != null && !"".equals(team01Cost)) {
                                                team01Cost = String.format("%,d", Integer.parseInt(team01Cost));
                                            }

                                            String team02 = d.getTeam02();
                                            String team02Cost = d.getTeam02Cost();
                                            if (team02Cost != null && !"".equals(team02Cost)) {
                                                team02Cost = String.format("%,d", Integer.parseInt(team02Cost));
                                            }
                                            //String team03 = d.getTeam03();
                                            //String team03Cost = d.getTeam03Cost();

                                            String fCompany = d.getFCompany();
                                            String fCompanyCost = d.getFCompanyCost();
                                            if (fCompanyCost != null && !"".equals(fCompanyCost)) {
                                                fCompanyCost = String.format("%,d", Integer.parseInt(fCompanyCost));
                                            }

                                            String etcTeam = d.getEtcTeam();
                                            String etcTeamCost = d.getEtcTeamCost();
                                            if (etcTeamCost != null && !"".equals(etcTeamCost)) {
                                                etcTeamCost = String.format("%,d", Integer.parseInt(etcTeamCost));
                                            }

                                            String completeStatus = d.getCompleteStatus();

                                    %>

                                    <tr>
                                        <td style="text-align: center; "><%=jqprNo%></td>
                                        <td style="text-align: center; "><%=status%></td>
                                        <td style="text-align: center; "><%=hogi%></td>

                                        <td style="text-align: center;"><%=receiptDate %></td>
                                        <td style="text-align: center;"><%=creator%></td>
                                        <td><%=projectName%></td>
                                        <td style="text-align: center;"><%=problemStatus%></td>
                                        <td><%=problemCause%></td>

                                        <td><%=problemPart%></td>
                                        <td style="text-align: center;"><%=mUser%></td>
                                        <td style="text-align: center;"><%=eUser%></td>

                                        <td style="text-align: center;"><%=jajeCost%></td>
                                        <td style="text-align: center;"><%=nomoCost%></td>
                                        <td style="text-align: center;"><%=failCost%></td>

                                        <td style="text-align: center;"><%=team01%></td>
                                        <td><%=team01Cost%></td>
                                        <td style="text-align: center;"><%=team02%></td>
                                        <td><%=team02Cost%></td>
                                        <%--<td><%=team03%></td>
                                        <td><%=team03Cost%></td>--%>

                                       <%-- <td><%=fCompany%></td>
                                        <td><%=fCompanyCost%></td>

                                        <td><%=etcTeam%></td>
                                        <td><%=etcTeamCost%></td>
                                        <td><%=completeStatus%></td>
                                        --%>
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
        "pageLength": 30,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        "paging": false,
        "searching": false,
        "order" : [[ 0, "desc" ]],
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

    });



</script>

</html>
