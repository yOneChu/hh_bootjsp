package com.kyhslam.pidSimul;

import java.math.BigDecimal;

/*
 * =======================================================================
 * CLASS PidBomComputeOps
 * DESCRIPTION
 * Bom계산을 위한 연산 함수들의 집합
 * DEPENDENCIES
 * 
 * MADY BY
 * guijubae 2008/04/
 * ==========================================================================
 */
public class PidBomComputeOps
{

	private static final int LESS_THAN = 0;
	private static final int GREATER_THAN = 1;
	private static final int LESS_THAN_EQ = 2;
	private static final int GREATER_THAN_EQ = 3;

	/*
	 * =======================================================================
	 * FUNCTION initNotOp
	 * 
	 * DESCRIPTION !를 처리하기 위한 함수로 !A !(A) !(A,B) !(?AF?,?GH?)와 같은 패턴을 처리하며 시작 에서
	 * !을 빼고 비교 시작한다 DEPENDENCIES
	 * 
	 * RETURN VALUE boolean // true if 일치,false if 불일치
	 * 
	 * SIDE EFFECTS
	 * ==============================================================
	 * ============
	 */
	public static boolean initNotOp(String input_data, String spec_data)
	{
		char token = spec_data.charAt(0);

		boolean IsPartial = false;
		// System.out.println("initNotOp() Called");

		if(token == '(')
		{
			int length = spec_data.length();
			int index = 1; // 첫번째 괄호를 빼고 parsing시작

			StringBuffer specBuffer = new StringBuffer();
			String specString = null;

			while(index < length)
			{
				token = spec_data.charAt(index);
				// System.out.println("token : "+token);
				switch (token)
				{
					case ')':
					case ',':
						if(!IsPartial)
						{
							specString = specBuffer.toString();
							// System.out.println("Spec : "+specString);

							if(specString.equals(input_data))
							{
								// System.out.println("Op Fail");
								return false; // 같지 않아야 하는데 같은 조건이 나타났으므로 더 이상
												// 비교없이 false리턴
							}
							specBuffer.delete(0, specBuffer.length());
						}
						IsPartial = false; // gjbae_080324 not 오류 수정
											// !(SPCC,?EGI?,?SUS?,N)
						break;
					case '?': // !(?AG?,?GT?) 경우 하나씩 잘라서 테스트 한다

						int endIdx = (spec_data.substring(index + 1)).indexOf('?') + index + 1;// 다음
																								// ?가
																								// 나오는
																								// index
						// System.out.println("?의 endIdx : "+endIdx);
						// System.out.println("index :"+index);
						String temp = spec_data.substring(index + 1, endIdx + 1);
						if(initPartialStOp(input_data, temp))
						{ // NOT 이므로 partial 인 경우 false
							// System.out.println("Op Fail");
							return false;
						}
						index = endIdx; // 테스트 한 만큼 건너뛴다
						IsPartial = true;
						break;
					default:
						specBuffer.append(token);
						break;
				}
				index++;
			}

			return true; // 모두 돌았는데 모두 같지 않으면 true 리턴
		}
		else
		{
			if(spec_data.contains("?"))
			{
				return !initPartialStOp(input_data, spec_data);
			}
			else
			{
				return !spec_data.equals(input_data);		
			}
		}

	}

	/*
	 * =======================================================================
	 * FUNCTION initSimpleOp
	 * 
	 * DESCRIPTION 단순 Simple String 비교 DEPENDENCIES
	 * 
	 * RETURN VALUE boolean // true if 일치,false if 불일치
	 * 
	 * SIDE EFFECTS
	 * ==============================================================
	 * ============
	 */
	public static boolean initSimpleOp(String input_data, String condition)
	{
		if(condition.equals("N")) // 수배데이타 N일때 영업데이타는 N 이거나 blank인경우 true
		{
			if(input_data.equals("N") || input_data.length() == 0)
				return true;
		}
		else if(condition.equals(input_data))
		{
			return true;
		}

		return false;
	}

