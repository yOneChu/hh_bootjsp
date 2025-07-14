package com.kyhslam.util;

import com.kyhslam.dto.PartInfoDTO;
import com.kyhslam.dto.ProductDto;
import org.springframework.util.StringUtils;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
                      --AND SUBSTR(V.MD$CDATE, 0,6) = '202506'
                      --AND SUBSTR(V.MD$NUMBER, 0, 1) NOT IN ('Q', 'V', '0', 'K', '1', 'H', 'T', 'M', 'C')
                      AND V.MD$NUMBER NOT LIKE '%Q%'
                      AND V.MD$NUMBER NOT LIKE '%V%'
                      AND V.MD$NUMBER NOT LIKE '%NB%'
                      AND V.MD$NUMBER NOT LIKE '%NC%'
                      AND V.MD$NUMBER NOT LIKE '%NS%'
                      AND V.MD$NUMBER NOT LIKE '%M%'
                      AND V.MD$NUMBER NOT LIKE '%TEST%'
                      AND V.MD$NUMBER NOT LIKE '%T%'
                      AND V.MD$DESC NOT LIKE '%가설계%'
                      AND V.MD$NUMBER NOT LIKE '%Y%'
                      AND V.MD$NUMBER NOT LIKE 'J%'
                      AND LENGTH(V.MD$NUMBER) < 10
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
                            V.VF$VERSION AS PRO_VER,
                            TO_CHAR(TO_DATE(V.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS CREDATE,
                            TO_CHAR(TO_DATE(V.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS MODDATE,
                            TO_CHAR(TO_DATE(V.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS APPDATE,
                            (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = V.MD$USER) USERNAME,
                            V.MD$STATUS,
                            V.E_BLOCK_F,
                            V.M_BLOCK_F,
                            (SELECT COD(E.EL_ATYP) FROM ELV_INFO$ID A, ELV_INFO$VF E
                             WHERE A.ID$OUID = E.VF$IDENTITY AND E.vf$ouid = A.id$wip AND E.MD$STATUS = 'RLS'
                             AND E.MD$NUMBER = V.MD$NUMBER) AS GISONG
                           -- V.*
                        FROM product$vf V
                        WHERE V.MD$STATUS = 'RLS'
                        --AND SUBSTR(V.MD$CDATE, 0,4) = '2025'
                        --AND SUBSTR(V.MD$NUMBER, 0, 1) NOT IN ('Q', 'V', '0', 'K', '1', 'H', 'T', 'M')
                        AND V.MD$NUMBER NOT LIKE '%Q%'
                        AND V.MD$NUMBER NOT LIKE '%V%'
                        AND V.MD$NUMBER NOT LIKE '%NB%'
                        AND V.MD$NUMBER NOT LIKE '%NC%'
                        AND V.MD$NUMBER NOT LIKE '%NS%'
                        AND V.MD$NUMBER NOT LIKE '%M%'
                        AND V.MD$NUMBER NOT LIKE '%TEST%'
                        AND V.MD$NUMBER NOT LIKE '%T%'
                        AND V.MD$DESC NOT LIKE '%가설계%'
                        AND V.MD$NUMBER NOT LIKE '%Y%'
                        AND V.MD$NUMBER NOT LIKE 'J%'
                        AND V.MD$NUMBER = ?
                        --ORDER BY V.VF$VERSION ASC
                        ORDER BY V.MD$CDATE ASC
                """;

            //Q,V,NB,NC,NS,M,TEST, T
            //System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, productNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String OID = rs.getString("OID");
                String PRODUCTNO = rs.getString("PRODUCTNO");
                String PRO_NAME  = rs.getString("PRO_NAME") == null ? "" : rs.getString("PRO_NAME");
                String PRO_VER   = rs.getString("PRO_VER") == null ? "" : rs.getString("PRO_VER");
                String CREDATE   = rs.getString("CREDATE") == null ? "" : rs.getString("CREDATE");
                String MODDATE   = rs.getString("MODDATE") == null ? "" : rs.getString("MODDATE");
                String APPDATE   = rs.getString("APPDATE") == null ? "" : rs.getString("APPDATE");
                String GISONG = rs.getString("GISONG") ==  null ? "" : rs.getString("GISONG");
                //String STATUS   = rs.getString("STATUS");

                ProductDto dto = new ProductDto();
                dto.setProductOid(OID);
                dto.setProductNo(PRODUCTNO);
                dto.setProductName(PRO_NAME);
                dto.setProductVersion(PRO_VER);
                dto.setProductCreDate(CREDATE);
                dto.setProductModDate(MODDATE);
                dto.setProductAppdate(APPDATE);
                dto.setGisong(GISONG);

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

    /**
     * 3.제품의 OID로 최초설계 BOM인지 검사
     * @param productOID
     * @param map
     * @return
     */
    public static boolean checkDesignBOM(String productOID, ArrayList<ProductDto> partList, HashMap<String,String> map, HashSet<String> dupCheck) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        boolean result = false;

        if (map.containsKey("APP_DATE")) {
            return true;
        }

        //ArrayList<ProductDto> result = new ArrayList<ProductDto>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                          PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO
                         , (SELECT PRODUCT.VF$VERSION FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PARENTNO_VER
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_CREDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_MODDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_APP_DATE
                         , NP.VF$VERSION AS PART_VERSION
                         , NP.MD$NUMBER AS PARTNO
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT
                         , VP.UCHECK AS UCHECK  -- 수정여부
                         , cod(NP.NATION) NATION
                         , NP.MD$DESC AS PARTNAME
                         , PE.QTY AS PART_QTY
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
                        AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('M')
                        GROUP BY PPE.PRODUCTOUID
                    ) AS M_CNT
                    , (   SELECT
                         SUM(PPE.QTY)
                        FROM
                         PARTOFEBOM PPE
                        INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                        WHERE
                        PPE.PRODUCTOUID = PE.PRODUCTOUID
                        AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('C')
                        GROUP BY PPE.PRODUCTOUID
                    ) AS C_CNT
                     , (   SELECT
                         SUM(PPE.QTY)
                        FROM
                         PARTOFEBOM PPE
                        INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                        WHERE
                        PPE.PRODUCTOUID = PE.PRODUCTOUID
                        AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('1')
                        GROUP BY PPE.PRODUCTOUID
                    ) AS ONE_CNT
                    , (   SELECT
                         SUM(PPE.QTY)
                        FROM
                         PARTOFEBOM PPE
                        INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                        WHERE
                        PPE.PRODUCTOUID = PE.PRODUCTOUID
                        AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('2')
                        GROUP BY PPE.PRODUCTOUID
                    ) AS TWO_CNT
                    , (   SELECT
                         SUM(PPE.QTY)
                        FROM
                         PARTOFEBOM PPE
                        INNER JOIN NORMALPART$VF NNP ON PPE.PARTOUID = NNP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VVP ON VVP.PRODUCTOUID = PPE.PRODUCTOUID AND VVP.ASSOOUID = PPE.ASSOOUID
                        WHERE
                        PPE.PRODUCTOUID = PE.PRODUCTOUID
                        AND (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NNP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NNP.BLOCKNO, 12))))) IN ('3')
                        GROUP BY PPE.PRODUCTOUID
                    ) AS THREE_CNT
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


            HashMap<String, String> flagMap = new HashMap<>();

            int m_ModCount = 0;
            int c_ModCount = 0;
            int one_ModCnt = 0;
            int two_ModCnt = 0;
            int three_ModCnt = 0;

            int modCnt = 0; //변경자재 건수
            double mCnt = 0;
            double cCnt = 0;
            double oneCnt = 0;
            double twoCnt = 0;
            double threeCnt = 0;
            String productNo = "";
            String productVersion = "";
            String PROD_APP_DATE = "";
            String PROD_CREDATE = "";

            while(rs.next()) {
                productNo = rs.getString("PARENTNO"); //제품번호
                productVersion = rs.getString("PARENTNO_VER") == null ? "" : rs.getString("PARENTNO_VER"); //제품버전
                PROD_CREDATE = rs.getString("PROD_CREDATE") == null ? "" : rs.getString("PROD_CREDATE"); //제품 등록일
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                PROD_APP_DATE = rs.getString("PARENTNO_VER") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일


                String PARTNO = rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME") == null ? "" : rs.getString("PARTNAME");
                String PART_VERSION = rs.getString("PART_VERSION") == null ? "" : rs.getString("PART_VERSION");
                String BLOCKNO =  rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String partQTY =  rs.getString("PART_QTY") == null ? "" : rs.getString("PART_QTY");
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");
                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String UCHECK = rs.getString("UCHECK") == null ? "" : rs.getString("UCHECK");

                String MODIFY_CNT = rs.getString("MODIFY_CNT") == null ? "" : rs.getString("MODIFY_CNT");
                String M_QTY = rs.getString("M_CNT") == null ? "" : rs.getString("M_CNT");
                String C_QTY = rs.getString("C_CNT") == null ? "" : rs.getString("C_CNT");
                String ONE_QTY = rs.getString("ONE_CNT") == null ? "" : rs.getString("ONE_CNT");
                String TWO_QTY = rs.getString("TWO_CNT") == null ? "" : rs.getString("TWO_CNT");
                String THREE_QTY = rs.getString("THREE_CNT") == null ? "" : rs.getString("THREE_CNT");

                ProductDto dto = new ProductDto();
                dto.setProductNo(productNo); //제품번호
                //dto.setProductVersion(productVersion); //제품버전
                //dto.setProductAppdate(PROD_APP_DATE); //제품승인일

                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setVersion(PART_VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setCmt(CMT);
                dto.setGlCode(GLCODE);
                dto.setUcheck(UCHECK);
                dto.setQty(partQTY);
                dto.setBlock_opt(BLOCK_OPT);
                dto.setMCount(M_QTY); // M전체 수량
                dto.setCCount(C_QTY); // C전체 수량
                dto.setOneCount(ONE_QTY);
                dto.setTwoCount(TWO_QTY);
                dto.setThreeCount(THREE_QTY);

                if(MODIFY_CNT != null && !MODIFY_CNT.equals("")) {
                    modCnt = Integer.parseInt(MODIFY_CNT);
                }

                if(M_QTY != null && !"".equals(M_QTY)) {
                    mCnt = Double.parseDouble(M_QTY);
                    //M수량이 1이상이면 해당 품목은 해당 버전에 최초설계이다.
                    if (mCnt > 0 && !map.containsKey("M_FLAG")) {
                        map.put("M_FLAG", "TRUE");
                    }
                }

                if(C_QTY != null && !C_QTY.equals("")) {
                    cCnt = Double.parseDouble(C_QTY);
                    if (cCnt > 0 && !map.containsKey("C_FLAG")) {
                        map.put("C_FLAG", "TRUE");
                    }
                }

                if(ONE_QTY != null && !ONE_QTY.equals("")) {
                    oneCnt = Double.parseDouble(ONE_QTY);
                    if (oneCnt > 0 && !map.containsKey("ONE_FLAG")) {
                        map.put("ONE_FLAG", "TRUE");
                    }
                }

                if(TWO_QTY != null && !TWO_QTY.equals("")) {
                    twoCnt = Double.parseDouble(TWO_QTY);
                    if (twoCnt > 0 && !map.containsKey("TWO_FLAG")) {
                        map.put("TWO_FLAG", "TRUE");
                    }
                }

                if(THREE_QTY != null && !THREE_QTY.equals("")) {
                    threeCnt = Double.parseDouble(THREE_QTY);
                    if (threeCnt > 0 && !map.containsKey("THREE_FLAG")) {
                        map.put("THREE_FLAG", "TRUE");
                    }
                }



                if (!dupCheck.contains(PARTNO)) {
                    dupCheck.add(PARTNO);

                    if("M".equals(BLOCK_OPT) && !map.containsKey("m_ModCount")) {
                        partList.add(dto);
                        if(UCHECK != null && !"".equals(UCHECK) && UCHECK.equals("1")) {
                            m_ModCount++;
                            //System.out.println(productVersion + " > " + "MM--" + PARTNO + " > " + PARTNAME);
                        }
                    }
                    //}

                    //if (cCnt > 0) {
                    if("C".equals(BLOCK_OPT) && !map.containsKey("c_ModCount")) {
                        partList.add(dto);
                        if(UCHECK != null && !"".equals(UCHECK) && dto.getUcheck().equals("1")) {
                            c_ModCount++;
                           //System.out.println(productVersion +  "> CC--" + PARTNO + " > " + PARTNAME);
                        }
                    }
                    //}

                    //if (oneCnt > 0) {
                    if("1".equals(BLOCK_OPT) && !map.containsKey("one_ModCnt")) {
                        partList.add(dto);
                        if(UCHECK != null && !"".equals(UCHECK) && UCHECK.equals("1")) {
                            one_ModCnt++;
                            //System.out.println("1111--" + PARTNO + " > " + PARTNAME);
                        }
                    }
                    // }

                    //if (twoCnt > 0) {
                    if("2".equals(BLOCK_OPT) && !map.containsKey("two_ModCnt")) {
                        partList.add(dto);
                        if(UCHECK != null && !"".equals(UCHECK) && UCHECK.equals("1")) {
                            two_ModCnt++;
                            //System.out.println("2222--" + PARTNO + " > " + PARTNAME);
                        }
                    }
                    //}

                    //if (threeCnt > 0) {
                    if("3".equals(BLOCK_OPT) && !map.containsKey("three_ModCnt")) {
                        partList.add(dto);
                        if(UCHECK != null && !"".equals(UCHECK) && UCHECK.equals("1")) {
                            three_ModCnt++;
                            //System.out.println("3333--" + PARTNO + " > " + PARTNAME);
                        }
                    }
                }

            } //end while


            if(mCnt > 0 && cCnt > 0 && oneCnt > 0 && twoCnt > 0 && threeCnt > 0) {
                //최초설계 대상 제품
                result = true;
                ///map.put("APP_DATE", PROD_CREDATE);
            }

            if(m_ModCount > 0  && !map.containsKey("m_ModCount")){
                map.put("m_ModCount", String.valueOf(m_ModCount));
            }

            if(c_ModCount > 0 && !map.containsKey("c_ModCount")){
                map.put("c_ModCount", String.valueOf(c_ModCount));
            }

            if(one_ModCnt > 0 && !map.containsKey("one_ModCnt")){
                map.put("one_ModCount", String.valueOf(one_ModCnt));
            }

            if(two_ModCnt > 0 && !map.containsKey("two_ModCnt")){
                map.put("two_ModCount", String.valueOf(two_ModCnt));
            }

            if(three_ModCnt > 0 && !map.containsKey("three_ModCnt")){
                map.put("three_ModCount", String.valueOf(three_ModCnt));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }



    //자재 Finder
    //1.빵구난거 찾기위해 가설계(wip) 상태 제품 전체 조회 ( 2025년 부터)
    public static ArrayList<String> findWipBom() {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<String> result = new ArrayList<String>();

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                with ouid as
                		     ( select V.vf$ouid from product$vf V, product$id A
                		      	where V.vf$identity = A.id$ouid and V.vf$ouid = A.id$wip
                		      	--AND SUBSTR(V.MD$MDATE, 0,4) >= '2025'
                		     )
                			 SELECT	B.md$Number AS PRODUCTNO,
                			        B.vf$ouid AS OID,
                			        B.VF$VERSION AS PRODVERSION
                			 FROM product$vf B
                			 WHERE B.VF$OUID in (select * from ouid)
                			 AND B.VF$VERSION = 'wip'
                             AND SUBSTR(B.MD$MDATE, 0,8) = '20250102'
                """;

            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, year);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String PRODUCTNO = rs.getString("PRODUCTNO");
                String oid = rs.getString("OID");

                result.add(oid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }


    //2.제품 하위에 해당 자재가 있는지 검사
    public static void findPartOfProduct(String productOID, String partNo, String con01, ArrayList<ProductDto> dataList) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    SELECT
                          PE.SEQ
                         , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO
                         , (SELECT PRODUCT.VF$VERSION FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PARENTNO_VER
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$CDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_CREDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.MD$MDATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_MODDATE
                         , (SELECT TO_CHAR(TO_DATE(PRODUCT.APP_DATE, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') AS PROD_MODDATE FROM PRODUCT$VF PRODUCT WHERE PRODUCT.VF$OUID = PE.PRODUCTOUID) AS PROD_APP_DATE
                         , NP.VF$VERSION AS PART_VERSION
                         , NP.MD$NUMBER AS PARTNO
                         , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO
                         , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT
                         , VP.UCHECK AS UCHECK  -- 수정여부
                         , cod(NP.NATION) NATION
                         , NP.MD$DESC AS PARTNAME
                         , PE.QTY AS PART_QTY
                         , VP.WORK_QTY
                         , PE.CMT AS CMT
                         , NVL(NP.G_L_CODE, '') AS GLCODE
                         , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) AS USERNAME
                        FROM
                         PARTOFEBOM PE
                        INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                        LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                        WHERE
                         -- PE.PRODUCTOUID = 제품의OID
                        --PE.PRODUCTOUID = ?
                        --AND NP.MD$NUMBER like concat('%',?,'%')
                        --ORDER BY TO_NUMBER(PE.SEQ)
                """;

            //System.out.println("sql = " + sql);

            if (productOID != null) {
                sql += " PE.PRODUCTOUID = '" + productOID + "' ";
            }


            if ("LIKE".equals(con01)) {
                sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
            } else {
                sql += " AND NP.MD$NUMBER = '" + partNo + "' ";
            }

            sql += " ORDER BY TO_NUMBER(PE.SEQ)";

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, productOID);
            //pstmt.setString(2, partNo);

            rs = pstmt.executeQuery();


            HashMap<String, String> flagMap = new HashMap<>();


            while(rs.next()) {
                String productNo = rs.getString("PARENTNO"); //제품번호
                String productVersion = rs.getString("PARENTNO_VER") == null ? "" : rs.getString("PARENTNO_VER"); //제품버전
                String PROD_CREDATE = rs.getString("PROD_CREDATE") == null ? "" : rs.getString("PROD_CREDATE"); //제품 등록일
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                String PROD_APP_DATE = rs.getString("PARENTNO_VER") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일


                String PARTNO = rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME") == null ? "" : rs.getString("PARTNAME");
                String PART_VERSION = rs.getString("PART_VERSION") == null ? "" : rs.getString("PART_VERSION");
                String BLOCKNO =  rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String partQTY =  rs.getString("PART_QTY") == null ? "" : rs.getString("PART_QTY");
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");
                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String UCHECK = rs.getString("UCHECK") == null ? "" : rs.getString("UCHECK");


                System.out.println(productNo +">" + productVersion + " >>> " + PARTNO + " > " + PARTNAME);


                ProductDto dto = new ProductDto();
                dto.setProductNo(productNo); //제품번호
                dto.setProductVersion(productVersion); //제품버전
                dto.setProductAppdate(PROD_APP_DATE); //제품승인일

                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setVersion(PART_VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setCmt(CMT);
                dto.setGlCode(GLCODE);
                dto.setUcheck(UCHECK);
                dto.setQty(partQTY);
                dto.setBlock_opt(BLOCK_OPT);

                dataList.add(dto);
            } //end while


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
    }


    //자재번호로
    //해당 자재번호를 사용하고 있는 제품 검색
    //2.제품 하위에 해당 자재가 있는지 검사
    public static ArrayList<ProductDto> findPartOfProduct_v2(String year, String partNo) {

        Connection con 			= null;
        PreparedStatement pstmt = null;
        ResultSet rs 			= null;

        ArrayList<ProductDto> dataList = new ArrayList<ProductDto>();

        try {
            con = PLMDBConnection.getConnection();
            String sql = """
                    with ouid as
                         ( select A.vf$ouid AS VFOID from product$vf A, product$id B
                          	where A.vf$identity = B.id$ouid and A.vf$ouid = B.id$wip
            """;

            if (year != null && !"".equals(year)) {
                sql += " AND SUBSTR(A.MD$CDATE, 0, 4) = '" + year + "' ";
            } else {
                sql += " AND SUBSTR(A.MD$CDATE, 0, 4) = '2025' ";
            }

            sql += """        
                         )
                        SELECT
                        			  PE.SEQ
                        			 , (SELECT MD$NUMBER FROM PRODUCT$VF WHERE VF$OUID = PE.PRODUCTOUID) AS PARENTNO
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
                        			 , NP.VF$VERSION AS PART_VERSION
                                     , PE.CMT AS CMT
                        			 , NVL(NP.G_L_CODE, '') AS GLCODE
                        			 , NVL(NP.SPEC, '') AS SPEC
                        			 , NVL(NP.PART_SIZE, '') AS PART_SIZE
                        			 , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCKNO
                        			 , (SELECT COD(BLOCK_OPT) FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) AS BLOCK_OPT
                        			 , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID =  DECODE(NP.UPPERBLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.UPPERBLOCKNO, 12))))) AS UPPERBLOCKNO
                        			 , NVL(COD(NP.UOM), '') AS UOM
                        			 , PE.QTY AS PART_QTY
                        			 , VP.WORK_QTY
                        			 , VP.WORK_CMT
                        			 , PE.COLOR
                        			 , VP.WORK_COLOR
                        			 , NVL(COD(NP.ORIGIN_DIV), '') DIV
                        			 , NVL(PE.MBOM, '') MBOM
                        			 , NVL(COD(NP.PART_MBOM), '') PART_MBOM
                        			 , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = NP.MD$USER) USERNAME
                        			 , NP.MD$USER USERID
                        			 , COD(NP.PARTMPCHECK) AS PARTMPCHECK
                        			 , (SELECT MD$DESC FROM FUSER$SF WHERE MD$NUMBER = PE.CUSER) AS CUSERNAME
                        			 , PE.CUSER AS CUSERID
                        			 , 1 LEV
                        			 , 'F' ISLEAF
                        			 , VP.UCHECK AS UCHECK  -- 수정여부
                        			 , VP.MCHECK
                        			 , NVL(COD(NP.PART_DIVISION), '') AS PART_DIVISION
                        			 , PE.CDATE
                        			 , VP.MDATE
                        			-- , DATEFORMAT(VP.MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS 등록일
                        			 , VP.user5
                        			 , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) HASCHILD -- 하위BOM 존재여부
                        			FROM
                        			 PARTOFEBOM PE
                        			INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID
                        			LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID
                        			WHERE
                                    PE.PRODUCTOUID IN (SELECT VFOID FROM ouid)
                        			--AND NP.MD$NUMBER LIKE '2300005%'
                        			--AND NP.MD$NUMBER LIKE ''
                        			--ORDER BY TO_NUMBER(PE.SEQ)
                """;
            

            if(partNo != null && !"".equals(partNo)){

                if (partNo.contains("*")) {

                    partNo = partNo.replace("*", "%");

                    //sql += " AND NP.MD$NUMBER LIKE '%" + partNo + "%' ";
                    sql += " AND NP.MD$NUMBER LIKE '" + partNo + "' ";
                } else {
                    sql += " AND NP.MD$NUMBER = '" + partNo + "' ";
                }



            }

            System.out.println("sql = " + sql);

            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, productOID);
            //pstmt.setString(2, partNo);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String productNo = rs.getString("PARENTNO"); //제품번호
                String productVersion = rs.getString("PARENT_VER") == null ? "" : rs.getString("PARENT_VER"); //제품버전
                String PROD_STATUS = rs.getString("PROD_STATUS") == null ? "" : rs.getString("PROD_STATUS");
                String PROD_CREDATE = rs.getString("PROD_CREDATE") == null ? "" : rs.getString("PROD_CREDATE"); //제품 등록일
                String PROD_MODDATE = rs.getString("PROD_MODDATE") == null ? "" : rs.getString("PROD_MODDATE"); //제품 수정일
                String PROD_APP_DATE = rs.getString("PROD_APP_DATE") == null ? "" : rs.getString("PROD_APP_DATE"); //제품 승인일
                String GISONG = rs.getString("GISONG") == null ? "" : rs.getString("GISONG");


                String PARTNO = rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO");
                String PARTNAME = rs.getString("PARTNAME") == null ? "" : rs.getString("PARTNAME");
                String PART_VERSION = rs.getString("PART_VERSION") == null ? "" : rs.getString("PART_VERSION");
                String BLOCKNO =  rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String partQTY =  rs.getString("PART_QTY") == null ? "" : rs.getString("PART_QTY");
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");
                String CMT = rs.getString("CMT") == null ? "" : rs.getString("CMT");
                String GLCODE = rs.getString("GLCODE") == null ? "" : rs.getString("GLCODE");
                String UCHECK = rs.getString("UCHECK") == null ? "" : rs.getString("UCHECK");


                System.out.println(GISONG + " ===== " + productNo +">" + productVersion + " >>> " + PARTNO + " > " + PARTNAME);


                ProductDto dto = new ProductDto();
                dto.setProductNo(productNo); //제품번호
                dto.setProductVersion(productVersion); //제품버전
                dto.setProductStatus(PROD_STATUS);
                dto.setProductCreDate(PROD_CREDATE);
                dto.setProductModDate(PROD_MODDATE);
                dto.setProductAppdate(PROD_APP_DATE); //제품승인일
                dto.setGisong(GISONG);

                dto.setPartNo(PARTNO);
                dto.setPartName(PARTNAME);
                dto.setVersion(PART_VERSION);
                dto.setBlockNo(BLOCKNO);
                dto.setCmt(CMT);
                dto.setGlCode(GLCODE);
                dto.setUcheck(UCHECK);
                dto.setQty(partQTY);
                dto.setBlock_opt(BLOCK_OPT);

                dataList.add(dto);
            } //end while

            System.out.println("dataList.size() = " + dataList.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        
        return dataList;
    }
}



