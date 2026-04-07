package com.example.mudvibe.data.messages.outbound;

/**
 * Messages that are not addressed towards any particular user. They have a payload, but no addressee.
 */
public sealed interface SimpleOutboundMessage extends OutboundMessage 
	permits EchoMessage, SystemErrorMessage, GreetingMessage, SystemBroadcastMessage, SimpleSystemResponseMessage { 

	public String messageText();
}
