package com.kyhslam.util.simulate;

import com.kyhslam.util.simulate.BlockInfo.PickInfo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * dyna.plmetc.subae.model.SubaeManager 의 bomSimulate() 관련 부분만 옮긴 클래스
 */
@Slf4j
public class BlockSubaeManager {

	private final BlockContext ctx;
	private final String elvOuid;

	private HashMap elvDataMap;
	private List<HashMap> floorMasterList;

	private Map<String, String[]> tempVariableMap;
	private ArrayList<String> computedPartList;
	private Map<String, Map<String, String>> tempVariablePartMap;
	/** 시뮬레이션 전용 공사수량 Collection */
	private Map<String, List<Map<String, String>>> variablePartMap4Simulate;

	/** bomSimulate 에서는 CAL_BOM_SUPPLIER_DESIGN 을 실행하지 않으므로 항상 비어 있음 */
	private final List<String> bno_list_all = new ArrayList<String>();
	private final List<String> bno_list_all_check = new ArrayList<String>();

	/** partOuid → 하위 BOM (같은 파트가 여러 블럭/층에서 pick 되어도 한 번만 조회, 인스턴스 = 요청 1건의 호기 1건) */
	private final Map<String, List<Map<String, String>>> partOfPartCache = new HashMap<>();
	private int partOfPartHit = 0;

	/** 요청한 블럭번호 목록 */
	private List<String> requestBlockList = Collections.emptyList();
	/** 고른 버전(테스트 / 지정 버전)으로 계산할 EL_P 블럭 PID (EL_P + 블럭번호) */
	private Set<String> elpTestPids = Collections.emptySet();

	public BlockSubaeManager(BlockContext ctx, String elvOuid) {
		this.ctx = ctx;
		this.elvOuid = elvOuid;
	}

	public Map<String, List<Map<String, String>>> getVariablePartMap4Simulate() {
		return variablePartMap4Simulate;
	}

	/**
	 * SubaeManager.bomSimulate
	 * @return partOuid → {cmt, qty, color, partNo, blockNo}
	 */
	public Map<String, String[]> bomSimulate(List<String> blockList) throws Exception {
		List<BlockInfo> blockInfoList = null;
		List<BlockInfo> floorBlockInfoList = null;

		long t = System.currentTimeMillis();
		BlockSpecLoader.SpecObject elv = ctx.getSpecLoader().load(elvOuid);
		if (elv == null)
			throw new Exception("공사정보가 없습니다. ouid=" + elvOuid);
		elvDataMap = elv.dataMap;

		if (elvOuid.startsWith(BlockConsts.PREFIX_ELVINFO_OUID)) {
			// pidSimul 과 같이 층 정보는 요청한 경우에만 읽는다 (BlockConsts.USE_FLOOR)
			if (BlockConsts.USE_FLOOR)
				floorMasterList = ctx.getSpecLoader().loadFloors(elvOuid, elvDataMap);
			t = lap("사양 로드", t);
			blockInfoList = findBLocksByNo(blockList, false);
			floorBlockInfoList = findBLocksByNo(blockList, true);
		} else {
			t = lap("사양 로드", t);
			blockInfoList = findBLocksByNo(blockList, null);
		}
		t = lap("블럭 조회 (블럭 " + blockInfoList.size() + "개, 층블럭 " + (floorBlockInfoList == null ? 0 : floorBlockInfoList.size()) + "개)", t);

		this.requestBlockList = blockList;
		if (elvOuid.startsWith(BlockConsts.PREFIX_ELVINFO_OUID))
			calculate_EL_P(BlockConsts.EL_P_PREFIX, true);
		else if (elvOuid.startsWith(BlockConsts.PREFIX_SHIPELVINFO_OUID))
			calculate_EL_P(BlockConsts.SHIPEL_P_PREFIX, false);
		else if (elvOuid.startsWith(BlockConsts.PREFIX_SVELVINFO_OUID))
			calculate_EL_P(BlockConsts.SVEL_P_PREFIX, false);
		t = lap("EL_P 계산 (층 " + (floorMasterList == null ? 0 : floorMasterList.size()) + "개)", t);

		this.tempVariableMap = new LinkedHashMap<>();
		this.computedPartList = new ArrayList<>();
		this.tempVariablePartMap = new LinkedHashMap<>();
		this.variablePartMap4Simulate = new LinkedHashMap<>();

		// 입력 블럭의 PICK/PID : 블럭 PID 버전(테스트 / 지정 버전)을 골랐으면 그 저장소로
		ctx.useBlockPid(ctx.isBlockPidSelected());
		try {
			pickAndCalculatePid(blockInfoList, floorBlockInfoList);
		} finally {
			ctx.useBlockPid(false);
		}
		lap("블럭 PICK/PID 계산 합계 (하위BOM 조회 " + partOfPartCache.size() + "건, 재사용 " + partOfPartHit + "건)", t);

		return tempVariableMap;
	}

