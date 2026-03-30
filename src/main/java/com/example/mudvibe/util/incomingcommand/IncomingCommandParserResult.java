package com.example.mudvibe.util.incomingcommand;

import com.example.mudvibe.common.interfaces.data.message.IncomingCommand;

public record IncomingCommandParserResult (IncomingCommand parsedCommand, boolean isValid) {
	
} 
