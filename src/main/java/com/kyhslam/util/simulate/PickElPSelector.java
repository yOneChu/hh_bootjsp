package com.kyhslam.util.simulate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 특정 블럭만 계산할 때 실행이 필요한 EL_P PID 만 고른다. (PID 로직 정적 분석)
 *
 * 근거
 *  - EL_P PID 들은 서로 독립이다. (PidVariant 가 실행마다 elvEnt 를 clone 하고, 결과는 전체 실행 후 한번에 dataMap 에 반영)
 *  - 따라서 블럭의 PICK 키 / 수량 PID 가 참조하는 키를 OUTPUT 하는 EL_P 만 실행하면 그 블럭의 결과는 전체 실행과 같다.
 *
 * 안전장치 (항상 과포함 방향)
 *  - 참조 키는 SPEC/CON/VAL 텍스트의 모든 식별자 토큰으로 넉넉하게 잡는다.
 *  - 선택된 EL_P 가 참조하는 키도 다시 필요 키에 넣어 반복한다. (층 EL_P 2회차는 1회차 결과를 읽을 수 있음)
 *  - METHOD 가 JAVA 인 EL_P 는 출력을 알 수 없으므로 항상 실행한다.
 *  - CALL 대상 / OUTPUT 키가 {..} 또는 [$..$] 로 동적이거나 순환 CALL 이 있으면 분석을 포기하고 null (= 전체 실행) 을 반환한다.
 */
public class PickElPSelector {

	private static final Pattern TOKEN = Pattern.compile("[A-Za-z_$][A-Za-z0-9_$@]*");

	private final PidDb db;

	/** PID → 로직 정보 (없는 PID 는 null) */
	private final Map<String, PidLogic> logicCache = new HashMap<String, PidLogic>();
	/** PID → CALL 을 따라간 전체 출력/참조 (분석 결과 캐시) */
	private final Map<String, Reach> reachCache = new HashMap<String, Reach>();

	/** 마지막 select 의 판단 근거 (로그용) */
	private String reason = "";

	public PickElPSelector(PidDb db) {
		this.db = db;
	}

	public String getReason() {
		return reason;
	}

