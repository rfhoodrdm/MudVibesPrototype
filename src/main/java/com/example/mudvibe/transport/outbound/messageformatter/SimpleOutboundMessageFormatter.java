package com.example.mudvibe.transport.outbound.messageformatter;

import org.springframework.stereotype.Component;

import com.example.mudvibe.data.messages.outbound.AddressedEchoMessage;
import com.example.mudvibe.data.messages.outbound.AddressedSystemErrorMessage;
import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;
import com.example.mudvibe.data.messages.outbound.CommandProcessingErrorMessage;
import com.example.mudvibe.data.messages.outbound.EchoMessage;
import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.data.messages.outbound.OutboundMessage;
import com.example.mudvibe.data.messages.outbound.RoomDescriptionMessage;
import com.example.mudvibe.data.messages.outbound.SimpleSystemResponseMessage;
import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;
import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.AddressedEchoMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.AddressedSystemErrorMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.AddressedSystemNotificationMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.CommandProcessingErrorMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.EchoMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.GreetingMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.RoomDescriptionMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.SimpleSystemResponseMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.SystemBroadcastMessageFormatter;
import com.example.mudvibe.transport.outbound.messageformatter.formatters.SystemErrorMessageFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SimpleOutboundMessageFormatter implements OutboundMessageFormatter {
	
	public static final String DEFAULT_MESSAGE = "There is a distant rumbling, as if the gears of the world grind away..."; //for when we forget to have a formatter.

	private final RoomDescriptionMessageFormatter roomDescriptionMessageFormatter;
	
	@Override
	public String formatMessage(OutboundMessage message) {
		
		log.debug("Formatting outbound message: {}", message);
		
		return switch (message) {
		//standard, static-accessed formatters.
		case EchoMessage em 							-> EchoMessageFormatter.format(em);
		case SystemErrorMessage sem 					-> SystemErrorMessageFormatter.format(sem);
		case SystemBroadcastMessage sbm                 -> SystemBroadcastMessageFormatter.format(sbm);
		case AddressedSystemNotificationMessage asnm 	-> AddressedSystemNotificationMessageFormatter.format(asnm);
		case AddressedSystemErrorMessage asem 			-> AddressedSystemErrorMessageFormatter.format(asem);
		case AddressedEchoMessage aem 					-> AddressedEchoMessageFormatter.format(aem);
		case GreetingMessage gm                         -> GreetingMessageFormatter.format(gm);
		case SimpleSystemResponseMessage ssrm			-> SimpleSystemResponseMessageFormatter.format(ssrm);
		case CommandProcessingErrorMessage cpem			-> CommandProcessingErrorMessageFormatter.format(cpem);
		
		//instances, for those requiring components to work.
		case RoomDescriptionMessage rdm 				-> roomDescriptionMessageFormatter.format(rdm);
		
		case null, default 								-> DEFAULT_MESSAGE;
		};
	}

}
