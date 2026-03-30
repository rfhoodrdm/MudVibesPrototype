package com.example.mudvibe.common.interfaces.service.message;

import com.example.mudvibe.common.interfaces.data.message.outbound.AddressedOutboundMessage;

public interface OutboundMessagePublisher {

	public void deliverOutBoundMessage(AddressedOutboundMessage outboundMessage);
}
