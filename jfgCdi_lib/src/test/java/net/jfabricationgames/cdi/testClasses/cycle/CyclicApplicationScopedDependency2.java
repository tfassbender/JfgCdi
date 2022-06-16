package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class CyclicApplicationScopedDependency2 {
	
	@Inject
	private CyclicApplicationScopedDependency1 dependency;
	
	public CyclicApplicationScopedDependency1 getDependency() {
		return dependency;
	}
}
