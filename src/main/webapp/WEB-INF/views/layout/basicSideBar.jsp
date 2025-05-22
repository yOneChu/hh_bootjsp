
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //기본 Sidebar
    System.out.println("--- basicSideBar.jsp ---");


%>
<!-- Main Sidebar Container -->
<aside class="main-sidebar sidebar-dark-primary elevation-4">
    <!-- Brand Logo -->
    <a href="#" class="brand-link">
        <h4 class="text-center"></h4>
    </a>

    <!-- Sidebar -->
    <div class="sidebar" style="zoom:95%;">

        <div class="text-center user-panel mt-3 pb-3 mb-3">
            <div class="info">
                <%--<h4><a href="#">수배로직설계팀</a></h4>--%>
                <%--<h4>🏢 <a href="#">수배로직설계팀</a></h4>--%>
                    <h4><img src="/resources/favicon.ico" alt="icon" width="16" height="16"> <a href="#">수배로직설계팀</a></h4>


            </div>
        </div>

        <!-- Sidebar Menu -->
        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">


                <li class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <%--<i class="nav-icon fas fa-tachometer-alt"></i>--%>
                        <p>
                            💡 수배로직
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>


                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/pid/searchPIDDetail" class="nav-link">
                                <p>PID-상세조회</p>
                            </a>
                        </li>
                        <li>
                            <a href="/designReq/dashboard" class="nav-link">
                                <p>(신)전산요청 현황</p>
                            </a>
                        </li>

                        <li>
                            <a href="/pid/pidDashboard" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>PID 작업 현황</p>
                            </a>
                        </li>

                        <li>
                            <a href="/subae/comparePartCN" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>본사/법인 자재비교(CN)</p>
                            </a>
                        </li>

                        <li>
                            <a href="/subae/searchStandardList" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>법인자재 1LV 표준수배자재리스트</p>
                            </a>
                        </li>

                        <li>
                            <a href="/elvinfoDashboard" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>영업사양 등록 현황(2024년)</p>
                            </a>
                        </li>
                    </ul>
                </li>




                <li class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <%--<i class="nav-icon fas fa-tachometer-alt"></i>--%>
                        <p>
                            💡 MLB
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>


                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/subae/searchByBlockNo" class="nav-link">
                                <p>제품 전 레벨 BOM추출</p>
                            </a>
                        </li>
                    </ul>
                </li>



                <li class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <%--<i class="nav-icon fas fa-book"></i>--%>
                        <p>
                            🖥️ SAP
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/sap/searchProductingStatus" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>(SAP)품목별 공정진행현황</p>
                            </a>
                        </li>
                    </ul>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/sap/getExportDate" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
                                <p>(SAP)출하예정일</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item menu-open">
                    <a href="#" class="nav-link">
                        <%--<i class="nav-icon fas fa-image"></i>--%>
                        <p>
                            📡 3D
                            <i class="fas fa-angle-left right"></i>
                        </p>
                    </a>

                    <%-- <ul class="nav nav-treeview">
                         <li>
                             <a href="/download/vaultFile" class="nav-link">
                                 &lt;%&ndash;<i class="far fa-circle nav-icon"></i>&ndash;%&gt;
                                 <p>설정파일 다운로드</p>
                             </a>
                         </li>
                     </ul>--%>

                    <ul class="nav nav-treeview">
                        <li>
                            <a href="/download/manualDashView" class="nav-link">
                                <%--<i class="far fa-circle nav-icon"></i>--%>
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


