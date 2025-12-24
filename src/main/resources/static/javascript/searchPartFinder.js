let dtTable = $("#infoTable").DataTable({
    "responsive": true,
    "lengthChange": true,
    "pageLength": 50,     //페이지 당 글 개수 설정
    "autoWidth": false, // 가로자동
    "processing": true,
    "destroy": true, // 테이블 재생성
    //"scrollX": true, // 가로 스크롤
    //"buttons": ["csv", "excel", "pdf", "print"]
    "buttons": ["csv", "excel", "copy"]
}).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

// 저장된 숨김 컬럼 적용
function getHiddenCols() {
    try {
        const val = localStorage.getItem('spf_hidden_cols');
        return val ? JSON.parse(val) : [];
    } catch (e) { return []; }
}
function applySavedColumnVisibility() {
    if (!$.fn.DataTable.isDataTable('#infoTable')) return;
    const table = $('#infoTable').DataTable();
    const hidden = getHiddenCols();
    const colCount = table.columns().indexes().length;
    for (let i = 0; i < colCount; i++) {
        const shouldShow = hidden.indexOf(i) === -1;
        table.column(i).visible(shouldShow);
    }
    // 버튼 상태 동기화 (HTML 쪽에서 정의됨)
    if (typeof window.syncColumnButtons === 'function') {
        window.syncColumnButtons();
    }
}
// 초기 1회 적용 시도 (DataTable DOM 준비 후 약간의 지연)
setTimeout(applySavedColumnVisibility, 200);

//ready
$(document).ready(function() {

    $("#dashboard").removeClass("menu-open");

    //엔터키 감지
    $(document).keyup(function(event) {
        if(event.which === 13) {
            searchPID();
            return false; // 추가 이벤트 방지위해 false 리턴
        }
    })


    //findDosCode
    codeSetting();


    $('#EL_ASPSCD').select2({
        tags: true,                 // 🔑 직접 입력 허용
        placeholder: "직접 입력 또는 선택",
        allowClear: true
    });


    $('#EL_ATYP').select2({
        tags: true,                 // 🔑 직접 입력 허용
        placeholder: "직접 입력 또는 선택",
        allowClear: true
    });

});


function codeSetting() {


    $.ajax({
        type: "get",
        crossDomain: true,
        url: "/subae/findGisong",

        success: function (data) {
            //console.log("data - ", data);

            if (data != null && data.length > 0) {
                let str = "";

                for (let i = 0; i < data.length; i++) {

                    let name = data[i].name;
                    let code = data[i].code;

                    str +=
                        `
                        <option value="${code}">${code} -> (${name})</option>
                        `;
                }

                $("#EL_ATYP").append(str);
            }
        },

        error: function (xhr, status, err) {
            console.error("AJAX error:", status, err, xhr?.responseText);
            alert("조회 중 오류가 발생했습니다.");
        },

        // ✅ 성공/실패 상관없이 항상 마지막에 로딩 제거 (가장 안전)
        complete: function () {
            //hideLoading();
        }
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
function searchPID()
{
    let year = $("#year").val(); //
    let partNo = $("#partNo").val(); // L
    let blockNo = $("#blockNo").val();
    let cmt = $("#cmt").val();
    let status = $("#status").val();
    let spec = $("#spec").val();
    let brand = $("#brand").val();
    let EL_ASPSCD = $("#EL_ASPSCD").val();
    let EL_ATYP = $("#EL_ATYP").val();

    //console.log(year);
    //console.log(partNo);

    // 입력값 트림 처리
    partNo = partNo ? partNo.trim() : "";
    blockNo = blockNo ? blockNo.trim() : "";

    // partNo, blockNo 둘 다 비어있으면 중단, 둘 중 하나라도 있으면 진행
    if ((partNo === "" || partNo == null) && (blockNo === "" || blockNo == null)) {
        alert("PartNo 또는 BlockNo 중 하나는 필수 입력 사항입니다.");
        return;
    }


    $('#infoTable').DataTable().destroy();
    $("#contentTable").empty();

    //showLoading(); // 로딩바 표시
    showLoading();

    requestAnimationFrame(() => {

        $.ajax({
            type: "post",
            crossDomain: true,
            url: "/subae/searchMissPartofProduct",
            data: {
                partNo: partNo,
                year: year,
                blockNo: blockNo,
                cmt: cmt,
                status: status,
                spec: spec,
                brand: brand,
                EL_ASPSCD: EL_ASPSCD,
                EL_ATYP: EL_ATYP
            },

            success: function (data) {
                //console.log("data - ", data);

                // ✅ 기존 테이블 내용 비우기 (append 누적 방지)
                $("#contentTable").empty();

                if (data != null && data.length > 0) {
                    let str = "";

                    for (let i = 0; i < data.length; i++) {
                        str += "<tr>";
                        str += "<td>" + (data[i].productNo ?? "") + "</td>";
                        str += "<td>" + (data[i].productVersion ?? "") + "</td>";
                        str += "<td>" + (data[i].productStatus ?? "") + "</td>";
                        str += "<td>" + (data[i].productModDate ?? "") + "</td>";
                        str += "<td>" + (data[i].brand ?? "") + "</td>";
                        str += "<td>" + (data[i].gisong ?? "") + "</td>";
                        str += "<td>" + (data[i].aspscd ?? "") + "</td>";
                        str += "<td>" + (data[i].partNo ?? "") + "</td>";
                        str += "<td>" + (data[i].partName ?? "") + "</td>";
                        str += "<td>" + (data[i].spec ?? "") + "</td>";
                        str += "<td>" + (data[i].qty ?? "") + "</td>";
                        str += "<td>" + (data[i].blockNo ?? "") + "</td>";
                        str += "<td>" + (data[i].blockopt ?? "") + "</td>";
                        str += "<td>" + (data[i].glCode ?? "") + "</td>";
                        str += "<td>" + (data[i].version ?? "") + "</td>";

                        let cmtVal = data[i].cmt || "";
                        cmtVal = cmtVal.replace(/-/g, "<br>-");
                        str += "<td>" + cmtVal + "</td>";

                        str += "</tr>";
                    }

                    $("#contentTable").append(str);

                    // ✅ DataTable이 이미 떠 있으면 먼저 destroy
                    if ($.fn.DataTable.isDataTable("#infoTable")) {
                        $("#infoTable").DataTable().clear().destroy();
                    }

                    // ✅ 다시 초기화
                    $("#infoTable").DataTable({
                        responsive: true,
                        lengthChange: true,
                        pageLength: 50,
                        autoWidth: false,
                        processing: true,
                        dom: "Bfrtip",
                        buttons: [
                            { extend: "csv", charset: "UTF-16LE", text: "CSV", filename: "csv_Result" },
                            { extend: "excel", charset: "UTF-8", text: "EXCEL", filename: "excel_Result" },
                            { extend: "copy" }
                        ]
                    }).buttons().container().appendTo("#infoTable_wrapper .col-md-6:eq(0)");

                    // 검색 후에도 저장된 컬럼 숨김 상태 적용
                    setTimeout(() => {
                        applySavedColumnVisibility();
                    }, 0);

                } else {
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


function doWorkAsync() {

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