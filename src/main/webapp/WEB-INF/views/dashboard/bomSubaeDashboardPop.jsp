<%@page import="java.util.ArrayList"%>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.kyhslam.service.SubaeService" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.kyhslam.dto.ProductDto" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    // BOM수배 대시보드 BOM 리스트 팝업화면
    // bomSubaeDashboardPop.jsp
    String contextPath = request.getContextPath();
    System.out.println("--- bomSubaeDashboardPop.jsp ---");


    String prodNo = (String)request.getParameter("prodNo");
    System.out.println("prodNo == " + prodNo);


    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // 원하는 Bean 가져오기
    SubaeService subaeService = (SubaeService) context.getBean("SubaeService");

    ProductDto param = new ProductDto();
    param.setProductNo(prodNo);
    ArrayList<ProductDto> list = subaeService.findSubaePartNoList(param);

    ProductDto productnfo = list.get(0);


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

<style>
    .truncate-text {
        white-space: nowrap;        /* 텍스트를 한 줄로 만듭니다. */
        overflow: hidden;           /* 넘치는 내용을 숨깁니다. */
        text-overflow: ellipsis;    /* 숨겨진 내용을 "..."으로 표시합니다. */
        max-width: 200px;           /* (선택 사항) td의 최대 너비를 지정하여 텍스트가 잘릴 기준을 명확히 합니다. */
        /* display: block; or inline-block; for max-width to work correctly on td content */
    }
    /* td 자체에 적용하거나, td 안에 div/span을 넣고 적용 */
    td.truncated {
        /* max-width는 td 자체에 적용하기 어려울 수 있으므로, 내부 요소에 적용하는 것이 좋음 */
    }

    /* 커스텀 툴팁을 위한 CSS */
    #custom-tooltip {
        position: absolute;       /* 절대 위치 */
        background-color: #333;   /* 배경색 */
        color: white;             /* 글자색 */
        padding: 8px 12px;        /* 패딩 */
        border-radius: 4px;       /* 모서리 둥글게 */
        box-shadow: 0 2px 5px rgba(0,0,0,0.3); /* 그림자 */
        display: none;            /* 초기에는 숨김 */
        z-index: 9999;            /* 다른 요소 위에 표시되도록 높은 z-index */
        max-width: 400px;         /* 툴팁 최대 너비 */
        word-wrap: break-word;    /* 긴 텍스트 줄바꿈 허용 */
        font-size: 14px;
        line-height: 1.4;
    }
</style>

<body>

<div class="wrapper">


    <!-- Content Wrapper. Contains page content -->
    <div class="content">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">


                        <h1> <%=productnfo.getProductNo()%> - <%=productnfo.getProductName()%> (<%=productnfo.getVersion() %>, 06:00기준)</font></h1>

                        <h4><font color="blue"> 최초설계 승인일 (<%=productnfo.getProductAppdate()%>) </font></h4>




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
                                            <th style="font-weight: bold; text-align: center;">호기</th>
                                            <th style="font-weight: bold; text-align: center;">수주명</th>
                                            <th style="font-weight: bold; text-align: center;">버전</th>
                                            <th style="font-weight: bold; text-align: center;">기종</th>

                                            <th style="font-weight: bold; text-align: center;">자재번호</th>
                                            <th style="font-weight: bold; text-align: center;">자재명</th>
                                            <th style="font-weight: bold; text-align: center;">품목</th>
                                            <th style="font-weight: bold; text-align: center;">수정여부</th>
                                            <th style="font-weight: bold; text-align: center;">수량</th>
                                            <th style="font-weight: bold; text-align: center;">BlockNo</th>

                                            <th style="font-weight: bold; text-align: center;">CMT</th>
                                        </tr>
                                    </thead>

                                    <tbody id="contentTable">
                                    <%
                                        for (int i = 0; i < list.size(); i++) {
                                            ProductDto info = list.get(i);
                                    %>
                                    <tr>
                                        <td style="text-align: center;"> <%=info.getProductNo() %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%=info.getProductName() %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%=info.getProductVersion() %> </td>
                                        <%--<td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <%=info.getGisong() %> </td>--%>
                                        <td style="font-weight: bold;  text-align: center;"> <%=info.getGisong() %> </td>

                                        <td style="font-weight: bold; text-align: center;"> <%=info.getPartNo() %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <%=info.getPartName() %> </td>
                                        <td style="font-weight: bold; text-align: center;"> <font color="red"><%=info.getBlockopt() %> </font> </td>
                                        <td style="text-align: center;"> <font color="red"><%=info.getUcheck() %> </font></td>
                                        <td style="font-weight: bold; text-align: center;"> <%=info.getQty() %> </td>
                                        <td style="text-align: center;"> <%=info.getBlockNo() %> </td>

                                        <%--<td style="text-align: left;" class="truncate-text" title="이것은 매우매우 긴 텍스트입니다. 한 줄로 표시하기에는 너무 길어서 잘리고 ...으로 표시됩니다. 하지만 마우스를 올리면 이 전체 내용을 볼 수 있습니다!">
                                            <%=info.getCmt() %> </td>--%>

                                        <td style="text-align: left;" class="has-custom-tooltip">
                                            <div class="truncate-text" data-full-text="<%=info.getCmt() %>">
                                                <%=info.getCmt() %>
                                            </div>
                                        </td>

                                    </tr>
                                    <%
                                        } // end for
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
        <strong>Copyright &copy; 2024 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>

