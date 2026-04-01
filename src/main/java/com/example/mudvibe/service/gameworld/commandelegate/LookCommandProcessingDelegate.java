package com.example.mudvibe.service.gameworld.commandelegate;

import java.util.List;

import com.example.mudvibe.data.gamestate.GameWorldState;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;

public class LookCommandProcessingDelegate {

	
	public static List<AddressedOutboundMessage> processCommand(LookCommand lc, GameWorldState state) {
		return List.of(); //Stub return value. TODO: implement.
	}
}
