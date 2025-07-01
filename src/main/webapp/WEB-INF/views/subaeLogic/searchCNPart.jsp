<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />



    <title>법인자재 조회(PLM)</title>
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
    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />



    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>법인자재 조회(PLM)</h1>
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
                    <div class="card-body" style="zoom:90%;">
                        <div class="row">
                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger">
                                   <%-- <h4><i class="fas fa-bullhorn"></i> 📢 도움말</h4>--%>
                                    <h4> 📢 도움말</h4>
                                       <h5>- PLM에 등록된 <font color="red">"자재코드Ownership이 중국법인"</font>인 자재들에 대해서만 조회하는 화면.</h5>
                                    <h5>- 모든 검색 조건은 LIKE 기준으로 조회. </h5>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>Part No.</label>
                                    <input type="search" id="partNo" class="form-control" placeholder="Part No.." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>활성</label>
                                    <select id="status" class="form-control select" style="width: 100%;">
                                        <option>ALL</option>
                                        <option selected="selected">활성</option>
                                        <option>비활성</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>SPEC</label>
                                    <input type="search" id="spec" class="form-control" placeholder="spec.." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>Block No.</label>
                                    <input type="search" id="blockNo" class="form-control" placeholder="blockNo.." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>


                    <div class="card-footer">
                        <!--  Visit <a href="https://select2.github.io/">Select2 documentation</a> for more examples and information about
                         the plugin. -->

                        <!-- <button class="btn btn-primary float-right" onclick="searchPopup()">검색(팝업)</button> -->
                        <button class="btn btn-primary float-right" style="margin-right: 5px;" onclick="search()">검색</button>



                    </div>

                </div>


                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">검색 결과</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:90%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                        <tr class="bg-secondary">
                                            <th style="font-weight: bold; text-align: center;">부품번호</th>
                                            <th style="font-weight: bold; text-align: center;">BLOCK.NO</th>
                                            <th style="font-weight: bold; text-align: center;">BLOCK명</th>
                                            <th style="font-weight: bold; text-align: center;">파트명</th>
                                            <th style="font-weight: bold; text-align: center;">SPEC</th>
                                            <th style="font-weight: bold; text-align: center;">최초구분</th>
                                            <th style="font-weight: bold; text-align: center;">상태</th>
                                            <th style="font-weight: bold; text-align: center;">단위</th>
                                            <th style="font-weight: bold; text-align: center;">SIZE</th>
                                            <th style="font-weight: bold; text-align: center;">GL_CODE</th>
                                            <th style="font-weight: bold; text-align: center;">활성상태</th>
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
        <strong>Copyright &copy; 2025 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

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


<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": [ "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');



    //ready
    $(document).ready(function() {

        $("#dashboard").removeClass("menu-open");

        //엔터키 감지
        $(document).keyup(function(event) {
            if(event.which === 13) {
                search();
                return false; // 추가 이벤트 방지위해 false 리턴
            }
        })

    });

    //검색
    function search()
    {
        let partNo = $("#partNo").val();
        let spec = $("#spec").val();
        let blockNo = $("#blockNo").val();
        let status = $("#status").val();

        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();


        $.ajax({
            type : "post",
            url : "/subae/searchCNPart",
            data : {
                partNo : partNo.trim(),
                spec : spec.trim(),
                blockNo : blockNo.trim(),
                status: status.trim()
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                //console.log("data - ", data);
                let str = "";

                if(data != null ) {

                    for(let i=0; i < data.length; i++) {
                        str += "<tr>";
                            str += "<td>" + data[i].partNo + "</td>";
                            str += "<td>" + data[i].blockNo + "</td>";
                            str += "<td>" + data[i].blockName + "</td>";
                            str += "<td>" + data[i].partName + "</td>";
                            str += "<td>" + data[i].spec + "</td>";
                            str += "<td>" + data[i].div + "</td>";
                            str += "<td>" + data[i].status + "</td>";
                            str += "<td>" + data[i].uom + "</td>";
                            str += "<td>" + data[i].partSize + "</td>";
                            str += "<td>" + data[i].glCode + "</td>";
                            str += "<td>" + data[i].active + "</td>";
                        str += "</tr>";
                    }

                    $("#contentTable").append(str);

                    $("#infoTable").DataTable({
                        "responsive": true,
                        "lengthChange": true,
                        "pageLength": 50,     //페이지 당 글 개수 설정
                        "autoWidth": false, // 가로자동
                        "processing": true,
                        "destroy": true, // 테이블 재생성
                        "buttons": ["csv", "excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


                }
            }
        });
    }

</script>

</html>
