package com.example.mudvibe.gameworldengine;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.commandqueue.IncomingCharacterCommandQueue;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.character.IncomingCharacterCommand;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.CommandProcessingErrorMessage;
import com.example.mudvibe.gameworldengine.clock.GameTickSubscriber;
import com.example.mudvibe.gameworldengine.delegates.command.LookCommandProcessingDelegate;
import com.example.mudvibe.gameworldengine.delegates.command.MoveCharacterCommandProcessingDelegate;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;
import com.example.mudvibe.transport.outbound.messagepublisher.OutboundMessagePublisher;
import com.example.mudvibe.util.system.SystemClockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameWorldLogic implements GameTickSubscriber {

	private final SystemClockUtil clockUtil;
	private final IncomingCharacterCommandQueue commandQueue;
	private final OutboundMessagePublisher messagePublisher;

	private final PlayerCharacterManager playerCharacterManager;
	private final AreaManager areaManager;
	private final LookCommandProcessingDelegate lookCommandProcessingDelegate;
	private final MoveCharacterCommandProcessingDelegate moveCharacterCommandProcessingDelegate;
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	public void update() {
		Instant now = clockUtil.getNow();
		log.debug("Begin update. Current time: {}", now);
		
		List<IncomingCharacterCommand> incomingCommandList = commandQueue.getEnqueuedCommands();
		log.debug("Now processing {} incoming commands.", incomingCommandList.size());
		
		//TODO: we may eventually make command processing more efficient and multi-threaded, but for now, a straightforward loop will work.
		for (IncomingCharacterCommand icc : incomingCommandList) {
			try {
				processCharacterCommand(icc);
			} catch (CommandProcessingException ex) {
				UUID commandingPlayerId = icc.commandingPlayerId();
				String rawCommand = icc.rawCommandText();
				String errorReason = "Cannot execute command: " + ex.getLocalizedMessage();
				
				log.error(errorReason);

				var errorMessage = new CommandProcessingErrorMessage(commandingPlayerId, errorReason, rawCommand);
				messagePublisher.sendAddressedMessage(errorMessage);
			}
		}
	}

    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/

	private void processCharacterCommand(IncomingCharacterCommand icc) throws CommandProcessingException {
		
		List<AddressedOutboundMessage> outboundMessageResultList = switch (icc) {
		case LookCommand lc 				-> lookCommandProcessingDelegate.processCommand(lc);
		case MoveCharacterCommand mcc 		-> moveCharacterCommandProcessingDelegate.processCommand(mcc);
		};
		
		outboundMessageResultList.stream().forEach(message -> messagePublisher.sendAddressedMessage(message));
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

}
