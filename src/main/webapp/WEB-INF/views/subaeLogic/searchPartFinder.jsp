<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

//searchPartFinder.jsp
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>자재 Finder</title>

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
    <%--<jsp:include page="../layout/basicSideBar.jsp" flush="true" />--%>


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>자재 Finder</h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <%--<li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>--%>

                            <li class="breadcrumb-item">
                                <div class="custom-control custom-switch custom-switch-off-danger custom-switch-on-success">
                                    <input type="checkbox" class="custom-control-input" id="darkModeToggle">
                                    <label class="custom-control-label" for="darkModeToggle">🌓 다크모드</label>
                                </div>
                            </li>
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
                    <div class="card-body" style="zoom:85%;">
                        <div class="row">
                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger">
                                    <%--<h4><i class="fas fa-bullhorn"></i> 도움말</h4>--%>
                                    <%--blue--%>
                                    <h4>🔊 도움말</h4>
                                    <h5 style="color: #60A5FA;">- 자재번호를 입력하면, PLM에서 그 자재가 실제 사용되고 있는 최신 제품을 조회하는 기능  </h5>
                                    <h5>- 사용방법: 26300551G0*  -> Like 검색 ('26300551G0' 문자 포함한 자재를 사용중인 제품 검색) </h5>
                                    <h5>- '*' 없으면 EQUAL 검색 </h5>
                                </div>
                            </div>


                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>년도</label>
                                    <select id="year" class="form-control select" style="width: 100%;">
                                        <option selected="selected">2025</option>
                                        <option>2024</option>
                                        <option>2023</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>Part No.</label>
                                    <input type="search" id="partNo" class="form-control" placeholder="Part No." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>BlockNo</label>
                                    <input type="search" id="blockNo" class="form-control" placeholder="Block No." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>CMT</label>
                                    <input type="search" id="cmt" class="form-control" placeholder="cmt..." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>제품 상태</label>
                                    <select id="status" class="form-control select" style="width: 100%;">
                                        <option value="" selected="selected">전체</option>
                                        <option value="RLS">릴리즈</option>
                                    </select>

                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                <%--            <div class="col-md-2">
                                <div class="form-group">
                                    <label>-조건</label>
                                    <select id="con-01" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>EQUAL</option>
                                    </select>
                                </div>
                            </div>--%>

                   <%--         <div class="col-md-4">
                                <div class="form-group">
                                    <label>PID-01</label>
                                    <input type="search" id="pidVal" class="form-control" placeholder="PID-01" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>--%>

                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>


                    <div class="card-footer">
                        <button class="btn btn-primary float-right" style="margin-right: 5px;" onclick="searchPID()">검색</button>
                    </div>

                </div>


                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

<%--
                            <div class="card-header">
                                <h3 class="card-title">검색 결과</h3>
                            </div>
--%>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style=" font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <!-- bg-primary -->
                                        <tr class="bg-secondary">
                                            <th>제품번호</th>
                                            <th>제품버전</th>
                                            <th>제품상태</th>
                                            <th>제품등록일</th>
                                            <th>기종</th>
                                            <th>품번</th>
                                            <th>품명</th>

                                            <th>QTY</th>
                                            <th>BlockNo.</th>
                                            <th>품목</th>
                                            <th>GL_CODE</th>
                                            <th>버전</th>
                                            <th>cmt</th>

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

<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 50,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

        $("#dashboard").removeClass("menu-open");

        //엔터키 감지
        $(document).keyup(function(event) {
            if(event.which === 13) {
                searchPID();
                return false; // 추가 이벤트 방지위해 false 리턴
            }
        })

    });


    //검색
    function searchPID()
    {
        let year = $("#year").val(); //
        let partNo = $("#partNo").val(); // L
        let blockNo = $("#blockNo").val();
        let cmt = $("#cmt").val();
        let status = $("#status").val();

        console.log(year);
        console.log(partNo);

        // 입력값 트림 처리
        partNo = partNo ? partNo.trim() : "";
        blockNo = blockNo ? blockNo.trim() : "";

        // partNo, blockNo 둘 다 비어있으면 중단, 둘 중 하나라도 있으면 진행
        if ((partNo === "" || partNo == null) && (blockNo === "" || blockNo == null)) {
            alert("partNo 또는 blockNo 중 하나는 입력하세요.");
            return;
        }


        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        showLoading(); // 로딩바 표시
        $.ajax({
            type : "post",
            //url : "searchPID.jsp",
            crossDomain : true,
            url : "/subae/searchMissPartofProduct",
            data : {
                partNo : partNo,
                year : year,
                blockNo: blockNo,
                cmt : cmt,
                status : status
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                console.log("data - ", data);

                let str = "";

                if(data != null && data.length > 0) {

                    for(let i=0; i < data.length; i++) {
                        str += "<tr>";

                        str += "<td>" + data[i].productNo + "</td>";
                        str += "<td>" + data[i].productVersion + "</td>";
                        str += "<td>" + data[i].productStatus + "</td>";
                        str += "<td>" + data[i].productCreDate + "</td>";
                        str += "<td>" + data[i].gisong + "</td>";
                        str += "<td>" + data[i].partNo + "</td>";
                        str += "<td>" + data[i].partName + "</td>";

                        str += "<td>" + data[i].qty + "</td>";
                        str += "<td>" + data[i].blockNo + "</td>";
                        str += "<td>" + data[i].blockopt + "</td>";
                        str += "<td>" + data[i].glCode + "</td>";
                        str += "<td>" + data[i].version + "</td>";

                        let cmtVal = data[i].cmt;
                        cmtVal = cmtVal.replace(/-/g, '<br>-');

                        str += "<td>" + cmtVal + "</td>";
                        str += "</tr>";
                    } // end for


                    $("#contentTable").append(str);


                    $("#infoTable").DataTable({
                        "responsive": true,
                        "lengthChange": true,
                        "pageLength": 50,     //페이지 당 글 개수 설정
                        "autoWidth": false, // 가로자동
                        "processing": true,
                        "destroy": true, // 테이블 재생성
                        //"scrollX" : true, //가로  스크롤
                        "destroy": true, // 테이블 재생성
                        //"scrollX": true, // 가로 스크롤
                        //"buttons": ["csv", "excel", "pdf", "print"]
                        //"buttons": ["csv", "excel"]
                        "dom": "Bfrtip",
                        "buttons": [
                            {
                                extend: "csv",
                                charset: "UTF-16LE",
                                text: "CSV",
                                filename: 'csv_Result'
                            },
                            {
                                extend: "excel",
                                charset: "UTF-8",
                                text: "EXCEL",
                                filename: 'excel_Result',
                            },
                            {
                                extend: "copy"
                            }
                        ]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                } else {
                    alert("검색결과가 없습니다.");
                }
            } // end success;
        });
        hideLoading(); // 성공 시 로딩바 제거
    }

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
