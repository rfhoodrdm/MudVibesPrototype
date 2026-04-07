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
		
		sb.append("[ ").append(message.roomTitle()).append(" ] \n");
		sb.append(formattedRoomDescription);
		
		return sb.toString();
	}
}
