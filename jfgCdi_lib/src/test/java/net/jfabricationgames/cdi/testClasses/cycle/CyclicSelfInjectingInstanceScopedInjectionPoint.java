package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;

public class CyclicSelfInjectingInstanceScopedInjectionPoint {
	
	@Inject
	private SelfInjectingInstanceScopedDependency dependency;
}
