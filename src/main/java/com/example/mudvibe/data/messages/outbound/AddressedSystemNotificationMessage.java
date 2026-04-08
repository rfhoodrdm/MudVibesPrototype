package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;
import java.util.UUID;

public record AddressedSystemNotificationMessage (UUID recipientPlayerId, String messageText) implements AddressedOutboundMessage {
    public AddressedSystemNotificationMessage {
        Objects.requireNonNull(recipientPlayerId, "Recipient player id may not be null.");
        Objects.requireNonNull(messageText, "Message text may not be null.");
    }
}
