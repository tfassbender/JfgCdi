package net.jfabricationgames.cdi.testClasses.ambiguous;

import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class AmbiguousScopeTypeImplementation2 implements AmbiguousScopeTypeInterface {
	
	@Override
	public void foo() {}
}
