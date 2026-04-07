package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.gameworldengine.delegates.response.RoomDescriptionResponseDelegate;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LookCommandProcessingDelegate extends CommandProcessingDelegate<LookCommand> {
	
	private final RoomDescriptionResponseDelegate roomDescriptionResponseDelegate;

	@Override
	public List<AddressedOutboundMessage> processCommand(LookCommand lc) throws CommandProcessingException {
		UUID commandingPlayerId = lc.commandingPlayerId(); 
		PlayerCharacterData pcData = playerCharacterManager.getActivePlayerCharacterDataByPlayerId(commandingPlayerId)
			.orElseThrow(() -> new CommandProcessingException("Unable to find player data."));
		
		Long locationId = pcData.getLocationId();
		RoomData room = areaManager.findRoomByLocationId(locationId);
		
		RoomDescriptionMessage result = roomDescriptionResponseDelegate.constructRoomDescriptionMessage(commandingPlayerId, room);
		return List.of(result); 
	}

}
