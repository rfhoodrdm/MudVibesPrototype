package com.example.mudvibe.data.messages.inbound.system;

public record LogoutCommand(String rawCommandText, String playerName) implements IncomingPlayerManagementCommand {

}
