package com.example.mudvibe.data.messages.outbound;

public record SystemErrorMessage (String messageText) implements SimpleOutboundMessage {

}
