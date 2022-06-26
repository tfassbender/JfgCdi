package net.jfabricationgames.cdi.event.testClasses;

import net.jfabricationgames.cdi.event.annotation.Observes;
import net.jfabricationgames.cdi.exception.EventHandlingObserverDeclarationException;

public class WrongObserverDeclarationWithMultipleParameters {
	
	/**
	 * Tries to observes a SimpleEvent, but contains a second parameter object, so the observer is not valid.
	 * Adding an instance of this class to the EventHandler will result in an {@link EventHandlingObserverDeclarationException}.
	 */
	public void observerMethod(@Observes SimpleEvent event, int invalidParameter) {}
}
