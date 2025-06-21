<%@ page import="com.kyhslam.service.PartUtilService" %>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%  request.setCharacterEncoding("utf-8"); %>


<%


%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests"> -->
    <link rel="icon" type="image/png" href="/resources/favicon.ico" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/handsontable/dist/handsontable.full.min.css">
    <title>한국v중국_자재비교</title>

    <style>

        /* Handsontable 컨테이너 스타일 */
        #hot-container {
            width: 800px;
            height: 400px;
            margin: 50px auto; /* 가운데 정렬을 위해 */
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
    </style>
</head>


<body>

<h1>Handsontable JSON 데이터 예제</h1>
<p>아래 스프레드시트에서 데이터를 직접 편집해보세요.</p>

<div id="hot-container"></div>


</body>


<script src="/resources/dist/js/jquery-3.7.1.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/handsontable/dist/handsontable.full.min.js"></script>

<%--<script src="app_json.js"></script>--%>

<script>

    /*document.addEventListener('DOMContentLoaded', function() {
        const container = document.getElementById('hot-container');

        // JSON 형식의 샘플 데이터 (객체들의 배열)
        const jsonData = [
            {id: 1, name: '김철수', age: 28, city: '서울'},
            {id: 2, name: '이영희', age: 34, city: '부산'},
            {id: 3, name: '박민준', age: 22, city: '대구'},
            {id: 4, name: '최유리', age: 31, city: '인천'}
        ];

        // Handsontable 초기화 옵션 설정
        const hotSettings = {
            data: jsonData,             // JSON 형식 데이터 지정

            // 열 헤더를 명시적으로 정의 (사용자에게 보여질 이름)
            colHeaders: ['ID', '이름', '나이', '도시'],

            // 데이터 객체의 속성과 열을 매핑
            columns: [
                {data: 'id', type: 'numeric', readOnly: true}, // ID는 숫자 타입, 읽기 전용
                {data: 'name', type: 'text'},                  // 이름은 텍스트 타입
                {data: 'age', type: 'numeric'},                 // 나이는 숫자 타입
                {data: 'city', type: 'text'}                   // 도시는 텍스트 타입
            ],

            rowHeaders: true,           // 행 헤더 표시
            contextMenu: true,          // 우클릭 컨텍스트 메뉴 활성화
            filters: true,              // 필터링 기능 활성화
            dropdownMenu: true,         // 드롭다운 메뉴 활성화
            height: 'auto',             // 높이를 내용에 맞게 자동 조절
            width: 'auto',              // 너비를 내용에 맞게 자동 조절
            licenseKey: 'non-commercial-and-evaluation' // 비상업적 또는 평가용 라이선스 키
        };

        // Handsontable 인스턴스 생성 및 초기화
        const hot = new Handsontable(container, hotSettings);

        console.log('Handsontable (JSON 데이터)이 성공적으로 로드되었습니다.');
    });*/


    $(document).ready(function() {

        //행 높이 고정을 위한 변수
        let rowHeightFixed = false;

        //test
        $.ajax({
            type : "post",
            url : "/subae/logiceditor",
            data : {
                partNo : 'test'
            },
            beforeSend: function() {
                $("html").css("cursor", "wait");
            },
            complete: function() {
                $("html").css("cursor", "auto");
            },
            success : function(data)
            {
                console.log("cn data - ", data);
                console.log("cn data.length - ", data.length);

                let jsonData = data;

                const container = document.getElementById('hot-container');
                const hotSettings = {
                    data: jsonData,             // JSON 형식 데이터 지정

                    // 열 헤더를 명시적으로 정의 (사용자에게 보여질 이름)
                    colHeaders: ['NO', 'ADDR',
                        'SPEC1', 'CON1','SPEC2', 'CON2', 'SPEC3','CON3','SPEC4','CON4','SPEC5','CON5','SPEC6','CON6','SPEC7','CON7'
                        ,'SPEC8','CON8','SPEC9','CON9','SPEC10','CON10','SPEC11','CON11','SPEC12','CON12','SPEC13','CON13','SPEC14','CON14','SPEC15','CON15',
                        'SPEC16','CON16','SPEC17','CON17','SPEC18','CON18','SPEC19','CON19','SPEC20','CON20',
                        'KEY1', 'VAL1','KEY2', 'VAL2', 'KEY3','VAL3','KEY4','VAL4','KEY5','VAL5','KEY6','VAL6','KEY7','VAL7'
                        ,'KEY', 'VAL8','KEY9','VAL9','KEY10','VAL10','KEY11','VAL11','KEY12','VAL12','KEY13','VAL13','KEY14','VAL14','KEY15','VAL15',
                        'KEY16','VAL16','KEY17','VAL17','KEY18','VAL18','KEY19','VAL19','KEY20','VAL20'
                    ],

                    columns: [
                        {data: 'no', type: 'text', readOnly: true}, // ID는 숫자 타입, 읽기 전용
                        {data: 'addr', type: 'text'},
                        {data: 'spec1', type: 'text'}, {data: 'con1', type: 'text'},
                        {data: 'spec2', type: 'text'}, {data: 'con2', type: 'text'},
                        {data: 'spec3', type: 'text'}, {data: 'con3', type: 'text'},
                        {data: 'spec4', type: 'text'}, {data: 'con4', type: 'text'},
                        {data: 'spec5', type: 'text'}, {data: 'con5', type: 'text'},
                        {data: 'spec6', type: 'text'}, {data: 'con6', type: 'text'},
                        {data: 'spec7', type: 'text'}, {data: 'con7', type: 'text'},
                        {data: 'spec8', type: 'text'}, {data: 'con8', type: 'text'},
                        {data: 'spec9', type: 'text'}, {data: 'con9', type: 'text'},
                        {data: 'spec10', type: 'text'}, {data: 'con10', type: 'text'},
                        {data: 'spec11', type: 'text'}, {data: 'con11', type: 'text'},
                        {data: 'spec12', type: 'text'}, {data: 'con12', type: 'text'},
                        {data: 'spec13', type: 'text'}, {data: 'con13', type: 'text'},
                        {data: 'spec14', type: 'text'}, {data: 'con14', type: 'text'},
                        {data: 'spec15', type: 'text'}, {data: 'con15', type: 'text'},
                        {data: 'spec16', type: 'text'}, {data: 'con16', type: 'text'},
                        {data: 'spec17', type: 'text'}, {data: 'con17', type: 'text'},
                        {data: 'spec18', type: 'text'}, {data: 'con18', type: 'text'},
                        {data: 'spec19', type: 'text'}, {data: 'con19', type: 'text'},
                        {data: 'spec20', type: 'text'}, {data: 'con20', type: 'text'}
                    ],

                    rowHeaders: true,           // 행 헤더 표시
                    contextMenu: true,          // 우클릭 컨텍스트 메뉴 활성화
                    filters: true,              // 필터링 기능 활성화
                    dropdownMenu: true,         // 드롭다운 메뉴 활성화
                    height: 'auto',             // 높이를 내용에 맞게 자동 조절
                    width: 'auto',              // 너비를 내용에 맞게 자동 조절
                    licenseKey: 'non-commercial-and-evaluation', // 비상업적 또는 평가용 라이선스 키


                    afterLoadData: function(initialLoad) {
                        if (initialLoad) {
                            //minimizeEmptyColumns(this);
                           hideEmptyColumns(this); //값 없는 열 숨기기

                        }
                    },

                    afterRender: function(isForced) {
                       /* const newHeights = getActualRowHeights(this);
                        this.updateSettings({
                            rowHeights: newHeights,
                            autoRowSize: false // 고정으로 전환
                        });*/
                        if (!rowHeightFixed) {
                            applyFixedRowHeightsOnce(this); // 행 높이 고정
                            rowHeightFixed = true; // 다시 실행되지 않도록 차단
                        }
                    }
                };


                const hot = new Handsontable(container, hotSettings);
            }
        });




    });


    //값 없는 열 간격 최소화
    function minimizeEmptyColumns(hotInstance) {
        const data = hotInstance.getData();
        console.log(data[0]);
        const columnCount = data[0]?.length || 0;
        const newColWidths = [];

        for (let col = 0; col < columnCount; col++) {
            let allEmpty = true;
            for (let row = 0; row < data.length; row++) {
                const value = data[row][col];
                if (value !== null && value !== '') {
                    allEmpty = false;
                    break;
                }

            }

            newColWidths[col] = allEmpty ? 5 : null;  // null은 auto size (또는 원하는 기본값)
        }

        // 열 너비 갱신
        hotInstance.updateSettings({
            colWidths: newColWidths
        });
    }

    //높이 유지
    function getActualRowHeightsaaa(hotInstance) {
        const rowCount = hotInstance.countRows();
        const rowHeights = [];

        for (let row = 0; row < rowCount; row++) {
            // 각 row의 DOM element를 찾음
            const rowElem = hotInstance.view.wt.wtTable.getRow(row);
            if (rowElem) {
                const height = rowElem.getBoundingClientRect().height;
                rowHeights.push(Math.ceil(height));
            } else {
                rowHeights.push(23); // 기본 높이 (Handsontable 기본값)
            }
        }

        return rowHeights;
    }

    function getActualRowHeights(hotInstance) {
        const rowCount = hotInstance.countRows();
        const colCount = hotInstance.countCols();
        const rowHeights = [];

        for (let row = 0; row < rowCount; row++) {
            let maxHeight = 0;

            // 모든 셀 중 가장 높은 셀을 기준으로
            for (let col = 0; col < colCount; col++) {
                const cell = hotInstance.getCell(row, col);
                if (cell) {
                    const height = cell.getBoundingClientRect().height;
                    console.log(cell);
                    console.log(height);
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }
            }

            // 최소값 설정 (예: 빈 줄일 경우 23)
            rowHeights[row] = maxHeight || 23;
        }

        return rowHeights;
    }


    /**
     * -행 높이 고정(행의 값중 높이가 높은 값을 기준 높이로 설정)
     * -각 행에서 셀 값들 중 가장 높은 셀(예: 줄바꿈, 이미지, 긴 텍스트 등)의 높이를 기준으로 행의 높이를 고정하고 싶다
     * @param hotInstance
     */
    function applyFixedRowHeightsOnce(hotInstance) {
        // DOM이 완전히 렌더된 후 실행
        setTimeout(() => {
            const rowCount = hotInstance.countRows();
            const colCount = hotInstance.countCols();
            const rowHeights = [];

            for (let row = 0; row < rowCount; row++) {
                let maxHeight = 0;

                for (let col = 0; col < colCount; col++) {
                    const cell = hotInstance.getCell(row, col);
                    if (cell) {
                        const height = cell.getBoundingClientRect().height;
                        if (height > maxHeight) {
                            maxHeight = height;
                        }
                    }
                }

                rowHeights.push(maxHeight || 23); // 기본 높이 보정
            }

            hotInstance.updateSettings({
                rowHeights: rowHeights,
                autoRowSize: false  // 이후 자동 높이 제거
            });
        }, 0); // 렌더 완료 직후 한 번만 실행
    }




    //값 없는 셀 숨기기
    function hideEmptyColumns(hotInstance) {
        const data = hotInstance.getData();
        const columnCount = data[0]?.length || 0;
        const emptyColumns = [];

        for (let col = 0; col < columnCount; col++) {
            let allEmpty = true;
            for (let row = 0; row < data.length; row++) {
                const value = data[row][col];
                if (value !== null && value !== '') {
                    allEmpty = false;
                    break;
                }
            }
            if (allEmpty) {
                emptyColumns.push(col);
            }
        }

        // 숨기기 반영
        hotInstance.updateSettings({
            hiddenColumns: {
                columns: emptyColumns,
                indicators: true
            }
        });
    }
</script>


</html>
