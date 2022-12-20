package com.hakop.sseovervoew.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api")
public class SSESocketOverviewController {

    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private static final String[] REG_INFO = "This app describe SSE using simple registration model.".split(" ");

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/info", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter info(@RequestHeader(name = "Last-Event-ID", required = false) String lastId) {
        SseEmitter sseEmitter = new SseEmitter(TimeUnit.SECONDS.toMillis(2));
        cachedThreadPool.execute(() -> {
            try {
                for (int i = parseLastId(lastId); i < REG_INFO.length; i++) {
                    sseEmitter.send(
                            SseEmitter.event()
                                    .id(String.valueOf(i))
                                    .data(REG_INFO[i])
                    );
                    TimeUnit.SECONDS.sleep(1);
                }
                sseEmitter.complete();
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });
        return sseEmitter;
    }

    private int parseLastId(String lastId) {
        try {
            return Integer.parseInt(lastId) + 1;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
