package com.example.mudvibe.data.messages.inbound.system;

import java.util.Objects;
import java.util.UUID;

public record CharacterRosterListCommand(String rawCommandText, UUID commandingPlayerId) implements IncomingCharacterManagementCommand {

	public CharacterRosterListCommand {
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(commandingPlayerId, "Player Id may not be null.");
	}
}
