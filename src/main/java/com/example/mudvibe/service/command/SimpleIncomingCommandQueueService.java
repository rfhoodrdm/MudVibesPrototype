package com.example.mudvibe.service.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.example.mudvibe.common.interfaces.data.message.IncomingCommand;
import com.example.mudvibe.common.interfaces.service.message.IncomingCommandQueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleIncomingCommandQueueService implements IncomingCommandQueueService {

	private final Queue<IncomingCommand> queuedCommands = new ConcurrentLinkedQueue<>();

	@Override
	public boolean enqueueCommand(IncomingCommand incomingCommand) {
		log.debug("Inside enqueueCommand(). Incoming command: {}", incomingCommand);

		if (incomingCommand == null) {
			log.debug("Leaving enqueueCommand(). Cannot enqueue null command.");
			return false;
		}

		boolean result = queuedCommands.offer(incomingCommand);
		log.debug("Leaving enqueueCommand(). Enqueue success: {}", result);
		return result;
	}

	@Override
	public List<IncomingCommand> getEnqueuedCommands() {
		log.debug("Inside emptyCommandQueue().");

		List<IncomingCommand> drainedCommands = new ArrayList<>();
		IncomingCommand command;
		while ((command = queuedCommands.poll()) != null) {
			drainedCommands.add(command);
		}

		log.debug("Leaving emptyCommandQueue(). Commands drained: {}", drainedCommands.size());
		return drainedCommands;
	}
}
