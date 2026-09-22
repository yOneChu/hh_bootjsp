/* ══════════════════════════════════════════════════════════════
   호기 영업사양 비교 (elevatorSpecDiff) — V2
   · searchPartAnalysisV2 와 동일한 Apple 스타일 화면 전용 스크립트
   · 기존 elevatorSpecDiff.js 는 JSP 화면에서 계속 사용하므로 건드리지 않는다.
   ══════════════════════════════════════════════════════════════ */

let dtTable = null;

/* 현재 조회 결과 (원본 + 판정 결과) */
let specRows = [];

/* "차이만 보기" 필터 상태 */
let diffOnly = false;

/* 현재 비교중인 호기 (테이블 헤더 / 카드 라벨 표시용) */
let curHogi1 = '';
let curHogi2 = '';


/* ── 유틸 ────────────────────────────────────────────────── */

function esc(v) {
    if (v === null || v === undefined) return '';
    return String(v)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

/* 값 정규화 — 공백 축약 + 소문자 */
function normVal(s) {
    return String(s === null || s === undefined ? '' : s).replace(/\s+/g, ' ').trim().toLowerCase();
}

/* 빈 값으로 취급할 것들 */
function isEmptyLike(s) {
    const v = normVal(s);
    return v === '' || v === '-' || v === 'null' || v === 'undefined';
}

/* 두 값의 비교 결과 판정
   same        : 동일
   changed     : 값 상이
   only1       : 호기1 에만 존재
   only2       : 호기2 에만 존재            */
function judge(v1, v2) {
    const e1 = isEmptyLike(v1);
    const e2 = isEmptyLike(v2);
    if (e1 && e2) return 'same';
    if (e1) return 'only2';
    if (e2) return 'only1';
    return normVal(v1) === normVal(v2) ? 'same' : 'changed';
}

const KIND_META = {
    changed: { label: '값 상이',      cls: 'kind-changed' },
    only1:   { label: '호기 1만 존재', cls: 'kind-only1' },
    only2:   { label: '호기 2만 존재', cls: 'kind-only2' },
    same:    { label: '동일',         cls: 'kind-same' }
};

/* 빈 값 표시용 */
function displayVal(v) {
    return isEmptyLike(v) ? '<span class="val-empty">(없음)</span>' : esc(String(v).trim());
}


/* ── 로딩 오버레이 ───────────────────────────────────────── */

function showLoading() {
    if (document.getElementById('loadingOverlay')) return;
    const html = `
        <div id="loadingOverlay" style="position:fixed;top:0;left:0;width:100%;height:100%;
             background:rgba(0,0,0,.45);display:flex;justify-content:center;align-items:center;z-index:9999;">
            <div style="background:#fff;padding:30px 40px;border-radius:16px;text-align:center;
                 box-shadow:0 24px 56px -8px rgba(0,0,0,.25);">
                <div style="border:4px solid rgba(0,0,0,.08);border-top:4px solid #0071E3;border-radius:50%;
                     width:40px;height:40px;animation:spin 1s linear infinite;margin:0 auto 15px;"></div>
                <p style="margin:0;font-size:14px;font-weight:600;color:#1D1D1F;">사양 비교 중...</p>
            </div>
        </div>
        <style>@keyframes spin{0%{transform:rotate(0)}100%{transform:rotate(360deg)}}</style>`;
    document.body.insertAdjacentHTML('beforeend', html);
}

function hideLoading() {
    const el = document.getElementById('loadingOverlay');
    if (el) el.remove();
}


/* ── 검색 ────────────────────────────────────────────────── */

function searchPID() {
    const hogi1 = ($('#hogi-01').val() || '').trim();
    const hogi2 = ($('#hogi-02').val() || '').trim();

    if (hogi1 === '') { alert('호기-01 값을 입력하세요.'); $('#hogi-01').focus(); return; }
    if (hogi2 === '') { alert('호기-02 값을 입력하세요.'); $('#hogi-02').focus(); return; }

    showLoading();

    $.ajax({
        type: 'post',
        crossDomain: true,
        url: '/subae/elevatorSpecDiff',
        data: { ho1: hogi1, ho2: hogi2 },
        success: function (data) {
            hideLoading();

            if (data && data[0] && data[0].msg) {
                alert(data[0].msg);
                return;
            }

            if (!data || data.length === 0) {
                alert('검색결과가 없습니다.');
                return;
            }

            specRows = data.map(function (d) {
                return {
                    type: d.TYPE || '',
                    name: d.SPEC_VALUE || '',
                    code: d.SPEC_CODE || '',
                    v1:   d.VALUE  === null || d.VALUE  === undefined ? '' : d.VALUE,
                    v2:   d.VALUE2 === null || d.VALUE2 === undefined ? '' : d.VALUE2,
                    kind: judge(d.VALUE, d.VALUE2)
                };
            });

            curHogi1 = hogi1;
            curHogi2 = hogi2;

            applyHogiLabels();
            renderDiffSummary(hogi1, hogi2);
            renderTable();
        },
        error: function (xhr) {
            hideLoading();
            console.error('elevatorSpecDiff error', xhr);
            alert('조회 중 오류가 발생했습니다.');
        }
    });
}


/* 비교 대상 호기번호를 테이블 헤더에 표시 (DataTable 생성 전에 호출) */
function applyHogiLabels() {
    $('#thHogi1No').text(curHogi1 || '');
    $('#thHogi2No').text(curHogi2 || '');
}


/* ── ① 차이점 요약 (상단) ────────────────────────────────── */

/* 요약은 "구분(값 상이 / 호기1만 / 호기2만)" 단위로 묶는다.
   영업사양 원본을 PLM DB 에서 직접 읽어오므로 TAB 정보가 없고,
   대신 무엇이 어떻게 다른지가 한눈에 들어오도록 구분으로 모은다. */
const DIFF_ORDER = ['changed', 'only1', 'only2'];

function renderDiffSummary(hogi1, hogi2) {
    const total = specRows.length;
    const diffs = specRows.filter(r => r.kind !== 'same');
    const diffCnt = diffs.length;
    const sameCnt = total - diffCnt;
    const matchRate = total > 0 ? ((sameCnt / total) * 100).toFixed(1) : '0.0';

    $('#sumHogi1').text(hogi1);
    $('#sumHogi2').text(hogi2);
    $('#statTotal').text(total.toLocaleString());
    $('#statDiff').text(diffCnt.toLocaleString());
    $('#statSame').text(sameCnt.toLocaleString());
    $('#statRate').text(matchRate + '%');
    $('#diffTotalBadge').text('차이 ' + diffCnt.toLocaleString() + '건 / 전체 ' + total.toLocaleString() + '건');

    /* 차이 없음 */
    if (diffCnt === 0) {
        $('#diffEmpty').show();
        $('#diffTabFilter').empty().hide();
        $('#diffGroups').empty().hide();
        $('#diffSummaryPanel').show();
        return;
    }
    $('#diffEmpty').hide();
    $('#diffGroups').show();

    /* 구분 단위 그룹핑 */
    const groups = new Map();
    DIFF_ORDER.forEach(function (kind) {
        const rows = diffs.filter(r => r.kind === kind);
        if (rows.length > 0) groups.set(kind, rows);
    });

    /* 구분 필터 칩 */
    let chips = `<button type="button" class="tab-chip active" data-kind="__ALL__">전체 <span class="chip-cnt">${diffCnt}</span></button>`;
    groups.forEach(function (rows, kind) {
        chips += `<button type="button" class="tab-chip" data-kind="${kind}">${KIND_META[kind].label} <span class="chip-cnt">${rows.length}</span></button>`;
    });
    $('#diffTabFilter').html(chips).show();

    /* 구분별 차이 카드 */
    let html = '';
    groups.forEach(function (rows, kind) {
        html += `<div class="diff-group" data-kind="${kind}">
                    <div class="section-label">${KIND_META[kind].label} <span class="sl-cnt">${rows.length}</span></div>
                    <div class="diff-list">`;
        rows.forEach(function (r) {
            const meta = KIND_META[r.kind];
            html += `
                <div class="diff-item">
                    <div class="diff-item-head">
                        <span class="diff-name">${esc(r.name)}</span>
                        <span class="diff-code">${esc(r.code)}</span>
                        <span class="diff-tab">${esc(r.type)}</span>
                        <span class="kind-badge ${meta.cls}">${meta.label}</span>
                    </div>
                    <div class="diff-vals">
                        <div class="val-box val-a">
                            <span class="val-cap">호기 1 · ${esc(curHogi1)}</span>
                            <span class="val-text">${displayVal(r.v1)}</span>
                        </div>
                        <span class="val-arrow">
                            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M5 12h14M13 6l6 6-6 6"/>
                            </svg>
                        </span>
                        <div class="val-box val-b">
                            <span class="val-cap">호기 2 · ${esc(curHogi2)}</span>
                            <span class="val-text">${displayVal(r.v2)}</span>
                        </div>
                    </div>
                </div>`;
        });
        html += `</div></div>`;
    });
    $('#diffGroups').html(html);
    $('#diffSummaryPanel').show();
}

/* 구분 칩 필터 (이벤트 위임) */
$(document).on('click', '#diffTabFilter .tab-chip', function () {
    const kind = $(this).data('kind');
    $('#diffTabFilter .tab-chip').removeClass('active');
    $(this).addClass('active');
    if (kind === '__ALL__') {
        $('#diffGroups .diff-group').show();
    } else {
        $('#diffGroups .diff-group').hide();
        $('#diffGroups .diff-group[data-kind="' + kind + '"]').show();
    }
});

/* 차이 목록 텍스트 복사 */
function copyDiffSummary() {
    const diffs = specRows.filter(r => r.kind !== 'same');
    if (diffs.length === 0) { alert('차이가 없습니다.'); return; }

    const h1 = ($('#hogi-01').val() || '').trim();
    const h2 = ($('#hogi-02').val() || '').trim();
    let txt = `[영업사양 차이] ${h1} vs ${h2} — 총 ${diffs.length}건\n`;
    txt += `그룹\t특성명\t특성코드\t구분\t${h1}\t${h2}\n`;
    diffs.forEach(function (r) {
        txt += `${r.type}\t${r.name}\t${r.code}\t${KIND_META[r.kind].label}\t${r.v1}\t${r.v2}\n`;
    });

    const ta = document.createElement('textarea');
    ta.value = txt;
    ta.style.position = 'fixed';
    ta.style.opacity = '0';
    document.body.appendChild(ta);
    ta.select();
    try { document.execCommand('copy'); alert('차이 ' + diffs.length + '건을 복사했습니다.'); }
    catch (e) { alert('복사에 실패했습니다.'); }
    document.body.removeChild(ta);
}


/* ── ② 전체 비교 테이블 ──────────────────────────────────── */

function renderTable() {
    if ($.fn.DataTable.isDataTable('#infoTable')) {
        $('#infoTable').DataTable().destroy();
    }
    $('#contentTable').empty();

    let str = '';
    specRows.forEach(function (r) {
        const meta = KIND_META[r.kind];
        const rowCls = r.kind === 'same' ? '' : ' row-diff';
        str += `
            <tr class="diffData${rowCls}">
                <td><span class="kind-badge ${meta.cls}">${meta.label}</span></td>
                <td>${esc(r.type)}</td>
                <td>${esc(r.name)}</td>
                <td>${esc(r.code)}</td>
                <td class="cell-val">${displayVal(r.v1)}</td>
                <td class="cell-val">${displayVal(r.v2)}</td>
            </tr>`;
    });
    $('#contentTable').append(str);

    dtTable = $('#infoTable').DataTable({
        responsive: false,
        autoWidth: false,
        lengthChange: false,
        scrollX: false,
        destroy: true,
        paging: false,
        info: false,
        searching: true,
        ordering: true,
        deferRender: true,
        dom: 'Bfrt',
        columnDefs: [
            { targets: 0, width: '110px' },
            { targets: 4, width: '240px' },
            { targets: 5, width: '240px' }
        ],
        language: {
            search: '',
            searchPlaceholder: '특성명 / 코드 / 값 검색',
            zeroRecords: '표시할 사양이 없습니다.',
            emptyTable: '조회된 사양이 없습니다.'
        },
        buttons: [
            { extend: 'csv',   charset: 'UTF-16LE', text: 'CSV',   filename: 'specDiff_result' },
            { extend: 'excel', charset: 'UTF-8',    text: 'EXCEL', filename: 'specDiff_result' },
            { extend: 'copy',  text: 'COPY' }
        ]
    });
    dtTable.buttons().container().appendTo('#tableToolbar');

    applyDiffOnly();
}

/* "차이만 보기" — DataTables 커스텀 필터 */
$.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {
    if (settings.nTable.id !== 'infoTable') return true;
    if (!diffOnly) return true;
    const row = settings.aoData[dataIndex].nTr;
    return row && row.classList.contains('row-diff');
});

function applyDiffOnly() {
    if (dtTable) dtTable.draw();
}

function toggleDiffOnly(checked) {
    diffOnly = !!checked;
    applyDiffOnly();
}


/* ── 초기화 ──────────────────────────────────────────────── */

$(document).ready(function () {

    /* 엔터키 조회 */
    $('#hogi-01, #hogi-02').on('keyup', function (event) {
        if (event.which === 13) {
            searchPID();
            return false;
        }
    });

    $('#diffOnlyToggle').on('change', function () {
        toggleDiffOnly(this.checked);
    });
});
