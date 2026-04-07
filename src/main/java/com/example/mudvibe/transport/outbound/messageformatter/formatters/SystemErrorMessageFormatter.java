package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;

public class SystemErrorMessageFormatter {
	
	public static final String SYSTEM_ERROR_MESSAGE_PREFIX = "[system] ";

	public static String format(SystemErrorMessage message) {
		return SYSTEM_ERROR_MESSAGE_PREFIX + message.messageText();
	}

}
