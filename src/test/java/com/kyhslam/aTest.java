package com.kyhslam;

import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.util.DesignReqCommonUtil;

import java.util.ArrayList;

public class aTest {

    public static void main(String[] args) {


        //2805876119 수배로직
        //2500087253 긱계
        ArrayList<DesignRequestDTO> data = DesignReqCommonUtil.findDesignReqFromType("2805876119", "2500087253");


        int cre202501 = 0;
        int cre202502 = 0;
        int cre202503 = 0;
        int cre202504 = 0;
        int cre202505 = 0;
        int cre202506 = 0;
        int cre202507 = 0;
        int cre202508 = 0;
        int cre202509 = 0;
        int cre202510 = 0;
        int cre202511 = 0;
        int cre202512 = 0;

        int com202501 = 0;
        int com202502 = 0;
        int com202503 = 0;
        int com202504 = 0;
        int com202505 = 0;
        int com202506 = 0;
        int com202507 = 0;
        int com202508 = 0;
        int com202509 = 0;
        int com202510 = 0;
        int com202511 = 0;
        int com202512 = 0;

        try {

            for (int i = 0; i < data.size(); i++) {
                DesignRequestDTO dto = data.get(i);

                String status = dto.getStatus();
                String creDate = dto.getCreDate();
                String modDate = dto.getModDate();
                String modMonth = dto.getModMon();

                if("CRT".equals(status) )  {

                    if("202501".equals(modMonth)) {
                        cre202501++;
                    } else if ("202502".equals(modMonth)) {
                        cre202502++;
                    } else if ("202503".equals(modMonth)) {
                        cre202503++;
                    } else if ("202504".equals(modMonth)) {
                        cre202504++;
                    } else if ("202505".equals(modMonth)) {
                        cre202505++;
                    } else if ("202506".equals(modMonth)) {
                        cre202506++;
                    } else if ("202507".equals(modMonth)) {
                        cre202507++;
                    } else if ("202508".equals(modMonth)) {
                        cre202508++;
                    } else if ("202509".equals(modMonth)) {
                        cre202509++;
                    } else if ("202510".equals(modMonth)) {
                        cre202510++;
                    } else if ("202511".equals(modMonth)) {
                        cre202511++;
                    } else if ("202512".equals(modMonth)) {
                        cre202512++;
                    }

                } else if("RLS".equals(status)){
                    if("202501".equals(modMonth)) {
                        com202501++;
                    } else if ("202502".equals(modMonth)) {
                        com202502++;
                    } else if ("202503".equals(modMonth)) {
                        com202503++;
                    } else if ("202504".equals(modMonth)) {
                        com202504++;
                    } else if ("202505".equals(modMonth)) {
                        com202505++;
                    } else if ("202506".equals(modMonth)) {
                        com202506++;
                    } else if ("202507".equals(modMonth)) {
                        com202507++;
                    } else if ("202508".equals(modMonth)) {
                        com202508++;
                    } else if ("202509".equals(modMonth)) {
                        com202509++;
                    } else if ("202510".equals(modMonth)) {
                        com202510++;
                    } else if ("202511".equals(modMonth)) {
                        com202511++;
                    } else if ("202512".equals(modMonth)) {
                        com202512++;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


        System.out.println("cre202501 = " + cre202501);
        System.out.println("com202501 = " + com202501);

    }
}
