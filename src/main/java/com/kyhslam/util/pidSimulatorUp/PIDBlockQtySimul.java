package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 블럭 수량 계산 (PID 시뮬레이터 화면의 [수량 계산])
 *   1. EL_P + 블럭번호 (ex. EL_PB181B) 를 실행해 결과변수 EL_PB181B_1, EL_PB181B_2 ... 를 구한다.
 *   2. 결과변수마다 partInfo 에 PICK = EL_PB181B_n 을 넣고 블럭번호 PID (ex. B181B) 를 따로 실행한다.
 *
 * PIDSimul 의 기존 실행 결과에 영향을 주지 않도록 별도 클래스로 둔다.
 */
public class PIDBlockQtySimul {

	public static final String EL_P = "EL_P";
	public static final String PICK = "PICK";

	private PIDBlockQtySimul() {}

	/** PICK 1건 실행 결과 */
	public static class PickRun {
		/** PICK 값 ex) EL_PB181B_1 */
		public String pick;
		/** EL_P 실행 결과의 PICK 변수 값 */
		public String pickValue;
		/** 블럭 PID 최종 결과값 (debugData / elvEnt 제외) */
		public Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		public String error;
	}

	/** 수량 계산 결과 */
	public static class Result {
		/** ELV : 엘리베이터 (EL_P), SHIP : 선박 (SH_P, PIDShipBlockQtySimul) */
		public String type = "ELV";
		public String projectNo;
		public String projectTitle;
		/** 블럭번호 PID ex) B181B */
		public String blockPid;
		public String blockPidName;
		/** 블럭 PID 실행 버전 */
		public Integer runVersion;
		public boolean testVersion;
		/** EL_P PID ex) EL_PB181B */
		public String elpPid;
		public String floor;
		public String beforePids;
		/** EL_P 실행 결과 전체 */
		public Map<String, Object> elpResultMap = new LinkedHashMap<String, Object>();
		public List<PickRun> runs = new ArrayList<PickRun>();
		public List<String> warnings = new ArrayList<String>();
		/** 기준일 (yyyy-MM-dd). null 이면 최신 버전 실행 */
		public String asOfDate;
		/** 기준일 실행 시 PID 별 실행 버전 {version, regDate} */
		public Map<String, Map<String, Object>> asOfVersions;
	}

	/**
	 * @param project    공사번호 또는 공사정보 ouid
	 * @param pid        블럭번호 PID (ex. B181B). EL_PB181B 를 넣어도 B181B 로 실행
	 * @param floor      층별 PID 일 때 적용할 층 (블럭 PID 기준)
	 * @param version    블럭 PID 버전 (null : 최신, -1 : TEST). EL_P / 전처리 PID 는 최신
	 * @param beforePids 전처리 PID (줄바꿈 구분). EL_P 실행 전에 먼저 실행
	 */
	public static Result run(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			boolean saveErrorLog) throws Exception {
		return run(db, project, pid, floor, version, beforePids, null, saveErrorLog);
	}

	/**
	 * 기준일 지정 실행
	 * @param asOfDate yyyy-MM-dd. 값이 있으면 version 은 무시하고 EL_P / 전처리 / 블럭 / CALL 하위 PID 모두
	 *                 기준일 이전(당일이면 그 날 마지막 시각) 중 가장 가까운 버전으로 실행. null 이면 기존과 동일
	 */
	public static Result run(SimDb db, String project, String pid, String floor, Integer version, String beforePids,
			String asOfDate, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(project))
			throw new HdelBusinessException("공사번호(project) 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		Result result = new Result();
		String blockPid = pid.trim();
		if (blockPid.toUpperCase().startsWith(EL_P) && blockPid.length() > EL_P.length())
			blockPid = blockPid.substring(EL_P.length());
		result.blockPid = blockPid;
		result.elpPid = EL_P + blockPid;
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
		SimVariant variant = initVariant(ctx, iOuid, floorArg, result);

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

		// 1. EL_P 실행 → 결과를 영업사양에 반영 (블럭 PID 가 EL_P 결과변수를 참조)
		SimVariantMap elpMap;
		try {
			elpMap = variant.calcVariantPID(result.elpPid, null);
		} catch (PidNotFoundException e) {
			throw new HdelBusinessException(result.elpPid + " PID 가 없습니다.");
		}
		variant.appendToElvEnt(elpMap);
		for (Object k : elpMap.keySet())
			result.elpResultMap.put(String.valueOf(k), elpMap.get(k));

		// 2. 결과변수 EL_PE321A(번호 없음) → EL_PB181B_n (번호순)
		List<String> picks = findPicks(result.elpPid, elpMap.keySet());
		if (picks.isEmpty())
			result.warnings.add(result.elpPid + " 실행 결과에 " + result.elpPid + " / " + result.elpPid + "_번호 변수가 없습니다.");

		// 3. PICK 마다 블럭 PID 실행
		for (String pick : picks) {
			PickRun run = new PickRun();
			run.pick = pick;
			run.pickValue = SimUtil.NVL(elpMap.get(pick), "");
			try {
				Map partInfo = new HashMap();
				partInfo.put(PICK, pick);
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

	/**
	 * keys 중 elpPid 자체(번호 없음, ex. EL_PE321A) 를 맨 앞에, 그 다음 elpPid_숫자 를 번호순으로
	 * (블럭에 따라 EL_PE321A 부터 시작하거나 EL_PB181B_1 부터 시작한다. 선박 SH_P 도 동일)
	 */
	static List<String> findPicks(String elpPid, Collection keys) {
		Pattern p = Pattern.compile("^" + Pattern.quote(elpPid) + "_(\\d+)$");
		TreeMap<Integer, String> sorted = new TreeMap<Integer, String>();
		for (Object k : keys) {
			String key = String.valueOf(k);
			if (key.equals(elpPid)) {
				sorted.put(-1, key);
				continue;
			}
			Matcher m = p.matcher(key);
			if (m.matches())
				sorted.put(Integer.valueOf(m.group(1)), key);
		}
		return new ArrayList<String>(sorted.values());
	}

	/** PIDSimul.initVariantWithOuid 와 같은 방식으로 영업사양(층) 준비 */
	static SimVariant initVariant(SimContext ctx, String iOuid, String floor, Result result) throws Exception {
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
			if (floor != null)
				throw e;
			result.warnings.add("층 정보 로드 실패 - 층 정보 없이 실행합니다 : " + e.getMessage());
		}

		HashMap elvEnt = null;
		if (floor != null && floorMasterList != null) {
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

		// appendToElvEnt 가 원본 맵을 바꾸므로 복사본 사용
		return new SimVariant(ctx, new HashMap(elvEnt), floorMasterList);
	}
}
