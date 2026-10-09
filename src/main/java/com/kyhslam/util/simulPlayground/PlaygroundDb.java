package com.kyhslam.util.simulPlayground;

import com.kyhslam.util.PLMDBConnection;
import com.kyhslam.util.pidSimulatorUp.SimDb;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 플레이그라운드 전용 DB 연결 : 읽기 전용.
 * 수정본은 절대 DB 에 저장하지 않는다. 엔진의 유일한 쓰기(variant_errorlog)도 SimContext(saveErrorLog=false) 로 막고,
 * 연결 자체도 읽기 전용으로 열어 혹시 모를 쓰기를 DB 에서 거부하게 한다.
 */
public final class PlaygroundDb {

	private PlaygroundDb() {}

	/** PLMDBConnection 으로 접속해 읽기 전용으로 설정한 SimDb */
	public static SimDb openReadOnly() throws SQLException {
		Connection con = PLMDBConnection.getConnection();
		if (con == null)
			throw new SQLException("PLM DB 접속 실패 (PLMDBConnection.getConnection)");
		try {
			con.setReadOnly(true);
		} catch (SQLException e) {
			con.close();
			throw e;
		}
		return new SimDb(con);
	}
}
