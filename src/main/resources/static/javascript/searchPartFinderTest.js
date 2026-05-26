// Handsontable 컬럼 정의
const HOT_COLUMNS = [
    { data: 'productNo',      title: '제품번호' },
    { data: 'productVersion', title: '제품버전' },
    { data: 'productStatus',  title: '제품상태' },
    { data: 'productModDate', title: '제품수정일' },
    { data: 'el_ZFDA',        title: '기계구조 최초설계일' },
    { data: 'brand',          title: '브랜드' },
    { data: 'gisong',         title: '기종' },
    { data: 'aspd',           title: '속도' },
    { data: 'aspscd',         title: '생산거점' },
    { data: 'el_ETHRU',       title: '관통' },
    { data: 'el_COB',         title: '전망타입' },
    { data: 'acapa',          title: '용량' },
    { data: 'ecbg',           title: 'CAR_BG' },
    { data: 'ecwbg',          title: 'CWT_BG' },
    { data: 'ecww',           title: 'CWT폭' },
    { data: 'el_ECWSF',           title: 'CWT;SAFETY' },
    { data: 'ucheck',           title: '수정여부' },
    { data: 'partNo',         title: '품번' },
    { data: 'partName',       title: '품명' },
    { data: 'spec',           title: 'SPEC' },
    { data: 'qty',            title: 'QTY' },
    { data: 'blockNo',        title: 'BlockNo.' },
    { data: 'blockopt',       title: '품목' },
    { data: 'glCode',         title: 'GL_CODE' },
    { data: 'version',        title: '버전' },
    { data: 'cmt',            title: 'cmt' },
    { data: 'el_BWALLT',      title: 'WALL 구조' }
];

window.hotInstance = null;

function getHiddenCols() {
    try {
        const val = localStorage.getItem('spf_hidden_cols');
        return val ? JSON.parse(val) : [];
    } catch (e) { return []; }
}

function initHandsontable(data) {
    const container = document.getElementById('infoTable');
    if (!container) return null;

    if (window.hotInstance) {
        try { window.hotInstance.destroy(); } catch (e) {}
        window.hotInstance = null;
    }

    const hidden = getHiddenCols();

    window.hotInstance = new Handsontable(container, {
        data: Array.isArray(data) ? data : [],
        columns: HOT_COLUMNS,
        colHeaders: HOT_COLUMNS.map(c => c.title),
        rowHeaders: true,
        width: '100%',
        height: 600,
        manualColumnResize: true,
        manualColumnMove: true,
        columnSorting: true,
        filters: true,
        dropdownMenu: true,
        contextMenu: true,
        hiddenColumns: {
            columns: hidden.slice(),
            indicators: true
        },
        stretchH: 'all',
        className: 'htCenter htMiddle',
        licenseKey: 'non-commercial-and-evaluation',
        afterRenderer(TD) {
            if (document.body.classList.contains('dark-mode')) {
                TD.style.removeProperty('background-color');
                TD.style.removeProperty('color');
            }
        }
    });

    return window.hotInstance;
}

function setupDarkModeHover() {
    const container = document.getElementById('infoTable');
    if (!container) return;

    let activeRowIdx = -1;
    const HOVER_BG    = 'rgba(253, 224, 71, 0.22)';
    const HOVER_COLOR = '#4a3d3d';

    function getCells(rowIdx) {
        const cells = [];
        container.querySelectorAll('.htCore tbody').forEach(function(tbody) {
            const tr = tbody.children[rowIdx];
            if (tr) cells.push.apply(cells, Array.from(tr.querySelectorAll('td, th')));
        });
        return cells;
    }

    function applyHover(rowIdx) {
        getCells(rowIdx).forEach(function(cell) {
            cell.style.setProperty('background-color', HOVER_BG, 'important');
            cell.style.setProperty('color', HOVER_COLOR, 'important');
        });
    }

    function clearHover(rowIdx) {
        getCells(rowIdx).forEach(function(cell) {
            cell.style.removeProperty('background-color');
            cell.style.removeProperty('color');
        });
    }

    container.addEventListener('mouseover', function(e) {
        if (!document.body.classList.contains('dark-mode')) return;
        const tr = e.target.closest && e.target.closest('.htCore tbody tr');
        if (!tr) return;
        const rowIdx = Array.from(tr.parentElement.children).indexOf(tr);
        if (rowIdx === activeRowIdx) return;
        if (activeRowIdx >= 0) clearHover(activeRowIdx);
        activeRowIdx = rowIdx;
        applyHover(rowIdx);
    });

    container.addEventListener('mouseout', function(e) {
        if (!document.body.classList.contains('dark-mode')) return;
        if (activeRowIdx < 0) return;
        const tr = e.target.closest && e.target.closest('.htCore tbody tr');
        const to = e.relatedTarget;
        if (tr && to && tr.contains(to)) return;
        if (!to || !to.closest || !to.closest('#infoTable')) {
            clearHover(activeRowIdx);
            activeRowIdx = -1;
        }
    });

    container.addEventListener('mouseleave', function() {
        if (activeRowIdx >= 0) {
            clearHover(activeRowIdx);
            activeRowIdx = -1;
        }
    });
}

