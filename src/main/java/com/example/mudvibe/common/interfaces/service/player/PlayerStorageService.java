package com.example.mudvibe.common.interfaces.service.player;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.example.mudvibe.common.exception.PlayerSaveDataException;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;

public interface PlayerStorageService {

	Optional<PlayerCharacterDataRecord> findById(UUID characterId);

	Optional<PlayerCharacterDataRecord> findByName(String playerName);

	Optional<PlayerCharacterDataRecord> savePlayerData(PlayerCharacterDataRecord playerRecord) throws PlayerSaveDataException;
	
	Set<PlayerCharacterDataRecord> saveAllPlayerData(Collection<PlayerCharacterDataRecord> playerDataCollection);

}
