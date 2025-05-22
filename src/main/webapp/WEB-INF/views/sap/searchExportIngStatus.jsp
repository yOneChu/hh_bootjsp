<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>[PP]출하예정일</title>

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
    <%--<jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />--%>
    <jsp:include page="../layout/basicSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>[PP]출하예정일(ZPPT027)</h1>
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
                    <div class="card-body" style="zoom:85%;">
                        <div class="row">
                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger">
                                    <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                    <h5>- SHIP_A,B,C,D,E,F -->  각 블럭의 출하일 </h5>
                                    <h5>- SHIP_MIN_A, B, C, D, E, F --> 같은 프로젝트 중 제일 빠른 출하일 </h5>
                                </div>
                            </div>

                            <!-- <div class="input-group input-group-lg"> -->
                            <div class="col-md-6">
                                <!-- <div class="col-6"> -->
                                <div class="form-group">
                                    <label>호기번호</label>
                                    <textarea class="form-control" id="hogiName" rows="5" placeholder="호기번호를 입력하시오. (여러 호기번호로 검색 시 구분값 ; 로 구분해주시기 바랍니다.) "></textarea>
                                </div>
                            </div>


                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>


                    <div class="card-footer">
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
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                        <tr class="bg-secondary">
                                            <th style="font-weight: bold; text-align: center;">호기번호</th>

                                            <th style="font-weight: bold; text-align: center;">SHIP_A</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_B</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_C</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_D</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_E</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_F</th>

                                            <th style="font-weight: bold; text-align: center;">SHIP_MIN_A</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_MIN_B</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_MIN_C</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_MIN_D</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_MIN_E</th>
                                            <th style="font-weight: bold; text-align: center;">SHIP_MIN_F</th>
                                        </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    </tbody>

                                </table>
                            </div> <!-- /.card-body -->
                        </div> <!-- /.card card-primary -->
                    </div>  <!-- /.col -->
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
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {


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
        let hogiName = $("#hogiName").val(); // 제품번호


        if(hogiName == null || "" == hogiName) {
            console.log(hogiName);
            alert("호기번호를 입력하세요.");
            return;
        } else {
            console.log("hogiName == ", hogiName);
            var sArr = hogiName.split(";");
            let sCount = sArr.length;
            //console.log("개수 --- " ,  sCount);
            //console.log("sArr --- " ,  sArr);

            if(sCount > 800) {
                alert("한번에 800건 이상은 검색할 수 없습니다.");
                return;
            }else {
                if(sCount == 1) {
                    //console.log("한개이다" , hogiName);
                    hogiName += ";";
                }
            }

        }

        //console.log("hogiName == ", hogiName);

        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();


        $.ajax({
            type : "post",
            //crossDomain: true,
            url : "/sap/getExportDate",
            data : {
                hogiNumber : hogiName
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                if(data != null ) {

                    let str = "";
                    for(let i=0; i < data.length; i++) {

                        let vData = data[i];
                        str += "<tr>";
                        str += "<td>" + vData.WBS + "</td>";
                        //str += "<td>" + vData.MANDT + "</td>";
                        //str += "<td>" + vData.PJTNAME + "</td>";

                        //console.log('vData--', vData);
                        //shipA = shipA.replace(/(\d{4}(\d{2})(\d))/, '$1-$2-$3');

                        let shipA = vData.SHIP_A.substr(0,4) + "-" + vData.SHIP_A.substr(4,2) + "-" + vData.SHIP_A.substr(6,2);
                        let shipB = vData.SHIP_B.substr(0,4) + "-" + vData.SHIP_B.substr(4,2) + "-" + vData.SHIP_B.substr(6,2);
                        let shipC = vData.SHIP_C.substr(0,4) + "-" + vData.SHIP_C.substr(4,2) + "-" + vData.SHIP_C.substr(6,2);
                        let shipD = vData.SHIP_D.substr(0,4) + "-" + vData.SHIP_D.substr(4,2) + "-" + vData.SHIP_D.substr(6,2);
                        let shipE = vData.SHIP_E.substr(0,4) + "-" + vData.SHIP_E.substr(4,2) + "-" + vData.SHIP_E.substr(6,2);
                        let shipF = vData.SHIP_F.substr(0,4) + "-" + vData.SHIP_F.substr(4,2) + "-" + vData.SHIP_F.substr(6,2);

                        let ship_min_A = vData.SHIP_MIN_A.substr(0,4) + "-" + vData.SHIP_MIN_A.substr(4,2) + "-" + vData.SHIP_MIN_A.substr(6,2);
                        let ship_min_B = vData.SHIP_MIN_B.substr(0,4) + "-" + vData.SHIP_MIN_B.substr(4,2) + "-" + vData.SHIP_MIN_B.substr(6,2);
                        let ship_min_C = vData.SHIP_MIN_C.substr(0,4) + "-" + vData.SHIP_MIN_C.substr(4,2) + "-" + vData.SHIP_MIN_C.substr(6,2);
                        let ship_min_D = vData.SHIP_MIN_D.substr(0,4) + "-" + vData.SHIP_MIN_D.substr(4,2) + "-" + vData.SHIP_MIN_D.substr(6,2);
                        let ship_min_E = vData.SHIP_MIN_E.substr(0,4) + "-" + vData.SHIP_MIN_E.substr(4,2) + "-" + vData.SHIP_MIN_E.substr(6,2);
                        let ship_min_F = vData.SHIP_MIN_F.substr(0,4) + "-" + vData.SHIP_MIN_F.substr(4,2) + "-" + vData.SHIP_MIN_F.substr(6,2);



                        str += "<td style='text-align: center;'>" + shipA + "</td>";
                        str += "<td style='text-align: center;'>" + shipB + "</td>";
                        str += "<td style='text-align: center;'>" + shipC + "</td>";
                        str += "<td style='text-align: center;'>" + shipD + "</td>";
                        str += "<td style='text-align: center;'>" + shipE + "</td>";
                        str += "<td style='text-align: center;'>" + shipF + "</td>";

                        str += "<td style='text-align: center;'>" + ship_min_A + "</td>";
                        str += "<td style='text-align: center;'>" + ship_min_B + "</td>";
                        str += "<td style='text-align: center;'>" + ship_min_C + "</td>";
                        str += "<td style='text-align: center;'>" + ship_min_D + "</td>";
                        str += "<td style='text-align: center;'>" + ship_min_E + "</td>";
                        str += "<td style='text-align: center;'>" + ship_min_F + "</td>";
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
                        "columnDefs" : [
                            {target: 6, width: 100}, //주석
                            {target: 7, width: 100}
                        ],
                        "buttons": ["csv", "excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                } // end if

            } // end success
        });
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
