package net.jfabricationgames.cdi.testClasses.indirect;

import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class InstanceScopedImplementation implements InstanceScopedInterface {
	
	@Override
	public void foo() {}
}
