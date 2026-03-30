package com.example.mudvibe.common.interfaces.data.message.incoming;

import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;

public sealed interface IncomingPlayerManagementCommand extends IncomingSystemCommand 
	permits LoginCommand, RegisterCharacterCommand, LogoutCommand {

}
