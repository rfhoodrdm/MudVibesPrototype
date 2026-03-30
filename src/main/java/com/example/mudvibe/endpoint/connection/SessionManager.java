package com.example.mudvibe.endpoint.connection;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionManager {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<WebSocketSession, UUID> sessionToPlayer = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, WebSocketSession> playerToSession = new ConcurrentHashMap<>();

    
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
    
	public void registerSession(WebSocketSession session) {
		String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
		log.debug("Inside registerSession(). Session id: {}", sessionId);
		sessions.add(session);
	}

	public void removeSession(WebSocketSession session) {
		String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
		log.debug("Inside removeSession(). Session id: {}", sessionId);
		if (session == null) {
			log.debug("Leaving removeSession(). Session is null.");
			return;
		}

		sessions.remove(session);
		Optional.ofNullable(sessionToPlayer.remove(session))
			.ifPresent(playerId -> {
				log.debug("Removing player mapping for session {}. Player id: {}", sessionId, playerId);
				playerToSession.remove(playerId, session);
			});
	}

	public Set<WebSocketSession> getAllSessions() {
		return sessions;
	}

	public Optional<WebSocketSession> findSessionForPlayerId(UUID playerId) {
		log.debug("Inside findSessionForPlayerId(). Player id: {}", playerId);
		return Optional.ofNullable(playerToSession.get(playerId));
	}
    
    public void registerSessionToPlayer(WebSocketSession session, UUID playerId) {
    	String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
    	log.debug("Inside registerSessionToPlayer(). Session id: {}. Player id: {}", sessionId, playerId);
    	
    	if(session == null || playerId == null) {
    		log.debug("Leaving registerSessionToPlayer(). Session or player id was null.");
    		return;
    	}
    	
    	sessionToPlayer.put(session, playerId);
    	playerToSession.put(playerId, session);
    }
    
    public void unregisterSessionFromPlayer(WebSocketSession session, UUID playerId) {
    	String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
    	log.debug("Inside unregisterSessionFromPlayer(). Session id: {}. Player id: {}", sessionId, playerId);
    	
    	if(session == null || playerId == null) {
    		log.debug("Leaving unregisterSessionFromPlayer(). Session or player id was null.");
    		return;
    	}
    	
    	sessionToPlayer.remove(session, playerId);
    	playerToSession.remove(playerId, session);
    }
    
    public Optional<UUID> findPlayerIdBySession(WebSocketSession session) {
    	String sessionId = Optional.ofNullable(session).map(WebSocketSession::getId).orElse("<null>");
    	log.debug("Inside findPlayerIdBySession(). Session id: {}", sessionId);
    	return Optional.ofNullable(sessionToPlayer.get(session));
    }

}
