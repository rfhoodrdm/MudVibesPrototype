package com.example.mudvibe.common.interfaces.data.message;

import com.example.mudvibe.data.messages.outbound.EchoMessage;
import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;
import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;

public sealed interface SimpleOutboundMessage extends OutboundMessage permits EchoMessage, SystemErrorMessage, GreetingMessage, SystemBroadcastMessage { 

	public String messageText();
}
