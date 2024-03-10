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

package org.quiltmc.config.impl;

import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.annotations.*;
import org.quiltmc.config.api.exceptions.ConfigFieldException;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.values.CompoundConfigValue;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.impl.util.NamingSchemeHelper;

import java.lang.annotation.Annotation;
import java.util.*;

public final class ConfigFieldAnnotationProcessors {
	private static final Map<Class<? extends Annotation>, List<ConfigFieldAnnotationProcessor<?>>> PROCESSORS = new HashMap<>();

	static {
		register(Comment.class, new CommentProcessor());
		register(Comments.class, new Comments.Processor());
		register(Alias.class, new AliasProcessor());
		register(Aliases.class, new Aliases.Processor());
		register(IntegerRange.class, new IntegerRangeProcessor());
		register(FloatRange.class, new FloatRangeProcessor());
		register(Matches.class, new MatchesProcessor());
		register(SerializedName.class, new SerialNameProcessor());
		register(SerializedNameConvention.class, new SerializedNameConventionProcessor());
	}

	public static <T extends Annotation> void register(Class<T> annotationClass, ConfigFieldAnnotationProcessor<T> processor) {
		PROCESSORS.computeIfAbsent(annotationClass, c -> new ArrayList<>())
				.add(processor);
	}

	private static <T extends Annotation> void process(ConfigFieldAnnotationProcessor<T> processor, T annotation, MetadataContainerBuilder<?> builder) {
		processor.process(annotation, builder);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void applyAnnotationProcessors(Annotation annotation, MetadataContainerBuilder<?> builder) {
		for (ConfigFieldAnnotationProcessor<?> processor : PROCESSORS.getOrDefault(annotation.annotationType(), Collections.emptyList())) {
			process((ConfigFieldAnnotationProcessor) processor, annotation, builder);
		}
	}

	private static final class CommentProcessor implements ConfigFieldAnnotationProcessor<Comment> {
		@Override
		public void process(Comment comment, MetadataContainerBuilder<?> builder) {
			for (String c : comment.value()) {
				builder.metadata(Comment.TYPE, comments -> comments.add(c));
			}
		}
	}

	private static final class AliasProcessor implements ConfigFieldAnnotationProcessor<Alias> {
		@Override
		public void process(Alias alias, MetadataContainerBuilder<?> builder) {
			for (String c : alias.value()) {
				builder.metadata(Alias.TYPE, aliases -> aliases.add(c));
			}
		}
	}

	private static final class SerialNameProcessor implements ConfigFieldAnnotationProcessor<SerializedName> {
		@Override
		public void process(SerializedName name, MetadataContainerBuilder<?> builder) {
			builder.metadata(SerializedName.TYPE, nameBuilder -> nameBuilder.withName(name.value()));
		}
	}

	private static final class SerializedNameConventionProcessor implements ConfigFieldAnnotationProcessor<SerializedNameConvention> {
		private final NamingSchemeHelper namingSchemeHelper = new NamingSchemeHelper();

		@Override
		public void process(SerializedNameConvention annotation, MetadataContainerBuilder<?> builder) {
			builder.metadata(SerializedNameConvention.TYPE, nameConventionBuilder -> nameConventionBuilder.set(
				namingSchemeHelper.getNamingScheme(annotation, ConfigFieldException::new)));
		}
	}

	private static final class FloatRangeProcessor implements ConfigFieldAnnotationProcessor<FloatRange> {
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

	private static final class IntegerRangeProcessor implements ConfigFieldAnnotationProcessor<IntegerRange> {
		@Override
		@SuppressWarnings("unchecked")
		public void process(IntegerRange range, MetadataContainerBuilder<?> builder) {
			if (builder instanceof TrackedValue.Builder) {
				Object defaultValue = ((TrackedValue.Builder<?>) builder).getDefaultValue();

				if (defaultValue instanceof Integer) {
					((TrackedValue.Builder<Integer>) builder).constraint(Constraint.range((int) range.min(), (int) range.max()));
				} else if (defaultValue instanceof Long) {
					((TrackedValue.Builder<Long>) builder).constraint(Constraint.range(range.min(), range.max()));
				} else if (defaultValue instanceof CompoundConfigValue && Integer.class.isAssignableFrom(((CompoundConfigValue<?>) defaultValue).getType())) {
					((TrackedValue.Builder<CompoundConfigValue<Integer>>) builder).constraint(Constraint.all(Constraint.range((int) range.min(), (int) range.max())));
				} else if (defaultValue instanceof CompoundConfigValue && Long.class.isAssignableFrom(((CompoundConfigValue<?>) defaultValue).getType())) {
					((TrackedValue.Builder<CompoundConfigValue<Long>>) builder).constraint(Constraint.all(Constraint.range(range.min(), range.max())));
				} else {
					throw new ConfigFieldException("Constraint LongRange not applicable for type '" + defaultValue.getClass() + "'");
				}
			}
		}
	}

	private static final class MatchesProcessor implements ConfigFieldAnnotationProcessor<Matches> {
		@Override
		@SuppressWarnings("unchecked")
		public void process(Matches matches, MetadataContainerBuilder<?> builder) {
			if (builder instanceof TrackedValue.Builder) {
				Object defaultValue = ((TrackedValue.Builder<?>) builder).getDefaultValue();

				if (defaultValue instanceof String) {
					((TrackedValue.Builder<String>) builder).constraint(Constraint.matching(matches.value()));
				} else if (defaultValue instanceof CompoundConfigValue && ((CompoundConfigValue<?>) defaultValue).getType().equals(String.class)) {
					((TrackedValue.Builder<CompoundConfigValue<String>>) builder).constraint(Constraint.all(Constraint.matching(matches.value())));
				}
			}
		}
	}
}
