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
    async function loadSpecs() {
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
    /** 하위 명세서 목록 (parent 로 연결된 항목들) */
    const childrenOf = (specId) => specs.filter(s => s.parent === specId);
    /** 상위 명세서 — 최상위 항목이면 null */
    const parentOf = (specId) => {
        const s = specs.find(x => x.id === specId);
        return (s && s.parent && specs.find(x => x.id === s.parent)) || null;
    };

    /** 명세서 한 줄 렌더 — 하위 명세서가 있으면 접기/펼치기 버튼을 붙인다 */
    function renderSpecRow(s, st, term) {
        const active = s.id === currentSpecId;
        const selfMatch = !term || s.name.toLowerCase().includes(term);
        // 상위 이름이 검색어와 맞으면 하위 전체를, 아니면 검색어에 맞는 하위만 보여준다
        const kids = childrenOf(s.id)
            .filter(k => selfMatch || k.name.toLowerCase().includes(term));
        const open = kids.length > 0 &&
            (!!term || expandedSpecs.has(s.id) || kids.some(k => k.id === currentSpecId));

        const toggle = kids.length
            ? `<button data-toggle="${escapeHtml(s.id)}" title="하위 명세서 ${open ? '접기' : '펼치기'}"
                       class="shrink-0 -ml-3 p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                   <i data-lucide="${open ? 'chevron-down' : 'chevron-right'}" class="w-3 h-3"></i>
               </button>`
            : '';

        const row = `
            <a href="#" data-spec="${escapeHtml(s.id)}"
               class="flex items-center gap-2 ${s.parent ? 'pl-9' : 'pl-4'} pr-2 py-1.5 rounded-lg text-[13.5px] transition ${
                active ? `${st.activeBg} ${st.activeText} font-medium`
                       : 'text-apple-ink dark:text-gray-300 hover:bg-black/[0.04] dark:hover:bg-white/[0.06]'}">
                ${toggle}
                <i data-lucide="${escapeAttr(s.icon || 'file-text')}" class="w-3.5 h-3.5 shrink-0 ${active ? '' : st.icon}"></i>
                <span class="truncate flex-1">${escapeHtml(s.name)}</span>
                ${s.live ? `<span title="실제 DB API 연결됨" class="w-1.5 h-1.5 rounded-full bg-emerald-500 shrink-0"></span>` : ''}
            </a>`;

        return row + (open ? kids.map(k => renderSpecRow(k, st, term)).join('') : '');
    }

    /** 카테고리(DB 명세서 / API 정의서 / 기타 규칙)별로 사이드바를 렌더링 */
    function renderSpecTree() {
        const root = $('#specGroups');
        const term = searchTerm.trim().toLowerCase();

        if (!specs.length) {
            root.innerHTML = `<div class="px-2.5 py-1 text-xs text-red-500">명세서를 불러오지 못했습니다.</div>`;
            return;
        }

        // 검색 중에는 자기 이름이 맞거나, 하위 명세서가 맞는 항목만 남긴다
        const matches = (s) => !term || s.name.toLowerCase().includes(term)
            || childrenOf(s.id).some(k => k.name.toLowerCase().includes(term));

        const html = specGroups.map(g => {
            const st = styleOf(g);
            const items = specs.filter(s => s.group === g.id && !s.parent && matches(s));
            if (term && !items.length) return '';   // 검색 중이면 빈 카테고리는 숨김

            const rows = items.length
                ? items.map(s => renderSpecRow(s, st, term)).join('')
                : `<div class="pl-4 pr-2 py-1 text-xs text-apple-gray/60 italic">항목 없음</div>`;

            return `
            <div data-group="${escapeHtml(g.id)}">
                <div class="flex items-center gap-1.5 px-2.5 py-1.5 text-apple-gray">
                    <i data-lucide="${escapeAttr(g.icon || 'folder')}" class="w-3.5 h-3.5 ${st.icon}"></i>
                    <span class="flex-1 truncate text-[13px] font-semibold">${escapeHtml(g.name)}</span>
                    <button data-reload="${escapeHtml(g.id)}" title="다시 불러오기"
                            class="p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                        <i data-lucide="refresh-cw" class="w-3 h-3"></i>
                    </button>
                </div>
                <nav class="mt-0.5 space-y-0.5">${rows}</nav>
            </div>`;
        }).join('');

        root.innerHTML = html || `<div class="px-2.5 py-1 text-xs text-apple-gray/60 italic">검색 결과 없음</div>`;
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
        const par = parentOf(id);
        if (par) expandedSpecs.add(par.id);   // 하위 명세서를 열면 상위를 펼쳐 둔다

        $('#breadcrumb').innerHTML = `
            <span class="inline-flex items-center gap-1.5">
                <i data-lucide="${escapeAttr(grp.icon || 'folder')}" class="w-3.5 h-3.5 ${st.icon}"></i>
                ${escapeHtml(grp.name)}
                <i data-lucide="chevron-right" class="w-3.5 h-3.5 opacity-50"></i>
                ${par ? `${escapeHtml(par.name)}<i data-lucide="chevron-right" class="w-3.5 h-3.5 opacity-50"></i>` : ''}
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
        const par = parentOf(id);
        $('#breadcrumb').innerHTML = `<span class="inline-flex items-center gap-1.5"><i data-lucide="${escapeAttr(grp.icon || 'folder')}" class="w-3.5 h-3.5 ${styleOf(grp).icon}"></i> ${escapeHtml(grp.name)} 편집 — ${par ? escapeHtml(par.name) + ' / ' : ''}${escapeHtml(spec.name)}</span>`;
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

            html += `
            <div class="mb-1" data-cat="${cat.id}">
                <div class="group flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-semibold uppercase tracking-wide text-apple-gray">
                    <i data-lucide="${escapeAttr(cat.icon || 'folder')}" class="w-3.5 h-3.5"></i>
                    <span class="flex-1 truncate normal-case tracking-normal text-[13px]">${escapeHtml(cat.name)}</span>
                    <button data-delcat="${cat.id}" title="카테고리 삭제" class="opacity-0 group-hover:opacity-100 p-0.5 rounded hover:bg-black/10 dark:hover:bg-white/10 transition">
                        <i data-lucide="trash-2" class="w-3 h-3"></i>
                    </button>
                </div>
                <div class="mt-0.5 space-y-0.5">`;

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
            const link = e.target.closest('[data-doc]');
            if (link) { e.preventDefault(); openDoc(link.dataset.doc); }
        });

        // ★ 카테고리별 명세서 클릭 / 새로고침 (동적 렌더링이므로 이벤트 위임)
        $('#specGroups').addEventListener('click', async (e) => {
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
                    await loadSpecs();
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
            const link = e.target.closest('[data-spec]');
            if (link) { e.preventDefault(); openSpec(link.dataset.spec); }
        });

        $('#searchInput').addEventListener('input', (e) => { searchTerm = e.target.value; renderTree(); renderSpecTree(); });
        $('#newDocBtn').addEventListener('click', () => {
            if (!categories.length) { toast('먼저 카테고리를 만들어 주세요.', 'alert-circle'); return; }
            startEdit(null);
        });
        $('#newCatBtn').addEventListener('click', newCategory);
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
            await loadSpecs();                       // ★ DB 명세서 목록
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
