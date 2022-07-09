package net.jfabricationgames.cdi.testClasses.recursive_injection;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

/**
 * A bean that has a dependency to another application scoped bean that is injected into this bean in the constructor.
 */
@ApplicationScoped
public class UpperBean {
	
	@Inject
	private LowerBean lowerBean;
	
	public UpperBean() {
		/*
		 * Injecting inside a bean class is not needed, because the bean class will be created from the container.
		 * This behaviour can lead to a ConcurrentModificationException in a HashMap, when the object is created from the container.
		 * 
		 * This test class is used to ensure that this problem will not occur.
		 * 
		 * NOTE: The exception would only occur in a java version >= 9, because of a bug in java 8
		 * See https://stackoverflow.com/a/54825115/8178842.
		 */
		CdiContainer.injectTo(this);
	}
	
	public LowerBean getLowerBean() {
		return lowerBean;
	}
}
