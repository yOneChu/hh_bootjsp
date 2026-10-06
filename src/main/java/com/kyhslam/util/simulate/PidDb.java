package com.kyhslam.util.simulate;

import com.kyhslam.util.PLMDBConnection;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * Spring JdbcTemplate / DBconnectionInfo 대신 사용하는 순수 JDBC 헬퍼.
 * 단일 커넥션을 사용하므로 스레드 간 공유하지 않는다.
 * 결과 Map 의 key 는 컬럼명 대문자이다.
 */
public class PidDb implements AutoCloseable {

	private final Connection con;

	public PidDb(Connection con) {
		this.con = con;
	}

	/** PLMDBConnection.getConnection() 으로 접속한다. */
	public static PidDb open() throws SQLException {
		Connection con = PLMDBConnection.getConnection();
		if (con == null)
			throw new SQLException("PLM DB 접속 실패 (PLMDBConnection.getConnection)");
		return new PidDb(con);
	}

	public Connection getConnection() {
		return con;
	}

	public List<Map<String, Object>> queryForList(String sql, Object... params) throws SQLException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try (PreparedStatement ps = prepare(sql, params)) {
			ps.setFetchSize(500);
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				int cnt = md.getColumnCount();
				while (rs.next()) {
					Map<String, Object> row = new LinkedHashMap<String, Object>();
					for (int i = 1; i <= cnt; i++) {
						row.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
					}
					result.add(row);
				}
			}
		}
		return result;
	}

	/** 결과가 없으면 null */
	public Map<String, Object> queryForFirst(String sql, Object... params) throws SQLException {
		List<Map<String, Object>> list = queryForList(sql, params);
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
			if (con != null && !con.isClosed())
				con.close();
		} catch (SQLException ignored) {
		}
	}
}
