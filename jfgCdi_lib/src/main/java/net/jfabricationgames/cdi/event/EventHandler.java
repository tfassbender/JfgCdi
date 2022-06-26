package net.jfabricationgames.cdi.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.event.annotation.Observes;
import net.jfabricationgames.cdi.exception.EventHandlingObserverDeclarationException;
import net.jfabricationgames.cdi.exception.EventHandlingObserverInvokationException;
import net.jfabricationgames.cdi.util.ReflectionUtils;

@ApplicationScoped
public class EventHandler {
	
	private List<Object> eventListeners = new ArrayList<>();
	/**
	 * Maps the listener class to a map of observed parameter types, to the set of methods, that take this type as parameter.
	 */
	private Map<Class<?>, Map<Class<?>, Set<Method>>> typeObserverMethodMappings = new HashMap<>();
	
	/**
	 * Fire an event, so that every registered event listeners methods, that observe this type of event, are invoked.
	 * 
	 * NOTE: Only the methods that observe the exact type are invoked. Not the ones observing a super type of the parameter event type.
	 */
	public void fireEvent(Object event) throws EventHandlingObserverInvokationException {
		for (Object listener : eventListeners) {
			Class<?> listenerType = listener.getClass();
			Class<?> eventType = event.getClass();
			
			// get the methods of the listener type that observe the event type
			Map<Class<?>, Set<Method>> observerMethodsByEventType = typeObserverMethodMappings.get(listenerType);
			Set<Method> observerMethods = observerMethodsByEventType.get(eventType);
			
			if (observerMethods != null) {
				for (Method observerMethod : observerMethods) {
					boolean accessible = observerMethod.isAccessible();
					observerMethod.setAccessible(true);
					
					try {
						observerMethod.invoke(listener, event);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new EventHandlingObserverInvokationException("The observer method " + observerMethod.getName() + //
								" of the type " + listenerType + " could not be invoked.", e);
					}
					finally {
						observerMethod.setAccessible(accessible);
					}
				}
			}
		}
	}
	
	/**
	 * Add a listener object to the EventHandler. 
	 * 
	 * NOTE: This object has to declare at least one observer method.
	 */
	public void addListener(Object listener) throws EventHandlingObserverDeclarationException {
		if (!typeObserverMethodMappings.containsKey(listener.getClass())) {
			registerObserverMethodMappingsForType(listener.getClass());
		}
		
		eventListeners.add(listener);
	}
	
	private void registerObserverMethodMappingsForType(Class<?> clazz) {
		boolean containsObserverMethod = false;
		
		for (Method method : ReflectionUtils.getAllMethodsOf(clazz)) {
			for (Parameter parameter : method.getParameters()) {
				
				if (parameter.isAnnotationPresent(Observes.class)) {
					containsObserverMethod = true;
					if (method.getParameterCount() > 1) {
						throw new EventHandlingObserverDeclarationException("The observer type " + clazz.getName() + //
								" declares an observer method " + method.getName() + " that contains multiple parameters. " + //
								"Observer methods must declare only one parameter.");
					}
					
					Set<Method> typeObserverMethods = typeObserverMethodMappings.computeIfAbsent(clazz, c -> new HashMap<>()) //
							.computeIfAbsent(parameter.getType(), t -> new HashSet<>());
					typeObserverMethods.add(method);
				}
			}
		}
		
		if (!containsObserverMethod) {
			throw new EventHandlingObserverDeclarationException("The observer type " + clazz.getName() + //
					" does not contain any observer methods. Maybe the @Observes annotation is missing?");
		}
	}
	
	/**
	 * Remove the given listener. The check is done by instance, so equal objects are not removed.
	 */
	public void removeListener(Object listener) {
		eventListeners.remove(listener);
	}
}
