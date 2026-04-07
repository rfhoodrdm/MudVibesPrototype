package com.example.mudvibe.gameworldengine.commanddelegate;

import java.util.List;

import com.example.mudvibe.area.AreaManager;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

public interface CommandProcessingDelegate<T extends IncomingCommand> {

	public List<AddressedOutboundMessage> processCommand(T command, 
			PlayerCharacterManager playerCharacterManager, 
			AreaManager areaManager) throws CommandProcessingException;
}
