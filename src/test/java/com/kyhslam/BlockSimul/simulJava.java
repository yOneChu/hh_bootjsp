package com.kyhslam.BlockSimul;

import com.kyhslam.BlockSimul.BlockExceptions.HdelBusinessException;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * 블록 BOM 시뮬레이션 (BOMController.simulateBlock 의 독립 실행 버전)
 *
 * 실행 : main 의 productNoList / blockList / blockOPTList 값을 수정한 뒤 IDE 에서 바로 실행
 *   productNoList : 호기번호 목록
 *   blockList     : 블록번호 목록
 *   blockOPTList  : 블럭 품목 목록 (선택, 비어있지 않으면 blockList 대신 사용 / 호기는 1개만)
 *
 * 시스템 프로퍼티 (선택)
 *   -Dblock.errorlog=true   variant_errorlog 에 PID 오류 저장 (기본 false : 콘솔 출력만)
 */
public class simulJava {

	public static void main(String[] args) throws Exception {
		List<String> productNoList = new ArrayList<String>(Arrays.asList("N26143L01"));
		List<String> blockList     = new ArrayList<String>(Arrays.asList("E321A"));
		List<String> blockOPTList  = new ArrayList<String>(); // ex) Arrays.asList("C", "M")

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("productNoList", productNoList);
		data.put("blockList", blockList);
		data.put("blockOPTList", blockOPTList);

		long start = System.currentTimeMillis();
		List<SimulateBomVO> result = simulateBlock(data);

		for (SimulateBomVO vo : result)
			System.out.println(vo);
		System.out.println("--------------------------------------------------");
		System.out.println("count : " + result.size() + ", " + (System.currentTimeMillis() - start) + "ms");
	}

	/**
	 * BOMController.simulateBlock 와 동일한 입력/반환
	 * @param data productNoList(List), blockList(List), blockOPTList(List)
	 */
	public static List<SimulateBomVO> simulateBlock(Map<String, Object> data) throws Exception {
		if (!data.containsKey("productNoList"))
			throw new HdelBusinessException("no productNoList");
		if ((!data.containsKey("blockList")) && (!data.containsKey("blockOPTList")))
			throw new HdelBusinessException("no blockList");

		List<String> productNoList = toList(data.get("productNoList"));
		List<String> blockList = toList(data.get("blockList"));

		// 빈 블록번호 제거
		Iterator<String> it = blockList.iterator();
		while (it.hasNext()) {
			if ("".equals(BlockUtil.NVL(it.next(), "")))
				it.remove();
		}

		List<String> blockOPTList = toList(data.get("blockOPTList"));

		if (blockOPTList.size() > 0) {
			if (blockOPTList.size() > BlockConsts.MAX_BLOCK_OPT_COUNT)
				throw new HdelBusinessException("블럭 품목은 최대 3개까지 선택 가능합니다.");
			if (productNoList.size() > 1)
				throw new HdelBusinessException("블럭 품목 선택시 호기번호는 한 현장만 입력 바랍니다.");

			List<BlockInfo> blockinfoList;
			try (BlockDb db = BlockDb.open()) {
				blockinfoList = findBLocksByOPT(db, blockOPTList);
			}
			blockList.clear();
			for (int i = 0; i < blockinfoList.size(); i++)
				blockList.add(blockinfoList.get(i).getBlockNo());

			String tempnum = productNoList.get(0);
			productNoList.clear();
			productNoList.add(tempnum);
		}

		return simulateBlockBatch(productNoList, blockList);
	}

	/** SubaeDaoImpl.findBLocksByOPT */
	public static List<BlockInfo> findBLocksByOPT(BlockDb db, List<String> OPTList) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT LOWER(CONCAT('blockno$sf@', DECTOHEX(SF$OUID))) OUID, SF$OUID LOUID, MD$NUMBER BLOCKNO, MD$DESC BLOCKNAME, cod(FLOOR_PART) FLOOR_PART, A.* FROM BLOCKNO$SF A");
		sql.append(" WHERE cod(block_opt) IN ('',");
		sql.append(OPTList.stream().map(o -> "?").collect(Collectors.joining(",")));
		sql.append(") ORDER BY MD$NUMBER ");

