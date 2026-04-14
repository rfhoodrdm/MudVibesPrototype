package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.system.CharacterRosterListCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.CharacterRosterDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.CharacterRosterDescriptionMessage.CharacterRosterEntry;
import com.example.mudvibe.data.player.PlayerCharacterData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CharacterRosterCommandProcessingDelegate extends CommandProcessingDelegate<CharacterRosterListCommand> {

	@Override
	public List<AddressedOutboundMessage> processCommand(CharacterRosterListCommand command)
			throws CommandProcessingException {
		
		UUID playerId = command.commandingPlayerId();
		List<PlayerCharacterData> pcDataList = playerCharacterManager.getAllCharactersByPlayerId(playerId);
		
		List<AddressedOutboundMessage> messageList = new ArrayList<>();
		List<CharacterRosterEntry> rosterEntryList = new ArrayList<>();
		for(PlayerCharacterData pcData: pcDataList) {
			CharacterRosterEntry newRosterEntry = constructRosterEntry(pcData);	
			rosterEntryList.add(newRosterEntry);
		}
		
		CharacterRosterDescriptionMessage message = new CharacterRosterDescriptionMessage(playerId, rosterEntryList);
		messageList.add(message);
		return messageList;
	}

	private CharacterRosterEntry constructRosterEntry(PlayerCharacterData pcData) {
		CharacterRosterEntry entry = new CharacterRosterEntry(pcData.getCharacterName());
		return entry;
	}

}
