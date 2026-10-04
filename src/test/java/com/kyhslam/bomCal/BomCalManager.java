package com.kyhslam.bomCal;

import com.kyhslam.bomCal.BomBlockInfo.PickInfo;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * dyna.plmetc.subae.model.SubaeManager.bomCalculate 의 조회 전용 독립 버전.
 *
 * 원본과 같은 순서로 사양계산(EL_P) → 블록옵션 비교 → PICK & PID 계산 까지 수행하고,
 * 마지막 저장 단계는 DB 에 쓰지 않고 저장될 내용을 BomCalResult 로 돌려준다.
 *   - link1LevelPart   (PARTOFEBOM insert)        → BomCalResult.level1List
 *   - makeEBomStructure (VARIABLEPART_NEW insert)  → BomCalResult.level2List
 *   - 수행하지 않음 : 공사정보 ebom_manager 수정(dos.set), 공사정보 승인(releaseElvInfo),
 *                    제품 블록옵션 계산완료 표시(updateCalcOptList), 제품 등록/WIP 생성/연결 (ProductInfo)
 */
public class BomCalManager {

	private final BomContext ctx;
	private final BomDao dao;
	private final String elvOuid;

	private HashMap elvDataMap;
	private List<HashMap> floorMasterList;
	private BomProductInfo productInfo;

	private Map<String, String[]> tempVariableMap;
	private ArrayList<String> computedPartList;
	private List<Map<String, String>> tempVariableList;
	private Map<String, Map<String, String>> tempVariablePartMap;

	/** 결과 표시용 : partOuid → pickPart 결과 (처음 pick 된 것) */
	private final Map<String, Map<String, String>> level1PartInfo = new HashMap<String, Map<String, String>>();
	/** 결과 표시용 : assoOuid → {PARTNO, B_NO, PARENT_PARTNO} */
	private final Map<String, Map<String, String>> level2PartInfo = new HashMap<String, Map<String, String>>();

	private final List<String> bno_list_all = new ArrayList<String>();
	private final List<String> bno_list_all_check = new ArrayList<String>();

	public BomCalManager(BomContext ctx, String elvOuid) {
		this.ctx = ctx;
		this.dao = new BomDao(ctx.getDb());
		this.elvOuid = elvOuid;
	}

