package com.kyhslam.util.simulate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * 영업사양(공사정보) / 층별정보 로더.
 * 원본은 DOS(dos.get + ConstructionMaster.setDataMap + SubaeManager) 를 통해 읽어오지만,
 * 여기서는 DOS 메타 테이블(dosclas, dossuperclas, dosfld, dosasso, doscoditm)을 직접 조회하여 동일한 dataMap 을 만든다.
 */
public class PidSpecLoader {
	// dyna.framework.iip.IIP 데이터 타입
	private static final int DATATYPE_BOOLEAN = 0x01;
	private static final int DATATYPE_DATETIME = 0x15;
	private static final int DATATYPE_DATE = 0x16;
	private static final int DATATYPE_TIME = 0x17;
	private static final int DATATYPE_CODE = 0x18;
	private static final int DATATYPE_REFERENCE_CODE = 0x19;

	private static final int IN_CHUNK = 500;

	private final PidDb db;
	/** DOS 메타 테이블 스키마 prefix (ex. "HDEL_SYSTEM.") */
	private final String meta;
	/** DOSImpl.CODE_FIELD_DISPLAY_METHOD : true 면 코드값을 "name [codeitemid]" 로 표기 */
	private boolean codeDisplayWithId = true;

	/** doscoditm ouid → {name, des, coditm}, 없는 코드는 null 로 캐싱 */
	private final Map<Long, String[]> codeItemCache = new HashMap<Long, String[]>();
	private final Map<Long, List<FieldDef>> classFieldCache = new HashMap<Long, List<FieldDef>>();

	public PidSpecLoader(PidDb db) {
		this(db, "HDEL_SYSTEM");
	}

	public PidSpecLoader(PidDb db, String metaSchema) {
		this.db = db;
		this.meta = (metaSchema == null || metaSchema.trim().isEmpty()) ? "" : metaSchema.trim() + ".";
	}

	public void setCodeDisplayWithId(boolean codeDisplayWithId) {
		this.codeDisplayWithId = codeDisplayWithId;
	}

	/** DOS 객체 1건의 데이터 */
	public static class SpecObject {
		public final String ouid;
		/** ConstructionMaster.getDataMap() 과 동일 (코드값은 description 으로 치환됨) */
		public final HashMap dataMap;
		/** DOSChangeable 의 "name@필드" 값 (코드 필드의 표시값). Variant.converCodeName 에서 사용 */
		public final HashMap<String, String> codeNameMap;

		SpecObject(String ouid, HashMap dataMap, HashMap<String, String> codeNameMap) {
			this.ouid = ouid;
			this.dataMap = dataMap;
			this.codeNameMap = codeNameMap;
		}
	}

	private static class FieldDef {
		String name;
		String column;
		int type;
		boolean collection;
	}

	/**
	 * 호기의 최신(wip) 영업사양 ouid 조회
	 * (PartCommonUtil.getLatestElvInfoOid + VaultReportController 의 ELV_INFO$VF 조회)
	 */
	public String findLatestElvOuid(String hogi) throws Exception {
		List<Map<String, Object>> latest = db.queryForList(
				" SELECT E.VF$VERSION EVERSION FROM ELV_INFO$VF E, ELV_INFO$ID I "
						+ " WHERE E.VF$IDENTITY = I.ID$OUID AND E.VF$OUID = I.ID$WIP AND E.MD$NUMBER = ? ", hogi);
		if (latest.isEmpty())
			throw new Exception("영업사양(ELV_INFO)이 없습니다. hogi=" + hogi);
		Object version = latest.get(latest.size() - 1).get("EVERSION");

		Map<String, Object> row = db.queryForFirst(
				" SELECT VF$OUID FROM ELV_INFO$VF WHERE MD$NUMBER = ? AND VF$VERSION = ? ", hogi, version);
		if (row == null)
			throw new Exception("영업사양(ELV_INFO)이 없습니다. hogi=" + hogi + ", version=" + version);

		return PidConsts.PREFIX_ELVINFO_OUID + Long.toHexString(toLong(row.get("VF$OUID")));
	}

