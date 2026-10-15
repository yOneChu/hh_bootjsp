package com.kyhslam.util.gongsaExtract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 공사번호 추출 결과
 * (원본: productNumExt.getSearchDesignedList() 의 head / data HashMap)
 */
public class GongSaExtractResult {

    /** 컬럼명 (DB 컬럼 alias) */
    private final List<String> head = new ArrayList<String>();

    /** 행 데이터 */
    private final List<List<String>> data = new ArrayList<List<String>>();

    /** 영업사양 컬럼명 -> 화면 타이틀 */
    private final Map<String, String> specTitleMap = new HashMap<String, String>();

    public List<String> getHead() {
        return head;
    }

    public List<List<String>> getData() {
        return data;
    }

    public Map<String, String> getSpecTitleMap() {
        return specTitleMap;
    }

    /** 원본: productNumExt.getTitle() */
    public String getTitle(String columnName) {
        String title = specTitleMap.get(columnName);
        return title == null ? columnName : title;
    }

    public List<String> getTitles() {
        List<String> titles = new ArrayList<String>();
        for (String col : head) {
            titles.add(getTitle(col));
        }
        return titles;
    }
}
