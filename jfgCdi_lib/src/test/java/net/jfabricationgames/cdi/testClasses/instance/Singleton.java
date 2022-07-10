package net.jfabricationgames.cdi.testClasses.instance;

import net.jfabricationgames.cdi.annotation.marker.Instance;
import net.jfabricationgames.cdi.annotation.scope.ApplicationScoped;

@ApplicationScoped
public class Singleton {
	
	private static Singleton instance;
	
	public final String name;
	
	/**
	 * The instance must be created using a parameterized method
	 */
	public static synchronized void createInstance(String name) {
		if (instance == null) {
			instance = new Singleton(name);
		}
	}
	
	/**
	 * The getInstance method is annotated to tell the CdiContainer that this method is to be used to get the instance of this class.
	 */
	@Instance
	public static synchronized Singleton getInstance() {
		return instance;
	}
	
	public Singleton(String name) {
		this.name = name;
	}
}
