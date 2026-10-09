package com.kyhslam.util.pidSimulatorUp;

import com.kyhslam.util.pidSimulatorUp.SimExceptions.NumberComparingException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidInfiniteLoopException;
import com.kyhslam.util.pidSimulatorUp.SimExceptions.PidNotFoundException;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository.PidInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dyna.plmetc.variant.Variant 의 독립 버전 (디버그 실행 포함)
 * - 디버그 실행시 행마다 rowTrue / compareResultList / resultMap 을 기록하고,
 *   결과에 debugData(로직 전체) 와 elvEnt(최종 사양) 를 넣는다. (PIDSimulator_right.jsp 출력용)
 */
public class SimVariant
{
	private final SimContext ctx;
	private HashMap elvEnt = null;
	private List<Map> floorMasterList = null;

	public SimVariant(SimContext ctx, HashMap elvEnt, List<Map> floorMasterList)
	{
		this.ctx = ctx;
		this.elvEnt = elvEnt;
		this.floorMasterList = floorMasterList;
	}

	/** PID로직을 실행. 최신버전만. */
	public SimVariantMap calcVariantPID(String PID, Map partInfo) throws Exception
	{
		return calcVariantPID(getLastPIDInfo(PID), partInfo, null, 1, false);
	}

	/**
	 * CALL 하위 PID 실행. depth
	 * 원본은 최신버전만. 여기서는 SimContext 에 버전을 지정한 하위 PID 면 그 버전으로 실행하고, 실행 기록을 남긴다.
	 */
	public SimVariantMap calcVariantPID(String PID, Map partInfo, int depth) throws Exception
	{
		return calcVariantPID(getSubPIDInfo(PID, depth), partInfo, null, depth, false);
	}

	/** PID로직을 실행. 버전 선택가능. (TEST 버전은 SimConsts.TEST_VERSION) 항상 디버그 */
	public SimVariantMap calcVariantPID(String PID, int version, Map partInfo, boolean debug) throws Exception
	{
		return calcVariantPID(getPIDInfo(PID, version), partInfo, null, 1, true);
	}

	/** 내부PID실행. 최신버전만. */
	public SimVariantMap calcVariantPID(String PID, Map partInfo, SimVariantMap resultMap, int depth, boolean doDebug) throws Exception
	{
		return calcVariantPID(getLastPIDInfo(PID), partInfo, resultMap, depth, doDebug);
	}

	/** 완성형 메소드. */
	private SimVariantMap calcVariantPID(PidInfo pid, Map partInfo, SimVariantMap resultMap, int depth, boolean doDebug) throws Exception
	{
		Map<String, String> elvData = null;

		HashMap localElvEnt = (HashMap) elvEnt.clone();

		if(pid == null)
			throw new PidNotFoundException("PID IS NULL");

		// "최초실행(depth=1)이면  resultMap은 null이므로 초기화해준다.
		if(resultMap == null)
			resultMap = new SimVariantMap();

		// partInfo가 없다면 null Exception을 피하기위해 초기화 해준다.
		if(partInfo == null)
			partInfo = new HashMap();

		// ouid로 실제 영업사양 값을 재 저장한다.
		String elvOuid = localElvEnt.containsKey("ouid") ? SimUtil.NVL(localElvEnt.get("ouid"),"") : "";
		if (!elvOuid.equals(""))
			elvData = ctx.getCodeNames(elvOuid);

		if(pid.getMethod().equals(SimConsts.METHOD_JAVA)) {
			resultMap = new SimPIDJavaMethod(ctx).executeJAVA(localElvEnt, floorMasterList, pid.getPid(), partInfo);
		}else if(pid.getMethod().equals(SimConsts.METHOD_DB)) {

			SimVariantMap pidLogicMap = doDebug
					? ctx.getPidRepository().getLogic(ctx.getDb(), pid.getPid(), pid.getVersion(), true)
					: ctx.getPidRepository().getLogic(ctx.getDb(), pid.getPid(), pid.getVersion()); //PID의 모든 라인 정보

			ArrayList data = (ArrayList) pidLogicMap.get("data");

			String prev_GOTO = "";

			for(int i = 0; i < data.size(); i++)
			{
				SimVariantMap row = (SimVariantMap) data.get(i);
				ArrayList compareResultList = new ArrayList();
				ArrayList specList = (ArrayList) row.get("specList");
				ArrayList conList = (ArrayList) row.get("conList");
				ArrayList keyList = (ArrayList) row.get("keyList");
				ArrayList valList = (ArrayList) row.get("valList");
				String isBlankLine = (String) row.get("isBlankLine");
				String line_no = (String) row.get("line_no");

				String GOTO = SimUtil.NVL(row.get("GOTO"),"").trim();
				String ADDR = SimUtil.NVL(row.get("ADDR"),"").trim();
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
						{
							compareResultList.add("");
							continue;
						}

						if(SimUtil.compareData(",PARTNO,REALPART,G_L_CODE,B_NO,PICK,PARAM1,PARAM2,PARAM3",specName))
							specVal = SimUtil.NVL(partInfo.get(specName),"");
						else if(specName.contains("[$") && specName.contains("$]"))
						{
							specName = convertVal(specName, localElvEnt, elvData);
							specVal  = SimUtil.calculate(specName);
						}
						else if (specName.contains("_CODE_NAME") && specName.length() > 10 && elvData != null) {	// _CODE_NAME 이 있을 경우 해당 코드의 특성값 내역을 출력
							specVal = converCodeName(specName, localElvEnt, elvData);
						}
						else
							specVal = SimUtil.NVL(localElvEnt.get(specName),"");

						// CON 데이터 보정 & 조건비교
						conVal = (String) conList.get(j);
						conValOrg = conVal;
						if(conVal == null || conVal.equals(""))
						{
							compareResultList.add("T");
							continue;
						}

						conVal = convertVal(conVal, localElvEnt, elvData);
						conVal = SimUtil.calculate(conVal);

						if((conVal.startsWith(">") || conVal.startsWith("<")) && specVal.equals("N"))
							throw new NumberComparingException(specVal, conVal);

						boolean conditionTrue = SimUtil.compareData(conVal,specVal);
						rowTrue &= conditionTrue;
						compareResultList.add(conditionTrue ? "T" : "F");

						// 디버깅중이 아니면 조건 불만족시 바로 break
						if(doDebug == false && rowTrue == false)
							break;
					}
					catch(Exception e)
					{
						saveErrorLog(DOUID, e.getClass().getSimpleName(), String.format("%s - SPEC:%s, CON:%s, ORG_SPEC:%s, ORG_CON:%s", e.getMessage(), specVal, conVal, specNameOrg, conValOrg));
						rowTrue = false;
						compareResultList.add("F");
					}
				}

				if(doDebug == true)
				{
					row.put("rowTrue",String.valueOf(rowTrue));
					row.put("compareResultList",compareResultList);
				}

				// 조건만족할시 결과값 저장
				if(rowTrue == true)
				{
					resultMap = calcResultValues(DOUID, keyList,valList,resultMap, localElvEnt, partInfo, depth, elvData);

					// 디버깅용
					if(doDebug == true && "false".equals(isBlankLine))
						row.put("resultMap",resultMap.clone());

					if(GOTO.equals("STOP"))
						break;
					else
						prev_GOTO = GOTO;
				}
			}

