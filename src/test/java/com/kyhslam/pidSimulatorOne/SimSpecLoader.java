package com.kyhslam.pidSimulatorOne;

import java.sql.SQLException;
import java.util.*;

/**
 * 공사정보/층정보/제품 로더.
 * 원본은 DOS(dos.get + ConstructionMaster.setDataMap) 로 읽지만, 여기서는 DOS 필드 메타(DOSFLD) 조회 없이
 * 테이블을 직접 읽고 SimConsts 의 규칙으로 dataMap 을 만든다.
 *   - 컬럼명에 '$' 가 있으면 소문자 (md$number ...), md$desc → md$description, 나머지(EL_ ...)는 그대로
 *   - 값이 코드아이템 OUID(DOSCODITM) 이면 description 으로 치환 (setDataMap 과 동일)
 */
public class SimSpecLoader {

	private static final int IN_CHUNK = 500;

	private final SimDb db;
	/** doscoditm ouid → {name, des, coditm}, 없는 코드는 null */
	private final Map<Long, String[]> codeItemCache = new HashMap<Long, String[]>();

	public SimSpecLoader(SimDb db) {
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
	 * searchProject.jsp : 공사번호(md$number)의 wip 공사정보 (elv_info / shipelv_info / monitorstation_info)
	 * @return IOUID, MD$NUMBER, MD$DESC, VF$VERSION (없으면 null)
	 */
	public Map<String, String> findProject(String projectNo) throws SQLException {
		String hex = "LOWER(TO_CHAR(vf$ouid, 'FMxxxxxxxxxxxxxxxx'))"; // 원본의 lower(dectohex(vf$ouid))
		return db.queryForFirst(
				" select '" + SimConsts.PREFIX_ELVINFO_OUID + "'||" + hex + " IOUID, md$number, md$desc, vf$version "
				+ " from elv_info$vf, elv_info$id where vf$identity = id$ouid and vf$Ouid = id$wip and md$Number = ? "
				+ " union "
				+ " select '" + SimConsts.PREFIX_SHIPELVINFO_OUID + "'||" + hex + " IOUID, md$number, md$desc, vf$version "
				+ " from shipelv_info$vf, shipelv_info$id where vf$identity = id$ouid and vf$Ouid = id$wip and md$Number = ? "
				+ " union "
				+ " select '" + SimConsts.PREFIX_SVELVINFO_OUID + "'||" + hex + " IOUID, md$number, md$desc, vf$version "
				+ " from monitorstation_info$vf, monitorstation_info$id where vf$identity = id$ouid and vf$Ouid = id$wip and md$Number = ? ",
				projectNo, projectNo, projectNo);
	}

	/** ProductService.findWipProductByNo 의 HEXOUID (없으면 null) */
	public String findWipProductOuid(String productNo) throws SQLException {
		Map<String, String> row = db.queryForFirst(
				" SELECT A.VF$OUID FROM PRODUCT$VF A, PRODUCT$ID WHERE VF$OUID=ID$WIP AND MD$NUMBER=? ", productNo);
		if (row == null)
			return null;
		return SimConsts.PREFIX_PRODUCT_OUID + SimUtil.deciTohex(row.get("VF$OUID"));
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
			if (SimConsts.EXCLUDE_SPEC_COLUMNS.contains(column))
				continue;
			String key;
			if (SimConsts.COLUMN_MD_DESC.equals(column))
				key = SimConsts.FIELD_MD_DESCRIPTION;
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
				codeNameMap.put("name@" + key, SimConsts.CODE_FIELD_DISPLAY_WITH_ID ? item[0] + " [" + item[2] + "]" : item[0]);
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
	 * SubaeManager.setFloorMasterList() 대체. (BlockSimul.BlockSpecLoader.loadFloors 와 동일)
	 * 층 정보를 읽어 공사정보 데이터로 덮어쓰고(addElvObjectData) md$index 순으로 정렬한다.
	 * 층 테이블/연결 테이블명은 SimConsts 값을 사용한다.
	 * @return 층이 없으면 null
	 */
	public List<HashMap> loadFloors(String elvOuid, Map elvDataMap) throws SQLException {
		if (SimUtil.isNullString(SimConsts.FLOOR_TABLE_CODE) || SimUtil.isNullString(SimConsts.ELVANDFLOOR_ASSO_TABLE_CODE))
			throw new IllegalStateException("층 정보 사용시 SimConsts.FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 를 설정해야 합니다.");

		String floor = SimConsts.FLOOR_TABLE_CODE.toLowerCase();
		String asso = SimConsts.ELVANDFLOOR_ASSO_TABLE_CODE.toLowerCase() + SimConsts.ELVANDFLOOR_ASSO_TABLE_SUFFIX;
		String elvTable = elvOuid.substring(0, elvOuid.indexOf('$')).toLowerCase();
		long elvReal = Long.parseLong(elvOuid.substring(elvOuid.indexOf('@') + 1), 16);
		String elvIds = " (?, (SELECT VF$IDENTITY FROM " + elvTable + "$vf WHERE VF$OUID = ?)) ";

		String sql;
		if (SimConsts.FLOOR_TABLE_VERSIONABLE) {
			sql = " SELECT F.VF$OUID FOUID FROM " + floor + "$vf F, " + floor + "$id I, " + asso + " A "
				+ " WHERE F.VF$IDENTITY = I.ID$OUID AND F.VF$OUID = I.ID$WIP "
				+ "   AND (   (A.AS$END1 IN" + elvIds + " AND A.AS$END2 IN (F.VF$OUID, F.VF$IDENTITY)) "
				+ "        OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 IN (F.VF$OUID, F.VF$IDENTITY)) ) ";
		} else {
			sql = " SELECT F.SF$OUID FOUID FROM " + floor + "$sf F, " + asso + " A "
				+ " WHERE (A.AS$END1 IN" + elvIds + " AND A.AS$END2 = F.SF$OUID) "
				+ "    OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 = F.SF$OUID) ";
		}

		List<Map<String, String>> rows = db.queryForList(sql, elvReal, elvReal, elvReal, elvReal);
		if (rows.isEmpty())
			return null;

		List<HashMap> floors = new ArrayList<HashMap>();
		for (Map<String, String> r : rows) {
			String floorOuid = floor + (SimConsts.FLOOR_TABLE_VERSIONABLE ? "$vf@" : "$sf@") + SimUtil.deciTohex(r.get("FOUID"));
			SpecObject obj = load(floorOuid);
			if (obj == null)
				continue;
			obj.dataMap.putAll(elvDataMap); // FloorMaster.addElvObjectData
			floors.add(obj.dataMap);
		}

		Collections.sort(floors, (p1, p2) -> Integer.compare(
				SimUtil.parseInt(p1.get(SimConsts.FIELD_NAME_INDEX)), SimUtil.parseInt(p2.get(SimConsts.FIELD_NAME_INDEX))));
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
		if (value == null || value.length() < SimConsts.CODE_OUID_MIN_DIGITS || value.length() > 18 || !value.matches("\\d+"))
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
