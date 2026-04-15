package com.example.mudvibe.transport.outbound.messageformatter.formatters;

import com.example.mudvibe.data.messages.outbound.CharacterLogDescriptionMessage;

public class CharacterLogDescriptionMessageFormatter {

	public static String format(CharacterLogDescriptionMessage cldm) {
		return switch (cldm.action()) {
		case LOGIN -> cldm.characterName() + " shimmers into existence.";
		case LOGOUT -> cldm.characterName() + " slowly fades from existence.";
		};
	}

}
