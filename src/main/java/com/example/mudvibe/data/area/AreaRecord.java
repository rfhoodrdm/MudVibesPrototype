package com.example.mudvibe.data.area;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "area")
public class AreaRecord implements AreaData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "area_id", nullable = false)
	private UUID areaId;
	
	@Column(name = "area_name", nullable = false)
	private String areaName;
	
	@Column(name = "area_Description", nullable = false)
	private String areaDescription;
	
	@Column(name = "min_room_id", nullable = false)
	private Long minRoomId;
	
	@Column(name = "max_room_id", nullable = false)
	private Long maxRoomId;
	
	
	
}
