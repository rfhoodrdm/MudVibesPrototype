package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.common.exception.PlayerCharacterRegistrationException;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterCharacterCommandProcessingDelegate extends CommandProcessingDelegate<RegisterCharacterCommand> {
	
	@Override
	public List<AddressedOutboundMessage> processCommand (RegisterCharacterCommand rcc) 
			throws CommandProcessingException {
		
		try {
			PlayerCharacterData pcData = playerCharacterManager.registerPlayerCharacter(rcc.commandingPlayerId(), rcc.characterName());
			
			Long location = pcData.getLocationId();
			RoomData room = areaManager.findRoomByLocationId(location);
			
			RoomDescriptionMessage currentRoomDescription = roomDescriptionResponseDelegate.constructRoomDescriptionMessage(pcData.getPlayerId(), room);
			return List.of(currentRoomDescription);
		} catch (PlayerCharacterRegistrationException ex) {
			throw new CommandProcessingException("Unable to register character: " + ex.getMessage(), ex);
		}
	}
}
