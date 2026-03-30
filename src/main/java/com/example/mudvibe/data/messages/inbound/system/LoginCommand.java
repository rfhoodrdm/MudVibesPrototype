package com.example.mudvibe.data.messages.inbound.system;

import com.example.mudvibe.common.interfaces.data.message.incoming.IncomingPlayerManagementCommand;

public record LoginCommand(String playerName) implements IncomingPlayerManagementCommand {

}
