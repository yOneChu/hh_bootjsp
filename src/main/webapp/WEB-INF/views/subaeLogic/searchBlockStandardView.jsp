<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //Block 기준정보 백업 조회 화면 - searchBlockStandardView.jsp


    String serverUrl = "http://" + request.getServerName() + ":" + request.getServerPort();
    //System.out.println("serverUrl = " + serverUrl);

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
                        <h1>Block No.기준정보 이력관리</h1>
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
                                    <h5 style="color: blue;"> ✅ 사용 예시. </h5>
                                    <h5>1.	2025.06.01 일자로 PLM에 있는 데이터를 백업해놓음 -> 해당 데이터가 이력조회 시, 버전 1에 해당 됨( 해당작업은 최초 1번 수행하고 수행되지 않음) </h5>
                                    <h5>2.	PLM에서 “B259B83” Block 정보가 변경 됨 -> “B259B83” 버전2로 데이터가 쌓임 </h5>
                                    <h5>3.	이력조회 화면에서 “B259B83” 버전1, 버전2 조회하여 데이터 비교하면 됨 </h5>

                                    <h5 style="color: blue;"> 📌 참고사항 </h5>
                                    <h5 style="color: blue;">- PLM에서의 Block No 정보 변경 시, 별도의 DB에 이력관리되어 조회되는 화면 </h5>
                                    <h5>- PLM의 Block 기준정보 변경 시, 월~금 오전 8시에 변경 이력이 쌓이고 관련 담당자들에게 E-Mail 자동 발송 -> <button class="btn btn-success" onclick="viewPDF()">
                                        📄 프로세스 메뉴얼 열람
                                    </button></h5>
                                    <h5 style="color: blue;">- 조회 시, 마지막 버전의 데이터가 PLM에서 조회되는 데이터 임 </h5>
                                </div>
                            </div>


                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Block No.</label>
                                    <input type="search" id="blockNo" class="form-control" placeholder="block No." value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>


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
                                        <th>NO</th>
                                        <th>Block no</th>
                                        <th>Block Name</th>
                                        <th>Version</th>
                                        <th>품목구분</th>
                                        <th>수정자</th>
                                        <th>수정일</th>
                                        <th>제품군</th>
                                        <th>단위</th>
                                        <th>자재유형</th>
                                        <th>활성상태</th>
                                        <th>재질관리</th>
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

        //엔터키 감지
        $(document).keyup(function(event) {
            if(event.which === 13) {
                searchPID();
                return false; // 추가 이벤트 방지위해 false 리턴
            }
        })

    });


    /**
     * Block 기준정보 상세화면
     * @param o
     */
    function blockView(blockNo, blockVer) {
        //console.log("1111")
        //console.log(o);
        //console.log(o.textContent);
        //console.log(o.innerText);


        let urlValue = "/subae/searchBlockStandardInfo?";
        urlValue += "blockNo=" + blockNo + "&version=" + blockVer;
        window.open(urlValue,'_blank','width=1200, height=800, top=50, left=50, scrollbars=yes');

    }

    function viewPDF() {

        let url = "<%=serverUrl%>";
        let pdfLink = url + "/subae/blockManual";  // 로컬 서버 URL
        let windowSize = "width=800,height=600,scrollbars=yes";  // 팝업 창 크기
        window.open(pdfLink, "pdfPopup", windowSize);
    }

    //검색
    function searchPID()
    {
        let blockNo = $("#blockNo").val(); // blockNo

        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        $.ajax({
            type : "post",
            crossDomain : true,
            url : "/subae/searchBlockLogic",
            data : {
                blockNo : blockNo.trim()
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(resultData)
            {
                console.log("resultData - ", resultData);

                let str = "";

                if(resultData != null && resultData.length > 0) {

                    console.log(resultData.length);

                    for(let i=0; i < resultData.length; i++) {
                        let blockDto = resultData[i];

                        console.log(blockDto.blockNo + " > " + blockDto.blockName);

                        //<a href='javascript:void(0);' onclick="viewList('cpMR_17_5', '202512');">

                        let blockNoVal = blockDto.blockNo;
                        let blockVer = blockDto.version;

                        str += "<tr>";
                            str += "<td>" + (i+1) + "</td>";

                            //str += "<td class='blockIn'><a href='#'>" + blockDto.blockNo + "</a></td>";
                            //str += "<td class='blockIn'><a href='javascript:void(0);' onclick='blockView(this)'>" + blockDto.blockNo + "</a></td>";
                            //str += "<td class='blockIn'><a href='javascript:void(0);' onclick='blockView(\"" + blockNoVal + "\")'>" + blockDto.blockNo + "</a></td>";
                            str += "<td class='blockIn'><a href='javascript:void(0);' onclick='blockView(\"" + blockNoVal + "\", \"" + blockVer + "\")'>" + blockDto.blockNo + "</a></td>";



                        str += "<td>" + blockDto.blockName + "</td>";
                            str += "<td>" + blockDto.version + "</td>";
                            str += "<td>" + blockDto.block_opt + "</td>";
                            str += "<td>" + toSafeString(blockDto.modUser) + "</td>";
                            str += "<td>" + blockDto.modDate + "</td>";
                            str += "<td>" + blockDto.gc_product + "</td>";
                            str += "<td>" + blockDto.uom + "</td>";
                            str += "<td>" + blockDto.partType + "</td>";
                            str += "<td>" + toSafeString(blockDto.block_status) + "</td>";
                            str += "<td>" + toSafeString(blockDto.meterial_check) + "</td>";
                        str += "</tr>";
                    } // end for


                    $("#contentTable").append(str);


                    $("#infoTable").DataTable({
                        "responsive": true,
                        "lengthChange": true,
                        "pageLength": 50,     //페이지 당 글 개수 설정
                        "autoWidth": false, // 가로자동
                        "processing": true,
                        //"scrollX" : true, //가로  스크롤
                        "buttons": ["csv", "excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                } else {
                    alert("검색결과가 없습니다.");
                }
            } // end success;
        });
    }


    /**
     * null이거나 undefined이거나 빈 문자열인 경우 공백("")으로 처리하고, 그 외에는 원래 값을 반환
     * @param value
     * @returns {string|*}
     */
    function toSafeString(value) {
        return value == null || value === '' ? '' : value;
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
