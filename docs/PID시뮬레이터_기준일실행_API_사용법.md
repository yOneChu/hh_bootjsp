# PID 시뮬레이터 기준일 실행 API 사용법

> 엔드포인트 : `POST /simulateup/simulatePidAsOf`
> 원본 로직 : `com.kyhslam.controller.simulate.PIDSimulatorUpController#simulatePidAsOf`
> → `com.kyhslam.service.simulate.PIDSimulatorUpService#simulateAsOf`
> → `com.kyhslam.util.pidSimulatorUp.PIDAsOfSimul#run`
> 관련 화면 : `/simulateup/pidSimulatorUp` (`thymeleaf/pid/pidSimulatorUp.html`) 의 '최종 OUTPUT'
> 최초 개발 : 2026-11-11 / 최종 수정 : 2026-11-18

---

## 1. 개요

**호기번호 + 기준일 + PID** 를 넘기면, 그 기준일 시점에 유효했던 PID 버전으로 실행한 **최종 OUTPUT(KEY → 값)** 을 돌려준다.

- 대상 PID 뿐 아니라 **CALL 로 호출되는 하위 PID 와 전처리 PID 도 모두 같은 기준일 버전**으로 실행된다.
- 결과는 PID 시뮬레이터(Up) 화면의 **'최종 OUTPUT'** 과 같다 (`debugData`, `elvEnt` 는 제외).

### 주요 용도

- 특정 일자에 PID 가 어떤 값을 냈는지 재현
- 일자별로 호출해서 PID 변경 전/후 OUTPUT 비교
- 외부 시스템에서 PID 실행 결과만 필요한 경우

---

## 2. 요청

| 항목 | 값 |
|---|---|
| Method | `POST` (쿼리스트링 또는 `application/x-www-form-urlencoded` 폼) |
| URL | `http://{서버}:8070/simulateup/simulatePidAsOf` |
| CORS | 허용 (`@CrossOrigin`) |

### 파라미터 (모두 필수)

| 이름 | 설명 | 예시 |
|---|---|---|
| `hogi` | 호기번호(공사번호). 공사정보 OUID(`$`, `@` 포함)도 받는다 | `206663L01` |
| `date` | 기준일. `yyyyMMdd` 또는 `yyyy-MM-dd` | `20261001` |
| `pid` | 실행할 PID | `EL_A101A` |

> JSON 바디(`application/json`)로는 받지 않는다. `@RequestParam` 이므로 쿼리스트링이나 폼 파라미터로 보내야 한다.

---

## 3. 버전 선택 규칙

각 PID(대상 · 전처리 · CALL 하위)마다 아래 조건으로 버전을 하나 고른다.

1. `VARIANT_H.REG_DATE` 가 **기준일 당일 23:59:59 이전**(당일 포함)인 버전 중
2. **가장 늦게 등록된 버전** (등록일시가 같으면 버전 번호가 큰 것)
3. **TEST 버전(-1)은 제외**

```sql
SELECT * FROM (
  SELECT A.VERSION, A.REG_DATE, A.REMARKS
  FROM VARIANT_H A
  WHERE A.PID = :pid
    AND A.VERSION <> -1
    AND A.REG_DATE < TO_DATE(:asOfDate, 'YYYY-MM-DD') + 1
  ORDER BY A.REG_DATE DESC, A.VERSION DESC
) WHERE ROWNUM = 1
```

---

## 4. 응답

### 성공 (200)

최종 OUTPUT 을 `KEY → 값` JSON 객체로 돌려준다. 키 순서는 실행 결과 순서다.

```json
{
  "EL_XXXX": "값1",
  "EL_YYYY": "값2",
  "...": "..."
}
```

> 위 키/값은 예시다. 실제 키는 PID 마다 다르다.

### 실패 (500)

```json
{ "error": "오류 메시지" }
```

입력 오류도 400 이 아니라 **500** 으로 돌려준다. `error` 메시지로 원인을 구분한다.

| 상황 | `error` 메시지 |
|---|---|
| `hogi` 가 비어 있음 | `호기번호(hogi) 가 없습니다.` |
| `pid` 가 비어 있음 | `PID 가 없습니다.` |
| 날짜 형식 오류 | `날짜 형식이 잘못되었습니다 (yyyyMMdd) : {date}` |
| 없는 날짜 (예: 20261332) | `없는 날짜입니다 (yyyyMMdd) : {date}` |
| 호기번호를 찾을 수 없음 | `No data : hogi={hogi}` |
| 기준일 이전에 대상 PID 버전이 없음 | `{pid} 의 {yyyy-MM-dd} 이전 버전이 없습니다.` |
| PID 실행 중 오류 | 실행 엔진의 오류 메시지 |

> 파라미터 자체를 빼먹으면(`hogi` 키 없음 등) 스프링이 컨트롤러 진입 전에 **400 Bad Request** 를 돌려준다.

