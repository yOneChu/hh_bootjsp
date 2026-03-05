
let hotTop;

$(document).ready(async function() {
    // 초기화 시 필요한 작업이 있다면 여기에 작성
});

function searchPIDList() {
    let pid = document.getElementById('searchInput').value.trim();
    if (!pid) {
        alert('PID를 입력하세요.');
        return;
    }

    const $select = $('#pidList');
    $select.empty();
    $select.append($('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true }));

    const $select2 = $('#pidList2');
    $select2.empty();
    $select2.append($('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true }));

    showLoading();
    $.ajax({
        type : "get",
        url : "/pid/findPIDList",
        data : { pid : pid.toUpperCase() },
        beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },
        success : function(rr) {
            try {
                if (rr && rr.length > 0) {
                    rr.forEach(function(item) {
                        let value, text;
                        console.log(item)
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

                            console.log("remarks == ", remarks);

                            const parts = [];
                            if(version === '-1') version = 'TEST';
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
        error: function() {
            hideLoading();
            alert('목록을 가져오는 중 오류가 발생했습니다.');
        }
    });
}

function searchPID(target) {
    let pid = document.getElementById('searchInput').value.trim();
    const pidList = document.getElementById("pidList");

    if (!pidList.value) {
        alert("버전을 선택해주세요.");
        return;
    }

    showLoading();
    $.ajax({
        type : "post",
        url : "/subae/findPIDLineViewV2",
        data : {
            pid : pid.toUpperCase(),
            pidOid : pidList.value
        },
        beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },
        success : function(rr) {
            if(rr != null && rr.length > 0) {
                initHandsontable(rr, target);
                hideEmptyColumns(target);
                hideLoading();
            } else {
                hideLoading();
                alert("검색결과가 없습니다.");
            }
        },
        error: function () {
            hideLoading();
            alert('데이터를 가져오는 중 오류가 발생하였습니다.');
        }
    });
}

function searchDiff() {
    let target = 'top';
    let pid = document.getElementById('searchInput').value.trim();
    const pidList = document.getElementById("pidList");
    const pidList2 = document.getElementById("pidList2");

    if (!pidList.value) {
        alert("버전을 선택해주세요.");
        return;
    }

    showLoading();
    $.ajax({
        type : "post",
        url : "/subae/findPIDLineDiff",
        data : {
            pid : pid.toUpperCase(),
            pidOid : pidList.value,
            pidOidb: pidList2.value
        },
        beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },
        success : function(rr) {
            if(rr != null && rr.length > 0) {
                initHandsontable(rr, target);
                hideEmptyColumns(target);
                hideLoading();
            } else {
                hideLoading();
                alert("검색결과가 없습니다.");
            }
        },
        error: function () {
            hideLoading();
            alert('데이터를 가져오는 중 오류가 발생하였습니다.');
        }
    });
}


function initHandsontable(data, target) {
    const containerId = 'handsontable-container-' + target;
    const container = document.getElementById(containerId);
    
    let hotInstance = hotTop;
    
    if (hotInstance) {
        hotInstance.destroy();
    }

    const colHeaders = [
        'ADDR', 'SPEC1', 'CON1', 'SPEC2', 'CON2', 'SPEC3', 'CON3', 'SPEC4', 'CON4', 'SPEC5', 'CON5',
        'SPEC6', 'CON6', 'SPEC7', 'CON7', 'SPEC8', 'CON8', 'SPEC9', 'CON9', 'SPEC10', 'CON10', 'SPEC11', 'CON11',
        'SPEC12', 'CON12', 'SPEC13', 'CON13', 'SPEC14', 'CON14', 'SPEC15', 'CON15', 'SPEC16', 'CON16', 'SPEC17', 'CON17' ,'SPEC18', 'CON18', 'SPEC19', 'CON19', 'SPEC20', 'CON20',
        'KEY1', 'VAL1', 'KEY2', 'VAL2', 'KEY3', 'VAL3', 'KEY4', 'VAL4', 'KEY5', 'VAL5',
        'KEY6', 'VAL6', 'KEY7', 'VAL7', 'KEY8', 'VAL8', 'KEY9', 'VAL9', 'KEY10', 'VAL10', 'KEY11', 'VAL11',
        'KEY12', 'VAL12', 'KEY13', 'VAL13', 'KEY14', 'VAL14', 'KEY15', 'VAL15', 'KEY16', 'VAL16', 'KEY17', 'VAL17' ,'KEY18', 'VAL18', 'KEY19', 'VAL19', 'KEY20', 'VAL20',
        'GOTO', 'REMARKS', 'DIFF'
    ];

    hotInstance = new Handsontable(container, {
        data: data,
        rowHeaders: true,
        colHeaders: colHeaders,
        width: '100%',
        height: '100%',
        licenseKey: 'non-commercial-and-evaluation',
        stretchH: 'all',
        manualRowResize: true,
        manualColumnResize: true,
        hiddenColumns: {
            indicators: true
        },
        cells: function(row, col) {
            const cellProperties = {};
            const diffColIndex = this.instance.countCols() - 1; // 'DIFF' 컬럼은 항상 마지막에 위치함
            
            // 2차원 배열 데이터에서 'DIFF' 값 확인
            const rowData = this.instance.getSourceDataAtRow(row);
            if (rowData && rowData[diffColIndex] === 'DIFF') {
                cellProperties.renderer = function(instance, td, row, col, prop, value, cellProperties) {
                    Handsontable.renderers.TextRenderer.apply(this, arguments);
                    td.style.backgroundColor = '#FFD580'; // 연한 주황색 (Light Orange)
                };
            }
            
            return cellProperties;
        },
        afterSelection: function(row, col) {
            const cellName = Handsontable.helper.columnLabel(col) + (row + 1);
            document.getElementById('selectedCell-' + target).innerText = cellName;
        },
        afterLoadData: function() {
            updateStatus(target);
        }
    });

    hotTop = hotInstance;

    updateStatus(target);
}

function updateStatus(target) {
    const hotInstance = hotTop;
    if (hotInstance) {
        document.getElementById('rowCount-' + target).innerText = hotInstance.countRows();
        document.getElementById('colCount-' + target).innerText = hotInstance.countCols();
        document.getElementById('statusText-' + target).innerText = '조회 완료';
    }
}

function hideEmptyColumns(target) {
    const hotInstance = hotTop;
    if (!hotInstance) return;

    const data = hotInstance.getData();
    const colCount = hotInstance.countCols();
    const hiddenCols = [];

    for (let col = 0; col < colCount; col++) {
        let hasValue = false;
        for (let row = 0; row < data.length; row++) {
            const cellValue = data[row][col];
            if (cellValue !== null && cellValue !== undefined && cellValue.toString().trim() !== '') {
                hasValue = true;
                break;
            }
        }
        if (!hasValue) {
            hiddenCols.push(col);
        }
    }

    hotInstance.updateSettings({
        hiddenColumns: {
            columns: hiddenCols,
            indicators: true
        }
    });
}

function showLoading() {
    if (document.getElementById('loadingOverlay')) return;
    const loadingHtml = `
        <div id="loadingOverlay" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 9999;">
            <div style="background: white; padding: 30px; border-radius: 8px; text-align: center; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                <div style="border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin: 0 auto 15px;"></div>
                <p style="margin: 0; font-size: 16px; color: #333;">데이터 처리 중입니다...</p>
            </div>
        </div>
        <style>@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }</style>
    `;
    document.body.insertAdjacentHTML('beforeend', loadingHtml);
}

function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) loadingOverlay.remove();
}
