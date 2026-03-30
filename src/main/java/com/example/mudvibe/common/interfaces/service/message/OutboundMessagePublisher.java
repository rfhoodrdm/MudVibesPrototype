package com.example.mudvibe.common.interfaces.service.message;

import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;

public interface OutboundMessagePublisher {

	public void deliverOutBoundMessage(AddressedOutboundMessage outboundMessage);
}
