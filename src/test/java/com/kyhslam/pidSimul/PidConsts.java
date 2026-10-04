package com.kyhslam.pidSimul;

/**
 * dyna.plmetc.spring.service.pid.PidConsts 의 독립 버전
 */
public class PidConsts {
	public static final String LASTEST = "lastest";
	public static final String TEST = "test";
	public static final String FLOOREXPANDS = "Y";

	/** Pid.JAVA / Pid.DB (variant_h.METHOD) */
	public static final String METHOD_JAVA = "JAVA";
	public static final String METHOD_DB = "DB";

	/** Variant.TESTversion */
	public static final int TEST_VERSION = -1;

	/** Variant.maxSpecFieldSize / maxResFieldSize */
	public static final int MAX_SPEC_FIELD_SIZE = 30;
	public static final int MAX_RES_FIELD_SIZE = 20;

	/** FloorConst.FIELD_NAME_FLOOR_NAME */
	public static final String FIELD_NAME_FLOOR_NAME = "FLOOR_NAME";

	/** SubaeConstants.ELVINFO_CLASS_OUID / FLOORINFO_CLASS_OUID / ELVANDFLOOR_ASSO_OUID */
	public static final String ELVINFO_CLASS_OUID = "860cebeb";
	public static final String FLOOR_CLASS_OUID = "860cece3";

	/**
	 * 상위 클래스 OUID (16진수) - HDEL_SYSTEM.DOSSUPERCLAS 대체.
	 * DOSClassHelper.listAllSuperClassOuid 결과 순서대로 넣는다. (md$number 등 상속 필드를 읽기 위해 필요)
	 * TODO 값 확인 필요 : 아래 쿼리 결과로 채울 것 (hdel_system 계정)
	 *   SELECT LOWER(TO_CHAR(DOSCLAS,'xxxxxxxx')) DOSCLAS, LOWER(TO_CHAR(SUPCLAS,'xxxxxxxx')) SUPCLAS, LEVEL
	 *     FROM HDEL_SYSTEM.DOSSUPERCLAS
	 *    START WITH DOSCLAS IN (TO_NUMBER('860cebeb','xxxxxxxx'), TO_NUMBER('860cece3','xxxxxxxx'))
	 *  CONNECT BY NOCYCLE PRIOR SUPCLAS = DOSCLAS
	 *    ORDER SIBLINGS BY SEQ;
	 */
	public static final String[] ELVINFO_SUPER_CLASS_OUIDS = {};
	public static final String[] FLOOR_SUPER_CLASS_OUIDS = {};
	public static final String ELVANDFLOOR_ASSO_OUID = "860cedaf";

	/**
	 * 층 정보 테이블 코드 - HDEL_SYSTEM.DOSCLAS 대체 (층 클래스 860cece3 의 CODE. ex) xxx → xxx$vf)
	 * TODO 값 확인 필요 : SELECT LOWER(CODE) FROM HDEL_SYSTEM.DOSCLAS WHERE OUID = TO_NUMBER('860cece3','xxxxxxxx');
	 */
	public static final String FLOOR_TABLE_CODE = "";
	/** 층 정보 테이블이 버전관리(vf) 인지 여부 (false 면 sf) */
	public static final boolean FLOOR_TABLE_VERSIONABLE = true;
	/**
	 * 공사정보-층 연결 테이블 코드 - HDEL_SYSTEM.DOSASSO 대체 (연결 860cedaf 의 CODE. ex) xxx → xxx$ac)
	 * TODO 값 확인 필요 : SELECT LOWER(CODE) FROM HDEL_SYSTEM.DOSASSO WHERE DOSCLAS = TO_NUMBER('860cedaf','xxxxxxxx');
	 */
	public static final String ELVANDFLOOR_ASSO_TABLE_CODE = "";

	/**
	 * getPick 의 층 정보 사용 여부 (pidSimul 의 isfloor 와 같은 방식 : 요청한 경우에만 층 정보를 읽는다)
	 * 기본 false : 층 정보를 읽지 않으므로 층별 블록은 계산되지 않는다.
	 * true 로 하려면 위 FLOOR_TABLE_CODE / ELVANDFLOOR_ASSO_TABLE_CODE 를 반드시 채워야 한다. (-Dpid.floor=Y)
	 */
	public static final boolean USE_FLOOR = "Y".equalsIgnoreCase(System.getProperty("pid.floor", "N"));

	public static final String PREFIX_ELVINFO_OUID = "elv_info$vf@";
}
