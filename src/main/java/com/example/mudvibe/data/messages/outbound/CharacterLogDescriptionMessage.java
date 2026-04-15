package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;
import java.util.UUID;

public record CharacterLogDescriptionMessage(UUID recipientPlayerId, String characterName, LogAction action) implements AddressedOutboundMessage {

	public CharacterLogDescriptionMessage {
		Objects.requireNonNull(recipientPlayerId, "Recipient player id may not be null.");
		Objects.requireNonNull(characterName, "characterName may not be null.");
		Objects.requireNonNull(action, "Log action may not be null.");
	}
	
	public static enum LogAction{
		LOGIN,
		LOGOUT;
	}
}