<div id="custom-tooltip"></div>

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



        const $customTooltip = $('#custom-tooltip');

        // .has-custom-tooltip 클래스를 가진 td 요소에 이벤트 바인딩
        // 정확히는 잘리는 텍스트를 포함하는 div에 이벤트를 걸어도 좋습니다.
        $('.has-custom-tooltip .truncate-text').on({
            mouseenter: function(e) {
                // 마우스를 올렸을 때
                const fullText = $(this).data('full-text'); // data-full-text 속성 값 가져오기

                // 툴팁에 내용 설정
                $customTooltip.text(fullText);

                // 툴팁 위치 계산 (마우스 커서 위치에 따라 조절)
                // e.pageX, e.pageY는 마우스 커서의 현재 위치입니다.
                // 툴팁이 화면 밖으로 나가지 않도록 조정하는 로직을 추가할 수 있습니다.
                let tooltipX = e.pageX + 15; // 마우스 커서에서 오른쪽으로 15px
                let tooltipY = e.pageY + 15; // 마우스 커서에서 아래쪽으로 15px

                // 화면 오른쪽 경계를 넘어가는지 확인하고 조정 (간단한 예시)
                if (tooltipX + $customTooltip.outerWidth() > $(window).width()) {
                    tooltipX = e.pageX - $customTooltip.outerWidth() - 15;
                }
                // 화면 아래쪽 경계를 넘어가는지 확인하고 조정
                if (tooltipY + $customTooltip.outerHeight() > $(window).height() + $(window).scrollTop()) {
                    tooltipY = e.pageY - $customTooltip.outerHeight() - 15;
                }


                $customTooltip.css({
                    left: tooltipX,
                    top: tooltipY
                }).show(); // 툴팁 보이기
            },
            mouseleave: function() {
                // 마우스가 벗어났을 때
                $customTooltip.hide(); // 툴팁 숨기기
            },
            mousemove: function(e) {
                // 마우스 이동 시 툴팁 위치 업데이트 (선택 사항, 툴팁이 마우스 따라다니게 하려면)
                // 위에 mouseenter에서 위치 계산한 로직을 여기에 복사하여 사용
                let tooltipX = e.pageX + 15;
                let tooltipY = e.pageY + 15;
                if (tooltipX + $customTooltip.outerWidth() > $(window).width()) {
                    tooltipX = e.pageX - $customTooltip.outerWidth() - 15;
                }
                if (tooltipY + $customTooltip.outerHeight() > $(window).height() + $(window).scrollTop()) {
                    tooltipY = e.pageY - $customTooltip.outerHeight() - 15;
                }
                $customTooltip.css({
                    left: tooltipX,
                    top: tooltipY
                });
            }
        });
    }); // end document ready

</script>

</html>
