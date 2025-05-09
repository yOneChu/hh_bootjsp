package com.kyhslam.publicTest;

import com.kyhslam.dto.HogiExportDTO;
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



        HashMap<String,Object> initMap = DashboardCommonUtil.initPartPublicData();


        ArrayList<String> cpMRL_5 = (ArrayList<String>) initMap.get("cpMRL_5");
        ArrayList<String> cpMRL_9 = (ArrayList<String>) initMap.get("cpMRL_9");
        ArrayList<String> cpMRL_14 = (ArrayList<String>) initMap.get("cpMRL_14");
        ArrayList<String> cpMRL_17 = (ArrayList<String>) initMap.get("cpMRL_17");

        ArrayList<String> cpMR_5_5 = (ArrayList<String>) initMap.get("cpMR_5_5");
        ArrayList<String> cpMR_9 = (ArrayList<String>) initMap.get("cpMR_9");
        ArrayList<String> cpMR_14 = (ArrayList<String>) initMap.get("cpMR_14");
        ArrayList<String> cpMR_17_5 = (ArrayList<String>) initMap.get("cpMR_17_5");


        //2단계
        ArrayList<String> cp2_MRL_General = (ArrayList<String>) initMap.get("cp2_MRL_General");
        ArrayList<String> cp2_MRL_Revive = (ArrayList<String>) initMap.get("cp2_MRL_Revive");
        ArrayList<String> cp2_MR_General = (ArrayList<String>) initMap.get("cp2_MR_General");
        ArrayList<String> cp2_MR_Revive = (ArrayList<String>) initMap.get("cp2_MR_Revive");


        ArrayList<String> carboxList = (ArrayList<String>) initMap.get("CARTOP"); //CAT TOP BOX

        PARTNO = "";
        for(int i=0; i < carboxList.size(); i++) {
            PARTNO += carboxList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        //savePartPublication(PARTNO, "cpMRL_5", "20240501", "20251231");


        String start_date_day = "20240501";
        String end_date_day = "20251231";


        try {
            HashMap resultHM = searchListBasedOnCondition.searchListBasedOnCondition(g_l_code, EL_ATYP, EL_ASPD_1, EL_ASPD_2, EL_AMAN_1, EL_AMAN_2, EL_ECSF, EL_ETM, EL_ECJJ_1, EL_ECJJ_2, PARTNO, BLOCKNO, start_date_day, end_date_day, specList, isOnlyElv, isGeneralElv);
            data = (ArrayList) resultHM.get("data");
            System.out.println("data.size == " + data.size());
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
