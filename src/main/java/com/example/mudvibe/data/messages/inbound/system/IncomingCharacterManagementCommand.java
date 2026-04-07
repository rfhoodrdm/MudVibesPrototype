package com.example.mudvibe.data.messages.inbound.system;

import com.example.mudvibe.data.messages.inbound.interfaces.HasCommandingPlayerId;

public sealed interface IncomingCharacterManagementCommand 
	extends IncomingSystemCommand, HasCommandingPlayerId
	permits LoginCommand, RegisterCharacterCommand, LogoutCommand {

}
