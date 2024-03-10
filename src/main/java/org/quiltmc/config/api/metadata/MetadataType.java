/*
 * Copyright 2022-2024 QuiltMC
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
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;

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
	private final boolean inherited;

	private MetadataType(Supplier<Optional<T>> defaultValueSupplier, Function<Type, Optional<T>> trackedValueDefaultValueSupplier, Supplier<B> builderSupplier, boolean inherited) {
		this.defaultValueSupplier = defaultValueSupplier;
		this.trackedValueDefaultValueSupplier = trackedValueDefaultValueSupplier;
		this.builderSupplier = builderSupplier;
		this.inherited = inherited;
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

	public boolean isInherited() {
		return this.inherited;
	}

	/**
	 * Creates a new {@link MetadataType} with the given parameters.
	 *
	 * <p>The {@link Type} passed to trackedValueDefaultFunction will always be one of the following:
	 * <ul>
	 *     <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *     <li>{@link ValueList} or {@link ValueMap}</li>
	 * </ul>
	 *
	 * @param defaultSupplier should provide the default value for the metadata for non-values
	 * @param trackedValueDefaultFunction can infer the default metadata based on the type of a {@link TrackedValue}
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<Optional<T>> defaultSupplier, Function<Type, Optional<T>> trackedValueDefaultFunction, Supplier<B> builderSupplier) {
		return new MetadataType<>(defaultSupplier, trackedValueDefaultFunction, builderSupplier, false);
	}

	/**
	 * @param defaultSupplier should provide the default value for the metadata for non-values
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<Optional<T>> defaultSupplier, Supplier<B> builderSupplier) {
		return create(defaultSupplier, builderSupplier, false);
	}

	/**
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<B> builderSupplier) {
		return create(builderSupplier, false);
	}

	/**
	 * Creates a new {@link MetadataType} with the given parameters.
	 *
	 * <p>The {@link Type} passed to trackedValueDefaultFunction will always be one of the following:
	 * <ul>
	 *     <li>A basic type ({@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link Boolean}, {@link String}, or enum)</li>
	 *     <li>{@link ValueList} or {@link ValueMap}</li>
	 * </ul>
	 *
	 * @param defaultSupplier should provide the default value for the metadata for non-values
	 * @param trackedValueDefaultFunction can infer the default metadata based on the type of a {@link TrackedValue}
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @param inherited true if the metadata should be inherited to containers when using the builder or reflective config
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<Optional<T>> defaultSupplier, Function<Type, Optional<T>> trackedValueDefaultFunction, Supplier<B> builderSupplier, boolean inherited) {
		return new MetadataType<>(defaultSupplier, trackedValueDefaultFunction, builderSupplier, inherited);
	}

	/**
	 * @param defaultSupplier should provide the default value for the metadata for non-values
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @param inherited true if the metadata should be inherited when using the builder or reflective config
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<Optional<T>> defaultSupplier, Supplier<B> builderSupplier, boolean inherited) {
		return new MetadataType<>(defaultSupplier, t -> defaultSupplier.get(), builderSupplier, inherited);
	}

	/**
	 * @param builderSupplier supplies a new instance of the {@link MetadataType}'s builder class
	 * @param inherited true if the metadata should be inherited when using the builder or reflective config
	 * @return a new {@link MetadataType}
	 */
	public static <T, B extends Builder<T>> MetadataType<T, B> create(Supplier<B> builderSupplier, boolean inherited) {
		return create(Optional::empty, builderSupplier, inherited);
	}

	public interface Builder<T> {
		T build();
	}
}
