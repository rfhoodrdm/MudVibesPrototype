package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.area.RoomData;
import com.example.mudvibe.data.messages.inbound.character.SpeechCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.gameworldengine.delegates.response.SpeechDescriptionResponseDelegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SpeechCommandProcessingDelegate extends CommandProcessingDelegate<SpeechCommand> {
	
	@Override
	public List<AddressedOutboundMessage> processCommand(SpeechCommand sc) throws CommandProcessingException {
		UUID commandingPlayerId = sc.commandingPlayerId(); 
		PlayerCharacterData pcData = playerCharacterManager.getActivePlayerCharacterDataByPlayerId(commandingPlayerId)
			.orElseThrow(() -> new CommandProcessingException("Unable to find player data."));
		
		String speakingCharacterName = pcData.getCharacterName();
		Long locationId = pcData.getLocationId();
		RoomData room = areaManager.findRoomByLocationId(locationId);
		
		List<PlayerCharacterData> charactersInRoom = playerCharacterManager.getActivePlayerCharacterDataByLocation(room.getLocationId());
		List<AddressedOutboundMessage> outboundMessages = new ArrayList<>();
		for (PlayerCharacterData currentCharacter: charactersInRoom) {
			UUID listeningPlayerId = currentCharacter.getPlayerId();
			
			String speaker = (listeningPlayerId.equals(commandingPlayerId))
					?  "You"
				    :  speakingCharacterName;
			String speechModeVerb = (listeningPlayerId.equals(commandingPlayerId))
					?  sc.speechMode().getSpeechVerbToSelf()
					:  sc.speechMode().getSpeechVerbToOthers();
			
			AddressedOutboundMessage speechMessage = speechDescriptionResponseDelegate.constructSpeechDescriptionResponse(listeningPlayerId, 
					speaker, speechModeVerb, sc.speech());
			outboundMessages.add(speechMessage);
		}
		return outboundMessages;
	}

}
