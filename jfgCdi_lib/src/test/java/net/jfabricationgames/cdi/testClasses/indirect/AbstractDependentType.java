package net.jfabricationgames.cdi.testClasses.indirect;

import net.jfabricationgames.cdi.annotation.Inject;

public class AbstractDependentType {
	
	@Inject
	private AbstractScopedImplementation abstractScopedImplementation;
	
	public AbstractScopedImplementation getAbstractScopedImplementation() {
		return abstractScopedImplementation;
	}
}
