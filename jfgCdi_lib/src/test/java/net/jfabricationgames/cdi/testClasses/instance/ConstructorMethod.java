package net.jfabricationgames.cdi.testClasses.instance;

import net.jfabricationgames.cdi.annotation.marker.Instance;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class ConstructorMethod {
	
	public final String name;
	
	/**
	 * A constructor with arguments, that can not be used to create the instance scoped object from the container.
	 */
	public ConstructorMethod(String name) {
		this.name = name;
	}
	
	@Instance
	public static ConstructorMethod createDefaultInstance() {
		return new ConstructorMethod("default");
	}
}
