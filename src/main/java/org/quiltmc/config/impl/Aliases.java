package org.quiltmc.config.impl;


import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.config.api.annotations.Alias;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Internal // just in case.
public @interface Aliases {
	Alias[] value();

	final class Processor implements ConfigFieldAnnotationProcessor<Aliases> {
		@Override
		public void process(Aliases aliases, MetadataContainerBuilder<?> builder) {
			for (Alias alias : aliases.value()) {
				for (String c : alias.value()) {
					builder.metadata(Alias.TYPE, b -> b.add(c));
				}
			}
		}
	}
}

