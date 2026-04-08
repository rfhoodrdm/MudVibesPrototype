package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;
import java.util.UUID;

public record AddressedEchoMessage(UUID recipientPlayerId, String messageText) implements AddressedOutboundMessage {
	public AddressedEchoMessage {
		Objects.requireNonNull(recipientPlayerId, "Recipient player id may not be null.");
		Objects.requireNonNull(messageText, "Message text may not be null.");
	}
}
