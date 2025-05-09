package com.kyhslam.service;

import com.kyhslam.dto.PartPublicDTO;
import com.kyhslam.util.VaultDBConnection;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@Service
public class PartPublicService {


    public ArrayList<PartPublicDTO> getPartPublicData() {

        ArrayList<PartPublicDTO> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs 			= null;
        Connection con          = null;
        try {

            con = VaultDBConnection.getConnection();

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT  ");
            sql.append(" PARTNO, MAINTYPE, PARTTYPE, PARTPRICE ");
            sql.append(" FROM PUBLICPART_LIST ");

            pstmt = con.prepareStatement(sql.toString());

            rs = pstmt.executeQuery();

            while(rs.next()) {
                String partNo = rs.getString("PARTNO") == null ? "" : rs.getString("PARTNO");
                String mainType = rs.getString("MAINTYPE") == null ? "" : rs.getString("MAINTYPE");
                String partType = rs.getString("PARTTYPE") == null ? "" : rs.getString("PARTTYPE");
                String partPrice = rs.getString("PARTPRICE") == null ? "" : rs.getString("PARTPRICE");

                PartPublicDTO dto = new PartPublicDTO();
                dto.setPartNo(partNo);
                dto.setMainType(mainType);
                dto.setPartType(partType);
                dto.setPartPrice(partPrice);

                result.add(dto);
            } // end while



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            VaultDBConnection.disconnect(con, pstmt, rs);
        }

        return result;
    }


}
