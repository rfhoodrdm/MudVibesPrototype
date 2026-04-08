package com.example.mudvibe.data.area;

import java.util.UUID;

import com.example.mudvibe.common.enums.MovementDirection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "directional_exit")
public class DirectionalExitRecord implements DirectionalExitData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "exit_id")
	private UUID exitId;
	
	@ManyToOne
	@JoinColumn(name = "parent_room_id", nullable = false)
	private RoomRecord parentRoom;
	
	@Column(name = "movement_direction", nullable = false)
	@Enumerated(EnumType.STRING)
	private MovementDirection movementDirection;
	
	@Column(name = "destination_room_id", nullable = false)
	private Long destinationRoomId;
	
}
