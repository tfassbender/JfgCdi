package net.jfabricationgames.cdi.testClasses.ambiguous;

import net.jfabricationgames.cdi.annotation.Inject;

public class AmbiguousScopeTypeDependent {
	
	@Inject
	private AmbiguousScopeTypeInterface ambiguousScopeTypeInterface;
}
