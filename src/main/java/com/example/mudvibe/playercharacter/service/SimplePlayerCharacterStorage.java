package com.example.mudvibe.playercharacter.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
	public PlayerCharacterData savePlayerCharacterData(PlayerCharacterData dataToSave)
			throws PlayerCharacterSaveDataException {
		
		if (dataToSave == null) {
			throw new PlayerCharacterSaveDataException("Player character data cannot be null.");
		}
		
		if (!(dataToSave instanceof PlayerCharacterDataRecord record)) {
			throw new PlayerCharacterSaveDataException("Unsupported player character data type: " + dataToSave.getClass().getName());
		}
		
		try {
			return playerCharacteRepo.save(record);
		} catch (Exception ex) {
			throw new PlayerCharacterSaveDataException("Unable to save player character data.", ex);
		}
	}


	@Override
	public PlayerCharacterData loadPlayerCharacterDataByCharacterName(String characterName)
			throws PlayerCharacterLoadDataException {
		
		var sanitizedName = CharacterNameNormalizationUtil.sanitize(characterName);
		if (sanitizedName == null) {
			throw new PlayerCharacterLoadDataException("Character name is required.");
		}
		
		return playerCharacteRepo.findByCharacterName(sanitizedName)
				.<PlayerCharacterLoadDataException>orElseThrow(() -> new PlayerCharacterLoadDataException("Character not found: " + sanitizedName));
	} 
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
    
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

	@Override
	public List<? extends PlayerCharacterData> findAllPlayerCharactersByPlayerId(UUID playerId) {
		return playerCharacteRepo.findAllByPlayerId(playerId);
	}
	
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
}
