/*
 * 용어 툴팁
 * - balance-terms.js 의 window.BALANCE_TERMS 를 읽어서 화면의 용어에 점선 밑줄을 달고,
 *   마우스를 올리거나(데스크톱) 탭하거나(모바일) 키보드 Tab으로 이동하면 설명을 보여줍니다.
 * - HTML 본문은 고칠 필요가 없습니다. 계산 결과가 다시 그려져도 자동으로 다시 표시합니다.
 * - 툴팁을 달 영역은 아래 SCOPES 로 정합니다.
 */
(function () {
  var TERMS = window.BALANCE_TERMS || {};

  // 툴팁을 달 화면 영역 (수식 .f, 숫자 .mono, 입력칸은 자동 제외)
  var SCOPES = [
    '.field label', 'table.res td:first-child', '.kpi .l', '.group-title',
    '.sec-head h2', '.sec-head p', '.verdict small', '.note', '.findings li > div', '.fig h3', '.fig .cap'
  ].join(',');
  var SKIP = '.f, .mono, .num, .tt-term, input, select, textarea, script, style, svg, button';

  // 단어 → key, 기호 → key
  var wordMap = {}, symMap = {}, words = [];
  Object.keys(TERMS).forEach(function (k) {
    (TERMS[k].aliases || []).forEach(function (a) { if (!wordMap[a]) { wordMap[a] = k; words.push(a); } });
    (TERMS[k].symbols || []).forEach(function (s) { symMap[s] = k; });
  });
  words.sort(function (a, b) { return b.length - a.length; });
  function esc(s) { return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); }
  var re = words.length ? new RegExp(words.map(esc).join('|'), 'g') : null;

  /* ---------- 스타일 (페이지의 색 토큰을 그대로 사용해 라이트·다크 모두 맞춤) ---------- */
  var css = document.createElement('style');
  css.textContent =
    '.tt-term{text-decoration:underline dotted;text-decoration-color:var(--accent,#1d5a86);text-underline-offset:3px;text-decoration-thickness:1.5px;cursor:help;border-radius:2px}' +
    '.tt-term:hover,.tt-term:focus-visible,.tt-term[aria-expanded="true"]{background:var(--accent-soft,#e3edf5);outline:none}' +
    '.sym.tt-term{text-decoration-style:dotted}' +
    '#tt-box{position:fixed;z-index:1000;max-width:min(340px,calc(100vw - 24px));background:var(--panel,#fff);color:var(--ink,#18222c);' +
      'border:1px solid var(--line,#d6dce2);border-radius:8px;box-shadow:0 8px 28px rgba(0,0,0,.18);padding:12px 14px;' +
      'font:13px/1.55 var(--f-body,system-ui,sans-serif);pointer-events:none;opacity:0;transform:translateY(4px);transition:opacity .12s,transform .12s}' +
    '#tt-box.show{opacity:1;transform:none}' +
    '#tt-box .tt-t{font-weight:700;font-size:13.5px;margin-bottom:4px}' +
    '#tt-box .tt-d{color:var(--ink,#18222c)}' +
    '#tt-box .tt-f{margin-top:8px;font-family:var(--f-mono,monospace);font-size:12px;background:var(--bg,#f3f5f7);padding:6px 8px;border-radius:5px;word-break:break-word}' +
    '#tt-box .tt-m{margin-top:8px;display:flex;flex-wrap:wrap;gap:4px 12px;font-size:11.5px;color:var(--muted,#5b6875)}' +
    '#tt-box .tt-m b{font-weight:600;color:var(--ink,#18222c)}' +
    '@media (prefers-reduced-motion:reduce){#tt-box{transition:none}}';
  document.head.appendChild(css);

  /* ---------- 용어 표시 ---------- */
  function mark(el, key) {
    el.classList.add('tt-term');
    el.setAttribute('data-term', key);
    el.setAttribute('tabindex', '0');
    el.setAttribute('aria-describedby', 'tt-box');
  }

  function annotate(root) {
    // 1) 입력칸 기호(.sym)는 글자가 정확히 같을 때만 연결
    root.querySelectorAll('.field label .sym:not(.tt-term)').forEach(function (s) {
      var k = symMap[s.textContent.trim()];
      if (k) mark(s, k);
    });
    // 2) 지정 영역의 글자에서 용어 찾기 (영역마다 같은 용어는 한 번만)
    if (!re) return;
    root.querySelectorAll(SCOPES).forEach(function (scope) {
      if (scope.dataset.ttDone === scope.textContent) return;
      var seen = {};
      scope.querySelectorAll('.tt-term').forEach(function (t) { seen[t.dataset.term] = 1; });
      var walker = document.createTreeWalker(scope, NodeFilter.SHOW_TEXT, {
        acceptNode: function (n) {
          return n.parentElement && n.parentElement.closest(SKIP) ? NodeFilter.FILTER_REJECT : NodeFilter.FILTER_ACCEPT;
        }
      });
      var nodes = [], n;
      while ((n = walker.nextNode())) nodes.push(n);
      nodes.forEach(function (node) {
        var text = node.nodeValue, last = 0, frag = null, m;
        re.lastIndex = 0;
        while ((m = re.exec(text))) {
          var key = wordMap[m[0]];
          if (seen[key]) continue;
          seen[key] = 1;
          frag = frag || document.createDocumentFragment();
          frag.appendChild(document.createTextNode(text.slice(last, m.index)));
          var span = document.createElement('span');
          span.textContent = m[0];
          mark(span, key);
          frag.appendChild(span);
          last = m.index + m[0].length;
        }
        if (frag) {
          frag.appendChild(document.createTextNode(text.slice(last)));
          node.parentNode.replaceChild(frag, node);
        }
      });
      scope.dataset.ttDone = scope.textContent;
    });
  }

  /* ---------- 툴팁 상자 ---------- */
  var box = document.createElement('div');
  box.id = 'tt-box'; box.setAttribute('role', 'tooltip');
  document.body.appendChild(box);
  var current = null;

  function html(s) { return String(s).replace(/[&<>"]/g, function (c) { return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]; }); }

  function show(el) {
    var t = TERMS[el.dataset.term];
    if (!t) return;
    if (current && current !== el) current.removeAttribute('aria-expanded');
    current = el; el.setAttribute('aria-expanded', 'true');
    var meta = [];
    if (t.cell) meta.push('<span>엑셀 <b>' + html(t.cell) + '</b></span>');
    if (t.unit) meta.push('<span>단위 <b>' + html(t.unit) + '</b></span>');
    box.innerHTML = '<div class="tt-t">' + html(t.title) + '</div><div class="tt-d">' + html(t.desc) + '</div>' +
      (t.formula ? '<div class="tt-f">' + html(t.formula) + '</div>' : '') +
      (meta.length ? '<div class="tt-m">' + meta.join('') + '</div>' : '');
    box.style.left = '0px'; box.style.top = '0px';
    box.classList.add('show');
    var r = el.getBoundingClientRect(), b = box.getBoundingClientRect(), gap = 8;
    var left = Math.min(Math.max(12, r.left), window.innerWidth - b.width - 12);
    var top = r.bottom + gap;
    if (top + b.height > window.innerHeight - 12) top = Math.max(12, r.top - b.height - gap);
    box.style.left = left + 'px'; box.style.top = top + 'px';
  }
  function hide() {
    box.classList.remove('show');
    if (current) current.removeAttribute('aria-expanded');
    current = null;
  }

  document.addEventListener('mouseover', function (e) {
    var t = e.target.closest && e.target.closest('.tt-term');
    if (t) show(t); else if (current) hide();
  });
  document.addEventListener('focusin', function (e) {
    var t = e.target.closest && e.target.closest('.tt-term');
    if (t) show(t);
  });
  document.addEventListener('focusout', function (e) {
    if (e.target.closest && e.target.closest('.tt-term')) hide();
  });
  // 터치: 탭하면 열고, 다른 곳을 탭하면 닫음 (label 안 용어를 눌러도 입력칸으로 포커스가 넘어가지 않게)
  document.addEventListener('click', function (e) {
    var t = e.target.closest && e.target.closest('.tt-term');
    if (t) { e.preventDefault(); if (current === t && e.pointerType !== 'mouse') hide(); else show(t); }
    else if (current) hide();
  });
  document.addEventListener('keydown', function (e) { if (e.key === 'Escape') hide(); });
  window.addEventListener('scroll', hide, { passive: true });
  window.addEventListener('resize', hide);

  /* ---------- 처음 표시 + 계산 결과가 다시 그려질 때 자동 재적용 ---------- */
  var pending = false;
  function schedule() {
    if (pending) return; pending = true;
    requestAnimationFrame(function () { pending = false; annotate(document); });
  }
  annotate(document);
  new MutationObserver(schedule).observe(document.body, { childList: true, subtree: true });
})();
