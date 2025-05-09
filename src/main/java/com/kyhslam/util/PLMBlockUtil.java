package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PLMBlockUtil {



    //초기화

    /**
     * PLM BlockNo 기준정보 히스토리 저장
     */
    public static void blockHistory_init() {

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                SELECT
                       A.MD$NUMBER AS BLOCKNO,
                       A.MD$CDATE, --등록일
                       A.MD$MDATE, --수정일
                       DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS MOD_DATE, --수정일2
                       SUBSTR(A.MD$MDATE, 0, 8) AS MOD_DAY,
                       A.MD$DESC AS BLOCKNAME,
                       A.MD$STATUS,
                       A.MD$USER AS CUSER,--등록자
                       A.MODIFIYUSER AS MODUSER,--수정자
                       CODN(A.ORIGIN_DIV) AS ORIGIN_DIV, --자재유형
                       A.BLOCKUSER,
                       CODN(A.PARTNAME_MANAGER) AS 부품명관리, --부품명 관리
                       CODN(A.LEVEL1) AS LEVEL1, --신1레벨여부
                       CODN(A.FLOOR_PART) AS FLOOR_PART, -- 층별부품
                       A.COLOR_PID,
                       CODN(A.BLOCK_STATUS) AS BLOCK_STATUS, -- 활성상태
                       CODN(A.UOM) AS UOM,
                       CODN(A.MATERIAL_CHECK) AS MATERIAL_CHECK, -- 재질관리
                       CODN(A.GC_PRODUCT) AS GC_PRODUCT, --제품군
                       A.LOSSRATE AS 로스율, --로스율
                       CODN(A.DRAWINGONLY) AS DRAWINGONLY,
                       CODN(A.BLOCK_OPT) AS BLOCK_OPT, --품목구분
                       A.QUALITYPERSON,
                    A.PICK1, A.PICK2, A.PICK3, A.PICK4, A.PICK5, A.PICK6, A.PICK7, A.PICK8, A.PICK9, A.PICK10, A.PICK11, A.PICK12, A.PICK13,
                    A.PICK14, A.PICK15, A.PICK16, A.PICK17, A.PICK18, A.PICK19, A.PICK20, A.PICK21, A.PICK22, A.PICK23, A.PICK24, A.PICK25, A.PICK26, A.PICK27, A.PICK28, A.PICK29, A.PICK30,
                    A.PICKNAME1, A.PICKNAME2, A.PICKNAME3, A.PICKNAME4, A.PICKNAME5, A.PICKNAME6, A.PICKNAME7, A.PICKNAME8, A.PICKNAME9, A.PICKNAME10,
                    A.PICKNAME11, A.PICKNAME12, A.PICKNAME13, A.PICKNAME14, A.PICKNAME15, A.PICKNAME16, A.PICKNAME17, A.PICKNAME18, A.PICKNAME19, A.PICKNAME20,
                    A.PICKNAME21, A.PICKNAME22, A.PICKNAME23, A.PICKNAME24, A.PICKNAME25, A.PICKNAME26, A.PICKNAME27, A.PICKNAME28, A.PICKNAME29, A.PICKNAME30,
                    A.QTY1, A.QTY2, A.QTY3, A.QTY4, A.QTY5, A.QTY6, A.QTY7, A.QTY8, A.QTY9, A.QTY10,
                    A.QTY11, A.QTY12, A.QTY13, A.QTY14, A.QTY15, A.QTY16, A.QTY17, A.QTY18, A.QTY19, A.QTY20,
                    A.QTY21, A.QTY22, A.QTY23, A.QTY24, A.QTY25, A.QTY26, A.QTY27, A.QTY28, A.QTY29, A.QTY30,
                    A.CMT1, A.CMT2, A.CMT3, A.CMT4, A.CMT5, A.CMT6, A.CMT7, A.CMT8, A.CMT9, A.CMT10,
                    A.CMT11, A.CMT12, A.CMT13, A.CMT14, A.CMT15, A.CMT16, A.CMT17, A.CMT18, A.CMT19, A.CMT20,
                    A.CMT21, A.CMT22, A.CMT23, A.CMT24, A.CMT25, A.CMT26, A.CMT27, A.CMT28, A.CMT29, A.CMT30,
                    A.COLOR1, A.COLOR2, A.COLOR3, A.COLOR4, A.COLOR5, A.COLOR6, A.COLOR7, A.COLOR8, A.COLOR9, A.COLOR10,
                    A.COLOR11, A.COLOR12, A.COLOR13, A.COLOR14, A.COLOR15, A.COLOR16, A.COLOR17, A.COLOR18, A.COLOR19, A.COLOR20,
                    A.COLOR21, A.COLOR22, A.COLOR23, A.COLOR24, A.COLOR25, A.COLOR26, A.COLOR27, A.COLOR28, A.COLOR29, A.COLOR30
                FROM BLOCKNO$SF A
                """;


            pstmt = con.prepareStatement(sql.toString());
            rs = pstmt.executeQuery();

            int cnt = 1;

            while(rs.next()) {
                String BLOCKNO = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String MOD_DAY = rs.getString("MOD_DAY") == null ? "" : rs.getString("MOD_DAY");
                String MODUSER = rs.getString("MODUSER") == null ? "" : rs.getString("MODUSER");  //BLOCK 명
                String BLOCKNAME = rs.getString("BLOCKNAME") == null ? "" : rs.getString("BLOCKNAME");  //BLOCK 명

                String GC_PRODUCT = rs.getString("GC_PRODUCT") == null ? "" : rs.getString("GC_PRODUCT"); //제품군
                String UOM = rs.getString("UOM") == null ? "" : rs.getString("UOM");


                String ORIGIN_DIV = rs.getString("ORIGIN_DIV") == null ? "" : rs.getString("ORIGIN_DIV");  //자재유형 - 외주(ROH)

                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");  //품목구분
                String DRAWINGONLY = rs.getString("DRAWINGONLY") == null ? "" : rs.getString("DRAWINGONLY");  //자재번호 사용 불가

                //PICK
                ArrayList<String> pickList = new ArrayList<>();
                for (int i = 1; i < 31; i++) {
                    String colName = "PICK" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    pickList.add(val.trim());
                }

                //PICKNAME
                //ArrayList<String> pickNameList = new ArrayList<>();
                String pickNameList = "";
                for (int i = 1; i < 31; i++) {
                    String colName = "PICKNAME" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    pickNameList += val.trim();
                    if (i != 30) {
                        pickNameList += "-";
                    }
                }



                //QTY
                //ArrayList<String> qtyList = new ArrayList<>();
                String qtyList = "";
                for (int i = 1; i < 31; i++) {
                    String colName = "QTY" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //qtyList.add(val.trim());
                    qtyList += val.trim();
                    if (i != 30) {
                        qtyList += "-";
                    }
                }

                //CMT
                //ArrayList<String> cmtList = new ArrayList<>();
                String cmtList = "";
                for (int i = 1; i < 31; i++) {
                    String colName = "CMT" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //cmtList.add(val.trim());
                    cmtList += val.trim();
                    if (i != 30) {
                        cmtList += "-";
                    }
                }

                //COLOR
                //ArrayList<String> colorList = new ArrayList<>();
                String colorList = "";
                for (int i = 1; i < 31; i++) {
                    String colName = "COLOR" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //colorList.add(val.trim());
                    colorList += val.trim();
                    if (i != 30) {
                        colorList += "-";
                    }
                }

                System.out.println((cnt++) + " :: " + BLOCKNO + " > " + MODUSER + " > " + pickNameList + " > " + colorList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }

    }

}
