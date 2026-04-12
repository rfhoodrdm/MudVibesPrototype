package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CharacterMoveException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.DirectionalExitData;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.MoveCharacterDescriptionMessage.TransitDirection;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.gameworldengine.delegates.response.MoveCharacterDescriptionMessageDelegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MoveCharacterCommandProcessingDelegate extends CommandProcessingDelegate<MoveCharacterCommand> {

	@Override
	public List<AddressedOutboundMessage> processCommand(MoveCharacterCommand command) throws CommandProcessingException {
		PlayerCharacterData movingPlayerData = playerCharacterManager.getActivePlayerCharacterDataByPlayerId(command.commandingPlayerId())
				.orElseThrow(() -> new CommandProcessingException("Unable to find logged in character."));
		Long sourceRoomId = movingPlayerData.getLocationId();
		Long destinationRoomId;
		
		DirectionalExitData selectedExit;
		try {
			selectedExit = validateDirectionalExitExists(movingPlayerData, command);
			destinationRoomId = selectedExit.getDestinationRoomId();
			playerCharacterManager.movePlayerCharacter(movingPlayerData, destinationRoomId);
		} catch (CharacterMoveException ex) {
			throw new CommandProcessingException("Unable to move: " + ex.getMessage(), ex);
		}

		RoomData destinationRoom = areaManager.findRoomByLocationId(selectedExit.getDestinationRoomId());
		List<AddressedOutboundMessage> messagesList = new ArrayList<>();
		List<? extends AddressedOutboundMessage> playerLeavesMessages = 
				constructPlayerMovesMessages(sourceRoomId, movingPlayerData, selectedExit, TransitDirection.LEAVING, destinationRoom);
		List<? extends AddressedOutboundMessage> playerArrivesMessages =
				constructPlayerMovesMessages(destinationRoomId, movingPlayerData, selectedExit, TransitDirection.ARRIVING, destinationRoom);
	
		messagesList.add( constructRoomDescriptionMessage(movingPlayerData, selectedExit, destinationRoom) );
		messagesList.addAll(playerArrivesMessages);
		messagesList.addAll(playerLeavesMessages);
		log.debug("Player {} moved {} to room {}", movingPlayerData.getPlayerId(), command.direction(), selectedExit.getDestinationRoomId());
		return messagesList;
	}
	

	private List<? extends AddressedOutboundMessage> constructPlayerMovesMessages(Long locationIdReceivingMessage, PlayerCharacterData movingPlayerData, 
			DirectionalExitData selectedExit, TransitDirection transitDirection, RoomData destinationRoom) {
		
		List<? extends AddressedOutboundMessage> playerLeavesMessages = 
			playerCharacterManager.getActivePlayerCharacterDataByLocation(locationIdReceivingMessage).stream()
			.filter(recipientData -> !recipientData.getPlayerId().equals(movingPlayerData.getPlayerId()))
			.map(recipientData -> messageDelegate.constructMessage(recipientData.getPlayerId(), movingPlayerData.getCharacterName(), 
					transitDirection, selectedExit.getMovementDirection(), destinationRoom))
			.toList();
		
		return playerLeavesMessages;
	}

	private RoomDescriptionMessage constructRoomDescriptionMessage(PlayerCharacterData playerData, DirectionalExitData selectedExit, RoomData destinationRoom) {
		RoomDescriptionMessage destinationDescription = roomDescriptionResponseDelegate
				.constructRoomDescriptionMessage(playerData.getPlayerId(), destinationRoom);
		
		return destinationDescription;
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
