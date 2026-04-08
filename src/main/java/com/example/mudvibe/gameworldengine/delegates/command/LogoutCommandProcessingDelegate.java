package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.common.exception.CharacterLogoutException;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;
import com.example.mudvibe.data.player.PlayerCharacterData;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

@Component
public class LogoutCommandProcessingDelegate extends CommandProcessingDelegate<LogoutCommand> {
	
	public static final String LOGOUT_SYSTEM_MESSAGE = "You are now logged out. You may REGISTER or LOGIN with a new character if you wish, or close the client to exit.";

	@Override
	public List<AddressedOutboundMessage> processCommand(LogoutCommand loc) throws CommandProcessingException {
		
		try {
			Optional<PlayerCharacterData> pcDataMaybe = playerCharacterManager.logoutPlayerCharacterByPlayerId(loc.commandingPlayerId());
			Optional<AddressedOutboundMessage> logoutNotificationMaybe = pcDataMaybe.map(pcData -> constructLogoutNotification(pcData));

			return logoutNotificationMaybe.stream()
					.toList();
		} catch (CharacterLogoutException ex) {
			throw new CommandProcessingException("Unable to logout character: " + ex.getMessage(), ex);
		}
	}
	
	private AddressedSystemNotificationMessage constructLogoutNotification(PlayerCharacterData pcData) {
		AddressedSystemNotificationMessage message = new AddressedSystemNotificationMessage(pcData.getPlayerId(), LOGOUT_SYSTEM_MESSAGE);
		
		return message;
	}
}
