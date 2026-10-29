package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.PIDBlockQtySimul.PickRun;
import com.kyhslam.util.pidSimulatorUp.PIDBlockQtySimul.Result;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;

/**
 * 선박 블럭 수량 계산 (PID 시뮬레이터 화면의 [수량 계산] - PID 가 SH 로 시작할 때)
 *   1. SH_P + 블럭 (ex. SH_PB181B) 을 실행해 결과변수 SH_PB181B_1, SH_PB181B_2 ... 를 구한다.
 *   2. 결과변수마다 partInfo 에 PICK = SH_PB181B_n 을 넣고 수량블럭 PID (ex. SH_B181B) 를 따로 실행한다.
 *
 * 엘리베이터(PIDBlockQtySimul) 와 흐름은 같고 PID 이름 규칙만 다르다. 엘리베이터 로직에 영향이 없도록 별도 클래스로 둔다.
 */
public class PIDShipBlockQtySimul {

	public static final String SH = "SH";
	/** 수량블럭 접두어 ex) SH_A101A */
	public static final String SH_ = "SH_";
	/** 자재수배 접두어 ex) SH_PA101A */
	public static final String SH_P = "SH_P";
	public static final String TYPE = "SHIP";

	private PIDShipBlockQtySimul() {}

	/** 선박 PID 여부 (SH 로 시작) */
	public static boolean isShipPid(String pid) {
		return pid != null && pid.trim().toUpperCase().startsWith(SH);
	}

	/**
	 * @param pid      수량블럭 PID (ex. SH_A101A). 자재수배 PID SH_PA101A 를 넣어도 SH_A101A 로 실행
	 * @param asOfDate yyyy-MM-dd. 값이 있으면 version 은 무시하고 모든 PID(하위 포함)를 기준일 이전 가장 가까운 버전으로 실행
	 * 그 외 파라미터는 PIDBlockQtySimul.run 과 같다.
	 */
	public static Result run(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			String asOfDate, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(project))
			throw new HdelBusinessException("공사번호(project) 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		String input = pid.trim();
		if (!input.toUpperCase().startsWith(SH_) || input.length() <= SH_P.length())
			throw new HdelBusinessException("선박 PID 는 SH_블럭 (ex. SH_A101A) 또는 SH_P블럭 (ex. SH_PA101A) 형식이어야 합니다 : " + input);

		Result result = new Result();
		result.type = TYPE;
		// SH_PA101A → 수량블럭 SH_A101A / 자재수배 SH_PA101A
		String body = input.toUpperCase().startsWith(SH_P) ? input.substring(SH_P.length()) : input.substring(SH_.length());
		String blockPid = SH_ + body;
		result.blockPid = blockPid;
		result.elpPid = SH_P + body;
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
		if (asOfRepo != null) {
			version = null;
			result.asOfDate = asOfRepo.getAsOfDate();
		}
		SimContext ctx = new SimContext(db, asOfRepo != null ? asOfRepo : new SimPidRepository(), saveErrorLog);

		PidInfo blockInfo = ctx.getPidRepository().getLastPid(db, blockPid);
		if (blockInfo == null) {
			if (asOfRepo != null)
				throw new HdelBusinessException(blockPid + " 의 " + asOfRepo.getAsOfDate() + " 이전 버전이 없습니다.");
			throw new PidNotFoundException(blockPid);
		}
		result.blockPidName = blockInfo.getName();
		result.testVersion = version != null && version == SimConsts.TEST_VERSION;
		if (version == null) {
			result.runVersion = blockInfo.getVersion();
		} else {
			PidInfo runInfo = ctx.getPidRepository().getPid(db, blockPid, version);
			if (runInfo == null)
				throw new HdelBusinessException(blockPid + " 의 " + (result.testVersion ? "TEST 버전" : version + " 버전") + "이 없습니다.");
			result.runVersion = runInfo.getVersion();
		}

		String floorArg = ("Y".equals(blockInfo.getIsfloorspec()) && floor != null && !floor.trim().isEmpty()) ? floor.trim() : null;
		SimVariant variant = PIDBlockQtySimul.initVariant(ctx, iOuid, floorArg, result);

		// 전처리 PID (PIDSimul.debugPid 와 동일)
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

		// 1. SH_P 실행 → 결과를 영업사양에 반영 (수량블럭 PID 가 SH_P 결과변수를 참조)
		SimVariantMap elpMap;
		try {
			elpMap = variant.calcVariantPID(result.elpPid, null);
		} catch (PidNotFoundException e) {
			throw new HdelBusinessException(result.elpPid + " PID 가 없습니다.");
		}
		variant.appendToElvEnt(elpMap);
		for (Object k : elpMap.keySet())
			result.elpResultMap.put(String.valueOf(k), elpMap.get(k));

		// 2. 결과변수 SH_PE321A(번호 없음) → SH_PB181B_n (번호순)
		List<String> picks = PIDBlockQtySimul.findPicks(result.elpPid, elpMap.keySet());
		if (picks.isEmpty())
			result.warnings.add(result.elpPid + " 실행 결과에 " + result.elpPid + " / " + result.elpPid + "_번호 변수가 없습니다.");

		// 3. PICK 마다 수량블럭 PID 실행
		for (String pick : picks) {
			PickRun run = new PickRun();
			run.pick = pick;
			run.pickValue = SimUtil.NVL(elpMap.get(pick), "");
			try {
				Map partInfo = new HashMap();
				partInfo.put(PIDBlockQtySimul.PICK, pick);
				SimVariantMap map = version != null
						? variant.calcVariantPID(blockPid, version, partInfo, true)
						: variant.calcVariantPID(blockPid, partInfo);
				for (Object k : map.keySet()) {
					if (!"debugData".equals(k) && !"elvEnt".equals(k))
						run.resultMap.put(String.valueOf(k), map.get(k));
				}
			} catch (Exception e) {
				run.error = e.getClass().getSimpleName() + " : " + e.getMessage();
			}
			result.runs.add(run);
		}

		// 기준일 실행 : 실제로 실행한 PID 별 버전 (기준일 이전 버전이 없던 PID 는 경고)
		if (asOfRepo != null) {
			result.asOfVersions = asOfRepo.getPicked();
			for (Map.Entry<String, Map<String, Object>> e : result.asOfVersions.entrySet()) {
				if (e.getValue().get("version") == null)
					result.warnings.add(e.getKey() + " : " + asOfRepo.getAsOfDate() + " 이전 버전이 없어 실행하지 못했습니다.");
			}
		}
		return result;
	}
}
