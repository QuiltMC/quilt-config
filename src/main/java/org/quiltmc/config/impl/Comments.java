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

package org.quiltmc.config.impl;

import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Containing class for multiple {@link Comment} annotations. Automatically converted by {@link java.lang.annotation.Repeatable};
 * see <a href=https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html>the oracle docs</a>.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Internal // just in case.
public @interface Comments {
	Comment[] value();

	final class Processor implements ConfigFieldAnnotationProcessor<Comments> {
		@Override
		public void process(Comments comments, MetadataContainerBuilder<?> builder) {
			for (Comment comment : comments.value()) {
				for (String c : comment.value()) {
					builder.metadata(Comment.TYPE, b -> b.add(c));
				}
			}
		}
	}
}
