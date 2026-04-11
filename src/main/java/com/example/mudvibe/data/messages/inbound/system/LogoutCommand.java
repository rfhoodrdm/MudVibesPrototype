package com.example.mudvibe.data.messages.inbound.system;

import java.util.Objects;
import java.util.UUID;

public record LogoutCommand(String rawCommandText, UUID commandingPlayerId, String characterName ) implements IncomingCharacterManagementCommand {
	
	public LogoutCommand {
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(commandingPlayerId, "Player Id may not be null.");
		Objects.requireNonNull(characterName, "Character Name may not be null.");
	}
}
