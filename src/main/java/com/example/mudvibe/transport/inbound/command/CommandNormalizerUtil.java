package com.example.mudvibe.transport.inbound.command;

import static java.util.Map.entry;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.example.mudvibe.common.exception.UnknownCommandException;

/**
 * Recognizes synonyms for incoming commands and translates them into a canonical format.
 */
public final class CommandNormalizerUtil {

	private static final Map<String, String> COMMAND_ALIAS_DICTIONARY = Map.ofEntries(
			entry("'", "say "),
			entry("/s", "say "),
			entry("n", "north "),
			entry("north-east", "northeast "),
			entry("ne", "northeast "),
			entry("north-west", "northwest "),
			entry("nw", "northwest "),
			entry("s", "south "),
			entry("south-east", "southeast "),
			entry("se", "southeast "),
			entry("south-west", "southwest "),
			entry("sw", "southwest "),
			entry("e", "east "),
			entry("w", "west "),
			entry("u", "up "),
			entry("d", "down ")
	);

	private CommandNormalizerUtil() {
		// utility class
	}

	public static String normalizeCommand(String incomingCommand) throws UnknownCommandException {

		String trimmedCommand = StringUtils.trim(incomingCommand);
		if (StringUtils.isBlank(trimmedCommand)) {
			throw new UnknownCommandException("Unknown command: command line was blank.");
		}
		
		String leadingCharacter = extractLeadingCharacter(trimmedCommand);
		String firstWord = extractFirstWord(trimmedCommand);
		
		if (StringUtils.isNotEmpty(leadingCharacter)
				&& !Character.isLetterOrDigit(trimmedCommand.charAt(0))
				&& COMMAND_ALIAS_DICTIONARY.containsKey(leadingCharacter)) {
			String replacement = COMMAND_ALIAS_DICTIONARY.get(leadingCharacter);
			String remainingText = trimmedCommand.substring(1);
			return replacement + remainingText.stripLeading();
		} else if (StringUtils.isNotEmpty(firstWord) && COMMAND_ALIAS_DICTIONARY.containsKey(firstWord)) {
			String replacement = COMMAND_ALIAS_DICTIONARY.get(firstWord);
			if (trimmedCommand.length() == firstWord.length()) {
				return replacement.strip();
			}
			String remainingText = trimmedCommand.substring(firstWord.length());
			return replacement + remainingText.stripLeading();
		} else {
			return trimmedCommand;
		}
	}
	
	private static String extractLeadingCharacter(String command) {
		if (command.isEmpty()) {
			return "";
		}
		return command.substring(0, 1).toLowerCase(Locale.ROOT);
	}
	
	private static String extractFirstWord(String command) {
		if (command.isEmpty()) {
			return "";
		}
		for (int i = 0; i < command.length(); i++) {
			if (Character.isWhitespace(command.charAt(i))) {
				return command.substring(0, i).toLowerCase(Locale.ROOT);
			}
		}
		return command.toLowerCase(Locale.ROOT);
	}
}
