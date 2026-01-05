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


    document.addEventListener("keydown", function(event) {
        if (event.key === "Enter") {
            searchFile();
            event.preventDefault();   // 기본 동작 막기
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


    searchFile();

    // 폴더 셀렉트 기본값 세팅 후 데이터 로딩
    settingFolder();

    // 상위 폴더 선택 시 하위 폴더 옵션 동적 변경
    $(document).on('change', '#folderList', function() {
        const parent = $(this).val() || '';
        const $child = $('#folderChild');
        let optionsHtml = "<option value=''>전체</option>";

        if (window._parentChildMap) {
            if (parent === '') {
                // 전체 선택 시: 모든 하위폴더를 모아 보여주되, 너무 많을 수 있으므로 상위 선택 유도 차원에서 전체만 보이도록 유지
                // 요구사항엔 명확히 없으므로 '전체'만 제공
            } else {
                const children = Array.from(window._parentChildMap.get(parent) || []).sort((a,b)=>a.localeCompare(b));
                for (const child of children) {
                    const esc = $('<div>').text(child).html();
                    optionsHtml += `<option value="${esc}">${esc}</option>`;
                }
            }
        }
        $child.html(optionsHtml).val('');
    });



    $("#modBtnSearch").on("click", function () {
        searchFile();
    });

    $("#modBtnSearch_m").on("click", function () {
        searchFile();
    });
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
    let fileName = $('#fileName').val();
    let folderList = $('#folderList').val();
    let folderChild = $('#folderChild').val();
    let filePath = '';

    // 폴더 조건 결합: 두 값이 공백이 아니라면 '/'로 연결하여 filePath에 설정
    const hasParent = folderList && folderList.length > 0;
    const hasChild = folderChild && folderChild.length > 0;

    if (hasParent && hasChild) {
        filePath = folderList + '/' + folderChild;
    } else if (hasParent) {
        // 상위만 선택된 경우 상위만 전달
        filePath = folderList;
    } // 둘 다 공백이면 filePath는 '' 유지




    console.log('folderList -- ' + folderList);
    console.log('folderChild -- ' + folderChild);
    console.log('filePath -- ' + filePath);

    $('#modDrawingTable').DataTable().destroy();
    $("#contentTable").empty();

    //showLoading(); // 로딩바 표시

    $.ajax({
        type : "post",
        crossDomain : true,
        url : "/vault/findModuleFolder",
        data: {
            fileName: fileName,
            filePath: filePath
        },
        success : function(data)
        {
            console.log("data - ", data);

            const prefix = "$/Dev Project/모듈러구조개발팀/";

            let str = "";
            // 상위/하위 폴더 분리를 위한 자료구조
            // parentChildMap: Map<parent, Set<child>>
            const parentChildMap = new Map();
            const parents = new Set();

            if(data != null && data.length > 0) {

                for(let i=0; i < data.length; i++) {

                    if (data[i].FILENAME && data[i].FILENAME.toLowerCase().includes("dwg")) {
                        // dwg가 포함된 경우 실행
                        continue;
                    }

                    str += "<tr>";
                        //http://10.225.80.35/vaultview/getView.html?filename=12500471_G02_FRAME%20ASSY
                        let viewLink = 'http://10.225.80.35/vaultview/getView.html?filename=';

                        let linkFileName = data[i].FILENAME.replace(/\.[^.]+$/, "");
                        viewLink += linkFileName;
                        viewLink = encodeURI(viewLink);
                        //12500477_GUSSET

                        //console.log(viewLink);

                        let sliceFilePath = data[i].FPATH ? data[i].FPATH.replace(prefix, "") : "";
                        if (sliceFilePath) {
                            // 상위/하위 분리: 첫 세그먼트를 상위, 나머지를 하위로
                            const sepIdx = sliceFilePath.indexOf('/');
                            let parent = sliceFilePath;
                            let child = '';
                            if (sepIdx > -1) {
                                parent = sliceFilePath.substring(0, sepIdx);
                                child = sliceFilePath.substring(sepIdx + 1);
                            }
                            parents.add(parent);
                            if (!parentChildMap.has(parent)) parentChildMap.set(parent, new Set());
                            if (child) parentChildMap.get(parent).add(child);
                        }

                        // moduleView.html 스타일을 참고한 팝업 버튼 (뷰어 링크)
                        str += `
                        <td style="width: 260px;"> ${data[i].FILENAME} </td>;
                        <td style="width: 130px;">
                            <button type="button" 
                                    onclick="window.open('${viewLink}', 'moduleViewer', 'width=1200,height=800,menubar=no,toolbar=no,location=no,status=no,resizable=yes,scrollbars=yes'); return false;">
                                <i class="bi bi-box-arrow-up-right me-1"></i>View
                            </button>
                        </td>
                        <td>${sliceFilePath}</td>
                        <td style="width: 100px;">${data[i].CREATOR}</td>
                        `;

                    str += "</tr>";
                } // end for


                $("#contentTable").append(str);

                hideLoading(); // 성공 시 로딩바 제거

                $("#modDrawingTable").DataTable({
                    "responsive": true,
                   // "lengthChange": true,
                    "pageLength": 50,     //페이지 당 글 개수 설정
                    "autoWidth": false, // 가로자동
                    "processing": true,
                    "scrollX" : true, //가로  스크롤
                    "destroy": true, // 테이블 재생성
                    //"scrollX": true, // 가로 스크롤
                    //"buttons": ["csv", "excel", "pdf", "print"]
                    //"buttons": ["csv", "excel"]
                   // "dom": "Bfrtip",
                    "buttons": [
                        {
                            extend: "excel",
                            charset: "UTF-8",
                            text: "EXCEL",
                            filename: 'MODULE_LIST',
                        },
                        {
                            extend: "copy"
                        }
                    ]
                }).buttons().container().appendTo('#modDrawingTable_wrapper .col-md-6:eq(0)');

                // 폴더 셀렉트박스 옵션 구성: 상위는 folderList, 하위는 folderChild
                window._parentChildMap = parentChildMap; // 전역 보관(이 페이지 범위)

                const $parentSel = $('#folderList');
                const $childSel = $('#folderChild');

                const prevParent = $parentSel.val();
                const prevChild = $childSel.val();

                const sortedParents = Array.from(parents).sort((a,b)=>a.localeCompare(b));
                let parentOptions = "<option value=''>전체</option>";
                for (const p of sortedParents) {
                    const esc = $('<div>').text(p).html();
                    parentOptions += `<option value="${esc}">${esc}</option>`;
                }
                $parentSel.html(parentOptions);

                // 이전 선택 복원 가능하면 복원
                if (prevParent && parents.has(prevParent)) {
                    $parentSel.val(prevParent);
                } else {
                    $parentSel.val('');
                }

                // 상위 선택에 따라 하위 셋업
                let childOptions = "<option value=''>전체</option>";
                const curParent = $parentSel.val();
                if (curParent) {
                    const children = Array.from(parentChildMap.get(curParent) || []).sort((a,b)=>a.localeCompare(b));
                    for (const c of children) {
                        const esc = $('<div>').text(c).html();
                        childOptions += `<option value="${esc}">${esc}</option>`;
                    }
                    if (prevChild && (parentChildMap.get(curParent) || new Set()).has(prevChild)) {
                        // will set after html
                    } else {
                        // no-op, keep default 전체
                    }
                }
                $childSel.html(childOptions);
                if (curParent && prevChild && (parentChildMap.get(curParent) || new Set()).has(prevChild)) {
                    $childSel.val(prevChild);
                } else {
                    $childSel.val('');
                }

            } else {
                //hideLoading(); // 성공 시 로딩바 제거
                alert("검색결과가 없습니다.");
            }


        } // end success;
    });


}