	/**
	 * DOS.get(ouid) + ConstructionMaster.setDataMap() 대체
	 * @param ouid ex) elv_info$vf@a810066d
	 * @return 객체가 없으면 null
	 */
	public SpecObject load(String ouid) throws Exception {
		if (!ouid.toLowerCase().startsWith(PidConsts.PREFIX_ELVINFO_OUID))
			throw new IllegalArgumentException("영업사양(elv_info) ouid 가 아닙니다 : " + ouid);
		return load(ouid, PidConsts.ELVINFO_CLASS_OUID, PidConsts.ELVINFO_SUPER_CLASS_OUIDS);
	}

	/**
	 * @param classOuidHex 클래스 OUID (16진수, ex. 860cebeb)
	 * @param superClassOuidHex 상위 클래스 OUID 목록 (16진수)
	 */
	private SpecObject load(String ouid, String classOuidHex, String[] superClassOuidHex) throws Exception {
		int dollar = ouid.indexOf('$');
		int at = ouid.indexOf('@');
		if (dollar < 0 || at < 0)
			throw new IllegalArgumentException("잘못된 ouid : " + ouid);

		String classCode = ouid.substring(0, dollar).toLowerCase();
		boolean versionable = ouid.substring(dollar + 1, at).toLowerCase().startsWith("vf");
		long realOuid = Long.parseLong(ouid.substring(at + 1), 16);

		long classOuid = Long.parseLong(classOuidHex, 16);

		List<FieldDef> fields = new ArrayList<FieldDef>();
		for (FieldDef f : getFields(classOuid, superClassOuidHex)) {
			if (!f.collection) // collection 필드는 setDataMap 에서 사용되지 않음
				fields.add(f);
		}
		if (fields.isEmpty())
			throw new Exception("No fields. (ouid:" + ouid + ")");

		StringBuilder sql = new StringBuilder("SELECT ");
		for (int i = 0; i < fields.size(); i++) {
			FieldDef f = fields.get(i);
			if (i > 0) sql.append(", ");
			if (f.type == DATATYPE_DATETIME)
				sql.append("TO_CHAR(TO_DATE(").append(f.column).append(",'YYYYMMDDHH24MISS'),'YYYY-MM-DD HH24:MI:SS')");
			else if (f.type == DATATYPE_DATE)
				sql.append("TO_CHAR(TO_DATE(").append(f.column).append(",'YYYYMMDD'),'YYYY-MM-DD')");
			else if (f.type == DATATYPE_TIME)
				sql.append("TO_CHAR(TO_DATE(").append(f.column).append(",'HH24MISS'),'HH24:MI:SS')");
			else
				sql.append(f.column);
			sql.append(" C").append(i);
		}
		sql.append(" FROM ").append(classCode).append(versionable ? "$vf WHERE vf$ouid = ?" : "$sf WHERE sf$ouid = ?");

		Map<String, Object> row = db.queryForFirst(sql.toString(), realOuid);
		if (row == null)
			return null;

		// 1. DOSChangeable valueMap 구성
		LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();
		HashMap<String, String> codeNameMap = new HashMap<String, String>();
		Map<String, Long> codeFieldOuid = new HashMap<String, Long>();
		for (int i = 0; i < fields.size(); i++) {
			FieldDef f = fields.get(i);
			Object v = row.get("C" + i);
			if ((f.type == DATATYPE_CODE || f.type == DATATYPE_REFERENCE_CODE) && v != null) {
				codeFieldOuid.put(f.name, toLong(v));
			}
			valueMap.put(f.name, v);
		}
		valueMap.put("ouid", ouid);

		// 2. 코드 아이템 일괄 조회 (setDataMap 은 모든 문자열 값에 대해 dos.getCodeItem 을 시도한다)
		Set<Long> candidates = new HashSet<Long>(codeFieldOuid.values());
		for (Map.Entry<String, Object> e : valueMap.entrySet()) {
			Long l = parseHex(asString(e.getValue()));
			if (l != null) candidates.add(l);
		}
		loadCodeItems(candidates);

		for (Map.Entry<String, Long> e : codeFieldOuid.entrySet()) {
			String[] item = codeItemCache.get(e.getValue());
			if (item != null) {
				valueMap.put(e.getKey(), Long.toHexString(e.getValue()));
				codeNameMap.put("name@" + e.getKey(), codeDisplayWithId ? item[0] + " [" + item[2] + "]" : item[0]);
			} else {
				valueMap.put(e.getKey(), null);
			}
		}

		// 3. ConstructionMaster.setDataMap
		HashMap dataMap = new HashMap();
		for (Map.Entry<String, Object> e : valueMap.entrySet()) {
			String key = e.getKey().trim();
			if (key.indexOf("name@") != -1)
				continue;

			Object raw = e.getValue();
			String value = (raw instanceof Boolean) ? "" : asString(raw);
			if (value != null)
				value = value.trim();

			if (value == null || value.isEmpty()) {
				dataMap.put(key, "");
			} else {
				Long l = parseHex(value);
				String[] item = (l == null) ? null : codeItemCache.get(l);
				dataMap.put(key, item != null ? item[1] : value);
			}
		}

		return new SpecObject(ouid, dataMap, codeNameMap);
	}

