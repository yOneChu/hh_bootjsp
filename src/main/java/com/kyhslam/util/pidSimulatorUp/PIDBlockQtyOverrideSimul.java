package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.PIDBlockQtySimul.PickRun;
import com.kyhslam.util.pidSimulatorUp.PIDBlockQtySimul.Result;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;

/**
 * 일자별 블럭 수량 계산 + 영업사양 지정 (화면 : thymeleaf/cop/bomBlockCalculator).
 *
 * PIDBlockQtySimul / PIDShipBlockQtySimul 의 기준일 실행과 같은 순서로 실행하되,
 *  - 자재수배 PID(EL_P / SH_P) 실행 직전에 영업사양 일부를 지정값으로 바꾸고
 *  - 실행된 PID(자재수배 / 블럭 / CALL 하위)의 SPEC 열 특성코드를 모은다.
 * 지정값은 이번 실행의 메모리에만 있고 DB / 공사정보에는 쓰지 않는다.
 *
 * 기존 수량 계산 클래스는 고치지 않고, 같은 패키지의 공개되지 않은 메서드(initVariant / runElp / findPicks)만 재사용한다.
 */
public final class PIDBlockQtyOverrideSimul {

	private PIDBlockQtyOverrideSimul() {}

	/** 실행 1건 (일자 1개) */
	public static class Run {
		public Result result;
		/** 자재수배 PID 실행 직전 영업사양 (원본 + 전처리 PID 결과, 지정값 적용 전) */
		public Map<String, Object> baseSpec = new HashMap<String, Object>();
		/** 실제로 적용한 지정값 {특성코드: 값} */
		public Map<String, String> applied = new LinkedHashMap<String, String>();
		/** SPEC 열에 쓰인 특성코드 (collectSpecs = true 일 때만) */
		public List<SpecItem> specs = new ArrayList<SpecItem>();
	}

	/** SPEC 열에 쓰인 특성코드 1개 */
	public static class SpecItem {
		public String code;
		/** 실행된 PID 전체에서 SPEC 으로 쓴 라인 수 */
		public int lines;
		/** 이 코드를 SPEC 으로 쓰는 PID (처음 나온 순서) */
		public List<String> pids = new ArrayList<String>();
		/** CON 값 (처음 나온 순서, 최대 50개) */
		public List<String> cons = new ArrayList<String>();
		public boolean inBase;
		/** 실행 직전 공사 영업사양 값. 없으면 "" */
		public String baseValue = "";
	}

