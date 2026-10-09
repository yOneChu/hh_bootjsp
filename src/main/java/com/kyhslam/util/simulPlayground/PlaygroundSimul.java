package com.kyhslam.util.simulPlayground;

import com.kyhslam.util.pidSimulatorUp.*;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;

/**
 * PID 플레이그라운드 : 화면에서 수정한 대상 PID 로직(DB 미저장)으로 바로 실행한다.
 *
 * pidSimulatorUp.PIDSimul 과 같은 흐름이지만 PID 저장소만 PlaygroundPidRepository 로 바꿔
 * 대상 PID 의 로직을 수정본으로 대체한다. CALL 하위 PID / 전처리 PID 는 DB 최신 버전 그대로.
 * pidSimulatorUp 의 클래스는 수정하지 않고 공개 API 만 사용한다.
 *
 * DB 쓰기 없음 : SimContext(saveErrorLog=false) + 읽기 전용 연결(PlaygroundDb) 로 실행한다.
 */
public final class PlaygroundSimul {

	private PlaygroundSimul() {}

	// ------------------------------------------------------------------------ 편집용 로직 조회

	/**
	 * 편집 화면에 불러올 PID 로직 (DB 원본)
	 * @param version null : 최신, -1 : TEST, 그 외 : 지정 버전
	 */
	public static PlaygroundLogic loadLogic(SimDb db, String pid, Integer version) throws Exception {
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");
		pid = pid.trim();

		String head = " SELECT A.HOUID, A.PID, A.NAME, A.METHOD, A.VERSION, A.ISFLOORSPEC, TO_CHAR(A.REG_DATE, 'YYYY-MM-DD HH24:MI') REG_DATE ";
		Map<String, String> h = version == null
				? db.queryForFirst(head + " FROM VARIANT_H A, VARIANT_ID B WHERE A.HOUID = B.LAST_HOUID AND A.PID = ? ", pid)
				// 같은 버전이 여럿이면(TEST 를 여러 번 저장 등) 가장 마지막 것
				: db.queryForFirst(" SELECT * FROM (" + head + " FROM VARIANT_H A WHERE A.PID = ? AND A.VERSION = ? ORDER BY A.HOUID DESC) WHERE ROWNUM = 1 ",
						pid, version);
		if (h == null)
			throw new HdelBusinessException(version == null ? "PID 가 없습니다 : " + pid
					: pid + " 의 " + (version == SimConsts.TEST_VERSION ? "TEST 버전" : version + " 버전") + "이 없습니다.");

		PlaygroundLogic logic = new PlaygroundLogic();
		logic.pid = h.get("PID");
		logic.name = h.get("NAME");
		logic.method = SimUtil.NVL(h.get("METHOD"), "");
		logic.version = SimUtil.parseInt(h.get("VERSION"));
		logic.regDate = h.get("REG_DATE");
		logic.houid = h.get("HOUID");

		SimPidRepository repo = new SimPidRepository();
		logic.versions = repo.getVersions(db, pid);
		PidInfo last = repo.getLastPid(db, pid);
		if (last != null) {
			logic.latestVersion = last.getVersion();
			logic.isFloorSpec = "Y".equals(last.getIsfloorspec()) ? "Y" : "N";
		} else {
			logic.isFloorSpec = "Y".equals(h.get("ISFLOORSPEC")) ? "Y" : "N";
		}

		for (Map<String, String> r : db.queryForList(" SELECT * FROM VARIANT_D WHERE HOUID = ? ORDER BY DOUID ", logic.houid)) {
			PlaygroundRow row = new PlaygroundRow();
			row.no = r.get("NO");
			row.douid = r.get("DOUID");
			row.addr = r.get("ADDR");
			row.gotoAddr = r.get("GOTO");
			row.remarks = r.get("REMARKS");
			for (int i = 1; i <= SimConsts.MAX_SPEC_FIELD_SIZE; i++) {
				row.spec.add(r.get("SPEC" + i));
				row.con.add(r.get("CON" + i));
			}
			for (int i = 1; i <= SimConsts.MAX_RES_FIELD_SIZE; i++) {
				row.key.add(r.get("KEY" + i));
				row.val.add(r.get("VAL" + i));
			}
			trimPair(row.spec, row.con);
			trimPair(row.key, row.val);
			logic.rows.add(row);
		}
		return logic;
	}

