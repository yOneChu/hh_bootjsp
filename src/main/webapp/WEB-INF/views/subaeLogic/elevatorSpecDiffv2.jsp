<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>영업사양 비교</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Bootstrap CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">



    <style>
        body {
            background-color: #f5f7fa;
            padding-top: 50px;
        }
        .highlight {
            background-color: #fff3cd !important; /* 노란 배경 */
        }
        .spec-table th {
            background-color: #006a44;
            color: white;
        }
        .card-header {
            background-color: #00563f;
            color: white;
        }
    </style>
</head>
<body>
<div class="container">
    <h3 class="text-center mb-4">🚀 두 엘리베이터 영업사양 비교</h3>

    <!-- Input Card -->
    <div class="card shadow-sm mb-4">
        <div class="card-header">호기번호 입력</div>
        <div class="card-body row g-3">
            <div class="col-md-5">
                <input type="text" id="ho1" class="form-control" placeholder="호기번호 1">
            </div>
            <div class="col-md-5">
                <input type="text" id="ho2" class="form-control" placeholder="호기번호 2">
            </div>
            <div class="col-md-2 d-grid">
                <button class="btn btn-primary" onclick="search()">비교하기</button>
                <button class="btn btn-primary" onclick="rowMatchHide()">다른부분만 표시</button>
            </div>
        </div>
    </div>

    <!-- Result Card -->
    <div class="card shadow-sm">
        <div class="card-header">공통 영업사양 비교 결과</div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-bordered spec-table m-0" id="infoTable">
                    <thead>
                        <tr>
                            <th>TAB명</th>
                            <th>특성명</th>
                            <th>특성코드</th>
                            <th>호기 1</th>
                            <th>호기 2</th>
                        </tr>
                    </thead>
                    <tbody id="result-body">
                    <!-- 결과 표시 영역 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

<script src="/resources/javascript/elevatorSpecDiff.js"></script>

</body>
</html>
