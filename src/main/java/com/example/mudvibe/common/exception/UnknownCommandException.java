package com.example.mudvibe.common.exception;

public class UnknownCommandException extends Exception {

	private static final long serialVersionUID = 4337652496253583172L;

	public UnknownCommandException() {
		super();
	}

	public UnknownCommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownCommandException(String message) {
		super(message);
	}

	public UnknownCommandException(Throwable cause) {
		super(cause);
	}

}