	/** 구간 소요시간 로그 후 현재 시각 반환 */
	private long lap(String step, long start) {
		long now = System.currentTimeMillis();
		log.info("[simulateBlock] {} - {} : {}ms", elvOuid, step, now - start);
		return now;
	}

	// ------------------------------------------------------------------------ EL_P

	/** SubaeManager.calculate_EL_P / calculate_SHIPEL_P / calculate_SVEL_P + EL_P */
	private void calculate_EL_P(String pidPrefix, boolean withFloor) throws Exception {
		List<Map> floorMaps = toMapList(floorMasterList);

		// 'EL_P블럭 PID 버전' : EL_P(SH_P, SV_P) + 입력 블럭번호 PID 만 고른 버전(테스트 / 지정 버전), 나머지 EL_P 는 최신
		elpTestPids = new HashSet<String>();
		if (ctx.isElpPidSelected()) {
			String head = pidPrefix.replace("%", "");
			for (String blockNo : requestBlockList)
				elpTestPids.add(head + blockNo);
			log.info("[simulateBlock] {} - EL_P 버전({}) 대상 : {}", elvOuid, ctx.getElpVersion(), elpTestPids);
		}

		make_EL_P_Data(elvDataMap, floorMaps, getEL_PList(pidPrefix, false));

		if (!withFloor || floorMasterList == null || floorMasterList.isEmpty())
			return;

		List<Map<String, String>> floorPidList = getEL_PList(pidPrefix, true);
		for (HashMap floorDataMap : floorMasterList) {
			make_EL_P_Data(floorDataMap, floorMaps, floorPidList);
		}
	}

	private List<Map<String, String>> getEL_PList(String pidPrefix, boolean isFloorSpec) throws Exception {
		List<Map<String, String>> list = ctx.getDb().queryForList(
				" SELECT A.PID, A.METHOD FROM VARIANT_H A, VARIANT_ID B "
				+ " WHERE A.PID = B.PID AND A.HOUID = B.LAST_HOUID AND A.PID LIKE ? AND NVL(A.ISFLOORSPEC, 'N') = ? "
				+ " ORDER BY PID ", pidPrefix, isFloorSpec ? "Y" : "N");

		// 대상 EL_P 블럭 PID 가 아직 고른 버전(주로 테스트 버전)만 있는 신규 PID 면 목록에 추가 (PID 순서 유지)
		if (elpTestPids.isEmpty())
			return list;
		Set<String> present = list.stream().map(m -> m.get("PID")).collect(Collectors.toSet());
		boolean added = false;
		for (String pid : elpTestPids) {
			if (present.contains(pid))
				continue;
			Map<String, String> row = ctx.getDb().queryForFirst(
					" SELECT A.PID, A.METHOD FROM VARIANT_H A WHERE A.PID = ? AND A.VERSION = ? AND NVL(A.ISFLOORSPEC, 'N') = ? ORDER BY A.HOUID DESC ",
					pid, String.valueOf(ctx.getElpVersion()), isFloorSpec ? "Y" : "N");
			if (row != null) {
				list = new ArrayList<>(list);
				list.add(row);
				added = true;
			}
		}
		if (added)
			list.sort(Comparator.comparing(m -> m.get("PID")));
		return list;
	}