function applySavedColumnVisibility() {
    const hot = window.hotInstance;
    if (!hot) return;
    const hiddenPlugin = hot.getPlugin('hiddenColumns');
    const hidden = getHiddenCols();
    const colCount = hot.countCols();
    const allIdx = [];
    for (let i = 0; i < colCount; i++) allIdx.push(i);
    hiddenPlugin.showColumns(allIdx);
    if (hidden.length > 0) {
        hiddenPlugin.hideColumns(hidden.filter(i => i >= 0 && i < colCount));
    }
    hot.render();
    if (typeof window.syncColumnButtons === 'function') {
        window.syncColumnButtons();
    }
}

$(document).ready(function() {
    $("#dashboard").removeClass("menu-open");

    initHandsontable([]);
    setTimeout(applySavedColumnVisibility, 100);
    setupDarkModeHover();

    $(document).keyup(function(event) {
        if (event.which === 13) {
            searchPID();
            return false;
        }
    });

    codeSetting('EL_ATYP');
    codeSettingAsBrand('EL_ABRAND');

    const EL_COBObj = $('#EL_COB');
    codeSetInit('EL_COB', EL_COBObj);
    setInput(EL_COBObj);

    const EL_BWALLTObj = $('#EL_BWALLT');
    codeSetInit('EL_BWALLT', EL_BWALLTObj);
    setInput(EL_BWALLTObj);

    $('#EL_ASPSCD').select2({ tags: true, placeholder: "직접 입력 또는 선택", allowClear: true });
    $('#EL_ATYP').select2({ tags: true, placeholder: "직접 입력 또는 선택", allowClear: true });
    $('#EL_BRAND').select2({ tags: true, placeholder: "직접 입력 또는 선택", allowClear: true });
});

function setInput(thisObj) {
    thisObj.select2({ tags: true, placeholder: "직접 입력 또는 선택", allowClear: true });
}

function codeSetting(typeName) {
    $.ajax({
        type: "post",
        crossDomain: true,
        url: "/subae/findCodeList",
        data: { typeName: typeName },
        success: function(data) {
            if (data != null && data.length > 0) {
                let str = "";
                for (let i = 0; i < data.length; i++) {
                    str += `<option value="${data[i].code}">${data[i].code} -> (${data[i].name})</option>`;
                }
                $("#EL_ATYP").append(str);
            }
        },
        error: function(xhr, status, err) {
            console.error("AJAX error:", status, err, xhr?.responseText);
        }
    });
}

function codeSettingAsBrand(typeName) {
    $.ajax({
        type: "post",
        crossDomain: true,
        url: "/subae/findCodeList",
        data: { typeName: typeName },
        success: function(data) {
            if (data != null && data.length > 0) {
                let str = "";
                for (let i = 0; i < data.length; i++) {
                    str += `<option value="${data[i].code}">${data[i].code} -> (${data[i].name})</option>`;
                }
                $("#EL_BRAND").append(str);
            }
        },
        error: function(xhr, status, err) {
            console.error("AJAX error:", status, err, xhr?.responseText);
        }
    });
}

