package net.jfabricationgames.cdi.testClasses.indirect;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class ApplicationScopedImplementation implements ApplicationScopedInterface {
	
	@Override
	public void foo() {}
}
