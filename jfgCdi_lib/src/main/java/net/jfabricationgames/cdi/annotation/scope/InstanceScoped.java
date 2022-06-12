package net.jfabricationgames.cdi.annotation.scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jfabricationgames.cdi.CdiContainer;

/**
 * A scope for classes, that are not shared, but for every instance that injects it there is a new instance created.
 * 
 * A class, annotated with this annotation has to define a public no-args constructor, so it can be created by the {@link CdiContainer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InstanceScoped {
	
}
