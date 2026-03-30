package com.example.mudvibe.data.messages.inbound.system;

public record RegisterCharacterCommand(String rawCommandText, String playerName) implements IncomingPlayerManagementCommand {

}
