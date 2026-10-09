package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;

/**
 * PID Simulator (jsp/plmetc/variant/PIDSimulator) 의 [실행] - Project 모드.
 *   PIDSimulator_left.jsp run() → searchProject.jsp / getPIDInfo.jsp
 *   → PIDSimulator_right.jsp → PidService.debugWithOuid → Variant.calcVariantPID (디버그)
 *
 * 층 정보 테이블은 SimConsts.FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 값을 사용한다.
 *
 * 사용 : PIDSimul.run(...) (공사번호/iOuid 자동 판별) 또는 simulate / simulateWithOuid 를 호출하고 PIDSimulResult 를 사용한다.
 */
public class PIDSimul {

	private PIDSimul() {}

	/**
	 * 공사번호 또는 공사정보 ouid 로 실행
	 * @param project      공사번호 ex) 206663L01, 또는 iOuid ex) elv_info$vf@a810066d
	 * @param saveErrorLog true 면 variant_errorlog 에 PID 오류 저장 (원본은 항상 저장)
	 */
	public static PIDSimulResult run(SimDb db, String project, String pid, String floor, boolean testVersion, String beforePids,
			boolean saveErrorLog) throws Exception {
		return run(db, project, pid, floor, toVersion(testVersion), beforePids, saveErrorLog);
	}

	/**
	 * 공사번호 또는 공사정보 ouid 로 지정 버전 실행
	 * @param version null : 최신 버전, -1(SimConsts.TEST_VERSION) : TEST 버전, 그 외 : 지정 버전
	 */
	public static PIDSimulResult run(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			boolean saveErrorLog) throws Exception {
		return run(db, project, pid, floor, version, beforePids, null, saveErrorLog);
	}

	/**
	 * 공사번호 또는 공사정보 ouid 로 지정 버전 실행 + CALL 하위 PID 버전 지정
	 * @param version     null : 최신 버전, -1(SimConsts.TEST_VERSION) : TEST 버전, 그 외 : 지정 버전
	 * @param subVersions CALL 하위 PID → 버전 (-1 : TEST). 지정하지 않은 하위 PID 는 원본과 같이 최신 버전
	 */
	public static PIDSimulResult run(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			Map<String, Integer> subVersions, boolean saveErrorLog) throws Exception {
		return isOuid(project)
				? simulateWithOuid(db, project, pid, floor, version, beforePids, subVersions, saveErrorLog)
				: simulate(db, project, pid, floor, version, beforePids, subVersions, saveErrorLog);
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
		return simulate(db, projectNo, pid, floor, toVersion(testVersion), beforePids, false);
	}

	/** 공사번호로 실행 (saveErrorLog : variant_errorlog 에 PID 오류 저장 여부) */
	public static PIDSimulResult simulate(SimDb db, String projectNo, String pid, String floor, boolean testVersion, String beforePids,
			boolean saveErrorLog) throws Exception {
		return simulate(db, projectNo, pid, floor, toVersion(testVersion), beforePids, saveErrorLog);
	}

	/** 공사번호로 지정 버전 실행 (version : null 최신, -1 TEST, 그 외 지정 버전) */
	public static PIDSimulResult simulate(SimDb db, String projectNo, String pid, String floor, Integer version, String beforePids,
			boolean saveErrorLog) throws Exception {
		return simulate(db, projectNo, pid, floor, version, beforePids, null, saveErrorLog);
	}

	/** 공사번호로 지정 버전 실행 + CALL 하위 PID 버전 지정 (subVersions : 하위 PID → 버전) */
	public static PIDSimulResult simulate(SimDb db, String projectNo, String pid, String floor, Integer version, String beforePids,
			Map<String, Integer> subVersions, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(projectNo))
			throw new HdelBusinessException("projectNo 가 없습니다.");

		// searchProject.jsp
		Map<String, String> project = new SimSpecLoader(db).findProject(projectNo.trim());
		if (project == null)
			throw new HdelBusinessException("No data : projectNo=" + projectNo);

