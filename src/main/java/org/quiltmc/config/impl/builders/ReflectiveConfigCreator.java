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

package org.quiltmc.config.impl.builders;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.impl.ConfigFieldAnnotationProcessors;
import org.quiltmc.config.api.exceptions.ConfigCreationException;
import org.quiltmc.config.api.exceptions.ConfigFieldException;
import org.quiltmc.config.impl.tree.TrackedValueImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectiveConfigCreator<C> implements Config.Creator {
	private final Class<C> creatorClass;
	private C instance;

	public ReflectiveConfigCreator(Class<C> creatorClass) {
		this.creatorClass = creatorClass;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void createField(Config.SectionBuilder builder, Object object, Field field) throws IllegalAccessException {
		if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
			if (!Modifier.isFinal(field.getModifiers())) {
				throw new ConfigFieldException("Field '" + field.getType().getName() + ':' + field.getName() + "' is not final!");
			}

			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}

			Object defaultValue = field.get(object);

			if (defaultValue instanceof TrackedValueImpl) {
				TrackedValueImpl<?> value = (TrackedValueImpl<?>) defaultValue;

				TrackedValueBuilderImpl<?> delegateBuilder = new TrackedValueBuilderImpl<>(value.getDefaultValue(), field.getName());

				for (Annotation annotation : field.getAnnotations()) {
					ConfigFieldAnnotationProcessors.applyAnnotationProcessors(annotation, delegateBuilder);
				}

				if (field.isAnnotationPresent(Processor.class)) {
					Processor processor = field.getAnnotation(Processor.class);

					try {
						Method method = field.getDeclaringClass().getMethod(processor.value(), TrackedValue.Builder.class);

						method.invoke(object, delegateBuilder);
					} catch (NoSuchMethodException e) {
						throw new ConfigCreationException("Processor method '" + processor.value() + "' not found for config field '" + this.creatorClass.getName() + "#" + field.getName() + "'.");
					} catch (InvocationTargetException | IllegalAccessException e) {
						throw new ConfigCreationException("Exception invoking processor method '" + processor.value() + "': " + e.getLocalizedMessage());
					}
				}

				TrackedValueImpl delegate = (TrackedValueImpl<?>) delegateBuilder.build();
				if (value.key() != null) {
					throw new IllegalStateException("Unexpected key set in TrackedValue. Please report this at https://github.com/QuiltMC/quilt-config/issues!");
				}

				value.setKey(delegate.key());
				if (!value.metadata.isEmpty()) {
					throw new IllegalStateException("Unexpected metadata value set in TrackedValue. Please report this at https://github.com/QuiltMC/quilt-config/issues!");
				}

				value.metadata = delegate.metadata;
				if (!value.constraints.isEmpty()) {
					throw new IllegalStateException("Unexpected constraints value set in TrackedValue. Please report this at https://github.com/QuiltMC/quilt-config/issues!");
				}

				value.constraints = delegate.constraints;
				if (!value.callbacks.isEmpty()) {
					throw new IllegalStateException("Unexpected callback value set in TrackedValue. Please report this at https://github.com/QuiltMC/quilt-config/issues!");
				}

				value.callbacks = delegate.callbacks;

				builder.field(value);
			} else if (defaultValue instanceof ReflectiveConfig.Section) {
				builder.section(field.getName(), b -> {
					for (Annotation annotation : field.getAnnotations()) {
						ConfigFieldAnnotationProcessors.applyAnnotationProcessors(annotation, b);
					}

					if (field.isAnnotationPresent(Processor.class)) {
						Processor processor = field.getAnnotation(Processor.class);

						try {
							Method method = field.getDeclaringClass().getMethod(processor.value(), Config.SectionBuilder.class);

							method.invoke(object, b);
						} catch (NoSuchMethodException e) {
							throw new ConfigCreationException("Processor method '" + processor.value() + "' not found for config section '" + this.creatorClass.getName() + "#" + field.getName() + "'.");
						} catch (InvocationTargetException | IllegalAccessException e) {
							throw new ConfigCreationException("Exception invoking processor method '" + processor.value() + "': " + e.getLocalizedMessage());
						}
					}

					for (Field f : defaultValue.getClass().getDeclaredFields()) {
						if (!f.isSynthetic()) {
							try {
								this.createField(b, defaultValue, f);
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							}
						}
					}
				});
			} else if (defaultValue == null) {
				throw new ConfigFieldException("Default value for field '" + field.getName() + "' cannot be null");
			} else {
				throw new ConfigFieldException("Class '" + defaultValue.getClass().getName() + "' of field '" + field.getName() + "' "
						+ "of config class '" + field.getDeclaringClass().getName() + "'is not a valid config value: it must be a TrackedValue or implement org.quiltmc.loader.api.Config.Section");
			}
		}
	}

	public void create(Config.Builder builder) {
		if (this.instance != null) {
			throw new ConfigCreationException("Reflective config creator used more than once!");
		}

		try {
			this.instance = this.creatorClass.getDeclaredConstructor().newInstance();

			for (Annotation annotation : this.creatorClass.getAnnotations()) {
				ConfigFieldAnnotationProcessors.applyAnnotationProcessors(annotation, builder);
			}

			for (Field field : this.creatorClass.getDeclaredFields()) {
				this.createField(builder, this.instance, field);
			}

			if (this.creatorClass.isAnnotationPresent(Processor.class)) {
				Processor processor = this.creatorClass.getAnnotation(Processor.class);

				try {
					Method method = this.creatorClass.getMethod(processor.value(), Config.Builder.class);

					method.invoke(this.instance, builder);
				} catch (NoSuchMethodException e) {
					throw new ConfigCreationException("Processor method '" + processor.value() + "' not found for config class '" + this.creatorClass.getName() + "'.");
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new ConfigCreationException("Exception invoking processor method '" + processor.value() + "': " + e.getLocalizedMessage());
				}
			}
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new ConfigCreationException(e);
		}
	}

	public static <C> ReflectiveConfigCreator<C> of(Class<C> creatorClass) {
		return new ReflectiveConfigCreator<>(creatorClass);
	}

	public C getInstance() {
		if (this.instance == null) {
			throw new RuntimeException("Config not built yet!");
		}

		return this.instance;
	}
}
