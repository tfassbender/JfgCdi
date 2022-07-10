package net.jfabricationgames.cdi.testClasses.instance;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;

public class DependentInstance {
	
	@Inject
	private Singleton singleton;
	@Inject
	private ConstructorMethod constructorMethod;
	
	public DependentInstance() {
		CdiContainer.injectTo(this);
	}
	
	public Singleton getSingleton() {
		return singleton;
	}
	
	public ConstructorMethod getConstructorMethod() {
		return constructorMethod;
	}
}