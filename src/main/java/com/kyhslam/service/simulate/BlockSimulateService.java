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
        return simulateBlock(productNoList, blockList, blockOPTList,
                testBlockPid ? (Integer) PidConsts.TEST_VERSION : null, testElpPid ? (Integer) PidConsts.TEST_VERSION : null);
    }

    /**
     * 입력 블럭의 PID 를 고른 버전으로 계산한다.
     * @param blockPidVersion 입력 블럭의 PICK/PID (ex. B128B08) 버전. null : 최신, -1 : 테스트 버전 우선(하위 PID 포함), 그 외 : 그 PID 만 지정 버전
     * @param elpPidVersion   입력 블럭의 EL_P 블럭 PID (ex. EL_PB128B08) 버전 (나머지 EL_P 는 최신)
     */
    public List<SimulateBomVO> simulateBlock(List<String> productNoList, List<String> blockList, List<String> blockOPTList,
                                             Integer blockPidVersion, Integer elpPidVersion) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("productNoList", productNoList);
        data.put("blockList", blockList);
        data.put("blockOPTList", blockOPTList);
        data.put("blockPidVersion", blockPidVersion);
        data.put("elpPidVersion", elpPidVersion);

        long start = System.currentTimeMillis();
        List<SimulateBomVO> result = simulateBlock(data);
        log.info("[simulateBlock] {} / {}{}{} 완료 : {}ms, {}건", productNoList, blockList,
                blockPidVersion == null ? "" : " (블럭 PID 버전 " + blockPidVersion + ")",
                elpPidVersion == null ? "" : " (EL_P 블럭 PID 버전 " + elpPidVersion + ")",
                System.currentTimeMillis() - start, result.size());
        return result;
    }

    /**
     * BOMController.simulateBlock 와 동일한 입력/반환
     * @param data productNoList(List), blockList(List), blockOPTList(List),
     *             blockPidVersion(Integer, 선택), elpPidVersion(Integer, 선택),
     *             testBlockPid(Boolean, 선택 : blockPidVersion = -1), testElpPid(Boolean, 선택 : elpPidVersion = -1)
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
            try (BlockDb db = BlockDb.openPooled()) {
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

        Integer blockPidVersion = toVersion(data.get("blockPidVersion"), data.get("testBlockPid"));
        Integer elpPidVersion = toVersion(data.get("elpPidVersion"), data.get("testElpPid"));
        // 지정 버전(-1 이외)은 블럭 1개를 입력했을 때만 쓴다 (블럭마다 버전이 다르므로)
        if (isPinned(blockPidVersion) || isPinned(elpPidVersion)) {
            if (blockOPTList.size() > 0 || blockList.stream().distinct().count() != 1)
                throw new HdelBusinessException("PID 버전 지정은 블럭번호 1개를 입력했을 때만 가능합니다.");
        }

        return simulateBlockBatch(productNoList, blockList, blockPidVersion, elpPidVersion);
    }

    /** blockPidVersion / elpPidVersion 값 (없으면 testXxxPid 가 true 일 때 -1, 아니면 null : 최신) */
    private static Integer toVersion(Object version, Object test) {
        if (version != null && !"".equals(String.valueOf(version)))
            return Integer.valueOf(String.valueOf(version).trim());
        return Boolean.TRUE.equals(test) ? (Integer) PidConsts.TEST_VERSION : null;
    }

    private static boolean isPinned(Integer version) {
        return version != null && version != PidConsts.TEST_VERSION;
    }

    /**
     * 고른 버전으로 계산할 PID 저장소 (null : 최신 버전 공유 캐시를 그대로 사용)
     * -1 은 기존 테스트 모드(하위 PID 도 테스트 버전 우선), 그 외 버전은 pids 만 지정 버전이고 하위 PID 는 최신이다.
     * 고른 버전은 같은 버전을 직접 고쳐 가며 쓸 수 있으므로 공유 캐시를 쓰지 않고 요청마다 새로 읽는다.
     */
    private static BlockPidRepository versionPidRepository(Integer version, Collection<String> pids) {
        if (version == null)
            return null;
        if (version == PidConsts.TEST_VERSION)
            return new BlockPidRepository(true);
        Map<String, Integer> pinned = new HashMap<String, Integer>();
        for (String pid : pids)
            pinned.put(pid, version);
        return new BlockPidRepository(false, pinned);
    }

    /** EL_P(SH_P, SV_P) + 블럭번호 PID 목록 */
    private static List<String> elpPids(Collection<String> blockList) {
        List<String> pids = new ArrayList<String>();
        for (String prefix : ELP_PREFIXES) {
            for (String blockNo : blockList)
                pids.add(prefix.replace("%", "") + blockNo);
        }
        return pids;
    }

    private static final String[] ELP_PREFIXES = { BlockConsts.EL_P_PREFIX, BlockConsts.SHIPEL_P_PREFIX, BlockConsts.SVEL_P_PREFIX };

    /**
     * 블럭 1개의 PID 버전 목록 (화면의 버전 선택용)
     * @return block : 블럭 PID (ex. B128B08) 버전, elp : EL_P(SH_P, SV_P) 블럭 PID 버전.
     *         각 항목은 {version, latest}. 테스트 버전(-1)이 맨 앞, 나머지는 버전 내림차순
     */
    public Map<String, Object> getPidVersions(String blockNo) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        try (BlockDb db = BlockDb.openPooled()) {
            result.put("block", findPidVersions(db, Collections.singletonList(blockNo)));
            result.put("elp", findPidVersions(db, elpPids(Collections.singletonList(blockNo))));
        }
        return result;
    }

    private List<Map<String, Object>> findPidVersions(BlockDb db, List<String> pids) throws Exception {
        String sql = " SELECT A.VERSION, MAX(CASE WHEN B.LAST_HOUID IS NULL THEN 'N' ELSE 'Y' END) LATEST "
                + " FROM VARIANT_H A LEFT JOIN VARIANT_ID B ON B.LAST_HOUID = A.HOUID "
                + " WHERE A.PID IN (" + pids.stream().map(p -> "?").collect(Collectors.joining(",")) + ") "
                + " GROUP BY A.VERSION ";
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, String> row : db.queryForList(sql, pids.toArray())) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("version", BlockUtil.parseInt(row.get("VERSION")));
            m.put("latest", "Y".equals(row.get("LATEST")));
            list.add(m);
        }
        list.sort(Comparator.comparing((Map<String, Object> m) -> (Integer) m.get("version") != PidConsts.TEST_VERSION)
                .thenComparing(m -> (Integer) m.get("version"), Comparator.reverseOrder()));
        return list;
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
    private List<SimulateBomVO> simulateBlockBatch(List<String> productNoList, List<String> blockList, Integer blockPidVersion, Integer elpPidVersion) {
        List<SimulateBomVO> res = Collections.synchronizedList(new ArrayList<SimulateBomVO>());

        List<String> distinctProductNoList = productNoList.stream().distinct().collect(Collectors.toList());
        List<String> distinctBlockList = blockList.stream().distinct().collect(Collectors.toList());

        // 기본은 최신 버전(공유 캐시). 고른 대상(블럭 PID / EL_P 블럭 PID)만 고른 버전의 저장소로 계산한다.
        BlockPidRepository pidRepository = getPidRepository();
        BlockPidRepository blockPidRepository = versionPidRepository(blockPidVersion, distinctBlockList);
        BlockPidRepository elpPidRepository = versionPidRepository(elpPidVersion, elpPids(distinctBlockList));
        boolean saveErrorLog = Boolean.getBoolean("block.errorlog");

        ForkJoinPool forkJoinPool = new ForkJoinPool(BlockConsts.SIMULATE_THREAD_COUNT);
        try {
            forkJoinPool.submit(() -> distinctProductNoList.parallelStream().map(productNo -> {
                try (BlockDb db = BlockDb.openPooled()) {
                    return simulateBlock(new BlockContext(db, pidRepository, blockPidRepository, elpPidRepository, elpPidVersion, saveErrorLog), productNo, distinctBlockList);
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
        try (BlockDb db = BlockDb.openPooled()) {
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
