package com.hakop.sseovervoew.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api")
public class SSESocketOverviewController {

    @Value("${sse.ttl.in.seconds}")
    private Long sseTtlInSeconds;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter register() throws IOException {
        SseEmitter sseEmitter = new SseEmitter(TimeUnit.SECONDS.toMillis(sseTtlInSeconds));

        sseEmitter.send("Open Connection!");

        return sseEmitter;
    }
}
