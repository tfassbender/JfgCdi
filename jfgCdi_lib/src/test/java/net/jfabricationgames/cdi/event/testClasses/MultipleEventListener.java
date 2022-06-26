package net.jfabricationgames.cdi.event.testClasses;

import net.jfabricationgames.cdi.event.annotation.Observes;

/**
 * An event listener, that observes multiple events
 */
public class MultipleEventListener {
	
	public int count = 0;
	public Object lastEventObject;
	public SimpleEvent lastSimpleEventObject;
	
	@SuppressWarnings("unused")
	private void count(@Observes SimpleEvent event) {
		count++;
	}
	
	/**
	 * This observer method will only be called if the event is of type {@link Object}. Not a sub-type of {@link Object}.
	 */
	protected void receiveEventType(@Observes Object event) {
		this.lastEventObject = event;
	}
	
	public void handleSimpleEventTwice(@Observes SimpleEvent event) {
		this.lastSimpleEventObject = event;
	}
}
