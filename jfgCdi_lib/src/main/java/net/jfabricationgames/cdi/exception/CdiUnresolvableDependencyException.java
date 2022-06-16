package net.jfabricationgames.cdi.exception;

public class CdiUnresolvableDependencyException extends CdiException {
	
	private static final long serialVersionUID = 1L;
	
	public CdiUnresolvableDependencyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CdiUnresolvableDependencyException(String message) {
		super(message);
	}
}
