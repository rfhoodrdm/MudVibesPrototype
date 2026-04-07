package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CharacterMoveException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.DirectionalExitData;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MoveCharacterCommandProcessingDelegate extends CommandProcessingDelegate<MoveCharacterCommand> {

	@Override
	public List<AddressedOutboundMessage> processCommand(MoveCharacterCommand command) throws CommandProcessingException {
		PlayerCharacterData playerData = playerCharacterManager.getActivePlayerCharacterDataByPlayerId(command.commandingPlayerId())
				.orElseThrow(() -> new CommandProcessingException("Unable to find logged in character."));

		DirectionalExitData selectedExit;
		try {
			selectedExit = validateDirectionalExitExists(playerData, command);
			playerCharacterManager.movePlayerCharacter(playerData, selectedExit.getDestinationRoomId());
		} catch (CharacterMoveException ex) {
			throw new CommandProcessingException("Unable to move: " + ex.getMessage(), ex);
		}

		RoomData destinationRoom = areaManager.findRoomByLocationId(selectedExit.getDestinationRoomId());
		RoomDescriptionMessage destinationDescription = roomDescriptionResponseDelegate
				.constructRoomDescriptionMessage(playerData.getPlayerId(), destinationRoom);
		
		log.debug("Player {} moved {} to room {}", playerData.getPlayerId(), command.direction(), selectedExit.getDestinationRoomId());
		return List.of(destinationDescription);
	}

	private DirectionalExitData validateDirectionalExitExists(PlayerCharacterData playerData, MoveCharacterCommand command) throws CommandProcessingException {
		RoomData currentRoom = areaManager.findRoomByLocationId(playerData.getLocationId());
		List<DirectionalExitData> exits = Optional.ofNullable(currentRoom)
				.map(RoomData::getDirectionalExits)
				.orElse(List.of());

		DirectionalExitData selectedExit = exits.stream()
				.filter(exit -> exit.getMovementDirection() == command.direction())
				.findFirst()
				.orElseThrow(() -> new CommandProcessingException("You cannot move " + command.direction().name().toLowerCase() + " from here."));
		
		return selectedExit;
	}
}
