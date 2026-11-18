package com.kyhslam.service.simulate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 기준일 PID 실행 API (/simulateup/simulatePidAsOf) 부하 보호 : 동시 실행 수 제한 + 결과 캐시
 *   - 캐시 : (호기, 기준일, PID) 로 성공한 결과만 TTL 동안 보관한다. 오류는 저장하지 않는다.
 *            영업사양은 기준일이 아니라 현재 값을 쓰므로 영구 보관하지 않고 TTL 로 만료시킨다.
 *   - 동시 실행 제한 : 캐시에 없어 실제로 실행하는 요청만 제한한다. 자리가 나지 않으면 TooManyRequestsException.
 *   이 API 전용이며 시뮬레이터 화면 등 다른 기능에는 쓰지 않는다.
 *
 * 설정 (선택, 괄호는 기본값)
 *   pid.asof.max-concurrent=5       동시에 실행할 수 있는 요청 수
 *   pid.asof.wait-seconds=3         자리가 날 때까지 기다리는 시간(초). 넘으면 429
 *   pid.asof.cache-ttl-minutes=10   결과 캐시 유지 시간(분). 0 이면 캐시 사용 안 함
 *   pid.asof.cache-max-size=1000    캐시 최대 건수. 넘으면 가장 오래된 것부터 지운다
 */
@Slf4j
@Component
public class PIDAsOfGuard {

    /** 동시 실행 수를 넘어 대기 시간 안에 실행하지 못한 경우 */
    public static class TooManyRequestsException extends RuntimeException {
        public TooManyRequestsException(String message) { super(message); }
    }

    @FunctionalInterface
    public interface AsOfCall {
        Map<String, Object> run() throws Exception;
    }

    private static class Entry {
        final Map<String, Object> value;
        final long expiresAt;
        Entry(Map<String, Object> value, long expiresAt) { this.value = value; this.expiresAt = expiresAt; }
    }

    private final Semaphore permits;
    private final long waitMillis;
    private final long ttlMillis;
    private final int maxSize;

    /** 넣은 순서대로 유지 → 최대 건수를 넘으면 가장 오래된 항목부터 지운다 */
    private final Map<String, Entry> cache;

    public PIDAsOfGuard(@Value("${pid.asof.max-concurrent:5}") int maxConcurrent,
                        @Value("${pid.asof.wait-seconds:3}") long waitSeconds,
                        @Value("${pid.asof.cache-ttl-minutes:10}") long ttlMinutes,
                        @Value("${pid.asof.cache-max-size:1000}") int maxSize) {
        this.permits = new Semaphore(Math.max(1, maxConcurrent), true);
        this.waitMillis = Math.max(0, waitSeconds) * 1000L;
        this.ttlMillis = Math.max(0, ttlMinutes) * 60_000L;
        this.maxSize = Math.max(1, maxSize);
        this.cache = Collections.synchronizedMap(new LinkedHashMap<String, Entry>(16, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
                return size() > PIDAsOfGuard.this.maxSize;
            }
        });
        log.info("[PIDSimul AsOf Guard] 동시 실행 {}건, 대기 {}초, 캐시 TTL {}분, 캐시 최대 {}건",
                maxConcurrent, waitSeconds, ttlMinutes, maxSize);
    }

    /**
     * 캐시에 있으면 그 결과, 없으면 동시 실행 수 안에서 call 을 실행하고 성공한 결과를 캐시에 넣는다.
     */
    public Map<String, Object> execute(String hogi, String date, String pid, AsOfCall call) throws Exception {
        String key = cacheKey(hogi, date, pid);

        Map<String, Object> cached = getCached(key);
        if (cached != null) {
            log.info("[PIDSimul AsOf] 캐시 사용 : {}", key);
            return cached;
        }

        if (!permits.tryAcquire(waitMillis, TimeUnit.MILLISECONDS)) {
            log.warn("[PIDSimul AsOf] 동시 실행 초과로 거절 : {}", key);
            throw new TooManyRequestsException("요청이 많아 처리하지 못했습니다. 잠시 후 다시 시도하세요.");
        }
        try {
            // 기다리는 동안 같은 요청이 먼저 끝나 캐시에 들어갔을 수 있다
            cached = getCached(key);
            if (cached != null) {
                log.info("[PIDSimul AsOf] 캐시 사용 : {}", key);
                return cached;
            }

            Map<String, Object> result = call.run();
            if (ttlMillis > 0 && result != null) {
                Map<String, Object> stored = Collections.unmodifiableMap(new LinkedHashMap<String, Object>(result));
                cache.put(key, new Entry(stored, System.currentTimeMillis() + ttlMillis));
                return stored;
            }
            return result;
        } finally {
            permits.release();
        }
    }

    private Map<String, Object> getCached(String key) {
        if (ttlMillis <= 0)
            return null;
        Entry e = cache.get(key);
        if (e == null)
            return null;
        if (e.expiresAt < System.currentTimeMillis()) {
            cache.remove(key, e);
            return null;
        }
        return e.value;
    }

    /** 호기|기준일(yyyyMMdd)|PID : 앞뒤 공백과 날짜의 '-' 만 정리한다 (yyyy-MM-dd 와 yyyyMMdd 는 같은 키) */
    private static String cacheKey(String hogi, String date, String pid) {
        return trim(hogi) + "|" + trim(date).replace("-", "") + "|" + trim(pid);
    }

    private static String trim(String v) {
        return v == null ? "" : v.trim();
    }
}
