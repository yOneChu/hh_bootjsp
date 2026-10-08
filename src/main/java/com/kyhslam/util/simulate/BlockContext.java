package com.kyhslam.util.simulate;

import java.util.HashMap;
import java.util.Map;

/**
 * 호기 1건 시뮬레이션 동안 공유하는 실행 환경 (DB, PID 캐시, 영업사양 로더)
 */
public class BlockContext {

	private final BlockDb db;
	/** 최신 버전 PID */
	private final BlockPidRepository pidRepository;
	/** 테스트 버전(-1) 우선 PID (테스트를 하나도 고르지 않으면 null) */
	private final BlockPidRepository testPidRepository;
	/** 입력 블럭의 PICK/PID 계산을 테스트 버전으로 */
	private final boolean testBlockPid;
	/** 입력 블럭의 EL_P+블럭번호 PID 를 테스트 버전으로 */
	private final boolean testElpPid;
	/** 지금 계산에서 쓰는 PID 저장소 */
	private BlockPidRepository activePidRepository;
	private final BlockSpecLoader specLoader;
	/** variant_errorlog 에 오류 저장 여부 (원본은 항상 저장, 기본 false : 콘솔 출력만) */
	private final boolean saveErrorLog;
	/** 공사정보 ouid → "name@필드" 값 (원본의 dos.get(ouid) = elvData) */
	private final Map<String, Map<String, String>> codeNameCache = new HashMap<String, Map<String, String>>();
	/** Java 메서드 PID 의 DB 조회 결과 (층마다 같은 값을 다시 조회하지 않도록 호기 1건 계산 동안 재사용) */
	private final Map<String, Object> javaMethodCache = new HashMap<String, Object>();

	public interface Loader<T> {
		T load() throws Exception;
	}

	public BlockContext(BlockDb db, BlockPidRepository pidRepository, boolean saveErrorLog) {
		this(db, pidRepository, null, false, false, saveErrorLog);
	}

	public BlockContext(BlockDb db, BlockPidRepository pidRepository, BlockPidRepository testPidRepository,
						boolean testBlockPid, boolean testElpPid, boolean saveErrorLog) {
		this.db = db;
		this.pidRepository = pidRepository;
		this.testPidRepository = testPidRepository;
		this.testBlockPid = testBlockPid && testPidRepository != null;
		this.testElpPid = testElpPid && testPidRepository != null;
		this.activePidRepository = pidRepository;
		this.specLoader = new BlockSpecLoader(db);
		this.saveErrorLog = saveErrorLog;
	}

	public BlockDb getDb() { return db; }
	public BlockPidRepository getPidRepository() { return activePidRepository; }
	public BlockPidRepository getLatestPidRepository() { return pidRepository; }
	public boolean isTestBlockPid() { return testBlockPid; }
	public boolean isTestElpPid() { return testElpPid; }

	/** true : 테스트 버전 우선 저장소 / false : 최신 버전 저장소 (그 PID 가 호출하는 하위 PID 도 같은 저장소를 쓴다) */
	public void useTestPid(boolean test) {
		this.activePidRepository = test && testPidRepository != null ? testPidRepository : pidRepository;
	}
	public BlockSpecLoader getSpecLoader() { return specLoader; }
	public boolean isSaveErrorLog() { return saveErrorLog; }

	/** dos.get(ouid) 의 "name@필드" 값 */
	public Map<String, String> getCodeNames(String ouid) throws Exception {
		if (!codeNameCache.containsKey(ouid)) {
			BlockSpecLoader.SpecObject obj = specLoader.load(ouid);
			codeNameCache.put(ouid, obj == null ? null : obj.codeNameMap);
		}
		return codeNameCache.get(ouid);
	}

	/** key 로 한 번만 조회하고 이후에는 저장된 값을 반환한다. 조회 중 예외가 나면 저장하지 않으므로 다음 호출에서 다시 조회한다. */
	@SuppressWarnings("unchecked")
	public <T> T cached(String key, Loader<T> loader) throws Exception {
		if (javaMethodCache.containsKey(key))
			return (T) javaMethodCache.get(key);
		T value = loader.load();
		javaMethodCache.put(key, value);
		return value;
	}

	/** StringUtil.isPidPattern */
	public boolean isPidPattern(String value) throws Exception {
		if (value == null)
			return false;
		return value.matches("^[A-Z]{1,2}[0-9]{2,4}[_A-Z0-9]{0,8}") || activePidRepository.hasPid(db, value);
	}
}
