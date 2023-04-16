package org.quiltmc.config.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides the implementation packages access to package-private methods. <strong>DO NOT USE THIS CLASS</strong>
 */

@ApiStatus.Internal
public final class InternalsHelper {
	private InternalsHelper() {
	}

	public static <T extends ReflectiveConfig> void setWrappedConfig(T wrapped, Config config) {
		wrapped.setWrappedConfig(config);
	}
}
