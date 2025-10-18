<%@ page import="com.kyhslam.util.UtilCommonAPI" %>
<%@ page import="org.springframework.util.StopWatch" %>
<%@ page import="java.util.HashMap" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //partDashboardv2.jsp
    //BOM 수배율 현황

    StopWatch sw = new StopWatch();
    sw.start();

    sw.stop();

    long millis = sw.getTotalTimeMillis();

    double seconds = millis / 1000.0;
    double minutes = seconds / 60.0;

    System.out.println("⏱ 수행 시간:");
    System.out.printf("   - %.3f 초%n", seconds);
    System.out.printf("   - %.3f 분%n", minutes);

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>JQPR 현황</title>


    <!-- Font Awesome -->
    <link rel="stylesheet" href="/resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="/resources/dist/plugins/select2/css/select2.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">



    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" rel="stylesheet">
    <%--<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">--%>


    <style>
        :root {
            --hyundai-blue: #003876;
            --hyundai-light-blue: #0066cc;
            --hyundai-gray: #f8f9fa;
            --hyundai-dark-gray: #6c757d;
            --hyundai-red: #dc3545;
            --hyundai-green: #28a745;
        }

        body {
            background-color: var(--hyundai-gray);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .header {
            background: linear-gradient(135deg, var(--hyundai-blue) 0%, var(--hyundai-light-blue) 100%);
            color: white;
            padding: 1.5rem 0;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }

        .header h1 {
            margin: 0;
            font-weight: 600;
            font-size: 2rem;
        }

        .stats-card {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            border-left: 4px solid var(--hyundai-light-blue);
            transition: transform 0.3s ease;
        }

        .stats-card:hover {
            transform: translateY(-5px);
        }

        .stats-number {
            font-size: 2.5rem;
            font-weight: 700;
            color: var(--hyundai-blue);
            margin: 0;
        }

        .stats-label {
            color: var(--hyundai-dark-gray);
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }

        .data-table {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .table-header {
            background: var(--hyundai-blue);
            color: white;
            padding: 1rem;
            border-radius: 12px 12px 0 0;
        }

        .table th {
            background: var(--hyundai-light-blue);
            color: white;
            border: none;
            font-weight: 600;
            padding: 1rem 0.75rem;
        }

        .table td {
            padding: 1rem 0.75rem;
            vertical-align: middle;
            border-color: #e9ecef;
        }

        .table tbody tr:hover {
            background-color: #f8f9ff;
        }

        .btn-hyundai {
            background: var(--hyundai-blue);
            border-color: var(--hyundai-blue);
            color: white;
            font-weight: 600;
            border-radius: 8px;
            padding: 0.5rem 1rem;
        }

        .btn-hyundai:hover {
            background: var(--hyundai-light-blue);
            border-color: var(--hyundai-light-blue);
            color: white;
        }

        .cost-high {
            color: var(--hyundai-red);
            font-weight: 600;
        }

        .cost-medium {
            color: #ffc107;
            font-weight: 600;
        }

        .cost-low {
            color: var(--hyundai-green);
            font-weight: 600;
        }

        .search-box {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .modal-header {
            background: var(--hyundai-blue);
            color: white;
            border-radius: 12px 12px 0 0;
        }

        .form-control:focus {
            border-color: var(--hyundai-light-blue);
            box-shadow: 0 0 0 0.2rem rgba(0, 102, 204, 0.25);
        }

        .pagination .page-link {
            color: var(--hyundai-blue);
        }

        .pagination .page-item.active .page-link {
            background-color: var(--hyundai-blue);
            border-color: var(--hyundai-blue);
        }


        .truncate-text {
            display: block;
            max-width: 300px;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            cursor: pointer;
        }


        /* td 자체에 적용하거나, td 안에 div/span을 넣고 적용 */
        td.truncated {
            /* max-width는 td 자체에 적용하기 어려울 수 있으므로, 내부 요소에 적용하는 것이 좋음 */
        }

        /* 커스텀 툴팁을 위한 CSS */
        .custom-tooltip {
            position: absolute;
            background-color: rgba(0, 0, 0, 0.85);
            color: #fff;
            padding: 8px 10px;
            border-radius: 6px;
            font-size: 13px;
            line-height: 1.4;
            max-width: 300px;
            white-space: pre-wrap; /* 줄바꿈(\n) 유지 */
            z-index: 1000;
            display: none;
        }
    </style>

</head>


<body class="hold-transition sidebar-mini text-sm" style="zoom:100%;">

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


    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true">
        <jsp:param name="menuType" value="dashboard" />
    </jsp:include>


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">

        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><i class="fas fa-building me-3"></i>JQPR 시스템</h1>
                        <p class="mb-0">엘리베이터 설치 현장 비용 관리 시스템</p>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <%--<li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>--%>

                            <div class="custom-control custom-switch custom-switch-off-danger custom-switch-on-success">
                                <input type="checkbox" class="custom-control-input" id="darkModeToggle">
                                <label class="custom-control-label" for="darkModeToggle">🌓 다크모드</label>
                            </div>
                        </ol>
                    </div>
                </div>
            </div><!-- /.container-fluid -->

            <div class="col-md-12">
                <div class="callout callout-danger">
                    <%--<i class="fas fa-bullhorn"></i> 🔊 도움말 <br>--%>
                    📢 도움말 <br>
                    <font color="red">- 집계 기준:  중국법인 자재 제외, 최신 릴리즈, 엘리베이터 자재(BlockNo 1,2,3) </font><br>

                    📝 사용 예시 <br>
                    - 자재번호 201153* 입력 시, 자재번호에 '201153'로 시작하는 모든 자재 조회하여 Excel 출력<br>
                    - *100325G02* 입력 시, 자재번호에 '100325G02' 포함된 모든 자재 출력 <br>
                </div>
            </div>
        </section>


        <%--<div class="container mt-4">--%>
        <div class="container-fluid">
            <div class="row mb-4">
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <p class="stats-number" id="totalCases">0</p>
                                <p class="stats-label">총 사례 수</p>
                            </div>
                            <i class="fas fa-clipboard-list fa-2x text-primary"></i>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <p class="stats-number" id="totalCost">₩0</p>
                                <p class="stats-label">총 비용</p>
                            </div>
                            <i class="fas fa-won-sign fa-2x text-danger"></i>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <p class="stats-number" id="avgCost">₩0</p>
                                <p class="stats-label">평균 비용</p>
                            </div>
                            <i class="fas fa-chart-bar fa-2x text-warning"></i>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <p class="stats-number" id="thisMonthCases">0</p>
                                <p class="stats-label">이번 달 사례</p>
                            </div>
                            <i class="fas fa-calendar-alt fa-2x text-success"></i>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row mb-4">
                <div class="col-md-3">
                    <div class="stats-card">
                        <h5 class="mb-3"><i class="fas fa-calendar me-2"></i>월별 분석 필터</h5>
                        <div class="mb-3">
                            <label class="form-label">분석 연도</label>
                            <select class="form-control" id="analysisYear" onchange="updateMonthlyAnalysis()">
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">분석 부서</label>
                            <select class="form-control" id="analysisTeam" onchange="updateMonthlyAnalysis()">
                                <option value="design">전체</option>
                                <option value="수배로직설계팀">수배로직설계팀</option>
                                <option value="중저속설계팀">중저속설계팀</option>
                                <option value="고속설계팀">고속설계팀</option>
                                <option value="양산개발PM팀">양산개발PM팀</option>
                                <option value="중저속SI팀">중저속SI팀</option>
                                <option value="고속SI팀">고속SI팀</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">분석 월</label>
                            <select class="form-control" id="analysisMonth" onchange="updateMonthlyAnalysis()">
                                <option value="">전체 월</option>
                                <option value="1">1월</option>
                                <option value="2">2월</option>
                                <option value="3">3월</option>
                                <option value="4">4월</option>
                                <option value="5">5월</option>
                                <option value="6">6월</option>
                                <option value="7">7월</option>
                                <option value="8">8월</option>
                                <option value="9">9월</option>
                                <option value="10">10월</option>
                                <option value="11">11월</option>
                                <option value="12">12월</option>
                            </select>
                        </div>
                        <div class="alert alert-info">
                            <strong id="monthlyStats"></strong>
                        </div>
                    </div>
                </div>
                <%--<div class="col-md-8">--%>
                <div class="col-md-6">
                    <div class="stats-card">
                        <h5 class="mb-3"><i class="fas fa-chart-line me-2"></i>월별 비용 추이</h5>
                        <canvas id="monthlyChart" height="100"></canvas>
                    </div>
                </div>

            </div>

            <div class="search-box">
                <div class="row">
                    <div class="col-md-3">
                        <label class="form-label">현장명 검색</label>
                        <input type="text" class="form-control" id="searchSite" placeholder="현장명을 입력하세요">
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">부서명</label>
                        <select class="form-control" id="searchTeam">
                            <option value="">전체</option>
                            <option value="수배로직설계팀">수배로직설계팀</option>
                            <option value="중저속설계팀">중저속설계팀</option>
                            <option value="고속설계팀">고속설계팀</option>
                            <option value="양산개발PM팀">양산개발PM팀</option>
                            <option value="중저속SI팀">중저속SI팀</option>
                            <option value="고속SI팀">고속SI팀</option>

                            <option value="권상시스템개발팀">권상시스템개발팀</option>
                            <option value="글로벌소싱팀">글로벌소싱팀</option>
                            <option value="글로벌출하팀">글로벌출하팀</option>
                            <option value="정보기술팀">정보기술팀</option>
                        </select>
                    </div>
                    <%--<div class="col-md-2">
                        <label class="form-label">발생자</label>
                        <input type="text" class="form-control" id="searchPerson" placeholder="발생자명">
                    </div>--%>
                    <div class="col-md-2">
                        <label class="form-label">상태</label>
                        <select class="form-control" id="searchState">
                            <option value="">전체</option>
                            <option value="종결완료">종결완료</option>
                            <option value="대책완료">대책완료</option>
                            <option value="접수완료">접수완료</option>
                            <option value="변상합의 발행">변상합의 발행</option>
                        </select>
                    </div>

                <%--<div class="col-md-2">
                        <label class="form-label">발생일자</label>
                        <input type="date" class="form-control" id="dateFilter">
                    </div>--%>
                    <div class="col-md-2">
                        <label class="form-label">검색 연도</label>
                        <select class="form-control" id="searchYear">
                            <option value="2025">2025</option>
                            <option value="2024">2024</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">검색 월</label>
                        <select class="form-control" id="searchMonth">
                            <option value="">전체 월</option>
                            <option value="01">1월</option>
                            <option value="02">2월</option>
                            <option value="03">3월</option>
                            <option value="04">4월</option>
                            <option value="05">5월</option>
                            <option value="06">6월</option>
                            <option value="07">7월</option>
                            <option value="08">8월</option>
                            <option value="09">9월</option>
                            <option value="10">10월</option>
                            <option value="11">11월</option>
                            <option value="12">12월</option>
                        </select>
                    </div>

                    <%--<div class="col-md-2">
                        <label class="form-label">기간 설정</label>
                        <select class="form-control" id="periodFilter" onchange="setDateFilterByPeriod()">
                            <option value="">전체 기간</option>
                            <option value="thisMonth">이번 달</option>
                            <option value="lastMonth">지난 달</option>
                            <option value="last3Months">최근 3개월</option>
                        </select>
                    </div>--%>
                    <div class="col-md-1">
                        <label class="form-label">&nbsp;</label>
                        <button class="btn btn-hyundai w-100" onclick="filterData()">
                            <i class="fas fa-search"></i>
                        </button>
                    </div>
                </div>
            </div>


            <div class="data-table">
                <div class="table-header">
                    <h5 class="mb-0"><i class="fas fa-table me-2"></i>JQPR 데이터 목록</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0" id="infoTable" style="zoom:95%;">
                        <thead>
                            <tr>
                                <th>JQPR번호</th>
                                <th>상태</th>
                                <th>프로젝트명</th>
                                <th>작성자</th>
                                <th>기계</th>
                                <th>전기</th>
                                <th>호기</th>
                                <th>작성일</th>

                                <th>문제점 제목</th>
                                <th>고장원인</th>
                                <th>상세</th>

                                <th>부서명1</th>
                                <th>부서명2</th>
                                <th>부서명3</th>

                                <th>실패비용</th>

                                <th>ITEM분류명</th>
                            </tr>
                        </thead>
                        <tbody id="contentTable">

                        </tbody>
                    </table>
                </div>
            </div>

            <nav class="mt-4">
                <ul class="pagination justify-content-center" id="pagination">
                </ul>
            </nav>
        </div>

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


<div class="modal fade" id="detailModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="fas fa-info-circle me-2"></i>JQPR 상세 정보</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="detailContent">
            </div>
        </div>
    </div>
</div>


<div id="custom-tooltip"></div>

<!-- 툴팁 엘리먼트 (공용) -->
<div id="tooltip" class="custom-tooltip"></div>


</body>

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script src="/resources/javascript/commonUtil.js"></script>
<script src="/resources/javascript/jqpr.js"></script>
<script src="/resources/javascript/StringUtil.js"></script>


<!-- AdminLTE App -->
<script src="/resources/dist/js/adminlte.min.js"></script>


<!-- Bootstrap 4 -->
<%--<script src="/resources/dist/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>--%>
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

<script>
    $(document).ready(function() {


        document.querySelectorAll(".has-custom-tooltip .truncate-text").forEach(el => {
            el.addEventListener("mouseenter", (e) => {
                const fullText = e.target.getAttribute("data-full-text");
                tooltip.textContent = fullText;
                tooltip.style.display = "block";
                tooltip.style.left = (e.pageX + 10) + "px";
                tooltip.style.top = (e.pageY + 10) + "px";
            });

            el.addEventListener("mousemove", (e) => {
                tooltip.style.left = (e.pageX + 10) + "px";
                tooltip.style.top = (e.pageY + 10) + "px";
            });

            el.addEventListener("mouseleave", () => {
                tooltip.style.display = "none";
            });
        });



    }) // end document ready
</script>

</html>
