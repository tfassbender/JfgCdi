package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;

public class CyclicDependentInjectionPoint {
	
	@Inject
	private SelfInjectingApplicationScopedDependency selfInjectingApplicationScopedDependency;
	@Inject
	private CyclicApplicationScopedDependency1 cyclicApplicationScopedDependency1;
	@Inject
	private CyclicApplicationScopedDependency2 cyclicApplicationScopedDependency2;
	
	public SelfInjectingApplicationScopedDependency getSelfInjectingApplicationScopedDependency() {
		return selfInjectingApplicationScopedDependency;
	}
	
	public CyclicApplicationScopedDependency1 getCyclicApplicationScopedDependency1() {
		return cyclicApplicationScopedDependency1;
	}
	
	public CyclicApplicationScopedDependency2 getCyclicApplicationScopedDependency2() {
		return cyclicApplicationScopedDependency2;
	}
}
