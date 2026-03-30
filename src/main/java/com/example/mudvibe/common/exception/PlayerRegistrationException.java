package com.example.mudvibe.common.exception;

/**
 * Occurs if there is a problem registering a new character.
 */
public class PlayerRegistrationException extends Exception {

	private static final long serialVersionUID = 799747168890994171L;

	public PlayerRegistrationException() {
		super();
	}

	public PlayerRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerRegistrationException(String message) {
		super(message);
	}

	public PlayerRegistrationException(Throwable cause) {
		super(cause);
	}
}
