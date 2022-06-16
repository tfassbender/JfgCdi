package net.jfabricationgames.cdi.testClasses.beans;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class DependentInstanceScopedBean {
	
	@Inject
	private ApplicationScopedBean applicationScopedBean;
	
	// no initialisation in constructor, to test transitive initialisation
	
	public ApplicationScopedBean getApplicationScopedBean() {
		return applicationScopedBean;
	}
}
