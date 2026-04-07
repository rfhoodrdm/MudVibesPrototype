package com.example.mudvibe.common.exception;

public class CharacterMoveException extends Exception {

	private static final long serialVersionUID = -4730081518508176116L;

	public CharacterMoveException() {
		super();
	}

	public CharacterMoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public CharacterMoveException(String message) {
		super(message);
	}

	public CharacterMoveException(Throwable cause) {
		super(cause);
	}
}
