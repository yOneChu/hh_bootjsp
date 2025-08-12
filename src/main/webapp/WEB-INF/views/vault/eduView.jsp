<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>

<%
    String filename = request.getParameter("filename");

    System.out.println("filename = " + filename);

%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <%--<meta name="viewport" content="width=device-width, initial-scale=1.0" />--%>
    <title>EduVision 3D - Viewer</title>
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" rel="stylesheet" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <!--APSviewer    -->
    <link rel="stylesheet" href="https://developer.api.autodesk.com/modelderivative/v2/viewers/style.min.css?v=v7.*" type="text/css">
    <script language="JavaScript" src="https://developer.api.autodesk.com/modelderivative/v2/viewers/viewer3D.min.js?v=v7.*"></script>

    <link rel="stylesheet" href="http://cdn.jsdelivr.net/gh/autodesk-forge/forge-extensions/public/extensions/camerarotation/contents/main.css">
    <script src="http://cdn.jsdelivr.net/gh/autodesk-forge/forge-extensions/public/extensions/camerarotation/contents/main.js"></script>


    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg: #0b0c0f;
            --panel: #111318;
            --panel-2: #161922;
            --line: #232634;
            --text: #e9edf1;
            --muted: #a9b0bb;
            --accent: #00d4ff;
            --accent-2: #6c5ce7;
            --shadow: 0 10px 30px rgba(0, 212, 255, 0.08);
        }

        * { box-sizing: border-box; }
        html, body { height: 100%; }
        body {
            margin: 0;
            background: radial-gradient(1200px 800px at 100% -10%, rgba(0,212,255,.08), transparent),
            radial-gradient(1200px 800px at -10% 100%, rgba(108,92,231,.08), transparent),
            var(--bg);
            color: var(--text);
            font-family: 'Noto Sans KR', system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
            overflow: hidden;
        }

        /* Layout */
        .viewer-app { display: grid; grid-template-rows: auto 1fr auto; height: 100vh; }
        .topbar { backdrop-filter: blur(10px); background: rgba(0,0,0,.35); border-bottom: 1px solid var(--line); }
        .topbar .brand { font-weight: 700; letter-spacing: .2px; background: linear-gradient(45deg, var(--accent), var(--accent-2)); -webkit-background-clip: text; background-clip: text; -webkit-text-fill-color: transparent; }

        .work-area { display: grid; grid-template-columns: 320px 6px 1fr; min-height: 0; }
        .sidebar { background: linear-gradient(180deg, var(--panel), var(--panel-2)); border-right: 1px solid var(--line); min-width: 220px; max-width: 600px; overflow: hidden; }
        .resizer { background: linear-gradient(180deg, #00d4ff22, #6c5ce722); cursor: col-resize; transition: background .2s; }
        .resizer:hover { background: linear-gradient(180deg, #00d4ff55, #6c5ce755); }

        .viewer-pane { background: #0a0c12; position: relative; }
        .viewer-toolbar { display: flex; gap: .5rem; align-items: center; padding: .6rem .8rem; border-bottom: 1px solid var(--line); background: rgba(0,0,0,.25); position: sticky; top: 0; z-index: 2; }
        .viewer-stage { position: absolute; inset: 42px 0 28px 0; }

        .statusbar { height: 28px; display: flex; align-items: center; gap: 1rem; padding: 0 .8rem; background: rgba(0,0,0,.35); border-top: 1px solid var(--line); color: var(--muted); font-size: .85rem; }


        /* ==== Sidebar Tree Style 개선 ==== */
        .sidebar {
            background: linear-gradient(180deg, var(--panel), var(--panel-2));
            border-right: 1px solid var(--line);
            min-width: 220px;
            max-width: 600px;
            overflow: hidden;
            font-size: 0.9rem;
        }

        .tree-header {
            padding: 0.6rem 0.85rem;
            border-bottom: 1px solid var(--line);
            display: flex;
            gap: .5rem;
            align-items: center;
            background: rgba(255,255,255,0.02);
        }

        .tree-search {
            background: #0f131a;
            color: var(--text);
            border: 1px solid var(--line);
            border-radius: .6rem;
            padding: .45rem .7rem;
            font-size: 0.85rem;
        }

        .tree-body {
            height: calc(100% - 50px);
            overflow-y: auto;
            padding: 0.5rem 0.6rem 1rem;
        }

        /* details.tree */
        details.tree {
            border-radius: .5rem;
            margin-bottom: .25rem;
            transition: background 0.2s;
        }
        details.tree[open] {
            background: rgba(255,255,255,.03);
            box-shadow: var(--shadow);
        }

        /* summary line */
        .tree summary {
            list-style: none;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: .5rem;
            padding: .4rem .55rem;
            border-radius: .4rem;
            font-weight: 500;
            color: var(--text);
        }
        .tree summary:hover {
            background: rgba(255,255,255,.05);
        }
        .tree summary::-webkit-details-marker {
            display: none;
        }

        /* twisty arrow */
        .twisty {
            width: 10px;
            height: 10px;
            border-right: 2px solid var(--muted);
            border-bottom: 2px solid var(--muted);
            transform: rotate(-45deg);
            transition: transform .2s, border-color 0.2s;
        }
        details[open] > summary .twisty {
            transform: rotate(45deg);
            border-color: var(--accent);
        }

        /* list items */
        .tree ul {
            margin: .3rem 0 .4rem 1.2rem;
            padding-left: .5rem;
            border-left: 1px dashed #2a2f3c;
        }
        .tree li {
            padding: .28rem .35rem;
            border-radius: .35rem;
            display: flex;
            align-items: center;
            gap: .5rem;
            font-size: 0.85rem;
            cursor: pointer;
            transition: background 0.2s;
        }
        .tree li:hover {
            background: rgba(255,255,255,.04);
        }
        .tree li i {
            font-size: 0.8rem;
            color: var(--muted);
        }

        /* badge */
        .badge-muted {
            background: #0f1620;
            color: var(--muted);
            border: 1px solid var(--line);
            padding: .08rem .4rem;
            border-radius: .5rem;
            font-size: .7rem;
            letter-spacing: 0.2px;
        }

        .tree li.active { background: rgba(0,212,255,.08); border: 1px solid var(--line); }


        /* Responsive tweak */
        @media (max-width: 960px) {
            .work-area { grid-template-columns: 260px 6px 1fr; }
            .topbar .brand { font-size: 1rem; }
        }
    </style>
</head>
<body>
<div class="viewer-app">
    <!-- Top bar -->
    <div class="topbar py-2 px-3 d-flex align-items-center justify-content-between">
        <div class="d-flex align-items-center gap-3">
            <div class="brand"><i class="fa-solid fa-cube me-2"></i>EduVision 3D</div>
            <div class="text-muted small">뷰어</div>
        </div>
        <!--<div class="d-flex align-items-center gap-2">
            <button class="btn-ghost" id="btnNew"><i class="fa-regular fa-file"></i><span class="d-none d-sm-inline"> 새 문서</span></button>
            <label class="btn-ghost m-0" for="fileInput"><i class="fa-solid fa-file-arrow-up"></i><span class="d-none d-sm-inline"> 파일 불러오기</span></label>
            <input id="fileInput" type="file" class="d-none" multiple />
            <button class="btn-ghost" id="btnSave"><i class="fa-regular fa-floppy-disk"></i><span class="d-none d-sm-inline"> 저장</span></button>
        </div>-->
    </div>

    <!-- Work area: sidebar | resizer | viewer -->
    <div class="work-area">
        <!-- Sidebar (Tree) -->
        <aside class="sidebar">
            <div class="tree-header">
                <input id="treeSearch" class="tree-search" placeholder="검색 (부품/어셈블리)" />
                <div class="btn-group">
                    <button class="btn-ghost" id="btnExpand" title="모두 펼치기"><i class="fa-solid fa-square-plus"></i></button>
                    <button class="btn-ghost" id="btnCollapse" title="모두 접기"><i class="fa-solid fa-square-minus"></i></button>
                </div>
            </div>
            <div class="tree-body">
                <!-- 샘플 트리 (placeholder) -->
                <details class="tree" open>
                    <summary><span class="twisty"></span><i class="fa-solid fa-boxes-stacked"></i> Elevator_Assembly <span class="badge-muted ms-auto">ASM</span></summary>
                    <ul>
                        <li>
                            <details class="tree">
                                <summary><span class="twisty"></span><i class="fa-solid fa-gear"></i> Machine_Room <span class="badge-muted ms-auto">SUB-ASM</span></summary>
                                <ul>
                                    <li data-value="10101187"><i class="fa-regular fa-circle"></i> TM HANDLE WHEEL <span class="badge-muted ms-auto">PART</span></li>
                                    <li data-value="10101305"><i class="fa-regular fa-circle"></i> TM HANDLE ASSY <span class="badge-muted ms-auto">PART</span></li>
                                    <li data-value="10101478"><i class="fa-regular fa-circle"></i> BRAKE LEVER ASSY <span class="badge-muted ms-auto">PART</span></li>
                                    <li data-value="10101325"><i class="fa-regular fa-circle"></i> SHEAVE COVER ASSY <span class="badge-muted ms-auto">PART</span></li>
                                </ul>
                            </details>
                        </li>
                        <li>
                            <details class="tree">
                                <summary><span class="twisty"></span><i class="fa-solid fa-elevator"></i> Car_Assembly <span class="badge-muted ms-auto">SUB-ASM</span></summary>
                                <ul>
                                    <li><i class="fa-regular fa-circle"></i> Car_Frame <span class="badge-muted ms-auto">PART</span></li>
                                    <li><i class="fa-regular fa-circle"></i> Door_System <span class="badge-muted ms-auto">PART</span></li>
                                    <li><i class="fa-regular fa-circle"></i> Interior_Panel <span class="badge-muted ms-auto">PART</span></li>
                                </ul>
                            </details>
                        </li>
                        <li>
                            <details class="tree">
                                <summary><span class="twisty"></span><i class="fa-solid fa-tower-observation"></i> Hoistway <span class="badge-muted ms-auto">SUB-ASM</span></summary>
                                <ul>
                                    <li><i class="fa-regular fa-circle"></i> Guide_Rail <span class="badge-muted ms-auto">PART</span></li>
                                    <li><i class="fa-regular fa-circle"></i> Counterweight <span class="badge-muted ms-auto">PART</span></li>
                                    <li><i class="fa-regular fa-circle"></i> Rope_System <span class="badge-muted ms-auto">PART</span></li>
                                </ul>
                            </details>
                        </li>
                    </ul>
                </details>
            </div>
        </aside>

        <!-- Resizer -->
        <div class="resizer" id="resizer"></div>

        <!-- Viewer pane -->
        <section class="viewer-pane">
            <div id="other-system-container" style="width:100%; height:100%;">
                <iframe src="http://10.225.80.35/vaultview/viewdesign.html?filename=<%=filename%>"
                        style="width:100%; height:100%; border:none;"></iframe>
            </div>
        </section>
    </div>

    <!-- Status bar -->
    <!-- <div class="statusbar">
         <div id="statusInfo">준비됨</div>
         <div class="ms-auto">마우스: 회전(좌클릭) · 이동(Shift+드래그) · 줌(휠)</div>
     </div>-->
</div>

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>

<script>

    /*var $folderTree

    $(document).ready(function (){

        function getQueryParam(param) {
            const urlParams = new URLSearchParams(window.location.search);

            console.log('---- getQueryParam start ----');
            console.log(urlParams);
            console.log(param);
            console.log('---- getQueryParam end ----');
            return urlParams.get(param);
        }

        // filename 파라미터 값 가져오기
        const fileName = getQueryParam('filename');
        const sabun = getQueryParam('sabun');
        console.log('fileName -->' + fileName);
        console.log('sabun -->' + sabun);
        sessionStorage.setItem('FileName', fileName);

        if('1111' == sabun) {
            alert('권한이 없어 뷰어를 확인할 수 없습니다.');
            window.close();
            return;
        }


        //view3DModel();

        if(fileName == null) {
            alert("도면 번호가 없습니다." );
        } else {
            view3D(fileName);

        }


    }); // END Jquery


    function view3D(fileName) {
        //debugger;

        //fileName = '10101310';


        const url = `http://10.225.80.35/vaultview/api/getdwffile?filename=${fileName}`;
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return response.text().then(errorText => {
                        //throw new Error(`HTTP Error ${response.status}: ${errorText}`);
                        throw (`${errorText}`);
                    });
                } else {

                }
                return response.json();
            })
            .then(returnData => {
                view3DModel(returnData.Output);
            })
            .catch(error => {
                alert(error);
            });
    }

    function view3DModel(fileName)
    {
        var DWFFileName = '3D_Models/' + fileName;
        //let DWFFileName = "/200C0374.iam.dwf";
        console.log("DWFFileName ---> " + DWFFileName);

        Autodesk.Viewing.Initializer({ env: 'Local' }, async function () {
            const viewer = new Autodesk.Viewing.GuiViewer3D(document.getElementById('3DViewerDiv'));

            viewer.start();

            viewer.setBimWalkToolPopup(false)
            console.log(viewer);

            viewer.loadModel(DWFFileName);
        })
    }*/


    // ===== Left/Right resizable splitter =====
    (function() {
        const sidebar = document.querySelector('.sidebar');
        const resizer = document.getElementById('resizer');
        let isDown = false;
        resizer.addEventListener('mousedown', (e) => { isDown = true; document.body.style.cursor = 'col-resize'; });
        window.addEventListener('mouseup', () => { isDown = false; document.body.style.cursor = ''; });
        window.addEventListener('mousemove', (e) => {
            if (!isDown) return;
            const min = 220, max = 600;
            const w = Math.min(max, Math.max(min, e.clientX));
            sidebar.style.width = w + 'px';
        });



    })();

    // ===== Tree expand/collapse & search =====
    const expandAll = () => document.querySelectorAll('details.tree').forEach(d => d.open = true);
    const collapseAll = () => document.querySelectorAll('details.tree').forEach(d => d.open = false);
    document.getElementById('btnExpand').addEventListener('click', expandAll);
    document.getElementById('btnCollapse').addEventListener('click', collapseAll);

    const searchInput = document.getElementById('treeSearch');
    searchInput.addEventListener('input', (e) => {
        const q = e.target.value.trim().toLowerCase();
        const items = document.querySelectorAll('.tree li, .tree summary');
        if (!q) {
            items.forEach(el => el.parentElement?.closest('details')?.classList.remove('matched'));
            document.querySelectorAll('details.tree').forEach(d => d.style.display = '');
            document.querySelectorAll('.tree li').forEach(li => li.style.display = '');
            return;
        }
        // simple filter: show branches that contain matches
        document.querySelectorAll('details.tree').forEach(d => d.open = true);
        document.querySelectorAll('.tree li').forEach(li => {
            const text = li.textContent.toLowerCase();
            li.style.display = text.includes(q) ? '' : 'none';
        });
    });


    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('li[data-value]').forEach(function (li) {
            li.addEventListener('click', function () {
                const filename = this.getAttribute('data-value');
                console.log('filename ---- ', filename);
                //alert(filename);
                // 화면 자체를 새로고침하면서 filename 파라미터 포함
                //window.location.href = `/vault/eduView?filename=${filename}`;
                window.location.href = "http://localhost:8070/vault/eduView?filename=" + filename;

            });
        });
    });


</script>
</body>
</html>