	/** 뒤쪽의 빈 칸 쌍을 잘라 응답을 줄인다 */
	private static void trimPair(List<String> a, List<String> b) {
		int n = a.size();
		while (n > 0 && a.get(n - 1) == null && b.get(n - 1) == null)
			n--;
		a.subList(n, a.size()).clear();
		b.subList(n, b.size()).clear();
	}

	// ------------------------------------------------------------------------ 실행

	/**
	 * 수정본(없으면 DB 원본)으로 대상 PID 를 디버그 실행한다. DB 에는 저장하지 않는다.
	 */
	public static PlaygroundResult run(SimDb db, PlaygroundRequest req) throws Exception {
		if (req == null || SimUtil.isNullString(req.project))
			throw new HdelBusinessException("공사번호(project) 가 없습니다.");
		if (SimUtil.isNullString(req.pid))
			throw new HdelBusinessException("PID 가 없습니다.");
		if (req.rows != null && req.rows.size() > 5000)
			throw new HdelBusinessException("라인이 너무 많습니다 (최대 5000) : " + req.rows.size());

		String project = req.project.trim();
		String pid = req.pid.trim();
		Integer version = req.version;
		String beforePids = toBeforePids(req.beforePid);

		// searchProject.jsp : 공사번호 → 공사정보 ouid
		String iOuid = project;
		Map<String, String> projectRow = null;
		if (!PIDSimul.isOuid(project)) {
			projectRow = new SimSpecLoader(db).findProject(project);
			if (projectRow == null)
				throw new HdelBusinessException("No data : projectNo=" + project);
			iOuid = projectRow.get("IOUID");
		}

		PlaygroundPidRepository repo = new PlaygroundPidRepository(pid, req.rows);
		// saveErrorLog = false : variant_errorlog 에도 쓰지 않는다
		SimContext ctx = new SimContext(db, repo, false);

		PlaygroundResult result = new PlaygroundResult();
		result.iOuid = iOuid;
		result.pid = pid;
		result.version = version;
		result.testVersion = version != null && version == SimConsts.TEST_VERSION;
		result.beforePids = beforePids;
		if (projectRow != null) {
			result.projectNo = projectRow.get("MD$NUMBER");
			result.projectTitle = projectRow.get("MD$NUMBER") + " " + projectRow.get("VF$VERSION") + "\n" + SimUtil.NVL(projectRow.get("MD$DESC"), "");
		}

		// getPIDInfo.jsp : 층별 PID 여부 (최신 버전 기준)
		PidInfo pidInfo = repo.getLastPid(db, pid);
		if (pidInfo == null)
			throw new PidNotFoundException(pid);
		result.pidName = pidInfo.getName();
		result.isFloorSpec = "Y".equals(pidInfo.getIsfloorspec()) ? "Y" : "N";
		result.latestVersion = pidInfo.getVersion();

		PidInfo runInfo = pidInfo;
		if (version != null) {
			runInfo = repo.getPid(db, pid, version);
			if (runInfo == null)
				throw new HdelBusinessException(pid + " 의 " + (result.testVersion ? "TEST 버전" : version + " 버전") + "이 없습니다.");
		}
		result.runVersion = runInfo.getVersion();

		if (repo.hasDraft()) {
			if (SimConsts.METHOD_JAVA.equals(runInfo.getMethod()))
				throw new HdelBusinessException(pid + " 는 JAVA 방식 PID 라 라인 수정본으로 실행할 수 없습니다.");
			result.warnings.add("DB 미저장 수정본으로 실행했습니다 (라인 " + req.rows.size() + "개). CALL 하위 PID 와 전처리 PID 는 DB 최신 버전입니다.");
		}

		// PIDSimulator_right.jsp : "Y".equals(isfloor)? floor : null
		String floorArg = ("Y".equals(result.isFloorSpec) && req.floor != null) ? req.floor.trim() : null;

		HashMap[] elvEntRef = new HashMap[1];
		SimVariant variant = initVariant(ctx, iOuid, floorArg, result, elvEntRef);

		// 전처리 PID (최신) → 그 결과까지 반영된 영업사양을 대상 PID 실행 직전 값으로 기억 (디버그 값 계산용)
		runBeforePids(variant, beforePids);
		Map initialEnv = new HashMap(elvEntRef[0]);

		SimVariantMap logicMap = debugPid(ctx, variant, pid, version);

		toResult(ctx, variant, logicMap, result);
		if (!result.javaMethod)
			buildDebug(ctx, variant, logicMap, result, initialEnv);
		toSubPids(ctx, result);
		return result;
	}

