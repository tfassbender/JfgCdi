package net.jfabricationgames.cdi.testClasses.recursive_injection;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

/**
 * An application scoped bean that will be injected to another application scoped bean.
 */
@ApplicationScoped
public class LowerBean {
	
}
