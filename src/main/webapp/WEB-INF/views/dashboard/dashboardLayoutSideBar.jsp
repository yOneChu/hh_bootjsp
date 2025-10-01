
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //대시보드 전용 Sidebar
    System.out.println("--- dashboardLayoutSideBar.jsp ---");

    String menuType = request.getParameter("menuType");

    String dashStyle = "";
    if (menuType != null && !"".equals(menuType)) {
        if (menuType.equals("dashboard")) {
            dashStyle = "menu-open";
        }
    }
    System.out.println("dashStyle = " + dashStyle);

%>
<%--
<link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">--%>


<!-- Main Sidebar Container -->
<aside class="main-sidebar sidebar-dark-primary elevation-4">
    <!-- Brand Logo -->
    <a href="#" class="brand-link">
        <h4 class="text-center"></h4>
    </a>

    <!-- Sidebar -->
    <div class="sidebar" style="zoom:95%;">

        <div class="user-panel mt-3 pb-3 mb-3 d-flex">
            <div class="image">
                <%--<img src="dist/img/user2-160x160.jpg" class="img-circle elevation-2" alt="User Image">--%>
                <img src="/resources/favicon.ico" class="img-circle elevation-2" alt="icon" width="16" height="16">
            </div>
            <div class="info">
                <%--<h4><a href="#">수배로직설계팀</a></h4>--%>
                <%--<h4>🏢 <a href="#">수배로직설계팀</a></h4>--%>
                    <%--<h4><img src="/resources/favicon.ico" alt="icon" width="16" height="16"> <a href="#">수배로직설계팀</a></h4>--%>
                    <h4><a href="#">수배로직설계팀</a></h4>
            </div>
        </div>



        <!-- Sidebar Menu -->
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">


                <li id="dashboard" class="nav-item menu-open">
                    <a href="#" class="nav-link">
                            <i class="nav-icon fas">📊</i>
                        <p>
                            Dashboard
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/subae/bomDashboard" class="nav-link">
                                <p>BOM 수배율 부품 현황(월별)</p>
                            </a>
                        </li>

                        <li>
                                <a href="/subae/partDashboard" class="nav-link">
                                <p>PLM 자재 현황 및 Excel 다운로드</p>
                            </a>
                        </li>
                        <li>
                            <%--<a href="https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionRate.jsp" class="nav-link">--%>
                            <a href="/public/searchPriceReductionRate" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>부품공용화 집계(수량)</p>
                            </a>
                        </li>

                        <li>
                            <%--<a href="https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionRev.jsp" class="nav-link">--%>
                            <a href="/public/searchPriceReductionRev" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>부품공용화 집계(대수)</p>
                            </a>
                        </li>

                        <li>
                            <%--<a href="https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionDate.jsp" class="nav-link">--%>
                            <a href="/public/searchPriceReductionDate" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>부품공용화 집계 (출하예정일) </p>
                                <!-- <p>부품공용화 집계 <small> (출하예정일) </small></p> -->
                            </a>
                        </li>

                        <li>
                            <%--<a href="https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionDatePrice.jsp" class="nav-link">--%>
                            <a href="/public/searchPriceReductionDatePrice" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>부품공용화 집계 (금액) </p>
                                <!-- <p>부품공용화 집계 <small> (출하예정일) </small></p> -->
                            </a>
                        </li>



                    </ul>
                </li>

                <li id="sap" class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas">🖥️</i>
                        <p>
                             SAP
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/sap/searchProductingStatus" class="nav-link">
                                <p>(SAP)품목별 공정진행현황</p>
                            </a>
                        </li>
                    </ul>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/sap/getExportDate" class="nav-link">
                                <p>(SAP)출하예정일</p>
                            </a>
                        </li>
                    </ul>
                </li>




                <li id="subae" class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas">🔍</i>
                        <p>
                            수배로직
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>


                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/pid/searchPIDDetail" class="nav-link">
                                <p>PID-상세조회</p>
                            </a>
                        </li>
                        <%--<li>
                            <a href="/designReq/dashboard" class="nav-link">
                                <p>(신)전산요청 현황</p>
                            </a>
                        </li>--%>
                        <li>
                            <a href="/subae/searchMissPartofProduct" class="nav-link">
                                <p>자재 실적 전체 조회(ERP 전송현장 포함)(테스트)</p>
                            </a>
                        </li>

                        <li>
                            <a href="#" class="nav-link" onclick="window.open('/subae/logicView', 'firstPopup', 'width=1200,height=1000'); return false;">
                            <%--<a href="/subae/logicView" class="nav-link">--%>
                                <p>로직 특성값 View</p>
                            </a>
                        </li>
                        <li>
                            <a href="#" class="nav-link" onclick="window.open('/subae/elevatorSpecDiff', 'firstPopup', 'width=1000,height=1000'); return false;">
                                <%--<a href="/subae/logicView" class="nav-link">--%>
                                <p>호기 영업사양 비교</p>
                            </a>
                        </li>

                        <li>
                            <a href="/pid/pidDashboard" class="nav-link">
                                <p>PID 작업 현황</p>
                            </a>
                        </li>

                        <li>
                            <a href="/subae/comparePartCN" class="nav-link">
                                <p>본사/법인 자재비교(CN)</p>
                            </a>
                        </li>

                        <li>
                            <a href="/subae/searchStandardList" class="nav-link">
                                <p>법인자재 1LV 표준수배자재리스트</p>
                            </a>
                        </li>

                        <li>
                            <a href="/subae/searchBlockStandardView" class="nav-link">
                                <p>Block 기준정보 이력관리</p>
                            </a>
                        </li>

                        <li>
                            <a href="/subae/searchCNPart" class="nav-link">
                                <p>법인자재 조회(PLM)</p>
                            </a>
                        </li>

                        <%--<li>
                            <a href="/elvinfoDashboard" class="nav-link">
                                <p>영업사양 등록 현황(2024년)</p>
                            </a>
                        </li>--%>
                    </ul>
                </li>


                <li id="mlb" class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas">✍️</i>
                        <p>
                             MLB
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>
                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/mlb/searchPartQtyPid" class="nav-link">
                                <p>수량-PID 조회</p>
                            </a>
                        </li>
                    </ul>
                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/subae/searchByBlockNo" class="nav-link">
                                <p>부품 하위 추출</p>
                            </a>
                        </li>
                    </ul>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/subae/searchByBlockNo" class="nav-link">
                                <p>제품 전 레벨 BOM추출</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <li id="vault" class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <i class="nav-icon fas">📡️</i>
                        <p>
                             3D
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/download/manualDashView" class="nav-link">
                                <p>3D 설정파일 및 메뉴얼</p>
                            </a>
                        </li>
                    </ul>
                </li>


            </ul>
        </nav>
        <!-- /.sidebar-menu -->
    </div>
    <!-- /.sidebar -->
</aside>


