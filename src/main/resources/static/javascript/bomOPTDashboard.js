let dtTable = $("#infoTable").DataTable({
    "responsive": true,
    "lengthChange": true,
    "pageLength": 50,     //페이지 당 글 개수 설정
    "autoWidth": false, // 가로자동
    "processing": true,
    "destroy": true, // 테이블 재생성
    //"scrollX": true, // 가로 스크롤
    //"buttons": ["csv", "excel", "pdf", "print"]
    "buttons": ["csv", "excel", "copy"]
}).buttons().container().appendTo('#infoTable_wrapper .col-md-6:eq(0)');


const summary = {
    totalRequest: 5175587,
    totalModify: 211405
};

const monthlyRateData = [
    { label: "2025-01", rate: 95.42 },
    { label: "2025-02", rate: 95.85 },
    { label: "2025-03", rate: 96.69 },
    { label: "2025-04", rate: 96.15 },
    { label: "2025-05", rate: 96.18 },
    { label: "2025-06", rate: 96.68 },
    { label: "2025-07", rate: 96.48 },
    { label: "2025-08", rate: 95.98 },
    { label: "2025-09", rate: 95.08 },
    { label: "2025-10", rate: 95.89 },
    { label: "2025-11", rate: 94.99 },
    { label: "2025-12", rate: 95.81 },
    { label: "2026-01", rate: 96.57 },
    { label: "2026-02", rate: 95.03 },
    { label: "2026-03", rate: 95.91 }
];

const machineTypeRateData = [
    { type: "GTSS", rate: 99.04 },
    { type: "GTLX", rate: 96.45 },
    { type: "WBSS", rate: 95.07 },
    { type: "LXVF", rate: 94.84 },
    { type: "HSVF", rate: 93.34 },
    { type: "SUVF", rate: 78.68 },
    { type: "STS7H", rate: 75.40 },
    { type: "WLSH", rate: 69.66 },
    { type: "WSH", rate: 61.93 }
];
/*
const baseRows = [
    { blockNo: "B182D01", material: "RAIL LINER", request: 63282, modify: 1061 },
    { blockNo: "B126A01", material: "GUIDE SHOE", request: 63219, modify: 3016 },
    { blockNo: "A230A04", material: "JOB SITE TOOL", request: 54487, modify: 66 },
    { blockNo: "A103C", material: "ISOLATION PAD", request: 53460, modify: 298 },
    { blockNo: "C361A", material: "JAMB", request: 49491, modify: 2322 },
    { blockNo: "B183B01", material: "BUFFER BLOCKING", request: 47106, modify: 2504 },
    { blockNo: "E280A01", material: "CABLE ASSY", request: 45953, modify: 14 },
    { blockNo: "A230B02", material: "SPRAY", request: 44083, modify: 3 },
    { blockNo: "B182A01", material: "GUIDE RAIL", request: 43628, modify: 3260 },
    { blockNo: "E281A01", material: "CABLE ASSY", request: 42003, modify: 36 },
    { blockNo: "B126C01", material: "GUIDE SHOE ADAPTOR", request: 40701, modify: 3387 },
    { blockNo: "D112A04", material: "BRACKET", request: 39528, modify: 2201 },
    { blockNo: "M220A11", material: "MOTOR BASE", request: 38210, modify: 514 },
    { blockNo: "K201F02", material: "PANEL", request: 37102, modify: 1280 }
];*/

let blockRows = [];
let sortConfig = { key: null, direction: 'asc' };

const PAGE_SIZE = 30;
let currentPage = 1;
let filteredRows = [];
let monthlyChartInstance = null;
let typeChartInstance = null;



function formatNumber(value) {
    return new Intl.NumberFormat("ko-KR").format(value);
}

function calculateRate(request, modify) {
    if (!request) return 0;
    return ((request - modify) / request) * 100;
}

function formatRate(value) {
    return `${value.toFixed(2)}%`;
}

