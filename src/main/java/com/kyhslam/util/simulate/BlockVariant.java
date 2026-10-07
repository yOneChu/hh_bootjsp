package com.kyhslam.util.simulate;

import com.kyhslam.util.simulate.PidExceptions.NumberComparingException;
import com.kyhslam.util.simulate.PidExceptions.PidInfiniteLoopException;
import com.kyhslam.util.simulate.PidExceptions.PidNotFoundException;
import com.kyhslam.util.simulate.BlockPidRepository.PidInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dyna.plmetc.variant.Variant 의 독립 버전 (운영용 PID 실행. 디버그 기능 제외)
 */
public class BlockVariant
{
	private final BlockContext ctx;
	private HashMap elvEnt = null;
	private List<Map> floorMasterList = null;

	public BlockVariant(BlockContext ctx, HashMap elvEnt, List<Map> floorMasterList)
	{
		this.ctx = ctx;
		this.elvEnt = elvEnt;
		this.floorMasterList = floorMasterList;
	}

	/** PID로직을 실행. 최신버전만. */
	public PidVariantMap calcVariantPID(String PID, Map partInfo) throws Exception
	{
		return calcVariantPID(getLastPIDInfo(PID), partInfo, null, 1);
	}

	/** PID로직을 실행. 최신버전만. depth */
	public PidVariantMap calcVariantPID(String PID, Map partInfo, int depth) throws Exception
	{
		return calcVariantPID(getLastPIDInfo(PID), partInfo, null, depth);
	}

	private PidVariantMap calcVariantPID(PidInfo pid, Map partInfo, PidVariantMap resultMap, int depth) throws Exception
	{
		Map<String, String> elvData = null;

		HashMap localElvEnt = (HashMap) elvEnt.clone();

		if(pid == null)
			throw new PidNotFoundException("PID IS NULL");

		// "최초실행(depth=1)이면  resultMap은 null이므로 초기화해준다.
		if(resultMap == null)
			resultMap = new PidVariantMap();

		// partInfo가 없다면 null Exception을 피하기위해 초기화 해준다.
		if(partInfo == null)
			partInfo = new HashMap();

		// ouid로 실제 영업사양 값을 재 저장한다.
		String elvOuid = localElvEnt.containsKey("ouid") ? BlockUtil.NVL(localElvEnt.get("ouid"),"") : "";
		if (!elvOuid.equals(""))
			elvData = ctx.getCodeNames(elvOuid);

		if(pid.getMethod().equals(BlockConsts.METHOD_JAVA)) {
			resultMap = new BlockPIDJavaMethod(ctx).executeJAVA(localElvEnt, floorMasterList, pid.getPid(), partInfo);
		}else if(pid.getMethod().equals(BlockConsts.METHOD_DB)) {

			PidVariantMap pidLogicMap = ctx.getPidRepository().getLogic(ctx.getDb(), pid.getPid(), pid.getVersion()); //PID의 모든 라인 정보

			ArrayList data = (ArrayList) pidLogicMap.get("data");

			String prev_GOTO = "";

			for(int i = 0; i < data.size(); i++)
			{
				PidVariantMap row = (PidVariantMap) data.get(i);
				ArrayList specList = (ArrayList) row.get("specList");
				ArrayList conList = (ArrayList) row.get("conList");
				ArrayList keyList = (ArrayList) row.get("keyList");
				ArrayList valList = (ArrayList) row.get("valList");
				String line_no = (String) row.get("line_no");

				String GOTO = BlockUtil.NVL(row.get("GOTO"),"").trim();
				String ADDR = BlockUtil.NVL(row.get("ADDR"),"").trim();
				String DOUID = (String) row.get("DOUID");
				if(i!=0 && !prev_GOTO.equals("") && !prev_GOTO.equals("STOP") && !prev_GOTO.equals(ADDR))
					continue;
				else if(prev_GOTO.equals(ADDR))
					prev_GOTO = "";

				// # 라인은 주석처리
				if("#".equals(ADDR))
					continue;

				// 해당 라인 추출 기능 추가
				localElvEnt.put("LINE_NO", line_no);

				// 조건비교
				boolean rowTrue = true;
				// 1.해당 라인의 SPEC,CON을 수행한다.
				for(int j = 0; j < specList.size(); j++)
				{
					String specName="",specVal="", conVal="", specNameOrg="", conValOrg="";
					try
					{
						// SPEC 데이터 보정
						specName = (String) specList.get(j);
						specNameOrg = specName;
						if(specName == null)
							continue;

						if(BlockUtil.compareData(",PARTNO,REALPART,G_L_CODE,B_NO,PICK,PARAM1,PARAM2,PARAM3",specName))
							specVal = BlockUtil.NVL(partInfo.get(specName),"");
						else if(specName.contains("[$") && specName.contains("$]"))
						{
							specName = convertVal(specName, localElvEnt, elvData);
							specVal  = BlockUtil.calculate(specName);
						}
						else if (specName.contains("_CODE_NAME") && specName.length() > 10 && elvData != null) {	// _CODE_NAME 이 있을 경우 해당 코드의 특성값 내역을 출력
							specVal = converCodeName(specName, localElvEnt, elvData);
						}
						else
							specVal = BlockUtil.NVL(localElvEnt.get(specName),"");

						// CON 데이터 보정 & 조건비교
						conVal = (String) conList.get(j);
						conValOrg = conVal;
						if(conVal == null || conVal.equals(""))
							continue;

						conVal = convertVal(conVal, localElvEnt, elvData);
						conVal = BlockUtil.calculate(conVal);

						if((conVal.startsWith(">") || conVal.startsWith("<")) && specVal.equals("N"))
							throw new NumberComparingException(specVal, conVal);

						rowTrue &= BlockUtil.compareData(conVal,specVal);

						// 조건 불만족시 바로 break
						if(rowTrue == false)
							break;
					}
					catch(Exception e)
					{
						saveErrorLog(DOUID, e.getClass().getSimpleName(), String.format("%s - SPEC:%s, CON:%s, ORG_SPEC:%s, ORG_CON:%s", e.getMessage(), specVal, conVal, specNameOrg, conValOrg));
						rowTrue = false;
					}
				}

				// 조건만족할시 결과값 저장
				if(rowTrue == true)
				{
					resultMap = calcResultValues(DOUID, keyList,valList,resultMap, localElvEnt, partInfo, depth, elvData);

					if(GOTO.equals("STOP"))
						break;
					else
						prev_GOTO = GOTO;
				}
			}
		}

		return resultMap;
	}

