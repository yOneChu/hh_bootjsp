<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%


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
    <%--<jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />--%>
    <jsp:include page="../layout/basicSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>PID 상세조회</h1>
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
                                    <h5 style="color: blue;">- 10,000건 이상의 경우 관리자에게 요청하시기 바랍니다 </h5>
                                    <h5>- 현재 Excel 다운로드 시 숫자인식 관련 문제가 있어 Copy로 복사 후, EXCEL에 붙여넣기 하시기 바랍니다. </h5>
                                    <h5>- 조건1에 REMARKS로 검색 시, 조건2의 PID는 검색할 수 없습니다. </h5>
                                    <h5>- PID02 조건이 공백이라면 해당 조건은 포함되지 않고 조회된다. </h5>
                                    <h5>- PID-03,04,05는 앞 부분이 입력되어야 뒷부분을 입력가능합니다. (ex. PID04만 입력불가(03누락), PID03,05만 입력불가(04누락)). </h5>
                                    <h5>- PID-GROUP을 EQUAL,LIKE 입력시  PID03,04,05는 OR 조건으로 동작하고, NOT EQUAL, NOT LIKE 입력시 PID03,04,05는 AND 조건으로 동작합니다. </h5>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>조건-01</label>
                                    <select id="con-01" class="form-control select" style="width: 100%;">
                                        <option selected="selected">SPEC</option>
                                        <option>CON</option>
                                        <option>KEY</option>
                                        <option>VAL</option>
                                        <option>REMARKS</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>-</label>
                                    <select id="con-02" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>EQUAL</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>PID-01</label>
                                    <input type="search" id="pidVal" class="form-control" placeholder="PID-01" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>


                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>조건-02</label>
                                    <select id="con-03" class="form-control select" style="width: 100%;">
                                        <option selected="selected">SPEC</option>
                                        <option>CON</option>
                                        <option>KEY</option>
                                        <option>VAL</option>
                                        <!-- <option>REMARKS</option> -->
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>-</label>
                                    <select id="con-04" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>NOT LIKE</option>
                                        <option>EQUAL</option>
                                        <option>NOT_EQUAL</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>PID-02</label>
                                    <input type="search" id="pidVal02" class="form-control" placeholder="PID-02" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>


                            <%--PID-GROUP--%>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>PID-GROUP</label>
                                    <select id="con-05" class="form-control select" style="width: 100%;">
                                        <option selected="selected">LIKE</option>
                                        <option>NOT LIKE</option>
                                        <option>EQUAL</option>
                                        <option>NOT EQUAL</option>
                                    </select>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>PID-03</label>
                                    <input type="search" id="pidVal03" class="form-control" placeholder="PID-03" value="">
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>PID-04</label>
                                    <input type="search" id="pidVal04" class="form-control" placeholder="PID-04" value="" readonly>
                                    <div class="input-group-append">
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-2">
                                <div class="form-group">
                                    <label>PID-05</label>
                                    <input type="search" id="pidVal05" class="form-control" placeholder="PID-05" value="" readonly>
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
                                        <th>PID</th>
                                        <th>NO</th>
                                        <th>REMARKS</th>

                                        <th>SPEC1</th> <th>CON1</th>
                                        <th>SPEC2</th> <th>CON2</th>
                                        <th>SPEC3</th> <th>CON3</th>
                                        <th>SPEC4</th> <th>CON4</th>
                                        <th>SPEC5</th> <th>CON5</th>
                                        <th>SPEC6</th> <th>CON6</th>
                                        <th>SPEC7</th> <th>CON7</th>
                                        <th>SPEC8</th> <th>CON8</th>
                                        <th>SPEC9</th> <th>CON9</th>
                                        <th>SPEC10</th> <th>CON10</th>

                                        <th>SPEC11</th> <th>CON11</th>
                                        <th>SPEC12</th> <th>CON12</th>
                                        <th>SPEC13</th> <th>CON13</th>
                                        <th>SPEC14</th> <th>CON14</th>
                                        <th>SPEC15</th> <th>CON15</th>
                                        <th>SPEC16</th> <th>CON16</th>
                                        <th>SPEC17</th> <th>CON17</th>
                                        <th>SPEC18</th> <th>CON18</th>
                                        <th>SPEC19</th> <th>CON19</th>
                                        <th>SPEC20</th> <th>CON20</th>

                                        <th>KEY1</th> <th>VAL1</th>
                                        <th>KEY2</th> <th>VAL2</th>
                                        <th>KEY3</th> <th>VAL3</th>
                                        <th>KEY4</th> <th>VAL4</th>
                                        <th>KEY5</th> <th>VAL5</th>
                                        <th>KEY6</th> <th>VAL6</th>
                                        <th>KEY7</th> <th>VAL7</th>
                                        <th>KEY8</th> <th>VAL8</th>
                                        <th>KEY9</th> <th>VAL9</th>
                                        <th>KEY10</th> <th>VAL10</th>

                                        <th>KEY11</th> <th>VAL11</th>
                                        <th>KEY12</th> <th>VAL12</th>
                                        <th>KEY13</th> <th>VAL13</th>
                                        <th>KEY14</th> <th>VAL14</th>
                                        <th>KEY15</th> <th>VAL15</th>
                                        <th>KEY16</th> <th>VAL16</th>
                                        <th>KEY17</th> <th>VAL17</th>
                                        <th>KEY18</th> <th>VAL18</th>
                                        <th>KEY19</th> <th>VAL19</th>
                                        <th>KEY20</th> <th>VAL20</th>
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


        //pidVal03 입력하면 pidVal04 활성화
        $('#pidVal03').on('input', function () {
            const value = $(this).val();
            if (value.trim() !== '') {
                $('#pidVal04').prop('readonly', false);
            } else {
                $('#pidVal04').prop('readonly', true);
                $('#pidVal04').val('');

                $('#pidVal05').prop('readonly', true);
                $('#pidVal05').val('');
            }
        });

        //pidVal04 입력하면 pidVal05 활성화
        $('#pidVal04').on('input', function () {
            const value = $(this).val();
            if (value.trim() !== '') {
                $('#pidVal05').prop('readonly', false);
            } else {
                $('#pidVal05').prop('readonly', true);
                $('#pidVal05').val('');
            }
        });

    });






    //검색
    function searchPID()
    {
        let con01 = $("#con-01").val(); // SPEC
        let con02 = $("#con-02").val(); // LIKE
        let pidVal = $("#pidVal").val();


        let SPEC03 = $("#con-03").val(); // SPEC
        let CON04 = $("#con-04").val(); // LIKE
        let pidVal02 = $("#pidVal02").val(); // SPEC

        //PID-GROUP
        let CON05 = $("#con-05").val();
        let pidVal03 = $("#pidVal03").val();
        let pidVal04 = $("#pidVal04").val();
        let pidVal05 = $("#pidVal05").val();


        if(pidVal == null || "" == pidVal) {
            console.log(pidVal);
            alert("PID값을 입력하세요.");
            return;
        }


        if(con01 == 'REMARKS' && pidVal02 != '') {
            alert("조건1을 REMARKS로 검색 시, 조건2의 PID는 검색할 수 없습니다.");
            return;
        }


        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        $.ajax({
            type : "post",
            //url : "searchPID.jsp",
            crossDomain : true,
            url : "/pid/searchPIDSpecViewJson",
            data : {
                pid : pidVal,
                FIELD : con01,
                GUBUN : con02,
                SPEC02 : SPEC03,
                GUBUN02 : CON04,
                PID02 : pidVal02,
                CON05 : CON05,
                PID03 : pidVal03,
                PID04 : pidVal04,
                PID05 : pidVal05
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

                if(data[0] != null && data[0].msg != null) {
                    let msg = data[0].msg;

                    console.log(msg);
                    if(msg != null || "" != msg) {
                        alert(msg);
                        return;
                    }
                }


                let str = "";

                if(data != null && data.length > 0) {

                    for(let i=0; i < data.length; i++) {
                        str += "<tr>";

                        str += "<td>" + data[i].PID + "</td>";
                        str += "<td>" + data[i].NO + "</td>";
                        str += "<td>" + data[i].REMARKS + "</td>";

                        str += "<td>" + data[i].SPEC1 + "</td>";  str += "<td>" + data[i].CON1 + "</td>";
                        str += "<td>" + data[i].SPEC2 + "</td>";  str += "<td>" + data[i].CON2 + "</td>";
                        str += "<td>" + data[i].SPEC3 + "</td>";  str += "<td>" + data[i].CON3 + "</td>";
                        str += "<td>" + data[i].SPEC4 + "</td>";  str += "<td>" + data[i].CON4 + "</td>";
                        str += "<td>" + data[i].SPEC5 + "</td>";  str += "<td>" + data[i].CON5 + "</td>";
                        str += "<td>" + data[i].SPEC6 + "</td>";  str += "<td>" + data[i].CON6 + "</td>";
                        str += "<td>" + data[i].SPEC7 + "</td>";  str += "<td>" + data[i].CON7 + "</td>";
                        str += "<td>" + data[i].SPEC8 + "</td>";  str += "<td>" + data[i].CON8 + "</td>";
                        str += "<td>" + data[i].SPEC9 + "</td>";  str += "<td>" + data[i].CON9 + "</td>";
                        str += "<td>" + data[i].SPEC10 + "</td>";  str += "<td>" + data[i].CON10 + "</td>";
                        str += "<td>" + data[i].SPEC11 + "</td>";  str += "<td>" + data[i].CON11 + "</td>";
                        str += "<td>" + data[i].SPEC12 + "</td>";  str += "<td>" + data[i].CON12 + "</td>";
                        str += "<td>" + data[i].SPEC13 + "</td>";  str += "<td>" + data[i].CON13 + "</td>";
                        str += "<td>" + data[i].SPEC14 + "</td>";  str += "<td>" + data[i].CON14 + "</td>";
                        str += "<td>" + data[i].SPEC15 + "</td>";  str += "<td>" + data[i].CON15 + "</td>";
                        str += "<td>" + data[i].SPEC16 + "</td>";  str += "<td>" + data[i].CON16 + "</td>";
                        str += "<td>" + data[i].SPEC17 + "</td>";  str += "<td>" + data[i].CON17 + "</td>";
                        str += "<td>" + data[i].SPEC18 + "</td>";  str += "<td>" + data[i].CON18 + "</td>";
                        str += "<td>" + data[i].SPEC19 + "</td>";  str += "<td>" + data[i].CON19 + "</td>";
                        str += "<td>" + data[i].SPEC20 + "</td>";  str += "<td>" + data[i].CON20 + "</td>";

                        str += "<td>" + data[i].KEY1 + "</td>";  str += "<td>" + data[i].VAL1 + "</td>";
                        str += "<td>" + data[i].KEY2 + "</td>";  str += "<td>" + data[i].VAL2 + "</td>";
                        str += "<td>" + data[i].KEY3 + "</td>";  str += "<td>" + data[i].VAL3 + "</td>";
                        str += "<td>" + data[i].KEY4 + "</td>";  str += "<td>" + data[i].VAL4 + "</td>";
                        str += "<td>" + data[i].KEY5 + "</td>";  str += "<td>" + data[i].VAL5 + "</td>";
                        str += "<td>" + data[i].KEY6 + "</td>";  str += "<td>" + data[i].VAL6 + "</td>";
                        str += "<td>" + data[i].KEY7 + "</td>";  str += "<td>" + data[i].VAL7 + "</td>";
                        str += "<td>" + data[i].KEY8 + "</td>";  str += "<td>" + data[i].VAL8 + "</td>";
                        str += "<td>" + data[i].KEY9 + "</td>";  str += "<td>" + data[i].VAL9 + "</td>";
                        str += "<td>" + data[i].KEY10 + "</td>";  str += "<td>" + data[i].VAL10 + "</td>";
                        str += "<td>" + data[i].KEY11 + "</td>";  str += "<td>" + data[i].VAL11 + "</td>";
                        str += "<td>" + data[i].KEY12 + "</td>";  str += "<td>" + data[i].VAL12 + "</td>";
                        str += "<td>" + data[i].KEY13 + "</td>";  str += "<td>" + data[i].VAL13 + "</td>";
                        str += "<td>" + data[i].KEY14 + "</td>";  str += "<td>" + data[i].VAL14 + "</td>";
                        str += "<td>" + data[i].KEY15 + "</td>";  str += "<td>" + data[i].VAL15 + "</td>";
                        str += "<td>" + data[i].KEY16 + "</td>";  str += "<td>" + data[i].VAL16 + "</td>";
                        str += "<td>" + data[i].KEY17 + "</td>";  str += "<td>" + data[i].VAL17 + "</td>";
                        str += "<td>" + data[i].KEY18 + "</td>";  str += "<td>" + data[i].VAL18 + "</td>";
                        str += "<td>" + data[i].KEY19 + "</td>";  str += "<td>" + data[i].VAL19 + "</td>";
                        str += "<td>" + data[i].KEY20 + "</td>";  str += "<td>" + data[i].VAL20 + "</td>";

                        //str += "<td>" + data[i].KEY1 + "</td>";
                        //str += "<td>" + data[i].VAL1 + "</td>";

                        str += "</tr>";
                    } // end for


                    $("#contentTable").append(str);


                    $("#infoTable").DataTable({
                        "responsive": true,
                        "lengthChange": true,
                        "pageLength": 50,     //페이지 당 글 개수 설정
                        "autoWidth": false, // 가로자동
                        "processing": true,
                        "scrollX" : true, //가로  스크롤
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
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(1)');

                } else {
                    alert("검색결과가 없습니다.");

                }
            } // end success;
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
