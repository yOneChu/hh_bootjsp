let dtTable = $("#infoTable").DataTable({
    "responsive": true,
    "lengthChange": true,
    "pageLength": 50,     //페이지 당 글 개수 설정
    "autoWidth": false, // 가로자동
    "processing": true,
    //"scrollX" : true, //가로  스크롤
    //"ordering" : false,
    //"searching" : false,
    //"paging" : false, // 페이징표시 삭제
    "destroy": true, // 테이블 재생성
    "buttons": ["csv", "excel", "copy"]
}).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


//ready
$(document).ready(function() {

    $("#subae").removeClass("menu-open");
    $("#sap").removeClass("menu-open");
    $("#mlb").removeClass("menu-open");
    $("#vault").removeClass("menu-open");


    // Initialize event bindings
    reSetData();
    
    // Initial load
    searchPID();

}); // end document ready


/**
 * 대시보드 상세화면 리스트
 * @param type
 * @param viewDate
 */
function viewList(type, viewDate) {

    //console.log(type + " -- " + viewDate);

    let todayVal = '<%=todayVal %>'

    //VAULT-운영
    let urlValue = "https://vault-in.hdel.co.kr:8070/dashboard/searchPriceReductionPopRev?";

    urlValue += "viewType=" + type;
    urlValue += "&startDate=" + viewDate;
    urlValue += "&todayVal=" + todayVal;
    urlValue += "&rate=TRUE";
    window.open(urlValue,'_blank','width=1500, height=800, top=50, left=50, scrollbars=yes');
}

function viewExportList(curDate) {

    //alert('curdate == ' + curDate);
    let todayVal = '<%=todayVal %>'
    //searchPriceReductionExportDataPop.jsp

    let urlValue = "https://plmpro.hdel.co.kr/jsp/searchLogic/searchPriceReductionExportDataPop.jsp?";
    //let urlValue = "http://localhost/jsp/searchLogic/searchPriceReductionExportDataPop.jsp?";
    urlValue += "curDate=" + curDate;
    urlValue += "&todayVal=" + todayVal;
    window.open(urlValue,'_blank','width=1600, height=800, top=50, left=50, scrollbars=yes');

}

function searchPID()
{
    let brand = $("#brand").val();
    let partName = $("#partName").val();


    $('#infoTable').DataTable().destroy();
    $("#contentTable").empty();

    showLoading(); // 로딩바 표시

    $.ajax({
        type : "post",
        //url : "searchPID.jsp",
        crossDomain : true,
        url : "/dashboard/findPlanDashAsBrand",
        data : {
            brand : brand,
            partName: partName
        },
        success : function(data)
        {
            console.log("data - ", data);

            let str = "";

            if(data != null && data.length > 0) {

                for(let i=0; i < data.length; i++) {

                    let batchDate = data[i].batchDate;
                    let brand = data[i].brand;
                    let partName = data[i].partName;
                    let partNo = data[i].partNo;
                    let planIndex = data[i].planIndex;
                    let totalCnt = data[i].totalCnt;
                    let toCost = data[i].toCost;



                    let dis202601 = data[i].dis202601;
                    let dis202602 = data[i].dis202602;
                    let dis202603 = data[i].dis202603;
                    let dis202604 = data[i].dis202604;
                    let dis202605 = data[i].dis202605;
                    let dis202606 = data[i].dis202606;
                    let dis202607 = data[i].dis202607;
                    let dis202608 = data[i].dis202608;
                    let dis202609 = data[i].dis202609;
                    let dis202610 = data[i].dis202610;
                    let dis202611 = data[i].dis202611;
                    let dis202612 = data[i].dis202612;

                    let toCostSum = formatMoney(toCost * totalCnt);


                    str +=
                        `
                        <tr>
                            <td style="font-weight: bold; text-align: center;">${(i+1)}</td>
                            <td style="font-weight: bold; text-align: center;">${planIndex}</td>
                            <td style="font-weight: bold; text-align: center;">${brand}</td>
                            <td style="font-weight: bold; text-align: center;">${partNo}</td>
                            <td style="font-weight: bold; text-align: center;">${partName}</td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> <font color="red"> ${totalCnt} </font> </a></td>
                            <td style="font-weight: bold; text-align: center;">${toCostSum}</td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202601}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202602}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202603}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202604}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202605}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202606}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202607}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202608}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202609}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202610}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202611}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> ${dis202612}  </a></td>
                        </tr>
                        `;




                } // end for


                $("#contentTable").append(str);



                $("#infoTable").DataTable({
                    "responsive": true,
                    "lengthChange": true,
                    "pageLength": 50,     //페이지 당 글 개수 설정
                    "autoWidth": false, // 가로자동
                    "processing": true,
                    "scrollX" : true, //가로  스크롤
                    "destroy": true, // 테이블 재생성
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
                }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(6)');

            }
        } // end success;

         // 성공 시 로딩바 제거
    });

    hideLoading();
}


function searchExcel() {

    showLoading(); // 로딩바 표시
    $.ajax({
        url: '/excel/searchPlanDataExcel',   // 요청 보낼 URL
        type: 'POST',              // 메서드 (GET/POST 등)
        data : {
            year : "2026"
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


function reSetData() {
    // 브랜드 선택 변경 시 검색 수행
    if (typeof $ !== 'undefined') {
        $("#brand").off('change.planC').on('change.planC', function() {
            try {
                searchPID();
            } catch (e) {
                console.error('searchPID 호출 중 오류:', e);
            }
        });

        $("#partName").off('change.planC').on('change.planC', function() {
            try {
                searchPID();
            } catch (e) {
                console.error('searchPID 호출 중 오류:', e);
            }
        });
    }
}

