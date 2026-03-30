package com.example.mudvibe.common.interfaces.service.session;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.socket.WebSocketSession;

import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingPlayerManagementCommand;
import com.example.mudvibe.common.interfaces.data.message.outbound.SimpleOutboundMessage;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;

public interface SessionManagerService {

	void registerSession(WebSocketSession session);

	void removeSession(WebSocketSession session);

	Set<WebSocketSession> getAllSessions();

	Optional<WebSocketSession> findSessionForPlayerId(UUID playerId);

	void registerSessionToPlayer(WebSocketSession session, UUID playerId);

	Optional<SimpleOutboundMessage> handleCharacterManagementCommand(WebSocketSession session,
			IncomingPlayerManagementCommand ipmc);

	Optional<UUID> findPlayerIdBySession(WebSocketSession session);

	void unregisterSessionFromPlayer(WebSocketSession session, UUID playerId);

}
