package com.example.mudvibe.playercharacter.service;

import java.util.List;
import java.util.UUID;

import com.example.mudvibe.common.exception.PlayerCharacterLoadDataException;
import com.example.mudvibe.common.exception.PlayerCharacterSaveDataException;
import com.example.mudvibe.data.player.PlayerCharacterData;

public interface PlayerCharacterStorage {

	public PlayerCharacterData savePlayerCharacterData(PlayerCharacterData dataToSave) throws PlayerCharacterSaveDataException;
	
	public List<? extends PlayerCharacterData> findAllPlayerCharactersByPlayerId(UUID playerId);
	
	public PlayerCharacterData loadPlayerCharacterDataByCharacterName(String characterName) throws PlayerCharacterLoadDataException;
}
