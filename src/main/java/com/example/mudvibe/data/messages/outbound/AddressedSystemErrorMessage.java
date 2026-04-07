package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

public record AddressedSystemErrorMessage(UUID recipientPlayerId, String messageText) implements AddressedOutboundMessage {

}
