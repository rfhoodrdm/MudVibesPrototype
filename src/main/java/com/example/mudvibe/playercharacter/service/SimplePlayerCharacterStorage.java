package com.example.mudvibe.playercharacter.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.mudvibe.common.exception.PlayerCharacterLoadDataException;
import com.example.mudvibe.common.exception.PlayerCharacterSaveDataException;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;
import com.example.mudvibe.playercharacter.repository.PlayerCharacterRepository;
import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimplePlayerCharacterStorage implements PlayerCharacterStorage {

	private final PlayerCharacterRepository playerCharacteRepo;

	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/

	@Override
	public PlayerCharacterDataRecord savePlayerCharacterData(PlayerCharacterDataRecord dataToSave)
			throws PlayerCharacterSaveDataException {
		
		if (dataToSave == null) {
			throw new PlayerCharacterSaveDataException("Player character data cannot be null.");
		}
		
		try {
			return playerCharacteRepo.save(dataToSave);
		} catch (Exception ex) {
			throw new PlayerCharacterSaveDataException("Unable to save player character data.", ex);
		}
	}

	@Override
	public PlayerCharacterDataRecord loadPlayerCharacterDataByCharacterName(String characterName)
			throws PlayerCharacterLoadDataException {
		
		var sanitizedName = CharacterNameNormalizationUtil.sanitize(characterName);
		if (sanitizedName == null) {
			throw new PlayerCharacterLoadDataException("Character name is required.");
		}
		
		return playerCharacteRepo.findByCharacterName(sanitizedName)
				.<PlayerCharacterLoadDataException>orElseThrow(() -> new PlayerCharacterLoadDataException("Character not found: " + sanitizedName));
	} 
	
	@Override
	public List<PlayerCharacterDataRecord> saveAllPlayerCharacterData(Collection<PlayerCharacterDataRecord> playerCharactersToSaveList) {
		log.debug("Inside saveAllPlayerCharacterData()");
		if(CollectionUtils.isEmpty(playerCharactersToSaveList)) {
			log.warn("Found no character data to save. Returning.");
			return List.of();
		}

		List<PlayerCharacterDataRecord> savedCharacterDataList =  playerCharacteRepo.saveAll(playerCharactersToSaveList);
		log.debug("Successfully saved {} characters.", savedCharacterDataList.size());
		return savedCharacterDataList;
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
    
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

	@Override
	public List<PlayerCharacterDataRecord> findAllPlayerCharactersByPlayerId(UUID playerId) {
		if (null == playerId) {
			return List.of();
		}
		
		return playerCharacteRepo.findAllByPlayerId(playerId);
	}

	
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
}
