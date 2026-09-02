/* =============================================================================
 * apiv2.js — 로직 PID Analytics v4 : 데이터 접근 계층 (Data Access Layer)
 *
 * 화면(appv2.js)은 오직 window.PIDApi 만 사용합니다.
 * 엔드포인트가 바뀌어도 이 파일만 고치면 됩니다.
 *
 *   ┌───────────┐        ┌───────────┐        ┌──────────────┐
 *   │ appv2.js  │ ─────▶ │  PIDApi   │ ─────▶ │ Spring 서버   │
 *   └───────────┘        └───────────┘        └──────────────┘
 *
 * ── 사용 중인 엔드포인트 ──────────────────────────────────────────────
 *   POST /pid/searchPIDSpecViewJson   PID 상세 검색
 *   POST /excel/searchPIDExcel        엑셀 다운로드
 *   GET  /pid/findPIDList?pid=        PID 전체 버전
 *   POST /pid/findFirstPID            최초 등록 조회
 *
 * ── 최초 등록 조회 (PIDController.findFirstPID) ───────────────────────
 *   request  (application/x-www-form-urlencoded)
 *       word : 찾을 문구 (필수) — variant_d 의 VAL1~VAL20 을 LIKE 검색
 *       pid  : PID (선택) — 비우면 전체 PID 대상
 *   response (application/json)
 *       [ { NO, PID, NAME, REG_DATE, VERSION, REMARKS }, ... ]
 *       · 서버(PIDCommonUtil.findFirstPID)가 기준일 20250101 이전을 먼저 보고,
 *         없을 때만 이후를 조회해 "가장 먼저 등록된 버전"의 행들만 돌려준다.
 *       · 같은 버전에서 여러 줄이 걸리면 그 줄이 모두 담겨 온다.
 *       · 결과가 없으면 빈 배열.
 * ===========================================================================*/
