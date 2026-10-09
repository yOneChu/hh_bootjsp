package com.kyhslam.pidSimulatorOne;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * PID 시뮬레이터 상수 (DOS 메타데이터 조회 대신 사용)
 * 원본 : SubaeConstants, FloorConst, EBomConst, ProductConsts, Variant, PidConsts, DOSImpl
 */
public class SimConsts {

	private SimConsts() {}

	// ---------------------------------------------------------------- ouid prefix
	public static final String PREFIX_ELVINFO_OUID = "elv_info$vf@";
	public static final String PREFIX_SHIPELVINFO_OUID = "shipelv_info$vf@";
	public static final String PREFIX_SVELVINFO_OUID = "monitorstation_info$vf@";
	public static final String PREFIX_PRODUCT_OUID = "product$vf@";

	// ---------------------------------------------------------------- class / association ouid
	public static final String ELVINFO_CLASS_OUID = "860cebeb";
	public static final String FLOORINFO_CLASS_OUID = "860cece3";
	public static final String ELVANDFLOOR_ASSO_OUID = "860cedaf";

	/** 층 정보 테이블 코드 (층 클래스 860cece3 → FLOORMASTER$VF / FLOORMASTER$ID) */
	public static final String FLOOR_TABLE_CODE = "FLOORMASTER";
	/** 층 정보 테이블이 버전관리(vf) 인지 여부 (false 면 sf) */
	public static final boolean FLOOR_TABLE_VERSIONABLE = true;
	/** 공사정보-층 연결 테이블 (연결 860cedaf → ELVANDFLOOR$AS : AS$END1 = elv_info$vf, AS$END2 = floormaster$vf) */
	public static final String ELVANDFLOOR_ASSO_TABLE_CODE = "ELVANDFLOOR";
	/** 연결 테이블 접미사 (이 연결은 $ac 가 아니라 $as 테이블에 있다) */
	public static final String ELVANDFLOOR_ASSO_TABLE_SUFFIX = "$as";

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

	// ---------------------------------------------------------------- PID
	public static final String METHOD_JAVA = "JAVA";
	public static final String METHOD_DB = "DB";
	public static final int MAX_SPEC_FIELD_SIZE = 30;
	public static final int MAX_RES_FIELD_SIZE = 20;
	public static final int MAX_PID_DEPTH = 30;

	/** PidConsts.LASTEST / TEST */
	public static final String LASTEST = "lastest";
	public static final String TEST = "test";
	/** Variant.TESTversion */
	public static final int TEST_VERSION = -1;

	// ---------------------------------------------------------------- ebom (PIDJavaMethod 에서 사용)
	/** 오더 대상 구분 (ORIGIN_DIV) */
	public static final String DIV_INNER = "inner";
	public static final String DIV_OUTER = "outer";
	public static final String DIV_INNER_F = "inner_f";
}
