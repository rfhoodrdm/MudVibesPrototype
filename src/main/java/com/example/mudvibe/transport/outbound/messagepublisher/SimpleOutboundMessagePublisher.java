package com.example.mudvibe.transport.outbound.messagepublisher;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.SimpleOutboundMessage;
import com.example.mudvibe.transport.outbound.Session.SessionManager;
import com.example.mudvibe.transport.outbound.messageformatter.OutboundMessageFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleOutboundMessagePublisher implements OutboundMessagePublisher {
	
	private final SessionManager sessionManager;
	private final OutboundMessageFormatter messageFormatter;


    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	public void sendAddressedMessage(AddressedOutboundMessage message) {
		log.debug("Inside sendAddressedMessage(). Recipient: {} Message: {}", message.recipientPlayerId(), message);
		String messagePayload = messageFormatter.formatMessage(message);
		
		UUID playerId = message.recipientPlayerId();
		sessionManager.getSessionByPlayerId(playerId)
			.ifPresentOrElse(   session -> sendTo(session, messagePayload),
								() -> log.warn("No session found for player with id: " + playerId));
	}

	@Override
	public void sendBroadcastMessage(SimpleOutboundMessage message) {
		log.debug("Inside sendBroadcastMessage(). Message: {}", message);
		
		String messagePayload = messageFormatter.formatMessage(message);
		sessionManager.getAllSessions()
			.forEach(session -> sendTo(session, messagePayload));
	}

	@Override
	public void sendSimpleMessageToSession(WebSocketSession session, SimpleOutboundMessage message) {
		String sessionId = Optional.ofNullable(session)
				.map(WebSocketSession::getId)
				.orElse("<null session>");
		String formattedMessageText = messageFormatter.formatMessage(message);
		
		log.debug("Inside sendDirectMessageToSession(), Session id: {}    Message: {}", sessionId, message);
		sendTo(session, formattedMessageText);
	}

     
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
    
	private void sendTo(WebSocketSession session, String messagePayload) {
		if (session == null || !session.isOpen()) {
			log.warn("No open session. Could not transmit message: {}", messagePayload);
			sessionManager.removeSession(session);
			return;
		}

		try {
			session.sendMessage(new TextMessage(messagePayload));
		} catch (IOException ex) {
			log.warn("Unable to send message to session {}: {}", session.getId(), ex.getMessage());
			sessionManager.removeSession(session);
		}
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
     
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
}
