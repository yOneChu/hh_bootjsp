package com.kyhslam.util.simulate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * dyna.plmetc.subae.model.EL_P 의 독립 버전 (영업사양 EL_P% PID 실행 결과를 dataMap 에 추가)
 */
public class PickElP {

	private final PidDb db;
	private final PidSpecLoader loader;
	private final List<Map<String, Object>> elv_pidList;
	private final List<Map<String, Object>> floor_pidList;
	/** 실행할 EL_P PID (null 이면 전체 실행) */
	private Set<String> pidFilter = null;
	/** 실제 실행한 PID 수 (로그용) */
	private int executedCount = 0;

	/** EL_P.set_EL_P_pidList 포함 */
	public PickElP(PidDb db, PidSpecLoader loader, PickDao dao) throws Exception {
		this.db = db;
		this.loader = loader;
		this.elv_pidList = dao.getEL_PList(false);
		this.floor_pidList = dao.getEL_PList(true);
	}

	/** 영업사양 + 층 EL_P PID 목록 */
	public List<Map<String, Object>> getAllPidList() {
		List<Map<String, Object>> all = new ArrayList<Map<String, Object>>(elv_pidList);
		all.addAll(floor_pidList);
		return all;
	}

	public void setPidFilter(Set<String> pidFilter) {
		this.pidFilter = pidFilter;
	}

	public int getExecutedCount() {
		return executedCount;
	}

	/** EL_P.make_EL_P_Data */
	public void make_EL_P_Data(HashMap dataInfoMap, List<Map> floorMasterList, boolean isFloorSpec) {
		HashMap<String, String> resultMap = calc_EL_P(dataInfoMap, floorMasterList, isFloorSpec);
		dataInfoMap.putAll(resultMap);
	}

	/** EL_P.calc_EL_P */
	private HashMap<String, String> calc_EL_P(HashMap dataInfoMap, List<Map> floorMasterList, boolean isFloorSpec) {
		PidVariant variant = new PidVariant(db, loader, dataInfoMap, floorMasterList);
		variant.setSaveErrorLog(Boolean.getBoolean("pid.errorlog"));
		HashMap<String, String> resultMap = new HashMap<String, String>();

		List<Map<String, Object>> pidList = isFloorSpec ? floor_pidList : elv_pidList;
		if (pidList == null || pidList.size() == 0) {
			return resultMap;
		}

		for (Map<String, Object> pidMap : pidList) {
			String pid = (String) pidMap.get("PID");
			String method = (String) pidMap.get("METHOD");

			if (pidFilter != null && !pidFilter.contains(pid))
				continue;
			executedCount++;

			PidVariantMap localMap = null;

			try {
				localMap = variant.calcVariantPID(pid, null, 1);
			} catch (Exception e) {
				System.err.println(pid + "(" + method + ") : " + e.getMessage());
			}

			if (localMap != null) {
				PidVariantMap outputMap = localMap.getOUTPUTMap();
				for (Object key : outputMap.keySet()) {
					resultMap.put((String) key, PidUtil.NVL(localMap.get(key), ""));
				}
			}
		}

		return resultMap;
	}
}