function getRateTone(rate) {
    if (rate >= 97) {
        return "bg-emerald-50 text-emerald-700 ring-1 ring-emerald-100 dark:bg-emerald-500/20 dark:text-emerald-200 dark:ring-emerald-400/30";
    }
    if (rate >= 94) {
        return "bg-sky-50 text-sky-700 ring-1 ring-sky-100 dark:bg-sky-500/20 dark:text-sky-200 dark:ring-sky-400/30";
    }

    return "bg-amber-50 text-amber-700 ring-1 ring-amber-100 dark:bg-amber-500/20 dark:text-amber-200 dark:ring-amber-400/30";
}

function isDarkMode() {
    return document.documentElement.classList.contains("dark");
}

async function renderSummary() {

    let totalCount = 0;
    let modifyCount = 0;

    /*showLoading();
    $.ajax({
        type : "get",
        url : "/subae/findSummaryAsCount",
        success : function(rr) {
            console.log(rr);

            //rr.forEach(row => {
                totalCount += Number(rr.totalCnt) || 0;
                modifyCount += Number(rr.modifyCnt) || 0;
            //})
            hideLoading();
        },
        error: function () {
            hideLoading();
            alert('데이터를 가져오는 중 오류가 발생하였습니다.');
        }
    });*/

    showLoading();

    try {
        const rr = await $.ajax({
            type: "get",
            url: "/subae/findSummaryAsCount"
        });

        console.log(rr);

        totalCount = Number(rr.TOTAL_COUNT) || 0;
        modifyCount = Number(rr.UCHECK_1_COUNT) || 0;

    } catch (e) {
        alert("데이터를 가져오는 중 오류가 발생하였습니다.");
    } finally {
        hideLoading();
    }

    console.log(`총 수배건수: ${totalCount}, 총 수정건수: ${modifyCount}`);


    /*if (blockRows.length === 0) {
        document.getElementById("mainRate").textContent = "0.00%";
        document.getElementById("requestCount").textContent = "0";
        document.getElementById("modifyCount").textContent = "0";
        document.getElementById("tfootRequest").textContent = "0";
        document.getElementById("tfootModify").textContent = "0";
        document.getElementById("tfootRate").textContent = "0.00%";
        return;
    }*/

    const totalRequest = totalCount; //blockRows.reduce((sum, row) => sum + (Number(row.totalCnt) || 0), 0);
    const totalModify = modifyCount; //blockRows.reduce((sum, row) => sum + (Number(row.modifyCnt) || 0), 0);
    const totalRate = calculateRate(totalRequest, totalModify);

    const mainRateEl = document.getElementById("mainRate");
    if (mainRateEl) mainRateEl.textContent = formatRate(totalRate);

    mainRateEl.textContent = formatRate(totalRate);
    
    const requestCountEl = document.getElementById("requestCount");
    if (requestCountEl) requestCountEl.textContent = formatNumber(totalRequest);
    
    const modifyCountEl = document.getElementById("modifyCount");
    if (modifyCountEl) modifyCountEl.textContent = formatNumber(totalModify);

    document.getElementById("tfootRequest").textContent = formatNumber(totalRequest);
    document.getElementById("tfootModify").textContent = formatNumber(totalModify);
    document.getElementById("tfootRate").textContent = formatRate(totalRate);
}

function getPaginatedRows() {
    const start = (currentPage - 1) * PAGE_SIZE;
    const end = start + PAGE_SIZE;
    return filteredRows.slice(start, end);
}


//데이터 조회
function searchInit(target) {

    showLoading();
    $.ajax({
        type : "get",
        url : "/subae/findSummaryAsBlockNo",
        beforeSend: function() {
            $("html").css("cursor", "wait");
        },
        complete: function() {
            $("html").css("cursor", "auto");
        },
        success : function(rr) {
            console.log(rr);

            // 데이터 할당
            blockRows = rr;
            filteredRows = [...blockRows];
            currentPage = 1;

            // searchType (자재명 선택) 옵션 설정
            updateMaterialSelect();

            // UI 렌더링
            renderTable();
            //renderSummary();
            renderCharts(); // 차트 데이터는 static이지만 초기화 시점에 호출

            hideLoading();
        },
        error: function () {
            hideLoading();
            alert('데이터를 가져오는 중 오류가 발생하였습니다.');
        }
    });
}

