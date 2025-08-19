<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>엑셀 어시스턴트</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/handsontable/12.4.0/handsontable.full.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .container-fluid, .row, .col-lg, .excel-container {
            height: 100%;
        }

        .navbar-brand {
            font-weight: bold;
            color: #198754 !important;
        }

        .toolbar {
            background-color: #ffffff;
            border-bottom: 1px solid #dee2e6;
            padding: 10px 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .btn-toolbar-group {
            margin-right: 15px;
        }

        .excel-container {
            /*background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin: 20px;
            overflow: hidden;
            height: calc(100vh - 200px); !* 화면에 꽉 차게, 필요에 맞게 조정 *!*/
            display: flex;
            flex-direction: column;
            margin: 0;              /* 불필요한 margin 제거 */
            border-radius: 0;
            box-shadow: none;
            height: calc(100vh - 160px); /* 상단 navbar, toolbar, formula-bar 높이 제외 */
        }

        #handsontable-container {
            /*height: 500px;*/
            flex: 1;               /* 남은 공간 전부 차지 */
            width: 100%;           /* 가로 전체 */
            border: 2px solid #198754;
            border-radius: 4px;
            overflow: hidden;        /* 스크롤/넘침 방지 */
        }

        /* Handsontable 커스텀 스타일 */
        .handsontable th {
            background-color: #e9ecef !important;
            color: #495057 !important;
            font-weight: 600 !important;
        }

        .handsontable .currentRow {
            background-color: #f8f9fa !important;
        }

        .handsontable .area {
            background-color: rgba(25, 135, 84, 0.1) !important;
        }

        .handsontable .current {
            background-color: #cfe2ff !important;
            border: 2px solid #0d6efd !important;
        }

        .formula-bar {
            background-color: #f8f9fa;
            border-bottom: 1px solid #dee2e6;
            padding: 10px 15px;
        }

        .cell-reference {
            width: 80px;
            margin-right: 10px;
        }

        .formula-input { /* Renamed to search-input for clarity in context */
            flex: 1;
        }

        .status-bar {
            background-color: #e9ecef;
            border-top: 1px solid #dee2e6;
            padding: 5px 15px;
            font-size: 0.875rem;
        }

             /* 데이터 셀 */
         .htCore td.pink-col {
             /*background-color: #ffe0f0 !important;*/
             background-color: #fdedea !important;
         }
        /* 컬럼 헤더 */
        .handsontable thead th.pink-col-header {
            background-color: #fdedea !important;
        }

        /* FOOTER 기본 스타일 (고정X, 자연스럽게 아래 배치) */
        .main-footer {
            background: #ffffff;       /* 화이트 배경 */
            color: #6c757d;            /* 은은한 회색 텍스트 */
            padding: 12px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 0.85rem;
            border-top: 1px solid #eaeaea; /* 얇은 경계선 */
            margin-top: 20px;          /* 본문과 간격 */
        }

        .main-footer .footer-link {
            color: #6c757d;
            text-decoration: none;
            transition: color 0.2s;
        }

        .main-footer .footer-link:hover {
            color: #212529; /* hover 시 살짝 진해짐 */
        }

    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <i class="fas fa-table me-2"></i>로직에디터 어시스턴트
        </a>
    </div>
</nav>

<div class="toolbar">
    <div class="d-flex align-items-center flex-wrap">
        <div class="btn-toolbar-group">
            <%--<button class="btn btn-outline-info btn-sm me-1" onclick="exportToCSV()">
                <i class="fas fa-download"></i> CSV 내보내기
            </button>--%>
            <button class="btn btn-outline-success btn-sm" onclick="insertChart()">
                <i class="fas fa-chart-line"></i> Maptify
            </button>
        </div>
    </div>
</div>

<div class="formula-bar">
    <div class="d-flex align-items-center">
        <input type="text" class="form-control cell-reference" id="cellReference" value="A1" readonly>
        <div class="me-2"><i class="fas fa-search"></i></div>
        <%--<input type="text" class="form-control formula-input" id="searchInput" placeholder="PID를 입력하세요...">--%>
        <input type="text" class="form-control formula-input" id="searchInput" placeholder="PID를 입력하세요..." style="max-width:250px;">
        <%--<button class="btn btn-primary ms-2" onclick="performSearch()">검색</button>--%>

        <%--<button class="btn btn-primary ms-2" onclick="searchPID()">--%>
        <button class="btn btn-primary ms-2" onclick="searchPID()" style="width:120px;">
            검색
        </button>
    </div>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-lg">
            <div class="excel-container">
                <div id="handsontable-container"></div>

                <div class="status-bar">
                    <div class="d-flex justify-content-between">
                        <span id="statusText">준비</span>
                        <span>행: <span id="rowCount">20</span> | 열: <span id="colCount">8</span> | 선택된 셀: <span id="selectedCell">A1</span></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Footer 추가 -->
<!-- Footer -->
<footer class="main-footer">
    <div class="footer-left">
        <i class="fas fa-code-branch me-1"></i><b>Version</b> 1.0.0
    </div>
    <div class="footer-right">
        <strong>
            Copyright &copy; 2025
            <a href="#" class="footer-link">수배로직설계팀-김영환 M</a>
        </strong>
        <span>All rights reserved.</span>
    </div>
</footer>

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/handsontable/12.4.0/handsontable.full.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/hyperformula@2.2.0/dist/hyperformula.full.min.js"></script>

<script src="/resources/javascript/logicView.js"></script>


</body>
</html>