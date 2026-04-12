package com.example.mudvibe.common.enums;

public enum MovementDirection {
	NORTH,
	NORTHWEST,
	WEST,
	SOUTHWEST,
	SOUTH,
	SOUTHEAST,
	EAST,
	NORTHEAST,
	UP,
	DOWN;
	
	public MovementDirection oppositeDirection() {
		return switch (this) {
		case NORTH -> MovementDirection.SOUTH;
		case SOUTH -> MovementDirection.NORTH;
		case EAST -> MovementDirection.WEST;
		case WEST -> MovementDirection.EAST;
		case NORTHEAST -> MovementDirection.SOUTHWEST;
		case NORTHWEST -> MovementDirection.SOUTHEAST;
		case SOUTHEAST -> MovementDirection.NORTHWEST;
		case SOUTHWEST -> MovementDirection.NORTHEAST;
		case UP -> MovementDirection.DOWN;
		case DOWN -> MovementDirection.UP;
		case null -> null;
		};
	}
}
