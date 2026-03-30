package com.example.mudvibe.common.exception;

public class CharacterLoginException extends Exception {

	private static final long serialVersionUID = -8708582275111191043L;

	public CharacterLoginException() {
		super();
	}

	public CharacterLoginException(String message, Throwable cause) {
		super(message, cause);
	}

	public CharacterLoginException(String message) {
		super(message);
	}

	public CharacterLoginException(Throwable cause) {
		super(cause);
	}
}
