package com.example.mudvibe.commandqueue;

import org.springframework.stereotype.Service;

import com.example.mudvibe.data.messages.inbound.character.IncomingCharacterCommand;

@Service
public final class SimpleIncomingCharacterCommandQueue extends SimpleIncomingCommandQueue<IncomingCharacterCommand>
	implements IncomingCharacterCommandQueue {

}
