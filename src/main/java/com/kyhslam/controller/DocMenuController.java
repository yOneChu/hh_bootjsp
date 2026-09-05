package com.kyhslam.controller;

import com.kyhslam.dto.DocMenuDTO;
import com.kyhslam.repository.DocMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 문서 매뉴얼(readDBFile.html) 왼쪽 사이드바 메뉴 관리 API.
 *
 *   GET  /api/docmenu?key=subae          → { groups:[...], specs:[...] }
 *   POST /api/docmenu/create             → 대분류 / 명세서 추가
 *   POST /api/docmenu/update             → 이름·아이콘 변경
 *   POST /api/docmenu/delete             → 삭제 (하위가 있으면 force=Y 필요)
 *
 * 본문(마크다운)은 기존 API 그대로 PLM_LLM_METADATA 에 저장된다.
 *   조회 : /api/getLogicVerifyAsDB?key=subae&type={ID}
 *   저장 : /api/update_PLM_DB_MetaData?key=subae&type={ID}
 */
@RestController
@RequestMapping("/api/docmenu")
@RequiredArgsConstructor
@Slf4j
public class DocMenuController {

    private final DocMenuRepository docMenuRepository;

    private static final String KEY = "subae";
    private static final String READ_URL_FORM = "/api/getLogicVerifyAsDB?key=subae&type=";
    private static final String SAVE_URL_FORM = "/api/update_PLM_DB_MetaData?key=subae&type=";
    /** 카테고리(대분류) 고유 주소 — 그 안의 문서 목록을 돌려준다 */
    private static final String GROUP_URL_FORM = "/api/docmenu/group/";
    private static final String GROUP_URL_SUFFIX = "?key=subae";
    /** 새 대분류에 돌아가며 부여할 색상 (app.js GROUP_STYLES 와 동일) */
    private static final String[] GROUP_COLORS = { "blue", "violet", "amber" };

    /* ------------------------------------------------------------------ *
     * 조회
     * ------------------------------------------------------------------ */
    @GetMapping
    @CrossOrigin
    public Map<String, Object> list(String key) {
        Map<String, Object> res = new HashMap<>();
        if (!KEY.equals(key)) return fail("인증 키가 올바르지 않습니다.");

        try {
            List<DocMenuDTO> all = docMenuRepository.findAll();
            List<DocMenuDTO> groups = new ArrayList<>();
            List<DocMenuDTO> specs = new ArrayList<>();
            for (DocMenuDTO m : all) {
                if ("GROUP".equalsIgnoreCase(m.getMenuType())) groups.add(m);
                else specs.add(m);
            }
            res.put("result", "OK");
            res.put("groups", groups);
            res.put("specs", specs);
            return res;
        } catch (Exception e) {
            log.error("메뉴 조회 실패", e);
            return fail("메뉴 테이블(PLM_DOC_MENU) 조회 실패: " + e.getMessage()
                    + " — src/main/resources/sql/PLM_DOC_MENU.sql 을 먼저 실행하세요.");
        }
    }

