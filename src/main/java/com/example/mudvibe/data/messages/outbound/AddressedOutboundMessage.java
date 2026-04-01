package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

public sealed interface AddressedOutboundMessage extends OutboundMessage 
	permits AddressedSystemNotificationMessage, CommandProcessingErrorMessage,
	RoomDescriptionMessage {

	public UUID recipientPlayerId();
}
