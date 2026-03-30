package com.example.mudvibe.data.messages.outbound;

public record EchoMessage (String messageText) implements SimpleOutboundMessage {
	
}
