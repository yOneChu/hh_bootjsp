package com.kyhslam.util.simulate;

import java.util.HashMap;
import java.util.Map;

/**
 * 호기 1건 시뮬레이션 동안 공유하는 실행 환경 (DB, PID 캐시, 영업사양 로더)
 */
public class BlockContext {

	private final BlockDb db;
	private final BlockPidRepository pidRepository;
	private final BlockSpecLoader specLoader;
	/** variant_errorlog 에 오류 저장 여부 (원본은 항상 저장, 기본 false : 콘솔 출력만) */
	private final boolean saveErrorLog;
	/** 공사정보 ouid → "name@필드" 값 (원본의 dos.get(ouid) = elvData) */
	private final Map<String, Map<String, String>> codeNameCache = new HashMap<String, Map<String, String>>();

	public BlockContext(BlockDb db, BlockPidRepository pidRepository, boolean saveErrorLog) {
		this.db = db;
		this.pidRepository = pidRepository;
		this.specLoader = new BlockSpecLoader(db);
		this.saveErrorLog = saveErrorLog;
	}

	public BlockDb getDb() { return db; }
	public BlockPidRepository getPidRepository() { return pidRepository; }
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

	/** StringUtil.isPidPattern */
	public boolean isPidPattern(String value) throws Exception {
		if (value == null)
			return false;
		return value.matches("^[A-Z]{1,2}[0-9]{2,4}[_A-Z0-9]{0,8}") || pidRepository.hasPid(db, value);
	}
}
