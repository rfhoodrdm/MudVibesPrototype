package com.example.mudvibe.data.messages.outbound;

import com.example.mudvibe.common.interfaces.data.message.outbound.SimpleOutboundMessage;

public record SimpleSystemResponseMessage(String messageText) implements SimpleOutboundMessage {

}
