<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%



%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <%--<meta http-equiv="Cache-Control" content="no-cache"/>--%>
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />



    <title>3D 사용 가이드</title>

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
                        <h1>📥 3D 사용자 메뉴얼 다운로드</h1>
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

                <div class="card-body" style="zoom:85%;">
                        <div class="col-12">
                            <div class="callout callout-info">
                                <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                <%--<h5 style="color: blue">- 3D 사용을 위한 설치 및 모델링 가이드 </h5>--%>
                                <h5>- 3D 사용을 위한 설치 및 모델링 가이드 </h5>
                                <%--<h5>- PID 별 라인 개수 : 각 PID(최신버전)에 연결된 라인 총 수 (1주 회 집계)</h5>
                                <h5>- PID 전체 라인 수 : 작업한 전체 라인 수 (매일)</h5>
                                <h5>- PID 개수 : 등록된 PID 개수 (매일)</h5>--%>
                            </div>
                        </div>
                </div> <!-- /.card-body -->

                <div class="row">
                    <div class="col-12">
                        <div class="card card-info">

                            <div class="card-header">
                                <h3 class="card-title"><i class="ion ion-clipboard mr-1"></i>📥 3D 사용자 메뉴얼 다운로드</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:100%;">

                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th style="width: 10px">#</th>
                                            <th style="font-weight: bold;"><h5>내용</h5></th>
                                            <th style="font-weight: bold;"><h5>다운로드 링크</h5></th>
                                            <th style="font-weight: bold;"><h5>설명</h5></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>1.</td>
                                            <td style="font-weight: bold;">
                                                <h5>환경설정 파일</h5><br>
                                                <%--<h5 style="color: red"> (개발 진행 중으로 4/28 이후 설치 요청)</h5>--%>
                                            </td>
                                            <td>
                                                <button class="btn btn-success"><a href="http://10.225.80.35/vaultview/api/downloadaddin" target="_blank" style="color: white">⚙️ VaultAddin.zip</a>  </button>
                                            </td>
                                            <td>zip파일 압축해제 후, 설치</td>
                                        </tr>
                                        <tr>
                                            <td>2.</td>
                                            <td style="font-weight: bold;">
                                                <h5> 환경설정 파일 설치 가이드</h5><br>
                                                <%--<h5 style="color: red"> (개발 진행 중으로 4/28 이후 설치 요청)</h5>--%>
                                            </td>
                                            <td>
                                                <button class="btn btn-success" onclick="downloadManual('3D 환경 설정 설치 가이드.pptx', '/resources/document/menual/')">📘 3D 환경설정 설치 가이드.pptx v1</button>
                                            </td>
                                            <%--<td>AutoDesk Inventor(3D CAD), Vault(데이터 관리)과 PLM을 유기적으로 연동하여 효율적인 설계 및 데이터 관리를 구현하기 위해 설치 필요.</td>--%>
                                            <td>AutoDesk Inventor(3D CAD), Vault(데이터 관리)과 PLM 연동하여 효율적인 설계 위해 설치 필요.</td>
                                        </tr>
                                        <tr>
                                            <td>3.</td>
                                            <td style="font-weight: bold;"><h5> 3D User Guide</h5></td>
                                            <td>
                                                <button class="btn btn-success" onclick="downloadManual('3D User Guide.docx', '/resources/document/menual/')">📑 3D User Guide.ppt v1</button>
                                            </td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <td>4.</td>
                                            <td style="font-weight: bold;"><h5>3D 매개변수 정의 및 체계</h5></td>
                                            <td>
                                                <button class="btn btn-success" onclick="downloadManual('3D 매개변수 정의 및 체계.pptx', '/resources/document/menual/')">📑 3D 매개변수 정의 및 체계.pptx v1</button>
                                            </td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <td>5.</td>
                                            <td style="font-weight: bold;"><h5>HDEL Snippets</h5></td>
                                            <td>
                                                <%--<button class="btn btn-success" onclick="downloadManual('HDEL Snippets.xml', '/resources/document/menual/')">📑 HDEL Snippets.xml v1</button>--%>
                                                    <button class="btn btn-success" onclick="window.open('http://10.225.80.35/AutodeskTC/HDEL/explore/file/397001', '_blank')">📑 HDEL Snippets.xml v1</button>
                                            </td>
                                            <td>문의사항 -> 강형주M</td>
                                        </tr>
                                        <tr>
                                            <td>6.</td>
                                            <td style="font-weight: bold;"><h5>Inventor 2024 설치 요령</h5></td>
                                            <td>
                                                <button class="btn btn-success" onclick="window.open('https://portal.hdel.co.kr/Portal/Board/HEL_AUTOCAD/View?ItemID=21a52a66-bd7b-4184-9526-e77dcd1d2cba', '_blank')">📑 Inventor 2024 설치</button>
                                            </td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <td>7.</td>
                                            <td style="font-weight: bold;"><h5>Vault 2024 설치</h5></td>
                                            <td>
                                                <button class="btn btn-success" onclick="window.open('https://portal.hdel.co.kr/Portal/Board/HEL_AUTOCAD/View?ItemID=c1d57d2f-97f0-4a28-9b25-a03759b67329', '_blank')">📑 Vault 2024 설치 </button>
                                            </td>
                                            <td>-</td>
                                        </tr>
                                    </tbody>
                                </table>


                            </div>
                            <!-- /.card-body -->
                        </div><!-- /.card -->
                        <!-- /.card -->

                    </div> <!-- /.col-lg-6 -->


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

<script>
    function downloadManual(filename, filePath) {
        //const filePath = `/downloads/` + filename; // 상대 경로 설정 (또는 서버 경로로 변경 가능)
        const link = document.createElement('a');
        link.href = filePath + filename;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    //ready
    $(document).ready(function() {

    }); // end document ready

</script>

</html>
