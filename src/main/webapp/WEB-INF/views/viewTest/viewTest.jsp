
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>

<%

    String filePath = "C:\\viewTest\\10110434.obj";

    System.out.println("-------VIEW TEST --------");
    System.out.println("filePath = " + filePath);
%>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />


    <title>한국v중국_자재비교</title>

    <!-- Google Font: Source Sans Pro -->
    <link rel="stylesheet" href="/resources/dist/googleFont.css">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="/resources/dist/plugins/fontawesome-free/css/all.min.css">

    <!-- DataTables -->
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-responsive/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="/resources/dist/plugins/datatables-buttons/css/buttons.bootstrap4.min.css">

    <link rel="stylesheet" href="/resources/dist/plugins/select2/css/select2.min.css">

    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/dist/css/adminlte.min.css">
    <style>
        body {
            font-family: 'Cascadia Code', sans-serif;
        }
    </style>


   <%-- <script type="importmap">
        {
            "imports": {
                "three": "https://cdn.jsdelivr.net/npm/three@latest/build/three.module.js",
                "three/addons/": "https://cdn.jsdelivr.net/npm/three@latest/examples/jsm/"
            }
        }

    </script>--%>

<%--    <script type="importmap">
        {
            "imports": {
                "three": "./resources/dist/three/three.module.js",
                "ObJLoader": "./resources/dist/three/OBJLoader.js"
            }
        }

    </script>--%>

</head>


<body class="hold-transition sidebar-mini text-sm" style="zoom:95%;">

