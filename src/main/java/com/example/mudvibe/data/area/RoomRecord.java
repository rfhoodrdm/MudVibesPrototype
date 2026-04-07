package com.example.mudvibe.data.area;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.mudvibe.common.enums.MovementDirection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "rooms")
public class RoomRecord implements RoomData {

	@Id
	@Column(name = "location_id", nullable = false, unique = true)
	private long locationId;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "parentRoom")
	private final List<DirectionalExitRecord> directionalExits = new ArrayList<>();
	
	@Override
	public List<DirectionalExitData> getDirectionalExits() {
		return Collections.unmodifiableList(new ArrayList<>(directionalExits));
	}

	public List<DirectionalExitData> addDirectionalExit(Long destinationRoomId, MovementDirection movementDirection) {
		if (null == destinationRoomId  ||  null == movementDirection) {
			return getDirectionalExits();
		}
	
		DirectionalExitRecord newExit = new DirectionalExitRecord();
		newExit.setDestinationRoomId(destinationRoomId);
		newExit.setMovementDirection(movementDirection);
		newExit.setParentRoom(this);
		
		directionalExits.add(newExit);
		return getDirectionalExits();
	}
}
