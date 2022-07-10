package net.jfabricationgames.cdi.testClasses.instance;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;

public class DependentWrongTypeInstanceMethod {
	
	@Inject
	private WrongTypeInstanceMethod wrongTypeInstanceMethod;
	
	public DependentWrongTypeInstanceMethod() {
		CdiContainer.injectTo(this);
	}
}