	/**
	 * SubaeManager.bomCalculate
	 * @param inputOptList 블록옵션 (C, M, F, 1, 2, 3)
	 * @param ignoreCalculated true 면 제품에 계산완료로 표시된 블록옵션도 계산한다 (원본은 제외)
	 */
	public BomCalResult bomCalculate(List<String> inputOptList, boolean ignoreCalculated) throws Exception {
		BomCalResult result = new BomCalResult();
		result.setElvOuid(elvOuid);
		result.setInputOptList(new ArrayList<String>(inputOptList));

		System.out.println("========= Check Elv Status =============");
		String status = ctx.getSpecLoader().getStatus(elvOuid);
		result.setElvStatus(status);
		System.out.println("status = " + status);
		// 조회 전용이므로 승인(RLS) 버전도 계산한다. 승인된 제품은 블록옵션이 계산완료로 표시되어 있으므로 모두 계산한다.
		if ("RLS".equals(status)) {
			ignoreCalculated = true;
			result.getNotes().add("공사정보가 승인(RLS) 상태 : 원본은 '이미 승인된 버전입니다. WIP생성 후 BOM계산 해주세요.' 로 중단되지만, 조회용이므로 계산한다. (계산완료 블록옵션 포함)");
		}

		System.out.println("========= Create Elv & Floor Master =============");
		BomSpecLoader.SpecObject elv = ctx.getSpecLoader().load(elvOuid);
		if (elv == null)
			throw new Exception("공사정보가 없습니다. ouid=" + elvOuid);
		elvDataMap = elv.dataMap;
		result.setHogi(BomUtil.NVL(elvDataMap.get("md$number"), ""));

		if (isElv()) {
			floorMasterList = ctx.getSpecLoader().loadFloors(elvOuid, elvDataMap);
			result.setFloorCount(floorMasterList == null ? 0 : floorMasterList.size());
		}

		// 원본은 inputOptList 에 직접 추가한다 (SV)
		List<String> optList = new ArrayList<String>(inputOptList);

		System.out.println("========= Make EL_P Spec =============");
		if (isElv()) {
			calculate_EL_P(BomConsts.EL_P_PREFIX, true);
			// 원본 : floorMasterList.forEach(f -> f.getDataMap().putAll(elvMaster.getDataMap()))
			// (원본은 층이 없으면 여기서 NullPointerException 이 난다)
			if (floorMasterList != null) {
				for (HashMap floorDataMap : floorMasterList)
					floorDataMap.putAll(elvDataMap);
			} else {
				result.getNotes().add("층 정보 없음 : 원본은 층 정보가 없으면 NullPointerException 으로 중단된다.");
			}
		} else if (elvOuid.startsWith(BomConsts.PREFIX_SVELVINFO_OUID)) {
			optList.addAll(Arrays.asList(BomConsts.SV_ADD_OPT_LIST));
			calculate_EL_P(BomConsts.SVEL_P_PREFIX, false);
		} else if (elvOuid.startsWith(BomConsts.PREFIX_SHIPELVINFO_OUID)) {
			calculate_EL_P(BomConsts.SHIPEL_P_PREFIX, false);
		}

		System.out.println("========= Ready Product =============");
		productInfo = new BomProductInfo(ctx, dao, elvOuid, result.getHogi());
		productInfo.readyProduct4Calculate();
		productInfo.compareBlockOption(optList, ignoreCalculated);
		result.setProductOuid(productInfo.getProductOuid());
		result.setBlockOptList4Calc(productInfo.getBlockOptList4Calc());
		result.getNotes().addAll(productInfo.getNotes());
		System.out.println("blockOptList4Calc : " + productInfo.getBlockOptList4Calc());
		if (productInfo.getBlockOptList4Calc().isEmpty()) {
			result.getNotes().add("계산할 블록옵션이 없습니다.");
			result.setPidErrors(ctx.getPidErrors());
			return result;
		}

		System.out.println("========= Ready [EL_ASPSC = KC01] =============");
		String el_aspsc = BomUtil.NVL(elvDataMap.get("EL_ASPSC"), "");
		if (isElv() && el_aspsc.equals("KC01")) {
			// kc01 대상 블록 리스트들 추출
			cal_bom_supplier_design(result);
		}

		System.out.println("========= Create and Ready PartListSeeker =============");
		this.tempVariableMap = new LinkedHashMap<String, String[]>();
		this.computedPartList = new ArrayList<String>();
		this.tempVariableList = new ArrayList<Map<String, String>>();
		this.tempVariablePartMap = new LinkedHashMap<String, Map<String, String>>();

		List<BomBlockInfo> blockList;
		List<BomBlockInfo> floorBlockList = null;

		List<String> blockOpts = productInfo.getBlockOptList4Calc();
		if (isElv() || elvOuid.startsWith(BomConsts.PREFIX_SVELVINFO_OUID))
			blockList = dao.getBlockList(blockOpts, false);
		else
			blockList = dao.getShipBlockList(blockOpts);

		if (floorMasterList != null)
			floorBlockList = dao.getBlockList(blockOpts, true);

		pickAndCalculatePid(blockList, floorBlockList, result);

		System.out.println("========= Make EBOM Structure (조회 전용 : 저장하지 않음) =============");
		makeEBomStructure(result);

		result.setPidErrors(ctx.getPidErrors());
		return result;
	}

	private boolean isElv() {
		return elvOuid.startsWith(BomConsts.PREFIX_ELVINFO_OUID);
	}

	// ------------------------------------------------------------------------ EL_P

