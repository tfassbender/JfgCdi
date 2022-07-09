package net.jfabricationgames.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;
import net.jfabricationgames.cdi.exception.CdiAmbiguousDependencyException;
import net.jfabricationgames.cdi.exception.CdiDependencyCycleException;
import net.jfabricationgames.cdi.exception.CdiUnresolvableDependencyException;
import net.jfabricationgames.cdi.testClasses.ambiguous.AmbiguousScopeTypeDependent;
import net.jfabricationgames.cdi.testClasses.ambiguous.AmbiguousScopeTypeImplementation1;
import net.jfabricationgames.cdi.testClasses.ambiguous.AmbiguousScopeTypeImplementation2;
import net.jfabricationgames.cdi.testClasses.beans.ApplicationScopedBean;
import net.jfabricationgames.cdi.testClasses.beans.DependentClass;
import net.jfabricationgames.cdi.testClasses.beans.DependentSubClass;
import net.jfabricationgames.cdi.testClasses.beans.InstanceScopedBean;
import net.jfabricationgames.cdi.testClasses.cycle.CyclicDependentInjectionPoint;
import net.jfabricationgames.cdi.testClasses.cycle.CyclicInstanceScopedDependency1;
import net.jfabricationgames.cdi.testClasses.cycle.CyclicInstanceScopedDependency2;
import net.jfabricationgames.cdi.testClasses.cycle.CyclicInstanceScopedInjectionPoint;
import net.jfabricationgames.cdi.testClasses.cycle.CyclicSelfInjectingInstanceScopedInjectionPoint;
import net.jfabricationgames.cdi.testClasses.cycle.SelfInjectingInstanceScopedDependency;
import net.jfabricationgames.cdi.testClasses.indirect.AbstractDependentType;
import net.jfabricationgames.cdi.testClasses.indirect.ApplicationScopedImplementation;
import net.jfabricationgames.cdi.testClasses.indirect.InstanceScopedImplementation;
import net.jfabricationgames.cdi.testClasses.indirect.InterfaceDependentType;
import net.jfabricationgames.cdi.testClasses.local.ClassWithLocalClass.LocalClassInNormalClass;
import net.jfabricationgames.cdi.testClasses.local.LocalClassDependentType;
import net.jfabricationgames.cdi.testClasses.local.ScopedClassWithLocalClass;
import net.jfabricationgames.cdi.testClasses.local.ScopedClassWithLocalClass.LocalClassInScopedClass;
import net.jfabricationgames.cdi.testClasses.recursive_injection.DependentRecursiveClass;
import net.jfabricationgames.cdi.testClasses.unmanaged.NotDependent;
import net.jfabricationgames.cdi.testClasses.unmanaged.UnmanagedType;
import net.jfabricationgames.cdi.testClasses.unmanaged.UnmanagedTypeDependency;

public class CdiContainerTest {
	
	@BeforeAll
	public static void initializeCdiContainer() throws IOException {
		CdiContainer.create("net.jfabricationgames.cdi");
	}
	
	@AfterAll
	public static void destroyCdiContainer() {
		CdiContainer.destroy();
	}
	
