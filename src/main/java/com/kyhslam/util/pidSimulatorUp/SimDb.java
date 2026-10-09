package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.PLMDBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 순수 JDBC 헬퍼 (JdbcTemplate / MyBatis 대체)
 * - 결과 Map 의 key 는 컬럼명 대문자, 값은 rs.getString() (MyBatis String 매핑과 동일)
 * - 단일 커넥션이므로 스레드 간 공유하지 않는다.
 */
public class SimDb implements AutoCloseable {

	private final Connection con;

	public SimDb(Connection con) throws SQLException {
		if (con == null)
			throw new SQLException("DB 커넥션이 없습니다.");
		this.con = con;
	}

	/** PLMDBConnection.getConnection() 으로 접속한다. */
	public static SimDb open() throws SQLException {
		Connection con = PLMDBConnection.getConnection();
		if (con == null)
			throw new SQLException("PLM DB 접속 실패 (PLMDBConnection.getConnection)");
		return new SimDb(con);
	}

	public List<Map<String, String>> queryForList(String sql, Object... params) throws SQLException {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try (PreparedStatement ps = prepare(sql, params)) {
			ps.setFetchSize(500);
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				int cnt = md.getColumnCount();
				while (rs.next()) {
					Map<String, String> row = new LinkedHashMap<String, String>();
					for (int i = 1; i <= cnt; i++) {
						row.put(md.getColumnLabel(i).toUpperCase(), rs.getString(i));
					}
					result.add(row);
				}
			}
		}
		return result;
	}

	/** 결과가 없으면 null */
	public Map<String, String> queryForFirst(String sql, Object... params) throws SQLException {
		List<Map<String, String>> list = queryForList(sql, params);
		return list.isEmpty() ? null : list.get(0);
	}

	public int update(String sql, Object... params) throws SQLException {
		try (PreparedStatement ps = prepare(sql, params)) {
			return ps.executeUpdate();
		}
	}

	private PreparedStatement prepare(String sql, Object... params) throws SQLException {
		PreparedStatement ps = con.prepareStatement(sql);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
		}
		return ps;
	}

	@Override
	public void close() {
		try {
			if (!con.isClosed())
				con.close();
		} catch (SQLException ignored) {
		}
	}
}
