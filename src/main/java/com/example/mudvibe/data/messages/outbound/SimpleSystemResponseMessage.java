package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;

public record SimpleSystemResponseMessage(String messageText) implements SimpleOutboundMessage {

	public SimpleSystemResponseMessage {
		Objects.requireNonNull(messageText, "Message text may not be null.");
	}
}
