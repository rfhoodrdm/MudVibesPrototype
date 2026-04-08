package com.example.mudvibe.gameworldengine.delegates.response;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.data.area.DirectionalExitData;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomDescriptionResponseDelegate {
	
	private final AreaManager areaManager;
	private final PlayerCharacterManager playerCharacterManager;

	public RoomDescriptionMessage constructRoomDescriptionMessage(UUID sendToPlayerId, RoomData room) {
		Long locationId = room.getLocationId();
		
		String roomTitle = room.getTitle();
		String roomDescription = room.getDescription();
		
		List<PlayerCharacterData> otherCharacters = playerCharacterManager.getActivePlayerCharacterDataByLocation(locationId);
		Set<String> otherCharacterNames = otherCharacters.stream()
				.map(PlayerCharacterData::getCharacterName)
				.collect(Collectors.toSet());
		
		List<MovementDirection> exitDirections =  room.getDirectionalExits().stream()
			.map(DirectionalExitData::getMovementDirection)
			.toList();
		
		RoomDescriptionMessage result = new RoomDescriptionMessage(sendToPlayerId, roomTitle, roomDescription, exitDirections, otherCharacterNames);
		return result;
	}
}
