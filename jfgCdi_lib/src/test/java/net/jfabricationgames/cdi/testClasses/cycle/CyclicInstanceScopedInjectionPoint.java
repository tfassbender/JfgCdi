package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;

public class CyclicInstanceScopedInjectionPoint {
	
	@Inject
	private CyclicInstanceScopedDependency1 cyclicInstanceScopedDependency1;
	
	public CyclicInstanceScopedDependency1 getCyclicInstanceScopedDependency1() {
		return cyclicInstanceScopedDependency1;
	}
}
