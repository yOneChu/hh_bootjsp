<%@page import="java.util.HashSet"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.time.LocalDate"%>
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

    // BOM수배 대시보드 BOM 리스트 팝업화면
    // bomSubaeDashboardPop.jsp.jsp
    //


    String contextPath = request.getContextPath();
    System.out.println("--- searchPriceReductionDatePop.jsp ---");


    String curDate = (String)request.getParameter("curDate"); // 202411/2024/2025/total
    String partType = (String)request.getParameter("partType"); // 월date or total
    String todayVal = (String)request.getParameter("todayVal");



    System.out.println("curDate == " + curDate);
    System.out.println("partType == " + partType);
    System.out.println("todayVal == " + todayVal);

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

    HashSet<String> duplicatedCheck = new HashSet<String>();
    String title = partType;

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;

    try
    {


        String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
        String id = "SA";
        String pw = "AutodeskVault@26200"; // "qwe123!@#"
        //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        //con = DriverManager.getConnection(url,id,pw);
        con = VaultDBConnection.getConnection();


        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT ");
        sql.append(" A.HOGI, A.batch_date, A.part_type, ");
        sql.append(" A.erp_send_date, A.erp_send_month, A.part_no,A.qty, A.dwg_no, A.export_date, ");
        sql.append(" A.gi_song, A.BLOCK_NO, ");
        sql.append(" A.gong_sa, A.SPEC, A.M_USER, A.E_USER ");
        sql.append(" FROM dash_publicdata A ");

        sql.append(" WHERE A.BATCH_DATE = ? ");


        if("total".equals(curDate)) {
            //sql.append(" AND SUBSTRING(A.export_date, 1, 6) = ? ");


        } else if("2024".equals(curDate)) {
            sql.append(" AND SUBSTRING(A.export_date, 1, 4) = '" + curDate + "'");
        } else if("2025".equals(curDate)) {
            sql.append(" AND SUBSTRING(A.export_date, 1, 4) = '" + curDate + "'");
        } else if("ETC".equals(curDate)) {
            sql.append(" AND SUBSTRING(A.export_date, 1, 4) != '2024' AND SUBSTRING(A.export_date, 1, 4) != '2025' ");
        } else {
            sql.append(" AND SUBSTRING(A.export_date, 1, 6) = '" + curDate + "'");
        }



        //sql.append(" AND A.part_type = ? ");
        //sql.append(" AND A.part_type = ? ");

        if("LAMP".equals(partType)) {
            //sql.append(" AND A.part_type LIKE '" + partType + "%'");
            sql.append(" AND A.part_type IN ('LAMP_CARTOP', 'LAMP_HOIST', 'LAMP_OVER', 'LAMP_PIT') ");

        } else if("HIP".equals(partType)) {
            sql.append(" AND A.part_type IN ('HIP_BOT', 'HIP_MID', 'HIP_TOP' ) ");

        } else if("HPB".equals(partType)) {
            sql.append(" AND A.part_type IN ('HPB_BOT', 'HPB_MID', 'HPB_TOP' ) ");

        } else {
            sql.append(" AND A.part_type ='" + partType + "'");
        }



        System.out.println("sql.tostring == " + sql.toString());
        pstmt = con.prepareStatement(sql.toString());
        pstmt.setString(1, todayVal);
        //pstmt.setString(2, curDate);
        //pstmt.setString(3, partType);

        rs = pstmt.executeQuery();

        while(rs.next())
        {
            String part_name = rs.getString("part_type") == null ? "" : rs.getString("part_type");
            String batch_date = rs.getString("batch_date") == null ? "" : rs.getString("batch_date");
            String erp_send_date = rs.getString("erp_send_date") == null ? "" : rs.getString("erp_send_date");
            String erp_send_month = rs.getString("erp_send_month") == null ? "" : rs.getString("erp_send_month");

            String hogi = rs.getString("HOGI") == null ? "" : rs.getString("HOGI");
            String export_date = rs.getString("export_date") == null ? "" : rs.getString("export_date");
            String part_no = rs.getString("part_no") == null ? "" : rs.getString("part_no");
            String qty = rs.getString("qty") == null ? "" : rs.getString("qty");
            String dwg_no = rs.getString("dwg_no") == null ? "" : rs.getString("dwg_no");
            String gi_song = rs.getString("gi_song") == null ? "" : rs.getString("gi_song");
            String gong_sa = rs.getString("gong_sa") == null ? "" : rs.getString("gong_sa");
            String spec = rs.getString("spec") == null ? "" : rs.getString("spec");
            String m_user = rs.getString("m_user") == null ? "" : rs.getString("m_user");
            String e_user = rs.getString("e_user") == null ? "" : rs.getString("e_user");
            String BLOCK_NO = rs.getString("BLOCK_NO") == null ? "" : rs.getString("BLOCK_NO");

            HashMap<String, String> dMap = new HashMap<String, String>();
            dMap.put("part_name", part_name);
            dMap.put("batch_date", batch_date);
            dMap.put("erp_send_date", erp_send_date);
            dMap.put("erp_send_month", erp_send_month);

            dMap.put("BLOCK_NO", BLOCK_NO);
            dMap.put("HOGI", hogi);
            dMap.put("export_date", export_date);
            dMap.put("part_no", part_no);
            dMap.put("qty", qty);
            dMap.put("dwg_no", dwg_no);
            dMap.put("gi_song", gi_song);
            dMap.put("gong_sa", gong_sa);
            dMap.put("spec", spec);
            dMap.put("m_user", m_user);
            dMap.put("e_user", e_user);

            dataList.add(dMap);


            if(!duplicatedCheck.contains(hogi.trim())) {
                //dataList.add(dMap);
                //duplicatedCheck.add(hogi.trim());
            }

        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //DynaUtils.close(rs,pstmt,con);
        VaultDBConnection.disconnect(con, pstmt, rs);

    }

    if(partType.contains("CP")) {
        title = "CP";
    } else if(partType.contains("TM")) {
        title = "TM(Belt Type)";
    } else if(partType.contains("CAR")) {
        //title = "Car Top Box";
    } else if(partType.contains("CAR")) {
        //title = "Car Top Box";
    } else if(partType.contains("CAR")) {
        //title = "Car Top Box";
    } else if(partType.contains("CAR")) {
        //title = "Car Top Box";
    }


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->

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

                        <%
                            if("total".equals(curDate)){
                        %>
                        <h1><%= title  + " 전체, 출하(예정) 자재" %> <font color="red"> (<%=todayVal %>, 06:00기준)</font></h1>

                        <h4><font color="blue"> (2024.05 이후 출하예정일 모든 자재 포함) </font></h4>

                        <%
                        } else {
                        %>
                        <%-- <h1><%= curDate.substring(0,4) + "-" + curDate.substring(4,6) + ", " + partType  + " 출하(예정) 자재" %> <font color="red"> (<%=todayVal %>, 06:00기준)</font></h1> --%>
                        <%
                            }
                        %>


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
                                        <th style="font-weight: bold; text-align: center;">제품번호</th>
                                        <th style="font-weight: bold; text-align: center;">기종</th>
                                        <th style="font-weight: bold; text-align: center;">파트번호</th>
                                        <th style="font-weight: bold; text-align: center;">파트명</th>
                                        <th style="font-weight: bold; text-align: center;">BlockNo</th>
                                        <th style="font-weight: bold; text-align: center;">수량</th>
                                        <th style="font-weight: bold; text-align: center;">품목</th>
                                        <th style="font-weight: bold; text-align: center;">수정여부</th>
                                        <th style="font-weight: bold; text-align: center;">CMT</th>

                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <%


                                    %>

                                    <tr>
                                        <td style="text-align: center;"> <%=strNewDtFormat %> </td>
                                        <td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <%=row.get("HOGI") %> </td>
                                        <td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <%=row.get("gi_song") %> </td>
                                        <td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <%=row.get("part_no") %> </td>
                                        <td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <%=getPartType %> </td>
                                        <td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <font color="red"> <%=strExportDate %> </font> </td>
                                        <td style="font-weight: bold; background-color: #e6ffff; text-align: center;"> <font color="red"><%=row.get("qty") %> </font> </td>
                                        <td style="text-align: center;"> <%=row.get("dwg_no") %> </td>
                                        <td style="text-align: center;"> <%=row.get("BLOCK_NO") %> </td>
                                        <td> <%=row.get("gong_sa") %> </td>
                                        <td> <%=row.get("spec") %> </td>
                                        <td style="text-align: center;"> <%=row.get("m_user") %> </td>
                                        <td style="text-align: center;"> <%=row.get("e_user")  %> </td>
                                    </tr>
                                    <%

                                        } // end for

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


<!-- Highhart -->
<script src="/resources/dist/js/highcharts.js"></script>
<script src="/resources/dist/js/exporting.js"></script>
<script src="/resources/dist/js/export-data.js"></script>
<script src="/resources/dist/js/accessibility.js"></script>




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
