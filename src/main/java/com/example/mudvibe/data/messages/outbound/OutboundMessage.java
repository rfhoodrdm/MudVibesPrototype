package com.example.mudvibe.data.messages.outbound;

public sealed interface OutboundMessage permits SimpleOutboundMessage, AddressedOutboundMessage {

}
