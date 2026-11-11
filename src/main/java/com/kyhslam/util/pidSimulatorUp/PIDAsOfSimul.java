package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.HdelBusinessException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 기준일 PID 실행 (API 용) : 호기번호 + 일자 + PID 로 최종 OUTPUT 만 구한다.
 *   대상 PID 와 CALL 하위 PID 모두 기준일 이전(당일 포함) 중 가장 늦게 등록된 버전으로 실행한다 (SimPidRepositoryAsOf).
 *   TEST 버전(-1)은 고르지 않는다. 영업사양(공사정보)은 기준일이 아니라 현재 값이다.
 */
public class PIDAsOfSimul {

	private PIDAsOfSimul() {}

	public static class Result {
		/** 기준일 yyyy-MM-dd */
		public String asOfDate;
		/** 대상 PID 의 실행 버전 */
		public int runVersion;
		/** 최종 OUTPUT (시뮬레이터 화면의 '최종 OUTPUT' 과 같은 resultMap : debugData / elvEnt 제외) */
		public Map<String, Object> output = new LinkedHashMap<String, Object>();
		/** 실제로 고른 PID 별 버전 (PID → {version, regDate}). 기준일 이전 버전이 없으면 version null */
		public Map<String, Map<String, Object>> versions = new LinkedHashMap<String, Map<String, Object>>();
		public List<String> warnings = new ArrayList<String>();
	}

	/**
	 * @param hogi 호기번호(공사번호) ex) 206663L01, 또는 공사정보 ouid
	 * @param date 기준일 yyyyMMdd (yyyy-MM-dd 도 허용)
	 * @param pid  실행할 PID ex) EL_A101A
	 */
	public static Result run(SimDb db, String hogi, String date, String pid, boolean saveErrorLog) throws Exception {
		if (SimUtil.isNullString(hogi))
			throw new HdelBusinessException("호기번호(hogi) 가 없습니다.");
		if (SimUtil.isNullString(pid))
			throw new HdelBusinessException("PID 가 없습니다.");

		String asOfDate = toAsOfDate(date);
		String runPid = pid.trim();

		String iOuid = hogi.trim();
		if (!PIDSimul.isOuid(iOuid)) {
			Map<String, String> p = new SimSpecLoader(db).findProject(iOuid);
			if (p == null)
				throw new HdelBusinessException("No data : hogi=" + hogi);
			iOuid = p.get("IOUID");
		}

		SimPidRepositoryAsOf repo = new SimPidRepositoryAsOf(asOfDate);
		SimContext ctx = new SimContext(db, repo, saveErrorLog);

		PidInfo pidInfo = repo.getLastPid(db, runPid);
		if (pidInfo == null)
			throw new HdelBusinessException(runPid + " 의 " + asOfDate + " 이전 버전이 없습니다.");

		Result result = new Result();
		result.asOfDate = asOfDate;
		result.runVersion = pidInfo.getVersion();

		// 층 지정 없이 실행 (공사정보 기준)
		PIDSimulResult info = new PIDSimulResult();
		SimVariant variant = PIDSimul.initVariantWithOuid(ctx, iOuid, null, info);
		result.warnings.addAll(info.warnings);

		// 대상 / CALL 하위 PID 모두 저장소(repo)의 getLastPid = 기준일 버전으로 실행된다
		SimVariantMap map = variant.calcVariantPID(runPid, null);
		for (Object k : map.keySet()) {
			if (!"debugData".equals(k) && !"elvEnt".equals(k))
				result.output.put(String.valueOf(k), map.get(k));
		}

		result.versions = repo.getPicked();
		for (Map.Entry<String, Map<String, Object>> e : result.versions.entrySet()) {
			if (e.getValue().get("version") == null)
				result.warnings.add(e.getKey() + " : " + asOfDate + " 이전 버전이 없어 실행하지 못했습니다.");
		}
		return result;
	}

	/** yyyyMMdd (또는 yyyy-MM-dd) → yyyy-MM-dd. 없는 날짜면 오류 */
	static String toAsOfDate(String date) {
		String d = date == null ? "" : date.trim().replace("-", "");
		if (!d.matches("\\d{8}"))
			throw new HdelBusinessException("날짜 형식이 잘못되었습니다 (yyyyMMdd) : " + date);
		try {
			return LocalDate.parse(d, DateTimeFormatter.BASIC_ISO_DATE).toString();
		} catch (Exception e) {
			throw new HdelBusinessException("없는 날짜입니다 (yyyyMMdd) : " + date);
		}
	}
}