	@Test
	public void testInitializeCdiContainer() {
		// the container was initialised before the test - check whether the initialisation was successful
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(ApplicationScopedBean.class));
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(InstanceScoped.class).contains(InstanceScopedBean.class));
		
		assertFalse(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(InstanceScopedBean.class));
		assertFalse(CdiContainer.getInstance().getAnnotatedClasses(InstanceScoped.class).contains(ApplicationScopedBean.class));
		
		assertFalse(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(DependentClass.class));
		assertFalse(CdiContainer.getInstance().getAnnotatedClasses(InstanceScoped.class).contains(DependentClass.class));
	}
	
	@Test
	public void testInjectApplicationScopedBean() {
		// test that two instances share the same application scoped bean
		DependentClass dependent1 = new DependentClass();
		DependentClass dependent2 = new DependentClass();
		
		assertNotNull(dependent1.getApplicationScopedBean());
		assertNotNull(dependent2.getApplicationScopedBean());
		assertTrue(dependent1.getApplicationScopedBean() == dependent2.getApplicationScopedBean());
	}
	
	@Test
	public void testInjectInstanceScopedBeans() {
		// test that two instances don't share the same instance scoped bean
		DependentClass dependent1 = new DependentClass();
		DependentClass dependent2 = new DependentClass();
		
		assertNotNull(dependent1.getInstanceScopedBean());
		assertNotNull(dependent2.getInstanceScopedBean());
		assertTrue(dependent1.getInstanceScopedBean() != dependent2.getInstanceScopedBean());
	}
	
	@Test
	public void testInjectTransientDependency() {
		DependentClass dependent = new DependentClass();
		
		assertNotNull(dependent.getApplicationScopedBean());
		assertNotNull(dependent.getDependentInstanceScopedBean());
		assertNotNull(dependent.getDependentInstanceScopedBean().getApplicationScopedBean());
		assertTrue(dependent.getApplicationScopedBean() == dependent.getDependentInstanceScopedBean().getApplicationScopedBean());
	}
	
	@Test
	public void testInjectIntoNotDependentInstance() {
		NotDependent notDependent = new NotDependent();
		CdiContainer.injectTo(notDependent);
		
		assertNull(notDependent.getFoo());
	}
	
	@Test
	public void testNoSecondInjection() {
		DependentClass dependent = new DependentClass();
		InstanceScopedBean instanceScopedBean = dependent.getInstanceScopedBean();
		
		// inject twice
		CdiContainer.injectTo(dependent);
		
		assertTrue(dependent.getInstanceScopedBean() == instanceScopedBean);
	}
	
	@Test
	public void testInjectIntoSubclass() {
		DependentSubClass dependent = new DependentSubClass();
		
		assertNotNull(dependent.getApplicationScopedBean());
		assertNotNull(dependent.getDependentInstanceScopedBean());
		assertNotNull(dependent.getDependentInstanceScopedBean().getApplicationScopedBean());
	}
	
	@Test
	public void testInjectSelfInjectingApplicationScopedDependency() {
		CyclicDependentInjectionPoint dependent = new CyclicDependentInjectionPoint();
		CdiContainer.injectTo(dependent);
		
		assertNotNull(dependent.getSelfInjectingApplicationScopedDependency());
		assertTrue(dependent.getSelfInjectingApplicationScopedDependency() == dependent.getSelfInjectingApplicationScopedDependency().getDependency());
	}
	
	@Test
	public void testInjectCyclicApplicationScopedDependencies() {
		CyclicDependentInjectionPoint dependent1 = new CyclicDependentInjectionPoint();
		CyclicDependentInjectionPoint dependent2 = new CyclicDependentInjectionPoint();
		
		CdiContainer.injectTo(dependent1);
		CdiContainer.injectTo(dependent2);
		
		assertNotNull(dependent1.getCyclicApplicationScopedDependency1());
		assertNotNull(dependent2.getCyclicApplicationScopedDependency2());
		assertTrue(dependent1.getCyclicApplicationScopedDependency1() == dependent2.getCyclicApplicationScopedDependency2().getDependency());
		assertTrue(dependent2.getCyclicApplicationScopedDependency2() == dependent1.getCyclicApplicationScopedDependency1().getDependency());
	}
	
	@Test
	public void testInjectCyclicSelfInjectingInstanceScopedDependency() {
		CyclicSelfInjectingInstanceScopedInjectionPoint dependent = new CyclicSelfInjectingInstanceScopedInjectionPoint();
		CdiDependencyCycleException exception = assertThrows(CdiDependencyCycleException.class, () -> CdiContainer.injectTo(dependent));
		assertTrue(exception.getMessage().contains(SelfInjectingInstanceScopedDependency.class.getName() + " -> " + SelfInjectingInstanceScopedDependency.class.getName()));
	}
	
	@Test
	public void testInjectCyclicInstanceScopedDependency() {
		CyclicInstanceScopedInjectionPoint dependent = new CyclicInstanceScopedInjectionPoint();
		CdiDependencyCycleException exception = assertThrows(CdiDependencyCycleException.class, () -> CdiContainer.injectTo(dependent));
		assertTrue(exception.getMessage().contains(CyclicInstanceScopedDependency1.class.getName() + " -> " //
				+ CyclicInstanceScopedDependency2.class.getName() + " -> " + CyclicInstanceScopedDependency1.class.getName()));
	}
	
	@Test
	public void testUnhandledTypeDependency() {
		UnmanagedTypeDependency dependent = new UnmanagedTypeDependency();
		CdiUnresolvableDependencyException exception = assertThrows(CdiUnresolvableDependencyException.class, () -> CdiContainer.injectTo(dependent));
		assertTrue(exception.getMessage().contains(UnmanagedTypeDependency.class.getName()));
		assertTrue(exception.getMessage().contains("unmanagedType"));
		assertTrue(exception.getMessage().contains(UnmanagedType.class.getName()));
	}
	
	@Test
	public void testInjectIntoInterfaceFields() {
		InterfaceDependentType dependent = new InterfaceDependentType();
		InterfaceDependentType dependent2 = new InterfaceDependentType();
		CdiContainer.injectTo(dependent);
		CdiContainer.injectTo(dependent2);
		
		assertNotNull(dependent.getApplicationScopedInterface());
		assertNotNull(dependent.getInstanceScopedInterface());
		assertNotNull(dependent2.getApplicationScopedInterface());
		assertNotNull(dependent2.getInstanceScopedInterface());
		
		assertEquals(ApplicationScopedImplementation.class, dependent.getApplicationScopedInterface().getClass());
		assertEquals(InstanceScopedImplementation.class, dependent.getInstanceScopedInterface().getClass());
		assertTrue(dependent.getApplicationScopedInterface() == dependent2.getApplicationScopedInterface());
		assertFalse(dependent.getInstanceScopedInterface() == dependent2.getInstanceScopedInterface());
	}
	
	@Test
	public void testInjectIntoAbstractType() {
		AbstractDependentType dependent = new AbstractDependentType();
		CdiContainer.injectTo(dependent);
		
		assertNotNull(dependent.getAbstractScopedImplementation());
		assertNotNull(dependent.getAbstractScopedImplementation().getApplicationScopedInterface());
		assertNotNull(dependent.getAbstractScopedImplementation().getInstanceScopedInterface());
		assertEquals(ApplicationScopedImplementation.class, dependent.getAbstractScopedImplementation().getApplicationScopedInterface().getClass());
		assertEquals(InstanceScopedImplementation.class, dependent.getAbstractScopedImplementation().getInstanceScopedInterface().getClass());
	}
	
	@Test
	public void testAmbiguousType() {
		AmbiguousScopeTypeDependent dependent = new AmbiguousScopeTypeDependent();
		CdiAmbiguousDependencyException exception = assertThrows(CdiAmbiguousDependencyException.class, () -> CdiContainer.injectTo(dependent));
		assertTrue(exception.getMessage().contains(AmbiguousScopeTypeDependent.class.getName()));
		assertTrue(exception.getMessage().contains("ambiguousScopeTypeInterface"));
		assertTrue(exception.getMessage().contains(AmbiguousScopeTypeImplementation1.class.getName()));
		assertTrue(exception.getMessage().contains(AmbiguousScopeTypeImplementation2.class.getName()));
	}
	
	@Test
	public void testLocalClassOfScopedClass() {
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(LocalClassInNormalClass.class));
	}
	
	@Test
	public void testLocalClassOfNotScopedClass() {
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(ScopedClassWithLocalClass.class));
		assertTrue(CdiContainer.getInstance().getAnnotatedClasses(ApplicationScoped.class).contains(LocalClassInScopedClass.class));
	}
	
	@Test
	public void testInjectLocalClasses() {
		LocalClassDependentType dependent = new LocalClassDependentType();
		CdiContainer.injectTo(dependent);
		
		assertNotNull(dependent.getLocalClassInNormalClass());
		assertNotNull(dependent.getLocalClassInScopedClass());
	}
	
	@Test
	public void testInjectRecursiveDepencenciesDoesNotThrowAConcurrentModificationException() {
		DependentRecursiveClass dependent = new DependentRecursiveClass();
		assertNotNull(dependent.getUpperBean());
		assertNotNull(dependent.getUpperBean().getLowerBean());
	}
}
