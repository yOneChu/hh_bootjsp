package com.kyhslam.dto;

import lombok.Data;

/**
 * 문서 매뉴얼(readDBFile.html) 사이드바 메뉴 한 건.
 * 테이블 : PLM_DOC_MENU  (스크립트: src/main/resources/sql/PLM_DOC_MENU.sql)
 *
 *  · menuType = 'GROUP' : 대분류(DB 명세서 / API 정의서 …)  → color 사용
 *  · menuType = 'SPEC'  : 명세서                            → groupId / parentId 사용
 *    명세서의 id 는 본문 테이블 PLM_LLM_METADATA.CATEGORY 와 동일하다.
 */
@Data
public class DocMenuDTO {

    private String id;
    private String menuType;
    private String name;
    private String icon;
    private String color;
    private String groupId;
    private String parentId;
    private int    sortNo;

    private String readUrl;
    private String saveUrl;
    private String saveMethod;
    private String saveFormat;
    private String saveField;

    private String useYn;
}
