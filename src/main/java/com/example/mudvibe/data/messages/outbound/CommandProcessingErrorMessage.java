package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

public record CommandProcessingErrorMessage(UUID recipientPlayerId, String messageText, String rawCommand) implements AddressedOutboundMessage {

}
