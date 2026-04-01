package com.example.mudvibe.util.incomingcommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.common.exception.InvalidCommandException;
import com.example.mudvibe.common.exception.UnknownCommandException;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.inbound.system.LoginCommand;
import com.example.mudvibe.data.messages.inbound.system.LogoutCommand;
import com.example.mudvibe.data.messages.inbound.system.RegisterCharacterCommand;
import com.example.mudvibe.util.data.CharacterNameNormalizationUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IncomingTextCommandParserUtil {
	
	public static final String INVALID_COMMAND_MUST_BE_LOGGED_IN = "Must be logged in";
	public static final String INVALID_COMMAND_LOOK_USAGE = "LOOK  -or-  LOOK <object/character>  -or-  LOOK <object/character> <ordinal number>";
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	/**
	 * Parses 
	 * @throws UnknownCommandException if a command cannot be recognized at all.
	 * @throws InvalidCommandException if a command is recognized but not valid. E.g. missing parts to complete.
	 */
	public IncomingCommandParserResult parseCommand(String commandToParse, Optional<UUID> commandingPlayerIdMaybe) 
			throws InvalidCommandException, UnknownCommandException {
		log.debug("Inside parseCommand(). Commanding player id: {}. Command to parse: {}", commandingPlayerIdMaybe, commandToParse);
		
		if(!StringUtils.hasText(commandToParse)) {
			log.debug("Command is invalid");
			return new IncomingCommandParserResult(null, false);
		}
		
		String sanitizedTextCommand = commandToParse.trim();
		var incomingCommand = parseStringToIncomingCommand(commandingPlayerIdMaybe, sanitizedTextCommand);
		return new IncomingCommandParserResult(incomingCommand, true);
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	private IncomingCommand parseStringToIncomingCommand(Optional<UUID> commandingPlayerIdMaybe, String commandTextToParse)
			throws InvalidCommandException {
		String[] tokens = commandTextToParse.split("\\s+");
		if (tokens.length == 0) {
			return unknownCommand(commandTextToParse);
		}
		
        String commandRootWord = tokens[0].toLowerCase(Locale.ROOT);
        String[] commandArguments = tokens.length > 1 ? Arrays.copyOfRange(tokens, 1, tokens.length) : new String[0];
		
        switch (commandRootWord) {
        case "look":
        	return createLookCommand(commandingPlayerIdMaybe, commandTextToParse, commandArguments);
		case "login":
			return createPlayerManagementCommand(commandingPlayerIdMaybe, commandTextToParse, commandArguments, SystemCommandType.LOGIN);
		case "logout":
			return createPlayerManagementCommand(commandingPlayerIdMaybe, commandTextToParse, commandArguments, SystemCommandType.LOGOUT);
		case "register":
			return createPlayerManagementCommand(commandingPlayerIdMaybe, commandTextToParse, commandArguments, SystemCommandType.REGISTER);
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
            return createMovementCommand(commandingPlayerIdMaybe, commandTextToParse, commandRootWord);
		default:
			return unknownCommand(commandTextToParse);
		}
	}
	
	private IncomingCommand createLookCommand(Optional<UUID> commandingPlayerIdMaybe, String rawText, String[] commandArguments)
			throws InvalidCommandException {
		UUID commandingPlayerId = commandingPlayerIdMaybe
				.orElseThrow(() -> new InvalidCommandException(INVALID_COMMAND_MUST_BE_LOGGED_IN));
		
		if (commandArguments.length > 2) {
			throw new InvalidCommandException(INVALID_COMMAND_LOOK_USAGE);
		}
		
		Optional<String> targetNameMaybe = Optional.empty();
		Optional<Integer> targetOrdinalMaybe = Optional.empty();
		
		if (commandArguments.length >= 1) {
			String targetNameToken = commandArguments[0];
			if (!StringUtils.hasText(targetNameToken)) {
				throw new InvalidCommandException(INVALID_COMMAND_LOOK_USAGE);
			}
			targetNameMaybe = Optional.of(targetNameToken.trim());
		}
		
		if (commandArguments.length == 2) {
			String ordinalToken = commandArguments[1];
			if (!StringUtils.hasText(ordinalToken)) {
				throw new InvalidCommandException(INVALID_COMMAND_LOOK_USAGE);
			}
			
			try {
				int ordinalValue = Integer.parseInt(ordinalToken.trim());
				if (ordinalValue <= 0) {
					throw new InvalidCommandException(INVALID_COMMAND_LOOK_USAGE);
				}
				targetOrdinalMaybe = Optional.of(ordinalValue);
			} catch (NumberFormatException ex) {
				throw new InvalidCommandException(INVALID_COMMAND_LOOK_USAGE, ex);
			}
		}
		
		return new LookCommand(rawText, commandingPlayerId, targetNameMaybe, targetOrdinalMaybe);
	}

	private IncomingCommand createPlayerManagementCommand(Optional<UUID> commandingPlayerIdMaybe, String rawText, String[] commandArgs, SystemCommandType type)
			throws InvalidCommandException {
		if (commandArgs.length == 0 || !StringUtils.hasText(commandArgs[0])) {
			return unknownCommand(rawText);
		}
		
		String sanitizedPlayerName = CharacterNameNormalizationUtil.sanitize(commandArgs[0]);
		if (sanitizedPlayerName == null) {
			return unknownCommand(rawText);
		}
		
		return switch (type) {
		case LOGIN -> new LoginCommand(rawText, sanitizedPlayerName);
		case LOGOUT -> commandingPlayerIdMaybe
				.map(playerId -> new LogoutCommand(rawText, sanitizedPlayerName, playerId))
				.orElseThrow(() -> new InvalidCommandException(INVALID_COMMAND_MUST_BE_LOGGED_IN));
		case REGISTER -> new RegisterCharacterCommand(rawText, sanitizedPlayerName);
		};
	}
	
	/**
	 * Parse into an unknown command, so we can log the raw text for the error message.
	 */
	private IncomingCommand createMovementCommand(Optional<UUID> commandingPlayerId, String rawCommandText, String directionToken) {
		if (commandingPlayerId.isEmpty()) {
			log.debug("Cannot create movement command, player id missing. Raw command text: {}", rawCommandText);
			return unknownCommand(rawCommandText);
		}
		
		var direction = MovementDirectionMapper.fromToken(directionToken);
		if (direction == null) {
			return unknownCommand(rawCommandText);
		}
		return new MoveCharacterCommand(rawCommandText, commandingPlayerId.get(), direction);
	}

	private IncomingCommand unknownCommand(String rawCommandText) {
		return new IncomingCommand() {
			@Override
			public String rawCommandText() {
				return rawCommandText;
			}
		};
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
	
    /* ********************************************************
     * 	                    Static Members
     * ********************************************************/
	
	private enum SystemCommandType {
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
	

}
