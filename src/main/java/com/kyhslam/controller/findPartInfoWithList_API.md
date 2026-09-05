# API 정의서 - 품번 목록 속성정보 조회 (findPartInfoWithList)

## 1. 개요

| 항목 | 내용 |
|---|---|
| API 명 | 품번 목록 속성정보 조회 |
| 설명 | 품번들을 `,`로 연결한 문자열을 받아, 해당 품번들의 PLM 속성정보를 한 번에 조회한다. |
| Controller | `com.kyhslam.controller.APIController#findPartInfoWithList` |
| 구현 로직 | `com.kyhslam.util.MLBCommonUtil#findPartInfoWithList_v2` |
| 대상 테이블 | `NORMALPART$VF A`, `NORMALPART$id B` (PLM) |
| 인증 | `key` 파라미터 값이 `subae` 인 경우에만 조회 수행 |

## 2. 요청 (Request)

| 항목 | 내용 |
|---|---|
| URL | `/apiv2/findPartInfoWithList` |
| Method | `POST` |
| Content-Type | `application/x-www-form-urlencoded` (Query String 전달도 동작) |
| CORS | 허용 (`@CrossOrigin`) |

### 2.1 요청 파라미터

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|---|---|---|---|---|
| `key` | String | Y | 인증 키. `subae` 가 아니면 빈 배열 반환 | `subae` |
| `PartNoList` | String | Y | 품번들을 `,`로 연결한 문자열. 대소문자 변환 없음(입력값 그대로 조회), 각 품번의 앞뒤 공백은 제거됨 | `2117040001,2117040002,2117040003` |

> 파라미터명 `PartNoList` 는 **대문자 P**로 시작한다. (`partNoList` 로 보내면 바인딩되지 않음)

### 2.2 요청 예시

```bash
curl -X POST "https://vault-in.hdel.co.kr:8070/apiv2/findPartInfoWithList" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "key=subae" \
  --data-urlencode "PartNoList=2117040001,2117040002,2117040003"
```

```javascript
// jQuery
$.ajax({
    url  : "/apiv2/findPartInfoWithList",
    type : "POST",
    data : { key : "subae", PartNoList : "2117040001,2117040002,2117040003" },
    success : function (list) { console.log(list); }
});
```

## 3. 응답 (Response)

| 항목 | 내용 |
|---|---|
| Content-Type | `application/json` |
| 반환 타입 | `ArrayList<PartDTO>` (JSON Array) |
| 조회 결과 없음 | 빈 배열 `[]` (HTTP 200) |

### 3.1 응답 필드 (`PartDTO`)

| JSON 키 | 타입 | 조회 컬럼 | 설명                |
|---|---|---|---------------------|
| `oid` | String | `A.VF$OUID` | PLM 객체 OID        |
| `partNo` | String | `A.MD$NUMBER` | 품번                |
| `partName` | String | `A.MD$DESC` | 품명                |
| `version` | String | `A.VF$VERSION` | 버전                |
| `nation` | String | `CODN(A.NATION)` | 국가                |
| `desc` | String | - |                     |
| `glCode` | String | `A.G_L_CODE` | G/L 코드            |
| `spec` | String | `A.SPEC` | 사양                |
| `uom` | String | `COD(A.UOM)` | 단위                |
| `partSize` | String | `A.PART_SIZE` |                     |
| `design` | String | `COD(A.DESIGN_USE)` | 설계 사용 여부      |
| `cost` | String | `COD(A.COST_USE)` | 원가 사용 여부      |
| `originDiv` | String | `CODN(A.ORIGIN_DIV)` | 품목                |
| `blockNo` | String | `A.BLOCKNO_NUMBER` | 블록 번호           |
| `blockName` | String | - | 블럭명              |
| `status` | String | `CODN(A.PART_STATUS)` | 부품 상태           |
| `active` | String | - | 활성여부            |
| `creDate` | String | `SUBSTR(A.MD$CDATE,0,8)` | 생성일자 `YYYYMMDD` |
| `modDate` | String | `SUBSTR(A.MD$MDATE,0,8)` | 수정일자 `YYYYMMDD` |
| `div` | String | - | 최초구분            |
| `disAway` | String | - | 폐기여부            |

### 3.2 응답 예시

```json
[
  {
    "oid": "2483920011",
    "partNo": "2117040001",
    "partName": "GUIDE RAIL BRACKET",
    "version": "A.1",
    "nation": "한국",
    "desc": null,
    "glCode": "1010",
    "spec": "SS400",
    "uom": "EA",
    "partSize": "100*100*10",
    "design": "사용",
    "cost": "사용",
    "originDiv": "국산",
    "blockNo": "P1170400",
    "blockName": null,
    "status": "승인",
    "active": null,
    "creDate": "20240115",
    "modDate": "20250320",
    "div": null,
    "disAway": null
  }
]
```

## 4. 처리 로직

1. `key` 가 `subae` 가 아니면 조회하지 않고 빈 배열 반환.
2. `PartNoList` 를 `,` 로 분리 → 각 항목 `trim()` → **빈 문자열 및 중복 품번 제거**.
3. 유효 품번이 하나도 없으면 빈 배열 반환.
4. 품번 개수만큼 바인딩 변수(`?`)를 생성하여 `AND A.MD$NUMBER IN (?, ?, ...)` 조건절 구성.
   - 품번은 SQL 에 직접 연결하지 않고 `PreparedStatement` 로 바인딩 (SQL Injection 방지).
5. 기본 필터: `SUBSTR(A.BLOCKNO_NUMBER, 2, 1) IN ('1','2','3')` — 해당 블록 구분에 속한 부품만 조회된다.
6. 조회 결과를 `PartDTO` 로 매핑하여 리스트로 반환.

## 5. 유의사항 / 제약

- **요청 품번 개수와 응답 건수는 일치하지 않을 수 있다.**
  - 존재하지 않는 품번, 블록 구분 필터(`'1','2','3'`)에 해당하지 않는 품번은 결과에서 제외된다.
  - 동일 품번의 버전(`VF$VERSION`)이 여러 건이면 복수 건이 반환될 수 있다.
  - 중복 입력된 품번은 조회 전에 1건으로 정리된다.
- 품번 개수만큼 `IN` 바인딩 변수가 생성되므로, 대량(수천 건) 요청 시 DB의 `IN` 절 파라미터 제한에 걸릴 수 있다. 필요 시 분할 호출 권장.
- DB 오류 발생 시에도 **HTTP 200 + 빈 배열**이 반환되고 예외는 서버 로그(stack trace)에만 남는다. 호출 측은 빈 배열을 "조회 결과 없음"과 "오류"로 구분할 수 없다.
- 단건 조회는 `GET /apiv2/findPartOneWithPartNo?key=subae&partNo=...` 사용.

## 6. 상태 코드

| 코드 | 설명 |
|---|---|
| 200 | 정상 (결과 없음 포함, `[]` 반환) |
| 405 | Method Not Allowed — `GET` 으로 호출한 경우 |
| 500 | 서버 오류 (Spring 레벨 예외) |
