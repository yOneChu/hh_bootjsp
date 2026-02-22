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


    //chart();

    //lineChart();

    barChart();

}); // end document ready


/**
 * 대시보드 상세화면 리스트
 * 클릭한 라인의 brand, partNo를 추출하여 팝업을 연다.
 * @param el 클릭된 a 엘리먼트(this)
 */
function viewList(el, month) {
    // 클릭된 엘리먼트가 속한 행(tr)을 찾고, 컬럼 인덱스로 brand/partNo 추출
    // 컬럼 구조: 0:idx, 1:planIndex, 2:brand, 3:partNo, 4:partName, 5:toCost, 6:totalCnt, 7:toCostSum, 8~: 월별 수치
/*
    const $tr = $(el).closest('tr');
    const tds = $tr.find('td');

    let brand = $(tds[1]).text().trim();
    let partNo = $(tds[2]).text().trim();

    // URL 구성 (인코딩 포함)
    const urlValue = "/dashboard/planDashboardPop?" +
        "brand=" + encodeURIComponent(brand) +
        "&partNo=" + encodeURIComponent(partNo) +
        "&month=" + encodeURIComponent(month) ;

    window.open(urlValue, '_blank', 'width=1500, height=800, top=50, left=50, scrollbars=yes');

*/
    const $tr = $(el).closest('tr');
    const tds = $tr.find('td');

    let brand = $(tds[1]).text().trim();
    let partNo = $(tds[2]).text().trim();
    //let urlValue = "https://vault-in.hdel.co.kr:8070/dashboard/searchPriceReductionDatePop?";
    let urlValue = "/dashboard/searchPriceReductionDatePop?";
    //http://localhost/jsp/searchLogic/searchPriceReductionDatePrice.jsp

    urlValue += "month=" + month;
    urlValue += "&brand=" + brand;
    urlValue += "&partNo=" + partNo;

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
                    let toCostVal = formatMoney(toCost);


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
                            <td style="font-weight: bold; text-align: center;">${toCostVal}</td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);'> <font color="red"> ${totalCnt} </font> </a></td>
                            <td style="font-weight: bold; text-align: center;">${toCostSum}</td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202601')"> ${dis202601}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202602')"> ${dis202602}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202603')"> ${dis202603}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202604')"> ${dis202604}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202605')"> ${dis202605}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202606')"> ${dis202606}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202607')"> ${dis202607}   </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202608')"> ${dis202608}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202609')"> ${dis202609}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202610')"> ${dis202610}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202611')"> ${dis202611}  </a></td>
                            <td style="font-weight: bold; text-align: center;"><a href='javascript:void(0);' onclick="viewList(this, '202612')"> ${dis202612}  </a></td>
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
                    //"scrollX" : true, //가로  스크롤
                    "destroy": true, // 테이블 재생성
                    "dom": "Bfrtip",
                    "order": [[6, 'desc']], // 0번 컬럼 오름차순
                    "columnDefs": [
                        { targets: 0, visible: false } // 0번 컬럼 숨김
                    ],
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


//현 집계중인 자재 리스트
function searchPlanPartView() {

    let urlValue = "/dashboard/searchPlanPartView?";
    window.open(urlValue,'_blank','width=1600, height=800, top=50, left=50, scrollbars=yes');
}


function reSetData() {
    showLoading(); // 로딩바 표시
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
    hideLoading(); // 성공 시 로딩바 제거
}

function chart() {
    Highcharts.chart('container', {
        chart: {
            type: 'pie',
            zooming: {
                type: 'xy'
            },
            panning: {
                enabled: true,
                type: 'xy'
            },
            panKey: 'shift'
        },
        title: {
            text: 'Egg Yolk Composition'
        },
        tooltip: {
            valueSuffix: '%'
        },
        subtitle: {
            text:
                'Source:<a href="https://www.mdpi.com/2072-6643/11/3/684/htm" target="_default">MDPI</a>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: [{
                    enabled: true,
                    distance: 20
                }, {
                    enabled: true,
                    distance: -40,
                    format: '{point.percentage:.1f}%',
                    style: {
                        fontSize: '1.2em',
                        textOutline: 'none',
                        opacity: 0.7
                    },
                    filter: {
                        operator: '>',
                        property: 'percentage',
                        value: 10
                    }
                }]
            }
        },
        series: [
            {
                name: 'Percentage',
                colorByPoint: true,
                data: [
                    {
                        name: 'Water',
                        y: 55.02
                    },
                    {
                        name: 'Fat',
                        sliced: true,
                        selected: true,
                        y: 26.71
                    },
                    {
                        name: 'Carbohydrates',
                        y: 1.09
                    },
                    {
                        name: 'Protein',
                        y: 15.5
                    },
                    {
                        name: 'Ash',
                        y: 1.68
                    }
                ]
            }
        ]
    });
}

