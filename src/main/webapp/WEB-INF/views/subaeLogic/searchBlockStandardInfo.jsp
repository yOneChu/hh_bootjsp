<%@ page import="com.kyhslam.service.BlockHistoryService" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.kyhslam.dto.BlockHistoryDTO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //Block 기준정보 상세 화면 - searchBlockStandardInfo.jsp
    String blockNo = request.getParameter("blockNo");
    String blockName = request.getParameter("blockName");
    String version = request.getParameter("version");

    blockNo = "A115A";

    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // 원하는 Bean 가져오기
    BlockHistoryService blockHistoryService = (BlockHistoryService) context.getBean("BlockHistoryService");

    ArrayList<BlockHistoryDTO> list = blockHistoryService.findByBlockNo(blockNo);
    BlockHistoryDTO dto = list.get(0);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>수배로직</title>

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


    <!-- Content Wrapper. Contains page content -->
    <%--<div class="content-wrapper">--%>
    <div class="content">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-12">
                        <h1>Block 기준정보 상세화면 - <%=dto.getBlockNo() %> ( <%=dto.getBlockName()%> )</h1>
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
                                <h3 class="card-title">dto.getPick</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>

                                        <tr class="bg-secondary">
                                            <th>NO</th>
                                            <th>PICK_NAME</th>
                                            <th></th>
                                            <th>PICK</th>
                                            <th></th>
                                            <th>수량</th>
                                            <th></th>
                                            <th>주석</th>
                                            <th></th>
                                            <th>도장</th>
                                            <th></th>
                                        </tr>

                                    </thead>

                                    <tbody id="contentTable">

                                    <%

                                        if (dto != null) {
                                            List<String> pickList = Arrays.stream(dto.getPick().split("-")).collect(Collectors.toList());
                                            List<String> pickNameList = Arrays.stream(dto.getPickName().split("-")).collect(Collectors.toList());
                                            List<String> qtyList = Arrays.stream(dto.getQty().split("-")).collect(Collectors.toList());
                                            List<String> cmtList = Arrays.stream(dto.getCmt().split("-")).collect(Collectors.toList());
                                            List<String> colorList = Arrays.stream(dto.getColor().split("-")).collect(Collectors.toList());
                                            for (int i = 0; i < 33; i++) {
                                                String pick = pickList.get(i) == "X" ? "" : pickList.get(i);
                                                String pickName = pickNameList.get(i) == "X" ? "" : pickNameList.get(i);
                                                String qty = qtyList.get(i) == "X" ? "" : qtyList.get(i);
                                                String cmt = cmtList.get(i) == "X" ? "" : cmtList.get(i);
                                                String color = colorList.get(i) == "X" ? "" : colorList.get(i);

                                    %>
                                        <tr>
                                            <td> <%=(i+1) %></td>

                                            <td>PICK <%=(i+1) %></td>
                                            <td><%=pick%></td>

                                            <td>PICKNAME <%=(i+1) %></td>
                                            <td><%=pickName%></td>

                                            <td>QTY <%=(i+1) %></td>
                                            <td><%=qty%></td>

                                            <td>CMT <%=(i+1) %></td>
                                            <td><%=cmt%></td>

                                            <td>COLOR <%=(i+1) %></td>
                                            <td><%=color%></td>
                                        </tr>
                                    <%
                                            }
                                            System.out.println("pickList = " + pickList);
                                            System.out.println("pickList = " + pickList.size());
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
        <strong>Copyright &copy; 2025 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
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


<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 50,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        "buttons": ["excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

        //엔터키 감지
        $(document).keyup(function(event) {
            if(event.which === 13) {
                searchPID();
                return false; // 추가 이벤트 방지위해 false 리턴
            }
        })

    });

    function isStringAndNotEmptyOrWhitespace(value) {
        // 1. 문자열인지 확인
        if (typeof value === 'string') {
            // 2. 공백만 있는지 확인 (trim()으로 공백 제거 후 빈 문자열인지 체크)
            if (value.trim() === '') {
                return false; // 공백 문자열
            }
            return true; // 유효한 문자열
        }
        return false; // 문자열이 아님
    }

</script>

</html>
