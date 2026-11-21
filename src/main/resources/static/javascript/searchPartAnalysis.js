/* =====================================================
   searchPartAnalysis.js
   PLM 제품-자재 Analytics – DataTables 버전
   (templates/thymeleaf/logic/searchPartAnalysisV2.html, searchPartAnalysis.html)

   서버 요청 : POST /dash/searchMissPartofProduct  — 파라미터는 collectParams() 한 곳에서 만든다
   V2 화면에만 있는 요소(#searchNotice, #resultState, #resultCriteria …)는 없으면 건너뛴다
===================================================== */

/* ── 공통 상태 ── */
const SPF_COND_KEY  = 'spf_last_cond';   // 마지막 조회 조건 (화면 다시 열 때 복원)
const SPF_CHIP_MAX  = 12;                // 분포 칩 기본 표시 수
let _kvCounter      = 0;
let _searchBusy     = false;
let _lastSearch     = null;              // { key, data } : 같은 조건이면 그래프/인터렉티브가 다시 조회하지 않음
let _chipFilter     = {};                // DataTable 컬럼 index -> 칩으로 고른 값
let _shownData      = null;              // 지금 표에 그린 결과 (통계 칩 다시 그리기용)

function spfEl(id) { return document.getElementById(id); }
function spfEsc(s) {
    return String(s == null ? '' : s).replace(/[&<>"']/g, function(c) {
        return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
}
function spfBlank(v) { v = (v == null ? '' : String(v)).trim(); return v === '' || v === '-'; }

/* =====================================================
   동적 K-V 조건
===================================================== */

const KV_OP_SQL = { like: 'LIKE', notlike: 'NOT LIKE', '=': '=', '!=': '!=' };

/**
 * Key-Value 조건 행 추가
 * 구조: [번호] [Key 입력] [조건 선택] [Value 입력] [삭제 버튼] / [안내 줄]
 * @param {{key?:string, op?:string, value?:string}=} init  값을 채워 만들 때 (포커스 이동 없음)
 */
function addKeyValueRow(init) {
    if (init && init.type) init = null;     // onclick 이벤트 객체가 넘어온 경우
    _kvCounter++;
    const id = 'kvRow_' + _kvCounter;

    const row = document.createElement('div');
    row.className = 'kv-row';
    row.id        = id;
    row.style.flexWrap = 'wrap';
    row.innerHTML =
        '<span class="kv-row-num">1</span>' +

        '<input type="text" class="spf-input kv-key"' +
        ' placeholder="Key  (예: EL_ECAA, EL_ECWRL …)"' +
        ' autocomplete="off" aria-label="동적 조건 Key">' +

        '<select class="spf-input kv-op" aria-label="동적 조건 비교 방법">' +
        '<option value="like">Like  (포함)</option>' +
        '<option value="notlike">Not Like  (미포함)</option>' +
        '<option value="=">=  Equal  (일치)</option>' +
        '<option value="!=">!=  Not Equal  (불일치)</option>' +
        '</select>' +

        '<input type="text" class="spf-input kv-value"' +
        ' placeholder="Value  (비우면 컬럼만 추가)" autocomplete="off" aria-label="동적 조건 Value">' +

        '<button type="button" class="btn-remove-kv"' +
        ' onclick="removeKeyValueRow(\'' + id + '\')"' +
        ' title="이 조건 삭제" aria-label="이 조건 삭제">' +
        '<i class="fas fa-times" aria-hidden="true"></i>' +
        '</button>' +

        '<div class="kv-hint" aria-live="polite" style="flex-basis:100%;"></div>';

    document.getElementById('kvConditionList').appendChild(row);

    if (init) {
        row.querySelector('.kv-key').value = init.key || '';
        if (init.op && KV_OP_SQL[init.op]) row.querySelector('.kv-op').value = init.op;
        row.querySelector('.kv-value').value = init.value || '';
    }
    ['input', 'change'].forEach(function(ev) {
        row.addEventListener(ev, function() { updateKvHint(row); });
    });
    row.querySelector('.kv-key').addEventListener('paste', onKvPaste);
    updateKvHint(row);
    _renumberKvRows();

    /* 직접 추가한 행이면 Key 입력 필드에 포커스 */
    if (!init) row.querySelector('.kv-key').focus();
    return row;
}

/** K-V 행 삭제 */
function removeKeyValueRow(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.style.transition = 'opacity 0.18s, transform 0.18s';
    el.style.opacity    = '0';
    el.style.transform  = 'translateY(-4px)';
    setTimeout(function() { el.remove(); _renumberKvRows(); }, 180);
}

/** 번호 재정렬 */
function _renumberKvRows() {
    const rows = document.querySelectorAll('#kvConditionList .kv-row');
    rows.forEach(function(row, idx) {
        const numEl = row.querySelector('.kv-row-num');
        if (numEl) numEl.textContent = idx + 1;
    });
}

/**
 * 행 아래 안내 : 이 조건이 실제로 어떻게 적용되는지 (서버 SQL 규칙 기준)
 *   - Value 가 비면 조건 없이 컬럼만 추가
 *   - Like 는 % 를 자동으로 붙이지 않는다 → % 가 없으면 일치 비교와 같다
 */
function updateKvHint(row) {
    const hint = row.querySelector('.kv-hint');
    if (!hint) return;
    const key = (row.querySelector('.kv-key').value   || '').trim();
    const op  = (row.querySelector('.kv-op').value    || '').trim();
    const val = (row.querySelector('.kv-value').value || '').trim();
    let html = '', cls = '';

    if (!key) {
        if (val) { html = 'Key(특성 코드)를 넣어야 조건이 적용됩니다'; cls = 'warn'; }
    } else if (!val) {
        html = '값이 비어 있어 조건 없이 결과 표에 <b>' + spfEsc(key) + '</b> 컬럼만 추가됩니다';
    } else if ((op === 'like' || op === 'notlike') && !/[%_]/.test(val)) {
        html = 'Like 는 % 를 자동으로 붙이지 않아 지금은 <b>' + (op === 'like' ? '일치' : '불일치') + '</b> 비교와 같습니다. '
             + '<button type="button" class="kv-fix">%' + spfEsc(val) + '% 로 바꾸기</button>';
        cls = 'warn';
    } else {
        html = '조건 <code>' + spfEsc(key) + ' ' + (KV_OP_SQL[op] || spfEsc(op)) + " '" + spfEsc(val) + "'</code>";
    }
    hint.className = 'kv-hint' + (cls ? ' ' + cls : '');
    hint.innerHTML = html;

    const fix = hint.querySelector('.kv-fix');
    if (fix) fix.addEventListener('click', function() {
        const v = row.querySelector('.kv-value');
        v.value = '%' + v.value.trim() + '%';
        updateKvHint(row);
        v.focus();
    });
}

/**
 * 엑셀에서 복사한 여러 줄을 Key 칸에 붙여넣으면 행을 한 번에 만든다
 *   2열 : Key  Value      3열 : Key  조건(like/notlike/=/!=)  Value
 */
function onKvPaste(e) {
    const text = (e.clipboardData || window.clipboardData).getData('text') || '';
    if (!/[\t\n]/.test(text.trim())) return;          // 한 칸짜리 붙여넣기는 그대로
    e.preventDefault();

    const items = text.split(/\r?\n/).map(function(line) {
        const c = line.split('\t').map(function(s) { return s.trim(); });
        if (!c[0]) return null;
        if (c.length >= 3 && KV_OP_SQL[c[1].toLowerCase()]) return { key: c[0], op: c[1].toLowerCase(), value: c[2] };
        return { key: c[0], value: c[1] || '' };
    }).filter(Boolean);
    if (!items.length) return;

    const first = e.target.closest('.kv-row');
    items.forEach(function(it, i) {
        let row = i === 0 ? first : addKeyValueRow(it);
        if (i === 0) {
            row.querySelector('.kv-key').value = it.key;
            if (it.op) row.querySelector('.kv-op').value = it.op;
            row.querySelector('.kv-value').value = it.value;
            updateKvHint(row);
        }
    });
    showNotice(items.length + '개 동적 조건을 붙여넣었습니다', 'info');
}

/**
 * 현재 입력된 K-V 조건 수집
 * @returns {{ key:string, op:string, value:string }[]}
 */
function getKvConditions() {
    const result = [];
    document.querySelectorAll('#kvConditionList .kv-row').forEach(function(row) {
        const key   = (row.querySelector('.kv-key')  .value || '').trim();
        const op    = (row.querySelector('.kv-op')   .value || '').trim();
        const value = (row.querySelector('.kv-value').value || '').trim();
        if (key) result.push({ key: key, op: op, value: value });
    });
    return result;
}

/* ── localStorage 유틸 ── */
function getHiddenCols() {
    try { const v = localStorage.getItem('spf_hidden_cols'); return v ? JSON.parse(v) : []; }
    catch(e) { return []; }
}
function setHiddenCols(arr) {
    try { localStorage.setItem('spf_hidden_cols', JSON.stringify(arr)); } catch(e) {}
}

/* ── 저장된 컬럼 숨김 복원 ── */
function applySavedColumnVisibility() {
    if (!$.fn.DataTable.isDataTable('#infoTable')) return;
    const table  = $('#infoTable').DataTable();
    const hidden = getHiddenCols();
    table.columns().every(function(i) {
        this.visible(hidden.indexOf(i) === -1);
    });
    if (typeof window.syncColumnButtons === 'function') window.syncColumnButtons();
}
setTimeout(applySavedColumnVisibility, 250);

/* ── DOMContentLoaded ── */
$(document).ready(function() {
    $('#dashboard').removeClass('menu-open');

    /* 엔터 키로 조회
       V2 : 조건 영역의 입력칸(동적 조건 포함)에서 Enter → 분석, 어디서나 Ctrl+Enter → 분석
            select2 직접입력 칸은 드롭다운(body 아래)에 있어 해당 없음
       구 화면 : partNo, blockNo 에서만 */
    if (spfEl('condRoot')) {
        $(document).on('keydown', '#condRoot input', function(e) {
            if (e.key !== 'Enter' || e.isComposing || e.originalEvent && e.originalEvent.isComposing) return;
            e.preventDefault();
            searchPID();
        });
        $(document).on('keydown', function(e) {
            if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) { e.preventDefault(); searchPID(); }
        });
    } else {
        $('#partNo, #blockNo').on('keyup', function(e) {
            if (e.which !== 13) return;
            searchPID();
            return false;
        });
    }

    /* 코드 목록 로드 */
    codeSetting('EL_ATYP');
    codeSettingAsBrand('EL_ABRAND');

    const cobObj    = $('#EL_COB');
    codeSetInit('EL_COB', cobObj);
    initSelect2(cobObj);

    const bwallObj  = $('#EL_BWALLT');
    codeSetInit('EL_BWALLT', bwallObj);
    initSelect2(bwallObj);

    $('#EL_ASPSCD').select2({ tags: true, placeholder: '직접 입력 또는 선택', allowClear: true });
    $('#EL_ATYP').select2({   tags: true, placeholder: '직접 입력 또는 선택', allowClear: true });
    $('#EL_BRAND').select2({  tags: true, placeholder: '직접 입력 또는 선택', allowClear: true });

    /* V2 : 마지막 조회 조건 복원 / 초기화 / 결과 칩 필터 */
    if (spfEl('spfResetBtn')) {
        restoreConditions();
        $('#spfResetBtn').on('click', resetConditions);
    }
    $(document).on('click', '.stat-chip[data-col]', function() {
        filterByChip(Number(this.getAttribute('data-col')), this.getAttribute('data-val'));
    });
    $(document).on('click', '.chip-more', function() {
        const box = this.parentNode;
        box.setAttribute('data-expanded', box.getAttribute('data-expanded') === '1' ? '0' : '1');
        if (_shownData) renderStats(_shownData, true);
    });
    $(document).on('click', '#statsFilterClear', function() { clearChipFilter(); });

    /* 핵심 조건이 채워지면 '조건 부족' 표시를 바로 지운다 */
    $('#partNo, #blockNo, #EL_BRAND, #EL_ASPSCD, #EL_ATYP').on('input change', function() {
        if (!hasKeyCondition(collectParams())) return;
        $('#partNo, #blockNo').removeAttr('aria-invalid');
        const n = spfEl('searchNotice');
        if (n && n.classList.contains('error')) showNotice('');
    });
});

function initSelect2(obj) {
    obj.select2({ tags: true, placeholder: '직접 입력 또는 선택', allowClear: true });
}

/* ── 코드 목록 ── */
function codeSetting(typeName) {
    $.ajax({
        type: 'post', crossDomain: true,
        url: '/dash/findCodeList',
        data: { typeName },
        success(data) {
            if (data && data.length) {
                const str = data.map(d => `<option value="${d.code}">${d.code} → (${d.name})</option>`).join('');
                $('#EL_ATYP').append(str);
            }
        },
        error(xhr, status, err) { console.error('codeSetting error:', status, err); }
    });
}

function codeSettingAsBrand(typeName) {
    $.ajax({
        type: 'post', crossDomain: true,
        url: '/dash/findCodeList',
        data: { typeName },
        success(data) {
            if (data && data.length) {
                const str = data.map(d => `<option value="${d.code}">${d.code} → (${d.name})</option>`).join('');
                $('#EL_BRAND').append(str);
            }
        },
        error(xhr, status, err) { console.error('codeSettingAsBrand error:', status, err); }
    });
}

/**
 * K-V 조건에서 중복 제거한 동적 Key 목록 추출 (입력 순서 유지)
 * @returns {string[]}
 */
function getDynamicKeys() {
    const seen = [];
    getKvConditions().forEach(function(c) {
        const k = (c.key || '').trim();
        if (k && seen.indexOf(k) === -1) seen.push(k);
    });
    return seen;
}

/**
 * 동적 Key 헤더(th) 동기화
 * 이전 조회에서 추가된 동적 컬럼은 제거 후 새로 추가한다.
 * @param {string[]} dynKeys
 */
function syncDynamicHeaders(dynKeys) {
    const headRow = document.querySelector('#infoTable thead tr');
    if (!headRow) return;
    headRow.querySelectorAll('th.dyn-col').forEach(function(th) { th.remove(); });
    dynKeys.forEach(function(key) {
        const th = document.createElement('th');
        th.className   = 'dyn-col';
        th.textContent = key;
        headRow.appendChild(th);
    });
}

/* =====================================================
   조회 파라미터 / 안내 / 상태
===================================================== */

/** 서버로 보내는 조회 파라미터 (분석 · 그래프 · 인터렉티브 공통, 기존과 같은 키·값) */
function collectParams() {
    return {
        partNo:       ($('#partNo').val()  || '').trim(),
        year:         $('#year').val(),
        blockNo:      ($('#blockNo').val() || '').trim(),
        cmt:          $('#cmt').val(),
        status:       $('#status').val(),
        spec:         $('#spec').val(),
        brand:        $('#EL_BRAND').val(),
        EL_ASPSCD:    $('#EL_ASPSCD').val(),
        EL_ATYP:      $('#EL_ATYP').val(),
        EL_ETHRU:     $('#EL_ETHRU').val(),
        EL_COB:       $('#EL_COB').val(),
        EL_ZFDA:      $('#EL_ZFDA').val(),
        EL_ZFDA_TYPE: $('#EL_ZFDA_TYPE').val(),
        EL_ZFDC:      $('#EL_ZFDC').val(),
        EL_ZFDC_TYPE: $('#EL_ZFDC_TYPE').val(),
        EL_BWALLT:    $('#EL_BWALLT').val(),
        kvConditions: JSON.stringify(getKvConditions())
    };
}
function paramKey(p) { return JSON.stringify(p); }

/** PartNo · BlockNo · 브랜드 · 생산거점 · 기종 중 하나라도 있는지 (없으면 전체 제품을 훑어 오래 걸린다) */
function hasKeyCondition(p) {
    return !spfBlank(p.partNo) || !spfBlank(p.blockNo) || !spfBlank(p.brand) || !spfBlank(p.EL_ASPSCD) || !spfBlank(p.EL_ATYP);
}

/** 조회 버튼 아래 안내 (V2). kind : info | warn | error */
function showNotice(msg, kind) {
    const el = spfEl('searchNotice');
    if (!el) return;
    el.className = 'spf-notice' + (kind ? ' ' + kind : '');
    el.textContent = msg || '';
    el.hidden = !msg;
}

/**
 * 핵심 조건 검사 : PartNo · BlockNo · 브랜드 · 생산거점 · 기종 중 하나는 있어야 조회한다
 * (없으면 그 년도 전체 제품을 훑어 DB 부하가 크다). 빈 값과 '-' 는 없는 것으로 본다.
 * @returns {boolean} 조회해도 되면 true
 */
function checkKeyCondition(p) {
    if (hasKeyCondition(p)) { showNotice(''); return true; }
    const msg = 'Part No. · Block No. 중 하나, 또는 브랜드 · 생산거점 · 기종 중 하나를 넣어야 조회할 수 있습니다.';
    if (spfEl('searchNotice')) showNotice(msg, 'error'); else alert(msg);
    ['partNo', 'blockNo'].forEach(function(id) { const e = spfEl(id); if (e) e.setAttribute('aria-invalid', 'true'); });
    const first = spfEl('partNo');
    if (first) first.focus();
    return false;
}

/** 조회 중 버튼 잠금 (중복 조회 방지) */
function setBusy(on) {
    _searchBusy = on;
    document.querySelectorAll('[data-spf-action]').forEach(function(b) { b.disabled = on; });
}

/**
 * 결과 영역 상태 (V2) : null(표 표시) | 'idle' | 'empty' | 'error'
 * @returns {boolean} 상태 영역이 있으면 true (구 화면은 false → alert 로 대신)
 */
function setResultState(kind, detail) {
    const box = spfEl('resultState');
    const tbl = document.querySelector('#infoTable') ? document.querySelector('#infoTable').closest('.tbl-scroll') : null;
    if (!box) return false;
    box.hidden = kind === null;
    if (tbl) tbl.style.display = kind === null ? '' : 'none';
    if (kind === null) return true;

    const icon = '<div class="rs-ico" aria-hidden="true">'
        + (kind === 'error'
            ? '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 8v5M12 16h.01"/></svg>'
            : '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.35-4.35"/></svg>')
        + '</div>';
    let html;
    if (kind === 'empty') {
        html = icon + '<p class="rs-title">조건에 맞는 결과가 없습니다</p>'
             + '<p class="rs-text">년도 · 제품 상태를 바꾸거나, Part No. 에 <b>*</b> 를 붙여 Like 검색(예: <code>26300551G0*</code>)을 해 보세요.<br>동적 조건의 Like 는 % 를 직접 넣어야 부분일치가 됩니다.</p>';
    } else if (kind === 'error') {
        html = icon + '<p class="rs-title">조회 중 오류가 발생했습니다</p>'
             + '<p class="rs-text">조건을 확인한 뒤 다시 분석하세요. 같은 오류가 반복되면 아래 내용을 담당자에게 전달해 주세요.</p>'
             + (detail ? '<pre>' + spfEsc(detail) + '</pre>' : '')
             + '<button type="button" class="btn-mini" onclick="searchPID()" style="margin-top:14px;">다시 분석</button>';
    } else {
        html = icon + '<p class="rs-title">조건을 정하고 분석을 누르세요</p>'
             + '<p class="rs-text">Part No. 또는 Block No. 를 넣고 <kbd>Enter</kbd> — 조건 칸 어디서든 Enter, 화면 어디서든 <kbd>Ctrl</kbd>+<kbd>Enter</kbd> 로 분석합니다.<br>'
             + '마지막으로 분석한 조건은 다음에 이 화면을 열 때 그대로 채워집니다.</p>';
    }
    box.className = 'result-state' + (kind === 'error' ? ' error' : '');
    box.innerHTML = html;
    return true;
}

/** 결과 머리의 조회 조건 띠 + 건수 · 걸린 시간 (V2) */
function renderCriteria(p, count, sec) {
    const el = spfEl('resultCriteria');
    if (!el) return;
    const items = [];
    function add(label, v, op) { if (!spfBlank(v)) items.push('<span class="crit"><span class="k">' + label + '</span>' + (op ? '<span class="op">' + spfEsc(op) + '</span>' : '') + '<b>' + spfEsc(v) + '</b></span>'); }
    add('년도', p.year);
    add('Part', p.partNo);
    add('Block', p.blockNo);
    add('상태', p.status === 'RLS' ? '릴리즈' : p.status);
    add('브랜드', p.brand);
    add('생산거점', p.EL_ASPSCD);
    add('기종', p.EL_ATYP);
    add('기계설계일', p.EL_ZFDA, p.EL_ZFDA_TYPE);
    add('전기설계일', p.EL_ZFDC, p.EL_ZFDC_TYPE);
    add('CMT', p.cmt);
    add('SPEC', p.spec);
    add('관통', p.EL_ETHRU);
    add('전망', p.EL_COB);
    add('WALL', p.EL_BWALLT);
    JSON.parse(p.kvConditions || '[]').forEach(function(c) {
        items.push('<span class="crit kv"><span class="k">' + spfEsc(c.key) + '</span>'
            + (c.value ? '<span class="op">' + (KV_OP_SQL[c.op] || spfEsc(c.op)) + '</span><b>' + spfEsc(c.value) + '</b>' : '<b>컬럼만</b>') + '</span>');
    });
    el.innerHTML = '<span class="crit-count"><b>' + Number(count).toLocaleString('ko-KR') + '</b>건 · ' + sec.toFixed(1) + '초</span>' + items.join('');
    el.hidden = false;
}

/* =====================================================
   조회 (분석)
===================================================== */
function searchPID() {
    if (_searchBusy) return;
    const p       = collectParams();
    const dynKeys = getDynamicKeys();

    if (!checkKeyCondition(p)) return;
    saveConditions();

    /* 기존 DataTable 파괴 */
    if ($.fn.DataTable.isDataTable('#infoTable')) {
        $('#infoTable').DataTable().clear().destroy();
    }
    $('#contentTable').empty();
    _chipFilter = {};

    showLoading();
    setBusy(true);
    const started = performance.now();

    requestAnimationFrame(() => {
        $.ajax({
            type: 'post', crossDomain: true,
            url: '/dash/searchMissPartofProduct',
            data: p,
            success(data) {
                const sec = (performance.now() - started) / 1000;
                if (data && data.length > 0) {
                    _lastSearch = { key: paramKey(p), data: data };
                    _shownData  = data;
                    setResultState(null);
                    buildTableRows(data, dynKeys);
                    initDataTable();
                    renderStats(data);
                    renderCriteria(p, data.length, sec);
                    setTimeout(function() {
                        applySavedColumnVisibility();
                        /* PartNo / BlockNo 둘 다 미입력이면 일부 컬럼 자동 숨김 */
                        if (typeof window.applyAutoHideBySearchKey === 'function') {
                            window.applyAutoHideBySearchKey();
                        }
                    }, 50);
                } else {
                    _lastSearch = null;
                    syncDynamicHeaders([]);   // 이전 동적 컬럼 정리
                    hideStats();
                    renderCriteria(p, 0, sec);
                    if (!setResultState('empty')) alert('검색결과가 없습니다.');
                }
            },
            error(xhr, status, err) {
                console.error('searchPID error:', status, err);
                _lastSearch = null;
                syncDynamicHeaders([]);
                hideStats();
                const detail = 'HTTP ' + xhr.status + (xhr.statusText ? ' ' + xhr.statusText : '') + (err && err !== xhr.statusText ? ' · ' + err : '');
                if (!setResultState('error', detail)) alert('조회 중 오류가 발생했습니다.');
            },
            complete() { hideLoading(); setBusy(false); }
        });
    });
}

/* ── 테이블 행 렌더 ── */
function buildTableRows(data, dynKeys) {
    dynKeys = dynKeys || [];
    /* 동적 Key 헤더를 먼저 동기화 (헤더 컬럼 수와 행 컬럼 수 일치 유지) */
    syncDynamicHeaders(dynKeys);

    let html = '';
    for (const d of data) {
        const cmt = (d.cmt || '').replace(/-/g, '<br>-');
        let row = `<tr>
            <td class="text-center">${d.productNo       ?? ''}</td>
            <td class="text-center">${d.productVersion  ?? ''}</td>
            <td>${d.productStatus   ?? ''}</td>
            <td>${d.productModDate  ?? ''}</td>
            <td>${d.el_ZFDA         ?? ''}</td>
            <td>${d.brand           ?? ''}</td>
            <td>${d.gisong          ?? ''}</td>
            <td>${d.aspd            ?? ''}</td>
            <td>${d.aspscd          ?? ''}</td>
            <td>${d.acapa           ?? ''}</td>

            <td>${d.ucheck          ?? ''}</td>
            <td>${d.partNo          ?? ''}</td>
            <td>${d.partName        ?? ''}</td>
            <td>${d.qty             ?? ''}</td>
            <td>${d.blockNo         ?? ''}</td>
            <td>${cmt}</td>
            <td>${d.spec            ?? ''}</td>
            <td>${d.blockopt        ?? ''}</td>
            <td>${d.glCode          ?? ''}</td>
            <td>${d.version         ?? ''}</td>

            <td>${d.ecbg            ?? ''}</td>
            <td>${d.ecwbg           ?? ''}</td>
            <td>${d.ecww            ?? ''}</td>
            <td>${d.el_BWALLT       ?? ''}</td>
            <td>${d.el_ETHRU        ?? ''}</td>
            <td>${d.el_COB          ?? ''}</td>`;

        /* 동적 Key 값 (백엔드가 Key명 그대로 result Map에 담아준다) */
        for (const key of dynKeys) {
            row += `<td>${d[key] ?? ''}</td>`;
        }
        row += `</tr>`;
        html += row;
    }
    $('#contentTable').html(html);
}

/* ── DataTable 초기화 ── */
function initDataTable() {
    $('#infoTable').DataTable({
        responsive: true,
        lengthChange: true,
        pageLength: 50,
        lengthMenu: [
            [50, 100, -1],
            ['50개', '100개', '전체']
        ],
        autoWidth: false,
        processing: true,
        /* B=버튼, l=페이지수 선택, f=검색창, r=처리중, t=테이블, i=정보, p=페이징 */
        dom: '<"row align-items-center mb-2"<"col-sm-12 col-md-6 d-flex align-items-center gap-2"Bl><"col-sm-12 col-md-6"f>>rt<"row mt-2 align-items-center"<"col-sm-12 col-md-5"i><"col-sm-12 col-md-7"p>>',
        buttons: [
            { extend: 'csv',   charset: 'UTF-16LE', text: '<i class="fas fa-file-csv"></i> CSV',   filename: 'analytics_result' },
            { extend: 'excel', charset: 'UTF-8',    text: '<i class="fas fa-file-excel"></i> Excel', filename: 'analytics_result' },
            { extend: 'copy',                        text: '<i class="fas fa-copy"></i> Copy' }
        ],
        language: {
            search:         '결과에서 찾기:',
            lengthMenu:     '_MENU_ 표시',
            info:           '전체 _TOTAL_ 건 중 _START_ – _END_ 건',
            infoEmpty:      '0 건',
            infoFiltered:   '(전체 _MAX_ 건 중 필터)',
            zeroRecords:    '찾는 값이 들어간 행이 없습니다',
            processing:     '처리 중…',
            paginate: {
                first: '◀◀', last: '▶▶', next: '▶', previous: '◀'
            }
        }
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');
}

/* ── 로딩 오버레이 (걸린 시간 표시) ── */
let _loadingTimer = null;
function showLoading() {
    if (document.getElementById('loadingOverlay')) return;
    const html = `
        <div id="loadingOverlay" role="status" aria-live="polite"
             style="position:fixed;inset:0;background:rgba(0,0,0,0.45);
                    display:flex;justify-content:center;align-items:center;z-index:2147483647;">
            <div style="background:#fff;padding:2rem 2.5rem;border-radius:1.25rem;min-width:220px;
                        text-align:center;box-shadow:0 24px 56px -8px rgba(0,0,0,0.3);">
                <div style="width:40px;height:40px;margin:0 auto 1rem;border-radius:50%;
                            border:3px solid #F2F2F7;border-top:3px solid #4F46E5;
                            animation:spfSpin 0.75s linear infinite;"></div>
                <p style="margin:0;font-size:0.875rem;font-weight:600;color:#1C1C1E;letter-spacing:-0.01em;">
                    데이터 분석 중…
                </p>
                <p id="loadingElapsed" style="margin:.35rem 0 0;font-size:0.78rem;color:#8E8E93;font-variant-numeric:tabular-nums;">0.0초</p>
            </div>
        </div>
        <style>
            @keyframes spfSpin { 0%{transform:rotate(0deg)} 100%{transform:rotate(360deg)} }
        </style>`;
    (document.body || document.documentElement).insertAdjacentHTML('beforeend', html);
    const t0 = performance.now();
    clearInterval(_loadingTimer);
    _loadingTimer = setInterval(function() {
        const el = document.getElementById('loadingElapsed');
        if (!el) { clearInterval(_loadingTimer); return; }
        const s = (performance.now() - t0) / 1000;
        el.textContent = s.toFixed(1) + '초' + (s > 15 ? ' · 조건이 넓으면 오래 걸립니다' : '');
    }, 100);
}

function hideLoading() {
    clearInterval(_loadingTimer);
    const el = document.getElementById('loadingOverlay');
    if (el) el.remove();
}

/* =====================================================
   통계 패널
===================================================== */
function hideStats() {
    const el = document.getElementById('statsPanel');
    if (el) el.style.display = 'none';
}

/** DataTable 컬럼 index (분포 칩 → 표 걸러 보기) */
const SPF_COL = { brand: 5, gisong: 6 };
const SPF_COL_NAME = { 5: '브랜드', 6: '기종' };

function renderStats(data, keepFilter) {
    const panel = document.getElementById('statsPanel');
    if (!panel || !data || !data.length) { hideStats(); return; }

    /* KPI: 전체 건수 */
    const total = data.length;
    const totalEl = document.getElementById('statTotal');
    if (totalEl) totalEl.textContent = total.toLocaleString('ko-KR');
    const badge = document.getElementById('statsTotalBadge');
    if (badge) badge.textContent = '총 ' + total.toLocaleString('ko-KR') + '건';

    /* KPI: 고유 제품 수 */
    const uniqueProducts = new Set(data.map(r => r.productNo).filter(Boolean));
    const prodEl = document.getElementById('statProducts');
    if (prodEl) prodEl.textContent = uniqueProducts.size.toLocaleString('ko-KR');

    /* KPI: 속도 (aspd) */
    const speeds = data.map(r => parseFloat(r.aspd)).filter(n => !isNaN(n) && n > 0);
    const avgEl  = document.getElementById('statSpeedAvg');
    const rngEl  = document.getElementById('statSpeedRange');
    if (speeds.length) {
        const avg = Math.round(speeds.reduce((a, b) => a + b, 0) / speeds.length);
        if (avgEl) avgEl.textContent = avg.toLocaleString('ko-KR');
        if (rngEl) rngEl.textContent = Math.min(...speeds) + ' ~ ' + Math.max(...speeds);
    } else {
        if (avgEl) avgEl.textContent = '-';
        if (rngEl) rngEl.textContent = '-';
    }

    /* 분포 헬퍼 */
    function countDist(field) {
        const map = {};
        data.forEach(function(r) {
            const v = ((r[field] || '') + '').trim() || '(미정)';
            map[v] = (map[v] || 0) + 1;
        });
        return Object.entries(map).sort(function(a, b) { return b[1] - a[1]; });
    }

    /* 칩 : 많은 순 상위 SPF_CHIP_MAX 개 + 더보기, 누르면 표를 그 값으로 걸러 봄 */
    function renderChips(id, dist, col) {
        const el = document.getElementById(id);
        if (!el) return;
        const expanded = el.getAttribute('data-expanded') === '1';
        const shown = expanded ? dist : dist.slice(0, SPF_CHIP_MAX);
        const sel = _chipFilter[col];
        el.innerHTML = shown.map(function(item) {
            const k = item[0], cnt = item[1];
            const on = sel === k;
            return '<button type="button" class="stat-chip' + (on ? ' active' : '') + '" data-col="' + col + '" data-val="' + spfEsc(k) + '"'
                + ' aria-pressed="' + on + '" title="' + spfEsc(k) + ' : ' + cnt + '건 (' + Math.round(cnt / total * 100) + '%) — 누르면 표를 이 값으로 걸러 봅니다">'
                + spfEsc(k) + '<span class="chip-cnt">' + cnt + '</span></button>';
        }).join('')
            + (dist.length > SPF_CHIP_MAX
                ? '<button type="button" class="chip-more">' + (expanded ? '접기' : '+' + (dist.length - SPF_CHIP_MAX) + '개 더보기') + '</button>'
                : '');
    }

    /* 브랜드 / 기종 분포 */
    renderChips('statBrandDist',  countDist('brand'),  SPF_COL.brand);
    renderChips('statGisongDist', countDist('gisong'), SPF_COL.gisong);

    /* 속도 구간 분포 */
    const speedDistEl = document.getElementById('statSpeedDist');
    if (speedDistEl && speeds.length) {
        const speedMap = {};
        speeds.forEach(function(s) {
            const bucket = Math.floor(s / 25) * 25;
            const label  = bucket + '~' + (bucket + 25);
            speedMap[label] = (speedMap[label] || 0) + 1;
        });
        const entries = Object.entries(speedMap).sort(function(a, b) {
            return parseInt(a[0]) - parseInt(b[0]);
        });
        speedDistEl.innerHTML = entries.map(function(item) {
            const label = item[0], cnt = item[1];
            const pct   = Math.round(cnt / speeds.length * 100);
            return '<div class="flex items-center gap-2" style="font-size:0.72rem;" title="' + label + ' m/min : ' + cnt + '건 (' + pct + '%)">' +
                '<span style="width:5rem;color:#6E6E73;flex-shrink:0;">' + label + '</span>' +
                '<div class="speed-bar-wrap flex-1"><div class="speed-bar" style="width:' + pct + '%;"></div></div>' +
                '<span style="width:2.5rem;text-align:right;font-weight:600;color:#4F46E5;">' + cnt + '</span>' +
                '</div>';
        }).join('');
    } else if (speedDistEl) {
        speedDistEl.innerHTML = '<span style="color:#8E8E93;font-size:0.8rem;">속도 데이터 없음</span>';
    }

    if (!keepFilter) renderChipFilterNote();
    panel.style.display = '';
}

/** 분포 칩 → DataTable 컬럼 정확히 일치 필터 (같은 칩을 다시 누르면 해제) */
function filterByChip(col, val) {
    if (!$.fn.DataTable.isDataTable('#infoTable')) return;
    const table = $('#infoTable').DataTable();
    if (_chipFilter[col] === val) {
        delete _chipFilter[col];
        table.column(col).search('');
    } else {
        _chipFilter[col] = val;
        const rx = val === '(미정)' ? '^\\s*$' : '^\\s*' + $.fn.dataTable.util.escapeRegex(val) + '\\s*$';
        table.column(col).search(rx, true, false);
    }
    table.draw();
    if (_shownData) renderStats(_shownData, true);
    renderChipFilterNote();
}

function clearChipFilter() {
    if ($.fn.DataTable.isDataTable('#infoTable')) {
        const table = $('#infoTable').DataTable();
        Object.keys(_chipFilter).forEach(function(c) { table.column(Number(c)).search(''); });
        table.draw();
    }
    _chipFilter = {};
    if (_shownData) renderStats(_shownData, true);
    renderChipFilterNote();
}

function renderChipFilterNote() {
    const el = spfEl('statsFilterNote');
    if (!el) return;
    const keys = Object.keys(_chipFilter);
    if (!keys.length) { el.hidden = true; el.innerHTML = ''; return; }
    el.innerHTML = '표 걸러 보는 중 : ' + keys.map(function(c) { return '<b>' + (SPF_COL_NAME[c] || c) + ' = ' + spfEsc(_chipFilter[c]) + '</b>'; }).join(', ')
        + ' <button type="button" id="statsFilterClear">해제</button>';
    el.hidden = false;
}

/* =====================================================
   조건 저장 / 복원 / 초기화 (V2)
===================================================== */
const SPF_FIELDS = ['year', 'partNo', 'blockNo', 'status', 'EL_BRAND', 'EL_ASPSCD', 'EL_ATYP',
                    'EL_ZFDA', 'EL_ZFDA_TYPE', 'EL_ZFDC', 'EL_ZFDC_TYPE', 'cmt', 'spec', 'EL_ETHRU', 'EL_COB', 'EL_BWALLT'];
const SPF_ASYNC_SELECTS = ['EL_BRAND', 'EL_ATYP', 'EL_COB', 'EL_BWALLT', 'EL_ASPSCD'];   // 코드 목록을 나중에 받아오는 select

function saveConditions() {
    if (!spfEl('spfResetBtn')) return;
    const c = { v: 1, kv: getKvConditions() };
    SPF_FIELDS.forEach(function(id) { c[id] = $('#' + id).val(); });
    try { localStorage.setItem(SPF_COND_KEY, JSON.stringify(c)); } catch (e) {}
}

function setFieldVal(id, v) {
    const $e = $('#' + id);
    if (!$e.length || v === undefined) return;
    if ($e.is('select')) {
        if (v == null || v === '') v = $e.find('option').first().val();
        const has = $e.find('option').filter(function() { return this.value === String(v); }).length;
        if (!has) {
            if (SPF_ASYNC_SELECTS.indexOf(id) === -1) return;          // 고정 목록에 없는 값은 버린다
            $e.append(new Option(v, v, false, false));                 // select2 직접입력 값 / 아직 안 온 코드
        }
        $e.val(v).trigger('change');
    } else {
        $e.val(v);
    }
}

function restoreConditions() {
    let c = null;
    try { c = JSON.parse(localStorage.getItem(SPF_COND_KEY)); } catch (e) { c = null; }
    if (!c) { setResultState('idle'); return; }

    SPF_FIELDS.forEach(function(id) { setFieldVal(id, c[id]); });
    (c.kv || []).forEach(function(kv) { addKeyValueRow(kv); });
    setResultState('idle');
    showNotice('마지막으로 분석한 조건을 채워 두었습니다. 그대로 분석하거나 [초기화] 하세요.', 'info');

    /* 코드 목록이 뒤늦게 오면 같은 값 option 이 둘이 되므로, 목록이 다 온 뒤 중복을 정리한다 */
    let done = false;
    function dedupe() {
        if (done) return; done = true;
        SPF_ASYNC_SELECTS.forEach(function(id) {
            const sel = spfEl(id);
            if (!sel) return;
            const cur = $(sel).val(), seen = {};
            Array.prototype.slice.call(sel.options).forEach(function(o) {
                const prev = seen[o.value];
                if (!prev) { seen[o.value] = o; return; }
                // 설명이 붙은 쪽(코드 → (이름))을 남긴다
                if (prev.text === prev.value) { prev.remove(); seen[o.value] = o; } else { o.remove(); }
            });
            $(sel).val(cur).trigger('change.select2');
        });
    }
    $(document).one('ajaxStop', dedupe);
    setTimeout(dedupe, 4000);
}

function resetConditions() {
    setFieldVal('year', $('#year option').first().val());
    ['partNo', 'blockNo', 'EL_ZFDA', 'EL_ZFDC', 'cmt', 'spec'].forEach(function(id) { $('#' + id).val(''); });
    setFieldVal('status', 'RLS');
    setFieldVal('EL_ZFDA_TYPE', '=');
    setFieldVal('EL_ZFDC_TYPE', '=');
    setFieldVal('EL_ETHRU', '');
    ['EL_BRAND', 'EL_ASPSCD', 'EL_ATYP', 'EL_COB', 'EL_BWALLT'].forEach(function(id) { setFieldVal(id, ''); });
    $('#kvConditionList').empty();
    try { localStorage.removeItem(SPF_COND_KEY); } catch (e) {}
    if (typeof window.releaseAutoHiddenColumns === 'function') window.releaseAutoHiddenColumns();
    showNotice('조건을 초기화했습니다.', 'info');
    $('#partNo').trigger('focus');
}

/* =====================================================
   그래프 / 인터렉티브 팝업
   조회 조건으로 데이터를 조회한 뒤 팝업에 결과를 넘긴다.
   팝업은 window.opener.__spfGraphData 또는 localStorage('spf_graph_data') 를 기다린다.
   방금 분석한 조건과 같으면 다시 조회하지 않고 그 결과를 바로 넘긴다.
===================================================== */
function searchGraph()       { openAnalysisPopup('/dash/searchGraph'); }
function searchInteractive() { openAnalysisPopup('/dash/searchInteractive'); }

function openAnalysisPopup(url) {
    if (_searchBusy) return;
    const p = collectParams();

    /* 조건 검사는 팝업을 열기 전에 (열고 나서 빠져나가면 빈 팝업이 데이터를 기다리며 멈춘다) */
    if (!checkKeyCondition(p)) return;

    /* 데이터 전달 상태 초기화 (팝업은 이 값이 채워질 때까지 대기) */
    window.__spfGraphData  = null;
    window.__spfGraphError = false;
    try {
        localStorage.removeItem('spf_graph_data');
        localStorage.removeItem('spf_graph_error');
    } catch (e) {}

    /* 팝업은 클릭(사용자 제스처) 시점에 동기적으로 열어야 차단되지 않는다. */
    const popup = window.open(url, 'spfGraphPopup', 'width=1280,height=920,scrollbars=yes,resizable=yes');
    if (!popup || popup.closed || typeof popup.closed === 'undefined') {
        alert('팝업이 차단되었습니다.\n브라우저 주소창의 팝업 차단을 해제한 뒤 다시 시도해주세요.');
        return;
    }
    popup.focus();   // 팝업이 본 창 뒤에 가려지지 않도록

    function deliver(data) {
        window.__spfGraphData = data;
        try { localStorage.setItem('spf_graph_data', JSON.stringify(data)); }
        catch (e) { /* 용량 초과 시 opener 경유로 전달 */ }
    }
    function fail(msg) {
        window.__spfGraphError = true;
        try { localStorage.setItem('spf_graph_error', '1'); } catch (e) {}
        if (popup && !popup.closed) popup.close();
        if (spfEl('searchNotice')) showNotice(msg, 'error'); else alert(msg);
    }

    const key = paramKey(p);
    if (_lastSearch && _lastSearch.key === key) {
        deliver(_lastSearch.data);
        showNotice('방금 분석한 결과(' + _lastSearch.data.length.toLocaleString('ko-KR') + '건)를 다시 조회하지 않고 새 창에 넘겼습니다.', 'info');
        return;
    }

    saveConditions();
    setBusy(true);
    $.ajax({
        type: 'post', crossDomain: true,
        url: '/dash/searchMissPartofProduct',
        data: p,
        success(data) {
            if (data && data.length > 0) {
                _lastSearch = { key: key, data: data };
                deliver(data);
            } else {
                fail('검색결과가 없습니다. 조건을 바꿔 다시 시도하세요.');
            }
        },
        error(xhr, status, err) {
            console.error('searchGraph error:', status, err);
            fail('조회 중 오류가 발생했습니다. (HTTP ' + xhr.status + ')');
        },
        complete() { setBusy(false); }
    });
}
