package com.example.mudvibe.data.messages.outbound;

import java.util.UUID;

import com.example.mudvibe.common.interfaces.data.message.outbound.AddressedOutboundMessage;

public record AddressedSystemNotificationMessage (UUID recipientPlayerId, String messageText) implements AddressedOutboundMessage {

}
