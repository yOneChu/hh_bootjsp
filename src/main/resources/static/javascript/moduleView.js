/*
let dtTable = $("#modDrawingTable").DataTable({
    "responsive": true,
    "lengthChange": true,
    "pageLength": 50,     //페이지 당 글 개수 설정
    "autoWidth": false, // 가로자동
    "processing": true,
    "destroy": true, // 테이블 재생성
    //"scrollX": true, // 가로 스크롤
    //"buttons": ["csv", "excel", "pdf", "print"]
    "buttons": ["csv", "excel", "copy"]
}).buttons().container().appendTo('#modDrawingTable_wrapper .col-md-6:eq(0)');

*/

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


    //pidVal03 입력하면 pidVal04 활성화
    $('#pidVal03').on('input', function () {
        const value = $(this).val();
        if (value.trim() !== '') {
            $('#pidVal04').prop('readonly', false);
        } else {
            $('#pidVal04').prop('readonly', true);
            $('#pidVal04').val('');

            $('#pidVal05').prop('readonly', true);
            $('#pidVal05').val('');
        }
    });

    //pidVal04 입력하면 pidVal05 활성화
    $('#pidVal04').on('input', function () {
        const value = $(this).val();
        if (value.trim() !== '') {
            $('#pidVal05').prop('readonly', false);
        } else {
            $('#pidVal05').prop('readonly', true);
            $('#pidVal05').val('');
        }
    });



    // 다크모드: 저장된 테마 적용
    const savedTheme = localStorage.getItem('theme');
    const $themeLabel = $('label[for="darkModeToggle"]');

    const updateThemeLabel = function(isDark) {
        if (isDark) {
            $themeLabel.text('🌙 다크모드');
        } else {
            $themeLabel.text('☀️ 라이트모드');
        }
    };

    if (savedTheme === 'dark') {
        $('body').addClass('dark-mode');
        $('#darkModeToggle').prop('checked', true);
        updateThemeLabel(true);
    } else {
        $('#darkModeToggle').prop('checked', false);
        updateThemeLabel(false);
    }

    // 다크모드 토글 스위치 핸들러
    $('#darkModeToggle').on('change', function() {
        const willBeDark = $(this).is(':checked');
        if (willBeDark) {
            $('body').addClass('dark-mode');
            localStorage.setItem('theme', 'dark');
        } else {
            $('body').removeClass('dark-mode');
            localStorage.setItem('theme', 'light');
        }
        updateThemeLabel(willBeDark);
    });

    // 폴더 셀렉트 기본값 세팅 후 데이터 로딩
    settingFolder();
    searchFile();

}); // END JQUERY


function settingFolder() {
    // 기본 옵션(전체)만 우선 세팅
    const defaultOption = "<option value=''>전체</option>";
    const $folder = $('#folderList');
    // 이미 옵션이 있으면 덮어쓰지 않고 유지
    if ($folder.find('option').length === 0) {
        $folder.html(defaultOption);
    }
}


function searchFile()
{

    $('#modDrawingTable').DataTable().destroy();
    $("#contentTable").empty();

    //showLoading(); // 로딩바 표시

    $.ajax({
        type : "get",
        crossDomain : true,
        url : "/vault/findModuleFolder",
        success : function(data)
        {
            console.log("data - ", data);

            const prefix = "$/Dev Project/모듈러구조개발팀/";


            let str = "";
            const uniqueFolders = new Set();


            if(data != null && data.length > 0) {

                for(let i=0; i < data.length; i++) {

                    if (data[i].FILENAME && data[i].FILENAME.toLowerCase().includes("dwg")) {
                        // dwg가 포함된 경우 실행
                        continue;
                    }

                    str += "<tr>";

                        let viewLink = 'vault.co.kr/fileName=?';
                        viewLink += data[i].FILENAME;

                        let sliceFilePath = data[i].FPATH ? data[i].FPATH.replace(prefix, "") : "";
                        if (sliceFilePath) {
                            uniqueFolders.add(sliceFilePath);
                        }

                        // moduleView.html 스타일을 참고한 팝업 버튼 (뷰어 링크)
                        str += `
                        <td style="width: 200px;"> ${data[i].FILENAME} </td>;
                        <td style="width: 100px;">
                            <button type="button" 
                                    onclick="window.open('${viewLink}', 'moduleViewer', 'width=1200,height=800,menubar=no,toolbar=no,location=no,status=no,resizable=yes,scrollbars=yes'); return false;">
                                <i class="bi bi-box-arrow-up-right me-1"></i>View
                            </button>
                        </td>
                        <td>${sliceFilePath}</td>
                        <td style="width: 100px;">${data[i].CREATOR}</td>
                        `;



                        //str += "<td>" + data[i].FILEMASTERID + "</td>";
                        //str += "<td>" + data[i].FILEVERSION + "</td>";
                        //str += "<td>" + data[i].FolderName + "</td>";


                        //str += "<td>" + data[i].FSTATUS + "</td>";

                    str += "</tr>";
                } // end for


                $("#contentTable").append(str);

                hideLoading(); // 성공 시 로딩바 제거

                $("#modDrawingTable").DataTable({
                    "responsive": true,
                    "lengthChange": true,
                    "pageLength": 50,     //페이지 당 글 개수 설정
                    "autoWidth": false, // 가로자동
                    "processing": true,
                    "scrollX" : true, //가로  스크롤
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
                }).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(1)');

                // 폴더 셀렉트박스 옵션 구성 (sliceFilePath 기준, 중복 제거 및 정렬)
                const $folder = $('#folderList');
                const prev = $folder.val();
                const sorted = Array.from(uniqueFolders).sort((a,b)=>a.localeCompare(b));
                let optionsHtml = "<option value=''>전체</option>";
                for (const path of sorted) {
                    const escValue = $('<div>').text(path).html();
                    optionsHtml += `<option value="${escValue}">${escValue}</option>`;
                }
                $folder.html(optionsHtml);
                if (prev && uniqueFolders.has(prev)) {
                    $folder.val(prev);
                } else {
                    $folder.val("");
                }
                $folder.trigger('change');

            } else {
                //hideLoading(); // 성공 시 로딩바 제거
                alert("검색결과가 없습니다.");
            }


        } // end success;
    });


}
