package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class CyclicInstanceScopedDependency2 {
	
	@Inject
	private CyclicInstanceScopedDependency1 cyclicInstanceScopedDependency1;
	
	public CyclicInstanceScopedDependency1 getCyclicInstanceScopedDependency1() {
		return cyclicInstanceScopedDependency1;
	}
}
