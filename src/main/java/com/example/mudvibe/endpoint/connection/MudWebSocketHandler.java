package com.example.mudvibe.endpoint.connection;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.mudvibe.common.interfaces.service.session.SessionManagerService;
import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MudWebSocketHandler extends TextWebSocketHandler {
	
	public static final String INVALID_COMMAND_TEXT = "<invalid command text>";

    private final MudMessageGateway messageGateway;
    private final SessionManagerService sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
    	sessionManager.registerSession(session);
    	messageGateway.sendDirectMessageToSession(session, new GreetingMessage());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    	sessionManager.removeSession(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
    	messageGateway.processIncomingCommand(session, message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("Transport error for session {}: {}", session != null ? session.getId() : "n/a", exception.getMessage());
        messageGateway.sendDirectMessageToSession(session, new SystemErrorMessage("A connection error occurred."));
    }

}
