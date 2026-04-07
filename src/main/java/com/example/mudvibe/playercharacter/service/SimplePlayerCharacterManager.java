package com.example.mudvibe.playercharacter.service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.mudvibe.area.AreaManager;
import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.CharacterLogoutException;
import com.example.mudvibe.common.exception.PlayerCharacterLoadDataException;
import com.example.mudvibe.common.exception.PlayerCharacterRegistrationException;
import com.example.mudvibe.common.exception.PlayerCharacterSaveDataException;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;
import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;
import com.example.mudvibe.util.system.SystemClockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimplePlayerCharacterManager implements PlayerCharacterManager {
	
	private final AreaManager areaManager;
	private final PlayerCharacterStorage playerCharacterStorage;
	private final SystemClockUtil clockUtil;
	
	private final Map<UUID, PlayerCharacterData> activePlayerCharacterMap = new ConcurrentHashMap<>();
	private final Map<String, PlayerCharacterData> currentlyActivePlayerMap = new ConcurrentHashMap<>();
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	public PlayerCharacterData loginPlayerCharacter(UUID playerId, String characterName) throws CharacterLoginException {
		log.debug("Inside loginPlayerCharacter(). Player id: {} Character name: {}", playerId, characterName);
		
		var sanitizedName = validateLoginPlayerCharacterRequest(playerId, characterName);
		
		PlayerCharacterData characterData = loadCharacterData(sanitizedName);
		if (characterData == null) {
			throw new CharacterLoginException("Character does not exist.");
		}
		
		if (characterData.getPlayerId() == null || !characterData.getPlayerId().equals(playerId)) {
			throw new CharacterLoginException("Player does not own this character.");
		}
		
		markCharacterActive(characterData);
		return characterData;
	}

	@Override
	public Optional<PlayerCharacterData> logoutPlayerCharacterByPlayerId(UUID playerId) throws CharacterLogoutException {
		log.debug("Inside logoutPlayerCharacterByPlayerId(). Player id: {}", playerId);
		
		if (playerId == null) {
			return Optional.empty();
		}
		
		PlayerCharacterData removedCharacter = activePlayerCharacterMap.remove(playerId);
		if (removedCharacter == null) {
			return Optional.empty();
		}
		
		removeCharacterFromNameMap(removedCharacter);
		saveCharacterData(removedCharacter);
		return Optional.of(removedCharacter);
	}

	@Override
	public PlayerCharacterData logoutPlayerCharacterByCharacterName(String characterName) {
		log.debug("Inside logoutPlayerCharacterByCharacterName(). Character name: {}", characterName);
		
		var sanitizedName = CharacterNameNormalizationUtil.sanitize(characterName);
		if (sanitizedName == null) {
			return null;
		}
		
		var normalizedName = CharacterNameNormalizationUtil.normalize(sanitizedName);
		PlayerCharacterData removedCharacter = currentlyActivePlayerMap.remove(normalizedName);
		if (removedCharacter == null) {
			return null;
		}
		
		if (removedCharacter.getPlayerId() != null) {
			activePlayerCharacterMap.remove(removedCharacter.getPlayerId(), removedCharacter);
		}
		
		try {
			playerCharacterStorage.savePlayerCharacterData(removedCharacter);
		} catch (PlayerCharacterSaveDataException ex) {
			throw new IllegalStateException("Unable to persist character data during logout.", ex);
		}
		
		return removedCharacter;
	}

	@Override
	public Optional<PlayerCharacterData> getActivePlayerCharacterDataByPlayerId(UUID playerId) {
		if (playerId == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(activePlayerCharacterMap.get(playerId));
	}

	@Override
	public PlayerCharacterData registerPlayerCharacter(UUID playerId, String characterName) throws PlayerCharacterRegistrationException {
		log.debug("Inside registerPlayerCharacter(). Player id: {} Character name: {}", playerId, characterName);

		//check to see if the player already has an entry in the mapping for a character. If so, then they cannot register a new character until logging out first.
		if (activePlayerCharacterMap.containsKey(playerId)) {
			throw new PlayerCharacterRegistrationException("Please logout before registering another character.");
		}
		
		if (playerId == null) {
			throw new PlayerCharacterRegistrationException("Player id is required to register.");
		}

		var sanitizedName = CharacterNameNormalizationUtil.sanitize(characterName);
		if (sanitizedName == null) {
			throw new PlayerCharacterRegistrationException("Character name is required.");
		}

		try {
			playerCharacterStorage.loadPlayerCharacterDataByCharacterName(sanitizedName);
			throw new PlayerCharacterRegistrationException("Character name already exists.");
		} catch (PlayerCharacterLoadDataException ex) {
			// indicates not found, safe to proceed
		}

		var newCharacterRecord = initializeNewPlayerCharacter(playerId, sanitizedName);

		try {
			PlayerCharacterData savedCharacter = playerCharacterStorage.savePlayerCharacterData(newCharacterRecord);
			markCharacterActive(savedCharacter);
			return savedCharacter;
		} catch (PlayerCharacterSaveDataException ex) {
			throw new PlayerCharacterRegistrationException("Unable to register character.", ex);
		}
	}


    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	private PlayerCharacterData loadCharacterData(String characterName) throws CharacterLoginException {
		try {
			return playerCharacterStorage.loadPlayerCharacterDataByCharacterName(characterName);
		} catch (PlayerCharacterLoadDataException ex) {
			throw new CharacterLoginException("Unable to load character data.", ex);
		}
	}
	
	private void saveCharacterData(PlayerCharacterData characterData) throws CharacterLogoutException {
		try {
			playerCharacterStorage.savePlayerCharacterData(characterData);
		} catch (PlayerCharacterSaveDataException ex) {
			throw new CharacterLogoutException("Unable to save character data.", ex);
		}
	}
	
	private void markCharacterActive(PlayerCharacterData characterData) {
		if (characterData == null || characterData.getPlayerId() == null || characterData.getCharacterName() == null) {
			return;
		}
		
		activePlayerCharacterMap.put(characterData.getPlayerId(), characterData);
		var normalizedName = CharacterNameNormalizationUtil.normalize(characterData.getCharacterName());
		if (normalizedName != null) {
			currentlyActivePlayerMap.put(normalizedName, characterData);
		}
	}
	
	private void removeCharacterFromNameMap(PlayerCharacterData characterData) {
		var normalizedName = CharacterNameNormalizationUtil.normalize(characterData.getCharacterName());
		if (normalizedName != null) {
			currentlyActivePlayerMap.remove(normalizedName, characterData);
		}
	}
	
	private String validateLoginPlayerCharacterRequest(UUID playerId, String characterName) throws CharacterLoginException {
		if (playerId == null) {
			throw new CharacterLoginException("Player id is required to login.");
		}
		
		var sanitizedName = CharacterNameNormalizationUtil.sanitize(characterName);
		if (sanitizedName == null) {
			throw new CharacterLoginException("Character name is required.");
		}
		
		var normalizedName = CharacterNameNormalizationUtil.normalize(sanitizedName);
		
		if (activePlayerCharacterMap.containsKey(playerId)) {
			throw new CharacterLoginException("Player already controls an active character.");
		}
		
		if (currentlyActivePlayerMap.containsKey(normalizedName)) {
			throw new CharacterLoginException("Character is already logged in.");
		}
		
		return sanitizedName;
	}
	
	private PlayerCharacterDataRecord initializeNewPlayerCharacter(UUID playerId, String sanitizedName) {
		Instant currentTime = clockUtil.getNow();
		Long startingLocationId = areaManager.getStartingLocationId();

		var newCharacterRecord = new PlayerCharacterDataRecord();
		newCharacterRecord.setPlayerId(playerId);
		newCharacterRecord.setCharacterName(sanitizedName);
		newCharacterRecord.setCreatedOn(currentTime);
		newCharacterRecord.setLocationId(startingLocationId);
		
		return newCharacterRecord;
	}
    
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
     
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
     
     
}
