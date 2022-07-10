package net.jfabricationgames.cdi;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.marker.Instance;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;
import net.jfabricationgames.cdi.exception.CdiAmbiguousDependencyException;
import net.jfabricationgames.cdi.exception.CdiDependencyCycleException;
import net.jfabricationgames.cdi.exception.CdiException;
import net.jfabricationgames.cdi.exception.CdiUnresolvableDependencyException;
import net.jfabricationgames.cdi.util.ReflectionUtils;

/**
 * The main container class that is used to identify scoped classes and inject their instances into dependent instances.
 */
public class CdiContainer {
	
	private static final String SELF_PACKAGE = "net.jfabricationgames.cdi";
	
	private static CdiContainer instance;
	
	/**
	 * A lock variable to ensure, that bean instance that is created from the CdiContainer does not call the injectTo 
	 * method on itself to inject dependencies. Instead the container will inject dependencies to the instance afterwards.
	 * Otherwise this can lead to a problem in a HashMap. See https://stackoverflow.com/a/54825115/8178842.
	 */
	private static boolean injecting = false;
	
	/**
	 * Inject all dependencies into the parameter object.
	 */
	public static synchronized void injectTo(Object dependent) throws CdiException {
		if (instance == null) {
			throw new CdiException("The CdiContainer was not yet initialized. Use CdiContainer.initialize(String...) to initialize it.");
		}
		
		if (!injecting) {
			injecting = true;
			try {
				instance.injectManagedObjectsTo(dependent, new ArrayList<>());
			}
			finally {
				injecting = false;
			}
		}
	}
	
	/**
	 * Create a {@link CdiContainer} that searches the given packages and all sub-packages for classes that are annotated 
	 * with a scope annotation ({@link ApplicationScoped} or {@link InstanceScoped}).
	 */
	public static synchronized void create(String... packages) throws CdiException, IOException {
		if (instance != null) {
			throw new CdiException("An instance of the CdiContainer was already initialized.");
		}
		
		Set<String> packagesToLoad = new HashSet<>(Arrays.asList(packages));
		packagesToLoad.add(SELF_PACKAGE); // add the top level package of this library to be able to use scoped classes from this library
		
		instance = new CdiContainer();
		instance.registerScopedTypes(packagesToLoad);
	}
	
	/**
	 * Destroys the {@link CdiContainer}.
	 * 
	 * NOTE: only the reverence is set to null. Other references to the container may avoid it's removing by the GC. 
	 */
	public static synchronized void destroy() {
		instance = null;
	}
	
	protected static CdiContainer getInstance() throws CdiException {
		if (instance == null) {
			throw new CdiException("The CdiContainer was not yet initialized. Use CdiContainer.initialize(String...) to initialize it.");
		}
		return instance;
	}
	
	private Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses = new HashMap<>();
	private Map<Class<?>, Object> applicationScopedInstances = new HashMap<>();
	
	private CdiContainer() {}
	
	private void registerScopedTypes(Set<String> packagesToLoad) throws IOException {
		Reflections reflections = new Reflections(packagesToLoad);
		
		Set<Class<?>> applicationScopedClasses = reflections.getTypesAnnotatedWith(ApplicationScoped.class);
		Set<Class<?>> instanceScopedClasses = reflections.getTypesAnnotatedWith(InstanceScoped.class);
		
		annotatedClasses.put(ApplicationScoped.class, applicationScopedClasses);
		annotatedClasses.put(InstanceScoped.class, instanceScopedClasses);
	}
	
