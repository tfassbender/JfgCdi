package net.jfabricationgames.cdi.testClasses.indirect;

import net.jfabricationgames.cdi.annotation.Inject;

public class AbstractScopedClass {
	
	@Inject
	private ApplicationScopedInterface applicationScopedInterface;
	
	public ApplicationScopedInterface getApplicationScopedInterface() {
		return applicationScopedInterface;
	}
}
