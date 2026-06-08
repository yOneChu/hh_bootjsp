/* =====================================================
   searchPartFinderTest.js
   PLM 제품-자재 Analytics – DataTables 버전
===================================================== */

/* ── 동적 K-V 조건 관리 ── */
let _kvCounter = 0;

/**
 * Key-Value 조건 행 추가
 * 구조: [번호] [Key 입력] [Like / Not Like 선택] [Value 입력] [삭제 버튼]
 */
function addKeyValueRow() {
    _kvCounter++;
    const id = 'kvRow_' + _kvCounter;

    const row = document.createElement('div');
    row.className = 'kv-row';
    row.id        = id;
    row.innerHTML =
        '<span class="kv-row-num">1</span>' +

        '<input type="text" class="spf-input kv-key"' +
        ' placeholder="Key  (예: EL_ECAA, EL_ECWRL …)"' +
        ' autocomplete="off">' +

        '<select class="spf-input kv-op">' +
        '<option value="like">Like  (포함)</option>' +
        '<option value="notlike">Not Like  (미포함)</option>' +
        '<option value="=">=  Equal  (일치)</option>' +
        '<option value="!=">!=  Not Equal  (불일치)</option>' +
        '</select>' +

        '<input type="text" class="spf-input kv-value"' +
        ' placeholder="Value" autocomplete="off">' +

        '<button type="button" class="btn-remove-kv"' +
        ' onclick="removeKeyValueRow(\'' + id + '\')"' +
        ' title="이 조건 삭제">' +
        '<i class="fas fa-times"></i>' +
        '</button>';

    document.getElementById('kvConditionList').appendChild(row);
    _renumberKvRows();

    /* 추가된 행의 Key 입력 필드에 포커스 */
    row.querySelector('.kv-key').focus();
}

