package com.example.mudvibe.util.incomingcommand;

import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingCommand;

public record IncomingCommandParserResult (IncomingCommand parsedCommand, boolean isValid) {
	
} 