    /* ------------------------------------------------------------------ *
     * 카테고리(대분류) 고유 주소
     *   GET /api/docmenu/group/{groupId}?key=subae
     *   → 그 카테고리에 속한 문서 목록과, 문서별 조회/저장 주소를 함께 돌려준다.
     *     (화면 상단 'API 링크' 카드에서 이 주소를 복사할 수 있다)
     * ------------------------------------------------------------------ */
    @GetMapping("/group/{groupId}")
    @CrossOrigin
    public Map<String, Object> group(@PathVariable String groupId, String key) {
        if (!KEY.equals(key)) return fail("인증 키가 올바르지 않습니다.");

        try {
            DocMenuDTO g = docMenuRepository.findById(groupId);
            if (g == null || !"GROUP".equalsIgnoreCase(g.getMenuType())) {
                return fail("카테고리를 찾을 수 없습니다: " + groupId);
            }

            Map<String, Object> res = new HashMap<>();
            res.put("result", "OK");
            res.put("group", g);
            res.put("specs", docMenuRepository.findByGroup(groupId));
            return res;
        } catch (Exception e) {
            log.error("카테고리 조회 실패", e);
            return fail("조회 실패: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ *
     * 추가
     *   menuType : 'GROUP'(대분류) | 'SPEC'(명세서)
     *   id       : 비우면 자동 생성. 명세서면 PLM_LLM_METADATA.CATEGORY 로도 쓰인다.
     *   groupId  : 명세서가 속할 대분류 id
     *   parentId : (선택) 상위 명세서 id — 지정하면 하위 명세서로 등록
     * ------------------------------------------------------------------ */
    @PostMapping("/create")
    @CrossOrigin
    public Map<String, Object> create(String key, String menuType, String id, String name,
                                      String icon, String color, String groupId, String parentId) {
        if (!KEY.equals(key)) return fail("인증 키가 올바르지 않습니다.");
        if (isBlank(name)) return fail("이름을 입력하세요.");

        boolean isGroup = "GROUP".equalsIgnoreCase(menuType);

        try {
            // 코드를 따로 주지 않으면 SPEC00001, SPEC00002 … 순번으로 자동 발급한다
            String newId = normalizeId(id);
            if (isBlank(newId)) newId = docMenuRepository.nextMenuCode();
            if (docMenuRepository.exists(newId)) return fail("이미 있는 코드입니다: " + newId);

            DocMenuDTO menu = new DocMenuDTO();
            menu.setId(newId);
            menu.setName(name.trim());
            menu.setUseYn("Y");

            if (isGroup) {
                menu.setMenuType("GROUP");
                menu.setIcon(isBlank(icon) ? "folder" : icon.trim());
                menu.setColor(isBlank(color)
                        ? GROUP_COLORS[docMenuRepository.countGroups() % GROUP_COLORS.length]
                        : color.trim());
                menu.setSortNo(docMenuRepository.nextGroupSortNo());
                // 카테고리도 고유 주소를 갖는다 — 화면 상단에서 복사해 쓸 수 있다
                menu.setReadUrl(GROUP_URL_FORM + newId + GROUP_URL_SUFFIX);
            } else {
                // 하위 명세서로 만들 때는 상위 명세서의 대분류를 그대로 따라간다.
                // 상위가 또 하위 명세서여도 상관없다 — 단계 제한 없이 중첩된다.
                String parent = isBlank(parentId) ? null : parentId.trim();
                String group = isBlank(groupId) ? null : groupId.trim();
                if (parent != null) {
                    DocMenuDTO p = docMenuRepository.findById(parent);
                    if (p == null) return fail("상위 명세서를 찾을 수 없습니다: " + parent);
                    if ("GROUP".equalsIgnoreCase(p.getMenuType())) {
                        group = p.getId();      // 대분류를 상위로 넘긴 경우 = 그 카테고리 최상위에 추가
                        parent = null;
                    } else {
                        group = p.getGroupId();
                    }
                }
                if (isBlank(group)) return fail("대분류(카테고리)를 지정하세요.");
                if (docMenuRepository.findById(group) == null) return fail("대분류를 찾을 수 없습니다: " + group);

                menu.setMenuType("SPEC");
                menu.setIcon(isBlank(icon) ? "file-text" : icon.trim());
                menu.setGroupId(group);
                menu.setParentId(parent);
                menu.setSortNo(docMenuRepository.nextSortNo(group, parent));
                // 본문 조회·저장 API 는 규칙에 따라 자동 연결된다
                menu.setReadUrl(READ_URL_FORM + newId);
                menu.setSaveUrl(SAVE_URL_FORM + newId);
                menu.setSaveMethod("POST");
                menu.setSaveFormat("form");
                menu.setSaveField("updatedContent");
            }

            docMenuRepository.insert(menu);
            if (!isGroup) docMenuRepository.ensureContentRow(newId);   // 본문 자리 확보

            Map<String, Object> res = new HashMap<>();
            res.put("result", "OK");
            res.put("menu", menu);
            return res;
        } catch (Exception e) {
            log.error("메뉴 추가 실패", e);
            return fail("추가 실패: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ *
     * 이름 / 아이콘 변경
     * ------------------------------------------------------------------ */
    @PostMapping("/update")
    @CrossOrigin
    public Map<String, Object> update(String key, String id, String name, String icon) {
        if (!KEY.equals(key)) return fail("인증 키가 올바르지 않습니다.");
        if (isBlank(id)) return fail("대상이 없습니다.");
        if (isBlank(name)) return fail("이름을 입력하세요.");

        try {
            int n = docMenuRepository.updateNameIcon(id, name.trim(), isBlank(icon) ? null : icon.trim());
            if (n == 0) return fail("메뉴를 찾을 수 없습니다: " + id);

            Map<String, Object> res = new HashMap<>();
            res.put("result", "OK");
            res.put("menu", docMenuRepository.findById(id));
            return res;
        } catch (Exception e) {
            log.error("메뉴 수정 실패", e);
            return fail("수정 실패: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ *
     * 삭제
     *   하위(대분류 안의 명세서 / 명세서의 하위 명세서)가 있으면 force=Y 를 받아야 지운다.
     *   ※ 메뉴만 지운다. 본문(PLM_LLM_METADATA)은 남겨 두므로 같은 코드로 다시 만들면 복구된다.
     * ------------------------------------------------------------------ */
    @PostMapping("/delete")
    @CrossOrigin
    public Map<String, Object> delete(String key, String id, String force) {
        if (!KEY.equals(key)) return fail("인증 키가 올바르지 않습니다.");
        if (isBlank(id)) return fail("대상이 없습니다.");

        try {
            DocMenuDTO menu = docMenuRepository.findById(id);
            if (menu == null) return fail("메뉴를 찾을 수 없습니다: " + id);

            boolean forced = "Y".equalsIgnoreCase(force) || "true".equalsIgnoreCase(force);
            boolean isGroup = "GROUP".equalsIgnoreCase(menu.getMenuType());
            int children = isGroup ? docMenuRepository.countByGroup(id) : docMenuRepository.countByParent(id);

            if (children > 0 && !forced) {
                Map<String, Object> res = fail("하위 항목 " + children + "개가 있습니다.");
                res.put("children", children);
                return res;
            }

            int deleted = 0;
            if (children > 0) {
                deleted += isGroup ? docMenuRepository.deleteByGroup(id) : docMenuRepository.deleteByParent(id);
            }
            deleted += docMenuRepository.delete(id);

            Map<String, Object> res = new HashMap<>();
            res.put("result", "OK");
            res.put("deleted", deleted);
            return res;
        } catch (Exception e) {
            log.error("메뉴 삭제 실패", e);
            return fail("삭제 실패: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ *
     * 내부 유틸
     * ------------------------------------------------------------------ */
    private Map<String, Object> fail(String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("result", "FAIL");
        res.put("message", message);
        return res;
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    /** 메뉴 코드 정리 — 영문/숫자/_ 만 남기고 대문자로 (DB CATEGORY 키로 쓰이므로) */
    private String normalizeId(String id) {
        if (isBlank(id)) return null;
        String v = id.trim().toUpperCase().replaceAll("[^A-Z0-9_]", "_");
        v = v.replaceAll("^_+", "");
        return v.length() > 100 ? v.substring(0, 100) : v;
    }
}
