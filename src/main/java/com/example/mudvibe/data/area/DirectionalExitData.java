package com.example.mudvibe.data.area;

import com.example.mudvibe.common.enums.MovementDirection;

public interface DirectionalExitData {

	MovementDirection getMovementDirection();
	Long getDestinationRoomId();
}
