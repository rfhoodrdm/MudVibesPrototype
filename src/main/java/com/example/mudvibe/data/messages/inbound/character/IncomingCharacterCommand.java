package com.example.mudvibe.data.messages.inbound.character;

import java.util.UUID;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.inbound.interfaces.HasCommandingPlayerId;

public sealed interface IncomingCharacterCommand 
	extends IncomingCommand, HasCommandingPlayerId
	permits MoveCharacterCommand, LookCommand, SpeechCommand {

	public UUID commandingPlayerId();
}