	/** 결과값 KEY, VAL 계산 */
	private PidVariantMap calcResultValues(String DOUID, ArrayList keyList, ArrayList valList, PidVariantMap localMap, HashMap localElvEnt, Map partInfo, int depth, Map<String, String> elvData)
	{
		Pattern p = Pattern.compile("^[0-9 ]*$");
		Matcher m = null;
		for(int j = 0; j < keyList.size(); j++)
		{
			String key = (String) keyList.get(j);
			String val = BlockUtil.NVL(valList.get(j),"");
			String valOrg = val;

			try
			{
				if(key == null)
					continue;

				val = convertVal(val, localElvEnt, elvData);
				val = BlockUtil.calculate(val);
			}
			catch(Exception e)
			{
				saveErrorLog(DOUID, e.getClass().getSimpleName(), String.format("%s - KEY:%s, VAL: %s, ORG_VAL: %s", e.getMessage(), key, val, valOrg));
			}

			if(key.equals("CALL")) // 다른 PID를 호출한다.
			{
				try
				{
					if(depth+1 > BlockConsts.MAX_PID_DEPTH)
						throw new PidInfiniteLoopException(val);

					BlockVariant variant = new BlockVariant(ctx, localElvEnt, floorMasterList);
					PidVariantMap innerFunctionMap = variant.calcVariantPID(val, partInfo, depth+1);

					if(innerFunctionMap != null)
					{
						localMap.putAll(innerFunctionMap);
						localElvEnt.putAll(innerFunctionMap);
					}
				}
				catch(Exception e)
				{
					saveErrorLog(DOUID, e.getClass().getSimpleName(), e.getMessage()+" : "+key+", "+val);
				}
			}
			else if(key.equals("OUTPUT")) // 출력될 key들을 선언한다.
			{
				localMap.put(key+DOUID+j, val);
			}
			else
			{
				m = p.matcher(val);
				if(m.matches() == true)
					val = val.trim(); // 결과값이 숫자라면 trim한다.

				localElvEnt.put(key,val);
				localMap.put(key,val);
			}
		}

		return localMap;
	}

	public String convertVal(String value, HashMap localElvEnt, Map<String, String> elvData) throws Exception
	{
		boolean isEnd = false;
		int nCount = 0;

		while(!isEnd && nCount < 50)
		{
			if(value.indexOf("{") != -1)
			{
				int nStart = value.indexOf("{");
				int nEnd = value.indexOf("}");
				String specKey = value.substring(nStart + 1,nEnd);
				String specValue = "";

				if (value.contains("_CODE_NAME") && value.length() > 10 && elvData != null)		// _CODE_NAME 이 있을 경우 해당 코드의 특성값 내역을 출력
					specValue = converCodeName(specKey, localElvEnt, elvData);
				else
					specValue = BlockUtil.NVL(localElvEnt.get(specKey),"");

				value = value.replace("{" + specKey + "}",specValue);
			}
			else
			{
				isEnd = true;
			}
			nCount++;
		}

		return value.replace("&#160;","");
	}

	public String converCodeName(String specKey, HashMap localElvEnt, Map<String, String> elvData)
	{
		String specCodeVal = specKey.substring(0, specKey.length()-10);		// ex) EL_ASPSC
		String specCodeName = "name@" + specCodeVal;						// ex) name@EL_ASPSC
		return BlockUtil.NVL(elvData.get(specCodeName),BlockUtil.NVL(localElvEnt.get(specCodeVal),""));	// name이 없을 경우 특성값 그대로 출력
	}

	public void saveErrorLog(String DOUID, String exceptionType, String msg)
	{
		String hogi_number = (String) elvEnt.get("md$number");

		if(!ctx.isSaveErrorLog())
		{
			System.err.println("[PID-ERROR] hogi=" + hogi_number + ", DOUID=" + DOUID + ", " + exceptionType + " : " + msg);
			return;
		}

		String sql = " MERGE INTO variant_errorlog a USING DUAL ON (a.DOUID = ? AND a.EXCEPTION_TYPE = ?) "
				+ " WHEN MATCHED THEN UPDATE SET LAST_OCCUR_HOGI = ?, LAST_OCCUR_DATE = SYSDATE, MSG = ?, hit = hit + 1 "
				+ " WHEN NOT MATCHED THEN INSERT (DOUID,EXCEPTION_TYPE,MSG, LAST_OCCUR_HOGI, LAST_OCCUR_DATE) VALUES (?,?,?,?,sysdate) ";
		try {
			ctx.getDb().update(sql, DOUID, exceptionType, hogi_number, msg, DOUID, exceptionType, msg, hogi_number);
		}catch (Exception e){
			System.err.println(e.getMessage());
		}
	}

	private PidInfo getLastPIDInfo(String PID) throws Exception
	{
		PidInfo result = ctx.getPidRepository().getLastPid(ctx.getDb(), PID);
		if(result == null)
			throw new PidNotFoundException(PID);
		return result;
	}
}
