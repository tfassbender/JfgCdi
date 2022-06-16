package net.jfabricationgames.cdi.testClasses.indirect;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class AbstractScopedImplementation extends AbstractScopedClass {
	
	@Inject
	private InstanceScopedInterface instanceScopedInterface;
	
	public InstanceScopedInterface getInstanceScopedInterface() {
		return instanceScopedInterface;
	}
}
