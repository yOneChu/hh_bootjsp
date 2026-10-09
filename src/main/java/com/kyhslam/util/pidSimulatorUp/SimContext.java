package com.kyhslam.util.pidSimulatorUp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 호기 1건 시뮬레이션 동안 공유하는 실행 환경 (DB, PID 캐시, 영업사양 로더)
 */
public class SimContext {

	private final SimDb db;
	private final SimPidRepository pidRepository;
	private final SimSpecLoader specLoader;
	/** variant_errorlog 에 오류 저장 여부 (원본은 항상 저장, 기본 false : 콘솔 출력만) */
	private final boolean saveErrorLog;
	/** 공사정보 ouid → "name@필드" 값 (원본의 dos.get(ouid) = elvData) */
	private final Map<String, Map<String, String>> codeNameCache = new HashMap<String, Map<String, String>>();
	/** CALL 하위 PID 의 실행 버전 지정 (PID → 버전, -1 : TEST). 없으면 원본과 같이 최신 버전 */
	private final Map<String, Integer> subPidVersions = new HashMap<String, Integer>();
	/** CALL 로 실행된 하위 PID 기록 (처음 호출된 순서) */
	private final Map<String, SubPidRun> subPidRuns = new LinkedHashMap<String, SubPidRun>();

	/** CALL 로 실행된 하위 PID 1건의 기록 */
	public static class SubPidRun {
		public String pid;
		public String name;
		/** 실제 실행한 버전 (실패시 null) */
		public Integer runVersion;
		/** 지정한 버전 (null : 최신) */
		public Integer requestedVersion;
		/** 처음 호출된 깊이 (대상 PID 가 1, 바로 아래 CALL 이 2) */
		public int depth;
		/** 호출 횟수 */
		public int callCount;
		/** 실행 실패 사유 (지정 버전 없음 등) */
		public String error;
	}

	public SimContext(SimDb db, SimPidRepository pidRepository, boolean saveErrorLog) {
		this.db = db;
		this.pidRepository = pidRepository;
		this.specLoader = new SimSpecLoader(db);
		this.saveErrorLog = saveErrorLog;
	}

	public SimDb getDb() { return db; }
	public SimPidRepository getPidRepository() { return pidRepository; }
	public SimSpecLoader getSpecLoader() { return specLoader; }
	public boolean isSaveErrorLog() { return saveErrorLog; }

	/** dos.get(ouid) 의 "name@필드" 값 */
	public Map<String, String> getCodeNames(String ouid) throws Exception {
		if (!codeNameCache.containsKey(ouid)) {
			SimSpecLoader.SpecObject obj = specLoader.load(ouid);
			codeNameCache.put(ouid, obj == null ? null : obj.codeNameMap);
		}
		return codeNameCache.get(ouid);
	}

	/** true 인 동안만 하위 PID 버전 지정을 적용하고 기록한다 (대상 PID 실행 구간. 전처리 PID 는 제외) */
	private boolean subPidTracking = false;

	/**
	 * 대상 PID 실행 시작 : 이후 CALL 하위 PID 는 지정 버전으로 실행하고 기록한다.
	 * @param versions 하위 PID 버전 지정 (null 이면 지정 없음 = 전부 최신)
	 */
	public void startSubPidTracking(Map<String, Integer> versions) {
		setSubPidVersions(versions);
		subPidTracking = true;
	}

	/** CALL 하위 PID 버전 지정 (null 이면 지정 없음 = 전부 최신) */
	public void setSubPidVersions(Map<String, Integer> versions) {
		subPidVersions.clear();
		if (versions != null) {
			for (Map.Entry<String, Integer> e : versions.entrySet()) {
				if (e.getKey() != null && e.getValue() != null)
					subPidVersions.put(e.getKey().trim(), e.getValue());
			}
		}
	}

	/** CALL 하위 PID 에 지정한 버전 (없으면 null : 최신) */
	public Integer getSubPidVersion(String pid) {
		return (!subPidTracking || pid == null) ? null : subPidVersions.get(pid.trim());
	}

	/** CALL 하위 PID 실행 기록 (같은 PID 는 처음 기록에 횟수만 더한다) */
	public void recordSubPid(String pid, String name, Integer runVersion, int depth, String error) {
		if (!subPidTracking)
			return;
		SubPidRun run = subPidRuns.get(pid);
		if (run == null) {
			run = new SubPidRun();
			run.pid = pid;
			run.depth = depth;
			run.requestedVersion = getSubPidVersion(pid);
			subPidRuns.put(pid, run);
		}
		run.callCount++;
		if (run.name == null)
			run.name = name;
		if (run.runVersion == null)
			run.runVersion = runVersion;
		if (run.error == null)
			run.error = error;
	}

	public Collection<SubPidRun> getSubPidRuns() { return subPidRuns.values(); }

	/** StringUtil.isPidPattern */
	public boolean isPidPattern(String value) throws Exception {
		if (value == null)
			return false;
		return value.matches("^[A-Z]{1,2}[0-9]{2,4}[_A-Z0-9]{0,8}") || pidRepository.hasPid(db, value);
	}
}
