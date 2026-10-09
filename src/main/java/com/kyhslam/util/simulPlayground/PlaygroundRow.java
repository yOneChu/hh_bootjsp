package com.kyhslam.util.simulPlayground;

import java.util.ArrayList;
import java.util.List;

/**
 * PID 로직 한 라인 (variant_d 1행) - 화면 편집 / 수정본 실행 공용.
 * 빈 문자열은 실행 시 null 로 바꾼다 (DB 의 빈 칸과 동일하게 취급).
 */
public class PlaygroundRow {

	/** variant_d.NO (화면 라인 번호, 새 라인이면 null) */
	public String no;
	/** variant_d.DOUID (새 라인이면 null → 실행 시 임시 DOUID 부여) */
	public String douid;
	public String addr;
	/** variant_d.GOTO */
	public String gotoAddr;
	public String remarks;
	/** SPEC1 ~ SPEC30 (index 0 = SPEC1) */
	public List<String> spec = new ArrayList<String>();
	/** CON1 ~ CON30 */
	public List<String> con = new ArrayList<String>();
	/** KEY1 ~ KEY20 */
	public List<String> key = new ArrayList<String>();
	/** VAL1 ~ VAL20 */
	public List<String> val = new ArrayList<String>();
}
