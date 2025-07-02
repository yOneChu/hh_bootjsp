package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SubaeCommonUtil {

    /**
     * PLM에서 중국부품 조회
     * @param param
     * @return
     */
    public static ArrayList<PartInfoDTO> findOneFromPartNo(PartInfoDTO param) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<PartInfoDTO> result = new ArrayList<PartInfoDTO>();

        try {

            //con = DBconnectionInfo.getPDM_DBConnection();
            con = PLMDBConnection.getConnection();

            String sql = """
                    SELECT A.md$number AS PARTNO,
                           A.md$desc AS PARTNAME,
                           A.BLOCKNO_NUMBER AS BLOCKNO, 
                           NVL((SELECT BLOCKNO$SF.MD$DESC FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(A.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(A.BLOCKNO, 12))))), '-') AS BLOCKNO_NAME,
                           A.spec AS SPEC, 
                           A.g_l_code AS GL_CODE,
                           A.PART_SIZE,
                           A.nation,
                           A.VF$VERSION AS VERSION,
                           CODN(A.NATION) AS NATION_KO, 
                           cod(A.uom) AS UOM,
                           CODN(A.origin_div) AS origin_div, --외주
                           cod(A.spt) AS spt,
                           A.MD$STATUS AS PART_STATUS,
                           --A.PART_STATUS,
                           --CODN(A.PART_STATUS)  ,
                           CODN(A.part_status) AS ACTIVE,
                           A.old_code, A.old_code2, A.old_code3, old_code4
                    FROM normalpart$vf A, normalpart$id B
                    WHERE A.vf$ouid = B.id$last
                      AND LENGTH (A.md$number)=11 AND A.NATION = 2803457356
                    """;

            String pPartNo = param.getPartNo();
            String pBlockNo = param.getBlockNo();
            String pPartName = param.getPartName();
            String pSpec = param.getSpec();
            String div = param.getDiv();
            String status = param.getStatus();

            if (pPartNo != null && !"".equals(pPartNo)) {
                sql += "AND A.md$number like '%" + pPartNo + "%'";
            }

            if (pBlockNo != null && !"".equals(pBlockNo)) {
                sql += " AND A.BLOCKNO_NUMBER like '%" + pBlockNo + "%'";
            }

            if (pPartName != null && !"".equals(pPartName)) {
                sql += " AND A.md$desc like '%" + pPartName + "%'";
            }

            if (div != null && !"".equals(div)) {
                sql += " AND CODN(A.origin_div) like '%" + div + "%'";
            }

            if (status != null && !"".equals(status) && !"ALL".equals(status)) {
                sql += " AND CODN(A.part_status) = '" + status + "'";
            }

            if (pSpec != null && !"".equals(pSpec)) {
                sql += " AND A.SPEC like '%" + pSpec.trim() + "%'";
            }

            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PARTNO = rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME");
                String GLCODE = rs.getString("GL_CODE") == null ? "" : rs.getString("GL_CODE");
                String SPEC = rs.getString("SPEC") == null ? "" : rs.getString("SPEC");
                String PART_SIZE   = rs.getString("PART_SIZE") == null ? "" : rs.getString("PART_SIZE");
                String origin_div = rs.getString("origin_div");

                String NATION_KO   = rs.getString("NATION_KO");
                String PART_STATUS   = rs.getString("PART_STATUS");
                String UOM   = rs.getString("UOM");
                String ACTIVE   = rs.getString("ACTIVE");

                String BLOCKNO   = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String BLOCKNO_NAME   = rs.getString("BLOCKNO_NAME") == null ? "" : rs.getString("BLOCKNO_NAME");

                PartInfoDTO dto = new PartInfoDTO();
                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setGlCode(GLCODE);
                dto.setSpec(SPEC);
                dto.setPartSize(PART_SIZE);
                dto.setUom(UOM);
                dto.setDiv(origin_div);
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNO_NAME);
                dto.setStatus(PART_STATUS);
                dto.setActive(ACTIVE);

                result.add(dto);
                //System.out.println(PARTNO + " - " + BLOCKNO + " - " + DESCVAL + " , " + GLCODE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


    // 1.2025년도 수배율 대상 제품번호 조회
    public static ArrayList<String> findSubaeProductNo() {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<String>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                      DISTINCT V.MD$NUMBER AS PRODUCTNO
                      FROM product$vf V
                      WHERE V.MD$STATUS = 'RLS'
                      AND SUBSTR(V.MD$CDATE, 0,4) = '2025'
                      AND SUBSTR(V.MD$NUMBER, 0, 1) NOT IN ('Q', 'V', '0', 'K', '1', 'H', 'T', 'M', 'C')
                """;

            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, year);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String productNo = rs.getString("PRODUCTNO");
                result.add(productNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }



    //2.제품번호로 모든 버전의 제품OID 조회
    public static ArrayList<ProductDto> findProductOIDS(String productNo) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<ProductDto> result = new ArrayList<ProductDto>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                            V.VF$OUID AS OID,
                            V.MD$NUMBER AS PRODUCTNO,
                            V.MD$DESC AS PRO_NAME,
                            V.VF$VERSION AS VER,
                            TO_CHAR(TO_DATE(V.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS CREDATE,
                            TO_CHAR(TO_DATE(V.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS MODDATE,
                            TO_CHAR(TO_DATE(V.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS APPDATE,
                            (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = V.MD$USER) USERNAME,
                            V.MD$STATUS,
                            V.E_BLOCK_F,
                            V.M_BLOCK_F
                           -- V.*
                        FROM product$vf V
                        WHERE V.MD$STATUS = 'RLS'
                        AND SUBSTR(V.MD$CDATE, 0,4) = '2025'
                        AND SUBSTR(V.MD$NUMBER, 0, 1) NOT IN ('Q', 'V', '0', 'K', '1', 'H', 'T', 'M')
                        AND V.MD$NUMBER = ?
                        ORDER BY V.VF$VERSION ASC
                """;

            //System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PRODUCTNO = rs.getString("PRODUCTNO");
                String PRO_NAME = rs.getString("PRO_NAME") == null ? "" : rs.getString("PRO_NAME");
                String VER = rs.getString("VER") == null ? "" : rs.getString("VER");
                String CREDATE   = rs.getString("CREDATE") == null ? "" : rs.getString("CREDATE");
                String MODDATE   = rs.getString("MODDATE") == null ? "" : rs.getString("MODDATE");
                String APPDATE   = rs.getString("APPDATE") == null ? "" : rs.getString("APPDATE");
                //String STATUS   = rs.getString("STATUS");

                ProductDto dto = new ProductDto();
                dto.setProductOid(OID);
                dto.setProductNo(PRODUCTNO);
                dto.setProductName(PRO_NAME);
                dto.setVersion(VER);
                dto.setProductCreDate(CREDATE);
                dto.setProductModDate(MODDATE);
                dto.setProductAppdate(APPDATE);
                //dto.setProductStatus(STATUS);

                result.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }




    //3.제품의 OID로 최초설계 BOM인지 검사
    public static boolean checkDesignBOM(String productOID) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        boolean result = false;

        //ArrayList<ProductDto> result = new ArrayList<ProductDto>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                          PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO
                         , (SELECT PRODUCT.VF$VERSION FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PARENTNO_VER
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_MODDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_APP_DATE
                         , NP.VF$VERSION AS PART_VERSION
                         , NP.MD$NUMBER AS PARTNO
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCK_OPT
                         , VP.UCHECK AS UCHECK  -- 수정여부
                         , cod(NP.NATION) NATION
                         , NP.MD$DESC PARTNAME
                         , PE.QTY
                         , VP.WORK_QTY
                         , PE.CMT AS CMT
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) AS USERNAME
                         , (   SELECT
                             SUM(VVP.UCHECK)
                            FROM
                             PARTOFEBOM PPE
                            INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                            LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                            WHERE
                            PPE.PRODUCTOUID = PE.PRODUCTOUID
                            GROUP BY PPE.PRODUCTOUID
                        ) AS MODIFY_CNT
                         , (   SELECT
                             SUM(PPE.QTY)
                            FROM
                             PARTOFEBOM PPE
                            INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                            LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                            WHERE
                            PPE.PRODUCTOUID = PE.PRODUCTOUID
                            AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('M','C')
                            GROUP BY PPE.PRODUCTOUID
                        ) AS MECH_CNT
                         , (   SELECT
                             SUM(PPE.QTY)
                            FROM
                             PARTOFEBOM PPE
                            INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                            LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                            WHERE
                            PPE.PRODUCTOUID = PE.PRODUCTOUID
                            AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('1','2','3')
                            GROUP BY PPE.PRODUCTOUID
                        ) AS ELE_CNT
                        FROM
                         PARTOFEBOM PE
                        INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                        WHERE
                         -- PE.PRODUCTOUID = 제품의OID
                        PE.PRODUCTOUID = ?
                        ORDER BY TO_NUMBER(PE.SEQ)
                """;

            //System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productOID);

            rs = pstmt.executeQuery();

            int modCnt = 0; //변경자재 건수
            double mCnt = 0;
            double eCnt = 0;
            String PRODUCTNO = "";
            String PARENTNO_VER = "";
            String PROD_APP_DATE = "";

            ArrayList<String> aaa = new ArrayList<>();
            while(rs.next()) {
                PRODUCTNO = rs.getString("PARENTNO"); //제품번호
                PARENTNO_VER = rs.getString("PARENTNO_VER") == null ? "" : rs.getString("PARENTNO_VER"); //제품버전
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                PROD_APP_DATE = rs.getString("PARENTNO_VER") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");

                
                String PARTNO = rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO");
                String PART_VERSION = rs.getString("PART_VERSION") == null ? "" : rs.getString("PART_VERSION");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");
                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");

                String UCHECK = rs.getString("UCHECK") == null ? "" : rs.getString("UCHECK");

                String MODIFY_CNT = rs.getString("MODIFY_CNT") == null ? "" : rs.getString("MODIFY_CNT");
                String MECH_CNT = rs.getString("MECH_CNT") == null ? "" : rs.getString("MECH_CNT");
                String ELE_CNT = rs.getString("ELE_CNT") == null ? "" : rs.getString("ELE_CNT");


                if(MECH_CNT != null && !"".equals(MECH_CNT)) {
                    mCnt = Double.parseDouble(MECH_CNT);
                }

                if(ELE_CNT != null && !ELE_CNT.equals("")) {
                    eCnt = Double.parseDouble(ELE_CNT);
                }

                if(MODIFY_CNT != null && !MODIFY_CNT.equals("")) {
                    modCnt = Integer.parseInt(MODIFY_CNT);
                }

                if(mCnt > 0 && eCnt > 0) {

                    result = true;
                    //최초설계 제품이다.
                    //DB에 저장
                    //System.out.println(PRODUCTNO + " > " + PARENTNO_VER + "> " + PROD_APP_DATE + " > " + modCnt + " > " + mCnt + " > " + eCnt);
                    //return true;
                    aaa.add(PARTNO);
                } else {
                    //XXX
                    result = false;
                    System.out.println(PRODUCTNO + " > " + PARENTNO_VER + " xxxxxxxx");
                    return false;
                }

            }

            System.out.println(PRODUCTNO + " > " + aaa.size() + "> " + PROD_APP_DATE + " > " + modCnt + " > " + mCnt + " > " + eCnt);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }
}



