package com.kyhslam.util.simulPlayground;

import com.kyhslam.util.pidSimulatorUp.SimDb;
import com.kyhslam.util.pidSimulatorUp.SimPidRepository;
import com.kyhslam.util.pidSimulatorUp.SimVariantMap;

import java.util.List;

/**
 * 대상 PID 의 로직만 수정본으로 바꿔 주는 PID 저장소 (요청 1건 동안만 사용, DB 에는 저장하지 않는다).
 * 그 외 PID(CALL 하위 PID, 전처리 PID)는 SimPidRepository 그대로 DB 에서 읽는다.
 */
public class PlaygroundPidRepository extends SimPidRepository {

	private final String targetPid;
	/** null 이면 수정본 없음 (DB 원본) */
	private final List<PlaygroundRow> draftRows;

	public PlaygroundPidRepository(String targetPid, List<PlaygroundRow> draftRows) {
		this.targetPid = targetPid;
		this.draftRows = draftRows;
	}

	public boolean hasDraft() {
		return draftRows != null;
	}

	private boolean isDraft(String pid) {
		return draftRows != null && targetPid != null && targetPid.equals(pid);
	}

	@Override
	public SimVariantMap getLogic(SimDb db, String pid, int version) {
		return isDraft(pid) ? PlaygroundLogic.toLogicMap(draftRows, false) : super.getLogic(db, pid, version);
	}

	@Override
	public SimVariantMap getLogic(SimDb db, String pid, int version, boolean doDebug) {
		return isDraft(pid) ? PlaygroundLogic.toLogicMap(draftRows, doDebug) : super.getLogic(db, pid, version, doDebug);
	}
}
