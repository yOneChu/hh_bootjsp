package com.kyhslam.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * PLM DB(hdel_default) 공용 커넥션 풀 (HikariCP)
 * - PLMDBConnection.getConnection() 과 같은 방식으로 쓴다. close() / PLMDBConnection.disconnect() 하면 풀로 반납된다.
 * - 접속은 PLMDBConnection.getConnection() 을 그대로 쓰고, 맺은 커넥션만 재사용한다.
 * - 로그인 중 끊김(ORA-03113 등)으로 접속에 실패하면 connectionTimeout 안에서 다시 접속을 시도한다.
 * - 첫 사용 시점에 풀을 만든다 (서버 기동시 DB 접속 안 함).
 * - 시범 적용 : MLBCommonUtil
 */
public final class PLMDBPool {

    private PLMDBPool() {
    }

    private static class Holder {
        static final HikariDataSource DS = create();

        private static HikariDataSource create() {
            HikariConfig config = new HikariConfig();
            config.setPoolName("PLMPool");
            config.setDataSource(new PlmDataSource());
            // 운영 DB(메인 시스템과 공유) 보호 : 세션 수를 작게 유지한다
            config.setMaximumPoolSize(5);              // 동시 세션 상한
            config.setMinimumIdle(1);                  // 1개는 열어 두어 매 요청마다 로그인하지 않는다
            config.setIdleTimeout(5 * 60 * 1000L);     // 5분 미사용시 최소 개수 초과분을 닫음
            config.setMaxLifetime(20 * 60 * 1000L);    // RDS/방화벽 측 유휴 끊김 전에 새 커넥션으로 교체
            config.setKeepaliveTime(4 * 60 * 1000L);   // 유휴 커넥션을 4분마다 확인해 끊긴 것은 버린다
            config.setConnectionTimeout(30 * 1000L);   // 접속 실패시 이 시간 동안 재시도하며 대기
            config.setValidationTimeout(5 * 1000L);
            config.setAutoCommit(true);                // DriverManager 기본값과 동일
            config.setInitializationFailTimeout(-1);   // 풀 생성시 접속 실패해도 생성 (getConnection 시점에 오류)
            HikariDataSource ds = new HikariDataSource(config);
            Runtime.getRuntime().addShutdownHook(new Thread(ds::close, "PLMPool-shutdown"));
            return ds;
        }
    }

    /**
     * 풀에서 커넥션을 받는다. close() 하면 풀로 반납된다.
     * PLMDBConnection.getConnection() 과 같이 실패하면 오류를 출력하고 null 을 돌려준다.
     */
    public static Connection getConnection() {
        try {
            return Holder.DS.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
