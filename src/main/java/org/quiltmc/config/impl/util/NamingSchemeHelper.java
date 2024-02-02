package org.quiltmc.config.impl.util;


import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.metadata.NamingScheme;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class NamingSchemeHelper {
	private final ClassLoader classLoader;
	private final Map<String, NamingScheme> customSchemeCache;

	public NamingSchemeHelper(ClassLoader classLoader) {
		this.classLoader = classLoader;
		this.customSchemeCache = new HashMap<>();
	}

	public NamingScheme getNamingScheme(SerializedNameConvention annotation, BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
		if (annotation.custom().isEmpty()) {
			return annotation.value();
		} else {
			return createCustomNamingScheme(annotation.custom(), exceptionFactory);
		}
	}

	public NamingScheme getNamingScheme(NamingScheme defaultScheme, AnnotatedElement element,
										BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
		SerializedNameConvention annotation = element.getAnnotation(SerializedNameConvention.class);
		if (annotation != null) {
			defaultScheme = getNamingScheme(annotation, exceptionFactory);
		}

		return defaultScheme;
	}

	private NamingScheme createCustomNamingScheme(String className, BiFunction<String, Throwable, RuntimeException> exceptionFactory) {
		return customSchemeCache.computeIfAbsent(className, customSchemeName -> {
			NamingScheme customScheme;
			try {
				Class<?> customSchemeClass = Class.forName(customSchemeName, true, classLoader);
				customScheme = (NamingScheme) customSchemeClass.newInstance();
			} catch (ClassNotFoundException e) {
				throw exceptionFactory.apply("Couldn't find custom naming scheme class '" + customSchemeName + "'", e);
			} catch (InstantiationException | IllegalAccessException e) {
				throw exceptionFactory.apply("Couldn't create instance of custom name scheme class '" + customSchemeName + "'", e);
			} catch (ClassCastException e) {
				throw exceptionFactory.apply("Class '" + customSchemeName + "' does not implement '" + NamingScheme.class.getName() + "'", e);
			}
			return customScheme;
		});
	}
}
