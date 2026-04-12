package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;
import java.util.UUID;

import com.example.mudvibe.common.enums.MovementDirection;

public record MoveCharacterDescriptionMessage(UUID recipientPlayerId, String characterNameMoving, TransitDirection transitDirection,
		MovementDirection movementDirection, Boolean isOneWay) 
	implements AddressedOutboundMessage {
	
	public MoveCharacterDescriptionMessage {
		Objects.requireNonNull(recipientPlayerId, "recipientPlayerId cannot be null");
		Objects.requireNonNull(characterNameMoving, "characterNameMoving cannot be null");
		Objects.requireNonNull(transitDirection, "transitDirection cannot be null");
		Objects.requireNonNull(isOneWay, "isOneWay cannot be null");
		
		//later, once portals/items are made at least one of these will have to be non-null.
//		if (movementDirection == null && portalName == null) {
//			throw new IllegalArgumentException("At least one of movementDirection or portalName must be provided.");
//		}
	}

	public static enum TransitDirection {
		ARRIVING,
		LEAVING;
	}
}

