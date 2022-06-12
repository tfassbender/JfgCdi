package net.jfabricationgames.cdi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jfabricationgames.cdi.CdiContainer;

/**
 * An annotation that can be added to fields, to inject a dependency from the {@link CdiContainer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
	
}
