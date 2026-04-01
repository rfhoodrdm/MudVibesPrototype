package com.example.mudvibe.data.messages.inbound.character;

import java.util.UUID;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

public sealed interface IncomingCharacterCommand extends IncomingCommand 
	permits MoveCharacterCommand, LookCommand {

	public UUID commandingPlayerId();
}
