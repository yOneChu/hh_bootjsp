package com.kyhslam.bomCal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * BOM 계산 상수 (DOS 메타데이터 조회 대신 사용)
 * 원본 : SubaeConstants, FloorConst, EBomConst, ProductConsts, Variant, DOSImpl
 */
public class BomConsts {

	private BomConsts() {}

	// ---------------------------------------------------------------- ouid prefix
	public static final String PREFIX_ELVINFO_OUID = "elv_info$vf@";
	public static final String PREFIX_SHIPELVINFO_OUID = "shipelv_info$vf@";
	public static final String PREFIX_SVELVINFO_OUID = "monitorstation_info$vf@";
	public static final String PREFIX_PRODUCT_OUID = "product$vf@";
	public static final String PREFIX_NORMALPART_OUID = "normalpart$vf@";

	// ---------------------------------------------------------------- class / association ouid
	public static final String ELVINFO_CLASS_OUID = "860cebeb";
	public static final String FLOORINFO_CLASS_OUID = "860cece3";
	public static final String ELVANDFLOOR_ASSO_OUID = "860cedaf";

	/** 공사정보-제품 연결 (SubaeConstants.ELVANDPRODUCT_ASSO_OUID) */
	public static final String ELVANDPRODUCT_ASSO_OUID = "9502fa58";

	/**
	 * 공사정보-제품 연결 테이블 코드 - HDEL_SYSTEM.DOSASSO 대체 (연결 9502fa58 의 CODE. ex) xxx → xxx$ac)
	 * TODO 값 확인 필요 : SELECT LOWER(CODE) FROM HDEL_SYSTEM.DOSASSO WHERE DOSCLAS = TO_NUMBER('9502fa58','xxxxxxxx');
	 * 비어 있으면 연결된 제품을 찾지 않고 wip 제품(md$number = 호기)으로 대신한다.
	 */
	public static final String ELVANDPRODUCT_ASSO_TABLE_CODE = "";

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
	 * FLOOR_TABLE_CODE 와 함께 비어 있으면 층 정보를 읽지 않는다. (층별 블록은 계산되지 않음)
	 */
	public static final String ELVANDFLOOR_ASSO_TABLE_CODE = "";

	// ---------------------------------------------------------------- product (SubaeConstants)
	/** 제품의 블록옵션별 계산완료 필드 / 블록옵션 */
	public static final String[] BLOCK_OPT_FIELD_LIST = { "option_c", "option_m", "option_f", "option_1", "option_2", "option_3" };
	public static final String[] BLOCK_OPT_LIST = { "C", "M", "F", "1", "2", "3" };
	/** 서비스(SV) 공사정보는 블록옵션 1,2,3,M,F 를 추가한다 (SubaeManager.bomCalculate) */
	public static final String[] SV_ADD_OPT_LIST = { "1", "2", "3", "M", "F" };

	/** BLOCKNO$SF.BLOCK_STATUS 사용 상태 (SubaeDao.xml getBlockList) */
	public static final long BLOCK_STATUS_ACTIVE = 2466425004L;

	/** KC01 사양에서 블록 공급구분을 확인하는 PID */
	public static final String PID_CAL_BOM_SUPPLIER_DESIGN = "CAL_BOM_SUPPLIER_DESIGN";

	// ---------------------------------------------------------------- field
	public static final String FIELD_NAME_FLOOR_NAME = "FLOOR_NAME";
	public static final String FIELD_NAME_INDEX = "md$index";

	/** DOS 필드로 노출되지 않는 시스템 컬럼 (영업사양 dataMap 에서 제외) */
	public static final Set<String> EXCLUDE_SPEC_COLUMNS = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList("vf$ouid", "vf$identity", "sf$ouid")));

	/** 컬럼명 → DOS 필드명 이 다른 경우 */
	public static final String COLUMN_MD_DESC = "md$desc";
	public static final String FIELD_MD_DESCRIPTION = "md$description";

	/** DOSImpl.CODE_FIELD_DISPLAY_METHOD (codeFieldDisplay 기본값 T) : "name [codeitemid]" 형태로 표시 */
	public static final boolean CODE_FIELD_DISPLAY_WITH_ID = true;

	/** 코드 필드 판단 기준 : DOSCODITM.OUID 는 8자리 이상 숫자 */
	public static final int CODE_OUID_MIN_DIGITS = 8;

	// ---------------------------------------------------------------- block / pick
	public static final int MAX_PICK_COUNT = 33;

	// ---------------------------------------------------------------- PID
	public static final String METHOD_JAVA = "JAVA";
	public static final String METHOD_DB = "DB";
	public static final int MAX_SPEC_FIELD_SIZE = 30;
	public static final int MAX_RES_FIELD_SIZE = 20;
	public static final int MAX_PID_DEPTH = 30;

	public static final String PID_NOT_FOUND = "PID_NOT_FOUND";
	public static final String ERROR = "ERROR";

	/** 공사정보 종류별 EL_P PID prefix */
	public static final String EL_P_PREFIX = "EL_P%";
	public static final String SHIPEL_P_PREFIX = "SH_P%";
	public static final String SVEL_P_PREFIX = "SV_P%";

	// ---------------------------------------------------------------- ebom (SubaeManager.link1LevelPart)
	/** 1레벨 BOM SEQ 증가값 */
	public static final int EBOM_SEQ_STEP = 10;

	/** 오더 대상 구분 (ORIGIN_DIV) */
	public static final String DIV_INNER = "inner";
	public static final String DIV_OUTER = "outer";
	public static final String DIV_INNER_F = "inner_f";
}
