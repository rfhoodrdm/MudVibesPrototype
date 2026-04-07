package com.example.mudvibe.gameworldengine.commanddelegate;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.AreaManager;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.common.exception.PlayerCharacterRegistrationException;
import com.example.mudvibe.data.area.Room;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

@Component
public class RegisterCharacterCommandProcessingDelegate implements CommandProcessingDelegate<RegisterCharacterCommand> {
	
	@Override
	public List<AddressedOutboundMessage> processCommand (RegisterCharacterCommand rcc,
			PlayerCharacterManager playerCharacterManager, 
			AreaManager areaManager) throws CommandProcessingException {
		
		try {
			PlayerCharacterData pcData = playerCharacterManager.registerPlayerCharacter(rcc.commandingPlayerId(), rcc.characterName());
			
			Room room = areaManager.findRoomByLocationId(Optional.of(pcData.getLocationId()));
			RoomDescriptionMessage currentRoomDescription = LookCommandProcessingDelegate.constructRoomDescriptionMessage(pcData.getPlayerId(), room);
			
			return List.of(currentRoomDescription);
		} catch (PlayerCharacterRegistrationException ex) {
			throw new CommandProcessingException("Unable to register character: " + ex.getMessage(), ex);
		}
	}
}
