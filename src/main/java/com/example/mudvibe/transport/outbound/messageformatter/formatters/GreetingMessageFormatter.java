package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.GreetingMessage;

public class GreetingMessageFormatter {

	public static String format(GreetingMessage message) {
		return message.messageText();
	}

}
