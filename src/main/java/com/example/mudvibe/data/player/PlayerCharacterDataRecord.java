package com.example.mudvibe.data.player;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "player_character_data_record")
public class PlayerCharacterDataRecord  implements PlayerCharacterData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "character_id")
	private UUID characterId;
	
	@Column(name = "player_id", nullable = false)
	private UUID playerId;
	
	@Column(name = "character_name", unique = true, nullable = false)
	private String characterName;
	
	@Column(name = "location_id", nullable = false)
	private Long locationId;	
	
	@Column(name = "created_on", nullable = false, updatable = false)
	private Instant createdOn;

}
