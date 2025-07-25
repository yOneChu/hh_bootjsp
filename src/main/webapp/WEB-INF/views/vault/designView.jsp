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



    <title>시각화 뷰어</title>

    <!-- Google Font: Source Sans Pro -->
    <!--    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">-->
<%--    <link rel="stylesheet" href="/resources/dist/googleFont.css">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="/resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="/resources/dist/plugins/select2/css/select2.min.css">

    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">--%>


    <!--APSviewer    -->
    <link rel="stylesheet" href="https://developer.api.autodesk.com/modelderivative/v2/viewers/style.min.css?v=v7.*" type="text/css">
    <script language="JavaScript" src="https://developer.api.autodesk.com/modelderivative/v2/viewers/viewer3D.min.js?v=v7.*"></script>

    <link rel="stylesheet" href="http://cdn.jsdelivr.net/gh/autodesk-forge/forge-extensions/public/extensions/camerarotation/contents/main.css">
    <script src="http://cdn.jsdelivr.net/gh/autodesk-forge/forge-extensions/public/extensions/camerarotation/contents/main.js"></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard/dist/web/pretendard.css" />

    <style>
        body {
            margin: 0;
            /*font-family: 'Pretendard', sans-serif;*/
            font-family: 'Cascadia Code', sans-serif;
            background-color: #121212;
            color: #f0f0f0;
        }

        /* ===== 상단 메뉴 ===== */
        .top-nav {
            height: 56px;
            background-color: #1e1e1e;
            display: flex;
            align-items: center;
            padding: 0 24px;
            justify-content: space-between;
            border-bottom: 1px solid #333;
        }

        .top-nav .logo {
            font-weight: bold;
            font-size: 20px;
            color: #26d07c;
        }

        .top-nav .menu {
            display: flex;
            gap: 16px;
        }

        .top-nav .menu a {
            color: #ccc;
            text-decoration: none;
            font-size: 14px;
            transition: color 0.2s;
        }

        .top-nav .menu a:hover {
            color: #ffffff;
        }

        /* ===== 메인 컨텐츠 영역 ===== */
        .viewer-container {
            display: flex;
            height: calc(100vh - 56px);
        }

        .tree-panel {
            width: 300px;
            background-color: #1a1a1a;
            padding: 16px;
            border-right: 1px solid #333;
            overflow-y: auto;
        }

        .tree-panel h3 {
            font-size: 16px;
            margin-bottom: 12px;
        }

        .tree-panel ul {
            list-style: none;
            padding-left: 0;
            font-size: 14px;
        }

        .tree-panel li {
            margin: 8px 0;
            cursor: pointer;
            color: #ccc;
        }

        .tree-panel li:hover {
            color: #fff;
        }

        .viewer-panel {
            flex-grow: 1;
            background-color: #000;
            position: relative;
        }

        .viewer-placeholder {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            color: #888;
            font-size: 18px;
        }

    </style>
</head>


<body>

<!-- 상단 메뉴 -->
<div class="top-nav">
    <div class="logo">Hyundai 3D Viewer 👀</div>
    <div class="menu">
        <a href="#">파일 열기</a>
        <a href="#">저장</a>
        <a href="#">설정</a>
    </div>
</div>

<!-- 본문 영역 -->
<div class="viewer-container">
    <!-- 왼쪽 트리 -->
    <div class="tree-panel">
        <h3>부품 트리</h3>
        <ul>
            <li>엘리베이터</li>
            <li>├ 승강로</li>
            <li>├ 카(CAR)</li>
            <li>├ 도어 시스템</li>
            <li>└ 제어반</li>
        </ul>
    </div>

    <!-- 오른쪽 뷰어 -->
    <div class="viewer-panel">
        <!-- Three.js 또는 Autodesk Viewer 삽입 예정 -->
        <%--<div class="viewer-placeholder">3D 모델이 여기에 표시됩니다.</div>--%>

        <div id="3DViewerDiv">

        </div>
    </div>
</div>




</body>

<!-- <script src="https://code.jquery.com/jquery-3.5.1.js"></script> -->

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

<!-- AdminLTE App -->
<script src="/resources/dist/js/adminlte.min.js"></script>

<!-- Bootstrap 4 -->
<script src="/resources/dist/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>






<script>

    let $folderTree;


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
        //let DWFFileName = "modelA/200C0374.iam.dwf";
        let DWFFileName = "static/modelA/200C0374.iam.dwf";

        //200C0374.iam.dwf

        console.log(DWFFileName);

        Autodesk.Viewing.Initializer({ env: 'Local' }, async function () {
            const viewer = new Autodesk.Viewing.GuiViewer3D(document.getElementById('3DViewerDiv'));

            console.log("11111111");
            console.log(viewer)

            viewer.start();

            viewer.setBimWalkToolPopup(false)
            console.log(viewer);

            viewer.loadModel(DWFFileName);


        })
    }





</script>

</html>