	// ------------------------------------------------------------------------ 디버그 (라인별 그 시점의 값)

	/** 엔진이 부품정보(partInfo)에서 읽는 SPEC (PID 시뮬에서는 부품정보가 없어 빈 값) */
	private static final String PART_SPECS = ",PARTNO,REALPART,G_L_CODE,B_NO,PICK,PARAM1,PARAM2,PARAM3";

	/**
	 * 라인별 디버그 정보 : 실행 순서, 조건을 비교한 그 시점의 SPEC 값 / CON 계산값, 이 라인으로 바뀐 값.
	 * 엔진은 조건을 만족한 라인의 KEY=VAL 을 영업사양에도 넣으므로(SimVariant.calcResultValues),
	 * 대상 PID 실행 직전 영업사양에 앞 라인들의 resultMap 변경을 차례로 반영해 그 시점의 값을 재현한다.
	 */
	private static void buildDebug(SimContext ctx, SimVariant variant, SimVariantMap logicMap, PlaygroundResult result, Map initialEnv) {
		SimVariantMap debugData = (SimVariantMap) logicMap.get("debugData");
		List data = (List) debugData.get("data");
		Map<String, String> elvData = null;
		try {
			elvData = ctx.getCodeNames(result.iOuid);
		} catch (Exception ignored) {}

		HashMap env = new HashMap(initialEnv);
		Map prevSnap = new HashMap();
		int step = 0;

		for (int i = 0; i < data.size(); i++) {
			Map row = (Map) data.get(i);
			PIDSimulResult.Line line = result.lines.get(i);
			PlaygroundResult.LineDebug d = new PlaygroundResult.LineDebug();

			if (line.state != PIDSimulResult.State.UNREAD) {
				d.step = ++step;
				env.put("LINE_NO", row.get("line_no"));

				List specList = (List) row.get("specList");
				List conList = (List) row.get("conList");
				List cmpList = (List) row.get("compareResultList");
				for (int j = 0; j < specList.size(); j++) {
					String spec = (String) specList.get(j);
					if (spec == null)
						continue;
					PlaygroundResult.CondDebug c = new PlaygroundResult.CondDebug();
					c.idx = j + 1;
					c.spec = spec;
					c.con = (String) conList.get(j);
					c.compare = (cmpList != null && cmpList.size() > j) ? SimUtil.NVL(cmpList.get(j), "") : "";
					evalSpec(variant, c, env, initialEnv, elvData);
					evalCon(variant, c, env, elvData);
					d.conds.add(c);
				}

				// 조건 만족 : resultMap 스냅샷과 직전 스냅샷의 차이 = 이 라인으로 바뀐 값
				Map snap = (Map) row.get("resultMap");
				if (line.state == PIDSimulResult.State.TRUE && snap != null) {
					for (Object o : snap.entrySet()) {
						Map.Entry e = (Map.Entry) o;
						String key = String.valueOf(e.getKey());
						String after = str(e.getValue());
						boolean existed = prevSnap.containsKey(key);
						String before = existed ? str(prevSnap.get(key)) : null;
						if (!existed || !Objects.equals(before, after)) {
							PlaygroundResult.Change ch = new PlaygroundResult.Change();
							ch.key = key;
							ch.before = before;
							ch.after = after;
							d.changes.add(ch);
						}
						env.put(key, e.getValue());
					}
					prevSnap = snap;
				}
			}
			result.debug.add(d);
		}
		result.steps = step;
	}

