

let beforeData;

document.getElementById('pidInput').addEventListener('keydown', (e) => {

    //console.log(e.keyCode);
    if (e.key === 'Enter') {
        searchPIDList();
    }
});

function searchPIDList() {
    let pid = document.getElementById('pidInput').value;
    //console.log("pid --------", pid);
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
                        //console.log(item)
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

                            //console.log("remarks == ", remarks);

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



document.getElementById('searchBtn').addEventListener('click', () => {
    const pid = document.getElementById('pidInput').value;
    const v1 = document.getElementById('baseVersion').value;
    const v2 = document.getElementById('compareVersion').value;

    //console.log(v1, v2);

    showLoading();
    $.ajax({
        type : "post",
        url : "/diff/findPIDLineDiff",
        data : {
            pid : pid.toUpperCase().trim(),
            pidOid : v1,
            pidOidb: v2
        },
        beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },
        success : function(rr) {

            console.log(rr);

            let data = rr.result;
            let beforeMap = rr.beforeDetailMap;

            //console.log('data -- ' , data);
            //console.log('beforeMap-- ', beforeMap);

            beforeData = beforeMap;

            executeCompareData(data);

            hideLoading();


            //openBeforeDataPopup();
        },
        error: function () {
            hideLoading();
            alert('데이터를 가져오는 중 오류가 발생하였습니다.');
        }

    });



    /*if(!pid || !v1 || !v2) {
        alert("PID와 두 버전을 모두 선택해주세요.");
        return;
    }

    // TODO: 실제 환경에서는 fetch API나 axios를 사용해 Spring Boot 백엔드에 요청
    // 예시: axios.get(`/api/bom/compare?pid=${pid}&base=${v1}&target=${v2}`).then(res => executeCompare(res.data.past, res.data.latest));

    // 샘플 테스트용 더미 데이터
    const pastDummy = [
        { id: `${pid}-001`, name: 'CAR HEADER ARM', spec01: 'Steel', con01: 'A-Type' },
        { id: `${pid}-002`, name: '시브커브', spec01: 'Iron', con01: 'B-Type' },
        { id: `${pid}-003`, name: '컴펜체인', spec01: 'Alloy', con01: 'C-Type' },
    ];

    const latestDummy = [
        { id: `${pid}-001`, name: 'CAR HEADER ARM', spec01: 'Steel', con01: 'A-Type' },
        { id: `${pid}-002`, name: '시브커브', spec01: 'Aluminium', con01: 'B-Type' },
        { id: `${pid}-004`, name: 'pit screen', spec01: 'Mesh', con01: 'D-Type' },
    ];*/

    // 렌더링 함수 호출
    //executeCompare(pastDummy, latestDummy);


    document.getElementById('searchBeforeBtn').addEventListener('click', () => {
        openBeforeDataPopup();
    });


        //이전버전의 데이터 조회 팝업
    function openBeforeDataPopup() {

        const form = document.createElement("form");
        form.method = "post";
        form.action = "/subae/logicViewDiffPopup";
        form.target = "popupWindow";

        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "data";
        input.value = JSON.stringify(beforeData);

        form.appendChild(input);
        document.body.appendChild(form);

        window.open("/subae/logicViewDiffPopup", "popupWindow", "width=1200,height=400");
        form.submit();

        document.body.removeChild(form);
        //window.open(url, name, options);
    }


    function executeCompareData(items) {
        const tbody = document.getElementById('bom-tbody') || document.querySelector('table tbody');
        if (!tbody) {
            console.error('tbody 요소를 찾을 수 없습니다.');
            return;
        }
        tbody.innerHTML = ''; // 기존 테이블 초기화

        // 화면 렌더링
        const statusConfig = {
            'DIFF': { label: '추가 or 수정', rowClass: 'bg-pink-100 hover:bg-pink-200 transition-colors', labelClass: 'bg-green-100 text-green-700 font-bold px-2 py-1 rounded' },
            'DELETED': { label: '삭제', rowClass: 'bg-red-50 opacity-60 hover:opacity-100 transition-opacity line-through text-red-500', labelClass: 'bg-red-100 text-red-700 font-bold px-2 py-1 rounded line-through-none' },
           /* 'MODIFIED': { label: '수정', rowClass: 'hover:bg-slate-50 transition-colors', labelClass: 'bg-yellow-100 text-yellow-700 font-bold px-2 py-1 rounded' },*/
            'EQUAL': { label: '동일', rowClass: 'hover:bg-slate-300 transition-colors text-slate-500', labelClass: 'bg-slate-100 text-slate-500 px-2 py-1 rounded' }
        };


        //EL_PB182F01
        //기본
        let cellClass = "px-4 py-2 border-b border-r border-gray-300";
        let cellKeyClass = "px-4 py-2 bg-pink-50 border-b border-r border-gray-300";
        let cellClassKey = "px-4 py-2 bg-pink-10 border-b border-r border-gray-300";

        for(let i=0; i < items.length; i++) {
            const tr = document.createElement('tr');

            let item = items[i]

            //console.log(item);


            let html = '';
            let status = item[83];
            const config = statusConfig[status];
            tr.className = config.rowClass;

            if('EQUAL' === status) {
                html = `<td class="px-4 py-2 text-center border-b border-r border-gray-300 sticky left-0 z-10"><span class="text-xs ${config.labelClass}">${config.label}</span></td>`;
            } else if('DIFF' === status) {
                html = `<td class="px-4 py-2 text-center border-b border-r border-gray-300 sticky left-0 z-10"><span class="text-xs ${config.labelClass}">${config.label}</span></td>`;
            }

            let no = item[84];
            let goto = item[81];
            let remarks = item[82];
            let addr = item[0];

            let spec1 = item[1];
            let con1 = item[2];
            let spec2 = item[3];
            let con2 = item[4];
            let spec3 = item[5];
            let con3 = item[6];
            let spec4 = item[7];
            let con4 = item[8];
            let spec5 = item[9];
            let con5 = item[10];
            let spec6 = item[11];
            let con6 = item[12];
            let spec7 = item[13];
            let con7 = item[14];
            let spec8 = item[15];
            let con8 = item[16];
            let spec9 = item[17];
            let con9 = item[18];
            let spec10 = item[19];
            let con10 = item[20];
            let spec11 = item[21];
            let con11 = item[22];
            let spec12 = item[23];
            let con12 = item[24];
            let spec13 = item[25];
            let con13 = item[26];
            let spec14 = item[27];
            let con14 = item[28];
            let spec15 = item[29];
            let con15 = item[30];
            let spec16 = item[31];
            let con16 = item[32];
            let spec17 = item[33];
            let con17 = item[34];
            let spec18 = item[35];
            let con18 = item[36];
            let spec19 = item[37];
            let con19 = item[38];
            let spec20 = item[39];
            let con20 = item[40];

            let key1 = item[41];
            let val1 = item[42];
            let key2 = item[43];
            let val2 = item[44];
            let key3 = item[45];
            let val3 = item[46];
            let key4 = item[47];
            let val4 = item[48];
            let key5 = item[49];
            let val5 = item[50];
            let key6 = item[51];
            let val6 = item[52];
            let key7 = item[53];
            let val7 = item[54];
            let key8 = item[55];
            let val8 = item[56];
            let key9 = item[57];
            let val9 = item[58];
            let key10 = item[59];
            let val10 = item[60];

            let key11 = item[61];
            let val11 = item[62];
            let key12 = item[63];
            let val12 = item[64];
            let key13 = item[65];
            let val13 = item[66];
            let key14 = item[67];
            let val14 = item[68];
            let key15 = item[69];
            let val15 = item[70];
            let key16 = item[71];
            let val16 = item[72];
            let key17 = item[73];
            let val17 = item[74];
            let key18 = item[75];
            let val18 = item[76];
            let key19 = item[77];
            let val19 = item[78];
            let key20 = item[79];
            let val20 = item[80];

            // 툴팁용 텍스트 생성
            let tooltipText = "";
            for (let k = 1; k <= 20; k++) {
                let kVal = eval(`key${k}`);
                let vVal = eval(`val${k}`);
                if (kVal && kVal !== '-' && kVal !== '') {
                    //tooltipText += `KEY${k}: ${kVal}, VAL${k}: ${vVal}\n`;
                    tooltipText += `KEY${k}-VAL${k} :::  ${kVal} > ${vVal}\n`;
                }
            }

            if (goto !== '-' && goto !== '') {
                tooltipText += `GOTO ::: ${goto}\n`;
            }

            if (remarks !== '-' && remarks !== '') {
                tooltipText += `remarks ::: ${remarks}\n`;
            }

            // tr.title = tooltipText.trim(); // 기존 브라우저 툴팁 제거

            // 커스텀 팝오버 이벤트 추가
            if (tooltipText.trim()) {
                const popover = document.getElementById('custom-popover');
                tr.addEventListener('mouseenter', (e) => {
                    popover.innerText = tooltipText.trim();
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

            let stopFlag = item[81];

            //console.log(spec1, con1);
            //onsole.log(stopFlag);

            //html += `<td class="${cellClass}">${no}</td>`;
            html += `<td class="${cellClass} sticky left-[64px] z-10 ${config.rowClass.split(' ')[0]}">${no}</td>`;
            html += `<td class="${cellClass}">${addr}</td>`;
            html += `<td class="${cellClass}">${spec1}</td>`;
            html += `<td class="${cellClass}">${con1}</td>`;
            html += `<td class="${cellClass}">${spec2}</td>`;
            html += `<td class="${cellClass}">${con2}</td>`;
            html += `<td class="${cellClass}">${spec3}</td>`;
            html += `<td class="${cellClass}">${con3}</td>`;
            html += `<td class="${cellClass}">${spec4}</td>`;
            html += `<td class="${cellClass}">${con4}</td>`;
            html += `<td class="${cellClass}">${spec5}</td>`;
            html += `<td class="${cellClass}">${con5}</td>`;
            html += `<td class="${cellClass}">${spec6}</td>`;
            html += `<td class="${cellClass}">${con6}</td>`;
            html += `<td class="${cellClass}">${spec7}</td>`;
            html += `<td class="${cellClass}">${con7}</td>`;
            html += `<td class="${cellClass}">${spec8}</td>`;
            html += `<td class="${cellClass}">${con8}</td>`;
            html += `<td class="${cellClass}">${spec9}</td>`;
            html += `<td class="${cellClass}">${con9}</td>`;
            html += `<td class="${cellClass}">${spec10}</td>`;
            html += `<td class="${cellClass}">${con10}</td>`;

            html += `<td class="${cellClass}">${spec11}</td>`;
            html += `<td class="${cellClass}">${con11}</td>`;
            html += `<td class="${cellClass}">${spec12}</td>`;
            html += `<td class="${cellClass}">${con12}</td>`;
            html += `<td class="${cellClass}">${spec13}</td>`;
            html += `<td class="${cellClass}">${con13}</td>`;
            html += `<td class="${cellClass}">${spec14}</td>`;
            html += `<td class="${cellClass}">${con14}</td>`;
            html += `<td class="${cellClass}">${spec15}</td>`;
            html += `<td class="${cellClass}">${con15}</td>`;
            html += `<td class="${cellClass}">${spec16}</td>`;
            html += `<td class="${cellClass}">${con16}</td>`;
            html += `<td class="${cellClass}">${spec17}</td>`;
            html += `<td class="${cellClass}">${con17}</td>`;
            html += `<td class="${cellClass}">${spec18}</td>`;
            html += `<td class="${cellClass}">${con18}</td>`;
            html += `<td class="${cellClass}">${spec19}</td>`;
            html += `<td class="${cellClass}">${con19}</td>`;
            html += `<td class="${cellClass}">${spec20}</td>`;
            html += `<td class="${cellClass}">${con20}</td>`;


            html += `<td class="${cellKeyClass}">${key1}</td>`;
            html += `<td class="${cellClass}">${val1}</td>`;
            html += `<td class="${cellKeyClass}">${key2}</td>`;
            html += `<td class="${cellClass}">${val2}</td>`;
            html += `<td class="${cellKeyClass}">${key3}</td>`;
            html += `<td class="${cellClass}">${val3}</td>`;

            html += `<td class="${cellKeyClass}">${key4}</td>`;
            html += `<td class="${cellClass}">${val4}</td>`;
            html += `<td class="${cellKeyClass}">${key5}</td>`;
            html += `<td class="${cellClass}">${val5}</td>`;
            html += `<td class="${cellKeyClass}">${key6}</td>`;
            html += `<td class="${cellClass}">${val6}</td>`;
            html += `<td class="${cellKeyClass}">${key7}</td>`;
            html += `<td class="${cellClass}">${val7}</td>`;
            html += `<td class="${cellKeyClass}">${key8}</td>`;
            html += `<td class="${cellClass}">${val8}</td>`;
            html += `<td class="${cellKeyClass}">${key9}</td>`;
            html += `<td class="${cellClass}">${val9}</td>`;
            html += `<td class="${cellKeyClass}">${key10}</td>`;
            html += `<td class="${cellClass}">${val10}</td>`;
            html += `<td class="${cellClass}">${key11}</td>`;
            html += `<td class="${cellClass}">${val11}</td>`;
            html += `<td class="${cellClass}">${key12}</td>`;
            html += `<td class="${cellClass}">${val12}</td>`;
            html += `<td class="${cellClass}">${key13}</td>`;
            html += `<td class="${cellClass}">${val13}</td>`;
            html += `<td class="${cellClass}">${key14}</td>`;
            html += `<td class="${cellClass}">${val14}</td>`;
            html += `<td class="${cellClass}">${key15}</td>`;
            html += `<td class="${cellClass}">${val15}</td>`;

            html += `<td class="${cellClass}">${goto}</td>`;
            html += `<td class="${cellClass}">${remarks}</td>`;
            html += `<td class="${cellClass}">${no}</td>`;

            tr.innerHTML = html;
            
            // 행 클릭 이벤트 추가 (노란색 하이라이트)
            // Ctrl + 클릭 시 텍스트가 선택되는 것을 방지
            tr.addEventListener('mousedown', function(e) {
                if (e.ctrlKey) {
                    e.preventDefault();
                }
            });
            tr.addEventListener('click', function(e) {
                const highlight = (row) => {
                    row.classList.add('selected-row');
                    row.style.backgroundColor = '#fef08a';
                };
                const unhighlight = (row) => {
                    row.classList.remove('selected-row');
                    row.style.backgroundColor = '';
                };

                if (e.ctrlKey) {
                    // Ctrl 눌린 상태: 클릭한 행 토글
                    if (this.classList.contains('selected-row')) {
                        unhighlight(this);
                    } else {
                        highlight(this);
                    }
                } else {
                    // Ctrl 안 눌린 상태: 모든 행 선택 해제 후 클릭한 행만 선택
                    const tbodyEl = this.parentNode;
                    if (tbodyEl) {
                        tbodyEl.querySelectorAll('tr.selected-row').forEach(row => {
                            unhighlight(row);
                        });
                    }
                    highlight(this);
                }
            });
            
            tbody.appendChild(tr);
        }

    // 빈 열 숨기기 기능 구현
    document.getElementById('hide-empty-cols').addEventListener('click', function() {
        const table = document.querySelector('table');
        const rows = table.querySelectorAll('tbody tr');
        const headers = table.querySelectorAll('thead th');
        
        // SPEC1~20, CON1~20, KEY1~20, VAL1~20 열의 인덱스 확인
        const targetIndices = [];
        headers.forEach((th, index) => {
            const text = th.textContent.trim().toUpperCase();
            const isSpecOrCon = text.startsWith('SPEC') || text.startsWith('CON');
            const isKeyOrVal = text.startsWith('KEY') || text.startsWith('VAL');
            
            if (isSpecOrCon || isKeyOrVal) {
                const numStr = text.replace('SPEC', '').replace('CON', '').replace('KEY', '').replace('VAL', '');
                const num = parseInt(numStr);
                if (num >= 1 && num <= 20) {
                    targetIndices.push(index);
                }
            }
        });

        // 각 인덱스에 대해 모든 행의 값이 비어있는지 체크
        targetIndices.forEach(colIndex => {
            let hasValue = false;
            rows.forEach(row => {
                const cell = row.cells[colIndex];
                if (cell) {
                    const text = cell.textContent.trim();
                    if (text !== '' && text !== '-' && text !== 'undefined' && text !== 'null') {
                        hasValue = true;
                    }
                }
            });

            // 값이 하나도 없으면 해당 열 숨김
            if (!hasValue) {
                headers[colIndex].style.display = 'none';
                rows.forEach(row => {
                    if (row.cells[colIndex]) {
                        row.cells[colIndex].style.display = 'none';
                    }
                });
            }
        });
        
        this.textContent = '빈 열 숨김 완료';
        this.disabled = true;
        this.classList.replace('bg-green-600', 'bg-gray-400');
    });

    }


});