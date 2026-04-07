package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.AddressedEchoMessage;

public final class AddressedEchoMessageFormatter {
	
	public static final String ECHO_MESSAGE_PREFIX = ">> ";

	private AddressedEchoMessageFormatter() {
	}

	public static String format(AddressedEchoMessage message) {
		return ECHO_MESSAGE_PREFIX + message.messageText();
	}
}
