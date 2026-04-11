package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

public record SpeechDescriptionMessage(UUID recipientPlayerId, String speaker, String speechModeVerb, String speech) implements AddressedOutboundMessage {

}
