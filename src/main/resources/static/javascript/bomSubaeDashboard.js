    let dtTable = $("#infoTable").DataTable({
        "responsive": true,
        "lengthChange": true,
        "pageLength": 50,     //페이지 당 글 개수 설정
        "autoWidth": false, // 가로자동
        "processing": true,
        "destroy": true, // 테이블 재생성
        //"scrollX": true, // 가로 스크롤
        //"buttons": ["csv", "excel", "pdf", "print"]
        "buttons": ["excel", "copy"]
    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

    // 초기화
    $(document).ready(function() {
        $("#subae").removeClass("menu-open");
        $("#sap").removeClass("menu-open");
        $("#mlb").removeClass("menu-open");
        $("#vault").removeClass("menu-open");

        searchPID();
        //renderTable();
        //updateSummaryCards();
    });

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


    //null, undefined, NaN, 빈 문자열, 혹은 falsy한 값 일 때 0으로 초기화
    function initIfEmpty(value) {
        return value ?? 0; // null 또는 undefined일 때만 0
    }

    //검색
    function searchPID(year, month)
    {
        //let month = $("#year").val(); // SPEC
        let partNo = $("#partNo").val(); // LIKE

        month = $('#monthSelect').val();


        //console.log(year);
        console.log(month);

  /*      if(partNo == null || "" == partNo) {
            console.log(partNo);
            alert("partNo 을 입력하세요.");
            return;
        }*/





        $('#infoTable').DataTable().destroy();
        $("#contentTable").empty();

        $.ajax({
            type : "post",
            //url : "searchPID.jsp",
            crossDomain : true,
            url : "/subae/bomDashboard",
            data : {
                year : year,
                month : month
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
                        let qty = Number(initIfEmpty(data[i].qty));
                        let mCount = Number(initIfEmpty(data[i].mcount));
                        let ccount = Number(initIfEmpty(data[i].ccount));
                        let oneCount = Number(initIfEmpty(data[i].oneCount));
                        let twoCount = Number(initIfEmpty(data[i].twoCount));
                        let threeCount = Number(initIfEmpty(data[i].threeCount));

                        let allModCount = mCount + ccount + oneCount + twoCount + threeCount;

                        str += "<tr>";
                            str += "<td>" + data[i].productNo + "</td>";
                            str += "<td>" + data[i].productVersion + "</td>";
                            str += "<td>" + data[i].productVersion + "</td>";
                            str += "<td>" + data[i].gisong + "</td>";
                            str += "<td>" + data[i].productAppdate + "</td>";
                            str += "<td>" + qty + "</td>";
                            str += "<td>" + allModCount + "</td>";

                            str +=
                            `
                                <td>
                                    <div class="modification-items">
                                        <span class='mod-item modified'>M: ${mCount}</span>
                                        <span class='mod-item modified'>C: ${ccount}</span>
                                        <span class='mod-item modified'>1: ${oneCount}</span>
                                        <span class='mod-item modified'>2: ${twoCount}</span>
                                        <span class='mod-item modified'>3: ${threeCount}</span>
                                    </div>
                                </td>
                            `;

                            /*str += "<td> <div class=\"modification-items\">";
                                str += "<span class='mod-item modified'>M:1</span>";
                                str += "<span class='mod-item'>C:1</span>";
                                str += "<span class='mod-item'>1:1</span>";
                                str += "<span class='mod-item'>2:1</span>";
                                str += "<span class='mod-item'>3:1</span>";
                            str += "</div></td>";*/


                            //str += "<td>" + initIfEmpty(data[i].ccount) + "</td>";
                            //str += "<td>" + initIfEmpty(data[i].oneCount) + "</td>";
                            //str += "<td>" + data[i].twoCount + "</td>";



                            //console.log("allModCount -- " + allModCount);
                            let percentage = ( (qty - allModCount) / qty ) * 100;
                            //console.log("percentage == " + percentage);

                            //let percentageVal = Math.round(parseFloat(percentage) * 10) / 10; // => 99.4
                            let percentageVal = Math.round(parseFloat(percentage) * 100) / 100;

                            //console.log(percentageVal);

                            str +=
                                `
                                <td>
                                    <div>${percentageVal}%</div>
                                    <div class="progress-bar">
                                        <div class="progress-fill" style="width: ${percentageVal}%"></div>
                                    </div>
                                </td>
                                 <td>${data[i].mmanager} </td>
                                 <td>${data[i].emanager} </td>
                                 <td>${data[i].cmt} </td>
                                `;



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
                        //"dom": "Bfrtip",
                        "buttons": ["excel", "copy"]
                    }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');

                } else {
                    alert("검색결과가 없습니다.");

                }
            } // end success;
        });
    }