package com.example.mudvibe.service.gameworld.commandelegate;

import java.util.List;
import java.util.UUID;

import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.common.interfaces.service.player.PlayerManagerService;
import com.example.mudvibe.data.area.Room;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterDataRecord;
import com.example.mudvibe.service.area.AreaManagerService;

public class LookCommandProcessingDelegate {

	public static List<AddressedOutboundMessage> processCommand(LookCommand lc, PlayerManagerService playerManagerService, 
			AreaManagerService areaManagerService) throws CommandProcessingException {
		UUID commandingPlayerId = lc.commandingPlayerId();
		PlayerCharacterDataRecord pcData = playerManagerService.findCharacterById(commandingPlayerId)
			.orElseThrow(() -> new CommandProcessingException("Unable to find player data."));
		Long locationId = pcData.getLocationId();
		
		Room room = areaManagerService.findRoomByLocationId(locationId);
		String roomTitle = room.getTitle();
		String roomDescription = room.getDescription();

		//TODO: look up who else is in the same location as the player and construct a list of player names.
		//TODO: look up any items which are listed as laying in this location.
		//TODO: construct a list of names for the items.
		
		RoomDescriptionMessage result = new RoomDescriptionMessage(commandingPlayerId, roomTitle, roomDescription);
		return List.of(result); 
	}
}
