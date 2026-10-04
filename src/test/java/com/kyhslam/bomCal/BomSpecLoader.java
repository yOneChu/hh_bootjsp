package com.kyhslam.bomCal;

import java.sql.SQLException;
import java.util.*;

/**
 * 공사정보/층정보/제품/파트 로더.
 * 원본은 DOS(dos.get + ConstructionMaster.setDataMap) 로 읽지만, 여기서는 테이블을 직접 읽고 BomConsts 의 규칙으로 dataMap 을 만든다.
 * (층/제품 연결 테이블 코드는 DOS 메타 테이블 dosclas, dosasso 대신 BomConsts 상수를 사용한다)
 *   - 컬럼명에 '$' 가 있으면 소문자 (md$number ...), md$desc → md$description, 나머지(EL_ ...)는 그대로
 *   - 값이 코드아이템 OUID(DOSCODITM) 이면 description 으로 치환 (setDataMap 과 동일)
 */
public class BomSpecLoader {

	private static final int IN_CHUNK = 500;

	private final BomDb db;
	/** doscoditm ouid → {name, des, coditm}, 없는 코드는 null */
	private final Map<Long, String[]> codeItemCache = new HashMap<Long, String[]>();

	public BomSpecLoader(BomDb db) {
		this.db = db;
	}

	/** DOS 객체 1건 */
	public static class SpecObject {
		public final String ouid;
		/** ConstructionMaster.getDataMap() */
		public final HashMap dataMap;
		/** DOSChangeable 의 "name@필드" (코드 필드 표시값) */
		public final HashMap<String, String> codeNameMap;

		SpecObject(String ouid, HashMap dataMap, HashMap<String, String> codeNameMap) {
			this.ouid = ouid;
			this.dataMap = dataMap;
			this.codeNameMap = codeNameMap;
		}
	}

	/**
	 * ElvService.getConsOuid : 호기의 wip 공사정보 ouid (wip 가 없으면 승인된 최신 버전, 없으면 null)
	 * bomCalculate 는 서비스(monitorstation_info) 공사정보도 대상이므로 추가로 조회한다.
	 */
	public String getConsOuid(String productNo) throws SQLException {
		Map<String, String> row = db.queryForFirst(
				" select * from ( "
				+ " select 'elv_info$vf@' P, vf$ouid O from elv_info$vf, elv_info$id where vf$identity=id$ouid and vf$ouid=nvl(id$wip,id$last) and md$Number=? "
				+ " union all "
				+ " select 'shipelv_info$vf@' P, vf$ouid O from shipelv_info$vf, shipelv_info$id where vf$identity=id$ouid and vf$ouid=nvl(id$wip,id$last) and md$Number=? "
				+ " ) where rownum=1 ", productNo, productNo);
		if (row != null)
			return row.get("P") + BomUtil.deciTohex(row.get("O"));

		try {
			row = db.queryForFirst(
					" select vf$ouid O from monitorstation_info$vf, monitorstation_info$id where vf$identity=id$ouid and vf$ouid=nvl(id$wip,id$last) and md$Number=? and rownum=1 ",
					productNo);
		} catch (SQLException e) {
			return null; // 서비스 공사정보 테이블이 없는 환경
		}
		return row == null ? null : BomConsts.PREFIX_SVELVINFO_OUID + BomUtil.deciTohex(row.get("O"));
	}

	/** dos.getStatus(ouid) 대체 : md$status 컬럼 값 (ex. WIP, RLS). 참고용 */
	public String getStatus(String ouid) throws SQLException {
		String table = ouid.substring(0, ouid.indexOf('$')).toLowerCase();
		long real = Long.parseLong(ouid.substring(ouid.indexOf('@') + 1), 16);
		Map<String, String> row = db.queryForFirst(" SELECT * FROM " + table + "$vf WHERE vf$ouid = ? ", real);
		if (row == null)
			return null;
		for (String col : new String[] { "MD$STATUS", "VF$STATUS" }) {
			if (row.containsKey(col))
				return row.get(col);
		}
		return null;
	}

