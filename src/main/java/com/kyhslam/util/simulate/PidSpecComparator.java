package com.kyhslam.util.simulate;

import java.util.Map;

/**
 * 사양 조건 비교 (BlockSimul.BlockSpecComparator 의 운영 버전, PidJavaMethod 에서 쓰는 부분)
 */
public class PidSpecComparator
{
	private Map<String,Object> specMap = null;

	public PidSpecComparator(Map<String,Object> specMap)
	{
		this.specMap = specMap;
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
				throw new Exception("PidSpecComparator - specMap is null");

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
					isCorrect = PidUtil.compareData(condition, PidUtil.NVL(specMap.get(specName), ""));
			}
		}
		catch(Exception e)
		{
			System.err.println("sc error - name: "+specName+", val: "+PidUtil.NVL(specMap.get(specName), "")+", con: "+condition);
			isCorrect = false;
		}

		return isCorrect;
	}
}
