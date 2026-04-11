package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.SpeechDescriptionMessage;

public class SpeechDescriptionMessageFormatter {

	public static String format(SpeechDescriptionMessage sdm) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(sdm.speaker()).append(" ");
		sb.append(sdm.speechModeVerb()).append(" ");
		sb.append("\"").append(sdm.speech()).append("\"");
		
		return sb.toString();
	}
}
