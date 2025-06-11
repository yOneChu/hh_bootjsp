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

    String fileName = request.getParameter("fileName");
    System.out.println("fileName = " + fileName);

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
    <%--<jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />--%>
    <jsp:include page="../layout/basicSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>본사/법인 자재비교</h1>
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

                        <div id="3DViewerDiv">

                        </div>
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




<!--APSviewer    -->
<link rel="stylesheet" href="https://developer.api.autodesk.com/modelderivative/v2/viewers/style.min.css?v=v7.*" type="text/css">
<script language="JavaScript" src="https://developer.api.autodesk.com/modelderivative/v2/viewers/viewer3D.min.js?v=v7.*"></script>


<link rel="stylesheet" href="http://cdn.jsdelivr.net/gh/autodesk-forge/forge-extensions/public/extensions/camerarotation/contents/main.css">
<script src="http://cdn.jsdelivr.net/gh/autodesk-forge/forge-extensions/public/extensions/camerarotation/contents/main.js"></script>



<script>

    //ready
    $(document).ready(function() {


        view3DModel();

    });


    function view3DModel(fileName)
    {
        //var DWFFileName = '3D_Models/' + fileName;
        //C:\
        //let DWFFileName = 'c:/' + '200C0374.iam.dwf';
        //let DWFFileName = "C:\\200C0374.iam.dwf";
        let DWFFileName = "<%=fileName%>";
        console.log(DWFFileName);

        Autodesk.Viewing.Initializer({ env: 'Local' }, async function () {
            const viewer = new Autodesk.Viewing.GuiViewer3D(document.getElementById('3DViewerDiv'));

            viewer.start();
            //viewer.loadExtension('Autodesk.DocumentBrowser');



            viewer.setBimWalkToolPopup(false)
            console.log(viewer);
            //console.log(viewer.toolController.isToolActivated('bimWalk');



            //ext.extendLocalization(locales);
            //viewer.extendLocalization(locales);
            //viewer.setTheme('light-theme');
            viewer.loadModel(DWFFileName);


            const toolbar = viewer.getToolbar();
            console.log('toolbar --', toolbar);
            //viewer.loadExtension('Autodesk.DWF').then(() => {
            //  viewer.loadModel(DWFFileName);
            //});
        })
    }





</script>

</html>
