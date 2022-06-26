package net.jfabricationgames.cdi.event.testClasses;

import net.jfabricationgames.cdi.event.annotation.Observes;

public class SuperTypeCounterObserver {
	
	public int count = 0;
	
	/**
	 * This observer method will only be called if the event is of type {@link Object}. Not a sub-type of {@link Object}.
	 */
	protected void count(@Observes Object event) {
		count++;
	}
}
