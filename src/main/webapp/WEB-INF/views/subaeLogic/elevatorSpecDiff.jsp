<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>영업사양 비교</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- jQuery & DataTables CDN -->
    <link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css">
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js"></script>

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
            </div>
        </div>
    </div>

    <!-- Result Card -->
    <div class="card shadow-sm">
        <div class="card-header">공통 영업사양 비교 결과</div>
        <div class="card-body">
            <div class="table-responsive">
                <table id="specTable" class="table table-bordered table-hover spec-table">
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
                    <!-- 결과 행 자동 생성 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>


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
<script src="/resources/javascript/elevatorSpecDiff.js"></script>

<script>
    const dummyData = {
        "H001": { ho1: "1050kg", ho2: "1050kg" },
        "H002": { ho1: "1.75m/s", ho2: "1.75m/s" },
        "H003": { ho1: "M2 도어", ho2: "M3 도어" },
        "H004": { ho1: "지진감지센서", ho2: "지진감지센서" },
        "H005": { ho1: "현장조립", ho2: "공장조립" },
    };

    let table;

    /*function compareSpecs() {
        const tbody = document.getElementById("result-body");
        tbody.innerHTML = "";

        for (const code in dummyData) {
            const val1 = dummyData[code].ho1;
            const val2 = dummyData[code].ho2;

            const tr = document.createElement("tr");
            if (val1 !== val2) tr.classList.add("highlight");

            tr.innerHTML = `
        <td>${code}</td>
        <td>${val1}</td>
        <td>${val2}</td>
        <td>${val2}</td>
        <td>${val2}</td>
      `;
            tbody.appendChild(tr);
        }

        // DataTable 재초기화
        if ($.fn.DataTable.isDataTable('#specTable')) {
            table.destroy();
        }

        table = $('#specTable').DataTable({
            paging: true,
            searching: true,
            info: false,
            lengthChange: false
        });
    }*/
</script>
</body>
</html>
