package com.kyhslam.pidSimul;

import com.kyhslam.BlockSimul.BlockComDb;
import com.kyhslam.BlockSimul.BlockSpecComparator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * dyna.plmetc.variant.PIDJavaMethod 의 독립 버전 (METHOD 가 JAVA 인 PID)
 * - BlockSimul.BlockPIDJavaMethod 에서 필요한 메소드만 옮겨온다. (PID 명 = 메소드명)
 * - 새 JAVA PID 가 필요하면 BlockPIDJavaMethod 의 같은 이름 메소드를 이 클래스로 옮기면 된다.
 */
public class PidJavaMethod {

	private final PidDb db;

	public PidJavaMethod(PidDb db) {
		this.db = db;
	}

	/** PIDJavaMethod.executeJAVA : PID 명과 같은 이름의 메소드를 호출 */
	public PidVariantMap executeJAVA(Map elvEnt, List<Map> floorMasterList, String pid, Map partInfo) throws Exception {
		Method m;
		try {
			m = getClass().getMethod(pid, Map.class, List.class, Map.class);
		} catch (NoSuchMethodException e) {
			throw new UnsupportedOperationException("JAVA 방식 PID 메소드가 PidJavaMethod 에 없습니다 (BlockPIDJavaMethod 에서 옮겨야 함) : " + pid);
		}

		try {
			return (PidVariantMap) m.invoke(this, elvEnt, floorMasterList, partInfo);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			throw cause instanceof Exception ? (Exception) cause : e;
		}
	}

