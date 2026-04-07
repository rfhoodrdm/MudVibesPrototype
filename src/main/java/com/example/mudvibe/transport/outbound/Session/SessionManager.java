package com.example.mudvibe.transport.outbound.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

public interface SessionManager {

	public Optional<WebSocketSession> getSessionByPlayerId(UUID playerId);
	
	public WebSocketSession addSession(WebSocketSession sessionToAdd);
	
	public WebSocketSession removeSession(WebSocketSession sessionToRemove);
	
	public WebSocketSession removeSessionByPlayerId(UUID playerId);
	
	public List<UUID> removeInactiveSessions();
	
	public List<WebSocketSession> getAllSessions();
}
