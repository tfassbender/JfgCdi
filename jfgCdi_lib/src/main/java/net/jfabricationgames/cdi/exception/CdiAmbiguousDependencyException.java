package net.jfabricationgames.cdi.exception;

public class CdiAmbiguousDependencyException extends CdiException {
	
	private static final long serialVersionUID = 1L;
	
	public CdiAmbiguousDependencyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CdiAmbiguousDependencyException(String message) {
		super(message);
	}
}
