package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class VaultCommonUtil {

    /**
     * @category 하위폴더 리스트
     * @param folderId
     * @return
     */
    public static ArrayList<HashMap<String, String>> getFolderList(String folderId) {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String,String>>();

        folderId = "";

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=HDEL;encrypt=false;";
        String id  = "SA";
        String pw  = "AutodeskVault@26200"; // "qwe123!@#"

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            con = DriverManager.getConnection(url,id,pw);

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT F.FolderName AS FolderName , F.VaultPath FPATH ,F.FolderID AS OID,    ");
            sql.append(" (SELECT A.FOLDERNAME FROM FOLDER A WHERE A.FOLDERID = F.ParentFolderId) AS PARENTFOLDER   ");
            sql.append(" FROM Folder f   ");

            //sql.append(" WHERE f.ParentFolderId  = '84765'    ");


            if(folderId != null && !"".equals(folderId)) {
                sql.append(" WHERE f.ParentFolderId '" +  folderId + "'    ");
            } else {
                sql.append(" WHERE f.ParentFolderId  = '84765'    ");
            }

            sql.append(" ORDER BY F.FolderName   ");

            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                String FolderName = rs.getString("FolderName") == null ? "" : rs.getString("FolderName"); // 사양
                String FPATH = rs.getString("FPATH") == null ? "" : rs.getString("FPATH"); //사양명

                String folerId = rs.getString("OID") == null ? "" : rs.getString("OID");
                String PARENTFOLDER = rs.getString("PARENTFOLDER") == null ? "" : rs.getString("PARENTFOLDER");

                HashMap<String,String> rMap = new HashMap<String,String>();
                rMap.put("FolderName", FolderName);
                rMap.put("FPATH", FPATH);
                rMap.put("folerId", folerId);
                rMap.put("PARENTFOLDER", PARENTFOLDER);

                resultList.add(rMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DynaUtils.close(rs,pstmt,con);
            VaultDBConnection.disconnect(con,pstmt,rs);
        }

        return resultList;
    }


    //조회
    /**
     * @category 파일명, 폴더id로 조회
     * @param fileName
     * @param folerId
     * @return
     */
    public static ArrayList<HashMap<String, String>> findFile(String fileName, String folerId) {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String,String>>();

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=HDEL;encrypt=false;";
        String id  = "SA";
        String pw  = "AutodeskVault@26200"; // "qwe123!@#"

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            con = DriverManager.getConnection(url,id,pw);

            StringBuffer sql = new StringBuffer();
            //sql.append(" SELECT a.name \"사양\", a.TIT \"사양명\", c.NAME \"특성명\", c.DES \"특성값\", c.ouid, b.name FROM HDEL_SYSTEM.dosfld a ");

            sql.append(" SELECT fm.TipFileBaseName AS FILENAMEEE, "); //파일명(임시)
            sql.append(" (SELECT VFF.FILENAME FROM vw_File VFF WHERE VFF.FILEITERATIONID = LI.IterationId) AS FILENAME, "); // 파일명
            sql.append(" (SELECT MAX(FF.VERSION) FROM FileResource FF WHERE FF.FileMasterId = fm.FileMasterID) AS FILEVERSION, "); // 버전

            sql.append(" vf.CreateUserName AS CREATOR, "); // 작성자
            sql.append(" vf.CategoryName AS CATEGO, "); // 조립품


            sql.append(" F.FolderName AS FOLDERNAME , "); //  -- 폴더명
            sql.append(" F.VaultPath AS FPATH, "); // -- 파일경로
            sql.append(" (SELECT FI.LIFECYCLESTATENAME FROM FileIteration FI WHERE FI.FILEITERATIONID = LI.IterationId) AS FSTATUS, ");  // -- 0: 작업진행중
            sql.append(" FM.Hidden AS FHIDDEN "); //

            sql.append(" FROM FileMaster fm, Folder f, vw_LastIteration LI  ");
            sql.append(" ,vw_File vf   ");
            //sql.append(" where F.FolderID = fm.FolderId AND  fm.FolderId = '86684' ");
            sql.append(" where F.FolderID = fm.FolderId  "); //
            sql.append(" AND FM.FileMasterID = LI.MasterId   ");
            sql.append(" AND vf.FileIterationId = LI.IterationId    ");
            sql.append(" AND FM.HIDDEN = 0 "); //
            //sql.append(" AND fm.FolderId = '86684' ");


            if( !"ALL".contains(folerId) ) {
                sql.append(" AND F.FolderID = '" +  folerId + "'    "); //
            }

            if( fileName != null && !"".contains(fileName) ) {
                fileName = fileName.trim().toUpperCase();
                sql.append(" AND fm.TipFileBaseName LIKE '" +  fileName + "%'    "); //
            }


            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while(rs.next())
            {
                String FILENAME = rs.getString("FILENAME") == null ? "" : rs.getString("FILENAME"); // 파일명
                String FILEVERSION = rs.getString("FILEVERSION") == null ? "" : rs.getString("FILEVERSION"); // 버전
                String FolderName = rs.getString("FOLDERNAME") == null ? "" : rs.getString("FOLDERNAME"); // 폴더명

                String FPATH = rs.getString("FPATH") == null ? "" : rs.getString("FPATH"); // 경로
                String FSTATUS = rs.getString("FSTATUS") == null ? "" : rs.getString("FSTATUS"); // 상태

                String CREATOR = rs.getString("CREATOR") == null ? "" : rs.getString("CREATOR"); // 작성자
                String CATEGORY = rs.getString("CATEGO") == null ? "" : rs.getString("CATEGO"); // 구분(조립품,부품)


                HashMap<String,String> rMap = new HashMap<String,String>();
                rMap.put("FILENAME", FILENAME);
                rMap.put("FILEVERSION", FILEVERSION);
                rMap.put("FolderName", FolderName);

                rMap.put("FPATH", FPATH);
                rMap.put("FSTATUS", FSTATUS);

                rMap.put("CREATOR", CREATOR);
                rMap.put("CATEGORY", CATEGORY);

                resultList.add(rMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DynaUtils.close(rs,pstmt,con);
            VaultDBConnection.disconnect(con,pstmt,rs);
        }

        return resultList;
    }

    //파일의 속성정보
    /**
     * @category 부품의 속성정보
     * @param fileResourceId
     * @return
     */
    public static HashMap<String,String> findFileBasicInfo(String fileResourceId ) {

        //System.out.println("findFileBasicInfo --- " + fileResourceId);

        Connection con 			= null;
        PreparedStatement pstmt = null;
        Statement smt = null;
        ResultSet rs 			= null;

        String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=HDEL;encrypt=false;";
        String id  = "SA";
        String pw  = "AutodeskVault@26200"; // "qwe123!@#"


        HashMap<String,String> rMap = new HashMap<String,String>();

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            con = DriverManager.getConnection(url,id,pw);

            StringBuffer sql = new StringBuffer();


            //sql.append(" SELECT CAST(A.DisplayName AS NVARCHAR(128)) AS DISP, CAST(P.Value AS NVARCHAR(128)) AS VAL");
            //sql.append(" SELECT A.DisplayName AS DISP, P.Value AS VAL");
            //sql.append(" SELECT A.DisplayName, P.Value, A.ContentSourcePropertyMoniker AS PKEY ");

            sql.append(" SELECT DISP = CAST(A.DisplayName AS NVARCHAR(128)) , ");
            sql.append(" VAL = CAST(P.Value AS NVARCHAR(128)), ");
            sql.append(" PKEY = CAST(A.ContentSourcePropertyMoniker AS NVARCHAR(128)) ");


            sql.append(" FROM ContentSourceProperties P, ContentSourcePropertyDef A ");
            sql.append(" where P.ContentSourcePropertyMoniker = A.ContentSourcePropertyMoniker ");
            sql.append(" AND A.ContentSourceID = '4' ");
            sql.append(" AND P.EntityId = '" +  fileResourceId + "' ");


            //System.out.println("sql -- " + sql.toString());

            smt = con.createStatement();
            //pstmt = con.prepareStatement(sql.toString());

            rs = smt.executeQuery(sql.toString());


            while(rs.next())
            {

                String disp 	= (String)rs.getObject("DISP") == null ? "" : (String)rs.getObject("DISP");
                String value 	= (String)rs.getObject("VAL") == null ? "" : (String)rs.getObject("VAL");
                String key 		= rs.getString("PKEY") == null ? "" : rs.getString("PKEY");


                //System.out.println(disp + " > " + value + " > " + key);
                //System.out.println(disp + " > " + key + " > " + value);

                //제목

                if("2!{F29F85E0-4FF9-1068-AB91-08002B27B3D9}!nvarchar".equals(key)) {
                    //String TITLE = rs.getString("DisplayName") == null ? "" : rs.getString("DisplayName"); // 제목
                    rMap.put("TITLE", value);
                } else if("도면구분!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    rMap.put("DWGGUBUN", value);
                } else if("도면번호!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    rMap.put("DWGNUMBER", value);
                } else if("도명!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    rMap.put("DWGNAME", value);
                } else if("부품 LEVEL!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    rMap.put("PARTLEVEL", value);
                } else if("APPAR!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    rMap.put("APPAR", value);
                } else if("Block No!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    rMap.put("BLOCKNO", value);
                } else if("Part Number!{32853F0F-3444-11D1-9E93-0060B03C1CA6}!nvarchar".equals(key)) {
                    //부품번호
                    rMap.put("PARTNUMBER", value);
                } else if("PLM Version!{D5CDD505-2E9C-101B-9397-08002B2CF9AE}!nvarchar".equals(key)) {
                    //PLM버전
                    rMap.put("PLMVERSION", value);
                }


            } // end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return rMap;
    }


    /**
     * @category 도면정보_조회
     * @param dwgNo
     * @return
     */
    public static HashMap<String,String> getAutoCADInfo(String dwgNo) {

        HashMap<String, String> result = new HashMap<String, String>();

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        try {

            //con = DBconnectionInfo.getPDM_DBConnection();
            con = PLMDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append("with T AS ( ");
            sql.append(" select A.VF$OUID AS OID from AUTOCAD_FILE$VF A, AUTOCAD_FILE$ID B ");
            sql.append(" where A.VF$IDENTITY = B.ID$OUID and A.VF$OUID = B.ID$WIP ");
            sql.append(" ) ");

            sql.append(" SELECT A.VF$OUID, A.MD$NUMBER AS DWGNO, A.MD$STATUS AS STATUS, A.VF$VERSION AS VERSION, A.STD_CODE AS APPAR, ");
            sql.append(" COD(A.GC_PRODUCT), A.MD$DESC AS PARTNAME, A.MD$MDATE AS MOD_DATE, A.MD$CDATE AS CRE_DATE, ");
            sql.append(" NVL((SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(A.FO_BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(A.FO_BLOCKNO, 12))))), '-') AS BLOCKNO_NO, ");
            sql.append(" NVL((SELECT BLOCKNO$SF.MD$DESC FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(A.FO_BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(A.FO_BLOCKNO, 12))))), '-') AS BLOCKNO_NAME, ");
            sql.append(" (SELECT MAX(TO_NUMBER(B.VF$VERSION)) FROM AUTOCAD_FILE$VF B WHERE B.MD$STATUS = 'RLS' AND B.MD$NUMBER = ?) AS MAXVERSION, ");
            sql.append(" CODN(A.DRAWINGTYPE) AS DWGTYPE, ");
            sql.append(" CODN(A.DRAWINGSOURCE) AS DWGFROM ");



            sql.append(" FROM AUTOCAD_FILE$VF A ");
            sql.append(" WHERE A.VF$OUID IN (SELECT OID FROM T) ");
            sql.append(" AND A.MD$NUMBER = ? ");


            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, dwgNo);
            pstmt.setString(2, dwgNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String DWGNO = rs.getString("DWGNO") == null ? "" : rs.getString("DWGNO");
                String STATUS = rs.getString("STATUS") == null ? "" : rs.getString("STATUS");

                String VERSION = rs.getString("VERSION") == null ? "" : rs.getString("VERSION");
                String APPAR = rs.getString("APPAR") == null ? "" : rs.getString("APPAR");
                String PARTNAME = rs.getString("PARTNAME") == null ? "" : rs.getString("PARTNAME");
                String BLOCKNO_NO = rs.getString("BLOCKNO_NO") == null ? "" : rs.getString("BLOCKNO_NO");
                String BLOCKNO_NAME = rs.getString("BLOCKNO_NAME") == null ? "" : rs.getString("BLOCKNO_NAME");
                String MAXVERSION = rs.getString("MAXVERSION") == null ? "" : rs.getString("MAXVERSION"); //WIP 제외한 마지막 버전
                String DWGTYPE = rs.getString("DWGTYPE") == null ? "" : rs.getString("DWGTYPE");
                String DWGFROM = rs.getString("DWGFROM") == null ? "" : rs.getString("DWGFROM");
                String MOD_DATE = rs.getString("MOD_DATE") == null ? "" : rs.getString("MOD_DATE");
                String CRE_DATE = rs.getString("CRE_DATE") == null ? "" : rs.getString("CRE_DATE");


                result.put("DWGNO", DWGNO);
                result.put("STATUS", STATUS);
                result.put("VERSION", VERSION);
                result.put("APPAR", APPAR);
                result.put("PARTNAME", PARTNAME);
                result.put("BLOCKNO_NO", BLOCKNO_NO);
                result.put("BLOCKNO_NAME", BLOCKNO_NAME);
                result.put("MAXVERSION", MAXVERSION);
                result.put("DWGTYPE", DWGTYPE);
                result.put("DWGFROM", DWGFROM);
                result.put("MOD_DATE", MOD_DATE);
                result.put("CRE_DATE", CRE_DATE);
            }

            //System.out.println("result --> " + result);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DynaUtils.close(rs, pstmt, con);
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    /**
     * 설계복사화면에서 호기 입력 시 제품의 버전, 상태 조회
     * @param productNo
     * @return
     */
    public static HashMap<String, String> findProductInfo(String productNo) {


        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        HashMap<String, String> result = new HashMap<String, String>();


        try {

            con = PLMDBConnection.getConnection();
            //con = DBconnectionInfo.getPDM_DBConnection();



            StringBuffer sql = new StringBuffer();

            sql.append(" with ouid as ");
            sql.append(" ( select A.vf$ouid from product$vf A, product$id B ");
            sql.append("  	where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip ");
            sql.append("  	and A.md$number in ( ? )	");
            sql.append(" ) ");
            sql.append(" SELECT b.md$Number AS HOGI, ");
            sql.append(" b.VF$VERSION AS VERSION,	");
            sql.append(" b.MD$STATUS AS STATUS	");
            sql.append(" FROM  product$vf b ");
            sql.append(" WHERE b.vf$ouid = (select * from ouid)  ");

            System.out.println(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productNo);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String hogi = rs.getString("HOGI");
                String version = rs.getString("VERSION");
                String status = rs.getString("STATUS") == null ? "" : rs.getString("STATUS");

                result.put("HOGI", hogi);
                result.put("VERSION", version);
                result.put("STATUS", status);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    /**
     * 제품의 하위 BOM 중 수정된 자재만 조회
     * @param productNo
     * @return
     */
    public static ArrayList<PartInfoDTO> findModParts(String productNo) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {

            //con = DBconnectionInfo.getPDM_DBConnection();
            con = PLMDBConnection.getConnection();
            StringBuffer sql = new StringBuffer();

            sql.append(" with ouid as ");
            sql.append(" ( select A.vf$ouid AS VFOID from product$vf A, product$id B ");
            sql.append("  	where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip ");
            sql.append("  	and A.md$number in ( ? )	");
            sql.append(" ) ");

            sql.append(" SELECT ");
            sql.append(" (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO, ");
            sql.append(" NP.MD$NUMBER AS PARTNO, ");
            sql.append(" cod(NP.NATION) AS NATION, ");
            sql.append(" NP.MD$DESC AS PARTNAME, ");
            sql.append(" NP.VF$VERSION AS VERSION, ");
            sql.append(" NVL(NP.G_L_CODE, '') AS GLCODE, ");
            sql.append(" NVL(NP.SPEC, '') AS SPEC, ");
            sql.append(" NVL(NP.PART_SIZE, '') AS PART_SIZE, ");
            sql.append(" (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO, ");
            sql.append(" PE.QTY AS QTY, ");
            sql.append(" NVL(COD(NP.UOM), '') AS UOM, ");
            sql.append(" PE.CMT AS CMT, ");
            sql.append(" VP.UCHECK AS UCHECK , ");
            sql.append(" NVL(COD(NP.ORIGIN_DIV), '') AS DIV ");

            sql.append(" FROM  PARTOFEBOM PE ");
            sql.append(" INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID  ");
            sql.append(" LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID ");
            sql.append(" WHERE PE.PRODUCTOUID = (SELECT VFOID FROM ouid) ");
            sql.append(" AND VP.UCHECK = '1' ");
            sql.append(" ORDER BY TO_NUMBER(PE.SEQ) ");


            System.out.println(sql.toString());
            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productNo);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION") == null ? "" : rs.getString("VERSION");

                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String SPEC = rs.getString("SPEC") == null ? "" : rs.getString("SPEC");
                String BLOCKNO = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");

                String QTY = rs.getString("QTY") == null ? "" : rs.getString("QTY");
                String UOM = rs.getString("UOM") == null ? "" : rs.getString("UOM");

                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");

                PartInfoDTO partInfoDTO = new PartInfoDTO();
                partInfoDTO.setPartNo(PARTNO);
                partInfoDTO.setPartName(PARTNAME);
                partInfoDTO.setVersion(VERSION);
                partInfoDTO.setGlCode(GLCODE);
                partInfoDTO.setSpec(SPEC);
                partInfoDTO.setBlockNo(BLOCKNO);
                partInfoDTO.setQty(QTY);
                partInfoDTO.setUom(UOM);
                partInfoDTO.setCmt(CMT);

                result.add(partInfoDTO);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;

    }

}
