package com.kyhslam.service.simulate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyhslam.util.simulate.*;
import com.kyhslam.util.simulate.BlockExceptions.HdelBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * 블록 BOM 시뮬레이션 (BOMController.simulateBlock, test 의 BlockSimul.simulJava 운영 버전)
 *
 * 입력
 *   productNoList : 호기번호 목록
 *   blockList     : 블록번호 목록
 *   blockOPTList  : 블럭 품목 목록 (선택, 비어있지 않으면 blockList 대신 사용 / 호기는 1개만)
 *
 * 시스템 프로퍼티 (선택)
 *   -Dblock.errorlog=true   variant_errorlog 에 PID 오류 저장 (기본 false : 콘솔 출력만)
 */
@Slf4j
@Service
public class BlockSimulateService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** PID 캐시 최대 유지시간 (변경 감지를 못 하는 같은 버전 직접 수정 대비) */
    private static final long PID_CACHE_TTL_MS = 10 * 60 * 1000L;

    /** 요청 간 공유하는 PID 캐시. VARIANT_ID 가 바뀌었거나 TTL 이 지나면 새로 만든다. */
    private BlockPidRepository pidRepository;
    private String pidStamp;
    private long pidLoadedAt;

    /**
     * 호기/블록 목록으로 시뮬레이션한다.
     * @param productNoList 호기번호 목록 ex) N26143L01
     * @param blockList     블록번호 목록 ex) E321A
     * @param blockOPTList  블럭 품목 목록 (null 가능) ex) C, M
     */
    public List<SimulateBomVO> simulateBlock(List<String> productNoList, List<String> blockList, List<String> blockOPTList) throws Exception {
        return simulateBlock(productNoList, blockList, blockOPTList, false, false);
    }

    /**
     * 입력 블럭의 PID 를 선택적으로 테스트 버전(VERSION = -1, 없으면 최신)으로 계산한다.
     * @param testBlockPid true 면 입력 블럭의 PICK/PID (ex. B128B08) 를 테스트 버전으로
     * @param testElpPid   true 면 입력 블럭의 EL_P 블럭 PID (ex. EL_PB128B08) 를 테스트 버전으로 (나머지 EL_P 는 최신)
     */
    public List<SimulateBomVO> simulateBlock(List<String> productNoList, List<String> blockList, List<String> blockOPTList,
                                             boolean testBlockPid, boolean testElpPid) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("productNoList", productNoList);
        data.put("blockList", blockList);
        data.put("blockOPTList", blockOPTList);
        data.put("testBlockPid", testBlockPid);
        data.put("testElpPid", testElpPid);

        long start = System.currentTimeMillis();
        List<SimulateBomVO> result = simulateBlock(data);
        log.info("[simulateBlock] {} / {}{}{} 완료 : {}ms, {}건", productNoList, blockList,
                testBlockPid ? " (블럭 PID 테스트)" : "", testElpPid ? " (EL_P 블럭 PID 테스트)" : "",
                System.currentTimeMillis() - start, result.size());
        return result;
    }

    /**
     * BOMController.simulateBlock 와 동일한 입력/반환
     * @param data productNoList(List), blockList(List), blockOPTList(List), testBlockPid(Boolean, 선택), testElpPid(Boolean, 선택)
     */
    public List<SimulateBomVO> simulateBlock(Map<String, Object> data) throws Exception {
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

            long t = System.currentTimeMillis();
            List<BlockInfo> blockinfoList;
            try (BlockDb db = BlockDb.open()) {
                blockinfoList = findBLocksByOPT(db, blockOPTList);
            }
            log.info("[simulateBlock] 품목 {} 블럭 조회 : {}ms, 블럭 {}개", blockOPTList, System.currentTimeMillis() - t, blockinfoList.size());
            blockList.clear();
            for (int i = 0; i < blockinfoList.size(); i++)
                blockList.add(blockinfoList.get(i).getBlockNo());

            String tempnum = productNoList.get(0);
            productNoList.clear();
            productNoList.add(tempnum);
        }

        return simulateBlockBatch(productNoList, blockList,
                Boolean.TRUE.equals(data.get("testBlockPid")), Boolean.TRUE.equals(data.get("testElpPid")));
    }

    /** SubaeDaoImpl.findBLocksByOPT */
    private List<BlockInfo> findBLocksByOPT(BlockDb db, List<String> OPTList) throws Exception {
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
    private List<SimulateBomVO> simulateBlockBatch(List<String> productNoList, List<String> blockList, boolean testBlockPid, boolean testElpPid) {
        List<SimulateBomVO> res = Collections.synchronizedList(new ArrayList<SimulateBomVO>());

        List<String> distinctProductNoList = productNoList.stream().distinct().collect(Collectors.toList());
        List<String> distinctBlockList = blockList.stream().distinct().collect(Collectors.toList());

        // 기본은 최신 버전(공유 캐시). 고른 대상(블럭 PID / EL_P 블럭 PID)만 테스트 버전 우선 저장소로 계산한다.
        // 테스트 버전은 같은 버전(-1)을 직접 고쳐 가며 쓰므로 공유 캐시를 쓰지 않고 요청마다 새로 읽는다.
        BlockPidRepository pidRepository = getPidRepository();
        BlockPidRepository testPidRepository = testBlockPid || testElpPid ? new BlockPidRepository(true) : null;
        boolean saveErrorLog = Boolean.getBoolean("block.errorlog");

        ForkJoinPool forkJoinPool = new ForkJoinPool(BlockConsts.SIMULATE_THREAD_COUNT);
        try {
            forkJoinPool.submit(() -> distinctProductNoList.parallelStream().map(productNo -> {
                try (BlockDb db = BlockDb.open()) {
                    return simulateBlock(new BlockContext(db, pidRepository, testPidRepository, testBlockPid, testElpPid, saveErrorLog), productNo, distinctBlockList);
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

    /**
     * 공유 PID 캐시를 반환한다.
     * B. 요청마다 VARIANT_ID 의 건수/LAST_HOUID 해시를 비교해 PID 가 새 버전으로 바뀌었으면 새로 만든다.
     * A. 같은 버전을 직접 수정한 경우는 감지 못 하므로 PID_CACHE_TTL_MS 가 지나도 새로 만든다.
     */
    private synchronized BlockPidRepository getPidRepository() {
        String stamp;
        try (BlockDb db = BlockDb.open()) {
            Map<String, String> row = db.queryForFirst(
                    " SELECT COUNT(1) CNT, SUM(ORA_HASH(PID || '#' || LAST_HOUID)) HSUM FROM VARIANT_ID ");
            stamp = row == null ? null : row.get("CNT") + "#" + row.get("HSUM");
        } catch (Exception e) {
            log.warn("[simulateBlock] PID 변경 확인 실패, PID 캐시를 새로 만듭니다.", e);
            stamp = null;
        }

        long now = System.currentTimeMillis();
        String reason = null;
        if (pidRepository == null)
            reason = "최초";
        else if (stamp == null || !stamp.equals(pidStamp))
            reason = "PID 변경";
        else if (now - pidLoadedAt > PID_CACHE_TTL_MS)
            reason = "유효시간 경과";

        if (reason != null) {
            log.info("[simulateBlock] PID 캐시 새로 생성 ({})", reason);
            pidRepository = new BlockPidRepository();
            pidStamp = stamp;
            pidLoadedAt = now;
        }
        return pidRepository;
    }

    /** EBOMServiceImpl.simulateBlock : 호기 1건 */
    private List<SimulateBomVO> simulateBlock(BlockContext ctx, String productNo, List<String> blockList) throws Exception {
        BlockSpecLoader loader = ctx.getSpecLoader();

        long t = System.currentTimeMillis();
        String consOuid = loader.getConsOuid(productNo);
        if (consOuid == null)
            throw new HdelBusinessException("공사정보가 없습니다. productNo=" + productNo);

        BlockSubaeManager subaeManager = new BlockSubaeManager(ctx, consOuid);
        Map<String, String[]> topLevelBomMap4Simulate = subaeManager.bomSimulate(blockList);
        t = lap(productNo, "bomSimulate 합계", t);

        // filter topLevelBomMap4Simulate's value[1] is not 0
        topLevelBomMap4Simulate = topLevelBomMap4Simulate.entrySet().stream()
                .filter(o -> !BigDecimal.ZERO.equals(BlockUtil.parseBigDecimal(o.getValue()[1])))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        Map<String, Map<String, String>> topLevelPartMap4simulate = new HashMap<String, Map<String, String>>();
        for (String partOuid : topLevelBomMap4Simulate.keySet())
            topLevelPartMap4simulate.put(partOuid, loader.getPartInfo(partOuid));
        t = lap(productNo, "파트정보 조회 (" + topLevelBomMap4Simulate.size() + "건)", t);

        Map<String, List<Map<String, String>>> variablePartMap4Simulate = subaeManager.getVariablePartMap4Simulate();

        String productOuid = loader.findWipProductOuid(productNo);
        List<Map<String, Object>> topLevelBomList = new ArrayList<>();
        if (productOuid != null)
            topLevelBomList = new BlockEBomReader(ctx.getDb()).getTopLevelOrderBom(productOuid);
        t = lap(productNo, "현재 BOM 조회 (" + topLevelBomList.size() + "건)", t);

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
                simulateBomVO.setSimulateVariableData(Base64.getEncoder().encodeToString(OBJECT_MAPPER.writeValueAsString(variablePartData).getBytes()));

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
        lap(productNo, "결과 생성/BOM 매칭", t);

        return simulateResultList;
    }

    /** 구간 소요시간 로그 후 현재 시각 반환 */
    private long lap(String productNo, String step, long start) {
        long now = System.currentTimeMillis();
        log.info("[simulateBlock] {} - {} : {}ms", productNo, step, now - start);
        return now;
    }

    /** 현재 BOM 값 설정 */
    private void acceptBomData(SimulateBomVO vo, Map<String, Object> item) {
        vo.setBomPartNo((String) item.get("partNo"));
        vo.setBomPartOuid(BlockConsts.PREFIX_NORMALPART_OUID + BlockUtil.deciTohex(item.get("ouid").toString()));
        vo.setBomQty(BlockUtil.parseBigDecimal((String) item.get("qty")));
        vo.setBomCmt((String) item.get("cmt"));
        vo.setBomGlcode((String) item.get("glCode"));
        vo.setBomSpec((String) item.get("spec"));
        vo.setBomPartSize((String) item.get("part_size"));
        vo.setBomUCheck("1".equals(item.get("uCheck")));	// 수정 Flag
    }

    private List<String> toList(Object obj) {
        List<String> list = new ArrayList<String>();
        if (obj instanceof List) {
            for (Object o : (List) obj)
                list.add(o == null ? null : o.toString());
        }
        return list;
    }
}
