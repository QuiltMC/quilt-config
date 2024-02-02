package org.quiltmc.config.api.metadata;

/**
 * A naming scheme to indicate how a string should be formatted.
 * <p>
 * Common formats are exposed in {@link NamingSchemes}
 * </p>
 * @see org.quiltmc.config.api.annotations.SerializedNameConvention
 */
@FunctionalInterface
public interface NamingScheme {
	String coerce(String input);
}
