package com.example.mudvibe.gameworldengine;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.commandqueue.IncomingSystemCommandQueue;
import com.example.mudvibe.common.exception.CommandProcessingException;
import com.example.mudvibe.data.messages.inbound.interfaces.HasCommandingPlayerId;
import com.example.mudvibe.data.messages.inbound.system.CharacterRosterListCommand;
import com.example.mudvibe.data.messages.inbound.system.IncomingSystemCommand;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.data.messages.outbound.CommandProcessingErrorMessage;
import com.example.mudvibe.gameworldengine.clock.GameTickSubscriber;
import com.example.mudvibe.gameworldengine.delegates.command.CharacterRosterCommandProcessingDelegate;
import com.example.mudvibe.gameworldengine.delegates.command.LoginCommandProcessingDelegate;
import com.example.mudvibe.gameworldengine.delegates.command.LogoutCommandProcessingDelegate;
import com.example.mudvibe.gameworldengine.delegates.command.RegisterCharacterCommandProcessingDelegate;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;
import com.example.mudvibe.transport.outbound.messagepublisher.OutboundMessagePublisher;
import com.example.mudvibe.util.system.SystemClockUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleSystemCommandProcessor implements GameTickSubscriber {

	private final SystemClockUtil clockUtil;
	private final PlayerCharacterManager characterManager;
	private final AreaManager areaManager;
	private final IncomingSystemCommandQueue commandQueue;
	private final OutboundMessagePublisher messagePublisher;
	
	private final LoginCommandProcessingDelegate loginCommandProcessingDelegate;
	private final LogoutCommandProcessingDelegate logoutCommandProcessingDelegate;
	private final RegisterCharacterCommandProcessingDelegate registerCharacterCommandProcessingDelegate;
	private final CharacterRosterCommandProcessingDelegate characterRosterCommandProcesingDelegate;
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	public void update() {
		Instant now = clockUtil.getNow();
		log.debug("Begin update. Current time: {}", now);
		
		List<IncomingSystemCommand> incomingCommandList = commandQueue.getEnqueuedCommands();
		log.debug("Now processing {} incoming commands.", incomingCommandList.size());
		
		//TODO: we may eventually make command processing more efficient and multi-threaded, but for now, a straightforward loop will work.
		for (IncomingSystemCommand isc : incomingCommandList) {
			try {
				processCharacterCommand(isc);
			} catch (CommandProcessingException ex) {
				String rawCommand = isc.rawCommandText();
				String errorReason = "Cannot execute command: " + ex.getLocalizedMessage();

				log.error("Unable to execute system command {}: {}", rawCommand, ex.getLocalizedMessage());
				
				if (isc instanceof HasCommandingPlayerId hcpi) {
					UUID commandingPlayerId = hcpi.commandingPlayerId();
					var errorMessage = new CommandProcessingErrorMessage(commandingPlayerId, errorReason, rawCommand);
					messagePublisher.sendAddressedMessage(errorMessage);
				}
			}
		}
	}


     
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/

	private void processCharacterCommand(IncomingSystemCommand isc) throws CommandProcessingException {
		
		List<AddressedOutboundMessage> outboundMessageResultList = switch (isc) {
		case LoginCommand lic 					-> loginCommandProcessingDelegate.processCommand(lic);
		case LogoutCommand loc					-> logoutCommandProcessingDelegate.processCommand(loc);
		case RegisterCharacterCommand rcc 		-> registerCharacterCommandProcessingDelegate.processCommand(rcc);
		case CharacterRosterListCommand crlc 	-> characterRosterCommandProcesingDelegate.processCommand(crlc);
		};
		
		outboundMessageResultList.stream().forEach(message -> messagePublisher.sendAddressedMessage(message));
		
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
     
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
}
