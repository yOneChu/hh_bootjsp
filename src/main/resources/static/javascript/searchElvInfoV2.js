// Handsontable 컬럼 정의 (헤더 순서와 일치)
const HOT_COLUMNS = [
    { data: 'productno',  title: '현장번호' },
    { data: 'productoid',  title: '현장번호OID' },
    { data: 'status',  title: '상태' },
    { data: 'el_ABRAND',  title: '브랜드' },
    { data: 'el_AUSE',    title: '용도' },
    { data: 'el_ASPSCD',  title: '생산거점' },
    { data: 'el_ATYP',    title: '기종' },
    { data: 'el_ASPD',    title: '속도' },
    { data: 'el_AOPEN',   title: '열림방식' },
    { data: 'el_ERPW',    title: 'ROPE/BELT본수' },
    { data: 'el_ECWRL',   title: 'CWT RAIL' },
    { data: 'el_AMAN',    title: '인승' },
    { data: 'el_ECWBG',   title: 'el_ECWBG' },
    { data: 'el_ETM',     title: 'EL_ETM(권상기)' },
    { data: 'el_ECJJ',    title: 'el_ECJJ' },
    { data: 'el_COB',     title: '관통' },
    { data: 'el_ACAPA',   title: '용량' }
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

/* ─────────────────────────────────────────
   다크모드 행 hover 처리
   Handsontable이 JS로 인라인 스타일을 적용하므로
   CSS !important 로는 덮을 수 없어 JS에서 강제 처리
───────────────────────────────────────── */
function setupDarkModeHover() {
    var container = document.getElementById('infoTable');
    if (!container) return;

    var activeRowIdx = -1;   // 현재 hover 중인 tbody 내 행 인덱스

    var HOVER_BG    = 'rgba(99,102,241,0.22)';
    var HOVER_COLOR = '#e2e8f0';

    function getCells(rowIdx) {
        // 모든 pane(.ht_master, .ht_clone_left 등) 에서 같은 인덱스의 tr 수집
        var cells = [];
        container.querySelectorAll('.htCore tbody').forEach(function(tbody) {
            var tr = tbody.children[rowIdx];
            if (tr) {
                cells.push.apply(cells, Array.from(tr.querySelectorAll('td, th')));
            }
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
        var tr = e.target.closest && e.target.closest('.htCore tbody tr');
        if (!tr) return;
        var rowIdx = Array.from(tr.parentElement.children).indexOf(tr);
        if (rowIdx === activeRowIdx) return;
        if (activeRowIdx >= 0) clearHover(activeRowIdx);
        activeRowIdx = rowIdx;
        applyHover(rowIdx);
    });

    container.addEventListener('mouseout', function(e) {
        if (!document.body.classList.contains('dark-mode')) return;
        if (activeRowIdx < 0) return;
        var to = e.relatedTarget;
        // 마우스가 같은 tr 내부로 이동한 경우는 무시
        var tr = e.target.closest && e.target.closest('.htCore tbody tr');
        if (tr && to && tr.contains(to)) return;
        // 컨테이너 밖으로 나갔을 때만 초기화
        if (!to || !to.closest || !to.closest('#infoTable')) {
            clearHover(activeRowIdx);
            activeRowIdx = -1;
        }
    });

    // 컨테이너 영역을 완전히 벗어날 때 확실히 초기화
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
    // 우선 모두 표시
    const allIdx = [];
    for (let i = 0; i < colCount; i++) allIdx.push(i);
    hiddenPlugin.showColumns(allIdx);
    // 저장된 숨김 컬럼만 다시 숨김
    if (hidden.length > 0) {
        hiddenPlugin.hideColumns(hidden.filter(i => i >= 0 && i < colCount));
    }
    hot.render();
    if (typeof window.syncColumnButtons === 'function') {
        window.syncColumnButtons();
    }
}

//ready
$(document).ready(function() {

    $("#dashboard").removeClass("menu-open");

    // Handsontable 초기 생성 (빈 데이터)
    initHandsontable([]);
    setTimeout(applySavedColumnVisibility, 100);
    setupDarkModeHover();

    //엔터키 감지
    $(document).keyup(function(event) {
        if(event.which === 13) {
            search();
            return false; // 추가 이벤트 방지위해 false 리턴
        }
    })


    let EL_ATYPObj = $('#EL_ATYP');
    codeSetInit('EL_ATYP', EL_ATYPObj);
    setInput(EL_ATYPObj);

    let EL_ABRANDObj = $('#EL_ABRAND');
    codeSetInit('EL_ABRAND', EL_ABRANDObj);
    setInput(EL_ABRANDObj);

    let EL_AOPENObj = $('#EL_AOPEN');
    codeSetInit('EL_AOPEN', EL_AOPENObj);
    setInput(EL_AOPENObj);

    let EL_ECWRLObj = $('#EL_ECWRL');
    codeSetInit('EL_ECWRL', EL_ECWRLObj);
    setInput(EL_ECWRLObj);

    //용도
    let EL_AUSEOb = $('#EL_AUSE');
    codeSetInit('EL_AUSE', EL_AUSEOb);
    setInput(EL_AUSEOb);

    //속도
    let EL_ASPDObj = $('#EL_ASPD');
    codeSetInit('EL_ASPD', EL_ASPDObj);
    setInput(EL_ASPDObj);

    let EL_COBObj = $('#EL_COB');
    codeSetInit('EL_COB', EL_COBObj);
    setInput(EL_COBObj);


    let EL_ACAPAObj = $('#EL_ACAPA');
    codeSetInit('EL_ACAPA', EL_ACAPAObj);
    setInput(EL_ACAPAObj);
    
    $('#EL_ASPSCD').select2({
        tags: true,                 // 🔑 직접 입력 허용
        placeholder: "직접 입력 또는 선택",
        allowClear: true
    });

});


function setInput(thisObj) {
    thisObj.select2({
        tags: true,                 // 🔑 직접 입력 허용
        placeholder: "직접 입력 또는 선택",
        allowClear: true
    });
}



function runHeavyWork() {
    showLoading();

    // 브라우저에게 렌더링 기회 주기
    requestAnimationFrame(() => {
        // 여기서 실제 오래 걸리는 작업 / fetch / ajax 호출
        doWorkAsync().finally(hideLoading);
    });
}

//검색
function search()
{
    let year = $('#year').val()
    let EL_ABRAND = $("#EL_ABRAND").val();
    let EL_ASPSCD = $("#EL_ASPSCD").val();
    let EL_ATYP = $("#EL_ATYP").val();
    let EL_ASPD = $("#EL_ASPD").val();
    let EL_ERPW = $("#EL_ERPW").val();
    let EL_AOPEN = $("#EL_AOPEN").val();
    let EL_ECWRL = $("#EL_ECWRL").val();
    let EL_AUSE = $("#EL_AUSE").val();
    let EL_COB = $("#EL_COB").val();
    let EL_ACAPA = $("#EL_ACAPA").val();

    showLoading();

    requestAnimationFrame(() => {

        $.ajax({
            type: "post",
            crossDomain: true,
            url: "/subae/searchElvInfo",
            data: {
                year: year,
                EL_ABRAND: EL_ABRAND,
                EL_ASPSCD: EL_ASPSCD,
                EL_ATYP: EL_ATYP,
                EL_ASPD: EL_ASPD,
                EL_ERPW: EL_ERPW,
                EL_AOPEN: EL_AOPEN,
                EL_ECWRL: EL_ECWRL,
                EL_AUSE: EL_AUSE,
                EL_COB: EL_COB,
                EL_ACAPA: EL_ACAPA
            },
            success: function (data) {
                console.log("data - ", data);

                if (data != null && data.length > 0) {
                    initHandsontable(data);
                    setTimeout(applySavedColumnVisibility, 50);
                    renderStats(data);
                } else {
                    initHandsontable([]);
                    setTimeout(applySavedColumnVisibility, 50);
                    hideStats();
                    alert("검색결과가 없습니다.");
                }
            },

            error: function (xhr, status, err) {
                console.error("AJAX error:", status, err, xhr?.responseText);
                alert("조회 중 오류가 발생했습니다.");
            },

            // ✅ 성공/실패 상관없이 항상 마지막에 로딩 제거 (가장 안전)
            complete: function () {
                hideLoading();
            }
        });

    });
}


// PID 분석 조건으로 추가 조회 - 조회결과(infoTable) 1열(현장번호) 값을 출력
function readRow() {
    let arrList = new Array();
    const hot = window.hotInstance;

    console.log(hot);

    if (!hot) {
        alert('테이블이 초기화되지 않았습니다.');
        return;
    }

    const data = hot.getData();
    console.log(data, data);
    if (!Array.isArray(data) || data.length === 0) {
        alert('조회된 데이터가 없습니다. 먼저 [조회]를 실행해 주세요.');
        return;
    }

    // 1열(현장번호) 값 추출 - 빈 값 제외
    const firstColValues = data
        .map(row => (row && row.length > 0) ? row[0] : null)
        .filter(v => v !== null && v !== undefined && String(v).trim() !== '');

    console.log('===== infoTable 1열(현장번호) 출력 (총 ' + firstColValues.length + '건) =====');
    firstColValues.forEach((v, i) => {
        console.log('[' + (i + 1) + '] ' + v);
        arrList.push(v);
    });
    console.log('전체 배열 :', firstColValues);
    console.log('CSV       :', firstColValues.join(', '));

    alert('총 ' + firstColValues.length + '건의 현장번호가 콘솔에 출력되었습니다.');

    return arrList;
}

// #blockNo 입력값을 조회결과(infoTable) 모든 행에 새 컬럼으로 추가
function addSearchData() {

    const hot = window.hotInstance;
    if (!hot) {
        alert('테이블이 초기화되지 않았습니다.');
        return;
    }

    const blockNoVal = ($('#blockNo').val() || '').trim();
    if (!blockNoVal) {
        alert('부품 BlockNo를 입력해 주세요.');
        $('#blockNo').focus();
        return;
    }

    console.log("blockNoVal -- ", blockNoVal);

    let arrList = new Array();
    arrList = readRow();

    //console.log("arrList --> ", arrList);


    const sourceData = hot.getSourceData();
    if (!Array.isArray(sourceData) || sourceData.length === 0) {
        alert('조회된 데이터가 없습니다. 먼저 [조회]를 실행해 주세요.');
        return;
    }

    // 새 컬럼 키 생성 (영문/숫자 외 문자는 언더스코어로 치환, 동일 입력 반복 시 _1, _2 ...)
    const safeBase = 'block_' + blockNoVal.replace(/[^a-zA-Z0-9_]/g, '_');
    const existingKeys = HOT_COLUMNS.map(c => c.data);
    let newKey = safeBase;
    let suffix = 1;
    while (existingKeys.includes(newKey)) {
        newKey = safeBase + '_' + suffix;
        suffix++;
    }

    console.log('-------------------');
    // 모든 행 객체에 동일한 값으로 새 프로퍼티 추가
    sourceData.forEach(row => {
        console.log(row);

        //여기에 넣자.

        if (row && typeof row === 'object') {
            row[newKey] = blockNoVal;
        }
    });

    // 컬럼 정의에 푸시 (헤더에는 사용자가 입력한 BlockNo 값을 보여줌)
    HOT_COLUMNS.push({ data: newKey, title: 'BlockNo: ' + blockNoVal });

    // Handsontable 재구성 - 새 컬럼/헤더 반영
    hot.updateSettings({
        columns: HOT_COLUMNS,
        colHeaders: HOT_COLUMNS.map(c => c.title),
        data: sourceData
    });

    // 저장된 숨김 컬럼 상태 재적용 + 토글 버튼 동기화
    if (typeof applySavedColumnVisibility === 'function') {
        setTimeout(applySavedColumnVisibility, 30);
    }

    alert('총 ' + sourceData.length + '행에 컬럼 [BlockNo: ' + blockNoVal + ']이(가) 추가되었습니다.');
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

    // 평균 속도 (el_ASPD)
    const speeds = data.map(r => parseFloat(r.el_ASPD)).filter(n => !isNaN(n) && n > 0);
    document.getElementById('stat-speed').textContent = speeds.length
        ? Math.round(speeds.reduce((a, b) => a + b, 0) / speeds.length)
        : '-';

    // 평균 용량 (el_ACAPA)
    const capas = data.map(r => parseFloat(r.el_ACAPA)).filter(n => !isNaN(n) && n > 0);
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

    // 브랜드 수 (미정 제외)
    const brandDist = countDist('el_ABRAND');
    const brandKinds = brandDist.filter(([k]) => k !== '(미정)').length;
    document.getElementById('stat-brand-cnt').textContent = String(brandKinds);

    // chip 렌더 헬퍼
    function renderDist(id, dist) {
        const el = document.getElementById(id);
        if (!el) return;
        el.innerHTML = dist.map(([k, cnt]) =>
            `<span class="stat-chip">${k}<span class="chip-cnt">${cnt}</span></span>`
        ).join('');
    }

    renderDist('stat-brand-dist', brandDist);
    renderDist('stat-type-dist',  countDist('el_ATYP'));
    renderDist('stat-site-dist',  countDist('el_ASPSCD'));

    panel.style.display = '';
}


function isStringAndNotEmptyOrWhitespace(value) {
    // 1. 문자열인지 확인
    if (typeof value === 'string') {
        // 2. 공백만 있는지 확인 (trim()으로 공백 제거 후 빈 문자열인지 체크)
        if (value.trim() === '') {
            return false; // 공백 문자열
        }
        return true; // 유효한 문자열
    }
    return false; // 문자열이 아님
}


// 로딩바 제거 함수
function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}


// 로딩바 표시 함수
function showLoading() {

    console.log('showloading 44 ------------');

    if (document.getElementById('loadingOverlay')) return;


    // 로딩바 HTML 생성
    const loadingHtml = `
        <div id="loadingOverlay" style="
              position: fixed;
  inset: 0;
  background: rgba(0,0,0,.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2147483647; /* 거의 최댓값 */
        ">
            <div style="
                background: white;
                padding: 30px;
                border-radius: 8px;
                text-align: center;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            ">
                <div style="
                    border: 4px solid #f3f3f3;
                    border-top: 4px solid #3498db;
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 15px;
                "></div>
                <p style="margin: 0; font-size: 16px; color: #333;">데이터 분석 중입니다...</p>
            </div>
        </div>
        <style>
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        </style>
    `;

    // 로딩바를 body에 추가
    //document.body.insertAdjacentHTML('beforeend', loadingHtml);
    //(document.body || document.documentElement).insertAdjacentHTML('beforeend', loadingHtml);

    console.log('document.body : ', document.body);
    console.log('document.documentElement : ', document.documentElement);

    const target = document.body || document.documentElement;
    target.insertAdjacentHTML('beforeend', loadingHtml);
}

