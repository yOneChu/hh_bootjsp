package com.kyhslam.bomCal;

import com.kyhslam.bomCal.BomExceptions.PidNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.subae.model.VariableAction 의 독립 버전
 */
public class BomVariableAction {
	private final BomContext ctx;
	private final BomVariant variant;

	public BomVariableAction(BomContext ctx, HashMap dataInfoMap, List<Map> floorMasterList) {
		// 엘리베이터 or 층별 사양 정보 입력
		this.ctx = ctx;
		this.variant = new BomVariant(ctx, dataInfoMap, floorMasterList);
	}

	public String[] get1LevelVariable(Map partInfo, String qtyPid, String cmtPids, String colorPid) throws Exception {
		String qty = "";
		String cmt = "";
		String color = "";

		// 수량 PID 호출
		if (!BomUtil.isNullString(qtyPid)) {
			if (ctx.isPidPattern(qtyPid))
				qty = calcPID(qtyPid, partInfo, "QTY");
			else
				qty = qtyPid;

			if (BomUtil.isNullString(qty))
				qty = qtyPid;
		}

		// 주석 PID 호출
		if(!BomUtil.isNullString(cmtPids)) {
			ArrayList cmtArr = getMethods(cmtPids);
			String cmt_block_tmp = "";
			for(int i = 0; i < cmtArr.size(); i++) {
				String cmt_pid = (String) cmtArr.get(i);
				if (ctx.isPidPattern(cmt_pid)) {
					cmt_block_tmp = calcPID(cmt_pid, partInfo, "CMT");
					cmt_block_tmp = BomUtil.addLine(cmt_block_tmp);
				} else {
					cmt_block_tmp = BomUtil.addLine(cmt_pid);
				}

				if(cmt_block_tmp == null || cmt_block_tmp.trim().equals("null"))
					cmt_block_tmp = "";

				cmt += cmt_block_tmp;
			}
		}

		// 도장 PID 호출
		if (!BomUtil.isNullString(colorPid)) {
			if (ctx.isPidPattern(colorPid))
				color = calcPID(colorPid, partInfo, "COLOR");
			else
				color = colorPid;

			if (BomUtil.isNullString(color))
				color = colorPid;
		}
		String[] variable = new String[5];
		variable[0] = BomUtil.removeLastLine(cmt);
		variable[1] = qty;
		variable[2] = color;
		variable[3] = (String)partInfo.get("PARTNO");
		variable[4] = (String)partInfo.get("B_NO");

		return variable;
	}

	public HashMap<String, String> getOtherLevelVariable(String assoOuid, HashMap partInfo, String partQty, String partCmt, String partColor, boolean isCalculated) throws Exception {
		String qty = "";
		String cmt = "";
		String color = "";
		// 1level이 이미 계산되었으면 계산하지 않는다.
		if (!isCalculated) {
			if (ctx.isPidPattern(partQty))
				qty = calcPID(partQty, partInfo, "QTY"); // 수량PID 호출
		}

		if(!BomUtil.isNullString(partCmt)) {
			ArrayList cmtList = getMethods(partCmt);
			String cmt1 = "";
			for(int i = 0; i < cmtList.size(); i++) {
				String tmpCmt = (String) cmtList.get(i);

				if (ctx.isPidPattern(tmpCmt)) {
					cmt1 = calcPID(tmpCmt, partInfo, "CMT"); // 주석PID 호출
					cmt1 = BomUtil.addLine(cmt1);
				} else {
					cmt1 = tmpCmt;
				}

				if(!BomUtil.isNullString(cmt1)) {
					if(i == 0)
						cmt = cmt1;
					else
						cmt = cmt + ", " + cmt1;
				}
			}
		}

		if(!BomUtil.isNullString(partColor)) {
			if (ctx.isPidPattern(partColor))
				color = calcPID(partColor, partInfo, "COLOR"); // 도장PID 호출
		}

		HashMap<String, String> variable = new HashMap<String, String>();
		variable.put("assoOuid", assoOuid);
		variable.put("cmt",  BomUtil.removeLastLine(cmt));
		variable.put("qty", qty);
		variable.put("color", color);
		return variable;
	}

	private String calcPID(String PID, Map partInfo, String returnKey) {
		String result = "";

		try {
			if(PID == null)
				throw new Exception("PID is null");

			HashMap resultMap = variant.calcVariantPID(PID, partInfo);
			result = (String) resultMap.get(returnKey);
		} catch(PidNotFoundException e){
			System.err.println(e.getMessage());
			variant.saveErrorLog("0", e.getClass().getSimpleName()+"-"+PID, e.getMessage());
			result = BomConsts.PID_NOT_FOUND;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			result = BomConsts.ERROR;
		}

		return result;
	}

	/** 문자열(;구분)에서 함수명의 배열을 얻는다. */
	private ArrayList getMethods(String cmt_pid_str) {
		boolean isEnd = false;
		ArrayList cmt_arrList = new ArrayList();
		if(cmt_pid_str == null || cmt_pid_str.equals(""))
			return cmt_arrList;

		int nStart = 0;
		int nEnd = 0;
		while(!isEnd) {
			nEnd = cmt_pid_str.indexOf(";", nStart);
			if(nEnd == -1) {
				String cmt_pid = cmt_pid_str.substring(nStart).trim();
				if(!cmt_pid.equals(""))
					cmt_arrList.add(cmt_pid);
				isEnd = true;
			} else {
				String cmt_pid = cmt_pid_str.substring(nStart, nEnd).trim();
				if(!cmt_pid.equals(";") && !cmt_pid.equals(""))
					cmt_arrList.add(cmt_pid);
				nStart = nEnd + 1;
			}
		}

		return cmt_arrList;
	}
}
