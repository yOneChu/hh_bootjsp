package com.kyhslam.api.pickExtrate;

import com.kyhslam.pidSimul.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.subae.model.EL_P 의 독립 버전 (영업사양 EL_P% PID 실행 결과를 dataMap 에 추가)
 */
public class PickElP {

	private final PidDb db;
	private final PidSpecLoader loader;
	private final List<Map<String, Object>> elv_pidList;
	private final List<Map<String, Object>> floor_pidList;

	/** EL_P.set_EL_P_pidList 포함 */
	public PickElP(PidDb db, PidSpecLoader loader, PickDao dao) throws Exception {
		this.db = db;
		this.loader = loader;
		this.elv_pidList = dao.getEL_PList(false);
		this.floor_pidList = dao.getEL_PList(true);
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