	/** 최초 설계일자 : 수주번호(ABENGBYSALES$SF) 의 EL_ZFDA ~ EL_ZFDD */
	public PidVariantMap AUTO_FIRSTDESIGNDATE(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap result = new PidVariantMap();

		try {
			String hogiNum = PidUtil.NVL(elvEnt.get("md$number"), "");
			String sujuNum = hogiNum.substring(0, 6);
			if (hogiNum.contains("-")) {
				sujuNum = hogiNum.substring(0, hogiNum.indexOf("-"));
			}

			// JdbcTemplate.queryForMap : 0건이면 null, 2건 이상이면 예외
			List<Map<String, Object>> rows = db.queryForList(
					" SELECT MD$NUMBER, EL_ZFDA, EL_ZFDB, EL_ZFDC, EL_ZFDD from ABENGBYSALES$SF where MD$NUMBER = ? ", sujuNum);
			if (rows.size() > 1)
				throw new Exception("Incorrect result size: expected 1, actual " + rows.size());

			if (rows.size() == 1) {
				Map<String, Object> plmBomData = rows.get(0);
				result.put("O_ZFDA", PidUtil.NVL(plmBomData.get("EL_ZFDA"), "0"));
				result.put("O_ZFDB", PidUtil.NVL(plmBomData.get("EL_ZFDB"), "0"));
				result.put("O_ZFDC", PidUtil.NVL(plmBomData.get("EL_ZFDC"), "0"));
				result.put("O_ZFDD", PidUtil.NVL(plmBomData.get("EL_ZFDD"), "0"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/** 오늘 날짜 (yyyyMMdd) */
	public PidVariantMap FUNCTION_NOW(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap result = new PidVariantMap();
		result.put("O_DATE", new SimpleDateFormat("yyyyMMdd").format(new Date()));
		return result;
	}

	/** V_STR 를 V_SEPARATOR(정규식) 로 분리 → O_CNT, O_STR1 ~ O_STRn */
	public PidVariantMap FUNCTION_SPLIT(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap result = new PidVariantMap();
		String V_STR = PidUtil.NVL(elvEnt.get("V_STR"), "");
		String V_SEPARATOR = PidUtil.NVL(elvEnt.get("V_SEPARATOR"), "");

		if ("".equals(V_SEPARATOR) || "".equals(V_STR)) {
			result.put("O_CNT", "0");
		} else {
			String[] split = V_STR.split(V_SEPARATOR);
			result.put("O_CNT", String.valueOf(split.length));
			for (int i = 1; i <= split.length; i++) {
				result.put("O_STR" + i, split[i - 1]);
			}
		}

		return result;
	}

	/**
	 * 블록별 출하일 : COMDB(SRM) ZPPT027 조회 (BlockSimul.BlockComDb 사용, 접속은 CommonDBConnection)
	 * 데이터가 없거나 값이 비어있으면 오늘 + 60일
	 */
	public PidVariantMap FUNCTION_READ_ZPPT027(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap result = new PidVariantMap();
		String hogiNum = PidUtil.NVL(elvEnt.get("EL_ZORINO"), "");
		List<Map> ZPPT027DATA = new BlockComDb().getZPPT027DATA(hogiNum);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 60);
		String tempDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

		if (ZPPT027DATA.isEmpty()) {
			result.put("MANDT", "100");
			result.put("VBELN", hogiNum.substring(0, 6));
			for (String block : ZPPT027_BLOCKS) {
				result.put("SHIP_" + block, tempDate);
				result.put("SHIP_MIN_" + block, tempDate);
			}
			return result;
		}

		// 여러 건이면 마지막 행 값 사용 (원본과 동일)
		for (Map ZPPT027 : ZPPT027DATA) {
			result.put("MANDT", PidUtil.NVL(ZPPT027.get("MANDT"), ""));
			result.put("VBELN", PidUtil.NVL(ZPPT027.get("VBELN"), ""));
			for (String block : ZPPT027_BLOCKS) {
				result.put("SHIP_" + block, PidUtil.NVL(ZPPT027.get("SHIP_" + block), tempDate));
				result.put("SHIP_MIN_" + block, PidUtil.NVL(ZPPT027.get("SHIP_MIN_" + block), tempDate));
			}
		}
		return result;
	}

	private static final String[] ZPPT027_BLOCKS = { "A", "B", "C", "D", "E", "F" };

	/** 계약 상태 : COMDB(SRM) zmaster02 에 취소(TXT04=C) 건이 있으면 CONTRACT_STATUS=C */
	public PidVariantMap FUNCTION_READ_CONTRACT_STATUS(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap result = new PidVariantMap();
		String hogiNum = PidUtil.NVL(elvEnt.get("V_STRING"), ""); //V_STRING 송근원 매니저 요청

		List<Map> ZMASTER02 = new BlockComDb().getZMASTER02TXT04(hogiNum);
		result.put("CONTRACT_STATUS", ZMASTER02.isEmpty() ? "" : "C");
		return result;
	}

	/** 층 목록 : 층별 FL_* / FE_* 값과 층 위치별 수량 (층이 2개 이상일 때만) */
	public PidVariantMap CALC_FLOOR_LIST(Map elvEnt, List<Map> floorMasterList, Map partInfo) {
		PidVariantMap result = new PidVariantMap();

		try {
			if (floorMasterList != null && floorMasterList.size() >= 2) {
				//FL_F{n}	FL_R{n}	FL_H{n}	FL_L{n}	FL_S{n}
				//층표기(FRONT)	층표기(REAR)	층고	층위치	층구분
				int V_FLOOR_ETC_Q = 0;
				int V_FLOOR_UNDERMAIN_Q = 0;
				int i = 1;
				for (Map floorInfo : floorMasterList) {
					int EL_EFLOORH = PidUtil.parseInt(floorInfo.get("EL_EFLOORH"));
					String EL_AFFT = PidUtil.NVL(floorInfo.get("EL_AFFT"), ""); //FRONT 층표기
					String EL_ARFT = PidUtil.NVL(floorInfo.get("EL_ARFT"), ""); //REAR 층표기
					String EL_EFPO = PidUtil.NVL(floorInfo.get("EL_EFPO"), ""); //층위치

					if ("MAIN".equals(EL_EFPO) || "UNDER".equals(EL_EFPO))
						V_FLOOR_UNDERMAIN_Q++;
					else if ("ETC".equals(EL_EFPO))
						V_FLOOR_ETC_Q++;

					result.put("FL_S" + i, i == 1 ? "최하층" : "중간층");
					result.put("FL_H" + i, EL_EFLOORH);
					result.put("FL_F" + i, EL_AFFT);
					result.put("FL_R" + i, EL_ARFT);
					result.put("FL_L" + i, EL_EFPO);

					// 층 사양값 (EL_EFLOORH 는 숫자로 넣는다 - 원본과 동일)
					for (String field : FLOOR_EXTRA_FIELDS) {
						result.put("FE_" + field + i, "EL_EFLOORH".equals(field) ? (Object) EL_EFLOORH : PidUtil.NVL(floorInfo.get(field), ""));
					}

					i++;
				}

				result.put("FL_S" + floorMasterList.size(), "최상층");
				result.put("V_FLOOR_ETC_Q", String.valueOf(V_FLOOR_ETC_Q));
				result.put("V_FLOOR_UNDERMAIN_Q", String.valueOf(V_FLOOR_UNDERMAIN_Q));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("ERRMSG", e.getMessage());
		}

		return result;
	}

	/** CALC_FLOOR_LIST 의 FE_{필드}{n} 로 내보내는 층 필드 */
	private static final String[] FLOOR_EXTRA_FIELDS = {
			"EL_AFFT", "EL_ARFT", "EL_CMDL", "EL_EFLOORH", "EL_EFLOORHA", "EL_EFLOORQ", "EL_EFPO", "EL_EFT",
			"EL_CJAMBT", "EL_CJAMBM", "EL_CJAMBC", "EL_CHDM", "EL_CHDOD", "EL_CHDC", "EL_CHDET", "EL_CHDFR",
			"EL_CHDGD", "EL_CHPB", "EL_CHPBM", "EL_CHPBMA", "EL_CHPIT", "EL_CHSM", "EL_DHSE", "EL_CHLCDT",
			"EL_CHLT", "EL_CHCARD", "EL_EJMJD", "EL_EJMMH", "EL_CHDAD" };

	/** 현장 엘리베이터 대수 : 같은 수주(앞 6자리) 호기 중 L / NC 호기 수 */
	public PidVariantMap CAL_COUNT_EL(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap drawMap = new PidVariantMap();

		BlockSpecComparator sc = new BlockSpecComparator(elvEnt);

		if ("T".equals(elvEnt.get("IS_COST"))) {
			if (sc.c("CO_QTDAT", ">20230413") || sc.c("CO_NQTGBDAT", ">20230413"))
				drawMap.put("COUNT_EL", elvEnt.get("CO_ELQTY"));
			else
				drawMap.put("COUNT_EL", null);
			return drawMap;
		}

		String CAL_COUNT_EL = "0";
		try {
			drawMap.put("COUNT_EL", "");
			String hogiNum = PidUtil.NVL(elvEnt.get("md$number"), "");
			int L_count = 0;
			int NC_count = 0;

			//TEST 호기의 경우 EL_ZORINO (기존설계호기번호) 의 값 사용
			if (hogiNum.contains("TEST")) {
				String imsiHogiNum = getEL_ZORINO(hogiNum);
				if (imsiHogiNum != null && imsiHogiNum.length() > 0)
					hogiNum = imsiHogiNum;
			}

			if (!hogiNum.contains("TEST")) {
				for (String md$number : getCOUNT_ELData(hogiNum.substring(0, 6))) {
					if (md$number.contains("L"))
						L_count++;
					if (md$number.contains("NC"))
						NC_count++;
				}
				CAL_COUNT_EL = L_count == 0 ? "0" : Integer.toString(L_count + NC_count);
			}
			drawMap.put("COUNT_EL", CAL_COUNT_EL);
		} catch (Exception e) {
			e.printStackTrace();
			drawMap.put("COUNT_EL", "CHK");
		}

		return drawMap;
	}

	/** 호기의 EL_ZORINO (기존설계호기번호). 1건이 아니거나 오류면 "" */
	private String getEL_ZORINO(String hogiNum) {
		try {
			List<Map<String, Object>> rows = db.queryForList(
					" SELECT EL_ZORINO FROM ELV_INFO$VF, ELV_INFO$ID WHERE VF$OUID = ID$WIP AND MD$NUMBER = ?", hogiNum);
			if (rows.size() == 1)
				return PidUtil.NVL(rows.get(0).get("EL_ZORINO"), null);
		} catch (Exception e) {
			// 오류 시 "" 로 리턴
		}
		return "";
	}

	/** 수주번호로 시작하는 호기 목록 (영업사양 / 선박 / JQPR) */
	private List<String> getCOUNT_ELData(String hogiNum_project_no) {
		List<String> resultList = new ArrayList<String>();
		try {
			for (Map<String, Object> r : db.queryForList(
					" select md$number from elv_info$vf, elv_info$id where vf$ouid=id$wip and md$Number like ?||'%' "
							+ " union "
							+ " select md$number from shipelv_info$vf, shipelv_info$id where vf$ouid=id$wip and md$Number like ?||'%' "
							+ " union "
							+ " select md$number from JQPR_info$vf, jqpr_info$id where vf$ouid=id$wip and md$Number like ?||'%' ",
					hogiNum_project_no, hogiNum_project_no, hogiNum_project_no)) {
				resultList.add(PidUtil.NVL(r.get("MD$NUMBER"), ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	/** PROJ_NO 호기의 영업사양 주요 값 → O_{필드} */
	public PidVariantMap FUNCTION_READ_ELEV(Map elvEnt, List<Map> floorMasterList, Map partInfo) throws Exception {
		PidVariantMap result = new PidVariantMap();
		String projectNo = PidUtil.NVL(elvEnt.get("PROJ_NO"), "");

		Map<String, Object> elvinfo = null;
		try {
			elvinfo = db.queryForFirst(" select " + String.join(",", READ_ELEV_SELECT)
					+ " from elv_info$vf, elv_info$id where vf$ouid=id$wip and md$Number =? ", projectNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 원본은 rs.getString(대소문자 무시) / 값이 없으면 "" (PidDb 결과 키는 대문자)
		for (String field : READ_ELEV_FIELDS) {
			String value = elvinfo == null ? "" : PidUtil.NVL(elvinfo.get(field.toUpperCase()), "");
			result.put("O_" + field, value);
		}

		result.put("FUNCTION_READ_ELEV", "Y");
		return result;
	}

	/** FUNCTION_READ_ELEV 결과 필드 (출력 순서 = 원본 순서, 키는 O_{필드}) */
	private static final String[] READ_ELEV_FIELDS = {
			"EL_AUSE", "EL_AMAN", "EL_ACAPA", "EL_AOPEN", "EL_ASPD", "EL_AFQ", "EL_ASTQ", "EL_ADRV", "EL_ATYP", "EL_ECN",
			"EL_ATF", "EL_EMF", "EL_AFF", "EL_ARF", "EL_ANST", "EL_AGRS", "EL_ACD2", "CO_LAND1", "EL_ABRAND", "EL_AMS",
			"EL_ARDR", "EL_ASPC", "EL_ASPCD", "EL_ASPLY", "EL_BCLCD", "EL_BCLCD2",
			"EL_CHLCDT0", "EL_CHLCDT1", "EL_CHLCDT2", "EL_CHLCDT3", "EL_CHLCDT4", "EL_CHLCDT5", "EL_CHLCDT6", "EL_CHLCDT7",
			"EL_DINTQ", "EL_DSV1", "EL_DSV2", "EL_ASPSC", "EL_ACONST", "EL_AFFQ", "EL_AMNO", "EL_ANSTQ", "EL_ARFQ", "EL_ASNO", "EL_BCLCDQ",
			"EL_EFLOORQ0", "EL_EFLOORQ1", "EL_EFLOORQ2", "EL_EFLOORQ3", "EL_EFLOORQ4", "EL_EFLOORQ5", "EL_EFLOORQ6", "EL_EFLOORQ7",
			"EL_AARRT", "EL_ECGP", "EL_ESPBS", "EL_ECWTP", "EL_DETS", "EL_DCCA", "EL_AARGRP", "EL_DELDTY", "remarks" };

	/** FUNCTION_READ_ELEV 조회 컬럼 (원본 getelvinfo 와 동일 : 코드 필드는 cod()) */
	private static final String[] READ_ELEV_SELECT = {
			"cod(EL_AUSE) EL_AUSE", "EL_AMAN", "cod(EL_ACAPA) EL_ACAPA",
			"cod(CO_LAND1) CO_LAND1", "cod(EL_ABRAND) EL_ABRAND", "cod(EL_AMS) EL_AMS", "cod(EL_ARDR) EL_ARDR",
			"cod(EL_ASPC) EL_ASPC", "cod(EL_ASPCD) EL_ASPCD", "cod(EL_ASPLY) EL_ASPLY", "cod(EL_BCLCD) EL_BCLCD", "cod(EL_BCLCD2) EL_BCLCD2",
			"cod(EL_CHLCDT0) EL_CHLCDT0", "cod(EL_CHLCDT1) EL_CHLCDT1", "cod(EL_CHLCDT2) EL_CHLCDT2", "cod(EL_CHLCDT3) EL_CHLCDT3",
			"cod(EL_CHLCDT4) EL_CHLCDT4", "cod(EL_CHLCDT5) EL_CHLCDT5", "cod(EL_CHLCDT6) EL_CHLCDT6", "cod(EL_CHLCDT7) EL_CHLCDT7",
			"cod(EL_DINTQ) EL_DINTQ", "cod(EL_DSV1) EL_DSV1", "cod(EL_DSV2) EL_DSV2", "cod(EL_ASPSC) EL_ASPSC",
			"EL_ACONST", "EL_AFFQ", "EL_AMNO", "EL_ANSTQ", "EL_ARFQ", "EL_ASNO", "EL_BCLCDQ",
			"EL_EFLOORQ0", "EL_EFLOORQ1", "EL_EFLOORQ2", "EL_EFLOORQ3", "EL_EFLOORQ4", "EL_EFLOORQ5", "EL_EFLOORQ6", "EL_EFLOORQ7", "remarks",
			"cod(EL_AOPEN) EL_AOPEN", "cod(EL_ASPD) EL_ASPD",
			"EL_AFQ", "EL_ASTQ", "cod(EL_ADRV) EL_ADRV", "cod(EL_ATYP) EL_ATYP", "EL_ECN", "EL_ATF", "cod(EL_ACD2) EL_ACD2",
			"EL_EMF", "EL_AFF", "EL_ARF", "EL_ANST", "cod(EL_AGRS) EL_AGRS",
			"cod(EL_AARRT) EL_AARRT", "cod(EL_ECGP) EL_ECGP", "cod(EL_ESPBS) EL_ESPBS", "cod(EL_ECWTP) EL_ECWTP",
			"cod(EL_DETS) EL_DETS", "cod(EL_DCCA) EL_DCCA", "EL_AARGRP", "cod(EL_DELDTY) EL_DELDTY" };
}
