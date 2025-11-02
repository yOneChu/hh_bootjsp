package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

public class MLBCommonUtil {


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
            con = PLMDBConnection.getConnection();
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
            con = PLMDBConnection.getConnection();
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
            con = PLMDBConnection.getConnection();
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
                DECODE(COD(CHILD.PART_DIVISION), 'P', '일반', 'T', '타사보수', 'G', 'GHOST', '일반') AS DIVISION2,
                A.BLOCKNO_NUMBER,
                A.SPEC,
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
                String DIVISION2 =  rs.getString("DIVISION2");

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
            con = PLMDBConnection.getConnection();
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
            con = PLMDBConnection.getConnection();
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
            con = PLMDBConnection.getConnection();
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
            con = PLMDBConnection.getConnection();
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
            con = PLMDBConnection.getConnection();
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

}
