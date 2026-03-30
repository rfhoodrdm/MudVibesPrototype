package com.example.mudvibe.util.incomingcommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IncomingTextCommandParserUtil {
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	/**
	 * 1) Text command should be not blank.
	 */
	public IncomingCommandParserResult parseCommand(String commandToParse) {
		log.debug("Inside parseCommand(). Command to parse: {}", commandToParse);
		
		if(!StringUtils.hasText(commandToParse)) {
			log.debug("Command is invalid");
			return new IncomingCommandParserResult(null, false);
		}
		
		String sanitizedTextCommand = commandToParse.trim();
		var incomingCommand = parseStringToIncomingCommand(sanitizedTextCommand);
		return new IncomingCommandParserResult(incomingCommand, true);
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	private IncomingCommand parseStringToIncomingCommand(String commandTextToParse) {
		String[] tokens = commandTextToParse.split("\\s+");
		if (tokens.length == 0) {
			return unknownCommand(commandTextToParse);
		}
		
        String commandRootWord = tokens[0].toLowerCase(Locale.ROOT);
        String[] commandArguments = tokens.length > 1 ? Arrays.copyOfRange(tokens, 1, tokens.length) : new String[0];
		
        switch (commandRootWord) {
		case "login":
			return createPlayerManagementCommand(commandTextToParse, commandArguments, CommandType.LOGIN);
		case "logout":
			return createPlayerManagementCommand(commandTextToParse, commandArguments, CommandType.LOGOUT);
		case "register":
			return createPlayerManagementCommand(commandTextToParse, commandArguments, CommandType.REGISTER);
        case "north": case "n":
        case "south": case "s":
        case "east": case "e":
        case "west": case "w":
        case "northeast": case "ne": case "north-east":
        case "northwest": case "nw": case "north-west":
        case "southeast": case "se": case "south-east":
        case "southwest": case "sw": case "south-west":
        case "up": case "u":
        case "down": case "d":
            return createMovementCommand(commandTextToParse, commandRootWord);
		default:
			return unknownCommand(commandTextToParse);
		}
	}
	
	private IncomingCommand createPlayerManagementCommand(String rawText, String[] commandArgs, CommandType type) {
		if (commandArgs.length == 0 || !StringUtils.hasText(commandArgs[0])) {
			return unknownCommand(rawText);
		}
		
		String sanitizedPlayerName = CharacterNameNormalizationUtil.sanitize(commandArgs[0]);
		if (sanitizedPlayerName == null) {
			return unknownCommand(rawText);
		}
		
		return switch (type) {
		case LOGIN -> new LoginCommand(rawText, sanitizedPlayerName);
		case LOGOUT -> new LogoutCommand(rawText, sanitizedPlayerName);
		case REGISTER -> new RegisterCharacterCommand(rawText, sanitizedPlayerName);
		};
	}
	
	/**
	 * Parse into an unknown command, so we can log the raw text for the error message.
	 */
	private IncomingCommand createMovementCommand(String rawCommandText, String directionToken) {
		var direction = MovementDirectionMapper.fromToken(directionToken);
		if (direction == null) {
			return unknownCommand(rawCommandText);
		}
		return new MoveCharacterCommand(rawCommandText, null, direction);
	}

	private IncomingCommand unknownCommand(String rawCommandText) {
		return new IncomingCommand() {
			@Override
			public String rawCommandText() {
				return rawCommandText;
			}
		};
	}
	
	private enum CommandType {
		LOGIN,
		LOGOUT,
		REGISTER
	}

	private static final class MovementDirectionMapper {
		private static final Map<String, MovementDirection> LOOKUP = new HashMap<>();
		static {
			register(MovementDirection.NORTH, "north", "n");
			register(MovementDirection.SOUTH, "south", "s");
			register(MovementDirection.EAST, "east", "e");
			register(MovementDirection.WEST, "west", "w");
			register(MovementDirection.NORTHEAST, "northeast", "north-east", "ne");
			register(MovementDirection.NORTHWEST, "northwest", "north-west", "nw");
			register(MovementDirection.SOUTHEAST, "southeast", "south-east", "se");
			register(MovementDirection.SOUTHWEST, "southwest", "south-west", "sw");
			register(MovementDirection.UP, "up", "u");
			register(MovementDirection.DOWN, "down", "d");
		}
		
		private static void register(MovementDirection direction, String... aliases) {
			for (String alias : aliases) {
				LOOKUP.put(alias, direction);
			}
		}
		
		private static MovementDirection fromToken(String token) {
			if (token == null) {
				return null;
			}
			return LOOKUP.get(token.toLowerCase(Locale.ROOT));
		}
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
}