function lineChart() {
    Highcharts.chart('container-02', {

        title: {
            text: 'U.S Solar Employment Growth',
            align: 'left'
        },

        subtitle: {
            text: 'By Job Category. Source: <a href="https://irecusa.org/programs/solar-jobs-census/" target="_blank">IREC</a>.',
            align: 'left'
        },

        yAxis: {
            title: {
                text: 'Number of Employees'
            }
        },

        xAxis: {
            accessibility: {
                rangeDescription: 'Range: 2010 to 2022'
            }
        },

        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle'
        },

        plotOptions: {
            series: {
                label: {
                    connectorAllowed: false
                },
                pointStart: 2010
            }
        },

        series: [{
            name: 'Installation & Developers',
            data: [
                43934, 48656, 65165, 81827, 112143, 142383,
                171533, 165174, 155157, 161454, 154610, 168960, 171558
            ]
        }, {
            name: 'Manufacturing',
            data: [
                24916, 37941, 29742, 29851, 32490, 30282,
                38121, 36885, 33726, 34243, 31050, 33099, 33473
            ]
        }, {
            name: 'Sales & Distribution',
            data: [
                11744, 30000, 16005, 19771, 20185, 24377,
                32147, 30912, 29243, 29213, 25663, 28978, 30618
            ]
        }, {
            name: 'Operations & Maintenance',
            data: [
                null, null, null, null, null, null, null,
                null, 11164, 11218, 10077, 12530, 16585
            ]
        }, {
            name: 'Other',
            data: [
                21908, 5548, 8105, 11248, 8989, 11816, 18274,
                17300, 13053, 11906, 10073, 11471, 11648
            ]
        }],

        responsive: {
            rules: [{
                condition: {
                    maxWidth: 500
                },
                chartOptions: {
                    legend: {
                        layout: 'horizontal',
                        align: 'center',
                        verticalAlign: 'bottom'
                    }
                }
            }]
        }

    });

}


function barChart() {
    /*Highcharts.chart('container_bar', {
        chart: {
            type: 'column'
        },
        title: {
            //text: 'Column chart with negative values'
        },
        xAxis: {
            categories: ['PIT LADDER', 'COMPEN CHAIN', 'RAIL', 'Grapes', 'Bananas', 'A', 'B', 'C', 'D']
        },
        credits: {
            enabled: false
        },
        plotOptions: {
            column: {
                borderRadius: '25%'
            }
        },
        series: [{
            //name: 'John',
            data: [850000, 30000, 47000, 789000, -200200, 789000, 789000 ,789000 ,789000]
        }]
    });*/


    Highcharts.chart('container_bar', {
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Historic World Population by Region'
        },
        subtitle: {
            text: 'Source: <a ' +
                'href="https://en.wikipedia.org/wiki/List_of_continents_and_continental_subregions_by_population"' +
                'target="_blank">Wikipedia.org</a>'
        },
        xAxis: {
            categories: ['Africa', 'America', 'Asia', 'Europe'],
            title: {
                text: null
            },
            gridLineWidth: 1,
            lineWidth: 0
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Population (millions)',
                align: 'high'
            },
            labels: {
                overflow: 'justify'
            },
            gridLineWidth: 0
        },
        tooltip: {
            valueSuffix: ' millions'
        },
        plotOptions: {
            bar: {
                borderRadius: '50%',
                dataLabels: {
                    enabled: true
                },
                groupPadding: 0.1
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -40,
            y: 80,
            floating: true,
            borderWidth: 1,
            backgroundColor: 'var(--highcharts-background-color, #ffffff)',
            shadow: true
        },
        credits: {
            enabled: false
        },
        series: [
        /*{
            name: 'Year 1990',
            data: [632]
        }, {
            name: 'Year 2000',
            data: [814]
        }, */
        {
            name: 'Year 2021',
            data: [1393, 1031, 4695, 745]
        }]
    });

}

