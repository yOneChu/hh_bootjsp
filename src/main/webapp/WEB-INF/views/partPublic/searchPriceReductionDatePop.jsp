<%@page import="java.time.LocalDate"%>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.kyhslam.service.PlanCService" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.kyhslam.domain.ProductPlanC" %>
<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%


    String contextPath = request.getContextPath();
    System.out.println("--- searchPriceReductionDatePop.jsp ---");


    String brand = (String)request.getParameter("brand"); // 202411/2024/2025/total
    String partNo = (String)request.getParameter("partNo"); // 월date or total
    String month = (String)request.getParameter("month");

    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    LocalDate now = LocalDate.now();
    String todayVal = now.toString();

    PlanCService planCService = (PlanCService) context.getBean("PlanCService");

    List<ProductPlanC> data = planCService.findProductByBatchDate_v3(todayVal, partNo, brand, month);


    System.out.println("brand == " + brand);
    System.out.println("partNo == " + partNo);
    System.out.println("month == " + month);

    System.out.println("data.size() = " + data.size());

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();



%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->

    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->

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


<body>

<div class="wrapper">


    <!-- Content Wrapper. Contains page content -->
    <div class="content">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><%= partNo  + ", " %> <font color="red"> (<%=todayVal %>, 06:00기준)</font></h1>



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

                            <!-- <div class="card-header">
                                <h3 class="card-title">Dashboard</h3>
                            </div>
 -->
                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:90%;">
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">ERP전송일자</th>
                                        <th style="font-weight: bold; text-align: center;">INDEX</th>
                                        <th style="font-weight: bold; text-align: center;">호기</th>
                                        <th style="font-weight: bold; text-align: center;">브랜드</th>
                                        <th style="font-weight: bold; text-align: center;">자재번호</th>
                                        <th style="font-weight: bold; text-align: center;">단가</th>
                                        <th style="font-weight: bold; text-align: center;">자재명</th>
                                        <th style="font-weight: bold; text-align: center;">생산거점</th>
                                        <th style="font-weight: bold; text-align: center;">기종</th>
                                        <th style="font-weight: bold; text-align: center;">공사정보</th>
                                        <th style="font-weight: bold; text-align: center;">수량</th>
                                        <th style="font-weight: bold; text-align: center;">도면번호</th>
                                        <th style="font-weight: bold; text-align: center;">출하예정일</th>
                                        <th style="font-weight: bold; text-align: center;">SPEC</th>
                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">
                                    <%
                                        if (data != null & data.size() > 0) {


                                        for (int i = 0; i < data.size(); i++) {
                                            ProductPlanC dto = data.get(i);
                                    %>

                                        <tr>
                                            <td><%=dto.getErpSendDate()%></td>
                                            <td><%=dto.getIndexNo()%></td>
                                            <td><%=dto.getProductNo()%></td>
                                            <td><%=dto.getBrand()%></td>
                                            <td><%=dto.getPartNo()%></td>
                                            <td><%=dto.getToCost()%></td>
                                            <td><%=dto.getPartName()%></td>
                                            <td><%=dto.getAspscd()%></td>
                                            <td><%=dto.getGisong()%></td>
                                            <td><%=dto.getGongSa()%></td>
                                            <td><%=dto.getQty()%></td>
                                            <td><%=dto.getDwgNo()%></td>
                                            <td><%=dto.getExportDate()%></td>
                                            <td><%=dto.getSpec()%></td>
                                        </tr>


                                    <%

                                            }
                                        }
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


<!-- Highhart -->
<script src="/resources/dist/js/highcharts.js"></script>
<script src="/resources/dist/js/exporting.js"></script>
<script src="/resources/dist/js/export-data.js"></script>
<script src="/resources/dist/js/accessibility.js"></script>




<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 50,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        //"processing": true,
        "searching" : true,
        "paging" : true, // 페이징표시 삭제
        "destroy": true, // 테이블 재생성
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

        console.log('start');

    }); // end document ready





</script>

</html>
