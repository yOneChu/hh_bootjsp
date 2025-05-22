<%@page import="java.sql.ResultSetMetaData"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.kyhslam.util.PLMDBConnection" %>
<%@ page import="java.util.Iterator" %>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>
<%!

    public static boolean chCheck(String partNo) {


        boolean flag = false;

        if(partNo.length() < 9) {
            return false;
        }

        //시작열
        if(partNo.startsWith("C") || partNo.startsWith("S")) {

            //3자리 숫자 여부 체크
            if(partNo.substring(1,4).matches("[+-]?\\d*(\\.\\d+)?")) {

                if(partNo.substring(4).startsWith("M") || partNo.substring(4).startsWith("P") ) {

                    //뒷자리 숫자 여부 체크
                    if(partNo.substring(5).matches("[+-]?\\d*(\\.\\d+)?")) {
                        //System.out.println("OK china");
                        flag = true;
                    }
                }
            }
        }

        return flag;
    }


%>

<%

    //법인자재1LV표준수배자재리스트
    // searchStandardList.jsp
    // http://10.225.4.20/jsp/searchLogic/searchStandardList.jsp
    // http://localhost/jsp/searchLogic/searchStandardList.jsp


    String contextPath = request.getContextPath();
    //SvServer.setClientContextId(SessionUtils.getSessionId(session));
    System.out.println("--- searchStandardList.jsp ---");

    HashMap<String, ArrayList<String>> data = new HashMap<String, ArrayList<String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;

    try {

        //con = DBconnectionInfo.getPDM_DBConnection();
        con = PLMDBConnection.getConnection();

        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT h.pid,D.ADDR, D.VAL1,D.VAL2,D.VAL3,D.VAL4,D.VAL5,D.VAL6,D.VAL7,D.VAL8,D.VAL9,D.VAL10, ");
        sql.append(" D.VAL11,D.VAL12,D.VAL13,D.VAL14,D.VAL15,D.VAL16,D.VAL17,D.VAL18,D.VAL19,D.VAL20 ");

        sql.append(" FROM variant_d d, variant_h h, variant_id id ");
        sql.append(" WHERE h.HOUID = id.LAST_HOUID AND h.HOUID =d.HOUID ");
        sql.append(" AND ( ");
        sql.append(" REGEXP_LIKE(D.VAL1,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$')  ");
        sql.append(" OR REGEXP_LIKE(D.VAL2,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL3,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL4,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL5,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL6,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL7,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL8,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL9,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL10,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL11,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL12,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL13,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL14,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL15,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL16,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL17,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL18,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL19,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" OR REGEXP_LIKE(D.VAL20,'^[CS][0-9][0-9][0-9][MP][0-9][0-9][0-9][0-9][0-9][0-9]$') ");
        sql.append(" ) ORDER BY PID, NO");

        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();

        while(rs.next())
        {
            String PID = rs.getString("PID") == null ? "" : rs.getString("PID");
            String ADDR = rs.getString("ADDR") == null ? "" : rs.getString("ADDR");
            String VAL1 = rs.getString("VAL1") == null ? "" : rs.getString("VAL1");
            String VAL2 = rs.getString("VAL2") == null ? "" : rs.getString("VAL2");
            String VAL3 = rs.getString("VAL3") == null ? "" : rs.getString("VAL3");
            String VAL4 = rs.getString("VAL4") == null ? "" : rs.getString("VAL4");
            String VAL5 = rs.getString("VAL5") == null ? "" : rs.getString("VAL5");
            String VAL6 = rs.getString("VAL6") == null ? "" : rs.getString("VAL6");
            String VAL7 = rs.getString("VAL7") == null ? "" : rs.getString("VAL7");
            String VAL8 = rs.getString("VAL8") == null ? "" : rs.getString("VAL8");
            String VAL9 = rs.getString("VAL9") == null ? "" : rs.getString("VAL9");
            String VAL10 = rs.getString("VAL10") == null ? "" : rs.getString("VAL10");
            String VAL11 = rs.getString("VAL11") == null ? "" : rs.getString("VAL11");
            String VAL12 = rs.getString("VAL12") == null ? "" : rs.getString("VAL12");
            String VAL13 = rs.getString("VAL13") == null ? "" : rs.getString("VAL13");
            String VAL14 = rs.getString("VAL14") == null ? "" : rs.getString("VAL14");
            String VAL15 = rs.getString("VAL15") == null ? "" : rs.getString("VAL15");
            String VAL16 = rs.getString("VAL16") == null ? "" : rs.getString("VAL16");
            String VAL17 = rs.getString("VAL17") == null ? "" : rs.getString("VAL17");
            String VAL18 = rs.getString("VAL18") == null ? "" : rs.getString("VAL18");
            String VAL19 = rs.getString("VAL19") == null ? "" : rs.getString("VAL19");
            String VAL20 = rs.getString("VAL20") == null ? "" : rs.getString("VAL20");


            //boolean flag1 = false;
            //boolean flag2 = false;


            ArrayList<String> rList = new ArrayList<String>();

            if(data.containsKey(PID)) {
                //ArrayList<String> rList = data.get(PID);
                rList = data.get(PID);

				/* if(VAL1 != null && !"".equals(VAL1) && VAL1.length() > 6) {
					if( !rList.contains(VAL1) ) {
						rList.add(VAL1);
					}
				}

				if(VAL2 != null && !"".equals(VAL2) && VAL2.length() > 6) {
					if( !rList.contains(VAL2) ) {
						rList.add(VAL2);
					}
				} */


                data.put(PID, rList);

            } else {
                rList = new ArrayList<String>();

				/* if(VAL1 != null && !"".equals(VAL1) && VAL1.length() > 6) {
					rList.add(VAL1);
				}

				if(VAL2 != null && !"".equals(VAL2) && VAL2.length() > 6) {
					rList.add(VAL2);
				}

				data.put(PID, rList); */
            }


            if(VAL1 != null && !"".equals(VAL1) && VAL1.length() > 6) {
                if(chCheck(VAL1) && !rList.contains(VAL1)) rList.add(VAL1);
            }

            if(VAL2 != null && !"".equals(VAL2) && VAL2.length() > 6) {
                if(chCheck(VAL2) && !rList.contains(VAL2)) rList.add(VAL2);
            }

            if(VAL3 != null && !"".equals(VAL3) && VAL3.length() > 6) {
                if(chCheck(VAL3) && !rList.contains(VAL3)) rList.add(VAL3);
            }

            if(VAL4 != null && !"".equals(VAL4) && VAL4.length() > 6) {
                if(chCheck(VAL4) && !rList.contains(VAL4)) rList.add(VAL4);
            }

            if(VAL5 != null && !"".equals(VAL5) && VAL5.length() > 6) {
                if(chCheck(VAL5) && !rList.contains(VAL5)) rList.add(VAL5);
            }

            if(VAL6 != null && !"".equals(VAL6) && VAL6.length() > 6) {
                if(chCheck(VAL6) && !rList.contains(VAL6)) rList.add(VAL6);
            }

            if(VAL7 != null && !"".equals(VAL7) && VAL7.length() > 6) {
                if(chCheck(VAL7) && !rList.contains(VAL7)) rList.add(VAL7);
            }

            if(VAL8 != null && !"".equals(VAL8) && VAL8.length() > 6) {
                if(chCheck(VAL8) && !rList.contains(VAL8)) rList.add(VAL8);
            }

            if(VAL9 != null && !"".equals(VAL9) && VAL9.length() > 6) {
                if(chCheck(VAL9) && !rList.contains(VAL9)) rList.add(VAL9);
            }

            if(VAL10 != null && !"".equals(VAL10) && VAL10.length() > 6) {
                if(chCheck(VAL10) && !rList.contains(VAL10)) rList.add(VAL10);
            }

            if(VAL11 != null && !"".equals(VAL11) && VAL11.length() > 6) {
                if(chCheck(VAL11) && !rList.contains(VAL11)) rList.add(VAL11);
            }

            if(VAL12 != null && !"".equals(VAL12) && VAL12.length() > 6) {
                if(chCheck(VAL12) && !rList.contains(VAL12)) rList.add(VAL12);
            }

            if(VAL13 != null && !"".equals(VAL13) && VAL13.length() > 6) {
                if(chCheck(VAL13) && !rList.contains(VAL13)) rList.add(VAL13);
            }

            if(VAL14 != null && !"".equals(VAL14) && VAL14.length() > 6) {
                if(chCheck(VAL14) && !rList.contains(VAL14)) rList.add(VAL14);
            }

            if(VAL15 != null && !"".equals(VAL15) && VAL15.length() > 6) {
                if(chCheck(VAL15) && !rList.contains(VAL15)) rList.add(VAL15);
            }

            if(VAL16 != null && !"".equals(VAL16) && VAL16.length() > 6) {
                if(chCheck(VAL16) && !rList.contains(VAL16)) rList.add(VAL16);
            }

            if(VAL17 != null && !"".equals(VAL17) && VAL17.length() > 6) {
                if(chCheck(VAL17) && !rList.contains(VAL17)) rList.add(VAL17);
            }

            if(VAL18 != null && !"".equals(VAL18) && VAL18.length() > 6) {
                if(chCheck(VAL18) && !rList.contains(VAL18)) rList.add(VAL18);
            }

            if(VAL19 != null && !"".equals(VAL19) && VAL19.length() > 6) {
                if(chCheck(VAL19) && !rList.contains(VAL19)) rList.add(VAL19);
            }

            if(VAL20 != null && !"".equals(VAL20) && VAL20.length() > 6) {
                if(chCheck(VAL20) && !rList.contains(VAL20)) rList.add(VAL20);
            }



            data.put(PID, rList);

        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        PLMDBConnection.disconnect(con, pstmt, rs);
    }





%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />



    <title>법인자재 1LV 표준수배자재리스트</title>

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


    <!-- Main Sidebar Container -->
    <%--<jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />--%>
    <jsp:include page="../layout/basicSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>법인자재 1LV 표준수배자재리스트</h1>
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
        <section class="content" style="zoom:90%;">

            <div class="container-fluid"> <!-- start - container-fluid -->



                <div class="row">
                    <!-- <div class="col-12"> -->
                    <div class="col-lg-7">

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">검색 결과</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">PID</th>
                                        <th style="font-weight: bold; text-align: center;">자재명</th>
                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">


                                    <%
                                        if(data != null && data.size() > 0) {

                                            Iterator keys = data.keySet().iterator();

                                            while(keys.hasNext()) {
                                                String key = (String)keys.next();

                                                ArrayList<String> rList = data.get(key);

                                                for(int i=0; i < rList.size(); i++) {
                                                    String a = rList.get(i);
                                    %>
                                    <tr>
                                        <td> <%=key %> </td>
                                        <td> <%=a %></td>
                                    </tr>
                                    <%
                                                }
                                            }
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


<script>

    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 25,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["csv", "excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


    //ready
    $(document).ready(function() {


        //엔터키 감지
        $(document).keyup(function(event) {
            if(event.which === 13) {
                search();
                return false; // 추가 이벤트 방지위해 false 리턴
            }
        })

    });

    //검색
    function search()
    {
        let partNo = $("#partNo").val(); // 제품번호


        if(partNo == null || "" == partNo) {
            console.log(partNo);
            alert("부품번호를 입력하세요.");
            return;
        }

        console.log("partNo == ", partNo);

        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        partNo = partNo.replaceAll(/^\s+|\s+$/g, "");
        partNo = partNo.replaceAll(" ", "");
        partNo = partNo.replaceAll("\n", "");

        const partList = partNo.split(",");

        console.log("partList == ", partList);

        let dupCheck = new Array();

        let koDataList = new Array();

        if(partList != null && partList.length > 0) {

            for(let i=0; i < partList.length; i++) {

                let partNumber = partList[i].trim();

                if(dupCheck.indexOf(partNumber) == -1) {
                    dupCheck.push(partNumber);

                } else {
                    //종복된 것 있음
                    console.log(" 중복 ------- ", partNumber);
                    continue;
                }

                let koData = new Object();

                //한국데이터 조회
                $.ajax({
                    type : "post",
                    crossDomain: true,
                    //url : "/plmetc/vault/getPartInfoWithKO",
                    url : "/china/getPartInfoWithKO",
                    data : {
                        partNo : partNumber // partNo.trim()
                    },
                    success : function(data)
                    {
                        koData = data;

                        if(data != null) {
                            koDataList.push(koData);
                        }

                        console.log("한국 data - ", data);
                        console.log("data.length - ", data.length);
                    }
                });



            } // end for
        }


        console.log("koDataList - ", koDataList);


        //중국조회
        $.ajax({
            type : "get",
            //crossDomain: true,
            //url : "https://10.225.80.35:8070/part/getPartInfo",
            url : "https://vault-in.hdel.co.kr:8070/part/getPartALLInfo",
            //url : "/plmetc/vault/getPartInfoWithKO",
            data : {
                partNo : partNo.trim()
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                console.log("cn data - ", data);
                console.log("cn data.length - ", data.length);
                let str = "";

                if(data != null ) {

                    for(let i=0; i < data.length; i++) {

                        let cData = data[i];
                        let koData = new Object();


                        for(let k=0; k < koDataList.length; k++) {

                            let koObj = koDataList[k];
                            let koPartNo = koObj.PARTNO;

                            //console.log(koPartNo + "  !!!!!!!!!!!!!!  " + cData.PARTNO);

                            if(koPartNo == cData.PARTNO) {
                                koData = koObj;
                            }
                        }

                        let koGLCODE = koData.GLCODE;
                        let cnGLCODE = cData.GLCODE;

                        if(koData.PARTNO == null && cData.PARTNO == null) {
                            continue;
                        }

                        str += "<tr>";
                        str += "<td>" + koData.PARTNO + "</td>";

                        //한국
                        str += "<td>" + koData.BLOCKNO + "</td>";
                        str += "<td>" + koData.BLOCKNONAME + "</td>";
                        str += "<td>" + koData.DESCVAL + "</td>"; // PARTNAME
                        str += "<td>" + koData.SPEC + "</td>";
                        str += "<td>" + koData.UOM + "</td>"; // 단위
                        str += "<td>" + koData.PART_SIZE + "</td>"; // 단위
                        //PART_SIZE

                        if(koGLCODE != cnGLCODE) {
                            str += "<td class='bg-maroon color-palette'>" + koData.GLCODE + "</td>";
                        } else {
                            str += "<td>" + koData.GLCODE + "</td>";
                        }
                        str += "<td>" + koData.STATUS + "</td>"; // 상태


                        //중국
                        str += "<td>" + cData.PARTNO + "</td>";
                        str += "<td>" + cData.ENAME + "</td>";

                        if(koGLCODE != cnGLCODE) {
                            str += "<td class='bg-maroon color-palette'>" + cData.GLCODE + "</td>";
                        } else {
                            str += "<td>" + cData.GLCODE + "</td>";
                        }

                        str += "<td>" + cData.BLOCKNO + "</td>";
                        str += "<td>" + cData.UOM + "</td>";

                        //str += "<td>" + cData.DISAWAY + "</td>";
                        if(cData.DISAWAY == "Y") {
                            str += "<td class='bg-maroon color-palette'>" + cData.DISAWAY + "</td>";
                        } else {
                            str += "<td>" + cData.DISAWAY + "</td>";
                        }


                        str += "<td>" + cData.STATUS + "</td>"; // 상태

                        str += "<td>" + cData.PART_VERSION + "</td>";
                        str += "<td>" + cData.PART_SIZE + "</td>";
                        str += "<td>" + cData.DIV + "</td>";
                        str += "<td>" + cData.SPEC + "</td>";
                        str += "</td>";
                        str += "</tr>";

                    }

                    //<td bgcolor="#e6f2ff">DD</td>

                    $("#contentTable").append(str);


                    $("#infoTable").DataTable({
                        "responsive": true,
                        "lengthChange": true,
                        "pageLength": 50,     //페이지 당 글 개수 설정
                        "autoWidth": false, // 가로자동
                        "processing": true,
                        "destroy": true, // 테이블 재생성
                        "columnDefs" : [
                            {target: 6, width: 100}, //주석
                            {target: 7, width: 100}
                        ],
                        "buttons": ["csv", "excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


                }
            }
        });
    }

</script>

</html>
