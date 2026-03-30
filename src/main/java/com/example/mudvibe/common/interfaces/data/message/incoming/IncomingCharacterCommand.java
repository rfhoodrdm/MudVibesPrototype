package com.example.mudvibe.common.interfaces.data.message.incoming;

import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;

public sealed interface IncomingCharacterCommand extends IncomingCommand 
	permits MoveCharacterCommand {

}
