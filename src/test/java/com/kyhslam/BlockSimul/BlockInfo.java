package com.kyhslam.BlockSimul;

import java.util.List;

/**
 * dyna.plmetc.subae.model.BlockInfo
 */
public class BlockInfo {
	private String ouid;
	private long lOuid;
	private String blockNo;
	private String blockName;
	private List<PickInfo> pickList;
	private boolean isFloorPart;

	public String getOuid() { return ouid; }
	public void setOuid(String ouid) { this.ouid = ouid; }
	public long getlOuid() { return lOuid; }
	public void setlOuid(long lOuid) { this.lOuid = lOuid; }
	public String getBlockNo() { return blockNo; }
	public void setBlockNo(String blockNo) { this.blockNo = blockNo; }
	public String getBlockName() { return blockName; }
	public void setBlockName(String blockName) { this.blockName = blockName; }
	public List<PickInfo> getPickList() { return pickList; }
	public void setPickList(List<PickInfo> pickList) { this.pickList = pickList; }
	public boolean isFloorPart() { return isFloorPart; }
	public void setFloorPart(boolean isFloorPart) { this.isFloorPart = isFloorPart; }

	/** dyna.plmetc.subae.model.PickInfo */
	public static class PickInfo {
		private String pick;
		private String qty;
		private String cmt;
		private String color;

		public String getPick() { return pick; }
		public void setPick(String pick) { this.pick = pick; }
		public String getQty() { return qty; }
		public void setQty(String qty) { this.qty = qty; }
		public String getCmt() { return cmt; }
		public void setCmt(String cmt) { this.cmt = cmt; }
		public String getColor() { return color; }
		public void setColor(String color) { this.color = color; }
	}
}
