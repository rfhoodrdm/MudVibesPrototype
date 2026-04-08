package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;

public record EchoMessage (String messageText) implements SimpleOutboundMessage {
	
	public EchoMessage {
		Objects.requireNonNull(messageText, "Message text may not be null.");
	}
}
