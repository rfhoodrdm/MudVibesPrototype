package com.example.mudvibe.gameworldengine.delegates.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.gameworldengine.delegates.response.MoveCharacterDescriptionMessageDelegate;
import com.example.mudvibe.gameworldengine.delegates.response.RoomDescriptionResponseDelegate;
import com.example.mudvibe.gameworldengine.delegates.response.SpeechDescriptionResponseDelegate;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

public abstract class CommandProcessingDelegate<T extends IncomingCommand> {

	//managers to access information
	@Autowired protected AreaManager areaManager;
	@Autowired protected PlayerCharacterManager playerCharacterManager;
	
	//response delegates to construct response messages. 
	@Autowired protected RoomDescriptionResponseDelegate roomDescriptionResponseDelegate;
	@Autowired protected SpeechDescriptionResponseDelegate speechDescriptionResponseDelegate; 
	@Autowired protected MoveCharacterDescriptionMessageDelegate messageDelegate;

	
	public abstract List<AddressedOutboundMessage> processCommand(T command) throws CommandProcessingException;
}
