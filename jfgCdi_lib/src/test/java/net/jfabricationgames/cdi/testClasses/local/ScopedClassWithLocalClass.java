package net.jfabricationgames.cdi.testClasses.local;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class ScopedClassWithLocalClass {
	
	/**
	 * NOTE: local class needs to be public static, to be injected into another class. 
	 */
	@ApplicationScoped
	public static class LocalClassInScopedClass {}
}
