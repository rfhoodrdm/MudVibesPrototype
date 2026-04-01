package com.example.mudvibe.service.gameworld.commandelegate;

import java.util.List;

import com.example.mudvibe.data.gamestate.GameWorldState;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;

public class MoveCharacterCommandProcessingDelegate {

	public static List<AddressedOutboundMessage> processCommand(MoveCharacterCommand lc, GameWorldState state) {
		return List.of(); //Stub return value. TODO: implement.
	}
}