### 요청 과다 (429)

동시에 실행 중인 요청이 상한(기본 5건)을 넘고 대기 시간(기본 3초) 안에 자리가 나지 않으면 **429 Too Many Requests** 를 돌려준다. `Retry-After: 5` 헤더가 붙으니 잠시 후 다시 호출한다.

```json
{ "error": "요청이 많아 처리하지 못했습니다. 잠시 후 다시 시도하세요." }
```

---

## 4-1. 부하 보호 (캐시 / 동시 실행 제한)

| 항목 | 내용 |
|---|---|
| 결과 캐시 | 같은 `(hogi, date, pid)` 의 **성공 결과**를 10분간 보관해서 바로 돌려준다. 오류는 보관하지 않는다. `20261001` 과 `2026-10-01` 은 같은 요청으로 본다 |
| 동시 실행 제한 | 캐시에 없어 실제로 실행하는 요청만 동시 5건까지. 넘으면 최대 3초 기다린 뒤 429 |
| 적용 범위 | 이 API 만. 시뮬레이터 화면 등 다른 기능에는 적용되지 않는다 |

> 영업사양은 현재 값을 쓰므로, 영업사양이 바뀐 직후에는 최대 캐시 유지 시간(10분) 동안 이전 결과가 나올 수 있다.

설정 (`application.properties`, 없으면 기본값) :

```properties
pid.asof.max-concurrent=5       # 동시 실행 수
pid.asof.wait-seconds=3         # 자리 대기 시간(초)
pid.asof.cache-ttl-minutes=10   # 캐시 유지 시간(분), 0 이면 캐시 끔
pid.asof.cache-max-size=1000    # 캐시 최대 건수
```

---

## 5. 호출 예시

### curl

```bash
curl -X POST "http://localhost:8070/simulateup/simulatePidAsOf" \
     -d "hogi=206663L01" -d "date=20261001" -d "pid=EL_A101A"
```

### JavaScript (fetch)

```javascript
const qs = new URLSearchParams({ hogi: '206663L01', date: '20261001', pid: 'EL_A101A' });
const res = await fetch(`/simulateup/simulatePidAsOf?${qs}`, {
    method: 'POST',
    headers: { Accept: 'application/json' }
});
const data = await res.json();
if (!res.ok) throw new Error(data.error);
console.log(data);   // { KEY: 값, ... }
```

### Java (RestTemplate)

```java
MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
form.add("hogi", "206663L01");
form.add("date", "20261001");
form.add("pid", "EL_A101A");

Map<String, Object> output = new RestTemplate().postForObject(
        "http://localhost:8070/simulateup/simulatePidAsOf", form, Map.class);
```

### Postman

- Method : `POST`
- URL : `http://localhost:8070/simulateup/simulatePidAsOf`
- Body : `x-www-form-urlencoded` → `hogi`, `date`, `pid` 입력 (또는 Params 탭에 입력)

---

## 6. 주의사항

1. **영업사양(공사정보)은 기준일 값이 아니라 현재 값**을 쓴다. 기준일이 적용되는 것은 PID 버전뿐이다.
2. **층(floor) 지정 없이** 공사정보 기준으로 실행한다.
3. **하위 PID 가 기준일 이전 버전이 없어도 오류가 나지 않는다.** 해당 하위 PID 만 실행되지 않고 나머지 결과를 돌려준다. 이 경고는 응답에 없고 **서버 로그**(`[PIDSimul AsOf] ... 이전 버전이 없어 실행하지 못했습니다.`)에만 남는다.
4. 실제로 고른 PID 별 버전(`versions`)도 응답에 없고 서버 로그에만 남는다.
   ```
   [PIDSimul AsOf] 206663L01 / EL_A101A v3 기준일=2026-10-01 완료 : 812ms, OUTPUT 42건, 버전 {EL_A101A={version=3, regDate=...}, ...}
   ```
5. TEST 버전(-1)으로 실행해야 한다면 이 API 대신 화면(`/simulateup/pidSimulatorUp`)이나 `GET /simulateup/simulatePid` 를 쓴다.

---

## 7. 관련 소스

| 구분 | 파일 |
|---|---|
| Controller | `src/main/java/com/kyhslam/controller/simulate/PIDSimulatorUpController.java` (`simulatePidAsOf`) |
| Service | `src/main/java/com/kyhslam/service/simulate/PIDSimulatorUpService.java` (`simulateAsOf`) |
| 실행 로직 | `src/main/java/com/kyhslam/util/pidSimulatorUp/PIDAsOfSimul.java` |
| 기준일 버전 선택 | `src/main/java/com/kyhslam/util/pidSimulatorUp/SimPidRepositoryAsOf.java` |
| 부하 보호 (캐시 / 동시 실행 제한) | `src/main/java/com/kyhslam/service/simulate/PIDAsOfGuard.java` |
