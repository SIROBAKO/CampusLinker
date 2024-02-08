package com.hako.web.config;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class CustomInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // WebSocket 핸드셰이크 이전의 동작을 정의합니다.

        // Sec-WebSocket-Protocol 헤더가 없을 때 기본 헤더를 추가합니다.
        if (request.getHeaders() == null || request.getHeaders().get("Sec-WebSocket-Protocol") == null) {
            response.getHeaders().add("Sec-WebSocket-Protocol", "default-protocol");
        }

        
        // Sec-WebSocket-Key를 생성하여 헤더에 추가합니다.
        String secWebSocketKey = generateSecWebSocketKey();
        response.getHeaders().add("Sec-WebSocket-Key", secWebSocketKey);
        
        // Sec-WebSocket-Key 확인 및 출력
        List<String> secWebSocketKeyHeaders = request.getHeaders().get("Sec-WebSocket-Key");
        if (secWebSocketKeyHeaders != null && !secWebSocketKeyHeaders.isEmpty()) {
            secWebSocketKey = secWebSocketKeyHeaders.get(0);
            System.out.println("Sec-WebSocket-Key 헤더: " + secWebSocketKey);
            // 여기에서 Sec-WebSocket-Key 헤더를 확인하고 필요한 작업을 수행할 수 있습니다.
        } else {
            System.out.println("Sec-WebSocket-Key 헤더가 없습니다.");
        }

        // 업그레이드 헤더 확인 및 출력
        List<String> upgradeHeaders = request.getHeaders().get("Upgrade");
        if (upgradeHeaders != null && !upgradeHeaders.isEmpty()) {
            String upgradeHeader = upgradeHeaders.get(0);
            System.out.println("Upgrade 헤더: " + upgradeHeader);
            // 여기에서 Upgrade 헤더를 확인하고 필요한 작업을 수행할 수 있습니다.
        } else {
            System.out.println("Upgrade 헤더가 없습니다.");
        }

        // 요청 헤더 확인 (예: Authorization 헤더)
        List<String> authorizationHeaders = request.getHeaders().get("Authorization");
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String authorization = authorizationHeaders.get(0);
            System.out.println("Authorization 헤더: " + authorization);
            // 여기에서 Authorization 헤더를 확인하고 필요한 작업을 수행할 수 있습니다.
        } else {
            System.out.println("Authorization 헤더가 없습니다.");
        }

        return true; // true를 반환하면 핸드셰이크 계속 진행됩니다.
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        // WebSocket 핸드셰이크 이후의 동작을 정의합니다.

    
        
    }

    // Sec-WebSocket-Key 생성 메서드
    private String generateSecWebSocketKey() {
        byte[] key = new byte[16];
        new Random().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
