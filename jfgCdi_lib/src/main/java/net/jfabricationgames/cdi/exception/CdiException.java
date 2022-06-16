package net.jfabricationgames.cdi.exception;

public class CdiException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CdiException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CdiException(String message) {
		super(message);
	}
}
