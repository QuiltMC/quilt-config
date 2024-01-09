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

package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.metadata.MetadataContainerBuilder;

import java.lang.annotation.Annotation;

@Deprecated
public final class ConfigFieldAnnotationProcessors {
	/**
	 * @deprecated use {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}
	 */
	@Deprecated
	public static <T extends Annotation> void register(Class<T> annotationClass, ConfigFieldAnnotationProcessor<T> processor) {
		ConfigFieldAnnotationProcessor.register(annotationClass, processor);
	}

	/**
	 * @deprecated use {@link ConfigFieldAnnotationProcessor#applyAnnotationProcessors(Annotation, MetadataContainerBuilder)}
	 */
	@Deprecated
	public static void applyAnnotationProcessors(Annotation annotation, MetadataContainerBuilder<?> builder) {
		ConfigFieldAnnotationProcessor.applyAnnotationProcessors(annotation, builder);
	}
}
