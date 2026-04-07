package com.example.mudvibe.commandqueue;

import org.springframework.stereotype.Service;

import com.example.mudvibe.data.messages.inbound.system.IncomingSystemCommand;

@Service
public final class SimpleIncomingSystemCommandQueue extends SimpleIncomingCommandQueue<IncomingSystemCommand>
	implements IncomingSystemCommandQueue {

}
