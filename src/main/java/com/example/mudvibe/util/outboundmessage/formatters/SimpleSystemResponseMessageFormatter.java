package com.example.mudvibe.util.outboundmessage.formatters;

import com.example.mudvibe.data.messages.outbound.SimpleSystemResponseMessage;

public class SimpleSystemResponseMessageFormatter {
	public static final String SYSTEM_RESPONSE_MESSAGE_PREFIX = "[system] ";
	
	public static String format(SimpleSystemResponseMessage message) {
		return SYSTEM_RESPONSE_MESSAGE_PREFIX + message.messageText();
	}
}
