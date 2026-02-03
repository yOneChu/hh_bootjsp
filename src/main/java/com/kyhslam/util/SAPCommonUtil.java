package com.kyhslam.util;

import com.kyhslam.dto.HogiExportDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class SAPCommonUtil {



    //원가절감실적조회
    //PARTNO는 자재번호를 ","로 연결한 파라미터임
    /**
     *
     * @param PARTNO
     * @param start_date_day
     * @param end_date_day
     * @return
     */
    public static ArrayList findSAPIF(String PARTNO, String start_date_day, String end_date_day) {

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
        specList += "EL_ABRAND\n"; //브랜드
        specList += "ARKTX\n"; // 사양
        specList += "EL_ASPSC\n";  //생산거점
        specList += "EL_AMDLR\n"; //모듈러


        ArrayList data = new ArrayList();
        try {

            HashMap<String, HogiExportDTO> tempExportInfoMap = new HashMap<>();

            boolean isOnlyElv = false;
            boolean isGeneralElv = false;

            System.out.println("PARTNO = " + PARTNO);

            HashMap resultHM = searchListBasedOnCondition.searchListBasedOnCondition(g_l_code, EL_ATYP, EL_ASPD_1, EL_ASPD_2, EL_AMAN_1, EL_AMAN_2, EL_ECSF, EL_ETM, EL_ECJJ_1, EL_ECJJ_2, PARTNO, BLOCKNO, start_date_day, end_date_day, specList, isOnlyElv, isGeneralElv);
            data = (ArrayList) resultHM.get("data");
            System.out.println("data.size == " + data);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * 출하예정일 데이터 700개씩 나눠서 MAP에 넣는다.
     * @param data
     * @param resultMap
     * @return
     */
    public static HashMap<String, HogiExportDTO> findExportDateV3(ArrayList<String> data, HashMap<String, HogiExportDTO> resultMap) {

        try {

            String appendVal = "";

            int cnt = 0;
            for(int i=0; i < data.size(); i++) {
                //ArrayList<String> row = (ArrayList<String>) data.get(i);
                String hogi = data.get(i);
                appendVal += (" '" + hogi + "',");

                cnt++;

                if(cnt == 700) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    PartCommonUtil.findExportDateV4(appendVal, resultMap);
                    appendVal = "";
                    cnt = 0;
                }
            }

            if (cnt > 0) {
                if(appendVal != null && !appendVal.equals("")) {
                    appendVal = appendVal.substring(0, appendVal.length() - 1);
                    PartCommonUtil.findExportDateV4(appendVal, resultMap);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
