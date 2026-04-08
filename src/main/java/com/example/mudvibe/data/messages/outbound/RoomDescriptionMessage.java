package com.example.mudvibe.data.messages.outbound;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.example.mudvibe.common.enums.MovementDirection;

public record RoomDescriptionMessage(UUID recipientPlayerId, String roomTitle, String roomDescription,
        List<MovementDirection> exits, Set<String> otherCharacters) implements AddressedOutboundMessage {

    public RoomDescriptionMessage {
        Objects.requireNonNull(recipientPlayerId, "Recipient player id may not be null.");
        Objects.requireNonNull(roomTitle, "Room title may not be null.");
        Objects.requireNonNull(roomDescription, "Room description may not be null.");
        exits = exits == null ? List.of() : List.copyOf(exits);
        otherCharacters = otherCharacters == null ? Set.of() : Set.copyOf(otherCharacters);
    }
}
