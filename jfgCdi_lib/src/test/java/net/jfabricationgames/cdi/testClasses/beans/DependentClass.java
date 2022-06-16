package net.jfabricationgames.cdi.testClasses.beans;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;

public class DependentClass {
	
	@Inject
	private ApplicationScopedBean applicationScopedBean;
	@Inject
	private InstanceScopedBean instanceScopedBean;
	@Inject
	private DependentInstanceScopedBean dependentInstanceScopedBean;
	
	public DependentClass() {
		CdiContainer.injectTo(this);
	}
	
	public ApplicationScopedBean getApplicationScopedBean() {
		return applicationScopedBean;
	}
	
	public InstanceScopedBean getInstanceScopedBean() {
		return instanceScopedBean;
	}
	
	public DependentInstanceScopedBean getDependentInstanceScopedBean() {
		return dependentInstanceScopedBean;
	}
}
