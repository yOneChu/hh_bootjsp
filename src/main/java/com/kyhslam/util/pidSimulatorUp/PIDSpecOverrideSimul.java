package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;

/**
 * 영업사양 지정 PID 실행 (화면 : thymeleaf/cop/bomCalculatorResult).
 *
 * PIDSimul.simulateWithOuid 와 같은 순서로 실행하되, 대상 PID 실행 직전에 영업사양(elvEnt)의 일부 값을
 * 사용자가 입력한 값으로 바꿔서 실행한다. 바꾼 값은 이번 실행의 메모리에만 있고 DB / 공사정보에는 쓰지 않는다.
 *
 * 기존 PIDSimul 은 고치지 않고, 같은 패키지의 공개되지 않은 메서드(initVariantWithOuid / toResult / toSubPids)만 재사용한다.
 */
public final class PIDSpecOverrideSimul {

	private PIDSpecOverrideSimul() {}

	/** 실행 결과 + 대상 PID 실행 직전 영업사양 */
	public static class Run {
		public PIDSimulResult result;
		/** 대상 PID 실행 직전 영업사양 (원본 + 전처리 PID 결과, 사용자 지정값 적용 전) */
		public Map<String, Object> baseSpec = new HashMap<String, Object>();
		/** 실제로 적용한 지정값 {특성코드: 값} */
		public Map<String, String> applied = new LinkedHashMap<String, String>();
	}

	/** SPEC 열에 쓰인 특성코드 1개 */
	public static class SpecItem {
		public String code;
		/** 대상 PID 에서 SPEC 으로 쓴 라인 수 */
		public int lines;
		/** CALL 하위 PID 에서 SPEC 으로 쓴 라인 수 */
		public int sub;
		/** 이 코드를 SPEC 으로 쓰는 CALL 하위 PID */
		public List<String> subPids = new ArrayList<String>();
		/** 대상 PID 의 CON 값 (처음 나온 순서) */
		public List<String> cons = new ArrayList<String>();
		/** 공사 영업사양(실행 직전)에 키가 있는지 */
		public boolean inBase;
		/** 공사 영업사양(실행 직전) 값. 없으면 "" */
		public String baseValue = "";
		/** 처음 나온 PID (대상 PID 또는 하위 PID) */
		public String firstPid;
	}

	/**
	 * 영업사양 일부를 지정값으로 바꿔 PID 를 실행한다.
	 * @param project    공사번호 ex) 206663L01, 또는 공사정보 ouid ex) elv_info$vf@a810066d
	 * @param version    null : 최신, -1 : TEST, 그 외 : 지정 버전
	 * @param beforePids 전처리 PID (줄바꿈 구분, null 가능)
	 * @param overrides  {특성코드: 값}. null / 비어 있으면 공사값 그대로 실행
	 */
	public static Run run(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			Map<String, String> overrides, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(project))
			throw new HdelBusinessException("공사번호(project) 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		String iOuid = project.trim();
		String projectNo = null, projectTitle = null;
		if (!PIDSimul.isOuid(iOuid)) {
			Map<String, String> p = new SimSpecLoader(db).findProject(iOuid);
			if (p == null)
				throw new HdelBusinessException("No data : projectNo=" + project);
			iOuid = p.get("IOUID");
			projectNo = p.get("MD$NUMBER");
			projectTitle = p.get("MD$NUMBER") + " " + p.get("VF$VERSION") + "\n" + SimUtil.NVL(p.get("MD$DESC"), "");
		}

		SimContext ctx = new SimContext(db, new SimPidRepository(), saveErrorLog);

		PIDSimulResult result = new PIDSimulResult();
		result.iOuid = iOuid;
		result.projectNo = projectNo;
		result.pid = pid.trim();
		result.version = version;
		result.testVersion = version != null && version == SimConsts.TEST_VERSION;
		result.beforePids = beforePids;

		PidInfo pidInfo = ctx.getPidRepository().getLastPid(db, result.pid);
		if (pidInfo == null)
			throw new PidNotFoundException(result.pid);
		result.pidName = pidInfo.getName();
		result.isFloorSpec = "Y".equals(pidInfo.getIsfloorspec()) ? "Y" : "N";
		result.latestVersion = pidInfo.getVersion();

		if (version == null) {
			result.runVersion = pidInfo.getVersion();
		} else {
			PidInfo runInfo = ctx.getPidRepository().getPid(db, result.pid, version);
			if (runInfo == null)
				throw new HdelBusinessException(result.pid + " 의 " + (result.testVersion ? "TEST 버전" : version + " 버전") + "이 없습니다.");
			result.runVersion = runInfo.getVersion();
		}

		String floorArg = ("Y".equals(result.isFloorSpec) && floor != null && !floor.trim().isEmpty()) ? floor.trim() : null;
		SimVariant variant = PIDSimul.initVariantWithOuid(ctx, iOuid, floorArg, result);
		if (projectTitle != null)
			result.projectTitle = projectTitle;
		if (result.projectNo == null)
			result.projectNo = projectNo;

		Run run = new Run();
		run.result = result;

		// 지정값 : 전처리 PID 도 바뀐 값으로 계산되도록 먼저 넣고, 전처리 PID 가 덮어쓴 경우를 위해 대상 PID 직전에 한 번 더 넣는다
		Map<String, String> ov = normalize(overrides);
		@SuppressWarnings("unchecked")
		Map<String, Object> original = new HashMap<String, Object>(variant.getElvEnt());
		applyOverrides(variant, ov);

		if (!SimUtil.isNullString(beforePids)) {
			for (String before : beforePids.split("\n")) {
				if (!SimUtil.isNullString(before.trim())) {
					try {
						SimVariantMap r = variant.calcVariantPID(before.trim(), null);
						variant.appendToElvEnt(r);
					} catch (PidNotFoundException ignored) {}
				}
			}
		}

		// 대상 PID 실행 직전 영업사양 (지정값 적용 전 값으로 기록 : 원래 값과 비교용)
		@SuppressWarnings("unchecked")
		Map<String, Object> before = new HashMap<String, Object>(variant.getElvEnt());
		for (String code : ov.keySet()) {
			if (original.containsKey(code))
				before.put(code, original.get(code));
			else
				before.remove(code);
		}
		run.baseSpec = before;

		applyOverrides(variant, ov);
		run.applied.putAll(ov);

		// 대상 PID 실행 (PIDSimul.debugPid 와 같은 순서. CALL 하위 PID 는 최신 버전)
		ctx.startSubPidTracking(null);
		ctx.startSpecUsageTracking(variant.getElvEnt());
		SimVariantMap logicMap = version != null
				? variant.calcVariantPID(result.pid, version, null, true)
				: variant.calcVariantPID(result.pid, null, null, 1, true);

		PIDSimul.toResult(ctx, variant, logicMap, result);
		PIDSimul.toSubPids(ctx, result);
		return run;
	}

