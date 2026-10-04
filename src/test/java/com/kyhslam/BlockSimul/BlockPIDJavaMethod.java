package com.kyhslam.BlockSimul;

import com.kyhslam.BlockSimul.BlockExceptions.InputValueUnvalidException;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * dyna.plmetc.variant.PIDJavaMethod 의 독립 버전.
 * 원본 소스를 그대로 옮기고 Spring/DOS/서비스 의존 부분만 BlockSimul 클래스로 대체했다.
 *   - StringUtil → BlockUtil, PartCommonFun → BlockUtil, BlockSpecComparator → BlockSpecComparator
 *   - BlockFloorInfoHandler → BlockFloorInfoHandler
 *   - DBconnectionInfo → PLMDBConnection (PLM DB), commondbImpl → BlockComDb (CommonDBConnection)
 *   - ebomService.getOrderBom → BlockEBomReader.getOrderBom
 */
public class BlockPIDJavaMethod {

	/** 원본 log(slf4j) 대체 */
	private static final Log log = new Log();

	private static class Log {
		void debug(Object msg) {}
		void info(Object msg) {}
		void error(Object msg) { System.err.println(msg); }
		void error(Object msg, Throwable t) { System.err.println(msg); }
	}

	private final BlockContext ctx;

	public BlockPIDJavaMethod(BlockContext ctx) {
		this.ctx = ctx;
	}

	private boolean c(String[] salesData, String[] condition) {
		boolean result = true;
		for(int i=0; i<=condition.length && result==true; i++) {
			result &= c(condition[i], salesData[i]);
		}
		return result;
	}

	private boolean c(String salesData, String condition) {
		return BlockUtil.compareData(condition, salesData);
	}


	public BlockVariantMap executeJAVA(Map elvEnt, List<Map> floorMasterList, String pid, Map partInfo) throws Exception {
		BlockVariantMap res = null;

		try {
			Class c = this.getClass();
			Class[] parameterTypes = new Class[] {Map.class, List.class ,  Map.class};
			Object[] arguments = new Object[] {};
			arguments = new Object[] { elvEnt, floorMasterList,  partInfo };
			Method m = c.getMethod(pid, parameterTypes);
			res = (BlockVariantMap) m.invoke(this, arguments);
		} catch(NoSuchMethodException e) {
			throw new NoSuchMethodException("JAVA에 해당 메소드 정의 안됨. PID : " + pid);
		}
		catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}

