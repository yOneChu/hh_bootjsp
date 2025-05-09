<%@page import="java.util.HashSet"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    String elvType = (String)request.getParameter("elvType"); // 202411
    String month = (String)request.getParameter("month"); // 월date or total

    System.out.println("elvType == " + elvType);
    System.out.println("month == " + month);


    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;




%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->

    <title>부품공용화 Dahsboard</title>

    <!-- Google Font: Source Sans Pro -->
    <!--    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">-->
    <link rel="stylesheet" href="/resources/dist/googleFont.css">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="/resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="/resources/dist/plugins/select2/css/select2.min.css">

    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">
</head>


<body>

<div class="wrapper">


    <!-- Content Wrapper. Contains page content -->
    <div class="content">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><%= elvType %> <font color="red"> (<%=month %>,)</font></h1>
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



        <!-- Main content -->
        <section class="content" style="zoom:100%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

                            <!-- <div class="card-header">
                                <h3 class="card-title">Dashboard</h3>
                            </div>
 -->
                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:90%;">
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">호기</th>
                                        <th style="font-weight: bold; text-align: center;">EL_ATYP<BR>(기종)</th>
                                        <th style="font-weight: bold; text-align: center;">수정일</th>
                                        <th style="font-weight: bold; text-align: center;">상태</th>
                                        <th style="font-weight: bold; text-align: center;">버전</th>
                                        <th style="font-weight: bold; text-align: center;">수주명</th>


                                        <th style="font-weight: bold; text-align: center;">EL_ABRAND<BR>(브랜드)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_ACAPA<BR>(용량)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_ADRV<BR>(운행방식</th>
                                        <th style="font-weight: bold; text-align: center;">EL_AEXP<BR>(기종파생모델) </th>
                                        <th style="font-weight: bold; text-align: center;">EL_AFQ<BR>(층수)  </th>
                                        <th style="font-weight: bold; text-align: center;">EL_AMAN<BR>(인승) </th>

                                        <th style="font-weight: bold; text-align: center;">EL_AOPEN<BR>(열림)</th>
                                        <th style="font-weight: bold; text-align: center;">ARKTX</th>
                                        <th style="font-weight: bold; text-align: center;">EL_ASPD<BR>(속도) </th>
                                        <th style="font-weight: bold; text-align: center;">EL_AUSE<BR>(용도) </th>
                                        <th style="font-weight: bold; text-align: center;">EL_BCDM<BR>(DOOR 재질) </th>

                                        <th style="font-weight: bold; text-align: center;">EL_BCL<BR>(천장종류)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_BCS<BR>(SILL재질)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_BDUSE<BR>(건물용도)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_BETC<BR>(TRANSOM 색상/무늬)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_BETM<BR>(TRANSOM 재질/무늬)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_BFLOORS<BR>(FLOOR종류/공급주체)</th>
                                        <th style="font-weight: bold; text-align: center;">EL_CRE_DATE</th>

                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <%
                                        try
                                        {
                                            con = VaultDBConnection.getConnection();

                                            StringBuffer sql = new StringBuffer();
                                            sql.append(" SELECT A.ELVINFO_ID, ABRAND, ACAPA, ADRV, AEXP, AFQ, AMAN, AOPEN, ARKTX, ASPD, ATYP, AUSE, BCDM, BCL, BCS,  ");
                                            sql.append(" BDUSE, BETC, BETM, BFLOORS, CRE_DATE, HOGI, MOD_DATE, MOD_MONTH, STATUS, SUJU_NAME, VERSION ");
                                            sql.append(" FROM elv_info A ");
                                            sql.append(" WHERE A.atyp = ? ");

                                            if (month.equals("total")) {

                                            } else {
                                                sql.append(" AND A.mod_month = ? ");
                                            }




                                            pstmt = con.prepareStatement(sql.toString());
                                            pstmt.setString(1, elvType);
                                            //pstmt.setString(2, month);
                                            if (month.equals("total")) {

                                            } else {
                                                pstmt.setString(2, month);
                                            }

                                            rs = pstmt.executeQuery();

                                            while(rs.next())
                                            {
                                                String ABRAND = rs.getString("ABRAND") == null ? "" : rs.getString("ABRAND");
                                                String ACAPA = rs.getString("ACAPA") == null ? "" : rs.getString("ACAPA");
                                                String ADRV = rs.getString("ADRV") == null ? "" : rs.getString("ADRV");
                                                String AEXP = rs.getString("AEXP") == null ? "" : rs.getString("AEXP");
                                                String AFQ  = rs.getString("AFQ") == null ? "" : rs.getString("AFQ");

                                                String AMAN  = rs.getString("AMAN") == null ? "" : rs.getString("AMAN");
                                                String AOPEN = rs.getString("AOPEN") == null ? "" : rs.getString("AOPEN");
                                                String ARKTX = rs.getString("ARKTX") == null ? "" : rs.getString("ARKTX");
                                                String ASPD  = rs.getString("ASPD") == null ? "" : rs.getString("ASPD");
                                                String ATYP  = rs.getString("ATYP") == null ? "" : rs.getString("ATYP");
                                                String AUSE  = rs.getString("AUSE") == null ? "" : rs.getString("AUSE");
                                                String BCDM  = rs.getString("BCDM") == null ? "" : rs.getString("BCDM");
                                                String BCL   = rs.getString("BCL") == null ? "" : rs.getString("BCL");
                                                String BCS   = rs.getString("BCS") == null ? "" : rs.getString("BCS");
                                                String BDUSE = rs.getString("BDUSE") == null ? "" : rs.getString("BDUSE");


                                                //BETC, BETM, BFLOORS, CRE_DATE, HOGI, MOD_DATE, MOD_MONTH, STATUS, SUJU_NAME, VERSION ");

                                                String BETC     = rs.getString("BETC") == null ? "" : rs.getString("BETC");
                                                String BETM     = rs.getString("BETM") == null ? "" : rs.getString("BETM");
                                                String BFLOORS  = rs.getString("BFLOORS") == null ? "" : rs.getString("BFLOORS");
                                                String CRE_DATE = rs.getString("CRE_DATE") == null ? "" : rs.getString("CRE_DATE");
                                                String HOGI     = rs.getString("HOGI") == null ? "" : rs.getString("HOGI");
                                                String MOD_DATE = rs.getString("MOD_DATE") == null ? "" : rs.getString("MOD_DATE");
                                                String MOD_MONTH = rs.getString("MOD_MONTH") == null ? "" : rs.getString("MOD_MONTH");
                                                String SUJU_NAME = rs.getString("SUJU_NAME") == null ? "" : rs.getString("SUJU_NAME");
                                                String STATUS   = rs.getString("MOD_MONTH") == null ? "" : rs.getString("STATUS");
                                                String VERSION  = rs.getString("VERSION") == null ? "" : rs.getString("VERSION");


                                                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
                                                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

                                                Date formatDate = formatter1.parse(MOD_DATE.substring(0,7));

                                                String strNewFormatDate =  formatter2.format(formatDate);

                                        %>
                                                <tr style="text-align-all: center">
                                                    <td style="color: red;"><%=HOGI     %></td>
                                                    <td style="color: blue; text-align: center;"><%=ATYP %> </td>
                                                    <td style="text-align: center;"><%=strNewFormatDate %></td>

                                                    <td style="text-align: center;"><%=STATUS   %></td>
                                                    <td style="text-align: center;"><%=VERSION  %></td>

                                                    <td style="text-align: center;"><%=SUJU_NAME%></td>

                                                    <td style="text-align: center;"><%=ABRAND%></td>
                                                    <td style="text-align: center;"><%=ACAPA %></td>
                                                    <td style="text-align: center;"><%=ADRV %></td>
                                                    <td style="text-align: center;"><%=AEXP %></td>
                                                    <td style="text-align: center;"><%=AFQ %></td>

                                                    <td style="text-align: center;"><%=AMAN %> </td>
                                                    <td style="text-align: center;"><%=AOPEN%> </td>
                                                    <td style="text-align: center;"><%=ARKTX%> </td>
                                                    <td style="text-align: center;"><%=ASPD %> </td>
                                                    <td style="text-align: center;"><%=AUSE %> </td>
                                                    <td style="text-align: center;"><%=BCDM %> </td>

                                                    <td style="text-align: center;"><%=BCL  %> </td>
                                                    <td style="text-align: center;"><%=BCS  %> </td>
                                                    <td style="text-align: center;"><%=BDUSE%> </td>
                                                    <td style="text-align: center;"><%=BETC     %></td>
                                                    <td style="text-align: center;"><%=BETM     %></td>
                                                    <td style="text-align: center;"><%=BFLOORS  %></td>
                                                    <td style="text-align: center;"><%=CRE_DATE %></td>

                                                </tr>
                                         <%
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            VaultDBConnection.disconnect(con, pstmt, rs);
                                        }


                                    %>


                                    </tbody>

                                </table>
                            </div>
                            <!-- /.card-body -->
                        </div>
                        <!-- /.card -->
                    </div>
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
        <strong>Copyright &copy; 2024 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>


</body>

<!-- <script src="https://code.jquery.com/jquery-3.5.1.js"></script> -->

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




<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 50,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        //"processing": true,
        "searching" : true,
        "paging" : true, // 페이징표시 삭제
        "destroy": true, // 테이블 재생성
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

        console.log('start');

    }); // end document ready

</script>

</html>
