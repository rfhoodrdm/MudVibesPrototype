package com.example.mudvibe.transport.outbound.Session;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.transport.outbound.messagepublisher.OutboundMessagePublisher;
import com.example.mudvibe.util.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionController  extends TextWebSocketHandler {
	
	public static final String WEBSOCKET_TRASPORT_ERROR_MESSAGE = "[system] The gears that move the world rattle and churn suspiciously...." ;
	public static final String WEBSOCKET_INCOMING_MESSAGE = "[system] The void does not acknowledge your shout.";
	
	private final SessionManager sessionManager;
	private final OutboundMessagePublisher messagePublisher;
	private final SecurityUtil securityUtil;
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		UUID playerId = securityUtil.extractPlayerId(session)
				.orElseGet(() -> securityUtil.getPlayerId());
		session.getAttributes().putIfAbsent(SecurityUtil.PLAYER_ID_ATTRIBUTE, playerId);
		sessionManager.addSession(session);
		
		messagePublisher.sendSimpleMessageToSession(session, new GreetingMessage() );
		log.debug("Session established for player {}. Session id: {}", playerId, session.getId());
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sessionManager.removeSession(session);
		log.debug("Session closed. Session id: {} Status: {}", session != null ? session.getId() : "<null>", status);
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		log.error("Transport error for session {}: {}", session != null ? session.getId() : "<null>", exception.getMessage());
		log.trace("Transport error stack trace", exception);
		if (session != null && session.isOpen()) {
			try {
				session.sendMessage(new TextMessage(WEBSOCKET_TRASPORT_ERROR_MESSAGE));
			} catch (IOException ex) {
				log.warn("Unable to send transport error message to session {}: {}", session.getId(), ex.getMessage());
			}
		}
		sessionManager.removeSession(session);
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		UUID playerId = securityUtil.extractPlayerId(session)
				.orElseGet(() -> securityUtil.getPlayerId());
		log.debug("Received websocket text message from player {}.", playerId);
		log.trace("Incoming websocket payload: {}", message != null ? message.getPayload() : "<null>");
        try {
            session.sendMessage(new TextMessage(WEBSOCKET_INCOMING_MESSAGE));
        } catch (IOException ex) {
            log.warn("Unable to send message to session {}: {}", session.getId(), ex.getMessage());
            sessionManager.removeSession(session);
        }
	}
}
