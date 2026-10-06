package com.kyhslam.service.simulate;

import com.kyhslam.util.simulate.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 제품(호기)의 PICK 정보 추출 (searchProductOneLevelJson.jsp 의 SubaeManagerPick.bomCalculate)
 *
 * 결과
 *   pickMap      : 품번 → PICK (하나의 품번에 PICK 이 여러개면 "PICK1&PICK2")
 *   separateInfo : "품번-PICK" → 수량
 *   bomList      : BOM 행 목록 (BLOCKNO 순)
 */
@Slf4j
@Service
public class PickSimulateService {

    /** searchProductOneLevelJson.jsp 의 inputOptList */
    public static final List<String> DEFAULT_OPT_LIST = Arrays.asList("C", "M", "F", "1", "2", "3");

    /** DOS 메타테이블(dosclas, dosfld ...) 스키마 */
    private static final String META_SCHEMA = "HDEL_SYSTEM";

    /**
     * 호기번호로 PICK 정보를 추출한다. (옵션 목록은 DEFAULT_OPT_LIST)
     * @param productNumber 호기번호 ex) 204201L11
     * @return pickMap(품번 → PICK), separateInfo("품번-PICK" → 수량), bomList(BOM 행 목록, BLOCKNO 순)
     */
    public HashMap<String, Object> getPick(String productNumber) throws Exception {
        return getPick(productNumber, DEFAULT_OPT_LIST);
    }

    public HashMap<String, Object> getPick(String productNumber, List<String> optList) throws Exception {
        return getPick(productNumber, optList, null);
    }

    /**
     * @param blockNos 계산할 BLOCKNO (null 또는 비어있으면 전체 블럭).
     *                 지정하면 해당 블럭 계산에 필요한 EL_P PID 만 실행한다. (PickElPSelector)
     */
    public HashMap<String, Object> getPick(String productNumber, List<String> optList, List<String> blockNos) throws Exception {
        long start = System.currentTimeMillis();
        HashMap<String, String> separateInfo = new HashMap<String, String>();
        List<Map<String, Object>> bomList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> pickMap;

        try (PidDb db = PidDb.open()) {
            PidSpecLoader loader = new PidSpecLoader(db, META_SCHEMA);

            // PartCommonUtil.getLatestElvInfoOid + ELV_INFO$VF 조회
            String elvOuid = loader.findLatestElvOuid(productNumber.trim());
            pickMap = bomCalculate(db, loader, elvOuid, optList, blockNos, separateInfo, bomList);
        }
        log.info("[getPick] {} 완료 : 전체 {}ms, BOM {}건", productNumber, System.currentTimeMillis() - start, bomList.size());

        // BLOCKNO 순 정렬 (같은 블럭 안에서는 계산 순서 유지)
        bomList.sort(Comparator.comparing(row -> PidUtil.NVL(row.get("BLOCKNO"), "")));

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("pickMap", pickMap);
        result.put("separateInfo", separateInfo);
        result.put("bomList", bomList);
        return result;
    }

    /**
     * SubaeManagerPick.bomCalculate (영업사양 elv_info 만 지원)
     * @param elvOuid ex) elv_info$vf@a810066d
     */
    private HashMap<String, Object> bomCalculate(PidDb db, PidSpecLoader loader, String elvOuid, List<String> optList, List<String> blockNos,
            HashMap<String, String> separateInfo, List<Map<String, Object>> bomList) throws Exception {
        if (!elvOuid.startsWith(PidConsts.PREFIX_ELVINFO_OUID))
            throw new IllegalArgumentException("영업사양(elv_info) ouid 가 아닙니다 : " + elvOuid);

        PickDao dao = new PickDao(db);
        boolean byBlock = blockNos != null && !blockNos.isEmpty();
        long t = System.currentTimeMillis();

        //1.영업사양 값 셋팅
        PidSpecLoader.SpecObject elvMaster = loader.load(elvOuid);
        if (elvMaster == null)
            throw new Exception("영업사양 객체가 없습니다. ouid=" + elvOuid);
        HashMap elvDataMap = elvMaster.dataMap;
        t = logStep("1.영업사양 로드", t);

        //2. 층 정보 셋팅 (층 dataMap 에 영업사양 값 포함, md$index 순)
        //   층 정보는 요청한 경우에만 읽는다 (PidConsts.USE_FLOOR, 테이블 코드는 PidConsts 상수)
        List<Map> floorMasterList = new ArrayList<Map>();
        if (PidConsts.USE_FLOOR) {
            for (PidSpecLoader.SpecObject floor : loader.loadFloors(elvOuid, elvDataMap)) {
                floorMasterList.add(floor.dataMap);
            }
            t = logStep("2.층 정보 로드 (" + floorMasterList.size() + "개)", t);
        }

        //3. 블럭 목록 (ProductInfoManager.compareBlockOption 은 optList 를 그대로 사용)
        //   EL_P 결과와 무관하므로 EL_P 선별을 위해 먼저 조회한다.
        List<PickDao.BlockInfo> blockList = dao.getBlockList(optList, false, blockNos);
        List<PickDao.BlockInfo> floorBlockList = floorMasterList.isEmpty() ? null : dao.getBlockList(optList, true, blockNos);
        t = logStep("3.블럭 목록 (" + blockList.size() + (floorBlockList == null ? "" : " / 층 " + floorBlockList.size()) + "개)", t);

        //4. EL_P 계산 (SubaeManagerPick.calculate_EL_P)
        PickElP el_p = new PickElP(db, loader, dao);
        if (byBlock) {
            PickElPSelector selector = new PickElPSelector(db);
            Set<String> pidFilter = selector.select(el_p.getAllPidList(), Arrays.asList(blockList, floorBlockList));
            el_p.setPidFilter(pidFilter);
            t = logStep("4-1.EL_P 선별 (" + selector.getReason() + ", "
                    + (pidFilter == null ? "전체 실행" : pidFilter.size() + "/" + el_p.getAllPidList().size() + "개 실행") + ")", t);
        }
        calculate_EL_P(el_p, elvDataMap, floorMasterList);
        for (Map floorDataMap : floorMasterList) {
            floorDataMap.putAll(elvDataMap);
        }
        t = logStep("4.EL_P 계산 (PID " + el_p.getExecutedCount() + "회 실행)", t);

        //5. PICK & 수량 계산
        HashMap<String, Object> pickMap = pickAndCalculatePid(db, loader, dao, elvDataMap, floorMasterList, blockList, floorBlockList, separateInfo, bomList);
        logStep("5.PICK & 수량 계산", t);
        return pickMap;
    }

