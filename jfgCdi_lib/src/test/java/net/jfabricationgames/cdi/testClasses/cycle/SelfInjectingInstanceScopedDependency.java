package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class SelfInjectingInstanceScopedDependency {
	
	@Inject
	private SelfInjectingInstanceScopedDependency selfInjectingInstanceScopedDependency;
}
