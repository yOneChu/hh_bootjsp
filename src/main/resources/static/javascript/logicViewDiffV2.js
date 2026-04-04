

let beforeData;


function searchPIDList() {
    let pid = document.getElementById('pidInput').value;
    //console.log("pid --------", pid);
    if (!pid) {
        alert('PID를 입력하세요.');
        return;
    }

    //console.log('--------searchPIDList');

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

/*

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
*/





document.getElementById('searchBtn').addEventListener('click', () => {
    const pid = document.getElementById('pidInput').value;
    const v1 = document.getElementById('baseVersion').value;
    const v2 = document.getElementById('compareVersion').value;


    console.log(v1, v2);

    showLoading();
    $.ajax({
        type : "post",
        url : "/subae/findPIDLineDiff",
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

            console.log('data -- ' , data);
            console.log('beforeMap-- ', beforeMap);

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
       // const url = "subae/logicViewDiffPopup";
        //const name = "popupWindow";
        //const options = "width=600,height=400,top=200,left=300,resizable=yes,scrollbars=yes";

        //let postData = encodeURIComponent(JSON.stringify(beforeData));
        //console.log('postData -- ', postData);

        /*window.open(
            "/subae/logicViewDiffPopup?data=" + postData,
            "popupWindow",
            "width=600,height=400,top=200,left=300, resizable=yes,scrollbars=yes"
        );*/

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


            html += `<td class="${cellClassKey}">${key1}</td>`;
            html += `<td class="${cellClass}">${val1}</td>`;
            html += `<td class="${cellClassKey}">${key2}</td>`;
            html += `<td class="${cellClass}">${val2}</td>`;
            html += `<td class="${cellClassKey}">${key3}</td>`;
            html += `<td class="${cellClass}">${val3}</td>`;

            html += `<td class="${cellClassKey}">${key4}</td>`;
            html += `<td class="${cellClass}">${val4}</td>`;
            html += `<td class="${cellClassKey}">${key5}</td>`;
            html += `<td class="${cellClass}">${val5}</td>`;
            html += `<td class="${cellClassKey}">${key6}</td>`;
            html += `<td class="${cellClass}">${val6}</td>`;
            html += `<td class="${cellClassKey}">${key7}</td>`;
            html += `<td class="${cellClass}">${val7}</td>`;
            html += `<td class="${cellClassKey}">${key8}</td>`;
            html += `<td class="${cellClass}">${val8}</td>`;
            html += `<td class="${cellClassKey}">${key9}</td>`;
            html += `<td class="${cellClass}">${val9}</td>`;
            html += `<td class="${cellClassKey}">${key10}</td>`;
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
            tbody.appendChild(tr);
        }




    }


});