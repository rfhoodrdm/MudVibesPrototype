package com.example.mudvibe.util.message;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.mudvibe.config.TextFormattingConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LongTextFormatter {
	
	private final TextFormattingConfig config;
	
	public String formatLongText(String textToFormat) {
		if (!StringUtils.hasText(textToFormat)) {
			return "";
		}
		
		final int lineLengthLimit = Math.max(config.getRoomDescriptionLineLength(), 1);
		StringBuilder sb = new StringBuilder(textToFormat.length());
		int currentLineLength = 0;
		
		String[] tokens = textToFormat.trim().split("\\s+");
		for (String token : tokens) {
			if (!StringUtils.hasText(token)) {
				continue;
			}
			
			if (token.length() >= lineLengthLimit) {
				if (currentLineLength > 0) {
					sb.append('\n');
					currentLineLength = 0;
				}
				
				sb.append(token);
				sb.append('\n');
				currentLineLength = 0;
				continue;
			}
			
			int additionalLength = token.length();
			if (currentLineLength > 0) {
				additionalLength++; // include space
			}
			
			if (currentLineLength + additionalLength > lineLengthLimit) {
				sb.append('\n');
				sb.append(token);
				currentLineLength = token.length();
			} else {
				if (currentLineLength > 0) {
					sb.append(' ');
				}
				sb.append(token);
				currentLineLength += additionalLength;
			}
		}
		
		return sb.toString();
	}
}
