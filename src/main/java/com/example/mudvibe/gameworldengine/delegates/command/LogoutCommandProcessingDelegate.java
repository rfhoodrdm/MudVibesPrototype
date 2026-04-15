package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CharacterLogoutException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;
import com.example.mudvibe.data.messages.outbound.CharacterLogDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.CharacterLogDescriptionMessage.LogAction;
import com.example.mudvibe.data.player.PlayerCharacterData;

@Component
public class LogoutCommandProcessingDelegate extends CommandProcessingDelegate<LogoutCommand> {
	
	public static final String LOGOUT_SYSTEM_MESSAGE = "You are now logged out. You may REGISTER or LOGIN with a new character if you wish, or close the client to exit.";

	@Override
	public List<AddressedOutboundMessage> processCommand(LogoutCommand loc) throws CommandProcessingException {
		
		try {
			Optional<PlayerCharacterData> pcDataMaybe = playerCharacterManager.logoutPlayerCharacterByPlayerId(loc.commandingPlayerId());
			if (pcDataMaybe.isEmpty()) {
				return List.of();	//no messages to send.
			}
			
			PlayerCharacterData pcData = pcDataMaybe.get();
			AddressedOutboundMessage logoutNotification = constructLogoutNotification(pcData);

			List<PlayerCharacterData> pcDataForWitnessesList =  playerCharacterManager.getActivePlayerCharacterDataByLocation(pcData.getLocationId());
			List<AddressedOutboundMessage> messagesForWitnesesList = pcDataForWitnessesList.stream()
					.filter(witnessData -> !witnessData.getPlayerId().equals(pcData.getPlayerId()))
					.map(witnessPcData -> constructLogoutNotificationsForOtherPlayer(witnessPcData, pcData.getCharacterName()))
					.toList();
			
			List<AddressedOutboundMessage> messageList = new ArrayList<>();
			messageList.add(logoutNotification);
			messageList.addAll(messagesForWitnesesList);
			return messageList;
		} catch (CharacterLogoutException ex) {
			throw new CommandProcessingException("Unable to logout character: " + ex.getMessage(), ex);
		}
	}
	
	private AddressedOutboundMessage constructLogoutNotificationsForOtherPlayer(PlayerCharacterData pcData, String characterLoggingOut) {
		CharacterLogDescriptionMessage message = new CharacterLogDescriptionMessage(pcData.getPlayerId(), characterLoggingOut, LogAction.LOGOUT);
		return message;
	}
	
	private AddressedSystemNotificationMessage constructLogoutNotification(PlayerCharacterData pcData) {
		AddressedSystemNotificationMessage message = new AddressedSystemNotificationMessage(pcData.getPlayerId(), LOGOUT_SYSTEM_MESSAGE);
		
		return message;
	}
}
