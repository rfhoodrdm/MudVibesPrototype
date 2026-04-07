package com.example.mudvibe.data.player;

import java.time.Instant;
import java.util.UUID;

public interface PlayerCharacterData {

	public UUID getCharacterId();
	public UUID getPlayerId();
	public String getCharacterName();
	public Instant getCreatedOn();
	public Long getLocationId();
}