	/**
	 * SubaeManager.setFloorMasterList() 대체.
	 * 공사정보에 연결된 층 정보를 읽어 공사정보 데이터를 덮어쓰고(addElvObjectData) md$index 순으로 정렬한다.
	 */
	public List<SpecObject> loadFloors(String elvOuid, Map elvDataMap) throws Exception {
		// DOSCLAS / DOSASSO 대신 상수 사용 (PidConsts)
		if (PidUtil.NVL(PidConsts.FLOOR_TABLE_CODE, "").trim().isEmpty() || PidUtil.NVL(PidConsts.ELVANDFLOOR_ASSO_TABLE_CODE, "").trim().isEmpty())
			throw new IllegalStateException("층 정보 사용 시 PidConsts.FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 를 설정해야 합니다.");
		String floorCode = PidConsts.FLOOR_TABLE_CODE.trim().toLowerCase().replace(' ', '_');
		String assoCode = PidConsts.ELVANDFLOOR_ASSO_TABLE_CODE.trim().toLowerCase().replace(' ', '_');

		String elvTable = elvOuid.substring(0, elvOuid.indexOf('$')).toLowerCase();
		long elvReal = Long.parseLong(elvOuid.substring(elvOuid.indexOf('@') + 1), 16);
		String elvIds = " (?, (SELECT VF$IDENTITY FROM " + elvTable + "$vf WHERE VF$OUID = ?)) ";

		List<String> floorOuidList = new ArrayList<String>();
		if (PidConsts.FLOOR_TABLE_VERSIONABLE) {
			// 층 클래스가 버전관리(vf) 인 경우 : wip 버전만
			String sql = " SELECT F.VF$OUID FOUID FROM " + floorCode + "$vf F, " + floorCode + "$id I, " + assoCode + "$ac A "
					+ " WHERE F.VF$IDENTITY = I.ID$OUID AND F.VF$OUID = I.ID$WIP "
					+ "   AND (   (A.AS$END1 IN" + elvIds + " AND A.AS$END2 IN (F.VF$OUID, F.VF$IDENTITY)) "
					+ "        OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 IN (F.VF$OUID, F.VF$IDENTITY)) ) ";
			for (Map<String, Object> r : db.queryForList(sql, elvReal, elvReal, elvReal, elvReal))
				floorOuidList.add(floorCode + "$vf@" + Long.toHexString(toLong(r.get("FOUID"))));
		} else {
			// 버전관리 안하는 클래스(sf)
			String sql = " SELECT F.SF$OUID FOUID FROM " + floorCode + "$sf F, " + assoCode + "$ac A "
					+ " WHERE (A.AS$END1 IN" + elvIds + " AND A.AS$END2 = F.SF$OUID) "
					+ "    OR (A.AS$END2 IN" + elvIds + " AND A.AS$END1 = F.SF$OUID) ";
			for (Map<String, Object> r : db.queryForList(sql, elvReal, elvReal, elvReal, elvReal))
				floorOuidList.add(floorCode + "$sf@" + Long.toHexString(toLong(r.get("FOUID"))));
		}

		List<SpecObject> floors = new ArrayList<SpecObject>();
		for (String floorOuid : floorOuidList) {
			SpecObject floor = load(floorOuid, PidConsts.FLOOR_CLASS_OUID, PidConsts.FLOOR_SUPER_CLASS_OUIDS);
			if (floor == null)
				continue;
			floor.dataMap.putAll(elvDataMap); // FloorMaster.addElvObjectData
			floors.add(floor);
		}

		Collections.sort(floors, (p1, p2) -> Integer.compare(
				PidUtil.parseInt(p1.dataMap.get("md$index")), PidUtil.parseInt(p2.dataMap.get("md$index"))));
		return floors;
	}

