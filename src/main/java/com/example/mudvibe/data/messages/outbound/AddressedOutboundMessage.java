package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

import com.example.mudvibe.data.messages.outbound.interfaces.HasPlayerIdRecipient;

public sealed interface AddressedOutboundMessage extends OutboundMessage, HasPlayerIdRecipient
	permits AddressedSystemNotificationMessage, CommandProcessingErrorMessage, AddressedEchoMessage, AddressedSystemErrorMessage, 
		RoomDescriptionMessage, SpeechDescriptionMessage, MoveCharacterDescriptionMessage, CharacterRosterDescriptionMessage {

	public UUID recipientPlayerId();
}
