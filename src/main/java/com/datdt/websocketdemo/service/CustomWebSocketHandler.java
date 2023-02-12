package com.datdt.websocketdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {
    @Autowired(required = false)
    private RedissonClient redissonClient;

    @Value("${spring.redis.topic:'datdt'}")
    private String value;

    @Value("${spring.redis.enabled:false}")
    private boolean redisEnabled;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        if (redisEnabled) {
            RTopic topic = redissonClient.getTopic(value, StringCodec.INSTANCE);
            topic.publish(message.getPayload());
        } else {
            String payload = message.getPayload();
            session.sendMessage(new TextMessage(payload));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (redisEnabled) {
            RTopic topic = redissonClient.getTopic(value, StringCodec.INSTANCE);
            int regId = topic.addListener(String.class, (channel, msg) -> {
                try {
                    log.info("send to channel +: " + channel + " message: " + handleMessage(msg));
                    session.sendMessage(new TextMessage(handleMessage(msg)));
                } catch (IOException e) {
                    log.info("sendMessage error", e);
                }
            });
            log.info("regId: " + regId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed");
        super.afterConnectionClosed(session, status);
    }

    private String handleMessage(String payload) {
        return payload;
//        if (payload == null) {
//            return null;
//        }
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            MessageDTO messageDTO = objectMapper.readValue(payload, MessageDTO.class);
//            return messageDTO.getMessage();
//        } catch (JsonProcessingException e) {
//            log.error("jsonToMessageDTO", e);
//        }
//        return null;
    }
}
