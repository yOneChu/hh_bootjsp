<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    //부품공용화 집계 - 대수
    // searchPriceReductionPopRev.jsp
    // http://10.225.4.20/jsp/searchLogic/searchPriceReductionRev.jsp
    // http://localhost/jsp/searchLogic/searchPriceReductionRev.jsp

    String contextPath = request.getContextPath();
    //SvServer.setClientContextId(SessionUtils.getSessionId(session));
    System.out.println("--- searchPriceReductionPopRev.jsp ---");


    String viewType = (String)request.getParameter("viewType"); // parttype
    String startDate = (String)request.getParameter("startDate"); // 월date or total
    String todayVal = (String)request.getParameter("todayVal");
    String rate = (String)request.getParameter("rate");



    System.out.println("viewType == " + viewType);
    System.out.println("startDate == " + startDate);
    System.out.println("todayVal == " + todayVal);
    System.out.println("rate == " + rate);

    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;

    try
    {

 /*       String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
        String id = "SA";
        String pw = "AutodeskVault@26200"; // "qwe123!@#"

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        con = DriverManager.getConnection(url,id,pw);*/

        con = VaultDBConnection.getConnection();


        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT ");
        sql.append(" A.batch_date, A.part_type, ");
        sql.append(" A.erp_send_date, A.erp_send_month, A.part_no,A.qty, A.dwg_no, A.export_date, ");
        sql.append(" A.gi_song, A.HOGI, A.BLOCK_NO, ");
        sql.append(" A.gong_sa, A.SPEC, A.M_USER, A.E_USER, A.CREATE_NATION, A.MODULE ");
        sql.append(" FROM dash_publicdata A ");
        //sql.append(" WHERE A.BATCH_DATE = ? ");
        sql.append(" WHERE A.BATCH_DATE = '" + todayVal + "'");
        //sql.append(" AND A.part_type = ? ");

        if(rate != null && !"".equals(rate)) {
            if("TRUE".equals(rate) ) {
                //수량조회
                if("LAMP_HOIST".equals(viewType)) {
                    viewType = "LAMP";
                    sql.append(" AND A.part_type LIKE 'LAMP%' ");

                } else if("HPB_BOT".equals(viewType)) {
                    viewType = "HPB";
                    sql.append(" AND A.part_type LIKE 'HPB%' ");

                } else if("HIP_BOT".equals(viewType)) {
                    viewType = "HIP";
                    //sql.append(" AND A.part_type LIKE 'HIP_%' ");
                    sql.append(" AND A.part_type IN ('HIP_TOP', 'HIP_MID', 'HIP_BOT')   ");

                    //sql.append(" AND A.part_type !=  'HIP700' ");
                } else {
                    sql.append(" AND A.part_type = '" + viewType + "'");
                }
            }else {
                sql.append(" AND A.part_type = '" + viewType + "'");
            }
        } else {
            //rate null이면 대수조회 쿼리
            sql.append(" AND A.part_type = '" + viewType + "'");
        }


        if( !"total".equals(startDate) ) {
            sql.append(" AND A.erp_send_month = '" + startDate + "' ");
        }

        System.out.println("sql.tostring == " + sql.toString());
        pstmt = con.prepareStatement(sql.toString());
        //pstmt.setString(1, todayVal);
        //pstmt.setString(2, viewType);


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
            String create_nation = rs.getString("CREATE_NATION") == null ? "" : rs.getString("CREATE_NATION");
            String module = rs.getString("MODULE") == null ? "" : rs.getString("MODULE");

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

            dMap.put("create_nation", create_nation);
            dMap.put("module", module);

            dataList.add(dMap);
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //DynaUtils.close(rs,pstmt,con);
        VaultDBConnection.disconnect(con, pstmt, rs);
    }

    if("CARTOPBOX".equals(viewType)) {
        viewType = "Car Top Box";

    } else if("cpMRL_5".equals(viewType)) {
        viewType = "CP (MRL_5.5kW_일반)";

    } else if("cpMRL_9".equals(viewType)) {
        viewType = " CP(MRL_9kW_일반)";

    } else if("cpMRL_14".equals(viewType)) {
        viewType = " CP(MRL_14kW_일반)";

    } else if("cpMRL_17".equals(viewType)) {
        viewType = " CP(MRL_17.5kW_일반)";

    } else if("cpMR_5_5".equals(viewType)) {
        viewType = " CP(MR_5.5kW_회생)";

    } else if("cpMR_9".equals(viewType)) {
        viewType = " CP(MR_9kW_회생)";

    } else if("cpMR_14".equals(viewType)) {
        viewType = " CP(MR_14kW_회생)";

    } else if("cpMR_17_5".equals(viewType)) {
        viewType = " CP(MR_17.5kW_회생)";

    } else if("TM".equals(viewType)) {
        viewType = "TM(Belt Type)";

    } else if("TMRope".equals(viewType)) {
        viewType = "TM(Rope)";

    } else if("GOV".equals(viewType)) {
        viewType = "Governor";

    } else if("LAMP_HOIST".equals(viewType)) {
        viewType = "승강로 LAMP(HOISTWAY)";

    } else if("PIT".equals(viewType)) {
        viewType = "PIT_SW";

    } else if("HPB_BOT".equals(viewType)) {
        viewType = "HPB(J21,BOT)";

    } else if("HIP_BOT".equals(viewType)) {
        viewType = "HIP(SJ21,BOT)";

    } else if("HIP700".equals(viewType)) {
        viewType = "HPI(S700)";

    } else if("cp1_5_MRL_General".equals(viewType)) {
        viewType = "1.5단계 CP(MRL_일반)";

    } else if("cp1_5_MRL_Revive".equals(viewType)) {
        viewType = "1.5단계 CP(MRL_회생)";
    }


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

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


