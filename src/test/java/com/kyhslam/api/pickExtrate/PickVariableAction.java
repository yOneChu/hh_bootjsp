package com.kyhslam.api.pickExtrate;

import com.kyhslam.pidSimul.PidDb;
import com.kyhslam.pidSimul.PidExceptions.PidNotFoundException;
import com.kyhslam.pidSimul.PidSpecLoader;
import com.kyhslam.pidSimul.PidVariant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.subae.model.VariableAction 의 독립 버전.
 * pickMap / separateInfo 에는 수량만 사용되므로 수량(QTY) 계산만 구현한다. (주석, 도장 PID 는 실행하지 않음)
 */
public class PickVariableAction {

	public final static String PID_NOT_FOUND = "PID_NOT_FOUND";
	public final static String ERROR = "ERROR";

	private final PidVariant variant;
	private final PickDao dao;

	public PickVariableAction(PidDb db, PidSpecLoader loader, PickDao dao, HashMap dataInfoMap, List<Map> floorMasterList) {
		// 엘리베이터 or 층별 사양 정보 입력
		this.variant = new PidVariant(db, loader, dataInfoMap, floorMasterList);
		this.variant.setSaveErrorLog(Boolean.getBoolean("pid.errorlog"));
		this.dao = dao;
	}

	/** VariableAction.get1LevelVariable 의 수량(variable[1]) 부분 */
	public String get1LevelQty(Map partInfo, String qtyPid) throws Exception {
		String qty = "";

		// 수량 PID 호출
		if (qtyPid != null && !qtyPid.isEmpty()) {
			if (dao.isPidPattern(qtyPid)) {
				qty = calcPID(qtyPid, partInfo, "QTY");
			} else {
				qty = qtyPid;
			}

			if (qty == null || qty.isEmpty())
				qty = qtyPid;
		}
		return qty;
	}

	/** VariableAction.calcPID */
	private String calcPID(String PID, Map partInfo, String returnKey) {
		String result = "";

		try {
			HashMap resultMap = variant.calcVariantPID(PID, partInfo, 1);
			result = (String) resultMap.get(returnKey);
		} catch (PidNotFoundException e) {
			System.err.println(e.getMessage());
			variant.saveErrorLog("0", e.getClass().getSimpleName() + "-" + PID, e.getMessage());
			result = PID_NOT_FOUND;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			result = ERROR;
		}

		return result;
	}
}
