package com.example.mudvibe.service.player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.PlayerRegistrationException;
import com.example.mudvibe.common.exception.PlayerSaveDataException;
import com.example.mudvibe.common.interfaces.service.player.PlayerManagerService;
import com.example.mudvibe.common.interfaces.service.player.PlayerStorageService;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;
import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages players who are currently active in the world and coordinates persistence.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SimplePlayerManagerService implements PlayerManagerService {
	
	private final PlayerStorageService storageService;
	
	private final Map<UUID, PlayerCharacterDataRecord> activePlayersById = new ConcurrentHashMap<>();
	private final Map<String, UUID> activePlayersByName = new ConcurrentHashMap<>();

    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	/**
	 * 1) Name must be valid.
	 * 2) Character name must not already exist. If it does, throw a PlayerRegistrationException
	 * 3) Create the new character.
	 * 	3a) Mark the character as active.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Optional<PlayerCharacterDataRecord> registerCharacter(String playerName) throws PlayerRegistrationException {
		log.debug("Inside registerCharacter(). Player name: {}", playerName);
		var sanitizedName = Optional.ofNullable(playerName)
				.map(CharacterNameNormalizationUtil::sanitize)
				.orElseThrow( () -> new PlayerRegistrationException("Player name cannot be blank."));
		
		if (storageService.findByName(sanitizedName).isPresent()) {
			throw new PlayerRegistrationException("Player name already exists.");
		}
		
		var newPlayer = new PlayerCharacterDataRecord();
		newPlayer.setPlayerName(sanitizedName);
		
		try {
			var savedPlayer = storageService.savePlayerData(newPlayer);
			savedPlayer.ifPresent(this::markPlayerActive);
			return savedPlayer;
		} catch (PlayerSaveDataException ex) {
			throw new PlayerRegistrationException("Unable to save new player.", ex);
		}
	}
	
	/**
	 * 1) Name must be valid.
	 * 2) If character is already logged in, throw a CharacterLoginException: Can't be logged in by more than one person.
	 * 3) Mark the character as active.
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<PlayerCharacterDataRecord> loginCharacter(String playerName) throws CharacterLoginException {
		log.debug("Inside loginCharacter(). Player name: {}", playerName);
		
		var sanitizedName = CharacterNameNormalizationUtil.sanitize(playerName);
		if (sanitizedName == null) {
			log.debug("Leaving loginCharacter(). Player name blank.");
			return Optional.empty();
		}
		
		var normalizedName = CharacterNameNormalizationUtil.normalize(sanitizedName);
		if (activePlayersByName.containsKey(normalizedName)) {
			throw new CharacterLoginException("Character is already logged in.");
		}
		
		var storedPlayer = storageService.findByName(sanitizedName);
		storedPlayer.ifPresent(this::markPlayerActive);
		return storedPlayer;
	}
	
	@Override
	public Optional<PlayerCharacterDataRecord> logoutCharacter(UUID characterId) throws PlayerSaveDataException {
		log.debug("Inside logoutCharacter(). Player id: {}", characterId);
		
		if (characterId == null) {
			log.debug("Leaving logoutCharacter(). Player id null.");
			return Optional.empty();
		}
		
		PlayerCharacterDataRecord removed = activePlayersById.remove(characterId);
		if (removed == null) {
			log.debug("Player not active. Player id: {}", characterId);
			return Optional.empty();
		}
		
		activePlayersByName.remove(CharacterNameNormalizationUtil.normalize(removed.getPlayerName()), characterId);
		storageService.savePlayerData(removed);
		return Optional.of(removed);
	}
	

	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	private void markPlayerActive(PlayerCharacterDataRecord record) {
		var sanitizedName = CharacterNameNormalizationUtil.sanitize(record.getPlayerName());
		if (record == null || record.getCharacterId() == null || sanitizedName == null) {
			return;
		}
		
		var normalizedName = CharacterNameNormalizationUtil.normalize(sanitizedName);
		activePlayersById.put(record.getCharacterId(), record);
		activePlayersByName.put(normalizedName, record.getCharacterId());
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
	
	@Override
	public void saveAllCharacterData() {
		log.debug("Inside saveAllCharacterData(). Active player count: {}", activePlayersById.size());
		storageService.saveAllPlayerData(activePlayersById.values());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PlayerCharacterDataRecord> findCharacterByName(String playerName) {
		return storageService.findByName(playerName);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PlayerCharacterDataRecord> findCharacterById(UUID characterId) {
		return storageService.findById(characterId);
	}
}