	/**
	 * ProductInfo.getLinkedProductOuid : 공사정보에 연결된 wip 제품 (없으면 null)
	 * dos.listLinkFrom(elvOuid, ELVANDPRODUCT 연결, wip) 대체
	 */
	public String findLinkedProductOuid(String elvOuid) throws SQLException {
		// DOSASSO 대신 상수 사용 (BomConsts)
		String asso = tableCode(BomConsts.ELVANDPRODUCT_ASSO_TABLE_CODE);
		if (asso == null)
			return null;

		String elvTable = elvOuid.substring(0, elvOuid.indexOf('$')).toLowerCase();
		long elvReal = Long.parseLong(elvOuid.substring(elvOuid.indexOf('@') + 1), 16);
		String elvIds = " (?, (SELECT VF$IDENTITY FROM " + elvTable + "$vf WHERE VF$OUID = ?)) ";

		String sql = " SELECT P.VF$OUID POUID FROM PRODUCT$VF P, PRODUCT$ID I, " + asso + "$ac A "
				+ " WHERE P.VF$IDENTITY = I.ID$OUID AND P.VF$OUID = NVL(I.ID$WIP, I.ID$LAST) "
				+ "   AND (   (A.AS$END1 IN" + elvIds + " AND A.AS$END2 IN (P.VF$OUID, P.VF$IDENTITY)) "
				+ "        OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 IN (P.VF$OUID, P.VF$IDENTITY)) ) ";
		Map<String, String> row = db.queryForFirst(sql, elvReal, elvReal, elvReal, elvReal);
		return row == null ? null : BomConsts.PREFIX_PRODUCT_OUID + BomUtil.deciTohex(row.get("POUID"));
	}

	/** BomConsts 의 테이블 코드 상수 정리 (xxx → xxx$vf / xxx$ac). 비어 있으면 null */
	private static String tableCode(String code) {
		String c = BomUtil.NVL(code, "").trim();
		return c.isEmpty() ? null : c.toLowerCase().replace(' ', '_');
	}

	/** ProductService.findWipProductByNo 의 HEXOUID (wip 가 없으면 승인된 최신 버전, 없으면 null) */
	public String findWipProductOuid(String productNo) throws SQLException {
		Map<String, String> row = db.queryForFirst(
				" SELECT A.VF$OUID FROM PRODUCT$VF A, PRODUCT$ID B WHERE A.VF$IDENTITY=B.ID$OUID AND A.VF$OUID=NVL(B.ID$WIP, B.ID$LAST) AND A.MD$NUMBER=? ", productNo);
		if (row == null)
			return null;
		return BomConsts.PREFIX_PRODUCT_OUID + BomUtil.deciTohex(row.get("VF$OUID"));
	}

	/** dos.get(normalpart ouid) 중 시뮬레이션에 필요한 필드 */
	public Map<String, String> getPartInfo(String partOuid) throws SQLException {
		long real = Long.parseLong(partOuid.substring(partOuid.indexOf('@') + 1), 16);
		Map<String, String> row = db.queryForFirst(
				" SELECT MD$NUMBER, BLOCKNO_NUMBER, G_L_CODE, SPEC, PART_SIZE FROM NORMALPART$VF WHERE VF$OUID = ? ", real);
		Map<String, String> result = new HashMap<String, String>();
		if (row != null) {
			result.put("md$number", row.get("MD$NUMBER"));
			result.put("blockno_number", row.get("BLOCKNO_NUMBER"));
			result.put("g_l_code", row.get("G_L_CODE"));
			result.put("spec", row.get("SPEC"));
			result.put("part_size", row.get("PART_SIZE"));
		}
		result.put("ouid", partOuid);
		return result;
	}

