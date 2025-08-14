package com.kyhslam.util;

import com.kyhslam.dto.BlockHistoryDTO;
import com.kyhslam.repository.BlockHistoryRepository;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class PLMBlockUtil {

    /**
     * PLM BlockNo 기준정보 히스토리 저장
     */
    public static ArrayList<BlockHistoryDTO> blockHistory_init() {

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;

        ArrayList<BlockHistoryDTO> list = new ArrayList<BlockHistoryDTO>();

        try {

            con = PLMDBConnection.getConnection();

            String sql = """
                SELECT
                       A.MD$NUMBER AS BLOCKNO,
                       A.MD$CDATE, --등록일
                       A.MD$MDATE AS MODDATE, --수정일
                       DATEFORMAT(A.MD$MDATE, 'YYYYMMDDHH24MISS', 'YYYY-MM-DD HH24:MI:SS') AS MOD_DATE, --수정일2
                       SUBSTR(A.MD$MDATE, 0, 8) AS MOD_DAY,
                       A.MD$DESC AS BLOCKNAME,
                       A.MD$STATUS,
                       A.MD$USER AS CUSER,--등록자
                       A.MODIFIYUSER AS MODUSER,--수정자
                       CODN(A.PART_TYPE) AS PART_TYPE, --자재유형
                       A.BLOCKUSER,
                       CODN(A.PARTNAME_MANAGER) AS PARTNAME_MANAGER, --부품명 관리
                       CODN(A.LEVEL1) AS LEVEL1, --신1레벨여부
                       CODN(A.FLOOR_PART) AS FLOOR_PART, -- 층별부품
                       A.COLOR_PID,
                       CODN(A.BLOCK_STATUS) AS BLOCK_STATUS, -- 활성상태
                       CODN(A.UOM) AS UOM,
                       CODN(A.MATERIAL_CHECK) AS MATERIAL_CHECK, -- 재질관리
                       CODN(A.GC_PRODUCT) AS GC_PRODUCT, --제품군
                       A.LOSSRATE AS LOSSRATE, --로스율
                       CODN(A.DRAWINGONLY) AS DRAWINGONLY,
                       CODN(A.BLOCK_OPT) AS BLOCK_OPT, --품목구분
                       A.QUALITYPERSON,
                    A.PICK1, A.PICK2, A.PICK3, A.PICK4, A.PICK5, A.PICK6, A.PICK7, A.PICK8, A.PICK9, A.PICK10, A.PICK11, A.PICK12, A.PICK13,
                    A.PICK14, A.PICK15, A.PICK16, A.PICK17, A.PICK18, A.PICK19, A.PICK20, A.PICK21, A.PICK22, A.PICK23, A.PICK24, A.PICK25, A.PICK26, A.PICK27, A.PICK28, A.PICK29, A.PICK30, A.PICK31, A.PICK32, A.PICK33,
                    A.PICKNAME1, A.PICKNAME2, A.PICKNAME3, A.PICKNAME4, A.PICKNAME5, A.PICKNAME6, A.PICKNAME7, A.PICKNAME8, A.PICKNAME9, A.PICKNAME10,
                    A.PICKNAME11, A.PICKNAME12, A.PICKNAME13, A.PICKNAME14, A.PICKNAME15, A.PICKNAME16, A.PICKNAME17, A.PICKNAME18, A.PICKNAME19, A.PICKNAME20,
                    A.PICKNAME21, A.PICKNAME22, A.PICKNAME23, A.PICKNAME24, A.PICKNAME25, A.PICKNAME26, A.PICKNAME27, A.PICKNAME28, A.PICKNAME29, A.PICKNAME30, A.PICKNAME31, A.PICKNAME32, A.PICKNAME33,
                    A.QTY1, A.QTY2, A.QTY3, A.QTY4, A.QTY5, A.QTY6, A.QTY7, A.QTY8, A.QTY9, A.QTY10,
                    A.QTY11, A.QTY12, A.QTY13, A.QTY14, A.QTY15, A.QTY16, A.QTY17, A.QTY18, A.QTY19, A.QTY20,
                    A.QTY21, A.QTY22, A.QTY23, A.QTY24, A.QTY25, A.QTY26, A.QTY27, A.QTY28, A.QTY29, A.QTY30, A.QTY31, A.QTY32, A.QTY33,
                    A.CMT1, A.CMT2, A.CMT3, A.CMT4, A.CMT5, A.CMT6, A.CMT7, A.CMT8, A.CMT9, A.CMT10,
                    A.CMT11, A.CMT12, A.CMT13, A.CMT14, A.CMT15, A.CMT16, A.CMT17, A.CMT18, A.CMT19, A.CMT20,
                    A.CMT21, A.CMT22, A.CMT23, A.CMT24, A.CMT25, A.CMT26, A.CMT27, A.CMT28, A.CMT29, A.CMT30, A.CMT31, A.CMT32, A.CMT33,
                    A.COLOR1, A.COLOR2, A.COLOR3, A.COLOR4, A.COLOR5, A.COLOR6, A.COLOR7, A.COLOR8, A.COLOR9, A.COLOR10,
                    A.COLOR11, A.COLOR12, A.COLOR13, A.COLOR14, A.COLOR15, A.COLOR16, A.COLOR17, A.COLOR18, A.COLOR19, A.COLOR20,
                    A.COLOR21, A.COLOR22, A.COLOR23, A.COLOR24, A.COLOR25, A.COLOR26, A.COLOR27, A.COLOR28, A.COLOR29, A.COLOR30, COLOR31, COLOR32, COLOR33
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
                String UOM = rs.getString("UOM") == null ? "" : rs.getString("UOM"); //단위
                String BLOCK_STATUS = rs.getString("BLOCK_STATUS") == null ? "" : rs.getString("BLOCK_STATUS");

                String PART_TYPE = rs.getString("PART_TYPE") == null ? "" : rs.getString("PART_TYPE");  //자재유형 - 외주(ROH)
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");  //품목구분
                String DRAWINGONLY = rs.getString("DRAWINGONLY") == null ? "" : rs.getString("DRAWINGONLY");  //자재번호 사용 불가
                String PARTNAME_MANAGER = rs.getString("PARTNAME_MANAGER") == null ? "" : rs.getString("PARTNAME_MANAGER");
                String MATERIAL_CHECK = rs.getString("MATERIAL_CHECK") == null ? "" : rs.getString("MATERIAL_CHECK");
                String LEVEL1 = rs.getString("LEVEL1") == null ? "" : rs.getString("LEVEL1");
                String FLOOR_PART = rs.getString("FLOOR_PART") == null ? "" : rs.getString("FLOOR_PART");



                //PICK
                String pickList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "PICK" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    pickList += val.trim() + "|";
                }

                //PICKNAME
                //ArrayList<String> pickNameList = new ArrayList<>();
                String pickNameList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "PICKNAME" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    pickNameList += val.trim() + "|";
                }

                //QTY
                String qtyList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "QTY" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    qtyList += val.trim() + "|";
                }

                //CMT
                String cmtList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "CMT" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //cmtList.add(val.trim());
                    cmtList += val.trim() + "|";
                }

                //COLOR
                //ArrayList<String> colorList = new ArrayList<>();
                String colorList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "COLOR" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //colorList.add(val.trim());
                    colorList += val.trim() + "|";
                }

                if ("B259B83".equals(BLOCKNO)) {
                    System.out.println((cnt++) + " :: " + BLOCKNO + " > " + BLOCKNAME + " > " + MOD_DAY + " > " + PART_TYPE);
                }

                //BLOCKNO 정보 넣기
                BlockHistoryDTO dto = new BlockHistoryDTO();
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNAME);
                dto.setModUser(MODUSER);
                dto.setPartType(PART_TYPE);
                dto.setGc_product(GC_PRODUCT);
                dto.setUom(UOM);
                dto.setBlock_opt(BLOCK_OPT);
                dto.setBlock_status(BLOCK_STATUS);
                dto.setDrawingOnly(DRAWINGONLY);

                dto.setPartManagement(PARTNAME_MANAGER);
                dto.setMaterial_check(MATERIAL_CHECK);
                dto.setLevel1(LEVEL1);
                dto.setFloor_part(FLOOR_PART);

                dto.setModDate(MOD_DAY);
                dto.setPick(pickList);
                dto.setPickName(pickNameList);
                dto.setQty(qtyList);
                dto.setCmt(cmtList);
                dto.setColor(colorList);


                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return list;
    }


    /**
     * PLM에서 금일 수정된 날짜의 해당 BLOCKNO들 정보 조회
     * @return
     */
    public static ArrayList<BlockHistoryDTO> findByTodayBlockNo() {

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;

        //LocalDate now = LocalDate.now(); //YYYYMMDD //현재날짜 구하기
        LocalDate now = LocalDate.now().minusDays(1); // 하루 전 날짜
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayValue = now.format(formatter);

        ArrayList<BlockHistoryDTO> list = new ArrayList<>();
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
                       CODN(A.PART_TYPE) AS PART_TYPE, --자재유형
                       A.BLOCKUSER,
                       CODN(A.PARTNAME_MANAGER) AS PARTNAME_MANAGER, --부품명 관리
                       CODN(A.LEVEL1) AS LEVEL1, --신1레벨여부
                       CODN(A.FLOOR_PART) AS FLOOR_PART, -- 층별부품
                       A.COLOR_PID,
                       CODN(A.BLOCK_STATUS) AS BLOCK_STATUS, -- 활성상태
                       CODN(A.UOM) AS UOM,
                       CODN(A.MATERIAL_CHECK) AS MATERIAL_CHECK, -- 재질관리
                       CODN(A.GC_PRODUCT) AS GC_PRODUCT, --제품군
                       A.LOSSRATE AS LOSSRATE, --로스율
                       CODN(A.DRAWINGONLY) AS DRAWINGONLY,
                       CODN(A.BLOCK_OPT) AS BLOCK_OPT, --품목구분
                       A.QUALITYPERSON,
                    A.PICK1, A.PICK2, A.PICK3, A.PICK4, A.PICK5, A.PICK6, A.PICK7, A.PICK8, A.PICK9, A.PICK10, A.PICK11, A.PICK12, A.PICK13,
                    A.PICK14, A.PICK15, A.PICK16, A.PICK17, A.PICK18, A.PICK19, A.PICK20, A.PICK21, A.PICK22, A.PICK23, A.PICK24, A.PICK25, A.PICK26, A.PICK27, A.PICK28, A.PICK29, A.PICK30, A.PICK31, A.PICK32, A.PICK33,
                    A.PICKNAME1, A.PICKNAME2, A.PICKNAME3, A.PICKNAME4, A.PICKNAME5, A.PICKNAME6, A.PICKNAME7, A.PICKNAME8, A.PICKNAME9, A.PICKNAME10,
                    A.PICKNAME11, A.PICKNAME12, A.PICKNAME13, A.PICKNAME14, A.PICKNAME15, A.PICKNAME16, A.PICKNAME17, A.PICKNAME18, A.PICKNAME19, A.PICKNAME20,
                    A.PICKNAME21, A.PICKNAME22, A.PICKNAME23, A.PICKNAME24, A.PICKNAME25, A.PICKNAME26, A.PICKNAME27, A.PICKNAME28, A.PICKNAME29, A.PICKNAME30, A.PICKNAME31, A.PICKNAME32, A.PICKNAME33,
                    A.QTY1, A.QTY2, A.QTY3, A.QTY4, A.QTY5, A.QTY6, A.QTY7, A.QTY8, A.QTY9, A.QTY10,
                    A.QTY11, A.QTY12, A.QTY13, A.QTY14, A.QTY15, A.QTY16, A.QTY17, A.QTY18, A.QTY19, A.QTY20,
                    A.QTY21, A.QTY22, A.QTY23, A.QTY24, A.QTY25, A.QTY26, A.QTY27, A.QTY28, A.QTY29, A.QTY30, A.QTY31, A.QTY32, A.QTY33,
                    A.CMT1, A.CMT2, A.CMT3, A.CMT4, A.CMT5, A.CMT6, A.CMT7, A.CMT8, A.CMT9, A.CMT10,
                    A.CMT11, A.CMT12, A.CMT13, A.CMT14, A.CMT15, A.CMT16, A.CMT17, A.CMT18, A.CMT19, A.CMT20,
                    A.CMT21, A.CMT22, A.CMT23, A.CMT24, A.CMT25, A.CMT26, A.CMT27, A.CMT28, A.CMT29, A.CMT30, A.CMT31, A.CMT32, A.CMT33,
                    A.COLOR1, A.COLOR2, A.COLOR3, A.COLOR4, A.COLOR5, A.COLOR6, A.COLOR7, A.COLOR8, A.COLOR9, A.COLOR10,
                    A.COLOR11, A.COLOR12, A.COLOR13, A.COLOR14, A.COLOR15, A.COLOR16, A.COLOR17, A.COLOR18, A.COLOR19, A.COLOR20,
                    A.COLOR21, A.COLOR22, A.COLOR23, A.COLOR24, A.COLOR25, A.COLOR26, A.COLOR27, A.COLOR28, A.COLOR29, A.COLOR30, A.COLOR31, A.COLOR32, A.COLOR33
                FROM BLOCKNO$SF A
                WHERE SUBSTR(A.MD$MDATE, 0, 8) = ?
                """;


            pstmt = con.prepareStatement(sql.toString());
            pstmt.setString(1, todayValue);
            rs = pstmt.executeQuery();

            int cnt = 1;

            while(rs.next()) {
                String BLOCKNO = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String MOD_DAY = rs.getString("MOD_DAY") == null ? "" : rs.getString("MOD_DAY");
                String MODUSER = rs.getString("MODUSER") == null ? "" : rs.getString("MODUSER");  //BLOCK 명
                String BLOCKNAME = rs.getString("BLOCKNAME") == null ? "" : rs.getString("BLOCKNAME");  //BLOCK 명
                String GC_PRODUCT = rs.getString("GC_PRODUCT") == null ? "" : rs.getString("GC_PRODUCT"); //제품군
                String UOM = rs.getString("UOM") == null ? "" : rs.getString("UOM"); //단위
                String BLOCK_STATUS = rs.getString("BLOCK_STATUS") == null ? "" : rs.getString("BLOCK_STATUS"); //활성상태

                String PART_TYPE = rs.getString("PART_TYPE") == null ? "" : rs.getString("PART_TYPE");  //자재유형 - 외주(ROH)
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");  //품목구분
                String DRAWINGONLY = rs.getString("DRAWINGONLY") == null ? "" : rs.getString("DRAWINGONLY");  //자재번호 사용 불가
                String PARTNAME_MANAGER = rs.getString("PARTNAME_MANAGER") == null ? "" : rs.getString("PARTNAME_MANAGER");
                String MATERIAL_CHECK = rs.getString("MATERIAL_CHECK") == null ? "" : rs.getString("MATERIAL_CHECK");
                String LEVEL1 = rs.getString("LEVEL1") == null ? "" : rs.getString("LEVEL1");
                String FLOOR_PART = rs.getString("FLOOR_PART") == null ? "" : rs.getString("FLOOR_PART");

                //PICK
                String pickList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "PICK" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    pickList += val.trim() + "|";
                }

                //PICKNAME
                String pickNameList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "PICKNAME" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    pickNameList += val.trim() + "|";
                }

                //QTY
                String qtyList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "QTY" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    qtyList += val.trim() + "|";
                }

                //CMT
                String cmtList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "CMT" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //cmtList.add(val.trim());
                    cmtList += val.trim() + "|";
                }

                //COLOR
                String colorList = "";
                for (int i = 1; i < 34; i++) {
                    String colName = "COLOR" + String.valueOf(i);
                    String val = rs.getString(colName) == null ? "X" : rs.getString(colName);
                    //colorList.add(val.trim());
                    colorList += val.trim() + "|";
                }

                //BLOCKNO 정보 넣기
                BlockHistoryDTO dto = new BlockHistoryDTO();
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNAME);
                dto.setModDate(MOD_DAY); //수정일
                dto.setModUser(MODUSER);
                dto.setPartType(PART_TYPE);
                dto.setGc_product(GC_PRODUCT);
                dto.setUom(UOM);
                dto.setBlock_opt(BLOCK_OPT);
                dto.setBlock_status(BLOCK_STATUS);
                dto.setDrawingOnly(DRAWINGONLY);
                dto.setPartManagement(PARTNAME_MANAGER);
                dto.setMaterial_check(MATERIAL_CHECK);
                dto.setLevel1(LEVEL1);
                dto.setFloor_part(FLOOR_PART);

                dto.setPick(pickList);
                dto.setPickName(pickNameList);
                dto.setQty(qtyList);
                dto.setCmt(cmtList);
                dto.setColor(colorList);

                //System.out.println(dto.toString());
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return list;
    }


    /**
     * @apiNote BlockNo의 속성정보 조회
     * @param
     * @return
     */
    public static HashMap<String,String> findBlockInfo() {

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;

        //ArrayList<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> result = new  HashMap<>();

        BlockHistoryDTO dto = new BlockHistoryDTO();
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
                       CODN(A.PART_TYPE) AS PART_TYPE, --자재유형
                       A.BLOCKUSER,
                       CODN(A.PARTNAME_MANAGER) AS PARTNAME_MANAGER, --부품명 관리
                       CODN(A.LEVEL1) AS LEVEL1, --신1레벨여부
                       CODN(A.FLOOR_PART) AS FLOOR_PART, -- 층별부품
                       A.COLOR_PID,
                       CODN(A.BLOCK_STATUS) AS BLOCK_STATUS, -- 활성상태
                       CODN(A.UOM) AS UOM,
                       CODN(A.MATERIAL_CHECK) AS MATERIAL_CHECK, -- 재질관리
                       CODN(A.GC_PRODUCT) AS GC_PRODUCT, --제품군
                       A.LOSSRATE AS LOSSRATE, --로스율
                       CODN(A.DRAWINGONLY) AS DRAWINGONLY,
                       CODN(A.BLOCK_OPT) AS BLOCK_OPT, --품목구분
                       A.QUALITYPERSON
                FROM BLOCKNO$SF A
                WHERE A.BLOCK_STATUS = '2466425004' -- 활성
                -- A.MD$NUMBER = ?
                """;


            pstmt = con.prepareStatement(sql.toString());
            //pstmt.setString(1, blockNo);
            rs = pstmt.executeQuery();

            int cnt = 1;

            while(rs.next()) {
                String BLOCKNO = rs.getString("BLOCKNO") == null ? "" : rs.getString("BLOCKNO");
                String MOD_DAY = rs.getString("MOD_DAY") == null ? "" : rs.getString("MOD_DAY");
                String MODUSER = rs.getString("MODUSER") == null ? "" : rs.getString("MODUSER");  //BLOCK 명
                String BLOCKNAME = rs.getString("BLOCKNAME") == null ? "" : rs.getString("BLOCKNAME");  //BLOCK 명
                String GC_PRODUCT = rs.getString("GC_PRODUCT") == null ? "" : rs.getString("GC_PRODUCT"); //제품군
                String UOM = rs.getString("UOM") == null ? "" : rs.getString("UOM"); //단위
                String BLOCK_STATUS = rs.getString("BLOCK_STATUS") == null ? "" : rs.getString("BLOCK_STATUS"); //활성상태

                String PART_TYPE = rs.getString("PART_TYPE") == null ? "" : rs.getString("PART_TYPE");  //자재유형 - 외주(ROH)
                String BLOCK_OPT = rs.getString("BLOCK_OPT") == null ? "" : rs.getString("BLOCK_OPT");  //품목구분


                //BLOCKNO 정보 넣기
                //BlockHistoryDTO dto = new BlockHistoryDTO();
                dto.setBlockNo(BLOCKNO);
                dto.setBlockName(BLOCKNAME);
                dto.setModDate(MOD_DAY); //수정일
                dto.setModUser(MODUSER);
                dto.setPartType(PART_TYPE);
                dto.setGc_product(GC_PRODUCT);
                dto.setUom(UOM);
                dto.setBlock_opt(BLOCK_OPT);
                dto.setBlock_status(BLOCK_STATUS);


                result.put(BLOCKNO, BLOCKNAME);
                //System.out.println(dto.toString());
                //list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }
        return result;
    }

}
