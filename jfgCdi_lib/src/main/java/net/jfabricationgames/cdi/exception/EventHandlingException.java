package net.jfabricationgames.cdi.exception;

public class EventHandlingException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public EventHandlingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public EventHandlingException(String message) {
		super(message);
	}
}
