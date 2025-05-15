package com.kyhslam.util;

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
                         , (SELECT COUNT(*) FROM PARTANDCAD$AS WHERE AS$END1 = PE.PARTOUID) CADCNT
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
                dto.setBlock_opt(BLOCK_OPT);
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

                ProductDto dto = new ProductDto();
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

                list.add(dto);
                System.out.println(PARTNO + " > " + PARTNAME + " > " + GLCODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, stmt, rs);
        }

        return list;
    }
}
