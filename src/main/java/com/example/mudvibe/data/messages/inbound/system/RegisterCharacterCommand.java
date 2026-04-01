package com.example.mudvibe.data.messages.inbound.system;

import java.util.Objects;

public record RegisterCharacterCommand(String rawCommandText, String playerName) implements IncomingPlayerManagementCommand {

	public RegisterCharacterCommand {
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(playerName, "Player Name may not be null.");
	}
}
