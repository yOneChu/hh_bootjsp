//searchPartQtyPid.js


document.addEventListener("DOMContentLoaded", () => {
    //console.log("DOM 준비됨!");

    $("#dashboard").removeClass("menu-open");
    //init();
});

// 수량-PID 조회 화면 전용 스크립트
// Enter 키 입력 시 조회 실행
const keyCodeCheck = function() {
    if (window && window.event && window.event.keyCode === 13) {


        filterData();
    }
}

// 검색 버튼 클릭 시 호출되는 함수
// dateFilter, searchBlockNo, searchPID 값을 읽어 /mlb/searchPartQtyPid 에 POST 호출
function filterData() {
    try {
        var dateFilterEl = document.querySelector('#dateFilter');
        var searchBlockNoEl = document.querySelector('#searchBlockNo');
        var searchPIDEl = document.querySelector('#searchPID');


        var year = (dateFilterEl && dateFilterEl.value) ? dateFilterEl.value : '';
        // Convert 'YYYY-MM-DD' to 'YYYYMMDD' for backend consumption
        if (year && year.indexOf('-') !== -1) {
            year = year.replace(/-/g, '');
        }
        var blockNo = (searchBlockNoEl && searchBlockNoEl.value) ? searchBlockNoEl.value.trim() : '';
        var qtyPid = (searchPIDEl && searchPIDEl.value) ? searchPIDEl.value.trim() : '';

        console.log('dateFilterEl == ', dateFilterEl);

        // 필수 입력값 체크
        if (!year) {
            alert('날짜를 입력하세요.');
            if (dateFilterEl) dateFilterEl.focus();
            return;
        }
        if (!blockNo) {
            alert('BlockNo.를 입력하세요.');
            if (searchBlockNoEl) searchBlockNoEl.focus();
            return;
        }
        if (!qtyPid) {
            alert('수량 PID를 입력하세요.');
            if (searchPIDEl) searchPIDEl.focus();
            return;
        }

        // 기존 테이블 초기화
        try { $('#infoTable').DataTable().destroy(); } catch (e) {}
        $("#contentTable").empty();

        showLoading(); // 로딩바 표시

        // 서버 컨트롤러가 year, blockNo, qtyPid 파라미터를 받음
        $.ajax({
            url: '/mlb/searchPartQtyPid',
            method: 'POST',
            data: {
                year: year,
                blockNo: blockNo,
                qtyPid: qtyPid
            },
            success: function (data) {
                console.log('searchPartQtyPid success', data);


                let flag = "";
                let msg = "";

                let rows = "";
                if (data && data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        var item = data[i] || {};

                        var oid = item.oid || '';

                        if(oid === 'TT') {
                            msg = "데이터가 너무 많습니다. 날짜의 범위를 줄이세요."
                            flag = "TT";
                        }


                        var parentPartNo = item.parentPartNo || '';
                        var parentPartName = item.parentPartName || '';
                        var parentBlockNo = item.parentBlockNo || '';
                        var creDate = item.creDate || '';
                        var partNo = item.partNo || '';
                        var partName = item.partName || '';
                        var blockNo2 = item.blockNo || '';
                        var spec = item.spec || '';
                        var qty = item.qty || '';
                        var cmt = item.cmt || '';

                        rows += `
                            <tr>
                                <td>${parentPartNo}</td>
                                <td>${parentPartName}</td>
                                <td>${parentBlockNo}</td>
                                
                                <td>${partNo}</td>
                                <td style="text-align: left">${partName}</td>
                                <td>${blockNo2}</td>
                                <td style="text-align: left">${spec}</td>
                                <td>${qty}</td>
                                <td style="text-align: left">${cmt}</td>
                            </tr>
                        `;
                    }

                    $("#contentTable").append(rows);

                    $("#infoTable").DataTable({
                        responsive: true,
                        lengthChange: true,
                        pageLength: 25,
                        autoWidth: false,
                        processing: true,
                        destroy: true,
                        buttons: ["excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');
                } else {
                    alert('검색결과가 없습니다.');
                }

                hideLoading();
                if (flag === "TT") {
                    alert(msg);
                }

            },
            error: function (xhr, status, err) {
                console.error('searchPartQtyPid error', status, err);
                alert('조회 요청 중 오류가 발생했습니다. 다시 시도해주세요.');
                hideLoading();
            }
        });

    } catch (e) {
        console.error('filterData exception', e);
        hideLoading();
    }
}



function excelData() {
    try {
        var dateFilterEl = document.querySelector('#dateFilter');
        var searchBlockNoEl = document.querySelector('#searchBlockNo');
        var searchPIDEl = document.querySelector('#searchPID');


        var year = (dateFilterEl && dateFilterEl.value) ? dateFilterEl.value : '';
        // Convert 'YYYY-MM-DD' to 'YYYYMMDD' for backend consumption
        if (year && year.indexOf('-') !== -1) {
            year = year.replace(/-/g, '');
        }
        var blockNo = (searchBlockNoEl && searchBlockNoEl.value) ? searchBlockNoEl.value.trim() : '';
        var qtyPid = (searchPIDEl && searchPIDEl.value) ? searchPIDEl.value.trim() : '';

        console.log('dateFilterEl == ', dateFilterEl);

        // 필수 입력값 체크
        if (!year) {
            alert('날짜를 입력하세요.');
            if (dateFilterEl) dateFilterEl.focus();
            return;
        }
        if (!blockNo) {
            alert('BlockNo.를 입력하세요.');
            if (searchBlockNoEl) searchBlockNoEl.focus();
            return;
        }
        if (!qtyPid) {
            alert('수량 PID를 입력하세요.');
            if (searchPIDEl) searchPIDEl.focus();
            return;
        }

        // 기존 테이블 초기화
        try { $('#infoTable').DataTable().destroy(); } catch (e) {}
        $("#contentTable").empty();

        showLoading(); // 로딩바 표시
        $.ajax({
            url: '/excel/searchQtyPid',   // 요청 보낼 URL
            type: 'POST',              // 메서드 (GET/POST 등)
            data : {
                year: year,
                blockNo: blockNo,
                qtyPid: qtyPid
            },
            xhrFields: {
                responseType: 'blob'    // 파일 다운로드용 응답 처리
            },
            success: function (data, status, xhr) {

                console.log(data);

                // 응답 헤더에서 파일명 추출
                const disposition = xhr.getResponseHeader('Content-Disposition');
                let filename = 'excel.xlsx';
                if (disposition && disposition.indexOf('filename=') !== -1) {
                    filename = disposition.split('filename=')[1].replace(/"/g, '');
                }

                // Blob으로 파일 생성 및 다운로드
                const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
                const link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = filename;
                link.click();

                hideLoading(); // 성공 시 로딩바 제거
                //alert('다운로드 완료하였습니다.');
            },
            error: function () {
                alert('오류 발생하였습니다. 김영환M 문의하세요.😅');
            }
        });





    } catch (e) {
        console.error('filterData exception', e);
        hideLoading();
    }
}