package com.kyhslam.pidSimulatorOne;

import com.kyhslam.pidSimulatorOne.SimExceptions.HdelBusinessException;
import com.kyhslam.pidSimulatorOne.SimExceptions.PidNotFoundException;
import com.kyhslam.pidSimulatorOne.SimPidRepository.PidInfo;

import java.util.*;

/**
 * PID Simulator (jsp/plmetc/variant/PIDSimulator) 의 [실행] - Project 모드 독립 실행 버전.
 *   PIDSimulator_left.jsp run() → searchProject.jsp / getPIDInfo.jsp
 *   → PIDSimulator_right.jsp → PidService.debugWithOuid → Variant.calcVariantPID (디버그)
 *
 * 실행 예)
 *   java -cp "classes;ojdbc6.jar" dyna.pidSimulatorOne.PIDSimul N26143L01 PID0001
 *   java -cp "classes;ojdbc6.jar" dyna.pidSimulatorOne.PIDSimul N26143L01 PID0001 -test -floor 1F -before EL_P001,EL_P002
 *
 * 인자 : projectNo(또는 iOuid) PID [옵션]
 *   -test             TEST 버전으로 실행 (화면의 "TEST 버전" 체크)
 *   -floor <층이름>    층별 PID 일 때 적용할 층 (FLOOR_NAME)
 *   -before <PID,...> 전처리 PID (콤마 구분, 화면의 "전처리 PID")
 *   -trueonly         실행된 행만 출력
 *
 * 시스템 프로퍼티 (선택)
 *   -Dpid.errorlog=true          variant_errorlog 에 오류 저장 (원본은 항상 저장. 기본 false : 콘솔 출력만)
 *
 * 층 정보 테이블은 SimConsts.FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 값을 사용한다.
 *
 * 다른 시스템에서 사용시 : PIDSimul.simulate(...) 를 호출하고 PIDSimulResult 를 사용한다.
 */
