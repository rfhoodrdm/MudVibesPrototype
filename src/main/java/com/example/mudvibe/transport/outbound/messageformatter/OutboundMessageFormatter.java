package com.example.mudvibe.transport.outbound.messageformatter;

import com.example.mudvibe.data.messages.outbound.OutboundMessage;

public interface OutboundMessageFormatter {

	public String formatMessage(OutboundMessage message);

}
