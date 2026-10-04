package com.kyhslam.api.pickExtrate;

import com.kyhslam.pidSimul.PidConsts;
import com.kyhslam.pidSimul.PidDb;
import com.kyhslam.pidSimul.PidSpecLoader;

import java.util.*;

/**
 * 제품(호기)의 PICK 정보 추출 (searchProductOneLevelJson.jsp 의 SubaeManagerPick.bomCalculate 독립 실행 버전)
 *
 * 실행 예)
 *   java -cp "classes;ojdbc6.jar" dyna.pickExtrate.getPick 204201L11
 *   java -cp "classes;ojdbc6.jar" dyna.pickExtrate.getPick 204201L11 C,M,F,1,2,3
 *
 * DB 접속 : PLMDBConnection.getConnection()
 *
 * 결과
 *   pickMap      : 품번 → PICK (하나의 품번에 PICK 이 여러개면 "PICK1&PICK2")
 *   separateInfo : "품번-PICK" → 수량
 *
 * 시스템 프로퍼티 (선택)
 *   -Dpid.meta.schema=HDEL_SYSTEM    DOS 메타테이블(dosclas, dosfld ...) 스키마
 *   -Dpid.errorlog=true              variant_errorlog 에 오류 저장 (기본 false : 콘솔 출력만)
 *   -Dpid.floor=Y                    층 정보 사용 (PidConsts.FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 설정 필요)
 */
public class getPick {

