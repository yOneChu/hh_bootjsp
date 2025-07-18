<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //partDashboardv2.jsp
    //BOM 수배율 현황


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



    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">

    <style>
        body {
            font-family: 'NotoSans', 'Cascadia Code', sans-serif;
        }

        :root {
            --hyundai-blue: #003c71;
            --hyundai-light-blue: #0066cc;
            --hyundai-gray: #6c757d;
            --hyundai-light-gray: #f8f9fa;
            --hyundai-dark-gray: #343a40;
            --hyundai-accent: #0066cc;
            --hyundai-success: #28a745;
            --hyundai-warning: #ffc107;
            --hyundai-danger: #dc3545;
            --card-shadow: 0 2px 10px rgba(0, 60, 113, 0.1);
            --border-color: #e9ecef;
        }

        * {
            font-family: 'Noto Sans KR', sans-serif;
        }

        body {
            background-color: #f5f6f8;
            color: var(--hyundai-dark-gray);
            line-height: 1.6;
        }

        .navbar-custom {
            background: linear-gradient(135deg, var(--hyundai-blue) 0%, var(--hyundai-light-blue) 100%);
            padding: 1rem 0;
            box-shadow: 0 2px 20px rgba(0, 60, 113, 0.15);
        }

        .main-header {
            background: linear-gradient(135deg, var(--hyundai-blue) 0%, var(--hyundai-light-blue) 100%);
            color: white;
            padding: 3rem 0 2rem 0;
            position: relative;
            overflow: hidden;
        }

        .main-header::before {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            width: 100%;
            height: 100%;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
            opacity: 0.3;
        }

        .header-content {
            position: relative;
            z-index: 2;
        }

        .card-hyundai {
            background: white;
            border: none;
            border-radius: 8px;
            box-shadow: var(--card-shadow);
            transition: all 0.3s ease;
            margin-bottom: 1.5rem;
        }

        .card-hyundai:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 20px rgba(0, 60, 113, 0.15);
        }

        .card-header-hyundai {
            background: white;
            border-bottom: 2px solid var(--hyundai-light-gray);
            padding: 1.5rem;
            border-radius: 8px 8px 0 0 !important;
        }

        .card-header-hyundai h5 {
            color: var(--hyundai-blue);
            font-weight: 600;
            margin: 0;
        }

        .stat-card-hyundai {
            background: white;
            border: none;
            border-radius: 8px;
            box-shadow: var(--card-shadow);
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .stat-card-hyundai::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: var(--hyundai-accent);
        }

        .stat-number-hyundai {
            font-size: 2.2rem;
            font-weight: 700;
            color: var(--hyundai-blue);
            margin: 0.5rem 0;
        }

        .stat-label {
            color: var(--hyundai-gray);
            font-size: 0.9rem;
            font-weight: 500;
        }

        .trend-indicator {
            padding: 0.25rem 0.5rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .trend-up {
            background-color: rgba(40, 167, 69, 0.1);
            color: var(--hyundai-success);
        }

        .trend-down {
            background-color: rgba(220, 53, 69, 0.1);
            color: var(--hyundai-danger);
        }

        .trend-stable {
            background-color: rgba(108, 117, 125, 0.1);
            color: var(--hyundai-gray);
        }

        .btn-hyundai {
            background: var(--hyundai-blue);
            border: none;
            color: white;
            padding: 0.6rem 1.5rem;
            border-radius: 4px;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .btn-hyundai:hover {
            background: var(--hyundai-light-blue);
            color: white;
            transform: translateY(-1px);
        }

        .btn-outline-hyundai {
            border: 1px solid var(--hyundai-blue);
            color: var(--hyundai-blue);
            background: transparent;
            padding: 0.6rem 1.5rem;
            border-radius: 4px;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .btn-outline-hyundai:hover {
            background: var(--hyundai-blue);
            color: white;
        }

        .progress-hyundai {
            height: 6px;
            background-color: var(--hyundai-light-gray);
            border-radius: 3px;
            overflow: hidden;
        }

        .progress-bar-hyundai {
            background: linear-gradient(90deg, var(--hyundai-blue) 0%, var(--hyundai-light-blue) 100%);
            transition: width 2s ease-in-out;
        }

        .material-item-hyundai {
            background: white;
            border: 1px solid var(--border-color);
            border-radius: 6px;
            padding: 1rem;
            margin-bottom: 0.75rem;
            transition: all 0.3s ease;
        }

        .material-item-hyundai:hover {
            border-color: var(--hyundai-accent);
            box-shadow: 0 2px 8px rgba(0, 60, 113, 0.1);
        }

        .material-code-hyundai {
            background: var(--hyundai-light-gray);
            color: var(--hyundai-blue);
            padding: 0.3rem 0.6rem;
            border-radius: 4px;
            font-family: 'Courier New', monospace;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .chart-container-hyundai {
            background: white;
            border-radius: 8px;
            padding: 2rem;
            height: 350px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 1px solid var(--border-color);
        }

        .filter-section-hyundai {
            background: white;
            border-radius: 8px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: var(--card-shadow);
        }

        .form-control-hyundai {
            border: 1px solid var(--border-color);
            border-radius: 4px;
            padding: 0.6rem 1rem;
            font-size: 0.9rem;
        }

        .form-control-hyundai:focus {
            border-color: var(--hyundai-accent);
            box-shadow: 0 0 0 0.2rem rgba(0, 102, 204, 0.25);
        }

        .alert-hyundai {
            border: none;
            border-radius: 6px;
            border-left: 4px solid;
        }

        .alert-warning-hyundai {
            background-color: rgba(255, 193, 7, 0.1);
            border-left-color: var(--hyundai-warning);
            color: #856404;
        }

        .alert-info-hyundai {
            background-color: rgba(0, 102, 204, 0.1);
            border-left-color: var(--hyundai-accent);
            color: var(--hyundai-blue);
        }

        .alert-success-hyundai {
            background-color: rgba(40, 167, 69, 0.1);
            border-left-color: var(--hyundai-success);
            color: #155724;
        }

        .section-title {
            color: var(--hyundai-blue);
            font-weight: 600;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid var(--hyundai-light-gray);
        }

        .metric-circle {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            font-weight: 700;
            color: white;
            margin: 0 auto 1rem auto;
        }

        .metric-circle.blue { background: var(--hyundai-blue); }
        .metric-circle.light-blue { background: var(--hyundai-light-blue); }
        .metric-circle.success { background: var(--hyundai-success); }
        .metric-circle.warning { background: var(--hyundai-warning); }

        .breadcrumb-hyundai {
            background: transparent;
            padding: 0;
            margin-bottom: 1rem;
        }

        .breadcrumb-hyundai .breadcrumb-item {
            color: rgba(255, 255, 255, 0.8);
        }

        .breadcrumb-hyundai .breadcrumb-item.active {
            color: white;
        }

        .status-indicator {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            display: inline-block;
            margin-right: 0.5rem;
        }

        .status-active { background-color: var(--hyundai-success); }
        .status-inactive { background-color: var(--hyundai-gray); }
        .status-warning { background-color: var(--hyundai-warning); }

        @media (max-width: 768px) {
            .main-header {
                padding: 2rem 0 1.5rem 0;
            }

            .stat-number-hyundai {
                font-size: 1.8rem;
            }
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
                        <h1>BOM 수배율 현황 </h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>
                        </ol>
                    </div>
                </div>
            </div><!-- /.container-fluid -->

            <div class="col-md-12">
                <div class="callout callout-danger">
                    <%--<i class="fas fa-bullhorn"></i> 🔊 도움말 <br>--%>
                         🔊 도움말 <br>
                    - 자재번호 201153* 검색 시, '201153'이 포함된 모든 자재 조회
                </div>
            </div>
        </section>



            <div class="container mt-4">
                <div class="filter-section-hyundai">
                    <div class="row g-3 align-items-center">
                        <div class="col-md-3">
                            <label class="form-label small text-muted">자재번호</label>
                            <input type="text" id="partNo" class="form-control form-control-hyundai" placeholder="자재 코드 또는 명칭">
                        </div>

                        <div class="col-md-3">
                            <label class="form-label small text-muted">자재명</label>
                            <input type="text" id="partName" class="form-control form-control-hyundai" placeholder="자재 코드 또는 명칭">
                        </div>

                        <div class="col-md-2">
                            <label class="form-label small text-muted">카테고리</label>
                            <select class="form-select form-control-hyundai">
                                <option>전체</option>
                                <option>전자부품</option>
                                <option>기계부품</option>
                                <option>원자재</option>
                                <option>소모품</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label small text-muted">기간</label>
                            <select class="form-select form-control-hyundai">
                                <option value="2025">2025년</option>
                                <option value="2024">2024년</option>
                                <option value="2023">2023년</option>
                                <option value="2024">2022년</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label small text-muted">상태</label>
                            <select class="form-select form-control-hyundai">
                                <option>전체 자재</option>
                                <option value="ACTIVE">활성 자재</option>
                                <option value="INACTIVE">비활성 자재</option>
                            </select>
                        </div>
                        <div class="col-md-2 text-end">
                            <label class="form-label small text-muted d-block">&nbsp;</label>
                            <button class="btn btn-hyundai w-100" id="partExcel">
                                <i class="bi bi-search me-1"></i>EXCEL 다운로드
                            </button>
                        </div>
                    </div>
                </div>

                <div class="row g-4 mb-4">
                    <div class="col-xl-3 col-md-6">
                        <div class="card stat-card-hyundai">
                            <div class="card-body p-4">
                                <div class="d-flex align-items-center">
                                    <div class="flex-grow-1">
                                        <p class="stat-label mb-1">총 자재 수</p>
                                        <h3 class="stat-number-hyundai">2,847</h3>
                                        <span class="trend-indicator trend-up">
                                        <i class="bi bi-arrow-up-short"></i>12.5%
                                    </span>
                                    </div>
                                    <div class="metric-circle blue">
                                        <i class="bi bi-box-seam"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="card stat-card-hyundai">
                            <div class="card-body p-4">
                                <div class="d-flex align-items-center">
                                    <div class="flex-grow-1">
                                        <p class="stat-label mb-1">활성 자재</p>
                                        <h3 class="stat-number-hyundai">1,924</h3>
                                        <span class="trend-indicator trend-up">
                                        <i class="bi bi-arrow-up-short"></i>8.3%
                                    </span>
                                    </div>
                                    <div class="metric-circle light-blue">
                                        <i class="bi bi-activity"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="card stat-card-hyundai">
                            <div class="card-body p-4">
                                <div class="d-flex align-items-center">
                                    <div class="flex-grow-1">
                                        <p class="stat-label mb-1">비활성</p>
                                        <h3 class="stat-number-hyundai">47</h3>
                                        <span class="trend-indicator trend-down">
                                        <i class="bi bi-arrow-down-short"></i>15.3%
                                    </span>
                                    </div>
                                    <div class="metric-circle warning">
                                        <i class="bi bi-exclamation-triangle"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>


                    <div class="col-xl-3 col-md-6">
                        <div class="card stat-card-hyundai">
                            <div class="card-body p-4">
                                <div class="d-flex align-items-center">
                                    <div class="flex-grow-1">
                                        <p class="stat-label mb-1">평균 활용률</p>
                                        <h3 class="stat-number-hyundai">73.2%</h3>
                                        <span class="trend-indicator trend-stable">
                                        <i class="bi bi-dash"></i>0.8%
                                    </span>
                                    </div>
                                    <div class="metric-circle success">
                                        <i class="bi bi-graph-up"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row g-4 mb-4">
                    <div class="col-lg-8">
                        <div class="card card-hyundai">
                            <div class="card-header-hyundai">
                                <h5><i class="bi bi-bar-chart-line me-2"></i>월별 자재 사용 추이</h5>
                            </div>
                            <div class="card-body p-0">
                                <div class="chart-container-hyundai">
                                    <canvas id="monthlyUsageChart"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4">
                        <div class="card card-hyundai">
                            <div class="card-header-hyundai">
                                <h5><i class="bi bi-trophy me-2"></i>TOP 10 사용 자재</h5>
                            </div>
                            <div class="card-body">
                                <div class="material-item-hyundai">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <span class="material-code-hyundai">MTL-001</span>
                                            <div class="small text-muted mt-1">반도체 IC 칩</div>
                                            <span class="status-indicator status-active"></span>
                                            <small class="text-success">정상</small>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary">1,234회</div>
                                            <div class="progress progress-hyundai mt-2" style="width: 100px;">
                                                <div class="progress-bar progress-bar-hyundai" style="width: 95%"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="material-item-hyundai">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <span class="material-code-hyundai">MTL-045</span>
                                            <div class="small text-muted mt-1">저항 (1KΩ)</div>
                                            <span class="status-indicator status-active"></span>
                                            <small class="text-success">정상</small>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary">987회</div>
                                            <div class="progress progress-hyundai mt-2" style="width: 100px;">
                                                <div class="progress-bar progress-bar-hyundai" style="width: 80%"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="material-item-hyundai">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <span class="material-code-hyundai">MTL-123</span>
                                            <div class="small text-muted mt-1">커패시터 (100μF)</div>
                                            <span class="status-indicator status-warning"></span>
                                            <small class="text-warning">부족</small>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary">756회</div>
                                            <div class="progress progress-hyundai mt-2" style="width: 100px;">
                                                <div class="progress-bar progress-bar-hyundai" style="width: 61%"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="material-item-hyundai">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <span class="material-code-hyundai">MTL-067</span>
                                            <div class="small text-muted mt-1">PCB 기판 (FR-4)</div>
                                            <span class="status-indicator status-active"></span>
                                            <small class="text-success">정상</small>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary">623회</div>
                                            <div class="progress progress-hyundai mt-2" style="width: 100px;">
                                                <div class="progress-bar progress-bar-hyundai" style="width: 50%"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="material-item-hyundai">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <span class="material-code-hyundai">MTL-189</span>
                                            <div class="small text-muted mt-1">연결 케이블</div>
                                            <span class="status-indicator status-inactive"></span>
                                            <small class="text-muted">대기</small>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary">445회</div>
                                            <div class="progress progress-hyundai mt-2" style="width: 100px;">
                                                <div class="progress-bar progress-bar-hyundai" style="width: 36%"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="text-center mt-3">
                                    <button class="btn btn-outline-hyundai btn-sm">전체 목록 보기</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row g-4 mb-4">
                    <div class="col-md-6">
                        <div class="card card-hyundai">
                            <div class="card-header-hyundai">
                                <h5><i class="bi bi-pie-chart me-2"></i>카테고리별 사용 현황</h5>
                            </div>
                            <div class="card-body">
                                <canvas id="categoryUsageChart" style="max-height: 300px;"></canvas>
                                <div class="row text-center mt-4">
                                    <div class="col-6 mb-4">
                                        <div class="metric-circle blue mx-auto mb-2" style="width: 60px; height: 60px; font-size: 1.2rem;">45%</div>
                                        <h6 class="mb-1">전자부품</h6>
                                        <small class="text-muted">1,281개</small>
                                    </div>
                                    <div class="col-6 mb-4">
                                        <div class="metric-circle success mx-auto mb-2" style="width: 60px; height: 60px; font-size: 1.2rem;">28%</div>
                                        <h6 class="mb-1">기계부품</h6>
                                        <small class="text-muted">797개</small>
                                    </div>
                                    <div class="col-6 mb-4">
                                        <div class="metric-circle warning mx-auto mb-2" style="width: 60px; height: 60px; font-size: 1.2rem;">18%</div>
                                        <h6 class="mb-1">원자재</h6>
                                        <small class="text-muted">512개</small>
                                    </div>
                                    <div class="col-6 mb-4">
                                        <div class="metric-circle light-blue mx-auto mb-2" style="width: 60px; height: 60px; font-size: 1.2rem;">9%</div>
                                        <h6 class="mb-1">소모품</h6>
                                        <small class="text-muted">257개</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card card-hyundai">
                            <div class="card-header-hyundai">
                                <h5><i class="bi bi-speedometer2 me-2"></i>운영 효율성 지표</h5>
                            </div>
                            <div class="card-body">
                                <div class="mb-4">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span class="fw-medium">재고 회전율</span>
                                        <span class="fw-bold text-primary">85.3%</span>
                                    </div>
                                    <div class="progress progress-hyundai">
                                        <div class="progress-bar progress-bar-hyundai" role="progressbar" style="width: 85.3%" aria-valuenow="85.3" aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>
                                </div>
                                <div class="mb-4">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span class="fw-medium">자재 활용도</span>
                                        <span class="fw-bold text-primary">72.8%</span>
                                    </div>
                                    <div class="progress progress-hyundai">
                                        <div class="progress-bar progress-bar-hyundai" role="progressbar" style="width: 72.8%" aria-valuenow="72.8" aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>
                                </div>
                                <div class="mb-4">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span class="fw-medium">비용 효율성</span>
                                        <span class="fw-bold text-primary">91.2%</span>
                                    </div>
                                    <div class="progress progress-hyundai">
                                        <div class="progress-bar progress-bar-hyundai" role="progressbar" style="width: 91.2%" aria-valuenow="91.2" aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>
                                </div>
                                <div class="mb-4">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span class="fw-medium">공급망 안정성</span>
                                        <span class="fw-bold text-primary">88.7%</span>
                                    </div>
                                    <div class="progress progress-hyundai">
                                        <div class="progress-bar progress-bar-hyundai" role="progressbar" style="width: 88.7%" aria-valuenow="88.7" aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


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


</body>

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>


<!-- AdminLTE App -->
<script src="/resources/dist/js/adminlte.min.js"></script>

<!-- Bootstrap 4 -->
<script src="/resources/dist/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- DataTables  & Plugins -->
<script src="/resources/dist/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/resources/dist/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="/resources/dist/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
<script src="/resources/dist/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>


<!-- Highhart -->
<script src="/resources/dist/js/highcharts.js"></script>
<script src="/resources/dist/js/exporting.js"></script>
<script src="/resources/dist/js/export-data.js"></script>
<script src="/resources/dist/js/accessibility.js"></script>


<script>
    $(document).ready(function() {
        $("#subae").removeClass("menu-open");
        $("#sap").removeClass("menu-open");
        $("#mlb").removeClass("menu-open");
        $("#vault").removeClass("menu-open");




        //자재전체 엑셀 다운로드
        $('#partExcel').on('click', function () {

            let partNo = $('#partNo').val();
            //console.log(month);
            showLoading(); // 로딩바 표시
            $.ajax({
                url: '/excel/searchPart',   // 요청 보낼 URL
                type: 'POST',              // 메서드 (GET/POST 등)
                data : {
                    partNo : partNo
                    //ucheck: ucheck
                },
                xhrFields: {
                    responseType: 'blob'    // 파일 다운로드용 응답 처리
                },
                success: function (data, status, xhr) {

                    console.log(data);

                    // 응답 헤더에서 파일명 추출
                    const disposition = xhr.getResponseHeader('Content-Disposition');
                    let filename = 'excel.xlsx';
                    if (disposition && disposition.indexOf('filename=') !== -1) {
                        filename = disposition.split('filename=')[1].replace(/"/g, '');
                    }

                    // Blob으로 파일 생성 및 다운로드
                    const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
                    const link = document.createElement('a');
                    link.href = window.URL.createObjectURL(blob);
                    link.download = filename;
                    link.click();

                    hideLoading(); // 성공 시 로딩바 제거
                },
                error: function () {
                    alert('엑셀 다운로드 중 오류가 발생했습니다.');
                }
            });
        });
    });

    // 로딩바 표시 함수
    function showLoading() {
        // 로딩바 HTML 생성
        const loadingHtml = `
        <div id="loadingOverlay" style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        ">
            <div style="
                background: white;
                padding: 30px;
                border-radius: 8px;
                text-align: center;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            ">
                <div style="
                    border: 4px solid #f3f3f3;
                    border-top: 4px solid #3498db;
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 15px;
                "></div>
                <p style="margin: 0; font-size: 16px; color: #333;">엑셀 파일을 다운로드 중입니다...</p>
            </div>
        </div>
        <style>
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        </style>
    `;

        // 로딩바를 body에 추가
        document.body.insertAdjacentHTML('beforeend', loadingHtml);
    }

    // 로딩바 제거 함수
    function hideLoading() {
        const loadingOverlay = document.getElementById('loadingOverlay');
        if (loadingOverlay) {
            loadingOverlay.remove();
        }
    }
</script>

</html>
