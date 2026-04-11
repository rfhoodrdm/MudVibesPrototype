package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.mudvibe.common.enums.MovementDirection;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.util.message.LongTextFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomDescriptionMessageFormatter {
	
	private final LongTextFormatter longTextFormatter;
	private static final Comparator<MovementDirection> MOVEMENT_DIRECTION_COMPARATOR =
	        Comparator.comparingInt(MovementDirection::ordinal);
	
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
		        .sorted(MOVEMENT_DIRECTION_COMPARATOR)
				.map(direction -> direction.name().toLowerCase())
				.collect(Collectors.joining(", "));
	}
}
