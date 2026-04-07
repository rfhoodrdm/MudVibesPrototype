package com.example.mudvibe.playercharacter.service;

import java.util.Optional;
import java.util.UUID;

import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.CharacterLogoutException;
import com.example.mudvibe.common.exception.PlayerCharacterRegistrationException;
import com.example.mudvibe.data.player.PlayerCharacterData;

public interface PlayerCharacterManager {

	public PlayerCharacterData loginPlayerCharacter(UUID playerId, String characterName) throws CharacterLoginException;
	
	public Optional<PlayerCharacterData> logoutPlayerCharacterByPlayerId(UUID playerId) throws CharacterLogoutException;
	
	public PlayerCharacterData logoutPlayerCharacterByCharacterName(String characterName);
	
	public Optional<PlayerCharacterData> getActivePlayerCharacterDataByPlayerId(UUID playerId);

	public PlayerCharacterData registerPlayerCharacter(UUID playerId, String characterName) throws PlayerCharacterRegistrationException;
}
