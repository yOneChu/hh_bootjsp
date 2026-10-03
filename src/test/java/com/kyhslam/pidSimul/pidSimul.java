package com.kyhslam.pidSimul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PID 라인 실행 시뮬레이터 (VaultReportController.pidExecuteLineData 의 독립 실행 버전)
 *
 * 실행 예)
 *   java -cp "classes;ojdbc6.jar" com.kyhslam.pidSimul.pidSimul N26143L01 PID0001
 *   java -cp "classes;ojdbc6.jar" com.kyhslam.pidSimul.pidSimul N26143L01 PID0001 on Y 1F
 *
 * DB 접속 : PLMDBConnection.getConnection()
 *
 * 인자 : hogi PID [testVersion] [isfloor] [floor] [type]
 *   testVersion : 공백이면 최신버전, "on"이면 test버전 (type=SELECT 이면 버전번호)
 *   isfloor     : Y이면 층별 PID
 *   floor       : 몇층인지 정보 (FLOOR_NAME)
 *   type        : SELECT 이면 testVersion 을 버전으로 그대로 사용
 *
 * 시스템 프로퍼티 (선택)
 *   -Dpid.meta.schema=HDEL_SYSTEM    DOS 메타테이블(dosclas, dosfld ...) 스키마
 *   -Dpid.errorlog=true              variant_errorlog 에 오류 저장 (기본 false : 콘솔 출력만)
 */
public class pidSimul {

    public static void main(String[] args) throws Exception {
/*
        if (args.length < 2) {
            System.out.println("usage : pidSimul <hogi> <PID> [testVersion] [isfloor] [floor] [type]");
            return;
        }
*/

        String hogi        = "N29532L02";
        String PID         = "E321A";
        String testVersion = args.length > 2 ? args[2] : "";
        String isfloor     = args.length > 3 ? args[3] : "";
        String floor       = args.length > 4 ? args[4] : "";
        String type        = args.length > 5 ? args[5] : "";

        try (PidDb db = PidDb.open()) {
            ArrayList data = pidExecuteLineData(db, hogi, PID, testVersion, isfloor, floor, type);
            //printLineData(data);
        }
    }

    /**
     * VaultReportController.pidExecuteLineData 와 동일한 기능.
     * PID 로직의 각 라인별 실행결과(specList, conList, compareResultList, keyList, valList, rowTrue, resultMap ...) 를 반환한다.
     */
    public static ArrayList pidExecuteLineData(PidDb db, String hogi, String PID, String testVersion, String isfloor, String floor, String type) throws Exception {

        // testVersion : 공백이면 최신버전으로, "on"이면 test버전으로 수행
        // isfloor : Y이면 층별 PID이다.
        // floor : 몇층인지 정보

        PidSpecLoader loader = new PidSpecLoader(db, System.getProperty("pid.meta.schema", "HDEL_SYSTEM"));

        String iOuid = loader.findLatestElvOuid(hogi);
        String beforePids = "";

        PID = (PID == null)? null : PID.trim();
        floor = (floor == null)? null : floor.trim();

        PidVariantMap logicMap = null;

        if(type != null && !"".equals(type) && "SELECT".equals(type)) {
            logicMap = debugWithOuid(db, loader, iOuid, "Y".equals(isfloor)? floor : null, PID, testVersion, beforePids);
        } else {
            logicMap = debugWithOuid(db, loader, iOuid, "Y".equals(isfloor)? floor : null, PID, "on".equals(testVersion)? PidConsts.TEST : PidConsts.LASTEST, beforePids);
        }

        PidVariantMap debugData = (PidVariantMap) logicMap.get("debugData");
        if (debugData == null)
            throw new Exception("PID 디버그 데이터가 없습니다. PID=" + PID);

        ArrayList data = (ArrayList) debugData.get("data");
        System.out.println(data);
        return data;
    }

    /** PidDesignService.debugWithOuid */
    public static PidVariantMap debugWithOuid(PidDb db, PidSpecLoader loader, String iOuid, String floor, String pid, String version, String beforePids) throws Exception {
        PidVariant variant = initVariantWithOuid(db, loader, iOuid, floor);
        variant.setSaveErrorLog(Boolean.getBoolean("pid.errorlog"));
        return debugPid(variant, pid, version, beforePids);
    }

