package com.example.mudvibe.common.interfaces.service.player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.PlayerRegistrationException;
import com.example.mudvibe.common.exception.PlayerSaveDataException;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;

public interface PlayerManagerService {

	Optional<PlayerCharacterDataRecord> registerCharacter(String playerName) throws PlayerRegistrationException;
	
	Optional<PlayerCharacterDataRecord> loginCharacter(String playerName) throws CharacterLoginException;
	
	Optional<PlayerCharacterDataRecord> logoutCharacter(UUID characterId) throws PlayerSaveDataException;

	Optional<PlayerCharacterDataRecord> findCharacterByName(String playerName);
	
	Optional<PlayerCharacterDataRecord> findCharacterById(UUID characterId);
	
	void saveAllCharacterData(); 

}
