package com.example.mudvibe.common.interfaces.service.message;

import java.util.List;

import com.example.mudvibe.common.interfaces.data.message.IncomingCommand;

public interface IncomingCommandQueueService {
	
	public boolean enqueueCommand(IncomingCommand incomingCommand);
	
	public List<IncomingCommand> getEnqueuedCommands();

}
