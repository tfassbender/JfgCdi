package net.jfabricationgames.cdi;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import net.jfabricationgames.cdi.annotation.Inject;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;
import net.jfabricationgames.cdi.exception.CdiAmbiguousDependencyException;
import net.jfabricationgames.cdi.exception.CdiDependencyCycleException;
import net.jfabricationgames.cdi.exception.CdiException;
import net.jfabricationgames.cdi.exception.CdiUnresolvableDependencyException;

/**
 * The main container class that is used to identify scoped classes and inject their instances into dependent instances.
 */
public class CdiContainer {
	
	private static CdiContainer instance;
	
	public static void injectTo(Object dependent) throws CdiException {
		if (instance == null) {
			throw new CdiException("The CdiContainer was not yet initialized. Use CdiContainer.initialize(String...) to initialize it.");
		}
		
		instance.injectManagedObjectsTo(dependent, new ArrayList<>());
	}
	
	public static synchronized void create(String... packages) throws CdiException, IOException {
		if (instance != null) {
			throw new CdiException("An instance of the CdiContainer was already initialized.");
		}
		
		Set<String> classNames = loadClasses(packages);
		
		instance = new CdiContainer();
		instance.registerScopedTypes(classNames);
	}
	
	private static Set<String> loadClasses(String... packages) throws IOException {
		Set<String> subPackages = new HashSet<>();
		
		for (String packageName : packages) {
			Enumeration<URL> roots = CdiContainer.class.getClassLoader().getResources(packageName.replaceAll("[.]", "/"));
			while (roots.hasMoreElements()) {
				File rootFile = new File(roots.nextElement().getPath());
				loadClassNames(subPackages, packageName, packageName, rootFile);
			}
		}
		
		return subPackages;
	}
	
	private static void loadClassNames(Set<String> classNames, String packageName, String searchedPackageName, File rootFile) throws IOException {
		final String separator = System.getProperty("file.separator");
		if (rootFile.listFiles() != null) {
			for (File file : rootFile.listFiles()) {
				String subFileName = packageName + "." + file.getName();
				if (file.isDirectory()) {
					loadClassNames(classNames, subFileName, packageName, file);
				}
				else {
					if (file.getPath().replace(separator, ".").contains(packageName)) {
						classNames.add(subFileName.substring(0, subFileName.lastIndexOf('.')));
					}
				}
			}
		}
	}
	
	protected static CdiContainer getInstance() throws CdiException {
		if (instance == null) {
			throw new CdiException("The CdiContainer was not yet initialized. Use CdiContainer.initialize(String...) to initialize it.");
		}
		return instance;
	}
	
	private static List<Field> getAllFieldsOf(Class<?> type) {
		return getAllFieldsOf(type, new ArrayList<>());
	}
	
	private static List<Field> getAllFieldsOf(Class<?> type, List<Field> fields) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		
		if (type.getSuperclass() != null) {
			getAllFieldsOf(type.getSuperclass(), fields);
		}
		
		return fields;
	}
	
	private Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses = new HashMap<>();
	private Map<Class<?>, Object> applicationScopedInstances = new HashMap<>();
	
	private CdiContainer() {}
	
	private void registerScopedTypes(Set<String> classNames) throws IOException {
		Set<Class<?>> scopeAnnotatedClasses = classNames.stream() //
				.map(this::getClass) //
				.filter(Objects::nonNull) //
				.filter(this::isScopeAnnotated) //
				.collect(Collectors.toSet());
		
		Set<Class<?>> applicationScopedClasses = scopeAnnotatedClasses.stream() //
				.filter(clazz -> clazz.isAnnotationPresent(ApplicationScoped.class)) //
				.collect(Collectors.toSet());
		Set<Class<?>> instanceScopedClasses = scopeAnnotatedClasses.stream() //
				.filter(clazz -> clazz.isAnnotationPresent(InstanceScoped.class)) //
				.collect(Collectors.toSet());
		
		annotatedClasses.put(ApplicationScoped.class, applicationScopedClasses);
		annotatedClasses.put(InstanceScoped.class, instanceScopedClasses);
	}
	
	private boolean isScopeAnnotated(Class<?> clazz) {
		return clazz.isAnnotationPresent(ApplicationScoped.class) || clazz.isAnnotationPresent(InstanceScoped.class);
	}
	
	private Class<?> getClass(String className) {
		try {
			return Class.forName(className, true, getClass().getClassLoader());
		}
		catch (ClassNotFoundException e) {
			throw new CdiException("Class couldn't be loaded: " + className, e);
		}
	}
	
	protected Set<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
		return annotatedClasses.get(annotation);
	}
	
	private <T> void injectManagedObjectsTo(Object dependent, List<Class<?>> transitiveDependenciesList) throws CdiException {
		for (Field field : getAllFieldsOf(dependent.getClass())) {
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
	private <T> T getObjectOf(Class<T> assignableClass) {
		T assignable = null;
		if (assignableClass.isAnnotationPresent(ApplicationScoped.class)) {
			// find an instance that was already created, or create a new one if none was created yet
			assignable = (T) applicationScopedInstances.computeIfAbsent(assignableClass, clazz -> {
				try {
					return assignableClass.newInstance();
				}
				catch (InstantiationException | IllegalAccessException e) {
					throw new CdiException("An instance of the class " + assignableClass.getName() + " could not be created.", e);
				}
			});
		}
		else if (assignableClass.isAnnotationPresent(InstanceScoped.class)) {
			// create a new instance every time one is needed
			try {
				assignable = assignableClass.newInstance();
			}
			catch (InstantiationException | IllegalAccessException e) {
				throw new CdiException("An instance of the class " + assignableClass.getName() + " could not be created.", e);
			}
		}
		
		return assignable;
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
