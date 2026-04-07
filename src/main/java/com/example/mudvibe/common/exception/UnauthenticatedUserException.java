package com.example.mudvibe.common.exception;

public class UnauthenticatedUserException extends RuntimeException {

	private static final long serialVersionUID = -8225373772333246028L;

	public UnauthenticatedUserException(String message) {
		super(message);
	}
}