public class PIDSimul {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			//System.out.println("usage : PIDSimul <projectNo|iOuid> <PID> [-test] [-floor <FLOOR_NAME>] [-before <PID,...>] [-trueonly]");
			//return;
		}

		//D375A
		String project = "206663L01"; //args[0];
		String pid = "EL_PB189C99"; //"EL_PB189C99"; //args[1];
		boolean testVersion = false;
		boolean trueOnly = false;
		String floor = null;
		String beforePids = null;

		//testVersion = true;

		for (int i = 2; i < args.length; i++) {
			if ("-test".equalsIgnoreCase(args[i]))
				testVersion = true;
			else if ("-trueonly".equalsIgnoreCase(args[i]))
				trueOnly = true;
			else if ("-floor".equalsIgnoreCase(args[i]) && i + 1 < args.length)
				floor = args[++i];
			else if ("-before".equalsIgnoreCase(args[i]) && i + 1 < args.length)
				beforePids = args[++i].replace(",", "\n");
			else
				throw new IllegalArgumentException("알 수 없는 옵션 : " + args[i]);
		}

		long start = System.currentTimeMillis();
		try (SimDb db = SimDb.open()) {
			PIDSimulResult result = isOuid(project)
					? simulateWithOuid(db, project, pid, floor, testVersion, beforePids)
					: simulate(db, project, pid, floor, testVersion, beforePids);
			result.print(System.out, !trueOnly);
		}
		System.out.println("--------------------------------------------------");
		System.out.println((System.currentTimeMillis() - start) + "ms");
	}

	/**
	 * 공사번호로 실행 (화면에서 Project 번호를 입력한 경우)
	 * @param projectNo  공사번호 (elv_info / shipelv_info / monitorstation_info 의 md$number)
	 * @param pid        테스트 PID
	 * @param floor      층별 PID 일 때 적용할 층 (FLOOR_NAME). 층별 PID 가 아니면 무시
	 * @param testVersion true 면 TEST 버전, false 면 최신 버전
	 * @param beforePids 전처리 PID (줄바꿈 구분)
	 */
	public static PIDSimulResult simulate(SimDb db, String projectNo, String pid, String floor, boolean testVersion, String beforePids) throws Exception {
		if (SimUtil.isNullString(projectNo))
			throw new HdelBusinessException("projectNo 가 없습니다.");

		// searchProject.jsp
		Map<String, String> project = new SimSpecLoader(db).findProject(projectNo.trim());
		if (project == null)
			throw new HdelBusinessException("No data : projectNo=" + projectNo);

		PIDSimulResult result = simulateWithOuid(db, project.get("IOUID"), pid, floor, testVersion, beforePids);
		result.projectNo = project.get("MD$NUMBER");
		result.projectTitle = project.get("MD$NUMBER") + " " + project.get("VF$VERSION") + "\n" + SimUtil.NVL(project.get("MD$DESC"), "");
		return result;
	}

	/**
	 * 공사정보 ouid 로 실행 (화면을 iOuid 파라미터로 연 경우)
	 * @param iOuid ex) elv_info$vf@a810066d
	 */
	public static PIDSimulResult simulateWithOuid(SimDb db, String iOuid, String pid, String floor, boolean testVersion, String beforePids) throws Exception {
		if (SimUtil.isNullString(iOuid))
			throw new HdelBusinessException("iOuid 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		SimContext ctx = new SimContext(db, new SimPidRepository(), Boolean.getBoolean("pid.errorlog"));

		PIDSimulResult result = new PIDSimulResult();
		result.iOuid = iOuid;
		result.pid = pid.trim();
		result.testVersion = testVersion;
		result.beforePids = beforePids;

		// getPIDInfo.jsp : 층별 PID 여부
		PidInfo pidInfo = ctx.getPidRepository().getLastPid(db, result.pid);
		if (pidInfo == null)
			throw new PidNotFoundException(result.pid);
		result.pidName = pidInfo.getName();
		result.isFloorSpec = "Y".equals(pidInfo.getIsfloorspec()) ? "Y" : "N";

		// PIDSimulator_right.jsp : "Y".equals(isfloor)? floor : null
		String floorArg = ("Y".equals(result.isFloorSpec) && floor != null) ? floor.trim() : null;

		// PidService.debugWithOuid
		SimVariant variant = initVariantWithOuid(ctx, iOuid, floorArg, result);
		SimVariantMap logicMap = debugPid(variant, result.pid, testVersion, beforePids);

		toResult(ctx, variant, logicMap, result);
		return result;
	}

	/** PidService.initVariantWithOuid (SubaeManager.getElvInfoAndFloorInfoWithELP) */
	private static SimVariant initVariantWithOuid(SimContext ctx, String iOuid, String floor, PIDSimulResult result) throws Exception {
		SimSpecLoader loader = ctx.getSpecLoader();

		SimSpecLoader.SpecObject elvMaster = loader.load(iOuid);
		if (elvMaster == null)
			throw new HdelBusinessException("공사정보가 없습니다. iOuid=" + iOuid);
		if (result.projectNo == null)
			result.projectNo = SimUtil.NVL(elvMaster.dataMap.get("md$number"), "");

		List<Map> floorMasterList = null;
		try {
			List<HashMap> floors = loader.loadFloors(iOuid, elvMaster.dataMap);
			if (floors != null)
				floorMasterList = new ArrayList<Map>(floors);
		} catch (Exception e) {
			if (floor != null && !floor.isEmpty())
				throw e;
			// 층별 PID 가 아니면 층 정보 없이 진행 (층 정보를 쓰는 JAVA PID 결과는 원본과 다를 수 있다)
			result.warnings.add("층 정보 로드 실패 - 층 정보 없이 실행합니다 : " + e.getMessage());
		}

		HashMap elvEnt = null;
		if (floor != null && !"".equals(floor) && floorMasterList != null) {
			for (Map floorMaster : floorMasterList) {
				if (floor.equals(SimUtil.NVL(floorMaster.get(SimConsts.FIELD_NAME_FLOOR_NAME), ""))) {
					elvEnt = (HashMap) floorMaster;
					break;
				}
			}
			// 원본은 elvEnt 가 null 인 채로 진행하여 오류가 난다
			if (elvEnt == null)
				throw new HdelBusinessException("층 정보를 찾을 수 없습니다. floor=" + floor);
			result.floor = floor;
		} else {
			elvEnt = elvMaster.dataMap;
		}

		return new SimVariant(ctx, elvEnt, floorMasterList);
	}

	/** PidService.debugPid */
	private static SimVariantMap debugPid(SimVariant variant, String pid, boolean testVersion, String beforePids) throws Exception {
		if (!SimUtil.isNullString(beforePids)) {
			for (String beforePID : beforePids.split("\n")) {
				if (!SimUtil.isNullString(beforePID.trim())) {
					try {
						SimVariantMap result = variant.calcVariantPID(beforePID.trim(), null);
						variant.appendToElvEnt(result);
					} catch (PidNotFoundException ignored) {}
				}
			}
		}

		if (testVersion)
			return variant.calcVariantPID(pid, SimConsts.TEST_VERSION, null, true);
		else
			return variant.calcVariantPID(pid, null, null, 1, true);
	}

	/** PIDSimulator_right.jsp 의 화면 구성 */
	private static void toResult(SimContext ctx, SimVariant variant, SimVariantMap logicMap, PIDSimulResult result) throws Exception {
		SimVariantMap debugData = (SimVariantMap) logicMap.get("debugData");

		// JAVA로 실행한 결과는 데이터구조가 다르니 별도로 출력하고 끝낸다
		if (debugData == null) {
			result.javaMethod = true;
			result.resultMap.putAll(logicMap);
			return;
		}

		Map localElvEnt = (Map) logicMap.get("elvEnt");
		HashMap localElvEntMap = new HashMap(localElvEnt);
		Map<String, String> elvData = ctx.getCodeNames(result.iOuid);

		result.elvEnt = localElvEnt;
		for (Object k : logicMap.keySet()) {
			if (!"debugData".equals(k) && !"elvEnt".equals(k))
				result.resultMap.put(String.valueOf(k), logicMap.get(k));
		}

		result.maxSpecIdx = SimUtil.parseInt(debugData.get("maxSpecIdx"));
		result.maxResIdx = SimUtil.parseInt(debugData.get("maxResIdx"));
		List data = (List) debugData.get("data");

		for (int i = 0; i < data.size(); i++) {
			Map row = (Map) data.get(i);
			List specList = (List) row.get("specList");
			List conList = (List) row.get("conList");
			List compareResultList = (List) row.get("compareResultList");
			List keyList = (List) row.get("keyList");
			List valList = (List) row.get("valList");
			SimVariantMap resultMap = (SimVariantMap) row.get("resultMap");
			String rowTrue = SimUtil.NVL(row.get("rowTrue"), "");

			PIDSimulResult.Line line = new PIDSimulResult.Line();
			line.no = i + 1;
			line.addr = (String) row.get("ADDR");
			line.gotoAddr = (String) row.get("GOTO");
			line.remarks = (String) row.get("REMARKS");
			line.state = "".equals(rowTrue) ? PIDSimulResult.State.UNREAD
					: ("true".equals(rowTrue) ? PIDSimulResult.State.TRUE : PIDSimulResult.State.FALSE);

			// SPEC, CON
			for (int j = 0; j < result.maxSpecIdx; j++) {
				PIDSimulResult.Condition c = new PIDSimulResult.Condition();
				c.spec = (String) specList.get(j);
				c.con = (String) conList.get(j);
				c.compare = (compareResultList != null && compareResultList.size() > j) ? SimUtil.NVL(compareResultList.get(j), "") : "";
				c.specValue = c.spec == null ? "" : SimUtil.NVL(localElvEnt.get(c.spec), "");
				line.conditions.add(c);
			}

			// KEY, VAL, OUTPUT
			StringBuilder output = new StringBuilder();
			Set<String> duplicationAvoid = new HashSet<String>();
			for (int j = 0; j < result.maxResIdx; j++) {
				PIDSimulResult.KeyVal kv = new PIDSimulResult.KeyVal();
				kv.key = (String) keyList.get(j);
				kv.val = (String) valList.get(j);
				line.results.add(kv);

				if (kv.key == null || line.state != PIDSimulResult.State.TRUE || !duplicationAvoid.add(kv.key))
					continue;

				try {
					if (output.length() > 0)
						output.append("\n");

					String val = SimUtil.NVL(kv.val, "");
					if ("CALL".equals(kv.key))
						output.append(kv.key).append("(").append(val).append(") = ").append(resultMap);
					else
						output.append(kv.key).append(" = ").append(resultMap == null ? "" : SimUtil.NVL(resultMap.get(kv.key), ""));

					if (val.contains("[$"))
						output.append(" (").append(variant.convertVal(val, localElvEntMap, elvData)).append(")");
				} catch (Exception e) {
					e.printStackTrace();
					output.setLength(0);
					output.append("에러!");
				}
			}
			line.output = output.toString();

			result.lines.add(line);
		}
	}

	private static boolean isOuid(String value) {
		return value != null && value.contains("$") && value.contains("@");
	}
}
