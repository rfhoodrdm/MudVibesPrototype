package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.AddressedSystemErrorMessage;

public final class AddressedSystemErrorMessageFormatter {

	private static final String PREFIX = "[system] ";

	private AddressedSystemErrorMessageFormatter() {
	}

	public static String format(AddressedSystemErrorMessage message) {
		return PREFIX + message.messageText();
	}
}
