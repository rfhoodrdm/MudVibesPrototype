package com.example.mudvibe.util.outboundmessage.formatters;

import com.example.mudvibe.data.messages.outbound.GreetingMessage;

public class GreetingMessageFormatter {

	public static String format(GreetingMessage message) {
		return message.messageText();
	}

}
