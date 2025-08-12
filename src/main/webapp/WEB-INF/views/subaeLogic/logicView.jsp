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
        }

        #handsontable-container {
            height: 500px;
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
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <i class="fas fa-table me-2"></i>로직에디터 어시스턴트
        </a>
        <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user-circle me-1"></i>개발자님
                </span>
            <button class="btn btn-outline-success btn-sm">
                <i class="fas fa-save me-1"></i>저장
            </button>
        </div>
    </div>
</nav>

<div class="toolbar">
    <div class="d-flex align-items-center flex-wrap">
        <div class="btn-toolbar-group">
            <button class="btn btn-outline-secondary btn-sm me-1">
                <i class="fas fa-undo"></i>
            </button>
            <button class="btn btn-outline-secondary btn-sm me-1">
                <i class="fas fa-redo"></i>
            </button>
        </div>

        <div class="btn-toolbar-group">
            <button class="btn btn-outline-secondary btn-sm me-1">
                <i class="fas fa-bold"></i>
            </button>
            <button class="btn btn-outline-secondary btn-sm me-1">
                <i class="fas fa-italic"></i>
            </button>
            <button class="btn btn-outline-secondary btn-sm me-1">
                <i class="fas fa-underline"></i>
            </button>
        </div>

        <div class="btn-toolbar-group">
            <button class="btn btn-outline-secondary btn-sm me-1" onclick="addRow()">
                <i class="fas fa-plus"></i> 행 추가
            </button>
            <button class="btn btn-outline-secondary btn-sm me-1" onclick="addColumn()">
                <i class="fas fa-plus"></i> 열 추가
            </button>
            <button class="btn btn-outline-danger btn-sm" onclick="deleteRowColumn()">
                <i class="fas fa-trash"></i> 삭제
            </button>
        </div>

        <div class="btn-toolbar-group">
            <button class="btn btn-outline-info btn-sm me-1" onclick="exportToCSV()">
                <i class="fas fa-download"></i> CSV 내보내기
            </button>
            <button class="btn btn-outline-success btn-sm" onclick="insertChart()">
                <i class="fas fa-chart-line"></i> 차트
            </button>
        </div>
    </div>
</div>

<div class="formula-bar">
    <div class="d-flex align-items-center">
        <input type="text" class="form-control cell-reference" id="cellReference" value="A1" readonly>
        <div class="me-2"><i class="fas fa-search"></i></div> <input type="text" class="form-control formula-input" id="searchInput" placeholder="검색 조건을 입력하세요...">
        <button class="btn btn-primary ms-2" onclick="performSearch()">검색</button>
    </div>
</div>

<div class="container-fluid">
    <div class="row">
        <div class="col-lg-9">
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

        <div class="col-lg-3">
            <div class="assistant-panel">
                <h5><i class="fas fa-robot me-2"></i>AI 어시스턴트</h5>
                <p class="mb-3">데이터 분석과 편집을 도와드리겠습니다!</p>

                <div class="assistant-suggestions">
                    <h6 class="mb-3">추천 기능</h6>
                    <button class="btn suggestion-item">
                        <i class="fas fa-chart-bar me-1"></i>차트 생성
                    </button>
                    <button class="btn suggestion-item">
                        <i class="fas fa-calculator me-1"></i>합계 계산
                    </button>
                    <button class="btn suggestion-item">
                        <i class="fas fa-filter me-1"></i>데이터 필터
                    </button>
                    <button class="btn suggestion-item">
                        <i class="fas fa-sort me-1"></i>정렬하기
                    </button>
                    <button class="btn suggestion-item">
                        <i class="fas fa-magic me-1"></i>자동 완성
                    </button>
                </div>

                <div class="mt-4">
                        <textarea class="form-control bg-transparent text-white"
                                  placeholder="AI에게 질문하세요... (예: '이 데이터의 평균을 구해줘')"
                                  rows="3" style="border: 1px solid rgba(255,255,255,0.3);"></textarea>
                    <button class="btn btn-light mt-2 w-100">
                        <i class="fas fa-paper-plane me-1"></i>전송
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="floating-toolbar">
    <button class="btn ai-btn" data-bs-toggle="tooltip" title="AI 도움말">
        <i class="fas fa-magic me-2"></i>AI 도우미
    </button>
</div>

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/handsontable/12.4.0/handsontable.full.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/hyperformula@2.2.0/dist/hyperformula.full.min.js"></script>

<script src="/resources/javascript/logicView.js"></script>


</body>
</html>