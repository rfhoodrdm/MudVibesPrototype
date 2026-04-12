package com.example.mudvibe.gameworldengine.delegates.response;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.mudvibe.area.service.AreaManager;
import com.example.mudvibe.data.messages.outbound.AddressedOutboundMessage;
import com.example.mudvibe.playercharacter.service.PlayerCharacterManager;

public abstract class ResponseDelegate<T extends AddressedOutboundMessage> {
	
	@Autowired protected AreaManager areaManager;
	@Autowired protected PlayerCharacterManager playerCharacterManager;

}
