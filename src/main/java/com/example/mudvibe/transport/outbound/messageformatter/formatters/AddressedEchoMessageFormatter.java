package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.AddressedEchoMessage;

public final class AddressedEchoMessageFormatter {

	private AddressedEchoMessageFormatter() {
	}

	public static String format(AddressedEchoMessage message) {
		return "[echo] " + message.messageText();
	}
}
