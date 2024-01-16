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

package org.quiltmc.config.api;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueTreeNode;

import java.nio.file.Path;
import java.util.Map;


/**
 * @deprecated for removal: use {@link ReflectiveConfig} instead.
 */
@Deprecated
public abstract class WrappedConfig implements Config {
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
	public Map<MetadataType<?, ?>, Object> metadata() {
		return this.wrapped.metadata();
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

	@Override
	public final ValueTreeNode getNode(Iterable<String> key) {
		return this.wrapped.getNode(key);
	}

	@ApiStatus.Internal
	public final void setWrappedConfig(Config config) {
		this.wrapped = config;
	}
}
