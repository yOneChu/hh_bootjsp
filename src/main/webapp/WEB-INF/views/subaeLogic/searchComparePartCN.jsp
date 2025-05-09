<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.kyhslam.service.PartUtilService" %>

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



    <title>한국v중국_자재비교</title>

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


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>부품 비교(한국-중국)</h1>
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
                                    <h5>- 한국PLM vs. 중국PLM - 실시간 자재 데이터 정보 비교.</h5>
                                    <h5>- 여러 자재 조회 시 ","콤마로 구분하여 검색 가능 (ex. C101P013252,C101P013253) </h5>
                                    <!-- <p class="text-info"> " 제품번호 입력 시 최신버전의 BOM 1레벨 추출." </p> -->
                                    <!-- <p class="text-danger"> " 제품번호 입력 시 최신버전의 BOM 1레벨 추출." </p> -->
                                    <!-- <p class="text-primary"> " 제품번호 입력 시 최신버전의 BOM 1레벨 추출."&nbsp; </p> -->
                                    <!-- <p> > 제품번호 입력 시 최신버전의 BOM 1레벨 추출." </p> 줄바꿈은 <br>로 해라. -->

                       <%--             - 조회되지 않을 경우 "비공개 연결 해제"해야되기 때문에 아래의 방법 수행해야 됨.<br>
                                    - 방법 : &nbsp;&nbsp; <a href="https://10.225.80.35:8070/part/getPartInfo?partNo=C121P012084">
                                    <font color="blue">https://10.225.80.35:8070/part/getPartInfo?partNo=C121P012084</font></a> 해당 URL 클릭 -> 고급 -> "10.255.80.35(안전하지않음) 계속하기" 클릭.--%>

                                    <!-- - <font color="red"> 현재는 1건 씩만 검색이 되며 멀티검색은 현재 개발 중. </font> -->
                                </div>
                            </div>

                            <!-- <div class="input-group input-group-lg"> -->
                            <div class="col-md-6">
                                <!-- <div class="col-6"> -->
                                <div class="form-group">
                                    <label>부품번호</label>
                                    <!-- <input type="search" id="pidVal" class="form-control" placeholder="제품번호 입력하시오." value=""> -->

                                    <textarea class="form-control" id="partNo" rows="5" placeholder="부품 NO 입력하시오. "></textarea>

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
                                    <tr >
                                        <th style="font-weight: bold; text-align: center;" colspan="9" class="bg-secondary">한국 PLM</th>
                                        <th style="font-weight: bold; text-align: center;" colspan="11" class="bg-secondary">중국 PDM</th>
                                    </tr>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">부품번호</th>
                                        <th style="font-weight: bold; text-align: center;">BLOCK.NO</th>
                                        <th style="font-weight: bold; text-align: center;">BLOCK명</th>
                                        <th style="font-weight: bold; text-align: center;">파트명</th>
                                        <th style="font-weight: bold; text-align: center;">SPEC</th>
                                        <th style="font-weight: bold; text-align: center;">단위</th>
                                        <th style="font-weight: bold; text-align: center;">SIZE</th>
                                        <th style="font-weight: bold; text-align: center;">GL_CODE</th>
                                        <th style="font-weight: bold; text-align: center;">상태</th>

                                        <!-- 중국 -->
                                        <th style="font-weight: bold; text-align: center;">부품번호(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">영문이름(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">GL_CODE(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">BLOCK.NO(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">단위(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">폐기여부(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">상태(CN)</th>

                                        <th style="font-weight: bold; text-align: center;">버전(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">SIZE(CN)</th>
                                        <th style="font-weight: bold; text-align: center;">최초구분(CN)</th>

                                        <th style="font-weight: bold; text-align: center;">SPEC</th>

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
        let partNo = $("#partNo").val(); // 제품번호


        if(partNo == null || "" == partNo) {
            console.log(partNo);
            alert("부품번호를 입력하세요.");
            return;
        }

        console.log("partNo == ", partNo);

        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        partNo = partNo.replaceAll(/^\s+|\s+$/g, "");
        partNo = partNo.replaceAll(" ", "");
        partNo = partNo.replaceAll("\n", "");

        const partList = partNo.split(",");

        console.log("partList == ", partList);

        let dupCheck = new Array();

        let koDataList = new Array();

        if(partList != null && partList.length > 0) {

            for(let i=0; i < partList.length; i++) {

                let partNumber = partList[i].trim();

                if(dupCheck.indexOf(partNumber) == -1) {
                    dupCheck.push(partNumber);

                } else {
                    //종복된 것 있음
                    console.log(" 중복 ------- ", partNumber);
                    continue;
                }

                let koData = new Object();

                //한국데이터 조회
                $.ajax({
                    type : "post",
                    crossDomain: true,
                    //url : "/plmetc/vault/getPartInfoWithKO",
                    url : "/china/getPartInfoWithKO",
                    data : {
                        partNo : partNumber // partNo.trim()
                    },
                    success : function(data)
                    {
                        koData = data;

                        if(data != null) {
                            koDataList.push(koData);
                        }

                        console.log("한국 data - ", data);
                        console.log("data.length - ", data.length);
                    }
                });



            } // end for
        }


        console.log("koDataList - ", koDataList);


        //중국조회
        $.ajax({
            type : "get",
            //crossDomain: true,
            //url : "https://10.225.80.35:8070/part/getPartInfo",
            url : "https://vault-in.hdel.co.kr:8070/part/getPartALLInfo",
            //url : "/plmetc/vault/getPartInfoWithKO",
            data : {
                partNo : partNo.trim()
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                console.log("cn data - ", data);
                console.log("cn data.length - ", data.length);
                let str = "";

                if(data != null ) {

                    for(let i=0; i < data.length; i++) {

                        let cData = data[i];
                        let koData = new Object();


                        for(let k=0; k < koDataList.length; k++) {

                            let koObj = koDataList[k];
                            let koPartNo = koObj.PARTNO;

                            //console.log(koPartNo + "  !!!!!!!!!!!!!!  " + cData.PARTNO);

                            if(koPartNo == cData.PARTNO) {
                                koData = koObj;
                            }
                        }

                        let koGLCODE = koData.GLCODE;
                        let cnGLCODE = cData.GLCODE;

                        if(koData.PARTNO == null && cData.PARTNO == null) {
                            continue;
                        }

                        str += "<tr>";
                        str += "<td>" + koData.PARTNO + "</td>";

                        //한국
                        str += "<td>" + koData.BLOCKNO + "</td>";
                        str += "<td>" + koData.BLOCKNONAME + "</td>";
                        str += "<td>" + koData.DESCVAL + "</td>"; // PARTNAME
                        str += "<td>" + koData.SPEC + "</td>";
                        str += "<td>" + koData.UOM + "</td>"; // 단위
                        str += "<td>" + koData.PART_SIZE + "</td>"; // 단위
                        //PART_SIZE

                        if(koGLCODE != cnGLCODE) {
                            str += "<td class='bg-maroon color-palette'>" + koData.GLCODE + "</td>";
                        } else {
                            str += "<td>" + koData.GLCODE + "</td>";
                        }
                        str += "<td>" + koData.STATUS + "</td>"; // 상태


                        //중국
                        str += "<td>" + cData.PARTNO + "</td>";
                        str += "<td>" + cData.ENAME + "</td>";

                        if(koGLCODE != cnGLCODE) {
                            str += "<td class='bg-maroon color-palette'>" + cData.GLCODE + "</td>";
                        } else {
                            str += "<td>" + cData.GLCODE + "</td>";
                        }

                        str += "<td>" + cData.BLOCKNO + "</td>";
                        str += "<td>" + cData.UOM + "</td>";

                        //str += "<td>" + cData.DISAWAY + "</td>";
                        if(cData.DISAWAY == "Y") {
                            str += "<td class='bg-maroon color-palette'>" + cData.DISAWAY + "</td>";
                        } else {
                            str += "<td>" + cData.DISAWAY + "</td>";
                        }


                        str += "<td>" + cData.STATUS + "</td>"; // 상태

                        str += "<td>" + cData.PART_VERSION + "</td>";
                        str += "<td>" + cData.PART_SIZE + "</td>";
                        str += "<td>" + cData.DIV + "</td>";
                        str += "<td>" + cData.SPEC + "</td>";
                        str += "</td>";
                        str += "</tr>";

                    }

                    //<td bgcolor="#e6f2ff">DD</td>

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


                }
            }
        });
    }

</script>

</html>