	/** SubaeManager.calculate_EL_P / calculate_SHIPEL_P / calculate_SVEL_P + EL_P */
	private void calculate_EL_P(String pidPrefix, boolean withFloor) throws Exception {
		long start = System.nanoTime();
		List<Map> floorMaps = toMapList(floorMasterList);

		make_EL_P_Data(elvDataMap, floorMaps, dao.getEL_PList(pidPrefix, false));

		if (withFloor && floorMasterList != null && !floorMasterList.isEmpty()) {
			List<Map<String, String>> floorPidList = dao.getEL_PList(pidPrefix, true);
			for (HashMap floorDataMap : floorMasterList) {
				System.out.println("-- floor : " + floorDataMap.get(BomConsts.FIELD_MD_DESCRIPTION));
				make_EL_P_Data(floorDataMap, floorMaps, floorPidList);
			}
		}
		System.out.println("calculate_EL_P() end : " + (System.nanoTime() - start) / 1000000 + "ms");
	}

	/** EL_P.make_EL_P_Data : EL_P PID 들의 OUTPUT 값을 사양에 추가 */
	private void make_EL_P_Data(HashMap dataInfoMap, List<Map> floorMaps, List<Map<String, String>> pidList) {
		BomVariant variant = new BomVariant(ctx, dataInfoMap, floorMaps);
		HashMap<String, String> resultMap = new HashMap<String, String>();

		for (Map<String, String> pidMap : pidList) {
			String pid = pidMap.get("PID");
			String method = pidMap.get("METHOD");

			BomVariantMap localMap = null;
			try {
				localMap = variant.calcVariantPID(pid, null);
			} catch (Exception e) {
				System.err.println(pid + "(" + method + ") : " + e.getMessage());
			}

			if (localMap != null) {
				Iterator<String> it = localMap.getOUTPUTMap().keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					resultMap.put(key, BomUtil.NVL(localMap.get(key), ""));
				}
			}
		}

