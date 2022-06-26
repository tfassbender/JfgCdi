package net.jfabricationgames.cdi.event.testClasses;

import net.jfabricationgames.cdi.event.annotation.Observes;

/**
 * A simple event listener that counts events.
 */
public class CountingEventListener {
	
	public int count = 0;
	
	/**
	 * Observes a SimpleEvent, that is fired from the EventHandler (if this object is registered as listener).
	 * 
	 * NOTE: The visibility of this method does not matter (it's private for testing reasons).
	 * NOTE: The observing method must have exactly one parameter that is annotated with <code>@Observes</code>.
	 *  
	 * @param event The event that is observed.
	 */
	@SuppressWarnings("unused")
	private void count(@Observes SimpleEvent event) {
		count++;
	}
}
