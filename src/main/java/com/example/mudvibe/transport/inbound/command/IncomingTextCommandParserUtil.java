package com.example.mudvibe.transport.inbound.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.common.enums.SpeechMode;
import com.example.mudvibe.common.exception.InvalidCommandException;
import com.example.mudvibe.common.exception.UnknownCommandException;
import com.example.mudvibe.data.messages.inbound.IncomingCommand;
import com.example.mudvibe.data.messages.inbound.character.LookCommand;
import com.example.mudvibe.data.messages.inbound.character.MoveCharacterCommand;
import com.example.mudvibe.data.messages.inbound.character.SpeechCommand;
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
	public IncomingCommandParserResult parseCommand(UUID commandingPlayerId, String commandToParse) 
			throws InvalidCommandException, UnknownCommandException {
		log.debug("Inside parseCommand(). Commanding player id: {}. Command to parse: {}", commandingPlayerId, commandToParse);
		
		if(!StringUtils.hasText(commandToParse)) {
			log.debug("Command is invalid: blank");
			throw new UnknownCommandException("Unknown command: command line was blank.");
		}
		
		String sanitizedTextCommand = CommandNormalizerUtil.normalizeCommand(commandToParse);
		var incomingCommand = parseStringToIncomingCommand(commandingPlayerId, sanitizedTextCommand);
		return new IncomingCommandParserResult(incomingCommand);
	}

    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/

	
	private IncomingCommand parseStringToIncomingCommand(UUID commandingPlayerId, String commandTextToParse)
			throws InvalidCommandException, UnknownCommandException {
		String[] tokens = commandTextToParse.split("\\s+");
		
        String commandRootWord = tokens[0].toLowerCase(Locale.ROOT);
        String[] commandArguments = tokens.length > 1 ? Arrays.copyOfRange(tokens, 1, tokens.length) : new String[0];
		
        switch (commandRootWord) {
        case "say":
        	return createSpeechCommand(commandingPlayerId, commandTextToParse);
        case "look":
        	return createLookCommand(commandingPlayerId, commandTextToParse, commandArguments);
		case "login":
			return createCharacterManagementCommand(commandingPlayerId, commandTextToParse, commandArguments, SystemCommandType.LOGIN);
		case "logout":
			return createCharacterManagementCommand(commandingPlayerId, commandTextToParse, commandArguments, SystemCommandType.LOGOUT);
		case "register":
			return createCharacterManagementCommand(commandingPlayerId, commandTextToParse, commandArguments, SystemCommandType.REGISTER);
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
            return createMovementCommand(commandingPlayerId, commandTextToParse, commandRootWord);
		default:
			throw new UnknownCommandException("Unknown command: " + commandTextToParse);
		}
	}
	
	private IncomingCommand createSpeechCommand(UUID commandingPlayerId, String commandTextToParse) {
		String spokenText = commandTextToParse.substring("say".length()).stripLeading();
		return new SpeechCommand(commandTextToParse, commandingPlayerId, SpeechMode.SAY, spokenText);
	}

	private IncomingCommand createLookCommand(UUID commandingPlayerId, String rawText, String[] commandArguments)
			throws InvalidCommandException {
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

	private IncomingCommand createCharacterManagementCommand(UUID commandingPlayerId, String rawText, String[] commandArgs, SystemCommandType type)
			throws InvalidCommandException, UnknownCommandException {
		if (commandArgs.length == 0 || !StringUtils.hasText(commandArgs[0])) {
			throw new InvalidCommandException("Invalid command: missing character name.");
		}
		
		String sanitizedCharacterName = CharacterNameNormalizationUtil.sanitize(commandArgs[0]);
		
		return switch (type) {
		case LOGIN -> new LoginCommand(rawText, commandingPlayerId, sanitizedCharacterName);
		case LOGOUT -> new LogoutCommand(rawText, commandingPlayerId, sanitizedCharacterName);
		case REGISTER -> new RegisterCharacterCommand(rawText, commandingPlayerId, sanitizedCharacterName);
		};
	}
	
	/**
	 * Parse into an unknown command, so we can log the raw text for the error message.
	 */
	private IncomingCommand createMovementCommand(UUID commandingPlayerId, String rawCommandText, String directionToken)
		throws InvalidCommandException {
		
		//can we replace this with Optional.ofnullable().orElseThrow()?
		var direction = Optional.ofNullable(MovementDirectionMapper.fromToken(directionToken))
				.orElseThrow(() -> new InvalidCommandException("Invalid command: unrecognized direction."));
		
		return new MoveCharacterCommand(rawCommandText, commandingPlayerId, direction);
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
