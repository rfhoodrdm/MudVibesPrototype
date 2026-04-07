package com.example.mudvibe.commandqueue;

import java.util.List;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

public interface IncomingCommandQueue<T extends IncomingCommand> {

	public void enqueueCommand(T incomingCommand);
	
	public List<T> getEnqueuedCommands();
}
