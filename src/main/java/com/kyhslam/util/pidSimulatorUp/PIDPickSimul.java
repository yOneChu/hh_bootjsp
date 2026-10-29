package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;

/**
 * 블럭번호 PID 의 PICK 지정 실행 (PID 시뮬레이터 화면의 [PICK 적용 실행])
 *   블럭번호 PID (B181B / SH_B181B) 로 실행할 때 자재수배 PID (EL_PB181B / SH_PB181B) 결과변수 중 하나를 골라
 *   partInfo 에 PICK = 고른 키 를 넣고 디버그 실행한다. 흐름은 수량 계산(PIDBlockQtySimul) 과 같다.
 *     전처리 PID → 자재수배 PID 실행 결과를 영업사양에 반영 → 블럭 PID 실행 (PICK)
 *
 * PIDSimul 의 기존 실행 결과에 영향을 주지 않도록 별도 클래스로 둔다. 화면 결과 구성은 PIDSimul 과 같다.
 */
public class PIDPickSimul {

	public static final String TYPE_ELV = "ELV";
	public static final String TYPE_SHIP = "SHIP";

	private PIDPickSimul() {}

	/** 블럭번호 PID 의 자재수배 PID 와 PICK 후보 */
	public static class PickKeys {
		/** ELV / SHIP. 블럭번호 PID 가 아니면 null */
		public String type;
		public String blockPid;
		/** 자재수배 PID ex) EL_PB181B, SH_PB181B */
		public String elpPid;
		/** PICK 후보 {key, value} (번호 없는 키 먼저, 그 다음 번호순) */
		public List<Map<String, String>> picks = new ArrayList<Map<String, String>>();
		/** 후보가 없는 이유 (블럭번호 PID 아님, 자재수배 PID 없음 등) */
		public String message;
	}

	/**
	 * 블럭번호 PID → [type, blockPid, elpPid]. 자재수배 PID(EL_P / SH_P) 를 넣었거나 형식이 다르면 null
	 *   엘베 : B181B → EL_PB181B / 선박 : SH_B181B → SH_PB181B
	 */
	static String[] toElpPid(String pid) {
		if (pid == null)
			return null;
		String p = pid.trim();
		String u = p.toUpperCase();
		if (u.startsWith(PIDBlockQtySimul.EL_P) || u.startsWith(PIDShipBlockQtySimul.SH_P))
			return null;
		if (PIDShipBlockQtySimul.isShipPid(p)) {
			if (!u.startsWith(PIDShipBlockQtySimul.SH_) || p.length() <= PIDShipBlockQtySimul.SH_.length())
				return null;
			return new String[] { TYPE_SHIP, p, PIDShipBlockQtySimul.SH_P + p.substring(PIDShipBlockQtySimul.SH_.length()) };
		}
		return new String[] { TYPE_ELV, p, PIDBlockQtySimul.EL_P + p };
	}

	/** 공사번호 → iOuid (ouid 면 그대로). [iOuid, projectNo, projectTitle] */
	private static String[] resolveProject(SimDb db, String project) throws Exception {
		if (SimUtil.isNullString(project))
			throw new HdelBusinessException("공사번호(project) 가 없습니다.");
		String v = project.trim();
		if (PIDSimul.isOuid(v))
			return new String[] { v, null, null };
		Map<String, String> p = new SimSpecLoader(db).findProject(v);
		if (p == null)
			throw new HdelBusinessException("No data : projectNo=" + project);
		return new String[] { p.get("IOUID"), p.get("MD$NUMBER"),
				p.get("MD$NUMBER") + " " + p.get("VF$VERSION") + "\n" + SimUtil.NVL(p.get("MD$DESC"), "") };
	}

	/** 전처리 PID (PIDSimul.debugPid 와 동일) */
	private static void runBeforePids(SimVariant variant, String beforePids) throws Exception {
		if (SimUtil.isNullString(beforePids))
			return;
		for (String beforePID : beforePids.split("\n")) {
			if (!SimUtil.isNullString(beforePID.trim())) {
				try {
					variant.appendToElvEnt(variant.calcVariantPID(beforePID.trim(), null));
				} catch (PidNotFoundException ignored) {}
			}
		}
	}