	// ------------------------------------------------------------------------

	/** DOSFieldHelper.listFieldInClassInternal : 자기 클래스 → 상위 클래스 순, 같은 name 은 먼저 나온 것 사용 */
	private List<FieldDef> getFields(long classOuid, String[] superClassOuidHex) throws SQLException {
		List<FieldDef> cached = classFieldCache.get(classOuid);
		if (cached != null)
			return cached;

		List<Long> classList = new ArrayList<Long>();
		classList.add(classOuid);
		for (String superOuid : superClassOuidHex) // DOSSUPERCLAS 대신 상수 사용 (PidConsts)
			classList.add(Long.parseLong(superOuid.trim(), 16));

		LinkedHashMap<String, FieldDef> fieldMap = new LinkedHashMap<String, FieldDef>();
		for (Long clas : classList) {
			for (Map<String, Object> r : db.queryForList(
					" SELECT NAME, CODE, TYPE, MULTFROM, MULTTO FROM " + meta + "DOSFLD WHERE DOSCLAS = ? ", clas)) {
				FieldDef f = new FieldDef();
				f.name = asString(r.get("NAME"));
				if (f.name == null || fieldMap.containsKey(f.name))
					continue;
				String code = asString(r.get("CODE")).toLowerCase().replace(' ', '_');
				f.column = "md$description".equals(code) ? "md$desc" : code;
				f.type = PidUtil.parseInt(r.get("TYPE"));
				int from = PidUtil.parseInt(r.get("MULTFROM"));
				int to = PidUtil.parseInt(r.get("MULTTO"));
				f.collection = from < to && (to > 1 || (to - from) > 1);
				fieldMap.put(f.name, f);
			}
		}

		List<FieldDef> result = new ArrayList<FieldDef>(fieldMap.values());
		classFieldCache.put(classOuid, result);
		return result;
	}

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
			for (Map<String, Object> r : db.queryForList(sql.toString(), chunk.toArray())) {
				codeItemCache.put(toLong(r.get("OUID")),
						new String[] { asString(r.get("NAME")), asString(r.get("DES")), asString(r.get("CODITM")) });
			}
		}
	}

	/** DOSCodeItemDatabaseMapper.getCodeItem 과 동일하게 16진수로 파싱되는 값만 코드 후보로 본다 */
	private static Long parseHex(String value) {
		if (value == null || value.isEmpty())
			return null;
		try {
			return Long.parseLong(value, 16);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String asString(Object o) {
		if (o == null)
			return null;
		if (o instanceof BigDecimal)
			return ((BigDecimal) o).toPlainString();
		return o.toString();
	}

	private static long toLong(Object o) {
		if (o instanceof Number)
			return ((Number) o).longValue();
		return new BigDecimal(o.toString().trim()).longValue();
	}
}
