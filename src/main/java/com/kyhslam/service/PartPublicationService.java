package com.kyhslam.service;


import com.kyhslam.domain.DashPublic;
import com.kyhslam.domain.DashPublicData;
import com.kyhslam.domain.DashPublicExportPrice;
import com.kyhslam.dto.HogiExportDTO;
import com.kyhslam.repository.DashboardRepository;
import com.kyhslam.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PartPublicationService {

    private final DashboardRepository dashboardRepository;
    
    /**
     * 월~금 수행
     * 1.CP만 수행 (3시 20분)
     * 2.CP, LAMP 제외 품목(4시 30분)
     * 3.Lamp만 수행 (5시 30분)
     *
     */


    /**
     * 1. CP만 수행
     * 월~금 3시에 부품공용화 배치 실행 (CP 만)
     */
    @Scheduled(cron = "0 20 3 * * 1-5")
    public void scheduleProcessV1() {
        LocalTime now = LocalTime.now();
        System.out.println("scheduleProcessV1 start now = " + now);

        long startTime = System.currentTimeMillis();

        //deletePublic();
        //deletePublicData();

        ArrayList<String> cpMRL_5 = new ArrayList<String>();
        ArrayList<String> cpMRL_9 = new ArrayList<String>();
        ArrayList<String> cpMRL_14 = new ArrayList<String>();
        ArrayList<String> cpMRL_17 = new ArrayList<String>();

        ArrayList<String> cpMR_5_5 = new ArrayList<String>();
        ArrayList<String> cpMR_9 = new ArrayList<String>();
        ArrayList<String> cpMR_14 = new ArrayList<String>();
        ArrayList<String> cpMR_17_5 = new ArrayList<String>();


        //2단게
        ArrayList<String> cp2_MRL_General = new ArrayList<String>();
        ArrayList<String> cp2_MRL_Revive = new ArrayList<String>();
        ArrayList<String> cp2_MR_General = new ArrayList<String>();
        ArrayList<String> cp2_MR_Revive = new ArrayList<String>();


        HashMap<String,Object> initMap = DashboardCommonUtil.initPartPublicData();


        cpMRL_5 = (ArrayList<String>) initMap.get("cpMRL_5");
        cpMRL_9 = (ArrayList<String>) initMap.get("cpMRL_9");
        cpMRL_14 = (ArrayList<String>) initMap.get("cpMRL_14");
        cpMRL_17 = (ArrayList<String>) initMap.get("cpMRL_17");

        cpMR_5_5 = (ArrayList<String>) initMap.get("cpMR_5_5");
        cpMR_9 = (ArrayList<String>) initMap.get("cpMR_9");
        cpMR_14 = (ArrayList<String>) initMap.get("cpMR_14");
        cpMR_17_5 = (ArrayList<String>) initMap.get("cpMR_17_5");


        //2단계
        cp2_MRL_General = (ArrayList<String>) initMap.get("cp2_MRL_General");
        cp2_MRL_Revive = (ArrayList<String>) initMap.get("cp2_MRL_Revive");
        cp2_MR_General = (ArrayList<String>) initMap.get("cp2_MR_General");
        cp2_MR_Revive = (ArrayList<String>) initMap.get("cp2_MR_Revive");


        String PARTNO = "";

        //cpMRL_5
        PARTNO = "";
        for(int i=0; i < cpMRL_5.size(); i++) {
            PARTNO += cpMRL_5.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMRL_5", "20240501", "20251231");

        //cpMRL_9
        PARTNO = "";
        for(int i=0; i < cpMRL_9.size(); i++) {
            PARTNO += cpMRL_9.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMRL_9", "20240501", "20251231");

        //cpMRL_14
        PARTNO = "";
        for(int i=0; i < cpMRL_14.size(); i++) {
            PARTNO += cpMRL_14.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMRL_14", "20240501", "20251231");

        //cpMRL_17
        PARTNO = "";
        for(int i=0; i < cpMRL_17.size(); i++) {
            PARTNO += cpMRL_17.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMRL_17", "20240501", "20251231");

        //cpMR_5_5
        PARTNO = "";
        for(int i=0; i < cpMR_5_5.size(); i++) {
            PARTNO += cpMR_5_5.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMR_5_5", "20240501", "20251231");

        //cpMR_9
        PARTNO = "";
        for(int i=0; i < cpMR_9.size(); i++) {
            PARTNO += cpMR_9.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMR_9", "20240501", "20251231");

        //cpMR_14
        PARTNO = "";
        for(int i=0; i < cpMR_14.size(); i++) {
            PARTNO += cpMR_14.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMR_14", "20240501", "20251231");

        //cpMR_17_5
        PARTNO = "";
        for(int i=0; i < cpMR_17_5.size(); i++) {
            PARTNO += cpMR_17_5.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cpMR_17_5", "20240501", "20251231");


        //2단계
        PARTNO = "";
        for(int i=0; i < cp2_MRL_General.size(); i++) {
            PARTNO += cp2_MRL_General.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cp2_MRL_General", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < cp2_MRL_Revive.size(); i++) {
            PARTNO += cp2_MRL_Revive.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cp2_MRL_Revive", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < cp2_MR_General.size(); i++) {
            PARTNO += cp2_MR_General.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cp2_MR_General", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < cp2_MR_Revive.size(); i++) {
            PARTNO += cp2_MR_Revive.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "cp2_MR_Revive", "20240501", "20251231");


        long endTime = System.currentTimeMillis();
        long secDiffTime = (endTime - startTime) / 1000;

        System.out.println("------ batch complete!! ----------");
        System.out.println("milli seconds :: " + (endTime - startTime));
        System.out.println("secDiffTime :: " + secDiffTime);

        LocalTime endNow = LocalTime.now();
        System.out.println("scheduleProcessV1 END endNow = " + endNow);
    }


    /**
     * 2. CP, LAMP 제외 품목
     * 매일 4시에 부품공용화 배치 실행 (CP 외)
     */
    @Scheduled(cron = "0 30 4 * * 1-5")
    public void scheduleProcessV2() {
        LocalTime now = LocalTime.now();
        System.out.println("scheduleProcessV2 start now = " + now);

        long startTime = System.currentTimeMillis();

        ArrayList<String> govList = new ArrayList<String>(); // GOV
        ArrayList<String> pitList = new ArrayList<String>();


        ArrayList<String> hipSJ21TOPList = new ArrayList<String>();
        ArrayList<String> hipSJ21MIDList = new ArrayList<String>();
        ArrayList<String> hipSJ21BOTList = new ArrayList<String>();

        ArrayList<String> opb_D521AG = new ArrayList<String>();
        ArrayList<String> opb_S521A = new ArrayList<String>();

        HashMap<String,Object> initMap = DashboardCommonUtil.initPartPublicData();


        ArrayList<String> carboxList = (ArrayList<String>) initMap.get("CARTOP"); //CAT TOP BOX
        ArrayList<String> tmBeltList = (ArrayList<String>) initMap.get("TM"); //TM(BELT)
        ArrayList<String> tmRopeList = (ArrayList<String>) initMap.get("TMRope"); //TM(Rope)


        govList = (ArrayList<String>) initMap.get("GOV");
        pitList = (ArrayList<String>) initMap.get("PIT");

        ArrayList<String> hpbTOPList = DashboardHPB.HPB_J21_TOP();
        ArrayList<String> hpbMIDList = DashboardHPB.HPB_J21_MID(); //(ArrayList<String>) initMap.get("hpbMIDList");
        ArrayList<String> hpbBOTList = DashboardHPB.HPB_J21_BOT(); //(ArrayList<String>) initMap.get("hpbBOTList");

        hipSJ21TOPList = (ArrayList<String>) initMap.get("hipSJ21TOPList");
        hipSJ21MIDList = (ArrayList<String>) initMap.get("hipSJ21MIDList");
        hipSJ21BOTList = (ArrayList<String>) initMap.get("hipSJ21BOTList");


        opb_D521AG = (ArrayList<String>) initMap.get("opb_D521AG");
        opb_S521A = (ArrayList<String>) initMap.get("opb_S521A");


        String PARTNO = "";


        //TM(Belt Type)
        PARTNO = "";
        for(int i=0; i < tmBeltList.size(); i++) {
            PARTNO += tmBeltList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "TM", "20240501", "20251231");

        //TM(Rope)
        PARTNO = "";
        for(int i=0; i < tmRopeList.size(); i++) {
            PARTNO += tmRopeList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "TMRope", "20240501", "20251231");

        //CAT TOP BOX
        PARTNO = "";
        for(int i=0; i < carboxList.size(); i++) {
            PARTNO += carboxList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "CARTOPBOX", "20240501", "20251231");

        //GOV
        PARTNO = "";
        for(int i=0; i < govList.size(); i++) {
            PARTNO += govList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "GOV", "20240501", "20251231");


        //HPB
        PARTNO = "";
        for(int i=0; i < hpbTOPList.size(); i++) {
            PARTNO += hpbTOPList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_TOP", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < hpbMIDList.size(); i++) {
            PARTNO += hpbMIDList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_MID", "20240501", "20251231");


        PARTNO = "";
        for(int i=0; i < hpbBOTList.size(); i++) {
            PARTNO += hpbBOTList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_BOT", "20240501", "20251231");


        //HIP
        PARTNO = "";
        for(int i=0; i < hipSJ21TOPList.size(); i++) {
            PARTNO += hipSJ21TOPList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HIP_TOP", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < hipSJ21MIDList.size(); i++) {
            PARTNO += hipSJ21MIDList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HIP_MID", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < hipSJ21BOTList.size(); i++) {
            PARTNO += hipSJ21BOTList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HIP_BOT", "20240501", "20251231");

        //PIT
        PARTNO = "";
        for(int i=0; i < pitList.size(); i++) {
            PARTNO += pitList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "PIT", "20241007", "20251231");

        //HPI700
        PARTNO = "";
        ArrayList<String> HPI_S700 = DashboardHPI.HPI_S700();
        for(int i=0; i < HPI_S700.size(); i++) {
            PARTNO += HPI_S700.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPI_S700", "20240501", "20251231");

        //HPI_SC
        PARTNO = "";
        ArrayList<String> HPI_SC = DashboardHPI.HPI_SC();
        for(int i=0; i < HPI_SC.size(); i++) {
            PARTNO += HPI_SC.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPI_SC", "20240501", "20251231");


        //OPB(D521AG)
        PARTNO = "";
        for(int i=0; i < opb_D521AG.size(); i++) {
            PARTNO += opb_D521AG.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "OPB_D521AG", "20240501", "20251231");

        //OPB(S521A)
        PARTNO = "";
        for(int i=0; i < opb_S521A.size(); i++) {
            PARTNO += opb_S521A.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "OPB_S521A", "20240501", "20251231");


        //HPB(K21,TOP)
        PARTNO = "";
        ArrayList<String> HPB_K21_TOP = DashboardHPB.getHPB_K21_TOP();
        for(int i=0; i < HPB_K21_TOP.size(); i++) {
            PARTNO += HPB_K21_TOP.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_K21_TOP", "20240501", "20251231");

        //HPB(K21,MID)
        PARTNO = "";
        ArrayList<String> HPB_K21_MID = DashboardHPB.getHPB_K21_MID();
        for(int i=0; i < HPB_K21_MID.size(); i++) {
            PARTNO += HPB_K21_MID.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_K21_MID", "20240501", "20251231");

        //HPB(K21,BOT)
        PARTNO = "";
        ArrayList<String> HPB_K21_BOT = DashboardHPB.getHPB_K21_BOT();
        for(int i=0; i < HPB_K21_BOT.size(); i++) {
            PARTNO += HPB_K21_BOT.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_K21_BOT", "20240501", "20251231");

        //HPB(K21A,TOP)
        PARTNO = "";
        ArrayList<String> HPB_K21A_TOP = DashboardHPB.getHPB_K21A_TOP();
        for(int i=0; i < HPB_K21A_TOP.size(); i++) {
            PARTNO += HPB_K21A_TOP.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_K21A_TOP", "20240501", "20251231");

        //HPB(K21A,MID)
        PARTNO = "";
        ArrayList<String> HPB_K21A_MID = DashboardHPB.getHPB_K21A_MID();
        for(int i=0; i < HPB_K21A_MID.size(); i++) {
            PARTNO += HPB_K21A_MID.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_K21A_MID", "20240501", "20251231");

        //HPB(K21A,BOT)
        PARTNO = "";
        ArrayList<String> HPB_K21A_BOT = DashboardHPB.getHPB_K21A_BOT();
        for(int i=0; i < HPB_K21A_BOT.size(); i++) {
            PARTNO += HPB_K21A_BOT.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HPB_K21A_BOT", "20240501", "20251231");


        //HIP(SK21,TOP)
        PARTNO = "";
        ArrayList<String> HIP_SK21_TOP = DashboardHIP.HIP_SK21_TOP();
        for(int i=0; i < HIP_SK21_TOP.size(); i++) {
            PARTNO += HIP_SK21_TOP.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HIP_SK21_TOP", "20240501", "20251231");

        PARTNO = "";
        ArrayList<String> HIP_SK21_MID = DashboardHIP.HIP_SK21_MID();
        for(int i=0; i < HIP_SK21_MID.size(); i++) {
            PARTNO += HIP_SK21_MID.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HIP_SK21_MID", "20240501", "20251231");

        PARTNO = "";
        ArrayList<String> HIP_SK21_BOT = DashboardHIP.HIP_SK21_BOT();
        for(int i=0; i < HIP_SK21_BOT.size(); i++) {
            PARTNO += HIP_SK21_BOT.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "HIP_SK21_BOT", "20240501", "20251231");



        long endTime = System.currentTimeMillis();
        long secDiffTime = (endTime - startTime) / 1000;

        System.out.println("------ batch complete!! ----------");
        System.out.println("milli seconds :: " + (endTime - startTime));
        System.out.println("secDiffTime :: " + secDiffTime);

        LocalTime endNow = LocalTime.now();
        System.out.println("scheduleProcessV2 END endNow = " + endNow);
    }



    /**
     * 3. Lamp만 수행
     * 매일 5시에 부품공용화 배치 실행 (CP 외)
     */
    @Scheduled(cron = "0 30 5 * * 1-5")
    public void scheduleProcessV3() {
        LocalTime now = LocalTime.now();
        System.out.println("scheduleProcessV3 start now = " + now);

        long startTime = System.currentTimeMillis();


        ArrayList<String> lampHOISTList = new ArrayList<String>();
        ArrayList<String> lampOVERList = new ArrayList<String>();
        ArrayList<String> lampPITList = new ArrayList<String>();
        ArrayList<String> lampCARTOPList = new ArrayList<String>();


        HashMap<String,Object> initMap = DashboardCommonUtil.initPartPublicData();


        lampHOISTList = (ArrayList<String>) initMap.get("lampHOISTList");
        lampOVERList = (ArrayList<String>) initMap.get("lampOVERList");
        lampPITList = (ArrayList<String>) initMap.get("lampPITList");
        lampCARTOPList = (ArrayList<String>) initMap.get("lampCARTOPList");


        String PARTNO = "";

        //LAMP
        PARTNO = "";
        for(int i=0; i < lampOVERList.size(); i++) {
            PARTNO += lampOVERList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "LAMP_OVER", "20240501", "20251231");

        PARTNO = "";
        for(int i=0; i < lampPITList.size(); i++) {
            PARTNO += lampPITList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "LAMP_PIT", "20240501", "20251231");


        PARTNO = "";
        for(int i=0; i < lampCARTOPList.size(); i++) {
            PARTNO += lampCARTOPList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "LAMP_CARTOP", "20240501", "20251231");


        PARTNO = "";
        for(int i=0; i < lampHOISTList.size(); i++) {
            PARTNO += lampHOISTList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        savePartPublication(PARTNO, "LAMP_HOIST", "20240501", "20251231");


        long endTime = System.currentTimeMillis();
        long secDiffTime = (endTime - startTime) / 1000;

        System.out.println("------ batch complete!! ----------");
        System.out.println("milli seconds :: " + (endTime - startTime));
        System.out.println("secDiffTime :: " + secDiffTime);

        LocalTime endNow = LocalTime.now();
        System.out.println("scheduleProcessV3 END endNow = " + endNow);
    }






    @Transactional
    public void savePartPublication(String PARTNO, String partType, String start_date_day, String end_date_day) {

        System.out.println("Start PartType = " + partType);

        //현재날짜 구하기
        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

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

        specList = "";
        specList += "EL_ATYP\n";   // 기종
        specList += "ARKTX\n"; // 사양

        try {

            DashPublic partData = new DashPublic();
            partData.setPartName(partType);

            ArrayList data = new ArrayList();
            HashMap<String, HogiExportDTO> tempExportInfoMap = new HashMap<>();

            boolean isOnlyElv = false;
            boolean isGeneralElv = false;

            HashMap resultHM = searchListBasedOnCondition.searchListBasedOnCondition(g_l_code, EL_ATYP, EL_ASPD_1, EL_ASPD_2, EL_AMAN_1, EL_AMAN_2, EL_ECSF, EL_ETM, EL_ECJJ_1, EL_ECJJ_2, PARTNO, BLOCKNO, start_date_day, end_date_day, specList, isOnlyElv, isGeneralElv);
            data = (ArrayList) resultHM.get("data");
            System.out.println("data.size == " + data.size());

            //출하예정일을 미리 큰 조건으로 셋팅해 놓는다.
            //tempExportInfoMap = PartCommonUtil.findExportDateV2(data);//700개만 미리 셋팅
            tempExportInfoMap = PartCommonUtil.findExportDateV3(data); //전체로해서


            ArrayList r202405 = new ArrayList();
            ArrayList r202406 = new ArrayList();
            ArrayList r202407 = new ArrayList();
            ArrayList r202408 = new ArrayList();
            ArrayList r202409 = new ArrayList();
            ArrayList r202410 = new ArrayList();
            ArrayList r202411 = new ArrayList();
            ArrayList r202412 = new ArrayList();

            ArrayList r202501 = new ArrayList();
            ArrayList r202502 = new ArrayList();
            ArrayList r202503 = new ArrayList();
            ArrayList r202504 = new ArrayList();
            ArrayList r202505 = new ArrayList();
            ArrayList r202506 = new ArrayList();
            ArrayList r202507 = new ArrayList();
            ArrayList r202508 = new ArrayList();
            ArrayList r202509 = new ArrayList();
            ArrayList r202510 = new ArrayList();
            ArrayList r202511 = new ArrayList();
            ArrayList r202512 = new ArrayList();



            //출하일 -> 수량 집계
            int exportCntTotal = 0;
            int exportCnt202405 = 0;
            int exportCnt202406 = 0;
            int exportCnt202407 = 0;
            int exportCnt202408 = 0;
            int exportCnt202409 = 0;
            int exportCnt202410 = 0;
            int exportCnt202411 = 0;
            int exportCnt202412 = 0;
            int exportCnt202501 = 0;
            int exportCnt202502 = 0;
            int exportCnt202503 = 0;
            int exportCnt202504 = 0;
            int exportCnt202505 = 0;
            int exportCnt202506 = 0;
            int exportCnt202507 = 0;
            int exportCnt202508 = 0;
            int exportCnt202509 = 0;
            int exportCnt202510 = 0;
            int exportCnt202511 = 0;
            int exportCnt202512 = 0;
            int exportEtc = 0;

            //출하일 -> 금액 집계
            int exportPrice202405 = 0;
            int exportPrice202406 = 0;
            int exportPrice202407 = 0;
            int exportPrice202408 = 0;
            int exportPrice202409 = 0;
            int exportPrice202410 = 0;
            int exportPrice202411 = 0;
            int exportPrice202412 = 0;
            int exportPrice202501 = 0;
            int exportPrice202502 = 0;
            int exportPrice202503 = 0;
            int exportPrice202504 = 0;
            int exportPrice202505 = 0;
            int exportPrice202506 = 0;
            int exportPrice202507 = 0;
            int exportPrice202508 = 0;
            int exportPrice202509 = 0;
            int exportPrice202510 = 0;
            int exportPrice202511 = 0;
            int exportPrice202512 = 0;
            int exportPriceEtc = 0;

            //수량으로 집계
            int totalQty = 0;
            int total202405 = 0;
            int total202406 = 0;
            int total202407 = 0;
            int total202408 = 0;
            int total202409 = 0;
            int total202410 = 0;
            int total202411 = 0;
            int total202412 = 0;

            int total202501 = 0;
            int total202502 = 0;
            int total202503 = 0;
            int total202504 = 0;
            int total202505 = 0;
            int total202506 = 0;
            int total202507 = 0;
            int total202508 = 0;
            int total202509 = 0;
            int total202510 = 0;
            int total202511 = 0;
            int total202512 = 0;

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
                String mUser = (String)row.get(10); //StringUtil.NVL(row.get(10), "*"); // 기계담당자
                String eUser = (String)row.get(11); // StringUtil.NVL(row.get(11), "*"); // 전기담당자

                if(hogi.startsWith("09999") || hogi.startsWith("N0999")
                        || hogi.startsWith("E0999") || hogi.startsWith("V")  || hogi.startsWith("Y") || hogi.startsWith("Q")) {
                    //Y로 시작하는거 -> 출하이후 영업사양이 바뀌어서 자재가 변경되는 호기(설계귀책X)
                    continue;
                }


                String exportDate = "";
                String exportDateMod = "";

                //출하예정일 미리 셋팅
                //미리 출하예정일 데이터 가져온거 있으면 거기서 데이터 꺼내서 가공한다.
                if(tempExportInfoMap.containsKey(hogi)) {

                    HogiExportDTO d = tempExportInfoMap.get(hogi.trim());

                    String fStartBlockNo = blockNo.substring(0,1);
                    exportDate = "";

                    switch (fStartBlockNo) {
                        case "A":
                            exportDate = d.getSHIP_A();
                            break;
                        case "B":
                            exportDate = d.getSHIP_B();
                            break;
                        case "C":
                            exportDate = d.getSHIP_C();
                            break;
                        case "D":
                            exportDate = d.getSHIP_D();
                            break;
                        case "E":
                            exportDate = d.getSHIP_E();
                            break;
                        case "F":
                            exportDate = d.getSHIP_F();
                            break;
                    }

                }else {
                    //출하예정일 추출
                    exportDate = PartCommonUtil.findExportDate(hogi.trim(), blockNo);
                    //String exportDateMod = "";
                    /*if(exportDate == null || "".equals(exportDate.trim())) {
                        exportDate = "";
                        System.out.println(hogi + " > " + partNo + " > " + blockNo);
                    } else {
                        exportDateMod = exportDate.substring(0, 6);
                    }*/
                }

                if(exportDate == null || "".equals(exportDate.trim())) {
                    exportDate = "";
                    System.out.println(hogi + " > " + partNo + " > " + blockNo);
                } else {
                    exportDateMod = exportDate.substring(0, 6);
                }



                //LAMP는 12/2이부터 출하


                String sendDate = erpSendDate.substring(0, 6);
                boolean insertFlag = false;

                DashPublicData dto = new DashPublicData();
                dto.setErpSendDate(erpSendDate);
                dto.setErpSendMonth(sendDate);
                dto.setHogi(hogi);
                dto.setPartNo(partNo);
                dto.setQty(qty);
                dto.setDwgNo(dwgNo);
                dto.setBlockNo(blockNo);
                dto.setGongSa(gongSa);
                dto.setSpec(spec);
                dto.setGiSong(gisong);
                dto.setMUser(mUser);
                dto.setEUser(eUser);
                dto.setPartType(partType);
                dto.setExportDate(exportDate);

                if ("202405".equals(sendDate)) {
                    insertFlag = true;
                    r202405.add(row);
                    total202405 += Double.parseDouble(qty);

                } else if ("202406".equals(sendDate)) {
                    r202406.add(row);
                    insertFlag = true;
                    total202406 += Double.parseDouble(qty);

                } else if ("202407".equals(sendDate)) {
                    r202407.add(row);
                    insertFlag = true;
                    total202407 += Double.parseDouble(qty);

                } else if ("202408".equals(sendDate)) {
                    r202408.add(row);
                    insertFlag = true;
                    total202408 += Double.parseDouble(qty);

                } else if ("202409".equals(sendDate)) {
                    r202409.add(row);
                    insertFlag = true;
                    total202409 += Double.parseDouble(qty);

                } else if ("202410".equals(sendDate)) {
                    r202410.add(row);
                    insertFlag = true;
                    total202410 += Double.parseDouble(qty);

                } else if ("202411".equals(sendDate)) {
                    r202411.add(row);
                    insertFlag = true;
                    total202411 += Double.parseDouble(qty);

                } else if ("202412".equals(sendDate)) {
                    r202412.add(row);
                    insertFlag = true;
                    total202412 += Double.parseDouble(qty);

                } else if ("202501".equals(sendDate)) {
                    r202501.add(row);
                    insertFlag = true;
                    total202501 += Double.parseDouble(qty);

                } else if ("202502".equals(sendDate)) {
                    r202502.add(row);
                    insertFlag = true;
                    total202502 += Double.parseDouble(qty);

                } else if ("202503".equals(sendDate)) {
                    r202503.add(row);
                    insertFlag = true;
                    total202503 += Double.parseDouble(qty);

                } else if ("202504".equals(sendDate)) {
                    r202504.add(row);
                    insertFlag = true;
                    total202504 += Double.parseDouble(qty);

                } else if ("202505".equals(sendDate)) {
                    r202505.add(row);
                    insertFlag = true;
                    total202505 += Double.parseDouble(qty);

                } else if ("202506".equals(sendDate)) {
                    r202506.add(row);
                    insertFlag = true;
                    total202506 += Double.parseDouble(qty);

                } else if ("202507".equals(sendDate)) {
                    r202507.add(row);
                    insertFlag = true;
                    total202507 += Double.parseDouble(qty);

                } else if ("202508".equals(sendDate)) {
                    r202508.add(row);
                    insertFlag = true;
                    total202508 += Double.parseDouble(qty);

                } else if ("202509".equals(sendDate)) {
                    r202509.add(row);
                    insertFlag = true;
                    total202509 += Double.parseDouble(qty);

                } else if ("202510".equals(sendDate)) {
                    r202510.add(row);
                    insertFlag = true;
                    total202510 += Double.parseDouble(qty);

                } else if ("2025011".equals(sendDate)) {
                    r202511.add(row);
                    insertFlag = true;
                    total202511 += Double.parseDouble(qty);

                } else if ("202512".equals(sendDate)) {
                    r202512.add(row);
                    insertFlag = true;
                    total202512 += Double.parseDouble(qty);
                }

                totalQty = total202405 + total202406 + total202407 + total202408 + total202409 + total202410 + total202411 + total202412
                        + total202501 + total202502 + total202503 + total202504 + total202505 + total202506 + total202507 + total202508
                        + total202509 + total202510 + total202511 + total202512;

                if(insertFlag == true) {
                    dto.setBatchDate(todayValue);
                    dashboardRepository.savePartData(dto);
                }


                //출하예정일로 구분하여 자재의 수량 집계
                if ("202405".equals(exportDateMod)) {
                    exportCnt202405 += Double.parseDouble(qty);
                    exportPrice202405 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202406".equals(exportDateMod)) {
                    exportCnt202406 += Double.parseDouble(qty);
                    exportPrice202406 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202407".equals(exportDateMod)) {
                    exportCnt202407 += Double.parseDouble(qty);
                    exportPrice202407 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202408".equals(exportDateMod)) {
                    exportCnt202408 += Double.parseDouble(qty);
                    exportPrice202408 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202409".equals(exportDateMod)) {
                    exportCnt202409 += Double.parseDouble(qty);
                    exportPrice202409 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202410".equals(exportDateMod)) {
                    exportCnt202410 += Double.parseDouble(qty);
                    exportPrice202410 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202411".equals(exportDateMod)) {
                    exportCnt202411 += Double.parseDouble(qty);
                    exportPrice202411 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202412".equals(exportDateMod)) {
                    exportCnt202412 += Double.parseDouble(qty);
                    exportPrice202412 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202501".equals(exportDateMod)) {
                    exportCnt202501 += Double.parseDouble(qty);
                    exportPrice202501 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202502".equals(exportDateMod)) {
                    exportCnt202502 += Double.parseDouble(qty);
                    exportPrice202502 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202503".equals(exportDateMod)) {
                    exportCnt202503 += Double.parseDouble(qty);
                    exportPrice202503 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202504".equals(exportDateMod)) {
                    exportCnt202504 += Double.parseDouble(qty);
                    exportPrice202504 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202505".equals(exportDateMod)) {
                    exportCnt202505 += Double.parseDouble(qty);
                    exportPrice202505 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202506".equals(exportDateMod)) {
                    exportCnt202506 += Double.parseDouble(qty);
                    exportPrice202506 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202507".equals(exportDateMod)) {
                    exportCnt202507 += Double.parseDouble(qty);
                    exportPrice202507 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202508".equals(exportDateMod)) {
                    exportCnt202508 += Double.parseDouble(qty);
                    exportPrice202508 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202509".equals(exportDateMod)) {
                    exportCnt202509 += Double.parseDouble(qty);
                    exportPrice202509 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202510".equals(exportDateMod)) {
                    exportCnt202510 += Double.parseDouble(qty);
                    exportPrice202510 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202511".equals(exportDateMod)) {
                    exportCnt202511 += Double.parseDouble(qty);
                    exportPrice202511 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else if ("202512".equals(exportDateMod)) {
                    exportCnt202512 += Double.parseDouble(qty);
                    exportPrice202512 += PartPublicCommonUtil.getPrice(partNo, partType);

                } else {
                    exportEtc += Double.parseDouble(qty);
                    exportPriceEtc += PartPublicCommonUtil.getPrice(partNo, partType);
                }

            } // end for

            partData.setDis202405(r202405.size());
            partData.setDis202406(r202406.size());
            partData.setDis202407(r202407.size());
            partData.setDis202408(r202408.size());
            partData.setDis202409(r202409.size());
            partData.setDis202410(r202410.size());
            partData.setDis202411(r202411.size());
            partData.setDis202412(r202412.size());

            partData.setDis202501(r202501.size());
            partData.setDis202502(r202502.size());
            partData.setDis202503(r202503.size());
            partData.setDis202504(r202504.size());
            partData.setDis202505(r202505.size());
            partData.setDis202506(r202506.size());
            partData.setDis202507(r202507.size());
            partData.setDis202508(r202508.size());
            partData.setDis202509(r202509.size());
            partData.setDis202510(r202510.size());
            partData.setDis202511(r202511.size());
            partData.setDis202512(r202512.size());



            partData.setPriceTotalCnt(totalQty);
            partData.setPrice202405(total202405);
            partData.setPrice202406(total202406);
            partData.setPrice202407(total202407);
            partData.setPrice202408(total202408);
            partData.setPrice202409(total202409);
            partData.setPrice202410(total202410);
            partData.setPrice202411(total202411);
            partData.setPrice202412(total202412);

            partData.setPrice202501(total202501);
            partData.setPrice202502(total202502);
            partData.setPrice202503(total202503);
            partData.setPrice202504(total202504);
            partData.setPrice202505(total202505);
            partData.setPrice202506(total202506);
            partData.setPrice202507(total202507);
            partData.setPrice202508(total202508);
            partData.setPrice202509(total202509);
            partData.setPrice202510(total202510);
            partData.setPrice202511(total202511);
            partData.setPrice202512(total202512);



            if(partType.equals("LAMP_OVER") || partType.equals("LAMP_PIT") || partType.equals("LAMP_CARTOP") || partType.equals("LAMP_HOIST")) {
                exportCnt202410 = 0;
                exportCnt202411 = 0;

                exportPrice202410 = 0;
                exportPrice202411 = 0;
            }

            //출하일로 집계
            int totalExportCnt = exportCnt202405 + exportCnt202406 + exportCnt202407 + exportCnt202408 + exportCnt202409 + exportCnt202410
                    + exportCnt202411 + exportCnt202412 + exportCnt202501 + exportCnt202502 + exportCnt202503
                    + exportCnt202504 + exportCnt202505 + exportCnt202506 + exportCnt202507 + exportCnt202508
                    + exportCnt202509 + exportCnt202510 + exportCnt202511 + exportCnt202512 + exportEtc;

            partData.setExportTotalCnt(totalExportCnt);
            partData.setExport202405(exportCnt202405);
            partData.setExport202406(exportCnt202406);
            partData.setExport202407(exportCnt202407);
            partData.setExport202408(exportCnt202408);
            partData.setExport202409(exportCnt202409);
            partData.setExport202410(exportCnt202410);
            partData.setExport202411(exportCnt202411);
            partData.setExport202412(exportCnt202412);
            partData.setExport202501(exportCnt202501);
            partData.setExport202502(exportCnt202502);
            partData.setExport202503(exportCnt202503);
            partData.setExport202504(exportCnt202504);
            partData.setExport202505(exportCnt202505);
            partData.setExport202506(exportCnt202506);
            partData.setExport202507(exportCnt202507);
            partData.setExport202508(exportCnt202508);
            partData.setExport202509(exportCnt202509);
            partData.setExport202510(exportCnt202510);
            partData.setExport202511(exportCnt202511);
            partData.setExport202512(exportCnt202512);
            partData.setExportEtc(exportEtc);
            partData.setBatchDate(todayValue);

            dashboardRepository.save(partData); //저장


            //출하일로 금액집계
            DashPublicExportPrice exportPrice = new DashPublicExportPrice();
            exportPrice.setPrice202405(exportPrice202405);
            exportPrice.setPrice202406(exportPrice202406);
            exportPrice.setPrice202407(exportPrice202407);
            exportPrice.setPrice202408(exportPrice202408);
            exportPrice.setPrice202409(exportPrice202409);
            exportPrice.setPrice202410(exportPrice202410);
            exportPrice.setPrice202411(exportPrice202411);
            exportPrice.setPrice202412(exportPrice202412);
            exportPrice.setPrice202501(exportPrice202501);
            exportPrice.setPrice202502(exportPrice202502);
            exportPrice.setPrice202503(exportPrice202503);
            exportPrice.setPrice202504(exportPrice202504);
            exportPrice.setPrice202505(exportPrice202505);
            exportPrice.setPrice202506(exportPrice202506);
            exportPrice.setPrice202507(exportPrice202507);
            exportPrice.setPrice202508(exportPrice202508);
            exportPrice.setPrice202509(exportPrice202509);
            exportPrice.setPrice202510(exportPrice202510);
            exportPrice.setPrice202511(exportPrice202511);
            exportPrice.setPrice202512(exportPrice202512);
            exportPrice.setPriceEtc(exportPriceEtc);
            exportPrice.setPartType(partType);
            exportPrice.setBatchDate(todayValue);
            dashboardRepository.saveExportPrice(exportPrice); //저장


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("End PartType = " + partType);
    }



    public void deletePublic() {

        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            LocalDate now = LocalDate.now();
            String todayValue = now.toString();

            /*Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
            String id = "SA";
            String pw = "AutodeskVault@26200";*/

            //con = DriverManager.getConnection(url,id,pw);
            con = VaultDBConnection.getConnection();

            StringBuffer temSql = new StringBuffer();
            temSql.append(" DELETE FROM dash_public ");
            temSql.append(" WHERE BATCH_DATE = ?   ");
            //temSql.append(" NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,   ");

            pstmt = con.prepareStatement(temSql.toString());
            pstmt.setString(1, todayValue);

            //rs = pstmt.executeQuery();
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

    }

    public void deletePublicData() {

        HashMap<String, String> data = new HashMap<String, String>();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        LocalDate now = LocalDate.now();
        String todayValue = now.toString();

        try {
            /*Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://;serverName=10.225.80.35;port=1433;databaseName=PLMPRDIF;encrypt=false;";
            String id = "SA";
            String pw = "AutodeskVault@26200"; // "qwe123!@#"
*/
            //con = DriverManager.getConnection(url,id,pw);
            con = VaultDBConnection.getConnection();

            StringBuffer temSql = new StringBuffer();
            temSql.append(" DELETE FROM dash_publicdata ");
            temSql.append(" WHERE BATCH_DATE = ?   ");
            //temSql.append(" NVL(D.SPEC1, '-') AS SPEC1, NVL(D.CON1, '-') AS CON1,   ");

            pstmt = con.prepareStatement(temSql.toString());
            pstmt.setString(1, todayValue);

            //rs = pstmt.executeQuery();
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con,pstmt,rs);
        }

    }

    public List<DashPublic> findPartPublications() {
        return dashboardRepository.findAll();
    }

    public List<DashPublic> findPartData() {
        return dashboardRepository.findAll();
    }



    /**
     * 불필요한 LAMP 데이터 삭제
     */
    @Scheduled(cron = "0 30 7 * * 1-5")
    public void deleteLamp() {

        System.out.println("START --> deleteLamp");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            LocalDate now = LocalDate.now();
            String todayValue = now.toString();

            con = VaultDBConnection.getConnection();

            StringBuffer temSql = new StringBuffer();
            //temSql.append(" DELETE FROM dash_public ");
            //temSql.append(" WHERE BATCH_DATE = ?   ");

            temSql.append(" DELETE FROM dash_publicdata ");
            temSql.append(" WHERE batch_date = ? AND part_type LIKE 'LAMP%' ");
            temSql.append(" AND LEFT(export_date, 6) IN ('202405', '202406', '202407', '202408', '202409', '202410', '202411') ");


            pstmt = con.prepareStatement(temSql.toString());
            pstmt.setString(1, todayValue);

            //rs = pstmt.executeQuery();
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }
        System.out.println("END --> deleteLamp");
    }

}
