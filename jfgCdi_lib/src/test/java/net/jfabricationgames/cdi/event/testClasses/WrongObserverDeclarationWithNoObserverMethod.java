package net.jfabricationgames.cdi.event.testClasses;

import net.jfabricationgames.cdi.event.annotation.Observes;
import net.jfabricationgames.cdi.exception.EventHandlingObserverDeclarationException;

public class WrongObserverDeclarationWithNoObserverMethod {
	
	/**
	 * This observer method is not valid, because the parameter is missing the {@link Observes} annotation.
	 * Adding an instance of this class to the EventHandler will result in an {@link EventHandlingObserverDeclarationException}.
	 */
	public void observe(SimpleEvent event) {}
}
