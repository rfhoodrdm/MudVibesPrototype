package com.example.mudvibe.data.player;

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
public class PlayerCharacterDataRecord  {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "character_id")
	private UUID characterId;
	
	@Column(name = "player_name", unique = true, nullable = false)
	private String playerName;
	
	@Column(name = "location_id", nullable = false)
	private Long locationId = 0L;
}
