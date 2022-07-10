package net.jfabricationgames.cdi.annotation.marker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that can be added to a static method that grants access to an instance of an object.
 * 
 * This annotation can be used if the construction of an instance cannot be done by a no-args constructor.
 * The annotated method must be public static and must not contain parameters. 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Instance {
	
}
