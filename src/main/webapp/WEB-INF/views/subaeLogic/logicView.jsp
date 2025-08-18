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
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin: 20px;
            overflow: hidden;
            height: calc(100vh - 200px); /* 화면에 꽉 차게, 필요에 맞게 조정 */
        }

        #handsontable-container {
            /*height: 500px;*/
            flex: 1;               /* 남은 공간 전부 차지 */
            width: 100%;           /* 가로 전체 */
            border: 2px solid #198754;
            border-radius: 4px;
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

        .assistant-panel {
            background: linear-gradient(135deg, #198754, #20c997);
            color: white;
            padding: 20px;
            border-radius: 8px;
            margin: 20px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .assistant-suggestions {
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 6px;
            padding: 15px;
            margin-top: 15px;
        }

        .suggestion-item {
            background-color: rgba(255, 255, 255, 0.2);
            border: none;
            color: white;
            padding: 8px 15px;
            margin: 5px;
            border-radius: 20px;
            font-size: 0.875rem;
            transition: all 0.3s ease;
        }

        .suggestion-item:hover {
            background-color: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
        }

        .floating-toolbar {
            position: fixed;
            bottom: 30px;
            right: 30px;
            z-index: 1000;
        }

        .ai-btn {
            background: linear-gradient(45deg, #6f42c1, #e83e8c);
            border: none;
            border-radius: 50px;
            padding: 15px 25px;
            color: white;
            font-weight: bold;
            box-shadow: 0 4px 15px rgba(111, 66, 193, 0.4);
            transition: all 0.3s ease;
        }

        .ai-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 6px 20px rgba(111, 66, 193, 0.6);
            color: white;
        }


        .col-lg {
            height: 100%;       /* 상위 grid 계층 모두 높이 채우기 */
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
            <button class="btn btn-outline-info btn-sm me-1" onclick="exportToCSV()">
                <i class="fas fa-download"></i> CSV 내보내기
            </button>
            <button class="btn btn-outline-success btn-sm" onclick="insertChart()">
                <i class="fas fa-chart-line"></i> Maptify
            </button>
        </div>
    </div>
</div>

<div class="formula-bar">
    <div class="d-flex align-items-center">
        <input type="text" class="form-control cell-reference" id="cellReference" value="A1" readonly>
        <div class="me-2"><i class="fas fa-search"></i></div> <input type="text" class="form-control formula-input" id="searchInput" placeholder="PID를 입력하세요...">
        <%--<button class="btn btn-primary ms-2" onclick="performSearch()">검색</button>--%>
        <button class="btn btn-primary ms-2" onclick="searchPID()">검색</button>
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



<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/handsontable/12.4.0/handsontable.full.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/hyperformula@2.2.0/dist/hyperformula.full.min.js"></script>

<script src="/resources/javascript/logicView.js"></script>


</body>
</html>