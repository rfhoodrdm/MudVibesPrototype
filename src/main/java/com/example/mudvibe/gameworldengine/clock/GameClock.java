package com.example.mudvibe.gameworldengine.clock;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private AtomicBoolean clockActive = new AtomicBoolean(true);
	
    @Scheduled(fixedRate = UPDATE_TICK_TIME_MS, initialDelay = 1000L)
    public void tick() {
    	if (clockActive.get()) {
    		log.debug("Clock not active. Skipping update.");
    		return;
    	}
    	
        for (GameTickSubscriber subscriber: gameTickSubscriberList) {
        	try {
        		log.trace("Tick.");
        		subscriber.update();
        	} catch (Exception ex) {
        		log.error("Exception thrown on game tick for subscriber: {}  Exception: {}", subscriber.getClass(), ex.getLocalizedMessage());
        	}
        }
    }
    
    public void stopClock() {
    	log.info("Stopping game clock for updates.");
    	clockActive.set(false);
    }
    
    public void restartClock() {
    	log.info("Restarting game clock for updates.");
    	clockActive.set(true);
    }
}
