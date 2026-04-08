package com.example.mudvibe.data.messages.outbound;

import java.util.Objects;

public record SystemErrorMessage (String messageText) implements SimpleOutboundMessage {
    public SystemErrorMessage {
        Objects.requireNonNull(messageText, "Message text may not be null.");
    }
}
