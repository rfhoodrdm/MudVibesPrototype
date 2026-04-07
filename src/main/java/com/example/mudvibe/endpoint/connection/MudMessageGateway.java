//package com.example.mudvibe.endpoint.connection;
//
//import java.io.IOException;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import com.example.mudvibe.common.exception.InvalidCommandException;
//import com.example.mudvibe.common.exception.UnknownCommandException;
//import com.example.mudvibe.common.interfaces.service.message.IncomingCommandQueueService;
//import com.example.mudvibe.common.interfaces.service.message.OutboundMessagePublisher;
//import com.example.mudvibe.common.interfaces.service.session.SessionManagerService;
//import com.example.mudvibe.data.messages.inbound.IncomingCommand;
//import com.example.mudvibe.data.messages.inbound.character.IncomingCharacterCommand;
//import com.example.mudvibe.data.messages.inbound.system.IncomingPlayerManagementCommand;
//import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
//import com.example.mudvibe.data.messages.outbound.EchoMessage;
//import com.example.mudvibe.data.messages.outbound.OutboundMessage;
//import com.example.mudvibe.data.messages.outbound.SimpleOutboundMessage;
//import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;
//import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;
//import com.example.mudvibe.util.incomingcommand.IncomingTextCommandParserUtil;
//import com.example.mudvibe.util.outboundmessage.OutboundMessageFormatterUtil;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class MudMessageGateway implements OutboundMessagePublisher {
//    
//    private final IncomingTextCommandParserUtil commandParser;
//    private final IncomingCommandQueueService commandQueue;
//    private final OutboundMessageFormatterUtil outboundMessageFormatter;
//    
//	private final SessionManagerService sessionManager;
//
//    /* ********************************************************
//     * 					    Public Methods
//     * ********************************************************/
//    
//    /**
//     * For sending message to game client, not a player.
//     */
//    public void sendDirectMessageToSession(WebSocketSession session, OutboundMessage message) {
//		String sessionId = Optional.ofNullable(session)
//				.map(WebSocketSession::getId)
//				.orElse("<null session>");
//    	log.debug("Inside sendDirectMessageToSession(), Session id: {}    Message: {}", sessionId, message);
//    	sendTo(session, message);
//    }
//
//	/**
//	 * 1) Echo the command back to the user to acknowledge receipt.
//	 * 2) Parse the command and attempt to validate.
//	 *   - If command is valid, then enqueue for processing.
//	 *   - If not, then respond with an error message.
//	 */
//	public void processIncomingCommand(WebSocketSession session, String messagePayload) {
//		log.debug("Inside processCommand(). Session id: {}. Message payload: {}", session.getId(), messagePayload);
//				
//		var echo = new EchoMessage(messagePayload);
//		sendTo(session, echo);
//	
//		Optional<UUID> commandingPlayerIdMaybe = sessionManager.findPlayerIdBySession(session);
//		Optional<? extends OutboundMessage> responseMaybe = Optional.empty(); //follow-up result to send, if any.
//
//		try {
//			var parsedCommandResult = commandParser.parseCommand(messagePayload, commandingPlayerIdMaybe);
//			responseMaybe = (parsedCommandResult.isValid())
//					?  routeCommand(session, parsedCommandResult.parsedCommand())
//					:  Optional.of(new SystemErrorMessage("Invalid command: " + messagePayload));
//		} catch (InvalidCommandException | UnknownCommandException ex) {
//			log.trace("Command was not valid: ", ex);	//this will probably happen a lot, so no need to log higher than trace.
//			responseMaybe = Optional.of(new SystemErrorMessage("Command was not valid: " + ex.getMessage()));
//		} catch (Exception ex) {
//			log.error("An exception occurred processing a command: {}", ex.toString());
//			log.trace("Command processing stack trace", ex);
//			responseMaybe = Optional.of(new SystemErrorMessage("An error occured parsing command: " + messagePayload));
//		}
//    	
//    	responseMaybe.ifPresent(message -> sendTo(session, message));
//	}
//
//	public void deliverOutBoundMessage(AddressedOutboundMessage addressedOutboundMessage) {
//		UUID playerId = addressedOutboundMessage.recipientPlayerId();
//	
//		try {
//			findWebsocketSessionForPlayer(playerId).ifPresentOrElse(
//					session -> sendTo(session, addressedOutboundMessage), 
//					() -> log.warn("Skipping message transmission: Could not find an available session for player with id {}", playerId)
//				);
//		} catch (Exception ex) {
//			//prevent an exception from killing whatever process is going on. Log the exception, though.
//			log.error("Could not send an addressed message to a player due to an exception: {}", ex.getLocalizedMessage());
//			log.trace("Stack trace: ", ex);
//		}
//
//	}
//
//	public void sendSystemBroadcast(SystemBroadcastMessage broadcastMessage) {
//		sessionManager.getAllSessions().stream()
//				.filter(WebSocketSession::isOpen)
//				.forEach(session -> sendTo(session, broadcastMessage));
//	}
//	
//    /* ********************************************************
//     * 					    Helper Methods
//     * ********************************************************/
//	
//	/**
//	 * Route commands to where they need to go to be processed.
//	 * Simple results or error messages may be returned. Complex updates, e.g. from character actions, will need to be routed.
//	 */
//	private Optional<SimpleOutboundMessage> routeCommand(WebSocketSession session, IncomingCommand command) {
//		
//		return switch (command) {
//			case IncomingPlayerManagementCommand ipmc -> sessionManager.handleCharacterManagementCommand(session, ipmc);
//			case IncomingCharacterCommand icc-> commandQueue.enqueueCommand(icc);
//			case null, default -> Optional.of(new SystemErrorMessage("Unrecognized command: " + command.rawCommandText()));
//		};
//	}
//	
//	private void sendTo(WebSocketSession session, OutboundMessage message) {
//		String formattedMessage = outboundMessageFormatter.formatOutboundMessage(message);
//		sendTo(session, formattedMessage);
//	}
//	
//    private void sendTo(WebSocketSession session, String simpleMessage) {
//        if (session == null || !session.isOpen()) {
//        	log.warn("No open session. Could not transmit message: {}", simpleMessage);
//        	sessionManager.removeSession(session);
//            return;
//        }
//        
//        try {
//            session.sendMessage(new TextMessage(simpleMessage));
//        } catch (IOException ex) {
//            log.warn("Unable to send message to session {}: {}", session.getId(), ex.getMessage());
//            sessionManager.removeSession(session);
//        }
//    }	
//    
//	private Optional<WebSocketSession> findWebsocketSessionForPlayer(UUID playerId) {
//		return sessionManager.findSessionForPlayerId(playerId);
//	}
//	
//    /* ********************************************************
//     * 	                    Deferred Methods
//     * ********************************************************/
//
//}