    private long logStep(String step, long since) {
        long now = System.currentTimeMillis();
        log.info("[getPick] {} : {}ms", step, now - since);
        return now;
    }

    /** SubaeManagerPick.calculate_EL_P */
    private void calculate_EL_P(PickElP el_p, HashMap elvDataMap, List<Map> floorMasterList) throws Exception {
        el_p.make_EL_P_Data(elvDataMap, floorMasterList, false);

        if (floorMasterList.isEmpty())
            return;

        // 원본과 동일하게 층별 EL_P 를 2회 수행 (1회차 결과가 2회차 입력에 반영됨)
        for (int pass = 0; pass < 2; pass++) {
            for (Map floorDataMap : floorMasterList) {
                el_p.make_EL_P_Data((HashMap) floorDataMap, floorMasterList, true);
            }
        }
    }

    /** SubaeManagerPick.pickAndCalculatePid */
    private HashMap<String, Object> pickAndCalculatePid(PidDb db, PidSpecLoader loader, PickDao dao, HashMap elvDataMap, List<Map> floorMasterList,
            List<PickDao.BlockInfo> blockList, List<PickDao.BlockInfo> floorBlockList, HashMap<String, String> separateInfo, List<Map<String, Object>> bomList) throws Exception {

        HashMap<String, Object> pickMap = new HashMap<String, Object>();

        // Common Block Pick & PID Calculate
        {
            PickVariableAction variableAction = new PickVariableAction(db, loader, dao, elvDataMap, floorMasterList);
            for (PickDao.BlockInfo blockInfo : blockList) {
                findBom(dao, elvDataMap, variableAction, blockInfo, "", pickMap, separateInfo, bomList);
            }
        }

        // Floor Block Pick & PID Calculate
        if (floorBlockList != null) {
            int floorNo = 0;
            for (Map floorDataMap : floorMasterList) {
                floorNo++;
                PickVariableAction variableAction = new PickVariableAction(db, loader, dao, (HashMap) floorDataMap, floorMasterList);
                for (PickDao.BlockInfo blockInfo : floorBlockList) {
                    findBom(dao, floorDataMap, variableAction, blockInfo, String.valueOf(floorNo), pickMap, separateInfo, bomList);
                }
            }
        }

        return pickMap;
    }

    /** SubaeManagerPick.findBom */
    private void findBom(PickDao dao, Map dataMap, PickVariableAction vAction, PickDao.BlockInfo blockInfo, String floorNo,
            HashMap<String, Object> oMap, HashMap<String, String> separateInfo, List<Map<String, Object>> bomList) throws Exception {

        for (PickDao.PickInfo pickInfo : blockInfo.pickList) {
            String pick = pickInfo.pick;
            if (pick != null) {
                pick = pick.trim();
            }

            Map<String, Object> picked = dao.pickPart(dataMap, blockInfo.ouid, pick);
            if (picked == null)
                continue;

            String partNo = (String) picked.get("PARTNO");

            //하나의 품번에 PICK번호 여러개일 경우 합치기
            if (!"".equals(pick)) {
                if (oMap.containsKey(partNo)) {
                    String tempValue = (String) oMap.get(partNo);

                    if (!tempValue.contains(pick)) {
                        tempValue += "&" + pick;
                        oMap.put(partNo, tempValue);
                    }
                } else {
                    oMap.put(partNo, pick);
                }
            }

            // Lv1 PID Calculate (수량)
            picked.put("PICK", pick);
            String qty = vAction.get1LevelQty(picked, pickInfo.qty);

            separateInfo.put(partNo + "-" + pick, qty);

            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("BLOCKNO", blockInfo.blockNo);
            row.put("BLOCKNAME", blockInfo.blockName);
            row.put("FLOOR", floorNo);
            row.put("PICK", pick);
            row.put("PICK_VALUE", PidUtil.NVL(dataMap.get(pick), ""));
            row.put("PARTNO", partNo);
            row.put("G_L_CODE", PidUtil.NVL(picked.get("G_L_CODE"), ""));
            row.put("SPEC", PidUtil.NVL(picked.get("SPEC"), ""));
            row.put("PART_SIZE", PidUtil.NVL(picked.get("PART_SIZE"), ""));
            row.put("QTY", qty);
            row.put("VER", PidUtil.NVL(picked.get("VER"), ""));
            row.put("ORIGIN_DIV", PidUtil.NVL(picked.get("ORIGIN_DIV"), ""));
            row.put("SPT", PidUtil.NVL(picked.get("SPT"), ""));
            row.put("HASCHILD", PidUtil.parseInt(picked.get("HASCHILD")) > 0 ? "Y" : "N");
            row.put("CMT", pickInfo.cmt);
            bomList.add(row);
        }
    }
}
