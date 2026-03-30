package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

public record AddressedSystemNotificationMessage (UUID recipientPlayerId, String messageText) implements AddressedOutboundMessage {

}