	/*
	 * =======================================================================
	 * FUNCTION initOrOp
	 * 
	 * DESCRIPTION ,로 시작하는 Or operation을 비교하는 데 simple string과 partial string이
	 * 함께 존재하기 때문에 구분하여 비교한다 DEPENDENCIES
	 * 
	 * RETURN VALUE boolean // true if 일치,false if 불일치
	 * 
	 * SIDE EFFECTS
	 * ==============================================================
	 * ============
	 */
	public static boolean initOrOp(String input_data, String spec_data)
	{
		char token;
		int length = spec_data.length();
		// boolean IsStart = true;
		boolean IsSimpleStr = true; // simple string인 경우 true,partial string인 경우
									// false

		StringBuffer strBuf = new StringBuffer();

		// System.out.println("initOrOp() Called");
		// System.out.println("Sales Data : "+input_data);
		// System.out.println("Subae Data : "+spec_data);

		for(int j = 0; j < length; j++)
		{
			token = spec_data.charAt(j);

			// System.out.println("Token : "+token);

			switch (token)
			{
				case ',':
					if(IsSimpleStr)
					{
						// System.out.println("Sales Data : "+input_data);
						// System.out.println("Subae Data :
						// "+strBuf.toString());
						if(PidBomComputeOps.initSimpleOp(input_data, strBuf.toString()))
						{
							// System.out.println("return true ");
							return true;
						}
						strBuf.delete(0, strBuf.length());
					}
					else
					{

						if(PidBomComputeOps.initPartialStOp(input_data, strBuf.toString()))
						{
							// System.out.println("return true ");
							return true;
						}
						strBuf.delete(0, strBuf.length());
					}
					IsSimpleStr = true;
					break;
				case ' ':
					break;
				case '?':
					IsSimpleStr = false;
					strBuf.append(token);
					break;
				default:
					strBuf.append(token);
					break;

			}
		}

		if(IsSimpleStr)
		{
			if(PidBomComputeOps.initSimpleOp(input_data, strBuf.toString()))
			{
				// System.out.println("return true ");
				return true;
			}

		}
		else
		{

			if(PidBomComputeOps.initPartialStOp(input_data, strBuf.toString()))
			{
				// System.out.println("return true ");
				return true;
			}
		}

		// System.out.println("return false ");
		return false;
	}

	/*
	 * =======================================================================
	 * FUNCTION initRelationalOp
	 * 
	 * DESCRIPTION < 또는 >로 시작되는 관계 연산자를 처리하는 함수 DEPENDENCIES
	 * 
	 * RETURN VALUE boolean // true if 일치,false if 불일치
	 * 
	 * SIDE EFFECTS
	 * ==============================================================
	 * ============
	 */
	// 관계 연산자 operator parsing ,spacebar 처리 로직 추가
	public static boolean initRelationalOp(String input_data, String spec_data)
	{
		char token;
		int length = spec_data.length();
		boolean result = true;
		BigDecimal input = new BigDecimal(input_data);
		StringBuffer number = new StringBuffer();
		boolean ORflag = false;

		int Operator = 0;

		// System.out.println("initRelationalOp() Called");

		for(int j = 0; j < length; j++)
		{
			token = spec_data.charAt(j);

			// System.out.println("Token : "+token);
			switch (token)
			{
				case '<':
					Operator = LESS_THAN;
					break;
				case '>':
					Operator = GREATER_THAN;
					break;
				case '=':
					if(Operator == LESS_THAN)
						Operator = LESS_THAN_EQ;
					else
						Operator = GREATER_THAN_EQ;
					break;
				case ',':// 여기서 한번 비교 한 후 number를 reset
					result = result && runRelationalOp(input, new BigDecimal(number.toString()), Operator);
					// System.out.println("result : "+result);
					number.delete(0, number.length());
					break;
				case '|':
					result = result && runRelationalOp(input, new BigDecimal(number.toString()), Operator);
					// System.out.println("result : "+result);
					number.delete(0, number.length());
					ORflag = true;
					break;
				case ' ':
					break;
				default: // 숫자
					number.append(token);
					break;
			}
		}
		
		if(ORflag == true)
		{
			result = result || runRelationalOp(input, new BigDecimal(number.toString()), Operator);
		}
		else
		{
			result = result && runRelationalOp(input, new BigDecimal(number.toString()), Operator);
		}
		// for문이 끝나고 마지막 걸 여기서 비교해야 한다

		return result;
	}

	// 부분 스트링으로 ?AG? 처리한다
	public static boolean initPartialStOp(String input_data, String spec_data)
	{
		char token;
		int length = spec_data.length();
		StringBuffer string_num = new StringBuffer();
		String temp = null;
		boolean result = false;
		boolean IsStart = true;

		// System.out.println("initPartialStOp() Called");
		// System.out.println("Sales Data : "+input_data);
		// System.out.println("Subae Data : "+spec_data);

		for(int j = 0; j < length; j++)
		{
			token = spec_data.charAt(j);

			// System.out.println("Token : "+token);

			switch (token)
			{
				case '?':
					if(IsStart)
					{
						IsStart = false;
					}
					else
					{
						temp = string_num.toString();
						if(input_data.indexOf(temp) >= 0)
							result = true;
						string_num.delete(0, string_num.length());
						IsStart = true;
					}
					break;
				default: // 숫자또는 문자
					string_num.append(token);
					IsStart = false;
					break;
			}
		}
		return result;
	}

	public static boolean runRelationalOp(BigDecimal input, BigDecimal conValue, int op)
	{
		boolean result = false;

		switch (op)
		{
			case LESS_THAN:
				if(input.compareTo(conValue) == -1)
					result = true;
				break;
			case GREATER_THAN:
				if(input.compareTo(conValue) == 1)
					result = true;
				break;
			case LESS_THAN_EQ:
				if(input.compareTo(conValue) <= 0)
					result = true;
				break;
			case GREATER_THAN_EQ:
				if(input.compareTo(conValue) >= 0)
					result = true;
				break;
			default:
				break;

		}

		return result;

	}

}