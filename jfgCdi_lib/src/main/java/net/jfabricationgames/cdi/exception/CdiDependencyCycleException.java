package net.jfabricationgames.cdi.exception;

public class CdiDependencyCycleException extends CdiException {
	
	private static final long serialVersionUID = 1L;
	
	public CdiDependencyCycleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CdiDependencyCycleException(String message) {
		super(message);
	}
}
