package net.jfabricationgames.cdi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;
import net.jfabricationgames.cdi.testClasses.ApplicationScopedBean;
import net.jfabricationgames.cdi.testClasses.InstanceScopedBean;

public class CdiContainerTest {
	
	@BeforeAll
	public static void initializeCdiContainer() throws IOException {
		CdiContainer.initialize("net.jfabricationgames.cdi");
	}
	
	@Test
	public void testInitializeCdiContainer() {
		// the container was initialised before the test - check whether the initialisation was successful
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(ApplicationScopedBean.class));
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(InstanceScoped.class).contains(InstanceScopedBean.class));
	}
	
	@Test
	public void testInjectApplicationScopedBean() {
		//TODO test that two instances share the same application scoped bean
		fail("not yet implemented");
	}
	
	@Test
	public void testInjectInstanceScopedBeans() {
		//TODO test that two instances don't share the same instance scoped bean
		fail("not yet implemented");
	}
	
	@Test
	public void testInjectTransientDependency() {
		//TODO test that an injected instance is injected with the needed dependencies
		fail("not yet implemented");
	}
	
	@Test
	public void testInjectCyclicDependency() {
		//TODO test that a cyclic dependency is detected and leads to an exception (no stack overflow error)
		fail("not yet implemented");
	}
}
