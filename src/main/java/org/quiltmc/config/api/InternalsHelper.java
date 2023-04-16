package org.quiltmc.config.api;

import org.jetbrains.annotations.ApiStatus;


/**
 * Provides access to internal values of other API classes. Do not use!
 */
@ApiStatus.Internal
public final class InternalsHelper {
	private InternalsHelper() {
	}

	public static <T extends ReflectiveConfig> void setWrappedConfig(T wrapped, Config config) {
		wrapped.setWrappedConfig(config);
	}
}
