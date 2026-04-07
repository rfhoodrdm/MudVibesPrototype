//package com.example.mudvibe.service.session;
//
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.socket.WebSocketSession;
//
//import com.example.mudvibe.common.exception.CharacterLoginException;
//import com.example.mudvibe.common.exception.PlayerRegistrationException;
//import com.example.mudvibe.common.exception.PlayerCharacterSaveDataException;
//import com.example.mudvibe.common.interfaces.service.player.PlayerManagerService;
//import com.example.mudvibe.common.interfaces.service.session.SessionManagerService;
//import com.example.mudvibe.data.messages.inbound.system.IncomingPlayerManagementCommand;
//import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
//import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
//import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
//import com.example.mudvibe.data.messages.outbound.SimpleOutboundMessage;
//import com.example.mudvibe.data.messages.outbound.SimpleSystemResponseMessage;
//import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;
//import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class SimpleSessionManagerService implements SessionManagerService {
//
//    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
//    private final ConcurrentMap<WebSocketSession, UUID> sessionToPlayer = new ConcurrentHashMap<>();
//    private final ConcurrentMap<UUID, WebSocketSession> playerToSession = new ConcurrentHashMap<>();
//
//    private final PlayerManagerService playerManagerService;
//    
//    /* ********************************************************
//     * 					    Public Methods
//     * ********************************************************/
//    
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//	public void registerSession(WebSocketSession session) {
//		String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
//		log.debug("Inside registerSession(). Session id: {}", sessionId);
//		sessions.add(session);
//	}
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//	public void removeSession(WebSocketSession session) {
//		String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
//		log.debug("Inside removeSession(). Session id: {}", sessionId);
//		if (session == null) {
//			log.debug("Leaving removeSession(). Session is null.");
//			return;
//		}
//
//		//We also need to remove the players from the collection of active players in the player manager service.
//		sessions.remove(session);
//		Optional.ofNullable(sessionToPlayer.remove(session))
//			.ifPresent(playerId -> {
//				log.debug("Removing player mapping for session {}. Player id: {}", sessionId, playerId);
//				playerToSession.remove(playerId, session);
//			});
//	}
//
//    @Override
//    @Transactional(readOnly = true)
//	public Set<WebSocketSession> getAllSessions() {
//		return sessions;
//	}
//
//    @Override
//    @Transactional(readOnly = true)
//	public Optional<WebSocketSession> findSessionForPlayerId(UUID playerId) {
//		log.debug("Inside findSessionForPlayerId(). Player id: {}", playerId);
//		return Optional.ofNullable(playerToSession.get(playerId));
//	}
//    
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void registerSessionToPlayer(WebSocketSession session, UUID playerId) {
//    	String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
//    	log.debug("Inside registerSessionToPlayer(). Session id: {}. Player id: {}", sessionId, playerId);
//    	
//    	if(session == null || playerId == null) {
//    		log.debug("Leaving registerSessionToPlayer(). Session or player id was null.");
//    		return;
//    	}
//    	
//    	sessionToPlayer.put(session, playerId);
//    	playerToSession.put(playerId, session);
//    }
//    
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void unregisterSessionFromPlayer(WebSocketSession session, UUID playerId) {
//    	String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
//    	log.debug("Inside unregisterSessionFromPlayer(). Session id: {}. Player id: {}", sessionId, playerId);
//    	
//    	if(session == null || playerId == null) {
//    		log.debug("Leaving unregisterSessionFromPlayer(). Session or player id was null.");
//    		return;
//    	}
//    	
//    	sessionToPlayer.remove(session, playerId);
//    	playerToSession.remove(playerId, session);
//    }
//    
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<UUID> findPlayerIdBySession(WebSocketSession session) {
//    	String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
//    	log.debug("Inside findPlayerIdBySession(). Session id: {}", sessionId);
//    	return Optional.ofNullable(sessionToPlayer.get(session));
//    }
//
//	@Override
//	public Optional<SimpleOutboundMessage> handleCharacterManagementCommand(WebSocketSession session, IncomingPlayerManagementCommand ipmc) {
//		if (session == null || ipmc == null) {
//			return Optional.of(new SystemErrorMessage("Invalid session or command."));
//		}
//		
//		return switch (ipmc) {
//			case LoginCommand lic -> handleCharacterLoginCommand(session, lic);
//			case RegisterCharacterCommand rcc -> handleRegisterCharacterCommand(session, rcc);
//			case LogoutCommand loc -> handleCharacterLogoutCommand(session, loc);
//		};
//	}
//	
//	
//    /* ********************************************************
//     * 					    Helper Methods
//     * ********************************************************/
//	
//
//	private Optional<SimpleOutboundMessage> handleCharacterLoginCommand(WebSocketSession session, LoginCommand lic) {
//		var sanitizedName = CharacterNameNormalizationUtil.sanitize(lic.playerName());
//		if (sanitizedName == null) {
//			return Optional.of(new SystemErrorMessage("Character name is required to login."));
//		}
//		
//		if (findPlayerIdBySession(session).isPresent()) {
//			return Optional.of(new SystemErrorMessage("This connection already controls a character."));
//		}
//		
//		try {
//			var loginResult = playerManagerService.loginCharacter(sanitizedName);
//			if (loginResult.isEmpty()) {
//				return Optional.of(new SystemErrorMessage("Character '" + sanitizedName + "' does not exist."));
//			}
//			
//			var characterRecord = loginResult.get();
//			if (findSessionForPlayerId(characterRecord.getCharacterId()).isPresent()) {
//				return Optional.of(new SystemErrorMessage("That character is already active on another connection."));
//			}
//			
//			registerSessionToPlayer(session, characterRecord.getCharacterId());
//			return Optional.of(new SimpleSystemResponseMessage("Logged in as " + characterRecord.getPlayerName() + "."));
//		} catch (CharacterLoginException ex) {
//			return Optional.of(new SystemErrorMessage(ex.getMessage()));
//		}
//	}
//
//	private Optional<SimpleOutboundMessage> handleRegisterCharacterCommand(WebSocketSession session, RegisterCharacterCommand rcc) {
//		var sanitizedName = CharacterNameNormalizationUtil.sanitize(rcc.playerName());
//		if (sanitizedName == null) {
//			return Optional.of(new SystemErrorMessage("Character name is required to register."));
//		}
//		
//		if (findPlayerIdBySession(session).isPresent()) {
//			return Optional.of(new SystemErrorMessage("Please logout before creating another character."));
//		}
//		
//		try {
//			var created = playerManagerService.registerCharacter(sanitizedName);
//			if (created.isEmpty()) {
//				return Optional.of(new SystemErrorMessage("Unable to create character '" + sanitizedName + "'."));
//			}
//			
//			var characterRecord = created.get();
//			registerSessionToPlayer(session, characterRecord.getCharacterId());
//			return Optional.of(new SimpleSystemResponseMessage("Character '" + characterRecord.getPlayerName() + "' created and logged in."));
//		} catch (PlayerRegistrationException ex) {
//			return Optional.of(new SystemErrorMessage(ex.getMessage()));
//		}
//	}
//	
//	private Optional<SimpleOutboundMessage> handleCharacterLogoutCommand(WebSocketSession session, LogoutCommand loc) {
//		var currentCharacterOpt = findPlayerIdBySession(session);
//		if (currentCharacterOpt.isEmpty()) {
//			return Optional.of(new SystemErrorMessage("No character logged in on this connection."));
//		}
//		
//		var sanitizedRequestedName = CharacterNameNormalizationUtil.sanitize(loc.playerName());
//		if (sanitizedRequestedName != null) {
//			var normalizedRequested = CharacterNameNormalizationUtil.normalize(sanitizedRequestedName);
//			var activeRecord = playerManagerService.findCharacterById(currentCharacterOpt.get());
//			var normalizedActive = activeRecord
//					.map(record -> CharacterNameNormalizationUtil.normalize(record.getPlayerName()))
//					.orElse(null);
//			
//			if (normalizedRequested != null && normalizedActive != null && !normalizedRequested.equals(normalizedActive)) {
//				return Optional.of(new SystemErrorMessage("You are logged in as '" +
//						activeRecord.map(record -> record.getPlayerName()).orElse("unknown") +
//						"', not '" + sanitizedRequestedName + "'."));
//			}
//		}
//		
//		try {
//			playerManagerService.logoutCharacter(currentCharacterOpt.get());
//			unregisterSessionFromPlayer(session, currentCharacterOpt.get());
//			return Optional.of(new SimpleSystemResponseMessage("Logged out successfully."));
//		} catch (PlayerCharacterSaveDataException ex) {
//			return Optional.of(new SystemErrorMessage("Unable to logout: " + ex.getMessage()));
//		}
//	}
//    
//    /* ********************************************************
//     * 	                    Deferred Methods
//     * ********************************************************/
//
//}