	/**
	 * PICK 후보 : 자재수배 PID 를 실행한 결과변수 중 elpPid / elpPid_번호
	 * @param floor 층별 PID 일 때 적용할 층 (블럭 PID 기준)
	 */
	public static PickKeys findPickKeys(SimDb db, String project, String pid, String floor, String beforePids,
			boolean saveErrorLog) throws Exception {
		PickKeys keys = new PickKeys();
		String[] names = toElpPid(pid);
		if (names == null) {
			keys.message = "블럭번호 PID 가 아닙니다.";
			return keys;
		}
		keys.blockPid = names[1];
		keys.elpPid = names[2];

		String[] proj = resolveProject(db, project);
		SimContext ctx = new SimContext(db, new SimPidRepository(), saveErrorLog);
		PidInfo blockInfo = ctx.getPidRepository().getLastPid(db, keys.blockPid);
		if (blockInfo == null) {
			keys.message = keys.blockPid + " PID 가 없습니다.";
			return keys;
		}
		if (ctx.getPidRepository().getLastPid(db, keys.elpPid) == null) {
			keys.message = keys.elpPid + " PID 가 없습니다.";
			return keys;
		}

		String floorArg = ("Y".equals(blockInfo.getIsfloorspec()) && floor != null && !floor.trim().isEmpty()) ? floor.trim() : null;
		SimVariant variant = PIDBlockQtySimul.initVariant(ctx, proj[0], floorArg, new PIDBlockQtySimul.Result());
		runBeforePids(variant, beforePids);

		SimVariantMap elpMap = variant.calcVariantPID(keys.elpPid, null);
		for (String k : PIDBlockQtySimul.findPicks(keys.elpPid, elpMap.keySet())) {
			Map<String, String> m = new LinkedHashMap<String, String>();
			m.put("key", k);
			m.put("value", SimUtil.NVL(elpMap.get(k), ""));
			keys.picks.add(m);
		}
		keys.type = names[0];
		if (keys.picks.isEmpty())
			keys.message = keys.elpPid + " 실행 결과에 " + keys.elpPid + " / " + keys.elpPid + "_번호 변수가 없습니다.";
		return keys;
	}

	/**
	 * 블럭번호 PID 를 PICK 지정으로 디버그 실행 (결과 구성은 PIDSimul.simulateWithOuid 와 동일)
	 * @param pick        PICK 값 ex) EL_PB181B_1
	 * @param version     블럭 PID 버전 (null : 최신, -1 : TEST)
	 * @param subVersions CALL 하위 PID → 버전
	 */
	public static PIDSimulResult simulate(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			Map<String, Integer> subVersions, String pick, boolean saveErrorLog) throws Exception {
		String[] names = toElpPid(pid);
		if (names == null)
			throw new HdelBusinessException("블럭번호 PID 가 아닙니다 (EL_P / SH_P 자재수배 PID 는 PICK 실행 불가) : " + pid);
		if (SimUtil.isNullString(pick))
			throw new HdelBusinessException("PICK 이 없습니다.");
		String elpPid = names[2];

		String[] proj = resolveProject(db, project);
		SimContext ctx = new SimContext(db, new SimPidRepository(), saveErrorLog);

		PIDSimulResult result = new PIDSimulResult();
		result.iOuid = proj[0];
		result.projectNo = proj[1];
		result.pid = names[1];
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

		String floorArg = ("Y".equals(result.isFloorSpec) && floor != null) ? floor.trim() : null;
		SimVariant variant = PIDSimul.initVariantWithOuid(ctx, proj[0], floorArg, result);

		// 전처리 → 자재수배 PID 결과를 영업사양에 반영 (수량 계산과 동일)
		runBeforePids(variant, beforePids);
		try {
			variant.appendToElvEnt(variant.calcVariantPID(elpPid, null));
		} catch (PidNotFoundException e) {
			throw new HdelBusinessException(elpPid + " PID 가 없습니다.");
		}

		// 블럭 PID : partInfo 에 PICK 을 넣고 디버그 실행 (CALL 하위 PID 버전 지정 / 실행 기록은 PIDSimul 과 동일)
		Map partInfo = new HashMap();
		partInfo.put(PIDBlockQtySimul.PICK, pick.trim());
		ctx.startSubPidTracking(subVersions);
		SimVariantMap logicMap = version != null
				? variant.calcVariantPID(result.pid, version, partInfo, true)
				: variant.calcVariantPID(result.pid, partInfo, null, 1, true);

		PIDSimul.toResult(ctx, variant, logicMap, result);
		PIDSimul.toSubPids(ctx, result);
		if (proj[1] != null)
			result.projectTitle = proj[2];
		return result;
	}
}
