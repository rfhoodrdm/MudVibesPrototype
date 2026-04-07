package com.example.mudvibe.playercharacter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mudvibe.data.player.PlayerCharacterDataRecord;

public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacterDataRecord, UUID> {

	List<PlayerCharacterDataRecord> findAllByPlayerId(UUID playerId);
	
	Optional<PlayerCharacterDataRecord> findByCharacterName(String characterName);

}
