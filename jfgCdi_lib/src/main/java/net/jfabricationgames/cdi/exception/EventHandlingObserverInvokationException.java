package net.jfabricationgames.cdi.exception;

public class EventHandlingObserverInvokationException extends EventHandlingException {
	
	private static final long serialVersionUID = 1L;
	
	public EventHandlingObserverInvokationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public EventHandlingObserverInvokationException(String message) {
		super(message);
	}
}
