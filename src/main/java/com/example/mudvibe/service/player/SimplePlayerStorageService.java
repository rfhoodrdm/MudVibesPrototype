package com.example.mudvibe.service.player;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.mudvibe.common.exception.PlayerSaveDataException;
import com.example.mudvibe.common.interfaces.service.player.PlayerStorageService;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;
import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class handles the persistant storage of character data.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SimplePlayerStorageService implements PlayerStorageService {

	private final PlayerCharacterStorageRepository playerStorageRepo;

    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	@Transactional(readOnly = true)
	public Optional<PlayerCharacterDataRecord> findById(UUID characterId) {
		log.debug("Inside findById(). Character id: {}", characterId);
		if (characterId == null) {
			log.debug("Leaving findById(). Player id was null.");
			return Optional.empty();
		}
		
		return playerStorageRepo.findById(characterId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<PlayerCharacterDataRecord> findByName(String playerName) {
		log.debug("Inside findByName(). Player name: {}", playerName);
		String sanitizedName = CharacterNameNormalizationUtil.sanitize(playerName);
		if (sanitizedName == null) {
			log.debug("Leaving findByName(). Player name was blank.");
			return Optional.empty();
		}
		
		return playerStorageRepo.findByPlayerNameIgnoreCase(sanitizedName);
	}
	
	/**
	 * 1) Make sure the name is not blank. (Use StringUtils. If so, throw a PlayerSaveDataException)
	 * 2) Trim the player name before saving.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Optional<PlayerCharacterDataRecord> savePlayerData(PlayerCharacterDataRecord playerRecord) throws PlayerSaveDataException {
		log.debug("Inside savePlayerData(). Player record: {}", playerRecord);
		
		if (playerRecord == null) {
			log.debug("Leaving savePlayerData(). Player record was null.");
			return Optional.empty();
		}
		
		String sanitizedName = CharacterNameNormalizationUtil.sanitize(playerRecord.getPlayerName());
		if (sanitizedName == null) {
			throw new PlayerSaveDataException("Player name cannot be blank.");
		}
		
		playerRecord.setPlayerName(sanitizedName);
		
		Optional<PlayerCharacterDataRecord> existingByName = findByName(sanitizedName);
		if (existingByName.isPresent() && !existingByName.get().getCharacterId().equals(playerRecord.getCharacterId())) {
			throw new PlayerSaveDataException("A player with that name already exists.");
		}
		
		PlayerCharacterDataRecord savedRecord = playerStorageRepo.save(playerRecord);
		log.debug("Leaving savePlayerData(). Player stored successfully. Player id: {}", savedRecord.getCharacterId());
		return Optional.of(savedRecord);
	}
	
	/**
	 * Used to periodically save the character data, in case of a crash.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Set<PlayerCharacterDataRecord> saveAllPlayerData(Collection<PlayerCharacterDataRecord> playerDataCollection) {
		int recordCount = playerDataCollection != null ? playerDataCollection.size() : 0;
		log.debug("Inside saveAllPlayerData(). Incoming record count: {}", recordCount);
		
		if (CollectionUtils.isEmpty(playerDataCollection)) {
			log.debug("Leaving saveAllPlayerData(). No player data provided.");
			return Collections.emptySet();
		}
		
		Set<PlayerCharacterDataRecord> savedRecords = new LinkedHashSet<>();
		
		for (PlayerCharacterDataRecord record : playerDataCollection) {
			if (record == null) {
				log.warn("Encountered null player record while saving all player data. Skipping.");
				continue;
			}
			
			try {
				savePlayerData(record).ifPresent(savedRecords::add);
			} catch (PlayerSaveDataException ex) {
				log.warn("Unable to save player record {}: {}", record.getPlayerName(), ex.getMessage());
			}
		}
		
		log.debug("Leaving saveAllPlayerData(). Successfully saved {} records.", savedRecords.size());
		return savedRecords;
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
}
