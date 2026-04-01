package com.example.mudvibe.data.messages.inbound.character;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record LookCommand(String rawCommandText, UUID commandingPlayerId, 
		Optional<String> targetNameMaybe, Optional<Integer> targetOrdinalMaybe) implements IncomingCharacterCommand {

	public LookCommand{
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(commandingPlayerId, "Player Id may not be null.");
		
		Objects.requireNonNull(targetNameMaybe, "Target name should be Optional with value, or empty.");
		Objects.requireNonNull(targetOrdinalMaybe, "Target ordinal should be Optional with value, or empty.");
	}
}
