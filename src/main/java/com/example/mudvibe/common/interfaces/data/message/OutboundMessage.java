package com.example.mudvibe.common.interfaces.data.message;

public sealed interface OutboundMessage permits SimpleOutboundMessage, AddressedOutboundMessage {

}
