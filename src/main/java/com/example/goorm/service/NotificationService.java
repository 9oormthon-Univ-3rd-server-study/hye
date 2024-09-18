package com.example.goorm.service;

import com.example.goorm.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationService {

    private final Map<String,SseEmitter> emitters = new ConcurrentHashMap<>();
    private final long timeout = 60 * 60 * 1000L;

    public SseEmitter addEmitter(User user) {
        log.info("등록 : {}",user.getId());
        SseEmitter emitter = new SseEmitter(timeout);
        emitters.put(user.getId(),emitter);
        try {
            emitter.send(SseEmitter.event().name("연결").id(user.getId()).data("연결 성공"));
        }
        catch (Exception e){
            log.info(e.getMessage());
        }
        emitter.onCompletion(() -> emitters.remove(user.getId()));
        emitter.onTimeout(() -> emitters.remove(user.getId()));
        return  emitter;
    }

    public void sendEvent(User user, String receivedId){
        log.info("알림 전송 : 수신자 {}",receivedId);
        SseEmitter emitter = emitters.get(receivedId);
        try {
            emitter.send(SseEmitter.event().name("쪽찌 발생").id(receivedId).data(user.getNickname()+"님에게 쪽지가 왔습니다.").reconnectTime(1000L));
        }
        catch (Exception e){
            log.info(e.getMessage());
        }

    }
}