<div class="wrapper">
    <!-- Navbar -->
    <!-- <nav class="main-header navbar navbar-expand navbar-white navbar-light"> -->
    <nav class="main-header navbar navbar-expand">
        <!-- Left navbar links -->
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
            </li>

            <li class="nav-item">
                <a class="nav-link" data-widget="fullscreen" href="#" role="button">
                    <i class="fas fa-expand-arrows-alt"></i>
                </a>
            </li>
        </ul>
    </nav>
    <!-- /.navbar -->


    <!-- Main Sidebar Container -->
    <jsp:include page="../dashboard/dashboardLayoutSideBar.jsp" flush="true" />


    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1>부품 비교(한국-중국)</h1>
                    </div>
                    <div class="col-sm-6">
                        <ol class="breadcrumb float-sm-right">
                            <li class="breadcrumb-item"><a href="#">Home</a></li>
                            <li class="breadcrumb-item active">DataTables</li>
                        </ol>
                    </div>
                </div>
            </div><!-- /.container-fluid -->
        </section>



        <!-- Main content -->
        <section class="content" style="zoom:100%;">

            <div class="container-fluid"> <!-- start - container-fluid -->

                <!-- 검색조건 -->
                <!-- <div class="card card-default"> -->
                <div class="card card-primary">
                    <div class="card-header">
                        <h3 class="card-title"> 검색 조건</h3>

                        <div class="card-tools">
                            <button type="button" class="btn btn-tool" data-card-widget="collapse">
                                <i class="fas fa-minus"></i>
                            </button>
                            <button type="button" class="btn btn-tool" data-card-widget="remove">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                    </div>

                    <!-- /.card-header -->
                    <div class="card-body" style="zoom:85%;">
                        <div class="row">
                            <!-- <div class="col-6"> -->
                            <div class="col-md-12">
                                <div class="callout callout-danger">
                                    <h4><i class="fas fa-bullhorn"></i> 도움말</h4>
                                    <h5>- 한국PLM vs. 중국PLM - 실시간 자재 데이터 정보 비교.</h5>
                                    <h5>- 여러 자재 조회 시 ","콤마로 구분하여 검색 가능 (ex. C101P013252,C101P013253) </h5>

                                </div>
                            </div>

                            <div class="col-md-6">
                                <!-- <div class="col-6"> -->
                                <div class="form-group">
                                    <label>부품번호</label>

                                    <textarea class="form-control" id="partNo" rows="5" placeholder="부품 NO 입력하시오. "></textarea>

                                </div>
                            </div>


                            <!-- /.col -->
                        </div>
                        <!-- /.row -->
                    </div>

                </div>


                <div class="row">
                    <div class="col-12">

                        <div class="card card-primary">

                            <div class="card-header">
                                <h3 class="card-title">검색 결과</h3>
                            </div>

                            <!-- /.card-header -->
                            <div class="card-body" style="zoom:85%;">


                                <script type="module">



                                    //import * as THREE from 'three';
                                    //import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
                                    //import { OBJLoader } from 'OBJLoader';

                                    import * as THREE from './resources/dist/three/three.module.js';
                                    import { OBJLoader } from './resources/dist/three/OBJLoader.js';
                                    import { OrbitControls } from './resources/dist/three/OrbitControls.js';
                                    //import { OrbitControls } from './resources/dist/three/three.core.js';

                                    //const filePath = '/resources/document/10110434.obj';
                                    //const filePath = "<%=filePath%>";
                                    /*const filePath = "${filePath.replace('\\', '\\\\')}";*/

                                    //const filePath = "./resources/10110434.obj";
                                    const filePath = "./resources/32100059.obj";


                                    console.log('ASDFASDF', THREE);
                                    console.log('filePath---', filePath)
                                    console.log('OrbitControls', OrbitControls);
                                    console.log('OBJLoader', OBJLoader);
                                    //const scene = new THREE.Scene();

                                    // 1️⃣ 씬(Scene) 생성
                                    const scene = new THREE.Scene();
                                    scene.background = new THREE.Color(0xf0f0f0); // 연한 회색 배경

                                    // 2️⃣ 카메라(Camera) 생성 (시야각, 종횡비, 근접/원거리)
                                    const camera = new THREE.PerspectiveCamera(
                                        75,
                                        window.innerWidth / window.innerHeight,
                                        0.1,
                                        1000);
                                    camera.position.set(0, 5, 10); // 카메라 위치 조정
                                    camera.lookAt(0, 0, 0);

                                    // 3️⃣ 렌더러(Renderer) 생성
                                    const renderer = new THREE.WebGLRenderer({ antialias: true });
                                    renderer.setSize(window.innerWidth, window.innerHeight);
                                    document.body.appendChild(renderer.domElement);

                                    // 4️⃣ 조명(Light) 추가
                                    const light = new THREE.DirectionalLight(0xffffff, 1);
                                    light.position.set(5, 5, 5);
                                    scene.add(light);

                                    // 💡 조명 보완: 위(하늘)와 아래(땅)에서 비추는 Hemispheric Light
                                    const hemiLight = new THREE.HemisphereLight(0xffffff, 0x444444, 0.6);
                                    hemiLight.position.set(0, 20, 0);
                                    scene.add(hemiLight);

                                    // 💡 전체적으로 밝기를 채워주는 Ambient Light
                                    const ambientLight = new THREE.AmbientLight(0xffffff, 0.4);
                                    scene.add(ambientLight);



                          



                                    // 5️⃣ OBJ 파일 로드
                                    const objLoader = new OBJLoader();
                                    objLoader.load(
                                        //'FinalBaseMesh.obj', // ⚠️ 여기에 불러올 .obj 파일 이름을 넣어야 함
                                        //'10110434.obj',
                                        filePath,
                                        function (object) {

                                            //조명 반응형 재질(Material) 강제 적용
                                            object.traverse((child) => {
                                                if (child.isMesh) {
                                                    // 조명 반응형 재질 강제 적용
                                                    child.material = new THREE.MeshStandardMaterial({
                                                        color: 0xaaaaaa,
                                                        metalness: 0.2,
                                                        roughness: 0.7,
                                                    });

                                                    child.castShadow = true;
                                                    child.receiveShadow = true;
                                                }
                                            });

                                            // 모델 씬에 추가
                                            scene.add(object);

                                            // 모델 위치 조정
                                            object.position.set(0, 0, 0);

                                            // 📦 모델 크기 측정
                                            const box = new THREE.Box3().setFromObject(object);
                                            const size = box.getSize(new THREE.Vector3());
                                            const center = box.getCenter(new THREE.Vector3());

                                            // 카메라 거리 계산 (크기에 비례)
                                            const maxDim = Math.max(size.x, size.y, size.z);
                                            const fov = camera.fov * (Math.PI / 180); // 시야각(rad)
                                            let cameraZ = Math.abs(maxDim / 2 / Math.tan(fov / 2));
                                            cameraZ *= 1.5; // 여유 거리 줌

                                            // 📷 카메라 위치 및 바라보는 방향 설정
                                            camera.position.set(center.x, center.y + maxDim * 0.5, cameraZ);
                                            camera.lookAt(center);

                                            // 컨트롤 업데이트
                                            controls.target.copy(center);
                                            controls.update();


                                        },
                                        function (xhr) {

                                        },
                                        function (error) {
                                            console.error('OBJ 로드 실패', error);
                                        }
                                    );

                                    // 6️⃣ OrbitControls 추가 (마우스로 모델 회전/줌 가능)
                                    const controls = new OrbitControls(camera, renderer.domElement);
                                    controls.enableDamping = true;

                                    // 7️⃣ 애니메이션 루프
                                    function animate() {
                                        requestAnimationFrame(animate);
                                        controls.update();
                                        renderer.render(scene, camera);
                                    }
                                    animate();

                                    // 8️⃣ 창 크기 변경 대응
                                    window.addEventListener('resize', () => {
                                        camera.aspect = window.innerWidth / window.innerHeight;
                                        camera.updateProjectionMatrix();
                                        renderer.setSize(window.innerWidth, window.innerHeight);
                                    });


                                </script>





                            </div>
                            <!-- /.card-body -->
                        </div>
                        <!-- /.card -->
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->

            </div> <!-- /.container-fluid -->

        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->


    <footer class="main-footer">
        <div class="float-right d-none d-sm-block">
            <b>Version</b> 1.0.0
        </div>
        <strong>Copyright &copy; 2024 <a href="#">수배로직설계팀-김영환 M</a>.</strong> All rights reserved.
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
    <!-- /.control-sidebar -->

</div>


</body>

<!-- <script src="https://code.jquery.com/jquery-3.5.1.js"></script> -->

<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

<!-- AdminLTE App -->
<script src="/resources/dist/js/adminlte.min.js"></script>

<!-- Bootstrap 4 -->
<script src="/resources/dist/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<!-- DataTables  & Plugins -->
<script src="/resources/dist/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/resources/dist/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="/resources/dist/plugins/datatables-responsive/js/dataTables.responsive.min.js"></script>
<script src="/resources/dist/plugins/datatables-responsive/js/responsive.bootstrap4.min.js"></script>


<script src="/resources/dist/plugins/select2/js/select2.full.min.js"></script>

<script src="/resources/dist/plugins/datatables-buttons/js/dataTables.buttons.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.bootstrap4.min.js"></script>
<script src="/resources/dist/plugins/jszip/jszip.min.js"></script>
<script src="/resources/dist/plugins/pdfmake/pdfmake.min.js"></script>
<script src="/resources/dist/plugins/pdfmake/vfs_fonts.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.html5.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.print.min.js"></script>
<script src="/resources/dist/plugins/datatables-buttons/js/buttons.colVis.min.js"></script>



</html>
