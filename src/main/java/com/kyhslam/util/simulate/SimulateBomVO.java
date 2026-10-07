package com.kyhslam.util.simulate;

import java.math.BigDecimal;

/**
 * dyna.plmetc.spring.vo.bom.SimulateBomVO 의 독립 버전 (필드/getter 동일)
 */
public class SimulateBomVO {
	String productNo;
	String blockNo;

	String bomPartOuid;
	String bomPartNo;
	BigDecimal bomQty;
	String bomCmt;
	String bomGlcode;
	String bomSpec;
	String bomPartSize;
	boolean bomUCheck;

	String simulatePartOuid;
	String simulatePartNo;
	BigDecimal simulateQty;
	String simulateCmt;
	String simulateGlcode;
	String simulateSpec;
	String SimulatePartSize;
	String simulateVariableData;

	public boolean getBomUCheck() { return bomUCheck; }
	public void setBomUCheck(boolean bomUCheck) { this.bomUCheck = bomUCheck; }
	public String getBomGlcode() { return bomGlcode; }
	public void setBomGlcode(String bomGlcode) { this.bomGlcode = bomGlcode; }
	public String getBomSpec() { return bomSpec; }
	public void setBomSpec(String bomSpec) { this.bomSpec = bomSpec; }
	public String getBomPartSize() { return bomPartSize; }
	public void setBomPartSize(String bomPartSize) { this.bomPartSize = bomPartSize; }
	public String getSimulateGlcode() { return simulateGlcode; }
	public void setSimulateGlcode(String simulateGlcode) { this.simulateGlcode = simulateGlcode; }
	public String getSimulateSpec() { return simulateSpec; }
	public void setSimulateSpec(String simulateSpec) { this.simulateSpec = simulateSpec; }
	public String getSimulatePartSize() { return SimulatePartSize; }
	public void setSimulatePartSize(String simulatePartSize) { SimulatePartSize = simulatePartSize; }
	public String getBomPartOuid() { return bomPartOuid; }
	public void setBomPartOuid(String bomPartOuid) { this.bomPartOuid = bomPartOuid; }
	public String getSimulatePartOuid() { return simulatePartOuid; }
	public void setSimulatePartOuid(String simulatePartOuid) { this.simulatePartOuid = simulatePartOuid; }
	public String getProductNo() { return productNo; }
	public void setProductNo(String productNo) { this.productNo = productNo; }
	public String getBlockNo() { return blockNo; }
	public void setBlockNo(String blockNo) { this.blockNo = blockNo; }
	public String getBomPartNo() { return bomPartNo; }
	public void setBomPartNo(String bomPartNo) { this.bomPartNo = bomPartNo; }
	public BigDecimal getBomQty() { return bomQty; }
	public void setBomQty(BigDecimal bomQty) { this.bomQty = bomQty; }
	public String getBomCmt() { return bomCmt; }
	public void setBomCmt(String bomCmt) { this.bomCmt = BlockUtil.hasText(bomCmt) ? bomCmt : null; }
	public String getSimulatePartNo() { return simulatePartNo; }
	public void setSimulatePartNo(String simulatePartNo) { this.simulatePartNo = simulatePartNo; }
	public BigDecimal getSimulateQty() { return simulateQty; }
	public void setSimulateQty(BigDecimal simulateQty) { this.simulateQty = simulateQty; }
	public String getSimulateCmt() { return BlockUtil.hasText(simulateCmt) ? simulateCmt : null; }
	public void setSimulateCmt(String simulateCmt) { this.simulateCmt = simulateCmt; }
	public String getSimulateVariableData() { return simulateVariableData; }
	public void setSimulateVariableData(String simulateVariableData) { this.simulateVariableData = simulateVariableData; }

	public boolean getQtyEquals() {
		if (bomQty == null && simulateQty == null)
			return true;
		return bomQty != null && simulateQty != null && bomQty.compareTo(simulateQty) == 0;
	}

	public boolean getCmtEquals() {
		if (!BlockUtil.hasText(bomCmt) && !BlockUtil.hasText(simulateCmt))
			return true;
		return (bomCmt != null) && bomCmt.equals(simulateCmt);
	}

	public String getChangeType() {
		if (simulatePartNo == null) {
			return "DELETE";
		} else if (bomPartNo == null) {
			return "ADD";
		} else {
			if (simulatePartNo.equals(bomPartNo)) {
				if (getQtyEquals() && getCmtEquals())
					return "SAME";
				else
					return "UPDATE";
			} else {
				return "REPLACE";
			}
		}
	}

	public String getBlockNo4Order() {
		return blockNo.substring(1);
	}

	@Override
	public String toString() {
		return "SimulateBomVO{productNo=" + productNo + ", blockNo=" + blockNo + ", changeType=" + getChangeType()
				+ ", bomPartNo=" + bomPartNo + ", bomQty=" + bomQty + ", bomCmt=" + bomCmt
				+ ", simulatePartNo=" + simulatePartNo + ", simulateQty=" + simulateQty + ", simulateCmt=" + getSimulateCmt() + "}";
	}
}