		return BlockSubaeManager.toBlockInfoList(db.queryForList(sql.toString(), OPTList.toArray()));
	}

	/**
	 * EBOMServiceImpl.simulateBlockBatch : 호기별 병렬(4) 실행 후 호기, 블록 순 정렬
	 * 호기별 스레드마다 PLMDBConnection 커넥션을 하나씩 열고 닫는다.
	 */
	public static List<SimulateBomVO> simulateBlockBatch(List<String> productNoList, List<String> blockList) {
		List<SimulateBomVO> res = Collections.synchronizedList(new ArrayList<SimulateBomVO>());

		List<String> distinctProductNoList = productNoList.stream().distinct().collect(Collectors.toList());
		List<String> distinctBlockList = blockList.stream().distinct().collect(Collectors.toList());

		BlockPidRepository pidRepository = new BlockPidRepository();
		boolean saveErrorLog = Boolean.getBoolean("block.errorlog");

		ForkJoinPool forkJoinPool = new ForkJoinPool(BlockConsts.SIMULATE_THREAD_COUNT);
		try {
			forkJoinPool.submit(() -> distinctProductNoList.parallelStream().map(productNo -> {
				try (BlockDb db = BlockDb.open()) {
					return simulateBlock(new BlockContext(db, pidRepository, saveErrorLog), productNo, distinctBlockList);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).forEach(res::addAll)).get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			forkJoinPool.shutdown();
		}

		return res.stream().sorted(Comparator.comparing(SimulateBomVO::getProductNo).thenComparing(SimulateBomVO::getBlockNo4Order))
				.collect(Collectors.toList());
	}

	/** EBOMServiceImpl.simulateBlock : 호기 1건 */
	public static List<SimulateBomVO> simulateBlock(BlockContext ctx, String productNo, List<String> blockList) throws Exception {
		BlockSpecLoader loader = ctx.getSpecLoader();

		String consOuid = loader.getConsOuid(productNo);
		if (consOuid == null)
			throw new HdelBusinessException("공사정보가 없습니다. productNo=" + productNo);

		BlockSubaeManager subaeManager = new BlockSubaeManager(ctx, consOuid);
		Map<String, String[]> topLevelBomMap4Simulate = subaeManager.bomSimulate(blockList);

		// filter topLevelBomMap4Simulate's value[1] is not 0
		topLevelBomMap4Simulate = topLevelBomMap4Simulate.entrySet().stream()
				.filter(o -> !BigDecimal.ZERO.equals(BlockUtil.parseBigDecimal(o.getValue()[1])))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		Map<String, Map<String, String>> topLevelPartMap4simulate = new HashMap<String, Map<String, String>>();
		for (String partOuid : topLevelBomMap4Simulate.keySet())
			topLevelPartMap4simulate.put(partOuid, loader.getPartInfo(partOuid));

		Map<String, List<Map<String, String>>> variablePartMap4Simulate = subaeManager.getVariablePartMap4Simulate();

		String productOuid = loader.findWipProductOuid(productNo);
		List<Map<String, Object>> topLevelBomList = new ArrayList<>();
		if (productOuid != null)
			topLevelBomList = new BlockEBomReader(ctx.getDb()).getTopLevelOrderBom(productOuid);

		List<SimulateBomVO> simulateResultList = new ArrayList<>();
		for (Entry<String, String[]> o : topLevelBomMap4Simulate.entrySet()) {
			Map<String, String> simulatePartInfo = topLevelPartMap4simulate.get(o.getKey());

			SimulateBomVO simulateBomVO = new SimulateBomVO();
			simulateBomVO.setProductNo(productNo);
			simulateBomVO.setBlockNo(simulatePartInfo.get("blockno_number"));
			simulateBomVO.setSimulatePartNo(simulatePartInfo.get("md$number"));
			simulateBomVO.setSimulatePartOuid(simulatePartInfo.get("ouid"));
			simulateBomVO.setSimulateQty(BlockUtil.parseBigDecimal(o.getValue()[1]));
			simulateBomVO.setSimulateCmt(o.getValue()[0]);
			simulateBomVO.setSimulateGlcode(simulatePartInfo.get("g_l_code"));
			simulateBomVO.setSimulateSpec(simulatePartInfo.get("spec"));
			simulateBomVO.setSimulatePartSize(simulatePartInfo.get("part_size"));

			List<Map<String, String>> variablePartData = variablePartMap4Simulate.get(o.getKey());
			if (variablePartData != null)
				simulateBomVO.setSimulateVariableData(Base64.getEncoder().encodeToString(toJson(variablePartData).getBytes()));

			simulateResultList.add(simulateBomVO);
		}

		// 1. 같은 파트번호끼리 현재 BOM 값 매칭
		for (Map<String, Object> o : topLevelBomList) {
			if (Boolean.TRUE.equals(o.get("skip")) || !blockList.contains(o.get("blockNo_org")))
				continue;
			String partNo = (String) o.get("partNo");

			SimulateBomVO simulateBomVO = simulateResultList.stream()
					.filter(b -> b.getBomPartOuid() == null && partNo.equals(b.getSimulatePartNo()))
					.findFirst()
					.orElse(null);

			if (simulateBomVO != null) {
				acceptBomData(simulateBomVO, o);
				o.put("skip", Boolean.TRUE);
			}
		}

		// 2. 같은 블록끼리 매칭, 없으면 현재 BOM 만 있는 항목(DELETE) 추가
		for (Map<String, Object> o : topLevelBomList) {
			if (Boolean.TRUE.equals(o.get("skip")) || !blockList.contains(o.get("blockNo_org")))
				continue;
			String blockNo = (String) o.get("blockNo_org");

			SimulateBomVO simulateBomVO = simulateResultList.stream()
					.filter(b -> b.getBomPartOuid() == null && blockNo.equals(b.getBlockNo()))
					.findFirst()
					.orElse(null);

			if (simulateBomVO != null) {
				acceptBomData(simulateBomVO, o);
				o.put("skip", Boolean.TRUE);
			} else {
				simulateBomVO = new SimulateBomVO();
				acceptBomData(simulateBomVO, o);
				simulateBomVO.setProductNo(productNo);
				simulateBomVO.setBlockNo(blockNo);
				simulateResultList.add(simulateBomVO);
			}
		}

		return simulateResultList;
	}

	/** 현재 BOM 값 설정 */
	private static void acceptBomData(SimulateBomVO vo, Map<String, Object> item) {
		vo.setBomPartNo((String) item.get("partNo"));
		vo.setBomPartOuid(BlockConsts.PREFIX_NORMALPART_OUID + BlockUtil.deciTohex(item.get("ouid").toString()));
		vo.setBomQty(BlockUtil.parseBigDecimal((String) item.get("qty")));
		vo.setBomCmt((String) item.get("cmt"));
		vo.setBomGlcode((String) item.get("glCode"));
		vo.setBomSpec((String) item.get("spec"));
		vo.setBomPartSize((String) item.get("part_size"));
		vo.setBomUCheck("1".equals(item.get("uCheck")));	// 수정 Flag
	}

	// ------------------------------------------------------------------------

	/** Jackson ObjectMapper.writeValueAsString(List<Map<String,String>>) 와 같은 형식의 JSON */
	static String toJson(List<Map<String, String>> list) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) sb.append(',');
			sb.append('{');
			boolean first = true;
			for (Entry<String, String> e : list.get(i).entrySet()) {
				if (!first) sb.append(',');
				first = false;
				appendJsonString(sb, e.getKey());
				sb.append(':');
				if (e.getValue() == null)
					sb.append("null");
				else
					appendJsonString(sb, e.getValue());
			}
			sb.append('}');
		}
		return sb.append(']').toString();
	}

	private static void appendJsonString(StringBuilder sb, String s) {
		sb.append('"');
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '"': sb.append("\\\""); break;
				case '\\': sb.append("\\\\"); break;
				case '\n': sb.append("\\n"); break;
				case '\r': sb.append("\\r"); break;
				case '\t': sb.append("\\t"); break;
				case '\b': sb.append("\\b"); break;
				case '\f': sb.append("\\f"); break;
				default:
					if (c < 0x20)
						sb.append(String.format("\\u%04X", (int) c));
					else
						sb.append(c);
			}
		}
		sb.append('"');
	}

	static List<String> toList(Object obj) {
		List<String> list = new ArrayList<String>();
		if (obj instanceof List) {
			for (Object o : (List) obj)
				list.add(o == null ? null : o.toString());
		}
		return list;
	}

	static List<String> splitArg(String arg) {
		List<String> list = new ArrayList<String>();
		if (arg != null && !arg.trim().isEmpty())
			list.addAll(Arrays.asList(arg.split(",")));
		return list;
	}
}
