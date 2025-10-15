package com.kyhslam;

import com.kyhslam.dto.DesignRequestDTO;
import com.kyhslam.util.DesignReqCommonUtil;

import java.util.ArrayList;

public class aTest {

    public static void main(String[] args) {


        //System.out.println("link02 == " + link02);
        String pid01 = "914";
        String pid02 = "32200452G01XB";
        String spec01 = "CON";
        String spec02 = "VAL";
        String link01 = "LIKE";
        String link02 = "EQUAL";
        String join = "AND";

        StringBuffer temSql = new StringBuffer();


        String param1 = "";
        if(link01 != null && !"".equals(link01)) {
            if(link01.equals("LIKE")) {
                param1 = "'%" + pid01.trim() + "%'";
            } else {
                link01 = "=";
                param1 = "'" + pid01.trim() + "'";
            }
        }

        String param2 = "";
        if(link02 != null && !"".equals(link02)) {
            if(link02.equals("LIKE") || link02.equals("NOT LIKE")) {
                param2 = "'%" + pid02.trim() + "%'";

            } else if(link02.equals("NOT_EQUAL")) {
                link02 = "!=";
                param2 = "'" + pid02.trim() + "'";

            } else {
                link02 = "=";
                param2 = "'" + pid02.trim() + "'";
            }
        }

        temSql.append(" FROM variant_d d, variant_h h, variant_id id ");
        temSql.append(" WHERE h.HOUID = id.LAST_HOUID  ");
        temSql.append(" AND h.HOUID =d.HOUID  ");
        temSql.append(" AND (  ");

        for(int i=1; i <= 20; i++) {
            if (i == 20) {
                //temSql.append(" (d." + spec01 + String.valueOf(i) + " " + gubun01 + " " + param1);
                //temSql.append(" AND d." + spec02 + String.valueOf(i) + " " + gubun02 + " " + param2 + ")");
                temSql.append(" d." + spec01 + String.valueOf(i) + " " + link01 + " " + param1);

            } else {
                temSql.append(" d." + spec01 + String.valueOf(i) + " " + link01 + " " + param1 + " OR ");
            }
        }

        temSql.append(" ) " + join + " (");

        for(int i=1; i <= 20; i++) {
            if (i == 20) {
                temSql.append(" d." + spec02 + String.valueOf(i) + " " + link02 + " " + param2 );

            } else {
                temSql.append(" d." + spec02 + String.valueOf(i) + " " + link02 + " " + param2 + " OR ");
            }
        }
        temSql.append(" ) ");


        //System.out.println("makeQueryKey == " + temSql.toString());
        System.out.println("makeQueryKeyV2 temSql== " + temSql.toString());

    }
}