	protected Set<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
		return annotatedClasses.get(annotation);
	}
	
	private <T> void injectManagedObjectsTo(Object dependent, List<Class<?>> transitiveDependenciesList) throws CdiException {
		for (Field field : ReflectionUtils.getAllFieldsOf(dependent.getClass())) {
			if (isCdiAnnotationPresent(field) && !isFieldSet(field, dependent)) {
				Set<Class<?>> assignableClasses = findAssignableTypes(field);
				
				assertAssignableClassUnambiguous(field, dependent, assignableClasses);
				
				Class<?> assignableClass = assignableClasses.stream().findFirst().get(); // cannot be empty, because we asserted that in the previous method
				assertNoCyclicDependencies(assignableClass, transitiveDependenciesList);
				
				Object assignable = getObjectOf(assignableClass);
				injectAssignableObjectTo(dependent, field, assignable);
				
				List<Class<?>> amendedTransitiveDependenciesList = new ArrayList<>(transitiveDependenciesList);
				amendedTransitiveDependenciesList.add(assignableClass);
				injectManagedObjectsTo(assignable, amendedTransitiveDependenciesList);
			}
		}
	}
	
	private boolean isCdiAnnotationPresent(Field field) {
		return field.isAnnotationPresent(Inject.class);
	}
	
	private boolean isFieldSet(Field field, Object dependent) {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		
		try {
			return field.get(dependent) != null;
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			throw new CdiException("Could not check whether the field [" + field.getName() + //
					"] is already set on object [" + dependent + "]");
		}
		finally {
			field.setAccessible(accessible);
		}
	}
	
	private Set<Class<?>> findAssignableTypes(Field field) {
		return annotatedClasses.values().stream() //
				.flatMap(Set::stream) //
				.filter(clazz -> field.getType().isAssignableFrom(clazz)) //
				.collect(Collectors.toSet());
	}
	
	private void injectAssignableObjectTo(Object dependent, Field field, Object assignable) {
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		try {
			field.set(dependent, assignable);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			throw new CdiException("Could not inject assignable object [" + assignable + "] to dependent object [" + //
					dependent + "] in field [" + field.getName() + "]", e);
		}
		finally {
			field.setAccessible(accessible);
		}
	}
	
	@SuppressWarnings("unchecked")
	private synchronized <T> T getObjectOf(Class<T> assignableClass) {
		T assignable = null;
		if (assignableClass.isAnnotationPresent(ApplicationScoped.class)) {
			// find an instance that was already created, or create a new one if none was created yet
			assignable = (T) applicationScopedInstances.computeIfAbsent(assignableClass, clazz -> createInstanceOf(assignableClass));
		}
		else if (assignableClass.isAnnotationPresent(InstanceScoped.class)) {
			// create a new instance every time one is needed
			return createInstanceOf(assignableClass);
		}
		
		return assignable;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T createInstanceOf(Class<T> assignableClass) throws CdiException {
		for (Method method : assignableClass.getMethods()) {
			if (method.isAnnotationPresent(Instance.class)) {
				if (!method.getReturnType().equals(assignableClass)) {
					throw new CdiException("The @Instance method '" + method.getName() + "' of the class '" + assignableClass + //
							"' has a wrong return type '" + method.getReturnType() + "'. The expected type is '" + assignableClass + "'");
				}
				
				try {
					T instance = (T) method.invoke(null);
					if (instance == null) {
						throw new CdiException("The @Instance method '" + method.getName() + "' of the class '" + assignableClass + //
								"' returns null, but an instance of the class '" + assignableClass + "' must be returned.");
					}
					return instance;
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new CdiException("An instance of the class " + assignableClass.getName() + //
							" could not be created using the @Instance method '" + method.getName() + "'.", e);
				}
			}
		}
		
		try {
			return assignableClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new CdiException("An instance of the class " + assignableClass.getName() + //
					" could not be created using the no-args constructor.", e);
		}
	}
	
	private void assertAssignableClassUnambiguous(Field field, Object dependent, Set<Class<?>> assignableClasses) throws CdiException {
		if (assignableClasses.isEmpty()) {
			throw new CdiUnresolvableDependencyException("A type that can be injected to the type [" //
					+ dependent.getClass().getName() + "] into the field [" + field.getName() //
					+ "] of type [" + field.getType().getName() + "] is not known in the container.");
		}
		else if (assignableClasses.size() > 1) {
			throw new CdiAmbiguousDependencyException("Multiple types were found that could be injected to the type [" + //
					dependent.getClass().getName() + "] into the field [" + field.getName() + "] of type [" //
					+ field.getType().getName() + "]: " //
					+ assignableClasses.stream().map(Class::getName).collect(Collectors.joining(", ")));
		}
	}
	
	private void assertNoCyclicDependencies(Class<?> assignableClass, List<Class<?>> superDependencies) {
		if (assignableClass.isAnnotationPresent(InstanceScoped.class) // ApplicationScoped classes can be cyclic because only one instance exists
				&& superDependencies.contains(assignableClass)) {
			throw new CdiDependencyCycleException("The following cyclic dependency cannot be created for InstanceScoped clases: " //
					+ superDependencies.stream().map(Class::getName).collect(Collectors.joining(" -> ")) //
					+ " -> " + assignableClass.getName());
		}
	}
}
