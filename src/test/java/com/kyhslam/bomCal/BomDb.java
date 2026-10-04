package com.kyhslam.bomCal;

import com.kyhslam.util.PLMDBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 조회 전용 JDBC 헬퍼 (JdbcTemplate / MyBatis 대체)
 * - 결과 Map 의 key 는 컬럼명 대문자, 값은 rs.getString() (MyBatis String 매핑과 동일)
 * - 조회 전용 : update 메소드가 없고, autoCommit=false 로 열어 close 시 rollback 한다.
 * - 단일 커넥션이므로 스레드 간 공유하지 않는다.
 */
public class BomDb implements AutoCloseable {

	private final Connection con;

	public BomDb(Connection con) throws SQLException {
		if (con == null)
			throw new SQLException("DB 커넥션이 없습니다.");
		this.con = con;
		this.con.setAutoCommit(false);
	}

	/** PLMDBConnection.getConnection() 으로 접속한다. */
	public static BomDb open() throws SQLException {
		Connection con = PLMDBConnection.getConnection();
		if (con == null)
			throw new SQLException("PLM DB 접속 실패 (PLMDBConnection.getConnection)");
		return new BomDb(con);
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

	/** 첫 행 첫 컬럼 (없으면 null) */
	public String queryForString(String sql, Object... params) throws SQLException {
		Map<String, String> row = queryForFirst(sql, params);
		return row == null ? null : row.values().iterator().next();
	}

	/** 첫 컬럼 목록 */
	public List<String> queryForStringList(String sql, Object... params) throws SQLException {
		List<String> result = new ArrayList<String>();
		for (Map<String, String> row : queryForList(sql, params))
			result.add(row.values().iterator().next());
		return result;
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
			if (!con.isClosed()) {
				con.rollback();
				con.close();
			}
		} catch (SQLException ignored) {
		}
	}
}
