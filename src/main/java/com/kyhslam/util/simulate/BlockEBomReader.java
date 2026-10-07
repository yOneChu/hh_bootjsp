package com.kyhslam.util.simulate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 현재 BOM 1레벨 오더 대상 조회.
 * 원본 : ebomService.getOrderBom(iOuid) (EBomManager.makeEBomList + BomUtils.setOrderExpect / convertOrderBom)
 *        → BomUtils.nonHierarchical → level == 1 필터
 * 시뮬레이션은 1레벨만 사용하므로 1레벨 항목만 같은 규칙으로 만든다.
 */
public class BlockEBomReader {

	private final BlockDb db;

	public BlockEBomReader(BlockDb db) {
		this.db = db;
	}

	/**
	 * @param productOuid product$vf@hex
	 * @return 1레벨 오더대상 항목 (key : partNo, ouid, qty, cmt, glCode, spec, part_size, uCheck, blockNo, blockNo_org, div, level)
	 */
	public List<Map<String, Object>> getTopLevelOrderBom(String productOuid) throws Exception {
		long lProdOuid = Long.parseLong(productOuid.substring(productOuid.indexOf('@') + 1), 16);

		// EBomDao.list1LevelPart 중 필요한 컬럼
		List<Map<String, String>> rows = db.queryForList(
				" SELECT PE.PARTOUID END2 "
				+ " , NP.MD$NUMBER PARTNO "
				+ " , NVL(NP.G_L_CODE, '') GLCODE "
				+ " , NVL(NP.SPEC, '') SPEC "
				+ " , NVL(NP.PART_SIZE, '') PART_SIZE "
				+ " , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID = DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO "
				+ " , PE.QTY "
				+ " , VP.WORK_QTY "
				+ " , PE.CMT "
				+ " , VP.WORK_CMT "
				+ " , NVL(COD(NP.ORIGIN_DIV), '') DIV "
				+ " , VP.UCHECK "
				+ " FROM PARTOFEBOM PE "
				+ " INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID "
				+ " LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID "
				+ " WHERE PE.PRODUCTOUID = ? "
				+ " ORDER BY TO_NUMBER(PE.SEQ) ", lProdOuid);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, String> r : rows) {
			// EBomManager.makeEBomNode (null 값은 JSON 변환시 key 가 제거됨)
			Map<String, Object> item = new HashMap<String, Object>();
			putIfNotNull(item, "ouid", r.get("END2") == null ? null : Long.valueOf(r.get("END2")));
			putIfNotNull(item, "partNo", r.get("PARTNO"));
			putIfNotNull(item, "glCode", r.get("GLCODE"));
			putIfNotNull(item, "spec", r.get("SPEC"));
			putIfNotNull(item, "part_size", r.get("PART_SIZE"));
			String blockNo = r.get("BLOCKNO");
			putIfNotNull(item, "blockNo", (blockNo != null && !blockNo.equals("")) ? blockNo.substring(1) : blockNo);
			putIfNotNull(item, "blockNo_org", blockNo);
			putIfNotNull(item, "qty", r.get("QTY"));
			putIfNotNull(item, "work_qty", r.get("WORK_QTY"));
			putIfNotNull(item, "cmt", r.get("CMT"));
			putIfNotNull(item, "work_cmt", r.get("WORK_CMT"));
			putIfNotNull(item, "div", r.get("DIV"));
			putIfNotNull(item, "uCheck", r.get("UCHECK"));
			item.put("level", 1);

			// BomUtils.setOrderExpect (level 1) : 수량/공사수량이 0 이거나 오더구분이 없으면 오더대상 아님
			if (BlockUtil.getRealQty((String) item.get("qty"), (String) item.get("work_qty")) == 0)
				continue;
			String div = (String) item.get("div");
			if (!BlockConsts.DIV_INNER.equals(div) && !BlockConsts.DIV_OUTER.equals(div) && !BlockConsts.DIV_INNER_F.equals(div))
				continue;

			// BomUtils.convertOrderBom
			if (BlockUtil.hasText((String) item.get("work_qty"))) {
				item.put("qty", item.get("work_qty"));
				item.remove("work_qty");
			}
			if (BlockUtil.hasText((String) item.get("work_cmt"))) {
				item.put("cmt", item.get("work_cmt"));
				item.remove("work_cmt");
			}

			result.add(item);
		}
		return result;
	}

	// ------------------------------------------------------------------------ 전체 오더 BOM (FUNCTION_READ_BOM)

	/**
	 * ebomService.getOrderBom(productOuid) : EBomManager.makeEBomList + BomUtils.setOrderExpect + convertOrderBom
	 * @return root 1건 (Items 에 하위 트리)
	 */
	public List<Map<String, Object>> getOrderBom(String productOuid) throws Exception {
		long lProdOuid = Long.parseLong(productOuid.substring(productOuid.indexOf('@') + 1), 16);

		// EBomManager.setEBomList : 1레벨 + 하위(listPartOfPartVariable) 를 순서대로 펼친 목록
		List<Map<String, String>> ebomList = new ArrayList<Map<String, String>>();
		for (Map<String, String> assy : db.queryForList(SQL_LIST_1LEVEL_PART, lProdOuid)) {
			assy.put("LEV", "1");
			assy.put("IDX", String.valueOf(ebomList.size()));
			ebomList.add(assy);

			if (!"0".equals(assy.get("HASCHILD"))) {
				assy.put("ISLEAF", "F");
				long lPartOuid = Long.parseLong(assy.get("END2"));
				for (Map<String, String> part : db.queryForList(SQL_LIST_PART_OF_PART_VARIABLE, lProdOuid, lPartOuid, lProdOuid, lPartOuid)) {
					part.put("IDX", String.valueOf(ebomList.size()));
					ebomList.add(part);
				}
			} else {
				assy.put("ISLEAF", "T");
			}
		}

		// EBomManager.makeRootNode
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("assoOuid", lProdOuid);
		root.put("ouid", lProdOuid);
		Map<String, String> productInfo = db.queryForFirst(" SELECT MD$NUMBER, MD$DESC, VF$VERSION FROM PRODUCT$VF WHERE VF$OUID = ? ", lProdOuid);
		if (productInfo != null) {
			putIfNotNull(root, "partNo", productInfo.get("MD$NUMBER"));
			putIfNotNull(root, "partName", productInfo.get("MD$DESC"));
			putIfNotNull(root, "ver", productInfo.get("VF$VERSION"));
		}
		if (!ebomList.isEmpty()) {
			List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
			for (Map<String, String> child : getChildren(ebomList, lProdOuid, 0, 1))
				items.add(makeEBomNode(ebomList, child));
			if (!items.isEmpty())
				root.put("Items", items);
		}

		List<Map<String, Object>> bomList = new ArrayList<Map<String, Object>>();
		bomList.add(root);

		setOrderExpect(bomList, 0, false);
		convertOrderBom(bomList);
		return bomList;
	}

	/** BomUtils.nonHierarchical */
	public static List<Map<String, Object>> nonHierarchical(List<Map<String, Object>> ebom) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> item : ebom) {
			result.add(item);
			if (item.containsKey("Items"))
				result.addAll(nonHierarchical((List<Map<String, Object>>) item.get("Items")));
		}
		return result;
	}

	private static List<Map<String, String>> getChildren(List<Map<String, String>> ebomList, long parentOuid, int startIdx, int level) {
		List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
		for (int i = startIdx; i < ebomList.size(); i++) {
			Map<String, String> ebom = ebomList.get(i);
			if (Integer.parseInt(ebom.get("LEV")) < level)
				break;
			if (Long.parseLong(ebom.get("END1")) == parentOuid)
				childList.add(ebom);
		}
		return childList;
	}

	/** EBomManager.makeEBomNode 중 사용하는 필드 */
	private static Map<String, Object> makeEBomNode(List<Map<String, String>> ebomList, Map<String, String> ebom) {
		Map<String, Object> node = new HashMap<String, Object>();
		putIfNotNull(node, "assoOuid", ebom.get("ASSOOUID") == null ? null : Long.valueOf(ebom.get("ASSOOUID")));
		putIfNotNull(node, "ouid", Long.valueOf(ebom.get("END2")));
		putIfNotNull(node, "partNo", ebom.get("PARTNO"));
		putIfNotNull(node, "partName", ebom.get("PARTNAME"));
		putIfNotNull(node, "glCode", ebom.get("GLCODE"));
		putIfNotNull(node, "nation", ebom.get("NATION"));
		String blockNo = ebom.get("BLOCKNO");
		putIfNotNull(node, "blockNo", (blockNo != null && !blockNo.equals("")) ? blockNo.substring(1) : blockNo);
		putIfNotNull(node, "blockNo_org", blockNo);
		putIfNotNull(node, "qty", ebom.get("QTY"));
		putIfNotNull(node, "work_qty", ebom.get("WORK_QTY"));
		putIfNotNull(node, "cmt", ebom.get("CMT"));
		putIfNotNull(node, "work_cmt", ebom.get("WORK_CMT"));
		putIfNotNull(node, "spec", ebom.get("SPEC"));
		putIfNotNull(node, "part_size", ebom.get("PART_SIZE"));
		putIfNotNull(node, "spt", ebom.get("SPT"));
		String part_spt = ebom.get("PART_SPT");
		if (!BlockUtil.isNullString(part_spt))
			part_spt = part_spt.equals("T") ? "1" : "0";
		putIfNotNull(node, "part_spt", part_spt);
		putIfNotNull(node, "div", ebom.get("DIV"));
		putIfNotNull(node, "uCheck", ebom.get("UCHECK"));

		if ("F".equals(ebom.get("ISLEAF"))) {
			List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
			for (Map<String, String> child : getChildren(ebomList, Long.parseLong(ebom.get("END2")),
					Integer.parseInt(ebom.get("IDX")) + 1, Integer.parseInt(ebom.get("LEV")) + 1))
				items.add(makeEBomNode(ebomList, child));
			node.put("Items", items);
		}
		return node;
	}

	/** BomUtils.setOrderExpect */
	private static void setOrderExpect(List<Map<String, Object>> ebom, int level, boolean parentIsSPT) {
		if (ebom == null)
			return;
		for (Map<String, Object> item : ebom) {
			item.put("level", level);

			// 부모가 SPT=30이면 child는 PART_SPT<>1 인 child는 오더대상에서 제외
			if (parentIsSPT && !"1".equals(item.get("part_spt")))
				continue;

			if (level != 0) {
				// 수량/공사수량 이 전부 0 이면 오더대상에서 제외
				if (BlockUtil.getRealQty((String) item.get("qty"), (String) item.get("work_qty")) == 0)
					continue;

				if (BlockConsts.DIV_INNER.equals(item.get("div"))) {
					item.put("doOrder", !"50".equals(item.get("spt")) ? "MAKE" : "PHANTOM");
				} else if (BlockConsts.DIV_OUTER.equals(item.get("div")) || BlockConsts.DIV_INNER_F.equals(item.get("div"))) {
					item.put("doOrder", "BUY");
				}
			} else {
				item.put("doOrder", "WBS");
			}

			List<Map<String, Object>> items = (List<Map<String, Object>>) item.get("Items");
			if (level == 0) { // root의 children 은 항상 오더대상임
				setOrderExpect(items, level + 1, false);
			} else if (BlockConsts.DIV_INNER.equals(item.get("div")) && !"50".equals(item.get("spt"))) { // 내작 하위의 child는 항상 오더대상임
				if (items != null)
					setOrderExpect(items, level + 1, false);
			} else if (BlockConsts.DIV_INNER.equals(item.get("div")) && "50".equals(item.get("spt"))) { // PHANTOM하위의 child는 상위 SPT를 계승함
				if (items != null)
					setOrderExpect(items, level + 1, parentIsSPT);
			} else if (BlockConsts.DIV_OUTER.equals(item.get("div")) || BlockConsts.DIV_INNER_F.equals(item.get("div"))) {
				if ("30".equals(item.get("spt")) && items != null) // 외주품이 SPT=30 이면 child는 오더대상임.
					setOrderExpect(items, level + 1, true);
			}
		}
	}

	/** BomUtils.convertOrderBom : 오더대상이 아닌 항목 제거, 공사수량/공사주석 반영 */
	private static void convertOrderBom(List<Map<String, Object>> ebom) {
		for (int i = ebom.size() - 1; i >= 0; i--) {
			Map<String, Object> item = ebom.get(i);
			if (item.containsKey("doOrder")) {
				if (item.containsKey("Items"))
					convertOrderBom((List<Map<String, Object>>) item.get("Items"));

				if (BlockUtil.hasText((String) item.get("work_qty"))) {
					item.put("qty", item.get("work_qty"));
					item.remove("work_qty");
				}
				if (BlockUtil.hasText((String) item.get("work_cmt"))) {
					item.put("cmt", item.get("work_cmt"));
					item.remove("work_cmt");
				}
			} else {
				ebom.remove(i);
			}
		}
	}

	/** EBomDao.list1LevelPart 중 사용하는 컬럼 */
	private static final String SQL_LIST_1LEVEL_PART =
			" SELECT PE.ASSOOUID ASSOOUID, PE.PRODUCTOUID END1, PE.PARTOUID END2 "
			+ " , NP.MD$NUMBER PARTNO, cod(NP.NATION) NATION, NP.MD$DESC PARTNAME "
			+ " , NVL(NP.G_L_CODE, '') GLCODE, NVL(NP.SPEC, '') SPEC, NVL(NP.PART_SIZE, '') PART_SIZE "
			+ " , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID = DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO "
			+ " , PE.QTY, VP.WORK_QTY, PE.CMT, VP.WORK_CMT "
			+ " , NVL(COD(NP.ORIGIN_DIV), '') DIV, COD(NP.SPT) SPT, VP.UCHECK "
			+ " , (SELECT COUNT(1) FROM PARTOFPART$AC WHERE AS$END1=NP.VF$OUID AND ROWNUM=1) HASCHILD "
			+ " FROM PARTOFEBOM PE "
			+ " INNER JOIN NORMALPART$VF NP ON PE.PARTOUID = NP.VF$OUID "
			+ " LEFT OUTER JOIN VARIABLEPART_NEW VP ON VP.PRODUCTOUID = PE.PRODUCTOUID AND VP.ASSOOUID = PE.ASSOOUID "
			+ " WHERE PE.PRODUCTOUID = ? "
			+ " ORDER BY TO_NUMBER(PE.SEQ) ";

	/** EBomDao.listPartOfPartVariable 중 사용하는 컬럼 (파라미터 : lProdOuid, lPartOuid, lProdOuid, lPartOuid) */
	private static final String SQL_LIST_PART_OF_PART_VARIABLE =
			" SELECT (LEVEL+1) LEV, A.SF$OUID ASSOOUID, A.AS$END1 END1, A.AS$END2 END2 "
			+ " , NP.MD$NUMBER PARTNO, NP.MD$DESC PARTNAME, NVL(NP.G_L_CODE, '') GLCODE, NVL(COD(NP.NATION), '') NATION "
			+ " , NVL(NP.SPEC, '') SPEC, NVL(NP.PART_SIZE, '') PART_SIZE "
			+ " , (SELECT MD$NUMBER FROM BLOCKNO$SF WHERE SF$OUID = DECODE(NP.BLOCKNO, NULL, NULL, HEXTODEC(UPPER(SUBSTR(NP.BLOCKNO, 12))))) BLOCKNO "
			+ " , A.QTY QTY, VP.WORK_QTY, A.CMT, VP.WORK_CMT "
			+ " , NVL(COD(NP.ORIGIN_DIV), '') DIV, COD(NP.SPT) SPT, NVL(A.PART_SPT, '') PART_SPT "
			+ " , DECODE(CONNECT_BY_ISLEAF, 0,'F', 1, 'T') ISLEAF, VP.UCHECK "
			+ " FROM PARTOFPART$AC A "
			+ " INNER JOIN NORMALPART$VF NP ON AS$END2 = NP.VF$OUID "
			+ " LEFT OUTER JOIN VARIABLEPART_NEW VP ON SF$OUID = VP.ASSOOUID AND VP.PRODUCTOUID = ? "
			+ " LEFT OUTER JOIN PARTOFEBOM B ON B.PARTOUID = ? AND B.PRODUCTOUID = ? "
			+ " START WITH AS$END1 = ? "
			+ " CONNECT BY PRIOR AS$END2 = AS$END1 "
			+ " ORDER SIBLINGS BY CAST(MD$SEQUENCE AS NUMBER DEFAULT 0 ON CONVERSION ERROR) ";

	private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
		if (value != null)
			map.put(key, value);
	}
}
