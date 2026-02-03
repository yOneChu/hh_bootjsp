

function excelData() {
    let year = $("#year").val(); // SPEC, CON


    showLoading(); // 로딩바 표시
    $.ajax({
        url: '/excel/searchDesignReqExcel',   // 요청 보낼 URL
        type: 'POST',              // 메서드 (GET/POST 등)
        data : {
            year : year,
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
        },
        error: function () {
            alert('엑셀 다운로드 중 오류가 발생했습니다.');
        }
    });
}
