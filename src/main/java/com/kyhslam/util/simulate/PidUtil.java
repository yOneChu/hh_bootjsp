package com.kyhslam.util.simulate;

import com.kyhslam.util.simulate.PidExceptions.CalculatorException;
import com.kyhslam.util.simulate.PidExceptions.IllegalConditionDefinitionException;
import com.kyhslam.util.simulate.evalEx.Expression;

/**
 * PID 실행에 필요한 유틸 모음 (StringUtil.NVL / parseInt, Calculator.parse, PartCommonFun.compareData 의 독립 버전)
 */
public class PidUtil {

	public final static String opt_start = "[$";
	public final static String opt_end = "$]";

	private PidUtil() {}

	/** dyna.plmetc.bom.StringUtil.NVL */
	public static String NVL(Object str1, String str2)
	{
		if(str1 == null)
			return str2;
		return str1.toString();
	}

	/** dyna.plmetc.util.StringUtil.parseInt */
	public static int parseInt(Object str)
	{
		try {
			return Integer.parseInt(NVL(str, "0").trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/** dyna.plmetc.util.Calculator.parse<br>
	 *  input 내의 [$ ... $] 수식을 모두 계산하여 치환한다.
	 */
	public static String calculate(String input) throws Exception
	{
		if(input != null)
		{
			try{
				while(input.contains(opt_start) && input.contains(opt_end))
				{
					int start = input.indexOf(opt_start);
					int end = input.indexOf(opt_end);

					String orgformula = input.substring(start+opt_start.length(), end);

					String convertedformula = orgformula.replaceAll("N", "0"); // N이면 0으로 간주한다.

					String formula_result = new Expression(convertedformula.replaceAll(" ", "")).eval().toPlainString(); // 빈칸 제거후 수식 계산

					input = input.replace(opt_start+orgformula+opt_end, formula_result);
				}
			}catch(RuntimeException e){
				throw new CalculatorException(input, e);
			}
		}

		return input;
	}

	/** dyna.plmetc.bom.PartCommonFun.compareData<br>
	 *  조건(condition)과 영업데이터(salesData)를 비교한다.
	 */
	public static boolean compareData(String condition, String salesData)
	{
		/* 수배데이타가 비어 있으면 true리턴 */
		if(condition == null || "".equals(condition))
			return true;

		if(condition.equals("ISNUMBER"))
		{
			boolean isNumber = true;
			try
			{
				Double.parseDouble(salesData);
			}
			catch(NumberFormatException e)
			{
				isNumber = false;
			}

			return isNumber;
		}

		// 예약어 처리
		if(condition.equals("BLANK"))
		{
			return salesData.equals("");
		}
		else if(condition.equals("!BLANK"))
		{
			return !salesData.equals("");
		}

		if((salesData.equals(""))) // 영업데이터가 없다면 N으로 간주한다.
			salesData = "N";

		try {
			switch (condition.charAt(0)) {
				case ',': // OR operation
					if (!PidBomComputeOps.initOrOp(salesData, condition.substring(1))) {
						return false;
					}
					break;
				case '!':
					if (!PidBomComputeOps.initNotOp(salesData, condition.substring(1))) {
						return false;
					}
					break;
				case '<':
				case '>':
					if (salesData.equals("N")) {
						return false;
					}
					if (!PidBomComputeOps.initRelationalOp(salesData, condition)) {
						return false;
					}
					break;
				case '?': /* 중간에 don't care로 나오는 String */
					if (!PidBomComputeOps.initPartialStOp(salesData, condition.substring(1))) {
						return false;
					}
					break;
				case ' ':
					break;
				default: // 기본 동일 정보
					if (!PidBomComputeOps.initSimpleOp(salesData, condition)) {
						return false;
					}
					break;
			}
			return true;
		} catch (RuntimeException e) {
			throw new IllegalConditionDefinitionException(condition);
		}
	}
}
