package net.jfabricationgames.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.annotation.marker.Instance;
import net.jfabricationgames.cdi.exception.CdiException;
import net.jfabricationgames.cdi.testClasses.instance.DependentInstance;
import net.jfabricationgames.cdi.testClasses.instance.DependentWrongTypeInstanceMethod;
import net.jfabricationgames.cdi.testClasses.instance.Singleton;

/**
 * Tests the creation or collecting of instances from classes that use the {@link Instance} annoatation. 
 */
public class InstanceTest {
	
	@BeforeAll
	public static void initializeCdiContainer() throws IOException {
		CdiContainer.create("net.jfabricationgames.cdi");
	}
	
	@AfterAll
	public static void destroyCdiContainer() {
		CdiContainer.destroy();
	}
	
	@Test
	public void testInstanceOfSingletonNotCreatedYet() {
		CdiException exception = assertThrows(CdiException.class, () -> new DependentInstance());
		assertTrue(exception.getMessage().contains("The @Instance method 'getInstance' of the class " + //
				"'class net.jfabricationgames.cdi.testClasses.instance.Singleton' returns null, but an instance of the class " + //
				"'class net.jfabricationgames.cdi.testClasses.instance.Singleton' must be returned."));
	}
	
	@Test
	public void testWrongTypeInstanceMethod() {
		CdiException exception = assertThrows(CdiException.class, () -> new DependentWrongTypeInstanceMethod());
		assertTrue(exception.getMessage().contains("The @Instance method 'getInstance' of the class " //
				+ "'class net.jfabricationgames.cdi.testClasses.instance.WrongTypeInstanceMethod' has a wrong return type " //
				+ "'class java.lang.Object'. The expected type is " //
				+ "'class net.jfabricationgames.cdi.testClasses.instance.WrongTypeInstanceMethod'"));
	}
	
	@Test
	public void testSuccessfulInjection() {
		Singleton.createInstance("Singleton");
		DependentInstance dependent = new DependentInstance();
		
		assertNotNull(dependent.getSingleton());
		assertNotNull(dependent.getConstructorMethod());
		assertEquals("Singleton", dependent.getSingleton().name);
		assertEquals("default", dependent.getConstructorMethod().name);
	}
}
