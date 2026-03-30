package com.example.mudvibe.util.incomingcommand;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.mudvibe.common.interfaces.data.message.IncomingCommand;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IncomingTextCommandParserUtil {
	
	/**
	 * 1) Text command should be not blank.
	 */
	public IncomingCommandParserResult parseCommand(String commandToParse) {
		log.debug("Inside parseCommand(). Command to parse: {}", commandToParse);
		
		if(!StringUtils.hasText(commandToParse)) {
			log.debug("Command is invalid");
			return new IncomingCommandParserResult(null, false);
		}
		
		String sanitizedTextCommand = commandToParse.trim();
		var incomingCommand = parseStringToIncomingCommand(sanitizedTextCommand);
		return new IncomingCommandParserResult(incomingCommand, true);
	}
	
	
	private IncomingCommand parseStringToIncomingCommand(String commandToParse) {
		//TODO: implement parsing of command
		var incomingCommand = new IncomingCommand() {}; //placeholder
		return incomingCommand;
	}
}
