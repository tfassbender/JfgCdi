package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class CyclicInstanceScopedDependency1 {
	
	@Inject
	private CyclicInstanceScopedDependency2 cyclicInstanceScopedDependency2;
	
	public CyclicInstanceScopedDependency2 getCyclicInstanceScopedDependency2() {
		return cyclicInstanceScopedDependency2;
	}
}
