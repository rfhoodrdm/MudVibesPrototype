package com.example.mudvibe.service.gameworld.commandelegate;

import java.util.List;

import com.example.mudvibe.common.interfaces.service.player.PlayerManagerService;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.service.area.AreaManagerService;

public class MoveCharacterCommandProcessingDelegate {

	public static List<AddressedOutboundMessage> processCommand(MoveCharacterCommand lc, 
			PlayerManagerService playerManagerService, AreaManagerService areaManagerService) {
		return List.of(); //Stub return value. TODO: implement.
	}
}
