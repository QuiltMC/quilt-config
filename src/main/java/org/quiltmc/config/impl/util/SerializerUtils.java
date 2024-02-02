/*
 * Copyright 2023-2024 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.config.impl.util;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.CompoundConfigValue;
import org.quiltmc.config.api.annotations.SerializedNameConvention;
import org.quiltmc.config.api.values.ValueKey;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.values.ValueKeyImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SerializerUtils {
	public static Optional<String> getDefaultValueString(Object defaultValue) {
		try {
			// we don't want to show the default toString method from object as it'll look something org.quiltmc.config.Vec3i@20527 and be nonsense to the user
			if (defaultValue.getClass().getMethod("toString").getDeclaringClass() != Object.class
					// similar story here: it'll be tough for the average user to parse a full map/list
					&& !(defaultValue instanceof CompoundConfigValue<?>)) {
				return Optional.of(defaultValue.toString());
			}
		} catch (NoSuchMethodException ignored) {
			// all classes contain the method toString, so this will never be thrown
		}

		return Optional.empty();
	}

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
	 * Gets the value's key, taking {@link SerializedName} into account. Should always be used when serializing and deserializing a config.
	 */
	public static ValueKey getSerializedKey(Config config, ValueTreeNode value) {
		List<String> serializedKey = new ArrayList<>();
		ValueKey key = value.key();

		List<String> rawKey = new ArrayList<>();
		for (int i = 0; i < value.key().length(); i++) {
			rawKey.add(key.getKeyComponent(i));

			ValueTreeNode currentNode = config.getNode(rawKey);
			serializedKey.add(getSerializedName(currentNode));
		}

		return new ValueKeyImpl(serializedKey.toArray(new String[0]));
	}

	public static String getSerializedName(ValueTreeNode value) {
		if (value.hasMetadata(SerializedName.TYPE)) {
			return value.metadata(SerializedName.TYPE).getName();
		} else if (value.hasMetadata(SerializedNameConvention.TYPE)) {
			return value.metadata(SerializedNameConvention.TYPE).coerce(value.key().getLastComponent());
		} else {
			return value.key().getLastComponent();
		}
	}
}