<body class="hold-transition sidebar-mini text-sm" style="zoom:95%;">
<div class="wrapper">


    <!-- Content Wrapper. Contains page content -->
    <div class="content">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">

                        <%
                            if("total".equals(startDate)) {
                        %>

                        <h1><%=viewType + ", 전체" %> <font color="red"> (<%=todayVal %>, 06:00기준)</font></h1>
                        <%
                        } else {
                        %>
                        <h1><%=viewType %> (<%=(startDate.substring(0,4) + "." + startDate.substring(4, startDate.length()))  %>) - <small> <font color="red"> (<%=todayVal %>, 06:00기준)</font> </small></h1>
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
                                        <th style="font-weight: bold; text-align: center;">전송일자</th>
                                        <th style="font-weight: bold; text-align: center;">호기</th>
                                        <th style="font-weight: bold; text-align: center;">생산거점</th>
                                        <th style="font-weight: bold; text-align: center;">모듈러</th>

                                        <th style="font-weight: bold; text-align: center;">출하예정일</th>
                                        <th style="font-weight: bold; text-align: center;">자재번호</th>
                                        <th style="font-weight: bold; text-align: center;">수량</th>
                                        <th style="font-weight: bold; text-align: center;">도면번호</th>
                                        <th style="font-weight: bold; text-align: center;">Block.NO</th>
                                        <th style="font-weight: bold; text-align: center;">공사번호</th>
                                        <th style="font-weight: bold; text-align: center;">기종</th>

                                        <th style="font-weight: bold; text-align: center;">SPEC</th>
                                        <th style="font-weight: bold; text-align: center;">기계담당자</th>
                                        <th style="font-weight: bold; text-align: center;">전기담당자</th>
                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">

                                    <%
                                        //String viewType = (String)request.getParameter("viewType");
                                        //String startDate = (String)request.getParameter("startDate");

                                        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
                                        SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");

                                        for(int i=0; i < dataList.size(); i++)
                                        {
                                            HashMap<String, String> row = (HashMap) dataList.get(i);

                                            String erpSendDate = row.get("erp_send_date");
                                            String exportDate = row.get("export_date") == null ? "" : row.get("export_date");

                                            String strExportDate = "";

                                            // String 타입을 Date 타입으로 변환
                                            Date formatDate = dtFormat.parse(erpSendDate);

                                            if(exportDate != null && !"".equals(exportDate.trim())) {
                                                Date exportFormatDate = dtFormat.parse(exportDate);
                                                strExportDate = newDtFormat.format(exportFormatDate);
                                            }

                                            // Date타입의 변수를 새롭게 지정한 포맷으로 변환
                                            String strNewDtFormat = newDtFormat.format(formatDate);

                                            //#FDE9D9
                                    %>

                                    <tr>
                                        <td> <%=strNewDtFormat %> </td>
                                        <td> <%=row.get("HOGI") %> </td>
                                        <td> <%=row.get("create_nation") %> </td>
                                        <td> <%=row.get("module") %> </td>
                                        <td style="font-weight: bold;"> <font color="red"> <%=strExportDate %> </font> </td>
                                        <td> <%=row.get("part_no") %> </td>
                                        <td> <%=row.get("qty") %> </td>
                                        <td> <%=row.get("dwg_no") %> </td>

                                        <td> <%=row.get("BLOCK_NO") %> </td>
                                        <td> <%=row.get("gong_sa") %> </td>
                                        <td> <%=row.get("gi_song") %> </td>
                                        <td> <%=row.get("spec") %> </td>
                                        <td> <%=row.get("m_user") %> </td>
                                        <td> <%=row.get("e_user")  %> </td>
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
        <strong>Copyright &copy; 2025 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
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
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "ordering" : false,
        "searching" : false,
        "paging" : false, // 페이징표시 삭제
        "destroy": true, // 테이블 재생성
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {

        console.log('highchart start');

    }); // end document ready

    function viewList(type, viewDate) {

        console.log(type + " -- " + viewDate);

        let todayVal = '<%=todayVal %>'

        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionPop.jsp?";
        //let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionPop.jsp?";
        //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionPopRev.jsp?";
        let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionPopRev.jsp?";

        urlValue += "viewType=" + type;
        urlValue += "&startDate=" + viewDate;
        urlValue += "&todayVal=" + todayVal;

        window.open(urlValue,'_blank','width=1500, height=800, top=50, left=50, scrollbars=yes');

    }

</script>

</html>
