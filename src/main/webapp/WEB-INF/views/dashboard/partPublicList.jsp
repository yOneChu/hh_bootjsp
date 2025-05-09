<%@page import="java.util.ArrayList"%>
<%@ page import="com.kyhslam.service.PartPublicService" %>
<%@ page import="com.kyhslam.dto.PartPublicDTO" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    PartPublicService partPublicService = new PartPublicService();
    ArrayList<PartPublicDTO> result = partPublicService.getPartPublicData();

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <meta http-equiv="Cache-Control" content="no-cache"/>
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
                        <h1>부품공용화 자재 리스트</h1>
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

<%--

                        <div class="col-md-4"
                             style="margin-bottom: 8px; padding-left: 0px">
                            <div class="category-filter input-group">
                                <div class="input-group-prepend">
                                    <label class="input-group-text" for="categoryFilter">MAIN_TYPE</label>
                                </div>
                                <select id="categoryFilter" class="form-control">
                                    <option value="ALL">Show All</option>
                                    <option value="TM">TM</option>
                                    <option value="LAMP">LAMP</option>
                                    <option value="GOV">GOV</option>
                                    <option value="CP">CP</option>
                                    <option value="HPB">HPB</option>
                                    <option value="HIP">HIP</option>
                                    <option value="HPI">HPI</option>
                                    <option value="CARTOPBOX">CARTOPBOX</option>
                                    <option value="PIT">PIT</option>
                                </select>
                            </div>
                        </div>
--%>


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
                                            <th style="font-weight: bold; text-align: center;">자재번호</th>
                                            <th style="font-weight: bold; text-align: center;">MAIN_TYPE</th>
                                            <th style="font-weight: bold; text-align: center;">상세 Type</th>
                                        </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <%
                                        for (int i = 0; i < result.size(); i++) {
                                            PartPublicDTO dto = result.get(i);
                                            String partNo = dto.getPartNo();
                                            String mainType = dto.getMainType();
                                            String partType = dto.getPartType();
                                    %>
                                        <tr id="<%=mainType%>">
                                            <td style="text-align: center"><%=partNo%></td>
                                            <td style="text-align: center"><%=mainType%></td>
                                            <td style="text-align: center"><%=partType%></td>

                                        </tr>

                                    <%

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
        //"processing": true,
        "ordering": false,
        "searching" : true,
        "paging" : false, // 페이징표시 삭제
        "destroy": true, // 테이블 재생성
        "buttons": ["csv", "excel", "copy"],
        initComplete: function () {
            this.api().columns().every( function (e) {
                if(e==1 ){
                    var column = this;


                    col_name = "타입_선택(전체)"; //dt_columns[Number(this[1].toString())].title
                    var select = $('<select class="form-control"><option value="">'+col_name+'</option></select>')
                        .appendTo( $(column.header()).empty() )
                        .on( 'change', function () {
                            var val = $.fn.dataTable.util.escapeRegex(
                                $(this).val()
                            );
                            column.search( val ? '^'+val+'$' : '', true, false ).draw();
                        } );

                    column.data().unique().sort().each( function ( d, j ) {
                        select.append( '<option value="'+d+'">'+d+'</option>' )
                    } );
                }
            } );
        }
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

    //ready
    $(document).ready(function() {

        console.log('start');
        let urlVal = "https://vault-in.hdel.co.kr:8070/jqpr/getJqplDashbard";


        $.ajax({
            type : "get",
            url : urlVal,
            // data : {
            // 	productNumber : productNumber,
            // 	blockNo : blockVal,
            // 	searchFlag : searchFlag
            // },
            success : function(data)
            {
                console.log("data - ", data);


            } // end success;

        });


        $("#categoryFilter").on("change", function () {

            //$(this).val();


            let selectedVal = $("#categoryFilter option:selected").val();
            console.log(selectedVal);

            $("#CP").hide();

            /*if ('ALL' == selectedVal) {
                //datas
                $("#TM").show();
                $("#LAMP").show();
                $("#GOV").show();
                $("#CP").show();
                $("#HPB").show();
                $("#HIP").show();
                $("#HPI").show();
                $("#CARTOPBOX").show();
                $("#PIT").show();
            } else if ("TM" == selectedVal) {
                $("#TM").show();
                $("#LAMP").hide();
                $("#GOV").hide();
                $("#CP").hide();
                $("#HPB").hide();
                $("#HIP").hide();
                $("#HPI").hide();
                $("#CARTOPBOX").hide();
                $("#PIT").hide();
            } else if (selectedVal.startsWith("LAMP")) {
                console.log("lamp");
                $("#TM").hide();
                $("#LAMP").show();
                $("#GOV").hide();
                $("#CP").hide();
                $("#HPB").hide();
                $("#HIP").hide();
                $("#HPI").hide();
                $("#CARTOPBOX").hide();
                $("#PIT").hide();
            } else if ("GOV" == selectedVal) {
                $('#TM').hide();
                $('#LAMP').hide();
                $('#GOV').show();
                $('#CP').hide();
                $('#HPB').hide();
                $('#HIP').hide();
                $('#HPI').hide();
                $('#CARTOPBOX').hide();
                $('#PIT').hide();
            } else if ("CP" == selectedVal) {
                $(".TM").hide();
                $(".LAMP").hide();
                $(".GOV").hide();
                $(".CP").show();
                $(".HPB").hide();
                $(".HIP").hide();
                $(".HPI").hide();
                $(".CARTOPBOX").hide();
                $(".PIT").hide();
            } else if ("HPB" == selectedVal) {
                $(".TM").hide();
                $(".LAMP").hide();
                $(".GOV").hide();
                $(".CP").hide();
                $(".HPB").show();
                $(".HIP").hide();
                $(".HPI").hide();
                $(".CARTOPBOX").hide();
                $(".PIT").hide();
            } else if ("HIP" == selectedVal) {
                $(".TM").hide();
                $(".LAMP").hide();
                $(".GOV").hide();
                $(".CP").hide();
                $(".HPB").hide();
                $(".HIP").show();
                $(".HPI").hide();
                $(".CARTOPBOX").hide();
                $(".PIT").hide();
            } else if ("HPI" == selectedVal) {
                $(".TM").hide();
                $(".LAMP").hide();
                $(".GOV").hide();
                $(".CP").hide();
                $(".HPB").hide();
                $(".HIP").hide();
                $(".HPI").show();
                $(".CARTOPBOX").hide();
                $(".PIT").hide();
            } else if ("CARTOPBOX" == selectedVal) {
                $(".TM").hide();
                $(".LAMP").hide();
                $(".GOV").hide();
                $(".CP").hide();
                $(".HPB").hide();
                $(".HIP").hide();
                $(".HPI").hide();
                $(".CARTOPBOX").show();
                $(".PIT").hide();
            } else if ("PIT" == selectedVal) {
                $(".TM").hide();
                $(".LAMP").hide();
                $(".GOV").hide();
                $(".CP").hide();
                $(".HPB").hide();
                $(".HIP").hide();
                $(".HPI").hide();
                $(".CARTOPBOX").hide();
                $(".PIT").show();
            }*/

            //aa();

        });


        function aa() {
            $(".CP").hide();
        }
        //categoryFilter
    }); // end document ready

</script>

</html>
