package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CharacterLoginException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.CharacterLogDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.CharacterLogDescriptionMessage.LogAction;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginCommandProcessingDelegate extends CommandProcessingDelegate<LoginCommand> {
	
	@Override
	public List<AddressedOutboundMessage> processCommand(LoginCommand lic) throws CommandProcessingException {
		
		try {
			PlayerCharacterData pcData =  playerCharacterManager.loginPlayerCharacter(lic.commandingPlayerId(), lic.characterName());
			Long location = pcData.getLocationId();
			String characterName = pcData.getCharacterName();
			
			RoomData room = areaManager.findRoomByLocationId(location);
			RoomDescriptionMessage currentRoomDescription = roomDescriptionResponseDelegate.constructRoomDescriptionMessage(pcData.getPlayerId(), room);
			
			List<PlayerCharacterData> pcDataForWitnessesList =  playerCharacterManager.getActivePlayerCharacterDataByLocation(location);
			List<AddressedOutboundMessage> messagesForWitnesesList = pcDataForWitnessesList.stream()
					.filter(witnessData -> !witnessData.getPlayerId().equals(pcData.getPlayerId()))
					.map(witnessPcData -> constructLoginNotificationsForOtherPlayer(witnessPcData, pcData.getCharacterName()))
					.toList();
			
			List<AddressedOutboundMessage> messagesList = new ArrayList<>();
			messagesList.add(currentRoomDescription);
			messagesList.addAll(messagesForWitnesesList);
			return messagesList; 
		} catch (CharacterLoginException ex) {
			throw new CommandProcessingException("Unable to login character: " + ex.getMessage(), ex);
		}
	}
	
	private AddressedOutboundMessage constructLoginNotificationsForOtherPlayer(PlayerCharacterData pcData, String characterLoggingIn) {
		CharacterLogDescriptionMessage message = new CharacterLogDescriptionMessage(pcData.getPlayerId(), characterLoggingIn, LogAction.LOGIN);
		return message;
	}

}
