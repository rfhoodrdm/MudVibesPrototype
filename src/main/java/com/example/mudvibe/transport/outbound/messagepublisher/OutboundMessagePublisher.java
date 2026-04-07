package com.example.mudvibe.transport.outbound.messagepublisher;

import org.springframework.web.socket.WebSocketSession;

import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.SimpleOutboundMessage;

public interface OutboundMessagePublisher {

	public void sendAddressedMessage( AddressedOutboundMessage message);
	
	public void sendBroadcastMessage( SimpleOutboundMessage message);
	
	public void sendSimpleMessageToSession(WebSocketSession session, SimpleOutboundMessage message);
}
