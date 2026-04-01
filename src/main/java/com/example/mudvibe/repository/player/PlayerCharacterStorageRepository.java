package com.example.mudvibe.repository.player;

import java.util.UUID;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mudvibe.data.player.PlayerCharacterDataRecord;

@Repository
public interface PlayerCharacterStorageRepository extends JpaRepository<PlayerCharacterDataRecord, UUID> {

	Optional<PlayerCharacterDataRecord> findByPlayerNameIgnoreCase(String playerName);

}
