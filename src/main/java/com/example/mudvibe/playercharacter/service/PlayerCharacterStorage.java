package com.example.mudvibe.playercharacter.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.example.mudvibe.common.exception.PlayerCharacterLoadDataException;
import com.example.mudvibe.common.exception.PlayerCharacterSaveDataException;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;

public interface PlayerCharacterStorage {

	public PlayerCharacterDataRecord savePlayerCharacterData(PlayerCharacterDataRecord dataToSave) throws PlayerCharacterSaveDataException;
	
	public List<PlayerCharacterDataRecord> findAllPlayerCharactersByPlayerId(UUID playerId);
	
	public PlayerCharacterDataRecord loadPlayerCharacterDataByCharacterName(String characterName) throws PlayerCharacterLoadDataException;

	List<PlayerCharacterDataRecord> saveAllPlayerCharacterData(
			Collection<PlayerCharacterDataRecord> playerCharactersToSave);

}
