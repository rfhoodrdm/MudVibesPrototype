package com.example.mudvibe.data.messages.inbound.system;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

public sealed interface IncomingSystemCommand extends IncomingCommand 
	permits IncomingPlayerManagementCommand {

}
