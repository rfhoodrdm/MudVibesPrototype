package com.example.mudvibe.gameworldengine.commanddelegate;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.AreaManager;
import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.Room;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

@Component
public class LoginCommandProcessingDelegate implements CommandProcessingDelegate<LoginCommand> {

	@Override
	public List<AddressedOutboundMessage> processCommand(LoginCommand lic, 
			PlayerCharacterManager characterManager,
			AreaManager areaManager) throws CommandProcessingException {
		
		try {
			PlayerCharacterData pcData =  characterManager.loginPlayerCharacter(lic.commandingPlayerId(), lic.characterName());
			
			Room room = areaManager.findRoomByLocationId(Optional.of(pcData.getLocationId()));
			RoomDescriptionMessage currentRoomDescription = LookCommandProcessingDelegate.constructRoomDescriptionMessage(pcData.getPlayerId(), room);
			
			return List.of(currentRoomDescription);
		} catch (CharacterLoginException ex) {
			throw new CommandProcessingException("Unable to login character: " + ex.getMessage(), ex);
		}
	}

}
