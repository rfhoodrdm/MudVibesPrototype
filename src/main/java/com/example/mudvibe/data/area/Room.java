package com.example.mudvibe.data.area;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "rooms")
public class Room {

	@Id
	@Column(name = "location_id")
	private Long locationId;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
}
