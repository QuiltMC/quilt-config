/*
 * Copyright 2022 QuiltMC
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

package org.quiltmc.config.impl.tree;

import org.jetbrains.annotations.NotNull;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.exceptions.TrackedValueException;
import org.quiltmc.config.api.values.ComplexConfigValue;
import org.quiltmc.config.api.values.ValueKey;
import org.quiltmc.config.impl.AbstractMetadataContainer;
import org.quiltmc.config.impl.ConfigImpl;
import org.quiltmc.config.impl.util.ImmutableIterable;
import org.quiltmc.config.impl.values.ValueKeyImpl;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TrackedValueImpl<T> extends AbstractMetadataContainer implements TrackedValue<T> {
	public List<UpdateCallback<T>> callbacks;
	public List<Constraint<T>> constraints;
	private final T defaultValue;

	private ValueKey key;
	private ConfigImpl config;
	private T value;

	private boolean isBeingOverridden = false;
	private T valueOverride;

	@SuppressWarnings("unchecked")
	public TrackedValueImpl(ValueKey key, T defaultValue, Map<MetadataType<?, ?>, Object> metadata, List<UpdateCallback<T>> callbacks, List<Constraint<T>> constraints) {
		super(metadata);
		this.key = key;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.callbacks = callbacks;
		this.constraints = constraints;

		this.assertValue(defaultValue);

		if (defaultValue instanceof ComplexConfigValue) {
			((ComplexConfigValue) defaultValue).setValue(this);
			this.value = (T) ((ComplexConfigValue) this.defaultValue).copy();
		} else {
			this.value = defaultValue;
		}
	}

	public void setConfig(ConfigImpl config) {
		if (this.config != null) {
			throw new TrackedValueException("TrackedValue '" + this.key + "' cannot be assigned to multiple configs");
		}

		this.config = config;
	}

	public TrackedValueImpl<T> setKey(ValueKey key) {
		this.key = key;

		return this;
	}

	@Override
	public ValueKey key() {
		return this.key;
	}

	@Override
	public T value() {
		return this.isBeingOverridden ? this.valueOverride : this.value;
	}

	@Override
	public boolean isBeingOverridden() {
		return this.isBeingOverridden;
	}

	@Override
	public T getRealValue() {
		return this.value;
	}

	private void assertValue(T value) {
		Optional<Iterable<String>> errors = this.checkForFailingConstraints(value);

		if (errors.isPresent()) {
			StringBuilder errorMessage = new StringBuilder();

			for (String message : errors.get()) {
				errorMessage.append(message).append('\n');
			}

			throw new TrackedValueException(errorMessage.toString());
		}
	}

	@Override
	public T setValue(@NotNull T newValue, boolean serialize) {
		this.assertValue(newValue);

		if (newValue instanceof ComplexConfigValue) {
			((ComplexConfigValue) newValue).setValue(this);
		}

		T oldValue = this.value;
		this.value = newValue;

		if (serialize) {
			this.config.save();
		}

		if (!this.isBeingOverridden()) {
			this.invokeCallbacks();
		}

		return oldValue;
	}

	@Override
	public void setOverride(T newValue) {
		this.assertValue(newValue);

		this.isBeingOverridden = true;
		this.valueOverride = newValue;

		this.invokeCallbacks();
	}

	@Override
	public void removeOverride() {
		this.isBeingOverridden = false;
		T oldValueOverride = this.valueOverride;
		this.valueOverride = null;

		this.invokeCallbacks();
	}

	@Override
	public T getDefaultValue() {
		return this.defaultValue;
	}

	@Override
	public void registerCallback(UpdateCallback<T> callback) {
		this.callbacks.add(callback);
	}

	@Override
	public Iterable<Constraint<T>> constraints() {
		return new ImmutableIterable<>(this.constraints);
	}

	@Override
	public Optional<Iterable<String>> checkForFailingConstraints(T value) {
		List<String> failing = null;

		if (value == null) {
			failing = new ArrayList<>();

			failing.add("Value cannot be null");
		}

		for (Constraint<T> constraint : this.constraints) {
			Optional<String> errorMessage = constraint.test(value);

			if (errorMessage.isPresent()) {
				if (failing == null) {
					failing = new ArrayList<>();
				}

				failing.add(errorMessage.get());
			}
		}

		if (failing == null) {
			return Optional.empty();
		} else {
			return Optional.of(new ImmutableIterable<>(failing));
		}
	}

	@Override
	public void invokeCallbacks() {
		this.config.invokeCallbacks();

		for (UpdateCallback<T> callback : this.callbacks) {
			callback.onUpdate(this);
		}
	}

	@Override
	public void serializeAndInvokeCallbacks() {
		this.config.save();

		this.config.invokeCallbacks();

		for (UpdateCallback<T> callback : this.callbacks) {
			callback.onUpdate(this);
		}
	}
//
//	@Override
//	public TrackedValue.Builder<T> key(String key) {
//		String[] keys = new String[this.key.length() + 1];
//
//		Iterator<String> keyIter = key().iterator();
//		for (int i = 0; i < key.length(); i++) {
//			keys[i] = keyIter.next();
//		}
//		keys[keys.length - 1] = key;
//		this.key = new ValueKeyImpl(keys);
//		return this;
//	}
//
//	@SuppressWarnings("unchecked")
//	public <M, B extends MetadataType.Builder<M>> TrackedValue.Builder<T> metadata(MetadataType<M, B> type, Consumer<B> builderConsumer) {
//		B builder = type.newBuilder();
//		if (this.metadata.get(type) != null) {
//			builder.
//		}
//		builderConsumer.accept((B) this.metadata.computeIfAbsent(type, t -> type.newBuilder().build()));
//
//		return this;
//	}
//
//	@Override
//	public TrackedValue.Builder<T> constraint(Constraint<T> constraint) {
//		this.constraints.add(constraint);
//
//		return this;
//	}
//
//	@Override
//	public TrackedValue.Builder<T> callback(TrackedValue.UpdateCallback<T> callback) {
//		this.callbacks.add(callback);
//
//		return this;
//	}


}
