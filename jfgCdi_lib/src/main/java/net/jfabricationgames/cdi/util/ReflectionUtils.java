package net.jfabricationgames.cdi.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {
	
	/**
	 * Get all fields of a class (including private and inherited fields).
	 */
	public static List<Field> getAllFieldsOf(Class<?> type) {
		return ReflectionUtils.getAllFieldsOf(type, new ArrayList<>());
	}
	
	private static List<Field> getAllFieldsOf(Class<?> type, List<Field> fields) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		
		if (type.getSuperclass() != null) {
			getAllFieldsOf(type.getSuperclass(), fields);
		}
		
		return fields;
	}
	
	/**
	 * Get all methods of a class (including private and inherited methods).
	 */
	public static List<Method> getAllMethodsOf(Class<?> type) {
		return ReflectionUtils.getAllMethodsOf(type, new ArrayList<>());
	}
	
	private static List<Method> getAllMethodsOf(Class<?> type, List<Method> methods) {
		methods.addAll(Arrays.asList(type.getDeclaredMethods()));
		
		if (type.getSuperclass() != null) {
			getAllMethodsOf(type.getSuperclass(), methods);
		}
		
		return methods;
	}
}
