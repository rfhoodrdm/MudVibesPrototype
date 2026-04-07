package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import org.springframework.stereotype.Component;

import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.util.message.LongTextFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomDescriptionMessageFormatter {
	
	private final LongTextFormatter longTextFormatter;
	
	public String format(RoomDescriptionMessage message) {
		var sb = new StringBuilder();
		String formattedRoomDescription = longTextFormatter.formatLongText(message.roomDescription());
		String exits = formatExits(message);
		String playersHere = formatPlayersHere(message);

		sb.append("\n");	//extra space to distinguish room movement.
		sb.append("[ ").append(message.roomTitle()).append(" ] \n");
		sb.append(formattedRoomDescription).append("\n");
		sb.append("- Exits: ").append(exits).append('\n');
		sb.append("- Also here: ").append(playersHere);
		return sb.toString();
	}

	private String formatPlayersHere(RoomDescriptionMessage message) {
		if (message.otherCharacters() == null || message.otherCharacters().isEmpty()) {
			return "no one";
		}
		
		return message.otherCharacters().stream()
				.sorted(String::compareToIgnoreCase)
				.reduce((a, b) -> a + ", " + b)
				.orElse("no one");
	}

	private String formatExits(RoomDescriptionMessage message) {
		if (message.exits() == null || message.exits().isEmpty()) {
			return "none";
		}
		
		return message.exits().stream()
				.map(direction -> direction.name().toLowerCase())
				.sorted()
				.reduce((a, b) -> a + ", " + b)
				.orElse("none");
	}
}
