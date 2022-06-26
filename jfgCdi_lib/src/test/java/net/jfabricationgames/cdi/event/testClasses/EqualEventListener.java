package net.jfabricationgames.cdi.event.testClasses;

import java.util.Objects;

import net.jfabricationgames.cdi.event.annotation.Observes;

/**
 * A simple event listener that implements an equals method.
 */
public class EqualEventListener {
	
	public int count = 0;
	
	private String identifier;
	
	public EqualEventListener(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Observes a SimpleEvent, that is fired from the EventHandler (if this object is registered as listener).
	 * 
	 * @param event The event that is observed.
	 */
	public void count(@Observes SimpleEvent event) {
		count++;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EqualEventListener other = (EqualEventListener) obj;
		return Objects.equals(identifier, other.identifier);
	}
}
