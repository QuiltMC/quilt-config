package org.quiltmc.config.api.annotations;

import org.quiltmc.config.api.metadata.MetadataContainerBuilder;

import java.lang.annotation.Annotation;

@Deprecated
public final class ConfigFieldAnnotationProcessors {
	/**
	 * @deprecated use {@link ConfigFieldAnnotationProcessor#register(Class, ConfigFieldAnnotationProcessor)}
	 */
	@Deprecated
	static <T extends Annotation> void register(Class<T> annotationClass, ConfigFieldAnnotationProcessor<T> processor) {
		ConfigFieldAnnotationProcessor.register(annotationClass, processor);
	}

	/**
	 * @deprecated use {@link ConfigFieldAnnotationProcessor#applyAnnotationProcessors(Annotation, MetadataContainerBuilder)}
	 */
	@Deprecated
	static void applyAnnotationProcessors(Annotation annotation, MetadataContainerBuilder<?> builder) {
		ConfigFieldAnnotationProcessor.applyAnnotationProcessors(annotation, builder);
	}
}
