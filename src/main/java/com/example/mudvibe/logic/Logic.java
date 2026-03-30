package com.example.mudvibe.logic;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.interfaces.data.message.IncomingCommand;
import com.example.mudvibe.common.interfaces.service.message.IncomingCommandQueueService;
import com.example.mudvibe.common.interfaces.service.message.OutboundMessagePublisher;
import com.example.mudvibe.state.State;
import com.example.mudvibe.util.system.SystemClockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class Logic {

	private final State state;
	private final SystemClockUtil clockUtil;
	private final IncomingCommandQueueService incomingCommandQueue;
	private final OutboundMessagePublisher messagePublisher;
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	public void update() {
		Instant now = clockUtil.getNow();
		log.debug("Begin update. Current time: {}", now);
		
		List<IncomingCommand> incomingCommandList = incomingCommandQueue.getEnqueuedCommands();
		log.debug("Now processing {} incoming commands.", incomingCommandList.size());
		
		//TODO: perform command processing.
	}

    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

}
