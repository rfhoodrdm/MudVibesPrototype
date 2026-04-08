package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;

public record SystemBroadcastMessage (String messageText) implements SimpleOutboundMessage {
    public SystemBroadcastMessage {
        Objects.requireNonNull(messageText, "Message text may not be null.");
    }
}
