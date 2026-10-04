package com.kyhslam.bomCal;

import java.util.*;

/**
 * 호기 1건 BOM 계산 동안 공유하는 실행 환경 (DB, PID 캐시, 영업사양 로더)
 */
public class BomContext {

	private final BomDb db;
	private final BomPidRepository pidRepository;
	private final BomSpecLoader specLoader;
	/** 공사정보 ouid → "name@필드" 값 (원본의 dos.get(ouid) = elvData) */
	private final Map<String, Map<String, String>> codeNameCache = new HashMap<String, Map<String, String>>();
	/** PID 실행 오류 (원본은 variant_errorlog 에 저장. 여기서는 수집만 한다) */
	private final Set<String> pidErrors = new LinkedHashSet<String>();

	public BomContext(BomDb db, BomPidRepository pidRepository) {
		this.db = db;
		this.pidRepository = pidRepository;
		this.specLoader = new BomSpecLoader(db);
	}

	public BomDb getDb() { return db; }
	public BomPidRepository getPidRepository() { return pidRepository; }
	public BomSpecLoader getSpecLoader() { return specLoader; }

	public void addPidError(String msg) { pidErrors.add(msg); }
	public List<String> getPidErrors() { return new ArrayList<String>(pidErrors); }

	/** dos.get(ouid) 의 "name@필드" 값 */
	public Map<String, String> getCodeNames(String ouid) throws Exception {
		if (!codeNameCache.containsKey(ouid)) {
			BomSpecLoader.SpecObject obj = specLoader.load(ouid);
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
