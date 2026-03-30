package com.example.mudvibe.util.data;

import org.springframework.util.StringUtils;

public final class CharacterNameNormalizationUtil {

	private CharacterNameNormalizationUtil() {
		// utility class
	}
	
	public static String sanitize(String playerName) {
		if (!StringUtils.hasText(playerName)) {
			return null;
		}
		return playerName.trim();
	}
	
	public static String normalize(String playerName) {
		String sanitized = sanitize(playerName);
		return sanitized != null ? sanitized.toLowerCase() : null;
	}
}