	/**
	 * @param elPidList  PickDao.getEL_PList 결과 (영업사양 + 층 EL_P)
	 * @param blockLists 계산할 블럭 목록들 (common / floor)
	 * @return 실행할 EL_P PID 집합. 안전하게 고를 수 없으면 null (= 전체 실행)
	 */
	public Set<String> select(List<Map<String, Object>> elPidList, List<List<PickDao.BlockInfo>> blockLists) throws Exception {
		preloadEL_P();

		// 1. 블럭이 직접 필요로 하는 키 : PICK 키 + 수량 PID 가 참조하는 키
		Set<String> needed = new HashSet<String>();
		for (List<PickDao.BlockInfo> blockList : blockLists) {
			if (blockList == null)
				continue;
			for (PickDao.BlockInfo block : blockList) {
				for (PickDao.PickInfo pickInfo : block.pickList) {
					String pick = PidUtil.NVL(pickInfo.pick, "").trim();
					if (pick.isEmpty())
						continue;
					needed.add(pick);

					String qty = PidUtil.NVL(pickInfo.qty, "").trim();
					if (!qty.isEmpty()) {
						Reach r = reach(qty, new HashSet<String>());
						if (r.dynamic) {
							reason = "수량 PID 에 동적 CALL/OUTPUT 존재 : " + qty;
							return null;
						}
						needed.addAll(r.refs);
					}
				}
			}
		}

		// 2. EL_P 별 출력/참조
		Map<String, Reach> elReach = new LinkedHashMap<String, Reach>();
		Set<String> selected = new LinkedHashSet<String>();
		for (Map<String, Object> pidMap : elPidList) {
			String pid = (String) pidMap.get("PID");
			if (elReach.containsKey(pid))
				continue;
			if (PidConsts.METHOD_JAVA.equals(PidUtil.NVL(pidMap.get("METHOD"), ""))) {
				selected.add(pid);
				elReach.put(pid, new Reach());
				continue;
			}
			Reach r = reach(pid, new HashSet<String>());
			if (r.dynamic) {
				reason = "EL_P 에 동적 CALL/OUTPUT 존재 : " + pid;
				return null;
			}
			elReach.put(pid, r);
		}

		// 3. 필요 키를 출력하는 EL_P 선택 → 그 EL_P 의 참조 키도 필요 키로 (고정점까지 반복)
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Map.Entry<String, Reach> e : elReach.entrySet()) {
				if (selected.contains(e.getKey()))
					continue;
				if (!Collections.disjoint(e.getValue().outputs, needed)) {
					selected.add(e.getKey());
					needed.addAll(e.getValue().refs);
					changed = true;
				}
			}
		}

		reason = "정적 분석 성공";
		return selected;
	}

	// ------------------------------------------------------------------------

	/** CALL 을 따라가며 출력 키 / 참조 키를 모은다 */
	private Reach reach(String pid, Set<String> visiting) throws Exception {
		Reach cached = reachCache.get(pid);
		if (cached != null)
			return cached;

		Reach result = new Reach();
		if (!visiting.add(pid)) {
			// 순환 CALL : 부분 결과가 캐시되면 과소포함될 수 있으므로 분석 포기 (전체 실행)
			result.dynamic = true;
			return result;
		}

		PidLogic logic = getLogic(pid);
		if (logic != null) {
			if (PidConsts.METHOD_JAVA.equals(logic.method)) {
				// PidJavaMethod 는 OUTPUT 키를 만들지 않음. 참조 키는 메소드 내부라 알 수 없으나 EL_P 출력 키를 읽는 메소드는 없다.
			} else {
				result.refs.addAll(logic.refs);
				result.outputs.addAll(logic.outputs);
				result.dynamic |= logic.dynamic;
				for (String callPid : logic.calls) {
					Reach sub = reach(callPid, visiting);
					result.refs.addAll(sub.refs);
					result.outputs.addAll(sub.outputs);
					result.dynamic |= sub.dynamic;
				}
			}
		}

		visiting.remove(pid);
		reachCache.put(pid, result);
		return result;
	}

	/** EL_P 최신 로직을 한 번에 읽어둔다 */
	private void preloadEL_P() throws Exception {
		List<Map<String, Object>> rows = db.queryForList(
				" SELECT A.PID H_PID, A.METHOD H_METHOD, D.* FROM VARIANT_H A "
				+ " JOIN VARIANT_ID I ON A.HOUID = I.LAST_HOUID "
				+ " LEFT JOIN VARIANT_D D ON D.HOUID = A.HOUID "
				+ " WHERE A.PID LIKE 'EL_P%' ");

		Map<String, List<Map<String, Object>>> byPid = new LinkedHashMap<String, List<Map<String, Object>>>();
		Map<String, String> methods = new HashMap<String, String>();
		for (Map<String, Object> row : rows) {
			String pid = (String) row.get("H_PID");
			methods.put(pid, PidUtil.NVL(row.get("H_METHOD"), ""));
			List<Map<String, Object>> list = byPid.get(pid);
			if (list == null) {
				list = new ArrayList<Map<String, Object>>();
				byPid.put(pid, list);
			}
			if (row.get("DOUID") != null)
				list.add(row);
		}
		for (Map.Entry<String, List<Map<String, Object>>> e : byPid.entrySet()) {
			logicCache.put(e.getKey(), parse(methods.get(e.getKey()), e.getValue()));
		}
	}

	private PidLogic getLogic(String pid) throws Exception {
		if (logicCache.containsKey(pid))
			return logicCache.get(pid);

		PidLogic logic = null;
		Map<String, Object> head = db.queryForFirst(
				" SELECT A.HOUID, A.METHOD FROM VARIANT_H A, VARIANT_ID I WHERE A.HOUID = I.LAST_HOUID AND A.PID = ? ", pid);
		if (head != null) {
			List<Map<String, Object>> rows = db.queryForList(" SELECT * FROM VARIANT_D WHERE HOUID = ? ", head.get("HOUID"));
			logic = parse(PidUtil.NVL(head.get("METHOD"), ""), rows);
		}
		logicCache.put(pid, logic);
		return logic;
	}

	private PidLogic parse(String method, List<Map<String, Object>> rows) {
		PidLogic logic = new PidLogic();
		logic.method = method;

		for (Map<String, Object> row : rows) {
			for (int i = 1; i <= PidConsts.MAX_SPEC_FIELD_SIZE; i++) {
				addTokens(logic.refs, (String) row.get("SPEC" + i));
				addTokens(logic.refs, (String) row.get("CON" + i));
			}
			for (int i = 1; i <= PidConsts.MAX_RES_FIELD_SIZE; i++) {
				String key = (String) row.get("KEY" + i);
				if (key == null)
					continue;
				String val = PidUtil.NVL(row.get("VAL" + i), "");
				addTokens(logic.refs, val);

				if (key.equals("CALL") || key.equals("OUTPUT")) {
					if (isDynamic(val)) {
						logic.dynamic = true;
						continue;
					}
					if (key.equals("CALL"))
						logic.calls.add(val);
					else {
						logic.outputs.add(val);
						logic.outputs.add(val.trim());
					}
				}
			}
		}
		return logic;
	}

	private static boolean isDynamic(String val) {
		return val.contains("{") || val.contains("[$");
	}

	private static void addTokens(Set<String> set, String text) {
		if (text == null || text.isEmpty())
			return;
		Matcher m = TOKEN.matcher(text);
		while (m.find()) {
			String token = m.group();
			set.add(token);
			if (token.endsWith("_CODE_NAME") && token.length() > 10)
				set.add(token.substring(0, token.length() - 10));
		}
	}

	private static class PidLogic {
		String method;
		final Set<String> refs = new HashSet<String>();
		final Set<String> outputs = new HashSet<String>();
		final List<String> calls = new ArrayList<String>();
		boolean dynamic = false;
	}

	private static class Reach {
		final Set<String> refs = new HashSet<String>();
		final Set<String> outputs = new HashSet<String>();
		boolean dynamic = false;
	}
}
