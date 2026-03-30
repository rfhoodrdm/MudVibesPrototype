package com.example.mudvibe.data.messages.inbound.character;

import java.util.UUID;

import com.example.mudvibe.common.enums.MovementDirection;

public record MoveCharacterCommand(String rawCommandText, UUID playerId, MovementDirection direction) implements IncomingCharacterCommand {

}
