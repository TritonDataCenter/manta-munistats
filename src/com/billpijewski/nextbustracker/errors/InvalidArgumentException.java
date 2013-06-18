package com.billpijewski.nextbustracker.errors;

public final class InvalidArgumentException extends Exception {

	private static final long serialVersionUID = 1780412662491261401L;
	
	public InvalidArgumentException(String message) {
		super(message);
	}
}