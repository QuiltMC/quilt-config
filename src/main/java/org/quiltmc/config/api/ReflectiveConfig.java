/*
 * Copyright 2023 QuiltMC
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

package org.quiltmc.config.api;

import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.builders.ValueMapBuilderImpl;
import org.quiltmc.config.impl.tree.TrackedValueImpl;
import org.quiltmc.config.impl.util.ConfigUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class ReflectiveConfig implements Config {
	private Config wrapped;

	@Override
	public final String family() {
		return this.wrapped.family();
	}

	@Override
	public final String id() {
		return this.wrapped.id();
	}

	@Override
	public final Path savePath() {
		return this.wrapped.savePath();
	}

	@Override
	public final void registerCallback(UpdateCallback callback) {
		this.wrapped.registerCallback(callback);
	}

	@Override
	public final <M> M metadata(MetadataType<M, ?> type) {
		return this.wrapped.metadata(type);
	}

	@Override
	public final <M> boolean hasMetadata(MetadataType<M, ?> type) {
		return this.wrapped.hasMetadata(type);
	}

	@Override
	public final void save() {
		this.wrapped.save();
	}

	@Override
	public final Iterable<TrackedValue<?>> values() {
		return this.wrapped.values();
	}

	@Override
	public final TrackedValue<?> getValue(Iterable<String> key) {
		return this.wrapped.getValue(key);
	}

	@Override
	public final Iterable<ValueTreeNode> nodes() {
		return this.wrapped.nodes();
	}

	final void setWrappedConfig(Config config) {
		this.wrapped = config;
	}


	public final <T> TrackedValue<T> value(T defaultValue) {
		ConfigUtils.assertValueType(defaultValue);

		return new TrackedValueImpl<>(null, defaultValue, new LinkedHashMap<>(0), new ArrayList<>(0), new ArrayList<>(0));
	}

	@SafeVarargs
	public final <T> TrackedValue<ValueList<T>> list(T defaultValue, T... values) {
		return value(ValueList.create(defaultValue, values));
	}

	public final <T> ValueMap.TrackedBuilder<T> map(T defaultValue) {
		return new ValueMapBuilderImpl.TrackedValueMapBuilderImpl<>(defaultValue, this::value);
	}

	public static class Section {
		public final <T> TrackedValue<T> value(T defaultValue) {
			ConfigUtils.assertValueType(defaultValue);

			return new TrackedValueImpl<>(null, defaultValue, new LinkedHashMap<>(0), new ArrayList<>(0), new ArrayList<>(0));
		}

		@SafeVarargs
		public final <T> TrackedValue<ValueList<T>> list(T defaultValue, T... values) {
			return value(ValueList.create(defaultValue, values));
		}

		public final <T> ValueMap.TrackedBuilder<T> map(T defaultValue) {
			return new ValueMapBuilderImpl.TrackedValueMapBuilderImpl<>(defaultValue, this::value);
		}
	}
}
