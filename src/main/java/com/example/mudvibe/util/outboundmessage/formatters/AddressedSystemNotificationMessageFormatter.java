package com.example.mudvibe.util.outboundmessage.formatters;

import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;

public class AddressedSystemNotificationMessageFormatter {
	
	public static final String SYSTEM_ERROR_MESSAGE_PREFIX = "[system] ";

	public static String format(AddressedSystemNotificationMessage message) {
		return SYSTEM_ERROR_MESSAGE_PREFIX + message.messageText();
	}

}
