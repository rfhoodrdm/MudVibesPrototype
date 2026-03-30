package com.example.mudvibe.data.messages.outbound;

import com.example.mudvibe.common.interfaces.data.message.SimpleOutboundMessage;

public record EchoMessage (String messageText) implements SimpleOutboundMessage {
	
}
