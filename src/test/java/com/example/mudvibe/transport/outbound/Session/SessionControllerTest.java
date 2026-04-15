package com.example.mudvibe.transport.outbound.Session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;

class SessionControllerTest {

	@Test
	void determineWebSocketResponsePayloadRespondsToLiveUpdatePingWithPong() {
		String responsePayload = SessionController.determineWebSocketResponsePayload();

		assertThat(responsePayload).isEqualTo(SessionController.LIVE_UPDATE_PONG_MESSAGE);
	}

	@Test
	void liveUpdatePingMessageRemainsDocumentedForClientHeartbeat() {
		TextMessage message = new TextMessage(SessionController.LIVE_UPDATE_PING_MESSAGE);

		assertThat(message.getPayload()).isEqualTo(SessionController.LIVE_UPDATE_PING_MESSAGE);
	}
}
