package org.quiltmc.config.impl.util;

import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.TrackedValue;

import java.util.Optional;

public class SerializerUtils {
	public static Optional<String> createEnumOptionsComment(Object defaultValue) {
		if (defaultValue.getClass().isEnum()) {
			StringBuilder options = new StringBuilder("options: ");
			Object[] enumConstants = defaultValue.getClass().getEnumConstants();

			for (int i = 0, enumConstantsLength = enumConstants.length; i < enumConstantsLength; i++) {
				Object o = enumConstants[i];

				options.append(o);

				if (i < enumConstantsLength - 1) {
					options.append(", ");
				}
			}

			return Optional.of(options.toString());
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Gets the value's name, taking {@link SerializedName} into account. Should always be used when serializing and deserializing a config.
	 */
	public static String getName(TrackedValue<?> value) {
		String name = value.key().toString();
		if (value.hasMetadata(SerializedName.TYPE)) {
			name = value.metadata(SerializedName.TYPE).getName();
		}

		return name;
	}
}
