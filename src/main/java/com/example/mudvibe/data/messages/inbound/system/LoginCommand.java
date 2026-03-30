package com.example.mudvibe.data.messages.inbound.system;

public record LoginCommand(String rawCommandText, String playerName) implements IncomingPlayerManagementCommand {

}
