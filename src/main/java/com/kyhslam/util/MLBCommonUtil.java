package com.kyhslam.util;

import com.kyhslam.dto.CodeInfoDTO;
import com.kyhslam.dto.PartDTO;
import com.kyhslam.dto.PartInfoDTO;
import org.springframework.context.annotation.Description;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class MLBCommonUtil {


    /**
     * apiNote PartNo로 부품 속성정보 조회 (단건)
     * @param partNo
     * @return
     */
    public static PartInfoDTO findPartOneWithPartNo(String partNo) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        PartInfoDTO result = new PartInfoDTO();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE,
                SUBSTR(A.MD$CDATE, 0, 8) AS CREDATE,
                SUBSTR(A.MD$MDATE, 0, 8) AS MODDATE
                --A.*
                FROM NORMALPART$VF A
                --WHERE A.VF$OUID IN (SELECT * FROM OUID)
                WHERE A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip 
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                --AND A.PART_STATUS = '2466425004'
                AND A.MD$NUMBER = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String BLOCKNO = rs.getString("BLOCKNO");
                String SPEC = rs.getString("SPEC");
                String PARTSIZE = rs.getString("PARTSIZE");
                String VERSION = rs.getString("VERSION");
                String PART_STATUS = rs.getString("PART_STATUS");
                String NATION = rs.getString("NATION");
                String GLCODE = rs.getString("GL_CODE");
                String UOM =  rs.getString("UOM");

                //PartInfoDTO dto  = new PartInfoDTO();
                result.setOid(OID);
                result.setPartNo(PARTNO);
                result.setPartName(PARTNAME);
                result.setPartSize(PARTSIZE);
                result.setBlockNo(BLOCKNO);
                result.setSpec(SPEC);
                result.setGlCode(GLCODE);
                result.setVersion(VERSION);
                result.setStatus(PART_STATUS);
                result.setNation(NATION);
                result.setUom(UOM);

                //result.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }





    /**
     * PartNo로 부품 속성정보 조회
     * @param partNo
     * @return
     */
    public static ArrayList<PartInfoDTO> findPartWithPartNo(String partNo) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  with ouid as
                     ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                       where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                       --and ( md$number in ( '18900360G0700') )
                        --AND SUBSTR(A.MD$CDATE, 0, 8) IN( ? )
                     )
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE
                --A.*
                FROM NORMALPART$VF A
                WHERE A.VF$OUID IN (SELECT * FROM OUID)
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                AND A.PART_STATUS = '2466425004'
                AND A.MD$NUMBER = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String BLOCKNO = rs.getString("BLOCKNO");
                String SPEC = rs.getString("SPEC");
                String PARTSIZE = rs.getString("PARTSIZE");
                String VERSION = rs.getString("VERSION");
                String PART_STATUS = rs.getString("PART_STATUS");
                String NATION = rs.getString("NATION");
                String GLCODE = rs.getString("GL_CODE");

                PartInfoDTO dto  = new PartInfoDTO();
                dto.setOid(OID);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setPartSize(PARTSIZE);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setGlCode(GLCODE);
                dto.setVersion(VERSION);
                dto.setStatus(PART_STATUS);
                dto.setNation(NATION);

                result.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    /**
     * @apiNote 품번으로 속성정보 조회
     * @param partNo
     * @return
     */
    public static PartDTO findPartOneWithPartNoV2(String partNo) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        PartDTO result = new PartDTO();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE,
                SUBSTR(A.MD$CDATE, 0, 8) AS CREDATE,
                SUBSTR(A.MD$MDATE, 0, 8) AS MODDATE
                --A.*
                FROM NORMALPART$VF A, NORMALPART$id B
                --WHERE A.VF$OUID IN (SELECT * FROM OUID)
                WHERE A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip 
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                --AND A.PART_STATUS = '2466425004'
                AND A.MD$NUMBER = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo.trim());
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String BLOCKNO = rs.getString("BLOCKNO");
                String SPEC = rs.getString("SPEC");
                String PARTSIZE = rs.getString("PARTSIZE");
                String VERSION = rs.getString("VERSION");
                String PART_STATUS = rs.getString("PART_STATUS");
                String NATION = rs.getString("NATION");
                String GLCODE = rs.getString("GL_CODE");
                String UOM =  rs.getString("UOM");
                String DESIGN_USE = rs.getString("DESIGN_USE");
                String COST_USE = rs.getString("COST_USE");
                String CREDATE =  rs.getString("CREDATE");
                String MODDATE = rs.getString("MODDATE");
                String ORIGIN_DIV = rs.getString("ORIGIN_DIV");


                //PartInfoDTO dto  = new PartInfoDTO();
                result.setOid(OID);
                result.setPartNo(PARTNO);
                result.setPartName(PARTNAME);
                result.setPartSize(PARTSIZE);
                result.setBlockNo(BLOCKNO);
                result.setSpec(SPEC);
                result.setGlCode(GLCODE);
                result.setVersion(VERSION);
                result.setStatus(PART_STATUS);
                result.setNation(NATION);
                result.setUom(UOM);
                result.setCreDate(CREDATE);
                result.setModDate(MODDATE);
                result.setDesign(DESIGN_USE);
                result.setCost(COST_USE);
                result.setOriginDiv(ORIGIN_DIV);

                //result.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    /**
     * @apiNote 품번 목록으로 속성정보 조회 (다건)
     * @param PartNoList 품번들을 ','로 연결한 문자열 (예: "A1234,B5678,C9012")
     * @return ArrayList<PartDTO>
     */
    public static ArrayList<PartDTO> findPartInfoWithList(String PartNoList) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartDTO> result = new ArrayList<PartDTO>();

        if (PartNoList == null || PartNoList.trim().isEmpty()) {
            return result;
        }

        // 1. ','로 분리 후 공백 제거 / 빈값, 중복 제외
        ArrayList<String> partNoArr = new ArrayList<String>();
        for (String partNo : PartNoList.split(",")) {
            String trimPartNo = partNo.trim();
            if (!trimPartNo.isEmpty() && !partNoArr.contains(trimPartNo)) {
                partNoArr.add(trimPartNo);
            }
        }

        if (partNoArr.isEmpty()) {
            return result;
        }

        // 2. IN 조건절에 사용할 바인딩 변수 생성 (?, ?, ...)
        StringBuilder inParams = new StringBuilder();
        for (int i = 0; i < partNoArr.size(); i++) {
            if (i > 0) {
                inParams.append(", ");
            }
            inParams.append("?");
        }

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE,
                SUBSTR(A.MD$CDATE, 0, 8) AS CREDATE,
                SUBSTR(A.MD$MDATE, 0, 8) AS MODDATE
                --A.*
                FROM NORMALPART$VF A, NORMALPART$id B
                --WHERE A.VF$OUID IN (SELECT * FROM OUID)
                WHERE A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip 
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                """
                + "AND A.MD$NUMBER IN (" + inParams.toString() + ")\n";

            pstmt = con.prepareStatement(sql);

            // 3. 품번 파라미터 바인딩
            for (int i = 0; i < partNoArr.size(); i++) {
                pstmt.setString(i + 1, partNoArr.get(i));
            }

            System.out.println("sql.toString() = " + sql.toString());
            
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String BLOCKNO = rs.getString("BLOCKNO");
                String SPEC = rs.getString("SPEC");
                String PARTSIZE = rs.getString("PARTSIZE");
                String VERSION = rs.getString("VERSION");
                String PART_STATUS = rs.getString("PART_STATUS");
                String NATION = rs.getString("NATION");
                String GLCODE = rs.getString("GL_CODE");
                String UOM =  rs.getString("UOM");
                String DESIGN_USE = rs.getString("DESIGN_USE");
                String COST_USE = rs.getString("COST_USE");
                String CREDATE =  rs.getString("CREDATE");
                String MODDATE = rs.getString("MODDATE");
                String ORIGIN_DIV = rs.getString("ORIGIN_DIV");

                PartDTO dto = new PartDTO();
                dto.setOid(OID);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setPartSize(PARTSIZE);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setGlCode(GLCODE);
                dto.setVersion(VERSION);
                dto.setStatus(PART_STATUS);
                dto.setNation(NATION);
                dto.setUom(UOM);
                dto.setCreDate(CREDATE);
                dto.setModDate(MODDATE);
                dto.setDesign(DESIGN_USE);
                dto.setCost(COST_USE);
                dto.setOriginDiv(ORIGIN_DIV);

                result.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    /**
     * @apiNote 품번 목록으로 속성정보 조회 (다건) - CONNECTION 개선한 방법
     * @param PartNoList 품번들을 ','로 연결한 문자열 (예: "A1234,B5678,C9012")
     * @return ArrayList<PartDTO>
     */
    public static ArrayList<PartDTO> findPartInfoWithList_v2(String PartNoList) {

        ArrayList<PartDTO> result = new ArrayList<PartDTO>();

        if (PartNoList == null || PartNoList.trim().isEmpty()) {
            return result;
        }

        // 1. ','로 분리 후 공백 제거 / 빈값, 중복 제외
        ArrayList<String> partNoArr = new ArrayList<String>();
        for (String partNo : PartNoList.split(",")) {
            String trimPartNo = partNo.trim();
            if (!trimPartNo.isEmpty() && !partNoArr.contains(trimPartNo)) {
                partNoArr.add(trimPartNo);
            }
        }

        if (partNoArr.isEmpty()) {
            return result;
        }

        // 2. IN 조건절에 사용할 바인딩 변수 생성 (?, ?, ...)
        StringBuilder inParams = new StringBuilder();
        for (int i = 0; i < partNoArr.size(); i++) {
            if (i > 0) {
                inParams.append(", ");
            }
            inParams.append("?");
        }

        String sql = """
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE,
                SUBSTR(A.MD$CDATE, 0, 8) AS CREDATE,
                SUBSTR(A.MD$MDATE, 0, 8) AS MODDATE
                FROM NORMALPART$VF A, NORMALPART$id B
                WHERE A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip 
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                """
                + "AND A.MD$NUMBER IN (" + inParams.toString() + ")\n";


        try (Connection conn = PLMDBPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. 품번 파라미터 바인딩
            for (int i = 0; i < partNoArr.size(); i++) {
                pstmt.setString(i + 1, partNoArr.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {



                // 🌟 핵심 로직: ResultSet을 순회하며 Map으로 변환
                while (rs.next()) {

                    String OID = rs.getString("OID");
                    String PARTNO = rs.getString("PARTNO");
                    String PARTNAME = rs.getString("PARTNAME");
                    String BLOCKNO = rs.getString("BLOCKNO");
                    String SPEC = rs.getString("SPEC");
                    String PARTSIZE = rs.getString("PARTSIZE");
                    String VERSION = rs.getString("VERSION"); //자재코드 Ownership
                    String PART_STATUS = rs.getString("PART_STATUS"); // 활성상태
                    String NATION = rs.getString("NATION");
                    String GLCODE = rs.getString("GL_CODE");
                    String UOM =  rs.getString("UOM");
                    String DESIGN_USE = rs.getString("DESIGN_USE"); // 설계사용
                    String COST_USE = rs.getString("COST_USE"); // 견적사용
                    String CREDATE =  rs.getString("CREDATE");
                    String MODDATE = rs.getString("MODDATE");
                    String ORIGIN_DIV = rs.getString("ORIGIN_DIV"); // 최초구분

                    PartDTO dto = new PartDTO();
                    dto.setOid(OID);
                    dto.setPartNo(PARTNO);
                    dto.setPartName(PARTNAME);
                    dto.setPartSize(PARTSIZE);
                    dto.setBlockNo(BLOCKNO);
                    dto.setSpec(SPEC);
                    dto.setGlCode(GLCODE);
                    dto.setVersion(VERSION);
                    dto.setStatus(PART_STATUS);
                    dto.setNation(NATION);
                    dto.setUom(UOM);
                    dto.setCreDate(CREDATE);
                    dto.setModDate(MODDATE);
                    dto.setDesign(DESIGN_USE);
                    dto.setCost(COST_USE);
                    dto.setOriginDiv(ORIGIN_DIV);

                    result.add(dto);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }



    /**
     * @apiNote 년도로 등록된 활성 부품 oid 조회
     * @param year
     * @return
     */
    public static ArrayList<String> findPartWithYear(String year) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  with ouid as
                     ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                       where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                       --and ( md$number in ( '18900360G0700') )
                        AND SUBSTR(A.MD$CDATE, 0, 8) IN( ? )
                     )
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER,
                A.SPEC,
                A.PART_SIZE AS PARTSIZE
                --A.*
                FROM NORMALPART$VF A
                WHERE A.VF$OUID IN (SELECT * FROM OUID)
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                AND A.PART_STATUS = '2466425004'
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");

                result.add(OID);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }

    /**
     * @apiNote 년도, blockNo로 부품 조회 (내작, 활성)
     * @param year
     * @param blockNo
     * @return
     */
    public static ArrayList<PartInfoDTO> findPartWithYearBlockNo(String year, String blockNo) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  with ouid as
                     ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                       where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                       --and ( md$number in ( '18900360G0700') )
                      --  AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
                      AND A.MD$CDATE >= ?
                     )
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                --DECODE(COD(CHILD.PART_DIVISION), 'P', '일반', 'T', '타사보수', 'G', 'GHOST', '일반') AS DIVISION2,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC AS SPEC,
                A.PART_SIZE AS PARTSIZE
                --A.*
                FROM NORMALPART$VF A
                WHERE A.VF$OUID IN (SELECT * FROM OUID)
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                AND A.PART_STATUS = '2466425004'
                AND A.ORIGIN_DIV = '2248978165' -- 내작
                AND A.BLOCKNO_NUMBER = ?
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            pstmt.setString(2, blockNo);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNAME = rs.getString("PARTNAME");
                String PARTNO = rs.getString("PARTNO");
                String GL_CODE = rs.getString("GL_CODE");
                String BLOCKNO = rs.getString("BLOCKNO");
                String VERSION = rs.getString("VERSION");
                String SPEC = rs.getString("SPEC");
                //String DIVISION2 =  rs.getString("DIVISION2");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setOid(OID);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setGlCode(GL_CODE);
                dto.setBlockNo(BLOCKNO);
                dto.setVersion(VERSION);
                dto.setSpec(SPEC);

                result.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }

    /**
     *
     * @param year
     * @return
     */
    public static ArrayList<PartInfoDTO> findPartWithYear_V2(String year) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  with ouid as
                     ( select A.vf$ouid from NORMALPART$vf A, NORMALPART$id B
                       where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                        AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
                     )
                SELECT
                A.VF$OUID AS OID,
                A.MD$NUMBER AS PARTNO,
                A.MD$DESC AS PARTNAME,
                A.G_L_CODE AS GL_CODE,
                --A.MD$CDATE,
                --DATEFORMAT(A.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE,
                CODN(A.PART_STATUS) AS PART_STATUS,
                COD(A.UOM) AS UOM,
                A.VF$VERSION AS VERSION,
                CODN(A.NATION) AS NATION,
                COD(A.DESIGN_USE) AS DESIGN_USE,
                COD(A.COST_USE) AS COST_USE,
                CODN(A.ORIGIN_DIV) AS ORIGIN_DIV,
                A.BLOCKNO_NUMBER AS BLOCKNO,
                A.SPEC,
                A.PART_SIZE AS PARTSIZE
                --A.*
                FROM NORMALPART$VF A
                WHERE A.VF$OUID IN (SELECT * FROM OUID)
                --WHERE 1=1
                AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                AND A.PART_STATUS = '2466425004'
                AND A.ORIGIN_DIV = '2248978165' --내작, (외주:2248978166)
                AND A.BLOCKNO_NUMBER = 'D375A'
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PARTNAME = rs.getString("PARTNAME");
                String PARTNO = rs.getString("PARTNO");
                String GL_CODE = rs.getString("GL_CODE");
                String BLOCKNO = rs.getString("BLOCKNO");
                String VERSION = rs.getString("VERSION");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setOid(OID);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setGlCode(GL_CODE);
                dto.setBlockNo(BLOCKNO);
                dto.setVersion(VERSION);

                result.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    //최신 부품 OID 조회
    public static ArrayList<String> searchPartOids(String year, String partName) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  select A.vf$ouid AS OID from NORMALPART$vf A, NORMALPART$id B
                  where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
                   AND SUBSTR(A.MD$CDATE, 0, 4) IN( ? )
                   -- AND A.MD$DESC = 'CAR WALL ASSY'
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, year);
            //pstmt.setString(2, partName);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");

                result.add(OID);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }



    //하위 조회

    /**
     * 하위 추출
     * MLB 일괄 추출 방식으로 추출
     * A-B
     * B-C
     * B-D
     * A-F
     * 자-하위
     * 모-자
     * @param oid
     * @param downPartList
     * @param parentDto
     */
    public static void findDownLevel(String oid, ArrayList<PartInfoDTO> downPartList, PartInfoDTO parentDto) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<>();

        String parentNo = parentDto.getPartNo();
        String parentPartName =  parentDto.getPartName();
        String parentSpec = parentDto.getSpec();
        String parentBlockNo = parentDto.getBlockNo();
        String parentGLCode = parentDto.getGlCode();
        String parentSize = parentDto.getPartSize();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  SELECT
                    TO_CHAR(LEVEL) AS P_LEVEL,
                       'partofpart$ac@' || lower(dectohex(BOM.SF$OUID)) AS OOID,
                       NP.MD$NUMBER AS PARTNO
                    , BOM.QTY AS QTY
                    , BOM.CMT AS CMT
                    , BOM.PART_SPT
                    , CODN(NP.NATION)
                    , NP.COMPEN_PART
                    , DECODE(BOM.SERVICEFLAG, 'T', BOM.SERVICEFLAG, NULL)
                    , BOM.MD$SEQUENCE
                    , CODN(NP.UOM) UOM -- 단위
                    , CODN(NP.CLASSIFICATION_01)
                    , BLOCKNO_NUMBER AS BLOCKNO
                    , NP.MD$DESC AS PARTNAME
                    , NP.VF$VERSION 
                    , NP.G_L_CODE  
                    , NP.PART_SIZE AS PART_SIZE
                    , NP.SPEC AS SPEC
                    , CODN(NP.SPT)
                    , CODN(NP.ORIGIN_DIV) -- 내작외작
                    , CODN(NP.REVISION)
                    , CODN(NP.PART_STATUS)
                    , CODN(NP.ACTIVE_YN)
                    , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                    , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                    , DECODE(B1.MD$NUMBER, NULL, NULL, B1.MD$NUMBER || ' ' || B1.MD$DESC) AS PARTNAMEV2
                    , DECODE(B2.MD$NUMBER, NULL, NULL, B2.MD$NUMBER || ' ' || B2.MD$DESC)
                    , DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC)
                    , DECODE(NAME.MD$NUMBER, NULL, NULL, NAME.MD$NUMBER || ' ' || NAME.MD$DESC)
                    , U.MD$DESC
                    , U2.MD$DESC
                 FROM
                    PARTOFPART$AC BOM
                    INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
                 LEFT OUTER JOIN BLOCKNO$SF B1 ON B1.SF$OUID = GETID(NP.BLOCKNO)
                 LEFT OUTER JOIN BLOCKNO$SF B2 ON B2.SF$OUID = GETID(NP.UPPERBLOCKNO)
                 LEFT OUTER JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(NP.DRAWING_NO)
                 LEFT OUTER JOIN FUSER$SF U ON U.MD$NUMBER = BOM.CUSER
                 LEFT OUTER JOIN FUSER$SF U2 ON U2.MD$NUMBER = NP.MD$USER
                 LEFT OUTER JOIN PARTNAME$SF NAME ON NAME.SF$OUID = GETID(NP.PARTNAME)
                 START WITH AS$END1 = ? -- 부품OID(VF$OUID)
                 CONNECT BY
                    PRIOR AS$END2 = AS$END1
                    ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, oid);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            PartInfoDTO tempDto = new PartInfoDTO();

            while(rs.next()) {
                String P_LEVEL = rs.getString("P_LEVEL");

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT");

                PartInfoDTO dto =  new PartInfoDTO();

                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setQty(qty);
                dto.setCmt(CMT);


                if ("2".equals(P_LEVEL)) {
                    dto.setParentPartNo(tempDto.getPartNo());
                    dto.setParentPartName(tempDto.getPartName());
                    dto.setParentGLCode(tempDto.getGlCode());
                    dto.setParentSpec(tempDto.getSpec());
                    dto.setParentBlockNo(tempDto.getBlockNo());
                    dto.setParentSize(tempDto.getPartSize());
                } else {
                    dto.setParentPartNo(parentNo);
                    dto.setParentPartName(parentPartName);
                    dto.setParentGLCode(parentGLCode);
                    dto.setParentSpec(parentSpec);
                    dto.setParentBlockNo(parentBlockNo);
                    dto.setParentSize(parentSize);
                    tempDto = dto;
                }

                downPartList.add(dto);
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

    }


    //수량검사
    public static void findDownLevelQTY_CE(String oid, ArrayList<PartInfoDTO> downPartList, PartInfoDTO parentDto) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        HashSet<String> dupCheck = new HashSet<>();
        ArrayList<String> result = new ArrayList<>();

        String parentLevel = parentDto.getParentLevel();
        String parentNo = parentDto.getPartNo();
        String parentPartName =  parentDto.getPartName();
        String parentSpec = parentDto.getSpec();
        String parentBlockNo = parentDto.getBlockNo();
        String parentGLCode = parentDto.getGlCode();
        String parentSize = parentDto.getPartSize();
        String parentVersion = parentDto.getVersion();
        String parentQty = parentDto.getQty();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  SELECT
                    TO_CHAR(LEVEL) AS P_LEVEL,
                       'partofpart$ac@' || lower(dectohex(BOM.SF$OUID)) AS OOID,
                       NP.MD$NUMBER AS PARTNO
                    , BOM.QTY AS QTY
                    , BOM.CMT AS CMT --공사주석
                    , BOM.PART_SPT
                    , CODN(NP.NATION)
                    , NP.COMPEN_PART
                    , DECODE(BOM.SERVICEFLAG, 'T', BOM.SERVICEFLAG, NULL)
                    , BOM.MD$SEQUENCE
                    , CODN(NP.UOM) UOM -- 단위
                    , CODN(NP.CLASSIFICATION_01)
                    , BLOCKNO_NUMBER AS BLOCKNO
                    , NP.MD$DESC AS PARTNAME
                    , NP.VF$VERSION AS VERSION
                    , NP.G_L_CODE AS G_L_CODE
                    , NP.PART_SIZE AS PART_SIZE
                    , NP.SPEC AS SPEC
                    , CODN(NP.SPT)
                    , CODN(NP.ORIGIN_DIV) -- 내작외작
                    , CODN(NP.REVISION)
                    , CODN(NP.PART_STATUS)
                    , CODN(NP.ACTIVE_YN)
                    , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                    , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                    , DECODE(B1.MD$NUMBER, NULL, NULL, B1.MD$NUMBER || ' ' || B1.MD$DESC) AS PARTNAMEV2
                    , DECODE(B2.MD$NUMBER, NULL, NULL, B2.MD$NUMBER || ' ' || B2.MD$DESC)
                    , DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC)
                    , DECODE(NAME.MD$NUMBER, NULL, NULL, NAME.MD$NUMBER || ' ' || NAME.MD$DESC)
                    , U.MD$DESC
                    , U2.MD$DESC
                 FROM
                    PARTOFPART$AC BOM
                    INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
                 LEFT OUTER JOIN BLOCKNO$SF B1 ON B1.SF$OUID = GETID(NP.BLOCKNO)
                 LEFT OUTER JOIN BLOCKNO$SF B2 ON B2.SF$OUID = GETID(NP.UPPERBLOCKNO)
                 LEFT OUTER JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(NP.DRAWING_NO)
                 LEFT OUTER JOIN FUSER$SF U ON U.MD$NUMBER = BOM.CUSER
                 LEFT OUTER JOIN FUSER$SF U2 ON U2.MD$NUMBER = NP.MD$USER
                 LEFT OUTER JOIN PARTNAME$SF NAME ON NAME.SF$OUID = GETID(NP.PARTNAME)
                -- WHERE BOM.QTY LIKE '%CE_0%'
                 START WITH AS$END1 = ?  -- 부품OID(VF$OUID)
                 CONNECT BY
                    PRIOR AS$END2 = AS$END1
                    ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, oid);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            PartInfoDTO tempDto = new PartInfoDTO();

            while(rs.next()) {
                String P_LEVEL = rs.getString("P_LEVEL");

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT"); // 공사주석
                String VERSION = rs.getString("VERSION");

                if (dupCheck.contains(PARTNO.trim())) {
                    continue;
                } else {
                    dupCheck.add(PARTNO.trim());
                }

                PartInfoDTO dto =  new PartInfoDTO();

                dto.setLevel(P_LEVEL);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setQty(qty);
                dto.setCmt(CMT);
                dto.setVersion(VERSION);


                dto.setParentLevel(parentLevel);
                dto.setParentPartNo(parentNo);
                dto.setParentPartName(parentPartName);
                dto.setParentGLCode(parentGLCode);
                dto.setParentSpec(parentSpec);
                dto.setParentBlockNo(parentBlockNo);
                dto.setParentSize(parentSize);
                dto.setParentVersion(parentVersion);
                dto.setParentQty(parentQty);

                if (qty.contains("CE")) {
                    downPartList.add(dto);
                }

            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

    }


    /**
     * 부품의 하위 레벨 bom 조회 (with 상위부품oid)
     * @param oid
     * @return
     */
    public static ArrayList<PartInfoDTO> findDownLevelBOM(String oid) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  SELECT
                    TO_CHAR(LEVEL) AS P_LEVEL,
                       'partofpart$ac@' || lower(dectohex(BOM.SF$OUID)) AS OOID,
                       NP.MD$NUMBER AS PARTNO
                    , BOM.QTY AS QTY
                    , BOM.CMT AS CMT --공사주석
                    , BOM.PART_SPT
                    , CODN(NP.NATION)
                    , NP.COMPEN_PART
                    , DECODE(BOM.SERVICEFLAG, 'T', BOM.SERVICEFLAG, NULL)
                    , BOM.MD$SEQUENCE
                    , CODN(NP.UOM) UOM -- 단위
                    , CODN(NP.CLASSIFICATION_01)
                    , BLOCKNO_NUMBER AS BLOCKNO
                    , NP.MD$DESC AS PARTNAME
                    , NP.VF$VERSION AS VERSION
                    , NP.G_L_CODE  
                    , NP.PART_SIZE AS PART_SIZE
                    , NP.SPEC AS SPEC
                    , CODN(NP.SPT)
                    , CODN(NP.ORIGIN_DIV) -- 내작외작
                    , CODN(NP.REVISION)
                    , CODN(NP.PART_STATUS)
                    , CODN(NP.ACTIVE_YN)
                    , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                    , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                    , DECODE(B1.MD$NUMBER, NULL, NULL, B1.MD$NUMBER || ' ' || B1.MD$DESC) AS PARTNAMEV2
                    , DECODE(B2.MD$NUMBER, NULL, NULL, B2.MD$NUMBER || ' ' || B2.MD$DESC)
                    , DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC)
                    , DECODE(NAME.MD$NUMBER, NULL, NULL, NAME.MD$NUMBER || ' ' || NAME.MD$DESC)
                    , U.MD$DESC
                    , U2.MD$DESC
                 FROM
                    PARTOFPART$AC BOM
                    INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
                 LEFT OUTER JOIN BLOCKNO$SF B1 ON B1.SF$OUID = GETID(NP.BLOCKNO)
                 LEFT OUTER JOIN BLOCKNO$SF B2 ON B2.SF$OUID = GETID(NP.UPPERBLOCKNO)
                 LEFT OUTER JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(NP.DRAWING_NO)
                 LEFT OUTER JOIN FUSER$SF U ON U.MD$NUMBER = BOM.CUSER
                 LEFT OUTER JOIN FUSER$SF U2 ON U2.MD$NUMBER = NP.MD$USER
                 LEFT OUTER JOIN PARTNAME$SF NAME ON NAME.SF$OUID = GETID(NP.PARTNAME)
                -- WHERE BOM.QTY LIKE '%CE_0%'
                 START WITH AS$END1 = ? -- 부품OID(VF$OUID)
                 CONNECT BY
                    PRIOR AS$END2 = AS$END1
                    ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, oid);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            PartInfoDTO tempDto = new PartInfoDTO();

            while(rs.next()) {
                String P_LEVEL = rs.getString("P_LEVEL");

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT");
                String VERSION = rs.getString("VERSION");


                PartInfoDTO dto =  new PartInfoDTO();

                dto.setLevel(P_LEVEL);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setBlockNo(BLOCKNO);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setQty(qty);
                dto.setCmt(CMT);
                dto.setVersion(VERSION);

                result.add(dto);

            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }



    // 부품 하위 분석해서 수량 PID 찾기
    @Description("부품 하위 분석해서 수량 PID 찾기")
    public static void findDownLevelQTY(PartInfoDTO parentDto, String qtyPID, ArrayList<PartInfoDTO> dataDTOList) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        try {


            if (con == null || con.isClosed()) {
                con = PLMDBPool.getConnection();
            } else {
                con = PLMDBPool.getConnection();
            }

            String sql = """
                  SELECT
                --    TO_CHAR(LEVEL) AS P_LEVEL,
                --       'partofpart$ac@' || lower(dectohex(BOM.SF$OUID)) AS OOID,
                       NP.MD$NUMBER AS PARTNO
                    , BOM.QTY AS QTY
                    , BOM.CMT AS CMT --공사주석
                    , BOM.PART_SPT
                    , CODN(NP.NATION)
                    , NP.COMPEN_PART
                   -- , DECODE(BOM.SERVICEFLAG, 'T', BOM.SERVICEFLAG, NULL)
                   -- , BOM.MD$SEQUENCE
                    , CODN(NP.UOM) UOM -- 단위
                    , CODN(NP.CLASSIFICATION_01)
                    , BLOCKNO_NUMBER AS BLOCKNO
                    , NP.MD$DESC AS PARTNAME
                    , NP.VF$VERSION AS VERSION
                    , NP.G_L_CODE AS G_L_CODE
                    , NP.PART_SIZE AS PART_SIZE
                    , NP.SPEC AS SPEC
                   -- , CODN(NP.SPT)
                    , CODN(NP.ORIGIN_DIV) -- 내작외작
                    , CODN(NP.REVISION)
                    , CODN(NP.PART_STATUS)
                    , CODN(NP.ACTIVE_YN)
                    , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                    , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                  --  , DECODE(B1.MD$NUMBER, NULL, NULL, B1.MD$NUMBER || ' ' || B1.MD$DESC) AS PARTNAMEV2
                   -- , DECODE(B2.MD$NUMBER, NULL, NULL, B2.MD$NUMBER || ' ' || B2.MD$DESC)
                   --, DECODE(CAD.MD$NUMBER, NULL, NULL, CAD.MD$NUMBER || ' ' || CAD.MD$DESC)
                   -- , DECODE(NAME.MD$NUMBER, NULL, NULL, NAME.MD$NUMBER || ' ' || NAME.MD$DESC)
                   -- , U.MD$DESC
                   -- , U2.MD$DESC
                 FROM
                    PARTOFPART$AC BOM
                    INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
                 --LEFT OUTER JOIN BLOCKNO$SF B1 ON B1.SF$OUID = GETID(NP.BLOCKNO)
                 --LEFT OUTER JOIN BLOCKNO$SF B2 ON B2.SF$OUID = GETID(NP.UPPERBLOCKNO)
                -- LEFT OUTER JOIN AUTOCAD_FILE$VF CAD ON CAD.VF$OUID = GETID(NP.DRAWING_NO)
                 --LEFT OUTER JOIN FUSER$SF U ON U.MD$NUMBER = BOM.CUSER
                 --LEFT OUTER JOIN FUSER$SF U2 ON U2.MD$NUMBER = NP.MD$USER
                -- LEFT OUTER JOIN PARTNAME$SF NAME ON NAME.SF$OUID = GETID(NP.PARTNAME)
                """;

            sql += "WHERE BOM.QTY LIKE '" + qtyPID + "%' ";

            sql += "OR BOM.CMT LIKE '" + qtyPID + "%' ";


            sql += """
                -- WHERE BOM.QTY LIKE '%CE_0%'
                 START WITH AS$END1 = ?  -- 부품OID(VF$OUID)
                 CONNECT BY
                    PRIOR AS$END2 = AS$END1
                ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, parentDto.getOid());
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String P_LEVEL = rs.getString("P_LEVEL");

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY") == null ? "" : rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT"); // 공사주석
                String VERSION = rs.getString("VERSION");


                //if (qty.contains(qtyPID) || CMT.contains(qtyPID)) {

                PartInfoDTO curDto =  new PartInfoDTO();
                curDto.setPartNo(PARTNO);
                curDto.setPartName(PARTNAME);
                curDto.setBlockNo(BLOCKNO);
                curDto.setSpec(SPEC);
                curDto.setPartSize(PART_SIZE);
                curDto.setQty(qty);
                curDto.setCmt(CMT);
                curDto.setVersion(VERSION);

                curDto.setParentPartNo(parentDto.getPartNo());
                curDto.setParentPartName(parentDto.getPartName());
                curDto.setParentSpec(parentDto.getSpec());
                curDto.setParentSize(parentDto.getPartSize());
                curDto.setParentBlockNo(parentDto.getPartNo());

                dataDTOList.add(curDto);
                //}
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

    }


    @Description("부품하위에 수량PID 조회")
    public static void findDownLevelQtyPID(String year, String blockNo, String qtyPID, ArrayList<PartInfoDTO> dataDTOList) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        try {


            if (con == null || con.isClosed()) {
                con = PLMDBPool.getConnection();
            } else {
                con = PLMDBPool.getConnection();
            }

            String sql = """
                  with ouid as
            (select A.vf$ouid
             from NORMALPART$vf A,
                  NORMALPART$id B
             where A.vf$identity = B.id$ouid
               and A.vf$ouid = B.id$wip
               AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                   AND A.PART_STATUS = '2466425004'
                   AND A.ORIGIN_DIV = '2248978165' -- 내작
                   AND A.BLOCKNO_NUMBER = ?
                   AND A.MD$CDATE >= ?
             )
                SELECT
                       (SELECT C.MD$NUMBER FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTNO
                       , (SELECT C.MD$DESC FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTNAME
                       , (SELECT C.SPEC FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTSPEC
                       , (SELECT C.BLOCKNO_NUMBER FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTBLOCK
                       , (SELECT C.PART_SIZE FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTSIZE
                       ,  NP.MD$NUMBER AS PARTNO
                       , BOM.QTY AS QTY
                       , BOM.CMT AS CMT --공사주석
                       , BOM.PART_SPT
                       , CODN(NP.NATION)
                       , NP.COMPEN_PART
                       , CODN(NP.UOM) UOM -- 단위
                       , CODN(NP.CLASSIFICATION_01)
                       , BLOCKNO_NUMBER AS BLOCKNO
                       , NP.MD$DESC AS PARTNAME
                       , NP.VF$VERSION AS VERSION
                       , NP.G_L_CODE AS G_L_CODE
                       , NP.PART_SIZE AS PART_SIZE
                       , NP.SPEC AS SPEC
                       , CODN(NP.ORIGIN_DIV) -- 내작외작
                       , CODN(NP.REVISION)
                       , CODN(NP.PART_STATUS)
                       , CODN(NP.ACTIVE_YN)
                       , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                       , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                FROM
                       PARTOFPART$AC BOM
                       INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
               """;


            sql += "WHERE BOM.QTY LIKE '" + qtyPID + "%' ";
            sql += "OR BOM.CMT LIKE '" + qtyPID + "%' ";
                           //OR BOM.CMT LIKE 'E321A_28%'

            sql += """
                    START WITH AS$END1 IN (SELECT * FROM OUID)  -- 부품OID(VF$OUID)
                    CONNECT BY
                       PRIOR AS$END2 = AS$END1
                    ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, blockNo);
            pstmt.setString(2, year);
            //pstmt.setString(3, qtyPID);
            //pstmt.setString(4, qtyPID);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARENTNO = rs.getString("PARENTNO");
                String PARENTNAME = rs.getString("PARENTNAME");
                String PARENTSPEC = rs.getString("PARENTSPEC");
                String PARENTBLOCK = rs.getString("PARENTBLOCK");
                String PARENTSIZE = rs.getString("PARENTSIZE");

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY") == null ? "" : rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT"); // 공사주석
                String VERSION = rs.getString("VERSION");


                //if (qty.contains(qtyPID) || CMT.contains(qtyPID)) {

                PartInfoDTO curDto =  new PartInfoDTO();
                curDto.setPartNo(PARTNO);
                curDto.setPartName(PARTNAME);
                curDto.setBlockNo(BLOCKNO);
                curDto.setSpec(SPEC);
                curDto.setPartSize(PART_SIZE);
                curDto.setQty(qty);
                curDto.setCmt(CMT);
                curDto.setVersion(VERSION);

                curDto.setParentPartNo(PARENTNO);
                curDto.setParentPartName(PARENTNAME);
                curDto.setParentSpec(PARENTSPEC);
                curDto.setParentSize(PARENTSIZE);
                curDto.setParentBlockNo(PARENTBLOCK);

                dataDTOList.add(curDto);
                //}
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

    }



    //품번으로 하위 BOM 조회
    public static ArrayList<PartInfoDTO> findAssyDownBOM(String partNo) {

        ArrayList<PartInfoDTO> result = new ArrayList();

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        try {


            if (con == null || con.isClosed()) {
                con = PLMDBPool.getConnection();
            } else {
                con = PLMDBPool.getConnection();
            }

            String sql = """
                  with ouid as
            ( SELECT A.vf$ouid
             from NORMALPART$vf A, NORMALPART$id B
             where A.vf$identity = B.id$ouid
               AND A.vf$ouid = B.id$wip
               AND SUBSTR(A.BLOCKNO_NUMBER, 2,1) IN ('1','2','3')
                   AND A.PART_STATUS = '2466425004'
                   AND A.ORIGIN_DIV = '2248978165' -- 내작
                   --AND A.BLOCKNO_NUMBER = ?
                   --AND A.MD$CDATE >= ?
                   AND A.MD$NUMBER = ?
             )
                SELECT
                       (SELECT C.MD$NUMBER FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTNO
                       , (SELECT C.MD$DESC FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTNAME
                       , (SELECT C.SPEC FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTSPEC
                       , (SELECT C.BLOCKNO_NUMBER FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTBLOCK
                       , (SELECT C.PART_SIZE FROM NORMALPART$vf C WHERE C.VF$OUID = AS$END1) AS PARENTSIZE
                       ,  NP.MD$NUMBER AS PARTNO
                       , BOM.QTY AS QTY
                       , BOM.CMT AS CMT --공사주석
                       , BOM.PART_SPT
                       , CODN(NP.NATION)
                       , NP.COMPEN_PART
                       , CODN(NP.UOM) UOM -- 단위
                       , CODN(NP.CLASSIFICATION_01)
                       , BLOCKNO_NUMBER AS BLOCKNO
                       , NP.MD$DESC AS PARTNAME
                       , NP.VF$VERSION AS VERSION
                       , NP.G_L_CODE AS G_L_CODE
                       , NP.PART_SIZE AS PART_SIZE
                       , NP.SPEC AS SPEC
                       , CODN(NP.ORIGIN_DIV) -- 내작외작
                       , CODN(NP.REVISION)
                       , CODN(NP.PART_STATUS)
                       , CODN(NP.ACTIVE_YN)
                       , NP.MD$STATUS
                       , DATEFORMAT(BOM.MD$CDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS CREATE_DATE
                       , DATEFORMAT(BOM.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_DATE
                FROM
                       PARTOFPART$AC BOM
                       INNER JOIN NORMALPART$VF NP ON 	BOM.AS$END2 = NP.VF$OUID
               """;


            //sql += "WHERE BOM.QTY LIKE '" + qtyPID + "%' ";
            //sql += "OR BOM.CMT LIKE '" + qtyPID + "%' ";
            //OR BOM.CMT LIKE 'E321A_28%'

            sql += """
                    START WITH AS$END1 IN (SELECT * FROM OUID)  -- 부품OID(VF$OUID)
                    CONNECT BY
                       PRIOR AS$END2 = AS$END1
                    ORDER SIBLINGS BY CAST(BOM.MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, partNo);
            //pstmt.setString(2, year);
            //pstmt.setString(3, qtyPID);
            //pstmt.setString(4, qtyPID);
            //pstmt.setString(1, productOID);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARENTNO = rs.getString("PARENTNO");
                String PARENTNAME = rs.getString("PARENTNAME");
                String PARENTSPEC = rs.getString("PARENTSPEC");
                String PARENTBLOCK = rs.getString("PARENTBLOCK");
                String PARENTSIZE = rs.getString("PARENTSIZE");

                String PARTNO =  rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String qty = rs.getString("QTY") == null ? "" : rs.getString("QTY");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT"); // 공사주석
                String VERSION = rs.getString("VERSION");


                //if (qty.contains(qtyPID) || CMT.contains(qtyPID)) {

                PartInfoDTO curDto =  new PartInfoDTO();
                curDto.setPartNo(PARTNO);
                curDto.setPartName(PARTNAME);
                curDto.setBlockNo(BLOCKNO);
                curDto.setSpec(SPEC);
                curDto.setPartSize(PART_SIZE);
                curDto.setQty(qty);
                curDto.setCmt(CMT);
                curDto.setVersion(VERSION);

                curDto.setParentPartNo(PARENTNO);
                curDto.setParentPartName(PARENTNAME);
                curDto.setParentSpec(PARENTSPEC);
                curDto.setParentSize(PARENTSIZE);
                curDto.setParentBlockNo(PARENTBLOCK);

                result.add(curDto);
                //}
            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    //특성값

    /**
     * 특성코드 리스트
     * @return
     */
    public static ArrayList<CodeInfoDTO> getCodeList() {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<CodeInfoDTO> result = new  ArrayList<>();

        try {
            con = PLMDBPool.getConnection();

            String sql = """
                    SELECT a.name AS CODE,
                    a.TIT AS CODENAME, 
                    c.NAME AS TYPENAME, 
                    c.DES AS TYPEVAL,
                    --c.ouid,
                    b.name AS NAME
                    FROM HDEL_SYSTEM.dosfld a
                        INNER JOIN HDEL_SYSTEM.doscod b
                        ON a.TYPECLAS  = b.OUID
                        INNER JOIN HDEL_SYSTEM.DOSCODITM c
                        ON c.DOSCOD = b.OUID
                        WHERE TYPE=24 AND DOSCLAS=2248993771
                    """;

            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String CODE = rs.getString("CODE"); // 사양 EL_ATYP
                String CODENAME = rs.getString("CODENAME"); // 사양명 기종
                String TYPENAME = rs.getString("TYPENAME"); // 특성명 GTLX
                String TYPEVAL = rs.getString("TYPEVAL"); // 특성값 GTLX_E, WBSS1_(SSVF)
                String NAME = rs.getString("NAME");

                CodeInfoDTO dto = new  CodeInfoDTO();
                dto.setCode(CODE);
                dto.setCodeName(CODENAME);
                dto.setTypeName(TYPENAME);
                dto.setTypeVal(TYPEVAL);
                dto.setName(NAME);

                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    //공사정보 필드 리스트 - 육상
    public static ArrayList<HashMap<String, String>> getCodeField() {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<HashMap<String, String>> result = new  ArrayList<>();

        try {
            con = PLMDBPool.getConnection();

            String sql = """
                    SELECT 
                    a.name AS CODE,
                    a.TIT AS CODENAME 
                    --c.NAME AS TYPENAME, 
                    --c.DES AS TYPEVAL,
                    --c.ouid,
                    --b.name AS NAME
                    FROM HDEL_SYSTEM.dosfld a
                        where a.dosclas=2248993771
                    """;

            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String CODE = rs.getString("CODE");
                String CODENAME = rs.getString("CODENAME");

                HashMap<String, String> map = new HashMap<>();
                map.put("NAME", CODE);
                map.put("TIT", CODENAME);

                result.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    //영업사양 값 팝업의 코드
    public static ArrayList<CodeInfoDTO> getCodeListV2() {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<CodeInfoDTO> result = new  ArrayList<>();

        try {
            con = PLMDBPool.getConnection();

            String sql = """
                    SELECT 
                        A.NAME AS CODE, 
                        A.TIT AS CODENAME
                    FROM HDEL_SYSTEM.dosfld A
                    WHERE A.DOSCLAS = '2248993771'
                    """;

            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            while(rs.next()) {
                String CODE = rs.getString("CODE"); // 특성코드
                String CODENAME = rs.getString("CODENAME"); // 사양명

                CodeInfoDTO dto = new  CodeInfoDTO();
                dto.setCode(CODE);
                dto.setCodeName(CODENAME);
                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    //선박용 엘리베이터 특성값 리스트 (getCodeList 의 선박용, 공사번호 추출 화면의 값 CODE 제안)
    public static ArrayList<CodeInfoDTO> getShipCodeValueList() {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<CodeInfoDTO> result = new  ArrayList<>();

        try {
            con = PLMDBPool.getConnection();

            String sql = """
                    SELECT a.name AS CODE,
                    a.TIT AS CODENAME,
                    c.NAME AS TYPENAME,
                    c.DES AS TYPEVAL,
                    b.name AS NAME
                    FROM HDEL_SYSTEM.dosfld a
                        INNER JOIN HDEL_SYSTEM.doscod b
                        ON a.TYPECLAS  = b.OUID
                        INNER JOIN HDEL_SYSTEM.DOSCODITM c
                        ON c.DOSCOD = b.OUID
                        WHERE TYPE=24 AND DOSCLAS=2706224418
                    """;

            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                CodeInfoDTO dto = new  CodeInfoDTO();
                dto.setCode(rs.getString("CODE"));
                dto.setCodeName(rs.getString("CODENAME"));
                dto.setTypeName(rs.getString("TYPENAME"));
                dto.setTypeVal(rs.getString("TYPEVAL"));
                dto.setName(rs.getString("NAME"));
                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    //선박용 엘리베이터 특성코드 (공사번호 추출 화면의 특성 CODE 검색 목록)
    public static ArrayList<CodeInfoDTO> getShipCodeList() {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<CodeInfoDTO> result = new  ArrayList<>();

        try {
            con = PLMDBPool.getConnection();

            String sql = """
                    SELECT
                        A.NAME AS CODE,
                        A.TIT AS CODENAME
                    FROM HDEL_SYSTEM.dosfld A
                    WHERE A.DOSCLAS = '2706224418'
                    """;

            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                CodeInfoDTO dto = new  CodeInfoDTO();
                dto.setCode(rs.getString("CODE")); // 특성코드
                dto.setCodeName(rs.getString("CODENAME")); // 사양명
                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }

    //blockNo로 특정기간에 제품에 수배된 자재 찾기
    /**
     * @apiNote blockNo로 특정기간에 제품에 수배된 자재 찾기
     * @param proCreDate 제품 생성일 (시작, YYYYMMDD)
     * @param proCreDateTo 제품 생성일 (종료, YYYYMMDD)
     * @param blockNo
     * @param partCreDate 자재 생성일
     */
    public static ArrayList<HashMap<String, String>> searchBomGetPartNo(String proCreDate, String proCreDateTo, String blockNo, String partCreDate) {
        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        try {
            con = PLMDBPool.getConnection();
            String sql = """
                  with ouid as
                   ( select A.vf$ouid AS VFOID from product$vf A, product$id B 
                      where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip 
                      --AND SUBSTR(A.MD$CDATE, 0, 6) IN('202609', '202610') 
                        AND SUBSTR(A.MD$CDATE, 0, 8) BETWEEN ? AND ? -- '20261013' ~ '20261016' 제품 생성일
                      AND A.MD$NUMBER NOT LIKE 'TEST%'
                      ) 
                  SELECT 
                        (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO
                      , (SELECT D.VF$VERSION FROM PRODUCT$VF D WHERE D.VF$OUID = PE.PRODUCTOUID) AS PARENTVER
                      , (SELECT TO_CHAR(TO_DATE(D.MD$CDATE,'YYYYMMDDHH24MISS'),'YYYY-MM-DD HH24:MI:SS') FROM PRODUCT$VF D WHERE D.VF$OUID = PE.PRODUCTOUID) AS PARENTVER_CDATE
                      , (SELECT TO_CHAR(TO_DATE(D.APP_DATE,'YYYYMMDDHH24MISS'),'YYYY-MM-DD HH24:MI:SS') FROM PRODUCT$VF D WHERE D.VF$OUID = PE.PRODUCTOUID) AS PARENTVER_APPDATE 
                       , CODN(NP.NATION) AS NATION --자재코드(Ownership)
                       , NP.MD$NUMBER AS PARTNO
                       , NP.MD$DESC AS PARTNAME 
                       , NP.VF$VERSION AS VERSION 
                       , NVL(NP.G_L_CODE, '') AS GLCODE 
                       , NVL(NP.SPEC, '') AS SPEC 
                       , NVL(NP.PART_SIZE, '') AS PART_SIZE 
                       , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO
                       , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT 
                       , NVL(COD(NP.UOM), '') AS UOM 
                       , PE.QTY AS QTY 
                       , VP.WORK_QTY 
                       , PE.CMT AS CMT 
                       , NVL(COD(NP.ORIGIN_DIV), '') AS DIV 
                       , NP.MD$USER AS USERID 
                       , VP.UCHECK -- 수정여부 
                       --, VP.MCHECK 
                       --, NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION 
                      , TO_CHAR(TO_DATE(PE.CDATE,'YYYYMMDDHH24MISS'),'YYYY-MM-DD HH24:MI:SS') AS CDATE -- 자재 등록일 
                      , TO_CHAR(TO_DATE(VP.MDATE,'YYYYMMDDHH24MISS'),'YYYY-MM-DD HH24:MI:SS') AS MDATE -- 자재 수정일 
                       , VP.user5 
                      FROM 
                       PARTOFEBOM PE 
                      INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                      LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID 
                      WHERE 
                       -- PE.PRODUCTOUID = 제품의OID 
                      PE.PRODUCTOUID IN (SELECT VFOID FROM ouid) 
                      AND (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) = ? -- BlockNo
                      AND SUBSTR(PE.CDATE, 0, 8) = ? -- BOM 자재 등록일
                """;

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, proCreDate);
            pstmt.setString(2, proCreDateTo);
            pstmt.setString(3, blockNo);
            pstmt.setString(4, partCreDate);

            //System.out.println("sql.toString() = " + sql.toString());

            rs = pstmt.executeQuery();

            PartInfoDTO tempDto = new PartInfoDTO();

            while(rs.next()) {
                String PARENTNO = rs.getString("PARENTNO");
                String PARENTVER =  rs.getString("PARENTVER");
                String PARENTVER_CDATE = rs.getString("PARENTVER_CDATE");
                String PARENTVER_APPDATE = rs.getString("PARENTVER_APPDATE");

                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String BLOCKNO = rs.getString("BLOCKNO");
                String CMT = rs.getString("CMT");
                String SPEC = rs.getString("SPEC");
                String CDATE = rs.getString("CDATE");
                String MDATE = rs.getString("MDATE");
                String QTY = rs.getString("QTY");
                String GLCODE = rs.getString("GLCODE");
                String PART_SIZE = rs.getString("PART_SIZE");
                String UOM = rs.getString("UOM");


                HashMap<String, String> dto = new HashMap<>();

                dto.put("PARENTNO", PARENTNO);
                dto.put("PARENTVER", PARENTVER);
                dto.put("PARENTVER_CDATE", PARENTVER_CDATE);
                dto.put("PARENTVER_APPDATE", PARENTVER_APPDATE);
                dto.put("PARTNO", PARTNO);
                dto.put("PARTNAME", PARTNAME);
                dto.put("BLOCKNO", BLOCKNO);
                dto.put("CMT", CMT);
                dto.put("SPEC", SPEC);
                dto.put("CDATE", CDATE);
                dto.put("MDATE", MDATE);
                dto.put("QTY", QTY);
                dto.put("GLCODE", GLCODE);
                dto.put("PART_SIZE", PART_SIZE);
                dto.put("UOM", UOM);

                result.add(dto);

            } //end while

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }
}
