package com.example.mudvibe.util.outboundmessage;

import org.springframework.stereotype.Component;

import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;
import com.example.mudvibe.data.messages.outbound.CommandProcessingErrorMessage;
import com.example.mudvibe.data.messages.outbound.EchoMessage;
import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.data.messages.outbound.OutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.SimpleSystemResponseMessage;
import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;
import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;
import com.example.mudvibe.util.outboundmessage.formatters.AddressedSystemNotificationMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.CommandProcessingErrorMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.EchoMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.GreetingMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.RoomDescriptionMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.SimpleSystemResponseMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.SystemBroadcastMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.SystemErrorMessageFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboundMessageFormatterUtil {
	
	public static final String DEFAULT_MESSAGE = "There is a distant rumbling, as if the gears of the world grind away..."; //for when we forget to have a formatter.

	private final RoomDescriptionMessageFormatter roomDescriptionMessageFormatter;
	
	public String formatOutboundMessage(OutboundMessage message) {
		
		log.debug("Formatting outbound message: {}", message);
		
		return switch (message) {
		//standard, static-accessed formatters.
		case EchoMessage em 							-> EchoMessageFormatter.format(em);
		case SystemErrorMessage sem 					-> SystemErrorMessageFormatter.format(sem);
		case SystemBroadcastMessage sbm                 -> SystemBroadcastMessageFormatter.format(sbm);
		case AddressedSystemNotificationMessage asnm 	-> AddressedSystemNotificationMessageFormatter.format(asnm);
		case GreetingMessage gm                         -> GreetingMessageFormatter.format(gm);
		case SimpleSystemResponseMessage ssrm			-> SimpleSystemResponseMessageFormatter.format(ssrm);
		case CommandProcessingErrorMessage cpem			-> CommandProcessingErrorMessageFormatter.format(cpem);
		
		//instances, for those requiring components to work.
		case RoomDescriptionMessage rdm 				-> roomDescriptionMessageFormatter.format(rdm);
		
		case null, default 								-> DEFAULT_MESSAGE;
		};
	}
}