function renderTable() {
    const tbody = document.getElementById("bomTableBody");
    tbody.innerHTML = "";

    // 헤더 정렬 아이콘 업데이트
    updateSortIcons();

    // DataTable 인스턴스가 있으면 파괴
    if ($.fn.DataTable.isDataTable('#infoTable')) {
        $('#infoTable').DataTable().destroy();
    }

    const rows = getPaginatedRows();

    if (rows.length === 0) {
        tbody.innerHTML = `
              <tr>
                <td colspan="5" class="px-5 py-12 text-center text-sm text-slate-500 dark:text-slate-400">
                  검색 결과가 없습니다.
                </td>
              </tr>
            `;
        renderPagination();
        return;
    }

    rows.forEach((row) => {
        const totalCnt = Number(row.totalCnt) || 0;
        const modifyCnt = Number(row.modifyCnt) || 0;
        const rate = calculateRate(totalCnt, modifyCnt);
        const tone = getRateTone(rate);

        const tr = document.createElement("tr");
        tr.className = "transition border-b border-slate-100/70 hover:bg-slate-50/80 dark:border-slate-800/70 dark:hover:bg-slate-800/60";

        tr.innerHTML = `
              <td class="px-5 py-4 font-medium text-slate-900 dark:text-slate-100 whitespace-nowrap">${row.blockNo}</td>
              <td class="px-5 py-4 text-slate-600 dark:text-slate-300">${row.partName}</td>
              <td class="px-5 py-4 text-right text-slate-700 dark:text-slate-200">${formatNumber(totalCnt)}</td>
              <td class="px-5 py-4 text-right text-slate-700 dark:text-slate-200">${formatNumber(modifyCnt)}</td>
              <td class="px-5 py-4 text-right">
                <button id="findDiff-Btn" type="button" class="inline-flex items-center gap-1 rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-1.5 text-xs font-semibold text-emerald-700 transition hover:bg-emerald-100 dark:border-emerald-400/40 dark:bg-emerald-500/20 dark:text-emerald-200 dark:hover:bg-emerald-500/30">
                  <i class="fas fa-file-excel"></i>
                  Excel
                </button>
              </td>
              <td class="px-5 py-4 text-right">
                <span class="inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold ${tone}">
                  ${formatRate(rate)}
                </span>
              </td>
            `;
        tbody.appendChild(tr);
    });

    renderPagination();
}

function renderPagination() {
    const total = filteredRows.length;
    const totalPages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    const start = total === 0 ? 0 : (currentPage - 1) * PAGE_SIZE + 1;
    const end = Math.min(currentPage * PAGE_SIZE, total);

    document.getElementById("paginationInfo").textContent = `${start} - ${end} / ${total}건`;

    const pageNumbers = document.getElementById("pageNumbers");
    pageNumbers.innerHTML = "";

    const pagesToShow = getVisiblePages(currentPage, totalPages);

    pagesToShow.forEach((page) => {
        if (page === "...") {
            const span = document.createElement("span");
            span.className = "px-2 text-sm text-slate-400";
            span.textContent = "...";
            pageNumbers.appendChild(span);
            return;
        }

        const btn = document.createElement("button");
        const active = page === currentPage;
        btn.className = active
        ? "inline-flex h-10 min-w-10 items-center justify-center rounded-xl bg-slate-900 px-3 text-sm font-semibold text-white dark:bg-emerald-400 dark:text-slate-900"
        : "inline-flex h-10 min-w-10 items-center justify-center rounded-xl border border-slate-200 bg-white px-3 text-sm font-semibold text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-900/70 dark:text-slate-200 dark:hover:bg-slate-800";
        btn.textContent = page;
        btn.addEventListener("click", () => {
            currentPage = page;
            renderTable();
        });
        pageNumbers.appendChild(btn);
    });

    document.getElementById("prevPageBtn").disabled = currentPage <= 1;
    document.getElementById("nextPageBtn").disabled = currentPage >= totalPages;

    setButtonDisabledStyle(document.getElementById("prevPageBtn"), currentPage <= 1);
    setButtonDisabledStyle(document.getElementById("nextPageBtn"), currentPage >= totalPages);
}

