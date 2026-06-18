package com.kyhslam.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SQLCommonUtil {


    public static List<Map<String, Object>> executeQuery(String sql) {

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        try {

            //con = PLMDBConnection.getConnection();
            con = PLMDBConnection.getSelectConnection(); //조회전용
            pstmt = con.prepareStatement(sql.toString());

            //---
            try (ResultSet resultSet = pstmt.executeQuery()) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                /*
                 * SELECT 컬럼 헤더 추출
                 *
                 * getColumnLabel():
                 *   SELECT A.MD$NUMBER AS REQUEST_NO인 경우 REQUEST_NO 반환
                 *
                 * getColumnName():
                 *   실제 DB 컬럼명인 MD$NUMBER 반환 가능
                 */
                for (int columnIndex = 1;
                     columnIndex <= columnCount;
                     columnIndex++) {

                    String columnLabel =
                            metaData.getColumnLabel(columnIndex);

                    System.out.println("columnLabel -- "+ columnLabel);
                    headers.add(columnLabel);
                }

                // 조회 데이터 추출
                while (resultSet.next()) {

                    // SELECT 컬럼 순서를 유지하기 위해 LinkedHashMap 사용
                    Map<String, Object> row = new LinkedHashMap<>();

                    for (int columnIndex = 1;
                         columnIndex <= columnCount;
                         columnIndex++) {

                        String header = headers.get(columnIndex - 1);
                        Object value = resultSet.getObject(columnIndex);

                        row.put(header, value);
                    }

                    rows.add(row);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PLMDBConnection.disconnect(con, pstmt, rs);
        }


        return rows;
    }
}
