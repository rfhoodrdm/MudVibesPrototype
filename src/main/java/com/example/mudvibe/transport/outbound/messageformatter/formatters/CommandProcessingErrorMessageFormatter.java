package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.CommandProcessingErrorMessage;

public class CommandProcessingErrorMessageFormatter {
	public static final String SYSTEM_ERROR_MESSAGE_PREFIX = "[system] ";

	public static String format(CommandProcessingErrorMessage message) {
		return SYSTEM_ERROR_MESSAGE_PREFIX + message.messageText();
	}
}
