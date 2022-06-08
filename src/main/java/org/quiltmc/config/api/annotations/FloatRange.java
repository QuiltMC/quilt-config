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
package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.exceptions.ConfigFieldException;
import org.quiltmc.config.api.values.CompoundConfigValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FloatRange {
	double min();
	double max();

	final class Processor implements ConfigFieldAnnotationProcessor<FloatRange> {
		@Override
		@SuppressWarnings("unchecked")
		public void process(FloatRange range, MetadataContainerBuilder<?> builder) {
			if (builder instanceof TrackedValue.Builder) {
				Object defaultValue = ((TrackedValue.Builder<?>) builder).getDefaultValue();

				if (defaultValue instanceof Float) {
					((TrackedValue.Builder<Float>) builder).constraint(Constraint.range((float) range.min(), (float) range.max()));
				} else if (defaultValue instanceof Double) {
					((TrackedValue.Builder<Double>) builder).constraint(Constraint.range(range.min(), range.max()));
				} else if (defaultValue instanceof CompoundConfigValue && Float.class.isAssignableFrom(((CompoundConfigValue<?>) defaultValue).getType())) {
					((TrackedValue.Builder<CompoundConfigValue<Float>>) builder).constraint(Constraint.all(Constraint.range((float) range.min(), (float) range.max())));
				} else if (defaultValue instanceof CompoundConfigValue && Double.class.isAssignableFrom(((CompoundConfigValue<?>) defaultValue).getType())) {
					((TrackedValue.Builder<CompoundConfigValue<Double>>) builder).constraint(Constraint.all(Constraint.range(range.min(), range.max())));
				} else {
					throw new ConfigFieldException("Constraint FloatRange not applicable for type '" + defaultValue.getClass() + "'");
				}
			}
		}
	}
}
