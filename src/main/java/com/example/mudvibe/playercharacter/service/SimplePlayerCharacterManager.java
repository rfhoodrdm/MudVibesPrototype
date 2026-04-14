package com.example.mudvibe.playercharacter.service;

import com.example.mudvibe.playercharacter.repository.PlayerCharacterRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Service;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.CharacterLogoutException;
import com.example.mudvibe.common.exception.CharacterMoveException;
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
	
	private final Map<UUID, PlayerCharacterData> activePlayerCharacterMap = new ConcurrentHashMap<>();	     //player Id to character data mapping.
	private final Map<String, PlayerCharacterData> currentlyActivePlayerMap = new ConcurrentHashMap<>();	 //character name to character data mapping.
	private final Map<Long, Set<PlayerCharacterData>> activePlayersByLocation = new ConcurrentHashMap<>();   //locationId to mapping of characters in that location.
	private final ReadWriteLock characterStateLock = new ReentrantReadWriteLock();
	private final Lock readLock = characterStateLock.readLock();
	private final Lock writeLock = characterStateLock.writeLock();

	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	public PlayerCharacterData loginPlayerCharacter(UUID playerId, String characterName) throws CharacterLoginException {
		log.debug("Inside loginPlayerCharacter(). Player id: {} Character name: {}", playerId, characterName);
		
		writeLock.lock();
		try {
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
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Optional<PlayerCharacterData> logoutPlayerCharacterByPlayerId(UUID playerId) throws CharacterLogoutException {
		log.debug("Inside logoutPlayerCharacterByPlayerId(). Player id: {}", playerId);
		
		writeLock.lock();
		try {
			if (playerId == null) {
				return Optional.empty();
			}
			
			PlayerCharacterData removedCharacter = activePlayerCharacterMap.remove(playerId);
			if (removedCharacter == null) {
				return Optional.empty();
			}
			
			removeCharacterFromNameMap(removedCharacter);
			removeCharacterFromLocationMap(removedCharacter);
			saveCharacterData(removedCharacter);
			return Optional.of(removedCharacter);
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public PlayerCharacterData logoutPlayerCharacterByCharacterName(String characterName) {
		log.debug("Inside logoutPlayerCharacterByCharacterName(). Character name: {}", characterName);
		
		writeLock.lock();
		try {
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
			removeCharacterFromLocationMap(removedCharacter);
			
			try {
				playerCharacterStorage.savePlayerCharacterData(removedCharacter);
			} catch (PlayerCharacterSaveDataException ex) {
				throw new IllegalStateException("Unable to persist character data during logout.", ex);
			}
			
			return removedCharacter;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Optional<PlayerCharacterData> getActivePlayerCharacterDataByPlayerId(UUID playerId) {
		readLock.lock();
		try {
			if (playerId == null) {
				return Optional.empty();
			}
			return Optional.ofNullable(activePlayerCharacterMap.get(playerId));
		} finally {
			readLock.unlock();
		}
	}
	
	@Override
	public PlayerCharacterData registerPlayerCharacter(UUID playerId, String characterName) throws PlayerCharacterRegistrationException {
		log.debug("Inside registerPlayerCharacter(). Player id: {} Character name: {}", playerId, characterName);
		writeLock.lock();
		try {
			String sanitizedName = CharacterNameNormalizationUtil.sanitize(characterName);
			validateRegisterPlayerCharacter(playerId, sanitizedName);
			
			PlayerCharacterDataRecord newCharacterRecord = initializeNewPlayerCharacter(playerId, sanitizedName);

			try {
				PlayerCharacterData savedCharacter = playerCharacterStorage.savePlayerCharacterData(newCharacterRecord);
				markCharacterActive(savedCharacter);
				return savedCharacter;
			} catch (PlayerCharacterSaveDataException ex) {
				throw new PlayerCharacterRegistrationException("Unable to register character.", ex);
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public List<PlayerCharacterData> getActivePlayerCharacterDataByLocation(Long locationId) {
		readLock.lock();
		try {
			if (locationId == null) {
				return List.of();
			}
			return new ArrayList<>(activePlayersByLocation.getOrDefault(locationId, Collections.emptySet()));
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public PlayerCharacterData movePlayerCharacter(PlayerCharacterData pcData, Long newLocationId) throws CharacterMoveException {
		log.debug("Inside of movePlayerCharacter(). Olayer id: {}   Character id: {}    New location: {}", 
				pcData.getPlayerId(), pcData.getCharacterId(), newLocationId);
		if (pcData == null || newLocationId == null) {
			throw new CharacterMoveException("Player data and destination location are required.");
		}
		
		writeLock.lock();
		try {
			if( pcData instanceof PlayerCharacterDataRecord pcRecord) {
				removeCharacterFromLocationMap(pcRecord);
				updateCharacterLocation(pcRecord, newLocationId);
				addCharacterToLocationMap(pcRecord);
			} else {
				log.warn("Couldn't perform player move; ocData was not a mutable plaeyr character record.");
			}

			return pcData;
		} finally {
			writeLock.unlock();
		}
	}
	

	@Override
	public List<PlayerCharacterData> getAllCharactersByPlayerId(UUID playerId) {
		if(null == playerId) {
			return List.of();
		}
		
		return new ArrayList<>(playerCharacterStorage.findAllPlayerCharactersByPlayerId(playerId));
	}

    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	private void validateRegisterPlayerCharacter(UUID playerId, String sanitizedCharacterName) 
			throws PlayerCharacterRegistrationException {
		//check to see if the player already has an entry in the mapping for a character. If so, then they cannot register a new character until logging out first.
		if (activePlayerCharacterMap.containsKey(playerId)) {
			throw new PlayerCharacterRegistrationException("Please logout before registering another character.");
		}
		
		if (playerId == null) {
			throw new PlayerCharacterRegistrationException("Player id is required to register.");
		}

		if (sanitizedCharacterName == null) {
			throw new PlayerCharacterRegistrationException("Character name is required.");
		}

		try {
			playerCharacterStorage.loadPlayerCharacterDataByCharacterName(sanitizedCharacterName);
			throw new PlayerCharacterRegistrationException("Character name already exists: " + sanitizedCharacterName);
		} catch (PlayerCharacterLoadDataException ex) {
			// indicates not found, safe to proceed
		}
	}
	
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
		addCharacterToLocationMap(characterData);
	}
	
	private void removeCharacterFromNameMap(PlayerCharacterData characterData) {
		var normalizedName = CharacterNameNormalizationUtil.normalize(characterData.getCharacterName());
		if (normalizedName != null) {
			currentlyActivePlayerMap.remove(normalizedName, characterData);
		}
	}
	
	private void addCharacterToLocationMap(PlayerCharacterData characterData) {
		if (characterData == null || characterData.getLocationId() == null) {
			return;
		}
		activePlayersByLocation
			.computeIfAbsent(characterData.getLocationId(), key -> ConcurrentHashMap.newKeySet())
			.add(characterData);
	}
	
	private void removeCharacterFromLocationMap(PlayerCharacterData characterData) {
		if (characterData == null || characterData.getLocationId() == null) {
			return;
		}
		Set<PlayerCharacterData> occupants = activePlayersByLocation.get(characterData.getLocationId());
		if (occupants == null) {
			return;
		}
		occupants.remove(characterData);
		if (occupants.isEmpty()) {
			activePlayersByLocation.remove(characterData.getLocationId(), occupants);
		}
	}
	
	private void updateCharacterLocation(PlayerCharacterData characterData, Long newLocationId) throws CharacterMoveException {
		if (characterData instanceof PlayerCharacterDataRecord record) {
			record.setLocationId(newLocationId);
		} else {
			throw new CharacterMoveException("Unable to update location for character. Character data provided is not mutable.");
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
