package com.kyhslam.util.pidSimulatorUp;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimSpecComparator
{
	private Map<String,Object> specMap = null;
	private Pattern pat = null;
	private Pattern sign_pat = null;

	public SimSpecComparator(Map<String,Object> specMap)
	{
		this.specMap = specMap;
		pat = Pattern.compile("[<>]");
		sign_pat = Pattern.compile("[<>]=*");
	}

	
	/** compare(String specName, String condition) 와 동일 : compare 입력하기 번거로워서 작성 */
	public boolean c(String specName, String condition)
	{
		return compare(specName, condition);
	}

	public boolean compare(String specName, String condition)
	{
		boolean isCorrect = true;

		try
		{
			if(specMap == null)
				throw new Exception("SimSpecComparator - specMap is null");
			
			if(specName == null || condition == null)
			{
				isCorrect = true;
			}
			else
			{
				specName = specName.trim();
				condition = condition.trim();
				
				if(specName.equals("") || condition.equals(""))
					isCorrect = true;
				else
					isCorrect = SimUtil.compareData(condition, SimUtil.NVL(specMap.get(specName), ""));
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			System.err.println("sc error - name: "+specName+", val: "+SimUtil.NVL(specMap.get(specName), "")+", con: "+condition);
			isCorrect = false;
		}
		
//		if(isCorrect == false)
//		{
//			System.out.println(specName + " is failed : " +condition+", "+SimUtil.NVL(specMap.get(specName), ""));
//		}

		return isCorrect;
	}

	/** 전기수량주석에서 활용 - 테이블형태이며 신규스펙이 중간에 삽입되지 않고 항상 마지막에 추가되는 형태에서 사용하기 적합하다*/
	public boolean compare(String specNames[], String conditions[])
	{
		boolean isCorrect = true;

		try
		{
			if(conditions.length > specNames.length)
				throw new Exception("SimSpecComparator - specName array size is less than condition array size.");

			for(int i = 0; i < specNames.length && isCorrect == true; i++)
			{
				isCorrect &= compare(specNames[i], conditions[i]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			isCorrect = false;
		}

		return isCorrect;
	}

	/** 기계 주석에서 활용 - 한문장으로 정의한 방식에서 사용한다.<br> ex) EL_AOPEN=1SCO && EL_ECDOP=P1 && EL_BCPI=,?400?,?410? && EL_AMAN > 16 */
	public boolean compare(String sentence)
	{
		boolean isCorrect = true;

		try
		{
			if(sentence.contains("&&&"))
				throw new Exception("SimSpecComparator - sentence has wrong expression :" + sentence);

			String specConditions[] = sentence.split("&&");
			for(int i = 0; i < specConditions.length; i++)
			{
				String def = specConditions[i];
				
				isCorrect &= compareWithInequlity(def);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			isCorrect = false;
		}

		return isCorrect;
	}
	
	/** 부등호 처리 로직 */
	private boolean compareWithInequlity(String sentence)
	{
		boolean isCorrect = true;
		try
		{
			Matcher mat = pat.matcher(sentence);
			
			int i = 0;
			while(mat.find())
			{
				i++;
			}
			
			if(i==2) // 부등호 2개 사용할 경우 ex) 60 < EL_ASPD < 120
			{
				String firstSign = "", secondSign = "";
				Matcher sign_mat = sign_pat.matcher(sentence);

				sign_mat.find();
				firstSign = sign_mat.group();
				sign_mat.find();
				secondSign = sign_mat.group();

				String firstArr[] = sentence.substring(0, sentence.lastIndexOf(secondSign)).split(firstSign);
				String secondArr[] = sentence.substring(sentence.indexOf(firstSign)+firstSign.length()).split(secondSign);
				
				isCorrect &= compare(firstArr[1], getOppositeSign(firstSign)+firstArr[0]);
				isCorrect &= compare(secondArr[0], secondSign+secondArr[1]);
			}
			else if(i==1) // 부등호 1개 사용할 경우 ex) EL_ASPD > 60
			{
				Matcher sign_mat = sign_pat.matcher(sentence);
				
				String sign = "";
				
				sign_mat.find();
				sign = sign_mat.group();
				
				
				String[] splitArr = sentence.split(sign);
				
				if(splitArr[0].contains("_")) // 위치 반대 일경우 처리 60 < EL_ASPD
				{
					isCorrect &= compare(splitArr[0], sign+splitArr[1]);
				}
				else
				{
					isCorrect &= compare(splitArr[1], getOppositeSign(sign)+splitArr[0]);
				}
			}
			else
			{
				String tokens[] = sentence.split("=");

				if(tokens.length != 2)
					throw new Exception("SimSpecComparator - token lengh is invalid : " + tokens.length);

				tokens[0] = tokens[0].trim();
				tokens[1] = tokens[1].trim();

				if(tokens[0].contains(" "))
					throw new Exception("SimSpecComparator - token[0] has space : " + tokens[0]);
				if(tokens[1].contains(" "))
					throw new Exception("SimSpecComparator - token[1] has space : " + tokens[1]);

				isCorrect &= compare(tokens[0], tokens[1]);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			isCorrect = false;
		}

		return isCorrect;
	}
	
	/** 입력부등호의 반대를 리턴한다. */
	private String getOppositeSign(String sign)
	{
		String res = "";
		if(sign.equals(">"))
			res = "<";
		else if(sign.equals(">="))
			res = "<=";
		else if(sign.equals("<"))
			res = ">";
		else if(sign.equals("<="))
			res = ">=";
		
		return res;
	}
}