	/**
	 * 실행 결과에서 SPEC 열에 쓰인 특성코드를 모은다 (대상 PID → CALL 하위 PID 순서, 처음 나온 순서).
	 * 실행된 PID 의 로직 전체(조건 성립 여부와 무관)를 기준으로 하며, 이번 실행에서 CALL 되지 않은 하위 PID 는 포함되지 않는다.
	 * @param includeSub CALL 하위 PID 의 SPEC 포함 여부
	 */
	public static List<SpecItem> collectSpecs(Run run, boolean includeSub) {
		PIDSimulResult result = run.result;
		Map<String, SpecItem> map = new LinkedHashMap<String, SpecItem>();

		// 대상 PID : 라인 수 / CON 값 (주석 라인 제외는 영업사양 사용 기록과 같이 ADDR '#' 기준)
		for (PIDSimulResult.Line line : result.lines) {
			if ("#".equals(SimUtil.NVL(line.addr, "").trim()))
				continue;
			Set<String> inLine = new HashSet<String>();
			for (PIDSimulResult.Condition c : line.conditions) {
				if (c.spec == null || (c.spec.contains("[$") && c.spec.contains("$]")))
					continue;
				String code = SimVariant.specCode(c.spec);
				if (code == null)
					continue;
				SpecItem item = item(map, code, result.pid, run.baseSpec);
				if (inLine.add(code))
					item.lines++;
				String con = c.con == null ? "" : c.con.trim();
				if (!con.isEmpty() && !item.cons.contains(con) && item.cons.size() < 50)
					item.cons.add(con);
			}
		}

		// CALL 하위 PID : 영업사양 사용 기록(SPEC 칸 사용 라인 수)
		if (includeSub) {
			for (PIDSimulResult.PidSpecUsage pu : result.specUsages) {
				if (pu.pid == null || pu.pid.equalsIgnoreCase(result.pid))
					continue;
				for (PIDSimulResult.SpecUse s : pu.specs) {
					if (s.cond <= 0 || s.code == null)
						continue;
					SpecItem item = item(map, s.code, pu.pid, run.baseSpec);
					item.sub += s.cond;
					if (!item.subPids.contains(pu.pid))
						item.subPids.add(pu.pid);
				}
			}
		}
		return new ArrayList<SpecItem>(map.values());
	}

	private static SpecItem item(Map<String, SpecItem> map, String code, String pid, Map<String, Object> base) {
		SpecItem item = map.get(code);
		if (item == null) {
			item = new SpecItem();
			item.code = code;
			item.firstPid = pid;
			item.inBase = base.containsKey(code);
			Object v = base.get(code);
			item.baseValue = v == null ? "" : String.valueOf(v);
			map.put(code, item);
		}
		return item;
	}

	@SuppressWarnings("unchecked")
	private static void applyOverrides(SimVariant variant, Map<String, String> ov) {
		if (!ov.isEmpty())
			variant.getElvEnt().putAll(ov);
	}

	private static Map<String, String> normalize(Map<String, String> overrides) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (overrides == null)
			return map;
		for (Map.Entry<String, String> e : overrides.entrySet()) {
			if (e.getKey() == null || e.getKey().trim().isEmpty())
				continue;
			map.put(e.getKey().trim(), e.getValue() == null ? "" : e.getValue().trim());
		}
		return map;
	}
}
