package com.kyhslam.pidSimul;

import com.kyhslam.pidSimul.PidExceptions.NumberComparingException;
import com.kyhslam.pidSimul.PidExceptions.PidInfiniteLoopException;
import com.kyhslam.pidSimul.PidExceptions.PidNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dyna.plmetc.variant.Variant 의 독립 버전 (DB 방식 PID 로직 실행기)
 * - DOS / Spring / PIDCache 없이 PidDb(JDBC) 로만 동작한다.
 * - METHOD 가 JAVA 인 PID 는 PidJavaMethod 로 실행한다. (옮겨온 메소드만 지원)
 */
public class PidVariant
{
	private final PidDb db;
	private final PidSpecLoader loader;
	private HashMap elvEnt = null;
	private List<Map> floorMasterList = null;
	/** variant_errorlog 에 오류 저장 여부 (원본은 항상 저장) */
	private boolean saveErrorLog = false;

	/** elv ouid → DOSChangeable 의 "name@필드" 값 캐시 (원본의 elvData) */
	private final Map<String, Map<String, String>> codeNameCache;

	public PidVariant(PidDb db, PidSpecLoader loader, HashMap elvEnt, List<Map> floorMasterList)
	{
		this(db, loader, elvEnt, floorMasterList, new HashMap<String, Map<String, String>>());
	}

	private PidVariant(PidDb db, PidSpecLoader loader, HashMap elvEnt, List<Map> floorMasterList, Map<String, Map<String, String>> codeNameCache)
	{
		this.db = db;
		this.loader = loader;
		this.elvEnt = elvEnt;
		this.floorMasterList = floorMasterList;
		this.codeNameCache = codeNameCache;
	}

	public void setSaveErrorLog(boolean saveErrorLog)
	{
		this.saveErrorLog = saveErrorLog;
	}

	/** PID로직을 실행. 최신버전만. depth */
	public PidVariantMap calcVariantPID(String PID, Map partInfo, int depth) throws Exception
	{
		PidInfo lastPIDInfo = getLastPIDInfo(PID);

		return calcVariantPID(lastPIDInfo, partInfo, null, depth, false);
	}

	/** PID로직을 실행. 최신버전, 디버그 */
	public PidVariantMap calcVariantPID(String PID, Map partInfo, PidVariantMap resultMap, int depth, boolean doDebug) throws Exception
	{
		PidInfo lastPidInfo = getLastPIDInfo(PID);

		return calcVariantPID(lastPidInfo, partInfo, resultMap, depth, doDebug);
	}

	/** PID로직을 실행. 버전 선택가능. (TEST 버전은 -1) */
	public PidVariantMap calcVariantPID(String PID, int version, Map partInfo, boolean debug) throws Exception
	{
		PidInfo pidInfo = getPIDInfo(PID, version);
		return calcVariantPID(pidInfo, partInfo, null, 1, true);
	}

	/**
	 * 완성형 메소드.
	 */
	private PidVariantMap calcVariantPID(PidInfo pid, Map partInfo, PidVariantMap resultMap, int depth, boolean doDebug) throws Exception
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
		String elvOuid = localElvEnt.containsKey("ouid") ? PidUtil.NVL(localElvEnt.get("ouid"),"") : "";
		if (!elvOuid.equals(""))
			elvData = getElvCodeNames(elvOuid);

