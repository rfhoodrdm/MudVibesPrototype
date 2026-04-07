package com.example.mudvibe.common.exception;

/**
 * Occurs if there is a problem registering a new character.
 */
public class PlayerCharacterRegistrationException extends Exception {

	private static final long serialVersionUID = 799747168890994171L;

	public PlayerCharacterRegistrationException() {
		super();
	}

	public PlayerCharacterRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerCharacterRegistrationException(String message) {
		super(message);
	}

	public PlayerCharacterRegistrationException(Throwable cause) {
		super(cause);
	}
}
