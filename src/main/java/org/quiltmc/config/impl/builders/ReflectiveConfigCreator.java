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
package org.quiltmc.config.impl.builders;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.annotations.Processor;
import org.quiltmc.config.api.annotations.SerializedName;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessors;
import org.quiltmc.config.api.exceptions.ConfigCreationException;
import org.quiltmc.config.api.exceptions.ConfigFieldException;
import org.quiltmc.config.impl.util.ConfigUtils;

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

	private void createField(Config.SectionBuilder builder, Object object, Field field) throws IllegalAccessException {
		if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
			if (!Modifier.isFinal(field.getModifiers())) {
				throw new ConfigFieldException("Field '" + field.getType().getName() + ':' + field.getName() + "' is not final");
			}
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			Object defaultValue = field.get(object);

			if (ConfigUtils.isValidValue(defaultValue)) {
				TrackedValue<?> value = TrackedValue.create(defaultValue, getFieldName(field), valueBuilder -> {
					field.setAccessible(true);

					valueBuilder.callback(tracked -> {
						try {
							field.set(object, tracked.value());
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					});

					for (Annotation annotation : field.getAnnotations()) {
						ConfigFieldAnnotationProcessors.applyAnnotationProcessors(annotation, valueBuilder);
					}

					if (field.isAnnotationPresent(Processor.class)) {
						Processor processor = field.getAnnotation(Processor.class);

						try {
							Method method = field.getDeclaringClass().getMethod(processor.value(), TrackedValue.Builder.class);

							method.invoke(object, valueBuilder);
						} catch (NoSuchMethodException e) {
							throw new ConfigCreationException("Processor method '" + processor.value() + "' not found.");
						} catch (InvocationTargetException | IllegalAccessException e) {
							throw new ConfigCreationException("Exception invoking processor method '" + processor.value() + "': " + e.getLocalizedMessage());
						}
					}
				});

				field.set(object, value.getRealValue());
				builder.field(value);
			} else if (defaultValue instanceof Config.Section) {
				builder.section(getFieldName(field), b -> {
					for (Annotation annotation : field.getAnnotations()) {
						ConfigFieldAnnotationProcessors.applyAnnotationProcessors(annotation, b);
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
				throw new ConfigFieldException("Class '" + defaultValue.getClass().getName() + "' of field '" + field.getName() + "' is not a valid config value; must be a basic type, complex type, or implement org.quiltmc.loader.api.Config.Section");
			}
		}
	}

	private String getFieldName(Field field) {
		SerializedName nameAnno = field.getAnnotation(SerializedName.class);
		if (nameAnno != null) {
			return nameAnno.value();
		}

		// TODO naming scheme

		return field.getName();
	}

	public void create(Config.Builder builder) {
		if (this.instance != null) {
			throw new ConfigCreationException("Reflective config creator used more than once");
		}

		try {
			this.instance = this.creatorClass.newInstance();

			for (Field field : this.creatorClass.getDeclaredFields()) {
				this.createField(builder, this.instance, field);
			}

			if (this.creatorClass.isAnnotationPresent(Processor.class)) {
				Processor processor = this.creatorClass.getAnnotation(Processor.class);

				try {
					Method method = this.creatorClass.getMethod(processor.value(), Config.Builder.class);

					method.invoke(this.instance, builder);
				} catch (NoSuchMethodException e) {
					throw new ConfigCreationException("Processor method '" + processor.value() + "' not found.");
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new ConfigCreationException("Exception invoking processor method '" + processor.value() + "': " + e.getLocalizedMessage());
				}
			}		} catch (InstantiationException | IllegalAccessException e) {
			throw new ConfigCreationException(e);
		}
	}

	public static <C> ReflectiveConfigCreator<C> of(Class<C> creatorClass) {
		return new ReflectiveConfigCreator<>(creatorClass);
	}

	public C getInstance() {
		if (this.instance == null) {
			throw new RuntimeException("Config not built yet");
		}

		return this.instance;
	}
}
