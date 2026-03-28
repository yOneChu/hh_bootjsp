

/*

document.getElementById('searchBtnPID').addEventListener('click', () => {

    let pid = document.getElementById('pidInput').value.trim();
    if (!pid) {
        alert('PID를 입력하세요.');
        return;
    }

    console.log('pid --------', pid);

    const $select = $('#baseVersion');
    $select.empty();
    $select.append($('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true }));

    const $select2 = $('#compareVersion');
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

});
*/

function searchPIDList() {
    let pid = document.getElementById('pidInput').value;
    console.log("pid --------", pid);
    if (!pid) {
        alert('PID를 입력하세요.');
        return;
    }

    console.log('--------searchPIDList');

    const $select = $('#baseVersion');
    $select.empty();
    $select.append($('<option>', { value: '', text: '버전을 선택하세요', disabled: true, selected: true }));

    const $select2 = $('#compareVersion');
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