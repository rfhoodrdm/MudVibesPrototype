package com.example.mudvibe.common.exception;

/**
 * Occurs if there is a problem with saving a character.
 */
public class PlayerSaveDataException extends Exception {

	private static final long serialVersionUID = -1342580483549949806L;

	public PlayerSaveDataException() {
		super();
	}

	public PlayerSaveDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerSaveDataException(String message) {
		super(message);
	}

	public PlayerSaveDataException(Throwable cause) {
		super(cause);
	}

}