		return res;
	}

	public BlockVariantMap WOODY_JAVATEST(Map elvEnt,  List<Map> floorMasterList, Map partInfo) {
		BlockVariantMap result = new BlockVariantMap();
		result.put("CMT", "TEST");
		return result;
	}

	public BlockVariantMap B189CB_JAVA1(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
	{
		partInfo = null; // partInfo는 input에 없음. 명시적으로 넣음.
		BlockVariantMap result = new BlockVariantMap();

		String LADDER_CMT = "";
		String LADDER_QTY = "";
		int ANCHOR_QTY = 0;

		String B189CB_LA = BlockUtil.NVL(elvEnt.get("B189CB_LA"), "");

		String EL_ABRAND = BlockUtil.NVL(elvEnt.get("EL_ABRAND"), "");
		String EL_AUSE = BlockUtil.NVL(elvEnt.get("EL_AUSE"), "");
		String EL_ELADT = BlockUtil.NVL(elvEnt.get("EL_ELADT"), "");
		String EL_DEHL = BlockUtil.NVL(elvEnt.get("EL_DEHL"), "");
		String EL_ESYSPO = BlockUtil.NVL(elvEnt.get("EL_ESYSPO"), "");
		String EL_ECEE = BlockUtil.NVL(elvEnt.get("EL_ECEE"), "");
		int EL_ECCH = BlockUtil.parseInt(elvEnt.get("EL_ECCH"));
		int EL_ECHH = BlockUtil.parseInt(elvEnt.get("EL_ECHH"));

		int MIN_FH = 0;
		int MAX_FH = 0;

		double anchorQty = 0;
		double ProtQty = 0;
		double ladderQty = 0;
		String ladderCmt = "";

		if (c(EL_ABRAND, ",NEX_MR,NEX_MRL,NEO_MRL") && c(EL_AUSE, "?E?") && c(EL_ELADT, "FIX") && c(EL_DEHL, ",SL,PS,CP,AL,EL")) {
			MIN_FH = EL_ECCH - EL_ECHH + 2900;
			MAX_FH = 6000;

			Map<Integer, Integer> ladderCountMap = new HashMap<Integer, Integer>();

			for (Map floorInfo : floorMasterList) {
				int EL_EFLOORH = BlockUtil.parseInt(floorInfo.get("EL_EFLOORH"));
				if (EL_EFLOORH <= MIN_FH) {
					continue;
				}
				else if (EL_EFLOORH > MIN_FH && EL_EFLOORH <= MAX_FH) {
					int LA = EL_ECHH + 500; /* 사다리 하나의 길이 */
					if(ladderCountMap.containsKey(LA))
						ladderCountMap.put(LA, ladderCountMap.get(LA)+1);
					else
						ladderCountMap.put(LA, 1);
				} else if (EL_EFLOORH >= MAX_FH) {
					int LA = EL_EFLOORH + EL_ECHH; /* 사다리 하나의 길이 */
					if(ladderCountMap.containsKey(LA))
						ladderCountMap.put(LA, ladderCountMap.get(LA)+1);
					else
						ladderCountMap.put(LA, 1);
				}
			}

			Iterator<Entry<Integer, Integer>> iterator = ladderCountMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Integer, Integer> next = iterator.next();
				int LA = next.getKey();
				int count = next.getValue();

				anchorQty += count * 6;
				ProtQty += count * 1;
				ladderQty += LA * count;
				ladderCmt += "LA="+LA+"*"+count+"EA\n";
			}

			ladderCmt = ladderCmt.trim();

			result.put("LADDER_CMT", ladderCmt);
			result.put("LADDER_QTY", BlockUtil.convertFloat(BlockUtil.ceil(ladderQty/1000, 3)));
			result.put("ANCHOR_QTY", BlockUtil.convertFloat(anchorQty));
			result.put("T_PROT_QTY", BlockUtil.convertFloat(ProtQty));

		} else if (c(EL_ABRAND, ",NEX_MRL,NEO_MRL") && c(EL_AUSE, "?E?") && c(EL_ELADT, "RAIL") && c(EL_DEHL, ",SL,PS,CP,AL,EL")) {
			MIN_FH = EL_ECCH - EL_ECHH + 2900;
			MAX_FH = 6000;

			int LADDER_Q = 0;
			for (Map floorInfo : floorMasterList) {
				int EL_EFLOORH = BlockUtil.parseInt(floorInfo.get("EL_EFLOORH"));
				if (EL_EFLOORH > MIN_FH)
				{
					if (EL_EFLOORH < 6000)
						LADDER_Q += 2;
					else
						LADDER_Q += BlockUtil.floor(EL_EFLOORH / 2000.0);
				}
			}

			result.put("LADDER_QTY", String.valueOf(LADDER_Q));
		}

		return result;
	}

	public BlockVariantMap A204AE_JAVA1(Map elvEnt,  List<Map> floorMasterList, Map partInfo) {
		partInfo = null; // partInfo는 input에 없음. 명시적으로 넣음.
		BlockVariantMap result = new BlockVariantMap();
		int EL_ASTQ = BlockUtil.parseInt(elvEnt.get("EL_ASTQ"));
		int EFLOORH_SUM = 0;
		int LENGTH_A = 0; // 최상층 - 30번째층 층고 합
		int LENGTH_B = 0; // 최상층 - 60번째층 층고 합

		floorMasterList.sort((o1, o2) -> {
			int o1Index = BlockUtil.parseInt(o1.get("md$index"));
			int o2Index = BlockUtil.parseInt(o2.get("md$index"));
			if (o1Index == o2Index)
				return 0;
			else
				return o1Index > o2Index ? 1 : -1;
		});

		// FULL 관통은 중복된 층이 있을 수 있음. 중복된 층은 둘 중 더 높은 층고만 남기고 나머지 버림.
		List<String> floorNameList = new ArrayList<String>();
		Map<String, Integer> floorMaxHeightMap = new HashMap<String, Integer>();
		for (Map floorInfo : floorMasterList) {
			String floorName = (String) floorInfo.get("FLOOR_NAME");
			int EL_EFLOORH = BlockUtil.parseInt(floorInfo.get("EL_EFLOORH"));

			if (!floorNameList.contains(floorName))
				floorNameList.add(floorName);

			if (floorMaxHeightMap.containsKey(floorName)) {
				int maxHeight = floorMaxHeightMap.get(floorName) > EL_EFLOORH ? floorMaxHeightMap.get(floorName)
						: EL_EFLOORH;
				floorMaxHeightMap.put(floorName, maxHeight);
			} else {
				floorMaxHeightMap.put(floorName, EL_EFLOORH);
			}
		}

		//최상층~30번째층 까지 층고 합계
		if(floorNameList.size() >= 31)
		{
			for (int i = 1; i <= 30; i++) {
				//HashMap floorInfo = floorMasterList.getDataMap();
				int EL_EFLOORH = floorMaxHeightMap.get(floorNameList.get(floorNameList.size()-i));
				LENGTH_A += EL_EFLOORH;
			}
			result.put("LENGTH_A", LENGTH_A);

		}
		//최상층~60번째층 까지 층고 합계
		if(floorNameList.size() >= 61)
		{
			for (int i = 1; i <= 60; i++) {
				int EL_EFLOORH = floorMaxHeightMap.get(floorNameList.get(floorNameList.size()-i));
				LENGTH_B += EL_EFLOORH;
			}
			result.put("LENGTH_B", LENGTH_B);

		}


		return result;
	}

	public BlockVariantMap DUTY_HOIST_HEIGHT(Map elvEnt,  List<Map> floorMasterList, Map partInfo) {
		BlockVariantMap result = new BlockVariantMap();
		int EL_EHTRH = 0;
		int EL_EHO = 0;
		int EL_EHTH = 0;

		int EL_EHP = BlockUtil.parseInt(elvEnt.get("EL_EHP"));

		if (floorMasterList != null && floorMasterList.size() >= 2) {
			// floorMasterList 정렬
			floorMasterList.sort((o1, o2) -> {
				int o1Index = BlockUtil.parseInt(o1.get("md$index"));
				int o2Index = BlockUtil.parseInt(o2.get("md$index"));
				if (o1Index == o2Index)
					return 0;
				else
					return o1Index > o2Index ? 1 : -1;
			});

			// FULL 관통은 중복된 층이 있을 수 있음. 중복된 층은 둘 중 더 높은 층고만 남기고 나머지 버림.
			List<String> floorNameList = new ArrayList<String>();
			Map<String, Integer> floorMaxHeightMap = new HashMap<String, Integer>();
			for (Map floorInfo : floorMasterList) {
				String floorName = (String) floorInfo.get("FLOOR_NAME");
				int EL_EFLOORH = BlockUtil.parseInt(floorInfo.get("EL_EFLOORH"));

				if(!floorNameList.contains(floorName))
					floorNameList.add(floorName);

				if(floorMaxHeightMap.containsKey(floorName)) {
					int maxHeight = floorMaxHeightMap.get(floorName) > EL_EFLOORH ? floorMaxHeightMap.get(floorName) : EL_EFLOORH;
					floorMaxHeightMap.put(floorName, maxHeight);
				}else {
					floorMaxHeightMap.put(floorName, EL_EFLOORH);
				}
			}

			// 최하층 ~ TOP-1 층까지 합산 = 주행거리
			for (int i = 0; i < floorNameList.size() - 1; i++) {
				EL_EHTRH += floorMaxHeightMap.get(floorNameList.get(i));
			}

			// 최상층 층고 = OVER HEAD
			EL_EHO = floorMaxHeightMap.get(floorNameList.get(floorNameList.size()-1));

			// 견적용 - 기존층고 치환
			for (int i = 0; i < floorNameList.size(); i++) {
				int EL_EFLOORH = floorMaxHeightMap.get(floorNameList.get(i));
				result.put("EL_EFH" + String.format("%02d", i + 1), EL_EFLOORH);
			}
		}

		// TOTAL HEIGHT = PIT + 주행거리 + OVER HEAD
		EL_EHTH = EL_EHP + EL_EHTRH + EL_EHO;

		result.put("EL_EHTRH", String.valueOf(EL_EHTRH));
		result.put("EL_EHO", String.valueOf(EL_EHO));
		result.put("EL_EHTH", String.valueOf(EL_EHTH));

		return result;
	}

	public BlockVariantMap DUTY_FLOORQ(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		BlockFloorInfoHandler floor = new BlockFloorInfoHandler();

		for(int i=0; i < 7; i++)
		{
			String EL_EFLOORH = BlockUtil.NVL(elvEnt.get("EL_EFLOORH" + Integer.toString(i)),""); //층고
			String EL_AFFT = BlockUtil.NVL(elvEnt.get("EL_AFFT" + Integer.toString(i)),""); //FRONT
			String EL_ARFT = BlockUtil.NVL(elvEnt.get("EL_ARFT" + Integer.toString(i)),""); //REAR
			String EL_EFLOORQ = BlockUtil.NVL(elvEnt.get("EL_EFLOORQ" + Integer.toString(i)),""); //REAR
			   boolean isOversee = false;
			      String CALLSYS =  BlockUtil.NVL((String) elvEnt.get("CALLSYS"),""); // 호기번호
			     
			  	if(CALLSYS.equals("E")) 
					isOversee = true;
			  	
			EL_EFLOORH = "N".equals(EL_EFLOORH) ? "" : EL_EFLOORH;
			EL_AFFT = "N".equals(EL_AFFT) ? "" : EL_AFFT;
			EL_ARFT = "N".equals(EL_ARFT) ? "" : EL_ARFT;
			EL_EFLOORQ = "N".equals(EL_EFLOORQ) ? "" : EL_EFLOORQ;

			if (EL_EFLOORH.equals("") && EL_EFLOORQ.equals("") && EL_AFFT.equals("") && EL_ARFT.equals(""))
				continue;

			ArrayList EL_AFFT_list = new ArrayList();
			ArrayList EL_ARFT_list = new ArrayList();

			if (!EL_AFFT.equals("")) {
				EL_AFFT_list = floor.get_EL_AFT_List(EL_AFFT,isOversee);
				result.put("EL_EFLOORQ" + Integer.toString(i), EL_AFFT_list.size());
			} else if (!EL_ARFT.equals("")) {
				EL_ARFT_list = floor.get_EL_AFT_List(EL_ARFT,isOversee);
				result.put("EL_EFLOORQ" + Integer.toString(i), EL_ARFT_list.size());
			} else {
				result.put("EL_EFLOORQ" + Integer.toString(i), "");
			}

		}



		return result;
	}
	public BlockVariantMap CALC_FLOOR_LIST(Map elvEnt,  List<Map> floorMasterList, Map partInfo) {
		BlockVariantMap result = new BlockVariantMap();
		int EL_EHTRH = 0;
		int EL_EHO = 0;
		int EL_EHTH = 0;
		int i = 1;

		try {
			int EL_EHP = BlockUtil.parseInt(elvEnt.get("EL_EHP"));
	
			if (floorMasterList != null && floorMasterList.size() >= 2) {
	
				// FULL 관통은 중복된 층이 있을 수 있음. 중복된 층은 둘 중 더 높은 층고만 남기고 나머지 버림.
				//FL_F{n}	FL_R{n}	FL_H{n}	FL_L{n}	FL_S{n}
				//층표기(FRONT)	층표기(REAR)	층고	층위치	층구분
	
				List<String> floorNameList = new ArrayList<String>();
				Map<String, Integer> floorMaxHeightMap = new HashMap<String, Integer>();
				Integer V_FLOOR_ETC_Q = 0;
				Integer V_FLOOR_UNDERMAIN_Q = 0;
				for (Map floorInfo : floorMasterList) {
					String floorName = (String) floorInfo.get("FLOOR_NAME");
					int EL_EFLOORH = BlockUtil.parseInt(floorInfo.get("EL_EFLOORH"));
					String EL_AFFT = BlockUtil.NVL(floorInfo.get("EL_AFFT"), ""); //FRONT 층표기
					String EL_ARFT = BlockUtil.NVL(floorInfo.get("EL_ARFT"), ""); //REAR 층표기
					String EL_EFPO = BlockUtil.NVL(floorInfo.get("EL_EFPO"), ""); //층위치
					
					// 신규 추가
					String EL_CMDL = BlockUtil.NVL(floorInfo.get("EL_CMDL"), "");
					String EL_EFLOORHA = BlockUtil.NVL(floorInfo.get("EL_EFLOORHA"), "");
					String EL_EFLOORQ = BlockUtil.NVL(floorInfo.get("EL_EFLOORQ"), "");
					String EL_EFT = BlockUtil.NVL(floorInfo.get("EL_EFT"), "");
					String EL_CJAMBT = BlockUtil.NVL(floorInfo.get("EL_CJAMBT"), "");
					String EL_CJAMBM = BlockUtil.NVL(floorInfo.get("EL_CJAMBM"), "");
					String EL_CJAMBC = BlockUtil.NVL(floorInfo.get("EL_CJAMBC"), "");
					String EL_CHDM = BlockUtil.NVL(floorInfo.get("EL_CHDM"), "");
					String EL_CHDOD = BlockUtil.NVL(floorInfo.get("EL_CHDOD"), "");
					String EL_CHDC = BlockUtil.NVL(floorInfo.get("EL_CHDC"), "");
					String EL_CHDET = BlockUtil.NVL(floorInfo.get("EL_CHDET"), "");
					String EL_CHDFR = BlockUtil.NVL(floorInfo.get("EL_CHDFR"), "");
					String EL_CHDGD = BlockUtil.NVL(floorInfo.get("EL_CHDGD"), "");
					String EL_CHPB = BlockUtil.NVL(floorInfo.get("EL_CHPB"), "");
					String EL_CHPBM = BlockUtil.NVL(floorInfo.get("EL_CHPBM"), "");
					String EL_CHPBMA = BlockUtil.NVL(floorInfo.get("EL_CHPBMA"), "");
					String EL_CHPIT = BlockUtil.NVL(floorInfo.get("EL_CHPIT"), "");
					String EL_CHSM = BlockUtil.NVL(floorInfo.get("EL_CHSM"), "");
					String EL_DHSE = BlockUtil.NVL(floorInfo.get("EL_DHSE"), "");
					String EL_CHLCDT = BlockUtil.NVL(floorInfo.get("EL_CHLCDT"), "");
					String EL_CHLT = BlockUtil.NVL(floorInfo.get("EL_CHLT"), "");
					String EL_CHCARD = BlockUtil.NVL(floorInfo.get("EL_CHCARD"), "");
					String EL_EJMJD = BlockUtil.NVL(floorInfo.get("EL_EJMJD"), "");
					String EL_EJMMH = BlockUtil.NVL(floorInfo.get("EL_EJMMH"), "");
					String EL_CHDAD = BlockUtil.NVL(floorInfo.get("EL_CHDAD"), "");
					
					if ("MAIN".equals(EL_EFPO) || "UNDER".equals(EL_EFPO))
						V_FLOOR_UNDERMAIN_Q++;
					else if ("ETC".equals(EL_EFPO))
						V_FLOOR_ETC_Q++;
	
					result.put("FL_S"+i, "중간층");
					result.put("FL_H"+i, EL_EFLOORH);
					result.put("FL_F"+i, EL_AFFT);
					result.put("FL_R"+i, EL_ARFT);
					result.put("FL_L"+i, EL_EFPO);
					
					if(i==1) {
						result.put("FL_S"+i, "최하층");
						result.put("FL_H"+i, EL_EFLOORH);
						result.put("FL_F"+i, EL_AFFT);
						result.put("FL_R"+i, EL_ARFT);
						result.put("FL_L"+i, EL_EFPO);
					}
					
					// 신규 추가
					result.put("FE_EL_AFFT"+i, EL_AFFT);
					result.put("FE_EL_ARFT"+i, EL_ARFT);
					result.put("FE_EL_CMDL"+i, EL_CMDL);
					result.put("FE_EL_EFLOORH"+i, EL_EFLOORH);
					result.put("FE_EL_EFLOORHA"+i,EL_EFLOORHA);
					result.put("FE_EL_EFLOORQ"+i, EL_EFLOORQ);
					result.put("FE_EL_EFPO"+i, EL_EFPO);
					result.put("FE_EL_EFT"+i, EL_EFT);
					result.put("FE_EL_CJAMBT"+i, EL_CJAMBT);
					result.put("FE_EL_CJAMBM"+i, EL_CJAMBM);
					result.put("FE_EL_CJAMBC"+i, EL_CJAMBC);
					result.put("FE_EL_CHDM"+i, EL_CHDM);
					result.put("FE_EL_CHDOD"+i, EL_CHDOD);
					result.put("FE_EL_CHDC"+i, EL_CHDC);
					result.put("FE_EL_CHDET"+i, EL_CHDET);
					result.put("FE_EL_CHDFR"+i, EL_CHDFR);
					result.put("FE_EL_CHDGD"+i, EL_CHDGD);
					result.put("FE_EL_CHPB"+i, EL_CHPB);
					result.put("FE_EL_CHPBM"+i, EL_CHPBM);
					result.put("FE_EL_CHPBMA"+i, EL_CHPBMA);
					result.put("FE_EL_CHPIT"+i, EL_CHPIT);
					result.put("FE_EL_CHSM"+i, EL_CHSM);
					result.put("FE_EL_DHSE"+i, EL_DHSE);
					result.put("FE_EL_CHLCDT"+i, EL_CHLCDT);
					result.put("FE_EL_CHLT"+i, EL_CHLT);
					result.put("FE_EL_CHCARD"+i, EL_CHCARD);
					result.put("FE_EL_EJMJD"+i, EL_EJMJD);
					result.put("FE_EL_EJMMH"+i, EL_EJMMH);
					result.put("FE_EL_CHDAD"+i, EL_CHDAD);
	
					i++;
				}
				
				result.put("FL_S"+floorMasterList.size(), "최상층");
				result.put("V_FLOOR_ETC_Q", V_FLOOR_ETC_Q.toString());
				result.put("V_FLOOR_UNDERMAIN_Q", V_FLOOR_UNDERMAIN_Q.toString());
	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			result.put("ERRMSG",e.getMessage());
		}

		return result;
	}

	public BlockVariantMap FIRST_VERIFY(Map<String, String> elvEnt,  List<Map> floorMasterList, Map partInfo)  throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String ERRMSG = "";

		//건물 층 표기, 층고, 층수,
		String EL_ATF = (String)elvEnt.get("EL_ATF");
		String FLOOR_VERIFY = BlockUtil.NVL(elvEnt.get("FLOOR_VERIFY"),"");


		BlockFloorInfoHandler floor = new BlockFloorInfoHandler();

		ArrayList<String> el_efloorh_list  = new ArrayList();
		ArrayList<ArrayList> totalFloorData  = new ArrayList();
		ArrayList<String> totalFloorDataSize  = new ArrayList();
		ArrayList<String> resultList =new ArrayList<String>();
		ArrayList EL_AFFT_list = new ArrayList();
		ArrayList EL_ARFT_list = new ArrayList();

		String EL_AFQ = BlockUtil.NVL(elvEnt.get("EL_AFQ"),"");
		Pattern pa = Pattern.compile("[0-9,\\*]*");
		   boolean isOversee = false;
		      String CALLSYS =  BlockUtil.NVL((String) elvEnt.get("CALLSYS"),""); // 호기번호
		     
		  	if(CALLSYS.equals("E")) 
				isOversee = true;

		ArrayList<String> floor_ATF_List = floor.get_EL_AFT_List(EL_ATF,isOversee);
		if(!"F".equals(FLOOR_VERIFY)) {
			try {
				for(int i=0; i < 7; i++)
				{
					String EL_EFLOORH = BlockUtil.NVL(elvEnt.get("EL_EFLOORH" + Integer.toString(i)),""); //층고
					String EL_AFFT = BlockUtil.NVL(elvEnt.get("EL_AFFT" + Integer.toString(i)),""); //FRONT 층표기
					String EL_ARFT = BlockUtil.NVL(elvEnt.get("EL_ARFT" + Integer.toString(i)),""); //REAR 층표기
					String EL_EFLOORQ =  BlockUtil.NVL(elvEnt.get("EL_EFLOORQ" + Integer.toString(i)),""); //적용수량
					Matcher ma = pa.matcher(EL_EFLOORH);


					if(EL_EFLOORH.equals("") && EL_AFFT.equals("") && EL_ARFT.equals(""))
						continue;


					String indx = (i==0) ? "CP" : Integer.toString(i);

					if(EL_EFLOORH.equals(""))
					{
						result.put("ERRMSG", "승장사양("+indx+")의 층고표기가 입력되지 않았습니다.##EL_EFLOORH"+Integer.toString(i));
						return result;

					}
					if(!EL_EFLOORH.equals("") && (EL_AFFT.equals("") && EL_ARFT.equals("")))
					{
						result.put("ERRMSG", "승장사양("+indx+")의 총표기가 입력되지 않았습니다.##EL_EFLOORH"+Integer.toString(i));
						return result;

					}
					if(EL_EFLOORH.equals("") && ((!EL_AFFT.equals("") && EL_ARFT.equals("")) || ((EL_AFFT.equals("") && !EL_ARFT.equals("")))))
					{
						result.put("ERRMSG", "승장사양("+indx+")의 층고 표기가 입력되지 않았습니다.##EL_AFFT"+Integer.toString(i));
						return result;

					}
					if(!ma.matches())
					{
						result.put("ERRMSG", "승장사양("+indx+")의 총고 표기가 올바르지 않습니다.##EL_EFLOORH"+Integer.toString(i));
						return result;

					}
					if(!EL_EFLOORH.equals("") && !EL_EFLOORQ.equals(""))
					{
						if(EL_AFFT.isEmpty() && EL_ARFT.isEmpty())
						{
							result.put("ERRMSG", "승장사양("+indx+")의 층표기가 FRONT, REAR 입력이 되어있지 않습니다.##EL_AFFT"+Integer.toString(i));
							return result;

						} 
						if(!EL_AFFT.isEmpty() && !EL_ARFT.isEmpty())
						{
							result.put("ERRMSG", "승장사양("+indx+")의 층표기가 FRONT, REAR 동시에 입력되어 있습니다.##EL_AFFT"+Integer.toString(i));
							return result;
						}
					}

					el_efloorh_list = floor.get_el_efloorh_list(EL_EFLOORH);
					//int num = Integer.parseInt(EL_EFLOORQ);
					if(!EL_AFFT.equals(""))
					{
						EL_AFFT_list =  floor.get_EL_AFT_List(EL_AFFT);
						totalFloorData.add(EL_AFFT_list);
					}
					else
					{
						EL_ARFT_list =  floor.get_EL_AFT_List(EL_ARFT);
						totalFloorData.add(EL_ARFT_list);
					}

					if(EL_AFFT_list.size() > 0 && el_efloorh_list.size() > 1)
					{
						if(EL_AFFT_list.size() != el_efloorh_list.size()) {
							result.put("ERRMSG","승장사양("+indx+")의 층표기 개수와 층고의 개수가 일치 하지 않습니다. 층표기 : "+EL_AFFT+"층고 : "+EL_EFLOORH+"##EL_AFFT"+Integer.toString(i));
						}
					}
					else if(EL_ARFT_list.size() > 0 && el_efloorh_list.size() > 1)
					{
						if(EL_ARFT_list.size() != el_efloorh_list.size())
						{
							result.put("ERRMSG","승장사양("+indx+")의 층표기 개수와 층고의 개수가 일치 하지 않습니다. 층표기 : "+EL_ARFT+"층고 : "+EL_EFLOORH+"##EL_ARFT"+Integer.toString(i));

						}
					}

				}

				//층표기에는 건물층 표기에도 있어야함
				for(ArrayList idx : totalFloorData)
				{
					for(int i=0; i < idx.size(); i++)
					{
						totalFloorDataSize.add((String)idx.get(i));
					}
				}

				//FRONT, REAR 중복 제거
				for (int i =0; i < totalFloorDataSize.size(); i++) {
					if (!resultList.contains(totalFloorDataSize.get(i))) {
						resultList.add(totalFloorDataSize.get(i));
					}
				}
				int EL_AFQ_SIZE = Integer.parseInt(EL_AFQ);

				Iterator itr = resultList.iterator();
				while(itr.hasNext())
				{
					int floorCnt = 0;
					String floor_ATF  = (String)itr.next();
					for(String idx : floor_ATF_List) // floor_ATF_List 1~8, resultList1~9
					{
						if(floor_ATF.equals(idx))
							floorCnt++;
					}
					if(floorCnt == 0) {
						result.put("ERRMSG","건물 층표기 입력을 다시 확인해주세요. 건물의 전체 층을 모두 표기해야 합니다.##EL_EFLOORH"+EL_AFQ);

					}

				}
			}catch(Exception e) {
				e.printStackTrace();
				result.put("ERRMSG",e.getMessage());
			}
		}

		return result;
	}

	public BlockVariantMap CALC_EXQ_EXPAND(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();

		String EL_DPEXQ = (String) elvEnt.get("EL_DPEXQ");
		if(!"".equals(EL_DPEXQ) && !"N".equals(EL_DPEXQ)) {
			if(EL_DPEXQ.matches("[0-9,]*")) {
				String[] EL_DPEXQs = EL_DPEXQ.split(",");
				if(EL_DPEXQs.length<=3) {
					for(int i=0; i<EL_DPEXQs.length; i++) {
						result.put("VAR_EL_DPEXQ"+(1+i), EL_DPEXQs[i]);
					}
				}else {
					result.put("CALC_EXQ_EXPAND_ERR_KR", "교환기연결대수 입력 부적합. 최대 3개만 입력가능");
					result.put("CALC_EXQ_EXPAND_ERR_EN", "Exchanger Input Invalid. Only up to 3 can be entered.");
				}
			}
			else {
				result.put("CALC_EXQ_EXPAND_ERR_KR", "교환기연결대수 입력 부적합. 숫자와 콤마(,)로만 입력.");
				result.put("CALC_EXQ_EXPAND_ERR_EN", "Exchanger Input Invalid. only number and comma(,) available.");
			}
		}

		return result;
	}

	public BlockVariantMap AUTO_DESIGNMANAGER(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();

		if("T".equals(elvEnt.get("IS_COST"))){
			result.put("DESIGNUSER", elvEnt.get("USER_ID"));
			result.put("DESIGNOPT", "BOTH");
			return result;
		}

		try {

			// 원본은 로그인 사용자(AUS) 기준. 독립 실행시에는 -Dblock.userId 로 지정한 사용자 (없으면 결과 없음)
			String userId = System.getProperty("block.userId");
			if (BlockUtil.isNullString(userId))
				return result;

			Map<String, String> userObject = ctx.getDb().queryForFirst(
					" select (select des from doscoditm where ouid=designpart) designpart, md$number, email, md$desc from fuser$sf where md$number=? ", userId);
			if (userObject == null)
				return result;

			String userName = BlockUtil.NVL(userObject.get("MD$DESC"),"");
			String designpart = BlockUtil.NVL(userObject.get("DESIGNPART"),"");

			result.put("DESIGNUSER", userName);
			result.put("DESIGNOPT", designpart);

		}catch(Exception e) {
//			e.printStackTrace();
		}

		return result;

	}

	public BlockVariantMap AUTO_FIRSTDESIGNDATE(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		List<String> plmBlockData = new ArrayList();

		try {

			String hogiNum = BlockUtil.NVL(elvEnt.get("md$number"),"");
			String sujuNum = "";
			sujuNum = hogiNum.substring(0,6);
			if(hogiNum.contains("-")) {
				int hyphen_idx = hogiNum.indexOf("-");
				sujuNum = hogiNum.substring(0,hyphen_idx);
			}
			Map<String, String> plmBomData = null;
			// JdbcTemplate.queryForMap : 0건이면 null, 2건 이상이면 예외
			List<Map<String, String>> rows = ctx.getDb().queryForList(
					" SELECT MD$NUMBER, EL_ZFDA, EL_ZFDB, EL_ZFDC, EL_ZFDD from ABENGBYSALES$SF where MD$NUMBER = ? ", sujuNum);
			if (rows.size() > 1)
				throw new Exception("Incorrect result size: expected 1, actual " + rows.size());
			if (rows.size() == 1)
				plmBomData = rows.get(0);

			if (plmBomData != null) {
				result.put("O_ZFDA", BlockUtil.NVL(plmBomData.get("EL_ZFDA"),"0"));
				result.put("O_ZFDB", BlockUtil.NVL(plmBomData.get("EL_ZFDB"),"0"));
				result.put("O_ZFDC", BlockUtil.NVL(plmBomData.get("EL_ZFDC"),"0"));
				result.put("O_ZFDD", BlockUtil.NVL(plmBomData.get("EL_ZFDD"),"0"));
			}


		}catch(Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public BlockVariantMap GET_OLD_SALES_AND_CONVERT(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();


		for(Iterator iterator = elvEnt.entrySet().iterator(); iterator.hasNext(); ) {
			Entry next = (Entry) iterator.next();
			result.put(next.getKey(), "");
		}

		if(elvEnt.containsKey("ESTIMATE_NO")) {
			String estimateNo = elvEnt.get("ESTIMATE_NO").toString();

			String[] tokens = estimateNo.split("-");

			if(tokens.length == 3) {
				String qtnum = null;
				int qtser = 0;
				int qtseq = 0;

				boolean isValidNo = true;

				try {
					qtnum = tokens[0];
					qtser = Integer.parseInt(tokens[1]);
					qtseq = Integer.parseInt(tokens[2]);
				} catch (NumberFormatException e) {
					result.put("ERRMSG", "견적번호 입력이 부적절함 -> " + e.getMessage());
					isValidNo = false;
				}

				if(isValidNo) {
					// 원본 : salesMigService.getSrmEstimateAndConvert (ECC6 DB + SAP JCo RFC ZCO4_SPEC_CONV) → 독립 실행 불가
					throw new UnsupportedOperationException("GET_OLD_SALES_AND_CONVERT 는 SAP(ECC6/JCo) 연동이 필요하여 독립 실행에서 지원하지 않습니다.");
				}

			}else {
				result.put("ERRMSG", "견적번호 입력이 부적절함 -> "+estimateNo);
			}
		}else {
			result.put("ERRMSG", "견적번호 입력 안됨");
		}

		return result;
	}

	public BlockVariantMap GET_OLD_SALES(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();


		for(Iterator iterator = elvEnt.entrySet().iterator(); iterator.hasNext(); ) {
			Entry next = (Entry) iterator.next();
			result.put(next.getKey(), "");
		}

		if(elvEnt.containsKey("ESTIMATE_NO")) {
			String estimateNo = elvEnt.get("ESTIMATE_NO").toString();

			String[] tokens = estimateNo.split("-");

			if(tokens.length == 3) {
				String qtnum = null;
				int qtser = 0;
				int qtseq = 0;

				boolean isValidNo = true;

				try {
					qtnum = tokens[0];
					qtser = Integer.parseInt(tokens[1]);
					qtseq = Integer.parseInt(tokens[2]);
				} catch (NumberFormatException e) {
					result.put("ERRMSG", "견적번호 입력이 부적절함 -> " + e.getMessage());
					isValidNo = false;
				}

				if(isValidNo) {
					// 원본 : erpService.getSrmEstimateSales (ECC6 DB2 saphee.zsdt1048) → 독립 실행 불가
					throw new UnsupportedOperationException("GET_OLD_SALES 는 SAP ECC6 DB 연동이 필요하여 독립 실행에서 지원하지 않습니다.");
				}

			}else {
				result.put("ERRMSG", "견적번호 입력이 부적절함 -> "+estimateNo);
			}
		}else {
			result.put("ERRMSG", "견적번호 입력 안됨");
		}

		return result;
	}


	public BlockVariantMap FLOOR_EXPANDABLE_CHECK_JAVA(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();

		Map<String, String> FloorNameAndQty = new LinkedHashMap<String, String>();
		FloorNameAndQty.put("EL_AFF", "EL_AFFQ");
		FloorNameAndQty.put("EL_ARF", "EL_ARFQ");
		FloorNameAndQty.put("EL_ANST", "EL_ANSTQ");
		FloorNameAndQty.put("EL_CHD1F", "EL_CHD1Q");
		FloorNameAndQty.put("EL_CHD2F", "EL_CHD2Q");
		FloorNameAndQty.put("EL_CHD3F", "EL_CHD3Q");
		FloorNameAndQty.put("EL_CJM1F", "EL_CJM1Q");
		FloorNameAndQty.put("EL_CJM2F", "EL_CJM2Q");
		FloorNameAndQty.put("EL_CJM3F", "EL_CJM3Q");
		FloorNameAndQty.put("EL_CJM4F", "EL_CJM4Q");
		FloorNameAndQty.put("EL_CHIPBF", "EL_CHIPBQ");
		FloorNameAndQty.put("EL_CHIPM1F", "EL_CHIPM1Q");
		FloorNameAndQty.put("EL_CHIPM2F", "EL_CHIPM2Q");
		FloorNameAndQty.put("EL_CHIPTF", "EL_CHIPTQ");
		FloorNameAndQty.put("EL_CHL2F", "EL_CHL2Q");
		FloorNameAndQty.put("EL_CHLCDF", "EL_CHLCDQ");
		FloorNameAndQty.put("EL_CHLF", "EL_CHLQ");
		FloorNameAndQty.put("EL_CHPBBF", "EL_CHPBBQ");
		FloorNameAndQty.put("EL_CHPBM1F", "EL_CHPBM1Q");
		FloorNameAndQty.put("EL_CHPBM2F", "EL_CHPBM2Q");
		FloorNameAndQty.put("EL_CHPBTF", "EL_CHPBTQ");
		FloorNameAndQty.put("EL_CHCARF", "EL_CHCRDQ");

		Map<String, String> FloorQtyAndSpec = new LinkedHashMap<String, String>();
		FloorQtyAndSpec.put("EL_CHD1Q", "EL_CHD1");
		FloorQtyAndSpec.put("EL_CHD2Q", "EL_CHD2");
		FloorQtyAndSpec.put("EL_CHD3Q", "EL_CHD3");
		FloorQtyAndSpec.put("EL_CJM1Q", "EL_CJM1");
		FloorQtyAndSpec.put("EL_CJM2Q", "EL_CJM2");
		FloorQtyAndSpec.put("EL_CJM3Q", "EL_CJM3");
		FloorQtyAndSpec.put("EL_CJM4Q", "EL_CJM4");
		FloorQtyAndSpec.put("EL_CHIPBQ", "EL_CHIPB");
		FloorQtyAndSpec.put("EL_CHIPM1Q", "EL_CHIPM1");
		FloorQtyAndSpec.put("EL_CHIPM2Q", "EL_CHIPM2");
		FloorQtyAndSpec.put("EL_CHIPTQ", "EL_CHIPT");
		FloorQtyAndSpec.put("EL_CHPBBQ", "EL_CHPBB");
		FloorQtyAndSpec.put("EL_CHPBM1Q", "EL_CHPBM1");
		FloorQtyAndSpec.put("EL_CHPBM2Q", "EL_CHPBM2");
		FloorQtyAndSpec.put("EL_CHPBTQ", "EL_CHPBT");
		FloorQtyAndSpec.put("EL_CHLQ", "EL_CHL");
		FloorQtyAndSpec.put("EL_CHL2Q", "EL_CHL2");
		FloorQtyAndSpec.put("EL_CHLCDQ", "EL_CHLCD");
		FloorQtyAndSpec.put("EL_CHCRDQ", "EL_CHCRD");


		String hogi = BlockUtil.NVL(elvEnt.get("HOGI"),"");
		String EL_ATYP = BlockUtil.NVL(elvEnt.get("EL_ATYP"),"");

		try {

			// NEX는 skip
			if ("NEX_MR".equals(elvEnt.get("EL_ABRAND")) || "NEX_MRL".equals(elvEnt.get("EL_ABRAND"))) {
				result.put("FLOOR_EXPANDABLE", "F");
				result.put("FLOOR_EXPAND_MSG", "NEX 매핑 안함");
				result.put("FLOOR_EXPAND_FIXABLE", "F");
				result.put("FLOOR_EXPAND_ISTARGET", "F");
				return result;
			}

			// 엘리베이터 신규영업 제품만 실행
			if (!hogi.matches(".*(NS|NB|NC).*")) {
				if (EL_ATYP == null || "".equals(EL_ATYP)
						|| "null".equals(EL_ATYP)) {

					result.put("FLOOR_EXPANDABLE", "F");
					result.put("FLOOR_EXPAND_MSG", "기종 누락");
					result.put("FLOOR_EXPAND_FIXABLE", "F");
					result.put("FLOOR_EXPAND_ISTARGET", "F");
					return result;
				}
			}

			// 선박용은 skip
			if (EL_ATYP.contains("SH")) {
				result.put("FLOOR_EXPANDABLE", "F");
				result.put("FLOOR_EXPAND_MSG", "선박용 매핑안함");
				result.put("FLOOR_EXPAND_FIXABLE", "F");
				result.put("FLOOR_EXPAND_ISTARGET", "F");
				return result;
			}

			result.put("FLOOR_EXPAND_ISTARGET", "T");

			boolean isOversee = false;
			String QTNUM = BlockUtil.NVL(elvEnt.get("QTNUM"), "");
			String CALLSYS = BlockUtil.NVL(elvEnt.get("CALLSYS"), "");
			String HOGI = BlockUtil.NVL(elvEnt.get("HOGI"), "");

			if(CALLSYS.equals("E")) 
				isOversee = true;

			
			boolean doStrict = true;
			String CO_QTDAT = BlockUtil.NVL(elvEnt.get("CO_QTDAT"), "1");
			try {
				doStrict = BlockUtil.parseInt(CO_QTDAT) > 20220318;
			}catch(Exception e) {
			}


 
			Set<String> allFloor = new LinkedHashSet<String>();
 
			for (String spec : Arrays.asList("EL_AFF", "EL_ARF", "EL_ANST")) {
				List<String> expandsFloor = null;
				String value = BlockUtil.NVL(elvEnt.get(spec),"").trim();

				if (value == null || "".equals(value))
					continue;

				value = value.trim();
				try {
					expandsFloor = expandsFloor(value, isOversee, doStrict);
				} catch (Exception e) {
					result.put("FLOOR_EXPANDABLE", "F");
					result.put("FLOOR_EXPAND_FIXABLE", "T");
					result.put("FLOOR_EXPAND_MSG", "입력오류로 층전개 불가 " + BlockUtil.getDosfldTitle(spec) + "=" + value);
					result.put("ERRMSG_KR", "층입력규칙에 위배됨 : "+e.getMessage()+"##"+spec);
					result.put("ERRMSG_EN", "Floor Input is invalid##"+spec);
					return result;
				}

				for (String floor : expandsFloor)
					allFloor.add(floor);

				result.put("FLOOR_ALL", allFloor.toString());
			}



			// 추가 전개 불가능한 케이스 검출
			{
				try {
					String EL_CHS2 = BlockUtil.NVL(elvEnt.get("EL_CHS2"), "").trim();

					if (!"".equals(EL_CHS2)) {
						result.put("FLOOR_EXPANDABLE", "F");
						result.put("FLOOR_EXPAND_FIXABLE", "F");
						result.put("FLOOR_EXPAND_MSG", "SILL2가 같이 사용되어 전개 불가");
						return result;
					}


					{
						String EL_ETHRU = BlockUtil.NVL(elvEnt.get("EL_ETHRU"), "").trim();
						boolean isDuplicatedFloor = false;
						String duplicatedFloor = "";
						List<String> frontFloorList = expandsFloor(BlockUtil.NVL(elvEnt.get("EL_AFF"), ""), isOversee, doStrict);
						List<String> rearFloorList = expandsFloor(BlockUtil.NVL(elvEnt.get("EL_ARF"), ""), isOversee, doStrict);

						for(String rearFloor : rearFloorList) {
							if(rearFloor != null) {
								rearFloor = rearFloor.trim();
								if(!"".equals(rearFloor) && frontFloorList.contains(rearFloor)) {
									isDuplicatedFloor = true;
									duplicatedFloor = rearFloor;
								}
							}
						}

						if(isDuplicatedFloor) {
							if("Y".equals(EL_ETHRU)) {
								result.put("FLOOR_EXPANDABLE", "F");
								result.put("FLOOR_EXPAND_FIXABLE", "F");
								result.put("FLOOR_EXPAND_MSG", "FULL관통은 전개 불가");
								return result;
							}else {
								result.put("FLOOR_EXPANDABLE", "F");
								result.put("FLOOR_EXPAND_FIXABLE", "T");
								result.put("FLOOR_EXPAND_MSG", "관통이 아님에도 중복된 층이 FRONT, REAR에 존재함 : "+duplicatedFloor+"층");
								result.put("ERRMSG_KR", "관통이 아님에도 중복된 층이 FRONT, REAR에 존재함 : "+duplicatedFloor+"층##EL_ARF");
								result.put("ERRMSG_EN", "duplicated floor FRONT/REAR : "+duplicatedFloor+"##EL_ARF");
								return result;
							}
						}
					}

					{ // 풀관통이 아닌데 동일층이 표기됨.
						for(Iterator<Entry<String, String>> iterator = FloorNameAndQty.entrySet().iterator(); iterator.hasNext();) {
							Entry<String, String> item = iterator.next();
							String floorDescription = BlockUtil.NVL(elvEnt.get(item.getKey()), "");
							List<String> floorList = expandsFloor(floorDescription, isOversee, doStrict);

							if(floorList.stream().distinct().count() != floorList.size()) {
								result.put("FLOOR_EXPANDABLE", "F");
								result.put("FLOOR_EXPAND_FIXABLE", "T");
								result.put("FLOOR_EXPAND_MSG", "동일한 층이 표기됨 "+BlockUtil.getDosfldTitle(item.getKey())+"="+floorDescription);
								result.put("ERRMSG_KR", "동일한 층이 표기됨 : "+floorDescription+"##"+item.getKey());
								result.put("ERRMSG_EN", "duplicated floor : "+floorDescription+"##"+item.getKey());

								return result;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


			boolean isNormalInput = true;
			for (String spec : Arrays.asList("EL_AFF","EL_ARF","EL_ANST","EL_CHCARF", "EL_CHD1F", "EL_CHD2F", "EL_CHD3F", "EL_CHIPBF", "EL_CHIPM1F",
					"EL_CHIPM2F", "EL_CHIPTF", "EL_CHL2F", "EL_CHLCDF", "EL_CHLF", "EL_CHPBBF", "EL_CHPBM1F",
					"EL_CHPBM2F", "EL_CHPBTF", "EL_CJM1F", "EL_CJM2F", "EL_CJM3F", "EL_CJM4F")) {
				String value = BlockUtil.NVL(elvEnt.get(spec),"").trim();
				value = (value != null) ? value.trim() : "";
				String pairKey = (String) FloorNameAndQty.get(spec);
				String pairVal = BlockUtil.NVL(elvEnt.get(pairKey),"").trim();

				if (value == null || "".equals(value)) {
					if (pairVal != null && !"".equals(pairVal) && !"0".equals(pairVal) && !"null".equals(pairVal)) {
						result.put("FLOOR_EXPANDABLE", "F");
						result.put("FLOOR_EXPAND_FIXABLE", "T");
						result.put("FLOOR_EXPAND_MSG", "적용수량있으나 적용층 입력 안함 : " + BlockUtil.getDosfldTitle(pairKey) + "=" + pairVal);
						result.put("ERRMSG_KR", "적용수량있으나 적용층 입력 안함. 적용수량="+pairVal+"##"+spec);
						result.put("ERRMSG_EN", "FLOOR NO is required when FLOOR Qty entered. Floor qty="+pairVal+"##"+spec);
						return result;
					} else {
						continue;
					}
				}else {
					List<String> floorList = null;
					try {
						floorList = expandsFloor(value, isOversee, doStrict);
					} catch (Exception e) {
						isNormalInput = false;
						result.put("FLOOR_EXPANDABLE", "F");
						result.put("FLOOR_EXPAND_FIXABLE", "T");
						result.put("FLOOR_EXPAND_MSG", "입력오류로 층전개 불가 : " + BlockUtil.getDosfldTitle(spec) + "=" + value);
						result.put("ERRMSG_KR", "층입력규칙에 위배됨 : "+e.getMessage()+"##"+spec);
						result.put("ERRMSG_EN", "Floor Input is invalid##"+spec);
						return result;
					}

					for (String floor : floorList) {
						if (!allFloor.contains(floor)) {
							result.put("FLOOR_EXPANDABLE", "F");
							result.put("FLOOR_EXPAND_FIXABLE", "T");
							result.put("FLOOR_EXPAND_MSG", floor + "층 없음 " + BlockUtil.getDosfldTitle(spec) + "=" + value);
							result.put("ERRMSG_KR", floor+" 층은 이 호기에 존재 하지 않음. 정확한 층 표기바람##"+spec);
							result.put("ERRMSG_EN", floor+" is not exist. please input specific floor name##"+spec);
							return result;
						}
					}

					if(floorList.size() != BlockUtil.parseInt(pairVal)) {
						result.put("FLOOR_EXPANDABLE", "F");
						result.put("FLOOR_EXPAND_FIXABLE", "T");
						result.put("FLOOR_EXPAND_MSG", BlockUtil.getDosfldTitle(spec)+" 적용층/적용수량 불일치. 적용층="+floorList.size()+"개층 입력됨 / 적용수량="+pairVal);
						result.put("ERRMSG_KR", "적용층/적용수량 불일치. 적용층="+floorList.size()+"개층 입력됨 / 적용수량="+pairVal+"##"+spec);
						result.put("ERRMSG_EN", "FLOOR and QTY are inconsistent. Floor No="+floorList.size()+" / Floor Qty="+pairVal+"##"+spec);
						return result;
					}
				}
			}


			for(Iterator<Entry<String, String>> it = FloorQtyAndSpec.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> next = it.next();
				String qtyName = next.getKey();
				String specName = next.getValue();

				int qty = BlockUtil.parseInt(elvEnt.get(qtyName));
				String specValue = BlockUtil.NVL(elvEnt.get(specName),"").trim();

				boolean isQtyExists = qty > 0;
				boolean isSpecExists = (null != specValue && !"".equals(specValue) && !"null".equals(specValue) && !"N".equals(specValue));

				if(isQtyExists == true && isSpecExists == false) {
					result.put("FLOOR_EXPANDABLE", "F");
					result.put("FLOOR_EXPAND_FIXABLE", "T");
					result.put("FLOOR_EXPAND_MSG", BlockUtil.getDosfldTitle(qtyName)+" "+"적용수량있으나 사양입력 누락");
					result.put("ERRMSG_KR", "적용수량있으나 사양입력 누락"+"##"+specName);
					result.put("ERRMSG_EN", "input missed##"+specName);
					return result;
				}else if(isQtyExists == false && isSpecExists == true) {
					result.put("FLOOR_EXPANDABLE", "F");
					result.put("FLOOR_EXPAND_FIXABLE", "T");
					result.put("FLOOR_EXPAND_MSG", BlockUtil.getDosfldTitle(specName)+" "+"사양있으나 적용수량 누락");
					result.put("ERRMSG_KR", "사양있으나 적용수량 누락"+"##"+qtyName);
					result.put("ERRMSG_EN", "input missed##"+qtyName);
					return result;
				}
			}


			for(Iterator<Entry<String, String>> it = FloorNameAndQty.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> next = it.next();
				String floorName = next.getKey();
				String floorValue = BlockUtil.NVL(elvEnt.get(floorName), "");
				if(floorValue.trim().matches("[,\\.\\-\\~]")) {
					result.put("FLOOR_EXPANDABLE", "F");
					result.put("FLOOR_EXPAND_FIXABLE", "T");
					result.put("FLOOR_EXPAND_MSG", "입력오류로 층전개 불가 : " + BlockUtil.getDosfldTitle(floorName) + "=" + floorValue);
					result.put("ERRMSG_KR", "층입력규칙에 위배됨 : "+floorValue+"##"+floorName);
					result.put("ERRMSG_EN", "Floor Input is invalid##"+floorName);
					return result;
				}
			}

			if (isNormalInput) {
				result.put("FLOOR_EXPANDABLE", "T");
			}
		} catch (Exception e) {
			result.put("FLOOR_EXPANDABLE", "F");
			result.put("FLOOR_EXPAND_FIXABLE", "T");
			result.put("FLOOR_EXPAND_MSG", e.getMessage());
			result.put("ERRMSG_KR", "오류발생. PLM관리자에게 문의하십시오. : "+e.getMessage());
			result.put("ERRMSG_EN", "error. please contact PLM administrator : "+e.getMessage());
			return result;
		}

		return result;
	}

	public BlockVariantMap CAL_COUNT_EL(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception
	{
		BlockVariantMap drawMap = new BlockVariantMap();

		BlockSpecComparator sc = new BlockSpecComparator(elvEnt);

		if("T".equals(elvEnt.get("IS_COST"))){
			if((sc.c("CO_QTDAT",">20230413") || sc.c("CO_NQTGBDAT",">20230413"))){
				drawMap.put("COUNT_EL",elvEnt.get("CO_ELQTY"));
				return drawMap;
			}
			else{
				drawMap.put("COUNT_EL", null);
				return drawMap;
			}
		}

		String CAL_COUNT_EL = "0";
		try
		{
			drawMap.put("COUNT_EL","");
			String hogiNum = BlockUtil.NVL(elvEnt.get("md$number"),"");
			int L_count = 0;
			int NC_count = 0;
			
			//TEST 호기의 경우 EL_ZORINO (기존설계호기번호) 의 값 사용
			if(hogiNum.contains("TEST")) {
				String imsiHogiNum = getEL_ZORINO(hogiNum);
				if (imsiHogiNum != null && imsiHogiNum.length() > 0) {
					hogiNum = imsiHogiNum;
				}
			}
			
			if (!hogiNum.contains("TEST")) {
				String hogiNum_project_no = hogiNum.substring(0,6);
	
				List<String> hogiList = getCOUNT_ELData(hogiNum_project_no);
	
				for(String md$number : hogiList)
				{
					if(md$number.contains("L"))
					{
						L_count++;
					}
					if(md$number.contains("NC"))
					{
						NC_count++;
					}
				}
	
				if(L_count == 0)
				{
					CAL_COUNT_EL = "0";
				}
				else if(L_count != 0)
				{
					CAL_COUNT_EL = Integer.toString(L_count + NC_count);
				}
			}
			drawMap.put("COUNT_EL",CAL_COUNT_EL);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			drawMap.put("COUNT_EL","CHK");
		}

		return drawMap;
	}

	public BlockVariantMap CALC_WALL_SIZE(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception
	{
		BlockVariantMap resultMap = new BlockVariantMap();
		BlockSpecComparator sc2 = null;
		sc2 = new BlockSpecComparator(elvEnt);
		String EL_ABRAND = BlockUtil.NVL(elvEnt.get("EL_ABRAND"),"");
		String EL_AOPEN = BlockUtil.NVL(elvEnt.get("EL_AOPEN"),"");
		String EL_ETHRU = BlockUtil.NVL(elvEnt.get("EL_ETHRU"),"");
		String EL_ATR = BlockUtil.NVL(elvEnt.get("EL_ATR"),"");
		String EL_BMDL = BlockUtil.NVL(elvEnt.get("EL_BMDL"),"");
		String EL_BTRM = BlockUtil.NVL(elvEnt.get("EL_BTRM"),"");
		String EL_BECM = BlockUtil.NVL(elvEnt.get("EL_BECM"),"");
		String EL_BWALLT = BlockUtil.NVL(elvEnt.get("EL_BWALLT"),"");
		String MANAGER_M = BlockUtil.NVL(elvEnt.get("MANAGER_M"),"");
		String EL_BWMT = BlockUtil.NVL(elvEnt.get("EL_BWMT"),"");

		int EL_ECCB = BlockUtil.parseInt(elvEnt.get("EL_ECCB"));
		double EL_ECJJ = BlockUtil.parseDouble(elvEnt.get("EL_ECJJ"));
		double EL_ECCA = BlockUtil.parseDouble(elvEnt.get("EL_ECCA"));

		/**
		 ** 구분1 ?NYS?,?NYD? NEX 현장 제외
		 **/
		if(!EL_ABRAND.contains("NEX")) {
			if(EL_BMDL.contains("NYS") || EL_BMDL.contains("NYD"))
			{
				double EL_EWM1 = BlockUtil.parseDouble(elvEnt.get("EL_EWM1"));
				double EL_EWM2 = BlockUtil.parseDouble(elvEnt.get("EL_EWM2"));
				double EL_EWM3 = BlockUtil.parseDouble(elvEnt.get("EL_EWM3"));
				double EL_EWM4 = BlockUtil.parseDouble(elvEnt.get("EL_EWM4"));
				double EL_EWM5 = BlockUtil.parseDouble(elvEnt.get("EL_EWM5"));

				/** (1) EL_EWM2 **/
				if(EL_EWM2 == 0)
				{
					if(EL_BWALLT.contains("T19") &&
							(EL_AOPEN.equals("1SCO")) &&
							(EL_ETHRU.equals("N") || EL_ETHRU.equals("")) &&
							((EL_BTRM.equals("N") || EL_BTRM.equals("")) || EL_BTRM.equals("S"))
							&& (EL_ECCA <= 1400))
					{
						EL_EWM2 = 269;
					}
					else if(EL_BWALLT.contains("T19") &&
							(EL_AOPEN.equals("1SCO")) &&
							(EL_ETHRU.equals("N") || EL_ETHRU.equals("")) &&
							((EL_BTRM.equals("N") || EL_BTRM.equals("")) || EL_BTRM.equals("S")) &&
							(EL_ECCA > 1400 && EL_ECCA <= 1600))
					{
						EL_EWM2 = 319;
					}
					else if(EL_BWALLT.contains("T19") &&
							(EL_AOPEN.equals("1SCO")) &&
							(EL_ETHRU.equals("N") || EL_ETHRU.equals("")) &&
							(EL_BTRM.equals("R") || EL_BTRM.equals("A")) &&
							(EL_ECCA <= 1400))
					{
						EL_EWM2 = 239;
					}
					else if(EL_BWALLT.contains("T19") &&
							(EL_AOPEN.equals("1SCO")) &&
							(EL_ETHRU.equals("N") || EL_ETHRU.equals("")) &&
							(EL_BTRM.equals("R") || EL_BTRM.equals("A")) &&
							(EL_ECCA > 1400 && EL_ECCA <= 1540))
					{
						EL_EWM2 = 289;
					}
					else if(EL_BWALLT.contains("T30"))
					{
						EL_EWM2 = 0;
					}
				}

				/** (2) EL_EWM4 **/
				if(EL_EWM4 == 0)
				{
					if((!EL_AOPEN.contains("U")) &&
							((EL_BTRM.equals("N") || EL_BTRM.equals("")) || EL_BTRM.equals("R")) &&
							(EL_ECCB <= 1400))
					{
						EL_EWM4 = 250;
					}
					else if((!EL_AOPEN.contains("U")) &&
							((EL_BTRM.equals("N") || EL_BTRM.equals("")) || EL_BTRM.equals("R")) &&
							(EL_ECCB > 1400 && EL_ECCB <= 1600))
					{
						EL_EWM4 = 300;
					}
					else if((!EL_AOPEN.contains("U")) && (EL_BTRM.equals("S") || EL_BTRM.equals("A")) && (EL_ECCB <= 1400))
					{
						EL_EWM4 = 220;
					}
					else if((!EL_AOPEN.contains("U")) && (EL_BTRM.equals("S") || EL_BTRM.equals("A")) && (EL_ECCB > 1400 && EL_ECCB <= 1540))
					{
						EL_EWM4 = 270;
					}
					else
					{
						EL_EWM4 = 0;
					}
				}

				/** (3) EL_EWM1 **/
				if(EL_EWM1 == 0)
				{
					if(EL_BWALLT.contains("T19") && (EL_EWM2 != 0))
					{
						EL_EWM1 = EL_ECCA - (EL_EWM2 - 19) * 2;
					}
					else if(EL_BWALLT.contains("T30"))
					{
						EL_EWM1 = 0;
					}
				}

				/** (4) EL_EWM3 **/
				if(EL_EWM3 == 0)
				{
					if((!EL_AOPEN.contains("U")) && (EL_EWM4 != 0))
					{
						EL_EWM3 = EL_ECCB - EL_EWM4 * 2;
					}
					else
					{
						EL_EWM3 = 0;
					}
				}

				/** (5) EL_EWM5 **/
				if(EL_EWM5 == 0)
				{
					if(EL_BWALLT.contains("T19")
							&& (EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO")|| EL_AOPEN.equals("2SR")|| EL_AOPEN.equals("2SL")|| EL_AOPEN.equals("2SLR"))
							&& !(EL_BECM.equals("")))
					{
						EL_EWM5 = ((EL_ECCA - EL_ECJJ) / 2) - 11;
					}
					else if(EL_BWALLT.contains("T19")
							&& (EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO")|| EL_AOPEN.equals("2SR")|| EL_AOPEN.equals("2SL")|| EL_AOPEN.equals("2SLR"))
							&& (EL_BECM.equals("")))
					{
						EL_EWM5 = ((EL_ECCA - EL_ECJJ) / 2) + 19;
					}
					else if(EL_BWALLT.contains("T30")
							&& (EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO")||EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
							&& !(EL_BECM.equals("")))
					{
						EL_EWM5 = (EL_ECCA - EL_ECJJ) / 2;
					}
					else if(EL_BWALLT.contains("T30")
							&& (EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO")||EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
							&& (EL_BECM.equals("N") || EL_BECM.equals("")))
					{
						EL_EWM5 = ((EL_ECCA - EL_ECJJ) / 2) + 30;
					}
					else
					{
						EL_EWM5 = 0;
					}
				}

				/** 변수 대입 - 0이면 blank 처리 **/
				String EL_EWM1_STR = (EL_EWM1 == 0) ? "" : BlockUtil.convertNumber(EL_EWM1);
				String EL_EWM2_STR = (EL_EWM2 == 0) ? "" : BlockUtil.convertNumber(EL_EWM2);
				String EL_EWM3_STR = (EL_EWM3 == 0) ? "" : BlockUtil.convertNumber(EL_EWM3);
				String EL_EWM4_STR = (EL_EWM4 == 0) ? "" : BlockUtil.convertNumber(EL_EWM4);
				String EL_EWM5_STR = (EL_EWM5 == 0) ? "" : BlockUtil.convertNumber(EL_EWM5);

				resultMap.put("VAR_EL_EWM1",EL_EWM1_STR);
				resultMap.put("VAR_EL_EWM2",EL_EWM2_STR);
				resultMap.put("VAR_EL_EWM3",EL_EWM3_STR);
				resultMap.put("VAR_EL_EWM4",EL_EWM4_STR);
				resultMap.put("VAR_EL_EWM5",EL_EWM5_STR);

			}

			/**
			 ** 구분2 !(?NYS?,?NYD?)
			 **/
			else if(!EL_BMDL.contains("NYS") && !EL_BMDL.contains("NYD"))
			{
				/******* 1. EL_EWM1 GET ******/
				double EWM1 = 0;

				if(sc2.compare("EL_BMDL","!(?NY2?,?NEO?,?VIS?,?VIP?)"))
				{
					if(EL_BWALLT.contains("T19"))
					{
						if(EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_ECCA < 1300)
								{
									EWM1 = 0;
								}
								else
								{
									if(EL_ECJJ < 1100)
										EWM1 = EL_ECJJ;
									else
										EWM1 = 1100;
								}

							}
							else
							{
								EWM1 = 9991; // blank
							}

						}
						if(EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_ECCA < 1300)
									EWM1 = 0;
								else if(EL_ECCA >= 1300 && EL_ECCA < 1700)
									EWM1 = EL_ECCA - 600;
								else if(EL_ECCA >= 1700)
									EWM1 = 1100;
							}
							else
							{
								EWM1 = 9991; // blank
							}
						}
					}
					else if(EL_BWALLT.contains("T30"))
					{
						if(EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_ECCA <= 1290)
									EWM1 = 900;
								if(EL_ECCA > 1290 && EL_ECCA <= 1470)
									EWM1 = 900;
								if(EL_ECCA > 1470 && EL_ECCA <= 1650)
									EWM1 = 900;
								if(EL_ECCA > 1650 && EL_ECCA <= 1830)
									EWM1 = 900;
								if(EL_ECCA > 1830 && EL_ECCA <= 2010)
									EWM1 = 900;
								if(EL_ECCA > 2010 && EL_ECCA <= 2190)
									EWM1 = 900;
								if(EL_ECCA > 2190 && EL_ECCA <= 2340)
									EWM1 = 900;
								if(EL_ECCA > 2340)
									EWM1 = 9993;
							}
							else
							{
								EWM1 = 9991; // blank
							}
						}
						if(EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_ECCA <= 1290)
									EWM1 = EL_ECCA - 700;
								if(EL_ECCA > 1290 && EL_ECCA <= 1470)
									EWM1 = EL_ECCA - 800;
								if(EL_ECCA > 1470 && EL_ECCA <= 1650)
									EWM1 = EL_ECCA - 900;
								if(EL_ECCA > 1650 && EL_ECCA <= 1830)
									EWM1 = EL_ECCA - 1000;
								if(EL_ECCA > 1830 && EL_ECCA <= 2010)
									EWM1 = EL_ECCA - 1100;
								if(EL_ECCA > 2010 && EL_ECCA <= 2190)
									EWM1 = EL_ECCA - 1200;
								if(EL_ECCA > 2190 && EL_ECCA <= 2340)
									EWM1 = EL_ECCA - 1300;
								if(EL_ECCA > 2340)
									EWM1 = 9993; // 범위초과
							}
							else
							{
								EWM1 = 9991; // blank
							}
						}
					}
				}

				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N") && (EL_ECCA >1250))//sc2.compare("EL_ECCA",">1250")
				{
					EWM1 = 1000;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N") && (EL_ECCA <=1250))//sc2.compare("EL_ECCA","<=1250")
				{
					EWM1 = EL_ECJJ;
				}
				else if(sc2.compare("EL_BMDL",",NEOA,NEOE,?VIP?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N"))

				{

					EWM1 = EL_ECJJ - 40;

				}
				else if(sc2.compare("EL_BMDL","?VIP?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N") && (EL_ECCA ==1150)&& (EL_ECCB ==1200))

				{

					EWM1 = 700;

				}

				else if(sc2.compare("EL_BMDL",",NEOC,NEOD,NEOF") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N"))
				{
					EWM1 = EL_ECJJ - 20;
				}
				else if(sc2.compare("EL_BMDL",",NEOB,?VIS?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N"))
				{
					EWM1 = EL_ECJJ;
				}

				int EWM1_INT = 0;
				EWM1_INT = (int) EWM1;
				String EWM1_STR = Integer.toString(EWM1_INT);
				if(EWM1_STR.equals("9991"))
					EWM1_STR = "";
				if(EWM1_STR.equals("9992"))
					EWM1_STR = "TRUNK Type Can't apply..";
				if(EWM1_STR.equals("9993"))
					EWM1_STR = "Scope Over..";

				double EL_EWM1 = BlockUtil.parseDouble(elvEnt.get("EL_EWM1"));
				String EL_EWM1_STR = BlockUtil.NVL(elvEnt.get("EL_EWM1"),"");


				if(EL_EWM1 == 0)
				{
					if("".equals(EL_EWM1_STR))
					{
						if(EWM1 > 0 && EWM1 < 9990)
						{ // 9991,9992,9993의 데이타 계산 error
							// 때문에
							resultMap.put("VAR_EL_EWM1",EWM1_STR);
						}
					}
					else
					{
						if(EWM1 > 0 && EWM1 < 9990)
						{
							resultMap.put("VAR_EL_EWM1","0");
							EWM1 = 0;
						}
					}


				}
				else
				{
					EWM1 = EL_EWM1; // 입력값있으면 입력값으로 EL_EWM2~5계산
				}

				EL_EWM1 = EWM1;

				/******** 2. EL_EWM2 GET ***********/

				double EWM2 = 0;

				//if(!EL_BMDL.contains("NEO"))
				if(sc2.compare("EL_BMDL","!(?NEO?,?VIS?,?VIP?)"))
				{
					if(EL_BWALLT.contains("T19"))
					{
						if(EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_BTRM.equals("N") || EL_BTRM.equals("") || EL_BTRM.equals("S"))
									EWM2 = ((EL_ECCA - EWM1) / 2) + 19;
								else if(EL_BTRM.equals("R") || EL_BTRM.equals("A"))
								{
									if(EWM1 == 0)
										EWM2 = ((EL_ECCA - 30) / 2) + 19;
									if(EWM1 != 0 && EWM1 != 9991 && EWM1 != 9992 && EWM1 != 9993)
										EWM2 = ((EL_ECCA - EWM1) / 2) - 11;
								}

							}
							else
							{
								EWM2 = 9991; // blank
							}
						}
						if(EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_BTRM.equals("N") || EL_BTRM.equals("") || EL_BTRM.equals("S"))
									EWM2 = ((EL_ECCA - EWM1) / 2) + 19;
								if(EL_BTRM.equals("R") || EL_BTRM.equals("A"))
								{
									if(EWM1 == 0)
										EWM2 = ((EL_ECCA - 30) / 2) + 19;
									if(EWM1 != 0 && EWM1 != 9991 && EWM1 != 9992 && EWM1 != 9993)
										EWM2 = ((EL_ECCA - EWM1) / 2) - 11;
								}
							}
							else
							{
								EWM2 = 9991; // blank
							}
						}
					}
					else if(EL_BWALLT.contains("T30"))
					{
						if(EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_BTRM.equals("N") || EL_BTRM.equals("") || EL_BTRM.equals("S"))
									EWM2 = ((EL_ECCA - EWM1) / 2) + 30;
								if(EL_BTRM.equals("R") || EL_BTRM.equals("A"))
								{
									if(EWM1 == 0)
										EWM2 = ((EL_ECCA - 30) / 2) + 30;
									if(EWM1 != 0 && EWM1 != 9991 && EWM1 != 9992 && EWM1 != 9993)
										EWM2 = (EL_ECCA - EWM1) / 2;
								}
							}
							else
							{
								EWM2 = 9991; // blank

							}
						}
						if(EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
						{
							if(EL_ETHRU.equals("N") || EL_ETHRU.equals(""))
							{
								if(EL_BTRM.equals("N") || EL_BTRM.equals("") || EL_BTRM.equals("S"))
									EWM2 = ((EL_ECCA - EWM1) / 2) + 30;
								if(EL_BTRM.equals("R") || EL_BTRM.equals("A"))
								{
									if(EWM1 == 0)
										EWM2 = ((EL_ECCA - 30) / 2) + 30;
									if(EWM1 != 0 && EWM1 != 9991 && EWM1 != 9992 && EWM1 != 9993)
										EWM2 = (EL_ECCA - EWM1) / 2;
								}
							}
							else
							{
								EWM2 = 9991; // blank
							}
						}
					}
				}
				else if(sc2.compare("EL_BMDL",",NEOA,NEOE,?VIP?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N"))
				{
					EWM2 = (( EL_ECCA - EL_EWM1 - 40 ) / 2) + 19;
				}
				else if(sc2.compare("EL_BMDL",",NEOC,NEOD,NEOF") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N"))
				{
					EWM2 = (( EL_ECCA - EL_EWM1 - 20 ) / 2) + 19;
				}
				else if(sc2.compare("EL_BMDL",",NEOB,?VIS?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","1SCO") && sc2.compare("EL_ETHRU","N"))
				{
					EWM2 = (( EL_ECCA - EL_EWM1 ) / 2) + 19;
				}


				int EWM2_INT = 0;
				EWM2_INT = (int) EWM2;
				String EWM2_STR = Integer.toString(EWM2_INT);

				if(EWM2_STR.equals("9991"))
					EWM2_STR = "";
				if(EWM2_STR.equals("9992"))
					EWM2_STR = "TRUNK Type Can't apply..";
				if(EWM2_STR.equals("9993"))
					EWM2_STR = "Scope Over..";

				// //System.out.println("EWM2 ----------------------> " + EWM2_STR);

				double EL_EWM2 = BlockUtil.parseDouble(elvEnt.get("EL_EWM2"));
				String EL_EWM2_STR = BlockUtil.NVL(elvEnt.get("EL_EWM2"),"");

				if(EL_EWM2 == 0)
				{
					if("".equals(EL_EWM2_STR))
					{ // 진짜 공백입력
						if(EWM2 > 0 && EWM2 < 9990)
						{ // 9991,9992,9993의 데이타 계산 error
							// 때문에
							resultMap.put("VAR_EL_EWM2",EWM2_STR);
						}
					}
					else
					{ // 0 입력시엔 0으로(사용자 0입력, 자동계산되어 입력된 0 )
						if(EWM2 > 0 && EWM2 < 9990)
						{
							resultMap.put("VAR_EL_EWM2","0");
							EWM2 = 0;
						}
					}
				}
				else
				{
					EWM2 = EL_EWM2; // 입력값있으면 입력값으로 EL_EWM2~5계산
				}

				EL_EWM2 = EWM2;

				/********** 3. EL_EWM3 GET ********/

				double EWM3 = 0;

				if(sc2.compare("EL_BMDL","!(?NY2?,?NEO?,?VIS?,?VIP?)"))
				{
					if(EL_BWALLT.contains("T19"))
					{
						if(EL_AOPEN.indexOf("U") == -1)
						{
							if(EL_ECCB < 1300)
								EWM3 = 0;
							if(EL_ECCB >= 1300 && EL_ECCB < 1600)
								EWM3 = EL_ECCB - 600;
							if(EL_ECCB >= 1600)
								EWM3 = 1000;
						}
					}
					else if(EL_BWALLT.contains("T30"))
					{
						if(EL_AOPEN.indexOf("U") == -1)
						{
							if(EL_ECCB <= 1350)
								EWM3 = 900;
							if(EL_ECCB > 1350 && EL_ECCB <= 1530)
								EWM3 = 900;
							if(EL_ECCB > 1530 && EL_ECCB <= 1710)
								EWM3 = 900;
							if(EL_ECCB > 1710 && EL_ECCB <= 1890)
								EWM3 = 900;
							if(EL_ECCB > 1890 && EL_ECCB <= 2070)
								EWM3 = 900;
							if(EL_ECCB > 2070 && EL_ECCB <= 2250)
								EWM3 = 900;
							if(EL_ECCB > 2250 && EL_ECCB <= 2400)
								EWM3 = 900;
							if(EL_ECCB > 2400 && EL_ECCB <= 3000)
								EWM3 = 900;
							if(EL_ECCB > 3000)
								EWM3 = 9993;
						}
					}
				}

				else if(sc2.compare("EL_BMDL",",?NEO?,?VIS?,?VIP?"))
				{
					EWM3 = 300;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA","<=550"))
				{
					EWM3 = 700;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","T19") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA",">550,<=600"))
				{
					EWM3 = 790;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","T19") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA",">600,<=700"))
				{
					EWM3 = 900;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","T19") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA",">700,<=750"))
				{
					EWM3 = 900;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","T19") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA",">750,<=900"))
				{
					EWM3 = 850;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","T19") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA",">900,<=1000"))
				{
					EWM3 = 900;
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","T19") && sc2.compare("EL_AOPEN","!(?U?)") && sc2.compare("EL_ACAPA",">1000"))
				{
					EWM3 = 800;
				}

				String EWM3_STR = String.valueOf((int) EWM3);
				if(EWM3_STR.equals("9991"))
					EWM3_STR = "";
				if(EWM3_STR.equals("9992"))
					EWM3_STR = "TRUNK Type Can't apply..";
				if(EWM3_STR.equals("9993"))
					EWM3_STR = "Scope Over..";

				// //System.out.println("EWM3 ----------------------> " + EWM3_STR);

				double EL_EWM3 = BlockUtil.parseDouble(elvEnt.get("EL_EWM3"));
				String EL_EWM3_STR = BlockUtil.NVL(elvEnt.get("EL_EWM3"),"");

				if(EL_EWM3 == 0)
				{
					if("".equals(EL_EWM3_STR))
					{ // 진짜 공백입력
						if(EWM3 > 0 && EWM3 < 9990)
						{ // 9991,9992,9993의 데이타 계산 error
							// 때문에
							resultMap.put("VAR_EL_EWM3",EWM3_STR);
						}
					}
					else
					{ // 0 입력시엔 0으로(사용자 0입력, 자동계산되어 입력된 0 )
						if(EWM3 > 0 && EWM3 < 9990)
						{
							resultMap.put("VAR_EL_EWM3","0");
							EWM3 = 0;
						}
					}
				}
				else
				{
					EWM3 = EL_EWM3; // 입력값있으면 입력값으로 EL_EWM3~5계산
				}

				EL_EWM3 = EWM3;

				/********** 4. EL_EWM4 GET ********/

				double EWM4 = 0;
				if(sc2.compare("EL_BMDL","!(?NYS?,?NYD?,?NY2?)") && sc2.compare("EL_AOPEN","!(?U?)"))
				{
					if(sc2.compare("EL_BTRM",",N,R"))
					{
						EWM4 = (EL_ECCB - EWM3) / 2;
					}
					else if(sc2.compare("EL_BTRM",",S,A"))
					{
						if(EWM3 == 0)
							EWM4 = (EL_ECCB - 30) / 2;
						if(EWM3 != 0 && EWM3 != 9991 && EWM3 != 9992 && EWM3 != 9993)
							EWM4 = ((EL_ECCB - EWM3) / 2) - 30;
					}
				}
				else if(sc2.compare("EL_BMDL","?NY2?") && sc2.compare("EL_BWALLT","?T19?") && sc2.compare("EL_AOPEN","!(?U?)"))
				{
					EWM4 = (EL_ECCB - EL_EWM3) / 2;
				}

				String EWM4_STR = String.valueOf((int) EWM4);
				if(EWM4_STR.equals("9991"))
					EWM4_STR = "";
				if(EWM4_STR.equals("9992"))
					EWM4_STR = "TRUNK Type Can't apply..";
				if(EWM4_STR.equals("9993"))
					EWM4_STR = "Scope Over..";

				// //System.out.println("EWM4 ----------------------> " + EWM4_STR);

				double EL_EWM4 = BlockUtil.parseInt(elvEnt.get("EL_EWM4"));
				String EL_EWM4_STR = BlockUtil.NVL(elvEnt.get("EL_EWM4"),"");

				if(EL_EWM4 == 0)
				{
					if("".equals(EL_EWM4_STR))
					{ // 진짜 공백입력
						if(EWM4 > 0 && EWM4 < 9990)
						{ // 9991,9992,9993의 데이타 계산 error
							// 때문에
							resultMap.put("VAR_EL_EWM4",EWM4_STR);
						}
					}
					else
					{ // 0 입력시엔 0으로(사용자 0입력, 자동계산되어 입력된 0 )
						if(EWM4 > 0 && EWM4 < 9990)
						{
							resultMap.put("VAR_EL_EWM4","0");
							EWM4 = 0;
						}
					}
				}
				else
				{
					EWM4 = EL_EWM4; // 입력값있으면 입력값으로 EL_EWM4~5계산
				}

				EL_EWM4 = EWM4;

				/************* 5. EL_EWM5 GET **************/

				double EWM5 = 0;

				if(EL_BWALLT.contains("T19"))
				{
					if(EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO"))
					{
						if(!"".equals(EL_BECM))
							EWM5 = ((EL_ECCA - EL_ECJJ) / 2) - 11;
						else
							EWM5 = ((EL_ECCA - EL_ECJJ) / 2) + 19;
					}
					if(EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
					{
						if(!EL_BECM.equals(""))
							EWM5 = EL_ECCA - EL_ECJJ - 16;
						else
							EWM5 = EL_ECCA - EL_ECJJ + 14;
					}
				}
				else if(EL_BWALLT.contains("T30"))
				{
					if(EL_AOPEN.equals("1SCO") || EL_AOPEN.equals("2SCO"))
					{
						if(!"".equals(EL_BECM))
							EWM5 = (EL_ECCA - EL_ECJJ) / 2;
						else
							EWM5 = ((EL_ECCA - EL_ECJJ) / 2) + 30;
					}
					if(EL_AOPEN.equals("2SR") || EL_AOPEN.equals("2SL") || EL_AOPEN.equals("2SLR"))
					{
						if(!EL_BECM.equals(""))
							EWM5 = EL_ECCA - EL_ECJJ - 5;
						else
							EWM5 = EL_ECCA - EL_ECJJ + 25;
					}
				}

				int EWM5_INT = 0;
				EWM5_INT = (int) EWM5;
				String EWM5_STR = Integer.toString(EWM5_INT);

				if(EWM5_STR.equals("9991"))
					EWM5_STR = "";
				if(EWM5_STR.equals("9992"))
					EWM5_STR = "TRUNK Type Can't apply..";
				if(EWM5_STR.equals("9993"))
					EWM5_STR = "Scope Over..";

				// //System.out.println("EWM5 ----------------------> " + EWM5_STR);

				double EL_EWM5 = BlockUtil.parseDouble(elvEnt.get("EL_EWM5"));
				String EL_EWM5_STR = BlockUtil.NVL(elvEnt.get("EL_EWM5"),"");

				if(EL_EWM5 == 0)
				{
					if("".equals(EL_EWM5_STR))
					{ // 진짜 공백입력
						if(EWM5 > 0 && EWM5 < 9990)
						{ // 9991,9992,9993의 데이타 계산 error
							// 때문에
							resultMap.put("VAR_EL_EWM5",EWM5_STR);
						}
					}
					else
					{ // 0 입력시엔 0으로(사용자 0입력, 자동계산되어 입력된 0 )
						if(EWM5 > 0 && EWM5 < 9990)
						{
							resultMap.put("VAR_EL_EWM5","0");
							EWM5 = 0;
						}
					}
				}
				else
				{
					EWM5 = EL_EWM5; // 입력값있으면 입력값으로 EL_EWM5~5계산
				}

				EL_EWM5 = EWM5;
			}
		}
		//NEX 현장인 경우
		else {
			double EL_EWM1 = BlockUtil.parseDouble(elvEnt.get("EL_EWM1"));
			double EL_EWM2 = BlockUtil.parseDouble(elvEnt.get("EL_EWM2"));
			double EL_EWM3 = BlockUtil.parseDouble(elvEnt.get("EL_EWM3"));
			double EL_EWM4 = BlockUtil.parseDouble(elvEnt.get("EL_EWM4"));
			double EL_EWM5 = BlockUtil.parseDouble(elvEnt.get("EL_EWM5"));
			/********** 1. EL_EWM1 GET ********/
			if(EL_EWM1 == 0) {
				if(("NEX_A1".equals(EL_BMDL) || "NEX_A2".equals(EL_BMDL) || "NEX_B1".equals(EL_BMDL) || "NEX_B2".equals(EL_BMDL) || "NEX_C1".equals(EL_BMDL)  || "NEX_C2".equals(EL_BMDL))
						&& ("NEX_A".equals(EL_BWMT) || "NEX_B".equals(EL_BWMT) || "NEX_C".equals(EL_BWMT))
						&& "1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					if(EL_ECJJ <=1000)
						EL_EWM1 = EL_ECJJ;
					else
						EL_EWM1 = 1000;
				}
				else if(("NEX_BB".equals(EL_BMDL) || "NEX_GW".equals(EL_BMDL) || "NEX_WB".equals(EL_BMDL) || "NEX_SB".equals(EL_BMDL) || "NEX_PB".equals(EL_BMDL)  || "NEX_PS".equals(EL_BMDL)) &&
						("NEX_BB".equals(EL_BWMT) || "NEX_GW".equals(EL_BWMT) || "NEX_WB".equals(EL_BWMT) || "NEX_SB".equals(EL_BWMT) || "NEX_PB".equals(EL_BWMT)  || "NEX_PS".equals(EL_BWMT)) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					if(EL_ECJJ <=1000)
						EL_EWM1 = EL_ECJJ;
					else
						EL_EWM1 = 1000;
				} else if("VISC".equals(EL_BMDL) &&
						"VISC".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					if(EL_ECJJ <=1000)
						EL_EWM1 = EL_ECJJ;
					else
						EL_EWM1 = 1000;
				} else if("NEOD".equals(EL_BMDL) &&
						"NEOD".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					if(EL_ECJJ <=1000)
						EL_EWM1 = EL_ECJJ-20;
					else
						EL_EWM1 = 980;
				}
			}

			/********** 2. EL_EWM2 GET ********/
			if(EL_EWM2 == 0) {
				if(("NEX_A1".equals(EL_BMDL) || "NEX_A2".equals(EL_BMDL) || "NEX_B1".equals(EL_BMDL) || "NEX_B2".equals(EL_BMDL) || "NEX_C1".equals(EL_BMDL)  || "NEX_C2".equals(EL_BMDL))
						&& ("NEX_A".equals(EL_BWMT) || "NEX_B".equals(EL_BWMT) || "NEX_C".equals(EL_BWMT))
						&& "1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					EL_EWM2 =  ( EL_ECCA - EL_EWM1 ) / 2  + 21;

				}
				else if(("NEX_BB".equals(EL_BMDL) || "NEX_GW".equals(EL_BMDL) || "NEX_WB".equals(EL_BMDL) || "NEX_SB".equals(EL_BMDL) || "NEX_PB".equals(EL_BMDL)  || "NEX_PS".equals(EL_BMDL)) &&
						("NEX_BB".equals(EL_BWMT) || "NEX_GW".equals(EL_BWMT) || "NEX_WB".equals(EL_BWMT) || "NEX_SB".equals(EL_BWMT) || "NEX_PB".equals(EL_BWMT)  || "NEX_PS".equals(EL_BWMT)) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					EL_EWM2 = ( EL_ECCA - EL_EWM1 ) / 2  + 21;
				} else if("VISC".equals(EL_BMDL) &&
						"VISC".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					EL_EWM2 =  ( EL_ECCA - EL_EWM1 ) / 2  + 19;
				} else if("NEOD".equals(EL_BMDL) &&
						"NEOD".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					EL_EWM2 =  ( EL_ECCA - EL_EWM1 - 20 ) / 2  + 19;
				}
			}
			/********** 3. EL_EWM3 GET ********/
			if(EL_EWM3 == 0) {
				if(("NEX_A1".equals(EL_BMDL) || "NEX_A2".equals(EL_BMDL) || "NEX_B1".equals(EL_BMDL) || "NEX_B2".equals(EL_BMDL) || "NEX_C1".equals(EL_BMDL)  || "NEX_C2".equals(EL_BMDL))
						&& ("NEX_A".equals(EL_BWMT) || "NEX_B".equals(EL_BWMT) || "NEX_C".equals(EL_BWMT))
						&& "1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {

					EL_EWM3 = 300;
				}
				else if(("NEX_BB".equals(EL_BMDL) || "NEX_GW".equals(EL_BMDL) || "NEX_WB".equals(EL_BMDL) || "NEX_SB".equals(EL_BMDL) || "NEX_PB".equals(EL_BMDL)  || "NEX_PS".equals(EL_BMDL)) &&
						("NEX_BB".equals(EL_BWMT) || "NEX_GW".equals(EL_BWMT) || "NEX_WB".equals(EL_BWMT) || "NEX_SB".equals(EL_BWMT) || "NEX_PB".equals(EL_BWMT)  || "NEX_PS".equals(EL_BWMT)) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					if(EL_ECCB <=1100)
						EL_EWM3 = 0;
					else if(EL_ECCB > 1100 && EL_ECCB <= 1250 )
						EL_EWM3 = 600;
					else if(EL_ECCB > 1250 && EL_ECCB <= 1400 )
						EL_EWM3 = 700;
					else if(EL_ECCB > 1400 && EL_ECCB <= 1500 )
						EL_EWM3 = 800;
					else if(EL_ECCB > 1500 && EL_ECCB <= 1600 )
						EL_EWM3 = 900;
					else
						EL_EWM3 = 1000;

				} else if("VISC".equals(EL_BMDL) &&
						"VISC".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					EL_EWM3 = 300;
				} else if("NEOD".equals(EL_BMDL) &&
						"NEOD".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) ) {
					EL_EWM3 = 300;
				}
			}

			/********** 4. EL_EWM4 GET ********/
			if(EL_EWM4 == 0) {
				if(("NEX_A1".equals(EL_BMDL) || "NEX_A2".equals(EL_BMDL) || "NEX_B1".equals(EL_BMDL) || "NEX_B2".equals(EL_BMDL) || "NEX_C1".equals(EL_BMDL)  || "NEX_C2".equals(EL_BMDL))
						&& ("NEX_A".equals(EL_BWMT) || "NEX_B".equals(EL_BWMT) || "NEX_C".equals(EL_BWMT))
						&& "1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) && ("".equals(EL_BTRM) || "N".equals(EL_BTRM)) ) {

					EL_EWM4 = ( EL_ECCB - EL_EWM3 ) / 2;
				}
				else if(("NEX_BB".equals(EL_BMDL) || "NEX_GW".equals(EL_BMDL) || "NEX_WB".equals(EL_BMDL) || "NEX_SB".equals(EL_BMDL) || "NEX_PB".equals(EL_BMDL)  || "NEX_PS".equals(EL_BMDL)) &&
						("NEX_BB".equals(EL_BWMT) || "NEX_GW".equals(EL_BWMT) || "NEX_WB".equals(EL_BWMT) || "NEX_SB".equals(EL_BWMT) || "NEX_PB".equals(EL_BWMT)  || "NEX_PS".equals(EL_BWMT)) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && ("".equals(EL_BTRM) || "N".equals(EL_BTRM)) ) {
					EL_EWM4 = ( EL_ECCB - EL_EWM3 ) / 2;

				} else if("VISC".equals(EL_BMDL) &&
						"VISC".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && ("".equals(EL_BTRM) || "N".equals(EL_BTRM)) ) {
					EL_EWM4 = ( EL_ECCB - EL_EWM3 ) / 2;
				} else if("NEOD".equals(EL_BMDL) &&
						"NEOD".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && ("".equals(EL_BTRM) || "N".equals(EL_BTRM)) ) {
					EL_EWM4 = ( EL_ECCB - EL_EWM3 ) / 2;
				}
			}
			/********** 5. EL_EWM5 GET ********/
			if(EL_EWM5 == 0) {
				if(("NEX_A1".equals(EL_BMDL) || "NEX_A2".equals(EL_BMDL) || "NEX_B1".equals(EL_BMDL) || "NEX_B2".equals(EL_BMDL) || "NEX_C1".equals(EL_BMDL)  || "NEX_C2".equals(EL_BMDL))
						&& ("NEX_A".equals(EL_BWMT) || "NEX_B".equals(EL_BWMT) || "NEX_C".equals(EL_BWMT))
						&& "1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU)) && ("".equals(EL_BECM) || "N".equals(EL_BECM)) ) {

					EL_EWM5 =  ( EL_ECCA - EL_ECJJ ) / 2  + 19;
				}
				else if(("NEX_BB".equals(EL_BMDL) || "NEX_GW".equals(EL_BMDL)) &&
						("NEX_BB".equals(EL_BWMT) || "NEX_GW".equals(EL_BWMT) ) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && !("".equals(EL_BECM) || "N".equals(EL_BECM)) ) {
					EL_EWM5 =  ( EL_ECCA - EL_ECJJ ) / 2  + 14;

				} else if(("NEX_WB".equals(EL_BMDL) || "NEX_SB".equals(EL_BMDL) || "NEX_PB".equals(EL_BMDL) || "NEX_PS".equals(EL_BMDL) ) &&
						("NEX_WB".equals(EL_BWMT) || "NEX_SB".equals(EL_BWMT) || "NEX_PB".equals(EL_BWMT) || "NEX_PS".equals(EL_BWMT) ) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && ("".equals(EL_BECM) || "N".equals(EL_BECM)) ) {
					EL_EWM5 =  ( EL_ECCA - EL_ECJJ ) / 2  + 19;

				} else if("VISC".equals(EL_BMDL) &&
						"VISC".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && ("".equals(EL_BECM) || "N".equals(EL_BECM)) ) {
					EL_EWM5 =  ( EL_ECCA - EL_ECJJ ) / 2  + 19;
				} else if("NEOD".equals(EL_BMDL) &&
						"NEOD".equals(EL_BWMT) &&
						"1SCO".equals(EL_AOPEN) && ("".equals(EL_ETHRU) || "N".equals(EL_ETHRU))  && !("".equals(EL_BECM) || "N".equals(EL_BECM)) ) {
					EL_EWM5 =  ( EL_ECCA - EL_ECJJ ) / 2  - 11;
				}

			}
			/** 변수 대입 - 0이면 blank 처리 **/
			String EL_EWM1_STR = (EL_EWM1 == 0) ? "" : BlockUtil.convertNumber(EL_EWM1);
			String EL_EWM2_STR = (EL_EWM2 == 0) ? "" : BlockUtil.convertNumber(EL_EWM2);
			String EL_EWM3_STR = (EL_EWM3 == 0) ? "" : BlockUtil.convertNumber(EL_EWM3);
			String EL_EWM4_STR = (EL_EWM4 == 0) ? "" : BlockUtil.convertNumber(EL_EWM4);
			String EL_EWM5_STR = (EL_EWM5 == 0) ? "" : BlockUtil.convertNumber(EL_EWM5);

			resultMap.put("VAR_EL_EWM1",EL_EWM1_STR);
			resultMap.put("VAR_EL_EWM2",EL_EWM2_STR);
			resultMap.put("VAR_EL_EWM3",EL_EWM3_STR);
			resultMap.put("VAR_EL_EWM4",EL_EWM4_STR);
			resultMap.put("VAR_EL_EWM5",EL_EWM5_STR);

		}


		return resultMap;

	}

	private List<String> getCOUNT_ELData(String hogiNum_project_no)
	{
		List<String> resultList = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pstmt = null;
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;

		try
		{
			con = PLMDBConnection.getConnection();

			sql.append(" select md$number from elv_info$vf, elv_info$id where vf$ouid=id$wip and md$Number like ?||'%' ");
			sql.append(" union ");
			sql.append(" select md$number from shipelv_info$vf, shipelv_info$id where vf$ouid=id$wip and md$Number like ?||'%'  ");
			sql.append(" union ");
			sql.append(" select md$number from JQPR_info$vf, jqpr_info$id where vf$ouid=id$wip and md$Number like ?||'%'  ");

			pstmt = con.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++,hogiNum_project_no);
			pstmt.setString(idx++,hogiNum_project_no);
			pstmt.setString(idx++,hogiNum_project_no);

			rs = pstmt.executeQuery();

			while(rs.next())
			{
				resultList.add(rs.getString("md$number"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PLMDBConnection.disconnect(con, pstmt, rs);
		}

		return resultList;
	}
	
	private String getEL_ZORINO(String hogiNum)
	{
		String el_zorino = "";
		try
		{
			// JdbcTemplate.queryForObject : 1건이 아니면 예외
			List<Map<String, String>> rows = ctx.getDb().queryForList(" SELECT EL_ZORINO FROM ELV_INFO$VF, ELV_INFO$ID WHERE VF$OUID = ID$WIP AND MD$NUMBER = ?", hogiNum);
			if (rows.size() == 1)
				el_zorino = rows.get(0).get("EL_ZORINO");
		}
		catch(Exception e)
		{
			// 오류 시 el_zorino = "" 로 리턴
		}

		return el_zorino;
	}

	public static List<String> expandsFloor(String value, boolean isOversee, boolean doStrict) throws Exception {
		if(doStrict) {
			if(isOversee) {
				if(!value.matches("[0-9A-Z,~\\-]*"))
					throw new InputValueUnvalidException("Only alphabetic characters and numeric commas (,) waves (~) can be entered.");
			}else {
				if(!value.matches("[0-9A-Z,~]*"))
					throw new InputValueUnvalidException("대문자,숫자,콤마,물결로만 표현가능.");
			}
		}else {
				if(isOversee) {
					if(!value.matches("[0-9A-Z,~\\-]*"))
						throw new InputValueUnvalidException("Only alphabetic characters and numeric commas (,) waves (~) can be entered.");
				}else {
					if(!value.matches("[0-9A-Z,~]*"))
						throw new InputValueUnvalidException("대문자,숫자,콤마,물결로만 표현가능.");
				}	
			}

		String floorSplitRegex = "[,\\.]";

		if(doStrict)
			floorSplitRegex = "[,]";

		String[] floorValue = value.split(floorSplitRegex);
		List<String> res = new ArrayList<String>();

		String matchRegex = ".*[~\\-].*";
		String splitRegex = "[~\\-]";

		if (doStrict) {
			matchRegex = ".*[~].*";
			splitRegex = "[~]";
		} else {
			if (isOversee == true) {
				matchRegex = ".*[~].*";
				splitRegex = "[~]";
			}
		}

		for(String token : floorValue) {
			token = token.trim();
			if(token.matches(matchRegex)) {
				String[] range = token.split(splitRegex);
				if(range.length == 2) {

					boolean hasB = false;

					String st = range[0].trim();
					String end = range[1].trim();

					Integer st_num =0;
					Integer end_num = 0;
					try {
						if (st.matches("B.*$")) {
							st_num = Integer.parseInt(st.replace("B", "-"));
							hasB = true;
						}
						else
							st_num = Integer.parseInt(st);
					}catch(NumberFormatException e) {
						if(isOversee) {
							throw new InputValueUnvalidException(token+" Floor markings not possible");
						}else
						throw new InputValueUnvalidException("층표기 "+token+" 표현 불가함");
					}

					try {
						if(end.matches("B.*$")) {
							end_num = Integer.parseInt(end.replace("B", "-")) + 1;
							hasB = true;
						}
						else
							end_num = Integer.parseInt(end) + 1;
					}catch(NumberFormatException e) {
						if(isOversee) {
							throw new InputValueUnvalidException(token+" Floor markings not possible");
						}else
						throw new InputValueUnvalidException("층표기 "+token+" 표현 불가함");
					}

					List<Integer> collect = IntStream.range(st_num, end_num).boxed().collect(Collectors.toList());

					// 국내는 0층이 없음
					if(isOversee == false) {
						if(collect.contains(Integer.valueOf(0)))
							collect.remove(Integer.valueOf(0));
					}

					for (Integer num : collect) {
						if (hasB == true) {
							res.add(num.toString().replace("-", "B"));
						}
						else {
							res.add(num.toString());
						}
					}
				}else if(range.length == 1) {
					throw new InputValueUnvalidException("범위 표기 잘못됨 : "+token);
				}
			}else {
				res.add(token);
			}
		}

		return res;
	}

	public BlockVariantMap FUNCTION_NOW(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

			result.put("O_DATE",dateFormat.format(new Date()));
		}catch(Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public BlockVariantMap FUNCTION_IS_NUMBER(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String V_NUM = BlockUtil.NVL(elvEnt.get("V_NUM"), "");

		if (BlockUtil.isCreatable(V_NUM))
			result.put("O_RESULT", "T");
		else
			result.put("O_RESULT", "E");

		return result;
	}

	public BlockVariantMap FUNCTION_SPLIT(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String V_STR = BlockUtil.NVL(elvEnt.get("V_STR"), "");
		String V_SEPARATOR = BlockUtil.NVL(elvEnt.get("V_SEPARATOR"), "");

		if("".equals(V_SEPARATOR) || "".equals(V_STR)) {
			result.put("O_CNT", "0");
		}else {
			String[] split = V_STR.split(V_SEPARATOR);

			result.put("O_CNT", String.valueOf(split.length));

			for(int i=1; i<=split.length; i++) {
				result.put("O_STR"+i, split[i-1]);
			}
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_SUBSTRING(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		String V_START = BlockUtil.NVL(elvEnt.get("V_START"), "");
		String V_END = BlockUtil.NVL(elvEnt.get("V_END"), "");
		
		int stringLength = V_STRING.length();
		int start, end;
		
		// 예외처리
		if ("".equals(V_START)) start = 0;				// start가 없는 경우: String 처음부터 추출
		else start = Integer.parseInt(V_START);
		
		if ("".equals(V_END)) end = stringLength;		// end가 없는 경우: String 끝까지 추출
		else end = Integer.parseInt(V_END);
		
		if (end > stringLength) end = stringLength;		// end가 문자열보다 길 경우: String 끝까지 추출
		
		if("".equals(V_STRING) || start < 0 || start > stringLength || start > end) {
			result.put("O_RESULT", V_STRING);
		}
		else {
			String substring = V_STRING.substring(start, end);
			result.put("O_RESULT", substring);
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_UPPERCASE(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		
		int stringLength = V_STRING.length();
		
		if("".equals(V_STRING)) {
			result.put("O_RESULT", V_STRING);
		}
		else {
			String upperString = V_STRING.toUpperCase();
			result.put("O_RESULT", upperString);
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_LOWERCASE(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		
		int stringLength = V_STRING.length();
		
		if("".equals(V_STRING)) {
			result.put("O_RESULT", V_STRING);
		}
		else {
			String lowerString = V_STRING.toLowerCase();
			result.put("O_RESULT", lowerString);
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_TRIM(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		
		if("".equals(V_STRING)) {
			result.put("O_RESULT", V_STRING);
		}
		else {
			String trimString = V_STRING.trim();
			trimString = trimString.replaceAll(" ", "").replaceAll("	", "");
			result.put("O_RESULT", trimString);
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_REPLACE(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		String V_TARGET = BlockUtil.NVL(elvEnt.get("V_TARGET"), "");
		String V_REPLACE = BlockUtil.NVL(elvEnt.get("V_REPLACE"), "");
		
		if("".equals(V_STRING)) {
			result.put("O_RESULT", V_STRING);
		}
		else {
			String replaceString = V_STRING.replace(V_TARGET, V_REPLACE);
			result.put("O_RESULT", replaceString);
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_REPLACEALL(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		String V_TARGET = BlockUtil.NVL(elvEnt.get("V_TARGET"), "");
		String V_REPLACE = BlockUtil.NVL(elvEnt.get("V_REPLACE"), "");
		
		if("".equals(V_STRING)) {
			result.put("O_RESULT", V_STRING);
		}
		else {
			String replaceString = V_STRING.replaceAll(V_TARGET, V_REPLACE);
			result.put("O_RESULT", replaceString);
		}

		return result;
	}
	
	public BlockVariantMap FUNCTION_MATCH_GROUP(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String V_STRING = BlockUtil.NVL(elvEnt.get("V_STRING"), "");
		String V_MATCHES = BlockUtil.NVL(elvEnt.get("V_MATCHES"), "");

		if("".equals(V_MATCHES) || "".equals(V_STRING)) {
			result.put("O_CNT", "0");
		}else {

			Pattern pattern = Pattern.compile(V_MATCHES);
			Matcher matcher = pattern.matcher(V_STRING);
			
			int cnt = 1;
			while (matcher.find()) {
				result.put("O_STR"+cnt, matcher.group());
				if (cnt >= 100) {	// 100건 까지만 추출
					break;
				}
				cnt++;
			}
			
			result.put("O_CNT", cnt - 1);
		}

		return result;
	}

	public BlockVariantMap SPEC_MAPPING_REVERSE_EL_EFH(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception
	{
		BlockVariantMap resultMap = new BlockVariantMap();

		List<String> floorHList = new ArrayList<String>();

		for(int i=0; i<=7; i++) {
			String FLOORH = BlockUtil.NVL(elvEnt.get("EL_EFLOORH"+i),"").trim();
			String FLOORHA = BlockUtil.NVL(elvEnt.get("EL_EFLOORHA"+i),"").trim();
			int FLOORQ = BlockUtil.parseInt(elvEnt.get("EL_EFLOORQ"+i));

			if(!"".equals(FLOORHA)) {
				FLOORH += FLOORHA;
			}

			if(!"".equals(FLOORH)) {
				if(FLOORH.contains(",") || FLOORH.contains("*")) {
					String[] split = FLOORH.split(",");
					for(String floorhExpression : split) {
						if(floorhExpression.contains("*")) {
							String[] tokens = floorhExpression.split("\\*");
							if(tokens.length != 2)
								throw new Exception("unable to expand floorHeight : "+floorhExpression);

							String height = tokens[0];
							int qty = BlockUtil.parseInt(tokens[1]);

							for(int j=0; j<qty; j++)
								floorHList.add(height);
						}else {
							floorHList.add(floorhExpression);
						}
					}
				}
				else {
					for(int j=0; j<FLOORQ; j++)
						floorHList.add(FLOORH);
				}
			}
		}

		DecimalFormat df = new DecimalFormat("00");

		for(int i=1; i<=floorHList.size(); i++) {
			resultMap.put("EL_EFH"+df.format(i), floorHList.get(i-1));
		}

		return resultMap;
	}

	public BlockVariantMap FUNCTION_READ_BOM(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String projectNo = BlockUtil.NVL(elvEnt.get("PROJ_NO"), (String) elvEnt.get("md$number"));

		String productOuid = ctx.getSpecLoader().findWipProductOuid(projectNo);
		if (productOuid == null)
			throw new NullPointerException("wip product not found : " + projectNo); // 원본 : product.get() NPE

		List<Map<String, Object>> nonHierarchicalEBOM = BlockEBomReader.nonHierarchical(new BlockEBomReader(ctx.getDb()).getOrderBom(productOuid));

		Map<String, Integer> blockCountMap = new HashMap<>();

		for(Map<String, Object> bom : nonHierarchicalEBOM){
			String partNo = BlockUtil.NVL(bom.get("partNo"),"");
			String blockNo = BlockUtil.NVL(bom.get("blockNo_org"),"");
			String qty = BlockUtil.NVL(bom.get("qty"),"");
			String cmt = BlockUtil.NVL(bom.get("cmt"),"");
			String mdf = BlockUtil.NVL(bom.get("uCheck"),"");
			String order = BlockUtil.NVL(bom.get("doOrder"),"");
			String glCode = BlockUtil.NVL(bom.get("glCode"),"");
			String spec = BlockUtil.NVL(bom.get("spec"),"");
			String size = BlockUtil.NVL(bom.get("size"),"");
			String partName = BlockUtil.NVL(bom.get("partName"),"");
			String nation = BlockUtil.NVL(bom.get("nation"),"");
			mdf = "1".equals(mdf) ? "Y" : "N";
			String firstChar = partNo.substring(0,1); // 자재 첫 문자 추출
			String strLength = Integer.toString(partNo.length()); //자재의 자리수
			
			
			Integer blockCount = blockCountMap.get(blockNo);

			if(blockCount == null) {
				blockCount = 0;
			}

			if(blockCount == 0){
				result.put(blockNo+"_PNO", partNo);
				result.put(blockNo+"_QTY", qty);
				result.put(blockNo+"_CMT", cmt);
				result.put(blockNo+"_MDF", mdf);
				result.put(blockNo+"_IOT", order);
				result.put(blockNo+"_GLC", glCode);
				result.put(blockNo+"_SPE", spec);
				result.put(blockNo+"_SIZ", size);
				result.put(blockNo+"_PNAME", partName);
				result.put(blockNo+"_OWN", nation);
				result.put(blockNo+"_FIRST", firstChar);
				result.put(blockNo+"_STRLEN", strLength);
			}else{
				result.put(blockNo+"_PNO_"+blockCount, partNo);
				result.put(blockNo+"_QTY_"+blockCount, qty);
				result.put(blockNo+"_CMT_"+blockCount, cmt);
				result.put(blockNo+"_MDF_"+blockCount, mdf);
				result.put(blockNo+"_IOT_"+blockCount, order);
				result.put(blockNo+"_GLC_"+blockCount, glCode);
				result.put(blockNo+"_SPE_"+blockCount, spec);
				result.put(blockNo+"_SIZ_"+blockCount, size);
				result.put(blockNo+"_PNAME_"+blockCount, partName);
				result.put(blockNo+"_OWN_"+blockCount, nation);
				result.put(blockNo+"_FIRST_"+blockCount, firstChar);
				result.put(blockNo+"_STRLEN_"+blockCount, strLength);
			}
			blockCountMap.put(blockNo, blockCount+1);
		}
		result.put("FUNCTION_READ_BOM_FLAG", "Y");
		return result;
	}
	public BlockVariantMap FUNCTION_SUM_MAT_QTY( Map elvEnt, List<Map> floorMasterList, Map partInfo	) 
			throws Exception {

		BlockVariantMap result = new BlockVariantMap();
	    // =========================================================
	    // 1. 공란, null 전부 0으로 치환 (nz 동일)
	    // =========================================================
	    String q02s = BlockUtil.NVL(elvEnt.get("H02_QTY_R"), "0").trim();
	    if (q02s.length() == 0) q02s = "0";

	    String q04s = BlockUtil.NVL(elvEnt.get("H04_QTY_R"), "0").trim();
	    if (q04s.length() == 0) q04s = "0";

	    String q05s = BlockUtil.NVL(elvEnt.get("H05_QTY_R"), "0").trim();
	    if (q05s.length() == 0) q05s = "0";

	    String q07s = BlockUtil.NVL(elvEnt.get("H07_QTY_R"), "0").trim();
	    if (q07s.length() == 0) q07s = "0";

	    String q08s = BlockUtil.NVL(elvEnt.get("H08_QTY_R"), "0").trim();
	    if (q08s.length() == 0) q08s = "0";

	    String q10s = BlockUtil.NVL(elvEnt.get("H10_QTY_R"), "0").trim();
	    if (q10s.length() == 0) q10s = "0";

	    // =========================================================
	    // 2. MAT (null 방지 + trim)
	    // =========================================================
	    String m02 = BlockUtil.NVL(elvEnt.get("EL_BWM02"), "").trim();
	    String m04 = BlockUtil.NVL(elvEnt.get("EL_BWM04"), "").trim();
	    String m05 = BlockUtil.NVL(elvEnt.get("EL_BWM05"), "").trim();

	    String m07 = BlockUtil.NVL(elvEnt.get("EL_BWM07"), "").trim();
	    String m08 = BlockUtil.NVL(elvEnt.get("EL_BWM08"), "").trim();
	    String m10 = BlockUtil.NVL(elvEnt.get("EL_BWM10"), "").trim();

	    // =========================================================
	    // 3. 숫자 검증 + 파싱 1회
	    // =========================================================
	    double h02, h04, h05, h07, h08, h10;

	    try {
	        h02 = Double.parseDouble(q02s);
	        h04 = Double.parseDouble(q04s);
	        h05 = Double.parseDouble(q05s);

	        h07 = Double.parseDouble(q07s);
	        h08 = Double.parseDouble(q08s);
	        h10 = Double.parseDouble(q10s);
	    } catch (Exception e) {
	        result.put("FUNCTION_SUM_MAT_QTY", "N");
	        result.put("ERR_CODE", "QTY_NOT_NUMBER");
	        return result;
	    }

	    // =========================================================
	    // 4. NEW 기본값
	    // =========================================================
	    double h02New = h02, h04New = h04, h05New = h05;
	    double h07New = h07, h08New = h08, h10New = h10;

	    String applyGrp1 = "N";
	    String applyGrp2 = "N";

	    // =========================================================
	    // 5. 그룹 1 : H02 + H04 + H05 → H02 보정
	    // =========================================================
	    if (!m02.isEmpty() && m02.equals(m04) && m02.equals(m05)) {

	        double sum1 = h02 + h04 + h05;
	        if (sum1 <= 1.0) {
	            h02New = h02 + (1.0 - sum1);
	            applyGrp1 = "Y";
	        }
	    }

	    // =========================================================
	    // 6. 그룹 2 : H07 + H08 + H10 → H10 보정
	    // =========================================================
	    if (!m07.isEmpty() && m07.equals(m08) && m07.equals(m10)) {

	        double sum2 = h07 + h08 + h10;
	        if (sum2 <= 1.0) {
	            h10New = h10 + (1.0 - sum2);
	            applyGrp2 = "Y";
	        }
	    }

	    // =========================================================
	    // 7. 결과 세팅
	    // =========================================================
	    DecimalFormat df = new DecimalFormat("0.00");
	    result.put("H02_QTY_NEW", df.format(h02New));
	    result.put("H04_QTY_NEW", df.format(h04New));
	    result.put("H05_QTY_NEW", df.format(h05New));

	    result.put("H07_QTY_NEW", df.format(h07New));
	    result.put("H08_QTY_NEW", df.format(h08New));
	    result.put("H10_QTY_NEW", df.format(h10New));

	    result.put("APPLY_H02_H04_H05", applyGrp1);
	    result.put("APPLY_H07_H08_H10", applyGrp2);
	    result.put(
	        "APPLY_FUNCTION",
	        ("Y".equals(applyGrp1) && "Y".equals(applyGrp2)) ? "Y" : "N"
	    );

	    result.put("FUNCTION_SUM_MAT_QTY", "Y");
	    return result;
	}

	
	public BlockVariantMap FUNCTION_CALSUBWEIGHT(Map elvEnt, List<Map> floorMasterList, Map partInfo)
	        throws Exception {

		BlockVariantMap result = new BlockVariantMap();
		 
	    // 입력값 읽기
	    String Xs  = BlockUtil.NVL(elvEnt.get("SUB_WT_1"), "");
	    String As  = BlockUtil.NVL(elvEnt.get("SUB_H_1"), "");
	    String P1s = BlockUtil.NVL(elvEnt.get("SUB_P_1"), "");
 
	    String Ys  = BlockUtil.NVL(elvEnt.get("SUB_WT_2"), "");
	    String Bs  = BlockUtil.NVL(elvEnt.get("SUB_H_2"), "");
	    String P2s = BlockUtil.NVL(elvEnt.get("SUB_P_2"), "");
 
	    String AA_s = BlockUtil.NVL(elvEnt.get("SUB_NEED_WT"), "");
	    String BB_s = BlockUtil.NVL(elvEnt.get("SUB_MAX_LOAD_H"), "");
 
	    // 정수만 허용
	    if (!isPositiveNumber(Xs) || !isPositiveNumber(As) || !isPositiveNumber(P1s)
	            || !isPositiveNumber(Ys) || !isPositiveNumber(Bs) || !isPositiveNumber(P2s)
	            || !isPositiveNumber(AA_s) || !isPositiveNumber(BB_s)) {
 
	        result.put("SUB_BEST_Q1", "ERROR");
	        result.put("SUB_BEST_Q2", "ERROR");
	        return result;
	    }
 
	    // 파싱
	    double X  = Double.parseDouble(Xs);
	    double A  = Double.parseDouble(As);
	    double p1 = Double.parseDouble(P1s);
 
	    double Y  = Double.parseDouble(Ys);
	    double B  = Double.parseDouble(Bs);
	    double p2 = Double.parseDouble(P2s);
 
	    double AA = Double.parseDouble(AA_s);  // 최소 무게
	    double BB = Double.parseDouble(BB_s);  // 최대 두께
 
	    int max = 200;
 
	    boolean found = false;
	    int bestC = -1, bestD = -1;
 
	    double bestCost = Double.MAX_VALUE;
	    double bestDiff1 = Double.MAX_VALUE;  // (sum1 - AA)
	    double bestDiff2 = Double.MAX_VALUE;  // (BB - sum2)
 
	    for (int C = 0; C <= max; C++) {
	        for (int D = 0; D <= max; D++) {
 
	        	double sum1 = X * C + Y * D;
	        	double sum2 = A * C + B * D;
 
	            if (sum1 < AA) continue;   // 무게 조건
	            if (sum2 >= BB) continue;   // 두께 조건 (≤ BB 허용)
 
	            double cost = p1 * C + p2 * D;
 
	            double diff1 = sum1 - AA; // 최소 초과량
	            double diff2 = BB - sum2; // 최대한 근접하도록
 
	            if (cost < bestCost
	                    || (cost == bestCost && (diff1 < bestDiff1
	                    || (diff1 == bestDiff1 && diff2 < bestDiff2)))) {
 
	                bestCost = cost;
	                bestDiff1 = diff1;
	                bestDiff2 = diff2;
	                bestC = C;
	                bestD = D;
	                found = true;
	            }
	        }
	    }
 
	    if (!found) {
	        result.put("SUB_BEST_Q1", "ERROR");
	        result.put("SUB_BEST_Q2", "ERROR");
	    } else {
	        result.put("SUB_BEST_Q1", String.valueOf(bestC));
	        result.put("SUB_BEST_Q2", String.valueOf(bestD));
	    }
 
		result.put("FUNCTION_CALSUBWEIGHT", "Y");
	    return result;
    }
	
	
	// 양의 정수 또는 양의 실수(소수점 포함) 허용
	private boolean isPositiveNumber(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        return str.matches("^\\d+(\\.\\d+)?$");
    }

	/**
	   * 숫자인지 판별하는 메소드 (소수점 포함)
	   * 빈 값("")이나 문자열이 들어오면 false를 반환
	   */
	private boolean isInteger(String str) {
	    if (str == null || str.trim().isEmpty()) return false;
	    return str.matches("-?\\d+"); // 음수/양수 정수만 허용
	}

	 
	public BlockVariantMap FUNCTION_READ_ELEV(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String projectNo = BlockUtil.NVL(elvEnt.get("PROJ_NO"), "");

	 

		BlockVariantMap elvinfo_func = getelvinfo(projectNo);

		Map<String, Integer> blockCountMap = new HashMap<>();

	
		String EL_AUSE  = BlockUtil.NVL(elvinfo_func.get("EL_AUSE"),"");
		String EL_AMAN  = BlockUtil.NVL(elvinfo_func.get("EL_AMAN"),"");
		String EL_ACAPA = BlockUtil.NVL(elvinfo_func.get("EL_ACAPA"),"");
		String EL_AOPEN = BlockUtil.NVL(elvinfo_func.get("EL_AOPEN"),"");
		String EL_ASPD  = BlockUtil.NVL(elvinfo_func.get("EL_ASPD"),"");
		String EL_AFQ   = BlockUtil.NVL(elvinfo_func.get("EL_AFQ"),"");
		String EL_ASTQ  = BlockUtil.NVL(elvinfo_func.get("EL_ASTQ"),"");
		String EL_ADRV  = BlockUtil.NVL(elvinfo_func.get("EL_ADRV"),"");
		String EL_ATYP  = BlockUtil.NVL(elvinfo_func.get("EL_ATYP"),"");
		String EL_ECN   = BlockUtil.NVL(elvinfo_func.get("EL_ECN"),"");
		String EL_ATF   = BlockUtil.NVL(elvinfo_func.get("EL_ATF"),"");
		String EL_EMF   = BlockUtil.NVL(elvinfo_func.get("EL_EMF"),"");
		String EL_AFF   = BlockUtil.NVL(elvinfo_func.get("EL_AFF"),"");
		String EL_ARF   = BlockUtil.NVL(elvinfo_func.get("EL_ARF"),"");
		String EL_ANST  = BlockUtil.NVL(elvinfo_func.get("EL_ANST"),"");
		String EL_AGRS  = BlockUtil.NVL(elvinfo_func.get("EL_AGRS"),"");
		String EL_ACD2  = BlockUtil.NVL(elvinfo_func.get("EL_ACD2"),"");
		String CO_LAND1  = BlockUtil.NVL(elvinfo_func.get("CO_LAND1"),"");
		String EL_ABRAND  = BlockUtil.NVL(elvinfo_func.get("EL_ABRAND"),"");
		String EL_AMS  = BlockUtil.NVL(elvinfo_func.get("EL_AMS"),"");
		String EL_ARDR  = BlockUtil.NVL(elvinfo_func.get("EL_ARDR"),"");
		String EL_ASPC  = BlockUtil.NVL(elvinfo_func.get("EL_ASPC"),"");
		String EL_ASPCD  = BlockUtil.NVL(elvinfo_func.get("EL_ASPCD"),"");
		String EL_ASPLY  = BlockUtil.NVL(elvinfo_func.get("EL_ASPLY"),"");
		String EL_BCLCD  = BlockUtil.NVL(elvinfo_func.get("EL_BCLCD"),"");
		String EL_BCLCD2  = BlockUtil.NVL(elvinfo_func.get("EL_BCLCD2"),"");
		String EL_CHLCDT0  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT0"),"");
		String EL_CHLCDT1  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT1"),"");
		String EL_CHLCDT2  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT2"),"");
		String EL_CHLCDT3  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT3"),"");
		String EL_CHLCDT4  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT4"),"");
		String EL_CHLCDT5  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT5"),"");
		String EL_CHLCDT6  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT6"),"");
		String EL_CHLCDT7  = BlockUtil.NVL(elvinfo_func.get("EL_CHLCDT7"),"");
		String EL_DINTQ  = BlockUtil.NVL(elvinfo_func.get("EL_DINTQ"),"");
		String EL_DSV1  = BlockUtil.NVL(elvinfo_func.get("EL_DSV1"),"");
		String EL_DSV2  = BlockUtil.NVL(elvinfo_func.get("EL_DSV2"),"");
		String EL_ASPSC  = BlockUtil.NVL(elvinfo_func.get("EL_ASPSC"),"");
		String EL_ACONST  = BlockUtil.NVL(elvinfo_func.get("EL_ACONST"),"");
		String EL_AFFQ  = BlockUtil.NVL(elvinfo_func.get("EL_AFFQ"),"");
		String EL_AMNO  = BlockUtil.NVL(elvinfo_func.get("EL_AMNO"),"");
		String EL_ANSTQ  = BlockUtil.NVL(elvinfo_func.get("EL_ANSTQ"),"");
		String EL_ARFQ  = BlockUtil.NVL(elvinfo_func.get("EL_ARFQ"),"");
		String EL_ASNO  = BlockUtil.NVL(elvinfo_func.get("EL_ASNO"),"");
		String EL_BCLCDQ  = BlockUtil.NVL(elvinfo_func.get("EL_BCLCDQ"),"");
		String EL_EFLOORQ0  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ0"),"");
		String EL_EFLOORQ1  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ1"),"");
		String EL_EFLOORQ2  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ2"),"");
		String EL_EFLOORQ3  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ3"),"");
		String EL_EFLOORQ4  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ4"),"");
		String EL_EFLOORQ5  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ5"),"");
		String EL_EFLOORQ6  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ6"),"");
		String EL_EFLOORQ7  = BlockUtil.NVL(elvinfo_func.get("EL_EFLOORQ7"),"");
		String EL_AARRT  = BlockUtil.NVL(elvinfo_func.get("EL_AARRT"),"");
		String EL_ECGP  = BlockUtil.NVL(elvinfo_func.get("EL_ECGP"),"");
		String EL_ESPBS  = BlockUtil.NVL(elvinfo_func.get("EL_ESPBS"),"");
		String EL_ECWTP  = BlockUtil.NVL(elvinfo_func.get("EL_ECWTP"),"");
		String EL_DETS  = BlockUtil.NVL(elvinfo_func.get("EL_DETS"),"");
		String EL_DCCA   = BlockUtil.NVL(elvinfo_func.get("EL_DCCA"),"");
		String EL_AARGRP   = BlockUtil.NVL(elvinfo_func.get("EL_AARGRP"),"");
		String EL_DELDTY   = BlockUtil.NVL(elvinfo_func.get("EL_DELDTY"),"");
		String remarks  = BlockUtil.NVL(elvinfo_func.get("remarks"),"");
		
		result.put("O_EL_AUSE", EL_AUSE);
		result.put("O_EL_AMAN", EL_AMAN);
		result.put("O_EL_ACAPA", EL_ACAPA);
		result.put("O_EL_AOPEN", EL_AOPEN);
		result.put("O_EL_ASPD", EL_ASPD);
		result.put("O_EL_AFQ", EL_AFQ);
		result.put("O_EL_ASTQ", EL_ASTQ);
		result.put("O_EL_ADRV", EL_ADRV);
		result.put("O_EL_ATYP", EL_ATYP);
		result.put("O_EL_ECN", EL_ECN);
		result.put("O_EL_ATF", EL_ATF);
		result.put("O_EL_EMF", EL_EMF);
		result.put("O_EL_AFF", EL_AFF);
		result.put("O_EL_ARF", EL_ARF);
		result.put("O_EL_ANST", EL_ANST);
		result.put("O_EL_AGRS", EL_AGRS);
		result.put("O_EL_ACD2", EL_ACD2);
		result.put("O_CO_LAND1", CO_LAND1);
		result.put("O_EL_ABRAND", EL_ABRAND);
		result.put("O_EL_AMS", EL_AMS);
		result.put("O_EL_ARDR", EL_ARDR);
		result.put("O_EL_ASPC", EL_ASPC);
		result.put("O_EL_ASPCD", EL_ASPCD);
		result.put("O_EL_ASPLY", EL_ASPLY);
		result.put("O_EL_BCLCD", EL_BCLCD);
		result.put("O_EL_BCLCD2", EL_BCLCD2);
		result.put("O_EL_CHLCDT0", EL_CHLCDT0);
		result.put("O_EL_CHLCDT1", EL_CHLCDT1);
		result.put("O_EL_CHLCDT2", EL_CHLCDT2);
		result.put("O_EL_CHLCDT3", EL_CHLCDT3);
		result.put("O_EL_CHLCDT4", EL_CHLCDT4);
		result.put("O_EL_CHLCDT5", EL_CHLCDT5);
		result.put("O_EL_CHLCDT6", EL_CHLCDT6);
		result.put("O_EL_CHLCDT7", EL_CHLCDT7);
		result.put("O_EL_DINTQ", EL_DINTQ);
		result.put("O_EL_DSV1", EL_DSV1);
		result.put("O_EL_DSV2", EL_DSV2);
		result.put("O_EL_ASPSC", EL_ASPSC);
		result.put("O_EL_ACONST", EL_ACONST);
		result.put("O_EL_AFFQ", EL_AFFQ);
		result.put("O_EL_AMNO", EL_AMNO);
		result.put("O_EL_ANSTQ", EL_ANSTQ);
		result.put("O_EL_ARFQ", EL_ARFQ);
		result.put("O_EL_ASNO", EL_ASNO);
		result.put("O_EL_BCLCDQ", EL_BCLCDQ);
		result.put("O_EL_EFLOORQ0", EL_EFLOORQ0);
		result.put("O_EL_EFLOORQ1", EL_EFLOORQ1);
		result.put("O_EL_EFLOORQ2", EL_EFLOORQ2);
		result.put("O_EL_EFLOORQ3", EL_EFLOORQ3);
		result.put("O_EL_EFLOORQ4", EL_EFLOORQ4);
		result.put("O_EL_EFLOORQ5", EL_EFLOORQ5);
		result.put("O_EL_EFLOORQ6", EL_EFLOORQ6);
		result.put("O_EL_EFLOORQ7", EL_EFLOORQ7);
		result.put("O_EL_AARRT", EL_AARRT);
		result.put("O_EL_ECGP", EL_ECGP);
		result.put("O_EL_ESPBS", EL_ESPBS);
		result.put("O_EL_ECWTP", EL_ECWTP);
		result.put("O_EL_DETS", EL_DETS);
		result.put("O_EL_DCCA", EL_DCCA);
		result.put("O_EL_AARGRP", EL_AARGRP);
		result.put("O_EL_DELDTY", EL_DELDTY);
		result.put("O_remarks", remarks);

		result.put("FUNCTION_READ_ELEV", "Y");
		return result;
	}
	
	public BlockVariantMap FUNCTION_READ_ZPPT027(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String hogiNum  = BlockUtil.NVL(elvEnt.get("EL_ZORINO"),"");
		BlockComDb commonDbDao = new BlockComDb();
		List<Map> ZPPT027DATA = commonDbDao.getZPPT027DATA(hogiNum);

		
		  SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
	        
			Calendar cal = Calendar.getInstance();
	        
			cal.add(Calendar.DATE,  60);
	        
			String tempDate= dtFormat.format(cal.getTime());

		if(ZPPT027DATA.size()==0) {
			result.put("MANDT", "100"  );
			result.put("VBELN", hogiNum.substring(0,6)  );
			result.put("SHIP_A",tempDate );
			result.put("SHIP_B",tempDate );
			result.put("SHIP_C",tempDate );
			result.put("SHIP_D",tempDate );
			result.put("SHIP_E",tempDate );
			result.put("SHIP_F",tempDate );
			result.put("SHIP_MIN_A",tempDate);
            result.put("SHIP_MIN_B",tempDate);
            result.put("SHIP_MIN_C",tempDate);
            result.put("SHIP_MIN_D",tempDate);
            result.put("SHIP_MIN_E",tempDate);
            result.put("SHIP_MIN_F",tempDate);
    		return result;
		}
		for(Map<String, Object> ZPPT027 : ZPPT027DATA){
			String WBS = BlockUtil.NVL(ZPPT027.get("WBS"),"");
			String MANDT = BlockUtil.NVL(ZPPT027.get("MANDT"),"");
			String VBELN = BlockUtil.NVL(ZPPT027.get("VBELN"),"");
			String SHIP_A = BlockUtil.NVL(ZPPT027.get("SHIP_A"),tempDate);
			String SHIP_B = BlockUtil.NVL(ZPPT027.get("SHIP_B"),tempDate);
			String SHIP_C = BlockUtil.NVL(ZPPT027.get("SHIP_C"),tempDate);
			String SHIP_D = BlockUtil.NVL(ZPPT027.get("SHIP_D"),tempDate);
			String SHIP_E = BlockUtil.NVL(ZPPT027.get("SHIP_E"),tempDate);
			String SHIP_F = BlockUtil.NVL(ZPPT027.get("SHIP_F"),tempDate);
			String SHIP_MIN_A = BlockUtil.NVL(ZPPT027.get("SHIP_MIN_A"),tempDate);
			String SHIP_MIN_B = BlockUtil.NVL(ZPPT027.get("SHIP_MIN_B"),tempDate);
			String SHIP_MIN_C = BlockUtil.NVL(ZPPT027.get("SHIP_MIN_C"),tempDate);
			String SHIP_MIN_D = BlockUtil.NVL(ZPPT027.get("SHIP_MIN_D"),tempDate);
			String SHIP_MIN_E = BlockUtil.NVL(ZPPT027.get("SHIP_MIN_E"),tempDate);
			String SHIP_MIN_F = BlockUtil.NVL(ZPPT027.get("SHIP_MIN_F"),tempDate);

		
			result.put("MANDT", MANDT  );
			result.put("VBELN", VBELN  );
			result.put("SHIP_A",SHIP_A );
			result.put("SHIP_B",SHIP_B );
			result.put("SHIP_C",SHIP_C );
			result.put("SHIP_D",SHIP_D );
			result.put("SHIP_E",SHIP_E );
			result.put("SHIP_F",SHIP_F );
			result.put("SHIP_MIN_A",SHIP_MIN_A);
            result.put("SHIP_MIN_B",SHIP_MIN_B);
            result.put("SHIP_MIN_C",SHIP_MIN_C);
            result.put("SHIP_MIN_D",SHIP_MIN_D);
            result.put("SHIP_MIN_E",SHIP_MIN_E);
            result.put("SHIP_MIN_F",SHIP_MIN_F);
			
		}
		return result;
	}

	public BlockVariantMap FUNCTION_READ_CONTRACT_STATUS(Map elvEnt,  List<Map> floorMasterList, Map partInfo)
			throws Exception {
		BlockVariantMap result = new BlockVariantMap();
		String hogiNum  = BlockUtil.NVL(elvEnt.get("V_STRING"),"");//V_STRING 송근원 매니저 요청
		
		BlockComDb commonDbDao = new BlockComDb();
		List<Map> ZMASTER02 = commonDbDao.getZMASTER02TXT04(hogiNum);

		
		if(ZMASTER02.size()!=0) {
			  result.put("CONTRACT_STATUS","C");
    	
		}else {
			 result.put("CONTRACT_STATUS","");
		}
		return result;
	}

	private 	 BlockVariantMap getelvinfo(String hogiNum_project_no)
	{
		BlockVariantMap resultList = new BlockVariantMap();
		Connection con = null;
		PreparedStatement pstmt = null;
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;

		try
		{
			con = PLMDBConnection.getConnection();

			sql.append(" select cod(EL_AUSE) EL_AUSE,EL_AMAN EL_AMAN,cod(EL_ACAPA) EL_ACAPA,"
					+" cod(CO_LAND1) CO_LAND1, cod(EL_ABRAND) EL_ABRAND, cod(EL_AMS) EL_AMS, cod(EL_ARDR) EL_ARDR,"
					+"cod(EL_ASPC) EL_ASPC, cod(EL_ASPCD) EL_ASPCD, cod(EL_ASPLY) EL_ASPLY, cod(EL_BCLCD) EL_BCLCD, cod(EL_BCLCD2) EL_BCLCD2,"
					+"cod(EL_CHLCDT0) EL_CHLCDT0, cod(EL_CHLCDT1) EL_CHLCDT1, cod(EL_CHLCDT2) EL_CHLCDT2, cod(EL_CHLCDT3) EL_CHLCDT3, cod(EL_CHLCDT4) EL_CHLCDT4, cod(EL_CHLCDT5) EL_CHLCDT5, cod(EL_CHLCDT6) EL_CHLCDT6, cod(EL_CHLCDT7) EL_CHLCDT7,"
					+" cod(EL_DINTQ) EL_DINTQ, cod(EL_DSV1) EL_DSV1, cod(EL_DSV2) EL_DSV2, cod(EL_ASPSC) EL_ASPSC,"
					+"EL_ACONST,EL_AFFQ,EL_AMNO,EL_ANSTQ,EL_ARFQ,EL_ASNO,EL_BCLCDQ,EL_EFLOORQ0,EL_EFLOORQ1,EL_EFLOORQ2,EL_EFLOORQ3,EL_EFLOORQ4,EL_EFLOORQ5,EL_EFLOORQ6,EL_EFLOORQ7,remarks,"
					+ "cod(EL_AOPEN) EL_AOPEN,cod(EL_ASPD) EL_ASPD"
					+ ",EL_AFQ,EL_ASTQ,cod(EL_ADRV) EL_ADRV,cod(EL_ATYP) EL_ATYP,EL_ECN,EL_ATF,cod(EL_ACD2) EL_ACD2,"
					+ "EL_EMF,EL_AFF,EL_ARF,EL_ANST,cod(EL_AGRS) EL_AGRS, "
					+ "cod(EL_AARRT) EL_AARRT, cod(EL_ECGP) EL_ECGP, cod(EL_ESPBS) EL_ESPBS, cod(EL_ECWTP) EL_ECWTP, cod(EL_DETS) EL_DETS, cod(EL_DCCA) EL_DCCA, EL_AARGRP,cod(EL_DELDTY) EL_DELDTY"
					+ " from elv_info$vf, elv_info$id where vf$ouid=id$wip and md$Number =? ");

			pstmt = con.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++,hogiNum_project_no);

			rs = pstmt.executeQuery();

			while(rs.next())
			{
				resultList.put("EL_AUSE",  BlockUtil.NVL(rs.getString("EL_AUSE"),""));
				resultList.put("EL_AMAN",  BlockUtil.NVL(rs.getString("EL_AMAN"),""));
				resultList.put("EL_ACAPA", BlockUtil.NVL(rs.getString("EL_ACAPA"),""));
				resultList.put("EL_AOPEN", BlockUtil.NVL(rs.getString("EL_AOPEN"),""));
				resultList.put("EL_ASPD",  BlockUtil.NVL(rs.getString("EL_ASPD"),""));
				resultList.put("EL_AFQ",   BlockUtil.NVL(rs.getString("EL_AFQ"),""));
				resultList.put("EL_ASTQ",  BlockUtil.NVL(rs.getString("EL_ASTQ"),""));
				resultList.put("EL_ADRV",  BlockUtil.NVL(rs.getString("EL_ADRV"),""));
				resultList.put("EL_ATYP",  BlockUtil.NVL(rs.getString("EL_ATYP"),""));
				resultList.put("EL_ECN",   BlockUtil.NVL(rs.getString("EL_ECN"),""));
				resultList.put("EL_ATF",   BlockUtil.NVL(rs.getString("EL_ATF"),""));
				resultList.put("EL_EMF",   BlockUtil.NVL(rs.getString("EL_EMF"),""));
				resultList.put("EL_AFF",   BlockUtil.NVL(rs.getString("EL_AFF"),""));
				resultList.put("EL_ARF",   BlockUtil.NVL(rs.getString("EL_ARF"),""));
				resultList.put("EL_ANST",  BlockUtil.NVL(rs.getString("EL_ANST"),""));
				resultList.put("EL_AGRS",  BlockUtil.NVL(rs.getString("EL_AGRS"),""));
				resultList.put("EL_ACD2",  BlockUtil.NVL(rs.getString("EL_ACD2"),""));
				resultList.put("CO_LAND1",  BlockUtil.NVL(rs.getString("CO_LAND1"),""));
				resultList.put("EL_ABRAND",  BlockUtil.NVL(rs.getString("EL_ABRAND"),""));
				resultList.put("EL_AMS",  BlockUtil.NVL(rs.getString("EL_AMS"),""));
				resultList.put("EL_ARDR",  BlockUtil.NVL(rs.getString("EL_ARDR"),""));
				resultList.put("EL_ASPC",  BlockUtil.NVL(rs.getString("EL_ASPC"),""));
				resultList.put("EL_ASPCD",  BlockUtil.NVL(rs.getString("EL_ASPCD"),""));
				resultList.put("EL_ASPLY",  BlockUtil.NVL(rs.getString("EL_ASPLY"),""));
				resultList.put("EL_BCLCD",  BlockUtil.NVL(rs.getString("EL_BCLCD"),""));
				resultList.put("EL_BCLCD2",  BlockUtil.NVL(rs.getString("EL_BCLCD2"),""));
				resultList.put("EL_CHLCDT0",  BlockUtil.NVL(rs.getString("EL_CHLCDT0"),""));
				resultList.put("EL_CHLCDT1",  BlockUtil.NVL(rs.getString("EL_CHLCDT1"),""));
				resultList.put("EL_CHLCDT2",  BlockUtil.NVL(rs.getString("EL_CHLCDT2"),""));
				resultList.put("EL_CHLCDT3",  BlockUtil.NVL(rs.getString("EL_CHLCDT3"),""));
				resultList.put("EL_CHLCDT4",  BlockUtil.NVL(rs.getString("EL_CHLCDT4"),""));
				resultList.put("EL_CHLCDT5",  BlockUtil.NVL(rs.getString("EL_CHLCDT5"),""));
				resultList.put("EL_CHLCDT6",  BlockUtil.NVL(rs.getString("EL_CHLCDT6"),""));
				resultList.put("EL_CHLCDT7",  BlockUtil.NVL(rs.getString("EL_CHLCDT7"),""));
				resultList.put("EL_DINTQ",  BlockUtil.NVL(rs.getString("EL_DINTQ"),""));
				resultList.put("EL_DSV1",  BlockUtil.NVL(rs.getString("EL_DSV1"),""));
				resultList.put("EL_DSV2",  BlockUtil.NVL(rs.getString("EL_DSV2"),""));
				resultList.put("EL_ASPSC",  BlockUtil.NVL(rs.getString("EL_ASPSC"),""));
				resultList.put("EL_ACONST",  BlockUtil.NVL(rs.getString("EL_ACONST"),""));
				resultList.put("EL_AFFQ",  BlockUtil.NVL(rs.getString("EL_AFFQ"),""));
				resultList.put("EL_AMNO",  BlockUtil.NVL(rs.getString("EL_AMNO"),""));
				resultList.put("EL_ANSTQ",  BlockUtil.NVL(rs.getString("EL_ANSTQ"),""));
				resultList.put("EL_ARFQ",  BlockUtil.NVL(rs.getString("EL_ARFQ"),""));
				resultList.put("EL_ASNO",  BlockUtil.NVL(rs.getString("EL_ASNO"),""));
				resultList.put("EL_BCLCDQ",  BlockUtil.NVL(rs.getString("EL_BCLCDQ"),""));
				resultList.put("EL_EFLOORQ0",  BlockUtil.NVL(rs.getString("EL_EFLOORQ0"),""));
				resultList.put("EL_EFLOORQ1",  BlockUtil.NVL(rs.getString("EL_EFLOORQ1"),""));
				resultList.put("EL_EFLOORQ2",  BlockUtil.NVL(rs.getString("EL_EFLOORQ2"),""));
				resultList.put("EL_EFLOORQ3",  BlockUtil.NVL(rs.getString("EL_EFLOORQ3"),""));
				resultList.put("EL_EFLOORQ4",  BlockUtil.NVL(rs.getString("EL_EFLOORQ4"),""));
				resultList.put("EL_EFLOORQ5",  BlockUtil.NVL(rs.getString("EL_EFLOORQ5"),""));
				resultList.put("EL_EFLOORQ6",  BlockUtil.NVL(rs.getString("EL_EFLOORQ6"),""));
				resultList.put("EL_EFLOORQ7",  BlockUtil.NVL(rs.getString("EL_EFLOORQ7"),""));
				resultList.put("EL_AARRT",  BlockUtil.NVL(rs.getString("EL_AARRT"),""));
				resultList.put("EL_ECGP",  BlockUtil.NVL(rs.getString("EL_ECGP"),""));
				resultList.put("EL_ESPBS",  BlockUtil.NVL(rs.getString("EL_ESPBS"),""));
				resultList.put("EL_ECWTP",  BlockUtil.NVL(rs.getString("EL_ECWTP"),""));
				resultList.put("EL_DETS",  BlockUtil.NVL(rs.getString("EL_DETS"),""));
				resultList.put("EL_DCCA",  BlockUtil.NVL(rs.getString("EL_DCCA"),""));
				resultList.put("EL_AARGRP",  BlockUtil.NVL(rs.getString("EL_AARGRP"),""));
				resultList.put("EL_DELDTY",  BlockUtil.NVL(rs.getString("EL_DELDTY"),""));
				resultList.put("remarks",  BlockUtil.NVL(rs.getString("remarks"),""));


			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PLMDBConnection.disconnect(con, pstmt, rs);
		}

		return resultList;
	}

    public BlockVariantMap CAL_B_MOLD_FIND(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception
    {
              BlockVariantMap resultMap = new BlockVariantMap();
              String BUTTON_MOLD = BlockUtil.NVL(elvEnt.get("BUTTON_MOLD"),"");
              StringBuilder sb = new StringBuilder();
              
              List<String> indicationSpecList = new ArrayList<String>();
              
              indicationSpecList.add("EL_AFF");
              indicationSpecList.add("EL_ARF");
              
              try
              {
                         if(!BUTTON_MOLD.equals(""))
                         {
                                    for(String spec : indicationSpecList)
                                    {
                                              String specValue = BlockUtil.NVL(elvEnt.get(spec), "");//B1.B11.B4
                                              String[] indications;
                                              if(specValue.contains(",")) {
                                              indications = specValue.split("[,~ -]");
                                              }else {
                                              indications = specValue.split("[,~ -.]");        
                                              }
                                              
                                              for(String indication : indications)
                                              {
                                    
                                                         if(indication != null && !indication.equals("") && BlockUtil.compareData(BUTTON_MOLD, indication) == false)
                                                         {
                                                                    if(sb.toString().length() == 0)
                                                                              sb.append(indication);
                                                                    else
                                                                              sb.append(","+indication);
                                                         }
                                              }
                                    }
                         }
                         
                         resultMap.put("O_BUTTON_MOLD", sb.toString());
              }
              catch(Exception e)
              {
            	  resultMap.put("O_BUTTON_MOLD","");e.printStackTrace();
              }
              
              return resultMap;
    }

    public BlockVariantMap CAL_VOICE_FIND(Map elvEnt,  List<Map> floorMasterList, Map partInfo) throws Exception
    {
              BlockVariantMap resultMap = new BlockVariantMap();
              String VOICE_LIST = BlockUtil.NVL(elvEnt.get("VOICE_LIST"),"");
              StringBuilder sb = new StringBuilder();
              
              List<String> indicationSpecList = new ArrayList<String>();
              
              indicationSpecList.add("EL_AFF");
              indicationSpecList.add("EL_ARF");
              
              try
              {
                         for(String spec : indicationSpecList)
                         {
                                    String specValue = BlockUtil.NVL(elvEnt.get(spec), "");
                                    String[] indications;
                                    if(specValue.contains(",")) {
                                    indications = specValue.split("[,~ -]");
                                    }else {
                                    indications = specValue.split("[,~ -.]");        
                                    }
                                    
                                    for(String indication : indications)
                                    {
                                              
                                              if(indication != null && !indication.equals("") && BlockUtil.compareData(VOICE_LIST, indication) == false)
                                              {
                                                         if(sb.toString().length() == 0)
                                                                    sb.append(indication);
                                                         else
                                                                    sb.append(","+indication);
                                              }
                                    }
                         }
                         
                         resultMap.put("O_VOICE_LIST", sb.toString());
              }
              catch(Exception e)
              {
            	  resultMap.put("O_VOICE_LIST","");
                         e.printStackTrace();
              }
              
              return resultMap;
    }

   

}