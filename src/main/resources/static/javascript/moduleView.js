// 모듈러시스템 3D 조회 (moduleView.html)
//  - /vault/findModuleFolder 로 도면 목록을 조회해서 DataTables 로 보여준다.
//  - 다크모드 토글은 commonUtil.js 가 처리한다.
(function () {
    'use strict';

    const PATH_PREFIX = '$/Dev Project/모듈러구조개발팀/';
    //const PATH_PREFIX = '$/Dev Project/케이지개발팀/';
    const VIEWER_URL = 'http://10.225.80.35/vaultview/getView.html?filename=';
    const VIEWER_FEATURES = 'width=1200,height=800,menubar=no,toolbar=no,location=no,status=no,resizable=yes,scrollbars=yes';

    const folderMap = new Map(); // 상위폴더 -> Set(하위폴더). 조회할 때마다 누적해서 목록이 줄어들지 않게 한다.
    let table = null;
    let xhr = null;
    let excluded = 0;

    function esc(s) {
        if (s === null || s === undefined) return '';
        return String(s)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
    }

    // "상위/하위/..." -> { parent, child }
    function splitPath(path) {
        const idx = path.indexOf('/');
        return idx > -1
            ? { parent: path.substring(0, idx), child: path.substring(idx + 1) }
            : { parent: path, child: '' };
    }

    function announce(msg) {
        $('#mvLive').text(msg);
    }

    /* ---------- 폴더 선택 ---------- */

    function optionsHtml(values) {
        let html = "<option value=''>전체</option>";
        values.forEach(v => { html += '<option value="' + esc(v) + '">' + esc(v) + '</option>'; });
        return html;
    }

    function fillParents() {
        const $sel = $('#folderList');
        const prev = $sel.val() || '';
        const parents = Array.from(folderMap.keys()).sort((a, b) => a.localeCompare(b));
        $sel.html(optionsHtml(parents)).val(folderMap.has(prev) ? prev : '');
    }

    function fillChildren(keep) {
        const parent = $('#folderList').val() || '';
        const $sel = $('#folderChild');
        const children = parent ? Array.from(folderMap.get(parent) || []).sort((a, b) => a.localeCompare(b)) : [];
        $sel.html(optionsHtml(children));
        $sel.val(keep && children.indexOf(keep) > -1 ? keep : '');
        $sel.prop('disabled', !parent || children.length === 0);
        $('#folderChildHint').text(
            !parent ? '상위폴더를 고르면 선택할 수 있습니다.'
                : children.length ? '하위폴더 ' + children.length + '개'
                : '하위폴더가 없습니다.');
    }

    /* ---------- 표 ---------- */

    function initTable() {
        table = $('#modDrawingTable').DataTable({
            data: [],
            autoWidth: false,
            deferRender: true,
            pageLength: 50,
            order: [[0, 'desc']],
            dom: 't<"mv-foot"ip>',
            columns: [
                {
                    data: 'name',
                    render: function (name, type, row) {
                        if (type !== 'display') return name;
                        const extCls = row.ext === 'iam' || row.ext === 'ipt' ? ' mv-ext-' + row.ext : '';
                        const extTitle = row.ext === 'iam' ? '조립품' : row.ext === 'ipt' ? '부품' : '파일';
                        return '<div class="mv-file">'
                            + '<span class="mv-ext' + extCls + '" title="' + extTitle + '">' + esc(row.ext.toUpperCase() || '-') + '</span>'
                            + '<span class="mv-name">' + esc(name) + '</span>'
                            + '<button type="button" class="mv-icon-btn js-copy" data-name="' + esc(name) + '"'
                            + ' aria-label="파일명 복사" title="파일명 복사"><i class="far fa-copy" aria-hidden="true"></i></button>'
                            + '</div>';
                    }
                },
                {
                    data: 'name',
                    orderable: false,
                    searchable: false,
                    render: function (name, type) {
                        if (type !== 'display') return '';
                        return '<button type="button" class="mv-view js-view" data-name="' + esc(name) + '"'
                            + ' title="새 창에서 3D 뷰어 열기"><i class="fas fa-cube" aria-hidden="true"></i>3D 보기</button>';
                    }
                },
                {
                    data: 'path',
                    render: function (path, type) {
                        if (type !== 'display') return path;
                        if (!path) return '';
                        const label = path.split('/').map(esc).join('<i class="fas fa-chevron-right" aria-hidden="true"></i>');
                        return '<button type="button" class="mv-path js-folder" data-path="' + esc(path) + '"'
                            + ' title="이 폴더의 도면만 보기">' + label + '</button>';
                    }
                },
                {
                    data: 'creator',
                    className: 'mv-creator',
                    render: function (v, type) { return type === 'display' ? esc(v) : v; }
                }
            ],
            language: {
                emptyTable: '조건에 맞는 도면이 없습니다. 도면번호의 <b>*</b> 위치나 폴더 선택을 바꿔 보세요.',
                zeroRecords: '결과 내 검색어와 일치하는 도면이 없습니다.',
                info: '_START_–_END_ / 총 _TOTAL_건',
                infoEmpty: '0건',
                infoFiltered: '(전체 _MAX_건 중)',
                paginate: { previous: '이전', next: '다음' }
            }
        });

        // Excel 내보내기 (파일명 / 폴더 / 작업자)
        new $.fn.dataTable.Buttons(table, {
            buttons: [{
                extend: 'excelHtml5',
                filename: 'MODULE_LIST',
                title: null,
                exportOptions: { columns: [0, 2, 3], orthogonal: 'export' }
            }]
        });

        table.on('draw', updateCount);
    }

    function updateCount() {
        const info = table.page.info();
        $('#mvCount').text(info.recordsTotal.toLocaleString());
        const parts = [];
        if (info.recordsDisplay !== info.recordsTotal) parts.push('결과 내 검색 ' + info.recordsDisplay.toLocaleString() + '건');
        if (excluded) parts.push('dwg · 특수문자 파일 ' + excluded.toLocaleString() + '건 제외');
        $('#mvCountSub').text(parts.join('  ·  '));
        $('#mvExcel').prop('disabled', info.recordsDisplay === 0);
    }

    /* ---------- 조회 ---------- */

    function setLoading(on) {
        $('#mvResult').toggleClass('is-loading', on).attr('aria-busy', String(on));
        $('#modBtnSearch').prop('disabled', on);
    }

    function searchFile() {
        const fileName = $('#fileName').val().trim();
        const parent = $('#folderList').val() || '';
        const child = $('#folderChild').val() || '';
        const filePath = parent && child ? parent + '/' + child : parent;

        if (xhr) xhr.abort();
        $('#mvResult').removeClass('is-error');
        setLoading(true);

        xhr = $.ajax({
            type: 'post',
            url: '/vault/findModuleFolder',
            data: { fileName: fileName, filePath: filePath },
            success: function (data) {
                const rows = [];
                excluded = 0;

                (data || []).forEach(function (item) {
                    const name = item.FILENAME || '';
                    // dwg 파일, 특수문자가 들어간 파일명은 뷰어가 열리지 않아 제외
                    if (!name || name.toLowerCase().includes('dwg') || /[^a-zA-Z0-9가-힣._\-()\s]/.test(name)) {
                        excluded++;
                        return;
                    }
                    const path = item.FPATH ? item.FPATH.replace(PATH_PREFIX, '') : '';
                    if (path) {
                        const p = splitPath(path);
                        if (!folderMap.has(p.parent)) folderMap.set(p.parent, new Set());
                        if (p.child) folderMap.get(p.parent).add(p.child);
                    }
                    const dot = name.lastIndexOf('.');
                    rows.push({
                        name: name,
                        ext: dot > -1 ? name.substring(dot + 1).toLowerCase() : '',
                        path: path,
                        creator: item.CREATOR || ''
                    });
                });

                fillParents();
                fillChildren($('#folderChild').val());

                $('#mvFilter').val('');
                table.search('').clear().rows.add(rows).draw();
                announce('도면 ' + rows.length + '건을 조회했습니다.');
            },
            error: function (jq, status) {
                if (status === 'abort') return;
                $('#mvResult').addClass('is-error');
                announce('도면 목록을 가져오지 못했습니다.');
            },
            complete: function (jq, status) {
                if (status === 'abort') return; // 새 조회가 진행 중
                xhr = null;
                setLoading(false);
            }
        });
    }
    window.searchFile = searchFile;

    /* ---------- 행 동작 ---------- */

    function openViewer(name) {
        const base = name.replace(/\.[^.]+$/, '');
        const win = window.open(VIEWER_URL + encodeURIComponent(base), 'moduleViewer', VIEWER_FEATURES);
        if (!win) alert('팝업이 차단되었습니다. 브라우저에서 이 사이트의 팝업을 허용해 주세요.');
    }

    // 사내 http 환경에서는 navigator.clipboard 를 쓸 수 없어 execCommand 로 대체
    function copyText(text) {
        if (navigator.clipboard && window.isSecureContext) return navigator.clipboard.writeText(text);
        return new Promise(function (resolve, reject) {
            const ta = document.createElement('textarea');
            ta.value = text;
            ta.style.position = 'fixed';
            ta.style.opacity = '0';
            document.body.appendChild(ta);
            ta.select();
            let ok = false;
            try { ok = document.execCommand('copy'); } catch (e) { ok = false; }
            ta.remove();
            ok ? resolve() : reject(new Error('copy failed'));
        });
    }

    /* ---------- 초기화 ---------- */

    $(function () {
        $('#dashboard').removeClass('menu-open');

        initTable();

        $('#modSearchForm').on('submit', function (e) {
            e.preventDefault(); // 도면번호 입력칸에서 Enter = 조회
            searchFile();
        });

        $('#modBtnReset').on('click', function () {
            $('#fileName').val('');
            $('#folderList').val('');
            fillChildren('');
            searchFile();
            $('#fileName').trigger('focus');
        });

        // 예시 버튼: 입력칸에 채우고 바로 조회
        $('.mv-eg').on('click', function () {
            $('#fileName').val($(this).data('eg'));
            searchFile();
        });

        // 폴더는 고르면 바로 조회
        $('#folderList').on('change', function () {
            fillChildren('');
            searchFile();
        });
        $('#folderChild').on('change', searchFile);

        // 결과 내 검색 / 페이지 크기 / Excel
        let filterTimer = null;
        $('#mvFilter').on('input', function () {
            const v = this.value;
            clearTimeout(filterTimer);
            filterTimer = setTimeout(function () { table.search(v).draw(); }, 150);
        });
        $('#mvLen').on('change', function () { table.page.len(Number(this.value)).draw(); });
        $('#mvExcel').on('click', function () { table.button(0).trigger(); });
        $('#mvRetry').on('click', searchFile);

        // 표 안 버튼 (3D 보기 / 파일명 복사 / 폴더로 좁히기)
        $('#modDrawingTable tbody')
            .on('click', '.js-view', function () {
                openViewer($(this).attr('data-name'));
            })
            .on('click', '.js-copy', function () {
                const $btn = $(this);
                const name = $btn.attr('data-name');
                copyText(name).then(function () {
                    $btn.addClass('is-done').find('i').attr('class', 'fas fa-check');
                    announce(name + ' 파일명을 복사했습니다.');
                    setTimeout(function () { $btn.removeClass('is-done').find('i').attr('class', 'far fa-copy'); }, 1500);
                }).catch(function () {
                    announce('복사하지 못했습니다.');
                    window.prompt('아래 파일명을 복사하세요.', name);
                });
            })
            .on('click', '.js-folder', function () {
                const p = splitPath($(this).attr('data-path'));
                $('#folderList').val(p.parent);
                fillChildren(p.child);
                searchFile();
            });

        searchFile(); // 첫 화면: 전체 목록
    });
})();