/** K-V 행 삭제 */
function removeKeyValueRow(id) {
    const el = document.getElementById(id);
    if (!el) return;
    /* 삭제 애니메이션 */
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
 * 현재 입력된 K-V 조건 수집
 * @returns {{ key:string, op:string, value:string }[]}
 */
function getKvConditions() {
    const result = [];
    document.querySelectorAll('#kvConditionList .kv-row').forEach(function(row) {
        const key   = (row.querySelector('.kv-key')  .value || '').trim();
        const op    = (row.querySelector('.kv-op')   .value || '').trim();
        const value = (row.querySelector('.kv-value').value || '').trim();
        //if (key && value) result.push({ key: key, op: op, value: value });
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

    /* 엔터 키로 조회 */
    $(document).keyup(function(e) {
        if (e.which === 13) { searchPID(); return false; }
    });

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
    //$('#status').select2({    placeholder: '선택', allowClear: true });
});

function initSelect2(obj) {
    obj.select2({ tags: true, placeholder: '직접 입력 또는 선택', allowClear: true });
}

/* ── 코드 목록 ── */
function codeSetting(typeName) {
    $.ajax({
        type: 'post', crossDomain: true,
        //url: '/subae/findCodeList',
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
        //url: '/subae/findCodeList',
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
    /* 이전 동적 헤더 제거 */
    headRow.querySelectorAll('th.dyn-col').forEach(function(th) { th.remove(); });
    /* 새 동적 헤더 추가 */
    dynKeys.forEach(function(key) {
        const th = document.createElement('th');
        th.className   = 'dyn-col';
        th.textContent = key;
        headRow.appendChild(th);
    });
}

/* ── 조회 ── */
function searchPID() {
    const year         = $('#year').val();
    const partNo       = ($('#partNo').val()  || '').trim();
    const blockNo      = ($('#blockNo').val() || '').trim();
    const cmt          = $('#cmt').val();
    const status       = $('#status').val();
    const spec         = $('#spec').val();
    const brand        = $('#EL_BRAND').val();
    const EL_ASPSCD    = $('#EL_ASPSCD').val();
    const EL_ATYP      = $('#EL_ATYP').val();
    const EL_ETHRU     = $('#EL_ETHRU').val();
    const EL_COB       = $('#EL_COB').val();
    const EL_BWALLT    = $('#EL_BWALLT').val();
    const EL_ZFDA      = $('#EL_ZFDA').val();
    const EL_ZFDA_TYPE = $('#EL_ZFDA_TYPE').val();

    /* 동적 K-V 조건 (유효한 항목만 직렬화) */
    const dynKeys      = getDynamicKeys();
    const kvConditions = JSON.stringify(getKvConditions());

    if (!partNo && !blockNo) {
        alert('PartNo 또는 BlockNo 중 하나는 필수 입력 사항입니다.');
        return;
    }

    /* 기존 DataTable 파괴 */
    if ($.fn.DataTable.isDataTable('#infoTable')) {
        $('#infoTable').DataTable().clear().destroy();
    }
    $('#contentTable').empty();

    showLoading();


    //console.log(kvConditions);

    requestAnimationFrame(() => {
        $.ajax({
            type: 'post', crossDomain: true,
            //url: '/subae/searchMissPartofProduct',
            url: '/dash/searchMissPartofProduct',
            data: { partNo, year, blockNo, cmt, status, spec, brand,
                EL_ASPSCD, EL_ATYP, EL_ETHRU, EL_COB,
                EL_ZFDA, EL_ZFDA_TYPE, EL_BWALLT,
                kvConditions },
            success(data) {
                if (data && data.length > 0) {
                    buildTableRows(data, dynKeys);
                    initDataTable();
                    renderStats(data);
                    setTimeout(applySavedColumnVisibility, 50);
                } else {
                    syncDynamicHeaders([]);   // 이전 동적 컬럼 정리
                    hideStats();
                    alert('검색결과가 없습니다.');
                }
            },
            error(xhr, status, err) {
                console.error('searchPID error:', status, err);
                alert('조회 중 오류가 발생했습니다.');
            },
            complete() { hideLoading(); }
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
        //console.log('data --- ',d)
        const cmt = (d.cmt || '').replace(/-/g, '<br>-');
        let row = `<tr>
            <td>${d.productNo       ?? ''}</td>
            <td>${d.productVersion  ?? ''}</td>
            <td>${d.productStatus   ?? ''}</td>
            <td>${d.productModDate  ?? ''}</td>
            <td>${d.el_ZFDA         ?? ''}</td>
            <td>${d.brand           ?? ''}</td>
            <td>${d.gisong          ?? ''}</td>
            <td>${d.aspd            ?? ''}</td>
            <td>${d.aspscd          ?? ''}</td>
            <td>${d.el_ETHRU        ?? ''}</td>
            <td>${d.el_COB          ?? ''}</td>
            <td>${d.acapa           ?? ''}</td>
            <td>${d.ecbg            ?? ''}</td>
            <td>${d.ecwbg           ?? ''}</td>
            <td>${d.ecww            ?? ''}</td>
            <td>${d.partNo          ?? ''}</td>
            <td>${d.partName        ?? ''}</td>
            <td>${d.spec            ?? ''}</td>
            <td>${d.qty             ?? ''}</td>
            <td>${d.blockNo         ?? ''}</td>
            <td>${d.blockopt        ?? ''}</td>
            <td>${d.glCode          ?? ''}</td>
            <td>${d.version         ?? ''}</td>
            <td>${cmt}</td>
            <td>${d.el_BWALLT       ?? ''}</td>`;
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
            search:         '검색:',
            lengthMenu:     '_MENU_ 표시',
            info:           '전체 _TOTAL_ 건 중 _START_ – _END_ 건',
            infoEmpty:      '0 건',
            infoFiltered:   '(전체 _MAX_ 건 중 필터)',
            paginate: {
                first: '◀◀', last: '▶▶', next: '▶', previous: '◀'
            }
        }
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');
}

/* ── 로딩 오버레이 ── */
function showLoading() {
    if (document.getElementById('loadingOverlay')) return;
    const html = `
        <div id="loadingOverlay"
             style="position:fixed;inset:0;background:rgba(0,0,0,0.45);
                    display:flex;justify-content:center;align-items:center;z-index:2147483647;">
            <div style="background:#fff;padding:2rem 2.5rem;border-radius:1.25rem;
                        text-align:center;box-shadow:0 24px 56px -8px rgba(0,0,0,0.3);">
                <div style="width:40px;height:40px;margin:0 auto 1rem;border-radius:50%;
                            border:3px solid #F2F2F7;border-top:3px solid #4F46E5;
                            animation:spfSpin 0.75s linear infinite;"></div>
                <p style="margin:0;font-size:0.875rem;font-weight:600;color:#1C1C1E;letter-spacing:-0.01em;">
                    데이터 분석 중…
                </p>
            </div>
        </div>
        <style>
            @keyframes spfSpin { 0%{transform:rotate(0deg)} 100%{transform:rotate(360deg)} }
        </style>`;
    (document.body || document.documentElement).insertAdjacentHTML('beforeend', html);
}

function hideLoading() {
    const el = document.getElementById('loadingOverlay');
    if (el) el.remove();
}

/* ── 통계 패널 ── */
function hideStats() {
    const el = document.getElementById('statsPanel');
    if (el) el.style.display = 'none';
}

function renderStats(data) {
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

    function renderChips(id, dist) {
        const el = document.getElementById(id);
        if (!el) return;
        el.innerHTML = dist.map(function(item) {
            const k = item[0], cnt = item[1];
            return '<span class="stat-chip">' + k +
                '<span class="chip-cnt">' + cnt + '</span></span>';
        }).join('');
    }

    /* 브랜드 / 기종 분포 */
    renderChips('statBrandDist',  countDist('brand'));
    renderChips('statGisongDist', countDist('gisong'));

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
            return '<div class="flex items-center gap-2" style="font-size:0.72rem;">' +
                '<span style="width:5rem;color:#6E6E73;flex-shrink:0;">' + label + '</span>' +
                '<div class="speed-bar-wrap flex-1"><div class="speed-bar" style="width:' + pct + '%;"></div></div>' +
                '<span style="width:2.5rem;text-align:right;font-weight:600;color:#4F46E5;">' + cnt + '</span>' +
                '</div>';
        }).join('');
    } else if (speedDistEl) {
        speedDistEl.innerHTML = '<span style="color:#8E8E93;font-size:0.8rem;">속도 데이터 없음</span>';
    }

    panel.style.display = '';
}


/* ──────────────────────────────────────────────
   그래프/통계 팝업
   조회 조건으로 데이터를 조회한 뒤 팝업(searchPartGraph.html)에
   결과를 넘겨 집계·통계 그래프를 그린다.
────────────────────────────────────────────── */
function searchGraph() {
    const partNo  = ($('#partNo').val()  || '').trim();
    const blockNo = ($('#blockNo').val() || '').trim();

    if (!partNo && !blockNo) {
        alert('PartNo 또는 BlockNo 중 하나는 필수 입력 사항입니다.');
        return;
    }

    /* 데이터 전달 상태 초기화 (팝업은 이 값이 채워질 때까지 대기) */
    window.__spfGraphData  = null;
    window.__spfGraphError = false;
    try {
        localStorage.removeItem('spf_graph_data');
        localStorage.removeItem('spf_graph_error');
    } catch (e) {}

    /* 팝업은 클릭(사용자 제스처) 시점에 동기적으로 열어야 차단되지 않는다. */
    const popup = window.open(
        '/dash/searchGraph', 'spfGraphPopup',
        'width=1280,height=920,scrollbars=yes,resizable=yes'
    );
    if (!popup || popup.closed || typeof popup.closed === 'undefined') {
        alert('팝업이 차단되었습니다.\n브라우저 주소창의 팝업 차단을 해제한 뒤 다시 시도해주세요.');
        return;
    }
    popup.focus();   // 팝업이 본 창 뒤에 가려지지 않도록

    /* 조회 파라미터 (searchPID 와 동일) */
    const year         = $('#year').val();
    const cmt          = $('#cmt').val();
    const status       = $('#status').val();
    const spec         = $('#spec').val();
    const brand        = $('#EL_BRAND').val();
    const EL_ASPSCD    = $('#EL_ASPSCD').val();
    const EL_ATYP      = $('#EL_ATYP').val();
    const EL_ETHRU     = $('#EL_ETHRU').val();
    const EL_COB       = $('#EL_COB').val();
    const EL_BWALLT    = $('#EL_BWALLT').val();
    const EL_ZFDA      = $('#EL_ZFDA').val();
    const EL_ZFDA_TYPE = $('#EL_ZFDA_TYPE').val();
    const kvConditions = JSON.stringify(getKvConditions());

    $.ajax({
        type: 'post', crossDomain: true,
        //url: '/subae/searchMissPartofProduct',
        url: '/dash/searchMissPartofProduct',
        data: { partNo, year, blockNo, cmt, status, spec, brand,
            EL_ASPSCD, EL_ATYP, EL_ETHRU, EL_COB,
            EL_ZFDA, EL_ZFDA_TYPE, EL_BWALLT,
            kvConditions },
        success(data) {
            if (data && data.length > 0) {
                /* 팝업은 window.opener.__spfGraphData 또는 localStorage 로 읽어간다 */
                window.__spfGraphData = data;
                try { localStorage.setItem('spf_graph_data', JSON.stringify(data)); }
                catch (e) { /* 용량 초과 시 opener 경유로 전달 */ }
            } else {
                window.__spfGraphError = true;
                try { localStorage.setItem('spf_graph_error', '1'); } catch (e) {}
                alert('검색결과가 없습니다.');
                if (popup && !popup.closed) popup.close();
            }
        },
        error(xhr, status, err) {
            console.error('searchGraph error:', status, err);
            window.__spfGraphError = true;
            try { localStorage.setItem('spf_graph_error', '1'); } catch (e) {}
            alert('조회 중 오류가 발생했습니다.');
            if (popup && !popup.closed) popup.close();
        }
    });
}

/**
 * 그래프
 */
function searchInteractive() {
    const partNo  = ($('#partNo').val()  || '').trim();
    const blockNo = ($('#blockNo').val() || '').trim();

    if (!partNo && !blockNo) {
        alert('PartNo 또는 BlockNo 중 하나는 필수 입력 사항입니다.');
        return;
    }

    /* 데이터 전달 상태 초기화 (팝업은 이 값이 채워질 때까지 대기) */
    window.__spfGraphData  = null;
    window.__spfGraphError = false;
    try {
        localStorage.removeItem('spf_graph_data');
        localStorage.removeItem('spf_graph_error');
    } catch (e) {}

    /* 팝업은 클릭(사용자 제스처) 시점에 동기적으로 열어야 차단되지 않는다. */
    const popup = window.open(
        '/dash/searchInteractive', 'spfGraphPopup',
        'width=1280,height=920,scrollbars=yes,resizable=yes'
    );
    if (!popup || popup.closed || typeof popup.closed === 'undefined') {
        alert('팝업이 차단되었습니다.\n브라우저 주소창의 팝업 차단을 해제한 뒤 다시 시도해주세요.');
        return;
    }
    popup.focus();   // 팝업이 본 창 뒤에 가려지지 않도록

    /* 조회 파라미터 (searchPID 와 동일) */
    const year         = $('#year').val();
    const cmt          = $('#cmt').val();
    const status       = $('#status').val();
    const spec         = $('#spec').val();
    const brand        = $('#EL_BRAND').val();
    const EL_ASPSCD    = $('#EL_ASPSCD').val();
    const EL_ATYP      = $('#EL_ATYP').val();
    const EL_ETHRU     = $('#EL_ETHRU').val();
    const EL_COB       = $('#EL_COB').val();
    const EL_BWALLT    = $('#EL_BWALLT').val();
    const EL_ZFDA      = $('#EL_ZFDA').val();
    const EL_ZFDA_TYPE = $('#EL_ZFDA_TYPE').val();
    const kvConditions = JSON.stringify(getKvConditions());

    $.ajax({
        type: 'post', crossDomain: true,
        //url: '/subae/searchMissPartofProduct',
        url: '/dash/searchMissPartofProduct',
        data: { partNo, year, blockNo, cmt, status, spec, brand,
            EL_ASPSCD, EL_ATYP, EL_ETHRU, EL_COB,
            EL_ZFDA, EL_ZFDA_TYPE, EL_BWALLT,
            kvConditions },
        success(data) {
            if (data && data.length > 0) {
                /* 팝업은 window.opener.__spfGraphData 또는 localStorage 로 읽어간다 */
                window.__spfGraphData = data;
                try { localStorage.setItem('spf_graph_data', JSON.stringify(data)); }
                catch (e) { /* 용량 초과 시 opener 경유로 전달 */ }
            } else {
                window.__spfGraphError = true;
                try { localStorage.setItem('spf_graph_error', '1'); } catch (e) {}
                alert('검색결과가 없습니다.');
                if (popup && !popup.closed) popup.close();
            }
        },
        error(xhr, status, err) {
            console.error('searchGraph error:', status, err);
            window.__spfGraphError = true;
            try { localStorage.setItem('spf_graph_error', '1'); } catch (e) {}
            alert('조회 중 오류가 발생했습니다.');
            if (popup && !popup.closed) popup.close();
        }
    });
}