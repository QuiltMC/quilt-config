/*
 * Copyright 2022-2023 QuiltMC
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

package org.quiltmc.config.api.metadata;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.values.*;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A typed key to be used for setting and getting metadata of a {@link ValueTreeNode} or {@link Config}.
 *
 * <p>See also {@link TrackedValue.Builder#metadata}, {@link Config.Builder#metadata}, and {@link Config.SectionBuilder#metadata}
 */
public final class MetadataType<T, B extends MetadataType.Builder<T>> {
	private final Supplier<Optional<T>> defaultValueSupplier;
	private final Function<Type, Optional<T>> trackedValueDefaultValueSupplier;
	private final Supplier<B> builderSupplier;

	private MetadataType(Supplier<Optional<T>> defaultValueSupplier, Function<Type, Optional<T>> trackedValueDefaultValueSupplier, Supplier<B> builderSupplier) {
		this.defaultValueSupplier = defaultValueSupplier;
		this.trackedValueDefaultValueSupplier = trackedValueDefaultValueSupplier;
		this.builderSupplier = builderSupplier;
	}

	/**
	 * @return an optional containing the default value if this type has one, or an empty value if not
	 */
	public Optional<T> getDefaultValue(MetadataContainer container) {
		if (container instanceof TrackedValue) {
			Object defaultValue = ((TrackedValue<?>) container).getDefaultValue();

			if (defaultValue instanceof ConfigSerializableObject) {
				defaultValue = ((ConfigSerializableObject<?>) defaultValue).getRepresentation();
			}

			return this.trackedValueDefaultValueSupplier.apply(defaultValue.getClass());
		} else {
			return this.defaultValueSupplier.get();
		}
	}

	public B newBuilder() {
		return this.builderSupplier.get();
	}

	/**
	 * Creates a new {@link MetadataType} with the given parameters.
	 *
	 * The {@link Type} passed to trackedValueDefaultFunction will always be one of the following:
	 * <ul>
	 *     <li>A basic type (int, long, float, double, boolean, String, or enum)</li>
	 *     <li>{@link ValueList} or {@link ValueMap}</li>
	 * </ul>
	 *
	 * @param defaultSupplier should provide the default value for the metadata for non-values
	 * @param trackedValueDefaultFunction can infer the default metadata based on the type of a {@link TrackedValue}
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<Optional<T>> defaultSupplier, Function<Type, Optional<T>> trackedValueDefaultFunction, Supplier<B> builderSupplier) {
		return new MetadataType<>(defaultSupplier, trackedValueDefaultFunction, builderSupplier);
	}

	/**
	 * @param defaultSupplier should provide the default value for the metadata for non-values
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<Optional<T>> defaultSupplier, Supplier<B> builderSupplier) {
		return new MetadataType<>(defaultSupplier, t -> defaultSupplier.get(), builderSupplier);
	}

	/**
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<B> builderSupplier) {
		return create(Optional::empty, builderSupplier);
	}

	public interface Builder<T> {
		T build();
	}
}
