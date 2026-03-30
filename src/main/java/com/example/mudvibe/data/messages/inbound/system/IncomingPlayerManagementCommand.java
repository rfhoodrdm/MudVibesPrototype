package com.example.mudvibe.data.messages.inbound.system;

public sealed interface IncomingPlayerManagementCommand extends IncomingSystemCommand 
	permits LoginCommand, RegisterCharacterCommand, LogoutCommand {

}
