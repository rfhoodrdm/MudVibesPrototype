package com.example.mudvibe.transport.outbound.Session;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.mudvibe.util.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleSessionManager implements SessionManager {

	private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
	private final ConcurrentMap<WebSocketSession, UUID> sessionToPlayer = new ConcurrentHashMap<>();
	private final ConcurrentMap<UUID, WebSocketSession> playerToSession = new ConcurrentHashMap<>();

	private final SecurityUtil securityUtil;

    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/

	@Override
	public Optional<WebSocketSession> getSessionByPlayerId(UUID playerId) {
		if (playerId == null) {
			return Optional.empty();
		}

		WebSocketSession session = playerToSession.get(playerId);
		if (session == null) {
			return Optional.empty();
		}

		if (!isSessionActive(session)) {
			removeSession(session);
			return Optional.empty();
		}
		
		return Optional.of(session);
	}

	@Override
	public WebSocketSession addSession(WebSocketSession sessionToAdd) {
		if (sessionToAdd == null) {
			return null;
		}

		sessions.add(sessionToAdd);
		securityUtil.extractPlayerId(sessionToAdd).ifPresent(playerId -> {
			sessionToPlayer.put(sessionToAdd, playerId);
			playerToSession.put(playerId, sessionToAdd);
		});

		return sessionToAdd;
	}

	@Override
	public WebSocketSession removeSession(WebSocketSession sessionToRemove) {
		if (sessionToRemove == null) {
			return null;
		}

		sessions.remove(sessionToRemove);
		Optional.ofNullable(sessionToPlayer.remove(sessionToRemove))
				.ifPresent(playerId -> playerToSession.remove(playerId, sessionToRemove));

		return sessionToRemove;
	}

	@Override
	public WebSocketSession removeSessionByPlayerId(UUID playerId) {
		if (playerId == null) {
			return null;
		}

		WebSocketSession session = playerToSession.remove(playerId);
		if (session != null) {
			sessions.remove(session);
			sessionToPlayer.remove(session, playerId);
		}

		return session;
	}

	@Override
	public List<UUID> removeInactiveSessions() {
		List<UUID> removedPlayers = new ArrayList<>();

		sessions.removeIf(session -> {
			boolean inactive = !isSessionActive(session);
			if (inactive) {
				Optional.ofNullable(sessionToPlayer.remove(session)).ifPresent(playerId -> {
					playerToSession.remove(playerId, session);
					removedPlayers.add(playerId);
				});
			}
			return inactive;
		});

		return removedPlayers;
	}

	@Override
	public List<WebSocketSession> getAllSessions() {
		return new ArrayList<>(sessions);
	}
	


    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/

	private boolean isSessionActive(WebSocketSession session) {
		return session != null && session.isOpen();
	}
	


    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
     
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/

}
