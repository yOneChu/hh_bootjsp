package com.kyhslam.bomCal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.subae.model.ProductInfo 의 조회 전용 버전.
 *
 * 원본 readyProduct4Calculate 는 제품이 없으면 등록(dos.add), RLS 면 WIP 생성(makeWipObject), 공사정보-제품 연결(link) 을 한다.
 * 여기서는 DB 를 변경하지 않으므로 현재 wip 제품을 찾기만 하고, 원본에서 수행될 작업은 notes 로 남긴다.
 *   - 제품이 없으면 : 원본은 새로 등록 → 계산완료 블록옵션 없음 (모든 옵션 계산)
 *   - 제품이 RLS 면 : 원본은 WIP 생성 (옵션 값은 복사되므로 현재 값으로 판단)
 */
public class BomProductInfo {

	private final BomContext ctx;
	private final BomDao dao;
	private final String elvOuid;
	private final String elvNumber;

	private String productOuid;
	private final HashMap<String, Boolean> calculatedBlockOptList = new HashMap<String, Boolean>();
	private ArrayList<String> blockOptList4Calc;
	private final List<String> notes = new ArrayList<String>();

	public BomProductInfo(BomContext ctx, BomDao dao, String elvOuid, String elvNumber) {
		this.ctx = ctx;
		this.dao = dao;
		this.elvOuid = elvOuid;
		this.elvNumber = elvNumber;
	}

	/** ProductInfo.readyProduct4Calculate (조회만) */
	public void readyProduct4Calculate() throws Exception {
		// 공사정보에 링크되있는 제품 가져오기
		String prodOuid = ctx.getSpecLoader().findLinkedProductOuid(elvOuid);

		if (BomUtil.isNullString(prodOuid)) {
			// 등록된 제품정보 가져오기 (wip)
			prodOuid = ctx.getSpecLoader().findWipProductOuid(elvNumber);
			if (BomUtil.isNullString(prodOuid))
				notes.add("제품 없음 : 원본은 제품을 새로 등록하고 공사정보에 연결한다. (기존 BOM 없음으로 계산)");
			else
				notes.add("공사정보에 연결된 제품 없음 : 원본은 기존 제품(" + prodOuid + ")을 공사정보에 연결한다.");
		}

		if (!BomUtil.isNullString(prodOuid)) {
			String status = ctx.getSpecLoader().getStatus(prodOuid);
			if ("RLS".equals(status))
				notes.add("제품이 승인(RLS) 상태 : 원본은 WIP 버전을 새로 만든 뒤 계산한다. (조회용은 승인 버전으로 계산)");
		}

		this.productOuid = prodOuid;
		setCalculatedBlockOpts();
	}

	/**
	 * ProductInfo.compareBlockOption : 이미 계산된 블록옵션은 제외한다.
	 * @param ignoreCalculated true 면 계산완료 여부와 관계없이 요청한 옵션을 모두 계산 (조회용 추가 기능)
	 */
	public void compareBlockOption(List<String> optList, boolean ignoreCalculated) {
		blockOptList4Calc = new ArrayList<String>();
		for (String opt : optList) {
			for (int i = 0; i < BomConsts.BLOCK_OPT_LIST.length; i++) {
				if (!BomConsts.BLOCK_OPT_LIST[i].equals(opt))
					continue;
				boolean calculated = Boolean.TRUE.equals(calculatedBlockOptList.get(BomConsts.BLOCK_OPT_FIELD_LIST[i]));
				if (ignoreCalculated || !calculated)
					blockOptList4Calc.add(opt);
				else
					notes.add("블록옵션 " + opt + " 은 이미 계산됨(" + BomConsts.BLOCK_OPT_FIELD_LIST[i] + ") : 제외");
			}
		}
	}

	/** ProductInfo.setCalculatedBlockOpts */
	private void setCalculatedBlockOpts() throws Exception {
		Map<String, String> values = productOuid == null ? new HashMap<String, String>() : dao.getProductBlockOptions(productOuid);
		for (String field : BomConsts.BLOCK_OPT_FIELD_LIST) {
			if (productOuid != null && !values.containsKey(field))
				notes.add("PRODUCT$VF 에 " + field + " 컬럼이 없음 : 미계산으로 간주");
			String divValue = values.get(field);
			calculatedBlockOptList.put(field, !(BomUtil.isNullString(divValue) || "N".equals(divValue)));
		}
	}

	public String getProductOuid() { return productOuid; }
	public ArrayList<String> getBlockOptList4Calc() { return blockOptList4Calc; }
	public Map<String, Boolean> getCalculatedBlockOptList() { return calculatedBlockOptList; }
	public List<String> getNotes() { return notes; }
}
