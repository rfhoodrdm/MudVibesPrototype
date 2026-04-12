package com.example.mudvibe.gameworldengine.delegates.response;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.data.messages.outbound.SpeechDescriptionMessage;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpeechDescriptionResponseDelegate extends ResponseDelegate<SpeechDescriptionMessage> {

	public SpeechDescriptionMessage constructSpeechDescriptionResponse(UUID recipientPlayerId, String actor, String speechModeVerb, String speech) {
		
		SpeechDescriptionMessage message = new SpeechDescriptionMessage(recipientPlayerId, actor, speechModeVerb, speech);
		return message;
	}
}
