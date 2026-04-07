package com.example.mudvibe.gameworldengine.clock;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameClock {
	
    private static final int TARGET_UPDATES_PER_SECOND = 4;	//TODO: eventually move this to a config property.
    private static final int UPDATE_TICK_TIME_MS = 1000 / TARGET_UPDATES_PER_SECOND;
	
	private final List<GameTickSubscriber> gameTickSubscriberList;
	
    @Scheduled(fixedRate = UPDATE_TICK_TIME_MS, initialDelay = 1000L)
    public void tick() {
        for (GameTickSubscriber subscriber: gameTickSubscriberList) {
        	try {
        		log.trace("Tick.");
        		subscriber.update();
        	} catch (Exception ex) {
        		log.error("Exception thrown on game tick for subscriber {}", subscriber.getClass());
        	}
        }
    }
}
