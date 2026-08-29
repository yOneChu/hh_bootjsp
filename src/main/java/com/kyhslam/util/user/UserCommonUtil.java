package com.kyhslam.util.user;

import com.kyhslam.dto.UserDTO;
import com.kyhslam.util.PLMDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserCommonUtil {


    public static ArrayList<UserDTO> getUserInfo() {


        ArrayList<UserDTO> userList = new ArrayList<>();

        String query = """
                SELECT F.SF$OUID, -- KEY OUID
                       --F.MD$STATUS, -- 상태
                       F.MD$NUMBER AS SABUN, -- 사번
                       F.MD$DESC AS NAME, -- 이름
                       F.EMAIL AS EMAIL -- EMAIL
                       --,F.DESIGNPART
                       --,F.*
                FROM FUSER$SF F
                WHERE F.MD$STATUS = 'CRT'
                AND F.DESIGNPART IS NOT NULL
                """;

        try (Connection conn = PLMDBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    String OID = rs.getString("OID");
                    String SABUN = rs.getString("SABUN");
                    String NAME = rs.getString("NAME");
                    String EMAIL = rs.getString("EMAIL");

                    UserDTO dto = new UserDTO();
                    dto.setOID(OID);
                    dto.setSabun(SABUN);
                    dto.setName(NAME);
                    dto.setEmail(EMAIL);

                    userList.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userList;
    }
}