		PIDSimulResult result = simulateWithOuid(db, project.get("IOUID"), pid, floor, version, beforePids, subVersions, saveErrorLog);
		result.projectNo = project.get("MD$NUMBER");
		result.projectTitle = project.get("MD$NUMBER") + " " + project.get("VF$VERSION") + "\n" + SimUtil.NVL(project.get("MD$DESC"), "");
		return result;
	}

	/**
	 * 공사정보 ouid 로 실행 (화면을 iOuid 파라미터로 연 경우)
	 * @param iOuid ex) elv_info$vf@a810066d
	 */
	public static PIDSimulResult simulateWithOuid(SimDb db, String iOuid, String pid, String floor, boolean testVersion, String beforePids) throws Exception {
		return simulateWithOuid(db, iOuid, pid, floor, toVersion(testVersion), beforePids, false);
	}

	/** 공사정보 ouid 로 실행 (saveErrorLog : variant_errorlog 에 PID 오류 저장 여부) */
	public static PIDSimulResult simulateWithOuid(SimDb db, String iOuid, String pid, String floor, boolean testVersion, String beforePids,
			boolean saveErrorLog) throws Exception {
		return simulateWithOuid(db, iOuid, pid, floor, toVersion(testVersion), beforePids, saveErrorLog);
	}

	/** 공사정보 ouid 로 지정 버전 실행 (version : null 최신, -1 TEST, 그 외 지정 버전) */
	public static PIDSimulResult simulateWithOuid(SimDb db, String iOuid, String pid, String floor, Integer version, String beforePids,
			boolean saveErrorLog) throws Exception {
		return simulateWithOuid(db, iOuid, pid, floor, version, beforePids, null, saveErrorLog);
	}

	/** 공사정보 ouid 로 지정 버전 실행 + CALL 하위 PID 버전 지정 (subVersions : 하위 PID → 버전) */
	public static PIDSimulResult simulateWithOuid(SimDb db, String iOuid, String pid, String floor, Integer version, String beforePids,
			Map<String, Integer> subVersions, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(iOuid))
			throw new HdelBusinessException("iOuid 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		SimContext ctx = new SimContext(db, new SimPidRepository(), saveErrorLog);

		PIDSimulResult result = new PIDSimulResult();
		result.iOuid = iOuid;
		result.pid = pid.trim();
		result.version = version;
		result.testVersion = version != null && version == SimConsts.TEST_VERSION;
		result.beforePids = beforePids;

		// getPIDInfo.jsp : 층별 PID 여부
		PidInfo pidInfo = ctx.getPidRepository().getLastPid(db, result.pid);
		if (pidInfo == null)
			throw new PidNotFoundException(result.pid);
		result.pidName = pidInfo.getName();
		result.isFloorSpec = "Y".equals(pidInfo.getIsfloorspec()) ? "Y" : "N";
		result.latestVersion = pidInfo.getVersion();

		// 실행할 버전 (지정 버전이 없으면 원본은 "PID IS NULL" 오류이므로 먼저 확인한다)
		if (version == null) {
			result.runVersion = pidInfo.getVersion();
		} else {
			PidInfo runInfo = ctx.getPidRepository().getPid(db, result.pid, version);
			if (runInfo == null)
				throw new HdelBusinessException(result.pid + " 의 " + (result.testVersion ? "TEST 버전" : version + " 버전") + "이 없습니다.");
			result.runVersion = runInfo.getVersion();
		}

		// PIDSimulator_right.jsp : "Y".equals(isfloor)? floor : null
		String floorArg = ("Y".equals(result.isFloorSpec) && floor != null) ? floor.trim() : null;

		// PidService.debugWithOuid
		SimVariant variant = initVariantWithOuid(ctx, iOuid, floorArg, result);
		SimVariantMap logicMap = debugPid(ctx, variant, result.pid, version, beforePids, subVersions);

		toResult(ctx, variant, logicMap, result);
		toSubPids(ctx, result);
		return result;
	}

	/** CALL 로 실행된 하위 PID 와 각 PID 의 선택 가능한 버전 목록 */
	private static void toSubPids(SimContext ctx, PIDSimulResult result) throws Exception {
		for (SimContext.SubPidRun run : ctx.getSubPidRuns()) {
			PIDSimulResult.SubPid sub = new PIDSimulResult.SubPid();
			sub.pid = run.pid;
			sub.name = run.name;
			sub.runVersion = run.runVersion;
			sub.requestedVersion = run.requestedVersion;
			sub.depth = run.depth;
			sub.callCount = run.callCount;
			sub.error = run.error;
			sub.versions = ctx.getPidRepository().getVersions(ctx.getDb(), run.pid);
			for (Map<String, Object> v : sub.versions) {
				if (Boolean.TRUE.equals(v.get("latest")))
					sub.latestVersion = (Integer) v.get("version");
			}
			if (sub.name == null) {
				PidInfo last = ctx.getPidRepository().getLastPid(ctx.getDb(), run.pid);
				if (last != null)
					sub.name = last.getName();
			}
			result.subPids.add(sub);
		}
	}

	/**
	 * PID 의 버전 목록 (TEST 버전 먼저, 그 다음 최신순)
	 * @return 항목 {version, latest}
	 */
	public static List<Map<String, Object>> findVersions(SimDb db, String pid) throws Exception {
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");
		return new SimPidRepository().getVersions(db, pid.trim());
	}

	private static Integer toVersion(boolean testVersion) {
		return testVersion ? Integer.valueOf(SimConsts.TEST_VERSION) : null;
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

	/** PidService.debugPid (subVersions : CALL 하위 PID 버전 지정. 전처리 PID 와 그 하위 PID 는 원본과 같이 최신) */
	private static SimVariantMap debugPid(SimContext ctx, SimVariant variant, String pid, Integer version, String beforePids,
			Map<String, Integer> subVersions) throws Exception {
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

		// 여기부터 대상 PID 실행 : CALL 하위 PID 버전 지정 적용 + 실행 기록
		ctx.startSubPidTracking(subVersions);

		// 지정 버전(TEST 포함)이면 그 버전, 없으면 최신 버전. CALL 하는 하위 PID 는 원본과 같이 항상 최신 버전
		if (version != null)
			return variant.calcVariantPID(pid, version, null, true);
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
					if ("CALL".equals(kv.key)) {
						// 원본 화면은 resultMap 전체를 문자열로 출력한다. 여기서는 건수만 쓰고 resultMap 은 line.resultMap 으로 전달
						output.append(kv.key).append("(").append(val).append(") = resultMap ")
								.append(resultMap == null ? 0 : resultMap.size()).append("건");
						if (resultMap != null)
							line.resultMap = new LinkedHashMap<String, Object>(resultMap);
					} else
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

	/** 공사정보 ouid 형식 (xxx$vf@hex) 여부 */
	public static boolean isOuid(String value) {
		return value != null && value.contains("$") && value.contains("@");
	}
}
