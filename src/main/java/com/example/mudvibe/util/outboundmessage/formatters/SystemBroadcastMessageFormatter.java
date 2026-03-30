package com.example.mudvibe.util.outboundmessage.formatters;

import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;

public class SystemBroadcastMessageFormatter {
	
	public static final String SYSTEM_BROADCAST_MESSAGE_PREFIX = "[system] ";
	
	public static String format(SystemBroadcastMessage message) {
		return SYSTEM_BROADCAST_MESSAGE_PREFIX + message.messageText();
	}
}
