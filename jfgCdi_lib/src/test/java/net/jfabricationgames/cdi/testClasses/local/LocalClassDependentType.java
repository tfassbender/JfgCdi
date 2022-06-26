package net.jfabricationgames.cdi.testClasses.local;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.testClasses.local.ClassWithLocalClass.LocalClassInNormalClass;
import net.jfabricationgames.cdi.testClasses.local.ScopedClassWithLocalClass.LocalClassInScopedClass;

public class LocalClassDependentType {
	
	@Inject
	private LocalClassInNormalClass localClassInNormalClass;
	@Inject
	private LocalClassInScopedClass localClassInScopedClass;
	
	public LocalClassInNormalClass getLocalClassInNormalClass() {
		return localClassInNormalClass;
	}
	
	public LocalClassInScopedClass getLocalClassInScopedClass() {
		return localClassInScopedClass;
	}
}