function setButtonDisabledStyle(button, disabled) {
    if (disabled) {
        button.disabled = true;
        button.classList.add("opacity-50", "cursor-not-allowed");
    } else {
        button.disabled = false;
        button.classList.remove("opacity-50", "cursor-not-allowed");
    }
}

function getVisiblePages(current, total) {
    if (total <= 7) {
        return Array.from({ length: total }, (_, i) => i + 1);
    }

    if (current <= 4) {
        return [1, 2, 3, 4, 5, "...", total];
    }

    if (current >= total - 3) {
        return [1, "...", total - 4, total - 3, total - 2, total - 1, total];
    }

    return [1, "...", current - 1, current, current + 1, "...", total];
}

function updateSortIcons() {
    const headers = document.querySelectorAll("#infoTable thead th");
    const keys = ["blockNo", "partName", "totalCnt", "modifyCnt", null, "rate"];

    headers.forEach((th, index) => {
        const key = keys[index];
        if (!key) return;

        // 기존 아이콘 제거
        const existingIcon = th.querySelector(".sort-icon");
        if (existingIcon) existingIcon.remove();

        const icon = document.createElement("i");
        icon.className = "fas sort-icon ml-2 opacity-30 text-[10px]";
        
        if (sortConfig.key === key) {
            icon.className = `fas sort-icon ml-2 text-[10px] ${sortConfig.direction === "asc" ? "fa-sort-up" : "fa-sort-down"}`;
            icon.classList.remove("opacity-30");
        } else {
            icon.className = "fas fa-sort sort-icon ml-2 opacity-30 text-[10px]";
        }
        
        th.appendChild(icon);
        th.style.cursor = "pointer";
    });
}

function handleSort(key) {
    if (!key) return;

    if (sortConfig.key === key) {
        sortConfig.direction = sortConfig.direction === "asc" ? "desc" : "asc";
    } else {
        sortConfig.key = key;
        sortConfig.direction = "asc";
    }

    applySort();
    currentPage = 1;
    renderTable();
}

function applySort() {
    if (!sortConfig.key) return;

    filteredRows.sort((a, b) => {
        let valA, valB;

        if (sortConfig.key === "rate") {
            valA = calculateRate(Number(a.totalCnt) || 0, Number(a.modifyCnt) || 0);
            valB = calculateRate(Number(b.totalCnt) || 0, Number(b.modifyCnt) || 0);
        } else if (sortConfig.key === "totalCnt" || sortConfig.key === "modifyCnt") {
            valA = Number(a[sortConfig.key]) || 0;
            valB = Number(b[sortConfig.key]) || 0;
        } else {
            valA = (a[sortConfig.key] || "").toString().toLowerCase();
            valB = (b[sortConfig.key] || "").toString().toLowerCase();
        }

        if (valA < valB) return sortConfig.direction === "asc" ? -1 : 1;
        if (valA > valB) return sortConfig.direction === "asc" ? 1 : -1;
        return 0;
    });
}

function applySearch() {
    const keyword = document.getElementById("searchInput").value.trim().toLowerCase();
    const selectedMaterial = document.getElementById("searchType").value;

    filteredRows = blockRows.filter(row => {
        const matchesKeyword = (row.blockNo && row.blockNo.toLowerCase().includes(keyword)) ||
                             (row.partName && row.partName.toLowerCase().includes(keyword));
        
        const matchesMaterial = !selectedMaterial || row.partName === selectedMaterial;

        return matchesKeyword && matchesMaterial;
    });

    applySort(); // 검색 후에도 현재 정렬 상태 유지
    currentPage = 1;
    renderTable();
}

