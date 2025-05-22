<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->

    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>MLB-검증</title>

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
    <jsp:include page="../layout/basicSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>제품 전 레벨 BOM 추출(Block.No)</h1>
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

                                    <h5>- 입력된 제품번호와 Block.No를 가지고 제품의 하위 <font color="red">전 레벨</font>을 검색하여 해당 Block.No와 연관된 부품들 출력.</h5>
                                    <h5 style="color: red">- 여러개의 제품번호를 입력하여 검색하고자 할 경우 꼭 구분값 ";"로 구분해주어야 됨. <font color="black">(ex. 206397L06;206397L25;299397L17 ) </font> </h5>
                                    <h5>- 속도 및 검색 개수의 한계가 있으므로 몇백 개 이상의 제품번호 입력을 통한 검색은 지양 요청. </h5>
                                    <h5>- EQUAL: 입력한 Block.No와 정확히 <font color="red">일치</font>하는 자재만 검색 </h5>
                                    <h5>- LIKE : 입력한 Block.No를 <font color="red">포함</font>하는 모든 자재 검색 </h5>
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
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                        <!-- bg-primary -->
                                        <tr class="bg-secondary">
                                            <th style="font-weight: bold; text-align: center;">제품번호</th>
                                            <th style="font-weight: bold; text-align: center;">공사/파트 번호</th>
                                            <th style="font-weight: bold; text-align: center;">공사/파트 명</th>
                                            <th style="font-weight: bold; text-align: center;">버전</th>
                                            <th style="font-weight: bold; text-align: center;">BLOCK.NO</th>
                                            <th style="font-weight: bold; text-align: center;">수량</th>
                                            <th style="font-weight: bold; text-align: center;">공사_수량</th>
                                            <th style="font-weight: bold; text-align: center;">주석</th>
                                            <th style="font-weight: bold; text-align: center;">공사_주석</th>
                                            <th style="font-weight: bold; text-align: center;">SPEC</th>
                                            <th style="font-weight: bold; text-align: center;">수정여부</th>
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
        let productNumber = $("#prodVal").val(); // 제품번호
        let blockVal = $("#blockVal").val(); // Block.No
        let searchFlag = $("#con-02").val(); // like, equal


        if(productNumber == null || "" == productNumber) {
            console.log(productNumber);
            alert("제품번호를 입력하세요.");
            return;
        }

        if(blockVal == null || "" == blockVal) {
            console.log(blockVal);
            alert("Block.No를 입력하세요.");
            return;
        }

        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        $.ajax({
            type : "post",
            url : "searchByBlockNoViewJson.jsp",
            data : {
                productNumber : productNumber,
                blockNo : blockVal,
                searchFlag : searchFlag
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

                let dataSize = data.COUNT;
                let result = data.RESULT;
                console.log("result.length - ", result.length);

                if(dataSize != null && dataSize > 0 && dataSize < 10000) {
                    //10000개 이상 검색되는 경우는 보류
                }

                if(dataSize != null && dataSize> 0 && result != null && result.length > 0) {

                    for(let i=0; i < result.length; i++) {

                        let info = result[i];
                        console.log('info --- ' , info[i]);

                        str += "<tr>";
                        str += "<td>" + result[i].PRODUCT + "</td>";
                        str += "<td>" + result[i].PART_NO + "</td>";
                        str += "<td>" + result[i].PART_NAME + "</td>";
                        str += "<td>" + result[i].VERSION + "</td>";

                        str += "<td>" + result[i].BLOCK_NO + "</td>";
                        str += "<td>" + result[i].QTY + "</td>";
                        str += "<td>" + result[i].WORK_QTY + "</td>";
                        str += "<td>" + result[i].CMT + "</td>";
                        str += "<td>" + result[i].WORK_CMT + "</td>";

                        str += "<td>" + result[i].SPEC + "</td>";
                        str += "<td>" + result[i].MOD_CHECK + "</td>";
                        str += "</td>";
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
                        //"scrollX": true, // 가로 스크롤
                        //"buttons": ["csv", "excel", "pdf", "print"]
                        "buttons": ["csv", "excel", "copy"]
                        //"dom": "Bfrtip"
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                } else {
                    alert("검색결과가 없습니다.");
                }
            } // end success;
        });
    }


</script>

</html>