    /** searchProductOneLevelJson.jsp 의 inputOptList */
    public static final List<String> DEFAULT_OPT_LIST = Arrays.asList("C", "M", "F", "1", "2", "3");

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            //System.out.println("usage : getPick <productNumber> [optList ex) C,M,F,1,2,3]");
            //return;
        }

        System.out.println("start--");

        String productNumber = "TEST-632563"; //args[0].trim();
        List<String> optList = args.length > 1 ? Arrays.asList(args[1].split(",")) : DEFAULT_OPT_LIST;

        HashMap<String, String> separateInfo = new HashMap<String, String>();
        HashMap<String, Object> pickMap = getPickMap(productNumber, optList, separateInfo);

        System.out.println("pickMap : " + pickMap);
        System.out.println("separateInfo : " + separateInfo);


        System.out.println("------- end ----------");
    }

    /**
     * PLMDBConnection 으로 접속하여 PICK 정보를 추출한다.
     * @param separateInfo 결과 : "품번-PICK" → 수량 (null 불가)
     * @return 품번 → PICK
     */
    public static HashMap<String, Object> getPickMap(String productNumber, List<String> optList, HashMap<String, String> separateInfo) throws Exception {
        try (PidDb db = PidDb.open()) {
            return getPickMap(db, productNumber, optList, separateInfo);
        }
    }

    /** 외부 커넥션을 사용하는 경우 (커넥션은 호출측에서 닫는다) */
    public static HashMap<String, Object> getPickMap(PidDb db, String productNumber, List<String> optList, HashMap<String, String> separateInfo) throws Exception {
        PidSpecLoader loader = new PidSpecLoader(db, System.getProperty("pid.meta.schema", "HDEL_SYSTEM"));

        // PartCommonUtil.getLatestElvInfoOid + ELV_INFO$VF 조회
        String elvOuid = loader.findLatestElvOuid(productNumber.trim());
        return bomCalculate(db, loader, elvOuid, optList, separateInfo);
    }

    /**
     * SubaeManagerPick.bomCalculate (영업사양 elv_info 만 지원)
     * @param elvOuid ex) elv_info$vf@a810066d
     */
    public static HashMap<String, Object> bomCalculate(PidDb db, PidSpecLoader loader, String elvOuid, List<String> optList, HashMap<String, String> separateInfo) throws Exception {
        if (!elvOuid.startsWith(PidConsts.PREFIX_ELVINFO_OUID))
            throw new IllegalArgumentException("영업사양(elv_info) ouid 가 아닙니다 : " + elvOuid);

        PickDao dao = new PickDao(db);

        //1.영업사양 값 셋팅
        PidSpecLoader.SpecObject elvMaster = loader.load(elvOuid);
        if (elvMaster == null)
            throw new Exception("영업사양 객체가 없습니다. ouid=" + elvOuid);
        HashMap elvDataMap = elvMaster.dataMap;

        //2. 층 정보 셋팅 (층 dataMap 에 영업사양 값 포함, md$index 순)
        //   pidSimul 과 같이 층 정보는 요청한 경우에만 읽는다 (PidConsts.USE_FLOOR, 테이블 코드는 PidConsts 상수)
        List<Map> floorMasterList = new ArrayList<Map>();
        if (PidConsts.USE_FLOOR) {
            for (PidSpecLoader.SpecObject floor : loader.loadFloors(elvOuid, elvDataMap)) {
                floorMasterList.add(floor.dataMap);
            }
        }

        //3. EL_P 계산 (SubaeManagerPick.calculate_EL_P)
        calculate_EL_P(db, loader, dao, elvDataMap, floorMasterList);
        for (Map floorDataMap : floorMasterList) {
            floorDataMap.putAll(elvDataMap);
        }

        //4. 블럭 목록 (ProductInfoManager.compareBlockOption 은 optList 를 그대로 사용)
        List<PickDao.BlockInfo> blockList = dao.getBlockList(optList, false);
        List<PickDao.BlockInfo> floorBlockList = floorMasterList.isEmpty() ? null : dao.getBlockList(optList, true);

        //5. PICK & 수량 계산
        return pickAndCalculatePid(db, loader, dao, elvDataMap, floorMasterList, blockList, floorBlockList, separateInfo);
    }

    /** SubaeManagerPick.calculate_EL_P */
    private static void calculate_EL_P(PidDb db, PidSpecLoader loader, PickDao dao, HashMap elvDataMap, List<Map> floorMasterList) throws Exception {
        long start = System.nanoTime();

        PickElP el_p = new PickElP(db, loader, dao);
        el_p.make_EL_P_Data(elvDataMap, floorMasterList, false);

        if (floorMasterList.isEmpty())
            return;

        // 원본과 동일하게 층별 EL_P 를 2회 수행 (1회차 결과가 2회차 입력에 반영됨)
        for (int pass = 0; pass < 2; pass++) {
            for (Map floorDataMap : floorMasterList) {
                el_p.make_EL_P_Data((HashMap) floorDataMap, floorMasterList, true);
            }
        }

        System.out.println("calculate_EL_P() end : " + (System.nanoTime() - start) / 1000000 + "ms");
    }

    /** SubaeManagerPick.pickAndCalculatePid */
    private static HashMap<String, Object> pickAndCalculatePid(PidDb db, PidSpecLoader loader, PickDao dao, HashMap elvDataMap, List<Map> floorMasterList,
            List<PickDao.BlockInfo> blockList, List<PickDao.BlockInfo> floorBlockList, HashMap<String, String> separateInfo) throws Exception {

        HashMap<String, Object> pickMap = new HashMap<String, Object>();

        // Common Block Pick & PID Calculate
        {
            PickVariableAction variableAction = new PickVariableAction(db, loader, dao, elvDataMap, floorMasterList);
            for (PickDao.BlockInfo blockInfo : blockList) {
                findBom(dao, elvDataMap, variableAction, blockInfo, pickMap, separateInfo);
            }
        }

        // Floor Block Pick & PID Calculate
        if (floorBlockList != null) {
            for (Map floorDataMap : floorMasterList) {
                PickVariableAction variableAction = new PickVariableAction(db, loader, dao, (HashMap) floorDataMap, floorMasterList);
                for (PickDao.BlockInfo blockInfo : floorBlockList) {
                    findBom(dao, floorDataMap, variableAction, blockInfo, pickMap, separateInfo);
                }
            }
        }

        return pickMap;
    }

    /** SubaeManagerPick.findBom */
    private static void findBom(PickDao dao, Map dataMap, PickVariableAction vAction, PickDao.BlockInfo blockInfo,
            HashMap<String, Object> oMap, HashMap<String, String> separateInfo) throws Exception {

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
        }
    }
}
