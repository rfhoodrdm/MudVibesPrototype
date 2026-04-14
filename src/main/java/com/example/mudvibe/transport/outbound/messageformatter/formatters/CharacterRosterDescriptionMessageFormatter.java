package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import java.util.List;
import com.example.mudvibe.data.messages.outbound.CharacterRosterDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.CharacterRosterDescriptionMessage.CharacterRosterEntry;

public class CharacterRosterDescriptionMessageFormatter {

	public static String format(CharacterRosterDescriptionMessage message) {
		List<CharacterRosterEntry> rosterEntryList =  message.rosterEntryList();
		StringBuilder sb = new StringBuilder();
		
		sb.append("Your characters:").append("\n");
		
		for(CharacterRosterEntry entry: rosterEntryList) {
			sb.append(entry.characterName()).append("\n");
		}
		
		return sb.toString();
	}
}
