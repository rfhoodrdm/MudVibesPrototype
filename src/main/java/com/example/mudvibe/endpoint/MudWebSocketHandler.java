package com.example.mudvibe.endpoint;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MudWebSocketHandler extends TextWebSocketHandler {

    private final MudMessageGateway messageGateway;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        messageGateway.registerSession(session);
        messageGateway.sendTo(session, "[system] Connected to MudVibes.");
        messageGateway.sendTo(session, "[system] Type a command and press submit to begin exploring.");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload == null || payload.trim().isEmpty()) {
            messageGateway.sendTo(session, "[system] Please enter a command before submitting.");
            return;
        }

        String sanitized = payload.trim();
        log.debug("Received input '{}' from session {}", sanitized, session.getId());
        messageGateway.sendTo(session, "[echo] " + sanitized);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        messageGateway.removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("Transport error for session {}: {}", session != null ? session.getId() : "n/a", exception.getMessage());
        if (session != null && session.isOpen()) {
            messageGateway.sendTo(session, "[system] A connection error occurred.");
        }
    }

    public void broadcast(String payload) {
        messageGateway.broadcast(payload);
    }
}
