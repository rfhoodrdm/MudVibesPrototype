package com.example.mudvibe.util.outboundmessage;

import org.springframework.stereotype.Component;

import com.example.mudvibe.data.messages.outbound.AddressedSystemNotificationMessage;
import com.example.mudvibe.data.messages.outbound.EchoMessage;
import com.example.mudvibe.data.messages.outbound.GreetingMessage;
import com.example.mudvibe.data.messages.outbound.OutboundMessage;
import com.example.mudvibe.data.messages.outbound.SimpleSystemResponseMessage;
import com.example.mudvibe.data.messages.outbound.SystemBroadcastMessage;
import com.example.mudvibe.data.messages.outbound.SystemErrorMessage;
import com.example.mudvibe.util.outboundmessage.formatters.AddressedSystemNotificationMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.EchoMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.GreetingMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.SimpleSystemResponseMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.SystemBroadcastMessageFormatter;
import com.example.mudvibe.util.outboundmessage.formatters.SystemErrorMessageFormatter;

@Component
public class OutboundMessageFormatterUtil {

	public String formatOutboundMessage(OutboundMessage message) {
		
		return switch (message) {
		case EchoMessage em 							-> EchoMessageFormatter.format(em);
		case SystemErrorMessage sem 					-> SystemErrorMessageFormatter.format(sem);
		case SystemBroadcastMessage sbm                 -> SystemBroadcastMessageFormatter.format(sbm);
		case AddressedSystemNotificationMessage asnm 	-> AddressedSystemNotificationMessageFormatter.format(asnm);
		case GreetingMessage gm                         -> GreetingMessageFormatter.format(gm);
		case SimpleSystemResponseMessage ssrm			-> SimpleSystemResponseMessageFormatter.format(ssrm);
		};
	}
	
}