		dataInfoMap.putAll(resultMap);
	}

	// ------------------------------------------------------------------------ KC01

	/** SubaeManager.cal_bom_supplier_design : KC01 대상 블록 목록 (BNO_LIST_ALL, ## 구분) */
	private void cal_bom_supplier_design(BomCalResult result) throws Exception {
		BomVariant variant = new BomVariant(ctx, elvDataMap, null);
		BomVariantMap variantMap = variant.calcVariantPID(BomConsts.PID_CAL_BOM_SUPPLIER_DESIGN, null);

		String bno_all = (String) variantMap.get("BNO_LIST_ALL");
		if (bno_all == null) {
			// 원본은 NullPointerException 으로 중단된다
			result.getNotes().add(BomConsts.PID_CAL_BOM_SUPPLIER_DESIGN + " 결과에 BNO_LIST_ALL 이 없음 : 원본은 NullPointerException 으로 중단된다.");
			return;
		}

		bno_list_all.addAll(Arrays.asList(bno_all.split("##")));
		for (int i = 0; i < bno_list_all.size(); i++)
			bno_list_all.set(i, bno_list_all.get(i).replaceAll("[^a-zA-Z0-9]", ""));
	}

	// ------------------------------------------------------------------------ pick

	private void pickAndCalculatePid(List<BomBlockInfo> blockList, List<BomBlockInfo> floorBlockList, BomCalResult result) throws Exception {
		List<Map> floorMaps = toMapList(floorMasterList);

		// Common Block Pick & PID Calculate
		{
			BomVariableAction variableAction = new BomVariableAction(ctx, elvDataMap, floorMaps);
			for (BomBlockInfo blockInfo : blockList)
				findBom(elvDataMap, variableAction, blockInfo);
		}

		// Floor Block Pick & PID Calculate
		if (floorMasterList != null) {
			for (HashMap floorDataMap : floorMasterList) {
				BomVariableAction variableAction = new BomVariableAction(ctx, floorDataMap, floorMaps);
				for (BomBlockInfo blockInfo : floorBlockList)
					findBom(floorDataMap, variableAction, blockInfo);
			}
		}

		// 원본은 여기서 Exception 으로 중단된다. 조회용이므로 오류만 기록하고 결과는 계속 만든다.
		if (bno_list_all_check.size() > 0) {
			List<String> errorList = bno_list_all_check.stream().distinct().collect(Collectors.toList());
			result.setCalcError(String.join(", ", errorList)
					+ " 블록 공급 구분(본사 or 법인) 이 누락되어 있으니 필히 CAL_BOM_SUPPLIER_DESIGN에 공급구분을 반영 할 것");
		}

		tempVariableList.addAll(tempVariablePartMap.entrySet().stream().map(Entry::getValue).collect(Collectors.toList()));
	}

	private void findBom(HashMap dataMap, BomVariableAction vAction, BomBlockInfo blockInfo) throws Exception {
		for (PickInfo pickInfo : blockInfo.getPickList()) {
			String pick = pickInfo.getPick();
			String qty = pickInfo.getQty();
			String cmt = pickInfo.getCmt();
			String color = pickInfo.getColor();

			Map<String, String> picked = dao.pickPart(dataMap, blockInfo.getOuid(), pick);
			if (picked == null)
				continue;

			// pick 된 block은 여기서 확인
			String pickBlock = blockInfo.getBlockNo();
			if (isElv()) {
				// bno_list_all 확인해서 없는 경우 오류 (Pick이 EL_ 인 경우에만 실행, 선박 제외)
				if (bno_list_all.size() > 0 && pick.startsWith("EL_")) {
					if (!bno_list_all.contains(BomUtil.NVL(pickBlock, "")))
						bno_list_all_check.add(pickBlock);
				}
			}

			String partOuid = picked.get("OUID");
			boolean hasChild = !BigDecimal.ZERO.equals(BomUtil.parseBigDecimal(picked.get("HASCHILD")));

			// Lv1 PID Calculate
			{
				picked.put("PICK", pick);
				String[] variable = vAction.get1LevelVariable(picked, qty, cmt, color);
				setTempVariableMap(partOuid, variable);
				if (!level1PartInfo.containsKey(partOuid))
					level1PartInfo.put(partOuid, picked);
			}

			// Child PID Calculate
			if (hasChild) {
				boolean isCalculated = true;
				if (computedPartList.isEmpty() || !computedPartList.contains(partOuid)) {
					computedPartList.add(partOuid);
					isCalculated = false;
				}

				ArrayList<Map<String, String>> variableList = new ArrayList<>();
				for (Map<String, String> partOfPart : dao.getListPartOfPart(partOuid)) {
					String assoOuid = String.valueOf(partOfPart.get("SF$OUID"));

					String child_qty = partOfPart.get("QTY");
					String child_cmt = partOfPart.get("CMT");
					String child_color = partOfPart.get("COLOR");

					if (ctx.isPidPattern(child_qty) || ctx.isPidPattern(child_cmt) || ctx.isPidPattern(child_color)) {
						HashMap<String, String> childPartInfo = generatePartInfo(partOfPart);
						HashMap<String, String> variable = vAction.getOtherLevelVariable(assoOuid, childPartInfo,
								child_qty, child_cmt, child_color, isCalculated);
						variableList.add(variable);

						if (!level2PartInfo.containsKey(assoOuid)) {
							Map<String, String> info = new HashMap<String, String>();
							info.put("PARTNO", partOfPart.get("PARTNO"));
							info.put("PARTNAME", partOfPart.get("PARTNAME"));
							info.put("B_NO", partOfPart.get("B_NO"));
							info.put("PARENT_PARTNO", picked.get("PARTNO"));
							level2PartInfo.put(assoOuid, info);
						}
					}
				}
				setTempVariableList(variableList);
			}
		}
	}

	private HashMap<String, String> generatePartInfo(Map<String, String> partOfPart) {
		HashMap<String, String> childPartInfo = new HashMap<>();
		childPartInfo.put("PARTNO", String.valueOf(partOfPart.get("PARTNO")));
		childPartInfo.put("G_L_CODE", String.valueOf(partOfPart.get("G_L_CODE")));
		childPartInfo.put("SPEC", String.valueOf(partOfPart.get("SPEC")));
		childPartInfo.put("PART_SIZE", String.valueOf(partOfPart.get("PART_SIZE")));
		childPartInfo.put("B_NO", String.valueOf(partOfPart.get("B_NO")));
		return childPartInfo;
	}

	/** 같은 assoOuid 의 공사주석/도장은 처음 계산된 variable 객체에 합친다. */
	private void setTempVariableList(ArrayList<Map<String, String>> variableList) {
		for (Map<String, String> variable : variableList) {
			if (tempVariablePartMap.containsKey(variable.get("assoOuid"))) {
				Map<String, String> tempVar = tempVariablePartMap.get(variable.get("assoOuid"));
				String tempCmt = tempVar.get("cmt");
				String tempColor = tempVar.get("color");
				if (BomUtil.isNullString(tempCmt)) {
					tempVar.put("cmt", variable.get("cmt"));
				} else {
					if (!BomUtil.isDupCmt(tempCmt, variable.get("cmt")))
						tempVar.put("cmt", tempCmt + variable.get("cmt"));
				}
				if (BomUtil.isNullString(tempColor)) {
					tempVar.put("color", variable.get("color"));
				} else {
					if (!BomUtil.isDupCmt(tempColor, variable.get("color")))
						tempVar.put("color", tempColor + variable.get("color"));
				}
			} else {
				tempVariablePartMap.put(variable.get("assoOuid"), variable);
			}
		}
	}

	/** 같은 품번이 여러 블록/층에서 pick 되면 주석은 합치고 수량은 더한다. */
	private void setTempVariableMap(String partOuid, String[] variable) {
		if (tempVariableMap.get(partOuid) == null) {
			tempVariableMap.put(partOuid, variable);
			return;
		}

		String[] tmpVar = tempVariableMap.get(partOuid);
		String cmts = variable[0];
		if (!BomUtil.isNullString(cmts)) {
			if (BomUtil.isNullString(tmpVar[0])) {
				tmpVar[0] = cmts;
			} else {
				String[] cmt = cmts.split("\n");
				tmpVar[0] += tmpVar[0].endsWith("\n") ? "" : "\n";
				for (int i = 0; i < cmt.length; i++)
					tmpVar[0] += BomUtil.addLine(tmpVar[0], cmt[i]);
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

	// ------------------------------------------------------------------------ ebom (조회 전용)

	/**
	 * SubaeManager.makeEBomStructure / link1LevelPart 의 저장 대상만 만든다. (insert 하지 않음)
	 * 제품이 없으면(원본은 새로 등록) 기존 BOM 이 없는 것으로 본다.
	 */
	private void makeEBomStructure(BomCalResult result) throws Exception {
		String prodOuid = productInfo.getProductOuid();
		Long lProdOuid = BomUtil.isNullString(prodOuid) ? null : BomUtil.toRealOuid(prodOuid);

		link1LevelPart(lProdOuid, result);

		// 이미 수배된 VARIABLEPART 자재
		List<String> variablePartListCheck = lProdOuid == null ? new ArrayList<String>() : dao.getVariablePartList(lProdOuid);

		int count = 0;
		for (Map<String, String> variable : tempVariableList) {
			String assoOuid = variable.get("assoOuid");
			String cmt = variable.get("cmt");
			String qty = variable.get("qty");
			String color = variable.get("color");

			BomCalResult.Level2Row row = new BomCalResult.Level2Row();
			row.assoOuid = assoOuid;
			row.qty = qty;
			row.cmt = cmt;
			row.color = color;
			Map<String, String> info = level2PartInfo.get(assoOuid);
			if (info != null) {
				row.partNo = info.get("PARTNO");
				row.partName = info.get("PARTNAME");
				row.blockNo = info.get("B_NO");
				row.parentPartNo = info.get("PARENT_PARTNO");
			}

			// 수배된 파트가 있을경우 수배대상에서 제외
			if (variablePartListCheck.contains(assoOuid)) {
				row.action = BomCalResult.ACTION_SKIP_EXIST;
			} else if (!BomUtil.isNullString(cmt) || !BomUtil.isNullString(qty) || !BomUtil.isNullString(color)) {
				// 공사 주석, 수량, 도장이 있을 경우만 입력
				row.action = BomCalResult.ACTION_INSERT;
				count++;
			} else {
				row.action = BomCalResult.ACTION_SKIP_EMPTY;
			}
			result.getLevel2List().add(row);
		}
		System.out.println("2Level part size : " + tempVariableList.size());
		System.out.println("2Level part insert 대상 (variablepart_new) : " + count + " rows");
	}

	private void link1LevelPart(Long lProdOuid, BomCalResult result) throws Exception {
		// 이미 수배된 1레벨 PARTOFEBOM 자재
		List<String> partListCheck = lProdOuid == null ? new ArrayList<String>() : dao.getPartList(lProdOuid);

		// 1level 링크 전 블록번호(B_NO 첫 글자 제외)로 정렬
		List<String> keyList = new ArrayList<String>(tempVariableMap.keySet());
		Collections.sort(keyList, (k1, k2) -> sortKey(tempVariableMap.get(k1)).compareTo(sortKey(tempVariableMap.get(k2))));

		int seq = lProdOuid == null ? 0 : dao.getMaxSeq(lProdOuid);
		for (String partOuid : keyList) {
			seq += BomConsts.EBOM_SEQ_STEP;
			String[] variable = tempVariableMap.get(partOuid);
			String qty = BomUtil.isNullString(variable[1]) ? "0" : variable[1];
			Map<String, String> picked = level1PartInfo.get(partOuid);

			BomCalResult.Level1Row row = new BomCalResult.Level1Row();
			row.seq = String.valueOf(seq);
			row.partOuid = partOuid;
			row.partNo = variable[3];
			row.blockNo = variable[4];
			row.cmt = variable[0];
			row.qty = qty;
			row.color = variable[2];
			if (picked != null) {
				row.partName = picked.get("PARTNAME");
				row.glCode = picked.get("G_L_CODE");
				row.spec = picked.get("SPEC");
				row.partSize = picked.get("PART_SIZE");
				row.pick = picked.get("PICK");
			}

			// 수배된 파트가 있을경우 수배대상에서 제외 (seq 는 원본과 같이 증가시킨다)
			String partDecOuid = BomUtil.hexTodeci(partOuid.substring(BomConsts.PREFIX_NORMALPART_OUID.length()));
			if (partListCheck.contains(partDecOuid)) {
				row.action = BomCalResult.ACTION_SKIP_EXIST;
			} else {
				row.action = BomCalResult.ACTION_INSERT;
				String div = picked != null ? picked.get("ORIGIN_DIV") : dao.getPartDiv(BomUtil.toRealOuid(partOuid));
				row.mBom = (BomConsts.DIV_INNER.equals(div) || BomConsts.DIV_OUTER.equals(div) || BomConsts.DIV_INNER_F.equals(div)) ? "T" : "";
			}
			result.getLevel1List().add(row);
		}
	}

	private static String sortKey(String[] variable) {
		return (variable[4] != null && variable[4].length() > 1) ? variable[4].substring(1) : "";
	}

	private static List<Map> toMapList(List<HashMap> list) {
		return list == null ? new ArrayList<Map>() : new ArrayList<Map>(list);
	}
}
