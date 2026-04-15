package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.data.messages.outbound.MoveCharacterDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.MoveCharacterDescriptionMessage.TransitDirection;

public class CharacterMovesMessageFormatter {

	public static String format(MoveCharacterDescriptionMessage message) {
		
		StringBuilder sb = new StringBuilder();
		boolean characterIsArriving = TransitDirection.ARRIVING.equals(message.transitDirection());
		
		sb.append(message.characterNameMoving()).append(" ");
		
		String transitDirection = characterIsArriving
				? "arrives from" 
				: "leaves heading";
		sb.append(transitDirection).append(" ");
		
		String formattedDirection = printFriendlyDirection(message.movementDirection());
		sb.append(formattedDirection);
		
		if(characterIsArriving  &&  message.isOneWay()) {
			sb.append(" ").append("via a one-way passage");
		}
		sb.append(".");
		
		
		return sb.toString();
	}

	private static String printFriendlyDirection(MovementDirection movementDirection) {
		if (movementDirection == null) {
			return "an unknown direction";
		}
		
		return switch (movementDirection) {
		case NORTH -> "north";
		case SOUTH -> "south";
		case EAST -> "east";
		case WEST -> "west";
		case NORTHEAST -> "northeast";
		case NORTHWEST -> "northwest";
		case SOUTHEAST -> "southeast";
		case SOUTHWEST -> "southwest";
		case UP -> "above";
		case DOWN -> "below";
		};
	}
}
