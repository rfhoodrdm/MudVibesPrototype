package com.example.mudvibe.gameworldengine.commanddelegate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.AreaManager;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.Location;
import com.example.mudvibe.data.area.Room;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

@Component
public class LookCommandProcessingDelegate implements CommandProcessingDelegate<LookCommand> {

	@Override
	public List<AddressedOutboundMessage> processCommand(LookCommand lc, 
			PlayerCharacterManager playerCharacterManager, 
			AreaManager areaManager) throws CommandProcessingException {
		UUID commandingPlayerId = lc.commandingPlayerId(); 
		PlayerCharacterData pcData = playerCharacterManager.getActivePlayerCharacterDataByPlayerId(commandingPlayerId)
			.orElseThrow(() -> new CommandProcessingException("Unable to find player data."));
		Optional<Long> locationIdMaybe = Optional.ofNullable(pcData).map(PlayerCharacterData::getLocationId);
		
		Room room = areaManager.findRoomByLocationId(locationIdMaybe);

		//TODO: look up who else is in the same location as the player and construct a list of player names.
		//TODO: look up any items which are listed as laying in this location.
		//TODO: construct a list of names for the items.
		
		RoomDescriptionMessage result = constructRoomDescriptionMessage(commandingPlayerId, room);
		return List.of(result); 
	}
	
	public static RoomDescriptionMessage constructRoomDescriptionMessage(UUID sendToPlayerId, Room room) {
		String roomTitle = room.getTitle();
		String roomDescription = room.getDescription();
		
		RoomDescriptionMessage result = new RoomDescriptionMessage(sendToPlayerId, roomTitle, roomDescription);
		return result;
	}
}
