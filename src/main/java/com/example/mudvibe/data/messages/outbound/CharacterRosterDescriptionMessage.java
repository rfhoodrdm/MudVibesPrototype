package com.example.mudvibe.data.messages.outbound;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CharacterRosterDescriptionMessage(UUID recipientPlayerId, List<CharacterRosterEntry> rosterEntryList) implements AddressedOutboundMessage   {

	public CharacterRosterDescriptionMessage{
        Objects.requireNonNull(recipientPlayerId, "Recipient player id may not be null.");
        Objects.requireNonNull(rosterEntryList, "rosterEntryList may not be null.");
	}
	
	public static record CharacterRosterEntry(String characterName) {
		
		public CharacterRosterEntry {
			Objects.requireNonNull(characterName, "characterName may not be null.");
		}
	}
}
