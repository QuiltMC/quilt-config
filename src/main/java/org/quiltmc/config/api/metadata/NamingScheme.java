package org.quiltmc.config.api.metadata;

@FunctionalInterface
public interface NamingScheme {
	String coerce(String input);
}
