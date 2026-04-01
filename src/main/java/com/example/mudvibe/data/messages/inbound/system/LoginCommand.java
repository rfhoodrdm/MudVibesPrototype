package com.example.mudvibe.data.messages.inbound.system;

import java.util.Objects;

public record LoginCommand(String rawCommandText, String playerName) implements IncomingPlayerManagementCommand {

	public LoginCommand{
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(playerName, "Player Name may not be null.");
	}
}
