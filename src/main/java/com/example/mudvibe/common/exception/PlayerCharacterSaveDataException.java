package com.example.mudvibe.common.exception;

/**
 * Occurs if there is a problem with saving a character.
 */
public class PlayerCharacterSaveDataException extends Exception {

	private static final long serialVersionUID = -1342580483549949806L;

	public PlayerCharacterSaveDataException() {
		super();
	}

	public PlayerCharacterSaveDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerCharacterSaveDataException(String message) {
		super(message);
	}

	public PlayerCharacterSaveDataException(Throwable cause) {
		super(cause);
	}

}
