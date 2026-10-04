package com.kyhslam.BlockSimul;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * COMDB(SRM) 조회 (dyna.plmetc.commonDB.commondbImpl / commonDB 매퍼 중 PIDJavaMethod 에서 쓰는 부분)
 * 접속 : CommonDBConnection.getConnection() (조회마다 열고 닫음)
 */
public class BlockComDb {

	/** commondbImpl.getZPPT027DATA → commonDB.getZPPT027_DATE */
	public List<Map> getZPPT027DATA(String hogiNumber) throws SQLException {
		StringBuilder ship = new StringBuilder();
		StringBuilder shipMin = new StringBuilder();
		for (String block : new String[] { "A", "B", "C", "D", "E", "F" }) {
			ship.append(" (SELECT B.ILDAT FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = '").append(block)
				.append("' AND B.POSID = A.POSID) SHIP_").append(block).append(", ");
			shipMin.append(" (SELECT MIN(TO_NUMBER(B.ILDAT)) FROM ZPPT027 B WHERE B.ACTIV = '05' AND B.GUBUN = '01' AND B.BLOCK = '").append(block)
				.append("' AND B.PSPID = A.PSPID) SHIP_MIN_").append(block).append(block.equals("F") ? " " : ", ");
		}

		String sql = "SELECT DISTINCT A.POSID WBS, A.MANDT MANDT, A.PSPID VBELN, " + ship + shipMin
				+ " FROM ZPPT027 A "
				+ " WHERE A.ILDAT IS NOT NULL "
				+ "   AND A.ACTIV = '05' "
				+ "   AND A.GUBUN = '01' "
				+ "   AND A.POSID NOT LIKE ('%Y%') "
				+ "   AND A.POSID NOT LIKE ('%T%') "
				+ "   AND A.POSID NOT LIKE ('%V%') "
				+ "   AND A.POSID NOT LIKE ('%J%') "
				+ "   AND A.POSID NOT LIKE ('%-%') "
				+ "   AND A.POSID NOT LIKE ('%Q%') "
				+ "   AND A.POSID NOT LIKE ('%U%') "
				+ "   AND A.POSID NOT LIKE ('%W%') "
				+ "   AND A.POSID NOT LIKE ('%NB%') AND A.POSID NOT LIKE ('%NS%') AND A.POSID NOT LIKE ('%NC%') "
				+ "   AND A.POSID NOT LIKE ('%C%') AND A.POSID NOT LIKE ('%H%') AND A.POSID NOT LIKE ('%S%') "
				+ "   AND A.POSID = ? ";

		return query(sql, hogiNumber);
	}

	/** commondbImpl.getZMASTER02TXT04 → commonDB.getzmaster02cancel */
	public List<Map> getZMASTER02TXT04(String hogiNumber) throws SQLException {
		return query(" SELECT POSID_1, TXT04 FROM zmaster02 WHERE mandt = '100' AND POSID_1 = ? AND txt04 = 'C' ", hogiNumber);
	}

	private List<Map> query(String sql, Object... params) throws SQLException {
		Connection con = CommonDBConnection.getConnection();
		if (con == null)
			throw new SQLException("공통DB 접속 실패 (CommonDBConnection.getConnection)");
		try (BlockDb db = new BlockDb(con)) {
			return new ArrayList<Map>(db.queryForList(sql, params));
		}
	}
}
