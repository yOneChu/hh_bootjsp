package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ModulerTeam {


    public static ArrayList<HashMap<String, String>> findFolderList(String filePath, String fileName) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        System.out.println("filePath = " + filePath);
        System.out.println("fileName = " + fileName);
        String folderName = "$/Dev Project/모듈러구조개발팀";

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            con = VaultHDELDBConnection.getConnection();

            String sql = """
                    SELECT fm.TipFileBaseName AS FILENAMEEE,
                     fm.FileMasterID AS FILEMASTERID,
                     (SELECT VFF.FILENAME FROM vw_File VFF WHERE VFF.FILEITERATIONID = LI.IterationId) AS FILENAME,
                     (SELECT MAX(FF.VERSION) FROM FileResource FF WHERE FF.FileMasterId = fm.FileMasterID) AS FILEVERSION,
                     vf.CreateUserName AS CREATOR,
                     vf.CategoryName AS CATEGO,
                     F.FolderName AS FOLDERNAME,
                     F.VaultPath AS FPATH,
                     (SELECT FI.LIFECYCLESTATENAME FROM FileIteration FI WHERE FI.FILEITERATIONID = LI.IterationId) AS FSTATUS,
                     vf.ResourceId AS RESOURCEID,
                     FM.Hidden AS FHIDDEN
                     FROM FileMaster fm, Folder f, vw_LastIteration LI,vw_File vf
                     where F.FolderID = fm.FolderId
                     AND FM.FileMasterID = LI.MasterId
                     AND vf.FileIterationId = LI.IterationId
                     AND FM.HIDDEN = 0
                     AND f.vaultpath not like '%ko-KR%'
                     AND f.vaultpath not like '%Materials%'
                     --AND F.VaultPath LIKE '$/Dev Project/모듈러구조개발팀%'
                    """;

            if(filePath!= null && !"".equals(filePath)){

                filePath = folderName + "/" +  filePath;

                sql += "AND F.VaultPath LIKE '" + filePath + "%'";

            } else {
                sql += " AND F.VaultPath LIKE '$/Dev Project/모듈러구조개발팀%'";
            }


            if(fileName != null && !"".equals(fileName)){
                if (fileName.contains("*")) {
                    fileName = fileName.replace("*", "%");
                    sql += " AND fm.TipFileBaseName LIKE '" + fileName + "' ";
                } else {
                    sql += " AND fm.TipFileBaseName = '" + fileName + "' ";
                }
            }

                //fm.TipFileBaseName


            /*if( !"ALL".contains(folderName) ) {
                sql.append(" AND F.FolderID = '" +  folderName + "'    ");
            }

            if( fileName != null && !"".contains(fileName) ) {

                if(fileName.contains("*")) {
                    fileName = fileName.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]", ""); //특수문자 제거
                }

                fileName = fileName.trim().toUpperCase();
                sql.append(" AND fm.TipFileBaseName LIKE '" +  fileName + "%'    ");
            }*/
            //

            //System.out.println("renew fileName =="+ fileName);

            //sql.append(" AND FM.TIPFILEBASENAME= 'V0011221'  "); // 파일명
            //System.out.println("sql = " + sql.toString());

            //System.out.println("sql = " + sql.toString());

            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {

                //Row curRow = sheet.createRow(totalCnt);

                String FILENAME = rs.getString("FILENAME") == null ? "" : rs.getString("FILENAME"); // 사양
                String FILEMASTERID = rs.getString("FILEMASTERID") == null ? "" : rs.getString("FILEMASTERID"); // 사양

                String FILEVERSION = rs.getString("FILEVERSION") == null ? "" : rs.getString("FILEVERSION");
                String FolderName = rs.getString("FolderName") == null ? "" : rs.getString("FolderName"); //사양명

                String FPATH = rs.getString("FPATH") == null ? "" : rs.getString("FPATH");
                String FSTATUS = rs.getString("FSTATUS") == null ? "" : rs.getString("FSTATUS");

                String RESOURCEID = rs.getString("RESOURCEID") == null ? "" : rs.getString("RESOURCEID");
                String FHIDDEN = rs.getString("FolderName") == null ? "" : rs.getString("FHIDDEN");

                String CREATOR = rs.getString("CREATOR") == null ? "" : rs.getString("CREATOR"); // 작성자
                String CATEGORY = rs.getString("CATEGO") == null ? "" : rs.getString("CATEGO"); // 구분(조립품,부품)


                HashMap<String,String> rMap = new HashMap<String,String>();
                rMap.put("FILENAME", FILENAME);
                rMap.put("FILEMASTERID", FILEMASTERID);
                rMap.put("FILEVERSION", FILEVERSION);
                rMap.put("FolderName", FolderName);

                rMap.put("FPATH", FPATH);
                rMap.put("FSTATUS", FSTATUS);
                rMap.put("RESOURCEID", RESOURCEID);
                rMap.put("FHIDDEN", FHIDDEN);

                rMap.put("CREATOR", CREATOR);
                rMap.put("CATEGORY", CATEGORY);

                list.add(rMap);
            }

            } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultHDELDBConnection.disconnect(con, pstmt, rs);
        }

        return list;
    }
}
