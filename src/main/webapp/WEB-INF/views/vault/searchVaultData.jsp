<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.kyhslam.util.VaultCommonUtil" %>
<%@ page import="com.kyhslam.util.VaultDBConnection" %>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%

    ArrayList<HashMap<String, String>> folderList = new ArrayList<HashMap<String, String>>();

    ArrayList<HashMap<String,String>> resultList = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> resultList02 = new ArrayList<HashMap<String,String>>();

    Connection con 			= null;
    PreparedStatement pstmt = null;
    ResultSet rs 			= null;

    String str01 = "";
    String str02 = "";
    String strTotal = "";

    String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=HDEL;encrypt=false;";
    String id = "SA";
    String pw = "AutodeskVault@26200"; // "qwe123!@#"


    try {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        con = DriverManager.getConnection(url,id,pw);

        //System.out.println("con -- " + con);

        StringBuffer sql = new StringBuffer();
        //sql.append(" SELECT a.name \"사양\", a.TIT \"사양명\", c.NAME \"특성명\", c.DES \"특성값\", c.ouid, b.name FROM HDEL_SYSTEM.dosfld a ");

        sql.append(" SELECT fm.TipFileBaseName AS FILENAMEEE, ");
        sql.append(" (SELECT VFF.FILENAME FROM vw_File VFF WHERE VFF.FILEITERATIONID = LI.IterationId) AS FILENAME, ");
        sql.append(" (SELECT MAX(FF.VERSION) FROM FileResource FF WHERE FF.FileMasterId = fm.FileMasterID) AS FILEVERSION, "); // 버전

        sql.append(" vf.CreateUserName AS CREATOR, "); // 작성자
        sql.append(" vf.CategoryName AS CATEGO, "); // 조립품

        sql.append(" F.FolderName AS FOLDERNAME , "); //  -- 폴더명
        sql.append(" F.VaultPath AS FPATH, "); // -- 파일경로
        sql.append(" (SELECT FI.LIFECYCLESTATENAME FROM FileIteration FI WHERE FI.FILEITERATIONID = LI.IterationId) AS FSTATUS, ");  // -- 0: 작업진행중
        sql.append(" vf.ResourceId AS RESOURCEID, ");
        sql.append(" FM.Hidden AS FHIDDEN ");


        sql.append(" FROM FileMaster fm, Folder f, vw_LastIteration LI  "); //
        sql.append(" ,vw_File vf   ");

        //sql.append(" where F.FolderID = fm.FolderId AND  fm.FolderId = '86684' "); //
        sql.append(" where F.FolderID = fm.FolderId  ");
        sql.append(" AND FM.FileMasterID = LI.MasterId   ");
        sql.append(" AND vf.FileIterationId = LI.IterationId    ");

        //sql.append(" AND fm.FolderId = '84765' "); // STD_고속기종_0213
        //sql.append(" AND fm.FolderId = '137668' "); // A101A15 HOUSING CASTING

        sql.append(" AND FM.HIDDEN = 0 "); //
        sql.append(" AND f.vaultpath not like '%ko-KR%'  ");
        sql.append(" AND f.vaultpath not like '%Materials%'  ");
        //sql.append(" AND f.vaultpath like '%A101A TM%'  ");

        sql.append(" AND f.vaultpath like '%zzzzzzzzzzz%'  ");

        //
        //Materials
        //sql.append(" AND FM.TIPFILEBASENAME= 'V0011221'  "); // 파일명


        pstmt = con.prepareStatement(sql.toString());
        rs = pstmt.executeQuery();

        while(rs.next())
        {
            String FILENAME = rs.getString("FILENAME") == null ? "" : rs.getString("FILENAME"); // 사양
            String FILEVERSION = rs.getString("FILEVERSION") == null ? "" : rs.getString("FILEVERSION"); // 사양
            //FILEVERSION
            String FolderName = rs.getString("FolderName") == null ? "" : rs.getString("FolderName"); //사양명

            String FPATH = rs.getString("FPATH") == null ? "" : rs.getString("FPATH");
            String FSTATUS = rs.getString("FSTATUS") == null ? "" : rs.getString("FSTATUS");
            String RESOURCEID = rs.getString("RESOURCEID") == null ? "" : rs.getString("RESOURCEID");
            String FHIDDEN = rs.getString("FolderName") == null ? "" : rs.getString("FHIDDEN");

            String CREATOR = rs.getString("CREATOR") == null ? "" : rs.getString("CREATOR"); // 작성자
            String CATEGORY = rs.getString("CATEGO") == null ? "" : rs.getString("CATEGO"); // 구분(조립품,부품)


			/* String TITLE = rs.getString("TITLE") == null ? "" : rs.getString("TITLE");
			String DWGGUBUN = rs.getString("DWGGUBUN") == null ? "" : rs.getString("DWGGUBUN");
			String DWGNUMBER = rs.getString("DWGNUMBER") == null ? "" : rs.getString("DWGNUMBER");
			String DWGNAME = rs.getString("DWGNAME") == null ? "" : rs.getString("DWGNAME");
			String PARTLEVEL = rs.getString("PARTLEVEL") == null ? "" : rs.getString("PARTLEVEL");
			String BLOCKNO = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
			String APPAR = rs.getString("APPAR") == null ? "" : rs.getString("APPAR"); */

            HashMap<String,String> rMap = new HashMap<String,String>();
            rMap.put("FILENAME", FILENAME);
            rMap.put("FILEVERSION", FILEVERSION);

            rMap.put("FolderName", FolderName);
            rMap.put("FPATH", FPATH);
            rMap.put("FSTATUS", FSTATUS);

            rMap.put("RESOURCEID", RESOURCEID);
            rMap.put("FHIDDEN", FHIDDEN);

            rMap.put("CREATOR", CREATOR);
            rMap.put("CATEGORY", CATEGORY);

			/* rMap.put("TITLE", TITLE);
			rMap.put("DWGGUBUN", DWGGUBUN);
			rMap.put("DWGNUMBER", DWGNUMBER);
			rMap.put("DWGNAME", DWGNAME);
			rMap.put("PARTLEVEL", PARTLEVEL);
			rMap.put("BLOCKNO", BLOCKNO);
			rMap.put("APPAR", APPAR); */

            resultList.add(rMap);
        }

        folderList = VaultCommonUtil.getFolderList("123");


    } catch(Exception e) {
        e.printStackTrace();
    }finally {
        VaultDBConnection.disconnect(con,pstmt,rs);
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->

    <!-- <script data-jsfiddle="common" src="/js/jquery-1.11.0.min.js"></script> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />

    <title>한국v중국_자재비교</title>

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
    <style>
        body {
            font-family: 'Cascadia Code', sans-serif;
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


    <!-- Main Sidebar Container -->
    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>VAULT 조회 (HDEL > STD_고속기종_0213) </h1>
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

                <!-- 검색조건 -->
                <!-- <div class="card card-default"> -->
                <div class="card card-primary">
                    <div class="card-header">
                        <h3 class="card-title"> 검색 조건</h3>

                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                            <button type="button" class="btn btn-tool" data-card-widget="remove">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                    </div>

                    <!-- /.card-header -->
                    <div class="card-body" style="zoom:85%;">
                        <div class="row">
                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger">
                                    <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                    <h5>- 파일명 클릭 시 Thin-Client 화면으로 이동.</h5>
                                    <!-- - 전체 검색은 속도로 인하여 막아놓았음. <br> -->
                                    <h5>- 전체 데이터 필요하다면 데이터 요청 바람. </h5>
                                    <!-- - <font color="red"> 현재는 1건 씩만 검색이 되며 멀티검색은 현재 개발 중. </font> -->
                                </div>
                            </div>

                            <!-- <div class="input-group input-group-lg"> -->
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>폴더(Blcok.No)</label>
                                    <select id=folderName class="form-control select2bs4" style="width: 100%;">
                                        <!-- <option>ALL</option> -->
                                        <%

                                            for(int i=0; i < folderList.size(); i++) {
                                                HashMap<String,String> oMap = folderList.get(i);
                                                String FolderName = oMap.get("FolderName");
                                                String FPATH = oMap.get("FPATH");
                                                String folerId = oMap.get("folerId");
                                                String PARENTFOLDER = oMap.get("PARENTFOLDER");

                                        %>
                                        <option value="<%=folerId%>"><%=FolderName %></option>
                                        <%
                                            }
                                        %>

                                    </select>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>파일명</label>
                                    <input type="search" id="fileName" class="form-control" placeholder="확장자 제외하고 입력하시오." value="">
                                </div>
                            </div>


                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>


                    <div class="card-footer">
                        <!--  Visit <a href="https://select2.github.io/">Select2 documentation</a> for more examples and information about
                         the plugin. -->

                        <!-- <button class="btn btn-primary float-right" onclick="searchPopup()">검색(팝업)</button> -->
                        <button class="btn btn-primary float-right" style="margin-right: 5px;" onclick="search()">검색</button>



                    </div>

                </div>


                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">검색 결과</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">
                                <!-- <table id="infoTable" class="table table-bordered table-striped" style="height:400px;"> -->
                                <table id="infoTable" class="table table-bordered table-hover" style="font-family: NotoSans; font-size:15px;">
                                    <thead>
                                    <!-- bg-primary -->

                                    <tr class="bg-secondary">
                                        <th style="font-weight: bold; text-align: center;">파일명</th>
                                        <th style="font-weight: bold; text-align: center;">버전</th>

                                        <th style="font-weight: bold; text-align: center;">작성자</th>
                                        <th style="font-weight: bold; text-align: center;">범주</th>

                                        <th style="font-weight: bold; text-align: center;">폴더명</th>

                                        <!-- <th style="font-weight: bold; text-align: center;">경로</th> -->

                                        <th style="font-weight: bold; text-align: center;">상태</th>
                                        <!-- <th style="font-weight: bold; text-align: center;">숨김여부</th> -->

                                        <!--  <th style="font-weight: bold; text-align: center;">제목</th> -->
                                        <th style="font-weight: bold; text-align: center;">도면구분</th>
                                        <!-- <th style="font-weight: bold; text-align: center;">도면번호</th> -->
                                        <th style="font-weight: bold; text-align: center;">도면이름</th>

                                        <th style="font-weight: bold; text-align: center;">부품 LEVEL</th>
                                        <th style="font-weight: bold; text-align: center;">Block.NO</th>
                                        <th style="font-weight: bold; text-align: center;">APPAR</th>
                                        <th style="font-weight: bold; text-align: center;">PLM Version</th>
                                        <th style="font-weight: bold; text-align: center;">부품ID</th>
                                    </tr>
                                    </thead>

                                    <tbody id="contentTable">
                                    <%
                                        for(int i=0; i < resultList.size(); i++) {
                                            HashMap<String,String> oMap = resultList.get(i);

                                            String FILENAME 	= oMap.get("FILENAME");
                                            String FILEVERSION  = oMap.get("FILEVERSION");

                                            String CREATOR = oMap.get("CREATOR");
                                            String CATEGORY = oMap.get("CATEGORY");

                                            String FPATH = oMap.get("FPATH");
                                            String FolderName	= oMap.get("FolderName");
                                            String FSTATUS = oMap.get("FSTATUS");

                                            String RESOURCEID = oMap.get("RESOURCEID");
                                            //String FHIDDEN = oMap.get("FHIDDEN");

                                            //String title = oMap.get("TITLE");

                                            String DWGGUBUN = oMap.get("DWGGUBUN");

                                            //String DWGNUMBER = oMap.get("DWGNUMBER");

                                            String DWGNAME = oMap.get("DWGNAME");

                                            String PARTLEVEL = oMap.get("PARTLEVEL");
                                            String BLOCKNO = oMap.get("BLOCKNO");
                                            String APPAR = oMap.get("APPAR");

                                            //Vault 속성정보
                                            HashMap<String,String> bInfo = VaultCommonUtil.findFileBasicInfo(RESOURCEID);


                                    %>
                                    <tr>
                                        <td> <%=FILENAME %> </td>
                                        <td> <%=FILEVERSION %> </td>

                                        <td> <%=CREATOR %> </td>
                                        <td> <%=CATEGORY %> </td>

                                        <td> <%=FolderName %> </td>
                                        <%-- <td> <%=FPATH %> </td> --%>
                                        <td> <%=FSTATUS %> </td>
                                        <%-- <td> <%=FHIDDEN %> </td> --%>

                                        <%-- <td> <%=bInfo.get("TITLE") == null ? "" : bInfo.get("TITLE") %> </td> --%>
                                        <td> <%=bInfo.get("DWGGUBUN") == null ? "" : bInfo.get("DWGGUBUN") %> </td>
                                        <%-- <td> <%=bInfo.get("DWGNUMBER") == null ? "" : bInfo.get("DWGNUMBER") %> </td> --%>
                                        <td> <%=bInfo.get("DWGNAME") == null ? "" : bInfo.get("DWGNAME") %> </td>


                                        <td> <%=bInfo.get("PARTLEVEL") == null ? "" : bInfo.get("PARTLEVEL") %> </td>
                                        <td> <%=bInfo.get("BLOCKNO") == null ? "" : bInfo.get("BLOCKNO") %> </td>
                                        <td> <%=bInfo.get("APPAR") == null ? "" : bInfo.get("APPAR") %> </td>

                                        <td> <%=bInfo.get("PLMVERSION") == null ? "" : bInfo.get("PLMVERSION") %> </td>
                                        <td> <%=bInfo.get("FILEMASTERID") == null ? "" : bInfo.get("FILEMASTERID") %> </td>
                                    </tr>
                                    <%
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

    function goView(fileId) {
        //let fileId = $(this).find("td:eq(12)").text();
        console.log("fileId -- ", fileId);

        if(fileId != null && "" != fileId) {
            let url = "http://10.225.80.35/AutodeskTC/HDEL/explore/file/";
            url += fileId;
            let wintab = window.open(url);
        }
    }


    //검색
    function search()
    {
        let folderName = $("#folderName").val(); // 폴더명
        let fileName = $("#fileName").val(); // 파일명
        //fileName

        /* if(productNumber == null || "" == productNumber) {
            console.log(productNumber);
            alert("제품번호를 입력하세요.");
            return;
        } */


        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        $.ajax({
            type : "post",
            url : "searchVaultViewJson.jsp",
            //url : "http://localhost/plmetc/vault/getJQPRfromSAP",
            data : {
                folderName : folderName,
                fileName : fileName
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                console.log("data - ", data);
                let str = "";

                let dataSize = data.COUNT;
                let result = data.RESULT;
                console.log("result.length - ", result.length);

                if(dataSize != null && dataSize > 0 && dataSize < 10000) {
                    //10000개 이상 검색되는 경우는 보류
                }

                if(dataSize != null && dataSize> 0 && result != null && result.length > 0) {

                    for(let i=0; i < result.length; i++) {

                        let info = result[i];
                        //console.log('info --- ' , info[i]);

                        str += "<tr>";
                        //str += "<td> <a href='#'> " + result[i].FILENAME + " </a></td>";
                        //str += "<td onClick='goView()'> " + result[i].FILENAME + "</td>";

                        let fileId = result[i].FILEMASTERID;

                        //str += "<td> <a href='javascript:goView(" + fileId + "' )> " + result[i].FILENAME + "</a></td>";
                        str += "<td> <a href='javascript:goView(" + fileId + ")'> " + result[i].FILENAME + "</a></td>";
                        str += "<td>" + result[i].FILEVERSION + "</td>";

                        str += "<td>" + result[i].CREATOR + "</td>";
                        str += "<td>" + result[i].CATEGORY + "</td>";

                        str += "<td>" + result[i].FolderName + "</td>";
                        /* str += "<td>" + result[i].FPATH + "</td>"; */
                        str += "<td>" + result[i].FSTATUS + "</td>";
                        //str += "<td>" + result[i].FHIDDEN + "</td>";


                        //str += "<td>" + result[i].TITLE + "</td>";
                        str += "<td>" + result[i].DWGGUBUN + "</td>";
                        //str += "<td>" + result[i].DWGNUMBER + "</td>";

                        str += "<td>" + result[i].DWGNAME + "</td>";


                        str += "<td>" + result[i].PARTLEVEL + "</td>";
                        str += "<td>" + result[i].BLOCKNO + "</td>";
                        str += "<td>" + result[i].APPAR + "</td>";

                        str += "<td>" + result[i].PLMVERSION + "</td>";
                        str += "<td>" + result[i].FILEMASTERID + "</td>";

                        str += "</td>";
                    }


                    $("#contentTable").append(str);

                    $("#infoTable").DataTable({
                        "responsive": true,
                        "lengthChange": true,
                        "pageLength": 50,     //페이지 당 글 개수 설정
                        "autoWidth": false, // 가로자동
                        "processing": true,
                        "destroy": true, // 테이블 재생성
                        "columnDefs" : [
                            {target: 1, width: 100}, //주석
                            {target: 6, width: 100}
                        ],
                        "buttons": ["csv", "excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                } else {
                    alert("검색결과가 없습니다.");
                }
            } // end success;
        });
    }


</script>

</html>
