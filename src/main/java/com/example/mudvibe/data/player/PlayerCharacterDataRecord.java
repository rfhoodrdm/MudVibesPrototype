package com.example.mudvibe.data.player;

import java.util.UUID;

import com.example.mudvibe.common.interfaces.data.player.PlayerCharacterData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "character_data_record")
public class PlayerCharacterDataRecord implements PlayerCharacterData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "character_id")
	private UUID characterId;
	
	@Column(name = "player_name", unique = true)
	private String playerName;
}
