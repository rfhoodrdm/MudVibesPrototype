package com.example.mudvibe.common.interfaces.service.message;

import java.util.List;
import java.util.Optional;

import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingCommand;
import com.example.mudvibe.common.interfaces.data.message.outbound.SimpleOutboundMessage;

public interface IncomingCommandQueueService {
	
	public Optional<SimpleOutboundMessage> enqueueCommand(IncomingCommand incomingCommand);
	
	public List<IncomingCommand> getEnqueuedCommands();

}
