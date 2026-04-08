package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;
import java.util.UUID;

public record CommandProcessingErrorMessage(UUID recipientPlayerId, String messageText, String rawCommand) implements AddressedOutboundMessage {
	public CommandProcessingErrorMessage {
		Objects.requireNonNull(recipientPlayerId, "Recipient player id may not be null.");
		Objects.requireNonNull(messageText, "Message text may not be null.");
		Objects.requireNonNull(rawCommand, "Raw command may not be null.");
	}
}
