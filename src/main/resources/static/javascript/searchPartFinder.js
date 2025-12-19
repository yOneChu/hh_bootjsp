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

});


//검색
function searchPID()
{
    let year = $("#year").val(); //
    let partNo = $("#partNo").val(); // L
    let blockNo = $("#blockNo").val();
    let cmt = $("#cmt").val();
    let status = $("#status").val();

    console.log(year);
    console.log(partNo);

    // 입력값 트림 처리
    partNo = partNo ? partNo.trim() : "";
    blockNo = blockNo ? blockNo.trim() : "";

    // partNo, blockNo 둘 다 비어있으면 중단, 둘 중 하나라도 있으면 진행
    if ((partNo === "" || partNo == null) && (blockNo === "" || blockNo == null)) {
        alert("partNo 또는 blockNo 중 하나는 입력하세요.");
        return;
    }


    $('#infoTable').DataTable().destroy();
    $("#contentTable").empty();

    showLoading(); // 로딩바 표시
    $.ajax({
        type : "post",
        //url : "searchPID.jsp",
        crossDomain : true,
        url : "/subae/searchMissPartofProduct",
        data : {
            partNo : partNo,
            year : year,
            blockNo: blockNo,
            cmt : cmt,
            status : status
        },
        beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },
        success : function(data)
        {
            console.log("data - ", data);

            let str = "";

            if(data != null && data.length > 0) {

                for(let i=0; i < data.length; i++) {
                    str += "<tr>";

                    str += "<td>" + data[i].productNo + "</td>";
                    str += "<td>" + data[i].productVersion + "</td>";
                    str += "<td>" + data[i].productStatus + "</td>";
                    str += "<td>" + data[i].productModDate + "</td>";
                    str += "<td>" + data[i].brand + "</td>";
                    str += "<td>" + data[i].gisong + "</td>";
                    str += "<td>" + data[i].aspscd + "</td>";

                    str += "<td>" + data[i].partNo + "</td>";
                    str += "<td>" + data[i].partName + "</td>";

                    str += "<td>" + data[i].spec + "</td>";
                    str += "<td>" + data[i].qty + "</td>";
                    str += "<td>" + data[i].blockNo + "</td>";
                    str += "<td>" + data[i].blockopt + "</td>";
                    str += "<td>" + data[i].glCode + "</td>";
                    str += "<td>" + data[i].version + "</td>";

                    let cmtVal = data[i].cmt;
                    cmtVal = cmtVal.replace(/-/g, '<br>-');

                    str += "<td>" + cmtVal + "</td>";
                    str += "</tr>";
                } // end for


                $("#contentTable").append(str);


                $("#infoTable").DataTable({
                    "responsive": true,
                    "lengthChange": true,
                    "pageLength": 50,     //페이지 당 글 개수 설정
                    "autoWidth": false, // 가로자동
                    "processing": true,
                    "destroy": true, // 테이블 재생성
                    //"scrollX" : true, //가로  스크롤
                    "destroy": true, // 테이블 재생성
                    //"scrollX": true, // 가로 스크롤
                    //"buttons": ["csv", "excel", "pdf", "print"]
                    //"buttons": ["csv", "excel"]
                    "dom": "Bfrtip",
                    "buttons": [
                        {
                            extend: "csv",
                            charset: "UTF-16LE",
                            text: "CSV",
                            filename: 'csv_Result'
                        },
                        {
                            extend: "excel",
                            charset: "UTF-8",
                            text: "EXCEL",
                            filename: 'excel_Result',
                        },
                        {
                            extend: "copy"
                        }
                    ]
                }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                // 검색 후에도 저장된 컬럼 숨김 상태 적용
                setTimeout(function(){
                    applySavedColumnVisibility();
                }, 0);

            } else {
                alert("검색결과가 없습니다.");
            }
        } // end success;
    });
    hideLoading(); // 성공 시 로딩바 제거
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