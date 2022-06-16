package net.jfabricationgames.cdi.testClasses.indirect;

import net.jfabricationgames.cdi.annotation.Inject;

public class InterfaceDependentType {
	
	@Inject
	private ApplicationScopedInterface applicationScopedInterface;
	@Inject
	private InstanceScopedInterface instanceScopedInterface;
	
	public ApplicationScopedInterface getApplicationScopedInterface() {
		return applicationScopedInterface;
	}
	
	public InstanceScopedInterface getInstanceScopedInterface() {
		return instanceScopedInterface;
	}
}
