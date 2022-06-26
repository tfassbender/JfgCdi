package net.jfabricationgames.cdi.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.jfabricationgames.cdi.CdiContainer;
import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.event.testClasses.CountingEventListener;
import net.jfabricationgames.cdi.event.testClasses.EqualEventListener;
import net.jfabricationgames.cdi.event.testClasses.InheritedCountingEventListener;
import net.jfabricationgames.cdi.event.testClasses.MultipleEventListener;
import net.jfabricationgames.cdi.event.testClasses.SimpleEvent;
import net.jfabricationgames.cdi.event.testClasses.SuperTypeCounterObserver;
import net.jfabricationgames.cdi.event.testClasses.WrongObserverDeclarationWithMultipleParameters;
import net.jfabricationgames.cdi.event.testClasses.WrongObserverDeclarationWithNoObserverMethod;
import net.jfabricationgames.cdi.exception.EventHandlingObserverDeclarationException;

public class EventHandlerTest {
	
	@Inject
	private EventHandler eventHandler;
	
	@BeforeAll
	public static void initializeCdiContainer() throws IOException {
		CdiContainer.create("net.jfabricationgames.cdi.event"); //NOTE: the super package "net.jfabricationgames.cdi" would work too
	}
	
	@AfterAll
	public static void destroyCdiContainer() {
		CdiContainer.destroy();
	}
	
	@BeforeEach
	public void resolveInjections() {
		CdiContainer.injectTo(this);
	}
	
	@Test
	public void testAddAndRemoveUniqueListeners() {
		CountingEventListener listener1 = new CountingEventListener();
		CountingEventListener listener2 = new CountingEventListener();
		
		eventHandler.addListener(listener1);
		eventHandler.addListener(listener2);
		
		eventHandler.fireEvent(new SimpleEvent());
		
		eventHandler.removeListener(listener1); // removes only listener 1
		
		eventHandler.fireEvent(new SimpleEvent());
		
		assertEquals(1, listener1.count);
		assertEquals(2, listener2.count);
	}
	
	@Test
	public void testAddAndRemoveEqualListeners() {
		final String identifier = "equal";
		EqualEventListener listener1 = new EqualEventListener(identifier);
		EqualEventListener listener2 = new EqualEventListener(identifier);
		
		eventHandler.addListener(listener1);
		eventHandler.addListener(listener2);
		
		eventHandler.fireEvent(new SimpleEvent());
		
		eventHandler.removeListener(listener1); // removes only listener 1, because the check is done by instance
		
		eventHandler.fireEvent(new SimpleEvent());
		
		assertEquals(listener1, listener2);
		assertEquals(1, listener1.count);
		assertEquals(2, listener2.count);
	}
	
	@Test
	public void testInheritedObserverMethod() {
		InheritedCountingEventListener listener = new InheritedCountingEventListener();
		
		eventHandler.addListener(listener);
		eventHandler.fireEvent(new SimpleEvent());
		
		assertEquals(1, listener.count);
	}
	
	@Test
	public void testObservedType() {
		InheritedCountingEventListener listener = new InheritedCountingEventListener();
		
		eventHandler.addListener(listener);
		eventHandler.fireEvent(new Object());
		
		assertEquals(0, listener.count);
	}
	
	@Test
	public void testSuperTypeCounterObserver() {
		SuperTypeCounterObserver listener = new SuperTypeCounterObserver();
		
		eventHandler.addListener(listener);
		SimpleEvent event = new SimpleEvent();
		eventHandler.fireEvent(event);
		
		// the observer was not called, because the event was only a sub-type of the observed type java.lang.Object
		assertEquals(0, listener.count);
		
		eventHandler.fireEvent(new Object());
		
		assertEquals(1, listener.count);
	}
	
	@Test
	public void testMultipleEventListener() {
		MultipleEventListener listener = new MultipleEventListener();
		
		eventHandler.addListener(listener);
		SimpleEvent event = new SimpleEvent();
		eventHandler.fireEvent(event);
		
		// both observer methods, that observe SimpleEvent objects were invoked
		assertEquals(1, listener.count);
		assertEquals(event, listener.lastSimpleEventObject);
		
		// the third observer was not called, because the event was only a sub-type of the observed type java.lang.Object
		assertNull(listener.lastEventObject);
		
		Object object = new Object();
		eventHandler.fireEvent(object);
		
		assertEquals(1, listener.count);
		assertEquals(event, listener.lastSimpleEventObject);
		assertEquals(object, listener.lastEventObject);
	}
	
	@Test
	public void testWrongObserverDeclaration_multipleParameters() {
		WrongObserverDeclarationWithMultipleParameters listener = new WrongObserverDeclarationWithMultipleParameters();
		
		EventHandlingObserverDeclarationException thrown = assertThrows(EventHandlingObserverDeclarationException.class, () -> eventHandler.addListener(listener));
		assertTrue(thrown.getMessage().contains("observerMethod"));
		assertTrue(thrown.getMessage().contains(WrongObserverDeclarationWithMultipleParameters.class.getName()));
	}
	
	@Test
	public void testWrongObserverDeclaration_noObserverMethod() {
		WrongObserverDeclarationWithNoObserverMethod listener = new WrongObserverDeclarationWithNoObserverMethod();
		
		EventHandlingObserverDeclarationException thrown = assertThrows(EventHandlingObserverDeclarationException.class, () -> eventHandler.addListener(listener));
		assertTrue(thrown.getMessage().contains(WrongObserverDeclarationWithNoObserverMethod.class.getName()));
	}
}
