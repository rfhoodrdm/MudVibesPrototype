package com.example.mudvibe.state;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class State {

	private final AtomicReference<Instant> lastHeartbeat = new AtomicReference<>(Instant.EPOCH);

	public Instant getLastHeartbeat() {
		return lastHeartbeat.get();
	}

	public void setLastHeartbeat(Instant instant) {
		lastHeartbeat.set(instant);
	}
}
