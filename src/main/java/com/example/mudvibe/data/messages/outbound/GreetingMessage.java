package com.example.mudvibe.data.messages.outbound;

public record GreetingMessage(String currentCharacterName) implements SimpleOutboundMessage {

	public static final String GREETING_BANNER = """
			**************************************************
			*         Welcome to MudVibes Prototype!         *
			************************************************** 
			""";
	public static final String LOGIN_INSTRUCTIONS = """
			Create a new character with REGISTER <character name>
			or log in with an existing character with LOGIN <character name> to proceed. 
			""";
	
	public static final String CURRENT_CHARACTER_NOTICE = """
			You are currently logged in as %s. 
			""";
	
	@Override
	public String messageText() {
		
		String nextInstructionText = (null == currentCharacterName) 
				?  LOGIN_INSTRUCTIONS
			    :  CURRENT_CHARACTER_NOTICE.formatted(currentCharacterName);
		
		StringBuilder sb = new StringBuilder();
		sb.append(GREETING_BANNER).append("\n");
		sb.append(nextInstructionText);
		
		return sb.toString();
	}

}
