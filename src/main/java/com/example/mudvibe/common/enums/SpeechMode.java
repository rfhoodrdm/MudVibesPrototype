package com.example.mudvibe.common.enums;

import lombok.Getter;

public enum SpeechMode {

	SAY("says", "say"),						//speech to room
	GROUP_SAY("says aside", "say aside"),	//speech private to own party
	YELL("yells", "yell"),					//speech to room and adjacent rooms
	WHISPER("whispers", "whisper");			//speech private to one person.
	
	SpeechMode(String speechVerbToOthers, String speechVerbToSelf) {
		this.speechVerbToOthers = speechVerbToOthers;
		this.speechVerbToSelf = speechVerbToSelf;
	}
	
	@Getter
	private String speechVerbToOthers;
	
	@Getter
	private String speechVerbToSelf;
}
