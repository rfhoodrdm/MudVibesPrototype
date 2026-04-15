package com.example.mudvibe.gameworldengine.delegates.response;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.outbound.MoveCharacterDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.MoveCharacterDescriptionMessage.TransitDirection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoveCharacterDescriptionMessageDelegate extends ResponseDelegate<MoveCharacterDescriptionMessage> {

	//TODO: eventually add in an item reference so we can specify portal names. E.g. a door. a hole in the wall.
	public MoveCharacterDescriptionMessage constructMessage(UUID recipientPlayerId, String characterNameMoving, TransitDirection transitType,
			MovementDirection movementDirection, RoomData destinationRoom) {

		MovementDirection oppositeMovementDirection = movementDirection.oppositeDirection();
		boolean isOneWay = destinationRoom.getDirectionalExits().stream()
				.map(exit -> exit.getMovementDirection())
				.noneMatch(exit -> exit.equals(oppositeMovementDirection));	//TODO: eventually factor in portal travel to determine if one way.
		
		//Arriving characters will move in from opposite direction of chosen movement. E.g. move north -> arrive from south.
		MovementDirection movementDirectionFromPerspective = (TransitDirection.ARRIVING.equals(transitType))
				?  oppositeMovementDirection
			    :  movementDirection;
		
		destinationRoom.getDirectionalExits().forEach( exit -> log.info(exit.getMovementDirection().toString()));
		log.debug("One way? {}    Movement direction: {}   Opposite movement direction: {}",
				isOneWay, movementDirection, oppositeMovementDirection);

		return new MoveCharacterDescriptionMessage(recipientPlayerId, characterNameMoving, transitType, movementDirectionFromPerspective, isOneWay);
	}

}
