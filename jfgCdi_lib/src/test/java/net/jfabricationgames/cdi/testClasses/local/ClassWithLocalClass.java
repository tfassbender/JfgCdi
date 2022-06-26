package net.jfabricationgames.cdi.testClasses.local;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

public class ClassWithLocalClass {
	
	/**
	 * NOTE: local class needs to be public static, to be injected into another class. 
	 */
	@ApplicationScoped
	public static class LocalClassInNormalClass {}
}
