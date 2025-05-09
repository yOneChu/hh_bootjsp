package com.kyhslam.controller;

import com.kyhslam.repository.DashboardRepository;
import com.kyhslam.service.PartPublicationService;
import com.kyhslam.util.DashboardCommonUtil;
import com.kyhslam.util.DashboardHPB;
import com.kyhslam.util.DashboardHPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    private final PartPublicationService partPublicationService;
    private final DashboardRepository dashboardRepository;




    //https://10.225.80.35:8070/dashboard/dashboardSchdule
    @GetMapping("/dashboardSchdule")
    //@CrossOrigin(origins = "*")
    @ResponseBody
    public String dashboardSchdule(Model model) {

        long startTime = System.currentTimeMillis();

        partPublicationService.deletePublic();
        partPublicationService.deletePublicData();

        ArrayList<String> cpMRL_5 = new ArrayList<String>();
        ArrayList<String> cpMRL_9 = new ArrayList<String>();
        ArrayList<String> cpMRL_14 = new ArrayList<String>();
        ArrayList<String> cpMRL_17 = new ArrayList<String>();

        ArrayList<String> cpMR_5_5 = new ArrayList<String>();
        ArrayList<String> cpMR_9 = new ArrayList<String>();
        ArrayList<String> cpMR_14 = new ArrayList<String>();
        ArrayList<String> cpMR_17_5 = new ArrayList<String>();

        ArrayList<String> carboxList = new ArrayList<String>(); //CAT TOP BOX
        ArrayList<String> tmBeltList = new ArrayList<String>(); //TM(BELT)
        ArrayList<String> tmRopeList = new ArrayList<String>();



        ArrayList<String> lampHOISTList = new ArrayList<String>();
        ArrayList<String> lampOVERList = new ArrayList<String>();
        ArrayList<String> lampPITList = new ArrayList<String>();
        ArrayList<String> lampCARTOPList = new ArrayList<String>();

        ArrayList<String> govList = new ArrayList<String>(); // GOV
        ArrayList<String> pitList = new ArrayList<String>();

        ArrayList<String> hpbTOPList = new ArrayList<String>();
        ArrayList<String> hpbMIDList = new ArrayList<String>();
        ArrayList<String> hpbBOTList = new ArrayList<String>();

        ArrayList<String> hipSJ21TOPList = new ArrayList<String>();
        ArrayList<String> hipSJ21MIDList = new ArrayList<String>();
        ArrayList<String> hipSJ21BOTList = new ArrayList<String>();

        //ArrayList<String> hip700List = new ArrayList<String>();

        ArrayList<String> opb_D521AG = new ArrayList<String>();
        ArrayList<String> opb_S521A = new ArrayList<String>();




        HashMap<String,Object> initMap = DashboardCommonUtil.initPartPublicData();


        cpMRL_5 = (ArrayList<String>) initMap.get("cpMRL_5");
        cpMRL_9 = (ArrayList<String>) initMap.get("cpMRL_9");
        cpMRL_14 = (ArrayList<String>) initMap.get("cpMRL_14");
        cpMRL_17 = (ArrayList<String>) initMap.get("cpMRL_17");

        cpMR_5_5 = (ArrayList<String>) initMap.get("cpMR_5_5");
        cpMR_9 = (ArrayList<String>) initMap.get("cpMR_9");
        cpMR_14 = (ArrayList<String>) initMap.get("cpMR_14");
        cpMR_17_5 = (ArrayList<String>) initMap.get("cpMR_17_5");

        carboxList = (ArrayList<String>) initMap.get("CARTOP");
        tmBeltList = (ArrayList<String>) initMap.get("TM");
        tmRopeList = (ArrayList<String>) initMap.get("TMRope");

        lampHOISTList = (ArrayList<String>) initMap.get("lampHOISTList");
        lampOVERList = (ArrayList<String>) initMap.get("lampOVERList");
        lampPITList = (ArrayList<String>) initMap.get("lampPITList");
        lampCARTOPList = (ArrayList<String>) initMap.get("lampCARTOPList");

        govList = (ArrayList<String>) initMap.get("GOV");
        pitList = (ArrayList<String>) initMap.get("PIT");

        hpbTOPList = (ArrayList<String>) initMap.get("hpbTOPList");
        hpbMIDList = (ArrayList<String>) initMap.get("hpbMIDList");
        hpbBOTList = (ArrayList<String>) initMap.get("hpbBOTList");

        hipSJ21TOPList = (ArrayList<String>) initMap.get("hipSJ21TOPList");
        hipSJ21MIDList = (ArrayList<String>) initMap.get("hipSJ21MIDList");
        hipSJ21BOTList = (ArrayList<String>) initMap.get("hipSJ21BOTList");

        //hip700List = (ArrayList<String>) initMap.get("HIP700");

        opb_D521AG = (ArrayList<String>) initMap.get("opb_D521AG");
        opb_S521A = (ArrayList<String>) initMap.get("opb_S521A");



        String PARTNO = "";


        //cpMRL_5
        PARTNO = "";
        for(int i=0; i < cpMRL_5.size(); i++) {
            PARTNO += cpMRL_5.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMRL_5", "20240501", "20250531");

        //cpMRL_5
        PARTNO = "";
        for(int i=0; i < cpMRL_9.size(); i++) {
            PARTNO += cpMRL_9.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMRL_9", "20240501", "20250531");

        //cpMRL_14
        PARTNO = "";
        for(int i=0; i < cpMRL_14.size(); i++) {
            PARTNO += cpMRL_14.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMRL_14", "20240501", "20250531");

        //cpMRL_17
        PARTNO = "";
        for(int i=0; i < cpMRL_17.size(); i++) {
            PARTNO += cpMRL_17.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMRL_17", "20240501", "20250531");

        //cpMR_5_5
        PARTNO = "";
        for(int i=0; i < cpMR_5_5.size(); i++) {
            PARTNO += cpMR_5_5.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMR_5_5", "20240501", "20250531");

        //cpMR_9
        PARTNO = "";
        for(int i=0; i < cpMR_9.size(); i++) {
            PARTNO += cpMR_9.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMR_9", "20240501", "20250531");

        //cpMR_14
        PARTNO = "";
        for(int i=0; i < cpMR_14.size(); i++) {
            PARTNO += cpMR_14.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMR_14", "20240501", "20250531");

        //cpMR_17_5
        PARTNO = "";
        for(int i=0; i < cpMR_17_5.size(); i++) {
            PARTNO += cpMR_17_5.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "cpMR_17_5", "20240501", "20250531");

        //TM(Belt Type)
        PARTNO = "";
        for(int i=0; i < tmBeltList.size(); i++) {
            PARTNO += tmBeltList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "TM", "20240501", "20250531");

        //TM(Rope)
        PARTNO = "";
        for(int i=0; i < tmRopeList.size(); i++) {
            PARTNO += tmRopeList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "TMRope", "20240501", "20250531");

        //CAT TOP BOX
        PARTNO = "";
        for(int i=0; i < carboxList.size(); i++) {
            PARTNO += carboxList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "CARTOPBOX", "20240501", "20250531");

        //GOV
        PARTNO = "";
        for(int i=0; i < govList.size(); i++) {
            PARTNO += govList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "GOV", "20240501", "20250531");


        //LAMP
        PARTNO = "";
        for(int i=0; i < lampOVERList.size(); i++) {
            PARTNO += lampOVERList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "LAMP_OVER", "20240501", "20250531");

        PARTNO = "";
        for(int i=0; i < lampPITList.size(); i++) {
            PARTNO += lampPITList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "LAMP_PIT", "20240501", "20250531");


        PARTNO = "";
        for(int i=0; i < lampCARTOPList.size(); i++) {
            PARTNO += lampCARTOPList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "LAMP_CARTOP", "20240501", "20250531");


        PARTNO = "";
        for(int i=0; i < lampHOISTList.size(); i++) {
            PARTNO += lampHOISTList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "LAMP_HOIST", "20240501", "20250531");


        //HPB
        PARTNO = "";
        for(int i=0; i < hpbTOPList.size(); i++) {
            PARTNO += hpbTOPList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_TOP", "20240501", "20250531");

        PARTNO = "";
        for(int i=0; i < hpbMIDList.size(); i++) {
            PARTNO += hpbMIDList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_MID", "20240501", "20250531");


        PARTNO = "";
        for(int i=0; i < hpbBOTList.size(); i++) {
            PARTNO += hpbBOTList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_BOT", "20240501", "20250531");

        //HIP
        PARTNO = "";
        for(int i=0; i < hipSJ21TOPList.size(); i++) {
            PARTNO += hipSJ21TOPList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HIP_TOP", "20240501", "20250531");

        PARTNO = "";
        for(int i=0; i < hipSJ21MIDList.size(); i++) {
            PARTNO += hipSJ21MIDList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HIP_MID", "20240501", "20250531");

        PARTNO = "";
        for(int i=0; i < hipSJ21BOTList.size(); i++) {
            PARTNO += hipSJ21BOTList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HIP_BOT", "20240501", "20250531");

        //PIT
        PARTNO = "";
        for(int i=0; i < pitList.size(); i++) {
            PARTNO += pitList.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "PIT", "20241007", "20250531");

        //HIP700
        PARTNO = "";
        ArrayList<String> HPI_S700 = DashboardHPI.HPI_S700();
        for(int i=0; i < HPI_S700.size(); i++) {
            PARTNO += HPI_S700.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPI_S700", "20240501", "20250531");

        //HPI_SC
        PARTNO = "";
        ArrayList<String> HPI_SC = DashboardHPI.HPI_SC();
        for(int i=0; i < HPI_SC.size(); i++) {
            PARTNO += HPI_SC.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPI_SC", "20240501", "20250531");


        //OPB(D521AG)
        PARTNO = "";
        for(int i=0; i < opb_D521AG.size(); i++) {
            PARTNO += opb_D521AG.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "OPB_D521AG", "20240501", "20250531");

        //OPB(S521A)
        PARTNO = "";
        for(int i=0; i < opb_S521A.size(); i++) {
            PARTNO += opb_S521A.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "OPB_S521A", "20240501", "20250531");


        //HPB(K21,TOP)
        ArrayList<String> HPB_K21_TOP = (ArrayList<String>) initMap.get("HPB_K21_TOP");
        PARTNO = "";
        for(int i=0; i < HPB_K21_TOP.size(); i++) {
            PARTNO += HPB_K21_TOP.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_K21_TOP", "20240501", "20250531");

        //HPB(K21,MID)
        ArrayList<String> HPB_K21_MID = (ArrayList<String>) initMap.get("HPB_K21_MID");
        PARTNO = "";
        for(int i=0; i < HPB_K21_MID.size(); i++) {
            PARTNO += HPB_K21_MID.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_K21_MID", "20240501", "20250531");

        //HPB(K21,BOT)
        ArrayList<String> HPB_K21_BOT = (ArrayList<String>) initMap.get("HPB_K21_BOT");
        PARTNO = "";
        for(int i=0; i < HPB_K21_BOT.size(); i++) {
            PARTNO += HPB_K21_BOT.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_K21_BOT", "20240501", "20250531");

        //HPB(K21A,TOP)
        ArrayList<String> HPB_K21A_TOP = (ArrayList<String>) initMap.get("HPB_K21A_TOP");
        PARTNO = "";
        for(int i=0; i < HPB_K21A_TOP.size(); i++) {
            PARTNO += HPB_K21A_TOP.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_K21A_TOP", "20240501", "20250531");

        //HPB(K21A,MID)
        ArrayList<String> HPB_K21A_MID = (ArrayList<String>) initMap.get("HPB_K21A_MID");
        PARTNO = "";
        for(int i=0; i < HPB_K21A_MID.size(); i++) {
            PARTNO += HPB_K21A_MID.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_K21A_MID", "20240501", "20250531");


        //HPB(K21A,BOT)
        PARTNO = "";
        ArrayList<String> HPB_K21A_BOT = (ArrayList<String>) initMap.get("HPB_K21A_BOT");
        for(int i=0; i < HPB_K21A_BOT.size(); i++) {
            PARTNO += HPB_K21A_BOT.get(i) + ",";
        }
        PARTNO = PARTNO.substring(0, PARTNO.length() - 1);
        partPublicationService.savePartPublication(PARTNO, "HPB_K21A_BOT", "20240501", "20250531");



        long endTime = System.currentTimeMillis();
        long secDiffTime = (endTime - startTime) / 1000;

        System.out.println("milli seconds :: " + (endTime - startTime));
        System.out.println("secDiffTime :: " + secDiffTime);

        return "ok";
    }


    //http://localhost:8070/dashboard/batchvDelete
    //dashboard/batchvDelete
    @GetMapping("/batchvDelete")
    @ResponseBody
    public String batchvDelete(Model model) {

        partPublicationService.deletePublic();
        partPublicationService.deletePublicData();
        return "ok";
    }

    //http://localhost:8070/dashboard/batchv1
    //dashboard/batchv1
    @GetMapping("/batchv1")
    @ResponseBody
    public String batchv1(Model model) {
        partPublicationService.scheduleProcessV1(); // CP 만 수행

        return "ok";
    }

    //http://localhost:8070/dashboard/batchv2
    //dashboard/batchv2
    @GetMapping("/batchv2")
    @ResponseBody
    public String batchv2(Model model) {
        partPublicationService.scheduleProcessV2(); // CP, LAMP 제외 품목 수행

        return "ok";
    }

    //http://localhost:8070/dashboard/batchv3
    @GetMapping("/batchv3")
    @ResponseBody
    public String batchv3(Model model) {
        partPublicationService.scheduleProcessV3(); // Lamp만 수행
        //partPublicationService.deleteLamp();

        return "ok";
    }

    //http://localhost:8070/dashboard/deleteLampETC
    @GetMapping("/deleteLampETC")
    @ResponseBody
    public String deleteLampETC(Model model) {
        partPublicationService.deleteLamp();

        return "ok";
    }
}
