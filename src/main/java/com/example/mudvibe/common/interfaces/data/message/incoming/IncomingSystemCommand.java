package com.example.mudvibe.common.interfaces.data.message.incoming;

public sealed interface IncomingSystemCommand extends IncomingCommand 
	permits IncomingPlayerManagementCommand {

}
