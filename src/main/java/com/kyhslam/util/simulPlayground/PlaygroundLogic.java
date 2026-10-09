package com.kyhslam.util.simulPlayground;

import com.kyhslam.util.pidSimulatorUp.SimConsts;
import com.kyhslam.util.pidSimulatorUp.SimVariantMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 편집 화면의 PID 로직 (DB 에서 불러온 원본) + 수정본 → 엔진 로직 데이터 변환
 */
public class PlaygroundLogic {

	public String pid;
	public String name;
	/** DB / JAVA (JAVA 방식은 라인이 없어 편집 불가) */
	public String method;
	/** 불러온 버전 (-1 : TEST) */
	public int version;
	/** 최신 버전 (없으면 null) */
	public Integer latestVersion;
	/** 층별 PID 여부 (Y/N, 최신 버전 기준) */
	public String isFloorSpec;
	/** 불러온 버전의 등록일시 */
	public String regDate;
	/** variant_h.HOUID */
	public String houid;
	/** 라인 (DOUID 순) */
	public List<PlaygroundRow> rows = new ArrayList<PlaygroundRow>();
	/** 선택 가능한 버전 목록 {version, latest, regDate, isFloorSpec} */
	public List<Map<String, Object>> versions = new ArrayList<Map<String, Object>>();

	/**
	 * 수정본 라인 → 엔진 로직 데이터 (SimPidRepository.getLogic 과 같은 구조).
	 * 엔진이 행마다 실행 결과를 기록하므로 호출할 때마다 새로 만든다.
	 * @param doDebug true 면 빈 SPEC/KEY 도 자리를 유지 (화면 열 맞춤), false 면 값이 있는 칸만
	 */
	public static SimVariantMap toLogicMap(List<PlaygroundRow> rows, boolean doDebug) {
		int maxSpecIdx = 0;
		int maxResIdx = 0;
		int newSeq = 0;
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		for (PlaygroundRow r : rows) {
			ArrayList specList = new ArrayList();
			ArrayList conList = new ArrayList();
			ArrayList keyList = new ArrayList();
			ArrayList valList = new ArrayList();
			boolean isBlankLine = true;

			for (int i = 1; i <= SimConsts.MAX_SPEC_FIELD_SIZE; i++) {
				String specTmp = cell(r.spec, i - 1);
				String conTmp = cell(r.con, i - 1);
				if (doDebug || specTmp != null) {
					specList.add(specTmp);
					conList.add(conTmp);
				}
				if (specTmp != null) {
					maxSpecIdx = Math.max(maxSpecIdx, i);
					isBlankLine = false;
				}
			}

			for (int i = 1; i <= SimConsts.MAX_RES_FIELD_SIZE; i++) {
				String keyTmp = cell(r.key, i - 1);
				String valTmp = cell(r.val, i - 1);
				if (doDebug || keyTmp != null) {
					keyList.add(keyTmp);
					valList.add(valTmp);
				}
				if (keyTmp != null) {
					maxResIdx = Math.max(maxResIdx, i);
					isBlankLine = false;
				}
			}

			// 새 라인은 임시 DOUID (OUTPUT 선언 키 'OUTPUT'+DOUID+칸 에 쓰인다)
			String douid = blankToNull(r.douid);
			if (douid == null)
				douid = "NEW" + (++newSeq);

			SimVariantMap rowMap = new SimVariantMap();
			rowMap.put("specList", specList);
			rowMap.put("conList", conList);
			rowMap.put("keyList", keyList);
			rowMap.put("valList", valList);
			rowMap.put("GOTO", blankToNull(r.gotoAddr));
			rowMap.put("ADDR", blankToNull(r.addr));
			rowMap.put("REMARKS", blankToNull(r.remarks));
			rowMap.put("DOUID", douid);
			rowMap.put("isBlankLine", String.valueOf(isBlankLine));
			rowMap.put("line_no", blankToNull(r.no));

			data.add(rowMap);
		}

		SimVariantMap res = new SimVariantMap();
		res.put("data", data);
		res.put("maxSpecIdx", String.valueOf(maxSpecIdx));
		res.put("maxResIdx", String.valueOf(maxResIdx));
		return res;
	}

	private static String cell(List<String> list, int idx) {
		return (list == null || idx >= list.size()) ? null : blankToNull(list.get(idx));
	}

	/** 빈 칸(공백만 있는 경우 포함)은 DB 의 NULL 과 같이 취급 */
	static String blankToNull(String s) {
		return (s == null || s.trim().isEmpty()) ? null : s;
	}
}
