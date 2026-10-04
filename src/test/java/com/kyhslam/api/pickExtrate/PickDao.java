package com.kyhslam.api.pickExtrate;

import com.kyhslam.pidSimul.PidDb;
import com.kyhslam.pidSimul.PidUtil;

import java.util.*;

/**
 * SubaeDaoImpl / SubaeDao(mybatis) / SubaeManagerPick.pickPart 의 독립 버전.
 * PidDb(JDBC) 로만 동작한다.
 */
public class PickDao {

	/** SubaeConstants.MAX_PICK_COUNT */
	public static final int MAX_PICK_COUNT = 33;

	private final PidDb db;

	/** PIDCache.hasPID 대체 (최신버전 PID 목록) */
	private Set<String> pidSet = null;

	public PickDao(PidDb db) {
		this.db = db;
	}

	/** dyna.plmetc.subae.model.PickInfo */
	public static class PickInfo {
		public String pick;
		public String qty;
		public String cmt;
		public String color;
	}

	/** dyna.plmetc.subae.model.BlockInfo */
	public static class BlockInfo {
		public String ouid;
		public String blockNo;
		public String blockName;
		public List<PickInfo> pickList;

		@Override
		public String toString() {
			return "BlockInfo [ouid=" + ouid + ", blockNo=" + blockNo + ", blockName=" + blockName + "]";
		}
	}

	/**
	 * SubaeDao.xml getBlockList
	 * @param optList 블럭옵션 (C, M, F, 1, 2, 3)
	 * @param isFloorPart 층별자재 블럭 여부
	 */
	public List<BlockInfo> getBlockList(List<String> optList, boolean isFloorPart) throws Exception {
		List<BlockInfo> blockInfos = new ArrayList<BlockInfo>();
		if (optList == null || optList.isEmpty())
			return blockInfos;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT LOWER(CONCAT('blockno$sf@', DECTOHEX(SF$OUID))) OUID, SF$OUID LOUID, MD$NUMBER BLOCKNO, MD$DESC BLOCKNAME, A.* ");
		sql.append(" FROM BLOCKNO$SF A ");
		sql.append(" WHERE ( ");
		for (int i = 1; i <= MAX_PICK_COUNT; i++) {
			sql.append(i == 1 ? "" : " OR ").append("PICK").append(i).append(" IS NOT NULL");
		}
		sql.append(" ) ");
		sql.append(" AND NVL(COD(FLOOR_PART), 'N') = ? ");
		sql.append(" AND BLOCK_STATUS = 2466425004 ");
		sql.append(" AND COD(BLOCK_OPT) IN (");
		for (int i = 0; i < optList.size(); i++) {
			sql.append(i == 0 ? "?" : ",?");
		}
		sql.append(") ");
		sql.append(" ORDER BY MD$NUMBER ");

		List<Object> params = new ArrayList<Object>();
		params.add(isFloorPart ? "Y" : "N");
		params.addAll(optList);

		for (Map<String, Object> row : db.queryForList(sql.toString(), params.toArray())) {
			BlockInfo blockInfo = new BlockInfo();
			blockInfo.ouid = PidUtil.NVL(row.get("OUID"), "");
			blockInfo.blockNo = PidUtil.NVL(row.get("BLOCKNO"), "");
			blockInfo.blockName = PidUtil.NVL(row.get("BLOCKNAME"), "");

			List<PickInfo> pickInfos = new ArrayList<PickInfo>();
			for (int i = 1; i <= MAX_PICK_COUNT; i++) {
				PickInfo pickInfo = new PickInfo();
				pickInfo.pick = PidUtil.NVL(row.get("PICK" + i), "");
				pickInfo.qty = PidUtil.NVL(row.get("QTY" + i), "");
				pickInfo.cmt = PidUtil.NVL(row.get("CMT" + i), "");
				pickInfo.color = PidUtil.NVL(row.get("COLOR" + i), "");
				pickInfos.add(pickInfo);
			}
			blockInfo.pickList = pickInfos;

			blockInfos.add(blockInfo);
		}
		return blockInfos;
	}

	/** SubaeDao.getEL_PList : 최신버전 EL_P% PID 목록 (PID, METHOD) */
	public List<Map<String, Object>> getEL_PList(boolean isFloorSpec) throws Exception {
		return db.queryForList(
				" SELECT A.PID, A.METHOD FROM VARIANT_H A, VARIANT_ID B "
				+ " WHERE A.PID = B.PID AND A.HOUID = B.LAST_HOUID AND A.PID LIKE 'EL_P%' AND NVL(A.ISFLOORSPEC, 'N') = ? "
				+ " ORDER BY PID ", isFloorSpec ? "Y" : "N");
	}

	/**
	 * SubaeManagerPick.pickPart
	 * 영업사양의 PICK 필드값(G/L코드 또는 품번)으로 해당 블럭의 최신 자재를 찾는다.
	 * @return 수배 대상이 아니면 null
	 */
	public Map<String, Object> pickPart(Map dataInfoMap, String blockOuid, String pickField) throws Exception {
		String glCode = (String) dataInfoMap.get(pickField);

		if (glCode == null || "".equals(glCode))
			return null;

		String sql = " SELECT 'normalpart$vf@' || lower(dectohex(VF$OUID)) OUID, VF$VERSION VER, COD(PART_STATUS) PART_STATUS "
				+ " ,MD$NUMBER PARTNO, G_L_CODE, SPEC, PART_SIZE, (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID=GETID(BLOCKNO)) AS B_NO, COD(ORIGIN_DIV) ORIGIN_DIV, COD(SPT) SPT "
				+ " ,(SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=A.VF$OUID AND ROWNUM=1) HASCHILD "
				+ " FROM NORMALPART$VF A, NORMALPART$ID B WHERE A.VF$OUID=B.ID$LAST ";

		if (glCode.length() == 11)
			sql += " AND MD$NUMBER LIKE ?||'_' AND BLOCKNO = ? ";
		else
			sql += " AND MD$NUMBER = ? AND BLOCKNO = ? ";

		Map<String, Object> map = db.queryForFirst(sql, glCode, blockOuid);
		if (map == null)
			return null;

		String version = (String) map.get("VER");
		String part_status = PidUtil.NVL(map.get("PART_STATUS"), "");
		if ("wip".equals(version))
			return null;

		if (!part_status.equals("Active"))
			return null;

		return map;
	}

	/** dyna.plmetc.util.StringUtil.isPidPattern (PIDCache.hasPID 는 최신버전 PID 목록으로 대체) */
	public boolean isPidPattern(String value) throws Exception {
		if (value == null)
			return false;
		if (value.matches("^[A-Z]{1,2}[0-9]{2,4}[_A-Z0-9]{0,8}"))
			return true;

		if (pidSet == null) {
			pidSet = new HashSet<String>();
			for (Map<String, Object> row : db.queryForList(
					" SELECT A.PID FROM VARIANT_H A, VARIANT_ID B WHERE A.HOUID = B.LAST_HOUID ")) {
				pidSet.add((String) row.get("PID"));
			}
		}
		return pidSet.contains(value);
	}
}