	/** SimVariant 의 SPEC 값 계산과 같은 순서로 그 시점의 SPEC 값을 구한다 */
	private static void evalSpec(SimVariant variant, PlaygroundResult.CondDebug c, HashMap env, Map initialEnv, Map<String, String> elvData) {
		String spec = c.spec;
		try {
			if (SimUtil.compareData(PART_SPECS, spec)) {
				c.specValue = "";
				c.note = "부품정보 (PID 시뮬에서는 빈 값)";
			} else if (spec.contains("[$") && spec.contains("$]")) {
				c.specValue = SimUtil.calculate(variant.convertVal(spec, env, elvData));
				c.note = "계산식";
			} else if (spec.contains("_CODE_NAME") && spec.length() > 10 && elvData != null) {
				c.specValue = variant.converCodeName(spec, env, elvData);
				c.note = "코드명";
			} else {
				c.specValue = SimUtil.NVL(env.get(spec), "");
				if (!Objects.equals(SimUtil.NVL(initialEnv.get(spec), ""), c.specValue))
					c.note = "앞 라인에서 변경 (원래 값 : " + SimUtil.NVL(initialEnv.get(spec), "(없음)") + ")";
			}
		} catch (Exception e) {
			c.specValue = "";
			c.note = "SPEC 계산 오류 : " + e.getMessage();
		}
	}

	/** CON 의 {변수} / [$ 계산식 $] 을 그 시점의 값으로 계산 */
	private static void evalCon(SimVariant variant, PlaygroundResult.CondDebug c, HashMap env, Map<String, String> elvData) {
		if (c.con == null || c.con.isEmpty()) {
			c.conValue = "";
			return;
		}
		try {
			c.conValue = SimUtil.calculate(variant.convertVal(c.con, env, elvData));
		} catch (Exception e) {
			c.conValue = c.con;
			c.note = (c.note == null ? "" : c.note + " / ") + "CON 계산 오류 : " + e.getMessage();
		}
	}

	private static String str(Object v) {
		return v == null ? null : String.valueOf(v);
	}

	/** "A, B C" → "A\nB\nC" (PIDSimul 의 전처리 PID 형식) */
	private static String toBeforePids(String value) {
		if (SimUtil.isNullString(value))
			return null;
		StringBuilder sb = new StringBuilder();
		for (String v : value.split("[,\\s]+")) {
			if (v.trim().isEmpty())
				continue;
			if (sb.length() > 0)
				sb.append("\n");
			sb.append(v.trim());
		}
		return sb.length() == 0 ? null : sb.toString();
	}

	// ------------------------------------------------------------------------ 이하 PIDSimul 과 같은 흐름 (private 이라 옮겨 옴)

	/** PidService.initVariantWithOuid (SubaeManager.getElvInfoAndFloorInfoWithELP) */
	private static SimVariant initVariant(SimContext ctx, String iOuid, String floor, PIDSimulResult result, HashMap[] elvEntRef) throws Exception {
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
			if (elvEnt == null)
				throw new HdelBusinessException("층 정보를 찾을 수 없습니다. floor=" + floor);
			result.floor = floor;
		} else {
			elvEnt = elvMaster.dataMap;
		}

		// SimVariant 는 이 영업사양을 그대로 쥐고 전처리 PID 결과를 덧붙인다 (appendToElvEnt)
		elvEntRef[0] = elvEnt;
		return new SimVariant(ctx, elvEnt, floorMasterList);
	}

	/** PidService.debugPid 의 전처리 PID 부분 : 최신 버전으로 실행해 결과를 영업사양에 덧붙인다 */
	private static void runBeforePids(SimVariant variant, String beforePids) throws Exception {
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
	}

	/** PidService.debugPid 의 대상 PID 부분 (수정본은 저장소가 대체) */
	private static SimVariantMap debugPid(SimContext ctx, SimVariant variant, String pid, Integer version) throws Exception {
		// 대상 PID 실행 구간 : CALL 하위 PID 기록 (버전 지정 없음 = 전부 최신)
		ctx.startSubPidTracking(null);

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

	/** CALL 로 실행된 하위 PID (읽기 전용 표시용) */
	private static void toSubPids(SimContext ctx, PIDSimulResult result) throws Exception {
		for (SimContext.SubPidRun run : ctx.getSubPidRuns()) {
			PIDSimulResult.SubPid sub = new PIDSimulResult.SubPid();
			sub.pid = run.pid;
			sub.name = run.name;
			sub.runVersion = run.runVersion;
			sub.depth = run.depth;
			sub.callCount = run.callCount;
			sub.error = run.error;
			PidInfo last = ctx.getPidRepository().getLastPid(ctx.getDb(), run.pid);
			if (last != null) {
				sub.latestVersion = last.getVersion();
				if (sub.name == null)
					sub.name = last.getName();
			}
			result.subPids.add(sub);
		}
	}
}
