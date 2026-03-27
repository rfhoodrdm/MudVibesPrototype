package com.example.mudvibe.endpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MudMessageGateway {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public void registerSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    public void sendTo(WebSocketSession session, String payload) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(payload));
        } catch (IOException ex) {
            log.warn("Unable to send message to session {}: {}", session.getId(), ex.getMessage());
        }
    }

    public void broadcast(String payload) {
        sessions.stream()
            .filter(WebSocketSession::isOpen)
            .forEach(session -> sendTo(session, payload));
    }
}
