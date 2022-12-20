package com.hakop.sseovervoew.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@EnableScheduling
public class SseSessionCache {
    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Value("${sse.ttl.in.seconds}")
    private Long sseTtlInSeconds;

    public SseEmitter create(String key) {
        SseEmitter existingEmitter = sseEmitterMap.get(key);
        if (existingEmitter != null) {
            existingEmitter.complete();
            log.info("SSE Session with key {} already exist. Existed completed and removed. New one created.", key);
            sseEmitterMap.remove(key);
        }
        SseEmitter newEmitter = new SseEmitter(TimeUnit.SECONDS.toMillis(sseTtlInSeconds)); // new SseEmitter(Long.MAX_VALUE);
        log.info("SSE Session with key {} created.", key);
        sseEmitterMap.put(key, newEmitter);
        return newEmitter;
    }

    private SseEmitter get(String key) {
        SseEmitter emitter = sseEmitterMap.get(key);
        if (emitter == null) {
            log.error("SSE Session with key {} not exist.", key);
        }
        return emitter;
    }

    public SseEmitter release(String key) {
        SseEmitter emitter = get(key);
        if (emitter != null) {
            sseEmitterMap.remove(key);
        }
        return emitter;
    }
}
