package com.example.mudvibe.gameworldengine.commanddelegate;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.AreaManager;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

@Component
public class MoveCharacterCommandProcessingDelegate implements CommandProcessingDelegate<MoveCharacterCommand> {

	@Override
	public List<AddressedOutboundMessage> processCommand(MoveCharacterCommand lc, 
			PlayerCharacterManager playerCharacterManager, 
			AreaManager areaManager) {
		return List.of(); //Stub return value. TODO: implement.
	}
}
