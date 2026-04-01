package com.example.mudvibe.common.exception;

import com.example.mudvibe.data.messages.inbound.IncomingCommand;

import lombok.Getter;
import lombok.Setter;

public class CommandProcessingException extends Exception {

	private static final long serialVersionUID = 1368054885227878864L;
	
	@Getter
	@Setter
	private IncomingCommand command;	//command that caused the error. Optionally set.

	public CommandProcessingException() {
		super();
	}

	public CommandProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandProcessingException(String message) {
		super(message);
	}

	public CommandProcessingException(Throwable cause) {
		super(cause);
	}

}
