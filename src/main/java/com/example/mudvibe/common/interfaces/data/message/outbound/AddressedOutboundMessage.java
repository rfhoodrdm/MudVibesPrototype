package com.example.mudvibe.common.interfaces.data.message.outbound;

import java.util.UUID;

import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;

public sealed interface AddressedOutboundMessage extends OutboundMessage permits AddressedSystemNotificationMessage {

	public UUID recipientPlayerId();
}
