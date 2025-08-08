<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.kyhslam.service.SubaeService" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.kyhslam.util.UtilCommonAPI" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //bomSubaeDashboard.jsp
    //BOM 수배율 현황

    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // 원하는 Bean 가져오기
    SubaeService subaeService = (SubaeService) context.getBean("SubaeService");

    ArrayList<HashMap<String, String>> topInfoList = subaeService.findTopModPartNo();

    String allProductCnt = subaeService.findALLProductCount();
    String allPartCnt = subaeService.findALLPartCount();
    String allPartModCnt = subaeService.findALLPartModCount();

    //PCOUNT=5654, PARTNO=VC011636G010A, PARTNAME=RELEASE CABLE},


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>BOM 수배율 현황</title>


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
            font-family: 'NotoSans', 'Cascadia Code', sans-serif;
        }

        :root {
            --blue: #007bff;
            --indigo: #6610f2;
            --purple: #6f42c1;
            --pink: #e83e8c;
            --red: #dc3545;
            --orange: #fd7e14;
            --yellow: #ffc107;
            --green: #28a745;
            --teal: #20c997;
            --cyan: #17a2b8;
            --white: #fff;
            --gray: #6c757d;
            --gray-dark: #343a40;
            --primary: #007bff;
            --secondary: #6c757d;
            --success: #28a745;
            --info: #17a2b8;
            --warning: #ffc107;
            --danger: #dc3545;
            --light: #f8f9fa;
            --dark: #343a40;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f6f9;
            min-height: 100vh;
            color: #333;
        }

        .header {
            background-color: var(--dark);
            color: white;
            padding: 20px 0;
            box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .header h1 {
            font-size: 2.2rem;
            font-weight: 400;
            text-align: center;
            margin-bottom: 5px;
        }

        .header p {
            text-align: center;
            font-size: 0.95rem;
            opacity: 0.9;
        }

        .main-content {
            padding: 20px 0;
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: 1fr 2fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }

        .summary-cards {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
        }

        .card {
            background: white;
            border-radius: 0.25rem;
            padding: 20px;
            box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
            transition: all 0.2s ease-in-out;
            border-top: 3px solid var(--primary);
        }

        .card:hover {
            box-shadow: 0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22);
            transform: translateY(-3px);
        }

        .card h3 {
            color: #1f2d3d;
            font-size: 1rem;
            margin-bottom: 10px;
            font-weight: 600;
        }

        .card-value {
            font-size: 1.8rem;
            font-weight: 700;
            color: #333;
            margin-bottom: 5px;
        }

        .card-label {
            color: #6c757d;
            font-size: 0.8rem;
        }

        .chart-container {
            background: white;
            border-radius: 0.25rem;
            padding: 20px;
            box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
            border-top: 3px solid var(--info);
        }

        .chart-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
        }

        /*
        .chart-placeholder {
            height: 280px;
            background: #e9ecef;
            border-radius: 0.25rem;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
            font-size: 1rem;
            border: 1px dashed #d2d6de;
        }
        */

        /* Top 10 변경자재 스타일 */
        .top10-container {
            background: white;
            border-radius: 0.25rem;
            padding: 20px;
            box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
            border-top: 3px solid var(--warning);
        }

        .top10-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .top10-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .top10-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
            font-size: 0.85rem;
        }

        .top10-item:last-child {
            border-bottom: none;
        }

        .top10-rank {
            width: 25px;
            height: 25px;
            border-radius: 50%;
            background: var(--primary);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 0.75rem;
            flex-shrink: 0;
        }

        .top10-rank.rank-1 {
            background: #FFD700;
            color: #333;
        }

        .top10-rank.rank-2 {
            background: #C0C0C0;
            color: #333;
        }

        .top10-rank.rank-3 {
            background: #CD7F32;
            color: white;
        }

        .top10-part-info {
            flex: 1;
            margin: 0 10px;
            overflow: hidden;
        }

        .top10-part-name {
            font-weight: 600;
            color: #333;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .top10-part-code {
            color: #6c757d;
            font-size: 0.75rem;
            margin-top: 2px;
        }

        .top10-count {
            font-weight: 600;
            color: var(--danger);
            background: rgba(220, 53, 69, 0.1);
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.75rem;
        }

        .table-container {
            background: white;
            border-radius: 0.25rem;
            padding: 20px;
            box-shadow: 0 0 1px rgba(0,0,0,.125), 0 1px 3px rgba(0,0,0,.2);
            overflow-x: auto;
            border-top: 3px solid var(--success);
        }

        .table-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .table-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
        }

        .header-controls {
            display: flex;
            gap: 15px;
            align-items: center;
        }

        .month-selector {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .month-selector label {
            font-weight: 600;
            color: #333;
            font-size: 0.9rem;
        }

        .month-selector select {
            padding: 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            font-size: 0.9rem;
            background: white;
            color: #495057;
            cursor: pointer;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        }

        .month-selector select:focus {
            outline: none;
            border-color: #80bdff;
            box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
        }

        .search-box {
            position: relative;
        }

        .search-box input {
            padding: 8px 35px 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            font-size: 0.9rem;
            width: 200px;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
            color: #495057;
        }

        .search-box input:focus {
            outline: none;
            border-color: #80bdff;
            box-shadow: 0 0 0 0.2rem rgba(0,123,255,.25);
        }

        .search-box::after {
            content: '\f002';
            font-family: 'Font Awesome 5 Free';
            font-weight: 900;
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .data-table th,
        .data-table td {
            padding: 12px 10px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        .data-table th {
            background-color: var(--secondary);
            color: white;
            font-weight: 600;
            font-size: 0.9rem;
            white-space: nowrap;
        }

        .data-table thead tr:first-child th:first-child {
            border-top-left-radius: 0.25rem;
        }

        .data-table thead tr:first-child th:last-child {
            border-top-right-radius: 0.25rem;
        }

        .data-table tr:hover {
            background-color: rgba(0, 0, 0, 0.03);
        }

        .status-badge {
            display: inline-block;
            padding: 0.35em 0.6em;
            font-size: 75%;
            font-weight: 700;
            line-height: 1;
            text-align: center;
            white-space: nowrap;
            vertical-align: baseline;
            border-radius: 0.25rem;
        }

        .status-excellent {
            color: #fff;
            background-color: var(--success);
        }

        .status-good {
            color: #fff;
            background-color: var(--info);
        }

        .status-normal {
            color: #1f2d3d;
            background-color: var(--warning);
        }

        .status-poor {
            color: #fff;
            background-color: var(--danger);
        }

        .progress-bar {
            width: 100%;
            height: 6px;
            background-color: #e9ecef;
            border-radius: 0.25rem;
            overflow: hidden;
            margin-top: 3px;
        }

        .progress-fill {
            height: 100%;
            background-color: var(--primary);
            transition: width 0.3s ease;
        }

        .filter-buttons {
            display: flex;
            gap: 8px;
            margin-bottom: 15px;
        }

        .filter-btn {
            padding: 6px 12px;
            border: 1px solid var(--primary);
            background: white;
            color: var(--primary);
            border-radius: 0.25rem;
            cursor: pointer;
            transition: all 0.15s ease-in-out;
            font-size: 0.85rem;
        }

        .filter-btn:hover,
        .filter-btn.active {
            background: var(--primary);
            color: white;
        }

        .modification-items {
            display: flex;
            gap: 6px;
            flex-wrap: wrap;
        }

        .mod-item {
            background: #f8f9fa;
            padding: 2px 7px;
            border-radius: 0.2rem;
            font-size: 0.75rem;
            border: 1px solid #dee2e6;
            color: #495057;
        }

        .mod-item.modified {
            background: #fdf7df;
            border-color: #ffeeba;
            color: #856404;
        }

        /* Responsive adjustments */
        @media (max-width: 1200px) {
            .dashboard-grid {
                grid-template-columns: 1fr 2fr;
            }

            .top10-container {
                grid-column: 1 / -1;
                margin-top: 20px;
            }
        }

        @media (max-width: 768px) {
            .dashboard-grid {
                grid-template-columns: 1fr;
            }

            .summary-cards {
                grid-template-columns: 1fr;
            }

            .header-controls {
                flex-direction: column;
                gap: 10px;
                align-items: stretch;
            }

            .search-box input {
                width: 100%;
            }

            .data-table th,
            .data-table td {
                padding: 10px 8px;
                font-size: 0.85rem;
            }

            .filter-btn {
                font-size: 0.8rem;
                padding: 5px 10px;
            }
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


    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true">
        <jsp:param name="menuType" value="dashboard" />
    </jsp:include>


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">

        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>BOM 수배율 현황 (2025년) </h1>
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


        <section class="content" style="zoom:95%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <div class="row">
                    <div class="col-12">

            <div class="dashboard-grid">
                <div class="summary-cards">
                    <div class="card">
                        <h3>총 호기 수</h3>
                        <div class="card-value" id="totalProducts"><%=UtilCommonAPI.formatNumberWithCommas(allProductCnt) %></div>
                        <div class="card-label">호기</div>
                    </div>
                    <div class="card">
                        <h3>총 수배 자재</h3>
                        <div class="card-value" id="avgRate"><%= UtilCommonAPI.formatNumberWithCommas(allPartCnt) %></div>
                        <div class="card-label">개</div>
                    </div>
                    <%--평균수배율, 완료율--%>
                    <div class="card">
                        <h3>총 수정 자재</h3>
                        <div class="card-value" id="modifiedParts"><%=UtilCommonAPI.formatNumberWithCommas(allPartModCnt) %></div>
                        <div class="card-label">개</div>
                    </div>
                    <div class="card">
                        <h3>-</h3>
                        <div class="card-value" id="completionRate">-</div>
                        <div class="card-label">-</div>
                    </div>
                </div>

                <div class="chart-container">
                    <div class="chart-title">📊 월별 집계 호기</div>
                   <%-- <figure class="highcharts-figure">--%>
                        <div class="chart-placeholder" id="cpContainer">
                            <%--📊 차트 영역 (Chart.js 또는 다른 차트 라이브러리 연동)--%>
                        </div>
                    <%--</figure>--%>
                </div>

                <!-- Top 10 변경자재 영역 -->
                <div class="top10-container">
                    <div class="top10-title">
                        🔧 Top 10 변경자재 (2025년)
                    </div>
                    <ul class="top10-list" id="top10List">

                        <%
                            for (int i=0; i < topInfoList.size(); i++) {
                                HashMap<String, String> o = topInfoList.get(i);

                                String cssVal  ="";
                                if(i == 0) cssVal = "rank-1";
                                if(i == 1) cssVal = "rank-2";
                                if(i == 2) cssVal = "rank-3";

                                String valCount = UtilCommonAPI.formatNumberWithCommas(String.valueOf(o.get("PCOUNT")));

                        %>
                        <li class="top10-item">
                            <div class="top10-rank <%=cssVal%>"><%=(i+1)%></div>
                            <div class="top10-part-info">
                                <div class="top10-part-name"><%=o.get("PARTNAME")%></div>
                                <div class="top10-part-code"><%=o.get("PARTNO")%></div>
                            </div>
                            <div class="top10-count"><%= valCount %>건</div>
                        </li>

                        <%

                            }

                        %>


<%--
                        <li class="top10-item">
                            <div class="top10-rank rank-1">1</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">모터 커플링</div>
                                <div class="top10-part-code">MC-4501A</div>
                            </div>
                            <div class="top10-count">23건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank rank-2">2</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">와이어 로프</div>
                                <div class="top10-part-code">WR-8MM</div>
                            </div>
                            <div class="top10-count">19건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank rank-3">3</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">도어 센서</div>
                                <div class="top10-part-code">DS-301B</div>
                            </div>
                            <div class="top10-count">17건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">4</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">제어판넬</div>
                                <div class="top10-part-code">CP-450</div>
                            </div>
                            <div class="top10-count">15건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">5</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">가이드 레일</div>
                                <div class="top10-part-code">GR-T16</div>
                            </div>
                            <div class="top10-count">12건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">6</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">안전장치</div>
                                <div class="top10-part-code">SF-203</div>
                            </div>
                            <div class="top10-count">11건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">7</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">버튼 스위치</div>
                                <div class="top10-part-code">BS-101</div>
                            </div>
                            <div class="top10-count">9건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">8</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">브레이크 패드</div>
                                <div class="top10-part-code">BP-450A</div>
                            </div>
                            <div class="top10-count">8건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">9</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">전원 케이블</div>
                                <div class="top10-part-code">PC-3X25</div>
                            </div>
                            <div class="top10-count">7건</div>
                        </li>
                        <li class="top10-item">
                            <div class="top10-rank">10</div>
                            <div class="top10-part-info">
                                <div class="top10-part-name">LED 표시등</div>
                                <div class="top10-part-code">LED-24V</div>
                            </div>
                            <div class="top10-count">6건</div>
                        </li>--%>
                    </ul>
                </div>


            </div>

                    </div>
                </div>
            </div>
        </section>


        <!-- Content Header (Page header) -->
        <%--<section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <!-- <h1>부품공용화 - 월별실적 Dashboard <font color="red">(2024/11/17, 06:00기준)</font> </h1> -->
                        <h1>엘리베이터 제품별 BOM 현황 </h1>
                    </div>
                    <div class="col-sm-6">
                        &lt;%&ndash;<ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>
                        </ol>&ndash;%&gt;
                    </div>
                </div>
            </div><
        </section>--%>



        <!-- Main content -->
        <section class="content" style="zoom:100%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <div class="row">
                    <div class="col-12">
                        <!-- <div class="col-lg-7"> -->
                        <!-- <section class="col-lg-6 connectedSortable ui-sortable"> -->

                        <div class="card card-primary">

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:100%;">
                                <%--<div class="table-container">--%>
                                    <div class="table-header">
                                        <div class="table-title">
                                            <h3>엘리베이터 부품별 현황</h3>
                                        </div>

                                        <div class="header-controls">
                                            <div class="month-selector">
                                                <label for="monthSelect">분석 기간:</label>
                                                <select id="monthSelect">
                                                    <option value="all">전체</option>
                                                    <option id="2025-01" value="2025-01">2025년 1월</option>
                                                    <option id="2025-02" value="2025-02">2025년 2월</option>
                                                    <option id="2025-03" value="2025-03">2025년 3월</option>
                                                    <option id="2025-04" value="2025-04">2025년 4월</option>
                                                    <option id="2025-05" value="2025-05">2025년 5월</option>
                                                    <option id="2025-06" value="2025-06">2025년 6월</option>
                                                    <option id="2025-07" value="2025-07">2025년 7월</option>
                                                    <option id="2025-08" value="2025-08" selected>2025년 8월</option>
                                                </select>
                                            </div>
                                            <%--<div class="search-box">
                                                <input type="text" id="searchInput" placeholder="호기번호 또는 제품명 검색">
                                            </div>--%>
                                        </div>
                                    </div>

                                    <div class="filter-buttons float-right">
                                        <%--<button class="filter-btn active" data-filter="all">전체</button>--%>
                                        <button class="filter-btn" data-filter="excellent" id="excel_all">자재전체 Excel 출력</button>

                                        <button class="filter-btn" data-filter="good" id="excelGo">제품 Excel 출력</button>
                                        <button class="filter-btn" data-filter="normal" id="excel_mod">변경자재 Excel 출력</button>
                                        <%--<button class="filter-btn" data-filter="poor">개선필요</button>--%>

                                        <%--<button class="filter-btn" data-filter="good">제품</button>
                                        <button class="filter-btn" data-filter="normal">보통</button>
                                        <button class="filter-btn" data-filter="poor">개선필요</button>--%>
                                    </div>

                                    <table class="data-table" id="infoTable">
                                        <thead>
                                            <tr>
                                                <th>호기번호</th>
                                                <th>버전</th>
                                                <th>수주명</th>
                                                <th>기종</th>
                                                <th>최종설계일<br>(승인일)</th>

                                                <th>총 수배 건수</th>
                                                <th>변경 건수</th>

                                                <th>수정 부품 (기계/전기) </th>
                                                <th>내용확인 </th>
                                                <th>자동수배율</th>
                                                <th>기계</th>
                                                <th>전기</th>
                                            </tr>
                                        </thead>

                                        <tbody id="contentTable">

                                        </tbody>
                                    </table>
                                <%--</div>--%> <!-- table container -->
                            </div>
                            <!-- /.card-body -->
                        </div>
                        <!-- /.card -->
                    </div>
                    <!-- </section> -->
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
        <strong>Copyright &copy; 2025 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>


</body>

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


<script src="/resources/dist/plugins/select2/js/select2.full.min.js"></script>

<script src="/resources/dist/plugins/datatables-buttons/js/dataTables.buttons.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.bootstrap4.min.js"></script>
<script src="/resources/dist/plugins/jszip/jszip.min.js"></script>
<script src="/resources/dist/plugins/pdfmake/pdfmake.min.js"></script>
<script src="/resources/dist/plugins/pdfmake/vfs_fonts.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.html5.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.print.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.colVis.min.js"></script>

<!-- Highhart -->
<script src="/resources/dist/js/highcharts.js"></script>
<script src="/resources/dist/js/exporting.js"></script>
<script src="/resources/dist/js/export-data.js"></script>
<script src="/resources/dist/js/accessibility.js"></script>


<script src="/resources/javascript/bomSubaeDashboard.js"></script>

<script>
    // 샘플 데이터 (더 많은 월별 데이터 추가)
    const elevatorData = [
        // 2024년 4월 데이터
        {
            id: 'ELV-001',
            name: '승용 엘리베이터 A타입',
            designDate: '2024-04-15',
            totalParts: 450,
            modifiedParts: { M: 12, C: 8, A: 5, B: 3, D: 2 },
            autoRate: 95.2,
            progress: 98,
            status: 'excellent'
        },
        {
            id: 'ELV-002',
            name: '화물 엘리베이터 B타입',
            designDate: '2024-04-20',
            totalParts: 680,
            modifiedParts: { M: 18, C: 12, A: 8, B: 6, D: 4 },
            autoRate: 87.3,
            progress: 85,
            status: 'good'
        },
        {
            id: 'ELV-003',
            name: '승용 엘리베이터 C타입',
            designDate: '2024-04-25',
            totalParts: 520,
            modifiedParts: { M: 25, C: 15, A: 10, B: 8, D: 5 },
            autoRate: 78.5,
            progress: 72,
            status: 'normal'
        },
        // 2024년 5월 데이터
        {
            id: 'ELV-004',
            name: '고속 엘리베이터 D타입',
            designDate: '2024-05-01',
            totalParts: 750,
            modifiedParts: { M: 35, C: 22, A: 15, B: 12, D: 8 },
            autoRate: 65.8,
            progress: 58,
            status: 'poor'
        },
        {
            id: 'ELV-005',
            name: '승용 엘리베이터 E타입',
            designDate: '2024-05-05',
            totalParts: 480,
            modifiedParts: { M: 10, C: 6, A: 4, B: 2, D: 1 },
            autoRate: 92.1,
            progress: 94,
            status: 'excellent'
        },
        {
            id: 'ELV-006',
            name: '화물 엘리베이터 F타입',
            designDate: '2024-05-10',
            totalParts: 620,
            modifiedParts: { M: 20, C: 14, A: 9, B: 7, D: 3 },
            autoRate: 83.7,
            progress: 79,
            status: 'good'
        },
        {
            id: 'ELV-007',
            name: '승용 엘리베이터 G타입',
            designDate: '2024-05-15',
            totalParts: 540,
            modifiedParts: { M: 15, C: 9, A: 6, B: 4, D: 2 },
            autoRate: 89.4,
            progress: 88,
            status: 'good'
        },
        // 2024년 6월 데이터
        {
            id: 'ELV-008',
            name: '화물 엘리베이터 H타입',
            designDate: '2024-06-01',
            totalParts: 710,
            modifiedParts: { M: 28, C: 18, A: 12, B: 9, D: 6 },
            autoRate: 76.2,
            progress: 75,
            status: 'normal'
        },
        {
            id: 'ELV-009',
            name: '승용 엘리베이터 I타입',
            designDate: '2024-06-08',
            totalParts: 465,
            modifiedParts: { M: 8, C: 5, A: 3, B: 2, D: 1 },
            autoRate: 96.8,
            progress: 99,
            status: 'excellent'
        },
        {
            id: 'ELV-010',
            name: '고속 엘리베이터 J타입',
            designDate: '2024-06-15',
            totalParts: 820,
            modifiedParts: { M: 42, C: 28, A: 18, B: 14, D: 9 },
            autoRate: 62.1,
            progress: 55,
            status: 'poor'
        },
        {
            id: 'ELV-011',
            name: '승용 엘리베이터 K타입',
            designDate: '2024-06-22',
            totalParts: 495,
            modifiedParts: { M: 13, C: 8, A: 5, B: 3, D: 2 },
            autoRate: 91.7,
            progress: 92,
            status: 'excellent'
        },
        // 2024년 7월 데이터
        {
            id: 'ELV-012',
            name: '화물 엘리베이터 L타입',
            designDate: '2024-07-05',
            totalParts: 650,
            modifiedParts: { M: 22, C: 16, A: 10, B: 8, D: 4 },
            autoRate: 81.5,
            progress: 82,
            status: 'good'
        },
        {
            id: 'ELV-013',
            name: '승용 엘리베이터 M타입',
            designDate: '2024-07-12',
            totalParts: 510,
            modifiedParts: { M: 17, C: 11, A: 7, B: 5, D: 3 },
            autoRate: 85.9,
            progress: 86,
            status: 'good'
        }
    ];

    // 상태별 스타일 매핑
    const statusStyles = {
        excellent: 'status-excellent',
        good: 'status-good',
        normal: 'status-normal',
        poor: 'status-poor'
    };

    const statusLabels = {
        excellent: '우수',
        good: '양호',
        normal: '보통',
        poor: '개선필요'
    };

    // 테이블 렌더링
    function renderTable(data = elevatorData) {
        const tbody = $('#tableBody');
        tbody.empty();

        data.forEach(item => {
            const modifiedPartsHtml = Object.entries(item.modifiedParts)
                .map(([key, value]) => `<span class="mod-item ${value > 10 ? 'modified' : ''}">${key}:${value}</span>`)
                .join('');

            const row = `
                    <tr data-status="${item.status}">
                        <td><strong>${item.id}</strong></td>
                        <td>${item.name}</td>
                        <td>${item.designDate}</td>
                        <td>${item.totalParts.toLocaleString()}</td>
                        <td>
                            <div class="modification-items">
                                ${modifiedPartsHtml}
                            </div>
                        </td>
                        <td>${item.autoRate}%</td>
                        <td>
                            <div>${item.progress}%</div>
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: ${item.progress}%"></div>
                            </div>
                        </td>
                        <td>
                            <span class="status-badge ${statusStyles[item.status]}">
                                ${statusLabels[item.status]}
                            </span>
                        </td>
                    </tr>
                `;

            console.log(row);
            tbody.append(row);
        });
    }

    // 검색 기능
    $('#searchInput').on('input', function() {
        const searchTerm = $(this).val().toLowerCase();
        const filteredData = elevatorData.filter(item =>
            item.id.toLowerCase().includes(searchTerm) ||
            item.name.toLowerCase().includes(searchTerm)
        );
        renderTable(filteredData);
    });

    // 필터 기능
    $('.filter-btn').on('click', function() {
        $('.filter-btn').removeClass('active');
        $(this).addClass('active');

        const filterType = $(this).data('filter');

        if (filterType === 'all') {
            renderTable(elevatorData);
        } else {
            const filteredData = elevatorData.filter(item => item.status === filterType);
            renderTable(filteredData);
        }
    });

    // 요약 카드 업데이트
    function updateSummaryCards() {
        const totalProducts = elevatorData.length;
        const avgRate = (elevatorData.reduce((sum, item) => sum + item.autoRate, 0) / totalProducts).toFixed(1);
        const modifiedParts = elevatorData.reduce((sum, item) =>
            sum + Object.values(item.modifiedParts).reduce((a, b) => a + b, 0), 0);
        const completionRate = (elevatorData.reduce((sum, item) => sum + item.progress, 0) / totalProducts).toFixed(1);

        $('#totalProducts').text(totalProducts);
        $('#avgRate').text(`${avgRate}%`);
        $('#modifiedParts').text(modifiedParts);
        $('#completionRate').text(`${completionRate}%`);
    }





</script>

</html>
