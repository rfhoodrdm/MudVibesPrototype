package com.example.mudvibe.service.gameworld;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.common.interfaces.service.message.IncomingCommandQueueService;
import com.example.mudvibe.common.interfaces.service.message.OutboundMessagePublisher;
import com.example.mudvibe.data.gamestate.GameWorldState;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.inbound.character.IncomingCharacterCommand;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.CommandProcessingErrorMessage;
import com.example.mudvibe.service.gameworld.commandelegate.LookCommandProcessingDelegate;
import com.example.mudvibe.service.gameworld.commandelegate.MoveCharacterCommandProcessingDelegate;
import com.example.mudvibe.util.system.SystemClockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameWorldLogic {

	private final GameWorldState state;
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
		
		//TODO: we may eventually make command processing more efficient and multi-threaded, but for now, a straightforward loop will work.
		for ( IncomingCommand currentCommand: incomingCommandList ) {
			
			if(currentCommand instanceof IncomingCharacterCommand icc ) {
				try { 
					processCharacterCommand(icc);
				} catch (CommandProcessingException ex) {
					UUID commandingPlayerId = icc.commandingPlayerId();
					String rawCommand = icc.rawCommandText();
					String errorReason = "Cannot execute command: " + ex.getLocalizedMessage();
					
					var errorMessage = new CommandProcessingErrorMessage(commandingPlayerId, errorReason, rawCommand);
					messagePublisher.deliverOutBoundMessage(errorMessage);
				}
				
			} else {
				log.warn("Didn't know what to do with command: {}", currentCommand);
			}
		}
	}


    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/

	private void processCharacterCommand(IncomingCharacterCommand icc) throws CommandProcessingException {
		
		List<AddressedOutboundMessage> outboundMessageResultList = switch (icc) {
		case LookCommand lc 				-> LookCommandProcessingDelegate.processCommand(lc, state);
		case MoveCharacterCommand mcc 		-> MoveCharacterCommandProcessingDelegate.processCommand(mcc, state);
		};
		
		outboundMessageResultList.stream().forEach(message -> messagePublisher.deliverOutBoundMessage(message));
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

}
