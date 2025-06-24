package com.kyhslam.publicTest;

import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.service.PartPublicationService;
import com.kyhslam.util.DashboardCommonUtil;
import com.kyhslam.util.searchListBasedOnCondition;

import java.util.ArrayList;
import java.util.HashMap;

public class searchTest01 {

    public static void main(String[] args) {


        String PARTNO = "";
        String partType = "";



        String g_l_code = "";
        String chk_detail = "";
        String chk_general = "";
        String EL_ATYP = "";
        String EL_ASPD_1 = "";
        String EL_ASPD_2 = "";
        String EL_AMAN_1 = "";
        String EL_AMAN_2 = "";
        String EL_ECSF = "";
        String EL_ETM = "";
        String EL_ECJJ_1 = "";
        String EL_ECJJ_2 = "";
        //String PARTNO = "";
        String BLOCKNO = "";
        String specList = "";

        ArrayList data = new ArrayList();

        boolean isOnlyElv = false;
        boolean isGeneralElv = false;

        specList = "";
        specList += "EL_ATYP\n";   // 기종
        specList += "ARKTX\n"; // 사양
        specList += "EL_ASPSC\n";  //생산거점
        specList += "EL_AMDLR\n"; //모듈러
        specList += "EL_ASPD\n"; //속도
        specList += "EL_ACAPA\n"; //용량

        //EL_ASPSC

        HashMap<String,Object> initMap = DashboardCommonUtil.initPartPublicData();



        ArrayList<String> tmBeltList = (ArrayList<String>) initMap.get("TM"); //TM(BELT)

        PARTNO = "";
        for(int i=0; i < tmBeltList.size(); i++) {
            PARTNO += tmBeltList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);

        String start_date_day = "20240501";
        String end_date_day = "20251231";


        try {
            HashMap resultHM = searchListBasedOnCondition.searchListBasedOnCondition(g_l_code, EL_ATYP, EL_ASPD_1, EL_ASPD_2, EL_AMAN_1, EL_AMAN_2, EL_ECSF, EL_ETM, EL_ECJJ_1, EL_ECJJ_2, PARTNO, BLOCKNO, start_date_day, end_date_day, specList, isOnlyElv, isGeneralElv);
            data = (ArrayList) resultHM.get("data");
            System.out.println("data.size == " + data.size());
            //System.out.println("data.size == " + data);


            for(int i=0;i<data.size();i++) {
                ArrayList row = (ArrayList) data.get(i);


                String erpSendDate = (String) row.get(0); //StringUtil.NVL(row.get(0), "*");
                String hogi = (String) row.get(1); //StringUtil.NVL(row.get(1), "*");
                String partNo = (String) row.get(3); // StringUtil.NVL(row.get(3), "*");
                String qty = (String) row.get(4); // StringUtil.NVL(row.get(4), "*"); // 수량
                String dwgNo = (String) row.get(5);  //StringUtil.NVL(row.get(5), "*"); // 도면번호
                String blockNo = (String) row.get(6); // StringUtil.NVL(row.get(6), "*");
                String gongSa = (String) row.get(7); //StringUtil.NVL(row.get(7), "*");
                String gisong = (String)row.get(8); // StringUtil.NVL(row.get(8), "*"); // 기종
                String spec = (String)row.get(9); //StringUtil.NVL(row.get(9), "*"); // 스펙

                String createNation = (String)row.get(10); // StringUtil.NVL(row.get(11), "*"); // 생산거점
                String module = (String)row.get(11); // StringUtil.NVL(row.get(11), "*"); // 모듈러
                String mUser = (String)row.get(12); //StringUtil.NVL(row.get(10), "*"); // 기계담당자
                String eUser = (String)row.get(13); // StringUtil.NVL(row.get(11), "*"); // 전기담당자




                System.out.println(hogi + " > " + row);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }





    }
}