function updateMaterialSelect() {
    const select = document.getElementById("searchType");
    const currentValue = select.value;
    
    // 기존 옵션 제거 (첫 번째 '전체' 옵션 제외 혹은 초기화)
    select.innerHTML = '<option value="">자재명 선택 (전체)</option>';

    // unique partNames 추출 및 정렬
    const partNames = [...new Set(blockRows.map(row => row.partName))].filter(Boolean).sort();

    partNames.forEach(name => {
        const option = document.createElement("option");
        option.value = name;
        option.textContent = name;
        if (name === currentValue) option.selected = true;
        select.appendChild(option);
    });
}

function bindSearch() {
    const headers = document.querySelectorAll("#infoTable thead th");
    const keys = ["blockNo", "partName", "totalCnt", "modifyCnt", null, "rate"];
    
    headers.forEach((th, index) => {
        if (keys[index]) {
            th.addEventListener("click", () => handleSort(keys[index]));
        }
    });

    document.getElementById("searchInput").addEventListener("input", applySearch);
    document.getElementById("searchType").addEventListener("change", applySearch);
    
    const resetBtn = document.getElementById("resetSearchBtn");
    if (resetBtn) {
        resetBtn.addEventListener("click", () => {
            document.getElementById("searchInput").value = "";
            document.getElementById("searchType").value = "";
            filteredRows = [...blockRows];
            currentPage = 1;
            renderTable();
        });
    }

    document.getElementById("prevPageBtn").addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            renderTable();
        }
    });

    document.getElementById("nextPageBtn").addEventListener("click", () => {
        const totalPages = Math.max(1, Math.ceil(filteredRows.length / PAGE_SIZE));
            if (currentPage < totalPages) {
            currentPage++;
            renderTable();
        }
    });

    document.getElementById("bomTableBody").addEventListener("click", (event) => {
        const excelBtn = event.target.closest("#findDiff-Btn");
        if (!excelBtn) return;

        const tr = excelBtn.closest("tr");
        if (!tr) return;

        const blockNo = tr.children[0]?.textContent?.trim() || "";
        const partName = tr.children[1]?.textContent?.trim() || "";

        getBlockDiffExcel(blockNo, partName);
    });
}

function buildMonthlyChart() {
    const ctx = document.getElementById("monthlyChart");
    const dark = isDarkMode();
    const lineColor = dark ? "#34d399" : "#16a34a";
    const fillColor = dark ? "rgba(52,211,153,0.20)" : "rgba(34,197,94,0.14)";

    if (monthlyChartInstance) {
        monthlyChartInstance.destroy();
    }

    monthlyChartInstance = new Chart(ctx, {
        type: "line",
        data: {
        labels: monthlyRateData.map(item => item.label),
        datasets: [
    {
    label: "BOM 수배율",
    data: monthlyRateData.map(item => item.rate),
    borderColor: lineColor,
    backgroundColor: fillColor,
    fill: true,
    tension: 0.34,
    borderWidth: 3,
    pointRadius: 0,
    pointHoverRadius: 5,
    pointBackgroundColor: lineColor
    }
        ]
    },
        options: {
        maintainAspectRatio: false,
        interaction: {
        mode: "index",
        intersect: false
    },
        plugins: {
            legend: { display: false },
            tooltip: {
            backgroundColor: dark ? "rgba(15,23,42,0.96)" : "rgba(15,23,42,0.88)",
            titleColor: "#fff",
            bodyColor: "#fff",
            padding: 12,
            displayColors: false,
            callbacks: {
                label: (context) => `수배율 ${context.parsed.y.toFixed(2)}%`
            }
        }
    },
    scales: {
        y: {
            min: 90,
            max: 98,
            ticks: {
                color: dark ? "#94a3b8" : "#64748b",
                callback: (value) => value + "%"
            },
            grid: {
                color: dark ? "rgba(148,163,184,0.20)" : "rgba(148,163,184,0.12)",
                drawBorder: false
            },
                border: { display: false }
        },
        x: {
        ticks: {
        color: dark ? "#94a3b8" : "#64748b",
        maxTicksLimit: 8
    },
        grid: { display: false },
        border: { display: false }
    }
}
}
});
}