	/**
	 * DOS.get(ouid) + ConstructionMaster.setDataMap() 대체
	 * @param ouid ex) elv_info$vf@a810066d
	 * @return 객체가 없으면 null
	 */
	public SpecObject load(String ouid) throws SQLException {
		int dollar = ouid.indexOf('$');
		int at = ouid.indexOf('@');
		String table = ouid.substring(0, dollar).toLowerCase();
		boolean versionable = ouid.substring(dollar + 1, at).toLowerCase().startsWith("vf");
		long real = Long.parseLong(ouid.substring(at + 1), 16);

		Map<String, String> row = db.queryForFirst(
				" SELECT * FROM " + table + (versionable ? "$vf WHERE vf$ouid = ?" : "$sf WHERE sf$ouid = ?"), real);
		if (row == null)
			return null;

		// 1. 컬럼 → 필드명, 값 정리
		LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> e : row.entrySet()) {
			String column = e.getKey().toLowerCase();
			if (BomConsts.EXCLUDE_SPEC_COLUMNS.contains(column))
				continue;
			String key;
			if (BomConsts.COLUMN_MD_DESC.equals(column))
				key = BomConsts.FIELD_MD_DESCRIPTION;
			else if (column.indexOf('$') != -1)
				key = column;
			else
				key = e.getKey();
			valueMap.put(key, e.getValue() == null ? "" : e.getValue().trim());
		}

		// 2. 코드아이템 일괄 조회 : 코드필드(10진 OUID) 와 setDataMap 의 16진 변환 후보 모두
		Set<Long> candidates = new HashSet<Long>();
		for (String v : valueMap.values()) {
			Long dec = parseCodeOuid(v);
			if (dec != null) candidates.add(dec);
			Long hex = parseHex(v);
			if (hex != null) candidates.add(hex);
		}
		loadCodeItems(candidates);

		// 3. setDataMap
		HashMap dataMap = new HashMap();
		HashMap<String, String> codeNameMap = new HashMap<String, String>();
		for (Map.Entry<String, String> e : valueMap.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();
			if (value.isEmpty()) {
				dataMap.put(key, "");
				continue;
			}

			Long dec = parseCodeOuid(value);
			String[] item = dec == null ? null : codeItemCache.get(dec);
			if (item != null) { // 코드 필드
				dataMap.put(key, item[1]);
				codeNameMap.put("name@" + key, BomConsts.CODE_FIELD_DISPLAY_WITH_ID ? item[0] + " [" + item[2] + "]" : item[0]);
				continue;
			}

			Long hex = parseHex(value);
			item = hex == null ? null : codeItemCache.get(hex);
			dataMap.put(key, item != null ? item[1] : value);
		}
		dataMap.put("ouid", ouid);

