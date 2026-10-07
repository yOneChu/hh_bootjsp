package com.kyhslam.util.simulate;

import com.kyhslam.util.simulate.BlockExceptions.InputValueUnvalidException;

import java.util.ArrayList;

/**
 * dyna.plmetc.floor.FloorInfoHandler 중 PID 함수에서 쓰는 층표기/층고 전개 부분
 */
public class BlockFloorInfoHandler {

	/** 층고 전개 (예: 3000*2,3500 → 3000,3000,3500) */
	public ArrayList<String> get_el_efloorh_list(String EL_EFLOORH) throws InputValueUnvalidException {
		ArrayList<String> el_efloorh_list = new ArrayList<String>();

		if (BlockUtil.isNullString(EL_EFLOORH))
			return el_efloorh_list;

		String[] el_efloorhs = EL_EFLOORH.split(",");
		for (int i = 0; i < el_efloorhs.length; i++) {
			String el_efloorh = el_efloorhs[i].trim();

			if (el_efloorh.contains("*")) {
				String tempEfloorh = el_efloorh.substring(0, el_efloorh.indexOf("*"));
				int count = 0;
				try {
					count = Integer.parseInt(el_efloorh.substring(el_efloorh.indexOf("*") + 1));
				} catch (NumberFormatException e) {
					throw new InputValueUnvalidException("층고 입력값이 부적합하여 전개실패. : " + EL_EFLOORH);
				}
				for (int j = 0; j < count; j++)
					el_efloorh_list.add(tempEfloorh);
			} else {
				el_efloorh_list.add(el_efloorh);
			}
		}
		return el_efloorh_list;
	}

	/** 층표기 전개 (예: B2~3 → B2,B1,1,2,3) */
	public ArrayList<String> get_EL_AFT_List(String EL_AFT) throws Exception {
		return get_EL_AFT_List(EL_AFT, true);
	}

	public ArrayList<String> get_EL_AFT_List(String EL_AFT, boolean isoversea) throws Exception {
		ArrayList<String> el_aft_list = new ArrayList<String>();

		if (BlockUtil.isNullString(EL_AFT))
			return el_aft_list;

		// 층 전개가 되는지 확인하는 용도 : InputValueUnvalidException을 throw함
		BlockPIDJavaMethod.expandsFloor(EL_AFT, isoversea, true);

		String[] el_afts = EL_AFT.split(",");
		for (int i = 0; i < el_afts.length; i++) {
			String el_aft = el_afts[i].trim();
			el_aft = el_aft.replace("-", "~");
			if (el_aft.contains("~")) {
				String[] arrEL_AFT = el_aft.split("~");
				String front = arrEL_AFT[0];
				String rear = arrEL_AFT[1];

				String prefix = "";
				if (front.contains("B")) {
					front = front.replace("B", "-");
					rear = rear.replace("B", "-");
					prefix = "B";
				} else if (front.contains("P")) {
					front = front.replace("P", "-");
					rear = rear.replace("P", "-");
					prefix = "P";
				}

				int ift = Integer.parseInt(front);
				int ire = Integer.parseInt(rear);

				int start = ift > ire ? ire : ift;
				int end = ift > ire ? ift : ire;

				for (int j = start; j <= end; j++) {
					if (j == 0)
						continue;

					String aft = Integer.toString(j);
					if (j < 0)
						aft = aft.replace("-", prefix);

					el_aft_list.add(aft);
				}
			} else {
				el_aft_list.add(el_aft);
			}
		}

		return el_aft_list;
	}
}
