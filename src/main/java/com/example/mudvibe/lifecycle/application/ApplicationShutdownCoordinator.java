package com.example.mudvibe.lifecycle.application;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.example.mudvibe.gameworldengine.clock.GameClock;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationShutdownCoordinator implements ApplicationListener<ContextClosedEvent> {

	private final PlayerCharacterManager playerCharacterManager;
	private final GameClock gameClock;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		log.info("Initiating game server shutdown procedure.");
		
		gameClock.stopClock();
		
		boolean saveAllCharactersSuccessResult = playerCharacterManager.saveAllActiveCharacters();
		if (!saveAllCharactersSuccessResult) {
			log.error("Failed to save all activa characters at shutdown.");
		}
		
		log.info("Shutdown procedure completed.");
	}

}
