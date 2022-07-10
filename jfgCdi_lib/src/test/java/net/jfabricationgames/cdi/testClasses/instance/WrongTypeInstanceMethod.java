package net.jfabricationgames.cdi.testClasses.instance;

import net.jfabricationgames.cdi.annotation.marker.Instance;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

@InstanceScoped
public class WrongTypeInstanceMethod {
	
	@Instance
	public Object getInstance() {
		return new WrongTypeInstanceMethod();
	}
}