		return new SpecObject(ouid, dataMap, codeNameMap);
	}

	/**
	 * SubaeManager.setFloorMasterList() 대체.
	 * 층 정보를 읽어 공사정보 데이터로 덮어쓰고(addElvObjectData) md$index 순으로 정렬한다.
	 * 층/연결 테이블 코드는 DOS 메타 테이블(dosclas, dosasso)에서 찾는다. (pidSimul.PidSpecLoader.loadFloors 와 동일)
	 * @return 층이 없으면 null (원본 getFloorOuidList 와 동일)
	 */
	public List<HashMap> loadFloors(String elvOuid, Map elvDataMap) throws Exception {
		// DOSCLAS / DOSASSO 대신 상수 사용 (BomConsts)
		String asso = tableCode(BomConsts.ELVANDFLOOR_ASSO_TABLE_CODE);
		String floor = tableCode(BomConsts.FLOOR_TABLE_CODE);
		if (asso == null || floor == null) {
			System.err.println("[WARN] BomConsts.FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 가 비어 있어 층 정보를 읽지 않습니다. (층별 블록 미계산)");
			return null;
		}

		String elvTable = elvOuid.substring(0, elvOuid.indexOf('$')).toLowerCase();
		long elvReal = Long.parseLong(elvOuid.substring(elvOuid.indexOf('@') + 1), 16);
		String elvIds = " (?, (SELECT VF$IDENTITY FROM " + elvTable + "$vf WHERE VF$OUID = ?)) ";

		List<String> floorOuidList = new ArrayList<String>();
		if (BomConsts.FLOOR_TABLE_VERSIONABLE) {
			// 층 클래스가 버전관리(vf) 인 경우 : wip 버전 (없으면 최신 버전)
			String sql = " SELECT F.VF$OUID FOUID FROM " + floor + "$vf F, " + floor + "$id I, " + asso + "$ac A "
				+ " WHERE F.VF$IDENTITY = I.ID$OUID AND F.VF$OUID = NVL(I.ID$WIP, I.ID$LAST) "
				+ "   AND (   (A.AS$END1 IN" + elvIds + " AND A.AS$END2 IN (F.VF$OUID, F.VF$IDENTITY)) "
				+ "        OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 IN (F.VF$OUID, F.VF$IDENTITY)) ) ";
			for (Map<String, String> r : db.queryForList(sql, elvReal, elvReal, elvReal, elvReal))
				floorOuidList.add(floor + "$vf@" + BomUtil.deciTohex(r.get("FOUID")));
		} else {
			// 버전관리 안하는 클래스(sf)
			String sql = " SELECT F.SF$OUID FOUID FROM " + floor + "$sf F, " + asso + "$ac A "
				+ " WHERE (A.AS$END1 IN" + elvIds + " AND A.AS$END2 = F.SF$OUID) "
				+ "    OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 = F.SF$OUID) ";
			for (Map<String, String> r : db.queryForList(sql, elvReal, elvReal, elvReal, elvReal))
				floorOuidList.add(floor + "$sf@" + BomUtil.deciTohex(r.get("FOUID")));
		}

		if (floorOuidList.isEmpty())
			return null;

		List<HashMap> floors = new ArrayList<HashMap>();
		for (String floorOuid : floorOuidList) {
			SpecObject obj = load(floorOuid);
			if (obj == null)
				continue;
			obj.dataMap.putAll(elvDataMap); // FloorMaster.addElvObjectData
			floors.add(obj.dataMap);
		}

		Collections.sort(floors, (p1, p2) -> Integer.compare(
				BomUtil.parseInt(p1.get(BomConsts.FIELD_NAME_INDEX)), BomUtil.parseInt(p2.get(BomConsts.FIELD_NAME_INDEX))));
		return floors;
	}

	// ------------------------------------------------------------------------

	private void loadCodeItems(Set<Long> ouids) throws SQLException {
		List<Long> todo = new ArrayList<Long>();
		for (Long l : ouids) {
			if (!codeItemCache.containsKey(l))
				todo.add(l);
		}

		for (int start = 0; start < todo.size(); start += IN_CHUNK) {
			List<Long> chunk = todo.subList(start, Math.min(start + IN_CHUNK, todo.size()));
			StringBuilder sql = new StringBuilder(" SELECT OUID, NAME, DES, CODITM FROM DOSCODITM WHERE OUID IN (");
			for (int i = 0; i < chunk.size(); i++)
				sql.append(i == 0 ? "?" : ",?");
			sql.append(")");

			for (Long l : chunk)
				codeItemCache.put(l, null);
			for (Map<String, String> r : db.queryForList(sql.toString(), chunk.toArray())) {
				codeItemCache.put(Long.parseLong(r.get("OUID")), new String[] { r.get("NAME"), r.get("DES"), r.get("CODITM") });
			}
		}
	}

	/** 코드필드 값 (DOSCODITM.OUID 10진수) 후보 */
	private static Long parseCodeOuid(String value) {
		if (value == null || value.length() < BomConsts.CODE_OUID_MIN_DIGITS || value.length() > 18 || !value.matches("\\d+"))
			return null;
		return Long.parseLong(value);
	}

	/** DOSCodeItemDatabaseMapper.getCodeItem : 16진수로 파싱되는 값 */
	private static Long parseHex(String value) {
		if (value == null || value.isEmpty())
			return null;
		try {
			return Long.parseLong(value, 16);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
