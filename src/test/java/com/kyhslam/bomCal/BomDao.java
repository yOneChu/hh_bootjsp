package com.kyhslam.bomCal;

import com.kyhslam.bomCal.BomBlockInfo.PickInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.subae.model.SubaeDaoImpl / SubaeDao(.xml) 중 bomCalculate 에서 쓰는 조회 부분.
 * 조회 전용 : insert/delete (insert1LevelPartOfEBom, insertVariablePartRow ...) 는 옮기지 않았다.
 */
public class BomDao {

	private final BomDb db;

	public BomDao(BomDb db) {
		this.db = db;
	}

	// ------------------------------------------------------------------------ block

	/** SubaeDao.xml getBlockList : 블록옵션(BLOCK_OPT)에 해당하는 사용중 블록 */
	public List<BomBlockInfo> getBlockList(List<String> optList, boolean isFloorPart) throws Exception {
		List<Object> param = new ArrayList<Object>();
		param.add(isFloorPart ? "Y" : "N");
		param.add(BomConsts.BLOCK_STATUS_ACTIVE);
		param.addAll(optList);

		String sql = "SELECT LOWER(CONCAT('blockno$sf@', DECTOHEX(SF$OUID))) OUID, SF$OUID LOUID, MD$NUMBER BLOCKNO, MD$DESC BLOCKNAME, A.* "
				+ " FROM BLOCKNO$SF A "
				+ " WHERE " + pickNotNullCondition()
				+ " AND NVL(COD(FLOOR_PART), 'N') = ? "
				+ " AND BLOCK_STATUS = ? "
				+ " AND COD(BLOCK_OPT) IN (" + placeholders(optList.size()) + ") "
				+ " ORDER BY MD$NUMBER ";
		return toBlockInfoList(db.queryForList(sql, param.toArray()), isFloorPart);
	}

	/** SubaeDao.xml getShipBlockList : 선박은 층 구분(FLOOR_PART) 조건이 없다 */
	public List<BomBlockInfo> getShipBlockList(List<String> optList) throws Exception {
		List<Object> param = new ArrayList<Object>();
		param.add(BomConsts.BLOCK_STATUS_ACTIVE);
		param.addAll(optList);

		String sql = "SELECT LOWER(CONCAT('blockno$sf@', DECTOHEX(SF$OUID))) OUID, SF$OUID LOUID, MD$NUMBER BLOCKNO, MD$DESC BLOCKNAME, A.* "
				+ " FROM BLOCKNO$SF A "
				+ " WHERE " + pickNotNullCondition()
				+ " AND BLOCK_STATUS = ? "
				+ " AND COD(BLOCK_OPT) IN (" + placeholders(optList.size()) + ") "
				+ " ORDER BY MD$NUMBER ";
		return toBlockInfoList(db.queryForList(sql, param.toArray()), false);
	}

	/** (PICK1 IS NOT NULL OR ... OR PICK33 IS NOT NULL) */
	private static String pickNotNullCondition() {
		StringBuilder sb = new StringBuilder("(");
		for (int i = 1; i <= BomConsts.MAX_PICK_COUNT; i++) {
			if (i > 1) sb.append(" OR ");
			sb.append("PICK").append(i).append(" IS NOT NULL");
		}
		return sb.append(")").toString();
	}

	/** SubaeDaoImpl RowMapper : BLOCKNO$SF → BlockInfo (빈 PICK 은 원본에서도 pickPart 에서 걸러지므로 제외) */
	private static List<BomBlockInfo> toBlockInfoList(List<Map<String, String>> rows, boolean isFloorPart) {
		List<BomBlockInfo> blockInfos = new ArrayList<BomBlockInfo>();
		for (Map<String, String> rs : rows) {
			BomBlockInfo blockInfo = new BomBlockInfo();
			blockInfo.setOuid(BomUtil.NVL(rs.get("OUID"), ""));
			blockInfo.setlOuid(Long.parseLong(rs.get("LOUID")));
			blockInfo.setBlockNo(BomUtil.NVL(rs.get("BLOCKNO"), ""));
			blockInfo.setBlockName(BomUtil.NVL(rs.get("BLOCKNAME"), ""));
			blockInfo.setFloorPart(isFloorPart);

			List<PickInfo> pickInfos = new ArrayList<PickInfo>();
			for (int i = 1; i <= BomConsts.MAX_PICK_COUNT; i++) {
				PickInfo pickInfo = new PickInfo();
				pickInfo.setPick(BomUtil.NVL(rs.get("PICK" + i), ""));
				pickInfo.setQty(BomUtil.NVL(rs.get("QTY" + i), ""));
				pickInfo.setCmt(BomUtil.NVL(rs.get("CMT" + i), ""));
				pickInfo.setColor(BomUtil.NVL(rs.get("COLOR" + i), ""));
				if (!"".equals(pickInfo.getPick().trim()))
					pickInfos.add(pickInfo);
			}
			blockInfo.setPickList(pickInfos);
			blockInfos.add(blockInfo);
		}
		return blockInfos;
	}

	// ------------------------------------------------------------------------ part