function buildTypeChart() {
    const ctx = document.getElementById("typeChart");
    const dark = isDarkMode();
    const barPalette = dark
        ? [
            "rgba(16,185,129,0.95)",
            "rgba(52,211,153,0.90)",
            "rgba(45,212,191,0.88)",
            "rgba(56,189,248,0.85)",
            "rgba(96,165,250,0.80)",
            "rgba(125,211,252,0.78)",
            "rgba(147,197,253,0.75)",
            "rgba(165,180,252,0.70)",
            "rgba(196,181,253,0.65)"
        ]
        : [
            "#166534",
            "#15803d",
            "#16a34a",
            "#22c55e",
            "#4ade80",
            "#86efac",
            "#bbf7d0",
            "#d1fae5",
            "#dcfce7"
        ];

    if (typeChartInstance) {
        typeChartInstance.destroy();
    }

    typeChartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels: machineTypeRateData.map(item => item.type),
            datasets: [
                {
                    data: machineTypeRateData.map(item => item.rate),
                    backgroundColor: barPalette,
                    borderRadius: 999,
                    barThickness: 18
                }
            ]
        },
        options: {
            indexAxis: "y",
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: dark ? "rgba(15,23,42,0.96)" : "rgba(15,23,42,0.88)",
                    titleColor: "#fff",
                    bodyColor: "#fff",
                    padding: 12,
                    displayColors: false,
                    callbacks: {
                        label: (context) => `${context.parsed.x.toFixed(2)}%`
                    }
                }
            },
            scales: {
                x: {
                        min: 0,
                        max: 100,
                        ticks: {
                        color: dark ? "#94a3b8" : "#64748b",
                        callback: (v) => v + "%"
                    },
                        grid: {
                        color: "rgba(148,163,184,0.12)",
                        drawBorder: false
                    },
                        border: { display: false }
                },
                y: {
                    ticks: {
                        color: dark ? "#cbd5e1" : "#475569",
                        font: { size: 12, weight: "600" }
                    },
                    grid: { display: false },
                    border: { display: false }
                }
            }
        },
        plugins: [{
            id: "valueLabelPlugin",
            afterDatasetsDraw(chart) {
                const { ctx } = chart;
                const meta = chart.getDatasetMeta(0);
                ctx.save();
                ctx.font = "600 12px Inter, sans-serif";
                ctx.fillStyle = dark ? "#e2e8f0" : "#334155";
        
                meta.data.forEach((bar, i) => {
                    const value = machineTypeRateData[i].rate.toFixed(2) + "%";
                    ctx.fillText(value, bar.x + 10, bar.y + 4);
                });
        
                ctx.restore();
            }
        }]
});
}

function renderCharts() {
    buildMonthlyChart();
    buildTypeChart();
}

function applyTheme(theme) {
    const root = document.documentElement;
    if (theme === "dark") {
        root.classList.add("dark");
        localStorage.setItem("theme", "dark");
        document.getElementById("themeToggleText").textContent = "라이트모드";
    } else {
        root.classList.remove("dark");
        localStorage.setItem("theme", "light");
        document.getElementById("themeToggleText").textContent = "다크모드";
    }
    renderCharts();
}

function initTheme() {
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme) {
        applyTheme(savedTheme);
        return;
    }

    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    applyTheme(prefersDark ? "dark" : "light");
}

function bindThemeToggle() {
    document.getElementById("themeToggle").addEventListener("click", () => {
        applyTheme(isDarkMode() ? "light" : "dark");
    });
}

function init() {
    initTheme();
    searchInit();
    renderSummary();
    bindSearch();
    bindThemeToggle();

    isDarkMode();
}

init();



//EXCEL
function getBlockDiffExcel(blockNo, partName) {

    showLoading(); // 로딩바 표시
    $.ajax({
        url: '/excel/searchBlockSubae',   // 요청 보낼 URL
        type: 'POST',              // 메서드 (GET/POST 등)
        data : {
            blockNo : blockNo,
            partName : partName,

        },
        xhrFields: {
            responseType: 'blob'    // 파일 다운로드용 응답 처리
        },
        success: function (data, status, xhr) {

            //console.log(data);

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