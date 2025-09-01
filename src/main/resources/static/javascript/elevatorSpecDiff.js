

$(document).ready(function () {




});


function search() {

    let ho1 = $("#ho1").val();
    let ho2 = $("#ho2").val();

    showLoading(); // 로딩바 표시
    $.ajax({
        type : "post",
        //url : "searchPID.jsp",
        crossDomain : true,
        url : "/subae/elevatorSpecDiff",
        data : {
            ho1 : ho1,
            ho2 : ho2
        },
        //async: true,
      /*  beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },*/
        success : function(data)
        {
            console.log("data - ", data);

            let str = "";

            if(data != null && data.length > 0) {

                for(let i=0; i < data.length; i++) {
                    let SPEC_VALUE = data[i].SPEC_VALUE;
                    let SPEC_CODE = data[i].SPEC_CODE;
                    let VALUE = data[i].VALUE;
                    let TYPE = data[i].TYPE;
                    let VALUE2 = data[i].VALUE2;

                    str +=
                        `
                                <tr class="diffData">
                                    <td>${TYPE} </td>
                                    <td>${SPEC_VALUE} </td>
                                    <td>${SPEC_CODE} </td> 
                                    <td class="word-wrap">${VALUE} </td>
                                    <td class="word-wrap">${VALUE2} </td>
                                </tr>
                            `;

                } // end for

                //console.log(str);

                $("#result-body").append(str);


                // 다른행 표시
                rowDiffColor();


                // DataTable 재초기화
                if ($.fn.DataTable.isDataTable('#specTable')) {
                    table.destroy();
                }

                table = $('#specTable').DataTable({
                    //paging: true,
                    responsive: true,
                    lengthChange: true,
                    pageLength: 100,     //페이지 당 글 개수 설정,
                    searching: true,
                    //info: false,
                    //paging : true,
                    buttons: ["csv", "excel", "copy"]
                }).buttons().container().appendTo('#specTable_wrapper .col-md-6:eq(0)');


                hideLoading(); // 성공 시 로딩바 제거

            } else {
                alert("검색결과가 없습니다.");

            }
        } // end success;
    });

}


function rowDiffColor() {
    console.log("----- rowDiffColor -----");
    $('#result-body tr').each(function() {
        //console.log(this);

        let $tds = $(this).find("td");
        // VALUE는 4번째 td (index 3), VALUE2는 5번째 td (index 4)
        let value1 = $(this).find('td').eq(3).text().trim();
        let value2 = $(this).find('td').eq(4).text().trim();

        //console.log(value1 + " - " + value2);

        if (value1 !== value2) {
            // 두 값이 다르면 tr에 분홍색 배경 적용
            //$(this).css('background-color', '#ffc0ed');
            $tds.css('background-color', 'pink');
        }
    });
}


let viewFlag = false;

function rowMatchHide() {
    console.log("----- rowMatchHide -----> " + viewFlag);

    if(viewFlag == true) {

        $(".diffData").show();
        viewFlag = false;
        return;
    }


    $('#result-body tr').each(function() {
        const $tr = $(this);
        const $tds = $tr.find('td');
        const value1 = $tds.eq(3).text().trim();
        const value2 = $tds.eq(4).text().trim();


        // 값이 동일하면 행을 숨기고, 다르면 보이게 처리
        if (value1 === value2) {
            // 숨길 때 배경색 등 스타일도 초기화
            $tds.css('background-color', '');
            $tr.hide();
        } else {
            $tr.show();
        }

        viewFlag = true;

    });
}


// 예시 데이터 (실제 개발 시 API 연동 예정)
const dummyData = {
    "H001": { ho1: "1050kg", ho2: "1050kg" },
    "H002": { ho1: "1.75m/s", ho2: "1.75m/s" },
    "H003": { ho1: "M2 도어", ho2: "M3 도어" },
    "H004": { ho1: "지진감지센서", ho2: "지진감지센서" },
    "H005": { ho1: "현장조립", ho2: "공장조립" },
};

function compareSpecs() {
    const tbody = document.getElementById("result-body");
    tbody.innerHTML = "";

    for (const code in dummyData) {
        const val1 = dummyData[code].ho1;
        const val2 = dummyData[code].ho2;

        const tr = document.createElement("tr");
        if (val1 !== val2) tr.classList.add("highlight");

        tr.innerHTML = `
        <td>${code}</td>
        <td>${val1}</td>
        <td>${val2}</td>
      `;
        tbody.appendChild(tr);
    }
}

// 로딩바 표시 함수
function showLoading() {
    // 로딩바 HTML 생성
    const loadingHtml = `
        <div id="loadingOverlay" style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
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
                <p style="margin: 0; font-size: 16px; color: #333;">데이터 분석중입니다...</p>
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
    document.body.insertAdjacentHTML('beforeend', loadingHtml);
}



// 로딩바 제거 함수
function hideLoading() {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.remove();
    }
}