		if(pid.getMethod().equals(PidConsts.METHOD_JAVA)) {
			resultMap = new PidJavaMethod(db).executeJAVA(localElvEnt, floorMasterList, pid.getPid(), partInfo);
		}else if(pid.getMethod().equals(PidConsts.METHOD_DB)) {

			PidVariantMap pidLogicMap = getPIDLogic(pid.getPid(), pid.getVersion(), doDebug); //PID의 모든 라인 정보 셋팅

			ArrayList data = (ArrayList) pidLogicMap.get("data"); ////PID의 모든 라인 정보 추출

			String prev_GOTO = "";

			for(int i = 0; i < data.size(); i++)
			{
				PidVariantMap row = (PidVariantMap) data.get(i);
				ArrayList compareResultList = new ArrayList();
				ArrayList specList = (ArrayList) row.get("specList");
				ArrayList conList = (ArrayList) row.get("conList");
				ArrayList keyList = (ArrayList) row.get("keyList");
				ArrayList valList = (ArrayList) row.get("valList");
				String isBlankLine = (String) row.get("isBlankLine");
				String line_no = (String) row.get("line_no");

				String GOTO = PidUtil.NVL(row.get("GOTO"),"").trim();
				String ADDR = PidUtil.NVL(row.get("ADDR"),"").trim();
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

						if(PidUtil.compareData(",PARTNO,REALPART,G_L_CODE,B_NO,PICK,PARAM1,PARAM2,PARAM3",specName))
							specVal = PidUtil.NVL(partInfo.get(specName),"");
						else if(specName.contains("[$") && specName.contains("$]"))
						{
							specName = convertVal(specName, localElvEnt, elvData);
							specVal  = PidUtil.calculate(specName);
						}
						else if (specName.contains("_CODE_NAME") && specName.length() > 10 && elvData != null) {	// _CODE_NAME 이 있을 경우 해당 코드의 특성값 내역을 출력
							specVal = converCodeName(specName, localElvEnt, elvData);
						}
						else
							specVal = PidUtil.NVL(localElvEnt.get(specName),"");

						// CON 데이터 보정 & 조건비교
						conVal = (String) conList.get(j);
						conValOrg = conVal;
						if(conVal == null || conVal.equals(""))
						{
							compareResultList.add("T");
							continue;
						}
						else
						{
							conVal = convertVal(conVal, localElvEnt, elvData);
							conVal = PidUtil.calculate(conVal);

							if((conVal.startsWith(">") || conVal.startsWith("<")) && specVal.equals("N"))
								throw new NumberComparingException(specVal, conVal);

							boolean conditionTrue = PidUtil.compareData(conVal,specVal);
							rowTrue &= conditionTrue;

							if(conditionTrue == true)
								compareResultList.add("T");
							else if(conditionTrue == false)
								compareResultList.add("F");
						}

						// 디버깅중이 아니면 조건 불만족시 바로 break
						if(doDebug == false && rowTrue == false)
						{
							break;
						}
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
					if(doDebug == true && isBlankLine.equals("false"))
					{
						row.put("resultMap",resultMap.clone());
					}

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

	/**
	 * 결과값 KEY, VAL 계산
	 */
	private PidVariantMap calcResultValues(String DOUID, ArrayList keyList, ArrayList valList, PidVariantMap localMap, HashMap localElvEnt, Map partInfo, int depth, Map<String, String> elvData)
	{
		Pattern p = Pattern.compile("^[0-9 ]*$");
		Matcher m = null;
		for(int j = 0; j < keyList.size(); j++)
		{
			String key = (String) keyList.get(j);
			String val = PidUtil.NVL(valList.get(j),"");
			String valOrg = val;

			try
			{
				if(key == null)
					continue;

				val = convertVal(val, localElvEnt, elvData);
				val = PidUtil.calculate(val);
			}
			catch(Exception e)
			{
				saveErrorLog(DOUID, e.getClass().getSimpleName(), String.format("%s - KEY:%s, VAL: %s, ORG_VAL: %s", e.getMessage(), key, val, valOrg));
			}

			if(key.equals("CALL")) // 다른 PID를 호출한다.
			{
				try
				{
					if(depth+1 > 30)
						throw new PidInfiniteLoopException(val);

					PidVariantMap innerFunctionMap = null;
					PidVariant variant = new PidVariant(db, loader, localElvEnt, floorMasterList, codeNameCache);
					variant.setSaveErrorLog(saveErrorLog);

					innerFunctionMap = variant.calcVariantPID(val, partInfo, depth+1);

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
				{
					val = val.trim(); // 결과값이 숫자라면 trim한다.
				}

				localElvEnt.put(key,val);
				localMap.put(key,val);
			}
		}

		return localMap;
	}

	/**
	 * PID 로직 불러오기 (variant_h / variant_d)
	 */
	public PidVariantMap getPIDLogic(String PID, int version, boolean doDebug)
	{
		int maxSpecIdx = 0;
		int maxResIdx = 0;

		PidVariantMap res = new PidVariantMap();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		try
		{
			StringBuffer sql = new StringBuffer();

			sql.append(" select b.* from variant_h a, variant_d b ");
			sql.append(" where a.houid = b.houid ");
			sql.append(" AND   a.pid = ? ");
			sql.append(" AND   a.version = ? ");
			sql.append(" order by DOUID ");
			List<Map<String, Object>> logicDataList = db.queryForList(sql.toString(), PID, version);

			for(Map<String, Object> row : logicDataList) {
				ArrayList specList = new ArrayList();
				ArrayList conList = new ArrayList();
				ArrayList keyList = new ArrayList();
				ArrayList valList = new ArrayList();
				boolean isBlankLine = true;

				for(int i = 1; i <= PidConsts.MAX_SPEC_FIELD_SIZE; i++)
				{
					String specTmp = (String) row.get("SPEC" + i);
					String conTmp = (String) row.get("CON" + i);

					if(doDebug == false) // 운영용
					{
						if(specTmp != null)
						{
							specList.add(specTmp);
							conList.add(conTmp);
						}
					}
					else // 디버깅용
					{
						specList.add(specTmp);
						conList.add(conTmp);
					}

					if(specTmp != null)
					{
						maxSpecIdx = (maxSpecIdx < i) ? i : maxSpecIdx;
						isBlankLine = false;
					}
				}

				for(int i = 1; i <= PidConsts.MAX_RES_FIELD_SIZE; i++)
				{
					String keyTmp = (String) row.get("KEY" + i);
					String valTmp = (String) row.get("VAL" + i);

					if(doDebug == false) // 운영용
					{
						if(keyTmp != null)
						{
							keyList.add(keyTmp);
							valList.add(valTmp);
						}
					}
					else // 디버깅용
					{
						keyList.add(keyTmp);
						valList.add(valTmp);
					}

					if(keyTmp != null)
					{
						maxResIdx = (maxResIdx < i) ? i : maxResIdx;
						isBlankLine = false;
					}
				}

				// 한 row에 담길 내용
				PidVariantMap rowMap = new PidVariantMap();
				rowMap.put("specList",specList);
				rowMap.put("conList",conList);
				rowMap.put("keyList",keyList);
				rowMap.put("valList",valList);
				rowMap.put("GOTO",row.get("GOTO"));
				rowMap.put("ADDR",row.get("ADDR"));
				rowMap.put("REMARKS",row.get("REMARKS"));
				rowMap.put("DOUID",row.get("DOUID").toString());
				rowMap.put("isBlankLine", String.valueOf(isBlankLine));
				rowMap.put("line_no", row.get("NO").toString());

				data.add(rowMap);
			}

			res.put("data",data);
			res.put("maxSpecIdx",String.valueOf(maxSpecIdx));
			res.put("maxResIdx",String.valueOf(maxResIdx));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return res;
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

				if (value.contains("_CODE_NAME") && value.length() > 10 && elvData != null) {		// _CODE_NAME 이 있을 경우 해당 코드의 특성값 내역을 출력
					specValue = converCodeName(specKey, localElvEnt, elvData);
				}
				else {
					specValue = PidUtil.NVL(localElvEnt.get(specKey),"");
				}

				value = value.replace("{" + specKey + "}",specValue);
			}
			else
			{
				isEnd = true;
			}
			nCount++;
		}

		value = value.replace("&#160;","");

		return value;
	}

	public String converCodeName(String specKey, HashMap localElvEnt, Map<String, String> elvData) throws Exception
	{
		String specCodeVal = specKey.substring(0, specKey.length()-10);		// ex) EL_ASPSC
		String specCodeName = "name@" + specCodeVal;						// ex) name@EL_ASPSC
		return PidUtil.NVL(elvData.get(specCodeName),PidUtil.NVL(localElvEnt.get(specCodeVal),""));	// name이 없을 경우 특성값 그대로 출력
	}

	public void saveErrorLog(String DOUID, String exceptionType, String msg)
	{
		String hogi_number = (String) elvEnt.get("md$number");

		if(!saveErrorLog)
		{
			System.err.println("[PID-ERROR] hogi=" + hogi_number + ", DOUID=" + DOUID + ", " + exceptionType + " : " + msg);
			return;
		}

		StringBuffer sql = new StringBuffer();

		sql.append(" MERGE INTO variant_errorlog a ");
		sql.append("      USING DUAL ");
		sql.append("         ON (a.DOUID = ? AND a.EXCEPTION_TYPE = ?) ");
		sql.append(" WHEN MATCHED ");
		sql.append(" THEN ");
		sql.append("    UPDATE SET ");
		sql.append("       LAST_OCCUR_HOGI = ?, ");
		sql.append("       LAST_OCCUR_DATE = SYSDATE, ");
		sql.append("       MSG = ?, ");
		sql.append("       hit = hit + 1 ");
		sql.append(" WHEN NOT MATCHED ");
		sql.append(" THEN ");
		sql.append("    INSERT     (DOUID,EXCEPTION_TYPE,MSG, LAST_OCCUR_HOGI, LAST_OCCUR_DATE) ");
		sql.append("    VALUES (?,?,?,?,sysdate) ");

		try {
			db.update(sql.toString(), DOUID, exceptionType, hogi_number, msg, DOUID, exceptionType, msg, hogi_number);
		}catch (Exception e){
			System.err.println(e.getMessage());
		}
	}

	public void appendToElvEnt(Map map){
		elvEnt.putAll(map);
	}

	// ------------------------------------------------------------------------

	private Map<String, String> getElvCodeNames(String ouid) throws Exception
	{
		Map<String, String> result = codeNameCache.get(ouid);
		if(result == null && !codeNameCache.containsKey(ouid))
		{
			PidSpecLoader.SpecObject obj = loader.load(ouid);
			result = (obj == null) ? null : obj.codeNameMap;
			codeNameCache.put(ouid, result);
		}
		return result;
	}

	private PidInfo getLastPIDInfo(String PID) throws Exception
	{
		PidInfo result = findPid(" select a.* from variant_h a, variant_id b where a.houid = b.last_houid and a.pid = ? ", PID);
		if(result == null)
			throw new PidNotFoundException(PID);
		return result;
	}

	private PidInfo getPIDInfo(String PID, int version) throws Exception
	{
		getLastPIDInfo(PID); // 원본은 PIDCache 에 PID 가 없으면 PidNotFoundException

		return findPid(" select * from variant_h where pid = ? and version = ? ", PID, version);
	}

	private PidInfo findPid(String sql, Object... params) throws Exception
	{
		Map<String, Object> row = db.queryForFirst(sql, params);
		if(row == null)
			return null;

		PidInfo pid = new PidInfo();
		pid.setHOuid(PidUtil.NVL(row.get("HOUID"), null));
		pid.setPid((String) row.get("PID"));
		pid.setName((String) row.get("NAME"));
		pid.setMethod(PidUtil.NVL(row.get("METHOD"), ""));
		pid.setVersion(PidUtil.parseInt(row.get("VERSION")));
		pid.setRemarks((String) row.get("REMARKS"));
		pid.setUserId((String) row.get("USERID"));
		pid.setIsfloorspec((String) row.get("ISFLOORSPEC"));
		return pid;
	}

	/** dyna.plmetc.variant.dto.Pid 의 필요한 부분 */
	public static class PidInfo
	{
		private String hOuid;
		private String pid;
		private String name;
		private String method;
		private int version;
		private String remarks;
		private String userId;
		private String isfloorspec;

		public String getHOuid() { return hOuid; }
		public void setHOuid(String hOuid) { this.hOuid = hOuid; }
		public String getPid() { return pid; }
		public void setPid(String pid) { this.pid = pid; }
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getMethod() { return method; }
		public void setMethod(String method) { this.method = method; }
		public int getVersion() { return version; }
		public void setVersion(int version) { this.version = version; }
		public String getRemarks() { return remarks; }
		public void setRemarks(String remarks) { this.remarks = remarks; }
		public String getUserId() { return userId; }
		public void setUserId(String userId) { this.userId = userId; }
		public String getIsfloorspec() { return isfloorspec; }
		public void setIsfloorspec(String isfloorspec) { this.isfloorspec = isfloorspec; }
	}
}