	/**
	 * SubaeManager.pickPart : 사양의 PICK 필드값(품번)으로 해당 블록의 최신 자재를 찾는다.
	 * @return 수배 대상이 아니면(없음, wip, Active 아님) null
	 */
	public Map<String, String> pickPart(Map dataInfoMap, String blockOuid, String pickField) throws Exception {
		String glCode = (String) dataInfoMap.get(pickField);
		if (glCode == null || "".equals(glCode))
			return null;

		String sql = " SELECT 'normalpart$vf@' || lower(dectohex(VF$OUID)) OUID, VF$VERSION VER, COD(PART_STATUS) PART_STATUS "
				+ " ,MD$NUMBER PARTNO, MD$DESC PARTNAME, G_L_CODE, SPEC, PART_SIZE, (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID=GETID(BLOCKNO)) AS B_NO, COD(ORIGIN_DIV) ORIGIN_DIV, COD(SPT) SPT "
				+ ", (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=A.VF$OUID AND ROWNUM=1) HASCHILD"
				+ " FROM NORMALPART$VF A, NORMALPART$ID B WHERE A.VF$OUID=B.ID$LAST "
				+ " AND MD$NUMBER =? AND BLOCKNO = ? ";

		List<Map<String, String>> pickedList = db.queryForList(sql, glCode, blockOuid);
		if (pickedList.isEmpty())
			return null;

		Map<String, String> map = new HashMap<String, String>(pickedList.get(0));
		if ("wip".equals(map.get("VER")))
			return null;
		if (!"Active".equals(BomUtil.NVL(map.get("PART_STATUS"), "")))
			return null;

		return map;
	}

	/** SubaeDao.getListPartOfPart : 하위 전체 구조 (CONNECT BY) */
	public List<Map<String, String>> getListPartOfPart(String partOuid) throws Exception {
		return db.queryForList(
				" SELECT A.SF$OUID, AS$END1, AS$END2, END1_HEXOUID, END2_HEXOUID, CMT, QTY, COLOR, "
				+ " B.MD$NUMBER AS PARTNO, B.MD$DESC AS PARTNAME, B.G_L_CODE, B.SPEC, B.PART_SIZE, C.MD$NUMBER AS B_NO "
				+ " FROM PARTOFPART$AC A "
				+ " LEFT OUTER JOIN NORMALPART$VF B ON AS$END2 = VF$OUID "
				+ " LEFT OUTER JOIN BLOCKNO$SF C ON C.SF$OUID = GETID(B.BLOCKNO) "
				+ " START WITH AS$END1 = ? "
				+ " CONNECT BY PRIOR AS$END2 = AS$END1 ", BomUtil.toRealOuid(partOuid));
	}

	/** SubaeDao.getPartDiv : ORIGIN_DIV 코드 */
	public String getPartDiv(long lPartOuid) throws Exception {
		return db.queryForString(" SELECT COD(ORIGIN_DIV) DIV FROM NORMALPART$VF WHERE VF$OUID = ? ", lPartOuid);
	}

	// ------------------------------------------------------------------------ EL_P

	/** SubaeDao.getEL_PList / getSHIPEL_PList / getSVEL_PList : 최신버전 사양계산 PID 목록 (PID, METHOD) */
	public List<Map<String, String>> getEL_PList(String pidPrefix, boolean isFloorSpec) throws Exception {
		return db.queryForList(
				" SELECT A.PID, A.METHOD FROM VARIANT_H A, VARIANT_ID B "
				+ " WHERE A.PID = B.PID AND A.HOUID = B.LAST_HOUID AND A.PID LIKE ? AND NVL(A.ISFLOORSPEC, 'N') = ? "
				+ " ORDER BY PID ", pidPrefix, isFloorSpec ? "Y" : "N");
	}

	// ------------------------------------------------------------------------ product / ebom

	/**
	 * ProductInfo.setCalculatedBlockOpts : 제품의 option_c ~ option_3 값 (필드명 → 값)
	 * 컬럼이 없는 필드는 결과에 넣지 않는다. (호출측에서 경고)
	 */
	public Map<String, String> getProductBlockOptions(String productOuid) throws Exception {
		Map<String, String> row = db.queryForFirst(" SELECT * FROM PRODUCT$VF WHERE VF$OUID = ? ", BomUtil.toRealOuid(productOuid));
		Map<String, String> result = new HashMap<String, String>();
		if (row == null)
			return result;
		for (String field : BomConsts.BLOCK_OPT_FIELD_LIST) {
			String column = field.toUpperCase();
			if (row.containsKey(column))
				result.put(field, row.get(column));
		}
		return result;
	}

	/** SubaeDao.getMaxSeq : 제품 1레벨 BOM 의 최대 SEQ */
	public int getMaxSeq(long lProdOuid) throws Exception {
		return BomUtil.parseInt(db.queryForString("select nvl(max(to_number(seq)), 0) from partofebom where productouid = ?", lProdOuid));
	}

	/** SubaeDao.getPartOuid : 이미 수배된 1레벨 자재 (PARTOUID 10진수) */
	public List<String> getPartList(long lProdOuid) throws Exception {
		return db.queryForStringList("select PARTOUID from partofebom where productouid = ?", lProdOuid);
	}

	/** SubaeDao.getVariablePartOuid : 이미 수배된 2레벨 공사수량 (ASSOOUID 10진수) */
	public List<String> getVariablePartList(long lProdOuid) throws Exception {
		return db.queryForStringList("select ASSOOUID from variablepart_new where productouid = ?", lProdOuid);
	}

	// ------------------------------------------------------------------------

	private static String placeholders(int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
			sb.append(i == 0 ? "?" : ",?");
		return sb.toString();
	}
}
