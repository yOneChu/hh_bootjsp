// 로직 비교 분석 V3
// 서버(/diff/findPIDLineDiff)가 과거/최신 버전을 행 단위로 매칭해서 내려준다.
//  - columns : 컬럼 순서
//  - rows    : 행별 diff
//        status    = EQUAL / MODIFIED / ADDED / DELETED
//        moved     = 행 순서가 바뀜,  fuzzy = ADDR 이 달라 내용 유사도로 매칭됨
//        shiftOnly = 값 변경 없이 열 위치만 달라짐
//        cells[col] = { b:과거값, a:최신값, c:차이여부, t:chg|add|del|mov, f:과거 컬럼(위치가 다를 때) }
//  - summary : 상태별 건수
// 이 스크립트는 그 결과를 셀 단위로 렌더링하고, 필터/검색/변경 이동/상세 비교/CSV 내보내기를 제공한다.
(function () {
    'use strict';

    const $id = (id) => document.getElementById(id);

    const STATUS = {
        ADDED: { label: '추가', cls: 'add', mark: '+' },
        MODIFIED: { label: '수정', cls: 'mod', mark: '~' },
        DELETED: { label: '삭제', cls: 'del', mark: '−' },
        EQUAL: { label: '동일', cls: 'eq', mark: '=' }
    };
    const CELL_TYPE = { chg: '값 변경', add: '값 추가', del: '값 삭제', mov: '열 위치 이동' };

    const state = {
        columns: [],
        rows: [],
        summary: {},
        show: { ADDED: true, MODIFIED: true, DELETED: true, EQUAL: true },
        hideEmpty: true,
        changedColsOnly: false,
        detailChangedOnly: false,
        query: '',
        selected: -1,
        colStats: {},     // col -> { hasValue, diff }
        diffList: [],     // 화면에 보이는 변경행 index 목록
        lastRequest: null
    };

    /* ---------- 공통 ---------- */

    function esc(s) {
        if (s === null || s === undefined) return '';
        return String(s)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
    }

    // 셀 표시용: 줄바꿈("값 \n(코드명)")을 <br> 로
    function fmt(v) {
        return esc(v).replace(/\n/g, '<br>');
    }

    // 두 값의 앞/뒤 공통 부분을 빼고 실제로 달라진 가운데만 강조
    function inlineDiff(before, after) {
        const b = String(before || ''), a = String(after || '');
        let p = 0;
        const max = Math.min(b.length, a.length);
        while (p < max && b[p] === a[p]) p++;
        let s = 0;
        while (s < max - p && b[b.length - 1 - s] === a[a.length - 1 - s]) s++;
        // 공통 부분이 거의 없으면 전체가 바뀐 것이므로 강조하지 않음
        if (p + s < 2) return { b: fmt(b), a: fmt(a) };
        const wrap = (str) => {
            const mid = str.slice(p, str.length - s);
            return fmt(str.slice(0, p)) + (mid ? '<mark>' + fmt(mid) + '</mark>' : '') + fmt(str.slice(str.length - s));
        };
        return { b: wrap(b), a: wrap(a) };
    }

    let toastTimer = null;
    function toast(msg, isError) {
        const el = $id('ld-toast');
        el.textContent = msg;
        el.classList.toggle('is-error', !!isError);
        el.classList.add('is-on');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => el.classList.remove('is-on'), 4000);
    }

    function setBusy(busy) {
        ['searchBtnPID', 'searchBtn'].forEach(id => { $id(id).disabled = busy; });
        document.documentElement.style.cursor = busy ? 'wait' : '';
        if (busy) { if (typeof showLoading === 'function') showLoading(); }
        else if (typeof hideLoading === 'function') hideLoading();
    }

    function showMessage(title, desc, retry) {
        $id('ld-table').style.display = 'none';
        const box = $id('ld-empty');
        box.style.display = '';
        box.innerHTML = '<p class="ld-empty-title">' + esc(title) + '</p>'
            + (desc ? '<p class="ld-empty-desc">' + esc(desc) + '</p>' : '')
            + (retry ? '<button type="button" class="ld-btn" id="ld-retry">다시 시도</button>' : '');
        if (retry) $id('ld-retry').addEventListener('click', retry);
    }

    /* ---------- PID 버전 목록 ---------- */

    function searchPIDList() {
        const pid = $id('pidInput').value.trim();
        if (!pid) {
            toast('PID를 입력하세요.', true);
            $id('pidInput').focus();
            return;
        }

        const $base = $('#baseVersion').empty();
        const $comp = $('#compareVersion').empty();
        const placeholder = () => $('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true });
        $base.append(placeholder());
        $comp.append(placeholder());

        setBusy(true);
        $.ajax({
            type: 'get',
            url: '/diff/findPIDList',
            data: { pid: pid.toUpperCase() },
            success: function (rr) {
                let count = 0;
                try {
                    (rr || []).forEach(function (item) {
                        let value, text;
                        if (typeof item === 'string') {
                            value = item;
                            text = item;
                        } else if (item && typeof item === 'object') {
                            let version = item.VERSION || item.version || '';
                            const remarks = item.REMARKS || item.remarks || '';
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
                        } else {
                            return;
                        }
                        $base.append($('<option>', { value: value, text: text }));
                        $comp.append($('<option>', { value: value, text: text }));
                        count++;
                    });
                } catch (e) {
                    console.error('목록 처리 중 오류:', e);
                }
                setBusy(false);
                if (count === 0) {
                    toast('"' + pid.toUpperCase() + '" 에 해당하는 버전이 없습니다. PID를 확인하세요.', true);
                } else {
                    toast('버전 ' + count + '개를 불러왔습니다. 과거/최신 버전을 선택하세요.');
                    $id('baseVersion').focus();
                }
            },
            error: function () {
                setBusy(false);
                toast('버전 목록을 가져오지 못했습니다. 잠시 후 다시 조회하세요.', true);
            }
        });
    }
    window.searchPIDList = searchPIDList;

    /* ---------- 비교 실행 ---------- */

    function compare() {
        const pid = $id('pidInput').value.trim();
        const base = $id('baseVersion');
        const comp = $id('compareVersion');

        if (!pid) { toast('PID를 입력하고 조회하세요.', true); $id('pidInput').focus(); return; }
        if (!base.value) { toast('기준 버전(과거)을 선택하세요.', true); base.focus(); return; }
        if (!comp.value) { toast('비교 버전(최신)을 선택하세요.', true); comp.focus(); return; }
        if (base.value === comp.value) { toast('서로 다른 두 버전을 선택하세요.', true); comp.focus(); return; }

        const req = {
            pid: pid.toUpperCase(),
            pidOid: base.value,    // 과거(기준)
            pidOidb: comp.value,   // 최신(비교)
            baseText: base.options[base.selectedIndex].text,
            compText: comp.options[comp.selectedIndex].text
        };
        state.lastRequest = req;

        setBusy(true);
        $.ajax({
            type: 'post',
            url: '/diff/findPIDLineDiff',
            data: { pid: req.pid, pidOid: req.pidOid, pidOidb: req.pidOidb },
            success: function (rr) {
                setBusy(false);
                load(rr || {}, req);
            },
            error: function () {
                setBusy(false);
                showMessage('비교 데이터를 가져오지 못했습니다.', '네트워크 또는 서버 상태를 확인한 뒤 다시 시도하세요.', compare);
                toast('데이터를 가져오는 중 오류가 발생했습니다.', true);
            }
        });
    }

    function load(rr, req) {
        state.columns = rr.columns || [];
        state.rows = rr.rows || [];
        state.summary = rr.summary || {};
        state.selected = -1;
        closeDetail();

        // 열 통계: 값 존재 여부 / 차이가 있는 행 수
        const stats = {};
        state.columns.forEach(col => { stats[col] = { hasValue: false, diff: 0 }; });
        state.rows.forEach(row => {
            const cells = row.cells || {};
            let text = (row.addr || '') + '\n';
            state.columns.forEach(col => {
                const c = cells[col];
                if (!c) return;
                const st = stats[col];
                if (c.a || c.b) { st.hasValue = true; text += (c.a || '') + '\n' + (c.b || '') + '\n'; }
                if (row.status === 'MODIFIED') { if (c.c) st.diff++; }
                else if (row.status !== 'EQUAL' && (c.a || c.b)) st.diff++;
            });
            row._text = text.toLowerCase();
        });
        state.colStats = stats;

        $id('ld-context').textContent = req.pid + '  ·  ' + req.baseText + '  →  ' + req.compText;
        $id('ld-tools').classList.remove('is-off');

        renderSummary();
        renderTable();

        const s = state.summary;
        const changed = (s.added || 0) + (s.modified || 0) + (s.deleted || 0);
        toast(changed ? '변경된 행 ' + changed + '건을 찾았습니다.' : '두 버전의 내용이 동일합니다.');
    }

    /* ---------- 요약(필터 칩) ---------- */

    function renderSummary() {
        const s = state.summary;
        const counts = { ADDED: s.added, MODIFIED: s.modified, DELETED: s.deleted, EQUAL: s.equal };
        let html = '';
        Object.keys(STATUS).forEach(key => {
            const conf = STATUS[key];
            html += '<button type="button" class="ld-chip ld-chip-' + conf.cls + '" data-status="' + key + '"'
                + ' aria-pressed="' + state.show[key] + '" title="' + conf.label + ' 행 표시/숨김">'
                + '<span class="ld-chip-mark" aria-hidden="true">' + conf.mark + '</span>'
                + conf.label + ' <b>' + (counts[key] || 0) + '</b></button>';
        });
        $id('diff-summary').innerHTML = html;

        const extra = [];
        if (s.changedCells) extra.push('변경 셀 ' + s.changedCells);
        if (s.moved) extra.push('순서 변경 ' + s.moved + '행');
        if (s.shiftOnly) extra.push('열 이동만 ' + s.shiftOnly + '행');
        if (s.fuzzy) extra.push('유사 매칭 ' + s.fuzzy + '행');
        $id('ld-extra').textContent = extra.join('  ·  ');
    }

    /* ---------- 표 ---------- */

    function groupOf(col) {
        if (/^(SPEC|CON)\d+$/.test(col)) return 'cond';
        if (/^(KEY|VAL)\d+$/.test(col)) return 'out';
        return 'etc';
    }

    // ADDR 은 고정 열로 따로 그리므로 제외
    function visibleColumns() {
        return state.columns.filter(col => {
            if (col === 'ADDR') return false;
            const st = state.colStats[col] || {};
            if (state.hideEmpty && !st.hasValue) return false;
            if (state.changedColsOnly && !st.diff) return false;
            return true;
        });
    }

    function cellInner(c, row) {
        if (!c) return '';
        if (row.status === 'DELETED') return fmt(c.b);
        if (!c.c) return fmt(c.a);

        const from = c.f ? '<span class="ld-from" title="과거 버전에서는 ' + esc(c.f) + ' 열에 있던 값">← ' + esc(c.f) + '</span>' : '';
        switch (c.t) {
            case 'add':
                return '<span class="ld-sign" aria-hidden="true">+</span>' + fmt(c.a);
            case 'del':
                return '<span class="ld-sign" aria-hidden="true">−</span><span class="ld-old">' + fmt(c.b) + '</span>' + from;
            case 'mov':
                return fmt(c.a) + from;
            default: {
                const d = inlineDiff(c.b, c.a);
                return '<div class="ld-new">' + d.a + '</div><div class="ld-old">' + d.b + from + '</div>';
            }
        }
    }

    function cellTd(col, c, row, extraCls) {
        let cls = extraCls || '';
        let title = '';
        if (c && c.c && row.status === 'MODIFIED') {
            cls += ' ld-c ld-c-' + (c.t || 'chg');
            title = ' title="' + col + ' · ' + (CELL_TYPE[c.t] || '변경') + '"';
        }
        return '<td class="' + cls.trim() + '"' + title + '>' + cellInner(c, row) + '</td>';
    }

    function renderTable() {
        const table = $id('ld-table');
        const cols = visibleColumns();

        if (!state.rows.length) {
            showMessage('비교 결과가 없습니다.', '선택한 두 버전에 로직 데이터가 없습니다.');
            updateCounts();
            return;
        }

        // 헤더
        let head = '<th class="ld-sticky ld-s1" scope="col">상태</th>'
            + '<th class="ld-sticky ld-s2" scope="col" title="행 번호 (과거 → 최신)">NO</th>'
            + '<th class="ld-sticky ld-s3" scope="col">ADDR' + diffBadge('ADDR') + '</th>';
        let prevGroup = '';
        cols.forEach(col => {
            const g = groupOf(col);
            const st = state.colStats[col] || {};
            const cls = 'ld-g-' + g + (g !== prevGroup ? ' ld-g-start' : '') + (st.diff ? ' ld-th-diff' : '');
            prevGroup = g;
            head += '<th scope="col" class="' + cls + '"' + (st.diff ? ' title="' + col + ' · 차이 ' + st.diff + '행"' : '') + '>'
                + esc(col) + diffBadge(col) + '</th>';
        });
        $id('table-header').innerHTML = head;

        // 본문
        const parts = [];
        state.rows.forEach((row, i) => {
            const conf = STATUS[row.status] || STATUS.EQUAL;
            const cells = row.cells || {};

            let label = conf.label;
            if (row.shiftOnly) label = '열이동';
            let tags = '';
            if (row.moved) tags += '<span class="ld-tag" title="과거 버전과 행 순서가 달라졌습니다">순서</span>';
            if (row.fuzzy) tags += '<span class="ld-tag" title="ADDR 이 달라 내용 유사도로 매칭한 행입니다">유사</span>';

            let no;
            if (row.noBefore && row.noAfter && row.noBefore !== row.noAfter) {
                no = '<span class="ld-no-old">' + esc(row.noBefore) + '</span><span aria-hidden="true">→</span>' + esc(row.noAfter);
            } else {
                no = esc(row.noAfter || row.noBefore || '');
            }

            let html = '<tr data-i="' + i + '" class="ld-r-' + conf.cls + '">'
                + '<td class="ld-sticky ld-s1"><span class="ld-badge ld-badge-' + conf.cls + '">'
                + '<span aria-hidden="true">' + conf.mark + '</span>' + label + '</span>' + tags + '</td>'
                + '<td class="ld-sticky ld-s2">' + no + '</td>'
                + cellTd('ADDR', cells.ADDR || { a: row.addr, b: row.addr }, row, 'ld-sticky ld-s3');

            let pg = '';
            cols.forEach(col => {
                const g = groupOf(col);
                html += cellTd(col, cells[col], row, g !== pg ? 'ld-g-start' : '');
                pg = g;
            });
            parts.push(html + '</tr>');
        });
        $id('bom-tbody').innerHTML = parts.join('');

        $id('ld-empty').style.display = 'none';
        table.style.display = '';
        applyFilter();

        if (state.selected >= 0) {
            const tr = rowEl(state.selected);
            if (tr) tr.classList.add('is-sel');
        }
    }

    function diffBadge(col) {
        const st = state.colStats[col];
        return st && st.diff ? '<span class="ld-th-n" aria-label="차이 ' + st.diff + '행">' + st.diff + '</span>' : '';
    }

    function rowEl(i) {
        return $id('bom-tbody').querySelector('tr[data-i="' + i + '"]');
    }

    /* ---------- 필터 / 검색 ---------- */

    function applyFilter() {
        const q = state.query;
        const diffList = [];
        let shown = 0;
        const trs = $id('bom-tbody').rows;
        for (let k = 0; k < trs.length; k++) {
            const tr = trs[k];
            const i = Number(tr.dataset.i);
            const row = state.rows[i];
            if (!row) continue;
            const ok = state.show[row.status] !== false && (!q || row._text.indexOf(q) >= 0);
            tr.style.display = ok ? '' : 'none';
            if (ok) {
                shown++;
                if (row.status !== 'EQUAL') diffList.push(i);
            }
        }
        state.diffList = diffList;
        state.shown = shown;
        updateCounts();

        const none = $id('ld-none');
        none.style.display = (state.rows.length && shown === 0) ? '' : 'none';
    }

    function updateCounts() {
        const total = state.rows.length;
        $id('ld-count').textContent = total ? '표시 ' + (state.shown || 0) + ' / 전체 ' + total + '행' : '';

        const n = state.diffList.length;
        const pos = state.diffList.indexOf(state.selected);
        $id('ld-nav-pos').textContent = (pos >= 0 ? pos + 1 : '–') + ' / ' + n;
        $id('ld-prev').disabled = n === 0;
        $id('ld-next').disabled = n === 0;
    }

    /* ---------- 선택 / 변경 이동 ---------- */

    function selectRow(i, opts) {
        opts = opts || {};
        const tbody = $id('bom-tbody');
        if (!opts.keep) tbody.querySelectorAll('tr.is-sel').forEach(r => r.classList.remove('is-sel'));
        const tr = rowEl(i);
        if (!tr) return;
        tr.classList.add('is-sel');
        state.selected = i;
        if (opts.scroll) scrollToRow(tr);
        renderDetail();
        updateCounts();
    }

    function scrollToRow(tr) {
        const wrap = $id('ld-wrap');
        const headH = $id('table-header').offsetHeight;
        const top = tr.offsetTop - headH;
        if (top < wrap.scrollTop || top + tr.offsetHeight > wrap.scrollTop + wrap.clientHeight - headH) {
            wrap.scrollTop = Math.max(0, top - Math.round(wrap.clientHeight / 3));
        }
        // 첫 변경 셀이 고정 열 뒤나 화면 밖에 있으면 가로로도 이동
        const cell = tr.querySelector('td.ld-c:not(.ld-sticky)');
        if (cell) {
            const fixed = tr.querySelector('td.ld-s3');
            const fixedW = fixed ? fixed.offsetLeft - wrap.scrollLeft + fixed.offsetWidth : 0;
            const left = cell.offsetLeft - wrap.scrollLeft;
            if (left < fixedW || left + cell.offsetWidth > wrap.clientWidth) {
                wrap.scrollLeft = Math.max(0, cell.offsetLeft - fixedW - 24);
            }
        }
    }

    function gotoDiff(dir) {
        const list = state.diffList;
        if (!list.length) { toast('이동할 변경 행이 없습니다.'); return; }
        let target;
        if (dir > 0) {
            target = list.find(i => i > state.selected);
            if (target === undefined) { target = list[0]; toast('마지막 변경입니다. 처음으로 돌아갑니다.'); }
        } else {
            for (let k = list.length - 1; k >= 0; k--) { if (list[k] < state.selected) { target = list[k]; break; } }
            if (target === undefined) { target = list[list.length - 1]; toast('첫 변경입니다. 마지막으로 이동합니다.'); }
        }
        selectRow(target, { scroll: true });
    }

    /* ---------- 상세 비교 패널 ---------- */

    function closeDetail() {
        $id('ld-detail').classList.remove('is-open');
    }

    function renderDetail() {
        const row = state.rows[state.selected];
        const panel = $id('ld-detail');
        if (!row) { closeDetail(); return; }
        const conf = STATUS[row.status] || STATUS.EQUAL;
        const cells = row.cells || {};

        let meta = '<span class="ld-badge ld-badge-' + conf.cls + '"><span aria-hidden="true">' + conf.mark + '</span>'
            + (row.shiftOnly ? '열이동' : conf.label) + '</span>'
            + '<strong class="ld-d-addr">' + (esc(row.addr) || '(ADDR 없음)') + '</strong>';
        const notes = [];
        if (row.noBefore || row.noAfter) notes.push('NO ' + (esc(row.noBefore) || '–') + ' → ' + (esc(row.noAfter) || '–'));
        if (row.status === 'MODIFIED') notes.push('변경 ' + (row.nChg || 0) + '셀' + (row.nMov ? ', 열 이동 ' + row.nMov + '셀' : ''));
        if (row.moved) notes.push('행 순서 변경');
        if (row.fuzzy) notes.push('ADDR 이 달라 유사도로 매칭');
        $id('ld-d-meta').innerHTML = meta;
        $id('ld-d-notes').textContent = notes.join('  ·  ');

        let body = '';
        state.columns.forEach(col => {
            const c = cells[col];
            if (!c || (!c.a && !c.b)) return;
            const diff = row.status === 'MODIFIED' ? c.c : row.status !== 'EQUAL';
            if (state.detailChangedOnly && !diff) return;

            let type = '';
            let b = fmt(c.b), a = fmt(c.a);
            if (row.status === 'MODIFIED' && c.c) {
                type = c.t || 'chg';
                if (type === 'chg') { const d = inlineDiff(c.b, c.a); b = d.b; a = d.a; }
            } else if (row.status === 'ADDED') {
                type = 'add';
            } else if (row.status === 'DELETED') {
                type = 'del';
            }
            const from = c.f ? '<span class="ld-from">' + esc(c.f) + '</span>' : '';
            body += '<tr class="' + (type ? 'ld-d-' + type : '') + '">'
                + '<th scope="row">' + esc(col) + (type ? '<span class="ld-d-type">' + CELL_TYPE[type] + '</span>' : '') + '</th>'
                + '<td>' + (b || '<span class="ld-nil">–</span>') + from + '</td>'
                + '<td>' + (a || '<span class="ld-nil">–</span>') + '</td></tr>';
        });
        $id('ld-d-body').innerHTML = body
            || '<tr><td colspan="3" class="ld-nil ld-d-none">표시할 항목이 없습니다.</td></tr>';
        panel.classList.add('is-open');
    }

    /* ---------- CSV 내보내기 ---------- */

    function exportCsv() {
        if (!state.rows.length) { toast('내보낼 비교 결과가 없습니다.', true); return; }
        const cols = ['ADDR'].concat(visibleColumns());
        const q = (v) => '"' + String(v === null || v === undefined ? '' : v).replace(/"/g, '""').replace(/\r?\n/g, ' ') + '"';
        const lines = [['상태', 'NO(과거)', 'NO(최신)', '비고'].concat(cols).map(q).join(',')];

        const trs = $id('bom-tbody').rows;
        for (let k = 0; k < trs.length; k++) {
            if (trs[k].style.display === 'none') continue;
            const row = state.rows[Number(trs[k].dataset.i)];
            if (!row) continue;
            const cells = row.cells || {};
            const note = [row.shiftOnly ? '열이동' : '', row.moved ? '순서변경' : '', row.fuzzy ? '유사매칭' : ''].filter(Boolean).join('/');
            const line = [(STATUS[row.status] || STATUS.EQUAL).label, row.noBefore, row.noAfter, note];
            cols.forEach(col => {
                const c = cells[col] || {};
                if (row.status === 'DELETED') line.push(c.b);
                else if (row.status === 'MODIFIED' && c.c && c.t !== 'mov') line.push((c.a || '(없음)') + '  [이전: ' + (c.b || '(없음)') + ']');
                else line.push(c.a);
            });
            lines.push(line.map(q).join(','));
        }

        const req = state.lastRequest || {};
        const blob = new Blob(['﻿' + lines.join('\r\n')], { type: 'text/csv;charset=utf-8' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = '로직비교_' + (req.pid || 'PID') + '.csv';
        document.body.appendChild(a);
        a.click();
        a.remove();
        setTimeout(() => URL.revokeObjectURL(a.href), 1000);
        toast('화면에 표시된 ' + (lines.length - 1) + '행을 CSV로 저장했습니다.');
    }

    /* ---------- 이벤트 ---------- */

    $id('pidInput').addEventListener('keydown', (e) => { if (e.key === 'Enter') searchPIDList(); });
    $id('searchBtnPID').addEventListener('click', searchPIDList);
    $id('searchBtn').addEventListener('click', compare);

    $id('ld-swap').addEventListener('click', () => {
        const base = $id('baseVersion'), comp = $id('compareVersion');
        const tmp = base.value;
        base.value = comp.value;
        comp.value = tmp;
        if (!base.value) base.selectedIndex = 0;
        if (!comp.value) comp.selectedIndex = 0;
    });

    $id('diff-summary').addEventListener('click', (e) => {
        const chip = e.target.closest('button[data-status]');
        if (!chip) return;
        const key = chip.dataset.status;
        state.show[key] = !state.show[key];
        chip.setAttribute('aria-pressed', String(state.show[key]));
        applyFilter();
    });

    $id('ld-opt-empty').addEventListener('change', (e) => { state.hideEmpty = e.target.checked; if (state.rows.length) renderTable(); });
    $id('ld-opt-changed').addEventListener('change', (e) => { state.changedColsOnly = e.target.checked; if (state.rows.length) renderTable(); });
    $id('ld-d-changed').addEventListener('change', (e) => { state.detailChangedOnly = e.target.checked; renderDetail(); });

    let searchTimer = null;
    $id('ld-search').addEventListener('input', (e) => {
        clearTimeout(searchTimer);
        const v = e.target.value;
        searchTimer = setTimeout(() => { state.query = v.trim().toLowerCase(); applyFilter(); }, 200);
    });

    $id('ld-prev').addEventListener('click', () => gotoDiff(-1));
    $id('ld-next').addEventListener('click', () => gotoDiff(1));
    $id('ld-export').addEventListener('click', exportCsv);
    $id('ld-d-close').addEventListener('click', closeDetail);
    $id('ld-none-reset').addEventListener('click', () => {
        Object.keys(state.show).forEach(k => { state.show[k] = true; });
        state.query = '';
        $id('ld-search').value = '';
        renderSummary();
        applyFilter();
    });

    // 행 클릭: 선택 + 상세 비교 (Ctrl+클릭은 강조만 추가/해제)
    const tbody = $id('bom-tbody');
    tbody.addEventListener('mousedown', (e) => { if (e.ctrlKey) e.preventDefault(); });
    tbody.addEventListener('click', (e) => {
        const tr = e.target.closest('tr[data-i]');
        if (!tr) return;
        if (window.getSelection && String(window.getSelection())) return; // 텍스트 드래그 중이면 무시
        const i = Number(tr.dataset.i);
        if (e.ctrlKey) {
            if (tr.classList.contains('is-sel') && i !== state.selected) tr.classList.remove('is-sel');
            else selectRow(i, { keep: true });
        } else {
            selectRow(i);
        }
    });

    // 단축키: N / P = 다음 / 이전 변경, Esc = 상세 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') { closeDetail(); return; }
        const tag = (e.target.tagName || '').toLowerCase();
        if (tag === 'input' || tag === 'select' || tag === 'textarea' || e.ctrlKey || e.altKey || e.metaKey) return;
        if (e.key === 'n' || e.key === 'N') { e.preventDefault(); gotoDiff(1); }
        else if (e.key === 'p' || e.key === 'P') { e.preventDefault(); gotoDiff(-1); }
    });
})();
