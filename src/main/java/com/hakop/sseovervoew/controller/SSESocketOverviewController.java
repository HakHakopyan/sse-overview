package com.hakop.sseovervoew.controller;

import com.hakop.sseovervoew.cache.SseSessionCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SSESocketOverviewController {
    private final SseSessionCache sseSessionCache;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "register/{name}/start/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter register(@PathVariable("name") String name) throws IOException {
        final String requestId = UUID.randomUUID().toString();
        SseEmitter sseEmitter = sseSessionCache.create(requestId);
        sseEmitter.send(
                SseEmitter.event()
                        .name("REG_START")
                        .id(requestId)
                        .data(String.format("%s your registration is stored.", name))
        );
        return sseEmitter;
    }

    @PostMapping("register/result")
    public String result(@RequestParam("request_id") final String requestId,
                         @RequestParam("result") final boolean result) {
        SseEmitter sseEmitter = sseSessionCache.release(requestId);
        if (sseEmitter == null) {
            return "Such registration request not exist";
        }
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(requestId)
                    .name("REG_RESULT")
                    .data(String.format("Registration result is: %s", result ? "success" : "denied"))
            );
        } catch (Exception ex) {
            log.error("Error on sending Registration result with reqID {}: {}", requestId, ex.getMessage());
            return "There is Error on user notification: " + ex.getMessage();
        }
        sseEmitter.complete();
        return "User success notified.";
    }
}