function searchPID() {
    let year       = $("#year").val();
    let partNo     = ($("#partNo").val() || "").trim();
    let blockNo    = ($("#blockNo").val() || "").trim();
    let cmt        = $("#cmt").val();
    let status     = $("#status").val();
    let spec       = $("#spec").val();
    let brand      = $("#EL_BRAND").val();
    let EL_ASPSCD  = $("#EL_ASPSCD").val();
    let EL_ATYP    = $("#EL_ATYP").val();
    let EL_ETHRU   = $("#EL_ETHRU").val();
    let EL_COB     = $("#EL_COB").val();
    let EL_BWALLT  = $("#EL_BWALLT").val();
    let EL_ZFDA    = $("#EL_ZFDA").val();
    let EL_ZFDA_TYPE = $("#EL_ZFDA_TYPE").val();

    if (!partNo && !blockNo) {
        alert("PartNo 또는 BlockNo 중 하나는 필수 입력 사항입니다.");
        return;
    }

    showLoading();

    requestAnimationFrame(() => {
        $.ajax({
            type: "post",
            crossDomain: true,
            url: "/subae/searchMissPartofProduct",
            data: { partNo, year, blockNo, cmt, status, spec, brand,
                    EL_ASPSCD, EL_ATYP, EL_ETHRU, EL_COB,
                    EL_ZFDA, EL_ZFDA_TYPE, EL_BWALLT },
            success: function(data) {
                if (data != null && data.length > 0) {
                    initHandsontable(data);
                    setTimeout(applySavedColumnVisibility, 50);
                    renderStats(data);
                } else {
                    initHandsontable([]);
                    hideStats();
                    alert("검색결과가 없습니다.");
                }
            },
            error: function(xhr, status, err) {
                console.error("AJAX error:", status, err, xhr?.responseText);
                alert("조회 중 오류가 발생했습니다.");
            },
            complete: function() {
                hideLoading();
            }
        });
    });
}

/* ─────────────────────────────────────────
   통계 요약 패널
───────────────────────────────────────── */
function hideStats() {
    const panel = document.getElementById('statsPanel');
    if (panel) panel.style.display = 'none';
}

function renderStats(data) {
    const panel = document.getElementById('statsPanel');
    if (!panel || !data || !data.length) { hideStats(); return; }

    // 총 건수
    document.getElementById('stat-total').textContent = data.length.toLocaleString('ko-KR');

    // 고유 제품 수 (productNo 기준)
    const uniqueProducts = new Set(data.map(r => r.productNo).filter(Boolean));
    document.getElementById('stat-product-cnt').textContent = String(uniqueProducts.size);

    // 평균 속도 (aspd)
    const speeds = data.map(r => parseFloat(r.aspd)).filter(n => !isNaN(n) && n > 0);
    document.getElementById('stat-speed').textContent = speeds.length
        ? Math.round(speeds.reduce((a, b) => a + b, 0) / speeds.length)
        : '-';

    // 평균 용량 (acapa)
    const capas = data.map(r => parseFloat(r.acapa)).filter(n => !isNaN(n) && n > 0);
    document.getElementById('stat-capa').textContent = capas.length
        ? Math.round(capas.reduce((a, b) => a + b, 0) / capas.length).toLocaleString('ko-KR')
        : '-';

    // 분포 집계 헬퍼 (내림차순 정렬)
    function countDist(field) {
        const map = {};
        data.forEach(r => {
            const v = (r[field] || '').trim() || '(미정)';
            map[v] = (map[v] || 0) + 1;
        });
        return Object.entries(map).sort((a, b) => b[1] - a[1]);
    }

    // chip 렌더 헬퍼
    function renderDist(id, dist) {
        const el = document.getElementById(id);
        if (!el) return;
        el.innerHTML = dist.map(([k, cnt]) =>
            `<span class="stat-chip">${k}<span class="chip-cnt">${cnt}</span></span>`
        ).join('');
    }

    renderDist('stat-brand-dist', countDist('brand'));
    renderDist('stat-type-dist',  countDist('gisong'));
    renderDist('stat-site-dist',  countDist('aspscd'));

    panel.style.display = '';
}

function hideLoading() {
    const el = document.getElementById('loadingOverlay');
    if (el) el.remove();
}

function showLoading() {
    if (document.getElementById('loadingOverlay')) return;
    const target = document.body || document.documentElement;
    target.insertAdjacentHTML('beforeend', `
        <div id="loadingOverlay" style="
            position:fixed;inset:0;background:rgba(0,0,0,.5);
            display:flex;justify-content:center;align-items:center;
            z-index:2147483647;">
            <div style="background:white;padding:30px;border-radius:8px;
                        text-align:center;box-shadow:0 4px 6px rgba(0,0,0,.1);">
                <div style="border:4px solid #f3f3f3;border-top:4px solid #4f46e5;
                            border-radius:50%;width:40px;height:40px;
                            animation:spin 1s linear infinite;margin:0 auto 15px;"></div>
                <p style="margin:0;font-size:16px;color:#333;">데이터 분석 중입니다...</p>
            </div>
        </div>
        <style>@keyframes spin{0%{transform:rotate(0deg)}100%{transform:rotate(360deg)}}</style>
    `);
}
