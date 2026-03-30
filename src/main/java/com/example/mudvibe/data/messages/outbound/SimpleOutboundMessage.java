package com.example.mudvibe.data.messages.outbound;

public sealed interface SimpleOutboundMessage extends OutboundMessage 
	permits EchoMessage, SystemErrorMessage, GreetingMessage, SystemBroadcastMessage, SimpleSystemResponseMessage { 

	public String messageText();
}
