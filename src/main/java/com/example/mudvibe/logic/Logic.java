package com.example.mudvibe.logic;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.example.mudvibe.endpoint.MudMessageGateway;
import com.example.mudvibe.state.State;
import com.example.mudvibe.util.SystemClockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class Logic {
	
	private static final Duration HEARTBEAT_INTERVAL = Duration.ofSeconds(5);

	private final State state;
	private final SystemClockUtil clockUtil;
	private final MudMessageGateway messageGateway;
	
	public void update() {
		Instant now = clockUtil.getNow();
		log.debug("Begin update. Current time: {}", now);
		
		Instant previous = state.getLastHeartbeat();
		if (Duration.between(previous, now).compareTo(HEARTBEAT_INTERVAL) >= 0) {
			state.setLastHeartbeat(now);
			messageGateway.broadcast("[system] Server heartbeat at " + now);
		}
	}

}
