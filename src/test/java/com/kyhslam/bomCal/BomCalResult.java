package com.kyhslam.bomCal;

import java.util.ArrayList;
import java.util.List;

/**
 * BOM 계산 결과 (SubaeManager.bomCalculate 에서 DB 에 저장되는 내용을 저장 대신 담는다)
 *   - level1List : link1LevelPart → PARTOFEBOM insert 대상 (insert1LevelPartOfEBom)
 *   - level2List : makeEBomStructure → VARIABLEPART_NEW insert 대상 (insertVariablePartRow)
 */
public class BomCalResult {

	/** 원본에서 수행될 동작 */
	public static final String ACTION_INSERT = "INSERT";
	/** 이미 수배되어 있어 원본에서 건너뛰는 항목 */
	public static final String ACTION_SKIP_EXIST = "SKIP(기수배)";
	/** 공사주석/수량/도장이 모두 없어 원본에서 저장하지 않는 항목 (2레벨) */
	public static final String ACTION_SKIP_EMPTY = "SKIP(값없음)";

	private String hogi;
	private String elvOuid;
	private String elvStatus;
	private String productOuid;
	private int floorCount;
	private List<String> inputOptList = new ArrayList<String>();
	private List<String> blockOptList4Calc = new ArrayList<String>();
	private List<String> notes = new ArrayList<String>();
	private List<String> pidErrors = new ArrayList<String>();
	/** 원본에서는 Exception 으로 BOM 계산이 실패하는 오류 (ex. CAL_BOM_SUPPLIER_DESIGN 공급구분 누락) */
	private String calcError;
	private List<Level1Row> level1List = new ArrayList<Level1Row>();
	private List<Level2Row> level2List = new ArrayList<Level2Row>();

	/** 1레벨 (PARTOFEBOM) */
	public static class Level1Row {
		public String action;
		public String seq;
		public String partOuid;
		public String partNo;
		/** 자재명 (NORMALPART$VF.MD$DESC) */
		public String partName;
		public String blockNo;
		public String glCode;
		public String spec;
		public String partSize;
		public String pick;
		public String qty;
		public String cmt;
		public String color;
		public String mBom;
	}

	/** 2레벨 이하 공사수량 (VARIABLEPART_NEW) */
	public static class Level2Row {
		public String action;
		/** PARTOFPART$AC.SF$OUID (10진수) */
		public String assoOuid;
		public String parentPartNo;
		public String partNo;
		/** 자재명 (NORMALPART$VF.MD$DESC) */
		public String partName;
		public String blockNo;
		public String qty;
		public String cmt;
		public String color;
	}

	public String getHogi() { return hogi; }
	public void setHogi(String hogi) { this.hogi = hogi; }
	public String getElvOuid() { return elvOuid; }
	public void setElvOuid(String elvOuid) { this.elvOuid = elvOuid; }
	public String getElvStatus() { return elvStatus; }
	public void setElvStatus(String elvStatus) { this.elvStatus = elvStatus; }
	public String getProductOuid() { return productOuid; }
	public void setProductOuid(String productOuid) { this.productOuid = productOuid; }
	public int getFloorCount() { return floorCount; }
	public void setFloorCount(int floorCount) { this.floorCount = floorCount; }
	public List<String> getInputOptList() { return inputOptList; }
	public void setInputOptList(List<String> inputOptList) { this.inputOptList = inputOptList; }
	public List<String> getBlockOptList4Calc() { return blockOptList4Calc; }
	public void setBlockOptList4Calc(List<String> blockOptList4Calc) { this.blockOptList4Calc = blockOptList4Calc; }
	public List<String> getNotes() { return notes; }
	public List<String> getPidErrors() { return pidErrors; }
	public void setPidErrors(List<String> pidErrors) { this.pidErrors = pidErrors; }
	public String getCalcError() { return calcError; }
	public void setCalcError(String calcError) { this.calcError = calcError; }
	public List<Level1Row> getLevel1List() { return level1List; }
	public List<Level2Row> getLevel2List() { return level2List; }
}
