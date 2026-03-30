package com.example.mudvibe.util.incomingcommand;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

public record IncomingCommandParserResult (IncomingCommand parsedCommand, boolean isValid) {
	
} 
