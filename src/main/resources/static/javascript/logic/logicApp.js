/* =============================================================================
 * appv2.js — 로직 PID Analytics v4 : 화면 로직
 *
 * · v3(searchLogicPIDv3.html) 의 인라인 스크립트를 그대로 옮긴 뒤
 *   "최초 등록 조회" 기능을 얹은 버전입니다.
 * · 데이터 접근은 전부 window.PIDApi (apiv2.js) 를 통해서만 합니다.
 * ===========================================================================*/
(function () {
    'use strict';

    const $id = (s) => document.getElementById(s);
    const api = window.PIDApi;

    /* ── 컬럼 정의: PID, NO, ADDR, REMARKS + SPEC/CON 1..30 + KEY/VAL 1..20 ──
       variant_d 테이블 컬럼 수와 반드시 일치시킬 것 (PIDCommonUtil.maxSlot 참조) */
    const SPEC_MAX = 30;
    const KEY_MAX  = 20;
    const COLUMNS = ['PID', 'NO', 'ADDR', 'REMARKS'];
    for (let i = 1; i <= SPEC_MAX; i++) COLUMNS.push('SPEC' + i, 'CON' + i);
    for (let i = 1; i <= KEY_MAX;  i++) COLUMNS.push('KEY' + i, 'VAL' + i);

    /* ── 상태 ── */
    let rawData = [];        // 서버 원본
    let viewData = [];       // 필터/정렬 적용본
    let visibleCols = [...COLUMNS];
    let curPage = 1;
    let sortCol = null, sortDir = 1;

    /* ══════════ 다크모드 ══════════ */
    (function initTheme() {
        const dark = localStorage.getItem('theme') !== 'light'; // 기본 다크모드
        document.documentElement.classList.toggle('dark', dark);
        $id('darkModeToggle').checked = dark;
        $id('themeLabel').textContent = dark ? '🌙 다크모드' : '☀️ 라이트모드';
        if (dark) localStorage.setItem('theme', 'dark');
    })();
    $id('darkModeToggle').addEventListener('change', function () {
        document.documentElement.classList.toggle('dark', this.checked);
        localStorage.setItem('theme', this.checked ? 'dark' : 'light');
        $id('themeLabel').textContent = this.checked ? '🌙 다크모드' : '☀️ 라이트모드';
    });

    document.querySelectorAll('#sidebar .sb-link').forEach(a => a.classList.remove('active'));
    document.querySelector('#sidebar a[href="/pid/logicpid"]')?.classList.add('active');

    /* ══════════ UI 유틸 ══════════ */
    function showLoading(msg) {
        const p = $id('loading').querySelector('p');
        if (p) p.textContent = msg || '데이터를 조회하고 있습니다…';
        $id('loading').classList.add('show');
    }
    function hideLoading() { $id('loading').classList.remove('show'); }

    let toastTimer = null;
    function toast(msg) {
        const t = $id('toast');
        t.textContent = msg;
        t.classList.add('show');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => t.classList.remove('show'), 2600);
    }

    function escapeHtml(v) {
        return String(v ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    /* ═══════════════════════════════════════════════════════════════
       검색 조건 빌더 — 선언형 정의
       ─ 조건 슬롯을 늘리려면 CONDITIONS 에 항목을 추가하고, 카드 안에
         <div id="cond-<id>"></div> 호스트를 두고, collectParams() 에
         wire 파라미터 매핑만 연결하면 된다.
       ─ option 의 value 는 서버가 그대로 해석하는 토큰이므로 변경 금지.
         조건02 는 NOT_EQUAL(언더스코어), PID GROUP 은 NOT EQUAL(공백)로
         서버가 서로 다른 토큰을 기대한다. (PIDCommonUtil 참조)
       ═══════════════════════════════════════════════════════════════ */
    const OP = {
        LIKE:      { value: 'LIKE',      label: 'LIKE',      desc: '포함' },
        EQUAL:     { value: 'EQUAL',     label: 'EQUAL',     desc: '일치' },
        NOT_LIKE:  { value: 'NOT LIKE',  label: 'NOT LIKE',  desc: '미포함' },
        NEQ_UNDER: { value: 'NOT_EQUAL', label: 'NOT EQUAL', desc: '불일치' },
        NEQ_SPACE: { value: 'NOT EQUAL', label: 'NOT EQUAL', desc: '불일치' },
    };

    const FIELD_DESC = { SPEC: '사양', CON: '조건', KEY: '키', VAL: '값', REMARKS: '비고' };

    const CONDITIONS = [
        {
            id: 'c1', badge: '조건 01', badgeCls: 'bg-apple-blue', note: '필수',
            fieldId: 'spec-01', opId: 'link-01', valId: 'pidVal',
            fields: ['SPEC', 'CON', 'KEY', 'VAL', 'REMARKS'],
            ops: [OP.LIKE, OP.EQUAL],
            placeholder: '검색할 값 입력 (필수)',
        },
        {
            id: 'c2', badge: '조건 02', badgeCls: 'bg-[#5856D6]', note: '선택',
            fieldId: 'spec-02', opId: 'link-02', valId: 'pidVal02',
            fields: ['SPEC', 'CON', 'KEY', 'VAL'],
            ops: [OP.LIKE, OP.NOT_LIKE, OP.EQUAL, OP.NEQ_UNDER],
            placeholder: '비워두면 조건 02 는 적용되지 않습니다',
        },
    ];

    const PID_GROUP = {
        opId: 'con-05',
        ops: [OP.LIKE, OP.NOT_LIKE, OP.EQUAL, OP.NEQ_SPACE],
        steps: [
            { id: 'pidVal03', label: 'PID-03' },
            { id: 'pidVal04', label: 'PID-04' },
            { id: 'pidVal05', label: 'PID-05' },
        ],
    };

    /* ── 렌더 ── */
    const opOptions = (ops) =>
        ops.map((o, i) => `<option value="${o.value}"${i === 0 ? ' selected' : ''}>${o.label} · ${o.desc}</option>`).join('');
    const fieldOptions = (fields) =>
        fields.map((f, i) => `<option value="${f}"${i === 0 ? ' selected' : ''}>${f} · ${FIELD_DESC[f]}</option>`).join('');

    function renderCondition(c) {
        $id('cond-' + c.id).innerHTML = `
        <div class="cond-card rounded-xl bg-apple-gray6/70 dark:bg-white/[.04] p-4" id="wrap-${c.id}">
            <div class="flex items-center gap-2 mb-3 flex-wrap">
                <span class="inline-flex items-center h-[22px] px-2.5 rounded-md ${c.badgeCls} text-white text-[11px] font-bold tracking-wide">${c.badge}</span>
                <span class="text-[12px] text-apple-gray1">${c.note}</span>
                <span class="text-[12px] font-medium text-amber-600 dark:text-amber-400 hidden" id="off-${c.id}"></span>
                <button type="button" class="btn-mini ml-auto !h-[26px] !text-[11.5px]" data-clear="${c.id}">지우기</button>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-[minmax(0,1fr)_minmax(0,1fr)_minmax(0,2fr)] gap-4">
                <div>
                    <label class="field-label" for="${c.fieldId}">대상 필드</label>
                    <select id="${c.fieldId}" class="form-select">${fieldOptions(c.fields)}</select>
                </div>
                <div>
                    <label class="field-label" for="${c.opId}">연산자</label>
                    <select id="${c.opId}" class="form-select">${opOptions(c.ops)}</select>
                </div>
                <div>
                    <label class="field-label" for="${c.valId}">값</label>
                    <input type="search" id="${c.valId}" class="form-input" placeholder="${c.placeholder}" autocomplete="off">
                </div>
            </div>
        </div>`;
    }

    function renderPidGroup() {
        const g = PID_GROUP;
        $id('cond-pidgroup').innerHTML = `
        <div class="cond-card rounded-xl bg-apple-gray6/70 dark:bg-white/[.04] p-4">
            <div class="flex items-center gap-2 mb-3 flex-wrap">
                <span class="inline-flex items-center h-[22px] px-2.5 rounded-md bg-teal-600 text-white text-[11px] font-bold tracking-wide">PID GROUP</span>
                <span class="text-[12px] text-apple-gray1">선택 · PID 자체를 단계로 좁혀 검색</span>
                <button type="button" class="btn-mini ml-auto !h-[26px] !text-[11.5px]" data-clear="pidgroup">지우기</button>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div>
                    <label class="field-label" for="${g.opId}">연산자</label>
                    <select id="${g.opId}" class="form-select">${opOptions(g.ops)}</select>
                </div>
                ${g.steps.map((s, i) => `
                <div>
                    <label class="field-label flex items-center gap-1.5" for="${s.id}">
                        <span class="step-badge" data-step="${i + 1}">${i + 1}</span>${s.label}
                    </label>
                    <input type="search" id="${s.id}" class="form-input"
                           placeholder="${i === 0 ? 'PID 앞부분' : (i + 3) + '단계 · 이전 입력 후 활성화'}"
                           ${i > 0 ? 'readonly' : ''} autocomplete="off">
                </div>`).join('')}
            </div>
            <p class="text-[11.5px] text-apple-gray1 mt-3" id="pgHint"></p>
        </div>`;
    }

    CONDITIONS.forEach(renderCondition);
    renderPidGroup();

    /* ── 상태 동기화 ── */
    function setJoin(v) {
        $id('joinOp').value = v;
        document.querySelectorAll('#joinSeg button').forEach(b => b.classList.toggle('on', b.dataset.val === v));
    }

    // 조건01 이 REMARKS 면 서버가 조건02 를 처리할 수 없다 → 아예 입력을 막고 이유를 보여준다
    function syncConditionState() {
        const isRemarks = $id('spec-01').value === 'REMARKS';
        const wrap = $id('wrap-c2'), off = $id('off-c2');
        wrap.classList.toggle('is-off', isRemarks);
        off.classList.toggle('hidden', !isRemarks);
        if (isRemarks) {
            off.textContent = '조건 01 이 REMARKS 이면 사용할 수 없습니다';
            $id('pidVal02').value = '';
        }
    }

    // PID-03 → 04 → 05 순차 활성화 (앞 단계가 비면 뒤 단계는 잠기고 값도 비운다)
    function syncPidGroup() {
        const els = PID_GROUP.steps.map(s => $id(s.id));
        for (let i = 1; i < els.length; i++) {
            if (els[i - 1].value.trim() === '') {
                els[i].readOnly = true;
                els[i].value = '';
            } else {
                els[i].readOnly = false;
            }
        }
        els.forEach((el, i) => {
            document.querySelector(`.step-badge[data-step="${i + 1}"]`)
                ?.classList.toggle('on', el.value.trim() !== '');
        });

        const op = $id(PID_GROUP.opId).value;
        const conn = op.trim().startsWith('NOT') ? 'AND' : 'OR';
        const filled = els.filter(el => el.value.trim() !== '').length;
        $id('pgHint').textContent = filled === 0
            ? 'PID-03 부터 입력하면 다음 단계가 열립니다. 비워두면 PID GROUP 조건은 적용되지 않습니다.'
            : `현재 연산자(${op}) 기준 → 입력한 ${filled}개 단계를 ${conn} 로 묶어 조회합니다.`;
    }

    function syncJoinHint() {
        const join = $id('joinOp').value;
        const hasC2 = $id('pidVal02').value.trim() !== '' && $id('spec-01').value !== 'REMARKS';
        let msg;
        if (!hasC2) {
            msg = 'AND 선택 시 조건 1·2 모두 만족하는 행 조회';
        } else if (join === 'F') {
            msg = '조건 01·02 가 같은 번호에서 함께 만족하는 행 (SPEC1↔VAL1 처럼 짝이 맞는 경우)';
        } else if (join === 'AND') {
            msg = 'AND 선택 시 조건 1·2 모두 만족하는 행 조회';
        } else {
            msg = '조건 01 또는 조건 02 중 하나라도 만족';
        }
        $id('joinHint').textContent = msg;
    }

    function refreshPreview() {
        const f1 = $id('spec-01').value, o1 = $id('link-01').value, v1 = $id('pidVal').value.trim();
        const isRemarks = f1 === 'REMARKS';
        const f2 = $id('spec-02').value, o2 = $id('link-02').value;
        const v2 = isRemarks ? '' : $id('pidVal02').value.trim();
        const join = $id('joinOp').value;

        const chip = (f, o, v) =>
            `<span class="q-f">${escapeHtml(f)}</span> <span class="q-o">${escapeHtml(o)}</span> <span class="q-v">"${escapeHtml(v)}"</span>`;

        let html;
        if (!v1) {
            html = '<span class="q-dim">조건 01 의 값을 입력하면 검색식이 여기에 표시됩니다.</span>';
        } else {
            html = chip(f1, o1, v1);
            if (v2) {
                const j = join === 'F' ? '같은 번호에서 함께' : join;
                html += ` <span class="q-j">${j}</span> ` + chip(f2, o2, v2);
            }
            const pgOp = $id(PID_GROUP.opId).value;
            const pgVals = PID_GROUP.steps.map(s => $id(s.id).value.trim()).filter(Boolean);
            if (pgVals.length) {
                const conn = pgOp.trim().startsWith('NOT') ? 'AND' : 'OR';
                const grp = pgVals
                    .map(v => `<span class="q-f">PID</span> <span class="q-o">${escapeHtml(pgOp)}</span> <span class="q-v">"${escapeHtml(v)}"</span>`)
                    .join(` <span class="q-j">${conn}</span> `);
                html = `( ${html} ) <span class="q-j">AND</span> ( ${grp} )`;
            }
        }
        $id('queryPreview').innerHTML = html;
    }

    function syncAll() {
        syncConditionState();
        syncPidGroup();
        syncJoinHint();
        refreshPreview();
    }

    /* ── 이벤트 위임 ── */
    $id('condRoot').addEventListener('input', syncAll);
    $id('condRoot').addEventListener('change', syncAll);

    $id('condRoot').addEventListener('click', (e) => {
        const btn = e.target.closest('[data-clear]');
        if (!btn) return;
        const target = btn.dataset.clear;
        if (target === 'pidgroup') {
            $id(PID_GROUP.opId).selectedIndex = 0;
            PID_GROUP.steps.forEach(s => $id(s.id).value = '');
        } else {
            const c = CONDITIONS.find(x => x.id === target);
            if (!c) return;
            $id(c.fieldId).selectedIndex = 0;
            $id(c.opId).selectedIndex = 0;
            $id(c.valId).value = '';
        }
        syncAll();
    });

    $id('joinSeg').addEventListener('click', (e) => {
        const btn = e.target.closest('button');
        if (!btn) return;
        setJoin(btn.dataset.val);
        syncAll();
    });

    /* ── 초기화 ── */
    $id('btnReset').addEventListener('click', () => {
        CONDITIONS.forEach(c => {
            $id(c.fieldId).selectedIndex = 0;
            $id(c.opId).selectedIndex = 0;
            $id(c.valId).value = '';
        });
        $id(PID_GROUP.opId).selectedIndex = 0;
        PID_GROUP.steps.forEach(s => $id(s.id).value = '');
        setJoin('F');
        syncAll();
        $id('pidVal').focus();
        toast('검색 조건을 초기화했습니다.');
    });

    /* ── 최근 검색 (localStorage) ── */
    const HIST_KEY = 'pidSearchHistory';
    const HIST_MAX = 8;

    function loadHist() {
        try { return JSON.parse(localStorage.getItem(HIST_KEY)) || []; } catch (e) { return []; }
    }
    function pushHist(p) {
        const key = JSON.stringify(p);
        const list = loadHist().filter(x => JSON.stringify(x) !== key);
        list.unshift(p);
        try { localStorage.setItem(HIST_KEY, JSON.stringify(list.slice(0, HIST_MAX))); } catch (e) { /* 용량 초과 무시 */ }
    }
    function histLabel(p) {
        let s = `${p.FIELD} ${p.GUBUN} "${p.pid}"`;
        if (p.PID02) s += ` ${p.join === 'F' ? '+' : p.join} ${p.SPEC02} ${p.GUBUN02} "${p.PID02}"`;
        const pg = [p.PID03, p.PID04, p.PID05].filter(Boolean);
        if (pg.length) s += ` · PID ${pg.join('/')}`;
        return s;
    }
    function applyParams(p) {
        $id('spec-01').value = p.FIELD;   $id('link-01').value = p.GUBUN;  $id('pidVal').value = p.pid;
        $id('spec-02').value = p.SPEC02;  $id('link-02').value = p.GUBUN02; $id('pidVal02').value = p.PID02;
        $id('con-05').value = p.CON05;
        // 순차 잠금 때문에 앞 단계부터 채운 뒤 매번 동기화해야 뒷 단계가 열린다
        PID_GROUP.steps.forEach((s, i) => { $id(s.id).readOnly = false; $id(s.id).value = [p.PID03, p.PID04, p.PID05][i] || ''; });
        setJoin(p.join || 'F');
        syncAll();
    }

    $id('btnRecent').addEventListener('click', (e) => {
        e.stopPropagation();
        const pop = $id('recentPop');
        const list = loadHist();
        pop.innerHTML = list.length === 0
            ? '<p class="px-3 py-3 text-[12.5px] text-apple-gray1">저장된 최근 검색이 없습니다.</p>'
            : list.map((p, i) => `<button type="button" class="pop-item" data-hist="${i}">${escapeHtml(histLabel(p))}</button>`).join('');
        pop.classList.toggle('hidden');
    });

    $id('recentPop').addEventListener('click', (e) => {
        const btn = e.target.closest('[data-hist]');
        if (!btn) return;
        const p = loadHist()[parseInt(btn.dataset.hist, 10)];
        if (p) { applyParams(p); toast('최근 검색 조건을 불러왔습니다.'); }
        $id('recentPop').classList.add('hidden');
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('#recentPop') && !e.target.closest('#btnRecent')) {
            $id('recentPop').classList.add('hidden');
        }
    });

    syncAll();

    /* ══════════ 엔터키 검색 ══════════ */
    document.addEventListener('keyup', (e) => {
        if (e.key !== 'Enter') return;
        if (e.target.id === 'tblFilter') return;
        if (e.target.tagName === 'BUTTON') return;          // 버튼은 자체 동작만 수행
        if (!$id('firstRegModal').classList.contains('hidden')) return; // 모달이 열려 있으면 무시
        // 최초 등록 조회 패널의 입력창에서는 그쪽 조회를 실행한다
        if (e.target.id === 'frWord' || e.target.id === 'frPid') { runFirstRegistered(); return; }
        searchPID();
    });

    /* ══════════ 검색 파라미터 수집 ══════════ */
    function collectParams() {
        return {
            pid:     $id('pidVal').value,
            FIELD:   $id('spec-01').value,
            GUBUN:   $id('link-01').value,
            SPEC02:  $id('spec-02').value,
            GUBUN02: $id('link-02').value,
            PID02:   $id('pidVal02').value,
            CON05:   $id('con-05').value,
            PID03:   $id('pidVal03').value,
            PID04:   $id('pidVal04').value,
            PID05:   $id('pidVal05').value,
            join:    $id('joinOp').value
        };
    }

    function validate(p) {
        if (!p.pid || p.pid.trim() === '') {
            toast('PID 값을 입력하세요.');
            $id('pidVal').focus();
            return false;
        }
        if (p.FIELD === 'REMARKS' && p.PID02 !== '') {
            toast('조건1을 REMARKS로 검색 시, 조건2의 PID는 검색할 수 없습니다.');
            return false;
        }
        return true;
    }

    /* ══════════ 결과 초기화 (검색결과 없음/오류 시) ══════════ */
    function clearResult() {
        rawData = [];
        viewData = [];
        sortCol = null; sortDir = 1;
        $id('contentTable').innerHTML = '';
        $id('headerInfo').innerHTML = '';
        $id('resultStat').textContent = '';
        $id('elapsedStat').textContent = '';
        $id('resultCard').classList.add('hidden');
        $id('emptyState').classList.remove('hidden');
    }

    /* ══════════ 검색 ══════════ */
    async function searchPID() {
        const p = collectParams();
        if (!validate(p)) return;

        showLoading();
        const t0 = performance.now();
        try {
            const data = await api.searchPID(p);
            hideLoading();

            if (data && data[0] && data[0].msg) { toast(data[0].msg); clearResult(); return; }
            if (!data || data.length === 0) { toast('검색결과가 없습니다.'); clearResult(); return; }

            rawData = data;
            pushHist(p);
            sortCol = null; sortDir = 1;
            $id('tblFilter').value = '';
            $id('elapsedStat').textContent = ((performance.now() - t0) / 1000).toFixed(2) + '초 소요';

            $id('emptyState').classList.add('hidden');
            $id('resultCard').classList.remove('hidden');
            rebuildView();
            $id('resultCard').scrollIntoView({ behavior: 'smooth', block: 'start' });
        } catch (err) {
            hideLoading();
            console.error(err);
            toast('조회 중 오류가 발생했습니다.');
        }
    }

    /* ══════════ EXCEL 다운로드 (서버 생성) ══════════ */
    async function searchExcel() {
        const p = collectParams();
        if (!validate(p)) return;

        showLoading('엑셀 파일을 만들고 있습니다…');
        try {
            const { blob, filename } = await api.downloadExcel(p);
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = filename;
            link.click();
            URL.revokeObjectURL(link.href);
            hideLoading();
            toast('엑셀 다운로드가 시작되었습니다.');
        } catch (err) {
            hideLoading();
            console.error(err);
            toast('엑셀 다운로드 중 오류가 발생했습니다.');
        }
    }

    /* ══════════ 뷰 재구성 (필터 → 정렬 → 컬럼 → 렌더) ══════════ */
    function rebuildView() {
        const q = $id('tblFilter').value.trim().toLowerCase();

        viewData = !q ? [...rawData] : rawData.filter(row =>
            COLUMNS.some(c => String(row[c] ?? '').toLowerCase().includes(q))
        );

        if (sortCol) {
            const col = sortCol, dir = sortDir;
            viewData.sort((a, b) =>
                String(a[col] ?? '').localeCompare(String(b[col] ?? ''), undefined, { numeric: true }) * dir
            );
        }

        computeVisibleCols();
        curPage = 1;
        renderTable();
    }

    function isEmptyVal(v) {
        return v === null || v === undefined || v === '' || v === '-';
    }

    function computeVisibleCols() {
        if (!$id('hideEmptyCols').checked || viewData.length === 0) {
            visibleCols = [...COLUMNS];
            return;
        }
        visibleCols = COLUMNS.filter((c, idx) =>
            idx < 4 || viewData.some(row => !isEmptyVal(row[c]))
        );
    }

    /* ══════════ 렌더링 ══════════ */
    function renderTable() {
        // header
        const hd = $id('headerInfo');
        hd.innerHTML = visibleCols.map(c => {
            const mark = (sortCol === c) ? `<span class="sort-mark">${sortDir === 1 ? '▲' : '▼'}</span>` : '';
            return `<th data-col="${c}">${c}${mark}</th>`;
        }).join('');

        // body (현재 페이지만)
        const pageSize = parseInt($id('pageSize').value, 10);
        const totalPages = Math.max(1, Math.ceil(viewData.length / pageSize));
        if (curPage > totalPages) curPage = totalPages;
        const start = (curPage - 1) * pageSize;
        const pageRows = viewData.slice(start, start + pageSize);

        const html = pageRows.map((row, i) => {
            const cells = visibleCols.map((c, ci) => {
                const v = row[c] ?? '';
                const cls = ci < 4 ? 'key-col' : (isEmptyVal(v) ? 'dim' : '');
                return `<td class="${cls}" data-col="${c}" title="더블클릭 → 최초 등록 조회">${escapeHtml(v)}</td>`;
            }).join('');
            return `<tr data-idx="${start + i}">${cells}</tr>`;
        }).join('');
        $id('contentTable').innerHTML = html;

        // stats
        $id('resultStat').textContent = viewData.length.toLocaleString() + '건';
        $id('pageInfo').textContent = viewData.length === 0 ? '표시할 데이터가 없습니다'
            : `${(start + 1).toLocaleString()} – ${Math.min(start + pageSize, viewData.length).toLocaleString()} / 총 ${viewData.length.toLocaleString()}건 · 컬럼 ${visibleCols.length}/${COLUMNS.length}개 표시`;

        renderPager(totalPages);
    }

    function renderPager(totalPages) {
        const pager = $id('pager');
        const mk = (label, page, opts = {}) => {
            const dis = opts.disabled ? 'opacity-30 pointer-events-none' : '';
            const on = opts.active
                ? 'bg-apple-blue text-white shadow-btn'
                : 'bg-apple-gray6 dark:bg-white/[.06] hover:bg-apple-gray5 dark:hover:bg-white/[.12]';
            return `<button type="button" class="min-w-[32px] h-[32px] px-2 rounded-[9px] text-[12.5px] font-semibold transition ${on} ${dis}" data-page="${page}">${label}</button>`;
        };

        let pages = [];
        const win = 2;
        for (let p = 1; p <= totalPages; p++) {
            if (p === 1 || p === totalPages || Math.abs(p - curPage) <= win) pages.push(p);
        }
        let html = mk('‹', curPage - 1, { disabled: curPage === 1 });
        let prev = 0;
        for (const p of pages) {
            if (p - prev > 1) html += `<span class="text-apple-gray1 px-1">…</span>`;
            html += mk(p, p, { active: p === curPage });
            prev = p;
        }
        html += mk('›', curPage + 1, { disabled: curPage === totalPages });
        pager.innerHTML = html;
    }

    $id('pager').addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-page]');
        if (!btn) return;
        curPage = parseInt(btn.dataset.page, 10);
        renderTable();
    });

    /* 헤더 클릭 정렬 */
    $id('headerInfo').addEventListener('click', (e) => {
        const th = e.target.closest('th[data-col]');
        if (!th) return;
        const col = th.dataset.col;
        if (sortCol === col) { sortDir *= -1; } else { sortCol = col; sortDir = 1; }
        rebuildView();
    });

    /* 결과 내 필터 / 페이지크기 / 빈컬럼 토글 */
    let filterTimer = null;
    $id('tblFilter').addEventListener('input', () => {
        clearTimeout(filterTimer);
        filterTimer = setTimeout(rebuildView, 200);
    });
    $id('pageSize').addEventListener('change', () => { curPage = 1; renderTable(); });
    $id('hideEmptyCols').addEventListener('change', () => { computeVisibleCols(); renderTable(); });

    /* ══════════ Row hover 툴팁: KEY→VAL 요약 ══════════ */
    const tipEl = $id('rowTip');
    $id('contentTable').addEventListener('mouseover', (e) => {
        const tr = e.target.closest('tr[data-idx]');
        if (!tr) return;
        const row = viewData[parseInt(tr.dataset.idx, 10)];
        if (!row) return;

        let lines = [];
        for (let k = 1; k <= KEY_MAX; k++) {
            const key = row['KEY' + k], val = row['VAL' + k];
            if (key && key !== '-' && val && val !== '-') {
                lines.push(`<div><span class="tip-k">KEY${k}</span> ${escapeHtml(key)} <span class="opacity-50">→</span> ${escapeHtml(val)}</div>`);
            }
        }
        if (lines.length === 0) { tipEl.classList.remove('show'); return; }
        tipEl.innerHTML = `<div class="font-bold mb-1 text-[12.5px]">${escapeHtml(row.PID ?? '')} · KEY/VAL 요약</div>` + lines.join('');
        tipEl.classList.add('show');
    });
    $id('contentTable').addEventListener('mousemove', (e) => {
        if (!tipEl.classList.contains('show')) return;
        const pad = 16;
        let x = e.clientX + pad, y = e.clientY + pad;
        const r = tipEl.getBoundingClientRect();
        if (x + r.width > window.innerWidth - 8) x = e.clientX - r.width - pad;
        if (y + r.height > window.innerHeight - 8) y = e.clientY - r.height - pad;
        tipEl.style.left = x + 'px';
        tipEl.style.top = y + 'px';
    });
    $id('contentTable').addEventListener('mouseleave', () => tipEl.classList.remove('show'));

    /* ══════════ 내보내기 (필터 적용본 전체, 전체 컬럼) ══════════ */
    function buildRows(delim) {
        const esc = (v) => {
            let s = String(v ?? '');
            if (delim === ',' && /[",\n]/.test(s)) s = '"' + s.replace(/"/g, '""') + '"';
            return s;
        };
        const lines = [COLUMNS.join(delim)];
        for (const row of viewData) {
            lines.push(COLUMNS.map(c => esc(row[c])).join(delim));
        }
        return lines.join('\r\n');
    }

    function exportCSV() {
        if (viewData.length === 0) { toast('내보낼 데이터가 없습니다.'); return; }
        // UTF-8 BOM: 엑셀에서 한글 깨짐 방지
        const blob = new Blob(['﻿' + buildRows(',')], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = 'csv_Result.csv';
        link.click();
        URL.revokeObjectURL(link.href);
        toast('CSV 파일이 다운로드되었습니다.');
    }

    async function copyText(text) {
        try {
            await navigator.clipboard.writeText(text);
            return true;
        } catch (err) {
            // http 환경 등 clipboard API 미지원 시 폴백
            const ta = document.createElement('textarea');
            ta.value = text;
            document.body.appendChild(ta);
            ta.select();
            document.execCommand('copy');
            document.body.removeChild(ta);
            return true;
        }
    }

    async function copyToClipboard() {
        if (viewData.length === 0) { toast('복사할 데이터가 없습니다.'); return; }
        await copyText(buildRows('\t'));
        toast(viewData.length.toLocaleString() + '건이 클립보드에 복사되었습니다. 엑셀에 붙여넣기 하세요.');
    }

    /* ══════════════════════════════════════════════════════════════
     * ★ 최초 등록 조회 (화면 하단 접이식 패널)
     *   검색 조건은 문구(필수) + PID(필수) 뿐이다.
     *   → 그 문구가 처음 등록된 PID · 버전 · 행(NO) · 날짜 · 로직수정자를 팝업 표시
     *
     *   조회 대상 컬럼은 버튼에 따라 갈린다.
     *     · [조회]             → VAL1~VAL20            (/pid/findFirstPID)
     *     · [전체컬럼대상조회] → SPEC/CON/KEY/VAL 전체 (/pid/findFirstPIDAsALLColumn)
     * ════════════════════════════════════════════════════════════ */
    const FR_MODAL_MAX_ROWS = 200;   // 모달 이력 테이블에 그릴 최대 행
    let frLast = null;               // 마지막 조회 결과 (복사 기능용)

    /* 조회 대상 컬럼 라벨 — 모달 부제 / 요약에 그대로 노출된다 */
    const FR_SCOPE_VAL = 'VAL1~VAL20';
    const FR_SCOPE_ALL = 'SPEC1~30 · CON1~30 · KEY1~20 · VAL1~20';

    const modal = $id('firstRegModal');
    const frPanel = $id('frPanel');

    function openModal() {
        modal.classList.remove('hidden');
        requestAnimationFrame(() => modal.classList.add('show'));
    }
    function closeModal() {
        modal.classList.remove('show');
        setTimeout(() => modal.classList.add('hidden'), 180);
    }

    $id('frClose').addEventListener('click', closeModal);
    $id('frCloseBottom').addEventListener('click', closeModal);
    modal.addEventListener('click', (e) => { if (e.target === modal) closeModal(); });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.classList.contains('hidden')) closeModal();
    });

    $id('btnFrClear').addEventListener('click', () => {
        $id('frWord').value = '';
        $id('frPid').value = '';
        $id('frWord').focus();
    });

    function frSkeleton() {
        $id('frBody').innerHTML = `
            <div class="fr-grid">
                <div class="fr-skel"></div><div class="fr-skel"></div>
                <div class="fr-skel"></div><div class="fr-skel"></div>
            </div>
            <div class="fr-skel mt-4" style="height:120px"></div>`;
        $id('frFootNote').textContent = '조회 중…';
    }

    function frEmpty(word, msg) {
        $id('frBody').innerHTML = `
            <div class="py-10 flex flex-col items-center justify-center gap-3 text-center">
                <div class="w-14 h-14 rounded-full bg-apple-gray6 dark:bg-white/[.06] flex items-center justify-center">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#AEAEB2" stroke-width="1.8" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.35-4.35"/></svg>
                </div>
                <p class="text-[14.5px] font-semibold">${escapeHtml(msg || '해당 문구의 등록 이력을 찾지 못했습니다')}</p>
                <p class="text-[12.5px] text-apple-gray1">"${escapeHtml(word)}" — 문구를 더 짧게 하거나 PID 명칭이 정확한지 확인해 보세요.</p>
            </div>`;
        $id('frFootNote').textContent = '';
    }

    const dash = (v) => (v && String(v).trim() !== '') ? escapeHtml(v) : '<span class="text-apple-gray1">—</span>';

    /* 서버가 기준일(20250101) 이전을 먼저 보고 없을 때만 이후를 조회하므로,
       결과가 어느 구간에서 나온 것인지 배지로 알려준다. */
    function frBucketBadge(regDate) {
        const base = Date.UTC(2025, 0, 1);
        const key = api._util.dateKey(regDate);
        if (!key) return `<span class="fr-badge server">최초 등록</span>`;
        /*return key < base
            ? `<span class="fr-badge server">2025-01-01 이전 등록</span>`
            : `<span class="fr-badge approx">2025-01-01 이후 등록</span>`;*/
        return '';
    }

    function renderFirstReg(word, pid, result, scopeLabel) {
        const first = result.first;
        const rows = result.rows || [];

        // 같은 버전에서 여러 줄이 걸리면 행(NO) 을 "12 외 2개" 로 요약해 보여준다
        const noLabel = rows.length > 1
            ? `${escapeHtml(rows[0].no || first.no)} <span class="text-[12px] font-semibold text-apple-gray1">외 ${rows.length - 1}개</span>`
            : dash(first.no);

        const tiles = `
            <div class="fr-grid">
                <div class="fr-tile accent"><div class="k">PID</div><div class="v">${dash(first.pid)}</div></div>
                <div class="fr-tile"><div class="k">버전</div><div class="v">${dash(first.version)}</div></div>
                <div class="fr-tile"><div class="k">행 (NO)</div><div class="v">${noLabel}</div></div>
                <div class="fr-tile"><div class="k">등록일</div><div class="v tight">${dash(first.regDate)}</div></div>
                <div class="fr-tile"><div class="k">로직수정자</div><div class="v">${dash(first.username)}</div></div>
            </div>`;

        const metaRow = (k, v) =>
            `<div class="flex gap-3 py-[7px] border-b border-black/[.05] dark:border-white/[.07] last:border-0">
                 <span class="w-[80px] shrink-0 text-apple-gray1 font-semibold">${k}</span>
                 <span class="flex-1 min-w-0 break-all">${v}</span>
             </div>`;

        const meta = `
            <div class="mt-4 rounded-[14px] border border-black/[.06] dark:border-white/[.08] px-4 py-1 text-[12.5px]">
                ${metaRow('검색 문구', `<b>${escapeHtml(word)}</b>`)}
                ${metaRow('검색 대상', escapeHtml(scopeLabel || FR_SCOPE_VAL))}
                ${metaRow('PID 조건', pid ? escapeHtml(pid) : '<span class="text-apple-gray1">전체 PID</span>')}
                ${metaRow('PID 명', dash(first.name))}
                ${metaRow('REMARKS', dash(first.remarks))}
            </div>`;

        // 대표 1건 외에 같은 버전에서 걸린 행이 더 있을 때만 표를 붙인다
        const listRows = rows.slice(0, FR_MODAL_MAX_ROWS).map((r, i) => `
            <tr class="${i === 0 ? 'is-first' : ''}">
                <td class="font-semibold">${i === 0 ? '★ ' : ''}${dash(r.no)}</td>
                <td>${dash(r.pid)}</td>
                <td>${dash(r.version)}</td>
                <td>${dash(r.regDate)}</td>
                <td class="whitespace-nowrap">${dash(r.username)}</td>
                <td class="max-w-[280px] truncate" title="${escapeHtml(r.remarks)}">${dash(r.remarks)}</td>
            </tr>`).join('');

        const detail = rows.length <= 1 ? '' : `
            <div class="mt-5">
                <div class="flex items-center gap-2 mb-2">
                    <h4 class="text-[13px] font-bold">같은 버전에서 걸린 행</h4>
                    <span class="text-[11.5px] text-apple-gray1">${rows.length.toLocaleString()}건 · 행(NO) 오름차순${rows.length > FR_MODAL_MAX_ROWS ? ` (상위 ${FR_MODAL_MAX_ROWS}건 표시)` : ''}</span>
                </div>
                <div class="fr-tbl-wrap">
                    <table class="fr-tbl">
                        <thead><tr><th>행 (NO)</th><th>PID</th><th>버전</th><th>등록일</th><th>로직수정자</th><th>REMARKS</th></tr></thead>
                        <tbody>${listRows}</tbody>
                    </table>
                </div>
            </div>`;

        $id('frBody').innerHTML = `
            <div class="flex items-center gap-2 mb-3 flex-wrap">
                ${frBucketBadge(first.regDate)}
                <span class="text-[12px] text-apple-gray1">이 문구가 가장 먼저 등록된 버전입니다.</span>
            </div>
            ${tiles}${meta}${detail}`;

        $id('frFootNote').textContent = rows.length > 1
            ? `최초 등록 버전에서 ${rows.length.toLocaleString()}개 행이 확인되었습니다.`
            : '최초 등록 1건';
    }

    /**
     * @param preset    {word, pid} — 결과 표 더블클릭 등으로 넘어온 조건 (없으면 입력값 사용)
     * @param allColumn true 면 SPEC/CON/KEY/VAL 전체 컬럼 대상으로 조회한다
     */
    async function runFirstRegistered(preset, allColumn) {
        // 결과 표 더블클릭 등으로 값을 넘겨받으면 접힌 패널을 펴고 조건을 채운다
        if (preset && preset.word !== undefined) {
            frPanel.open = true;
            $id('frWord').value = preset.word;
            $id('frPid').value = preset.pid || '';
        }
        const word = $id('frWord').value.trim();
        const pid  = $id('frPid').value.trim();

        // 검색 문구와 PID 둘 다 필수
        if (!word) {
            frPanel.open = true;
            toast('최초 등록을 확인할 문구를 입력하세요.');
            $id('frWord').focus();
            return;
        }
        if (!pid) {
            frPanel.open = true;
            toast('조회할 PID를 입력하세요.');
            $id('frPid').focus();
            return;
        }

        const scopeLabel = allColumn ? FR_SCOPE_ALL : FR_SCOPE_VAL;

        $id('frModalSub').textContent = `"${word}" · PID ${pid} · 대상 ${scopeLabel}`;
        frSkeleton();
        openModal();

        try {
            const result = await api.findFirstRegistered({ word, pid, allColumn: !!allColumn });
            frLast = { word, pid, result, scopeLabel };

            if (result.msg) { frEmpty(word, result.msg); return; }
            if (!result.first) { frEmpty(word); return; }

            renderFirstReg(word, pid, result, scopeLabel);
        } catch (err) {
            console.error(err);
            frEmpty(word, '조회 중 오류가 발생했습니다 (' + (err && err.message ? err.message : '알 수 없는 오류') + ')');
            toast('최초 등록 조회 중 오류가 발생했습니다.');
        }
    }

    /* 모달 결과 복사 (엑셀 붙여넣기용 TSV) */
    $id('frCopy').addEventListener('click', async () => {
        if (!frLast || !frLast.result || !frLast.result.rows.length) { toast('복사할 결과가 없습니다.'); return; }
        const lines = ['PID\t버전\t행(NO)\t등록일\t로직수정자\tPID명\tREMARKS'];
        frLast.result.rows.forEach(r => {
            lines.push([r.pid, r.version, r.no, r.regDate, r.username, r.name, r.remarks].map(v => String(v ?? '')).join('\t'));
        });
        await copyText(lines.join('\r\n'));
        toast(frLast.result.rows.length.toLocaleString() + '건이 클립보드에 복사되었습니다.');
    });

    /* 결과 테이블 셀 더블클릭 → 그 값 + 그 행의 PID 로 최초 등록 조회 (PID 도 필수라 함께 채운다) */
    $id('contentTable').addEventListener('dblclick', (e) => {
        const td = e.target.closest('td[data-col]');
        if (!td) return;
        const text = td.textContent.trim();
        if (!text || text === '-') return;
        if (td.dataset.col === 'PID') return;           // PID 컬럼은 문구가 아니다

        const tr = td.closest('tr[data-idx]');
        const row = tr ? viewData[parseInt(tr.dataset.idx, 10)] : null;
        runFirstRegistered({ word: text, pid: row ? String(row.PID ?? '').trim() : '' });
    });

    /* ══════════ 버튼 바인딩 ══════════ */
    $id('btnSearch').addEventListener('click', searchPID);
    $id('btnExcel').addEventListener('click', searchExcel);
    $id('btnCSV').addEventListener('click', exportCSV);
    $id('btnCopy').addEventListener('click', copyToClipboard);
    $id('btnFirstReg').addEventListener('click', () => runFirstRegistered(null, false));
    $id('btnFirstRegAll').addEventListener('click', () => runFirstRegistered(null, true));

})();
