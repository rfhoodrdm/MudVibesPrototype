package com.example.mudvibe.endpoint.connection;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingCharacterCommand;
import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingCommand;
import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingPlayerManagementCommand;
import com.example.mudvibe.common.interfaces.data.message.outbound.AddressedOutboundMessage;
import com.example.mudvibe.common.interfaces.data.message.outbound.OutboundMessage;
import com.example.mudvibe.common.interfaces.data.message.outbound.SimpleOutboundMessage;
import com.example.mudvibe.common.interfaces.service.message.IncomingCommandQueueService;
import com.example.mudvibe.common.interfaces.service.message.OutboundMessagePublisher;
import com.example.mudvibe.common.interfaces.service.session.SessionManagerService;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
import com.example.mudvibe.data.messages.outbound.EchoMessage;
import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;
import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;
import com.example.mudvibe.util.incomingcommand.IncomingTextCommandParserUtil;
import com.example.mudvibe.util.outboundmessage.OutboundMessageFormatterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MudMessageGateway implements OutboundMessagePublisher {
    
    private final IncomingTextCommandParserUtil commandParser;
    private final IncomingCommandQueueService commandQueue;
    private final OutboundMessageFormatterUtil outboundMessageFormatter;
    
	private final SessionManagerService sessionManager;

    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
    
    /**
     * For sending message to game client, not a player.
     */
    public void sendDirectMessageToSession(WebSocketSession session, OutboundMessage message) {
		String sessionId = Optional.ofNullable(session)
				.map(WebSocketSession::getId)
				.orElse("<null session>");
    	log.debug("Inside sendDirectMessageToSession(), Session id: {}    Message: {}", sessionId, message);
    	sendTo(session, message);
    }

	/**
	 * 1) Echo the command back to the user to acknowledge receipt.
	 * 2) Parse the command and attempt to validate.
	 *   - If command is valid, then enqueue for processing.
	 *   - If not, then respond with an error message.
	 */
	public void processIncomingCommand(WebSocketSession session, String messagePayload) {
		log.debug("Inside processCommand(). Session id: {}. Message payload: {}", session.getId(), messagePayload);
				
		var echo = new EchoMessage(messagePayload);
		sendTo(session, echo);
	
		var result = commandParser.parseCommand(messagePayload);
    	if (result.isValid()) {
    		routeCommand(session, result.parsedCommand());
    	} else {
    		var errorMessage = new SystemErrorMessage("Invalid command: " + messagePayload);
    		sendTo(session, errorMessage );
    	}	
	}


	public void deliverOutBoundMessage(AddressedOutboundMessage addressedOutboundMessage) {
		UUID playerId = addressedOutboundMessage.recipientPlayerId();
	
		findWebsocketSessionForPlayer(playerId).ifPresentOrElse(
				session -> sendTo(session, addressedOutboundMessage), 
				() -> log.warn("Skipping message transmission: Could not find an available session for player with id {}", playerId)
			);
	}

	public void sendSystemBroadcast(SystemBroadcastMessage broadcastMessage) {
		sessionManager.getAllSessions().stream()
				.filter(WebSocketSession::isOpen)
				.forEach(session -> sendTo(session, broadcastMessage));
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	/**
	 * Route commands to where they need to go to be processed.
	 * Simple results or error messages may be returned. Complex updates, e.g. from character actions, will need to be routed.
	 */
	private void routeCommand(WebSocketSession session, IncomingCommand command) {
		
		Optional<SimpleOutboundMessage> result = switch (command) {
			case IncomingPlayerManagementCommand ipmc -> sessionManager.handleCharacterManagementCommand(session, ipmc);
			case IncomingCharacterCommand icc-> commandQueue.enqueueCommand(icc);
			case null, default -> Optional.of(new SystemErrorMessage("Unrecognized command: " + command));
		};
		
		result.ifPresent(message -> sendTo(session, message));
	}
	
	private void greetNewSession(WebSocketSession session) {
		log.debug("Inside greetNewSession(). Session id: {}", session.getId());
		
		//TODO: refactor to use result sender module.
		var greeting = new GreetingMessage();
		sendTo(session, greeting);
	}
	
	private void sendTo(WebSocketSession session, OutboundMessage message) {
		String formattedMessage = outboundMessageFormatter.formatOutboundMessage(message);
		sendTo(session, formattedMessage);
	}
	
    private void sendTo(WebSocketSession session, String simpleMessage) {
        if (session == null || !session.isOpen()) {
        	log.warn("No open session. Could not transmit message: {}", simpleMessage);
        	sessionManager.removeSession(session);
            return;
        }
        
        try {
            session.sendMessage(new TextMessage(simpleMessage));
        } catch (IOException ex) {
            log.warn("Unable to send message to session {}: {}", session.getId(), ex.getMessage());
            sessionManager.removeSession(session);
        }
    }	
    
	private Optional<WebSocketSession> findWebsocketSessionForPlayer(UUID playerId) {
		return sessionManager.findSessionForPlayerId(playerId);
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

}
