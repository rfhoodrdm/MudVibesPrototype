package com.example.mudvibe.data.messages.outbound;

import com.example.mudvibe.common.interfaces.data.message.outbound.SimpleOutboundMessage;

public record GreetingMessage() implements SimpleOutboundMessage {

	@Override
	public String messageText() {
		return """
				**************************************************
				*         Welcome to MudVibes Prototype!         *
				************************************************** 
				Create a new character with REGISTER <character name>
				or log in with an existing character with LOGIN <character name> to proceed. """;
	}

}
