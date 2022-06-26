package net.jfabricationgames.cdi.exception;

public class EventHandlingObserverDeclarationException extends EventHandlingException {
	
	private static final long serialVersionUID = 1L;
	
	public EventHandlingObserverDeclarationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public EventHandlingObserverDeclarationException(String message) {
		super(message);
	}
}
