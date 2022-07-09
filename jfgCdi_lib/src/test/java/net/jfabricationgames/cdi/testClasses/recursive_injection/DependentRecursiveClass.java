package net.jfabricationgames.cdi.testClasses.recursive_injection;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;

public class DependentRecursiveClass {
	
	@Inject
	private UpperBean upperBean;
	
	public DependentRecursiveClass() {
		CdiContainer.injectTo(this);
	}
	
	public UpperBean getUpperBean() {
		return upperBean;
	}
}
