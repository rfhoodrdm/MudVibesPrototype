package com.example.mudvibe.data.messages.inbound.character;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

public sealed interface IncomingCharacterCommand extends IncomingCommand 
	permits MoveCharacterCommand {

}
