package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class SelfInjectingApplicationScopedDependency {
	
	@Inject
	private SelfInjectingApplicationScopedDependency dependency;
	
	public SelfInjectingApplicationScopedDependency getDependency() {
		return dependency;
	}
}
