// 로직 비교 분석 V3
// 서버(/diff/findPIDLineDiff)가 ADDR 기준으로 과거/최신 버전을 행 단위 매칭해서
//  - columns : 컬럼 순서
//  - rows    : 행별 diff (status = EQUAL/MODIFIED/ADDED/DELETED, cells[col] = {b,a,c})
//  - summary : 상태별 건수
// 를 내려준다. 이 스크립트는 그걸 셀 단위 하이라이트로 렌더링한다.

let currentColumns = [];

document.getElementById('pidInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        searchPIDList();
    }
});

function searchPIDList() {
    let pid = document.getElementById('pidInput').value;
    if (!pid) {
        alert('PID를 입력하세요.');
        return;
    }

    const $select = $('#baseVersion');
    $select.empty();
    $select.append($('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true }));

    const $select2 = $('#compareVersion');
    $select2.empty();
    $select2.append($('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true }));

    showLoading();
    $.ajax({
        type: "get",
        url: "/diff/findPIDList",
        data: { pid: pid.toUpperCase() },
        beforeSend: function () { $("html").css("cursor", "wait"); },
        complete: function () { $("html").css("cursor", "auto"); },
        success: function (rr) {
            try {
                if (rr && rr.length > 0) {
                    rr.forEach(function (item) {
                        let value, text;
                        if (typeof item === 'string') {
                            value = item;
                            text = item;
                        } else if (item && typeof item === 'object') {
                            let version = item.VERSION || item.version || '';
                            let remarks = item.REMARKS || item.remarks || '';
                            const regDate = item.REG_DATE || item.regDate || item.reg_date || '';
                            const name = item.NAME || item.name || '';
                            const pidStr = item.PID || item.pid || '';
                            const houid = item.HOUID || item.houid || '';

                            value = houid || version || pidStr || name || remarks;

                            const parts = [];
                            if (version === '-1') version = 'TEST';
                            if (version) parts.push('v' + version);
                            if (regDate) parts.push(regDate);
                            if (name) parts.push(name);
                            if (!parts.length && pidStr) parts.push(pidStr);
                            if (remarks) parts.push(remarks);
                            text = parts.join(' | ') || value;
                        }
                        $select.append($('<option>', { value: value, text: text }));
                        $select2.append($('<option>', { value: value, text: text }));
                    });
                }
            } catch (e) {
                console.error("목록 처리 중 오류:", e);
            }
            hideLoading();
        },
        error: function () {
            hideLoading();
            alert('목록을 가져오는 중 오류가 발생했습니다.');
        }
    });
}

document.getElementById('searchBtn').addEventListener('click', () => {
    const pid = document.getElementById('pidInput').value;
    const v1 = document.getElementById('baseVersion').value;
    const v2 = document.getElementById('compareVersion').value;

    if (!pid || !v1 || !v2) {
        alert("PID와 두 버전(과거/최신)을 모두 선택해주세요.");
        return;
    }

    showLoading();
    $.ajax({
        type: "post",
        url: "/diff/findPIDLineDiff",
        data: {
            pid: pid.toUpperCase().trim(),
            pidOid: v1,   // 과거(기준)
            pidOidb: v2   // 최신(비교)
        },
        beforeSend: function () { $("html").css("cursor", "wait"); },
        complete: function () { $("html").css("cursor", "auto"); },
        success: function (rr) {
            //console.log(rr);
            currentColumns = rr.columns || [];
            renderHeader(currentColumns);
            renderSummary(rr.summary || {});
            renderRows(rr.rows || [], currentColumns);
            applyFilter();
            hideLoading();
        },
        error: function () {
            hideLoading();
            alert('데이터를 가져오는 중 오류가 발생하였습니다.');
        }
    });
});

// HTML 이스케이프
function esc(s) {
    if (s === null || s === undefined) return '';
    return String(s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

// "값 \n(코드명)" 형태를 셀 표시용 HTML 로 변환
function fmtCell(v) {
    v = esc(v).replace(/\n/g, '<br>');
    return v === '' ? '' : v;
}

// 상태별 스타일
const STATUS = {
    EQUAL: { label: '동일', row: 'bg-white hover:bg-slate-100 text-slate-500', badge: 'bg-slate-100 text-slate-500' },
    MODIFIED: { label: '수정', row: 'bg-amber-50 hover:bg-amber-100', badge: 'bg-amber-200 text-amber-800 font-bold' },
    ADDED: { label: '추가', row: 'bg-green-50 hover:bg-green-100', badge: 'bg-green-200 text-green-800 font-bold' },
    DELETED: { label: '삭제', row: 'bg-red-50 text-red-400 line-through hover:bg-red-100', badge: 'bg-red-200 text-red-800 font-bold no-underline' }
};

// 헤더 렌더링: 상태 / NO / (컬럼들)
function renderHeader(columns) {
    const headerRow = document.getElementById('table-header');
    headerRow.innerHTML = '';

    const thBase = 'px-4 py-3 font-semibold border-b border-r border-gray-300 text-slate-600 text-xs';

    const thStatus = document.createElement('th');
    thStatus.className = thBase + ' text-center sticky left-0 bg-slate-100 z-30';
    thStatus.textContent = '상태';
    headerRow.appendChild(thStatus);

    const thNo = document.createElement('th');
    thNo.className = thBase + ' text-center sticky left-[64px] bg-slate-100 z-30';
    thNo.textContent = 'NO';
    headerRow.appendChild(thNo);

    columns.forEach(col => {
        const th = document.createElement('th');
        th.className = thBase;
        th.textContent = col;
        headerRow.appendChild(th);
    });
}

// 요약 바
function renderSummary(summary) {
    const el = document.getElementById('diff-summary');
    if (!el) return;
    const chip = (cls, label, n) =>
        `<span class="px-3 py-1 rounded-full text-xs font-bold ${cls}">${label} ${n || 0}</span>`;
    el.innerHTML =
        chip('bg-green-200 text-green-800', '추가', summary.added) +
        chip('bg-amber-200 text-amber-800', '수정', summary.modified) +
        chip('bg-red-200 text-red-800', '삭제', summary.deleted) +
        chip('bg-slate-200 text-slate-600', '동일', summary.equal) +
        chip('bg-blue-100 text-blue-700', '전체', summary.total);
}

// 행 렌더링
function renderRows(rows, columns) {
    const tbody = document.getElementById('bom-tbody');
    tbody.innerHTML = '';

    if (!rows.length) {
        tbody.innerHTML = '<tr><td colspan="200" class="text-center py-10 text-gray-500">비교 결과가 없습니다.</td></tr>';
        return;
    }

    const cellClass = "px-4 py-2 border-b border-r border-gray-300";

    rows.forEach(row => {
        const conf = STATUS[row.status] || STATUS.EQUAL;
        const tr = document.createElement('tr');
        tr.className = conf.row;
        tr.dataset.status = row.status;

        const cells = row.cells || {};
        const isDeleted = row.status === 'DELETED';
        const firstRowBg = conf.row.split(' ')[0]; // sticky 셀 배경 맞춤

        let html = '';

        // 상태 배지
        html += `<td class="px-4 py-2 text-center border-b border-r border-gray-300 sticky left-0 z-10 ${firstRowBg}">`
            + `<span class="text-xs px-2 py-1 rounded ${conf.badge}">${conf.label}</span></td>`;

        // NO (최신 우선, 없으면 과거)
        const no = row.noAfter || row.noBefore || '';
        html += `<td class="${cellClass} sticky left-[64px] z-10 ${firstRowBg}">${esc(no)}</td>`;

        // 각 컬럼 셀
        columns.forEach(col => {
            const c = cells[col] || { a: '', b: '', c: false };
            const display = isDeleted ? c.b : c.a;

            if (c.c) {
                // 변경된 셀: 최신값 + 그 아래 이전값(취소선) 표시 + 노란 배경
                const before = (c.b === '' || c.b === undefined) ? '<span class="text-gray-300">(없음)</span>' : fmtCell(c.b);
                html += `<td class="${cellClass} bg-yellow-200">`
                    + `<div class="font-bold">${fmtCell(c.a)}</div>`
                    + `<div class="text-[10px] text-red-500 line-through">${before}</div>`
                    + `</td>`;
            } else {
                html += `<td class="${cellClass}">${fmtCell(display)}</td>`;
            }
        });

        tr.innerHTML = html;
        attachTooltip(tr, row, columns);
        attachRowSelect(tr);
        tbody.appendChild(tr);
    });
}

// 행 호버 시 팝오버: 변경내역 + KEY/VAL/GOTO/REMARKS 요약
function attachTooltip(tr, row, columns) {
    const cells = row.cells || {};
    const isDeleted = row.status === 'DELETED';
    let text = '';

    if (row.status === 'MODIFIED') {
        const changes = [];
        columns.forEach(col => {
            const c = cells[col];
            if (c && c.c) {
                const b = (c.b || '').replace(/\n/g, ' ');
                const a = (c.a || '').replace(/\n/g, ' ');
                changes.push(`${col} ::: ${b || '(없음)'}  >>  ${a || '(없음)'}`);
            }
        });
        if (changes.length) text += '◆ 변경 내역\n' + changes.join('\n') + '\n\n';
    }

    const pick = (col) => {
        const c = cells[col];
        if (!c) return '';
        return ((isDeleted ? c.b : c.a) || '').replace(/\n/g, ' ');
    };

    let kv = '';
    for (let k = 1; k <= 20; k++) {
        const key = pick('KEY' + k);
        const val = pick('VAL' + k);
        if (key && key !== '-') kv += `KEY${k}-VAL${k} ::: ${key} > ${val}\n`;
    }
    const goto = pick('GOTO');
    const remarks = pick('REMARKS');
    if (kv) text += kv;
    if (goto) text += `GOTO ::: ${goto}\n`;
    if (remarks) text += `REMARKS ::: ${remarks}\n`;

    text = text.trim();
    if (!text) return;

    const popover = document.getElementById('custom-popover');
    tr.addEventListener('mouseenter', () => {
        popover.innerText = text;
        popover.style.display = 'block';
    });
    tr.addEventListener('mousemove', (e) => {
        popover.style.left = (e.clientX + 15) + 'px';
        popover.style.top = (e.clientY + 15) + 'px';
    });
    tr.addEventListener('mouseleave', () => {
        popover.style.display = 'none';
    });
}

// 행 클릭 선택(노란 하이라이트)
function attachRowSelect(tr) {
    tr.addEventListener('mousedown', (e) => { if (e.ctrlKey) e.preventDefault(); });
    tr.addEventListener('click', function (e) {
        const highlight = (r) => { r.classList.add('selected-row'); };
        const unhighlight = (r) => { r.classList.remove('selected-row'); };
        if (e.ctrlKey) {
            this.classList.contains('selected-row') ? unhighlight(this) : highlight(this);
        } else {
            const tbodyEl = this.parentNode;
            if (tbodyEl) tbodyEl.querySelectorAll('tr.selected-row').forEach(unhighlight);
            highlight(this);
        }
    });
}

// 상태 필터 (체크박스로 EQUAL 등 숨기기)
function applyFilter() {
    const show = {
        EQUAL: document.getElementById('filter-equal')?.checked ?? true,
        MODIFIED: document.getElementById('filter-modified')?.checked ?? true,
        ADDED: document.getElementById('filter-added')?.checked ?? true,
        DELETED: document.getElementById('filter-deleted')?.checked ?? true
    };
    document.querySelectorAll('#bom-tbody tr').forEach(tr => {
        const st = tr.dataset.status;
        if (st === undefined) return;
        tr.style.display = show[st] ? '' : 'none';
    });
}

['filter-equal', 'filter-modified', 'filter-added', 'filter-deleted'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.addEventListener('change', applyFilter);
});

// 빈 열 숨기기 (모든 표시행에서 값이 없는 데이터 컬럼 숨김)
document.getElementById('hide-empty-cols').addEventListener('click', function () {
    const table = document.querySelector('table');
    const rows = table.querySelectorAll('tbody tr');
    const headers = table.querySelectorAll('thead th');

    // 상태(0), NO(1) 는 건너뛰고 데이터 컬럼(2~)만 검사
    for (let colIndex = 2; colIndex < headers.length; colIndex++) {
        let hasValue = false;
        rows.forEach(row => {
            const cell = row.cells[colIndex];
            if (cell) {
                const t = cell.textContent.trim();
                if (t !== '' && t !== '-' && t !== 'undefined' && t !== 'null') hasValue = true;
            }
        });
        if (!hasValue) {
            headers[colIndex].style.display = 'none';
            rows.forEach(row => { if (row.cells[colIndex]) row.cells[colIndex].style.display = 'none'; });
        }
    }

    this.textContent = '빈 열 숨김 완료';
    this.disabled = true;
    this.classList.replace('bg-green-600', 'bg-gray-400');
});
