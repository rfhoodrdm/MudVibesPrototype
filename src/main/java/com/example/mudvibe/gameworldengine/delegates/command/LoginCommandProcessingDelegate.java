package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
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
public class LoginCommandProcessingDelegate extends CommandProcessingDelegate<LoginCommand> {
	
	private final RoomDescriptionResponseDelegate roomDescriptionResponseDelegate;

	@Override
	public List<AddressedOutboundMessage> processCommand(LoginCommand lic) throws CommandProcessingException {
		
		try {
			PlayerCharacterData pcData =  playerCharacterManager.loginPlayerCharacter(lic.commandingPlayerId(), lic.characterName());
			
			Long location = pcData.getLocationId();
			RoomData room = areaManager.findRoomByLocationId(location);
			
			RoomDescriptionMessage currentRoomDescription = roomDescriptionResponseDelegate.constructRoomDescriptionMessage(pcData.getPlayerId(), room);
			return List.of(currentRoomDescription);
		} catch (CharacterLoginException ex) {
			throw new CommandProcessingException("Unable to login character: " + ex.getMessage(), ex);
		}
	}

}
