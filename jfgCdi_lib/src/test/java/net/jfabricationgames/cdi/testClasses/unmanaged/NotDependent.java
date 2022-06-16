package net.jfabricationgames.cdi.testClasses.unmanaged;

public class NotDependent {
	
	// no injections in this class
	
	private Object foo;
	
	public Object getFoo() {
		return foo;
	}
	
	public void setFoo(Object foo) {
		this.foo = foo;
	}
}
