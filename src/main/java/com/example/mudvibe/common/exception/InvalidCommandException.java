package com.example.mudvibe.common.exception;

public class InvalidCommandException extends Exception {

	private static final long serialVersionUID = -3980277350756932430L;

	public InvalidCommandException() {
		super();
	}

	public InvalidCommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidCommandException(String message) {
		super(message);
	}

	public InvalidCommandException(Throwable cause) {
		super(cause);
	}

}
