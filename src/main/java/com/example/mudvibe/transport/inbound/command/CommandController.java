package com.example.mudvibe.transport.inbound.command;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.mudvibe.commandqueue.IncomingCharacterCommandQueue;
import com.example.mudvibe.commandqueue.IncomingSystemCommandQueue;
import com.example.mudvibe.common.exception.InvalidCommandException;
import com.example.mudvibe.common.exception.UnknownCommandException;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.inbound.character.IncomingCharacterCommand;
import com.example.mudvibe.data.messages.inbound.system.IncomingSystemCommand;
import com.example.mudvibe.data.messages.outbound.AddressedEchoMessage;
import com.example.mudvibe.data.messages.outbound.AddressedSystemErrorMessage;
import com.example.mudvibe.transport.outbound.messagepublisher.OutboundMessagePublisher;
import com.example.mudvibe.util.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommandController {

	private final SecurityUtil securityUtil;
	private final IncomingTextCommandParserUtil parser;

	private final IncomingCharacterCommandQueue incomingPlayerCommandQueue;
	private final IncomingSystemCommandQueue incomingSystemCommandQueue;
	private final OutboundMessagePublisher messagePublisher;
	
	@PostMapping("/api/commands")
	public ResponseEntity<Void> processCommand(@RequestBody String rawCommand) {
		log.debug("Inside processCommand(). Raw command: {}", rawCommand);
		UUID playerId = securityUtil.getPlayerId();
		
		try {
			var echo = new AddressedEchoMessage(playerId, rawCommand);
			messagePublisher.sendAddressedMessage(echo);
			
			IncomingCommandParserResult parseResult = parser.parseCommand(playerId, rawCommand);
			log.debug("Parsed incoming command from user with id {}: {}", playerId, parseResult);
			IncomingCommand parsedCommand = parseResult.parsedCommand();
			
			switch (parsedCommand) {
				case IncomingCharacterCommand icc   -> incomingPlayerCommandQueue.enqueueCommand(icc);
				case IncomingSystemCommand isc      -> incomingSystemCommandQueue.enqueueCommand(isc);
				case null, default 					-> throw new UnknownCommandException("Didn't know what to do with: " + parsedCommand.rawCommandText());
			}
			
			return ResponseEntity.noContent().build();
			
		} catch (InvalidCommandException | UnknownCommandException ex) {
			String errorMessageText = "Could not parse command: " + ex.getLocalizedMessage();
			var errorMessage = new AddressedSystemErrorMessage(playerId, errorMessageText);
			messagePublisher.sendAddressedMessage(errorMessage);
			
			return ResponseEntity.noContent().build();
		}
	}
	
}
