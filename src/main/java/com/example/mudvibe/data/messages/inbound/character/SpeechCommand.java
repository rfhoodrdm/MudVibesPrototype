package com.example.mudvibe.data.messages.inbound.character;

import java.util.Objects;
import java.util.UUID;

import com.example.mudvibe.common.enums.SpeechMode;

public record SpeechCommand(String rawCommandText, UUID commandingPlayerId, SpeechMode speechMode, String speech) implements IncomingCharacterCommand  {

	public SpeechCommand {
		Objects.requireNonNull(rawCommandText, "rawCommandText may not be null.");
		Objects.requireNonNull(commandingPlayerId, "commandingPlayerId may not be null.");
		Objects.requireNonNull(speechMode, "speechMode may not be null.");
		Objects.requireNonNull(speech, "speech may not be null.");
	}
}