			// 디버깅용
			if(doDebug == true)
			{
				resultMap.put("debugData",pidLogicMap);
				resultMap.put("elvEnt", localElvEnt);
			}
		}

		return resultMap;
	}

	/** 결과값 KEY, VAL 계산 */
	private SimVariantMap calcResultValues(String DOUID, ArrayList keyList, ArrayList valList, SimVariantMap localMap, HashMap localElvEnt, Map partInfo, int depth, Map<String, String> elvData)
	{
		Pattern p = Pattern.compile("^[0-9 ]*$");
		Matcher m = null;
		for(int j = 0; j < keyList.size(); j++)
		{
			String key = (String) keyList.get(j);
			String val = SimUtil.NVL(valList.get(j),"");
			String valOrg = val;

			try
			{
				if(key == null)
					continue;

				val = convertVal(val, localElvEnt, elvData);
				val = SimUtil.calculate(val);
			}
			catch(Exception e)
			{
				saveErrorLog(DOUID, e.getClass().getSimpleName(), String.format("%s - KEY:%s, VAL: %s, ORG_VAL: %s", e.getMessage(), key, val, valOrg));
			}

			if(key.equals("CALL")) // 다른 PID를 호출한다.
			{
				try
				{
					if(depth+1 > SimConsts.MAX_PID_DEPTH)
						throw new PidInfiniteLoopException(val);

					SimVariant variant = new SimVariant(ctx, localElvEnt, floorMasterList);
					SimVariantMap innerFunctionMap = variant.calcVariantPID(val, partInfo, depth+1);

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
					specValue = SimUtil.NVL(localElvEnt.get(specKey),"");

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
		return SimUtil.NVL(elvData.get(specCodeName),SimUtil.NVL(localElvEnt.get(specCodeVal),""));	// name이 없을 경우 특성값 그대로 출력
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

	/** PidService.debugPid : 전처리 PID 결과를 영업사양에 추가 */
	public void appendToElvEnt(Map map)
	{
		elvEnt.putAll(map);
	}

	private PidInfo getLastPIDInfo(String PID) throws Exception
	{
		PidInfo result = ctx.getPidRepository().getLastPid(ctx.getDb(), PID);
		if(result == null)
			throw new PidNotFoundException(PID);
		return result;
	}

	/** CALL 하위 PID 정보 : 지정 버전이 있으면 그 버전, 없으면 최신 버전 (실행 기록을 남긴다) */
	private PidInfo getSubPIDInfo(String PID, int depth) throws Exception
	{
		Integer version = ctx.getSubPidVersion(PID);
		PidInfo info;
		try {
			info = version == null ? getLastPIDInfo(PID) : getPIDInfo(PID, version);
		} catch (Exception e) {
			ctx.recordSubPid(PID, null, null, depth, e.getClass().getSimpleName() + " : " + e.getMessage());
			throw e;
		}
		if (info == null) {
			String msg = PID + " 의 " + (version == SimConsts.TEST_VERSION ? "TEST 버전" : version + " 버전") + "이 없습니다.";
			ctx.recordSubPid(PID, null, null, depth, msg);
			throw new PidNotFoundException(msg);
		}
		ctx.recordSubPid(PID, info.getName(), info.getVersion(), depth, null);
		return info;
	}

	/** Variant.getPIDInfo : 원본은 PIDCache 에 PID 가 없으면 PidNotFoundException */
	private PidInfo getPIDInfo(String PID, int version) throws Exception
	{
		getLastPIDInfo(PID);
		return ctx.getPidRepository().getPid(ctx.getDb(), PID, version);
	}
}
