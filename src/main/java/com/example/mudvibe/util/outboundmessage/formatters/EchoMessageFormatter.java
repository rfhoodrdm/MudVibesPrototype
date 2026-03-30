package com.example.mudvibe.util.outboundmessage.formatters;

import com.example.mudvibe.data.messages.outbound.EchoMessage;

public class EchoMessageFormatter {
	
	public static final String ECHO_MESSAGE_PREFIX = ">> ";

	public static String format(EchoMessage message) {
		return ECHO_MESSAGE_PREFIX + message.messageText();
	}
}
