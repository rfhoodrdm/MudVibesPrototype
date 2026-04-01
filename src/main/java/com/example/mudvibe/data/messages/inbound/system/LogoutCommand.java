package com.example.mudvibe.data.messages.inbound.system;

import java.util.Objects;
import java.util.UUID;

import com.example.mudvibe.common.exception.InvalidCommandException;

public record LogoutCommand(String rawCommandText, String playerName, UUID commandingPlayerId) implements IncomingPlayerManagementCommand {
	
	public LogoutCommand {
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(commandingPlayerId, "Player Id may not be null.");
		Objects.requireNonNull(playerName, "Player Name may not be null.");
	}
}