(function (global) {
    'use strict';

    /* ── 공통 설정 ── */
    const EP = {
        search:   '/pid/searchPIDSpecViewJson',
        excel:    '/excel/searchPIDExcel',
        pidList:  '/pid/findPIDList',
        firstPid: '/pid/findFirstPID',
    };

    /* ── 내부 유틸 ── */
    async function postForm(url, params) {
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams(params)
        });
        if (!res.ok) {
            const err = new Error('HTTP ' + res.status);
            err.status = res.status;
            throw err;
        }
        return res;
    }

    /* 대소문자/표기 흔들림을 흡수해 첫 번째로 값이 있는 키를 고른다 */
    function pick(obj, names) {
        if (!obj) return '';
        for (const n of names) {
            for (const k of [n, n.toUpperCase(), n.toLowerCase()]) {
                const v = obj[k];
                if (v !== undefined && v !== null && String(v).trim() !== '') return String(v).trim();
            }
        }
        return '';
    }

    /* REG_DATE 문자열 → 정렬용 숫자. 파싱 실패 시 0 (문자열 비교로 폴백) */
    function dateKey(s) {
        if (!s) return 0;
        const t = String(s).trim();
        // 20240501 / 20240501130000
        let m = t.match(/^(\d{4})(\d{2})(\d{2})(?:(\d{2})(\d{2})(\d{2}))?$/);
        if (m) return Date.UTC(+m[1], +m[2] - 1, +m[3], +(m[4] || 0), +(m[5] || 0), +(m[6] || 0));
        // 2024-05-01 13:00:00 / 2024/05/01 13:00
        m = t.match(/^(\d{4})[-/.](\d{1,2})[-/.](\d{1,2})(?:[ T](\d{1,2}):(\d{2})(?::(\d{2}))?)?/);
        if (m) return Date.UTC(+m[1], +m[2] - 1, +m[3], +(m[4] || 0), +(m[5] || 0), +(m[6] || 0));
        const d = Date.parse(t);
        return isNaN(d) ? 0 : d;
    }

    /* 서버 응답 한 행 → 화면이 쓰는 표준 형태 */
    function normalizeEntry(raw) {
        return {
            pid:     pick(raw, ['PID', 'PIDNAME']),
            name:    pick(raw, ['NAME', 'PID_NAME']),
            version: pick(raw, ['VERSION', 'VER', 'REV']),
            no:      pick(raw, ['NO', 'ROWNO', 'LINE', 'LINE_NO', 'SEQ']),
            regDate: pick(raw, ['REG_DATE', 'REGDATE', 'DATE', 'CREATE_DATE']),
            remarks: pick(raw, ['REMARKS', 'REMARK', 'NOTE']),
            userid:  pick(raw, ['USERID', 'USER_ID', 'REG_USER']),
        };
    }

    /* regDate 오름차순 정렬 — 날짜를 못 읽으면 문자열로 비교한다 */
    function sortByDateAsc(list) {
        return list.slice().sort((a, b) => {
            const ka = dateKey(a.regDate), kb = dateKey(b.regDate);
            if (ka && kb && ka !== kb) return ka - kb;
            if (ka !== kb) return (ka || Number.MAX_SAFE_INTEGER) - (kb || Number.MAX_SAFE_INTEGER);
            return String(a.regDate).localeCompare(String(b.regDate));
        });
    }

    /* NO 오름차순 (숫자 우선) */
    function sortByNoAsc(list) {
        return list.slice().sort((a, b) =>
            String(a.no).localeCompare(String(b.no), undefined, { numeric: true })
        );
    }

    /* ══════════════════════════════════════════════════════════════
     * 1) PID 상세 검색
     * ════════════════════════════════════════════════════════════ */
    async function searchPID(params) {
        const res = await postForm(EP.search, params);
        const data = await res.json();
        return Array.isArray(data) ? data : [];
    }

    /* ══════════════════════════════════════════════════════════════
     * 2) 엑셀 다운로드 — { blob, filename } 반환 (저장은 화면이 담당)
     * ════════════════════════════════════════════════════════════ */
    async function downloadExcel(params) {
        const res = await postForm(EP.excel, params);
        const disposition = res.headers.get('Content-Disposition');
        let filename = 'excel.xlsx';
        if (disposition && disposition.includes('filename=')) {
            filename = decodeURIComponent(disposition.split('filename=')[1].replace(/"/g, '').trim());
        }
        return { blob: await res.blob(), filename };
    }

    /* ══════════════════════════════════════════════════════════════
     * 3) PID 전체 버전 조회
     * ════════════════════════════════════════════════════════════ */
    async function findPIDVersions(pid) {
        const res = await fetch(EP.pidList + '?pid=' + encodeURIComponent(pid));
        if (!res.ok) throw new Error('HTTP ' + res.status);
        const data = await res.json();
        return Array.isArray(data) ? data : [];
    }

    /* ══════════════════════════════════════════════════════════════
     * 4) 최초 등록 조회 — 조건은 문구(필수) + PID(선택) 뿐이다
     *    반환 { first, rows, msg }
     *      first : 최초 등록 건 (대표 1건, 없으면 null)
     *      rows  : 같은 버전에서 걸린 행 전체 (NO 오름차순)
     * ════════════════════════════════════════════════════════════ */
    async function findFirstRegistered(opt) {
        const word = String(opt.word || '').trim();
        const pid  = String(opt.pid || '').trim();
        if (!word) return { first: null, rows: [] };

        const res = await postForm(EP.firstPid, { word, pid });
        const data = await res.json();

        let list = Array.isArray(data) ? data
                 : (data && Array.isArray(data.rows)) ? data.rows
                 : (data && data.first) ? [data.first] : [];

        // 서버가 안내 메시지만 돌려준 경우
        if (list.length === 1 && list[0] && list[0].msg && !list[0].PID && !list[0].pid) {
            return { first: null, rows: [], msg: list[0].msg };
        }

        const entries = list.map(normalizeEntry).filter(e => e.pid || e.no);
        if (entries.length === 0) return { first: null, rows: [] };

        // 대표 1건은 가장 오래된 등록일 기준. 나머지는 같은 버전의 나머지 행들이다.
        const first = sortByDateAsc(entries)[0];
        const rows = sortByNoAsc(entries);

        return { first, rows };
    }

    /* ── 공개 API ── */
    global.PIDApi = {
        searchPID,
        downloadExcel,
        findPIDVersions,
        findFirstRegistered,
        // 화면에서도 쓰는 헬퍼
        _util: { pick, dateKey, sortByDateAsc, sortByNoAsc, normalizeEntry }
    };

})(window);
