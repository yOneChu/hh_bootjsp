package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.CalculatorException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.IllegalConditionDefinitionException;
import com.kyhslam.util.pidSimulatorUp.evalEx.Expression;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 유틸 모음 (dyna.plmetc.util.StringUtil / dyna.plmetc.bom.StringUtil / Calculator / PartCommonFun / Utils 의 필요한 부분)
 */
public class SimUtil {

	public final static String OPT_START = "[$";
	public final static String OPT_END = "$]";

	private SimUtil() {}

	// ------------------------------------------------------------------------ null / empty

	public static String NVL(Object str1, String str2) {
		if (str1 == null)
			return str2;
		return str1.toString();
	}

	/** dyna.util.Utils.isNullString */
	public static boolean isNullString(String str) {
		return str == null || str.length() == 0;
	}

	/** org.springframework.util.StringUtils.hasText */
	public static boolean hasText(String str) {
		if (str == null || str.isEmpty())
			return false;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i)))
				return true;
		}
		return false;
	}

	public static boolean isEmpty(String val) {
		return (val == null) ? true : "".equals(val.trim()) || "null".equals(val.trim());
	}

	// ------------------------------------------------------------------------ number

	/** String 형을 Int로 변환(str이 null,N,empty 일경우 0을 반환) */
	public static int parseInt(Object obj) {
		String str = (obj == null) ? null : obj.toString();
		if (str == null || str.equals("N") || str.equals(""))
			str = "0";
		str = str.trim();
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			System.out.println(e.toString());
			return 0;
		}
	}

	public static int parseInt(double dnumber) {
		return (int) dnumber;
	}

	public static int parseInt(float fnumber) {
		return (int) fnumber;
	}

	/** String 형을 Double로 변환(str이 null,N,empty 일경우 0을 반환) */
	public static double parseDouble(Object obj) {
		String str = (obj == null) ? null : obj.toString();
		if (str == null || str.equals("N") || str.equals(""))
			str = "0";
		str = str.trim();
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			System.out.println(e.toString());
			return 0;
		}
	}

	/** StringUtil.parseBigDecimal(String) : 실패시 0 */
	public static BigDecimal parseBigDecimal(String val) {
		try {
			return new BigDecimal(val.trim());
		} catch (Exception e) {
			return BigDecimal.valueOf(0);
		}
	}

	public static boolean isNumber(String val) {
		try {
			new BigDecimal(val.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static double getRealQty(String qty, String work_qty) {
		double realQty = 0;
		if (!isEmpty(work_qty)) {
			if (isNumber(work_qty))
				realQty = parseDouble(work_qty);
		} else {
			if (isNumber(qty))
				realQty = parseDouble(qty);
		}
		return realQty;
	}

	/** FormulaExpression "ceiling(num,pos)" */
	public static double ceil(double num, int pos) {
		return scale(num, pos, RoundingMode.CEILING);
	}

	public static int ceil(float number) {
		return (int) Math.ceil(number);
	}

	public static int ceil(double number) {
		return (int) Math.ceil(number);
	}

	/** FormulaExpression "floor(num,pos)" */
	public static double floor(double num, int pos) {
		return scale(num, pos, RoundingMode.FLOOR);
	}

	public static int floor(float number) {
		return (int) Math.floor(number);
	}

	public static int floor(double number) {
		return (int) Math.floor(number);
	}

	private static double scale(double num, int pos, RoundingMode mode) {
		int precision = pos - 1;
		if (precision < 0)
			precision++;
		return parseDouble(new BigDecimal(Double.toString(num)).setScale(precision, mode).toPlainString());
	}

	/** 정수이면 소수점 제거 (1.0 → 1, 1.1 → 1.1) */
	public static String convertFloat(double qty) {
		if (qty == (int) qty)
			return String.valueOf((int) qty);
		return String.valueOf(qty);
	}

	public static String convertNumber(double qty) {
		return String.valueOf(ceil(qty));
	}

	public static String deciTohex(String deci) {
		return Long.toHexString(Long.parseLong(deci));
	}

	/** org.apache.commons.lang3.math.NumberUtils.isCreatable 근사 구현 */
	public static boolean isCreatable(String str) {
		if (str == null || str.isEmpty())
			return false;
		return str.matches("^[-+]?(0[xX]|#)[0-9a-fA-F]+$")
				|| str.matches("^[-+]?(\\d+\\.?\\d*|\\.\\d+)([eE][-+]?\\d+)?[fFdD]?$")
				|| str.matches("^[-+]?\\d+[lL]$");
	}

	/**
	 * StringUtil.getDosfldTitle : 원본은 DOSFLD 메타데이터에서 필드 제목을 조회하지만,
	 * 메타데이터를 조회하지 않으므로 필드 코드를 그대로 반환한다. (오류 메시지 표시용)
	 */
	public static String getDosfldTitle(String fieldName) {
		return NVL(fieldName, "");
	}

	// ------------------------------------------------------------------------ comment

	/** 한줄 띄어쓰기가 있지 않으면 입력 */
	public static String addLine(String cmt) {
		if (cmt == null || cmt.equals(""))
			return cmt;
		if (!cmt.endsWith("\n"))
			cmt = cmt + "\n";
		return cmt;
	}

	/** 중복 cmt 방지, 한줄띄어쓰기 입력 */
	public static String addLine(String text, String cmt) {
		if (cmt == null || cmt.equals(""))
			return cmt;
		if (!text.contains(cmt)) {
			if (!cmt.endsWith("\n"))
				cmt += "\n";
		} else {
			cmt = "";
		}
		return cmt;
	}

	/** 마지막 한줄 띄어쓰기가 있으면 삭제 */
	public static String removeLastLine(String cmt) {
		if (cmt == null || cmt.equals(""))
			return cmt;
		if (cmt.endsWith("\n"))
			cmt = cmt.substring(0, cmt.length() - 1);
		return cmt;
	}

	/** 주석중 중복되는 내용이 있는지 검사 */
	public static boolean isDupCmt(String cmt_block, String cmt_block1) {
		return cmt_block.indexOf(cmt_block1) != -1;
	}

	// ------------------------------------------------------------------------ PID

	/** Calculator.parse : [$ ... $] 수식을 계산하여 치환 */
	public static String calculate(String input) throws Exception {
		if (input != null) {
			try {
				while (input.contains(OPT_START) && input.contains(OPT_END)) {
					int start = input.indexOf(OPT_START);
					int end = input.indexOf(OPT_END);

					String orgformula = input.substring(start + OPT_START.length(), end);
					String convertedformula = orgformula.replaceAll("N", "0"); // N이면 0으로 간주한다.
					String formula_result = new Expression(convertedformula.replaceAll(" ", "")).eval().toPlainString();

					input = input.replace(OPT_START + orgformula + OPT_END, formula_result);
				}
			} catch (RuntimeException e) {
				throw new CalculatorException(input, e);
			}
		}
		return input;
	}

	/** PartCommonFun.compareData : 조건(condition)과 영업데이터(salesData)를 비교 */
	public static boolean compareData(String condition, String salesData) {
		if (condition == null || "".equals(condition))
			return true;

		if (condition.equals("ISNUMBER")) {
			try {
				Double.parseDouble(salesData);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		if (condition.equals("BLANK"))
			return salesData.equals("");
		else if (condition.equals("!BLANK"))
			return !salesData.equals("");

		if (salesData.equals("")) // 영업데이터가 없다면 N으로 간주한다.
			salesData = "N";

		try {
			switch (condition.charAt(0)) {
				case ',': // OR operation
					return SimBomComputeOps.initOrOp(salesData, condition.substring(1));
				case '!':
					return SimBomComputeOps.initNotOp(salesData, condition.substring(1));
				case '<':
				case '>':
					if (salesData.equals("N"))
						return false;
					return SimBomComputeOps.initRelationalOp(salesData, condition);
				case '?': /* 중간에 don't care로 나오는 String */
					return SimBomComputeOps.initPartialStOp(salesData, condition.substring(1));
				case ' ':
					return true;
				default: // 기본 동일 정보
					return SimBomComputeOps.initSimpleOp(salesData, condition);
			}
		} catch (RuntimeException e) {
			throw new IllegalConditionDefinitionException(condition);
		}
	}
}
