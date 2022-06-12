package net.jfabricationgames.cdi;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;
import net.jfabricationgames.cdi.annotation.scope.InstanceScoped;

/**
 * The main container class that is used to identify scoped classes and inject their instances into dependent instances.
 */
public class CdiContainer {
	private static final Logger log = LoggerFactory.getLogger(CdiContainer.class);
	
	private static CdiContainer instance;
	
	public static synchronized void initialize(String... packages) throws IOException {
		if (instance != null) {
			throw new IllegalStateException("An instance of the CdiContainer was already initialized.");
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
	
	protected static CdiContainer getInstance() {
		if (instance == null) {
			throw new IllegalStateException("The CdiContainer was not yet initialized. Use CdiContainer.initialize(String...) to initialize it.");
		}
		return instance;
	}
	
	public static void injectTo(Object dependent) {
		if (instance == null) {
			throw new IllegalStateException("The CdiContainer was not yet initialized. Use CdiContainer.initialize(String...) to initialize it.");
		}
		//TODO
	}
	
	private Map<Class<? extends Annotation>, Set<Class<?>>> annotatedClasses = new HashMap<>();
	
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
			log.warn("Class couldn't be loaded, because of a ClassNotFoundException: {}", className, e);
		}
		return null;
	}
	
	protected Set<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
		return annotatedClasses.get(annotation);
	}
}
