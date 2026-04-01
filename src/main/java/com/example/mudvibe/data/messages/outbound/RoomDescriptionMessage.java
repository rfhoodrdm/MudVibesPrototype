package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

public record RoomDescriptionMessage(UUID recipientPlayerId, String roomTitle, String roomDescription) implements AddressedOutboundMessage {

}