	/** EL_P.make_EL_P_Data : EL_P PID 들의 OUTPUT 값을 사양에 추가 */
	private void make_EL_P_Data(HashMap dataInfoMap, List<Map> floorMaps, List<Map<String, String>> pidList) {
		BlockVariant variant = new BlockVariant(ctx, dataInfoMap, floorMaps);
		HashMap<String, String> resultMap = new HashMap<String, String>();

		for (Map<String, String> pidMap : pidList) {
			String pid = pidMap.get("PID");
			String method = pidMap.get("METHOD");

			PidVariantMap localMap = null;
			ctx.useElpPid(elpTestPids.contains(pid));
			try {
				localMap = variant.calcVariantPID(pid, null);
			} catch (Exception e) {
				System.err.println(pid + "(" + method + ") : " + e.getMessage());
			} finally {
				ctx.useElpPid(false);
			}

			if (localMap != null) {
				Iterator<String> it = localMap.getOUTPUTMap().keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					resultMap.put(key, BlockUtil.NVL(localMap.get(key), ""));
				}
			}
		}

		dataInfoMap.putAll(resultMap);
	}

	// ------------------------------------------------------------------------ pick

	private void pickAndCalculatePid(List<BlockInfo> blockList, List<BlockInfo> floorBlockList) throws Exception {
		List<Map> floorMaps = toMapList(floorMasterList);

		// Common Block Pick & PID Calculate
		long t = System.currentTimeMillis();
		{
			BlockVariableAction variableAction = new BlockVariableAction(ctx, elvDataMap, floorMaps);
			for (BlockInfo blockInfo : blockList) {
				long b = System.currentTimeMillis();
				findBom(elvDataMap, variableAction, blockInfo);
				log.debug("[simulateBlock] {} - 블럭 {} (pick {}개) : {}ms", elvOuid, blockInfo.getBlockNo(),
						blockInfo.getPickList().size(), System.currentTimeMillis() - b);
			}
		}
		t = lap("공통 블럭 PICK/PID (" + blockList.size() + "개)", t);

		// Floor Block Pick & PID Calculate
		if (floorMasterList != null) {
			for (HashMap floorDataMap : floorMasterList) {
				BlockVariableAction variableAction = new BlockVariableAction(ctx, floorDataMap, floorMaps);
				for (BlockInfo blockInfo : floorBlockList)
					findBom(floorDataMap, variableAction, blockInfo);
			}
			lap("층 블럭 PICK/PID (층 " + floorMasterList.size() + " x 블럭 " + floorBlockList.size() + "개)", t);
		}

		if (bno_list_all_check.size() > 0) {
			List<String> errorList = bno_list_all_check.stream().distinct().collect(Collectors.toList());
			throw new Exception(String.join(", ", errorList) + " 블록 공급 구분(본사 or 법인) 이 누락되어 있으니 필히 CAL_BOM_SUPPLIER_DESIGN에 공급구분을 반영 할 것");
		}
	}

	private void findBom(HashMap dataMap, BlockVariableAction vAction, BlockInfo blockInfo) throws Exception {
		for (PickInfo pickInfo : blockInfo.getPickList()) {
			String pick = pickInfo.getPick();
			String qty = pickInfo.getQty();
			String cmt = pickInfo.getCmt();
			String color = pickInfo.getColor();

			Map picked = pickPart(dataMap, blockInfo.getOuid(), pick);
			if (picked == null)
				continue;

			// pick 된 block은 여기서 확인
			String pickBlock = blockInfo.getBlockNo();
			if (elvOuid.startsWith(BlockConsts.PREFIX_ELVINFO_OUID)) {
				// bno_list_all 확인해서 없는 경우 오류 (Pick이 EL_ 인 경우에만 실행, 선박 제외)
				if (bno_list_all.size() > 0 && pick.startsWith("EL_")) {
					if (!bno_list_all.contains(BlockUtil.NVL(pickBlock, "")))
						bno_list_all_check.add(pickBlock);
				}
			}

			String partOuid = (String) picked.get("OUID");
			boolean hasChild = !"0".equals(picked.get("HASCHILD"));

			// Lv1 PID Calculate
			{
				picked.put("PICK", pick);
				String[] variable = vAction.get1LevelVariable(picked, qty, cmt, color);
				setTempVariableMap(partOuid, variable);
			}

			// Child PID Calculate
			if (hasChild) {
				boolean isCalculated = true;
				if (computedPartList.isEmpty() || !computedPartList.contains(partOuid)) {
					computedPartList.add(partOuid);
					isCalculated = false;
				}

				ArrayList<Map<String, String>> variableList = new ArrayList<>();
				for (Map<String, String> partOfPart : getListPartOfPart(partOuid)) {
					String assoOuid = String.valueOf(partOfPart.get("SF$OUID"));

					String child_qty = partOfPart.get("QTY");
					String child_cmt = partOfPart.get("CMT");
					String child_color = partOfPart.get("COLOR");

					if (ctx.isPidPattern(child_qty) || ctx.isPidPattern(child_cmt) || ctx.isPidPattern(child_color)) {
						HashMap<String, String> childPartInfo = generatePartInfo(partOfPart);
						HashMap<String, String> variable = vAction.getOtherLevelVariable(assoOuid, childPartInfo,
								child_qty, child_cmt, child_color, isCalculated);
						variableList.add(variable);
					}
				}
				if (variablePartMap4Simulate != null && variableList.size() > 0)
					variablePartMap4Simulate.put(partOuid, variableList);
				setTempVariableList(variableList);
			}
		}
	}

	/** SubaeManager.pickPart */
	private Map pickPart(HashMap dataInfoMap, String blockOuid, String pickField) throws Exception {
		String glCode = (String) dataInfoMap.get(pickField);
		if (glCode == null || "".equals(glCode))
			return null;

		String sql = " SELECT 'normalpart$vf@' || lower(dectohex(VF$OUID)) OUID, VF$VERSION VER, COD(PART_STATUS) PART_STATUS "
				+ " ,MD$NUMBER PARTNO, G_L_CODE, SPEC, PART_SIZE, (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID=GETID(BLOCKNO)) AS B_NO, COD(ORIGIN_DIV) ORIGIN_DIV, COD(SPT) SPT "
				+ ", (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=A.VF$OUID AND ROWNUM=1) HASCHILD"
				+ " FROM NORMALPART$VF A, NORMALPART$ID B WHERE A.VF$OUID=B.ID$LAST "
				+ " AND MD$NUMBER =? AND BLOCKNO = ? ";

		List<Map<String, String>> pickedList = ctx.getDb().queryForList(sql, glCode, blockOuid);
		if (pickedList.isEmpty())
			return null;

		Map map = new HashMap(pickedList.get(0));
		String version = (String) map.get("VER");
		String part_status = BlockUtil.NVL(map.get("PART_STATUS"), "");
		if (version.equals("wip"))
			return null;
		if (!part_status.equals("Active"))
			return null;

		return map;
	}

	/** SubaeDao.getListPartOfPart */
	private List<Map<String, String>> getListPartOfPart(String partOuid) throws Exception {
		List<Map<String, String>> cached = partOfPartCache.get(partOuid);
		if (cached != null) {
			partOfPartHit++;
			return cached;
		}
		cached = queryListPartOfPart(partOuid);
		partOfPartCache.put(partOuid, cached);
		return cached;
	}

	private List<Map<String, String>> queryListPartOfPart(String partOuid) throws Exception {
		long lPartOuid = Long.parseLong(partOuid.substring(partOuid.indexOf('@') + 1), 16);
		return ctx.getDb().queryForList(
				" SELECT A.SF$OUID, AS$END1, AS$END2, END1_HEXOUID, END2_HEXOUID, CMT, QTY, COLOR, "
				+ " B.MD$NUMBER AS PARTNO, B.G_L_CODE, B.SPEC, B.PART_SIZE, C.MD$NUMBER AS B_NO "
				+ " FROM PARTOFPART$AC A "
				+ " LEFT OUTER JOIN NORMALPART$VF B ON AS$END2 = VF$OUID "
				+ " LEFT OUTER JOIN BLOCKNO$SF C ON C.SF$OUID = GETID(B.BLOCKNO) "
				+ " START WITH AS$END1 = ? "
				+ " CONNECT BY PRIOR AS$END2 = AS$END1 ", lPartOuid);
	}

	/** SubaeDaoImpl.findBLocksByNo */
	private List<BlockInfo> findBLocksByNo(List<String> blockList, Boolean isFloorPart) throws Exception {
		List<Object> param = new ArrayList<Object>(blockList);

		StringBuilder sql = new StringBuilder(
				"SELECT LOWER(CONCAT('blockno$sf@', DECTOHEX(SF$OUID))) OUID, SF$OUID LOUID, MD$NUMBER BLOCKNO, MD$DESC BLOCKNAME, cod(FLOOR_PART) FLOOR_PART, A.* FROM BLOCKNO$SF A");
		sql.append(" WHERE MD$NUMBER IN ('',");
		sql.append(blockList.stream().map(o -> "?").collect(Collectors.joining(",")));
		sql.append(")");

		if (isFloorPart != null) {
			sql.append(" AND NVL(COD(FLOOR_PART), 'N') = ?");
			param.add(isFloorPart ? "Y" : "N");
		}
		sql.append(" ORDER BY MD$NUMBER ");

		return toBlockInfoList(ctx.getDb().queryForList(sql.toString(), param.toArray()));
	}

	/** BlockNo$SF 조회결과 → BlockInfo (SubaeDaoImpl RowMapper) */
	public static List<BlockInfo> toBlockInfoList(List<Map<String, String>> rows) {
		List<BlockInfo> blockInfos = new ArrayList<BlockInfo>();
		for (Map<String, String> rs : rows) {
			BlockInfo blockInfo = new BlockInfo();
			blockInfo.setOuid(BlockUtil.NVL(rs.get("OUID"), ""));
			blockInfo.setlOuid(Long.parseLong(rs.get("LOUID")));
			blockInfo.setBlockNo(BlockUtil.NVL(rs.get("BLOCKNO"), ""));
			blockInfo.setBlockName(BlockUtil.NVL(rs.get("BLOCKNAME"), ""));
			blockInfo.setFloorPart("Y".equals(rs.get("FLOOR_PART")));

			List<PickInfo> pickInfos = new ArrayList<PickInfo>();
			for (int i = 1; i <= BlockConsts.MAX_PICK_COUNT; i++) {
				PickInfo pickInfo = new PickInfo();
				pickInfo.setPick(BlockUtil.NVL(rs.get("PICK" + i), ""));
				pickInfo.setQty(BlockUtil.NVL(rs.get("QTY" + i), ""));
				pickInfo.setCmt(BlockUtil.NVL(rs.get("CMT" + i), ""));
				pickInfo.setColor(BlockUtil.NVL(rs.get("COLOR" + i), ""));
				if (!"".equals(pickInfo.getPick().trim()))
					pickInfos.add(pickInfo);
			}
			blockInfo.setPickList(pickInfos);
			blockInfos.add(blockInfo);
		}
		return blockInfos;
	}

	// ------------------------------------------------------------------------ variable

	private HashMap<String, String> generatePartInfo(Map<String, String> partOfPart) {
		HashMap<String, String> childPartInfo = new HashMap<>();
		childPartInfo.put("PARTNO", String.valueOf(partOfPart.get("PARTNO")));
		childPartInfo.put("G_L_CODE", String.valueOf(partOfPart.get("G_L_CODE")));
		childPartInfo.put("SPEC", String.valueOf(partOfPart.get("SPEC")));
		childPartInfo.put("PART_SIZE", String.valueOf(partOfPart.get("PART_SIZE")));
		childPartInfo.put("B_NO", String.valueOf(partOfPart.get("B_NO")));
		return childPartInfo;
	}

	/** 같은 assoOuid 의 공사주석/도장은 처음 계산된 variable 객체에 합친다. (variablePartMap4Simulate 의 객체와 공유됨) */
	private void setTempVariableList(ArrayList<Map<String, String>> variableList) {
		for (Map<String, String> variable : variableList) {
			if (tempVariablePartMap.containsKey(variable.get("assoOuid"))) {
				Map<String, String> tempVar = tempVariablePartMap.get(variable.get("assoOuid"));
				String tempCmt = tempVar.get("cmt");
				String tempColor = tempVar.get("color");
				if (BlockUtil.isNullString(tempCmt)) {
					tempVar.put("cmt", variable.get("cmt"));
				} else {
					if (!BlockUtil.isDupCmt(tempCmt, variable.get("cmt")))
						tempVar.put("cmt", tempCmt + variable.get("cmt"));
				}
				if (BlockUtil.isNullString(tempColor)) {
					tempVar.put("color", variable.get("color"));
				} else {
					if (!BlockUtil.isDupCmt(tempColor, variable.get("color")))
						tempVar.put("color", tempColor + variable.get("color"));
				}
			} else {
				tempVariablePartMap.put(variable.get("assoOuid"), variable);
			}
		}
	}

	private void setTempVariableMap(String partOuid, String[] variable) {
		if (tempVariableMap.get(partOuid) == null) {
			tempVariableMap.put(partOuid, variable);
			return;
		}

		String[] tmpVar = tempVariableMap.get(partOuid);
		String cmts = variable[0];
		if (!BlockUtil.isNullString(cmts)) {
			if (BlockUtil.isNullString(tmpVar[0])) {
				tmpVar[0] = cmts;
			} else {
				String[] cmt = cmts.split("\n");
				tmpVar[0] += tmpVar[0].endsWith("\n") ? "" : "\n";
				for (int i = 0; i < cmt.length; i++)
					tmpVar[0] += BlockUtil.addLine(tmpVar[0], cmt[i]);
			}
		}

		BigDecimal qty;
		BigDecimal tmpQty;

		try {
			qty = new BigDecimal(variable[1]);
		} catch (NumberFormatException e) {
			qty = BigDecimal.ZERO;
			System.err.println("qty numberformatException " + partOuid + ", qty:" + variable[1]);
		}

		try {
			tmpQty = new BigDecimal(tmpVar[1]);
		} catch (NumberFormatException e) {
			tmpQty = BigDecimal.ZERO;
			System.err.println("qty numberformatException " + partOuid + ", tmpQty:" + tmpVar[1]);
		}

		// 수량이 계산되지 않았을 경우는 문자열 표시 (원본과 동일하게 참조 비교)
		if (qty != BigDecimal.ZERO && tmpQty != BigDecimal.ZERO)
			tmpVar[1] = qty.add(tmpQty).stripTrailingZeros().toPlainString();
	}

	private static List<Map> toMapList(List<HashMap> list) {
		return list == null ? new ArrayList<Map>() : new ArrayList<Map>(list);
	}
}