	/**
	 * @param pid          블럭번호 PID (엘리베이터 ex. B181B / EL_PB181B, 선박 ex. SH_A101A / SH_PA101A)
	 * @param asOfDate     yyyy-MM-dd. 값이 있으면 모든 PID(하위 포함)를 기준일 이전 가장 가까운 버전으로, 없으면 최신 버전으로 실행
	 * @param overrides    {특성코드: 값}. null / 비어 있으면 공사값 그대로
	 * @param collectSpecs true 면 실행된 PID 의 SPEC 특성코드를 모은다 (CON 값을 위해 PID 로직을 한 번 더 읽는다)
	 * @param withPickCmt  true 면 PICK 마다 BLOCKNO$SF 의 주석 PID(CMTn) 를 실행해 PickRun.cmt 에 넣는다 (기존 수량 계산과 동일)
	 */
	public static Run run(SimDb db, String project, String pid, String floor, String beforePids, String asOfDate,
			Map<String, String> overrides, boolean collectSpecs, boolean withPickCmt, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(project))
			throw new HdelBusinessException("공사번호(project) 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		// PID 이름 규칙 (PIDBlockQtySimul / PIDShipBlockQtySimul 과 동일)
		Result result = new Result();
		String input = pid.trim();
		String blockPid, elpPid;
		if (PIDShipBlockQtySimul.isShipPid(input)) {
			if (!input.toUpperCase().startsWith(PIDShipBlockQtySimul.SH_) || input.length() <= PIDShipBlockQtySimul.SH_P.length())
				throw new HdelBusinessException("선박 PID 는 SH_블럭 (ex. SH_A101A) 또는 SH_P블럭 (ex. SH_PA101A) 형식이어야 합니다 : " + input);
			result.type = PIDShipBlockQtySimul.TYPE;
			String body = input.toUpperCase().startsWith(PIDShipBlockQtySimul.SH_P)
					? input.substring(PIDShipBlockQtySimul.SH_P.length()) : input.substring(PIDShipBlockQtySimul.SH_.length());
			blockPid = PIDShipBlockQtySimul.SH_ + body;
			elpPid = PIDShipBlockQtySimul.SH_P + body;
		} else {
			blockPid = input;
			if (blockPid.toUpperCase().startsWith(PIDBlockQtySimul.EL_P) && blockPid.length() > PIDBlockQtySimul.EL_P.length())
				blockPid = blockPid.substring(PIDBlockQtySimul.EL_P.length());
			elpPid = PIDBlockQtySimul.EL_P + blockPid;
		}
		result.blockPid = blockPid;
		result.elpPid = elpPid;
		result.beforePids = beforePids;

		String iOuid = project.trim();
		if (!PIDSimul.isOuid(iOuid)) {
			Map<String, String> p = new SimSpecLoader(db).findProject(iOuid);
			if (p == null)
				throw new HdelBusinessException("No data : projectNo=" + project);
			iOuid = p.get("IOUID");
			result.projectNo = p.get("MD$NUMBER");
			result.projectTitle = p.get("MD$NUMBER") + " " + p.get("VF$VERSION") + "\n" + SimUtil.NVL(p.get("MD$DESC"), "");
		}

		SimPidRepositoryAsOf asOfRepo = SimUtil.isNullString(asOfDate) ? null : new SimPidRepositoryAsOf(asOfDate);
		if (asOfRepo != null)
			result.asOfDate = asOfRepo.getAsOfDate();
		SimContext ctx = new SimContext(db, asOfRepo != null ? asOfRepo : new SimPidRepository(), saveErrorLog);

		PidInfo blockInfo = ctx.getPidRepository().getLastPid(db, blockPid);
		if (blockInfo == null) {
			if (asOfRepo != null)
				throw new HdelBusinessException(blockPid + " 의 " + asOfRepo.getAsOfDate() + " 이전 버전이 없습니다.");
			throw new PidNotFoundException(blockPid);
		}
		result.blockPidName = blockInfo.getName();
		result.runVersion = blockInfo.getVersion();

		String floorArg = ("Y".equals(blockInfo.getIsfloorspec()) && floor != null && !floor.trim().isEmpty()) ? floor.trim() : null;
		// initVariant 는 영업사양 복사본으로 SimVariant 를 만든다 (원본 공사정보 맵은 바뀌지 않음)
		SimVariant variant = PIDBlockQtySimul.initVariant(ctx, iOuid, floorArg, result);

		Run run = new Run();
		run.result = result;

		// 지정값 : 전처리 PID 도 바뀐 값으로 계산되도록 먼저 넣고, 전처리 PID 가 덮어쓴 경우를 위해 자재수배 PID 직전에 한 번 더 넣는다
		Map<String, String> ov = normalize(overrides);
		@SuppressWarnings("unchecked")
		Map<String, Object> original = new HashMap<String, Object>(variant.getElvEnt());
		if (!ov.isEmpty())
			variant.getElvEnt().putAll(ov);

		if (!SimUtil.isNullString(beforePids)) {
			for (String beforePID : beforePids.split("\n")) {
				if (!SimUtil.isNullString(beforePID.trim())) {
					try {
						variant.appendToElvEnt(variant.calcVariantPID(beforePID.trim(), null));
					} catch (PidNotFoundException e) {
						result.warnings.add("전처리 PID 없음 : " + beforePID.trim());
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> before = new HashMap<String, Object>(variant.getElvEnt());
		for (String code : ov.keySet()) {
			if (original.containsKey(code))
				before.put(code, original.get(code));
			else
				before.remove(code);
		}
		run.baseSpec = before;
		if (!ov.isEmpty())
			variant.getElvEnt().putAll(ov);
		run.applied.putAll(ov);

		// 여기부터 실행되는 PID(자재수배 / 블럭 / CALL 하위)의 SPEC 사용 기록 (실행 결과에는 영향 없음)
		if (collectSpecs)
			ctx.startSpecUsageTracking(variant.getElvEnt());

		// 1. 자재수배 PID 실행 → 결과를 영업사양에 반영 (블럭 PID 가 결과변수를 참조)
		SimVariantMap elpMap = PIDBlockQtySimul.runElp(variant, result, null);
		variant.appendToElvEnt(elpMap);
		for (Object k : elpMap.keySet())
			result.elpResultMap.put(String.valueOf(k), elpMap.get(k));

		// 2. 결과변수 (번호 없음 → _번호 순)
		List<String> picks = PIDBlockQtySimul.findPicks(result.elpPid, elpMap.keySet());
		if (picks.isEmpty())
			result.warnings.add(result.elpPid + " 실행 결과에 " + result.elpPid + " / " + result.elpPid + "_번호 변수가 없습니다.");

		// 3. PICK 마다 블럭 PID 실행
		for (String pick : picks) {
			PickRun pr = new PickRun();
			pr.pick = pick;
			pr.pickValue = SimUtil.NVL(elpMap.get(pick), "");
			try {
				Map partInfo = new HashMap();
				partInfo.put(PIDBlockQtySimul.PICK, pick);
				SimVariantMap map = variant.calcVariantPID(blockPid, partInfo);
				for (Object k : map.keySet()) {
					if (!"debugData".equals(k) && !"elvEnt".equals(k))
						pr.resultMap.put(String.valueOf(k), map.get(k));
				}
			} catch (Exception e) {
				pr.error = e.getClass().getSimpleName() + " : " + e.getMessage();
			}
			result.runs.add(pr);
		}
		// PICK 주석 : 지정값이 들어간 같은 영업사양으로 주석 PID 를 실행 (주석 PID 의 SPEC 도 사용 기록에 포함된다)
		if (withPickCmt)
			PIDBlockQtySimul.fillPickCmt(db, ctx, variant, result);

		// 기준일 실행 : 실제로 실행한 PID 별 버전
		if (asOfRepo != null) {
			result.asOfVersions = new LinkedHashMap<String, Map<String, Object>>(asOfRepo.getPicked());
			for (Map.Entry<String, Map<String, Object>> e : result.asOfVersions.entrySet()) {
				if (e.getValue().get("version") == null)
					result.warnings.add(e.getKey() + " : " + asOfRepo.getAsOfDate() + " 이전 버전이 없어 실행하지 못했습니다.");
			}
		}

		if (collectSpecs)
			run.specs = collect(ctx, run.baseSpec);
		return run;
	}

	/** 실행된 PID 의 SPEC 특성코드 (PID 실행 순서 → 로직 순서) */
	private static List<SpecItem> collect(SimContext ctx, Map<String, Object> base) {
		Map<String, SpecItem> map = new LinkedHashMap<String, SpecItem>();
		for (SimContext.PidSpecUse u : ctx.getSpecUsages()) {
			if (u.javaMethod)
				continue;
			for (SimContext.SpecUse s : u.specs.values()) {
				if (s.cond <= 0 || s.code == null)
					continue;
				SpecItem item = item(map, s.code, base);
				item.lines += s.cond;
				if (!item.pids.contains(u.pid))
					item.pids.add(u.pid);
			}
			collectCons(ctx, u.pid, map);
		}
		return new ArrayList<SpecItem>(map.values());
	}

	/** PID 로직의 SPEC = CON 에서 CON 값을 모은다 (실행한 것과 같은 버전 기준. 실패해도 CON 없이 진행) */
	private static void collectCons(SimContext ctx, String pid, Map<String, SpecItem> map) {
		try {
			PidInfo info = ctx.getPidRepository().getLastPid(ctx.getDb(), pid);
			if (info == null)
				return;
			SimVariantMap logic = ctx.getPidRepository().getLogic(ctx.getDb(), pid, info.getVersion());
			List data = logic == null ? null : (List) logic.get("data");
			if (data == null)
				return;
			for (Object o : data) {
				Map row = (Map) o;
				if ("#".equals(SimUtil.NVL(row.get("ADDR"), "").trim()))
					continue;
				List specList = (List) row.get("specList");
				List conList = (List) row.get("conList");
				if (specList == null || conList == null)
					continue;
				for (int j = 0; j < specList.size() && j < conList.size(); j++) {
					Object spec = specList.get(j);
					if (spec == null)
						continue;
					String code = SimVariant.specCode(String.valueOf(spec));
					SpecItem item = code == null ? null : map.get(code);
					if (item == null)
						continue;
					String con = conList.get(j) == null ? "" : String.valueOf(conList.get(j)).trim();
					if (!con.isEmpty() && !con.contains("[$") && !item.cons.contains(con) && item.cons.size() < 50)
						item.cons.add(con);
				}
			}
		} catch (Exception ignored) {
			// CON 후보는 입력 보조용이라 실패해도 무시
		}
	}

	private static SpecItem item(Map<String, SpecItem> map, String code, Map<String, Object> base) {
		SpecItem item = map.get(code);
		if (item == null) {
			item = new SpecItem();
			item.code = code;
			item.inBase = base.containsKey(code);
			Object v = base.get(code);
			item.baseValue = v == null ? "" : String.valueOf(v);
			map.put(code, item);
		}
		return item;
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
