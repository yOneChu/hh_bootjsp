<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>3D Manual</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <style>
        .container {
            max-width: 600px;
            margin-top: 50px;
            text-align: center;
        }
    </style>
</head>


<body>

<div class="container">
    <h2>📥 3D 사용자 메뉴얼 다운로드</h2>
    <p>원하는 메뉴얼을 선택하여 다운로드하세요.</p>
    <div class="d-grid gap-2">
        <button class="btn btn-success"><a href="http://10.225.80.35/vaultview/api/downloadaddin">📘 <font color="white"> 환경설정 파일</font></a>  </button>
        <button class="btn btn-success" onclick="downloadManual('3D 환경 설정 설치 가이드.pptx', '/resources/document/menual/')">📘 3D 환경 설정 설치 가이드 v1 다운로드</button>
        <button class="btn btn-success" onclick="downloadManual('3D User Guide.docx', '/resources/document/menual/')">📗 3D User Guide v1 다운로드</button>
        <button class="btn btn-success" onclick="downloadManual('3D 매개변수 정의 및 체계.pptx', '/resources/document/menual/')">📗 3D 매개변수 정의 및 체계 v1 다운로드</button>

    </div>
</div>


</body>

<!-- <script src="https://code.jquery.com/jquery-3.5.1.js"></script> -->

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

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
