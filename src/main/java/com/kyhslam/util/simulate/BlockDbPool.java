package com.kyhslam.util.simulate;

import com.kyhslam.util.PLMDBConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 블록 시뮬 전용 PLM DB 커넥션 풀 (HikariCP)
 * - 접속은 PLMDBConnection.getConnection() 을 그대로 쓰고, 맺은 커넥션만 재사용한다.
 * - 다른 기능(PLMDBConnection, BlockDb.open)에는 영향이 없다.
 * - 첫 사용 시점에 풀을 만든다 (서버 기동시 DB 접속 안 함).
 */
public final class BlockDbPool {

	private BlockDbPool() {
	}

	private static class Holder {
		static final HikariDataSource DS = create();

		private static HikariDataSource create() {
			HikariConfig config = new HikariConfig();
			config.setPoolName("BlockSimulPool");
			config.setDataSource(new PlmDataSource());
			// 운영 DB(메인 시스템과 공유) 보호 : 세션을 최소로 쓰고 오래 붙잡지 않는다
			config.setMaximumPoolSize(BlockConsts.SIMULATE_THREAD_COUNT);	// 전체 동시 세션 상한 = 요청 1건의 호기 병렬 수(4)
			config.setMinimumIdle(0);				// 쓰지 않을 때는 커넥션을 유지하지 않는다
			config.setIdleTimeout(60 * 1000L);		// 1분 미사용시 닫음
			config.setMaxLifetime(20 * 60 * 1000L);	// RDS 측 유휴 끊김 대비
			config.setKeepaliveTime(0);				// 유휴 커넥션 핑 안 함
			config.setConnectionTimeout(10 * 60 * 1000L);	// 동시 요청은 실패시키지 않고 앞 요청이 끝날 때까지 대기
			config.setAutoCommit(true);				// DriverManager 기본값과 동일
			config.setInitializationFailTimeout(-1);	// 풀 생성시 접속 실패해도 생성 (getConnection 시점에 오류)
			HikariDataSource ds = new HikariDataSource(config);
			Runtime.getRuntime().addShutdownHook(new Thread(ds::close, "BlockSimulPool-shutdown"));
			return ds;
		}
	}

	/** 풀에서 커넥션을 받는다. close() 하면 풀로 반납된다. */
	public static Connection getConnection() throws SQLException {
		return Holder.DS.getConnection();
	}

	/** PLMDBConnection.getConnection() 을 DataSource 로 감싼다 */
	private static class PlmDataSource implements DataSource {
		@Override
		public Connection getConnection() throws SQLException {
			Connection con = PLMDBConnection.getConnection();
			if (con == null)
				throw new SQLException("PLM DB 접속 실패 (PLMDBConnection.getConnection)");
			return con;
		}

		@Override
		public Connection getConnection(String username, String password) throws SQLException {
			return getConnection();
		}

		@Override
		public PrintWriter getLogWriter() {
			return null;
		}

		@Override
		public void setLogWriter(PrintWriter out) {
		}

		@Override
		public void setLoginTimeout(int seconds) {
		}

		@Override
		public int getLoginTimeout() {
			return 0;
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			throw new SQLFeatureNotSupportedException();
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			throw new SQLException("unwrap 미지원");
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) {
			return false;
		}
	}
}
