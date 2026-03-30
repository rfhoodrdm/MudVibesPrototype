package com.example.mudvibe.common.exception;

public class CharacterLogoutException extends Exception {

	private static final long serialVersionUID = -1877609887100914003L;

	public CharacterLogoutException() {
		super();
	}

	public CharacterLogoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public CharacterLogoutException(String message) {
		super(message);
	}

	public CharacterLogoutException(Throwable cause) {
		super(cause);
	}

}
