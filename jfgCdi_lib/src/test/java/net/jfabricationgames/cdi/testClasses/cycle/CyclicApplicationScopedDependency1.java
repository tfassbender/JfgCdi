package net.jfabricationgames.cdi.testClasses.cycle;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class CyclicApplicationScopedDependency1 {
	
	@Inject
	private CyclicApplicationScopedDependency2 dependency;
	
	public CyclicApplicationScopedDependency2 getDependency() {
		return dependency;
	}
}
