package com.murder.user.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知 WebSocket 处理器
 */
@Slf4j
@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    // 存储用户ID和WebSocket会话的映射
    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 连接建立后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数中获取用户ID
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String userId = query.split("userId=")[1].split("&")[0];
            Long uid = Long.parseLong(userId);
            USER_SESSIONS.put(uid, session);
            log.info("WebSocket连接建立: userId={}, sessionId={}", uid, session.getId());
        } else {
            log.warn("WebSocket连接缺少userId参数: sessionId={}", session.getId());
            session.close();
        }
    }

    /**
     * 接收到消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("收到WebSocket消息: sessionId={}, message={}", session.getId(), message.getPayload());
        // 心跳检测
        if ("ping".equals(message.getPayload())) {
            session.sendMessage(new TextMessage("pong"));
        }
    }

    /**
     * 连接关闭后
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除会话
        USER_SESSIONS.entrySet().removeIf(entry -> entry.getValue().getId().equals(session.getId()));
        log.info("WebSocket连接关闭: sessionId={}, status={}", session.getId(), status);
    }

    /**
     * 发送异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输异常: sessionId={}", session.getId(), exception);
        if (session.isOpen()) {
            session.close();
        }
        USER_SESSIONS.entrySet().removeIf(entry -> entry.getValue().getId().equals(session.getId()));
    }

    /**
     * 推送通知给指定用户
     */
    public void pushNotification(Long userId, Map<String, Object> notification) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(notification);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("推送通知成功: userId={}, notification={}", userId, notification);
            } catch (IOException e) {
                log.error("推送通知失败: userId={}", userId, e);
            }
        } else {
            log.debug("用户不在线或会话已关闭: userId={}", userId);
        }
    }

    /**
     * 推送通知给所有在线用户
     */
    public void pushNotificationToAll(Map<String, Object> notification) {
        USER_SESSIONS.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString(notification);
                    session.sendMessage(new TextMessage(jsonMessage));
                    log.debug("推送通知成功: userId={}", userId);
                } catch (IOException e) {
                    log.error("推送通知失败: userId={}", userId, e);
                }
            }
        });
        log.info("推送通知给所有在线用户完成，在线用户数: {}", USER_SESSIONS.size());
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineUserCount() {
        return USER_SESSIONS.size();
    }
}