    /** PidDesignService.initVariantWithOuid */
    private static PidVariant initVariantWithOuid(PidDb db, PidSpecLoader loader, String iOuid, String floor) throws Exception {
        PidSpecLoader.SpecObject elvMaster = loader.load(iOuid);
        if (elvMaster == null)
            throw new Exception("영업사양 객체가 없습니다. ouid=" + iOuid);

        HashMap elvEnt = null;
        List<Map> floorMasterList = new ArrayList<Map>();

        if (floor != null && !"".equals(floor)) {
            for (PidSpecLoader.SpecObject floorMaster : loader.loadFloors(iOuid, elvMaster.dataMap)) {
                floorMasterList.add(floorMaster.dataMap);
                if (elvEnt == null && floor.equals(PidUtil.NVL(floorMaster.dataMap.get(PidConsts.FIELD_NAME_FLOOR_NAME), ""))) {
                    elvEnt = floorMaster.dataMap;
                }
            }
            if (elvEnt == null)
                throw new Exception("층 정보를 찾을 수 없습니다. floor=" + floor);
        } else {
            elvEnt = elvMaster.dataMap; //영업사양 셋팅
        }

        return new PidVariant(db, loader, elvEnt, floorMasterList);
    }

    /** PidDesignService.debugPid */
    private static PidVariantMap debugPid(PidVariant variant, String pid, String version, String beforePids) throws Exception {
        if (beforePids != null && !beforePids.trim().isEmpty()) {
            for (String beforePID : beforePids.split("\n")) {
                if (!beforePID.trim().isEmpty()) {
                    try {
                        PidVariantMap result = variant.calcVariantPID(beforePID.trim(), null, 1);
                        variant.appendToElvEnt(result);
                    } catch (PidExceptions.PidNotFoundException ignored) {}
                }
            }
        }

        if (PidConsts.TEST.equals(version)) {
            return variant.calcVariantPID(pid, PidConsts.TEST_VERSION, null, true);
        } else if (PidConsts.LASTEST.equals(version)) {
            return variant.calcVariantPID(pid, null, null, 1, true);
        } else {
            return variant.calcVariantPID(pid, Integer.parseInt(version), null, true);
        }
    }

    /** 라인별 실행결과 콘솔 출력 */
    public static void printLineData(ArrayList data) {
        HashMap<String, String> output = new HashMap<String, String>();

        for (int i = 0; i < data.size(); i++) {
            Map row = (Map) data.get(i);
            String rowTrue = PidUtil.NVL(row.get("rowTrue"), "");
            String state = "".equals(rowTrue) ? "SKIP" : ("true".equals(rowTrue) ? "TRUE" : "FALS");

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%4d [%s] ADDR=%-6s GOTO=%-6s | ", i + 1, state,
                    PidUtil.NVL(row.get("ADDR"), ""), PidUtil.NVL(row.get("GOTO"), "")));

            List specList = (List) row.get("specList");
            List conList = (List) row.get("conList");
            List compareResultList = (List) row.get("compareResultList");
            for (int j = 0; j < specList.size(); j++) {
                if (specList.get(j) == null) continue;
                String cmp = (compareResultList != null && compareResultList.size() > j) ? PidUtil.NVL(compareResultList.get(j), "") : "";
                sb.append(specList.get(j)).append("=").append(PidUtil.NVL(conList.get(j), "")).append("(").append(cmp).append(") ");
            }

            sb.append("| ");
            List keyList = (List) row.get("keyList");
            List valList = (List) row.get("valList");
            for (int j = 0; j < keyList.size(); j++) {
                if (keyList.get(j) == null) continue;
                sb.append(keyList.get(j)).append("=").append(PidUtil.NVL(valList.get(j), "")).append(" ");
            }
            System.out.println(sb);

            // 조건 만족 라인의 결과값 수집 (pidSelectExecute 와 동일)
            Map resultMap = (Map) row.get("resultMap");
            if ("true".equals(rowTrue) && resultMap != null) {
                for (int j = 0; j < keyList.size(); j++) {
                    String key = (String) keyList.get(j);
                    if (key == null || "CALL".equals(key) || "OUTPUT".equals(key.trim())) continue;
                    output.put(key.trim(), PidUtil.NVL(resultMap.get(key), "").trim());
                }
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("RESULT : " + output);
    }
}
