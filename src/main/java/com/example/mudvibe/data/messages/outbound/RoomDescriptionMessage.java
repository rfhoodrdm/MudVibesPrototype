package com.example.mudvibe.data.messages.outbound;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.mudvibe.common.enums.MovementDirection;

public record RoomDescriptionMessage(UUID recipientPlayerId, String roomTitle, String roomDescription, 
		List<MovementDirection> exits, Set<String> otherCharacters) implements AddressedOutboundMessage {

}
