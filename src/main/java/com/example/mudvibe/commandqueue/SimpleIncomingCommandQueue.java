package com.example.mudvibe.commandqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public sealed abstract class SimpleIncomingCommandQueue<T extends IncomingCommand>
	implements IncomingCommandQueue<T>
	permits SimpleIncomingCharacterCommandQueue, SimpleIncomingSystemCommandQueue {
	
	private final Queue<T> queuedCommands = new ConcurrentLinkedQueue<>();

    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/

	@Override
	public void enqueueCommand(T incomingCommand) {
		log.debug("Inside enqueueCommand(). Incoming command: {}", incomingCommand);

		if (incomingCommand == null) {
			log.debug("Leaving enqueueCommand(). Cannot enqueue null command.");
			return;
		}
		
		boolean result = queuedCommands.offer(incomingCommand);
		log.debug("Leaving enqueueCommand(). Enqueue success: {}", result);
		return;
	}

	@Override
	public List<T> getEnqueuedCommands() {
		log.debug("Inside emptyCommandQueue().");

		List<T> drainedCommands = new ArrayList<>();
		T command;
		while ((command = queuedCommands.poll()) != null) {
			drainedCommands.add(command);
		}

		log.debug("Leaving emptyCommandQueue(). Commands drained: {}", drainedCommands.size());
		return drainedCommands;
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
    
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
     
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
}
