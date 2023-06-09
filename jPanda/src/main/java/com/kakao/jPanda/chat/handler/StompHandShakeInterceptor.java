package com.kakao.jPanda.chat.handler;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompHandShakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("[beforeHandshake] {}의 연결시도", request.getURI().getQuery());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        log.info("[afterHandshake] {}가 연결되었습니다.", request.getURI().getQuery());
    }

}
