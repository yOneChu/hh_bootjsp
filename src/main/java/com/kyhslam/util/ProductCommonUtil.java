package com.kyhslam.util;

import com.kyhslam.dto.BomDTO;
import com.kyhslam.dto.BomPartDTO;
import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * 제품 관련 함수 모음
 */
public class ProductCommonUtil {


    /**
     * 제품의 최신 1레벨 조회
     * @param productNo
     * @return
     */
    public static ArrayList<ProductDto> findProductInfo(String productNo) {
        System.out.println("PartCommonUtil findProductInfo start ==-" + productNo );

        ArrayList<ProductDto> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
              ( select vf$ouid AS VFOID from product$vf, product$id
                where vf$identity = id$ouid and vf$ouid = id$wip
                and (
                      md$number = ?
                    )
              )
             SELECT
                         PE.ASSOOUID ASSOOUID
                         , PE.PRODUCTOUID PRODUCTOUID
                         , PE.PARTOUID PARTOUID
                         , PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                         , ( SELECT F.MD$DESC FROM FUSER$SF F
                              WHERE F.MD$NUMBER = (SELECT VV.MD$USER
                                                   FROM PRODUCT$VF VV WHERE VV.VF$OUID = PE.PRODUCTOUID )
                            ) AS PCREATOR
                            , ( SELECT F.EMAIL FROM FUSER$SF F
                              WHERE F.MD$NUMBER = (SELECT VV.MD$USER
                                                   FROM PRODUCT$VF VV WHERE VV.VF$OUID = PE.PRODUCTOUID )
                            ) AS PEMAIL
                         --, (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                         , NP.MD$NUMBER AS PARTNO
                         , CODN(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS VERSION
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , NP.OLD_CODE
                         , NP.OLD_CODE2
                         , NP.OLD_CODE3
                         , COD(NP.SPT) SPT
                         , COD(NP.PARTMPCHECK) PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                         , PE.CUSER CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = (SELECT VFOID FROM ouid)
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String PARTOUID = rs.getString("PARTOUID");

                String PCREATOR = rs.getString("PCREATOR");
                String PEMAIL = rs.getString("PEMAIL");

                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String WORK_QTY = rs.getString("WORK_QTY");
                String WORK_CMT = rs.getString("WORK_CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                String HASCHILD = rs.getString("HASCHILD");

                ProductDto dto = new ProductDto();
                dto.setProductOid(PRODUCTOUID);
                dto.setProductNo(PARENTNO);

                dto.setProductCreator(PCREATOR); // 제품 등록자
                dto.setProductEmail(PEMAIL); // 제품 등록자 이메일

                dto.setSeq(SEQ);
                dto.setPartNo(PARTNO);
                dto.setPartNoOID(PARTOUID);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockopt(BLOCK_OPT);
                dto.setGlCode(GLCODE);
                dto.setWorkQty(WORK_QTY);
                dto.setWorkCmt(WORK_CMT);

                dto.setNation(NATION);
                dto.setSpec(SPEC);
                dto.setPart_size(PART_SIZE);
                dto.setQty(QTY);
                dto.setCmt(CMT);
                dto.setUcheck(UCHECK);
                dto.setUsername(USERNAME);
                dto.setUserId(USERID);
                dto.setHASCHILD(HASCHILD);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }

    //

    /**
     * @apiNote 제품의 전체 레벨 하위 bom 조회
     * @param productNo
     * @return
     */
    public static ArrayList<BomPartDTO> findProductBOM(String productNo) {
        //System.out.println("PartCommonUtil findProductInfo start ==-" + productNo );

        ArrayList<BomPartDTO> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
              ( select vf$ouid AS VFOID from product$vf, product$id
                where vf$identity = id$ouid and vf$ouid = id$wip
                and (
                      md$number = ?
                    )
              )
             SELECT
                        -- PE.ASSOOUID ASSOOUID,
                          PE.PRODUCTOUID PRODUCTOUID
                         , PE.PARTOUID PARTOUID
                         ,  PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                         , ( SELECT F.MD$DESC FROM FUSER$SF F
                              WHERE F.MD$NUMBER = (SELECT VV.MD$USER
                                                   FROM PRODUCT$VF VV WHERE VV.VF$OUID = PE.PRODUCTOUID )
                            ) AS PCREATOR
                         , ( SELECT F.EMAIL FROM FUSER$SF F
                              WHERE F.MD$NUMBER = (SELECT VV.MD$USER
                                                   FROM PRODUCT$VF VV WHERE VV.VF$OUID = PE.PRODUCTOUID )
                            ) AS PEMAIL
                        -- , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                         , NP.MD$NUMBER AS PARTNO
                         , CODN(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS VERSION
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , NP.OLD_CODE
                         , NP.OLD_CODE2
                         , NP.OLD_CODE3
                         , COD(NP.SPT) SPT
                         , COD(NP.PARTMPCHECK) PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                         , PE.CUSER CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = (SELECT VFOID FROM ouid)
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String PARTOUID = rs.getString("PARTOUID");
                String PCREATOR = rs.getString("PCREATOR");
                String PEMAIL = rs.getString("PEMAIL");

                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String WORK_QTY = rs.getString("WORK_QTY");
                String WORK_CMT = rs.getString("WORK_CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                String UOM = rs.getString("UOM");
                String HASCHILD = rs.getString("HASCHILD");


                //ProductDto dto = new ProductDto();
                BomPartDTO dto = new BomPartDTO();
                //dto.setProductOid(PRODUCTOUID);
                dto.setProductNo(PARENTNO);
                dto.setUom(UOM);

                dto.setPCreator(PCREATOR); // 제품 등록자
                dto.setPEmail(PEMAIL); // 제품 등록자 EMAIL

                dto.setSeq(SEQ);
                dto.setPartNo(PARTNO);
                dto.setPartNoOID(PARTOUID);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockopt(BLOCK_OPT);
                dto.setGlCode(GLCODE);
                dto.setWorkQty(WORK_QTY);
                dto.setWorkCmt(WORK_CMT);

                dto.setNation(NATION);
                dto.setSpec(SPEC);
                dto.setPart_size(PART_SIZE);
                dto.setQty(QTY);
                dto.setCmt(CMT);
                dto.setUcheck(UCHECK);
                dto.setUsername(USERNAME);
                dto.setUserId(USERID);
                dto.setHASCHILD(HASCHILD);

                list.add(dto);

                if(HASCHILD.equals("1")) {
                    findProductDownLevelBOM(PRODUCTOUID, PARTOUID, list);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }

    /**
     * 제품의 최신 1레벨 조회
     * @param productNo, productVer
     * @return
     */
    public static ArrayList<ProductDto> findProductInfo(String productNo, String productVer) {

        ArrayList<ProductDto> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
              ( 
                select V.vf$ouid AS VFOID from product$vf V
                where V.md$number = ? AND V.VF$VERSION = ?
              )
             SELECT
                         PE.ASSOOUID ASSOOUID
                         , PE.PRODUCTOUID PRODUCTOUID
                         , PE.PARTOUID PARTOUID
                         , PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                         , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                         , NP.MD$NUMBER AS PARTNO
                         , CODN(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS VERSION
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , NP.OLD_CODE
                         , NP.OLD_CODE2
                         , NP.OLD_CODE3
                         , COD(NP.SPT) SPT
                         , COD(NP.PARTMPCHECK) PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                         , PE.CUSER CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = (SELECT VFOID FROM ouid)
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            stmt.setString(2, productVer);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String PARTOUID = rs.getString("PARTOUID");
                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                String HASCHILD = rs.getString("HASCHILD");

                ProductDto dto = new ProductDto();
                dto.setProductOid(PRODUCTOUID);
                dto.setProductNo(PARENTNO);

                dto.setSeq(SEQ);
                dto.setPartNo(PARTNO);
                dto.setPartNoOID(PARTOUID);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockopt(BLOCK_OPT);
                dto.setGlCode(GLCODE);

                dto.setNation(NATION);
                dto.setSpec(SPEC);
                dto.setPart_size(PART_SIZE);
                dto.setQty(QTY);
                dto.setCmt(CMT);
                dto.setUcheck(UCHECK);
                dto.setUsername(USERNAME);
                dto.setUserId(USERID);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }


    /**
     * @apiNote 제품하위에 해당 blockNo의 부품 조회
     * @param productNo
     * @param blockNo
     * @return
     */
    public static PartInfoDTO findProductInfoAsBlockNo(String productNo, String blockNo) {
        System.out.println("PartCommonUtil findProductInfo start ==-" + productNo );

        PartInfoDTO result = new PartInfoDTO();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
              ( select vf$ouid AS VFOID from product$vf, product$id
                where vf$identity = id$ouid and vf$ouid = id$wip
                and (
                      md$number = ?
                    )
              )
             SELECT
                         PE.ASSOOUID ASSOOUID
                         , PE.PRODUCTOUID PRODUCTOUID
                         , PE.PARTOUID PARTOUID
                         , PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                         , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                         , NP.MD$NUMBER AS PARTNO
                         , CODN(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS VERSION
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , NP.OLD_CODE
                         , NP.OLD_CODE2
                         , NP.OLD_CODE3
                         , COD(NP.SPT) SPT
                         , COD(NP.PARTMPCHECK) PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                         , PE.CUSER CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = (SELECT VFOID FROM ouid)
                 AND (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) = ?
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);
            stmt.setString(2, blockNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String PARTOUID = rs.getString("PARTOUID");
                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                String HASCHILD = rs.getString("HASCHILD");

                result.setPartNo(PARTNO);
                result.setPartName(PARTNAME);
                result.setSpec(SPEC);
                result.setBlockNo(BLOCKNO);
                result.setQty(QTY);
                result.setNation(NATION);
                result.setUCheck(UCHECK);
                result.setCmt(CMT);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return result;
    }

    // 제품의 OID로  해당 1레벨 BOM 조회
    /**
     * 제품의 OID로  해당 1레벨 BOM 조회
     * @param productOID
     * @return
     */
    public static ArrayList<ProductDto> findProductBOMWithOID(String productOID) {
        //System.out.println("PartCommonUtil findProductBOMWithOID start ==-" + productOID );

        ArrayList<ProductDto> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
             SELECT
                         PE.ASSOOUID ASSOOUID
                         , PE.PRODUCTOUID PRODUCTOUID
                         , PE.PARTOUID PARTOUID
                         , PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                         , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                         , (SELECT F.VF$VERSION FROM PRODUCT$VF F WHERE F.VF$OUID = PE.PRODUCTOUID) AS PARENT_VER
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_CREDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_MODDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_APP_DATE
                         , (SELECT PRODUCT.MD$STATUS FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_STATUS
                         , (SELECT COD(E.EL_ATYP) FROM ELV_INFO$ID A, ELV_INFO$VF E
                            WHERE A.ID$OUID = E.VF$IDENTITY AND E.vf$ouid = A.id$wip
                            AND E.MD$NUMBER = (SELECT F.MD$NUMBER FROM PRODUCT$VF F WHERE F.VF$OUID = PE.PRODUCTOUID) ) AS GISONG
                         , NP.MD$NUMBER AS PARTNO
                         , cod(NP.NATION) AS NATION
                         , NP.compen_part AS COMPEN_PART
                         , NP.MD$DESC AS PARTNAME
                         , NP.VF$VERSION AS VERSION
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , NVL(NP.SPEC, '') AS SPEC
                         , NVL(NP.PART_SIZE, '') AS PART_SIZE
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                         , NVL(COD(NP.UOM), '') AS UOM
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT
                         , VP.WORK_CMT
                         , PE.COLOR
                         , VP.WORK_COLOR
                         , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                         , NVL(PE.MBOM, '') MBOM
                         , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                         , NP.MD$USER USERID
                         , NP.OLD_CODE
                         , NP.OLD_CODE2
                         , NP.OLD_CODE3
                         , COD(NP.SPT) SPT
                         , COD(NP.PARTMPCHECK) PARTMPCHECK
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                         , PE.CUSER CUSERID
                         , 1 LEV
                         , 'F' ISLEAF
                         , VP.UCHECK AS UCHECK  -- 수정여부
                         , VP.MCHECK
                         , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                         , PE.CDATE
                         , VP.MDATE
                         , VP.user5
                         , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD -- 하위BOM 존재여부
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = ?
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productOID);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String productVersion = rs.getString("PARENT_VER") == null ? "" : rs.getString("PARENT_VER"); //제품버전
                String PROD_STATUS = rs.getString("PROD_STATUS") == null ? "" : rs.getString("PROD_STATUS");
                String PROD_CREDATE = rs.getString("PROD_CREDATE") == null ? "" : rs.getString("PROD_CREDATE"); //제품 등록일
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                String PROD_APP_DATE = rs.getString("PROD_APP_DATE") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일
                String GISONG = rs.getString("GISONG") == null ? "" : rs.getString("GISONG");


                String PARTOUID = rs.getString("PARTOUID");
                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                String HASCHILD = rs.getString("HASCHILD");


                //System.out.println(PARENTNO +">" + productVersion + " > " + PROD_APP_DATE + " >>> " + PARTNO + " > " + PARTNAME);

                ProductDto dto = new ProductDto();
                dto.setProductOid(PRODUCTOUID);
                dto.setProductNo(PARENTNO);
                dto.setProductVersion(productVersion); //제품버전
                dto.setProductStatus(PROD_STATUS);
                dto.setProductCreDate(PROD_CREDATE);
                dto.setProductModDate(PROD_MODDATE);
                dto.setProductAppdate(PROD_APP_DATE); //제품승인일
                dto.setGisong(GISONG);

                dto.setSeq(SEQ);
                dto.setPartNo(PARTNO);
                dto.setPartNoOID(PARTOUID);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockopt(BLOCK_OPT);
                dto.setGlCode(GLCODE);

                dto.setNation(NATION);
                dto.setSpec(SPEC);
                dto.setPart_size(PART_SIZE);
                dto.setQty(QTY);
                dto.setCmt(CMT);
                dto.setUcheck(UCHECK);
                dto.setUsername(USERNAME);
                dto.setUserId(USERID);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }


    /**
     * 제품의 하위 bom 조회 (제품oid와 partNo로 )
     * @param productOID
     * @param partNo
     * @return
     */
    public static ArrayList<ProductDto> findProductBOMWithOID_partNo(String productOID, String partNo) {
        //System.out.println("PartCommonUtil findProductBOMWithOID start ==-" + productOID );

        ArrayList<ProductDto> list = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = PLMDBConnection.getConnection();

            String sql = """
             SELECT
                     PE.ASSOOUID ASSOOUID
                     , PE.PRODUCTOUID PRODUCTOUID
                     , PE.PARTOUID PARTOUID
                     , PE.SEQ
                     , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) PARENTNO
                     , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
                     , (SELECT F.VF$VERSION FROM PRODUCT$VF F WHERE F.VF$OUID = PE.PRODUCTOUID) AS PARENT_VER
                     , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_CREDATE
                     , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_MODDATE
                     , (SELECT TO_CHAR(TO_DATE(PRODUCT.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_APP_DATE
                     , (SELECT PRODUCT.MD$STATUS FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_STATUS
                     , (SELECT COD(E.EL_ATYP) FROM ELV_INFO$ID A, ELV_INFO$VF E
                        WHERE A.ID$OUID = E.VF$IDENTITY AND E.vf$ouid = A.id$wip
                        AND E.MD$NUMBER = (SELECT F.MD$NUMBER FROM PRODUCT$VF F WHERE F.VF$OUID = PE.PRODUCTOUID) ) AS GISONG
                     , NP.MD$NUMBER AS PARTNO
                     , cod(NP.NATION) AS NATION
                     , NP.compen_part AS COMPEN_PART
                     , NP.MD$DESC AS PARTNAME
                     , NP.VF$VERSION AS VERSION
                     , NVL(NP.G_L_CODE, '') AS GLCODE
                     , NVL(NP.SPEC, '') AS SPEC
                     , NVL(NP.PART_SIZE, '') AS PART_SIZE
                     , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                     , (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE
                     , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                     , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) UPPERBLOCKNO
                     , NVL(COD(NP.UOM), '') AS UOM
                     , PE.QTY
                     , VP.WORK_QTY
                     , PE.CMT
                     , VP.WORK_CMT
                     , PE.COLOR
                     , VP.WORK_COLOR
                     , NVL(CODN(NP.ORIGIN_DIV), '') DIV
                     , NVL(PE.MBOM, '') MBOM
                     , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                     , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                     , NP.MD$USER USERID
                     , NP.OLD_CODE
                     , NP.OLD_CODE2
                     , NP.OLD_CODE3
                     , COD(NP.SPT) SPT
                     , COD(NP.PARTMPCHECK) PARTMPCHECK
                     , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) CUSERNAME
                     , PE.CUSER CUSERID
                     , 1 LEV
                     , 'F' ISLEAF
                     , VP.UCHECK AS UCHECK  -- 수정여부
                     , VP.MCHECK
                     , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                     , PE.CDATE
                     , VP.MDATE
                     , VP.user5
                 --    , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) AS HASCHILD -- 하위BOM 존재여부
                    FROM
                 PARTOFEBOM PE
                INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                WHERE
                 PE.PRODUCTOUID = ?
                 AND MD$NUMBER = ?
                ORDER BY TO_NUMBER(PE.SEQ)
        """;


            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productOID);
            stmt.setString(2, partNo);
            rs = stmt.executeQuery();

            while(rs.next()) {

                //PRODUCTOUID
                String PRODUCTOUID = rs.getString("PRODUCTOUID");
                String PARENTNO = rs.getString("PARENTNO");
                String productVersion = rs.getString("PARENT_VER") == null ? "" : rs.getString("PARENT_VER"); //제품버전
                String PROD_STATUS = rs.getString("PROD_STATUS") == null ? "" : rs.getString("PROD_STATUS");
                String PROD_CREDATE = rs.getString("PROD_CREDATE") == null ? "" : rs.getString("PROD_CREDATE"); //제품 등록일
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                String PROD_APP_DATE = rs.getString("PROD_APP_DATE") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일
                String GISONG = rs.getString("GISONG") == null ? "" : rs.getString("GISONG");


                String PARTOUID = rs.getString("PARTOUID");
                String SEQ = rs.getString("SEQ");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");

                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCK_OPT = rs.getString("BLOCK_OPT"); //내작외작
                String GLCODE = rs.getString("GLCODE");

                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");

                String QTY = rs.getString("QTY");
                String CMT = rs.getString("CMT");
                String UCHECK = rs.getString("UCHECK");
                String USERNAME = rs.getString("USERNAME");
                String USERID = rs.getString("USERID");
                //String HASCHILD = rs.getString("HASCHILD") == null ? "" : rs.getString("HASCHILD");


                //System.out.println(PARENTNO +">" + productVersion + " > " + PROD_APP_DATE + " >>> " + PARTNO + " > " + PARTNAME);

                ProductDto dto = new ProductDto();
                dto.setProductOid(PRODUCTOUID);
                dto.setProductNo(PARENTNO);
                dto.setProductVersion(productVersion); //제품버전
                dto.setProductStatus(PROD_STATUS);
                dto.setProductCreDate(PROD_CREDATE);
                dto.setProductModDate(PROD_MODDATE);
                dto.setProductAppdate(PROD_APP_DATE); //제품승인일
                dto.setGisong(GISONG);

                dto.setSeq(SEQ);
                dto.setPartNo(PARTNO);
                dto.setPartNoOID(PARTOUID);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockopt(BLOCK_OPT);
                dto.setGlCode(GLCODE);

                dto.setNation(NATION);
                dto.setSpec(SPEC);
                dto.setPart_size(PART_SIZE);
                dto.setQty(QTY);
                dto.setCmt(CMT);
                dto.setUcheck(UCHECK);
                dto.setUsername(USERNAME);
                dto.setUserId(USERID);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }
        return list;
    }


    /**
     * 제품 1레벨의 하위 BOM 조회
     * @param productOid
     * @param partOid
     */
    public static ArrayList<ProductDto> findProductDownLevel(String productOid, String partOid) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList<ProductDto> list = new ArrayList<>();

        try {

            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                    			 (ROWNUM-1) idx,
                    			 (LEVEL+1)  LEV,
                    			 A.SF$OUID ASSOOUID,
                    			 A.AS$END1 END1,
                    			 A.AS$END2 END2,
                    			 A.END1_HEXOUID END1HEX,
                    			 A.END2_HEXOUID END2HEX,
                    			 A.MD$SEQUENCE SEQ,
                    			 (SELECT MD$NUMBER FROM NORMALPART$VF WHERE VF$OUID = A.AS$END1) PARENTNO,
                    			 (SELECT NVL(COD(ORIGIN_DIV), '') FROM NORMALPART$VF WHERE VF$OUID = A.AS$END1) PARENTDIV,
                    			 (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = A.AS$END2) CADCNT,
                    			 NP.MD$NUMBER AS PARTNO,
                    			 NP.MD$DESC AS PARTNAME,
                    			 NP.VF$VERSION AS VERSION,
                    			 NVL(NP.G_L_CODE, '') AS GLCODE,
                    			 NVL(COD(NP.NATION), '') AS NATION,
                    			 NVL(NP.COMPEN_PART, '') AS COMPEN_PART,
                    			 NVL(A.SERVICEFLAG, '') SERVICEFLAG,
                    			 NVL(NP.SPEC, '') AS SPEC,
                    			 NVL(NP.PART_SIZE, '') AS PART_SIZE,
                    			 (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO,
                    			 (SELECT K.MD$DESC FROM BLOCKNO$SF K WHERE K.SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNAME,
                    			 (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE,
                    			 (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT,
                    			 (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) AS UPPERBLOCKNO,
                    			 NVL(COD(NP.UOM), '') AS UOM,
                    			 A.QTY AS QTY,
                    			 VP.WORK_QTY,
                    			 A.CMT,
                    			 VP.WORK_CMT,
                    			 A.COLOR,
                    			 VP.WORK_COLOR,
                    			 NVL(CODN(NP.ORIGIN_DIV), '') AS DIV,
                    			 NVL(A.MBOM, '') MBOM,
                    			 NVL(COD(NP.PART_MBOM), '') PART_MBOM,
                    			    (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) AS USERNAME,
                    			 NP.MD$USER USERID,
                    			 NP.OLD_CODE,NP.OLD_CODE2, NP.OLD_CODE3, COD(NP.SPT) SPT, COD(NP.PARTMPCHECK) AS PARTMPCHECK,
                    			    (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = B.CUSER) CUSERNAME, B.CUSER AS CUSERID,
                    			    NVL(A.PART_SPT, '') AS PART_SPT,
                    			 DECODE(CONNECT_BY_ISLEAF, 0,'F', 1, 'T') AS ISLEAF,
                                 VP.UCHECK AS UCHECK, VP.MCHECK, NVL(COD(NP.PART_DIVISION), '') PART_DIVISION, A.MD$CDATE CDATE, VP.MDATE,
                                 VP.user5
                    			 FROM PARTOFPART$AC A
                    			 INNER JOIN NORMALPART$VF NP ON AS$END2 = NP.VF$OUID
                    			 LEFT OUTER JOIN VARIABLEPART_NEW VP ON SF$OUID = VP.ASSOOUID AND VP.PRODUCTOUID = ?
                                    LEFT OUTER JOIN PARTOFEBOM B ON B.PARTOUID = ? 
                                    AND B.PRODUCTOUID = ?
                                START WITH AS$END1 = ?
                    			 CONNECT BY PRIOR AS$END2 = AS$END1
                    			 ORDER SIBLINGS BY CAST(MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                    """;

            //LEFT OUTER JOIN VARIABLEPART_NEW VP ON SF$OUID = VP.ASSOOUID AND VP.PRODUCTOUID = #{lProdOuid} "
            //       + " LEFT OUTER JOIN PARTOFEBOM B ON B.PARTOUID = #{lPartOuid} AND B.PRODUCTOUID = #{lProdOuid} "
            //      + " START WITH AS$END1 = #{lPartOuid} "

            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productOid);
            stmt.setString(2, partOid);
            stmt.setString(3, productOid);
            stmt.setString(4, partOid);

            rs = stmt.executeQuery();

            while(rs.next()) {
                String LEV = rs.getString("LEV");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");
                String GLCODE = rs.getString("GLCODE");
                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCKNAME = rs.getString("BLOCKNAME");
                String BLOCK_OPT = rs.getString("BLOCK_OPT");
                String UOM = rs.getString("PARTNAME");
                String QTY = rs.getString("QTY");
                String DIV = rs.getString("DIV");
                String UCHECK = rs.getString("UCHECK");
                String WORK_QTY = rs.getString("WORK_QTY");

                ProductDto dto = new ProductDto();
                dto.setLev(LEV);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setGlCode(GLCODE);
                dto.setNation(NATION);
                dto.setPart_size(PART_SIZE);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNAME);
                dto.setUcheck(UCHECK);
                dto.setQty(QTY);
                dto.setSpec(SPEC);
                dto.setWorkQty(WORK_QTY);



                if (QTY.contains("CE") && WORK_QTY.equals("0")) {

                } else {
                    list.add(dto);
                    System.out.println(LEV + ">" + PARTNO + " > " + PARTNAME + " > " + QTY + " > " + WORK_QTY);
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return list;
    }


    /**
     * 제품 1레벨의 하위 BOM 조회
     * @param productOid
     * @param partOid
     */
    public static ArrayList<BomPartDTO> findProductDownLevelBOM(String productOid, String partOid, ArrayList<BomPartDTO> list) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        //ArrayList<ProductDto> list = new ArrayList<>();

        try {

            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                    			 (ROWNUM-1) idx,
                    			 (LEVEL+1)  LEV,
                    			 A.SF$OUID ASSOOUID,
                    			 A.AS$END1 END1,
                    			 A.AS$END2 END2,
                    			 A.END1_HEXOUID END1HEX,
                    			 A.END2_HEXOUID END2HEX,
                    			 A.MD$SEQUENCE SEQ,
                    			 (SELECT MD$NUMBER FROM NORMALPART$VF WHERE VF$OUID = A.AS$END1) PARENTNO,
                    			 (SELECT NVL(COD(ORIGIN_DIV), '') FROM NORMALPART$VF WHERE VF$OUID = A.AS$END1) PARENTDIV,
                    			 (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = A.AS$END2) CADCNT,
                    			 NP.MD$NUMBER AS PARTNO,
                    			 NP.MD$DESC AS PARTNAME,
                    			 NP.VF$VERSION AS VERSION,
                    			 NVL(NP.G_L_CODE, '') AS GLCODE,
                    			 NVL(COD(NP.NATION), '') AS NATION,
                    			 NVL(NP.COMPEN_PART, '') AS COMPEN_PART,
                    			 NVL(A.SERVICEFLAG, '') SERVICEFLAG,
                    			 NVL(NP.SPEC, '') AS SPEC,
                    			 NVL(NP.PART_SIZE, '') AS PART_SIZE,
                    			 (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO,
                    			 (SELECT K.MD$DESC FROM BLOCKNO$SF K WHERE K.SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNAME,
                    			 (SELECT NVL(LOSSRATE, '') FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) LOSSRATE,
                    			 (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT,
                    			 (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) AS UPPERBLOCKNO,
                    			 NVL(COD(NP.UOM), '') AS UOM,
                    			 A.QTY AS QTY,
                    			 VP.WORK_QTY,
                    			 A.CMT,
                    			 VP.WORK_CMT,
                    			 A.COLOR,
                    			 VP.WORK_COLOR,
                    			 NVL(CODN(NP.ORIGIN_DIV), '') AS DIV,
                    			 NVL(A.MBOM, '') MBOM,
                    			 NVL(COD(NP.PART_MBOM), '') PART_MBOM,
                    			    (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) AS USERNAME,
                    			 NP.MD$USER USERID,
                    			 NP.OLD_CODE,NP.OLD_CODE2, NP.OLD_CODE3, COD(NP.SPT) SPT, COD(NP.PARTMPCHECK) AS PARTMPCHECK,
                    			    (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = B.CUSER) CUSERNAME, B.CUSER AS CUSERID,
                    			    NVL(A.PART_SPT, '') AS PART_SPT,
                    			 DECODE(CONNECT_BY_ISLEAF, 0,'F', 1, 'T') AS ISLEAF,
                                 VP.UCHECK AS UCHECK, VP.MCHECK, NVL(COD(NP.PART_DIVISION), '') PART_DIVISION, A.MD$CDATE CDATE, VP.MDATE,
                                 VP.user5
                    			 FROM PARTOFPART$AC A
                    			 INNER JOIN NORMALPART$VF NP ON AS$END2 = NP.VF$OUID
                    			 LEFT OUTER JOIN VARIABLEPART_NEW VP ON SF$OUID = VP.ASSOOUID AND VP.PRODUCTOUID = ?
                                    LEFT OUTER JOIN PARTOFEBOM B ON B.PARTOUID = ? 
                                    AND B.PRODUCTOUID = ?
                                START WITH AS$END1 = ?
                    			 CONNECT BY PRIOR AS$END2 = AS$END1
                    			 ORDER SIBLINGS BY CAST(MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR)
                    """;

            //LEFT OUTER JOIN VARIABLEPART_NEW VP ON SF$OUID = VP.ASSOOUID AND VP.PRODUCTOUID = #{lProdOuid} "
            //       + " LEFT OUTER JOIN PARTOFEBOM B ON B.PARTOUID = #{lPartOuid} AND B.PRODUCTOUID = #{lProdOuid} "
            //      + " START WITH AS$END1 = #{lPartOuid} "

            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productOid);
            stmt.setString(2, partOid);
            stmt.setString(3, productOid);
            stmt.setString(4, partOid);

            rs = stmt.executeQuery();

            while(rs.next()) {
                String LEV = rs.getString("LEV");
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String VERSION = rs.getString("VERSION");
                String GLCODE = rs.getString("GLCODE");
                String NATION = rs.getString("NATION");
                String SPEC = rs.getString("SPEC");
                String PART_SIZE = rs.getString("PART_SIZE");
                String BLOCKNO = rs.getString("BLOCKNO");
                String BLOCKNAME = rs.getString("BLOCKNAME");
                String BLOCK_OPT = rs.getString("BLOCK_OPT");
                String UOM = rs.getString("UOM");
                String QTY = rs.getString("QTY");
                String DIV = rs.getString("DIV");
                String UCHECK = rs.getString("UCHECK");
                String WORK_QTY = rs.getString("WORK_QTY");
                String WORK_CMT = rs.getString("WORK_CMT");

                //ProductDto dto = new ProductDto();
                BomPartDTO dto = new BomPartDTO();
                dto.setLev(LEV);
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setVersion(VERSION);
                dto.setGlCode(GLCODE);
                dto.setNation(NATION);
                dto.setPart_size(PART_SIZE);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNAME);
                dto.setBlockopt(BLOCK_OPT);
                dto.setUom(UOM);
                dto.setUcheck(UCHECK);
                dto.setQty(QTY);
                dto.setSpec(SPEC);
                dto.setWorkQty(WORK_QTY);
                dto.setWorkCmt(WORK_CMT);

                if (QTY.contains("CE") && WORK_QTY.equals("0")) {

                } else {
                    //System.out.println(LEV + ">" + PARTNO + " > " + PARTNAME + " > " + QTY + " > " + WORK_QTY);

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return list;
    }

    /**
     * 해당 호기의 최초설계BOM 조회
     * @param productNo
     * @return
     */
    public static ArrayList<ProductDto> getInitialDesignBom(String productNo) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList<ProductDto> list = new ArrayList<>();

        try {
            con = PLMDBConnection.getConnection();

            /*String sql = """
                    WITH PRODUCT_BOM AS(
                    	  SELECT B.MD$NUMBER  , B.VF$VERSION , B.MD$DESC , C.MD$NUMBER PART , C.MD$DESC PART_DESC, C.SPEC,
                    	         C.BLOCKNO_NUMBER,cod(E.block_opt) block_opt, A.QTY,
                    	         D.UCHECK, A.CDATE, A.CMT
                    	  FROM PARTOFEBOM A
                      	  INNER JOIN PRODUCT$VF B ON    A.PRODUCTOUID = B.VF$OUID
                      	  INNER JOIN NORMALPART$VF C ON    A.PARTOUID = C.VF$oUID
                      	  LEFT OUTER JOIN VARIABLEPART_NEW D ON   A.ASSOOUID = D.ASSOOUID    AND A.PRODUCTOUID = D.PRODUCTOUID
                      	  INNER JOIN blockno$sf E ON  'blockno$sf@'||lower(dectohex(E.sf$ouid)) =  c.blockno
                      	  WHERE B.MD$NUMBER = ?
                      	  ), FIRST_BOM_VF AS
                     	  (
                     	      SELECT BLOCK_OPT, MIN(TO_NUMBER(VF$VERSION)) FIRST_VF
                      	      FROM PRODUCT_BOM
                      	      GROUP BY  BLOCK_OPT
                      	  ),
                      	 FIRST_PRODUCT_BOM AS
                      	  (
                      	      SELECT * FROM (
                      	         SELECT A.*, ROW_NUMBER() OVER (PARTITION BY MD$NUMBER, PART, CDATE ORDER BY VF$VERSION) RN
                      	          FROM PRODUCT_BOM A
                      	         INNER JOIN FIRST_BOM_VF B ON A.BLOCK_OPT = B.BLOCK_OPT AND A.VF$VERSION = B.FIRST_VF) WHERE RN=1
                      	  )
                      	      SELECT * FROM FIRST_PRODUCT_BOM WHERE QTY > 0 or (QTY=0 AND UCHECK =1)
                    """;*/

            String sql = """
                    WITH PRODUCT_BOM AS(
                    	  SELECT B.MD$NUMBER  , B.VF$VERSION , B.MD$DESC , C.MD$NUMBER PART , C.MD$DESC PART_DESC, C.SPEC,
                    	         C.BLOCKNO_NUMBER,cod(E.block_opt) block_opt, A.QTY,
                    	         D.UCHECK, A.CDATE, A.CMT
                    	  FROM PARTOFEBOM A
                      	  INNER JOIN PRODUCT$VF B ON    A.PRODUCTOUID = B.VF$OUID
                      	  INNER JOIN NORMALPART$VF C ON    A.PARTOUID = C.VF$oUID
                      	  LEFT OUTER JOIN VARIABLEPART_NEW D ON   A.ASSOOUID = D.ASSOOUID    AND A.PRODUCTOUID = D.PRODUCTOUID
                      	  INNER JOIN blockno$sf E ON  'blockno$sf@'||lower(dectohex(E.sf$ouid)) =  c.blockno
                      	  WHERE B.MD$NUMBER = ?
                      	  ),
                        PRODUCT_BOM_NUM AS (
                        SELECT A.*,
                               TO_NUMBER(A.VF$VERSION) AS VF_VERSION_NUM
                        FROM PRODUCT_BOM A
                        WHERE REGEXP_LIKE(A.VF$VERSION, '^[0-9]+$')
                    ),
                    FIRST_BOM_VF AS (
                        SELECT BLOCK_OPT,
                               MIN(VF_VERSION_NUM) FIRST_VF
                        FROM PRODUCT_BOM_NUM
                        GROUP BY BLOCK_OPT
                    ),
                    FIRST_PRODUCT_BOM AS (
                        SELECT *
                        FROM (
                            SELECT A.*,
                                   ROW_NUMBER() OVER (
                                       PARTITION BY A.MD$NUMBER, A.PART, A.CDATE
                                       ORDER BY A.VF_VERSION_NUM
                                   ) RN
                            FROM PRODUCT_BOM_NUM A
                            INNER JOIN FIRST_BOM_VF B
                                ON A.BLOCK_OPT = B.BLOCK_OPT
                               AND A.VF_VERSION_NUM = B.FIRST_VF
                        )
                        WHERE RN = 1
                    )
                    SELECT MD$NUMBER,
                           VF$VERSION,
                           MD$DESC,
                           PART,
                           PART_DESC,
                           SPEC,
                           BLOCKNO_NUMBER,
                           BLOCK_OPT,
                           QTY,
                           UCHECK,
                           CDATE,
                           CMT
                    FROM FIRST_PRODUCT_BOM
                    WHERE QTY > 0
                       OR (QTY = 0 AND UCHECK = '1')
                    """;

            System.out.println("sql.toString() = " + sql.toString());
            stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, productNo);

            rs = stmt.executeQuery();

            while(rs.next()) {
                String hogi = rs.getString("MD$NUMBER");
                String productVer = rs.getString("VF$VERSION");
                String hogiName = rs.getString("MD$DESC");
                String partNo = rs.getString("PART");
                String partName = rs.getString("PART_DESC");
                String SPEC = rs.getString("SPEC");
                String BLOCKNO_NUMBER = rs.getString("BLOCKNO_NUMBER");
                String block_opt = rs.getString("block_opt");
                String ucheck = rs.getString("UCHECK");
                String qty = rs.getString("QTY");
                String cdate = rs.getString("cdate");
                String cmt = rs.getString("CMT");

                ProductDto dto = new ProductDto();
                dto.setProductNo(hogi);
                dto.setProductVersion(productVer);
                dto.setProductName(hogiName);
                dto.setPartNo(partNo);
                dto.setPartName(partName);
                dto.setSpec(SPEC);
                dto.setBlockNo(BLOCKNO_NUMBER);
                dto.setBlockopt(block_opt);
                dto.setUcheck(ucheck);
                dto.setQty(qty);
                dto.setCmt(cmt);

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return list;
    }
}
