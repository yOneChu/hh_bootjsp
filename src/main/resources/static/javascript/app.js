/* =============================================================================
 * app.js — TeamDocs UI 로직
 * 데이터는 window.TeamDocsStore (api.js) 만 통해 접근합니다.
 * ===========================================================================*/
(() => {
    const store = window.TeamDocsStore;
    const specStore = window.DbSpecStore;      // ★ DB 명세서 저장소 (api.js)
    const $ = (s, r = document) => r.querySelector(s);
    const $$ = (s, r = document) => [...r.querySelectorAll(s)];

    // 상태
    let categories = [];
    let docs = [];
    let currentDocId = null;
    let editingId = null;      // null=신규, id=기존 편집
    let searchTerm = '';

    // ★ DB 명세서 상태
    let specGroups = [];       // 사이드바 카테고리 [{id,name,icon,color}, ...]
    let specs = [];            // [{id,name,icon}, ...]
    let currentSpecId = null;  // 현재 보고 있는 명세서 id
    let editorMode = 'doc';    // 'doc' | 'spec' — 에디터가 무엇을 편집 중인지
    const expandedSpecs = new Set();   // 하위 명세서를 펼쳐 둔 상위 명세서 id
    const collapsedGroups = new Set(); // 접어 둔 명세서 카테고리(그룹) id
    const collapsedCats = new Set();   // 접어 둔 문서 카테고리 id

    /* --------------------- 마크다운 렌더 --------------------- */
    marked.setOptions({
        breaks: true,
        gfm: true,
    });
    const renderMarkdown = (md) => DOMPurify.sanitize(marked.parse(md || ''));

    /* marked v5 부터 highlight 옵션이 제거되어 렌더 중에는 하이라이팅을 걸 수 없다.
       그래서 DOM 에 넣은 뒤 코드블록마다 직접 적용한다. */
    function highlightIn(root) {
        if (!window.hljs || !root) return;
        root.querySelectorAll('pre code').forEach(el => {
            try { hljs.highlightElement(el); } catch { /* 미지원 언어는 그대로 둔다 */ }
        });
    }

    /* --------------------- 아이콘 새로고침 --------------------- */
    const icons = () => window.lucide && lucide.createIcons();

    /* --------------------- 토스트 --------------------- */
    let toastTimer;
    function toast(msg, icon = 'check-circle') {
        const el = $('#toast');
        el.firstElementChild.innerHTML = `<i data-lucide="${icon}" class="w-4 h-4"></i><span>${msg}</span>`;
        el.classList.remove('hidden');
        icons();
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => el.classList.add('hidden'), 2200);
    }

    /* --------------------- 데이터 로드 --------------------- */
    async function loadData() {
        [categories, docs] = await Promise.all([store.getCategories(), store.getDocs()]);
    }

    /* ============================================================== *
     * ★ 명세서 카테고리 (DB 명세서 / API 정의서 / 기타 규칙)
     * ============================================================== */
    async function loadSpecs(force = false) {
        if (force) await specStore.refreshMenu();      // DB(PLM_DOC_MENU)에서 다시 읽기
        [specGroups, specs] = await Promise.all([
            specStore.getSpecGroups(),
            specStore.getSpecTypes(),
        ]);
    }

    /* 카테고리별 색상 — Tailwind 가 인식하도록 클래스 전체를 문자열로 둔다 */
    const GROUP_STYLES = {
        blue:   { icon: 'text-apple-blue',  activeBg: 'bg-apple-blue/10',  activeText: 'text-apple-blue' },
        violet: { icon: 'text-violet-500',  activeBg: 'bg-violet-500/10',  activeText: 'text-violet-600 dark:text-violet-400' },
        amber:  { icon: 'text-amber-500',   activeBg: 'bg-amber-500/10',   activeText: 'text-amber-600 dark:text-amber-400' },
    };
    const styleOf = (g) => GROUP_STYLES[g?.color] || GROUP_STYLES.blue;
    const groupOf = (specId) => {
        const s = specs.find(x => x.id === specId);
        return specGroups.find(g => g.id === s?.group) || specGroups[0] || { name: '문서', icon: 'folder' };
    };
    /** 바로 아래 하위 명세서 목록 (parent 로 연결된 항목들) */
    const childrenOf = (specId) => specs.filter(s => s.parent === specId);
    /** 상위 명세서 — 최상위 항목이면 null */
    const parentOf = (specId) => {
        const s = specs.find(x => x.id === specId);
        return (s && s.parent && specs.find(x => x.id === s.parent)) || null;
    };
    /** 조상 명세서 — 최상위부터 순서대로. 깊이 제한 없이 몇 단계든 따라 올라간다 */
    const ancestorsOf = (specId) => {
        const chain = [];
        let cur = parentOf(specId);
        // 데이터가 잘못돼 순환이 생겨도 멈추도록 방문한 id 를 확인한다
        while (cur && !chain.some(a => a.id === cur.id)) {
            chain.unshift(cur);
            cur = parentOf(cur.id);
        }
        return chain;
    };
    /** 하위 전체(손자 이하 포함) 개수 */
    const descendantCount = (specId) =>
        childrenOf(specId).reduce((n, k) => n + 1 + descendantCount(k.id), 0);
    /** 자기 이름 또는 하위 어딘가가 검색어와 맞는지 (깊이 무관) */
    const matchesDeep = (s, term) => !term || s.name.toLowerCase().includes(term)
        || childrenOf(s.id).some(k => matchesDeep(k, term));

    /** 명세서 한 줄 렌더 — 하위가 있으면 접기/펼치기 버튼을 붙인다.
     *  depth 는 들여쓰기 단계(최상위 0). 몇 단계든 재귀로 그린다. */
    function renderSpecRow(s, st, term, depth = 0) {
        const active = s.id === currentSpecId;
        const selfMatch = !term || s.name.toLowerCase().includes(term);
        // 상위 이름이 검색어와 맞으면 하위 전체를, 아니면 검색어에 맞는 가지만 보여준다
        const kids = childrenOf(s.id)
            .filter(k => selfMatch || matchesDeep(k, term));
        // 펼침 상태는 expandedSpecs 로만 판단한다.
        // (문서를 열면 openSpec() 이 조상들을 expandedSpecs 에 넣어 주므로,
        //  여기서 '현재 경로'를 강제로 펼치면 사용자가 접을 수 없게 된다)
        const open = kids.length > 0 && (!!term || expandedSpecs.has(s.id));

        // 단계마다 14px 씩 들여쓴다 (너무 깊어지면 더 이상 밀리지 않도록 상한)
        const indent = 16 + Math.min(depth, 8) * 14;

        const toggle = kids.length
            ? `<button data-toggle="${escapeHtml(s.id)}" title="하위 명세서 ${open ? '접기' : '펼치기'}"
                       class="shrink-0 -ml-3 p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                   <i data-lucide="${open ? 'chevron-down' : 'chevron-right'}" class="w-3 h-3"></i>
               </button>`
            : '';

        // 마우스를 올렸을 때만 보이는 편집 버튼 (하위 추가 · 이름변경 · 삭제)
        // — 하위 추가는 단계 제한 없이 모든 행에 붙는다
        const actions = `
            <span class="flex items-center gap-0.5 pr-1.5 shrink-0 opacity-0 group-hover/row:opacity-100 transition">
                <button data-add-spec="${escapeHtml(s.id)}" data-as-child="1" title="하위 명세서 추가"
                        class="p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                    <i data-lucide="plus" class="w-3 h-3"></i>
                </button>
                <button data-rename-menu="${escapeHtml(s.id)}" title="이름 변경"
                        class="p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                    <i data-lucide="pencil" class="w-3 h-3"></i>
                </button>
                <button data-del-menu="${escapeHtml(s.id)}" title="명세서 삭제"
                        class="p-0.5 rounded hover:bg-red-500/10 hover:text-red-500 transition">
                    <i data-lucide="trash-2" class="w-3 h-3"></i>
                </button>
            </span>`;

        const row = `
            <div class="group/row flex items-center rounded-lg transition ${
                active ? `${st.activeBg} ${st.activeText} font-medium`
                       : 'hover:bg-black/[0.04] dark:hover:bg-white/[0.06]'}">
                <a href="#" data-spec="${escapeHtml(s.id)}" style="padding-left:${indent}px"
                   class="flex items-center gap-2 min-w-0 flex-1 pr-1 py-1.5 text-[13.5px] ${
                    active ? '' : 'text-apple-ink dark:text-gray-300'}">
                    ${toggle}
                    <i data-lucide="${escapeAttr(s.icon || 'file-text')}" class="w-3.5 h-3.5 shrink-0 ${active ? '' : st.icon}"></i>
                    <span class="truncate flex-1">${escapeHtml(s.name)}</span>
                    ${s.live ? `<span title="실제 DB API 연결됨" class="w-1.5 h-1.5 rounded-full bg-emerald-500 shrink-0"></span>` : ''}
                </a>
                ${actions}
            </div>`;

        return row + (open ? kids.map(k => renderSpecRow(k, st, term, depth + 1)).join('') : '');
    }

    /** 카테고리(DB 명세서 / API 정의서 / 기타 규칙)별로 사이드바를 렌더링 */
    function renderSpecTree() {
        const root = $('#specGroups');
        const term = searchTerm.trim().toLowerCase();

        if (!specGroups.length) {
            root.innerHTML = `<div class="px-2.5 py-1 text-xs text-red-500">메뉴를 불러오지 못했습니다.</div>`;
            return;
        }

        // 검색 중에는 자기 이름이 맞거나, 하위(손자 이하 포함)가 맞는 항목만 남긴다
        const matches = (s) => matchesDeep(s, term);

        const html = specGroups.map(g => {
            const st = styleOf(g);
            // parentOf() 로 걸러 상위가 사라진 항목(구 데이터)도 최상위로 살려서 보여준다
            const items = specs.filter(s => s.group === g.id && !parentOf(s.id) && matches(s));
            if (term && !items.length) return '';   // 검색 중이면 빈 카테고리는 숨김

            const rows = items.length
                ? items.map(s => renderSpecRow(s, st, term)).join('')
                : `<div class="pl-4 pr-2 py-1 text-xs text-apple-gray/60 italic">항목 없음</div>`;

            // 검색 중에는 접힌 카테고리도 결과를 보여준다
            const gOpen = !!term || !collapsedGroups.has(g.id);

            return `
            <div data-group="${escapeHtml(g.id)}">
                <div data-group-toggle="${escapeHtml(g.id)}" title="카테고리 ${gOpen ? '접기' : '펼치기'}"
                     class="group/hdr flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-apple-gray cursor-pointer select-none hover:bg-black/[0.04] dark:hover:bg-white/[0.06] transition">
                    <i data-lucide="${gOpen ? 'chevron-down' : 'chevron-right'}" class="w-3 h-3 shrink-0"></i>
                    <i data-lucide="${escapeAttr(g.icon || 'folder')}" class="w-3.5 h-3.5 ${st.icon}"></i>
                    <span class="flex-1 truncate text-[13px] font-semibold">${escapeHtml(g.name)}</span>
                    ${gOpen ? '' : `<span class="text-[11px] text-apple-gray/70">${items.length}</span>`}
                    <button data-add-spec="${escapeHtml(g.id)}" title="명세서 추가"
                            class="p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                        <i data-lucide="plus" class="w-3 h-3"></i>
                    </button>
                    <button data-rename-menu="${escapeHtml(g.id)}" title="카테고리 이름 변경"
                            class="p-0.5 rounded opacity-0 group-hover/hdr:opacity-100 hover:bg-black/10 dark:hover:bg-white/10 transition">
                        <i data-lucide="pencil" class="w-3 h-3"></i>
                    </button>
                    <button data-del-menu="${escapeHtml(g.id)}" title="카테고리 삭제"
                            class="p-0.5 rounded opacity-0 group-hover/hdr:opacity-100 hover:bg-red-500/10 hover:text-red-500 transition">
                        <i data-lucide="trash-2" class="w-3 h-3"></i>
                    </button>
                    <button data-reload="${escapeHtml(g.id)}" title="다시 불러오기"
                            class="p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                        <i data-lucide="refresh-cw" class="w-3 h-3"></i>
                    </button>
                </div>
                ${gOpen ? `<nav class="mt-0.5 space-y-0.5">${rows}</nav>` : ''}
            </div>`;
        }).join('');

        root.innerHTML = html || `<div class="px-2.5 py-1 text-xs text-apple-gray/60 italic">검색 결과 없음</div>`;
        icons();
    }

    /* ============================================================== *
     * ★ 사이드바 메뉴 편집 — 카테고리 / 명세서 추가·이름변경·삭제
     *   변경 내용은 DB(PLM_DOC_MENU)에 바로 저장됩니다. (api.js → specStore)
     * ============================================================== */

    /** 메뉴를 DB에서 다시 읽어 사이드바를 그린다 */
    async function reloadMenu() {
        await loadSpecs(true);
        renderSpecTree();
    }

    /* 코드(DB CATEGORY 키 = API 주소의 type 값)는 서버가 SPEC00001 부터 순번으로 발급한다.
       → DocMenuController.create() / DocMenuRepository.nextMenuCode() */

    /** 카테고리(대분류) 추가 */
    async function addGroup() {
        const name = prompt('새 카테고리 이름:\n(API 주소는 SPEC00001 형식으로 자동 부여됩니다)');
        if (name === null || !name.trim()) return;
        try {
            const r = await specStore.createGroup({ name: name.trim() });
            await reloadMenu();
            toast(`카테고리가 추가되었습니다${r?.menu?.id ? ` (${r.menu.id})` : ''}.`, 'folder-plus');
        } catch (err) {
            toast('추가 실패: ' + err.message, 'alert-circle');
        }
    }

    /** 명세서 추가 — parentId 를 주면 그 명세서의 하위로 만든다 */
    async function addSpec(groupId, parentId) {
        // 몇 단계든 들어갈 수 있으므로 "상위 / 상위 / 대상" 전체 경로를 보여 준다
        const where = parentId
            ? [...ancestorsOf(parentId).map(a => a.name), specs.find(s => s.id === parentId)?.name].join(' / ')
            : specGroups.find(g => g.id === groupId)?.name;

        const name = prompt(`"${where}" 에 추가할 ${parentId ? '하위 ' : ''}명세서 이름:\n`
            + '(API 주소는 SPEC00001 형식으로 자동 부여됩니다)');
        if (name === null || !name.trim()) return;

        try {
            const r = await specStore.createSpec({ name: name.trim(), groupId, parentId });
            await reloadMenu();
            const newId = r?.menu?.id;
            toast(`추가되었습니다${newId ? ` (${newId})` : ''}. 상단에서 API 주소를 복사할 수 있습니다.`, 'file-plus-2');
            if (newId) {
                if (parentId) expandedSpecs.add(parentId);
                openSpec(newId);
            }
        } catch (err) {
            toast('추가 실패: ' + err.message, 'alert-circle');
        }
    }

    /** 이름 / 아이콘 변경 — 카테고리·명세서 공통 */
    async function renameMenu(id) {
        const item = specGroups.find(g => g.id === id) || specs.find(s => s.id === id);
        if (!item) return;

        const name = prompt('이름 변경:', item.name);
        if (name === null || !name.trim()) return;
        const icon = prompt('아이콘 (lucide 이름 — 그대로 두려면 확인):', item.icon || '');
        if (icon === null) return;

        try {
            await specStore.renameMenu(id, { name: name.trim(), icon: icon.trim() });
            await reloadMenu();
            // 열려 있는 화면의 이름(브레드크럼)도 바로 갱신
            if (currentSpecId && (currentSpecId === id || groupOf(currentSpecId).id === id)) await openSpec(currentSpecId);
            toast('변경되었습니다.', 'pencil');
        } catch (err) {
            toast('변경 실패: ' + err.message, 'alert-circle');
        }
    }

    /** 카테고리 / 명세서 삭제 — 하위가 있으면 한 번 더 확인 */
    async function removeMenu(id) {
        const isGroup = specGroups.some(g => g.id === id);
        const label = (specGroups.find(g => g.id === id) || specs.find(s => s.id === id))?.name || id;

        // 하위(손자 이하 포함) 개수를 미리 알려 준다
        const kids = isGroup ? specs.filter(s => s.group === id).length : descendantCount(id);
        const msg = `"${label}" 을(를) 사이드바에서 삭제할까요?`
            + (kids ? `\n${isGroup ? '이 카테고리 안의' : '하위'} 항목 ${kids}개도 함께 삭제됩니다.` : '')
            + `\n\n작성된 본문은 DB에 남아 있어, 같은 코드로 다시 만들면 그대로 복구됩니다.`;
        if (!confirm(msg)) return;

        try {
            try {
                await specStore.deleteMenu(id, { force: kids > 0 });
            } catch (err) {
                const n = err.detail && err.detail.children;
                if (!n) throw err;
                // 화면 목록과 DB 가 다른 경우(다른 사람이 그 사이에 추가) 한 번 더 확인
                if (!confirm(`하위 항목 ${n}개가 있습니다. 함께 삭제할까요?`)) return;
                await specStore.deleteMenu(id, { force: true });
            }

            await reloadMenu();
            toast('삭제되었습니다.', 'trash-2');

            // 지금 보고 있던 명세서가 사라졌으면 다른 문서로 이동
            if (currentSpecId && !specs.some(s => s.id === currentSpecId)) {
                currentSpecId = null;
                if (specs[0]) openSpec(specs[0].id);
                else if (docs[0]) openDoc(docs[0].id);
                else showEmpty();
            }
        } catch (err) {
            toast('삭제 실패: ' + err.message, 'alert-circle');
        }
    }

    /* ============================================================== *
     * ★ 조회 API 카드 — 문서 제목 바로 아래에 표시
     *   주소는 DB(PLM_DOC_MENU.READ_URL) 값을 api.js 의 getApiLinks() 가 돌려줍니다.
     * ============================================================== */
    const METHOD_STYLES = {
        GET:    'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
        POST:   'bg-apple-blue/10 text-apple-blue',
        PUT:    'bg-amber-500/10 text-amber-600 dark:text-amber-400',
        PATCH:  'bg-violet-500/10 text-violet-600 dark:text-violet-400',
        DELETE: 'bg-red-500/10 text-red-600 dark:text-red-400',
    };

    /** 상대경로를 현재 서버 기준 전체 주소로 — 복사 시 바로 쓸 수 있게 */
    function absoluteUrl(u) {
        try { return new URL(u, location.href).href; } catch { return u; }
    }

    /** 클립보드 복사 (구형 브라우저는 textarea 방식으로 대체) */
    function copyText(text, msg = '복사했습니다.') {
        const fallback = () => {
            const ta = document.createElement('textarea');
            ta.value = text;
            ta.style.cssText = 'position:fixed;top:-9999px';
            document.body.appendChild(ta);
            ta.select();
            try { document.execCommand('copy'); toast(msg, 'copy'); }
            catch { toast('복사에 실패했습니다.', 'alert-circle'); }
            document.body.removeChild(ta);
        };
        if (navigator.clipboard && navigator.clipboard.writeText) {
            navigator.clipboard.writeText(text).then(() => toast(msg, 'copy')).catch(fallback);
        } else fallback();
    }

    /** 상단 카드 — 이 페이지의 조회 API 한 줄 */
    function apiLinkPanelHtml(links) {
        const l = links[0];
        const mStyle = METHOD_STYLES[l.method] || METHOD_STYLES.GET;
        const abs = absoluteUrl(l.url);

        return `
        <div id="apiLinkPanel"
             class="not-prose my-6 flex items-center gap-3 px-4 py-3 rounded-xl border border-apple-line dark:border-neutral-800 bg-[#fbfbfd] dark:bg-neutral-950">
            <span class="shrink-0 px-2 py-0.5 rounded-md text-[11px] font-semibold tracking-wide ${mStyle}">${escapeHtml(l.method)}</span>
            <div class="min-w-0 flex-1">
                <div class="text-[11px] font-semibold uppercase tracking-wider text-apple-gray">이 문서 조회 API</div>
                <div class="text-xs text-apple-gray break-all mt-0.5" style="font-family:'SF Mono',ui-monospace,monospace">${escapeHtml(l.url)}</div>
            </div>
            <button type="button" data-api-copy="${escapeHtml(abs)}" title="주소 복사"
                    class="shrink-0 h-7 w-7 flex items-center justify-center rounded-lg text-apple-gray hover:bg-black/[0.06] dark:hover:bg-white/[0.08] transition">
                <i data-lucide="copy" class="w-3.5 h-3.5"></i>
            </button>
            <a href="${escapeHtml(l.url)}" target="_blank" rel="noopener" title="새 탭에서 열기"
               class="shrink-0 h-7 w-7 flex items-center justify-center rounded-lg text-apple-gray hover:bg-black/[0.06] dark:hover:bg-white/[0.08] transition no-underline">
                <i data-lucide="external-link" class="w-3.5 h-3.5"></i>
            </a>
        </div>`;
    }

    /** 현재 명세서의 API 링크를 제목(H1) 아래에 끼워 넣는다 */
    async function renderApiLinks(specId) {
        let links = [];
        try { links = await specStore.getApiLinks(specId); } catch { links = []; }
        if (currentSpecId !== specId || !links.length) return;

        const viewer = $('#viewer');
        const wrap = document.createElement('div');
        wrap.innerHTML = apiLinkPanelHtml(links);
        const panel = wrap.firstElementChild;

        // 본문 제목(H1)이 있으면 그 바로 아래, 없으면 상단 메타 영역 아래에 배치
        const anchor = viewer.querySelector('h1') || viewer.firstElementChild;
        if (anchor) anchor.insertAdjacentElement('afterend', panel);
        else viewer.appendChild(panel);

        panel.addEventListener('click', (e) => {
            const btn = e.target.closest('[data-api-copy]');
            if (!btn) return;
            e.preventDefault();
            copyText(btn.dataset.apiCopy, 'API 주소를 복사했습니다.');
        });
        icons();
    }

    /** 명세서 본문을 DB에서 읽어 뷰어에 표시 */
    async function openSpec(id) {
        currentSpecId = id;
        currentDocId = null;
        editingId = null;

        showView('viewer');
        const meta = specs.find(s => s.id === id);
        const grp = groupOf(id);
        const st = styleOf(grp);
        const chain = ancestorsOf(id);
        chain.forEach(a => expandedSpecs.add(a.id));   // 상위 경로를 전부 펼쳐 둔다

        $('#breadcrumb').innerHTML = `
            <span class="inline-flex items-center gap-1.5">
                <i data-lucide="${escapeAttr(grp.icon || 'folder')}" class="w-3.5 h-3.5 ${st.icon}"></i>
                ${escapeHtml(grp.name)}
                <i data-lucide="chevron-right" class="w-3.5 h-3.5 opacity-50"></i>
                ${chain.map(a => `${escapeHtml(a.name)}<i data-lucide="chevron-right" class="w-3.5 h-3.5 opacity-50"></i>`).join('')}
                <span class="text-apple-ink dark:text-gray-200 font-medium">${escapeHtml(meta?.name || id)}</span>
            </span>`;
        $('#viewActions').innerHTML = '';
        $('#viewer').innerHTML = `<div class="text-sm text-apple-gray py-10">불러오는 중…</div>`;
        renderSpecTree();
        icons();

        let spec;
        try {
            spec = await specStore.getSpec(id);
        } catch (err) {
            $('#viewer').innerHTML = `
                <div class="py-10 text-center">
                    <p class="text-sm text-red-500 mb-3">명세서를 불러오지 못했습니다.</p>
                    <p class="text-xs text-apple-gray">${escapeHtml(err.message)}</p>
                </div>`;
            toast('명세서 로드 실패: ' + err.message, 'alert-circle');
            return;
        }
        if (currentSpecId !== id) return;   // 로딩 중 다른 문서로 이동한 경우

        // 저장 API가 없는 명세서는 읽기 전용 (api.js 에서 saveUrl 지정 시 편집 가능)
        const editBtn = spec.readOnly
            ? `<button id="editSpecBtn" disabled title="저장 API가 연결되지 않아 편집할 수 없습니다"
                       class="h-9 px-3.5 text-sm rounded-lg border border-apple-line dark:border-neutral-700 opacity-40 cursor-not-allowed flex items-center gap-1.5">
                   <i data-lucide="lock" class="w-4 h-4"></i> 편집
               </button>`
            : `<button id="editSpecBtn" class="h-9 px-3.5 text-sm rounded-lg border border-apple-line dark:border-neutral-700 hover:bg-black/[0.04] dark:hover:bg-white/[0.06] transition flex items-center gap-1.5">
                   <i data-lucide="pencil" class="w-4 h-4"></i> 편집
               </button>`;

        $('#viewActions').innerHTML = editBtn + `
            <button id="reloadSpecBtn" title="다시 불러오기" class="h-9 w-9 flex items-center justify-center text-sm rounded-lg border border-apple-line dark:border-neutral-700 hover:bg-black/[0.04] dark:hover:bg-white/[0.06] transition">
                <i data-lucide="refresh-cw" class="w-4 h-4"></i>
            </button>`;

        const badge = spec.live
            ? `<span class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 text-xs font-medium">DB 연결</span>`
            : `<span class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md bg-amber-500/10 text-amber-600 dark:text-amber-400 text-xs font-medium">목업</span>`;
        const roBadge = spec.readOnly
            ? `<span class="inline-flex items-center gap-1 text-xs"><i data-lucide="lock" class="w-3 h-3"></i> 읽기 전용</span>`
            : '';

        const head = `
            <div class="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-apple-gray mb-8 pb-6 border-b border-apple-line dark:border-neutral-800">
                <span class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md ${st.activeBg} ${st.activeText} text-xs font-medium">
                    <i data-lucide="${escapeAttr(grp.icon || 'folder')}" class="w-3 h-3"></i> ${escapeHtml(grp.name)}
                </span>
                ${badge}
                <span class="inline-flex items-center gap-1.5"><i data-lucide="user" class="w-4 h-4"></i> ${escapeHtml(spec.author || '미상')}</span>
                <span class="inline-flex items-center gap-1.5"><i data-lucide="clock" class="w-4 h-4"></i> ${formatDate(spec.updatedAt)} 수정</span>
                ${roBadge}
            </div>`;
        const body = spec.content
            ? renderMarkdown(spec.content)
            : `<p class="text-apple-gray">내용이 비어 있습니다. <b>편집</b> 버튼을 눌러 작성하세요.</p>`;

        $('#viewer').innerHTML = head + body;
        await renderApiLinks(id);            // 카테고리별 API 링크 (제목 아래)
        highlightIn($('#viewer'));
        $('#viewer').classList.remove('fade-up'); void $('#viewer').offsetWidth; $('#viewer').classList.add('fade-up');

        buildTOC();
        icons();
        $('#scrollArea').scrollTop = 0;

        if (!spec.readOnly) $('#editSpecBtn').onclick = () => startSpecEdit(id);
        $('#reloadSpecBtn').onclick = () => openSpec(id);
    }

    /** 명세서 편집 시작 — 기존 에디터 화면을 재사용 */
    async function startSpecEdit(id) {
        let spec;
        try {
            spec = await specStore.getSpec(id);
        } catch (err) {
            toast('명세서 로드 실패: ' + err.message, 'alert-circle');
            return;
        }

        currentSpecId = id;
        editingId = null;
        setEditorMode('spec');
        showView('editor');

        $('#editTitle').value = spec.name;
        $('#editAuthor').value = spec.author || '';
        $('#editContent').value = spec.content || '';
        updatePreview();

        const grp = groupOf(id);
        const path = ancestorsOf(id).map(a => escapeHtml(a.name) + ' / ').join('');
        $('#breadcrumb').innerHTML = `<span class="inline-flex items-center gap-1.5"><i data-lucide="${escapeAttr(grp.icon || 'folder')}" class="w-3.5 h-3.5 ${styleOf(grp).icon}"></i> ${escapeHtml(grp.name)} 편집 — ${path}${escapeHtml(spec.name)}</span>`;
        $('#viewActions').innerHTML = '';
        icons();
        $('#editContent').focus();
    }

    /** 명세서 저장 → DB(PUT) */
    async function saveSpec() {
        const id = currentSpecId;
        if (!id) return;
        const content = $('#editContent').value;
        const author = $('#editAuthor').value.trim() || '미상';

        const btn = $('#saveDoc');
        btn.disabled = true;
        try {
            await specStore.saveSpec(id, { content, author });
            toast('명세서가 저장되었습니다.');
            await openSpec(id);
        } catch (err) {
            toast('저장 실패: ' + err.message, 'alert-circle');
        } finally {
            btn.disabled = false;
        }
    }

    /** 에디터 모드 전환 (명세서는 제목/카테고리를 바꾸지 않음) */
    function setEditorMode(mode) {
        editorMode = mode;
        const isSpec = mode === 'spec';
        $('#editCategory').classList.toggle('hidden', isSpec);
        $('#editTitle').readOnly = isSpec;
        $('#editTitle').classList.toggle('text-apple-gray', isSpec);
    }

    /** 저장 버튼 / Ctrl+S 공통 진입점 */
    function handleSave() {
        return editorMode === 'spec' ? saveSpec() : saveDoc();
    }

    /** 취소 버튼 공통 진입점 */
    function handleCancel() {
        if (editorMode === 'spec' && currentSpecId) return openSpec(currentSpecId);
        if (currentDocId) return openDoc(currentDocId);
        showEmpty();
    }

    /* --------------------- 사이드바 트리 렌더 --------------------- */
    function renderTree() {
        const tree = $('#docTree');
        const term = searchTerm.trim().toLowerCase();

        const matches = (d) => !term ||
            d.title.toLowerCase().includes(term) ||
            (d.content || '').toLowerCase().includes(term);

        let html = '';
        for (const cat of categories) {
            const catDocs = docs.filter(d => d.categoryId === cat.id && matches(d));
            if (term && catDocs.length === 0) continue;

            // 검색 중에는 접힌 카테고리도 결과를 보여준다
            const catOpen = !!term || !collapsedCats.has(cat.id);

            html += `
            <div class="mb-1" data-cat="${cat.id}">
                <div data-cat-toggle="${cat.id}" title="카테고리 ${catOpen ? '접기' : '펼치기'}"
                     class="group flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-semibold uppercase tracking-wide text-apple-gray cursor-pointer select-none hover:bg-black/[0.04] dark:hover:bg-white/[0.06] transition">
                    <i data-lucide="${catOpen ? 'chevron-down' : 'chevron-right'}" class="w-3 h-3 shrink-0"></i>
                    <i data-lucide="${escapeAttr(cat.icon || 'folder')}" class="w-3.5 h-3.5"></i>
                    <span class="flex-1 truncate normal-case tracking-normal text-[13px]">${escapeHtml(cat.name)}</span>
                    ${catOpen ? '' : `<span class="text-[11px] normal-case tracking-normal text-apple-gray/70">${catDocs.length}</span>`}
                    <button data-delcat="${cat.id}" title="카테고리 삭제" class="opacity-0 group-hover:opacity-100 p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                        <i data-lucide="trash-2" class="w-3 h-3"></i>
                    </button>
                </div>
                <div class="mt-0.5 space-y-0.5 ${catOpen ? '' : 'hidden'}">`;

            if (catDocs.length === 0) {
                html += `<div class="pl-8 pr-2 py-1 text-xs text-apple-gray/60 italic">문서 없음</div>`;
            }
            for (const d of catDocs) {
                const active = d.id === currentDocId;
                html += `
                    <a href="#" data-doc="${d.id}"
                       class="flex items-center gap-2 pl-8 pr-2 py-1.5 rounded-lg text-[13.5px] transition ${
                        active ? 'bg-apple-blue/10 text-apple-blue font-medium' : 'text-apple-ink dark:text-gray-300 hover:bg-black/[0.04] dark:hover:bg-white/[0.06]'}">
                        <span class="truncate">${escapeHtml(d.title)}</span>
                    </a>`;
            }
            html += `</div></div>`;
        }

        if (!html) {
            html = `<div class="px-4 py-8 text-center text-sm text-apple-gray">${term ? '검색 결과가 없습니다.' : '문서가 없습니다.'}</div>`;
        }
        tree.innerHTML = html;
        icons();
    }

    /* --------------------- 문서 뷰어 --------------------- */
    async function openDoc(id) {
        const doc = await store.getDoc(id);
        if (!doc) return;
        currentDocId = id;
        currentSpecId = null;
        editingId = null;

        showView('viewer');
        const cat = categories.find(c => c.id === doc.categoryId);

        // 브레드크럼
        $('#breadcrumb').innerHTML = `
            <span class="inline-flex items-center gap-1.5">
                <i data-lucide="${escapeAttr(cat?.icon || 'folder')}" class="w-3.5 h-3.5"></i>
                ${escapeHtml(cat?.name || '미분류')}
                <i data-lucide="chevron-right" class="w-3.5 h-3.5 opacity-50"></i>
                <span class="text-apple-ink dark:text-gray-200 font-medium">${escapeHtml(doc.title)}</span>
            </span>`;

        // 액션 버튼
        $('#viewActions').innerHTML = `
            <button id="editDocBtn" class="h-9 px-3.5 text-sm rounded-lg border border-apple-line dark:border-neutral-700 hover:bg-black/[0.04] dark:hover:bg-white/[0.06] transition flex items-center gap-1.5">
                <i data-lucide="pencil" class="w-4 h-4"></i> 편집
            </button>
            <button id="delDocBtn" class="h-9 w-9 flex items-center justify-center text-sm rounded-lg border border-apple-line dark:border-neutral-700 hover:bg-red-50 hover:border-red-200 hover:text-red-600 dark:hover:bg-red-950/40 transition">
                <i data-lucide="trash-2" class="w-4 h-4"></i>
            </button>`;

        // 본문
        const meta = `
            <div class="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-apple-gray mb-8 pb-6 border-b border-apple-line dark:border-neutral-800">
                <span class="inline-flex items-center gap-1.5"><i data-lucide="user" class="w-4 h-4"></i> ${escapeHtml(doc.author || '미상')}</span>
                <span class="inline-flex items-center gap-1.5"><i data-lucide="clock" class="w-4 h-4"></i> ${formatDate(doc.updatedAt)} 수정</span>
            </div>`;
        $('#viewer').innerHTML = meta + renderMarkdown(doc.content);
        highlightIn($('#viewer'));
        $('#viewer').classList.remove('fade-up'); void $('#viewer').offsetWidth; $('#viewer').classList.add('fade-up');

        buildTOC();
        icons();
        renderTree();
        renderSpecTree();
        $('#scrollArea').scrollTop = 0;

        $('#editDocBtn').onclick = () => startEdit(doc.id);
        $('#delDocBtn').onclick = () => deleteDoc(doc.id);
    }

    /* --------------------- 목차(TOC) --------------------- */
    function buildTOC() {
        const heads = $$('#viewer h1, #viewer h2, #viewer h3');
        const toc = $('#toc');
        if (heads.length < 2) { $('#tocPanel').classList.add('hidden'); toc.innerHTML = ''; return; }
        $('#tocPanel').classList.remove('hidden');
        toc.innerHTML = heads.map((h, i) => {
            const id = 'h-' + i;
            h.id = id;
            const pad = h.tagName === 'H1' ? 'pl-3' : h.tagName === 'H2' ? 'pl-3' : 'pl-6';
            return `<li><a href="#${id}" data-toc="${id}" class="block ${pad} -ml-px border-l border-transparent hover:border-apple-blue hover:text-apple-blue text-apple-gray transition py-0.5 leading-snug">${escapeHtml(h.textContent)}</a></li>`;
        }).join('');
        toc.onclick = (e) => {
            const a = e.target.closest('[data-toc]');
            if (!a) return;
            e.preventDefault();
            $('#' + a.dataset.toc)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
        };
    }

    /* --------------------- 편집기 --------------------- */
    function fillCategorySelect(selectedId) {
        $('#editCategory').innerHTML = categories.map(c =>
            `<option value="${c.id}" ${c.id === selectedId ? 'selected' : ''}>${escapeHtml(c.name)}</option>`
        ).join('');
    }

    async function startEdit(id) {
        editingId = id;
        const doc = id ? await store.getDoc(id) : null;
        setEditorMode('doc');
        showView('editor');

        $('#editTitle').value = doc?.title || '';
        fillCategorySelect(doc?.categoryId || categories[0]?.id);
        $('#editAuthor').value = doc?.author || '';
        $('#editContent').value = doc?.content || '';
        updatePreview();

        $('#breadcrumb').innerHTML = `<span class="inline-flex items-center gap-1.5"><i data-lucide="${id ? 'pencil' : 'file-plus'}" class="w-3.5 h-3.5"></i> ${id ? '문서 편집' : '새 문서 작성'}</span>`;
        $('#viewActions').innerHTML = '';
        icons();
        $('#editTitle').focus();
    }

    function updatePreview() {
        $('#editPreview').innerHTML = renderMarkdown($('#editContent').value);
        highlightIn($('#editPreview'));
    }

    async function saveDoc() {
        const title = $('#editTitle').value.trim();
        const categoryId = $('#editCategory').value;
        const author = $('#editAuthor').value.trim() || '미상';
        const content = $('#editContent').value;

        if (!title) { toast('제목을 입력하세요.', 'alert-circle'); $('#editTitle').focus(); return; }
        if (!categoryId) { toast('먼저 카테고리를 만들어 주세요.', 'alert-circle'); return; }

        let saved;
        if (editingId) saved = await store.updateDoc(editingId, { title, categoryId, author, content });
        else saved = await store.createDoc({ title, categoryId, author, content });

        await loadData();
        toast('저장되었습니다.');
        openDoc(saved.id);
    }

    async function deleteDoc(id) {
        const doc = await store.getDoc(id);
        if (!confirm(`"${doc?.title}" 문서를 삭제할까요?`)) return;
        await store.deleteDoc(id);
        await loadData();
        currentDocId = null;
        toast('삭제되었습니다.', 'trash-2');
        const first = docs[0];
        if (first) openDoc(first.id); else showEmpty();
    }

    /* --------------------- 카테고리 --------------------- */
    async function newCategory() {
        const name = prompt('새 카테고리 이름:');
        if (!name || !name.trim()) return;
        await store.createCategory({ name: name.trim() });
        await loadData();
        renderTree();
        toast('카테고리가 추가되었습니다.', 'folder-plus');
    }
    async function deleteCategory(id) {
        const cat = categories.find(c => c.id === id);
        const n = docs.filter(d => d.categoryId === id).length;
        if (!confirm(`"${cat?.name}" 카테고리${n ? ` 및 하위 문서 ${n}개` : ''}를 삭제할까요?`)) return;
        await store.deleteCategory(id);
        await loadData();
        if (!docs.find(d => d.id === currentDocId)) { currentDocId = null; showEmpty(); }
        renderTree();
        toast('삭제되었습니다.', 'trash-2');
    }

    /* --------------------- 백업 (내보내기/가져오기) --------------------- */
    async function exportJSON() {
        const data = await store.exportAll();
        const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `teamdocs-${new Date().toISOString().slice(0, 10)}.json`;
        a.click();
        URL.revokeObjectURL(a.href);
        toast('JSON으로 내보냈습니다.', 'download');
    }
    function importJSON() { $('#importFile').click(); }
    async function handleImport(e) {
        const file = e.target.files[0];
        if (!file) return;
        try {
            const data = JSON.parse(await file.text());
            if (!data.categories || !data.docs) throw new Error('형식 오류');
            if (!confirm('현재 데이터를 가져온 파일로 덮어쓸까요?')) return;
            await store.importAll(data);
            await loadData();
            currentDocId = null;
            renderTree();
            showEmpty();
            toast('가져오기 완료.', 'upload');
        } catch (err) {
            toast('가져오기 실패: ' + err.message, 'alert-circle');
        } finally { e.target.value = ''; }
    }

    /* --------------------- 뷰 전환 --------------------- */
    function showView(which) {
        $('#viewerRoot').classList.toggle('hidden', which !== 'viewer');
        $('#editorRoot').classList.toggle('hidden', which !== 'editor');
        $('#emptyState').classList.add('hidden');
        $('#emptyState').classList.toggle('flex', false);
    }
    function showEmpty() {
        $('#viewerRoot').classList.add('hidden');
        $('#editorRoot').classList.add('hidden');
        $('#emptyState').classList.remove('hidden');
        $('#emptyState').classList.add('flex');
        $('#breadcrumb').innerHTML = '';
        $('#viewActions').innerHTML = '';
    }

    /* --------------------- 테마 --------------------- */
    function initTheme() {
        const saved = localStorage.getItem('teamdocs.theme');
        const dark = saved ? saved === 'dark' : window.matchMedia('(prefers-color-scheme: dark)').matches;
        document.documentElement.classList.toggle('dark', dark);
        updateThemeIcon(dark);
    }
    function toggleTheme() {
        const dark = !document.documentElement.classList.contains('dark');
        document.documentElement.classList.toggle('dark', dark);
        localStorage.setItem('teamdocs.theme', dark ? 'dark' : 'light');
        updateThemeIcon(dark);
    }
    function updateThemeIcon(dark) {
        $('#themeBtn').innerHTML = `<i data-lucide="${dark ? 'sun' : 'moon'}" class="w-4 h-4"></i>`;
        icons();
    }

    /* --------------------- 유틸 --------------------- */
    function escapeHtml(s = '') { return s.replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c])); }
    function escapeAttr(s = '') { return String(s).replace(/[^a-z0-9-]/gi, ''); }
    function formatDate(iso) {
        if (!iso) return '';
        const d = new Date(iso);
        return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`;
    }

    /* --------------------- 이벤트 바인딩 --------------------- */
    function bindEvents() {
        $('#docTree').addEventListener('click', (e) => {
            const del = e.target.closest('[data-delcat]');
            if (del) { e.preventDefault(); deleteCategory(del.dataset.delcat); return; }
            // 카테고리 제목을 누르면 그 안의 문서 목록을 접거나 펼친다
            const catToggle = e.target.closest('[data-cat-toggle]');
            if (catToggle) {
                e.preventDefault();
                const cid = catToggle.dataset.catToggle;
                if (collapsedCats.has(cid)) collapsedCats.delete(cid); else collapsedCats.add(cid);
                renderTree();
                return;
            }
            const link = e.target.closest('[data-doc]');
            if (link) { e.preventDefault(); openDoc(link.dataset.doc); }
        });

        // ★ 카테고리별 명세서 클릭 / 새로고침 (동적 렌더링이므로 이벤트 위임)
        $('#specGroups').addEventListener('click', async (e) => {
            /* --- 메뉴 편집 버튼은 접기/펼치기보다 먼저 처리한다 --- */
            const add = e.target.closest('[data-add-spec]');
            if (add) {
                e.preventDefault(); e.stopPropagation();
                const target = add.dataset.addSpec;
                // 명세서 행의 + 버튼이면 하위 명세서, 카테고리 헤더의 + 면 그 카테고리에 추가
                if (add.dataset.asChild) addSpec(groupOf(target).id, target);
                else addSpec(target, null);
                return;
            }
            const rename = e.target.closest('[data-rename-menu]');
            if (rename) {
                e.preventDefault(); e.stopPropagation();
                renameMenu(rename.dataset.renameMenu);
                return;
            }
            const del = e.target.closest('[data-del-menu]');
            if (del) {
                e.preventDefault(); e.stopPropagation();
                removeMenu(del.dataset.delMenu);
                return;
            }

            // 하위 명세서 접기/펼치기 — 본문은 열지 않는다
            const toggle = e.target.closest('[data-toggle]');
            if (toggle) {
                e.preventDefault();
                const pid = toggle.dataset.toggle;
                if (expandedSpecs.has(pid)) expandedSpecs.delete(pid); else expandedSpecs.add(pid);
                renderSpecTree();
                return;
            }
            const reload = e.target.closest('[data-reload]');
            if (reload) {
                e.preventDefault();
                try {
                    await loadSpecs(true);
                    renderSpecTree();
                    // 현재 열려 있는 문서가 이 카테고리 소속이면 본문도 다시 읽는다
                    if (currentSpecId && groupOf(currentSpecId).id === reload.dataset.reload) {
                        await openSpec(currentSpecId);
                    }
                    toast('다시 불러왔습니다.', 'refresh-cw');
                } catch (err) {
                    toast('불러오기 실패: ' + err.message, 'alert-circle');
                }
                return;
            }
            const groupToggle = e.target.closest('[data-group-toggle]');
            if (groupToggle) {
                e.preventDefault();
                const gid = groupToggle.dataset.groupToggle;
                if (collapsedGroups.has(gid)) collapsedGroups.delete(gid); else collapsedGroups.add(gid);
                renderSpecTree();
                return;
            }
            const link = e.target.closest('[data-spec]');
            if (link) {
                e.preventDefault();
                const sid = link.dataset.spec;
                // 하위가 있는 항목은 본문을 열지 않고 접기/펼치기만 한다
                if (childrenOf(sid).length) {
                    if (expandedSpecs.has(sid)) expandedSpecs.delete(sid); else expandedSpecs.add(sid);
                    renderSpecTree();
                    return;
                }
                openSpec(sid);
            }
        });

        $('#searchInput').addEventListener('input', (e) => { searchTerm = e.target.value; renderTree(); renderSpecTree(); });
        $('#newDocBtn').addEventListener('click', () => {
            if (!categories.length) { toast('먼저 카테고리를 만들어 주세요.', 'alert-circle'); return; }
            startEdit(null);
        });
        $('#newCatBtn').addEventListener('click', newCategory);
        $('#newSpecGroupBtn')?.addEventListener('click', addGroup);   // ★ 명세서 카테고리 추가(DB)
        $('#saveDoc').addEventListener('click', handleSave);
        $('#cancelEdit').addEventListener('click', handleCancel);
        $('#editContent').addEventListener('input', updatePreview);
        $('#exportBtn').addEventListener('click', exportJSON);
        $('#importBtn').addEventListener('click', importJSON);
        $('#importFile').addEventListener('change', handleImport);
        $('#themeBtn').addEventListener('click', toggleTheme);

        // 모바일 사이드바 토글
        $('#toggleSidebar').addEventListener('click', () => {
            $('#sidebar').classList.toggle('hidden');
        });

        // 단축키: Ctrl/Cmd+S 저장
        document.addEventListener('keydown', (e) => {
            if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 's') {
                if (!$('#editorRoot').classList.contains('hidden')) { e.preventDefault(); handleSave(); }
            }
        });
    }

    /* --------------------- 시작 --------------------- */
    async function init() {
        initTheme();
        bindEvents();
        try {
            await loadData();
        } catch (err) {
            toast('데이터 로드 실패: ' + err.message, 'alert-circle');
            console.error(err);
        }
        try {
            await loadSpecs();                       // ★ DB 명세서 목록 (PLM_DOC_MENU)
            if (!specStore.menuLive) {
                toast('메뉴를 DB에서 불러오지 못해 기본 목록으로 표시합니다.', 'alert-circle');
                console.warn('[TeamDocs] 메뉴 로드 실패 사유:', specStore.menuError);
            }
        } catch (err) {
            toast('명세서 목록 로드 실패: ' + err.message, 'alert-circle');
            console.error(err);
        }
        renderTree();
        renderSpecTree();

        // 시작 화면: DB 명세서 첫 항목 → 없으면 일반 문서 → 없으면 빈 화면
        if (specs[0]) openSpec(specs[0].id);
        else if (docs[0]) openDoc(docs[0].id);
        else showEmpty();
        icons();
    }

    document.addEventListener('DOMContentLoaded', init);
})();
