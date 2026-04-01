package com.example.mudvibe.data.messages.inbound.character;

import java.util.Objects;
import java.util.UUID;

import com.example.mudvibe.common.enums.MovementDirection;

public record MoveCharacterCommand(String rawCommandText, UUID commandingPlayerId, MovementDirection direction) implements IncomingCharacterCommand {

	public MoveCharacterCommand {
		Objects.requireNonNull(rawCommandText, "Raw command text may not be null.");
		Objects.requireNonNull(commandingPlayerId, "Player Id may not be null.");
		Objects.requireNonNull(direction, "Direction may not be null.");
	}
}
