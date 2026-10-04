package com.kyhslam.BlockSimul;

import com.kyhslam.BlockSimul.BlockExceptions.HdelBusinessException;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * 블록 BOM 시뮬레이션 결과만 출력 (현재 BOM 과의 비교 없음)
 * simulJava 와 입력은 같고, 현재 BOM(PARTOFEBOM) 조회/매칭을 하지 않는다.
 * 반환되는 SimulateBomVO 는 simulate* 필드만 채워지고 bom* 필드는 비어 있다.
 *
 * 실행 : main 의 productNoList / blockList / blockOPTList 값을 수정한 뒤 IDE 에서 바로 실행
 *   productNoList : 호기번호 목록
 *   blockList     : 블록번호 목록
 *   blockOPTList  : 블럭 품목 목록 (선택, 비어있지 않으면 blockList 대신 사용 / 호기는 1개만)
 *
 * 시스템 프로퍼티 (선택)
 *   -Dblock.errorlog=true   variant_errorlog 에 PID 오류 저장 (기본 false : 콘솔 출력만)
 */
public class simulJava_One {

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

		printResult(result);
		System.out.println("--------------------------------------------------");
		System.out.println("count : " + result.size() + ", " + (System.currentTimeMillis() - start) + "ms");
	}

	/**
	 * 블록 시뮬레이션 결과만 반환
	 * @param data productNoList(List), blockList(List), blockOPTList(List)
	 */
	public static List<SimulateBomVO> simulateBlock(Map<String, Object> data) throws Exception {
		if (!data.containsKey("productNoList"))
			throw new HdelBusinessException("no productNoList");
		if ((!data.containsKey("blockList")) && (!data.containsKey("blockOPTList")))
			throw new HdelBusinessException("no blockList");

		List<String> productNoList = simulJava.toList(data.get("productNoList"));
		List<String> blockList = simulJava.toList(data.get("blockList"));

		// 빈 블록번호 제거
		Iterator<String> it = blockList.iterator();
		while (it.hasNext()) {
			if ("".equals(BlockUtil.NVL(it.next(), "")))
				it.remove();
		}

		List<String> blockOPTList = simulJava.toList(data.get("blockOPTList"));

		if (blockOPTList.size() > 0) {
			if (blockOPTList.size() > BlockConsts.MAX_BLOCK_OPT_COUNT)
				throw new HdelBusinessException("블럭 품목은 최대 3개까지 선택 가능합니다.");
			if (productNoList.size() > 1)
				throw new HdelBusinessException("블럭 품목 선택시 호기번호는 한 현장만 입력 바랍니다.");

			List<BlockInfo> blockinfoList;
			try (BlockDb db = BlockDb.open()) {
				blockinfoList = simulJava.findBLocksByOPT(db, blockOPTList);
			}
			blockList.clear();
			for (BlockInfo blockInfo : blockinfoList)
				blockList.add(blockInfo.getBlockNo());

			String tempnum = productNoList.get(0);
			productNoList.clear();
			productNoList.add(tempnum);
		}

		return simulateBlockBatch(productNoList, blockList);
	}

	/** 호기별 병렬(4) 실행 후 호기, 블록, 파트번호 순 정렬 */
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
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			forkJoinPool.shutdown();
		}

		return res.stream()
				.sorted(Comparator.comparing(SimulateBomVO::getProductNo)
						.thenComparing(SimulateBomVO::getBlockNo4Order)
						.thenComparing(vo -> BlockUtil.NVL(vo.getSimulatePartNo(), "")))
				.collect(Collectors.toList());
	}

	/** 호기 1건 : 블록 시뮬레이션 결과 (수량 0 인 자재는 제외) */
	public static List<SimulateBomVO> simulateBlock(BlockContext ctx, String productNo, List<String> blockList) throws Exception {
		BlockSpecLoader loader = ctx.getSpecLoader();

		String consOuid = loader.getConsOuid(productNo);
		if (consOuid == null)
			throw new HdelBusinessException("공사정보가 없습니다. productNo=" + productNo);

		BlockSubaeManager subaeManager = new BlockSubaeManager(ctx, consOuid);
		Map<String, String[]> topLevelBomMap4Simulate = subaeManager.bomSimulate(blockList);
		Map<String, List<Map<String, String>>> variablePartMap4Simulate = subaeManager.getVariablePartMap4Simulate();

		List<SimulateBomVO> simulateResultList = new ArrayList<>();
		for (Entry<String, String[]> o : topLevelBomMap4Simulate.entrySet()) {
			// 수량이 0 인 자재 제외 (simulJava 와 동일)
			if (BigDecimal.ZERO.equals(BlockUtil.parseBigDecimal(o.getValue()[1])))
				continue;

			Map<String, String> simulatePartInfo = loader.getPartInfo(o.getKey());

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
				simulateBomVO.setSimulateVariableData(Base64.getEncoder().encodeToString(simulJava.toJson(variablePartData).getBytes()));

			simulateResultList.add(simulateBomVO);
		}

		return simulateResultList;
	}

	/** 시뮬레이션 결과 콘솔 출력 */
	public static void printResult(List<SimulateBomVO> result) {
		System.out.println(String.format("%-12s %-8s %-16s %-16s %8s  %s", "PRODUCT", "BLOCK", "PART_NO", "GL_CODE", "QTY", "CMT"));
		for (SimulateBomVO vo : result) {
			System.out.println(String.format("%-12s %-8s %-16s %-16s %8s  %s",
					BlockUtil.NVL(vo.getProductNo(), ""),
					BlockUtil.NVL(vo.getBlockNo(), ""),
					BlockUtil.NVL(vo.getSimulatePartNo(), ""),
					BlockUtil.NVL(vo.getSimulateGlcode(), ""),
					vo.getSimulateQty() == null ? "" : vo.getSimulateQty().toPlainString(),
					BlockUtil.NVL(vo.getSimulateCmt(), "").replace("\n", " / ")));
		}
	}
}
